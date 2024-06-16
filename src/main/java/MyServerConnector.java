import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MyServerConnector extends Thread {
    public MyServerConnector(PrintWriter writer) {
        this.writer = writer;
    }
    private PrintWriter writer;
    @Override
    public void run() {
        try (Socket myServer = new Socket(Client.MY_SERVER_IP, Client.MY_SERVER_PORT);
             BufferedReader myServerReader = new BufferedReader(new InputStreamReader(myServer.getInputStream()));) {
            String input;
            while ((input = myServerReader.readLine()) != null) {
                input = input.substring(1, input.length() - 1);
                String[] pairs = input.split(", ");
                StringBuilder formattedString = new StringBuilder();
                for (int i = 0; i < pairs.length; i++) {
                    formattedString.append(pairs[i]);
                    if (i < pairs.length - 1) {
                        formattedString.append(",");
                    }
                }
                writer.println(formattedString);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
