package sample;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

/**
 * Created by mgao on 8/9/15.
 */
public class Data  implements Serializable {

    public class Coord implements Serializable{
        Coord(int x,int y){
            this.x=x;
            this.y=y;
        }
        Coord(){
            ;
        }
        int x;
        int y;
    }
    public HashMap<Integer,Coord> treasures=new HashMap<Integer,Coord>();
    public HashMap<Integer,Coord> players=new HashMap<Integer,Coord>();
}
