import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {

    private static String host;
    private static String port;

    public static void main(String[] args) {

        CheckArguments(args);
        
        try (Socket clientSocket = new Socket(host, Integer.parseInt(port));
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));) {     
            System.out.println("Welcome to the MessaginApp!");
            AddShutdownHook(clientSocket);
            String nickname = AskForNicknameInput(br, clientSocket);  // Send NICKNAME to server
            AskForReadRoomNameInput(br, clientSocket);  // Send ROOMNAME to server
            ReceiveRoomStatus(clientSocket); // Wait for a response from the server and print it
            Thread incomingRoomMessagesThread = ListenToIncomingMessages(clientSocket); // Starts a new thread 
            StartMainLoopInput(incomingRoomMessagesThread, br, clientSocket, nickname); // Ask for input messages

        } catch (Exception e) {
            System.out.println(
                    "Exception caught: " + e.getMessage() + "\nStackTrace: " + e.getStackTrace() + " " + e.getCause());
        }
        System.out.println("I exit");
    }

    private static void CheckArguments(String[] args){
        if (args.length < 2) {
            System.out.println("Error: You must specify the host address and the port");
            System.exit(-1);
        }
        host = args[0];
        port = args[1];
    }

    private static String AskForNicknameInput(BufferedReader br, Socket clientSocket) throws IOException{
        // Read input for Nickname
        System.out.println("Which is your nickname?");
        String nickname = br.readLine();
        // Send Nickname to server
        SocketUtilities.WriteSocketData(clientSocket, nickname);
        return nickname;
    }

    private static void AskForReadRoomNameInput(BufferedReader br, Socket clientSocket) throws IOException{
        // Read input for Room Name
        System.out.print("Specify the room name you want to join/create to talk with others: ");
        String roomName = br.readLine();
        // Send RoomName to server
        SocketUtilities.WriteSocketData(clientSocket, roomName);
    }

    private static void ReceiveRoomStatus(Socket clientSocket){
        
        String response = SocketUtilities.ReadSocketData(clientSocket);
        if(response.equals("SERVER_SHUTDOWN")) {
            System.out.println("The server has been shutdown");
            System.exit(0);
        }
        System.out.println("You have joined to " + response);
    }

    private static Thread ListenToIncomingMessages(Socket clientSocket){
        // Listen to the incoming room messages (starting a new thread)
        
        Thread incomingRoomMessagesThread = new ClientThread(clientSocket);
        incomingRoomMessagesThread.start();
        return incomingRoomMessagesThread;
    }

    private static void StartMainLoopInput(Thread incomingRoomMessagesThread, BufferedReader br, Socket clientSocket, String nickname) throws IOException{
        System.out.println("Say whatever to the others. Type \"END\" to exit");
        String input;
        do {
            input = br.readLine();
            SocketUtilities.WriteSocketData(clientSocket, nickname + ":" + input);
        } while (!input.equals("END"));
        
        incomingRoomMessagesThread.interrupt();
    }

    private static void AddShutdownHook(Socket clienSocket){
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Server is shutting down...");
            SocketUtilities.WriteSocketData(clienSocket, "CLIENT_SHUTDOWN");
        }));
	}
}
