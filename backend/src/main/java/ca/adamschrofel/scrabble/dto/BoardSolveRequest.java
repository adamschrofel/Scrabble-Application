package ca.adamschrofel.scrabble.dto;

/**
 * Request payload for generating best plays on the current server-side board.
 *
 * @param rack the player's rack tiles (A–Z and '?' / '*')
 * @param limit maximum number of plays to return
 */
public record BoardSolveRequest(String rack, Integer limit) {}
