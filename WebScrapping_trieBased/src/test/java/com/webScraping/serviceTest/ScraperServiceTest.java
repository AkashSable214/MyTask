package com.webScraping.serviceTest;

import com.webScrapping.entity.ScrapedData;
import com.webScrapping.entity.ScrapingJob;
import com.webScrapping.repository.ScrapedDataRepository;
import com.webScrapping.repository.ScrapingJobRepository;
import com.webScrapping.service.ScraperService;
import com.webScrapping.service.TrieService;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ScraperServiceTest {

    private ScraperService scraperService;

    @Mock
    private ScrapedDataRepository scrapedDataRepository;
    
    @Mock
    private ScrapingJobRepository scrapingJobRepository;
    
    @Mock
    private TrieService trieService;
    
    @Mock
    private Logger logger;
    
    private MockedStatic<Jsoup> jsoupMockStatic;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        scraperService = new ScraperService(scrapedDataRepository, scrapingJobRepository, trieService);
    }

    @AfterEach
    void tearDown() {
        if (jsoupMockStatic != null) {
            jsoupMockStatic.close();
        }
    }

    @Test
    void testScrapeContentNoKeywordsFound() throws IOException {
        
        String url = "http://example.com";
        List<String> keywords = List.of("notfound");
        Long jobId = 1L;

        ScrapingJob job = new ScrapingJob();
        job.setJobId(jobId);
        when(scrapingJobRepository.findById(jobId)).thenReturn(Optional.of(job));

        Connection connection = mock(Connection.class);
        Document document = mock(Document.class);

        jsoupMockStatic = mockStatic(Jsoup.class);
        when(Jsoup.connect(url)).thenReturn(connection);
        when(connection.get()).thenReturn(document);
        when(document.text()).thenReturn("This is some other content.");

       
        scraperService.scrapeContent(url, keywords, jobId);

        verify(scrapingJobRepository, times(1)).save(argThat(jobToSave -> 
            jobToSave.getStatus().equals("completed") && jobToSave.getJobId().equals(jobId)
        ));
        verify(scrapedDataRepository, never()).save(any(ScrapedData.class));
    }

    @Test
    void testScrapeContentIOException() throws IOException {
        
        String url = "http://example.com";
        List<String> keywords = List.of("notfound");
        Long jobId = 1L;

        ScrapingJob job = new ScrapingJob();
        job.setJobId(jobId);
        when(scrapingJobRepository.findById(jobId)).thenReturn(Optional.of(job));

        jsoupMockStatic = mockStatic(Jsoup.class);
        when(Jsoup.connect(url)).thenThrow(new RuntimeException("Connection error"));

        
        scraperService.scrapeContent(url, keywords, jobId);

        verify(scrapingJobRepository, times(1)).save(argThat(jobToSave -> 
            jobToSave.getStatus().equals("failed") && jobToSave.getJobId().equals(jobId)
        ));
    }

}
