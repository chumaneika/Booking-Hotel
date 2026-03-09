package com.booking_hotel.notification_service.service.impl;

import com.booking_hotel.notification_service.dto.RegistrationSuccessNotificationRequest;
import com.booking_hotel.notification_service.entity.NotificationEntity;
import com.booking_hotel.notification_service.entity.NotificationType;
import com.booking_hotel.notification_service.repository.NotificationRepository;
import com.booking_hotel.notification_service.service.NotificationSender;
import com.booking_hotel.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceJpa implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final List<NotificationSender> notificationSenders;

    @Override
    public void sendRegistrationSuccessEmail(RegistrationSuccessNotificationRequest request) {
        String subject = "Успешная регистрация";
        String message = """
                Здравствуйте, %s!

                Вы успешно зарегистрировались в Booking Hotel.
                Теперь можете войти в аккаунт и начать бронирование.

                С уважением,
                Booking Hotel
                """.formatted(request.name());

        NotificationEntity notification = NotificationEntity.createPending(
                request.userId(),
                request.email(),
                subject,
                message,
                NotificationType.EMAIL
        );
        notificationRepository.save(notification);

        try {
            resolveSender(NotificationType.EMAIL).send(request.email(), subject, message);
            notification.markSent();
        } catch (RuntimeException exception) {
            notification.markFailed();
            throw exception;
        } finally {
            notificationRepository.save(notification);
        }
    }

    private NotificationSender resolveSender(NotificationType type) {
        return notificationSenders.stream()
                .filter(sender -> sender.supports(type))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No sender found for type: " + type));
    }
}
