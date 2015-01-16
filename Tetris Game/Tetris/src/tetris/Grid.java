package tetris;

import java.awt.*;
import javax.swing.ImageIcon;

public class Grid {

    private final boolean[][] locked = new boolean[10][20];
    private final int[][] colour = new int[10][20];

    //canHold
    //Pre: must be sent a Shape and the amount to shift the Shape by
    //Use: determines whether the Grid can hold the shape if it is shifted by the given amount (not covering another square or going off-screen)
    //Returns: if the Grid can hold the Shape (true) or not (false)
    public boolean canHold(Shape s, int x, int y) {
        try {
            for (Point point : s.getPoints()) {
                if (isLocked(point.x + x, point.y + y)) {
                    return false;
                }
            }
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return true;
    }

    //removeRows
    //Pre: none
    //Use: determines the number of lines that are "filled" and removes them (also shifts the blocks above the removed lines down by one)
    //Returns: the number of lines removed
    public int removeRows() {
        int lines = 0;
        for (int i = 20 - 1; i >= 0; i--) {
            int counter = 0;
            for (int j = 0; j < 10; j++) {
                if (isLocked(j, i)) {
                    counter++;
                }
            }
            if (counter == 10) {
                lines++;
                //shift blocks down
                for (int j = 0; j < 10; j++) {
                    for (int k = i; k > 0; k--) {
                        lock(j, k, isLocked(j, k - 1));
                        setColour(j, k, getColour(j, k - 1));
                    }
                }
                for (int j = 0; j < 10; j++) {
                    lock(j, 0, false);
                }
                i++;
            }
        }
        return lines;
    }

    //lock
    //Pre: must be sent a Shape
    //Use: locks the Shape into the Grid
    //Returns: none
    public void lock(Shape s) {
        for (Point point : s.getPoints()) {
            lock(point.x, point.y, true);
            setColour(point.x, point.y, s.getColour());
        }
    }

    //lock
    //Pre: must be sent a coordinate to lock/unlock and whether to lock (true) or unlock (false) the coordinate
    //Use: locks or unlocks the Shape
    //Returns: none
    public void lock(int x, int y, boolean b) {
        locked[x][y] = b;
    }

    //isLocked
    //Pre: must be sent a coordinate to examine
    //Use: determines whether the given coordinate on the Grid is locked or not
    //Returns: whether the Grid at the given coordinate is locked (true) or not (false)
    public boolean isLocked(int x, int y) {
        return locked[x][y];
    }

    //setColour
    //Pre: must be sent a coordinate to colour and the colour to make it
    //Use: sets the colour of the given Grid coordinate to the given colour
    //Returns: none
    public void setColour(int x, int y, int c) {
        colour[x][y] = c;
    }

    //getColour
    //Pre: must be sent a coordinate to examine
    //Use: determines the colour of the given coordinate on the Grid
    //Returns: the colour of the Grid at the given coordinate
    public int getColour(int x, int y) {
        return colour[x][y];
    }

    //draw
    //Pre: must be sent the graphics to draw on
    //Use: draws the entire Grid and all locked squares within it
    //Returns: none
    public void draw(Graphics g) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 20; j++) {
                g.setColor(Color.white);
                g.drawRect(i * 30, j * 30, 30, 30);
                if (isLocked(i, j)) {
                    g.drawImage(new ImageIcon(getClass().getResource("/Files/" + colour[i][j] + ".png")).getImage(), 1 + i * 30, 1 + j * 30, null);
                }
            }
        }
    }
}
