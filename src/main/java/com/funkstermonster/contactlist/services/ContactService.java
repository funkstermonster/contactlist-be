package com.funkstermonster.contactlist.services;

import com.funkstermonster.contactlist.model.Contact;
import com.funkstermonster.contactlist.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ImageStorageService imageStorageService;

    public List<Contact> findAll() {
        return contactRepository.findAll();
    }

    public Optional<Contact> findById(Long id) {
        return contactRepository.findById(id);
    }

    public Contact save(Contact contact) {
        return contactRepository.save(contact);
    }

    public Contact saveContact(Contact contact) {
        Optional<Contact> existingContact = contactRepository.findByNameAndPhoneNumberAndEmail(
                contact.getName(), contact.getPhoneNumber(), contact.getEmail());

        if (existingContact.isPresent()) {
            throw new RuntimeException("Contact with the same details already exists");
        }

        return contactRepository.save(contact);
    }

    public void deleteById(Long id) {
        contactRepository.deleteById(id);
    }

    public Contact updateContact(Long id, Contact contact, MultipartFile image) throws Exception {
        Optional<Contact> existingContactOpt = contactRepository.findById(id);
        if (!existingContactOpt.isPresent()) {
            throw new NoSuchElementException("Contact not found");
        }

        Contact existingContact = existingContactOpt.get();

        // Handle image update
        if (image != null && !image.isEmpty()) {
            // Delete the old image if it exists
            if (existingContact.getImgUrl() != null) {
                imageStorageService.deleteFile(existingContact.getImgUrl());
            }
            // Upload the new image
            String imageUrl = imageStorageService.uploadFile(image);
            contact.setImgUrl(imageUrl);
        } else {
            // If no new image, retain the old one
            contact.setImgUrl(existingContact.getImgUrl());
        }

        // Set the existing ID to update
        contact.setId(id);
        return contactRepository.save(contact);
    }

}
