package sample;

/**
 * Created by mgao on 8/9/15.
 */
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {
    char[][] GetMapFromSever() throws RemoteException;
    String Hello() throws  RemoteException;
    String upDate() throws RemoteException;
    String backUp() throws RemoteException;
    String heartBeat() throws RemoteException;
    //String GetTheBackServer() throws RemoteException;
}
