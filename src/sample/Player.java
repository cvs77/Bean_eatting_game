package sample;

/**
 * Created by mgao on 8/9/15.
 */
import java.rmi.RemoteException;
import java.rmi.Remote;
public interface Player extends Remote{
    String promoteToBackUpServer() throws RemoteException;
    String gameOn() throws  RemoteException;
    String hearBeat() throws RemoteException;
}
