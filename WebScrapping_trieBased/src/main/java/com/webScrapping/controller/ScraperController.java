package com.webScrapping.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.webScrapping.entity.ScrapedData;
import com.webScrapping.entity.ScrapingJob;
import com.webScrapping.service.ScraperService;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1")
public class ScraperController {

    private final ScraperService scraperService;

    public ScraperController(ScraperService scraperService) {
        this.scraperService = scraperService;
    }

    @PostMapping("/scrape")
    public Map<String, Object> initiateScraping(@RequestBody Map<String, Object> request) {
        try {
            List<String> urls = (List<String>) request.get("urls");
            List<String> keywords = (List<String>) request.get("keywords");
            String scheduleString = (String) request.get("schedule");

            
            if (urls == null || urls.isEmpty()) {
                return Map.of("status", "error", "message", "URLs cannot be empty.");
            }
            if (keywords == null || keywords.isEmpty()) {
                return Map.of("status", "error", "message", "Keywords cannot be empty.");
            }
            if (scheduleString == null || scheduleString.isEmpty()) {
                return Map.of("status", "error", "message", "Schedule cannot be empty.");
            }

            LocalDateTime schedule;
            try {
                schedule = LocalDateTime.parse(scheduleString);
            } catch (Exception e) {
                return Map.of("status", "error", "message", "Invalid schedule format. Please use ISO_LOCAL_DATE_TIME format.");
            }

            ScrapingJob job = scraperService.createJob(urls, keywords, schedule);
            urls.forEach(url -> scraperService.scrapeContent(url, keywords, job.getJobId()));

            return Map.of(
            	    "status", "success",
            	    "message", "Scraping initiated successfully.",  
            	    "jobId", job.getJobId(),
            	    "scheduledAt", schedule
            	);

        } catch (Exception e) {
            return Map.of("status", "error", "message", "An error occurred while initiating the scraping job: " + e.getMessage());
        }
    }

    @GetMapping("/status/{jobId}")
    public Map<String, Object> getJobStatus(@PathVariable Long jobId) {
        try {
            
            ScrapingJob job = scraperService.getJobStatus(jobId);

            
            if (job == null) {
                return Map.of(
                    "status", "error",
                    "message", "Job not found"
                );
            }

            Map<String, Object> response = new HashMap<>();
            response.put("status", job.getStatus());
            response.put("jobId", job.getJobId());

            if (job.getUrls() != null && !job.getUrls().isEmpty()) {
                response.put("urlsScraped", job.getUrls().split(", "));
            } else {
                response.put("urlsScraped", new String[0]);
            }

            if (job.getKeywords() != null && !job.getKeywords().isEmpty()) {
                response.put("keywordsFound", job.getKeywords().split(", "));
            } else {
                response.put("keywordsFound", new String[0]);
            }

            
            response.put("dataSize", "2 MB"); 

            
            if (job.getCompletedAt() != null) {
                response.put("finishedAt", job.getCompletedAt());
            } else {
                response.put("finishedAt", "Not yet completed");
            }

            return response;
        } catch (Exception e) {
            return Map.of("status", "error", "message", "An error occurred while fetching the job status: " + e.getMessage());
        }
    }

    @GetMapping("/scraped-data")
    public Map<String, Object> getScrapedData() {
        try {
            List<ScrapedData> data = scraperService.getAllScrapedData();
            return Map.of("status", "success", "data", data);
        } catch (Exception e) {
            return Map.of("status", "error", "message", "An error occurred while fetching scraped data: " + e.getMessage());
        }
    }
}
