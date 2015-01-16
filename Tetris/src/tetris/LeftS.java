package tetris;
import java.awt.Point;

public class LeftS extends Shape {

    //LeftS constructor
    //Pre: none
    //Use: call Shape's constructor to create a new Shape (given location and colour)
    //Returns: none
    public LeftS() {
        super(new Point(4, -1), new Point(4, 0), new Point(5, 1), new Point(5, 0), 7);
    }
}
