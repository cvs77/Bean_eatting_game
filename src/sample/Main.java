package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.ColumnConstraintsBuilder;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class Main extends Application {
    final static int gridSize=30;
    int mapSize;
    int treasureN;
    Map map;
    GridPane root;
    Canvas Treasures=null;
    Canvas Players=null;
    Canvas MapLandScape;
    GridPane sideBar;
    final ToggleGroup group = new ToggleGroup();
    RadioButton server;
    RadioButton client;
    Label label1,label2,labelCount,labelCount2,serverflag;
    TextField textField1, textField2;
    Button startButton;
    Button gameOnButtonTest;
    GameNode gameServer;

    Player gamePlayer;
    Server ServerStub=null;
    Server BackUpServerStub=null;
    Registry registry = null;
    EventHandler<KeyEvent> keyEventHandler;
    GameOnDrawCallBack callback=new GameOnDrawCallBack() {
        @Override
        public void gameOndrawTP() {
            Platform.runLater(() -> {
                drawThePlayers();
                drawTheTreasure();
                setServerFlagLabel();
            });

        }
    };

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Distributed System Assignment 1");
        //Group root = new Group();
        root = new GridPane();
        root.setPadding(new Insets(10, 10, 10, 10));
        root.setVgap(5);
        root.setHgap(5);


        //Draw the Map
        drawSideBar();

        keyEventHandler =
                new EventHandler<KeyEvent>() {
                    public void handle(final KeyEvent keyEvent) {
                        if (keyEvent.getCode() == KeyCode.UP) {
                            try {
                                map.moveUp();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                            finally {
                                labelCount2.setText(new Integer(map.map[map.map.length - 1][map.playerId]).toString());
                                drawThePlayers();
                                drawTheTreasure();
                                keyEvent.consume();
                                setServerFlagLabel();
                            }

                        }
                        if(keyEvent.getCode() == KeyCode.DOWN){
                            try {
                                map.moveDown();
                            } catch (RemoteException e) {
                                e.printStackTrace();

                            }
                            finally {
                                labelCount2.setText(new Integer(map.map[map.map.length-1][map.playerId]).toString());
                                drawThePlayers();
                                drawTheTreasure();
                                keyEvent.consume();
                                setServerFlagLabel();
                            }

                        }
                        if(keyEvent.getCode() == KeyCode.LEFT){
                            try {
                                map.moveLeft();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                            finally {
                                labelCount2.setText(new Integer(map.map[map.map.length-1][map.playerId]).toString());

                                drawThePlayers();
                                drawTheTreasure();
                                keyEvent.consume();
                                setServerFlagLabel();
                            }

                        }
                        if(keyEvent.getCode() == KeyCode.RIGHT){
                            try {
                                map.moveRight();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                            finally {
                                labelCount2.setText(new Integer(map.map[map.map.length-1][map.playerId]).toString());
                                drawThePlayers();
                                drawTheTreasure();
                                keyEvent.consume();
                                setServerFlagLabel();
                            }

                        }
                    }
                };

        root.setOnKeyPressed(keyEventHandler);

        startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                //if server state
                if (server.isSelected()) {
                    try {
                        serverStateInitialize();
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                    primaryStage.setWidth(primaryStage.getWidth() + mapSize * gridSize);
                    primaryStage.setHeight(mapSize * gridSize + 40);

                    intializeAndDrawMap();
                }
                if (client.isSelected()) {
                    clientStateInitialize();
                    primaryStage.setWidth(primaryStage.getWidth() + mapSize * gridSize);
                    primaryStage.setHeight(mapSize * gridSize + 40);
                    intializeAndDrawMap();
                }

            }
        });
        gameOnButtonTest.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                for (Integer key : gameServer.playerList.keySet()) {
                    try {
                        //System.out.print(gameServer.GlobalData.toString());
                        gameServer.playerList.get(key).gameOn();
                        if (key == 2) {
                            gameServer.playerList.get(2).promoteToBackupServer(gameServer.playerList);
                            gameServer.HaveBackUpServerFlag = true;
                            BackUpServerStub = (Server) gameServer.playerList.get(2);
                            BackUpServerStub.backUp(gameServer.GlobalMap);
                            for (Integer key2 : gameServer.playerList.keySet()) {
                                gameServer.playerList.get(key2).setBackUpServer((Server) gameServer.playerList.get(2));
                            }
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    private void  serverStateInitialize() throws RemoteException {
        mapSize = Integer.parseInt(textField1.getText());
        treasureN = Integer.parseInt(textField2.getText());
        map = new Map(mapSize, treasureN);
        map.generateMap();
        try {
            gameServer = new GameNode(mapSize, true);
            map.setGameServer(gameServer);
            gameServer.initializeServer(map.map, map.data);
            gameServer.setMap(map);
            gameServer.serverFlag=true;
            gameServer.setGameOnDrawCallBack(callback);
            gameServer.setMainServerStub(gameServer);
            //ServerStub=(Server) UnicastRemoteObject.exportObject(gameServer, 0);
            registry = LocateRegistry.createRegistry(1099);
            registry = LocateRegistry.getRegistry(null, 1099);
            Player tmp = gameServer;
            int id = gameServer.regestryToServer(tmp);
            map.playerId = id;
            registry.bind("Server", gameServer);
            System.err.println("Server ready");
            setServerFlagLabel();
        } catch (Exception e) {
            try {
                registry.unbind("Server");
                registry.bind("Server", gameServer);
                System.err.println("Server ready");
            } catch (Exception ee) {
                System.err.println("Server exception: " + ee.toString());
                ee.printStackTrace();
            }
        }
    }
    private void setServerFlagLabel(){
        if(gameServer.backUpServerFlag==true){
            serverflag.setText("Back Up Server");
        }
        else if(gameServer.serverFlag==true){
            serverflag.setText("Main Server");
        }
        else{
            serverflag.setText("Player");
        }
    }


    private void clientStateInitialize(){
        try {
            Registry registry = LocateRegistry.getRegistry(null, 1099);
            ServerStub = (Server) registry.lookup("Server");

            gameServer=new GameNode(false,false);
            setServerFlagLabel();
            gameServer.setMainServerStub(ServerStub);
            gameServer.setGameOnDrawCallBack(callback);
            char[][] mapR=ServerStub.GetMapFromSever();
            Integer id=ServerStub.regestryToServer(gameServer);
            map=new Map(mapR.length);
            map.setGameServer(ServerStub);
            gameServer.setMap(map);
            map.map=mapR;
            mapSize=mapR[0].length;
            map.playerId=id;


        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }

    }
    private void drawThePlayers() {
        if(Players!=null) {
            root.getChildren().remove(Players);
        }
        Players = new Canvas(mapSize*gridSize, mapSize*gridSize);
        GraphicsContext gcP = Players.getGraphicsContext2D();
        gcP.setFill(Color.BLUE);
        for(int i=0;i<mapSize;i++){
            for(int j=0;j<mapSize;j++){
                if(map.map[i][j]>10){
                    gcP.fillOval(i * gridSize + gridSize / 8, j * gridSize + gridSize / 8, gridSize*3/4, gridSize*3/4);
                    if(map.map[i][j]==(char)(map.playerId+10)){
                        map.data.players.put(new Integer(map.playerId), map.data.new Coord(i,j));
                        gcP.setFill(Color.RED);
                        gcP.fillOval(i * gridSize, j * gridSize, gridSize, gridSize);
                        gcP.setFill(Color.BLUE);
                    }
                }
            }
        }
        GridPane.setConstraints(Players, 0, 0);
        root.getChildren().add(Players);

    }
    private void drawTheTreasure(){
        if(Treasures!=null){
            root.getChildren().remove(Treasures);
        }
        Treasures = new Canvas(mapSize*gridSize, mapSize*gridSize);
        GraphicsContext gcT = Treasures.getGraphicsContext2D();
        drawTreasure(gcT);
        GridPane.setConstraints(Treasures, 0, 0);
        root.getChildren().add(Treasures);
    }

    private void intializeAndDrawMap() {
        MapLandScape = new Canvas(mapSize*gridSize, mapSize*gridSize);
        GraphicsContext gc = MapLandScape.getGraphicsContext2D();
        drawMap(gc);
        GridPane.setConstraints(MapLandScape, 0, 0);
        root.getChildren().add(MapLandScape);
    }

    private void drawSideBar() {
        //Draw the sideBars
        sideBar = new GridPane();
        sideBar.setHgap(5);
        sideBar.setVgap(5);


        server=new RadioButton("Server");
        server.setToggleGroup(group);
        server.setSelected(true);
        GridPane.setConstraints(server, 0, 0);
        sideBar.getChildren().add(server);

        client=new RadioButton("Client");
        GridPane.setConstraints(client,0,1);
        sideBar.getChildren().add(client);
        client.setToggleGroup(group);

        label1 = new Label("Map Size:");
        textField1 = new TextField ("20");
        GridPane.setConstraints(label1, 0, 4);
        sideBar.getChildren().add(label1);
        GridPane.setConstraints(textField1, 1, 4);
        sideBar.getChildren().add(textField1);

        label2 = new Label("Treasure Amount:");
        textField2 = new TextField ("8");
        GridPane.setConstraints(label2, 0, 7);
        sideBar.getChildren().add(label2);
        GridPane.setConstraints(textField2, 1, 7);
        sideBar.getChildren().add(textField2);


        startButton = new Button("Start");
        GridPane.setConstraints(startButton, 0, 10);
        sideBar.getChildren().add(startButton);

        gameOnButtonTest = new Button("GameOn");
        GridPane.setConstraints(gameOnButtonTest, 1, 10);
        sideBar.getChildren().add(gameOnButtonTest);

        labelCount = new Label("Point Count");
        labelCount2= new Label("0");
        GridPane.setConstraints(labelCount, 0, 14);
        sideBar.getChildren().add(labelCount);
        GridPane.setConstraints(labelCount2, 1, 14);
        sideBar.getChildren().add(labelCount2);

        serverflag= new Label("");
        GridPane.setConstraints(serverflag, 0, 16);
        sideBar.getChildren().add(serverflag);

        GridPane.setConstraints(sideBar, 1, 0);
        root.getChildren().add(sideBar);
    }

    private void drawMap(GraphicsContext gc) {
        gc.setFill(Color.GREEN);
        for (int i = 0; i < mapSize; i++) {
            for (int j = 0; j < mapSize; j++) {
                if (map.map[i][j] == 1) {
                    gc.fillRoundRect(i * gridSize, j * gridSize, 30, 30, 10, 10);
                }
            }
        }
    }
    private void drawTreasure(GraphicsContext gc){
        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(8);
        for(int i=0;i< mapSize;i++){
            for(int  j=0;j<mapSize;j++){
                if(map.map[i][j]==2){
                    gc.strokeOval(i*gridSize+gridSize/4, j*gridSize+gridSize/4, gridSize/2, gridSize/2);
                }
            }
        }
    }


    public static void main(String[] args) {
        launch(args);
    }


}
