package pres.hanshuo.pojo;

import java.awt.*;
import java.util.Objects;

/**
 * @author Li Hanshuo
 */
public class Piece {

    private int x;
    private int y;
    private Color color;

    public Piece(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Piece piece = (Piece) o;
        return x == piece.x && y == piece.y && Objects.equals(color, piece.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, color);
    }


}
