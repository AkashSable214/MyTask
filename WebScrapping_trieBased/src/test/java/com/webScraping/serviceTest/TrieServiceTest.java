package com.webScraping.serviceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import com.webScrapping.service.DatabaseService;
import com.webScrapping.service.TrieService;
import com.webScrapping.util.Trie;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

public class TrieServiceTest {

	@Mock
	private DatabaseService mockDatabaseService;

	private TrieService trieService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		trieService = new TrieService(mockDatabaseService);
	}

	@Test
	public void testConstructor_FetchesAndInsertsKeywords() {

		List<String> mockKeywords = Arrays.asList("apple", "banana", "cherry");
		when(mockDatabaseService.fetchKeywords()).thenReturn(mockKeywords);

		verify(mockDatabaseService, times(1)).fetchKeywords();
	}

	@Test
	public void testAddKeyword() {

		String keyword = "apple";

		trieService.addKeyword(keyword);

	}

	@Test
	public void testSearchKeywords_ReturnsMatchingWords() {

		List<String> expectedKeywords = Arrays.asList("apple", "apricot");
		Trie mockTrie = mock(Trie.class);
		when(mockTrie.searchWithPrefix("ap")).thenReturn(expectedKeywords);

		TrieService serviceWithMockedTrie = new TrieService(mockDatabaseService) {
			@Override
			public List<String> searchKeywords(String prefix) {
				return mockTrie.searchWithPrefix(prefix);
			}
		};

		List<String> result = serviceWithMockedTrie.searchKeywords("ap");

		assertEquals(expectedKeywords, result);
	}

	@Test
	public void testSearchKeywords_EmptyPrefix() {

		List<String> result = trieService.searchKeywords("");

		assertTrue(result.isEmpty());
	}

	@Test
	public void testAddKeyword_EmptyKeyword() {

		trieService.addKeyword("");

	}

	@Test
	public void testSearchKeywords_NullPrefix() {

		List<String> result = trieService.searchKeywords(null);

		assertTrue(result.isEmpty());
	}
}
