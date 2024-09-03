import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

public class ServerThread extends Thread {
    
    public Socket clientSocket;
    public Server server;
    public ClientData clientData;

    ServerThread(Server serverReceived, Socket clientSocketReceived){
        server = serverReceived;
        clientSocket = clientSocketReceived;
    }

    public void run(){

        // Read the Nickname from client
        String clientNickname = SocketUtilities.ReadSocketData(clientSocket);
        // Read the Room Name from client
        String clientDataRoomName = SocketUtilities.ReadSocketData(clientSocket);
        // New client data
        ClientData myClientData = new ClientData(clientSocket.getRemoteSocketAddress(), clientSocket, clientNickname);
        System.out.println("New client processed: " + clientNickname + " in room "+ clientDataRoomName + " with IP: " + clientSocket.getRemoteSocketAddress());

        // Create or update the room
        ManageRooms(clientDataRoomName, myClientData);
        
        // Send a response to the client with the current State of the Room.
        SocketUtilities.WriteSocketData(clientSocket, GetRoomState(clientDataRoomName));

        // Chat starts
        String clientDataNewMessage;
        do {
            // Get new message from the client
            clientDataNewMessage = SocketUtilities.ReadSocketData(clientSocket);

            // Send it to all other clients in the same room
            ArrayList<ClientData> clientsInTheSameRoom = server.Rooms.get(clientDataRoomName);
            for (ClientData clientData : clientsInTheSameRoom) {
                if(clientData.ClientSocket == clientSocket) continue;
                SocketUtilities.WriteSocketData(clientData.ClientSocket, clientData.ClientNickname + ":" + clientDataNewMessage);
            }

        } while (!clientDataNewMessage.equals("END"));
        

        // Close Socket
		try {
            System.out.println("I close the socket");
            clientSocket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage() + " --- " + e.getStackTrace());
        }
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
}
