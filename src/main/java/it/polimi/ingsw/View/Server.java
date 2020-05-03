package it.polimi.ingsw.View;

import it.polimi.ingsw.Controller.Controller;
import it.polimi.ingsw.Model.Divinity;
import it.polimi.ingsw.Model.Game;
import it.polimi.ingsw.Model.Player;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static it.polimi.ingsw.Model.Color.*;

public class Server {
    private static final int PORT = 12345;
    private ServerSocket serverSocket;
    private int numPlayers;

    private ExecutorService executor = Executors.newFixedThreadPool(128);

    private List<Connection> connections = new ArrayList<>();
    private Map<String, Connection> waitingConnection = new HashMap<>();
    private Map<Connection, Connection> playingConnection = new HashMap<>();
    private Map<Connection, Connection> playingConnection3players = new HashMap<>();
    private ArrayList<String> divinity = new ArrayList<>();
    private String divinityPlay1;
    private String divinityPlay2;
    private String divinityPlay3;
    private String firstPlayer;
    private String secondPlayer;
    private String thirdPlayer;
    private  boolean startGame = false;


    public Server() throws IOException {
        this.serverSocket = new ServerSocket(PORT);
    }

    //Register connection
    private synchronized void registerConnection(Connection c) {
        connections.add(c);
    }

    public void decideNumberPlayer(Connection c) {
        c.send("Number of players?");
        setNumPlayers(Integer.parseInt(c.getIn().next()));
        c.send("Choose the divinities. First divinity:");
        divinity.add(c.getIn().next());
        c.send("Second divinity: ");
        divinity.add(c.getIn().next());
        if (this.getNumPlayers() == 3) {
            c.send("Third divinity: ");
            divinity.add(c.getIn().next());
        }
        c.send("All divinities have been chosen");
    }

    public void startDivinity(){

        if ((connections.size()== numPlayers)){
            StringBuilder choices = new StringBuilder("Choose your Divinity between: ");
            for (String god : divinity){
                choices.append(god).append(' ');
            }
            getConnection(1).send(choices.toString());
            divinityPlay2 = getConnection(1).getIn().next();
            getConnection(1).send("Your Divinity: "+ divinityPlay2);
            divinity.remove(divinityPlay2);
            if (numPlayers==3) {
                choices = new StringBuilder("Choose your Divinity between: ");
                for (String god : divinity) {
                    choices.append(god).append(' ');
                }
                getConnection(2).send(choices.toString());
                divinityPlay3 = getConnection(2).getIn().next();
                getConnection(2).send("Your Divinity: " + divinityPlay3);
                divinity.remove(divinityPlay3);
            }
            divinityPlay1 = divinity.get(0);
            getConnection(0).send("Your Divinity: " + divinityPlay1);
        }
    }

    //Deregister connection
    public synchronized void deregisterConnection(Connection c){
        if(playingConnection.size() == 3) {
            for (Connection conn : connections) {
                if (c.equals(playingConnection.get(conn))) {
                    playingConnection.remove(conn);
                }
            }
            for (Connection conn : connections) {
                if (c.equals(playingConnection.get(conn))) {
                    playingConnection3players.remove(conn);
                }
            }
            playingConnection.remove(c); //rimuovo la prima connessione dalla prima hash map
            playingConnection3players.remove(c);
            Connection toadd = connections.get(0);
            if (playingConnection.containsKey(toadd)){
                playingConnection.put(connections.get(1),toadd);
            }
            else {
                playingConnection.put(toadd,connections.get(1));
            }
            playingConnection3players.clear();
        }
        if(playingConnection.size() == 2){
            Connection opponent = playingConnection.get(c);
            if (opponent!= null){
                opponent.closeConnection();
                playingConnection.remove(c);
                playingConnection.remove(opponent);
            }
        }
        connections.remove(c); //messo qui perchè altrimenti con 3 giocatori non ho il riferimento alla connessione eliminata in foreach
    }


    public synchronized void lobby(Connection c, String name){
        waitingConnection.put(name, c);
        if (waitingConnection.get(name).equals(connections.get(0))){ //primo giocatore
            firstPlayer = name;
            decideNumberPlayer(waitingConnection.get(name)); //decide num of player
        }

        if((waitingConnection.size()== numPlayers)) {
            startDivinity();
            List<String> keys = new ArrayList<>(waitingConnection.keySet());
            for (String i: keys
                 ) {
                if (waitingConnection.get(i).equals(connections.get(1))) secondPlayer = i;
                if (numPlayers == 3){
                    if (waitingConnection.get(i).equals(connections.get(2))) thirdPlayer = i;
                }
            }

            if (numPlayers == 2){
                keys.clear();
                keys.add(firstPlayer);
                keys.add(secondPlayer);
                }

            if (numPlayers == 3){
                keys.clear();
                keys.add(firstPlayer);
                keys.add(secondPlayer);
                keys.add(thirdPlayer);
            }


            Connection c1 = waitingConnection.get(keys.get(0));
            Connection c2 = waitingConnection.get(keys.get(1));

            //qui Remote View

            Game game = new Game();
            game.setNumPlayer(numPlayers);
            Controller controller = new Controller(game);
            Player player1 = new Player(keys.get(0), Red, game);
            Player player2 = new Player(keys.get(1), Yellow, game);
            player1.setDivinity(Divinity.valueOf(divinityPlay1));
            player2.setDivinity(Divinity.valueOf(divinityPlay2));
            //qui ci vanno gli observer
            game.addObserver(connections.get(0));
            game.addObserver(connections.get(1));
            connections.get(0).addObserver(controller);
            connections.get(1).addObserver(controller);

            playingConnection.put(c1, c2);
            playingConnection.put(c2, c1);

            if (numPlayers == 3) {
                Connection c3 = waitingConnection.get(keys.get(2));
                Player player3 = new Player(keys.get(2), Green, game);
                player3.setDivinity(Divinity.valueOf(divinityPlay3));

                game.addObserver(connections.get(2));
                connections.get(2).addObserver(controller);

                playingConnection.put(c2, c3);
                playingConnection.put(c3, c1);
                playingConnection3players.put(c1, c3);
                playingConnection3players.put(c3, c2);
            }
            waitingConnection.clear();
            connections.get(0).update(this.firstPlayer + " moves");
            startGame = true;
        }
    }

    public void run(){
        System.out.println("Server listening on port: " + PORT);
        //noinspection InfiniteLoopStatement
        while(true){
            try {
                Socket socket = serverSocket.accept();
                Connection connection = new Connection(socket, this);
                registerConnection(connection);
                executor.submit(connection);
            } catch (IOException e){
                System.err.println("Connection error!");
            }
        }
    }

    public boolean gameHasStarted() {
        return startGame;
    }

    public Connection getConnection(int i) {
        return connections.get(i);
    }

    public void setNumPlayers(int i) {
        numPlayers = i;
    }

    public int getNumPlayers() {
        return numPlayers;
    }
}
