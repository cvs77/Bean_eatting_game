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
    //Treasure Number
    int m;
    int playerId;
    static int  playerCount;
    public char[][] map;
    public Data data=new Data();

    Map(int n,int m){
        this.n=n;
        this.m=m;
        map =new char[n][n];
        playerId=0;
        playerCount=0;
    }
    Map(int n){
        this.n=n;
        map =new char[n][n];
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

        for(int i=0;i<3*n;i++){
            map[mapRan.nextInt(n-2)+1][mapRan.nextInt(n-2)+1]=1;
        }

        for(int i=0;i<m;i++){
            Data.Coord tempC=data.new Coord();
            do{
                tempC.x = mapRan.nextInt(n-2)  + 1;
                tempC.y = mapRan.nextInt(n-2)  + 1;
            }while(map[tempC.x][tempC.y]==1||map[tempC.x][tempC.y]==2);
            map[tempC.x][tempC.y]=2;
            data.treasures.put(i, tempC);
        }
    }
    public int ServerAddPlayer(){
        Random mapRan=new Random();
        Data.Coord tempC=data.new Coord();
        do{
            tempC.x = mapRan.nextInt(n-2)  + 1;
            tempC.y = mapRan.nextInt(n-2)  + 1;
        }while(map[tempC.x][tempC.y]==1||map[tempC.x][tempC.y]==2||map[tempC.x][tempC.y]==3);
        map[tempC.x][tempC.y]=3;
        data.treasures.put(playerCount++,tempC);
        return playerCount;
    }

    public Data getData(){
        return data;
    }




}
