package com.webScrapping.service;

import org.springframework.stereotype.Service;
import com.webScrapping.util.Trie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class TrieService {

    private static final Logger logger = LoggerFactory.getLogger(TrieService.class);
    private final Trie trie = new Trie();

    public TrieService(DatabaseService databaseService) {
        try {
            List<String> keywords = databaseService.fetchKeywords(); 
            if (keywords == null || keywords.isEmpty()) {
                logger.warn("No keywords found in the database.");
            } else {
                keywords.forEach(keyword -> {
                    trie.insert(keyword.toLowerCase()); 
                    logger.info("Inserted keyword: {}", keyword);
                });
            }
        } catch (Exception e) {
            logger.error("Error fetching keywords from the database: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize TrieService", e);
        }
    }

    
    public void addKeyword(String keyword) {
        try {
            if (keyword != null && !keyword.isEmpty()) {
                String keywordLower = keyword.toLowerCase();
                trie.insert(keywordLower); 
                logger.info("Inserted keyword: {}", keyword);
            } else {
                logger.warn("Attempted to add an empty or null keyword.");
            }
        } catch (Exception e) {
            logger.error("Error adding keyword '{}' to the Trie: {}", keyword, e.getMessage(), e);
            throw new RuntimeException("Error adding keyword to Trie", e);
        }
    }

    
    public List<String> searchKeywords(String prefix) {
        try {
            if (prefix != null && !prefix.isEmpty()) {
                return trie.searchWithPrefix(prefix.toLowerCase()); 
            } else {
                logger.warn("Search prefix is null or empty.");
                return List.of(); 
            }
        } catch (Exception e) {
            logger.error("Error searching for keywords with prefix '{}': {}", prefix, e.getMessage(), e);
            return List.of(); 
        }
    }
}
