import javafx.scene.control.ListView;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Server {
    int port;
    Consumer<Serializable> callback;
    ServerThread serverThread;
    HashMap<ClientThread, Integer> playerRankings;

    public Server(Consumer<Serializable> callback, int port){
        this.port = port;
        this.callback = callback;
        playerRankings = new HashMap<>();
        serverThread = new ServerThread();
        serverThread.start();
    }
    public class ServerThread extends Thread{
        @Override
        public void run(){
            try (ServerSocket serverSocket = new ServerSocket(port);){
                callback.accept("Server is running on port: " + port);
                while(true){
                    ClientThread clientThread = new ClientThread(serverSocket.accept());
                    synchronized (playerRankings){
                        playerRankings.put(clientThread, 0);
                    }
                    clientThread.start();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public class ClientThread extends Thread{
        public String username;
        public String gameDifficulty;
        private Socket socket;
        GameState gameState = new GameState();
        ObjectInputStream in;
        ObjectOutputStream out;
        public ClientThread(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run(){
            try{
                in = new ObjectInputStream(socket.getInputStream());
                out = new ObjectOutputStream(socket.getOutputStream());
                socket.setTcpNoDelay(true);
                username = in.readObject().toString();

                callback.accept("Client '" + username + "' has connected");
            }catch (Exception e){
                e.printStackTrace();
            }

            try{
                while (true){
                    gameDifficulty = in.readObject().toString(); //Game difficulty selection
                    GameLogic gameLogic = new GameLogic(gameDifficulty);
                    callback.accept(username + " is playing a new game on " + gameDifficulty + " difficulty");

                    synchronized (playerRankings){
                        findTopThreePlayers(gameState);
                    }

                    out.reset();
                    out.writeObject(gameState);

                    while(!gameLogic.isGameOver()){ //While game active
                        gameLogic.updateClientGameBoard(in.readObject().toString()); //Read in gameBoard

                        gameState.gameBoard = gameLogic.createServerMove();
                        gameState.gameOver = gameLogic.isGameOver();
                        gameState.clientWon = gameLogic.clientWon;
                        gameState.draw = gameLogic.draw;

                        out.reset();
                        out.writeObject(gameState);
                    }


                    //Create new gameState for current player and update all of the gameStates with the new rankings
                    synchronized (playerRankings){
                        gameState = new GameState();

                        int score = playerRankings.get(this);
                        playerRankings.put(this, score + gameLogic.pointsWon());

                        if (gameLogic.clientWon){
                            callback.accept(username + " has won their game and has " + playerRankings.get(this) + " points!");
                        }
                        else if (gameLogic.draw){
                            callback.accept(username + " has drawn their game with the server");
                        }
                        else{
                            callback.accept(username + " has lost their game to the server!");
                        }

                        playerRankings.forEach((clientThread, integer) -> {
                            clientThread.findTopThreePlayers(clientThread.gameState);
                        });
                    }
                }

            }catch (Exception e){
                playerRankings.remove(this);
                callback.accept(username + " has disconnected");
            }
        }

        private void findTopThreePlayers(GameState gameState){
            LinkedHashMap<ClientThread, Integer> sortedMap = playerRankings.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (oldValue, newValue) -> oldValue, LinkedHashMap::new));

            Set keySet = sortedMap.keySet();

            gameState.topScoreNames.clear();
            gameState.topScorePoints.clear();

            int i = 0;
            for (Object key : keySet){
                if (i < 3 && sortedMap.get(key) != 0){
                    gameState.topScoreNames.add(((ClientThread) key ).username);
                    gameState.topScorePoints.add(String.valueOf(sortedMap.get(key)));
                    i++;
                }
            }
        }
    }
}
