package sample;

import java.util.HashMap;

/**
 * Created by mgao on 8/9/15.
 */
public class Data {
    public class Coord{
        int x;
        int y;
    }
    public HashMap<Integer,Coord> treasures=new HashMap<Integer,Coord>();
    public HashMap<Integer,Coord> players=new HashMap<Integer,Coord>();
}
