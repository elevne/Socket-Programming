import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int SERVER_PORT = 40000; // Ensure this matches the port number used in the client
    private static final int THREAD_POOL_SIZE = 5; // Number of concurrent clients

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Server is running...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("asdf");
                executor.execute(new TokenHandler(clientSocket));
            }
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
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {
            String token = reader.readLine();
            // Forward token to the client
            forwardTokenToClient(token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void forwardTokenToClient(String token) {
        String clientIP = "127.0.0.1"; // Assuming client and server are on the same machine, adjust if necessary
        int clientPort = 60001; // Adjust if necessary
        try (Socket socket = new Socket(clientIP, clientPort);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
            writer.println(token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
