package pythonTogether.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Doccument {
	private String location;
	private String name;
	private String text;
	private Manager manager;
	private boolean saved;
	private String ID;

	public Doccument(String name, Manager manager){
		this.name = name;
		this.manager = manager;
		text = null;
		location = null;
		saved = true;
	}
	public String getName(){
		return name;
	}
	public String getText(){
		return text;
	}
	public String getLocation(){
		return location;
	}
	public void setLocation(String loc){
		location = loc;
	}
	public void setText(String text){
		this.text = text;
		manager.setText(name,text);
	}
	public boolean getSavedStatus(){
		return saved;
	}
	public void updateSaved(){
		saved = true;
	}
	public boolean hasChanged(){
		if(manager.getText(name).equals(text)){
			return false;
		}else{
			return true;
		}
	}
	public Map<Integer,String> getDifferences(){
		Map<Integer,String> differences = new HashMap<Integer,String>();
		String[] oldStringList = text.split("\n");
		String[] newStringList = manager.getText(name).split("\n");
		int oldStringLength = oldStringList.length;
		int newStringLength = newStringList.length;
		if(oldStringLength >= newStringLength){
			for(int i = 0; i<newStringLength; i++){
				if(!oldStringList[i].equals(newStringList[i])){
					differences.put(i,  newStringList[i]);
				}
			}
			for(int i = newStringLength; i<oldStringLength;i++){
				differences.put(i,"");
			}
		}else{
			for(int i = 0; i<oldStringLength; i++){
				if(!oldStringList[i].equals(newStringList[i])){
					differences.put(i, newStringList[i]);
				}	
			}
			for(int i = oldStringLength; i < newStringLength; i++){
				differences.put(i,newStringList[i]);
			}
		}
		saved = false;
		return differences;

	}
	public void updateSelf() {
		this.text = manager.getText(name);

	}
	public void update(Map map) {
		String[] curText = text.split("\n");
		int length = curText.length;
		String toUpdate = "";
		ArrayList<String> extra = new ArrayList<String>();
		Set<String> keys = map.keySet();
		for(String i: keys){
			int curInt = Integer.parseInt(i);
			if(curInt>= length){
				extra.add(curInt-length, (String) map.get(i));
			}else{
				curText[curInt] = (String) map.get(i);
			}
		}
		for(int i=0;i<length+extra.size();i++){
			if(i<length){
				toUpdate += curText[i] + "\n";
			}else{
				toUpdate +=extra.get(i- length) + "\n";
			}
		}
		setText(toUpdate);
		saved = false;
	}

}

