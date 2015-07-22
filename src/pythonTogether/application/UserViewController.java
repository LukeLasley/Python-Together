package pythonTogether.application;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import pythonTogether.managers.Manager;
import pythonTogether.model.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class UserViewController {
	@FXML
	private TableView<User> ConnectedUsers;
	@FXML
	private TableColumn<User,String> Names;
	@FXML
	private TableColumn<User,String> IPs;
	@FXML
	private TabPane Files;
	@FXML
	private Label InfoLabel;
	@FXML
	private TextArea TextArea;
	@FXML
	private TextArea Messages;
	@FXML
	private Button sendMessage;
	@FXML
	private TextField toSend;

	private PythonTogether mainApp;
	private Manager manager;
	private HashMap<String,Integer> doccumentMap;


	public UserViewController() {
	}

	@FXML
	private void initialize() {
		Names.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		IPs.setCellValueFactory(cellData -> cellData.getValue().IPProperty());
		doccumentMap = new HashMap<String,Integer>();
		Console console = new Console(TextArea,this);
		PrintStream printstream = new PrintStream(console,true);
		//System.setOut(printstream);  
		//System.setErr(printstream);
	}
	@FXML
	private void openFile(){
		mainApp.openFile();
	}
	@FXML
	private void createFile(){
		mainApp.makeNewFile();
	}
	@FXML
	private void connect(){
		mainApp.connect();
	}
	@FXML
	private void save(){
		String filename = getNameOfSelected();
		if(filename!=null){
			mainApp.saveFile(filename,false);
		}
	}
	@FXML
	private void runFileFromControl(){
		String filename = getNameOfSelected();
		if(filename!=null){
			mainApp.runFile(filename);
		}
	}
	@FXML
	private void disconnect(){
		mainApp.disconnect();
	}
	@FXML
	private void sendMessage(){
		String message = toSend.getText();
		String name = manager.getName();
		Platform.runLater(new Runnable() {
			@Override public void run() {
				if(message.length() >0){
					Messages.appendText(name +": " +message+"\n");
					toSend.clear();
					manager.sendTextMessage(message);
				}
			}
		});

	}
	public void updateMessages(String name, String message){
		Platform.runLater(new Runnable() {
			@Override public void run() {
				Messages.appendText(name +": " +message+"\n");
			}
		});
	}
	public void setUserInfo(String info){
		InfoLabel.setText(info);
	}

	public void createTab(String name,String contents){
		Platform.runLater(new Runnable() {
			@Override public void run() {
				Tab tab = new Tab(name);
				tab.setClosable(true);
				TextArea text = new TextArea(contents);
				tab.setContent(text);
				Files.getTabs().add(tab);
				doccumentMap.put(name,Files.getTabs().indexOf(tab));
			}
		});
	}
	public void setMainApp(PythonTogether mainApp) {
		this.mainApp = mainApp;
		ConnectedUsers.setItems(mainApp.getUserData());
	}

	public void addUser(String name, String IP){
		User user = new User(name, IP);
		mainApp.addUser(user);
	}
	public void updateText(String name, String text){
		Platform.runLater(new Runnable() {
			@Override public void run() {
				int index = doccumentMap.get(name);
				TextArea textArea = (javafx.scene.control.TextArea) Files.getTabs().get(index).getContent();
				int location = textArea.getCaretPosition();
				textArea.setText(text);
				textArea.positionCaret(location);
			}
		});

	}
	public String getCurText(String name){
		int index = doccumentMap.get(name);
		TextArea textArea = (javafx.scene.control.TextArea) Files.getTabs().get(index).getContent();
		return textArea.getText();
	}

	public void updateConsole(String value, TextArea output) {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				output.appendText(value);
			}
		});

	}

	public void askForSave(String filename, String string) {
		mainApp.askForSave(filename,string);
	}

	public void setManager(Manager manager) {
		this.manager = manager;

	}

	public void sendDuplicateMessage(String filename) {
		if(filename!=null){
			mainApp.duplicateMessageWithSave(filename);
		}else{
			mainApp.duplicateMessage();
		}
	}
	public String getNameOfSelected(){
		int i = Files.getSelectionModel().getSelectedIndex();
		String filename = null;
		if(i>=0){
			for(Map.Entry<String, Integer> entry : doccumentMap.entrySet()){
				if(entry.getValue().equals(i)){
					filename = entry.getKey();
				}
			}
		}
		return filename;
	}

	public void removeUser(String userName, String ip) {
		mainApp.removeUser(userName,ip);
	}
}
