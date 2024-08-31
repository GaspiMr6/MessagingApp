import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
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
        Client client = new Client();
        try {
            
            System.out.print("Send some bytes to the server: ");
            InputStreamReader inStreamReader = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(inStreamReader);
            String input = br.readLine();
            
            Socket clientSocket = new Socket(host, Integer.parseInt(port));
            client.WriteSocketData(clientSocket, input);
            System.out.println("Sending to the server: " + input);

        } catch (NumberFormatException | IOException e) {
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
