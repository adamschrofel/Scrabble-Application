package ca.adamschrofel.scrabble.dto;

import java.util.List;

/**
 * JSON-friendly grouping of playable rack-only words by length.
 */
public record LengthGroup(int length, List<String> words) {}
