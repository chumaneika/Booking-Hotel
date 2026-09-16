#!/usr/bin/env python3
"""Local integration test. Never point this at a production Compose project."""
import concurrent.futures
import datetime
import json
import os
import subprocess
import time
import urllib.error
import urllib.request
import uuid

PROJECT = "bookinghotel-flow-check"
BASE = "http://127.0.0.1:18080"
ENV = dict(os.environ, PUBLIC_DOMAIN="localhost", POSTGRES_USER="flow_test",
           POSTGRES_PASSWORD="local-flow-test-only-password",
           JWT_SECRET="local-flow-test-only-signing-key-not-for-production-64")
COMPOSE = ["docker", "compose", "-p", PROJECT, "--env-file", ".env.example",
           "-f", "docker-compose.yml", "-f", "deploy/compose.flow-test.yml"]


def compose(*args, input=None):
    return subprocess.run(COMPOSE + list(args), env=ENV, input=input, text=True,
                          check=True, capture_output=True).stdout.strip()


def sql(service, statement, database=None):
    db = database or {"auth-db": "auth_db", "booking-db": "booking_db",
                      "catalog-db": "catalogs_db", "payment-db": "payment_db"}[service]
    return compose("exec", "-T", service, "psql", "-U", "flow_test", "-d", db,
                   "-At", "-v", "ON_ERROR_STOP=1", "-c", statement)


def http(service, path, method="GET", body=None, token=None, expected=200):
    headers = {"Content-Type": "application/json"}
    if token:
        headers["Authorization"] = "Bearer " + token
    request = urllib.request.Request(BASE + "/" + service + path, method=method,
                                     headers=headers,
                                     data=None if body is None else json.dumps(body).encode())
    try:
        with urllib.request.urlopen(request, timeout=20) as response:
            status, data = response.status, response.read()
    except urllib.error.HTTPError as error:
        status, data = error.code, error.read()
    assert status == expected, f"{method} {service}{path}: expected {expected}, got {status}: {data[:500]}"
    return json.loads(data) if data else None


def wait(check, timeout=240):
    deadline = time.monotonic() + timeout
    last = None
    while time.monotonic() < deadline:
        try:
            value = check()
            if value:
                return value
        except (AssertionError, OSError, subprocess.CalledProcessError) as error:
            last = error
        time.sleep(1)
    raise AssertionError(f"Condition timed out: {last}")


def kafka(topic, envelope):
    compose("exec", "-T", "kafka", "/opt/kafka/bin/kafka-console-producer.sh",
            "--bootstrap-server", "localhost:9092", "--topic", topic,
            input=json.dumps(envelope) + "\n")


