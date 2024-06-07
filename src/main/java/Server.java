import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int SERVER_PORT = 40000;
    private static final int THREAD_POOL_SIZE = 10;
    public static final List<String> INFO_LIST = new Vector<>();

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            try (Socket myClient = serverSocket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(myClient.getInputStream()));
                PrintWriter writer = new PrintWriter(myClient.getOutputStream())) {
                System.out.println("Server is running...");
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    executor.execute(new TokenHandler(clientSocket));
                    clientSocket.close();
                    if (INFO_LIST.size() == 5) {
                        writer.println(INFO_LIST.toString());
                        break;
                    }
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

class TokenHandler implements Runnable {
    private Socket clientSocket;
    public TokenHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {
            String token = reader.readLine();
            Server.INFO_LIST.add(token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
