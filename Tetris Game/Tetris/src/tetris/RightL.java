package tetris;

import java.awt.Point;

public class RightL extends Shape {

    //RightL constructor
    //Pre: none
    //Use: call Shape's constructor to create a new Shape (given location and colour)
    //Returns: none
    public RightL() {
        super(new Point(4, 1), new Point(5, 1), new Point(5, -1), new Point(5, 0), 3);
    }
}
