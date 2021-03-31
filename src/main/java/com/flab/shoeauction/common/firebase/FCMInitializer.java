package com.flab.shoeauction.common.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FCMInitializer {

    @Value("${fcm.certification}")
    private String GOOGLE_APPLICATION_CREDENTIALS;

    @PostConstruct
    public void initialize() {
        ClassPathResource resource = new ClassPathResource(GOOGLE_APPLICATION_CREDENTIALS);

        try (InputStream is = resource.getInputStream()) {
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(is))
                .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("FirebaseApp initialization complete");
            }
        } catch (IOException exception) {
            log.error("FirebaseApp initialization fail");
        }
    }
}