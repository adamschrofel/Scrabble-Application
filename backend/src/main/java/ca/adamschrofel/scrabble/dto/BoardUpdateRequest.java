package ca.adamschrofel.scrabble.dto;

import java.util.List;

/**
 * Batch update request for setting multiple board squares in one call.
 *
 * @param tiles tile updates to apply
 */
public record BoardUpdateRequest(List<BoardTileUpdate> tiles) {}
