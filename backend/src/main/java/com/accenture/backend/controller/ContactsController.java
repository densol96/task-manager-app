package com.accenture.backend.controller;

import com.accenture.backend.dto.user.ContactDto;
import com.accenture.backend.dto.user.ContactRequestDto;
import com.accenture.backend.service.serviceimpl.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class ContactsController {

    private final ContactService contactService;

    @PostMapping("/requests")
    public ResponseEntity<ContactRequestDto> sendContactRequest(@RequestParam Long senderId,
                                                                @RequestParam Long receiverId) {
        ContactRequestDto requestDto = contactService.sendContactRequest(senderId, receiverId);
        return ResponseEntity.ok(requestDto);
    }

    @GetMapping("/requests")
    public ResponseEntity<List<ContactRequestDto>> getIncomingRequests(@RequestParam Long receiverId) {
        List<ContactRequestDto> requests = contactService.getIncomingRequests(receiverId);
        return ResponseEntity.ok(requests);
    }

    @PutMapping("/requests/{requestId}/accept")
    public ResponseEntity<ContactRequestDto> acceptContactRequest(@PathVariable Long requestId,
                                                                  @RequestParam Long receiverId) {
        ContactRequestDto requestDto = contactService.acceptContactRequest(requestId, receiverId);
        return ResponseEntity.ok(requestDto);
    }

    @PutMapping("/requests/{requestId}/decline")
    public ResponseEntity<ContactRequestDto> declineContactRequest(@PathVariable Long requestId,
                                                                   @RequestParam Long receiverId) {
        ContactRequestDto requestDto = contactService.declineContactRequest(requestId, receiverId);
        return ResponseEntity.ok(requestDto);
    }

    @DeleteMapping("/{contactId}")
    public ResponseEntity<Void> removeContact(@RequestParam Long userId,
                                              @PathVariable Long contactId) {
        contactService.removeContact(userId, contactId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ContactDto>> getContacts(@RequestParam Long userId) {
        List<ContactDto> contacts = contactService.getContacts(userId);
        return ResponseEntity.ok(contacts);
    }
}
