package com.accenture.backend.service.contactservice;

import com.accenture.backend.entity.Message;
import com.accenture.backend.entity.MessagePrivacy;
import com.accenture.backend.entity.Notification;
import com.accenture.backend.entity.User;
import com.accenture.backend.repository.ContactRepository;
import com.accenture.backend.repository.MessageRepository;
import com.accenture.backend.repository.NotificationRepository;
import com.accenture.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MessagingService {

    private final UserRepository userRepository;
    private final ContactRepository contactRepository;
    private final MessageRepository messageRepository;
    private final NotificationRepository notificationRepository;

    public void sendMessage(Long senderId, Long receiverId, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("No sender found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        if (receiver.getMessagePrivacy() == MessagePrivacy.CONTACTS_ONLY) {
            boolean isContact = contactRepository.findByUserAndContact(receiver, sender).isPresent();
            if (!isContact) {
                throw new RuntimeException("The sender is not in the recipient's contacts");
            }
        }

        Message newMessage = new Message();
        newMessage.setSender(sender);
        newMessage.setReceiver(receiver);
        newMessage.setContent(content);
        messageRepository.save(newMessage);

        Notification notification = new Notification();
        notification.setUser(receiver);
        notification.setTitle("New message");
        notification.setMessage("You have a new message from" + sender.getFirstName() + " " + sender.getLastName());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setHasBeenRead(false);
        notificationRepository.save(notification);
    }
}
