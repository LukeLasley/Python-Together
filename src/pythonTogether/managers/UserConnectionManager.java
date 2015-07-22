package pythonTogether.managers;

import java.util.ArrayList;

public class UserConnectionManager {
	private Manager manager;
	private ArrayList<String[]> connectedUsers;

	public UserConnectionManager(Manager manager) {
		this.manager = manager;
		connectedUsers = new ArrayList<String[]>();
	}

	public void addUser(String name, String IP) {
		String[] toAdd = { name, IP };
		connectedUsers.add(toAdd);
	}

	public void removeUser(String[] userInfo) {
		for( int i = 0; i<connectedUsers.size();i++){
			
			if(connectedUsers.get(i)[0].equals(userInfo[0])){
				if(connectedUsers.get(i)[1].equals(userInfo[1])){
					connectedUsers.remove(i);
					
			}
		}
		}
	}

	public void clearUsers() {
		connectedUsers.clear();
	}
	public ArrayList<String[]> getUsers(){
		return connectedUsers;
	}
}
