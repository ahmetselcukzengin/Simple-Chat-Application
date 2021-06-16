import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime; 

public class MyChatServerConnection extends Thread {
	public static HashMap<String, ArrayList<String>> userInboxes = new HashMap<String, ArrayList<String>>();
	
	private Socket sock;
	private MyChatServer server;
	private BufferedReader clientIn;
	private PrintWriter clientOut;
	private String userName;
	private boolean isSessionActive = false;

	public MyChatServerConnection(Socket sock, MyChatServer server) {
		this.sock = sock;
		this.server = server;
	}
	
	public void handleBeginSession(String[] tokens) {
		if (tokens.length < 2) {
			socketSend("Kullanici adi eksik");
			return;
		}
		int messageCount;
		userName = tokens[1];
		isSessionActive = true;
		ArrayList<String> userInbox = userInboxes.get(userName);
		socketSend("Oturum acildi.\nHos geldin "+userName);
		if (userInbox == null || userInbox.size() == 0) {
			messageCount = 0;
			socketSend("Bekleyen mesajiniz bulunmuyor.");
		}
		else {
			messageCount = userInbox.size();
			socketSend(messageCount + " adet SMS mesajiniz bulunuyor");		
			sendAllMessages(userInbox);
		}
	}
	
	
	/**
	 * Kullanicinin inbox'inda bekleyen tum mesajlari gonderir ve inbox'i bosaltir.
	 * @param userInbox Kullanicinin inbox'i
	 * @return Hic bir sey dondurmez
	 */
	public void sendAllMessages(ArrayList<String> userInbox) {
		if (userInbox != null) {
			while (userInbox.size() > 0) {
			    String message = userInbox.get(0);
				socketSend(message);
				userInbox.remove(0);
			}
		}
	}
	
	/**
	 * Client'tan gelen SEND_SMS komutunu isler. Alicinin inbox'ini bulup mesaji icine yerlestirir.
	 * Alicinin henuz bir inbox'i yoksa, yaratir.
	 * @param tokens Client'tan gelen komuttaki kelimeler
	 */
	public void handleSendSms(String[] tokens) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		
		if (tokens.length < 3) {
			socketSend("Hata: Alici adi veya mesaj eksik.");
			return;
		}
		String recipientUserName = tokens[1];
		String[] messageTokens = Arrays.copyOfRange(tokens, 2, tokens.length);
		String message = String.join(" ", messageTokens);
		
		ArrayList<String> recipientInbox = userInboxes.get(recipientUserName);
		if (recipientInbox == null) {
			recipientInbox = new ArrayList<String>();
			userInboxes.put(recipientUserName, recipientInbox);
		}
		recipientInbox.add(String.format(dtf.format(now)+" "+"%s: %s", userName, message));
	}
	
	/**
	 * Client'tan gelen POP_SMS komutunu isler ve bekleyen mesaj varsa kullaniciya gonderir.
	 * @param tokens Client'tan gelen komuttaki kelimeler
	 */
	public void handlePopSms(String[] tokens) {
		if(userName!=null) {
			ArrayList<String> userInbox = userInboxes.get(userName);
			if (userInbox == null || userInbox.size() == 0)
				return;
			else {
				sendAllMessages(userInbox);
			}
		}
	}
	
	public void handleEndSession(String[] tokens) {
		isSessionActive = false;
	}
	
	public void handleUnexpectedCommand(String[] tokens) {
        socketSend("Hata: Komut anlasilamadi: " + tokens[0]);
	}
	
	public void run() {
		try {
			clientIn = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			clientOut = new PrintWriter(sock.getOutputStream(), true);

			String line;
			String command = "";
			while((line = clientIn.readLine()) != null && !command.equalsIgnoreCase("END_SESSION")) {
				String[] tokens = line.split(" ");
				command = tokens[0];
				if (!isSessionActive) {
					if (command.equalsIgnoreCase("BEGIN_SESSION"))
						handleBeginSession(tokens);
					else if (command.equalsIgnoreCase("POP_SMS"))
						continue;
					else handleUnexpectedCommand(tokens);
				}
				else {
					
					if (command.equalsIgnoreCase("SEND_SMS"))
						handleSendSms(tokens);
					else if (command.equalsIgnoreCase("POP_SMS"))
						handlePopSms(tokens);
					else if (command.equalsIgnoreCase("END_SESSION"))
						handleEndSession(tokens);
					else handleUnexpectedCommand(tokens);
				}

			}

			server.removeConnection(this);
			clientOut.close();
			clientIn.close();
			sock.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Client'a socket uzerinden mesaj gonderir
	 * @param msg
	 */
	public void socketSend(String msg) {
		clientOut.println(msg);
	}
}
