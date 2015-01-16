package tetris;

import java.awt.Point;

public class Tee extends Shape {

    //Tee constructor
    //Pre: none
    //Use: call Shape's constructor to create a new Shape (given location and colour)
    //Returns: none
    public Tee() {
        super(new Point(4, -1), new Point(5, 0), new Point(4, 1), new Point(4, 0), 5);
    }
}
