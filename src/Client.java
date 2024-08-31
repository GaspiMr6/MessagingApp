import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Error: You must specify the host addres and the port");
            System.exit(-1);
        }
        String host = args[0];
        String port = args[1];
        host = "192.168.1.10";
        port = "8080";
        Client client = new Client();
        
        try (Socket clientSocket = new Socket(host, Integer.parseInt(port));
             BufferedReader br = new BufferedReader(new InputStreamReader(System.in));){
            System.out.print("Send some bytes to the server: ");
            String input = br.readLine();
            client.WriteSocketData(clientSocket, input);
            System.out.println("Sending to the server: " + input);

        } catch (Exception e) {
            System.out.println("Exception caught: " + e.getMessage() + "\nStackTrace: " + e.getStackTrace() + " " + e.getCause());
            e.printStackTrace();
        }
        System.out.println();
    }

    private void WriteSocketData(Socket socket, String data) {
        try {
            OutputStream outStream = socket.getOutputStream();
            DataOutputStream dataOutStream = new DataOutputStream(outStream);
            dataOutStream.writeUTF(data);
        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }
    }

}
