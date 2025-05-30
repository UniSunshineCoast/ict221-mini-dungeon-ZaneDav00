/**
 * Manages the user interface for the MiniDungeon game, handling user input,
 * updating the display, and interacting with the game engine.
 * Author: Zane Davis
 * Student ID: 1174117
 * Due Date: 30th May 2025
 */
package dungeon.gui;

import dungeon.engine.Direction;
import dungeon.engine.Entity;
import dungeon.engine.GameEngine;
import dungeon.engine.Player;
import dungeon.engine.ScoreEntry;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    // --- FXML UI Elements ---
    @FXML private GridPane gridPane;
    @FXML private Label healthLabel;
    @FXML private Label scoreLabel;
    @FXML private Label stepsLabel;
    @FXML private Label timerLabel;
    @FXML private Label levelLabel;
    @FXML private Label difficultyLabel;
    @FXML private TextArea statusTextArea;
    @FXML private Button saveButton;
    @FXML private Button loadButton;

    // --- Game Logic and State ---
    private GameEngine engine;
    private Timeline timeline;
    private int elapsedTime;

    private static final String SAVE_FILENAME = "minidungeon.save";

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded. Sets up keyboard listeners
     * and initial button states.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gridPane.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (engine == null || engine.isGameOver() || engine.hasWonGame()) return;
            Direction dir = null;
            if (e.getCode() == KeyCode.W || e.getCode() == KeyCode.UP) dir = Direction.UP;
            else if (e.getCode() == KeyCode.S || e.getCode() == KeyCode.DOWN) dir = Direction.DOWN;
            else if (e.getCode() == KeyCode.A || e.getCode() == KeyCode.LEFT) dir = Direction.LEFT;
            else if (e.getCode() == KeyCode.D || e.getCode() == KeyCode.RIGHT) dir = Direction.RIGHT;
            if (dir != null) { processMove(dir); e.consume(); }
        });
        gridPane.setFocusTraversable(true);

        File saveFile = new File(SAVE_FILENAME);
        if (loadButton != null) loadButton.setDisable(!saveFile.exists());
        if (saveButton != null) saveButton.setDisable(true); // Enabled once game starts
    }

    /**
     * Sets the initial difficulty for the game and starts a new game.
     * This method is typically called after the user selects a difficulty.
     * @param difficulty The initial difficulty level.
     */
    public void setInitialDifficulty(int difficulty) {
        // Default difficulty
        engine = new GameEngine(difficulty);
        engine.startNewGame();

        resetAndStartTimer();
        updateGui();
        gridPane.setDisable(false);
        if (statusTextArea != null) statusTextArea.clear();
        appendToStatus("Game started. Level 1. Difficulty: " + engine.getState().getDifficulty());
        gridPane.requestFocus();

        if (saveButton != null) saveButton.setDisable(false);
        if (loadButton != null) loadButton.setDisable(!new File(SAVE_FILENAME).exists());
    }

    /**
     * Resets and starts the game timer.
     */
    private void resetAndStartTimer() {
        if (timeline != null) timeline.stop();
        elapsedTime = 0;
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            elapsedTime++; updateTimerLabel();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Updates the timer label in the GUI.
     */
    private void updateTimerLabel() {
        if (timerLabel == null) return;
        int minutes = elapsedTime / 60;
        int seconds = elapsedTime % 60;
        timerLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));
    }

    /**
     * Refreshes the entire game GUI based on the current game engine state.
     * Updates map, player stats, labels, and button states.
     * Also handles logic for game over, win, and level advancement.
     */
    private void updateGui() {
        if (engine == null || engine.getState() == null) {
            System.err.println("Controller.updateGui: Engine or game state not initialized.");
            if (saveButton != null) saveButton.setDisable(true);
            return;
        }

        // Refresh map display
        gridPane.getChildren().clear();
        Entity[][] entityMap = engine.getMapEntities();
        int playerX = engine.getPlayerX();
        int playerY = engine.getPlayerY();
        for (int r = 0; r < entityMap.length; r++) {
            for (int c = 0; c < entityMap[r].length; c++) {
                Cell guiCell = new Cell();
                if (r == playerX && c == playerY) guiCell.setPlayerSymbol();
                else guiCell.setEntity(entityMap[r][c]);
                gridPane.add(guiCell, c, r);
            }
        }

        // Refresh player stats and game info labels
        Player currentPlayer = engine.getPlayer();
        if (currentPlayer != null) {
            healthLabel.setText("HP: " + currentPlayer.getHp());
            scoreLabel.setText("Score: " + currentPlayer.getScore());
        }
        stepsLabel.setText("Steps: " + engine.getSteps() + "/" + engine.getMaxSteps());
        if (levelLabel != null) levelLabel.setText("Level: " + engine.getState().getLevel());
        if (difficultyLabel != null) difficultyLabel.setText("Difficulty: " + engine.getState().getDifficulty());

        // Update button states based on game status
        boolean gameIsEffectivelyOver = engine.isGameOver() || engine.hasWonGame();
        if (saveButton != null) saveButton.setDisable(gameIsEffectivelyOver);
        if (gridPane != null) gridPane.setDisable(gameIsEffectivelyOver);
        if (loadButton != null) { // Load button can be active even if game over, to load another game
            loadButton.setDisable(!new File(SAVE_FILENAME).exists());
        }


        // Handle game end conditions or level advancement
        if (engine.isGameOver()) {
            if (timeline != null) timeline.stop();
            int finalScore = -1;
            if (currentPlayer != null) {
                if (currentPlayer.getHp() <= 0 || engine.getSteps() >= engine.getMaxSteps()) {
                    currentPlayer.setScore(-1);
                }
                finalScore = currentPlayer.getScore();
            }
            scoreLabel.setText("Score: " + finalScore); // Ensure score display is updated
            processEndOfGame(finalScore, false); // false: did not win
        } else if (engine.hasWonGame()) {
            if (timeline != null) timeline.stop();
            int finalScore = (currentPlayer != null) ? currentPlayer.getScore() : 0;
            scoreLabel.setText("Score: " + finalScore); // Ensure score display is updated
            processEndOfGame(finalScore, true); // true: did win
        } else if (engine.getState().hasReachedLadderThisTurn() && engine.getState().getLevel() == 1) {
            appendToStatus("You found the ladder!");
            if (engine.advanceToNextLevel()) {
                // Message for advancing is added by GameEngine, will be picked up below
                resetAndStartTimer();
                updateGui(); // Recursive call to refresh for new level
            } else {
                appendToStatus("Error advancing level.");
            }
        }

        // Fetch and display any general messages from GameState
        List<String> generalMessages = engine.getState().getAndClearTurnMessages();
        for (String msg : generalMessages) {
            appendToStatus(msg);
        }
    }

    /**
     * Processes the end of a game, checking for top scores and showing appropriate alerts.
     * @param finalScore The player's final score for the game.
     * @param wonGame True if the player won, false otherwise.
     */
    private void processEndOfGame(int finalScore, boolean wonGame) {
        if (finalScore != -1 && engine.isTopScore(finalScore)) {
            TextInputDialog nameDialog = new TextInputDialog("Player");
            nameDialog.setTitle("New High Score!");
            nameDialog.setHeaderText("Congratulations! You made it into the Top 5 with a score of " + finalScore + "!");
            nameDialog.setContentText("Please enter your name:");

            Optional<String> nameResult = nameDialog.showAndWait();
            if (nameResult.isPresent() && !nameResult.get().trim().isEmpty()) {
                String playerName = nameResult.get().trim();
                engine.addPlayerScore(playerName, finalScore, LocalDate.now());
                appendToStatus("Your score of " + finalScore + " as '" + playerName + "' has been saved to the Top 5!");
            } else {
                appendToStatus("You achieved a Top 5 score (" + finalScore + "), but no name was entered. Score not saved.");
            }
        }

        if (wonGame) {
            showWinAlert(finalScore);
        } else {
            boolean dueToHp = (engine.getPlayer() != null && engine.getPlayer().getHp() <= 0);
            showGameOverAlert(dueToHp, finalScore);
        }
    }

    /**
     * Appends a message to the status text area in the GUI and prints to console.
     * @param message The message to display.
     */
    private void appendToStatus(String message) {
        if (statusTextArea != null) {
            statusTextArea.appendText(message + "\n");
        }
        System.out.println("GUI_STATUS: " + message); // Console fallback/log
    }

    /**
     * Processes a player's move action.
     * @param direction The direction of the move.
     */
    private void processMove(Direction direction) {
        if (engine == null || engine.isGameOver() || engine.hasWonGame()) {
            return;
        }
        List<String> messagesFromMove = engine.handlePlayerMove(direction);
        for (String msg : messagesFromMove) {
            appendToStatus(msg);
        }
        updateGui(); // Refresh map, stats, and check game state
        gridPane.requestFocus();
    }

    // --- FXML Action Handlers ---

    /**
     * Displays the help dialog with game instructions.
     * Triggered by the Help button.
     */
    @FXML
    private void showHelpDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("MiniDungeon - Help");
        alert.setHeaderText("Game Instructions & Rules");
        String helpText = """
                Welcome to MiniDungeon!
                
                Goal:
                Achieve the highest possible score by collecting gold, defeating mutants, \
                and safely escaping the dungeon through the ladder on Level 2, all within a \
                limited number of moves (default 100).
                
                Player Stats:
                - Starts with 10 Health Points (HP). If HP drops to 0, the game is over and your score becomes -1.
                - Starts with 0 Score.
                - Max HP is 10.
                
                Controls:
                - Use the on-screen arrow buttons (↑, ↓, ←, →) to move.
                - Keyboard controls (W, A, S, D or Arrow Keys).
                - Each move counts as one step.
                
                Items & Map Symbols:
                - P: Player (You)
                - E: Entry point of the current level.
                - L: Ladder. Reaching this on Level 1 advances you to Level 2 (difficulty increases by 2). \
                Reaching this on Level 2 means you win the game!
                - #: Wall (Boundaries of the maze, blocks movement).
                - G: Gold. Collect for +2 score. The gold is picked up and the cell becomes empty.
                - H: Health Potion. Restores 4 HP (up to the max of 10). Consumed on pickup, cell becomes empty.
                - T: Trap. Stepping on a trap decreases your HP by 2. Traps remain active.
                - M: Melee Mutant. Stationary. Stepping on it results in a fight: -2 HP to you, but +2 score. The mutant is defeated and removed.
                - R: Ranged Mutant. Stationary. Can attack if you are 2 horizontal or vertical tiles away (50% chance, -2 HP per hit). \
                If you step directly on it to defeat it: +2 score, no HP lost from this direct engagement, mutant removed.
                
                Levels & Difficulty:
                - The game has two dungeon levels, each a 10x10 grid.
                - You select an initial difficulty (d, from 0 to 10, default 3).
                - The number of Ranged Mutants on a level is equal to the current difficulty 'd'.
                - When advancing to Level 2, the difficulty 'd' increases by 2.
                
                Game End Conditions:
                - Win: Successfully exit the Level 2 maze by reaching the Ladder.
                - Lose: Player HP drops to 0, or the maximum number of steps (100) is reached. If you lose, your final score is -1.
                
                Game Features:
                - Save Game: Saves your current progress (single save file).
                - Load Game: Loads your previously saved game.
                - Top Scores: View the hall of fame.
                
                Good luck exploring the MiniDungeon!""";
        alert.setContentText(helpText);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.setResizable(true);
        alert.showAndWait();
    }

    /**
     * Handles the Save Game button action.
     * Saves the current game state if the game is active.
     */
    @FXML
    private void handleSaveGame() {
        if (engine != null && !engine.isGameOver() && !engine.hasWonGame()) {
            engine.saveGameState(); // Engine adds success/fail message to its state
        } else {
            appendToStatus("Cannot save: Game is over or not properly started.");
        }
        // Fetch and display messages (e.g., "Game saved successfully")
        List<String> saveMessages = engine.getState().getAndClearTurnMessages();
        for (String msg : saveMessages) {
            appendToStatus(msg);
        }
        updateGui(); // Refresh button states if needed (though save doesn't change game state itself)
        gridPane.requestFocus();
    }

    /**
     * Handles the Load Game button action.
     * Loads a previously saved game state.
     */
    @FXML
    private void handleLoadGame() {
        if (engine == null) { // Should ideally not happen if button is managed correctly
            appendToStatus("Cannot load: Game engine not ready.");
            return;
        }
        boolean loaded = engine.loadGameState(); // Engine adds success/fail message to its state
        if (loaded) {
            resetAndStartTimer(); // Reset timer for the loaded game
            // updateGui() will be called and will fetch the "Game loaded" message
        }
        // Whether loaded or not, updateGui will fetch any messages (like "Game loaded" or "Load failed")
        // and refresh the display fully.
        updateGui();
        gridPane.requestFocus();
    }

    /**
     * Displays an alert indicating the game is over.
     *
     */
    private void showGameOverAlert(boolean dueToHp, int finalScore) {
        // System.out.println("DEBUG: showGameOverAlert - Method ENTERED. dueToHp: " + dueToHp + ", finalScore: " + finalScore);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        if (dueToHp) {
            alert.setContentText("You have died. Game Over!\nFinal Score: " + finalScore);
        } else {
            alert.setContentText("You have reached the maximum steps. Game Over!\nFinal Score: " + finalScore);
        }
        // System.out.println("DEBUG: showGameOverAlert - Alert created. About to call showAndWait().");
        try {
            alert.showAndWait();
        } catch (Exception e) { e.printStackTrace(); }
        // System.out.println("DEBUG: showGameOverAlert - showAndWait() finished.");
    }

    /**
     * Displays an alert indicating the player has won the game.
     * @param finalScore The player's final score.
     */
    private void showWinAlert(int finalScore) {
        // System.out.println("DEBUG: showWinAlert - Method ENTERED. Final Score: " + finalScore);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Congratulations!");
        alert.setHeaderText("You Won!");
        alert.setContentText("You've successfully escaped the dungeon from Level 2!\nFinal Score: " + finalScore);
        // System.out.println("DEBUG: showWinAlert - Alert created. About to call showAndWait().");
        try {
            alert.showAndWait();
        } catch (Exception e) { e.printStackTrace(); }
        // System.out.println("DEBUG: showWinAlert - showAndWait() finished.");
    }

    /**
     * Displays the Top 5 scores in a dialog.
     * Triggered by the Top Scores button.
     */
    @FXML
    private void showTopScoresDialog() {
        if (engine == null) {
            appendToStatus("Game engine not ready to show top scores.");
            return;
        }
        StringBuilder sb = getStringBuilder();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Top 5 Scores");
        alert.setHeaderText("Hall of Fame");

        TextArea textArea = new TextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setWrapText(false);
        textArea.setFont(Font.font("Monospaced", 12));

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expandableContent = new GridPane();
        expandableContent.setMaxWidth(Double.MAX_VALUE);
        expandableContent.add(textArea, 0, 0);

        alert.getDialogPane().setExpandableContent(expandableContent);
        alert.getDialogPane().setExpanded(true);
        alert.getDialogPane().setPrefSize(500, 350);
        alert.setResizable(true);

        alert.showAndWait();
        gridPane.requestFocus();
    }

    private StringBuilder getStringBuilder() {
        List<ScoreEntry> scores = engine.getTopScores();
        StringBuilder sb = new StringBuilder();
        if (scores.isEmpty()) {
            sb.append("No high scores recorded yet!");
        } else {
            sb.append(String.format("%-5s %-10s %-20s %s\n", "Rank", "Score", "Player", "Date"));
            sb.append("-----------------------------------------------------------\n");
            for (int i = 0; i < scores.size(); i++) {
                ScoreEntry entry = scores.get(i);
                sb.append(String.format("#%-4d %-10d %-20s %s\n",
                        i + 1,
                        entry.getScore(), // Ensure this is .getScore() and NOT .score()
                        entry.getPlayerName(),
                        entry.getFormattedDate()));
            }
        }
        return sb;
    }

    // --- Player Movement Action Handlers ---
    @FXML private void moveUp() { processMove(Direction.UP); }
    @FXML private void moveDown() { processMove(Direction.DOWN); }
    @FXML private void moveLeft() { processMove(Direction.LEFT); }
    @FXML private void moveRight() { processMove(Direction.RIGHT); }

}