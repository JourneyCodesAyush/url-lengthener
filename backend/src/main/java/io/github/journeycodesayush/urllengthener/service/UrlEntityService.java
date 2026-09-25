package io.github.journeycodesayush.urllengthener.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;

import io.github.journeycodesayush.urllengthener.dto.Url;
import io.github.journeycodesayush.urllengthener.entity.UrlEntity;
import io.github.journeycodesayush.urllengthener.repository.UrlEntityRepository;

@Service
public class UrlEntityService {

    private UrlEntityRepository urllengthenerRepository;

    public UrlEntityService(UrlEntityRepository urllengthenerRepository) {
        this.urllengthenerRepository = urllengthenerRepository;
    }

    public UrlEntity createUrlEntity(Url urlRequest) {
        try {
            String hashedUrl = getSha256(urlRequest.getUrl());

            Optional<UrlEntity> urlEntityOptional = urllengthenerRepository.findById(hashedUrl);
            if (urlEntityOptional.isPresent()) {
                return urlEntityOptional.get();
            }

            UrlEntity urlEntity = new UrlEntity();
            urlEntity.setCreatedAt(Instant.now());
            urlEntity.setHashedUrl(hashedUrl);
            urlEntity.setUrl(urlRequest.getUrl());

            return urllengthenerRepository.save(urlEntity);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm should always be available", e);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public UrlEntity getUrlEntity(String hashedUrl) {
        Optional<UrlEntity> existingUrlEntity = urllengthenerRepository.findById(hashedUrl);

        return existingUrlEntity.orElse(null);
    }

    public static String getSha256(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

        // Convert byte array to hexadecimal string
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
