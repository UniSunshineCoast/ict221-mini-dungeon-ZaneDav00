package dungeon.gui;

import dungeon.engine.Entity;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class Cell extends StackPane {
    private Entity entity;
    private Text symbolText;

    public Cell() {
        Rectangle border = new Rectangle(30, 30);
        border.setFill(Color.WHITE);
        border.setStroke(Color.BLACK);

        symbolText = new Text();
        getChildren().addAll(border, symbolText);
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
        if (entity != null) {
            symbolText.setText(String.valueOf(entity.getSymbol()));
        } else {
            symbolText.setText("");
        }
    }

    public void setPlayerSymbol() {
        symbolText.setText("P");
    }

    public void clearSymbol() {
        symbolText.setText("");
    }

    public Entity getEntity() {
        return entity;
    }

    public boolean isEmpty() {
        return entity == null;
    }
}
