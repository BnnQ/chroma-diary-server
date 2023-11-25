package me.bnnq.chromadiary.Services;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import me.bnnq.chromadiary.Configuration.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class FirebaseStorageService
{
    private final Storage storage;

    public FirebaseStorageService() throws IOException
    {
        FileInputStream serviceAccount = new FileInputStream(Environment.getFirebaseConfigPath());
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .setStorageBucket(Environment.getFirebaseBucketName())
                .build();

        FirebaseApp.initializeApp(options);

        this.storage = StorageOptions.newBuilder()
                .setCredentials(credentials)
                .build()
                .getService();
    }

    public String uploadFile(MultipartFile file) throws IOException
    {
        String blobName = "uploads/" + file.getOriginalFilename();
        BlobId blobId = BlobId.of(Environment.getFirebaseBucketName(), blobName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
        Blob blob = storage.create(blobInfo, file.getBytes());

        String encodedBlobName = URLEncoder.encode(blobName, StandardCharsets.UTF_8);
        String firebaseUrl = String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                Environment.getFirebaseBucketName(), encodedBlobName);

        return firebaseUrl;
    }
}
