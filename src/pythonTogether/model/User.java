package pythonTogether.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User {

    private final StringProperty name;
    private final StringProperty IP;
    
	public User(String name, String IP) {
        this.name = new SimpleStringProperty(name);
        this.IP = new SimpleStringProperty(IP);
    }
	 public String getName() {
	        return name.get();
	    }

	    public void setFirstName(String name) {
	        this.name.set(name);
	    }

	    public StringProperty nameProperty() {
	        return name;
	    }

	    public String getIP() {
	        return IP.get();
	    }
	    public void setLastName(String IP) {
	        this.IP.set(IP);
	    }

	    public StringProperty IPProperty() {
	        return IP;
	    }
}
