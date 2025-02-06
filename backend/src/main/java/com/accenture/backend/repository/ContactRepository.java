package com.accenture.backend.repository;

import com.accenture.backend.entity.Contact;
import com.accenture.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByUser(User user);
    Optional<Contact> findByUserAndContact(User user, User contact);
}
