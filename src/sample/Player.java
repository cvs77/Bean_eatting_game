package sample;

/**
 * Created by mgao on 8/9/15.
 */
import java.rmi.RemoteException;
import java.rmi.Remote;
import java.util.HashMap;

public interface Player extends Remote{

    String setBackUpServer() throws RemoteException;
    String promoteToBackupServer() throws  RemoteException;
    void  gameOn() throws  RemoteException;
    void setTreasures() throws RemoteException;
    String playerHearBeat() throws RemoteException;
}
