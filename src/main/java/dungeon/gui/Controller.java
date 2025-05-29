package dungeon.gui;

// Make sure Cell.java is moved to this package: package dungeon.gui;
// import dungeon.gui.Cell; // This import would change to:
import dungeon.gui.Cell;      // Assuming Cell.java is now in dungeon.gui

import dungeon.engine.Direction;
import dungeon.engine.Entity;
import dungeon.engine.GameEngine;
import dungeon.engine.Player; // Keep this for type casting if needed, but stats come via engine.getPlayer()

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea; // Assuming you add a TextArea with fx:id="statusTextArea"
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent; // Import for KeyEvent
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private GridPane gridPane;
    @FXML
    private Label healthLabel;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label stepsLabel;
    @FXML
    private Label timerLabel; // Not in assignment but you have it
    @FXML
    private Label levelLabel; // New Label for Level: fx:id="levelLabel"
    @FXML
    private Label difficultyLabel; // New Label for Difficulty: fx:id="difficultyLabel"

    @FXML
    private ComboBox<String> difficultyComboBox; // Still here, but game difficulty set on launch

    @FXML
    private TextArea statusTextArea; // ADD THIS TO YOUR FXML: <TextArea fx:id="statusTextArea" editable="false" wrapText="true"/>

    private GameEngine engine;
    private Timeline timeline;
    private int elapsedTime; // For your timer

    private int initialDifficulty = 3; // Default difficulty, will be overridden by GameGUI/RunGame

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Engine is not created here directly anymore, setDifficulty will do it.
        // difficultyComboBox.setDisable(true); // Or hide it if difficulty set once
        // difficultyComboBox.setVisible(false);

        // Setup for keyboard input on the gridPane (or scene later)
        // Using an event filter on gridPane for immediate focus. Otherwise, scene level is better.
        gridPane.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (engine == null || engine.isGameOver() || engine.hasWonGame()) return;

            Direction dir = null;
            if (e.getCode() == KeyCode.W || e.getCode() == KeyCode.UP) dir = Direction.UP;
            else if (e.getCode() == KeyCode.S || e.getCode() == KeyCode.DOWN) dir = Direction.DOWN;
            else if (e.getCode() == KeyCode.A || e.getCode() == KeyCode.LEFT) dir = Direction.LEFT;
            else if (e.getCode() == KeyCode.D || e.getCode() == KeyCode.RIGHT) dir = Direction.RIGHT;

            if (dir != null) {
                processMove(dir);
                e.consume(); // Consume event so it doesn't propagate further
            }
        });
        gridPane.setFocusTraversable(true); // Important for key events
        // Request focus after scene is shown for key input to work immediately
        // Platform.runLater(() -> gridPane.requestFocus()); // Or set on scene
    }

    public void setInitialDifficulty(int difficulty) {
        this.initialDifficulty = difficulty;
        engine = new GameEngine(this.initialDifficulty);
        engine.startNewGame(); // Start Level 1

        resetAndStartTimer();
        updateGui();
        gridPane.setDisable(false); // Ensure grid is enabled
        if (statusTextArea != null) statusTextArea.clear();
        appendToStatus("Game started. Level 1. Difficulty: " + engine.getState().getDifficulty());
        gridPane.requestFocus(); // Try to get focus for keyboard
    }

    private void resetAndStartTimer() {
        if (timeline != null) {
            timeline.stop();
        }
        elapsedTime = 0;
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            elapsedTime++;
            updateTimerLabel();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateTimerLabel() {
        if (timerLabel == null) return;
        int minutes = elapsedTime / 60;
        int seconds = elapsedTime % 60;
        timerLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));
    }

    private void updateGui() {
        if (engine == null || engine.getState() == null) { // Check if state is also initialized
            System.err.println("Engine or engine state not initialized in updateGui");
            return;
        }

        gridPane.getChildren().clear();
        Entity[][] entityMap = engine.getMapEntities();
        int playerX = engine.getPlayerX();
        int playerY = engine.getPlayerY();
        int mapSize = entityMap.length; // Assuming square map

        for (int r = 0; r < mapSize; r++) {
            for (int c = 0; c < mapSize; c++) {
                Cell guiCell = new Cell(); // dungeon.gui.Cell
                if (r == playerX && c == playerY) {
                    guiCell.setPlayerSymbol(); // TODO: Change to setPlayerIcon() later
                } else {
                    guiCell.setEntity(entityMap[r][c]); // TODO: Change to setEntityIcon() later
                }
                gridPane.add(guiCell, c, r); // GridPane uses (column, row)
            }
        }

        Player player = engine.getPlayer();
        if (player != null) {
            healthLabel.setText("HP: " + player.getHp());
            scoreLabel.setText("Score: " + player.getScore());
        }
        stepsLabel.setText("Steps: " + engine.getSteps() + "/" + engine.getMaxSteps());
        if (levelLabel != null) levelLabel.setText("Level: " + engine.getState().getLevel());
        if (difficultyLabel!= null) difficultyLabel.setText("Difficulty: " + engine.getState().getDifficulty());


        // Check game end conditions after updating primary stats
        if (engine.isGameOver()) {
            timeline.stop();
            player.setScore(-1); // Set score to -1 on loss
            scoreLabel.setText("Score: " + player.getScore()); // Update score display
            showGameOverAlert(player.getHp() <= 0);
            gridPane.setDisable(true);
        } else if (engine.hasWonGame()) {
            timeline.stop();
            showWinAlert();
            gridPane.setDisable(true);
            // TODO: Handle Top Scores
        } else if (engine.getState().hasReachedLadderThisTurn() && engine.getState().getLevel() == 1) {
            // Player is on ladder on level 1, ready to advance
            appendToStatus("You found the ladder!");
            if (engine.advanceToNextLevel()) {
                appendToStatus("Advanced to Level 2!");
                resetAndStartTimer(); // Reset timer for new level
                updateGui(); // Redraw for new level
            } else {
                appendToStatus("Error advancing level."); // Should not happen if logic is correct
            }
        }
    }

    private void appendToStatus(String message) {
        if (statusTextArea != null) {
            statusTextArea.appendText(message + "\n");
        }
        System.out.println("GUI_STATUS: " + message); // Fallback to console
    }

    private void processMove(Direction direction) {
        if (engine == null || engine.isGameOver() || engine.hasWonGame()) {
            return;
        }
        List<String> messages = engine.handlePlayerMove(direction);
        for (String msg : messages) {
            appendToStatus(msg);
        }
        updateGui(); // Update GUI after move and messages
        gridPane.requestFocus(); // Keep focus for keyboard input
    }


    private void showGameOverAlert(boolean dueToHp) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        if (dueToHp) {
            alert.setContentText("You have died. Game Over!\nFinal Score: " + engine.getPlayer().getScore());
            appendToStatus("Game Over - You died.");
        } else {
            alert.setContentText("You have reached the maximum steps. Game Over!\nFinal Score: " + engine.getPlayer().getScore());
            appendToStatus("Game Over - Max steps reached.");
        }
        alert.showAndWait();
    }

    private void showWinAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Congratulations!");
        alert.setHeaderText("You Won!");
        alert.setContentText("You've successfully escaped the dungeon from Level 2!\nFinal Score: " + engine.getPlayer().getScore());
        alert.showAndWait();
        appendToStatus("Congratulations! You won!");
    }

    @FXML private void moveUp() { processMove(Direction.UP); }
    @FXML private void moveDown() { processMove(Direction.DOWN); }
    @FXML private void moveLeft() { processMove(Direction.LEFT); }
    @FXML private void moveRight() { processMove(Direction.RIGHT); }

    @FXML
    private void onDifficultyChanged(ActionEvent event) {
        // This is likely not used if difficulty is set at the start.
        // If you want to allow changing difficulty mid-game (not recommended by assignment structure),
        // you would need to restart the game with the new difficulty.
        // String selected = difficultyComboBox.getValue();
        // appendToStatus("Difficulty change handling not fully implemented here.");
    }
}