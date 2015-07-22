package pythonTogether.updating;

import java.util.TimerTask;

import pythonTogether.managers.Manager;

public class UpdateTask extends TimerTask{
	private Updater updater;
	
	public UpdateTask(Updater updater){
		this.updater = updater;
	}
	
	public void run(){
		sendUpdate();
	}
	
	public void sendUpdate(){
		updater.sendUpdate();
	}
}
