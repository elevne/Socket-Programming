import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int SERVER_PORT = 40000;
    private static final int THREAD_POOL_SIZE = 10;
    public static List<String> INFO_LIST = new Vector<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            try (Socket myClient = serverSocket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(myClient.getInputStream()));
                PrintWriter writer = new PrintWriter(myClient.getOutputStream())) {
                INFO_LIST = new Vector<>();
                System.out.println("Server is running...");
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    new TokenHandler(clientSocket, writer).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class TokenHandler extends Thread {
    private Socket clientSocket;
    private PrintWriter writer;
    public TokenHandler(Socket clientSocket, PrintWriter writer) {
        this.clientSocket = clientSocket;
        this.writer = writer;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {
            String token = reader.readLine();
            System.out.println(token);
            Server.INFO_LIST.add(token);
            clientSocket.close();
            if (Server.INFO_LIST.size() == 5) {
                System.out.println(Server.INFO_LIST);
                writer.println(Server.INFO_LIST.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
