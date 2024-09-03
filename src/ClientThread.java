import java.net.Socket;

public class ClientThread extends Thread {

    Socket clientSocket;

    public ClientThread(Socket clientSocketReceived){
        clientSocket = clientSocketReceived;
    }

    public void run (){

        String response;
        do {
             response = SocketUtilities.ReadSocketData(clientSocket);
            System.out.println(response);
        } while (!response.equals("END"));
    }
}
