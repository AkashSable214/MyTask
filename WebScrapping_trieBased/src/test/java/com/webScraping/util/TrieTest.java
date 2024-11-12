package com.webScraping.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.webScrapping.util.Trie;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class TrieTest {

	private Trie trie;

	@BeforeEach
	public void setUp() {
		trie = new Trie();
	}

	@Test
	public void testInsert_SingleWord() {
		trie.insert("apple");

		List<String> result = trie.searchWithPrefix("apple");

		assertEquals(1, result.size());
		assertTrue(result.contains("apple"));
	}

	@Test
	public void testInsert_MultipleWords() {
		trie.insert("apple");
		trie.insert("app");
		trie.insert("appl");

		List<String> result = trie.searchWithPrefix("app");

		assertTrue(result.contains("apple"));
		assertTrue(result.contains("app"));
		assertTrue(result.contains("appl"));
	}

	@Test
	public void testInsert_WithInvalidCharacters() {
		trie.insert("app1");
		trie.insert("apple");

		List<String> result = trie.searchWithPrefix("apple");

		assertTrue(result.contains("apple"));
		assertFalse(result.contains("app1"));
	}

	@Test
	public void testSearchWithPrefix_EmptyPrefix() {
		trie.insert("apple");
		List<String> result = trie.searchWithPrefix("");

		assertTrue(result.isEmpty());
	}

	@Test
	public void testSearchWithPrefix_NoMatchingWords() {
		trie.insert("apple");
		trie.insert("banana");

		List<String> result = trie.searchWithPrefix("grape");

		assertTrue(result.isEmpty());
	}

	@Test
	public void testSearchWithPrefix_PrefixThatMatches() {
		trie.insert("apple");
		trie.insert("appetizer");
		trie.insert("apricot");

		List<String> result = trie.searchWithPrefix("ap");

		assertTrue(result.contains("apple"));
		assertTrue(result.contains("appetizer"));
		assertTrue(result.contains("apricot"));
	}

	@Test
	public void testInsert_EmptyString() {
		trie.insert("");

		List<String> result = trie.searchWithPrefix("");

		assertTrue(result.isEmpty());
	}

	@Test
	public void testInsert_NullString() {
		trie.insert(null);

		List<String> result = trie.searchWithPrefix("ap");

		assertTrue(result.isEmpty());
	}
}
