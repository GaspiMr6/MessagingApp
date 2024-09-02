import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

public class ServerThread extends Thread {
    
    public Socket clientSocket;
    public Server server;

    ServerThread(Server serverReceived, Socket clientSocketReceived){
        server = serverReceived;
        clientSocket = clientSocketReceived;
    }

    public void run(){

        // Read the Room Name from client
        String clientDataRoomName = SocketUtilities.ReadSocketData(clientSocket);
        System.out.println("I've received " + clientDataRoomName + " roomname from the client " + clientSocket.getRemoteSocketAddress());
        
        // Create or update the room
        ManageRooms(clientDataRoomName);
        
        // Send a response to the client with the current State of the Room.
        SocketUtilities.WriteSocketData(clientSocket, GetRoomState(clientDataRoomName));


        // Close Socket
		try {
            System.out.println("I close the socket");
            clientSocket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage() + " --- " + e.getStackTrace());
        }
    }

    private void ManageRooms(String clientDataRoomName){

        if(server.Rooms.containsKey(clientDataRoomName)){
            server.Rooms.get(clientDataRoomName).add( new ClientData(clientSocket.getRemoteSocketAddress(), clientSocket));
        }
        else {
            ClientData clientData = new ClientData(clientSocket.getRemoteSocketAddress(), clientSocket);
            server.Rooms.put(clientDataRoomName, new ArrayList<ClientData>(){{ add(clientData); }});
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
                state += " " + client.ClientID;
            }
        }
        else {
            System.out.println("Room Not found! We could not get the state of the Room " + roomID);
        }
        return state;
    }
}
