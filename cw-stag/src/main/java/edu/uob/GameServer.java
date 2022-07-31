package edu.uob;

import java.io.*;
import java.lang.*;
import java.lang.Character;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.ArrayList;

/** This class implements the STAG server. */
public final class GameServer {

    private static final char END_OF_TRANSMISSION = 4;
    private static ArrayList<Player> playerList;
    private Player currentPlayer;
    private SetGame gameState;

    public static void main(String[] args) throws IOException {
        File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
        GameServer server = new GameServer(entitiesFile, actionsFile);
        server.blockingListenOn(8888);
    }

    /**
    * KEEP this signature (i.e. {@code edu.uob.GameServer(File, File)}) otherwise we won't be able to mark
    * your submission correctly.
    *
    * <p>You MUST use the supplied {@code entitiesFile} and {@code actionsFile}
    *
    * @param entitiesFile The game configuration file containing all game entities to use in your game
    * @param actionsFile The game configuration file containing all game actions to use in your game
    *
    */
    public GameServer(File entitiesFile, File actionsFile) {
        // TODO implement your server logic here
        playerList = new ArrayList<>();
        try {
            gameState = new SetGame(entitiesFile, actionsFile);
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }

    /**
    * KEEP this signature (i.e. {@code edu.uob.GameServer.handleCommand(String)}) otherwise we won't be
    * able to mark your submission correctly.
    *
    * <p>This method handles all incoming game commands and carries out the corresponding actions.
    */
    public String handleCommand(String command) {
        // TODO implement your server logic here
        String[] commands = command.strip().split(":");
        CmdParser parser;
        try {
            checkPlayerName(commands[0]);
            verifyPlayer(commands[0]);
            parser = new CmdParser(command, gameState, currentPlayer);
        }catch (Exception exception){
            return exception.getMessage();
        }
        if(parser.getFinalResult() != null){
            return parser.getFinalResult();
        }else{
            return "Please check your command.\n";
        }
    }

    public void verifyPlayer(String name){
        boolean playerExist = false;
        for (Player player : playerList) {
            if (player.getName().equals(name)) {
                currentPlayer = player;
                playerExist = true;
            }
        }
        if(!playerExist){
            Player player = new Player(name,"player : " + name + " in the game\n");
            player.setGameState(gameState);
            player.setFirstLocation();
            playerList.add(player);
            currentPlayer = player;
            //add player to the first location's playerList.
            Location firstLocation = gameState.getGameMap().get(0);
            firstLocation.addPlayer(player);
        }
    }

    public void checkPlayerName(String name){
        for(int i = 0; i < name.length(); i++){
            if(!Character.isLetter(name.charAt(i))){
                if (name.charAt(i) != ' ' && name.charAt(i) != '\'' && name.charAt(i) != '-') {
                    throw new RuntimeException("Player's name should only contain uppercase and lowercase letters, spaces, apostrophes and hyphens.");
                }
            }
        }
    }

    //  === Methods below are there to facilitate server related operations. ===

    /**
    * Starts a *blocking* socket server listening for new connections. This method blocks until the
    * current thread is interrupted.
    *
    * <p>This method isn't used for marking. You shouldn't have to modify this method, but you can if
    * you want to.
    *
    * @param portNumber The port to listen on.
    * @throws IOException If any IO related operation fails.
    */
    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.out.println("Connection closed");
                }
            }
        }
    }

    /**
    * Handles an incoming connection from the socket server.
    *
    * <p>This method isn't used for marking. You shouldn't have to modify this method, but you can if
    * * you want to.
    *
    * @param serverSocket The client socket to read/write from.
    * @throws IOException If any IO related operation fails.
    */
    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
            System.out.println("Connection established");
            String incomingCommand = reader.readLine();
            if(incomingCommand != null) {
                System.out.println("Received message from " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();

            }
        }
    }
}
