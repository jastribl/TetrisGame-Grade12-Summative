package tetris;

import java.awt.*;
import javax.swing.ImageIcon;

public abstract class Shape {

    private final Point[] points;
    private final int colour;

    //Shape constructor
    //Pre: must be sent the locations of all squares in the Shape
    //Use: initializes all valiables for the Shape
    //Returns: none
    public Shape(Point p1, Point p2, Point p3, Point p4, int c) {
        points = new Point[4];
        points[0] = p1;
        points[1] = p2;
        points[2] = p3;
        points[3] = p4;
        colour = c;
    }

    //isResting
    //Pre: must be sent the Grid that contains the Shape
    //Use: determines whether any part of the Shape is resting on the bottom of the grid or on another square
    //Returns: returns if the Shape is resting (true) or not (false)
    public boolean isResting(Grid grid) {
        for (Point point : points) {
            if (point.y == 19) {
                return true;
            } else if (grid.isLocked(point.x, point.y + 1)) {
                return true;
            }
        }
        return false;
    }

    //getPoints
    //Pre: none
    //Use: none
    //Returns: retunes the locations of all squares in the Shape
    public Point[] getPoints() {
        return points;
    }

    //getColour
    //Pre: none
    //Use: none
    //Returns: returns the colour of the Shape
    public int getColour() {
        return colour;
    }

    //rotate
    //Pre: must be sent the Grid that contains the Shape
    //Use: rotates the Shape 90° clockwise
    //Returns: whether the Shape has been or can be rotated
    public boolean rotate(Grid grid) {
        Point center = points[3];//Point to rotate about
        for (int i = 0; i < points.length - 1; i++) {
            if (points[i].x > center.x) {//if point is to the right of the center
                if (points[i].y > center.y) {//if point is above the center
                    points[i].x -= 2;
                } else if (points[i].y < center.y) {//if point is below the center
                    points[i].y += 2;
                } else {//if point is directly to the right of the center
                    points[i].y += points[i].x - center.x;
                    points[i].x = center.x;
                }
            } else if (points[i].x < center.x) {//if point is to the left of the center
                if (points[i].y > center.y) {//if point is above the center
                    points[i].y -= 2;
                } else if (points[i].y < center.y) {//if point is below the center
                    points[i].x += 2;
                } else {//if point is directly to the left of the center
                    points[i].y -= center.x - points[i].x;
                    points[i].x = center.x;
                }
            } else {//if the point is above or below the center
                if (points[i].y > center.y) {//if point is above the center
                    points[i].x -= points[i].y - center.y;
                    points[i].y = center.y;
                } else {//if point is below the center
                    points[i].x += center.y - points[i].y;
                    points[i].y = center.y;
                }
            }
        }
        //if the Shape cannot stay where it is, try moving it by 1 or 2 squares in each direction
        if (!grid.canHold(this, 0, 0) && !move(grid, 1, 0) && !move(grid, -1, 0) && !move(grid, 0, -1) && !move(grid, 0, 1) && !move(grid, 2, 0) && !move(grid, -2, 0) && !move(grid, 0, -2) && !move(grid, 0, 2)) {
            //if the Shape cannot go in any of these locations, call rotateBack
            rotateBack();
            return false;
        }
        return true;
    }

    //rotateBack
    //Pre: none
    //Use: rotates the Shape back to its original location (does exact opposite to rotate)
    //Returns: none
    public void rotateBack() {
        Point center = points[3];
        for (int i = 0; i < points.length - 1; i++) {
            if (points[i].x > center.x) {
                if (points[i].y > center.y) {
                    points[i].y -= 2;
                } else if (points[i].y < center.y) {
                    points[i].x -= 2;
                } else {
                    points[i].y -= points[i].x - center.x;
                    points[i].x = center.x;
                }
            } else if (points[i].x < center.x) {
                if (points[i].y > center.y) {
                    points[i].x += 2;
                } else if (points[i].y < center.y) {
                    points[i].y += 2;
                } else {
                    points[i].y += center.x - points[i].x;
                    points[i].x = center.x;
                }
            } else {
                if (points[i].y > center.y) {
                    points[i].x += points[i].y - center.y;
                    points[i].y = center.y;
                } else {
                    points[i].x -= center.y - points[i].y;
                    points[i].y = center.y;
                }
            }
        }
    }

    //slamDown
    //Pre: must be sent the Grid that contains the Shape
    //Use: moves the Shape down quickly until it is resting on the bottom or on another square
    //Returns: none
    public void slamDown(Grid grid) {
        while (!isResting(grid)) {
            move(grid, 0, 1);
        }
        grid.lock(this);
    }

    //move
    //Pre: must be sent the Grid that contains the Shape and the amounts to move the shape by
    //Use: determines whether the shape can be moved by the given amount, and if so, moves it
    //Returns: whether the Shape has been or can be moved to the given location
    public boolean move(Grid grid, int x, int y) {
        if (x == 0 && y == 0) {
            return false;
        } else if (grid.canHold(this, x, y)) {
            for (Point point : points) {
                point.x += x;
                point.y += y;
            }
            return true;
        }
        return false;
    }

    //draw
    //Pre: must be sent the graphics to draw on and the amount to shift the shape by (used for drawing the shape to the "next shape" grid)
    //Use: draws each square of the Shape to the panel
    //Returns: none
    public void draw(Graphics g, int x, int y) {
        for (Point point : getPoints()) {
            g.drawImage(new ImageIcon(getClass().getResource("/Files/" + colour + ".png")).getImage(), x + point.x * 30, y + point.y * 30, null);
        }
    }
}
