package sample;

import javafx.application.Application;
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
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.ColumnConstraintsBuilder;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class Main extends Application {
    final static int gridSize=30;
    int mapSize;
    int treasureN;
    Map map;
    GridPane root;
    Canvas Treasures;
    Canvas Players;
    Canvas MapLandScape;
    GridPane sideBar;
    final ToggleGroup group = new ToggleGroup();
    RadioButton server;
    RadioButton client;
    Label label1,label2;
    TextField textField1, textField2;
    Button startButton;
    GameServer gameServer;
    GamePlayer gameplayer;
    Server SeverStub=null;
    Registry registry = null;

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Distributed System Assignment 1");
        //Group root = new Group();
        root = new GridPane();
        root.setPadding(new Insets(10, 10, 10, 10));
        root.setVgap(5);
        root.setHgap(5);
        //Draw the Map

        //intializeAndDrawMap();
        //Draw the Treasures


        //Draw the Players
        //drawThePlayers();
        drawSideBar();
        startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                //if server state
                if(server.isSelected()) {
                    serverStateInitialize();
                    primaryStage.setWidth(primaryStage.getWidth() + mapSize * gridSize);
                    primaryStage.setHeight(mapSize * gridSize + 40);
                    gameServer.initializeServer(map.map, map.data);
                    intializeAndDrawMap();
                }
                if(client.isSelected()){
                    clientStateInitialize();
                    primaryStage.setWidth(primaryStage.getWidth() + mapSize * gridSize);
                    primaryStage.setHeight(mapSize * gridSize + 40);
                    intializeAndDrawMap();
                }
                //if client state
            }});

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    private void  serverStateInitialize(){
        mapSize=Integer.parseInt(textField1.getText());
        treasureN=Integer.parseInt(textField2.getText());
        map=new Map(mapSize,treasureN);
        map.generateMap();
        try {
            gameServer=new GameServer(mapSize);
            SeverStub=(Server) UnicastRemoteObject.exportObject(gameServer, 0);
            registry= LocateRegistry.createRegistry(1099);
            registry = LocateRegistry.getRegistry(null,1099);
            registry.bind("Server", SeverStub);
            System.err.println("Server ready");
        } catch (Exception e) {
            try{
                registry.unbind("Server");
                registry.bind("Server",SeverStub);
                System.err.println("Server ready");
            }catch(Exception ee){
                System.err.println("Server exception: " + ee.toString());
                ee.printStackTrace();
            }
        }


    }
    private void clientStateInitialize(){
        try {
            Registry registry = LocateRegistry.getRegistry(null, 1099);
            SeverStub = (Server) registry.lookup("Server");
            char[][] tm=SeverStub.GetMapFromSever();
            map=new Map(tm.length);
            map.map=tm;
            mapSize=tm.length;

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }

    }
    private void drawThePlayers() {
        Players = new Canvas(mapSize*gridSize, mapSize*gridSize);
        GraphicsContext gcP = Players.getGraphicsContext2D();
        drawOtherPlayers(gcP);
        GridPane.setConstraints(Players, 0, 0);
        root.getChildren().add(Players);

    }
    private void drawTheTreasure(){
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


        GridPane.setConstraints(sideBar, 1, 0);
        root.getChildren().add(sideBar);
    }

    private void drawMap(GraphicsContext gc) {
        gc.setFill(Color.GREEN);
        for (int i = 0; i < mapSize; i++) {
            for (int j = 0; j < mapSize; j++) {
                if (map.map[i][j] == 1) {
                    gc.fillRoundRect(i * gridSize, j * gridSize, 30, 30, 10, 10);
                    //gc.clearRect();
                }
            }
        }
    }
    private void drawTreasure(GraphicsContext gc){
        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(8);
        for(Integer key:map.data.treasures.keySet()){
            gc.strokeOval(map.data.treasures.get(key).x*gridSize+gridSize/4, map.data.treasures.get(key).y*gridSize+gridSize/4, gridSize/2, gridSize/2);
        }

    }
    private void drawOtherPlayers(GraphicsContext gc){


    }


    public static void main(String[] args) {
        launch(args);
    }


}
