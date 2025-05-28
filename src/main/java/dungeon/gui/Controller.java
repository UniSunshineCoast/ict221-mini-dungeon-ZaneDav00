package dungeon.gui;

import dungeon.engine.Cell;
import dungeon.engine.GameEngine;
import dungeon.engine.Player;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

public class Controller {
    @FXML
    private GridPane gridPane;

    @FXML
    private Label healthLabel;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label stepsLabel;
    @FXML
    private Label timeLabel;

    private GameEngine engine;
    private int elapsedTime = 0; // in seconds
    private Timeline timeline;

    @FXML
    public void initialize() {
        engine = new GameEngine(3); // difficulty level

        startTimer();
        updateGui();
        updateStatus();
    }

    private void startTimer() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            elapsedTime++;
            timeLabel.setText("Time: " + elapsedTime + "s");
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateGui() {
        gridPane.getChildren().clear();
        Cell[][] guiMap = engine.getGuiMap();

        for (int i = 0; i < engine.getSize(); i++) {
            for (int j = 0; j < engine.getSize(); j++) {
                gridPane.add(guiMap[i][j], j, i);
            }
        }

        gridPane.setGridLinesVisible(true);
    }

    private void updateStatus() {
        Player player = engine.getPlayer();
        healthLabel.setText("Health: " + player.getHp());
        scoreLabel.setText("Score: " + player.getScore());
        stepsLabel.setText("Steps: " + player.getSteps());
        // timeLabel updated by timer
    }

    private void tryMove(int dx, int dy) {
        if (engine.movePlayer(dx, dy)) {
            updateGui();
            updateStatus();
        }
    }

    @FXML
    private void moveUp() {
        tryMove(-1, 0);
    }

    @FXML
    private void moveDown() {
        tryMove(1, 0);
    }

    @FXML
    private void moveLeft() {
        tryMove(0, -1);
    }

    @FXML
    private void moveRight() {
        tryMove(0, 1);
    }

    // Optionally, you can stop the timer when the game ends or the player dies
    private void stopTimer() {
        if (timeline != null) {
            timeline.stop();
        }
    }
}

