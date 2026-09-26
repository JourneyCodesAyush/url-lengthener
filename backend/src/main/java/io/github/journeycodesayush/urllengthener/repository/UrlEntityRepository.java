package io.github.journeycodesayush.urllengthener.repository;

import io.github.journeycodesayush.urllengthener.entity.UrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UrlEntityRepository extends JpaRepository<UrlEntity, String> {}
