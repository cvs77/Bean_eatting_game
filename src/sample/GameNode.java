package sample;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Random;
import java.util.function.IntToDoubleFunction;

/**
 * Created by mgao on 9/9/15.
 */
public class GameNode  extends  UnicastRemoteObject implements Server, Player {
    char[][] GlobalMap;
    Data GlobalData;
    HashMap<Integer,Player> playerList;
    boolean serverFlag=false;
    Server MainServerStub=null;
    Server BackupServerStub=null;
    boolean backUpServerFlag=false;
    static int playerCount=0;
    GameOnDrawCallBack callback;
    Map map;


    GameNode(int mapSize, boolean server) throws RemoteException {
        super();
        serverFlag=server;
        GlobalMap=new char[mapSize][mapSize];
        GlobalData =new Data();
        playerList = new HashMap<Integer,Player>();
    }
    GameNode(boolean server,boolean backup) throws RemoteException {
        super();
        serverFlag=server;
        backUpServerFlag=backup;
    }
    public void setMap(Map m){
        map=m;
    }
    public void setGameOnDrawCallBack(GameOnDrawCallBack callback){
        this.callback=callback;
    }
    public void setMainServerStub(Server s){
        MainServerStub =s;
    }
    public void setBackupServerStub(Server s){
        BackupServerStub =s;
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
    public void AddPlayer(int id){
        Random mapRan=new Random();
        Data.Coord tempC=GlobalData.new Coord();
        do{
            tempC.x = mapRan.nextInt(GlobalMap.length-2)  + 1;
            tempC.y = mapRan.nextInt(GlobalMap.length-2)  + 1;
        }while(GlobalMap[tempC.x][tempC.y]!=0);
        GlobalMap[tempC.x][tempC.y]=(char) (id+10);
        GlobalData.players.put(id,tempC);
    }
    @Override
    public char[][] GetMapFromSever() throws RemoteException {

        return  GlobalMap;
    }


    @Override
    public Integer  regestryToServer(Player player) throws RemoteException {
        synchronized (this){
            playerCount++;
            playerList.put(playerCount, player);
            AddPlayer(playerCount);
            return new Integer(playerCount);
        }

    }


    @Override
    public char[][] move(int id, int xO, int yO,int xN,int yN) {

        synchronized (this) {
            //Check the place available
            if (GlobalMap[xN][yN] == 2) {
                GlobalMap[xN][yN]=(char)(id+10);
                try {
                    playerList.get(id).setTreasures();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                GlobalMap[xO][yO]=0;
            }
            if(GlobalMap[xN][yN]==0){
                GlobalMap[xN][yN]=(char)(id+10);
                GlobalMap[xO][yO]=0;
            }
            if(backUpServerFlag!=true){
                BackupServerStub=pickBackUpServer();
            }
            else{
                try {
                    BackupServerStub.backUp(GlobalMap);
                } catch (RemoteException e) {
                    BackupServerStub=pickBackUpServer();
                    try {
                        BackupServerStub.backUp(GlobalMap);
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
            }
        }
        return GlobalMap;
    }
    private Server pickBackUpServer(){
        Server BackUpServer = null;

            for(Integer key:playerList.keySet()){
                try {
                    if(playerList.get(key).promoteToBackupServer(playerList)){
                        BackUpServer= (Server) playerList.get(key);

                    }
                } catch (RemoteException e) {
                    playerList.remove(key);
                    e.printStackTrace();
                }
            }
        for(Integer key:playerList.keySet()){
            try {
                playerList.get(key).setBackUpServer(BackUpServer);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return  BackUpServer;


    }
    @Override
    public char[][] upDate() throws RemoteException {
        return GlobalMap;
    }

    @Override
    public void  backUp(char[][] map) throws RemoteException {
        GlobalMap=map;
    }

    @Override
    public String serverHeartBeat() throws RemoteException {
        return null;
    }

    @Override
    public void  setBackUpServer(Server s) throws RemoteException {
        BackupServerStub=s;
        map.backUpServer=s;

    }

    @Override
    public boolean promoteToBackupServer(HashMap<Integer,Player> map) throws RemoteException {
        playerList=new HashMap<Integer,Player>();
        for(Integer key:map.keySet()){
            playerList.put(key,map.get(key));
        }
        return true;
    }

    @Override
    public void gameOn() throws RemoteException {
        map.map=MainServerStub.upDate();
        callback.gameOndrawTP();

    }

    @Override
    public void setTreasures() throws RemoteException {
        map.TreasureCount++;
    }

    @Override
    public boolean playerHearBeat() throws RemoteException {

        return true;
    }
}
