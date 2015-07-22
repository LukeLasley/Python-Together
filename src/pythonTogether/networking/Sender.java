package pythonTogether.networking;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Sender extends Thread {
	Socket s;
	PrintWriter output;
	String msg;

	public Sender(int port, String host, String msg)
			throws UnknownHostException, IOException {
		s = new Socket(host, port);
		output = new PrintWriter(s.getOutputStream());
		this.msg = msg;
	}

	public void send(String msg) {
		output.println(msg);
		output.flush();
	}

	public void close() throws IOException {
		s.close();
	}

	public void run() {
		send(msg);
		try {
			close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}