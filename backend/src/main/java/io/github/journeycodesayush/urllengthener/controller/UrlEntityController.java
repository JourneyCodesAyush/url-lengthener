package io.github.journeycodesayush.urllengthener.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.journeycodesayush.urllengthener.dto.Url;
import io.github.journeycodesayush.urllengthener.entity.UrlEntity;
import io.github.journeycodesayush.urllengthener.service.UrlEntityService;

@RestController
@RequestMapping("/api/urls")
@CrossOrigin(origins = "http://localhost:5173")
public class UrlEntityController {

    private UrlEntityService urllengthenerService;

    public UrlEntityController(UrlEntityService urllengthenerService) {
        this.urllengthenerService = urllengthenerService;
    }

    @PostMapping
    public ResponseEntity<UrlEntity> createUrlEntity(@RequestBody Url urlRequest) {
        UrlEntity urlEntity = urllengthenerService.createUrlEntity(urlRequest);

        if (urlEntity == null) {
            return ResponseEntity
                    .internalServerError()
                    .build();
        }
        return ResponseEntity
                .ok(urlEntity);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Url> getUrlEntity(@PathVariable String hash) {
        UrlEntity urlEntityResponse = urllengthenerService.getUrlEntity(hash);
        if (urlEntityResponse == null) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        Url url = new Url();
        url.setUrl(urlEntityResponse.getUrl());
        return ResponseEntity
                .ok(url);
    }

}
