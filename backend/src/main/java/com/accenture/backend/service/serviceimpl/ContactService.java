package com.accenture.backend.service.serviceimpl;

import com.accenture.backend.dto.user.ContactDto;
import com.accenture.backend.dto.user.ContactRequestDto;
import com.accenture.backend.entity.Contact;
import com.accenture.backend.entity.ContactRequest;
import com.accenture.backend.enums.ContactRequestStatus;
import com.accenture.backend.entity.User;
import com.accenture.backend.repository.ContactRepository;
import com.accenture.backend.repository.ContactRequestRepository;
import com.accenture.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final ContactRequestRepository contactRequestRepository;
    private final UserRepository userRepository;

    @Transactional
    public ContactRequestDto sendContactRequest(Long senderId, Long receiverId) {
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("You can not send a request to yourself");
        }
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("No sender found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        contactRequestRepository.findBySenderAndReceiver(sender, receiver)
                .ifPresent(cr -> { throw new RuntimeException("The request has already been sent"); });
        contactRepository.findByUserAndContact(sender, receiver)
                .ifPresent(c -> { throw new RuntimeException("Already in contact"); });

        ContactRequest request = new ContactRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setStatus(ContactRequestStatus.PENDING);
        ContactRequest savedRequest = contactRequestRepository.save(request);

        return mapToDto(savedRequest);
    }

    @Transactional
    public ContactRequestDto acceptContactRequest(Long requestId, Long receiverId) {
        ContactRequest request = contactRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!request.getReceiver().getId().equals(receiverId)) {
            throw new RuntimeException("No rights to confirm this request");
        }

        request.setStatus(ContactRequestStatus.ACCEPTED);
        contactRequestRepository.save(request);


        Contact contact1 = new Contact();
        contact1.setUser(request.getSender());
        contact1.setContact(request.getReceiver());
        contactRepository.save(contact1);

        Contact contact2 = new Contact();
        contact2.setUser(request.getReceiver());
        contact2.setContact(request.getSender());
        contactRepository.save(contact2);

        return mapToDto(request);
    }

    @Transactional
    public ContactRequestDto declineContactRequest(Long requestId, Long receiverId) {
        ContactRequest request = contactRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!request.getReceiver().getId().equals(receiverId)) {
            throw new RuntimeException("No rights to confirm this request");
        }

        request.setStatus(ContactRequestStatus.DECLINED);
        contactRequestRepository.save(request);
        return mapToDto(request);
    }

    @Transactional
    public void removeContact(Long userId, Long contactId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User contactUser = userRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("No contact found"));

        Contact contact = contactRepository.findByUserAndContact(user, contactUser)
                .orElseThrow(() -> new RuntimeException("No contact found"));
        contactRepository.delete(contact);

        contactRepository.findByUserAndContact(contactUser, user).ifPresent(contactRepository::delete);
    }


    public List<ContactDto> getContacts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Contact> contacts = contactRepository.findByUser(user);
        return contacts.stream().map(this::mapToContactDto).collect(Collectors.toList());
    }

    public List<ContactRequestDto> getIncomingRequests(Long receiverId) {
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<ContactRequest> requests = contactRequestRepository.findByReceiverAndStatus(receiver, ContactRequestStatus.PENDING);
        return requests.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private ContactRequestDto mapToDto(ContactRequest request) {
        return new ContactRequestDto(
                request.getId(),
                request.getSender().getId(),
                request.getReceiver().getId(),
                request.getStatus(),
                request.getCreatedAt()
        );
    }

    private ContactDto mapToContactDto(Contact contact) {
        return new ContactDto(
                contact.getId(),
                contact.getUser().getId(),
                contact.getContact().getId(),
                contact.getContact().getEmail(),
                contact.getContact().getFirstName(),
                contact.getContact().getLastName()
        );
    }
}
