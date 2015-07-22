package pythonTogether.managers;

import pythonTogether.application.UserViewController;
import pythonTogether.networking.Accepter;
import pythonTogether.networking.Sender;
import pythonTogether.updating.UpdateTimer;
import pythonTogether.updating.Updater;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JOptionPane;

import org.json.simple.JSONObject;
import org.python.util.PythonInterpreter;

@SuppressWarnings("rawtypes")
public class Manager {
	private String name;
	private String IP = null;
	private UserConnectionManager connectionManager;
	private UpdateTimer timer;
	private Updater updater;
	private UserViewController gui;
	private IOManager IO;
	private JSONManager JsonManager;
	private HashMap<String,Doccument> doccuments;

	public Manager(UserViewController gui,String name) {
		startConnection();
		this.gui = gui;
		this.name = name;
		doccuments = new HashMap<String,Doccument>();
		gui.setUserInfo("Your IP is: " + IP);
		connectionManager = new UserConnectionManager(this);
		updater = new Updater(this,doccuments);
		timer = new UpdateTimer(updater);
		IO = new IOManager(this);
		JsonManager = new JSONManager(this);
	}

	public void startConnection() {
		getIP();
		Accepter accepter = new Accepter(8888, this);
		accepter.start();
	}

	public void getIP(){
		Enumeration<NetworkInterface> interfaces;
		try {
			interfaces = NetworkInterface.getNetworkInterfaces();
			while(interfaces.hasMoreElements()){
				NetworkInterface n = (NetworkInterface) interfaces.nextElement();
				Enumeration<InetAddress> inetAddresses = n.getInetAddresses();
				while (inetAddresses.hasMoreElements()){
					InetAddress i = (InetAddress) inetAddresses.nextElement();
					String strIP = i.toString();
					if (strIP.split("\\.").length > 1 && !i.isLoopbackAddress()&& !i.isLinkLocalAddress() && !i.isMulticastAddress()){
						IP = strIP.split("\\/")[1];
					}
				}
			}
			if(IP == null){
				JOptionPane.showMessageDialog(null, 
						"IP address could not be found! Quitting Program.",
						"IP Obtaining Error",
						JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void connect(String connectTo, boolean response) {
		ArrayList<String> users = new ArrayList<String>();
		ArrayList<String> ips = new ArrayList<String>();
		users.add(name);
		ips.add(IP);
		ArrayList<String[]> connectedUsers = connectionManager.getUsers();
		for(int i = 0; i < connectedUsers.size(); i++){
			String userName = connectedUsers.get(i)[0];
			String userIP = connectedUsers.get(i)[1];
			users.add(userName);
			ips.add(userIP);
		}
		JSONObject message = JsonManager.createConnectionMessage(users, ips, response);
		Sender sender;
		try {
			sender = new Sender(8888,connectTo,message.toString());
			sender.start();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, 
					"IP address was not valid!",
					"Connection Error",
					JOptionPane.ERROR_MESSAGE);
		}
		sendAllDocs(connectTo);
	}
	//TODO When MAC address works, change
	private void sendAllDocs(String connectTo) {
		Iterator it = doccuments.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry pair = (Map.Entry)it.next();
			Doccument curDoc = (Doccument) pair.getValue();
			JSONObject message = JsonManager.createDoccumentMessage(curDoc.getName(), curDoc.getText(), false);
			Sender sender;
			try {
				sender = new Sender(8888,connectTo,message.toString());
				sender.start();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, 
						"IP address was not valid!",
						"Connection Error",
						JOptionPane.ERROR_MESSAGE);
				break;
			}
		}

	}

	public void recieveMessage(String message){
		JsonManager.readJSON("["+ message +"]");
	}
	//TODO MAC Address Important here
	public void openFile(File file) {
		String fileLocation = file.toString();
		String[] splitLocation = fileLocation.split("\\/");
		if(splitLocation.length == 1){
			splitLocation = fileLocation.split("\\\\");
		}
		String content = IO.readFile(fileLocation);
		String documentName = splitLocation[splitLocation.length-1];
		Doccument curDocument = new Doccument(documentName,this);
		if(!duplicateDoc(documentName)){
			gui.createTab(documentName, content);
			curDocument.setLocation(fileLocation);
			curDocument.setText(content);
			doccuments.put(documentName, curDocument);
			sendOpenDoccumentMessage(curDocument);
		}else{
			gui.sendDuplicateMessage(null);
		}
	}
	//TODO MAC Address
	private void sendOpenDoccumentMessage(Doccument curDoccument) {
		JSONObject message = JsonManager.createDoccumentMessage(curDoccument.getName(), curDoccument.getText(),false);
		sendMessageToAllUsers(message.toString());
	}

	public void addUser(String name, String ip) {
		connectionManager.addUser(name, ip);
		gui.addUser(name, ip);
	}

	public void addNewDoc(String documentName, String content, int version,boolean send,boolean localAdd) {
		Doccument curDoc = new Doccument(documentName,this);
		if(!duplicateDoc(documentName)){
			gui.createTab(documentName, content);
			curDoc.setText(content);
			doccuments.put(documentName, curDoc);
			if(send){
				sendOpenDoccumentMessage(curDoc);
			}
		}else{
			if(!localAdd){
				sendOutDatedVersionMessage(name,content);
			}
		}
	}

	private void sendOutDatedVersionMessage(String name, String text) {
		JSONObject message = JsonManager.createDoccumentMessage(name,text, true);
		sendMessageToAllUsers(message.toString());
	}

	public void sendMessageToAllUsers(String message) {
		ArrayList<String[]> connectedUsers = connectionManager.getUsers();
		for(int i = 0; i < connectedUsers.size(); i++){
			String userIP = connectedUsers.get(i)[1];
			Sender sender;
			try {
				sender = new Sender(8888,userIP,message);
				sender.start();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, 
						"IP address was not valid!",
						"Connection Error",
						JOptionPane.ERROR_MESSAGE);
			}

		}

	}

