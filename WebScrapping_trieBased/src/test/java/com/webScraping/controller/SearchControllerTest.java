package com.webScraping.controller;

import com.webScrapping.controller.SearchController;
import com.webScrapping.service.TrieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class SearchControllerTest {

	@Mock
	private TrieService trieService;

	@InjectMocks
	private SearchController searchController;

	private MockMvc mockMvc;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(searchController).build();
	}

	@Test
	public void testSearch_Success() throws Exception {
		String prefix = "key";
		List<String> mockResults = List.of("url1", "url2", "url3");
		when(trieService.searchKeywords(prefix)).thenReturn(mockResults);

		mockMvc.perform(post("/api/v1/search").contentType(MediaType.APPLICATION_JSON)
				.content("{\"prefix\": \"" + prefix + "\", \"limit\": 3}")).andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("success")).andExpect(jsonPath("$.results").isArray())
				.andExpect(jsonPath("$.results.length()").value(3))
				.andExpect(jsonPath("$.results[0].url").value("url1"))
				.andExpect(jsonPath("$.results[1].url").value("url2"))
				.andExpect(jsonPath("$.results[2].url").value("url3"));

		verify(trieService, times(1)).searchKeywords(prefix);
	}

	@Test
	public void testSearch_NoResults() throws Exception {
		String prefix = "key";
		List<String> mockResults = List.of();
		when(trieService.searchKeywords(prefix)).thenReturn(mockResults);

		mockMvc.perform(post("/api/v1/search").contentType(MediaType.APPLICATION_JSON)
				.content("{\"prefix\": \"" + prefix + "\", \"limit\": 5}")).andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("error"))
				.andExpect(jsonPath("$.message").value("No matching results found."));

		verify(trieService, times(1)).searchKeywords(prefix);
	}

	@Test
	public void testSearch_EmptyPrefix() throws Exception {
		mockMvc.perform(post("/api/v1/search").contentType(MediaType.APPLICATION_JSON)
				.content("{\"prefix\": \"\", \"limit\": 5}")).andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("error"))
				.andExpect(jsonPath("$.message").value("Prefix cannot be empty."));
	}

	@Test
	public void testSearch_Error() throws Exception {
		String prefix = "key";
		when(trieService.searchKeywords(prefix)).thenThrow(new RuntimeException("Service error"));

		mockMvc.perform(post("/api/v1/search").contentType(MediaType.APPLICATION_JSON)
				.content("{\"prefix\": \"" + prefix + "\", \"limit\": 5}")).andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("error"))
				.andExpect(jsonPath("$.message").value("An error occurred while searching: Service error"));
	}
}
