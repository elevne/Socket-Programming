import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client {
    private static final String SERVER_DOMAIN_NAME = "ec2-52-79-217-87.ap-northeast-2.compute.amazonaws.com";
    private static final int SERVER_PORT = 50000;
    private static final int LOCAL_SERVER_PORT = 60000; // Replace with your server's port number
    private static final int NUM_TOKENS = 5;

    private static List<String> tokens = new ArrayList<>();

    public static void main(String[] args) {
        // 먼저 내 서버에 연결
        // 그 다음 학교 서버에 연결
        // 그 다음 내 서버에서 정보 받기
        // 해당 정보 받은 것을 학교 서버에 넘겨주기
//        try (Socket myServer = new Socket("112.220.176.180", 55555);
//             BufferedReader reader = new BufferedReader(new InputStreamReader(myServer.getInputStream()))) {
//            System.out.println(reader.readLine());
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.exit(0);
//        }

        String serverIp = DNSResolver.requestDNS(SERVER_DOMAIN_NAME);
        System.out.println(serverIp);
        try (Socket socket = new Socket(serverIp, SERVER_PORT);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));) {
            String input, userInput;
            while ((input = reader.readLine()) != null) {
                System.out.println(input);
                if (input.contains("-->")) {
                    userInput = stdIn.readLine();
                    writer.println(userInput);
                }
            }

            // Receive tokens from local server
            for (int j = 0; j < NUM_TOKENS; j++) {
                String token = receiveTokenFromLocalServer();
                tokens.add(token);
            }

            // Combine tokens and send to designated server
            String combinedTokens = String.join("", tokens);
            writer.println(combinedTokens);

            // Read the result from the server
            String result = reader.readLine();
            System.out.println("Result: " + result);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String receiveTokenFromLocalServer() {
        try (ServerSocket serverSocket = new ServerSocket(LOCAL_SERVER_PORT);
             Socket clientSocket = serverSocket.accept();
             BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
