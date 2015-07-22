package pythonTogether.networking;

import java.net.*;
import java.io.*;

import pythonTogether.managers.Manager;

public class Accepter extends Thread {
	private ServerSocket accepter;
	private Manager manager;
	private static int port;

	public Accepter(int port, Manager manager) {
		this.manager = manager;
		Accepter.port = port;

	}

	public void listen() {
		for (;;) {
			Socket s;
			try {
				s = accepter.accept();
				new CommunicationsThread(manager, s).start();
			} catch (IOException e) {
				e.printStackTrace();
				// Better error handling here.
			}

		}
	}

	public void run() {
		try {
			accepter = new ServerSocket();
		} catch (IOException e1) {
			// Better error handling here.
			e1.printStackTrace();
		}
		try {
			accepter.setReuseAddress(true);
		} catch (SocketException e1) {
			// Better error handling here.
			e1.printStackTrace();
		}
		try {
			accepter.bind(new InetSocketAddress(port));
		} catch (IOException e) {
			// Better error handling here.
		}
		listen();
	}
}