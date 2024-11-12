package com.webScrapping.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.webScrapping.entity.ScrapedData;

public interface ScrapedDataRepository extends JpaRepository<ScrapedData, Long> {
}