package io.github.journeycodesayush.urllengthener.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.github.journeycodesayush.urllengthener.dto.Url;
import io.github.journeycodesayush.urllengthener.dto.UrlBackup;
import io.github.journeycodesayush.urllengthener.entity.UrlEntity;
import io.github.journeycodesayush.urllengthener.repository.UrlEntityRepository;

@Service
public class UrlEntityService {

    private UrlEntityRepository urlEntityRepository;

    private int backupVersion;

    public UrlEntityService(UrlEntityRepository urlEntityRepository,
            @Value("${app.backup.version}") int backupVersion) {
        this.urlEntityRepository = urlEntityRepository;
        this.backupVersion = backupVersion;
    }

    public UrlEntity createUrlEntity(Url urlRequest) {
        try {
            String hashedUrl = getSha256(urlRequest.getUrl());

            Optional<UrlEntity> urlEntityOptional = urlEntityRepository.findById(hashedUrl);
            if (urlEntityOptional.isPresent()) {
                return urlEntityOptional.get();
            }

            UrlEntity urlEntity = new UrlEntity();
            urlEntity.setCreatedAt(Instant.now());
            urlEntity.setHash(hashedUrl);
            urlEntity.setUrl(urlRequest.getUrl());

            return urlEntityRepository.save(urlEntity);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm should always be available", e);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public UrlEntity getUrlEntity(String hashedUrl) {
        Optional<UrlEntity> existingUrlEntity = urlEntityRepository.findById(hashedUrl);

        return existingUrlEntity.orElse(null);
    }

    public UrlBackup exportUrlBackup() {
        List<Url> urls = urlEntityRepository.findAll()
                .stream()
                .map((entity) -> {
                    Url url = new Url();
                    url.setCreatedAt(entity.getCreatedAt());
                    url.setUrl(entity.getUrl());
                    url.setHash(entity.getHash());
                    return url;
                })
                .toList();

        UrlBackup urlBackup = new UrlBackup();
        urlBackup.setVersion(backupVersion);
        urlBackup.setUrls(urls);

        return urlBackup;
    }

    public UrlBackup importUrlBackup(UrlBackup urlBackupRequest) {
        if (urlBackupRequest.getVersion() != backupVersion) {
            throw new IllegalArgumentException(
                    "Unsupported backup version: " + urlBackupRequest.getVersion() + ". Expected: " + backupVersion);
        }

        List<Url> imported = new ArrayList<>();
        for (Url url : urlBackupRequest.getUrls()) {
            try {
                String hashedUrl = getSha256(url.getUrl());

                Optional<UrlEntity> existing = urlEntityRepository.findById(hashedUrl);
                UrlEntity urlEntity;

                if (existing.isPresent()) {
                    urlEntity = existing.get();
                } else {
                    urlEntity = new UrlEntity();
                    urlEntity.setHash(hashedUrl);
                    urlEntity.setCreatedAt(Instant.now());
                    urlEntity.setUrl(url.getUrl());

                    urlEntity = urlEntityRepository.save(urlEntity);
                }

                Url result = new Url();
                result.setUrl(urlEntity.getUrl());
                result.setHash(urlEntity.getHash());
                result.setCreatedAt(urlEntity.getCreatedAt());

                imported.add(result);

            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException("SHA-256 algorithm should always be available", e);
            }
        }
        UrlBackup urlBackup = new UrlBackup();
        urlBackup.setVersion(urlBackupRequest.getVersion());
        urlBackup.setUrls(imported);

        return urlBackup;
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
