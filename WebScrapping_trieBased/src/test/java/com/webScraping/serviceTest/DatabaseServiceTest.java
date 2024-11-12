package com.webScraping.serviceTest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.jdbc.core.JdbcTemplate;

import com.webScrapping.service.DatabaseService;

import org.springframework.dao.DataAccessException;

import java.util.Arrays;
import java.util.List;

public class DatabaseServiceTest {

	@Mock
	private JdbcTemplate jdbcTemplate;

	@InjectMocks
	private DatabaseService databaseService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}
	
	
	@Test
	public void testFetchKeywords_Failure_UnexpectedError() {
	    when(jdbcTemplate.queryForList(anyString(), eq(String.class)))
	        .thenThrow(new RuntimeException("Unexpected error"));

	    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
	        databaseService.fetchKeywords();
	    });

	    assertEquals("Unexpected error while fetching keywords", exception.getMessage());
	}



	@Test
    public void testFetchKeywords_Failure_UnexpectedError1() {

		when(jdbcTemplate.queryForList(anyString(), eq(String.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            databaseService.fetchKeywords();
        });
        
        assertEquals("Unexpected error while fetching keywords", exception.getMessage());
    }
}
