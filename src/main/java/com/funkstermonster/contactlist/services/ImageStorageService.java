package com.funkstermonster.contactlist.services;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.Identity;
import com.google.cloud.Policy;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.StorageRoles;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;

@Service
public class ImageStorageService {

    private Storage storage = StorageOptions.getDefaultInstance().getService();


    @PostConstruct
    public void initFirebase() throws IOException {
        FileInputStream serviceAccount = new FileInputStream("src/main/resources/contactlist-4e422-firebase-adminsdk-ev8xa-64ea7cb174.json");
        storage = StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setProjectId("contactlist-4e422")
                .build()
                .getService();
        Policy originalPolicy = storage.getIamPolicy("contactlist-4e422.appspot.com");
        storage.setIamPolicy(
                "contactlist-4e422.appspot.com",
                originalPolicy
                        .toBuilder()
                        .addIdentity(StorageRoles.objectViewer(), Identity.allUsers())
                        .build());
    }
    public String uploadFile(MultipartFile file) throws IOException {
        BlobInfo blobInfo = storage.create(
                BlobInfo.newBuilder("contactlist-4e422.appspot.com", file.getOriginalFilename()).build(),
                file.getBytes()
        );

        return blobInfo.getMediaLink();
    }

    public void deleteFile(String fileUrl) {
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        storage.delete(BlobId.of("contactlist-4e422.appspot.com", fileName));
    }

}
