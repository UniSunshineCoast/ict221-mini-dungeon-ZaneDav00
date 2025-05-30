/**
 * Unit tests for the MiniDungeon game engine classes.
 * Adheres to criteria: no tests for constructors, getters/setters, or direct I/O testing.
 * Author: Zane Davis
 * Student ID: 1174117
 * Due Date: 30th May 2025
 */
import dungeon.engine.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.io.TempDir; // Not used due to hardcoded filenames in GameEngine

import java.io.File;
import java.time.LocalDate;
import java.util.List;
// import java.util.Random; // Not directly used in these revised tests

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Player class.
 * Tests core logic of HP and score adjustments, and status.
 */
class PlayerTest {
    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player(0, 0);
    }

    @Test
    void playerInitialStateAndIsAlive() {
        // This test verifies the outcome of constructor logic via observable state and isAlive()
        assertEquals(10, player.getHp(), "Player should start with 10 HP.");
        assertEquals(0, player.getScore(), "Player should start with 0 score.");
        assertTrue(player.isAlive(), "Player should start alive.");
    }

    @Test
    void adjustHpCorrectlyHandlesDamageAndSurvival() {
        player.adjustHp(-3);
        assertEquals(7, player.getHp(), "HP should decrease by 3.");
        assertTrue(player.isAlive());
    }

    @Test
    void adjustHpCorrectlyHandlesHealing() {
        player.adjustHp(-5); // HP is 5
        player.adjustHp(3);  // Heal 3
        assertEquals(8, player.getHp(), "HP should increase by 3.");
    }

    @Test
    void adjustHpDoesNotExceedMaxHp() {
        player.adjustHp(50); // Try to heal beyond max
        assertEquals(10, player.getHp(), "HP should not exceed max HP (10).");
    }

    @Test
    void adjustHpDoesNotGoBelowZeroAndPlayerDies() {
        player.adjustHp(-15); // Damage beyond zero
        assertEquals(0, player.getHp(), "HP should not go below 0.");
        assertFalse(player.isAlive(), "Player should not be alive if HP is 0.");
    }

    @Test
    void setHpAppliesLogicForMinMaxBounds() {
        // Testing the logic within setHp (clamping), not just that it sets a value.
        player.setHp(5);
        assertEquals(5, player.getHp());
        player.setHp(15); // Test max clamping
        assertEquals(10, player.getHp(), "setHp should respect max HP.");
        player.setHp(-5); // Test min clamping
        assertEquals(0, player.getHp(), "setHp should respect min HP (0).");
    }

    @Test
    void isAliveReturnsFalseWhenHpIsExactlyZero() {
        player.setHp(0); // Directly set HP to 0 to test isAlive boundary
        assertFalse(player.isAlive());
    }

    @Test
    void adjustScoreCorrectlyChangesScore() {
        player.adjustScore(5);
        assertEquals(5, player.getScore());
        player.adjustScore(-2);
        assertEquals(3, player.getScore());
    }
    // Removed tests for plain setScore and setPosition as they are direct setters/getters.
}

/**
 * Unit tests for the ScoreEntry class.
 * Tests comparison logic and date formatting.
 */
class ScoreEntryTest {
    @Test
    void compareToSortsScoresCorrectly() {
        ScoreEntry score1 = new ScoreEntry("Alice", 100, LocalDate.now());
        ScoreEntry score2 = new ScoreEntry("Bob", 200, LocalDate.now());
        ScoreEntry score3 = new ScoreEntry("Charlie", 100, LocalDate.now().minusDays(1)); // Older date, same score

        assertTrue(score2.compareTo(score1) < 0, "Higher score (Bob) should come before lower score (Alice).");
        assertTrue(score1.compareTo(score3) < 0, "For equal scores, newer date (Alice) should come before older date (Charlie).");
    }

    @Test
    void getFormattedDateReturnsCorrectStringFormat() {
        ScoreEntry entry = new ScoreEntry("TestPlayer", 10, LocalDate.of(2025, 5, 30));
        assertEquals("30/05/2025", entry.getFormattedDate());
    }
}


/**
 * Unit tests for interactions with various Entity types.
 * Focuses on the logic of the interact() method and related properties.
 */
