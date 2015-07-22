package pythonTogether.application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import pythonTogether.managers.Manager;
import pythonTogether.model.User;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

public class PythonTogether extends Application {

	private Stage primaryStage;
	private AnchorPane rootLayout;
	private ObservableList<User> userData = FXCollections.observableArrayList();
	private UserViewController controller;
	private Manager manager;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Python Together");
		this.primaryStage.setResizable(false);
		
		initRootLayout();
	}

	public void initRootLayout() {
		try {
			String name = getName();
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(PythonTogether.class.getResource("View.fxml"));
			rootLayout = (AnchorPane) loader.load();
			
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
			controller = loader.getController();
			manager = new Manager(controller,name);
			controller.setMainApp(this);
			controller.setManager(manager);
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
	            @Override
	            public void handle(WindowEvent t) {
	            	manager.disconnect();
	                Platform.exit();
	                System.exit(0);
	            }
	        });
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private String getName() {
		@SuppressWarnings("deprecation")
		Optional<String> response = Dialogs.create()
			.owner(primaryStage)
			.title("What is your name?")
			.masthead("Please enter your name")
			.showTextInput("");
		if(!response.isPresent()){
			System.exit(0);
		}else if(response.get().length() == 0){
			System.exit(0);
		}
		return response.get();
	}

	public void openFile(){
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Python Files", "*.py");
		fileChooser.getExtensionFilters().add(extFilter);
		fileChooser.setTitle("Open Python File");
		File file = fileChooser.showOpenDialog(primaryStage);
		if(file!= null){
			manager.openFile(file);
		}
	}

	public ObservableList<User> getUserData() {
		return userData;
	}

	public void addUser(User user){
		userData.add(user);
	}

	public Stage getPrimaryStage(){
		return primaryStage;
	}
	@SuppressWarnings("deprecation")
	public void connect(){
		Optional<String> IP = Dialogs.create()
				.owner(primaryStage)
				.title("What IP would you like to connect to?")
				.masthead("Enter an IP")
				.showTextInput("");
		if(!IP.isPresent()){
			return;
		}else if(IP.get().length() == 0){
			return;
		}
		manager.connect(IP.get(), false);
	}

	public static void main(String[] args) {
		launch(args);
	}

	public void removeDoccument(String key) {
		manager.removeDoccument(key);

	}

	public void makeNewFile() {
		@SuppressWarnings("deprecation")
		Optional<String> filename = Dialogs.create()
			.owner(primaryStage)
			.title("What would you like to name the file?")
			.masthead("Enter A Name")
			.showTextInput("");
		if(!filename.isPresent()){
			return;
		}else if(filename.get().length() == 0){
			return;
		}
		String name = filename.get();
		if(name.length()<3){
			name += ".py";
		}else{
			String substring = name.substring(Math.max(name.length() - 3, 0));
			if(!substring.equals(".py")){
				name += ".py";
			}
		}
		manager.addNewDoc(name, "", 0,true,true);
	}

	public void runFile(String filename) {
		manager.preRun(filename);

	}

	@SuppressWarnings("deprecation")
	public void askForSave(String filename, String location) {
		Action response = Dialogs.create()
				.owner(primaryStage)
				.title("Save First")
				.message("This File Must Be Saved First. Save?")
				.actions(Dialog.ACTION_OK, Dialog.ACTION_CANCEL)
				.showConfirm();

		if (response == Dialog.ACTION_OK) {
			if(location !=null){
				manager.saveFile(filename, location);
				manager.runFile(filename);
			}else{
				saveFile(filename,true);
			}
		} else {
			return;
		}

	}

	public void saveFile(String filename,Boolean needsRunning) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select Location");
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Python Files", "*.py");
		fileChooser.getExtensionFilters().add(extFilter);
		File file = fileChooser.showSaveDialog(primaryStage);
		if(file!= null){
			manager.saveFile(filename, file.toString());
		}
		if(needsRunning){
			manager.runFile(filename);
		}
	}

	public void duplicateMessageWithSave(String filename) {
		Action response = Dialogs.create()
				.owner(primaryStage)
				.title("Save First")
				.message("There is a more current version of this file currently open, changing tab contents. Save it first?")
				.actions(Dialog.ACTION_OK,Dialog.ACTION_NO)
				.showConfirm();
		if (response == Dialog.ACTION_OK) {
			saveFile(filename,false);
		} else {
			return;
		}
		
	}

	public void duplicateMessage() {
		Action response = Dialogs.create()
				.owner(primaryStage)
				.title("Save First")
				.message("There is a more current version of this file currently open. The file could not be opened.")
				.showConfirm();	
	}

	public void removeUser(String userName, String ip) {
		for(int i = 0;i<userData.size();i++){
			User curUser = userData.get(i);
			if(curUser.getName().equals(userName) && curUser.getIP().equals(ip)){
				userData.remove(i);
			}
		}
		
	}

	public void disconnect() {
		userData.clear();
		manager.disconnect();
		
	}

}
