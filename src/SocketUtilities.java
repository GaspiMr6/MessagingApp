import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketUtilities {
    

	public static String ReadSocketData(Socket socket) {

		try {
            InputStream inputStream = socket.getInputStream();
			DataInputStream dataInStream = new DataInputStream(inputStream);
			return dataInStream.readUTF();
		} catch (IOException e) {
			System.out.println("Error Reading: " + e + " " + e.getMessage() + " " + e.getCause());
		}
		return "";
	}

    public static void WriteSocketData(Socket socket, String data) {
        try {
            OutputStream outStream = socket.getOutputStream();
            DataOutputStream dataOutStream = new DataOutputStream(outStream);
            dataOutStream.writeUTF(data);
        } catch (Exception e) {
            System.out.println("Error Writing: " + e.getMessage() + e.getStackTrace().toString() + " " + e.getCause());
        }
    }

}
