package pythonTogether.updating;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import pythonTogether.managers.Doccument;
import pythonTogether.managers.Manager;

@SuppressWarnings("rawtypes")
public class Updater {
	private Manager manager;
	private int currentTimeStamp;
	private HashMap<String, Doccument> doccuments;
	
	public Updater(Manager manager, HashMap<String, Doccument> doccuments){
		this.manager = manager;
		currentTimeStamp = 0;
		this.doccuments = doccuments;
	}


	public void sendUpdate(){
		ArrayList<Map> changes = new ArrayList<Map>();
		ArrayList<String> changedDocs = new ArrayList<String>();
		Iterator it = doccuments.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry pair = (Map.Entry)it.next();
			Doccument curDoc = (Doccument) pair.getValue();
			if(curDoc.hasChanged()){
				changes.add(curDoc.getDifferences());
				changedDocs.add(curDoc.getName());
				curDoc.updateSelf();
			}
		}
		if(!changes.isEmpty()){
			currentTimeStamp++;
			manager.sendDoccumentUpdate(changedDocs,changes,currentTimeStamp);
		}
	}

	public boolean recieveUpdate(ArrayList<String> changedDocs,ArrayList<Map> changes, int timeStamp){
		if(currentTimeStamp < timeStamp){
			currentTimeStamp = timeStamp;
			for(int i = 0; i< changedDocs.size();i++){
				String curChangedDocName = changedDocs.get(i);
				if(doccuments.containsKey(curChangedDocName)){
					Doccument toChange = doccuments.get(curChangedDocName);
					toChange.update(changes.get(i));
				}else{
					System.out.println("need to decide what to do here");
				}
			}
			return true;
		}else{
			return false;
		}
	}

}
