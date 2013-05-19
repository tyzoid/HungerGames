package tk.tyzoid.plugins.hungergames.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Properties;

import tk.tyzoid.plugins.hungergames.Hungergames;

public class Settings {
	private Properties props = new Properties();
	
	private final HashMap<String, String> settingsHolder = new HashMap<String, String>();
	private String pluginname;
	
	public Settings(){
		this.pluginname = Hungergames.pluginname;
	}
	
	public void readSettings(){
		try{
			String path = "plugins/Template";
			File propertiesFile = new File(path + "/Template.properties");
    		if(!propertiesFile.exists()){
    			(new File(path)).mkdir();
    			propertiesFile.createNewFile();
    		}
		    
			FileInputStream propertiesStream = new FileInputStream(propertiesFile);
			
			props.load(propertiesStream);
			System.out.println("[" + pluginname + "] Properties loaded.");
			propertiesStream.close();
			
			loadProperty("options-timedelay", "120");
			loadProperty("options-blocks-allowed", "18,39,40,99,100,106");
			//loadProperty("", "");
			
			verifySettings();
			
			FileOutputStream propertiesOutputStream = new FileOutputStream(propertiesFile);
			props.store(propertiesOutputStream, "");
		} catch(Exception e){
			System.out.println("[" + pluginname + "] Failed to load properties. Aborting.");
			System.out.println("[" + pluginname + "] Error: " + e.toString());
		}
	}

	public String getProperty(String property){
		return settingsHolder.get(property);
	}
	
	public void reloadData(){
		readSettings();
	}
	
	private void loadProperty(String property, String defaultValue){
		settingsHolder.put(property, lProperty(property, defaultValue));
	}
	
	private String lProperty(String property, String defaultValue){
		String currentProperty;
		currentProperty = props.getProperty(property);
		String value;
    	if(currentProperty == null){
    		System.out.println("[" + pluginname + "] Property not found: " + property + ". Resetting to: " + defaultValue);
    		props.setProperty(property, defaultValue);
    		value = defaultValue;
    	} else {
    		value = currentProperty;
    	}
    	return value;
	}
	
	private boolean verifyInteger(String s){
		try{
			Integer.parseInt(s);
			return true;
		} catch(NumberFormatException e){
			return false;
		}
	}
	
	private boolean verifyIntegerList(String s, String delim){
		boolean result = true;
		String[] tmp = s.split(delim);
		
		for(int i = 0; i < tmp.length && result; i++){
			result = result && verifyInteger(tmp[i]);
		}
		
		return result;
	}

	private void verifySettings() {
		if(!verifyInteger(getProperty("options-timedelay"))){
			setCProperty("options-timedelay", "120");
		}
		
		if(!verifyIntegerList(getProperty("options-blocks-allowed"), ",")){
			setCProperty("options-blocks-allowed", "18,39,40,99,100,106");
		}
	}
	
	private void setCProperty(String property, String value){
		props.setProperty(property, value);		
	}
}