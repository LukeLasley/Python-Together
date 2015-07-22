package pythonTogether.networking;

import java.net.*;
import java.io.*;

import pythonTogether.managers.Manager;

public class CommunicationsThread extends Thread {
	private static Socket socket;
	private static Manager manager;

	 @SuppressWarnings("static-access")
	public CommunicationsThread(Manager manager, Socket socket) {
		CommunicationsThread.socket = socket;
		this.manager = manager;
	}

	public void run() {
		try {
			BufferedReader responses = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			while (!responses.ready()) {
			}
			while (responses.ready()) {
				String msg = responses.readLine();
				if (msg.length() == 0 || msg == "\n" || msg == null) {
				} else {
					parseData(msg);
				}
			}
			socket.close();
		} catch (IOException e) {
			// Better error handling here.
			e.printStackTrace();
		}
	}

	private static void parseData(String input) throws UnknownHostException,
			IOException {
		manager.recieveMessage(input);
	}

}