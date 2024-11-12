package com.webScrapping.service;

import org.jsoup.Jsoup;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webScrapping.entity.ScrapedData;
import com.webScrapping.entity.ScrapingJob;
import com.webScrapping.repository.ScrapedDataRepository;
import com.webScrapping.repository.ScrapingJobRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ScraperService {

	private final ScrapedDataRepository scrapedDataRepository;
	private final ScrapingJobRepository scrapingJobRepository;
	private final TrieService trieService;

	private static final Logger logger = LoggerFactory.getLogger(ScraperService.class);

	public ScraperService(ScrapedDataRepository scrapedDataRepository, ScrapingJobRepository scrapingJobRepository,
			TrieService trieService) {
		this.scrapedDataRepository = scrapedDataRepository;
		this.scrapingJobRepository = scrapingJobRepository;
		this.trieService = trieService;
	}

	@Async
	public void scrapeContent(String url, List<String> keywords, Long jobId) {
		try {
			String content = Jsoup.connect(url).get().text();

			boolean found = keywords.stream().anyMatch(content::contains);

			if (found) {

				ScrapedData scrapedData = new ScrapedData();
				scrapedData.setUrl(url);
				scrapedData.setContent(content);
				scrapedData.setTimestamp(LocalDateTime.now());
				scrapedData.setKeywords(String.join(", ", keywords));
				scrapedDataRepository.save(scrapedData);

				keywords.forEach(trieService::addKeyword);

				logger.info("Scraping job {}: Found keywords in URL {}", jobId, url);
			} else {
				logger.info("Scraping job {}: No keywords found in URL {}", jobId, url);
			}

			Optional<ScrapingJob> optionalJob = scrapingJobRepository.findById(jobId);
			if (optionalJob.isPresent()) {
				ScrapingJob job = optionalJob.get();
				job.setStatus("completed");
				job.setCompletedAt(LocalDateTime.now());
				scrapingJobRepository.save(job);
				logger.info("Scraping job {} completed.", jobId);
			} else {
				logger.error("Scraping job {} not found when updating status.", jobId);
			}

		} catch (IOException e) {
			logger.error("Error scraping URL {}: {}", url, e.getMessage());

			Optional<ScrapingJob> optionalJob = scrapingJobRepository.findById(jobId);
			if (optionalJob.isPresent()) {
				ScrapingJob job = optionalJob.get();
				job.setStatus("failed");
				scrapingJobRepository.save(job);
				logger.error("Scraping job {} failed due to an error.", jobId);
			} else {
				logger.error("Scraping job {} not found when updating status to failed.", jobId);
			}
		} catch (Exception e) {
			logger.error("Unexpected error occurred during scraping: {}", e.getMessage());

			Optional<ScrapingJob> optionalJob = scrapingJobRepository.findById(jobId);
			if (optionalJob.isPresent()) {
				ScrapingJob job = optionalJob.get();
				job.setStatus("failed");
				scrapingJobRepository.save(job);
				logger.error("Scraping job {} failed due to an unexpected error.", jobId);
			}
		}
	}

	public ScrapingJob createJob(List<String> urls, List<String> keywords, LocalDateTime schedule) {
		try {
			ScrapingJob job = new ScrapingJob();
			job.setUrls(String.join(", ", urls));
			job.setKeywords(String.join(", ", keywords));
			job.setStatus("pending");
			job.setScheduledAt(schedule);
			ScrapingJob savedJob = scrapingJobRepository.save(job);
			logger.info("Created new scraping job with ID {}", savedJob.getJobId());
			return savedJob;
		} catch (Exception e) {
			logger.error("Error creating new scraping job: {}", e.getMessage());
			throw new RuntimeException("Error creating scraping job", e);
		}
	}

	public List<ScrapedData> getAllScrapedData() {
		try {
			return scrapedDataRepository.findAll();
		} catch (Exception e) {
			logger.error("Error fetching scraped data: {}", e.getMessage());
			throw new RuntimeException("Error fetching scraped data", e);
		}
	}

	public ScrapingJob getJobStatus(Long jobId) {
		try {
			return scrapingJobRepository.findById(jobId)
					.orElseThrow(() -> new RuntimeException("Job with ID " + jobId + " not found"));
		} catch (Exception e) {
			logger.error("Error fetching job status for job ID {}: {}", jobId, e.getMessage());
			throw new RuntimeException("Error fetching job status", e);
		}
	}
}
