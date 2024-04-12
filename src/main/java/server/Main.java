package server;



import socket.object.Game;
import socket.object.GameResult;
import socket.object.Player;
import socket.object.Message;
import socket.object.MessageType;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

public class Main {
    private static final int PORT = 12345;
    static int MAX = 2;

    static int GOAL_SCORE = 10;

    static LinkedList<SocketThread> threads = new LinkedList<>();



    public static void main(String[] args) {
        while(true) {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println("Waiting for players...");
                threads.clear();
                try {

                    //Connect and wait until enough players
                    List<Player> playerList = new LinkedList<>();
                    handleConnectPlayers(serverSocket, playerList);
                    serverSocket.close();

                    //Send message to players to start game
                    Game game = new Game( GOAL_SCORE, playerList);
                    sendStartGameMessage(game);

                    //Use to store players' penalty
                    Map<Player, Integer> penalty = initPenaltyMap(playerList);

                    while(true) {
                        //generate new question
                        QuestionGenerator.generateQuestion();

                        // Create new threads for each iteration
                        List<SocketThread> newThreads = createNewThreads();

                        //start threads and wait for them to join
                        startAndWaitForThread(newThreads);

                        //update game after a turn
                        game.updateGame(newThreads, penalty);

                        //send game data after update and check if all user disconnect
                        int numberOfDisconnectedPlayers = sendGameData(game);

                        //check if all user lose their game
                        boolean isAllPlayersLosesTurn = checkIfAllPlayersLose(playerList);

                        //check end game condition, then send game result, close the connections
                        if(isAllPlayersLosesTurn || numberOfDisconnectedPlayers == threads.size() || game.getVictoryPlayer() != null) {
                            sendGameResult(playerList);
                            closeConnections();
                            break;
                        }
                    }
                }
                catch (ClassNotFoundException e) {
                    System.out.println("User disconnected");
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void startAndWaitForThread(List<SocketThread> newThreads) {
        // Start the new threads
        for(SocketThread thread : newThreads) {
            thread.start();
        }

        // Wait for the new threads to finish
        for(SocketThread thread : newThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.err.println("Thread interrupted while waiting for completion: " + e.getMessage());
            }
        }
    }

    private static List<SocketThread> createNewThreads() {
        List<SocketThread> newThreads = new LinkedList<>();
        for(SocketThread thread : threads) {
            newThreads.add(new SocketThread(thread.out, thread.in, thread.socket, thread.player)); // Assuming SocketThread constructor requires a Socket and a game instance
        }
        return newThreads;
    }

    public static int sendGameData(Game game) throws IOException {
        int numberOfDisconnectedPlayers = 0;
        for(SocketThread thread : threads) {
            try {
                thread.out.writeObject(new Game(game));
            }
            catch(SocketException e) {
                numberOfDisconnectedPlayers++;
                System.out.println("User disconnect");
            }
        }
        return numberOfDisconnectedPlayers;
    }

    public static boolean checkIfAllPlayersLose(List<Player> playerList) {
        for(Player player : playerList) {
            if(!player.isLosesTurn()) {
                return false;
            }
        }
        return true;
    }

    private static void closeConnections() {
        for(SocketThread thread : threads) {
            try {
                thread.socket.close();
            } catch (IOException e) {
                System.out.println("Cannot close connection!");
            }
        }
    }

    private static void sendGameResult(List<Player> playerList) {
        GameResult result = new GameResult(playerList);
        for(SocketThread thread : threads) {
            try {
                thread.out.writeObject(result);
            }
            catch(IOException e) {
                System.out.println("User disconnect");
            }
        }
    }

    public static Map<Player, Integer> initPenaltyMap(List<Player> playerList) {
        Map<Player, Integer> map = new HashMap<>();
        for(Player player : playerList) {
            map.put(player, 0);
        }
        return map;
    }

    public static void handleConnectPlayers(ServerSocket serverSocket, List<Player> playerList) throws IOException, ClassNotFoundException {
        Set<String> nameSet = new HashSet<>();
        while (threads.size() < MAX) {
            //connect to user
            Socket playerSocket = serverSocket.accept();
            ObjectOutputStream out = new ObjectOutputStream(playerSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(playerSocket.getInputStream());

            //get user information
            Player player = (Player) in.readObject();

            //check if name is in used
            if(nameSet.contains(player.getName())) {
                out.writeObject(new Message("Your game name is already taken!", MessageType.GAME_NAME_TAKEN));
                in.close();
                out.close();
                playerSocket.close();
                continue;
            }
            nameSet.add(player.getName());

            //if ok then add user to player list
            playerList.add(player);
            threads.add(new SocketThread(out, in, playerSocket, player));

            //send message inform how many players joined
            for(int i = 0; i < threads.size() ; i++) {
                if(!threads.get(i).socket.isClosed()) {
                    threads.get(i).out.writeObject(new Message(threads.size()  + "/" + MAX + " players joined...", MessageType.WAIT_FOR_NEW_GAME));
                }
            }
        }
    }

    public static void sendStartGameMessage(Game game) {
        for(SocketThread thread : threads) {
            try {
                thread.out.writeObject(new Message("game start", MessageType.START_GAME));
                thread.out.writeObject(game);
            }
            catch(IOException e) {
                System.out.println("User disconnect");
            }
        }
    }
}
