package io.github.journeycodesayush.urllengthener.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.journeycodesayush.urllengthener.dto.Url;
import io.github.journeycodesayush.urllengthener.dto.UrlBackup;
import io.github.journeycodesayush.urllengthener.entity.UrlEntity;
import io.github.journeycodesayush.urllengthener.repository.UrlEntityRepository;

@ExtendWith(MockitoExtension.class)
class UrlEntityServiceTest {

    @Mock
    private UrlEntityRepository urlEntityRepository;

    private UrlEntityService urlEntityService;

    // No @InjectMocks this time — constructing manually so it's visible
    // exactly what's being wired together.
    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        urlEntityService = new UrlEntityService(urlEntityRepository, 1);
    }

    // ---------- createUrlEntity ----------

    @Test
    void createUrlEntity_savesNewEntry_whenHashDoesNotExist() {
        Url request = new Url();
        request.setUrl("https://example.com");

        when(urlEntityRepository.findById(anyString())).thenReturn(Optional.empty());
        when(urlEntityRepository.save(any(UrlEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UrlEntity result = urlEntityService.createUrlEntity(request);

        assertNotNull(result);
        assertEquals("https://example.com", result.getUrl());
        assertNotNull(result.getHash());
        assertEquals(64, result.getHash().length());
        verify(urlEntityRepository).save(any(UrlEntity.class));
    }

    @Test
    void createUrlEntity_returnsExisting_whenHashAlreadyExists() {
        UrlEntity existing = new UrlEntity();
        existing.setUrl("https://example.com");
        existing.setHash("existing-hash");
        existing.setCreatedAt(Instant.parse("2020-01-01T00:00:00Z"));

        when(urlEntityRepository.findById(anyString())).thenReturn(Optional.of(existing));

        Url request = new Url();
        request.setUrl("https://example.com");

        UrlEntity result = urlEntityService.createUrlEntity(request);

        assertEquals(existing, result);
        assertEquals(Instant.parse("2020-01-01T00:00:00Z"), result.getCreatedAt());
        verify(urlEntityRepository, never()).save(any());
    }

    @Test
    void createUrlEntity_isIdempotent_sameUrlProducesSameHash() {
        when(urlEntityRepository.findById(anyString())).thenReturn(Optional.empty());
        when(urlEntityRepository.save(any(UrlEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Url request = new Url();
        request.setUrl("https://example.com");

        UrlEntity first = urlEntityService.createUrlEntity(request);
        UrlEntity second = urlEntityService.createUrlEntity(request);

        assertEquals(first.getHash(), second.getHash());
    }

    // ---------- getUrlEntity ----------

    @Test
    void getUrlEntity_returnsEntity_whenFound() {
        UrlEntity entity = new UrlEntity();
        entity.setHash("abc123");
        entity.setUrl("https://example.com");

        when(urlEntityRepository.findById("abc123")).thenReturn(Optional.of(entity));

        UrlEntity result = urlEntityService.getUrlEntity("abc123");

        assertEquals(entity, result);
    }

    @Test
    void getUrlEntity_returnsNull_whenNotFound() {
        when(urlEntityRepository.findById("unknown")).thenReturn(Optional.empty());

        UrlEntity result = urlEntityService.getUrlEntity("unknown");

        assertNull(result);
    }

    // ---------- exportUrlBackup ----------

    @Test
    void exportUrlBackup_mapsAllEntitiesToUrls() {
        UrlEntity entity1 = new UrlEntity();
        entity1.setHash("hash1");
        entity1.setUrl("https://example.com/a");
        entity1.setCreatedAt(Instant.parse("2021-01-01T00:00:00Z"));

        UrlEntity entity2 = new UrlEntity();
        entity2.setHash("hash2");
        entity2.setUrl("https://example.com/b");
        entity2.setCreatedAt(Instant.parse("2021-02-01T00:00:00Z"));

        when(urlEntityRepository.findAll()).thenReturn(List.of(entity1, entity2));

        UrlBackup backup = urlEntityService.exportUrlBackup();

        assertEquals(1, backup.getVersion());
        assertEquals(2, backup.getUrls().size());
        assertEquals("hash1", backup.getUrls().get(0).getHash());
        assertEquals("https://example.com/a", backup.getUrls().get(0).getUrl());
    }

    @Test
    void exportUrlBackup_returnsEmptyList_whenNoEntities() {
        when(urlEntityRepository.findAll()).thenReturn(List.of());

        UrlBackup backup = urlEntityService.exportUrlBackup();

        assertTrue(backup.getUrls().isEmpty());
    }

    // ---------- importUrlBackup ----------

    @Test
    void importUrlBackup_ignoresIncomingHash_rederivesFromUrl() {
        Url entry = new Url();
        entry.setHash("this-is-fake-and-should-be-ignored");
        entry.setUrl("https://example.com");

        UrlBackup request = new UrlBackup();
        request.setVersion(1);
        request.setUrls(List.of(entry));

        when(urlEntityRepository.findById(anyString())).thenReturn(Optional.empty());
        when(urlEntityRepository.save(any(UrlEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UrlBackup result = urlEntityService.importUrlBackup(request);

        String realHash = result.getUrls().get(0).getHash();
        assertNotEquals("this-is-fake-and-should-be-ignored", realHash);
        assertEquals(64, realHash.length());
    }

    @Test
    void importUrlBackup_isNoOp_whenHashAlreadyExists() {
        UrlEntity existing = new UrlEntity();
        existing.setHash("real-hash");
        existing.setUrl("https://example.com");
        existing.setCreatedAt(Instant.parse("2020-01-01T00:00:00Z"));

        when(urlEntityRepository.findById(anyString())).thenReturn(Optional.of(existing));

        Url entry = new Url();
        entry.setUrl("https://example.com");

        UrlBackup request = new UrlBackup();
        request.setVersion(1);
        request.setUrls(List.of(entry));

        UrlBackup result = urlEntityService.importUrlBackup(request);

        // original createdAt preserved, not overwritten
        assertEquals(Instant.parse("2020-01-01T00:00:00Z"), result.getUrls().get(0).getCreatedAt());
        verify(urlEntityRepository, never()).save(any());
    }

    @Test
    void importUrlBackup_savesNewEntry_whenHashDoesNotExist() {
        when(urlEntityRepository.findById(anyString())).thenReturn(Optional.empty());
        when(urlEntityRepository.save(any(UrlEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Url entry = new Url();
        entry.setUrl("https://example.com");

        UrlBackup request = new UrlBackup();
        request.setVersion(1);
        request.setUrls(List.of(entry));

        UrlBackup result = urlEntityService.importUrlBackup(request);

        assertEquals(1, result.getUrls().size());
        assertNotNull(result.getUrls().get(0).getCreatedAt());
        verify(urlEntityRepository).save(any(UrlEntity.class));
    }

    @Test
    void importUrlBackup_throwsForUnequalVersionNumbers() {
        Url entry = new Url();
        entry.setUrl("https://example.com");

        UrlBackup request = new UrlBackup();
        request.setVersion(2);
        request.setUrls(List.of(entry));

        assertThrows(IllegalArgumentException.class,
                () -> urlEntityService.importUrlBackup(request));
    }
}