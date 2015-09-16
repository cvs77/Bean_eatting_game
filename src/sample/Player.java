package sample;

/**
 * Created by mgao on 8/9/15.
 */
import java.rmi.RemoteException;
import java.rmi.Remote;
import java.util.HashMap;

public interface Player extends Remote{

    void setBackUpServer(Server s) throws RemoteException;
    void setMainServer(Server s) throws  RemoteException;
    boolean promoteToBackupServer(HashMap<Integer,Player> map) throws  RemoteException;
    void  gameOn() throws  RemoteException;
    boolean playerHearBeat() throws RemoteException;
}
