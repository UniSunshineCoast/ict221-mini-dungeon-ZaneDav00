package dungeon.gui;

import javafx.application.Platform;
import javafx.scene.control.ChoiceDialog;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RunGame {

    private static int difficulty = 3;  // Default difficulty

    public static void main(String[] args) {
        Platform.startup(() -> {
            List<Integer> choices = new ArrayList<>();
            // Populate choices from 0 to 10
            for (int i = 0; i <= 10; i++) {
                choices.add(i);
            }

            // --- Debugging output ---
            System.out.println("RunGame: Preparing ChoiceDialog.");
            System.out.println("RunGame: Choices list size: " + choices.size());
            System.out.println("RunGame: Choices list content: " + choices.toString());
            // --- End Debugging output ---

            if (choices.isEmpty()) {
                System.err.println("RunGame: CRITICAL ERROR - Choices list is empty! Dialog will be blank.");
                // You could show an error alert here and exit, or just proceed to see the blank dialog.
            }

            ChoiceDialog<Integer> dialog = new ChoiceDialog<>(difficulty, choices);
            dialog.setTitle("Select Difficulty Level");
            dialog.setHeaderText("Choose a game difficulty level (0-10).");
            dialog.setContentText("Difficulty:");

            System.out.println("RunGame: ChoiceDialog created. Showing dialog...");
            Optional<Integer> result = dialog.showAndWait();
            System.out.println("RunGame: ChoiceDialog closed.");

            if (result.isPresent()) {
                difficulty = result.get();
                System.out.println("RunGame: Difficulty selected: " + difficulty);
            } else {
                System.out.println("RunGame: No difficulty selected (dialog cancelled), using default: " + difficulty);
            }

            GameGUI.setDifficulty(difficulty);
            try {
                GameGUI mainGameGui = new GameGUI();
                Stage gameStage = new Stage();
                mainGameGui.start(gameStage);
            } catch (Exception e) {
                System.err.println("RunGame: Error starting GameGUI: " + e.getMessage());
                e.printStackTrace();
                Platform.exit();
            }
        });
    }
}