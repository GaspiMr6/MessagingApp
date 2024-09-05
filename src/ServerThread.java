import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

public class ServerThread extends Thread {
    
    public Socket clientSocket;
    public Server server;
    public ClientData clientData;

    private String clientDataRoomName;

    ServerThread(Server serverReceived, Socket clientSocketReceived){
        server = serverReceived;
        clientSocket = clientSocketReceived;
    }

    public void run(){

        ClientData clientData = AskForClientData();
        if(clientData != null) {
            ManageRooms(clientDataRoomName, clientData);  // Create or update the room
        
            WriteDataRoomState(clientDataRoomName);  // Send a response to the client with the current State of the Room.

            StartChat(clientDataRoomName);  // Main loop of the thread (it reads data from the client)
        }
        CloseSocket();

    }

    private void StartChat(String clientDataRoomName){
        // Chat starts
        String clientDataNewMessage = "";

        while(true) {

            // Get new message from the client
            clientDataNewMessage = SocketUtilities.ReadSocketData(clientSocket);

            System.out.println("Message received: " + clientDataNewMessage);

            // Check if app must finish
            if (clientDataNewMessage.endsWith("END") || clientDataNewMessage.equals("CLIENT_SHUTDOWN")){
                break;
            }

            // Send it to all other clients in the same room
            ArrayList<ClientData> clientsInTheSameRoom = server.Rooms.get(clientDataRoomName);
            for (ClientData iclientData : clientsInTheSameRoom) {
                if(iclientData.ClientSocket == clientSocket) continue;
                SocketUtilities.WriteSocketData(iclientData.ClientSocket, clientDataNewMessage);
            }
        }
    }


    private ClientData AskForClientData(){
        // Read the Nickname from client
        String clientNickname = SocketUtilities.ReadSocketData(clientSocket);
        // Read the Room Name from client
        clientDataRoomName = SocketUtilities.ReadSocketData(clientSocket);
        if(clientNickname.equals("CLIENT_SHUTDOWN") || clientDataRoomName.equals("CLIENT_SHUTDOWN")){
            System.out.println("Client disconnected " + clientSocket.getRemoteSocketAddress());
            Thread.currentThread().interrupt();
            return null;
        } 

        // New client data
        ClientData myClientData = new ClientData(clientSocket.getRemoteSocketAddress(), clientSocket, clientNickname);
        System.out.println("New client processed: " + myClientData.ClientNickname + " in room " + clientDataRoomName + " with IP: " + myClientData.ClientID);
        return myClientData;
    }

    private void ManageRooms(String clientRoomName, ClientData clientData){

        if(server.Rooms.containsKey(clientRoomName)){
            server.Rooms.get(clientRoomName).add( clientData);
        }
        else {
            server.Rooms.put(clientRoomName, new ArrayList<ClientData>(){{ add(clientData); }});
        }
        ShowRoomsState();
    }

    private void WriteDataRoomState(String clientDataRoomName){
        SocketUtilities.WriteSocketData(clientSocket, GetRoomState(clientDataRoomName));
    }

    private void ShowRoomsState(){
        for (Map.Entry<String, ArrayList<ClientData>> room : server.Rooms.entrySet()) {
            System.out.println( GetRoomState(room.getKey()) );
        }
    }

    String GetRoomState(String roomID){
        String state = "";
        if(server.Rooms.containsKey(roomID)){
            ArrayList<ClientData> clientsData = server.Rooms.get(roomID);
            state += "Room " + roomID + ": there are " + clientsData.size() + " clients ->";
            for (ClientData client : clientsData) {
                state += " " + client.ClientNickname;
            }
        }
        else {
            System.out.println("Room Not found! We could not get the state of the Room " + roomID);
        }
        return state;
    }

    void CloseSocket(){
		try {
            System.out.println("I close the socket");
            clientSocket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage() + " --- " + e.getStackTrace());
        }
        
    }
}
