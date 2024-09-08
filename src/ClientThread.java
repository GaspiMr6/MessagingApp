import java.net.Socket;

// ClientThread is used to be listening to other clients messages 
public class ClientThread extends Thread {

    Socket clientSocket;

    public ClientThread(Socket clientSocketReceived){
        clientSocket = clientSocketReceived;
    }

    public void run (){

        try {
            while (!Thread.currentThread().isInterrupted()) {
                SocketUtilities.Message newMsg = SocketUtilities.ReadSocketData(clientSocket);
                String newMsgData = Client.ProcessMessage(newMsg);
                System.out.println(newMsgData);
            }
        } catch (Exception e){
            System.out.println("Client Thread Exception: " + e.getCause() + " and " + e.getLocalizedMessage());
        }
    }
}
