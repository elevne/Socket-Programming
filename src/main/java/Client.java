import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
    public static final String SERVER_DOMAIN_NAME = "ec2-52-79-217-87.ap-northeast-2.compute.amazonaws.com";
    public static final int SERVER_PORT = 50000;
    public static final String MY_SERVER_IP = "13.124.209.6";
    public static final int MY_SERVER_PORT = 40000;

    public static void main(String[] args) {
        String SERVER_IP = DNSResolver.requestDNS(SERVER_DOMAIN_NAME);
        try (
                Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));) {
            new MyServerConnector(writer).start();
            String input, userInput;
            try {
                while ((input = reader.readLine()) != null) {
                    System.out.println(input);
                    if (input.contains("-->")) {
                        userInput = stdIn.readLine();
                        writer.println(userInput);
                    }
                    else if (input.contains("Result\t\t: Concurrent")) {
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

}

