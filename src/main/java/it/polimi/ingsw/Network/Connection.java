package it.polimi.ingsw.Network;

import it.polimi.ingsw.View.LiteBoard;
import it.polimi.ingsw.utils.Observable;
import it.polimi.ingsw.utils.Observer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Connection extends Observable implements Runnable, Observer {
    private final Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final Server server;
    private String name;
    private boolean active;

    /**
     * Constructor used to create a connection
     * @param socket is the socket created by Server
     * @param server is the Server of game
     */
    public Connection(Socket socket, Server server){
        this.socket = socket;
        this.server = server;
        this.active = true;
    }

    public ObjectInputStream getIn(){
        return this.in;
    }

    /**
     * @return if the connection is still active
     */
    private synchronized boolean isActive(){
        return active;
    }


    /**
     * Method that send the message and the board to each Client from Game.
     * @param board this is the LiteBoard that we send to Client. It can be a message or a message+board+game
     */
    public void send(LiteBoard board){
        try {
            out.reset();
            out.writeObject(board);
            out.flush();
        }  catch (IOException e) {
            System.out.println("Socket closed");
        }
    }

    /**
     * Read the input of client
     * @return the input of Client
     * @throws IOException if the socket is close
     */
    public String readString() throws IOException {
        String read = "";
        while (read.isBlank()) {
            read = (in.readUTF());
        }
        return read;
    }

    /**
     * close the socket and unregister the connection in the server arrays
     */
    public synchronized void close(){
        try {
            System.out.println("Unregistering client: " + name);
            socket.close();
            server.unregisterConnection(this);
            active = false;
            System.out.println("Done!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the ObjectOutputStream and ObjectInputStream. When the connection isActive read the input of each client.
     */
    @Override
    public void run() {

        try{
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            socket.setSoTimeout(360000);

            send( new LiteBoard("Welcome! What's your name?"));
            server.notEqualsName(this);
            server.lobby(this, name);

            while(isActive()){
                if(server.gameHasStarted()){
                    String read = readString();
                    read = name + " " + read;
                    notifyObservers(read);
                }
            }

        } catch (SocketTimeoutException e) {
            server.timeout(name);
            if(isActive()) close();
        } catch(IOException e){
            server.endGame();
            if(isActive()) close();
            System.err.println("Connection with " + name + " not more active");
        }
    }

    /**
     * Receives the board from the game and send it to each Client. Then it checks if there is a winner or a loser.
     * @param board is the board received from the game.
     */
    public void newBoard(LiteBoard board) {
        String[] parts = board.getMessage().split(" ");
        send(board);
        if ((parts[1].equals(name) && parts[2].equals("loses")) ||
             parts[2].equals("wins"))
                close();
    }

    public String getName(){ return name; }

    @Override
    public void update(String message) {
        // Method used only by the controller
    }

    public void setName(String n) { name = n; }
}
