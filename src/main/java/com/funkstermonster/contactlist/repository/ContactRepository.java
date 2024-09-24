package com.funkstermonster.contactlist.repository;

import com.funkstermonster.contactlist.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    Optional<Contact> findByNameAndPhoneNumberAndEmail(String name, String phoneNumber, String email);
}
