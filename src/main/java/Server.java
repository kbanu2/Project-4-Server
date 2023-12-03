import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Server {
    int port;
    ServerThread serverThread;

    public Server(int port){
        this.port = port;
        serverThread = new ServerThread();
        serverThread.start();
    }
    public class ServerThread extends Thread{
        @Override
        public void run(){
            try (ServerSocket serverSocket = new ServerSocket(port);){
                while(true){
                    ClientThread clientThread = new ClientThread(serverSocket.accept());
                    clientThread.start();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public class ClientThread extends Thread{
        public String username;
        private Socket socket;
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
            }catch (Exception e){
                e.printStackTrace();
            }

            try{
                while(true){
                    String gameBoard = in.readObject().toString();
                    out.writeObject(createExpertMove(gameBoard));
                }
            }catch (Exception e){
                System.out.println("Client has disconnected");
                e.printStackTrace();
            }

        }

        public String createExpertMove(String gameBoard){
            AI_MinMax aiMinMax = new AI_MinMax(gameBoard);

            aiMinMax.movesList.forEach(node -> {
                if (node.getMinMax() == 10){
                    aiMinMax.init_board[node.getMovedTo() - 1] = "X";
                }
            });

            return Arrays.toString(aiMinMax.init_board);
        }
    }
}