class EntityInteractionTest {
    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player(0, 0);
    }

    @Test
    void goldInteraction() {
        Gold gold = new Gold();
        assertTrue(gold.isPassable(), "Gold should be passable.");
        String msg = gold.interact(player);
        assertEquals(2, player.getScore());
        assertTrue(msg.contains("+2 score"));
    }

    @Test
    void trapInteraction() {
        Trap trap = new Trap();
        assertTrue(trap.isPassable(), "Trap should be passable to trigger effect.");
        String msg = trap.interact(player);
        assertEquals(8, player.getHp());
        assertTrue(msg.contains("-2 HP"));
    }

    @Test
    void healthPotionInteraction() {
        HealthPotion potion = new HealthPotion();
        player.setHp(5); // Ensure HP is not max
        assertTrue(potion.isPassable(), "HealthPotion should be passable.");
        String msg = potion.interact(player);
        assertEquals(9, player.getHp());
        assertTrue(msg.contains("+4 HP"));
    }

    @Test
    void healthPotionInteractionRespectsMaxHp() {
        HealthPotion potion = new HealthPotion();
        player.setHp(8);
        potion.interact(player);
        assertEquals(10, player.getHp(), "HealthPotion should not heal beyond max HP.");
    }

    @Test
    void meleeMutantInteraction() {
        MeleeMutant mutant = new MeleeMutant();
        assertTrue(mutant.isPassable(), "MeleeMutant should be passable for interaction.");
        String msg = mutant.interact(player);
        assertEquals(8, player.getHp());
        assertEquals(2, player.getScore());
        assertTrue(msg.contains("-2 HP") && msg.contains("+2 score"));
    }

    @Test
    void rangedMutantDirectInteraction() {
        RangedMutant mutant = new RangedMutant();
        assertTrue(mutant.isPassable(), "RangedMutant should be passable for direct interaction.");
        String msg = mutant.interact(player); // Player steps on it
        assertEquals(10, player.getHp(), "HP should not change on direct RangedMutant defeat.");
        assertEquals(2, player.getScore());
        assertTrue(msg.contains("+2 score"));
    }

    @Test
    void rangedMutantCanAttackLogic() {
        RangedMutant mutant = new RangedMutant();
        assertTrue(mutant.canAttack(5, 5, 5, 3), "Should attack 2 tiles vertically.");
        assertTrue(mutant.canAttack(5, 5, 3, 5), "Should attack 2 tiles horizontally.");
        assertTrue(mutant.canAttack(5, 5, 5, 4), "Should attack 1 tile vertically.");
        assertFalse(mutant.canAttack(5, 5, 5, 2), "Should not attack 3 tiles away.");
        assertFalse(mutant.canAttack(5, 5, 5, 5), "Should not attack same tile.");
        assertFalse(mutant.canAttack(5, 5, 6, 6), "Should not attack diagonally.");
    }

    @Test
    void rangedMutantTryAttackIsProbabilistic() {
        RangedMutant mutant = new RangedMutant();
        int hits = 0;
        int trials = 1000; // Sufficient trials for a basic probability check
        for (int i = 0; i < trials; i++) {
            if (mutant.tryAttack()) {
                hits++;
            }
        }
        double hitRate = (double) hits / trials;
        // Check if hit rate is roughly 50% (+/- a reasonable margin for randomness)
        assertTrue(hitRate > 0.4 && hitRate < 0.6,
                "Hit rate should be around 0.5 for 1000 trials, but was " + hitRate);
    }

    @Test
    void ladderInteraction() {
        Ladder ladder = new Ladder();
        assertTrue(ladder.isPassable(), "Ladder should be passable.");
        String msg = ladder.interact(player);
        assertTrue(msg.contains("climbed the ladder"));
    }

    @Test
    void entryInteraction() {
        Entry entry = new Entry();
        assertTrue(entry.isPassable(), "Entry should be passable.");
        String msg = entry.interact(player);
        assertTrue(msg.contains("dungeon entry"));
    }
}

/**
 * Unit tests for the GameState class.
 * Focuses on movement logic, map interactions, and effect on player state.
 */
class GameStateTest {
    private GameState gameState;
    private Player player;
    private final int MAP_SIZE_FOR_TEST = 10; // Use a distinct constant name

    @BeforeEach
    void setUp() {
        gameState = new GameState(MAP_SIZE_FOR_TEST, 3); // Default difficulty 3
        player = new Player(0, 0);
        gameState.setPlayer(player);
        gameState.setPlayerPosition(5, 5); // Start player mid-map
    }

    @Test
    void movePlayerUpdatesPositionStepsAndMessages() {
        gameState.movePlayer(Direction.UP);
        assertEquals(4, gameState.getPlayerX());
        assertEquals(1, gameState.getSteps());
        assertTrue(gameState.getAndClearTurnMessages().stream().anyMatch(m -> m.contains("moved up")));
    }

