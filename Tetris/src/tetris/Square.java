package tetris;

import java.awt.Point;

public class Square extends Shape {

    //Square constructor
    //Pre: none
    //Use: call Shape's constructor to create a new Shape (given location and colour)
    //Returns: none
    public Square() {
        super(new Point(4, -1), new Point(5, -1), new Point(4, 0), new Point(5, 0), 1);
    }

    //rotate
    //Pre: must be sent the grid that contains the Square (overrides Shape's rotate, as the Square does not need to be rotated)
    //Use: none
    //Returns: returns that the shape has not been changed
    @Override
    public boolean rotate(Grid grid) {
        return false;
    }
}
