package com.webScrapping.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.webScrapping.entity.ScrapingJob;

public interface ScrapingJobRepository extends JpaRepository<ScrapingJob, Long> {
}