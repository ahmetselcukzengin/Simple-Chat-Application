import java.io.*;
import java.net.*;

public class MyChatClientConnection extends Thread {
	private Socket sock;
	private MyChatClient client;
	private BufferedReader in;
	private PrintWriter out;

	public MyChatClientConnection(Socket sock, MyChatClient client) throws IOException {
		this.sock = sock;
		this.client = client;
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new PrintWriter(sock.getOutputStream(), true);
	}

	public void send(String msg) {
		this.out.println(msg);
	}
	
	public void run() {
		try {
			String line;
			while ((line = in.readLine()) != null) {	
				MyChatClient.gui.displayIncomingMessage(line);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			client.hangUp();
		}

		try {
			out.close();
			in.close();
			sock.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}	
}
