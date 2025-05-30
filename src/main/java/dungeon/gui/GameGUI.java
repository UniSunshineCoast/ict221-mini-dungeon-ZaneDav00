/**
 * Sets up and displays the main JavaFX graphical user interface for the MiniDungeon game.
 * It loads the FXML layout, initializes the controller, and shows the primary stage.
 * Author: Zane Davis
 * Student ID: 1174117
 * Due Date: 30th May 2025
 */
package dungeon.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GameGUI extends Application {

    private static int initialDifficulty = 3; // Default difficulty, can be updated before start()

    /**
     * Sets the initial difficulty for the game.
     * This method is called by the launcher (e.g., RunGame) before the GUI starts.
     * @param difficulty The selected difficulty level.
     */
    public static void setDifficulty(int difficulty) {
        GameGUI.initialDifficulty = difficulty;
    }

    /**
     * The main entry point for this JavaFX application, called after launch().
     * Loads the FXML, sets up the controller with the initial difficulty,
     * and displays the primary game window.
     * @param primaryStage The primary stage for this application, onto which
     * the application scene can be set.
     * @throws Exception if the FXML file cannot be loaded.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("game_gui.fxml"));
        Parent root = loader.load();

        Controller controller = loader.getController();
        controller.setInitialDifficulty(initialDifficulty); // Pass the stored difficulty

        Scene scene = new Scene(root, 800, 800); // Preferred scene size
        primaryStage.setScene(scene);
        primaryStage.setTitle("MiniDungeon Game");
        primaryStage.show();

        // Note: For keyboard focus on the gridPane, you might use Platform.runLater
        // in the Controller's initialize or setInitialDifficulty method
        // to request focus once the scene is shown e.g. gridPane.requestFocus().
    }

    // If you intend to launch this GameGUI directly (without RunGame),
    // you would add a main method here:
    // public static void main(String[] args) {
    //     launch(args);
    // }
}