	private boolean duplicateDoc(String name) {
		if(doccuments.containsKey(name)){
			return true;
		}else{
			return false;
		}
	}

	public void updateOutdated(String name, String text, int version) {
		doccuments.get(name).setText(text);
	}

	public void sendDoccumentUpdate(ArrayList<String> changedDocs,
			ArrayList<Map> changes, int currentTimeStamp) {
		JSONObject message = JsonManager.createUpdateMessage(changedDocs,changes,currentTimeStamp);
		sendMessageToAllUsers(message.toString());

	}

	public void updateDocs(int timeStamp, ArrayList<String> docs,
			ArrayList<Map> docChanges) {
		if(!updater.recieveUpdate(docs, docChanges, timeStamp)){
			for(int i=0; i < docs.size();i++){
				String curName = docs.get(i);
				for(int j = 0; j<doccuments.size();j++){
					Doccument curDoccument = doccuments.get(j);
					sendOutDatedVersionMessage(curName,curDoccument.getText());
				}
			}
		}

	}

	public String getText(String name) {
		return gui.getCurText(name);
	}

	public void setText(String name, String text) {
		gui.updateText(name, text);

	}

	public void removeDoccument(String key) {
		int index = 0;
		for(int i = 0;i<doccuments.size();i++){
			if(doccuments.get(i).getName() == key){
				index = i;
			}
		}
		doccuments.remove(index);

	}

	public void saveFile(String filename,String fileLocation){
		Doccument curDoc = doccuments.get(filename);
		String location = "";
		if(curDoc.getLocation() != null){
			location = curDoc.getLocation();
		}else{
			location = fileLocation;
			curDoc.setLocation(fileLocation);
		}
		String text = curDoc.getText();
		IO.saveFile(location, text);
	}
	public void preRun(String filename){
		Doccument curDoc = doccuments.get(filename);
		if(curDoc.getLocation() != null && curDoc.getSavedStatus()){
			runFile(filename);
		}else{
			if(curDoc.getLocation() != null){
				gui.askForSave(filename,curDoc.getLocation());
			}else{
				gui.askForSave(filename,null);
			}
		}
	}
	public void runFile(String filename) {
		Doccument curDoc = doccuments.get(filename);
		PythonInterpreter interpreter = new PythonInterpreter();
		if(curDoc.getLocation()!= null){
			IO.saveFile(curDoc.getLocation(), curDoc.getText());
			interpreter.execfile(curDoc.getLocation());
			interpreter.close();
		}
	}

	public String getName() {
		return name;
	}

	public void sendTextMessage(String message) {
		JSONObject toSend = JsonManager.createTextMessage(name,message);
		sendMessageToAllUsers(toSend.toString());


	}

	public void updateText(String name2, String text) {
		gui.updateMessages(name2, text);
	}

	public void disconnect() {
		JSONObject disconnectMessage = JsonManager.createDisconnectMessage(name,IP);
		sendMessageToAllUsers(disconnectMessage.toString());
		connectionManager.clearUsers();

	}

	public void removeUser(String userName, String ip) {
		String[] user = new String[2];
		user[0]= userName;
		user[1] = ip;
		connectionManager.removeUser(user);
		gui.removeUser(userName,ip);

	}
}
