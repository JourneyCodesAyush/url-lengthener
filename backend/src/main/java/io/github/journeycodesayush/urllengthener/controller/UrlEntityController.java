package io.github.journeycodesayush.urllengthener.controller;

import io.github.journeycodesayush.urllengthener.dto.Url;
import io.github.journeycodesayush.urllengthener.dto.UrlBackup;
import io.github.journeycodesayush.urllengthener.entity.UrlEntity;
import io.github.journeycodesayush.urllengthener.service.UrlEntityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/urls")
@CrossOrigin(origins = "http://localhost:5173")
public class UrlEntityController {

  private UrlEntityService urlEntityService;

  public UrlEntityController(UrlEntityService urlEntityService) {
    this.urlEntityService = urlEntityService;
  }

  @PostMapping
  public ResponseEntity<UrlEntity> createUrlEntity(@RequestBody Url urlRequest) {
    UrlEntity urlEntity = urlEntityService.createUrlEntity(urlRequest);

    if (urlEntity == null) {
      return ResponseEntity.internalServerError().build();
    }
    return ResponseEntity.ok(urlEntity);
  }

  @GetMapping("/{hash}")
  public ResponseEntity<Url> getUrlEntity(@PathVariable String hash) {
    UrlEntity urlEntityResponse = urlEntityService.getUrlEntity(hash);
    if (urlEntityResponse == null) {
      return ResponseEntity.notFound().build();
    }

    Url url = new Url();
    url.setUrl(urlEntityResponse.getUrl());
    url.setHash(urlEntityResponse.getHash());
    url.setCreatedAt(urlEntityResponse.getCreatedAt());
    return ResponseEntity.ok(url);
  }

  @GetMapping("/export")
  public ResponseEntity<UrlBackup> exportUrl() {
    UrlBackup urlBackup = urlEntityService.exportUrlBackup();

    return ResponseEntity.ok(urlBackup);
  }

  @PostMapping("/import")
  public ResponseEntity<UrlBackup> importUrlEntity(@RequestBody UrlBackup urlBackupRequest) {
    UrlBackup urlBackupResponse = urlEntityService.importUrlBackup(urlBackupRequest);

    if (urlBackupResponse == null) {
      return ResponseEntity.badRequest().build();
    }

    return ResponseEntity.ok(urlBackupResponse);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleBadRequest(IllegalArgumentException e) {
    return ResponseEntity.badRequest().body(e.getMessage());
  }
}
