package sample;

import java.rmi.RemoteException;
/**
 * Created by mgao on 9/9/15.
 */
public class GameServer implements Server {
    char[][] GlobalMap;
    Data GlobalData;
    GameServer(int mapSize){
        GlobalMap=new char[mapSize][mapSize];
        GlobalData =new Data();
    }
    public void initializeServer(char[][] map, Data data){
        for(int i=0;i<map.length;i++){
            for(int j=0;j<map.length;j++){
                GlobalMap[i][j]=map[i][j];
            }
        }
        for(int key:data.treasures.keySet()){
            GlobalData.treasures.put(key, data.treasures.get(key));
        }
    }

    @Override
    public char[][] GetMapFromSever() throws RemoteException {
        return  GlobalMap;
    }

    @Override
    public String Hello() throws RemoteException {
        return "Hello World";
    }

    @Override
    public String upDate() throws RemoteException {
        return null;
    }

    @Override
    public String backUp() throws RemoteException {
        return null;
    }

    @Override
    public String heartBeat() throws RemoteException {
        return null;
    }
}
