package tk.tyzoid.plugins.hungergames;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import tk.tyzoid.plugins.hungergames.lib.Data;
import tk.tyzoid.plugins.hungergames.lib.Settings;
import tk.tyzoid.plugins.hungergames.listeners.Playerlistener;


public class Hungergames extends JavaPlugin {
	public static boolean gamesongoing = false;
	public static final String pluginname = "HungerGames";
	
    private Playerlistener playerListener;
    public Settings settings = new Settings();
    public Data data = new Data(this);
    public boolean permissionsExists = false;
    public boolean useSuperperms = false;
    private static Hungergames instance = null;
    
    public Hungergames(){
    	instance = this;
    }
    
    public static void registerEvents(Listener listener){
        PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.registerEvents(listener, instance);
    }
    
    public void onDisable() {
    	playerListener.pluginClosing();
        System.out.println("[" + pluginname +"] " + pluginname + " is closing...");
    }

    public void onEnable() {
        settings.readSettings();
    	data.loadData();
        
        playerListener = new Playerlistener(this);
        
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(playerListener, this);
        
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println("[" + pluginname + "] Starting " + pluginname + " v" + pdfFile.getVersion() + "...");
    }
}