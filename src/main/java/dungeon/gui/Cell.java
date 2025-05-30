/**
 * Represents a single cell in the game grid's graphical user interface.
 * It displays entities or the player using text symbols.
 * Author: Zane Davis
 * Student ID: 1174117
 * Due Date: 30th May 2025
 */
package dungeon.gui;

import dungeon.engine.Entity;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class Cell extends StackPane {

    private static final double DEFAULT_CELL_SIZE = 30.0;

    private Entity entity; // The game entity currently in this cell (can be null)
    private Text symbolText;   // JavaFX Text node to display the symbol

    /**
     * Constructs a new visual Cell for the game grid.
     * Initializes with a border and a text field for symbols.
     */
    public Cell() {
        Rectangle border = new Rectangle(DEFAULT_CELL_SIZE, DEFAULT_CELL_SIZE);
        border.setFill(Color.WHITE); // Background color of the cell
        border.setStroke(Color.BLACK); // Border color of the cell

        this.symbolText = new Text(); // Text to display P, G, M, etc.

        // StackPane layers children on top of each other; text will be on border
        getChildren().addAll(border, this.symbolText);
    }

    /**
     * Sets the game entity to be displayed in this cell.
     * Updates the cell's symbol text based on the entity.
     * @param entity The entity to display, or null for an empty cell.
     */
    public void setEntity(Entity entity) {
        this.entity = entity;
        if (entity != null) {
            this.symbolText.setText(String.valueOf(entity.getSymbol()));
        } else {
            this.symbolText.setText(""); // Clear text for empty cell
        }
    }

    /**
     * Sets the cell to display the player symbol ('P').
     */
    public void setPlayerSymbol() {
        // Note: This method means the cell won't store an 'Entity' object for the player directly.
        // The Player object is managed by GameState/GameEngine; this cell just shows 'P'.
        this.entity = null; // Or a special PlayerEntity if you had one and wanted to store it
        this.symbolText.setText("P");
    }

    /**
     * Clears any symbol currently displayed in the cell.
     * Effectively makes the cell appear empty.
     */
    public void clearSymbol() {
        this.entity = null;
        this.symbolText.setText("");
    }

    /**
     * Gets the entity currently associated with this cell.
     * May be null if the cell is empty or displays the player symbol via setPlayerSymbol().
     * @return The entity in this cell, or null.
     */
    public Entity getEntity() {
        return this.entity;
    }

    /**
     * Checks if the cell is considered empty (no entity set).
     * @return true if no entity is set, false otherwise.
     */
    public boolean isEmpty() {
        return this.entity == null;
    }
}