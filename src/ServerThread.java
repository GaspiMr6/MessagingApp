import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;


public class ServerThread extends Thread {

    private Socket clientSocket;
    private Server server;
    private ClientData clientData;
    private String clientDataRoomName;

    ServerThread(Server serverReceived, Socket clientSocketReceived){
        server = serverReceived;
        clientSocket = clientSocketReceived;
    }

    public void run(){

        try {
            AskForClientData();
            if(clientData != null) {
                ManageRooms(clientData);  // Create or update the room
            
                WriteDataRoomState();  // Send a response to the client with the current State of the Room.

                StartChat();  // Main loop of the thread (it reads data from the client)
            }
        } catch (ClientDisconnectsException e){
            System.out.println("Client Disconnects Exception! Closing Socket " + clientSocket.getRemoteSocketAddress() + "...");
            ShowRoomsState();
        }
        CloseSocket();

    }

    private String ProcessMessage(SocketUtilities.Message msg){

        if(msg == null || msg.messageHeader == null) { ClientDisconnects(); };

        switch (msg.messageHeader) {
            case CLIENT_DATA:
                return msg.data;

            case CLIENT_DISCONNECTED:
                ClientDisconnects();
                break;

            default:
                System.out.println("Error processing Message! Header not implemented!");
                break;
        }
        return null;
    }

    private synchronized void ClientDisconnects(){
        server.Rooms.get(clientDataRoomName).remove(clientData);
        if(server.Rooms.get(clientDataRoomName).size() == 0){
            server.Rooms.remove(clientDataRoomName);
        }
        throw new ClientDisconnectsException("Client " + clientSocket.getRemoteSocketAddress() + " has disconnected");
    }

    private void AskForClientData(){
        // Read the Nickname from client
        SocketUtilities.Message msgClientNickname = SocketUtilities.ReadSocketData(clientSocket);
        String clientNickname = ProcessMessage(msgClientNickname);

        // Read the Room Name from client
        SocketUtilities.Message msgClientDataRoomName = SocketUtilities.ReadSocketData(clientSocket);
        clientDataRoomName = ProcessMessage(msgClientDataRoomName);

        // New client data
        clientData = new ClientData(clientSocket.getRemoteSocketAddress(), clientSocket, clientNickname);
        System.out.println("New client processed: " + clientData.ClientNickname + " in room " + clientDataRoomName + " with IP: " + clientData.ClientID);
    }

    private synchronized void ManageRooms(ClientData clientData){

        if(server.Rooms.containsKey(clientDataRoomName)){
            server.Rooms.get(clientDataRoomName).add( clientData);
        }
        else {
            server.Rooms.put(clientDataRoomName, new ArrayList<ClientData>(){{ add(clientData); }});
        }
        ShowRoomsState();
    }
    private synchronized void ShowRoomsState(){
        for (Map.Entry<String, ArrayList<ClientData>> room : server.Rooms.entrySet()) {
            System.out.println( GetRoomState(room.getKey()) );
        }
    }


    private void WriteDataRoomState(){
        String roomState = GetRoomState(clientDataRoomName);
        SocketUtilities.WriteSocketData(clientSocket, new SocketUtilities.Message(SocketUtilities.EMessageHeader.SERVER_DATA, roomState));
    }

    private String GetRoomState(String roomID){
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


    private void StartChat(){
        // Chat starts

        while(true) {
            // Get new message from the client
            SocketUtilities.Message newMsgClient = SocketUtilities.ReadSocketData(clientSocket);
            String newData = ProcessMessage(newMsgClient);
            System.out.println("Message received: " + newData);

            // Send it to all other clients in the same room
            ArrayList<ClientData> clientsInTheSameRoom = server.Rooms.get(clientDataRoomName);
            for (ClientData iclientData : clientsInTheSameRoom) {
                if(iclientData.ClientSocket == clientSocket) continue;
                SocketUtilities.Message newMessage = new SocketUtilities.Message(SocketUtilities.EMessageHeader.SERVER_DATA, newData);
                SocketUtilities.WriteSocketData(iclientData.ClientSocket, newMessage);
            }
        }
    }


    private void CloseSocket(){
		try {
            clientSocket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage() + " --- " + e.getStackTrace());
        }
    }
}