    @Test
    void movePlayerHandlesBoundaryConditions() {
        gameState.setPlayerPosition(0, 0); // Top-left corner
        gameState.movePlayer(Direction.UP); // Attempt to move out of bounds
        assertEquals(0, gameState.getPlayerX(), "Player X should remain at boundary.");
        assertEquals(0, gameState.getSteps(), "Steps should not increment for out-of-bounds move.");
        assertTrue(gameState.getAndClearTurnMessages().stream().anyMatch(m -> m.contains("out of bounds")));
    }

    @Test
    void movePlayerInteractionWithGoldUpdatesState() {
        Entity[][] map = gameState.getMap();
        map[4][5] = new Gold(); // Gold at (4,5), player at (5,5)
        int initialScore = player.getScore();
        gameState.movePlayer(Direction.UP); // Move onto gold
        assertEquals(initialScore + 2, player.getScore());
        assertNull(map[4][5], "Gold should be removed after collection.");
        assertTrue(gameState.getAndClearTurnMessages().stream().anyMatch(m -> m.contains("picked up gold")));
    }

    @Test
    void movePlayerInteractionWithTrapUpdatesStateAndTrapPersists() {
        Entity[][] map = gameState.getMap();
        Trap trapInstance = new Trap();
        map[4][5] = trapInstance; // Trap at (4,5)
        int initialHp = player.getHp();
        gameState.movePlayer(Direction.UP); // Move onto trap
        assertEquals(initialHp - 2, player.getHp());
        assertSame(trapInstance, map[4][5], "Trap should persist on the map.");
        assertTrue(gameState.getAndClearTurnMessages().stream().anyMatch(m -> m.contains("fell into a trap")));
    }

    @Test
    void movePlayerInteractionWithLadderSetsFlagAndLadderPersists() {
        Entity[][] map = gameState.getMap();
        Ladder ladderInstance = new Ladder();
        map[4][5] = ladderInstance; // Ladder at (4,5)
        assertFalse(gameState.hasReachedLadderThisTurn());
        gameState.movePlayer(Direction.UP); // Move onto ladder
        assertTrue(gameState.hasReachedLadderThisTurn());
        assertSame(ladderInstance, map[4][5], "Ladder should persist until engine handles transition.");
    }



    @Test
    void reinitializeForNextLevelResetsRelevantState() {
        gameState.setSteps(50);
        gameState.setDifficulty(3);
        gameState.getMap()[0][0] = new Ladder(); // Place a ladder
        gameState.setPlayerPosition(0,0);    // Move player to ladder
        gameState.movePlayer(Direction.UP);      // Interact with ladder (sets ladderReachedThisTurn)
        // Note: movePlayer clears messages, then adds "moved up", then "climbed ladder"
        // then checkForRangedMutantAttacks.
        gameState.getAndClearTurnMessages(); // Clear messages from the move before reinitializing

        gameState.reinitializeForNextLevel(5); // Call the method to test

        assertEquals(2, gameState.getLevel(), "Level should increment after reinitialize.");
        assertEquals(0, gameState.getSteps(), "Steps should reset after reinitialize.");
        assertEquals(5, gameState.getDifficulty(), "Difficulty should update after reinitialize.");
        assertFalse(gameState.hasReachedLadderThisTurn(), "LadderReachedThisTurn flag should reset.");
        assertTrue(gameState.getAndClearTurnMessages().isEmpty(), "Messages should be cleared after reinitialize.");
    }
}

/**
 * Unit tests for the GameEngine class.
 * Focuses on game lifecycle, level progression, and in-memory top score logic.
 * File I/O methods (save/load game, save/load top scores) are not unit tested directly
 * as per assignment constraints.
 */
class GameEngineTest {
    private GameEngine gameEngine;
    // Hardcoded filenames from GameEngine, used for cleanup.
    private static final String TEST_SAVE_FILENAME = "minidungeon.save";
    private static final String TEST_TOP_SCORES_FILENAME = "topscores.dat";


    @BeforeEach
    void setUp() {
        // Ensure any pre-existing test files are cleaned up before each test
        // to maintain test isolation, especially for tests interacting with top scores.
        cleanupTestFile(TEST_SAVE_FILENAME);
        cleanupTestFile(TEST_TOP_SCORES_FILENAME);
        gameEngine = new GameEngine(3); // Default difficulty 3
    }

    @AfterEach
    void tearDown() {
        // Clean up files that might have been created by methods like addPlayerScore (which calls saveTopScores)
        cleanupTestFile(TEST_SAVE_FILENAME);
        cleanupTestFile(TEST_TOP_SCORES_FILENAME);
    }