def main():
    run = uuid.uuid4().hex[:12]
    password = "LocalFlowTest123!"
    email = "flow-" + run + "@example.test"
    admin_email = "admin-" + run + "@example.test"
    register = lambda address: http("auth-service", "/api/auth/register", "POST",
                                    {"firstname": "Flow", "surname": "Test", "email": address, "password": password})
    wait(lambda: http("auth-service", "/test/hello", expected=403) is None)
    user = register(email)
    admin = register(admin_email)
    # Bootstrap only this isolated test account; public registration must stay USER.
    sql("auth-db", f"UPDATE accounts SET role='ADMIN' WHERE id={int(admin['userId'])}")
    login = lambda address: http("auth-service", "/api/auth/login", "POST", {"email": address, "password": password})
    user_token = login(email)["token"]
    admin_token = login(admin_email)["token"]
    print("PASS registration and login", flush=True)
    wait(lambda: http("catalog-service", "/api/hotels/active", token=user_token) is not None)
    http("catalog-service", "/api/hotels/add-hotel", "POST",
         {"name": "Flow " + run, "description": "Isolated integration test", "rating": 4,
          "country": "Test", "city": "Test", "address": "Test street", "status": "ACTIVE"},
         admin_token, 201)
    hotels = http("catalog-service", "/api/hotels/search?name=Flow%20" + run, token=user_token)
    hotel = hotels[0]["id"]
    http("catalog-service", "/api/room-types?hotelId=" + str(hotel), "POST",
         {"hotelId": hotel, "name": "STANDARD", "capacity": 2, "basePrice": 5000,
          "sizeSqm": 25, "bedType": "DOUBLE", "quantityRoom": 1}, admin_token, 201)
    room = http("catalog-service", "/api/room-types?hotelId=" + str(hotel), token=user_token)[0]["id"]
    print("PASS catalog through gateway", flush=True)
    start = datetime.date.today() + datetime.timedelta(days=10)

    def create(offset=0, quantity=1):
        return http("booking-service", "/api/bookings", "POST",
                    {"userId": user["userId"], "hotelId": hotel,
                     "checkInDate": str(start + datetime.timedelta(days=offset)),
                     "checkOutDate": str(start + datetime.timedelta(days=offset+3)),
                     "rooms": [{"roomTypeId": room, "quantity": quantity,
                                "pricePerNight": 0.01, "nights": 1}]}, user_token, 201)

    def booking(id):
        return http("booking-service", "/api/bookings/" + id, token=user_token)

    def status(id, value):
        return wait(lambda: (b if (b := booking(id))["status"] == value else None))

    def payment(id):
        return wait(lambda: http("payment-service", "/api/payments?bookingPublicId=" + id, token=user_token))

    first = create()
    first_id = first["publicId"]
    assert float(first["totalPrice"]) == 15000
    assert first["rooms"][0]["nights"] == 3 and float(first["rooms"][0]["pricePerNight"]) == 5000
    status(first_id, "PAYMENT_PENDING")
    p = payment(first_id)
    assert float(p["amount"]) == 15000 and p["currency"] == "RUB"
    print("PASS authoritative price, nights, inventory -> payment command", flush=True)
    rejected = create()
    status(rejected["publicId"], "CANCELLED")
    assert sql("catalog-db", f"SELECT count(*) FROM inventory_reservation_rooms WHERE room_type_id={room}") == "1"
    print("PASS overbooking rejection", flush=True)
    http("payment-service", "/api/payments?bookingPublicId=" + first_id, token=admin["token"], expected=403)
    http("booking-service", "/api/bookings/" + first_id + "/status", "PATCH", {"status": "CONFIRMED"}, admin_token, 409)
    # Fail -> cancel -> release -> dates become bookable again.
    http("payment-service", "/api/payments/" + p["paymentId"] + "/demo", "POST", {"outcome": "FAILED"}, user_token)
    status(first_id, "CANCELLED")
    wait(lambda: sql("catalog-db", f"SELECT count(*) FROM inventory_reservation_rooms WHERE room_type_id={room}") == "0")
    second = create()
    second_id = second["publicId"]
    status(second_id, "PAYMENT_PENDING")
    p2 = payment(second_id)
    http("payment-service", "/api/payments/" + p2["paymentId"] + "/demo", "POST", {"outcome": "SUCCEEDED"}, user_token)
    status(second_id, "PAID")
    http("booking-service", "/api/bookings/" + second_id + "/status", "PATCH", {"status": "CONFIRMED"}, admin_token)
    status(second_id, "CONFIRMED")
    print("PASS payment failure/release and success/confirmation", flush=True)
    # Replay the original command: no second allocation or second payment.
    def inventory_offset():
        return int(compose("exec", "-T", "kafka", "/opt/kafka/bin/kafka-get-offsets.sh",
                           "--bootstrap-server", "localhost:9092", "--topic", "inventory.reservation.events.v1").rsplit(":", 1)[1])
    previous_offset = inventory_offset()
    reservation_id = sql("catalog-db", f"SELECT reservation_id FROM inventory_reservations WHERE booking_public_id='{second_id}'")
    command = {"eventType": "ROOM_RESERVATION_REQUESTED", "payload": {
        "reservationId": reservation_id, "bookingPublicId": second_id, "hotelId": hotel,
        "checkInDate": str(start), "checkOutDate": str(start + datetime.timedelta(days=3)),
        "rooms": [{"roomTypeId": room, "quantity": 1}]}}
    kafka("inventory.reservation.commands.v1", command)
    wait(lambda: inventory_offset() > previous_offset)
    wait(lambda: sql("catalog-db", "SELECT count(*) FROM kafka_outbox") == "0")
    assert sql("catalog-db", f"SELECT count(*) FROM inventory_reservation_rooms WHERE reservation_id='{reservation_id}'") == "1"
    assert sql("payment-db", f"SELECT count(*) FROM payments WHERE booking_public_id='{second_id}'") == "1"
    # Different dates work. Simultaneous requests for a one-room stock cannot both win.
    with concurrent.futures.ThreadPoolExecutor(max_workers=2) as pool:
        futures = [pool.submit(create, 20) for _ in range(2)]
        ids = [f.result()["publicId"] for f in futures]
    wait(lambda: all(booking(id)["status"] != "NEW" for id in ids))
    assert sorted(booking(id)["status"] for id in ids) == ["CANCELLED", "PAYMENT_PENDING"]
    print("PASS duplicate delivery and concurrent inventory requests", flush=True)
    # Commit with Kafka stopped, restart booking, then resume: outbox must survive.
    compose("stop", "kafka")
    delayed = create(40)["publicId"]
    assert booking(delayed)["status"] == "NEW"
    assert int(sql("booking-db", "SELECT count(*) FROM kafka_outbox")) > 0
    compose("restart", "booking-service")
    compose("start", "kafka")
    status(delayed, "PAYMENT_PENDING")
    print("PASS interrupted business flow resumes after restart", flush=True)
    # Recreate (not delete) containers; named volume data and Kafka records remain.
    compose("up", "-d", "--no-build", "--force-recreate", "auth-db", "catalog-db", "booking-db", "payment-db", "kafka")
    compose("restart", "auth-service", "catalog-service", "booking-service", "payment-service")
    status(second_id, "CONFIRMED")
    assert wait(lambda: login(email))["userId"] == user["userId"]
    assert payment(second_id)["status"] == "SUCCEEDED"
    print("PASS persistence after database/Kafka/container recreation", flush=True)
    # Back up all four running test databases and validate the custom dump restore.
    backup_env = dict(ENV, COMPOSE_PROJECT_NAME=PROJECT,
                      COMPOSE_FILE="docker-compose.yml:deploy/compose.flow-test.yml", COMPOSE_ENV_FILES=".env.example")
    result = subprocess.run(["bash", "deploy/backup-databases.sh", "auth-db", "catalog-db", "booking-db", "payment-db"],
                            env=backup_env, text=True, capture_output=True, check=True)
    directory = result.stdout.strip().split("Backup saved: ", 1)[1]
    restore_db = "flow_restore_" + run
    sql("booking-db", f"CREATE DATABASE {restore_db}")
    # Shell redirection avoids loading the dump or credentials into test output.
    restore_env = dict(ENV, FLOW_DUMP=directory + "/booking-db.dump")
    subprocess.run(["bash", "-c", 'exec "$@" < "$FLOW_DUMP"', "_"] + COMPOSE +
                   ["exec", "-T", "booking-db", "pg_restore", "-U", "flow_test", "-d", restore_db,
                    "--no-owner", "--no-acl", "--exit-on-error"], env=restore_env, check=True,
                   stdout=subprocess.DEVNULL)
    assert sql("booking-db", f"SELECT status FROM bookings WHERE public_id='{second_id}'", restore_db) == "CONFIRMED"
    sql("booking-db", f"DROP DATABASE {restore_db}")
    print("PASS database backup and restore (temporary restored test database removed)", flush=True)
    compose("up", "-d", "--no-build", "database-backups")
    wait(lambda: compose("exec", "-T", "database-backups", "/bin/sh", "/opt/backup-health.sh") == "")
    complete_count = compose("exec", "-T", "database-backups", "/bin/sh", "-ec",
                            'task_marker="$(find /backups -name COMPLETE | sort | tail -n 1)"; '
                            'task_dir="${task_marker%/COMPLETE}"; '
                            'cd "$task_dir"; sha256sum -c SHA256SUMS >/dev/null; '
                            'find . -name "*.dump" | wc -l')
    assert int(complete_count) == 8
    print("PASS scheduled backup service: eight databases and checksums", flush=True)
    print("ALL FLOW CHECKS PASSED", flush=True)


if __name__ == "__main__":
    main()
