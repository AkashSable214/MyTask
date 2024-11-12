package com.webScrapping.util;

import java.util.ArrayList;
import java.util.List;

public class Trie {

    private static final int ALPHABET_SIZE = 26; // Number of lowercase English letters
    private TrieNode root;

    // Constructor
    public Trie() {
        root = new TrieNode();
    }

    // Method to insert a word into the Trie
    public void insert(String word) {
        if (word == null || word.isEmpty()) {
            return; // Avoid inserting null or empty strings
        }

        TrieNode currentNode = root;
        for (char c : word.toCharArray()) {
            if (c < 'a' || c > 'z') {
                continue; // Skip characters that are not lowercase letters
            }
            int index = c - 'a'; // Calculate index (0 for 'a', 1 for 'b', ..., 25 for 'z')
            if (currentNode.children[index] == null) {
                currentNode.children[index] = new TrieNode();
            }
            currentNode = currentNode.children[index];
        }
        currentNode.isEndOfWord = true; // Mark the end of the word
    }

    // Method to search for words with a specific prefix
    public List<String> searchWithPrefix(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return new ArrayList<>(); // Return empty list for null or empty prefix
        }

        TrieNode currentNode = root;
        for (char c : prefix.toCharArray()) {
            if (c < 'a' || c > 'z') {
                return new ArrayList<>(); // Return empty list if prefix contains invalid characters
            }
            int index = c - 'a'; // Calculate index
            if (currentNode.children[index] == null) {
                return new ArrayList<>(); // No matching prefix found
            }
            currentNode = currentNode.children[index];
        }

        List<String> result = new ArrayList<>();
        findWordsWithPrefix(currentNode, prefix, result); // Find all words with the prefix
        return result;
    }

    // Helper method to find all words with a given prefix
    private void findWordsWithPrefix(TrieNode node, String prefix, List<String> result) {
        if (node.isEndOfWord) {
            result.add(prefix); // Add the word to the result if it's a complete word
        }

        for (char i = 'a'; i <= 'z'; i++) {
            if (node.children[i - 'a'] != null) {
                findWordsWithPrefix(node.children[i - 'a'], prefix + i, result); // Recursively find words
            }
        }
    }

    // Inner class representing a Trie node
    private static class TrieNode {
        TrieNode[] children = new TrieNode[ALPHABET_SIZE]; // Array of children nodes
        boolean isEndOfWord; // Flag to mark the end of a word
    }
}