    private void cleanupTestFile(String filename) {
        File testFile = new File(filename);
        if (testFile.exists()) {
            if (!testFile.delete()) {
                System.err.println("Warning: Could not delete test file: " + filename);
            }
        }
    }

    @Test
    void startNewGameInitializesGameCorrectly() {
        gameEngine.startNewGame();
        GameState state = gameEngine.getState();
        assertNotNull(state, "GameState should be initialized.");
        assertEquals(1, state.getLevel(), "Game should start at level 1.");
        assertEquals(3, state.getDifficulty(), "Initial difficulty should be set.");
        assertNotNull(state.getPlayer(), "Player should be initialized.");
        assertEquals(9, state.getPlayerX(), "Player X start position incorrect."); // MAP_SIZE-1
        assertEquals(0, state.getPlayerY(), "Player Y start position incorrect."); // 0
        assertTrue(state.getMap()[9][0] instanceof Entry, "Entry entity should be at starting position.");
    }

    @Test
    void advanceToNextLevelUpdatesStateAndDifficultyCorrectly() {
        gameEngine.startNewGame(); // Starts at L1, Diff 3
        GameState level1State = gameEngine.getState();
        Player player = level1State.getPlayer();
        player.setScore(50); // Set some initial state to check carry-over
        player.setHp(8);

        // Simulate player moving to a ladder. Assume ladder is at (0,1) and player at (1,1)
        level1State.setPlayerPosition(1,1);
        level1State.getMap()[0][1] = new Ladder();
        gameEngine.handlePlayerMove(Direction.UP); // Player moves to (0,1) onto the ladder

        assertTrue(level1State.hasReachedLadderThisTurn(), "Ladder should be reached before advancing.");

        boolean advanced = gameEngine.advanceToNextLevel();
        assertTrue(advanced, "Should successfully advance from level 1.");

        GameState level2State = gameEngine.getState();
        assertNotNull(level2State, "New GameState for level 2 should exist.");
        assertEquals(2, level2State.getLevel(), "Should be level 2.");
        assertEquals(3 + 2, level2State.getDifficulty(), "Difficulty should increase by 2 for level 2.");
        assertEquals(50, level2State.getPlayer().getScore(), "Score should carry over to level 2.");
        assertEquals(8, level2State.getPlayer().getHp(), "HP should carry over to level 2.");
        assertEquals(0, level2State.getPlayerX(), "Player X should start at previous ladder's X position.");
        assertEquals(1, level2State.getPlayerY(), "Player Y should start at previous ladder's Y position.");
    }

    @Test
    void advanceToNextLevelDifficultyIsCappedAt10() {
        gameEngine = new GameEngine(9); // Initial difficulty 9
        gameEngine.startNewGame();
        GameState state = gameEngine.getState();
        // Simulate reaching ladder
        state.setPlayerPosition(0,1);
        state.getMap()[0][1] = new Ladder();
        gameEngine.handlePlayerMove(Direction.UP);

        gameEngine.advanceToNextLevel(); // Diff should be 9+2 = 11, capped to 10
        assertEquals(10, gameEngine.getState().getDifficulty(), "Difficulty should be capped at 10.");
    }

    @Test
    void isGameOverReturnsTrueIfPlayerIsNotAlive() {
        gameEngine.startNewGame();
        gameEngine.getPlayer().setHp(0); // Simulate player death
        assertTrue(gameEngine.isGameOver(), "Game should be over if player HP is 0.");
    }

    @Test
    void isGameOverReturnsTrueIfMaxStepsReached() {
        gameEngine.startNewGame();
        gameEngine.getState().setSteps(gameEngine.getMaxSteps()); // Simulate max steps
        assertTrue(gameEngine.isGameOver(), "Game should be over if max steps reached.");
    }

