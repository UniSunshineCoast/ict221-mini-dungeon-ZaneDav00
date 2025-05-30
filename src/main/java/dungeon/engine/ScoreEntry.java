/**
 * Represents a single entry in the top scores list, containing player name, score, and date.
 * Author: Zane Davis
 * Student ID: 1174117
 * Due Date: 30th May 2025
 */
package dungeon.engine; // Or your chosen package for score-related classes

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class ScoreEntry implements Serializable, Comparable<ScoreEntry> {
    @Serial
    private static final long serialVersionUID = 2024053001L; // Example serialVersionUID

    private final String playerName;
    private final int score;
    private final LocalDate date;

    /**
     * Constructs a new ScoreEntry.
     * @param playerName The name of the player.
     * @param score The score achieved.
     * @param date The date the score was achieved.
     * @throws NullPointerException if playerName or date is null.
     */
    public ScoreEntry(String playerName, int score, LocalDate date) {
        this.playerName = Objects.requireNonNull(playerName, "Player name cannot be null.");
        this.score = score;
        this.date = Objects.requireNonNull(date, "Date cannot be null.");
    }

    /**
     * Gets the player's name for this score entry.
     * @return The player's name.
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Gets the score for this entry.
     * @return The score.
     */
    public int getScore() {
        return score;
    }

    /**
     * Gets the date this score was achieved.
     * @return The date of the score.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Gets the date formatted as "dd/MM/yyyy".
     * @return The formatted date string.
     */
    public String getFormattedDate() {
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /**
     * Compares this ScoreEntry with another for ordering.
     * Primary sort is by score (descending). Secondary sort is by date (descending/newer first).
     * @param other The other ScoreEntry to compare to.
     * @return a negative integer, zero, or a positive integer as this ScoreEntry
     * is greater than, equal to, or less than the specified ScoreEntry.
     */
    @Override
    public int compareTo(ScoreEntry other) {
        Objects.requireNonNull(other, "Cannot compare to a null ScoreEntry.");
        int scoreCompare = Integer.compare(other.score, this.score); // Higher score comes first
        if (scoreCompare == 0) {
            return other.date.compareTo(this.date); // Newer date comes first if scores are tied
        }
        return scoreCompare;
    }

    /**
     * Returns a string representation of the ScoreEntry.
     * @return A string containing player name, score, and formatted date.
     */
    @Override
    public String toString() {
        // Rank is determined by position in a sorted list, not stored here.
        return String.format("%s: %d (%s)", playerName, score, getFormattedDate());
    }

    // Optional: equals and hashCode if ScoreEntry objects might be used in sets or as map keys.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScoreEntry that = (ScoreEntry) o;
        return score == that.score &&
                Objects.equals(playerName, that.playerName) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerName, score, date);
    }
}