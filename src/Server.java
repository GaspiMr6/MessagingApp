import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	public static void main(String[] args) {

		if (args.length < 1) {
			System.out.println("Error: You must specify the listening port of the server");
			System.exit(-1);
		}

		int port = Integer.parseInt(args[0]);
		System.out.println("Listening on port " + port);

		try (ServerSocket serverSocket= new ServerSocket(port, 50, InetAddress.getByName("0.0.0.0"))){
			System.out.println("Waiting for clients...");
			Socket clientSocket = serverSocket.accept();
			System.out.println("Client connected " + clientSocket.getRemoteSocketAddress());

			String clientData = ReadSocketData(clientSocket);
			System.out.println("I've received " + clientData);

			clientSocket.close();
		} catch (IOException exception) {
			System.out.println("IOException: " + exception.toString());
		}

        System.out.println();
	}

	private static String ReadSocketData(Socket socket) {

		try (InputStream inputStream = socket.getInputStream();
			 DataInputStream dataInStream = new DataInputStream(inputStream)){
			return dataInStream.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

}