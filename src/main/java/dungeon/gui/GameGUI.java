package dungeon.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// In GameGUI.java (or your main GUI setup class)
// ... other imports and class definition ...

public class GameGUI extends Application {

    private static int difficulty = 3; // Default difficulty, should match assignment's default

    public static void setDifficulty(int diff) { // This static method is fine for getting difficulty from RunGame
        difficulty = diff;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("game_gui.fxml"));
        Parent root = loader.load();

        Controller controller = loader.getController();
        // Make sure to call the correct method name here:
        controller.setInitialDifficulty(difficulty); // <<<< CHANGE THIS LINE

        primaryStage.setScene(new Scene(root, 800, 800)); // Or your preferred size
        primaryStage.setTitle("MiniDungeon Game");
        primaryStage.show();

        // Request focus for keyboard input after the scene is shown
        // If gridPane is the intended focus target in Controller:
        // Platform.runLater(() -> controller.getGridPane().requestFocus());
        // Or the scene itself, if key events are handled at scene level
        // root.requestFocus();
    }

    // ... main method if you have one here to launch ...
}
