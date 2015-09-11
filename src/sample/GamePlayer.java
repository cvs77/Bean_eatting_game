package sample;

import java.rmi.RemoteException;

/**
 * Created by mgao on 10/9/15.
 */
public class GamePlayer implements Player {
    @Override
    public String promoteToBackUpServer() throws RemoteException {
        return null;
    }

    @Override
    public String gameOn() throws RemoteException {
        return null;
    }

    @Override
    public String hearBeat() throws RemoteException {
        return null;
    }
}
