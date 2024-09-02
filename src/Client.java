import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Error: You must specify the host address and the port");
            System.exit(-1);
        }
        String host = args[0];
        String port = args[1];

        try (Socket clientSocket = new Socket(host, Integer.parseInt(port));
             BufferedReader br = new BufferedReader(new InputStreamReader(System.in));){

            // Read input for Room Name
            System.out.print("Specify the room name you want to join/create to talk with others: ");
            String input = br.readLine();
            // Send RoomName to server
            SocketUtilities.WriteSocketData(clientSocket, input);
            // Wait for a response from the server
            String response = SocketUtilities.ReadSocketData(clientSocket);

            System.out.println(response);


        } catch (Exception e) {
            System.out.println("Exception caught: " + e.getMessage() + "\nStackTrace: " + e.getStackTrace() + " " + e.getCause());
        }
        System.out.println("I exit");

    }

}
