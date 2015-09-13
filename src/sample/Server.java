package sample;

/**
 * Created by mgao on 8/9/15.
 */
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {
    char[][] GetMapFromSever() throws RemoteException;
    Integer regestryToServer(Player player) throws RemoteException;
    char[][] upDate() throws RemoteException;
    char[][] move(int id, int xO,int yO,int xN,int yN) throws RemoteException;
    String backUp() throws RemoteException;
    String serverHeartBeat() throws RemoteException;
    //String GetTheBackServer() throws RemoteException;
}
