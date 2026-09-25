package io.github.journeycodesayush.urllengthener.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.journeycodesayush.urllengthener.entity.UrlEntity;

public interface UrlEntityRepository extends JpaRepository<UrlEntity, String> {

}
