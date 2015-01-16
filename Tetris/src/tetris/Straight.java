package tetris;

import java.awt.Point;

public class Straight extends Shape {

    //Straight constructor
    //Pre: none
    //Use: call Shape's constructor to create a new Shape (given location and colour)
    //Returns: none
    public Straight() {
        super(new Point(3, -1), new Point(4, -1), new Point(6, -1), new Point(5, -1), 2);
    }
}
