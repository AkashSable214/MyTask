package com.webScrapping.controller;

import org.springframework.web.bind.annotation.*;
import com.webScrapping.service.TrieService;

import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1")
public class SearchController {

	private final TrieService trieService;

	public SearchController(TrieService trieService) {
		this.trieService = trieService;
	}

	@PostMapping("/search")
	public Map<String, Object> search(@RequestBody Map<String, Object> request) {
		try {
			String prefix = (String) request.get("prefix");
			int limit = (int) request.getOrDefault("limit", 5);

			if (prefix == null || prefix.isEmpty()) {
				return Map.of("status", "error", "message", "Prefix cannot be empty.");
			}

			List<String> results = trieService.searchKeywords(prefix);

			if (results.isEmpty()) {
				return Map.of("status", "error", "message", "No matching results found.");
			}

			List<Map<String, String>> matchedResults = results.stream().map(url -> {
				return Map.of("url", url, "matchedContent", "Sample matched content for " + url, "timestamp",
						LocalDateTime.now().toString());
			}).toList();

			return Map.of("status", "success", "results", matchedResults.stream().limit(limit).toList());
		} catch (Exception e) {
			return Map.of("status", "error", "message", "An error occurred while searching: " + e.getMessage());
		}
	}
}
