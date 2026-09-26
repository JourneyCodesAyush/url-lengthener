package io.github.journeycodesayush.urllengthener.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "urls")
public class UrlEntity {

  @Column(nullable = false, unique = true)
  private String url;

  @Id
  @Column(nullable = false, unique = true, length = 64)
  private String hash;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getHash() {
    return hash;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}
