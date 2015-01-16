package tetris;

import java.awt.Point;

public class RightS extends Shape {

    //RightS constructor
    //Pre: none
    //Use: call Shape's constructor to create a new Shape (given location and colour)
    //Returns: none
    public RightS() {
        super(new Point(5, -1), new Point(5, 0), new Point(4, 1), new Point(4, 0), 6);
    }
}
