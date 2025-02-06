package com.accenture.backend.repository;

import com.accenture.backend.entity.ContactRequest;
import com.accenture.backend.entity.ContactRequestStatus;
import com.accenture.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ContactRequestRepository extends JpaRepository<ContactRequest, Long> {
    List<ContactRequest> findByReceiverAndStatus(User receiver, ContactRequestStatus status);
    Optional<ContactRequest> findBySenderAndReceiver(User sender, User receiver);
}
