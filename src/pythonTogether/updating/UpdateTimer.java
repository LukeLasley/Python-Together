package pythonTogether.updating;

import java.util.Timer;

import pythonTogether.managers.Manager;

public class UpdateTimer {
	private Timer timer;
	private Updater updater;
	
	public UpdateTimer(Updater updater){
		timer = new Timer();
		this.updater = updater;
		timer.schedule(new UpdateTask(this.updater), 0, 1000);
	}
}
