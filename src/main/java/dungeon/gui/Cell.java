/**
 * Represents a single cell in the game grid's graphical user interface.
 * It displays entities or the player using images.
 * Author: Zane Davis
 * Student ID: 1174117
 * Due Date: 30th May 2025
 */
package dungeon.gui;

import dungeon.engine.Entity;
import dungeon.engine.Gold;
import dungeon.engine.HealthPotion;
import dungeon.engine.Ladder;
import dungeon.engine.MeleeMutant;
import dungeon.engine.RangedMutant;
import dungeon.engine.Trap;
import dungeon.engine.Entry;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
// import javafx.scene.shape.Rectangle; // Only needed if you want a visible background rectangle behind images

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Cell extends StackPane {

    private static final double CELL_SIZE = 30.0; // Or your preferred cell size
    private final ImageView imageView;

    // Image Cache to store loaded images and improve performance
    private static final Map<String, Image> imageCache = new HashMap<>();
    private static final Image emptyCellImage; // Your "dungeon.png" for empty/floor cells

    // Define the base path for your images ON THE CLASSPATH
    // This assumes images are in src/main/resources/dungeon/images/
    private static final String IMAGE_BASE_PATH = "/dungeon/images/";

    static {
        // Preload the image for empty cells (your "dungeon.png")
        emptyCellImage = loadImage(IMAGE_BASE_PATH + "dungeon.png");
        if (emptyCellImage == null) {
            System.err.println("CRITICAL WARNING: Default empty cell image (dungeon.png) not found at " +
                    IMAGE_BASE_PATH + "dungeon.png. Check resource path and file name.");
        }
    }

    /**
     * Helper method to load an image from resources and cache it.
     * Ensures images are loaded only once.
     * @param resourcePath The classpath-relative path to the image resource (e.g., "/dungeon/images/player.png").
     * @return The loaded Image object, or null if not found or on error.
     */
    private static Image loadImage(String resourcePath) {
        // This debug line helps verify the path being attempted for loading.
        System.out.println("DEBUG Cell.loadImage: Attempting to load resourcePath: [" + resourcePath + "]");

        if (imageCache.containsKey(resourcePath)) {
            Image cachedImage = imageCache.get(resourcePath);
            // If null was cached due to a previous load failure, return null to avoid re-attempting.
            return cachedImage;
        }
        try {
            // Cell.class.getResourceAsStream() loads resources from the classpath.
            Image image = new Image(Objects.requireNonNull(Cell.class.getResourceAsStream(resourcePath)));
            if (image.isError()) {
                System.err.println("Failed to load image: [" + resourcePath + "] - " + image.getException().getMessage());
                imageCache.put(resourcePath, null); // Cache null to indicate failure
                return null;
            }
            imageCache.put(resourcePath, image); // Cache successfully loaded image
            return image;
        } catch (NullPointerException e) {
            System.err.println("Error: Image resource not found at path: [" + resourcePath + "]. " +
                    "Ensure path is correct (starts with '/' for classpath root) " +
                    "and image is in the resources folder and included in the build.");
            imageCache.put(resourcePath, null);
            return null;
        } catch (Exception e) {
            System.err.println("Unexpected error loading image resource: [" + resourcePath + "] - " + e.getMessage());
            e.printStackTrace(); // Print full trace for other unexpected errors
            imageCache.put(resourcePath, null);
            return null;
        }
    }

    /**
     * Constructs a new visual Cell for the game grid.
     * Initializes an ImageView to display game elements.
     */
    public Cell() {
        // Optional: A background rectangle if your images have transparency
        // or if you want a border separate from the image content.
        // Rectangle background = new Rectangle(CELL_SIZE, CELL_SIZE);
        // background.setFill(Color.LIGHTSLATEGRAY); // Example background
        // getChildren().add(background);

        this.imageView = new ImageView();
        this.imageView.setFitWidth(CELL_SIZE);
        this.imageView.setFitHeight(CELL_SIZE);
        this.imageView.setPreserveRatio(true); // Adjust as needed for your art style

        getChildren().add(this.imageView); // Add ImageView to the StackPane

        // Set default visual (e.g., empty cell image)
        setVisual(null, false);
    }

    /**
     * Sets the visual representation of the cell using an appropriate image
     * based on the entity present or if it's the player's cell.
     * @param entity The game entity in the cell, or null if the cell is empty (shows floor/empty image).
     * @param isPlayerCell True if this cell currently contains the player.
     */
    public void setVisual(Entity entity, boolean isPlayerCell) {
        Image imageToDisplay = null;
        String imageFileName = null; // Just the filename, path is prepended from IMAGE_BASE_PATH

        if (isPlayerCell) {
            imageFileName = "player.png";
        } else if (entity != null) {
            // Determine image filename based on entity type
            switch (entity) {
                case Gold gold -> imageFileName = "gold.png";
                case Trap trap -> imageFileName = "trap_icon.png"; // Your specified name
                case Ladder ladder -> imageFileName = "ladder.png";
                case MeleeMutant meleeMutant -> imageFileName = "meleemonster.png"; // Your specified name
                case RangedMutant rangedMutant -> imageFileName = "rangedmonster.png"; // Your specified name
                case HealthPotion healthPotion -> imageFileName = "healthpotion.png"; // Your specified name
                case Entry entry -> imageFileName = "entry.png"; // Assuming you have/want entry.png
                default ->
                        System.err.println("Cell.setVisual: Unknown entity type, cannot determine image - " + entity.getClass().getSimpleName());
            }
        }

        // Load the determined image (or null if no specific entity/player)
        if (imageFileName != null) {
            imageToDisplay = loadImage(IMAGE_BASE_PATH + imageFileName);
        }

        // If no specific image was loaded (e.g., unknown entity, file not found, or cell is empty and not player),
        // then use the default empty cell image (your "dungeon.png").
        if (imageToDisplay == null) {
            imageToDisplay = emptyCellImage;
        }

        this.imageView.setImage(imageToDisplay);
    }
}