package tetris;

import java.awt.Point;

public class LeftL extends Shape {

    //LeftL constructor
    //Pre: none
    //Use: call Shape's constructor to create a new Shape (given location and colour)
    //Returns: none
    public LeftL() {
        super(new Point(5, 1), new Point(4, 1), new Point(4, -1), new Point(4, 0), 4);
    }
}
