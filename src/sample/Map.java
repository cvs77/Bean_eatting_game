package sample;

import com.sun.security.ntlm.*;
import com.sun.xml.internal.xsom.impl.scd.Iterators;
import sample.Data;

import java.rmi.RemoteException;
import java.util.Random;

/**
 * Created by mgao on 8/9/15.
 */
public class Map{
    //Map size
    int n;
    Server gameServer;
    //Treasure Number
    int m;
    int playerId=0;
    int TreasureCount=0;
    public char[][] map;
    public Data data;

    Map(int n,int m) throws RemoteException {
        this.n=n;
        this.m=m;
        map =new char[n][n];
        playerId=0;

        data = new Data();
    }
    Map(int n) throws RemoteException {
        this.n=n;
        map =new char[n][n];
        data = new Data();
    }
    void setGameServer(Server s){
        gameServer=s;

    }

    public void generateMap(){
        /*Generate the map*/
        Random mapRan=new Random();
        for(int i=0; i<n;i++){
            map[0][i]=1;
            map[i][0]=1;
            map[n-i-1][n-1]=1;
            map[n-1][n-i-1]=1;
        }
        //Build the wall in the middle

        for(int i=0;i< 3 * n;i++){
            map[mapRan.nextInt(n-2)+1][mapRan.nextInt(n-2)+1]=1;
        }

        for(int i=0;i<m;i++){
            Data.Coord tempC=data.new Coord();
            do{
                tempC.x = mapRan.nextInt(n-2)  + 1;
                tempC.y = mapRan.nextInt(n-2)  + 1;
            }while(map[tempC.x][tempC.y]!=0);
            map[tempC.x][tempC.y]=2;
            data.treasures.put(i, tempC);
        }
    }

    public void moveUp() throws RemoteException {
        int x=data.players.get(playerId).x;
        int y= data.players.get(playerId).y;
            //Get the data from the server
            map=gameServer.move(playerId,x,y,x,y-1);

    }
    public void moveDown() throws RemoteException{
        int x=data.players.get(playerId).x;
        int y= data.players.get(playerId).y;

            //Get the data from the server
            map=gameServer.move(playerId,x,y,x,y+1);
            //todo try catch


    }
    public void moveLeft() throws RemoteException{
        int x=data.players.get(playerId).x;
        int y= data.players.get(playerId).y;

            //Get the data from the server
            map=gameServer.move(playerId,x,y,x-1,y);
            //todo try catch


    }
    public void moveRight() throws RemoteException{
        int x=data.players.get(playerId).x;
        int y= data.players.get(playerId).y;

            //Get the data from the server
            map=gameServer.move(playerId,x,y,x+1,y);
            //todo try catch
       
    }

    public Data getData(){
        return data;
    }




}
