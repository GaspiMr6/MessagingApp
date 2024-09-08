import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {

    private static String host;
    private static String port;
    private static Thread incomingRoomMessagesThread;
    private static Socket clientSocket;
    
    public static void main(String[] args) {

        CheckArguments(args);
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); ) {
            clientSocket = new Socket(host, Integer.parseInt(port));
            //br = new BufferedReader(new InputStreamReader(System.in));       
            System.out.println("Welcome to the MessaginApp!");
            AddShutdownHook(clientSocket);
            String nickname = AskForNicknameInput(br, clientSocket);  // Send NICKNAME to server
            AskForReadRoomNameInput(br, clientSocket);  // Send ROOMNAME to server
            ReceiveRoomStatus(clientSocket); // Wait for a response from the server and print it
            incomingRoomMessagesThread = ListenToIncomingMessages(clientSocket); // Starts a new thread 
            StartMainLoopInput(incomingRoomMessagesThread, br, clientSocket, nickname); // Ask for input messages

        } 
        catch (Exception e) {
            System.out.println( "Exception caught: " + e.getMessage() + "\nStackTrace: " + e.getStackTrace() + " " + e.getCause());
        }
    }

    public static String ProcessMessage(SocketUtilities.Message msg){
        switch (msg.messageHeader) {
            case SERVER_DATA:
                return msg.data;

            case SERVER_DISCONNECTED:
                System.out.println("Server disconnected!");
                incomingRoomMessagesThread.interrupt();
                System.exit(0);
                break;

            default:
                System.out.println("Error processing Message! Header not implemented!");
                break;
        }
        return null;
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
        SocketUtilities.WriteSocketData(clientSocket, new SocketUtilities.Message(SocketUtilities.EMessageHeader.CLIENT_DATA, nickname));

        return nickname;
    }

    private static void AskForReadRoomNameInput(BufferedReader br, Socket clientSocket) throws IOException{
        // Read input for Room Name
        System.out.print("Specify the room name you want to join/create to talk with others: ");
        String roomName = br.readLine();
        // Send RoomName to server
        SocketUtilities.WriteSocketData(clientSocket, new SocketUtilities.Message(SocketUtilities.EMessageHeader.CLIENT_DATA, roomName));
    }

    private static void ReceiveRoomStatus(Socket clientSocket){
        SocketUtilities.Message msgRoomStatus = SocketUtilities.ReadSocketData(clientSocket);
        String roomStatus = ProcessMessage(msgRoomStatus);
        System.out.println("You have joined to " + roomStatus);
    }

    private static Thread ListenToIncomingMessages(Socket clientSocket){
        // Listen to the incoming room messages (starting a new thread)  
        Thread incomingRoomMessagesThread = new ClientThread(clientSocket);
        incomingRoomMessagesThread.start();
        return incomingRoomMessagesThread;
    }

    private static void StartMainLoopInput(Thread incomingRoomMessagesThread, BufferedReader br, Socket clientSocket, String nickname) throws IOException{
        System.out.println("Say whatever to the others. CTRL + C to exit");
        String input;
        while(true){
            input = br.readLine();
            SocketUtilities.Message msg = new SocketUtilities.Message(SocketUtilities.EMessageHeader.CLIENT_DATA, nickname + ":" + input);
            SocketUtilities.WriteSocketData(clientSocket, msg);
        }
    }

    private static void AddShutdownHook(Socket clientSocket){
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Client is shutting down...");
            SocketUtilities.Message msg = new SocketUtilities.Message(SocketUtilities.EMessageHeader.CLIENT_DISCONNECTED, "CLIENT_SHUTDOWN");
            SocketUtilities.WriteSocketData(clientSocket, msg);
            CloseSocketAndBufferedLine();
        }));
	}

    static void CloseSocketAndBufferedLine(){
		try {
            clientSocket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage() + " --- " + e.getStackTrace());
        }
    }
}
