package com.webScrapping.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DatabaseService {

	private static final Logger logger = LoggerFactory.getLogger(DatabaseService.class);

	@Autowired
	public JdbcTemplate jdbcTemplate;

	public List<String> fetchKeywords() {
		try {
			List<String> keywords = jdbcTemplate.queryForList("SELECT keywords FROM scraping_job", String.class);
			return keywords.stream().flatMap(keyword -> Arrays.stream(keyword.split(",\\s*")))
					.collect(Collectors.toList());
		} catch (DataAccessException e) {
			throw new RuntimeException("Failed to fetch keywords from the database", e);
		} catch (Exception e) {
			throw new RuntimeException("Unexpected error while fetching keywords", e);
		}
	}

}
