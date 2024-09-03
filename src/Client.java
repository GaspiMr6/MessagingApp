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
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));) {
            
            System.out.println("Welcome to the MessaginApp!");

            // Read input for Nickname
            System.out.println("Which is your nickname?");
            String nickname = br.readLine();
            // Send Nickname to server
            SocketUtilities.WriteSocketData(clientSocket, nickname);

            // Read input for Room Name
            System.out.print("Specify the room name you want to join/create to talk with others: ");
            String roomName = br.readLine();
            // Send RoomName to server
            SocketUtilities.WriteSocketData(clientSocket, roomName);
            
            // Wait for a response from the server
            String response = SocketUtilities.ReadSocketData(clientSocket);

            // Listen to the incoming room messages (starting a new thread)
            System.out.println("You have joined to " + response);
            Thread incomingRoomMessagesThread = new ClientThread(clientSocket);
            incomingRoomMessagesThread.start();

            System.out.println("Say whatever to the others. Type \"END\" to exit");
            String input;
            do {
                input = br.readLine();
                SocketUtilities.WriteSocketData(clientSocket, nickname + ":" + input);
            } while (!input.equals("END"));

        } catch (Exception e) {
            System.out.println(
                    "Exception caught: " + e.getMessage() + "\nStackTrace: " + e.getStackTrace() + " " + e.getCause());
        }
        System.out.println("I exit");

    }

}