    // In GameEngineTest.java
    @Test
    void hasWonGameReturnsTrueWhenLadderReachedOnLevel2() {
        gameEngine.startNewGame(); // Level 1, Diff 3. Player at (9,0) by default.
        GameState stateL1 = gameEngine.getState();

        // Simulate moving to a ladder on Level 1
        stateL1.getMap()[8][0] = new Ladder(); // Place ladder at (8,0) for player at (9,0) to move UP to.
        System.out.println("DEBUG WinTest L1: Before L1 move - Player at ("+stateL1.getPlayerX()+","+stateL1.getPlayerY()+")");
        gameEngine.handlePlayerMove(Direction.UP); // Player moves from (9,0) to (8,0) onto Ladder
        System.out.println("DEBUG WinTest L1: After L1 move - Player at ("+stateL1.getPlayerX()+","+stateL1.getPlayerY()+")");

        assertTrue(stateL1.hasReachedLadderThisTurn(), "L1: LadderReachedThisTurn should be true after stepping on L1 ladder.");
        assertEquals(1, stateL1.getLevel(), "L1: Should be level 1 before advancing.");

        boolean advanced = gameEngine.advanceToNextLevel();
        assertTrue(advanced, "Should successfully advance to Level 2.");

        GameState stateL2 = gameEngine.getState();
        assertNotNull(stateL2, "L2: GameState for Level 2 should not be null.");
        assertEquals(2, stateL2.getLevel(), "L2: Engine should now be on Level 2.");
        // Player should now be at the L1 ladder's old position (8,0)
        assertEquals(8, stateL2.getPlayerX(), "L2: Player X should be at old L1 ladder's X position.");
        assertEquals(0, stateL2.getPlayerY(), "L2: Player Y should be at old L1 ladder's Y position.");
        System.out.println("DEBUG WinTest L2: Player at ("+stateL2.getPlayerX()+","+stateL2.getPlayerY()+") on Level " + stateL2.getLevel());

        // Simulate reaching ladder on Level 2. Player is at (8,0).
        // Place ladder for L2, e.g., at (7,0) for player to move UP to.
        stateL2.getMap()[7][0] = new Ladder();
        System.out.println("DEBUG WinTest L2: Player at ("+stateL2.getPlayerX()+","+stateL2.getPlayerY()+"), moving UP to L2 ladder at (7,0)");
        gameEngine.handlePlayerMove(Direction.UP); // Player moves (8,0) -> (7,0) onto L2 ladder
        // This operates on stateL2.
        System.out.println("DEBUG WinTest L2: After L2 move - Player at ("+stateL2.getPlayerX()+","+stateL2.getPlayerY()+")");

        // Intermediate assertions to verify state after moving onto L2 ladder:
        assertEquals(2, stateL2.getLevel(), "L2: Level should still be 2 after moving on L2.");
        assertTrue(stateL2.hasReachedLadderThisTurn(), "L2: LadderReachedThisTurn flag in Level 2 GameState should be true.");

        assertTrue(gameEngine.hasWonGame(), "Game should be won when ladder is reached on level 2.");
    }

    // Test for isTopScore and the in-memory logic of addPlayerScore.
    // The saveTopScores() call within addPlayerScore is an I/O operation whose file effect is not asserted.
    @Test
    void topScoreLogicInMemory() {
        // GameEngine constructor calls loadTopScores. If file doesn't exist, list is empty.
        assertTrue(gameEngine.isTopScore(100), "Should be top score if list is empty/not full.");
        assertFalse(gameEngine.isTopScore(-1), "Score of -1 should not be a top score.");

        // Add scores and test list manipulation (sorting, trimming)
        gameEngine.addPlayerScore("Alice", 100, LocalDate.now().minusDays(2));
        gameEngine.addPlayerScore("Bob", 50, LocalDate.now().minusDays(3));
        gameEngine.addPlayerScore("Charlie", 150, LocalDate.now().minusDays(1));
        gameEngine.addPlayerScore("Dave", 75, LocalDate.now().minusDays(4));
        gameEngine.addPlayerScore("Eve", 120, LocalDate.now());

        // At this point, saveTopScores() would have been called multiple times.
        // We test the state of the in-memory list via getTopScores().
        List<ScoreEntry> topScores = gameEngine.getTopScores();
        assertEquals(5, topScores.size(), "Top scores list should be trimmed to 5.");
        assertEquals(150, topScores.get(0).getScore(), "Highest score (Charlie) should be first.");
        assertEquals("Charlie", topScores.get(0).getPlayerName());
        assertEquals(120, topScores.get(1).getScore(), "Second highest (Eve) should be second.");

        // Test adding another score that should displace the lowest
        gameEngine.addPlayerScore("Zane", 160, LocalDate.now().plusDays(1)); // New highest score
        topScores = gameEngine.getTopScores();
        assertEquals(5, topScores.size());
        assertEquals(160, topScores.get(0).getScore(), "New highest score (Zane) should be first.");
        assertEquals("Zane", topScores.get(0).getPlayerName());
        assertEquals(150, topScores.get(1).getScore(), "Charlie should now be second.");

        // Test isTopScore when list is full
        assertFalse(gameEngine.isTopScore(20), "Score of 20 should not be a top score when list is full of higher scores.");
        assertTrue(gameEngine.isTopScore(125), "Score of 125 should be a top score, displacing the current 5th.");
    }
}
