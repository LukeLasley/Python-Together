package pythonTogether.managers;

import java.util.ArrayList;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONManager {
	private Manager manager;
	
	public JSONManager(Manager manager){
		this.manager = manager;
	}
	@SuppressWarnings("unchecked")
	public JSONObject createConnectionMessage(ArrayList<String>users, ArrayList<String>IPs, boolean response){
		JSONObject message = new JSONObject();
		if(response){
			message.put("Status", "Connect-Response");
		}else{
			message.put("Status", "Connect-Initial");
		}
		message.put("Users", users);
		message.put("IPs", IPs);
		return message;
	}
	@SuppressWarnings("unchecked")
	public JSONObject createUpdateMessage(ArrayList<String> changedDocs, ArrayList<Map> changes, int currentTimeStamp) {
		JSONObject message = new JSONObject();
		message.put("Status", "Update");
		message.put("TimeStamp", currentTimeStamp);
		message.put("Doccuments", changedDocs);
		message.put("Changes", changes);
		return message;
		
		
	}
	@SuppressWarnings("unchecked")
	public JSONObject createDoccumentMessage(String name, String text,boolean outdated){
		JSONObject message = new JSONObject();
		if(outdated){
			message.put("Status", "Outdated");
		}else{
			message.put("Status", "New-Doc");
		}
		message.put("Name", name);
		message.put("Text", text);
		return message;
		
	}
	@SuppressWarnings("unchecked")
	public void readJSON(String message){
		JSONParser parser=new JSONParser();
        Object obj;
		try {
			obj = parser.parse(message);
			JSONArray array = (JSONArray)obj;
			JSONObject obj2 =  (JSONObject) array.get(0);
			String status = (String) obj2.get("Status");
			if(status.equals("Connect-Initial")){
				readInitialConnection((ArrayList<String>)obj2.get("Users"),(ArrayList<String>)obj2.get("IPs"));
			}else if(status.equals("Connect-Response")){
				readResponseConnection((ArrayList<String>)obj2.get("Users"),(ArrayList<String>)obj2.get("IPs"));
			}else if(status.equals("New-Doc")){
				readNewDocMessage((String)obj2.get("Name"),(String)obj2.get("Text"),obj2.get("Version"));
			}else if(status.equals("Outdated")){
				readOutdatedMessage((String)obj2.get("Name"),(String)obj2.get("Text"),obj2.get("Version"));
			}else if(status.equals("Text")){
				readTextMessageUpdate((String)obj2.get("Name"),(String)obj2.get("Text"));
			}else if(status.equals("Disconnect")){
				readDisconnectMessageUpdate((String)obj2.get("Name"),(String)obj2.get("IP"));
			}else{
				readUpdateMessage(obj2.get("TimeStamp"),
						(ArrayList<String>)obj2.get("Doccuments"),(ArrayList<Map>)obj2.get("Changes"));
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void readDisconnectMessageUpdate(String userName, String ip) {
		manager.removeUser(userName,ip);
		
	}
	private void readTextMessageUpdate(String name, String text) {
		manager.updateText(name,text);
		
	}
	private void readUpdateMessage(Object TimeStamp, ArrayList<String> docs, ArrayList<Map> docChanges) {
		Long l = (long) TimeStamp;
		manager.updateDocs(l.intValue(),docs,docChanges);
		
	}
	private void readOutdatedMessage(String name, String text, Object version) {
		Long l = (long) version;
		manager.updateOutdated(name,text,l.intValue());
		
	}
	private void readNewDocMessage(String name,String text, Object object) {
		Long l = (long) object;
		manager.addNewDoc(name,text,l.intValue(),false,false);
		
	}
	private void readResponseConnection(ArrayList<String> names,ArrayList<String> ips) {
		for(int i = 0; i< names.size();i++){
			manager.addUser(names.get(i),ips.get(i));
		}
		
	}
	private void readInitialConnection(ArrayList<String> names, ArrayList<String> ips) {
		for(int i = 0; i< names.size();i++){
			manager.connect(ips.get(i), true);
		}
		manager.sendMessageToAllUsers(createConnectionMessage(names, ips,true).toString());
		for(int i = 0; i< names.size();i++){
			manager.addUser(names.get(i),ips.get(i));
		}
	}
	public JSONObject createTextMessage(String name, String text) {
		JSONObject message = new JSONObject();
		message.put("Status", "Text");
		message.put("Name", name);
		message.put("Text",text);
		return message;
		
	}
	public JSONObject createDisconnectMessage(String name, String iP) {
		JSONObject message = new JSONObject();
		message.put("Status", "Disconnect");
		message.put("Name", name);
		message.put("IP",iP);
		return message;
	}
}