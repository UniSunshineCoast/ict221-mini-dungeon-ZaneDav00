/**
 * Main entry point for the MiniDungeon GUI application.
 * Handles initial difficulty selection before launching the main game window.
 * Author: Zane Davis
 * Student ID: 1174117
 * Due Date: 30th May 2025
 */
package dungeon.gui;

import javafx.application.Application;
import javafx.scene.control.ChoiceDialog;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RunGame extends Application {

    private static int selectedDifficulty = 3;  // Default difficulty, matching assignment

    /**
     * Main method to launch the JavaFX application.
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * The main entry point for this JavaFX application.
     * Shows a difficulty selection dialog and then launches the GameGUI.
     * @param ignoredPrimaryStage The primary stage provided by JavaFX (hidden in this setup).
     */
    @Override
    public void start(Stage ignoredPrimaryStage) {
        // Hide the initial stage provided by Application.start() as we are using dialogs/new stages.
        if (ignoredPrimaryStage != null) {
            ignoredPrimaryStage.hide();
        }

        Optional<Integer> result = getInteger();

        result.ifPresent(choice -> selectedDifficulty = choice);
        // If dialog is cancelled, selectedDifficulty remains its default value (3).

        // Pass the chosen (or default) difficulty to GameGUI
        GameGUI.setDifficulty(selectedDifficulty);

        try {
            // Create and show the main game stage
            GameGUI mainGameGui = new GameGUI();
            Stage gameStage = new Stage(); // GameGUI will use this new stage
            mainGameGui.start(gameStage);
        } catch (Exception e) {
            System.err.println("RunGame: Error starting GameGUI - " + e.getMessage());
            e.printStackTrace();
            javafx.application.Platform.exit(); // Exit if the main game GUI fails to load
        }
    }

    private static Optional<Integer> getInteger() {
        List<Integer> difficultyChoices = new ArrayList<>();
        for (int i = 0; i <= 10; i++) { // Difficulty options 0-10
            difficultyChoices.add(i);
        }

        ChoiceDialog<Integer> difficultyDialog = new ChoiceDialog<>(selectedDifficulty, difficultyChoices);
        difficultyDialog.setTitle("Select Difficulty Level");
        difficultyDialog.setHeaderText("Choose a game difficulty level (0-10).");
        difficultyDialog.setContentText("Difficulty:");

        Optional<Integer> result = difficultyDialog.showAndWait();
        return result;
    }
}