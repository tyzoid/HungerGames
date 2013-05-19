package tk.tyzoid.plugins.hungergames.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.Vector;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import tk.tyzoid.plugins.hungergames.Hungergames;


//import org.bukkit.entity.Player;

public class Data {
	private Properties gamesP = new Properties();
	private Properties invsP = new Properties();
	
	private String pluginname;
	private String[] comment;
	
	private File data;
	private File inv;
	private FileOutputStream dataOut;
	Hungergames plugin;
	
	public Data(Hungergames instance) {
		pluginname = Hungergames.pluginname;
		comment = new String[]{"[" + pluginname + "] data", "[" + pluginname + "] inventory data"};
		plugin = instance;
	}
	
	public void loadData() {
		try {
			String path = "plugins/" + pluginname;
			data = new File(path + "/data.dat");
			if (!data.exists()) {
				(new File(path)).mkdir();
				data.createNewFile();
			}
			
			inv = new File(path + "/inv.dat");
			if (!inv.exists()) {
				(new File(path)).mkdir();
				inv.createNewFile();
			}
			
			FileInputStream invsin = new FileInputStream(inv);
			FileInputStream gamesin = new FileInputStream(data);
			
			invsP.load(invsin);
			gamesP.load(gamesin);
			gamesin.close();
			
		} catch (Exception e) {
			System.out.println("[" + pluginname + "] Failed to load data. Aborting.");
			System.out.println("[" + pluginname + "] Error: " + e.toString());
		}
	}
	
	public Game getSavedGame(){
		Game game = new Game(plugin);
		
		String data = gamesP.getProperty("game1");
		if(data == null) return game;
		
		String[] sd = data.split(",");
		
		int slots = Integer.parseInt(sd[0]);
		
		for(int i = 0; i < slots; i++){
			System.out.println("[" + pluginname + "] Attempting to add location in world " + sd[i*5+1]
					+ " at coordinates " + sd[i*5+2] + ", " + sd[i*5+3] + ", " + sd[i*5+4] + ".");
			
			System.out.println("[" + pluginname + "] Adding location in world " + Bukkit.getWorld(sd[i*5+1]).getName()
					+ " at coordinates " + Double.parseDouble(sd[i*5+2]) + ", "
					+ Double.parseDouble(sd[i*5+3]) + ", " + Double.parseDouble(sd[i*5+4]) + ".");
			
			game.addStartingLocation(new Location(Bukkit.getWorld(sd[i*5+1]),
					Double.parseDouble(sd[i*5+2]),
					Double.parseDouble(sd[i*5+3]),
					Double.parseDouble(sd[i*5+4])));
			
			//if(!sd[i*5+5].equals("null")) game.addPlayer(Bukkit.getPlayer(sd[i*5+5]));
			if(!sd[i*5+5].equals("null")) game.addPlayer(sd[i*5+5]);
		}
		
		if(!sd[sd.length-1].equals("null")){
			game.setRespawnLocation(new Location(Bukkit.getWorld(sd[sd.length-4]),
					Double.parseDouble(sd[sd.length-3]),
					Double.parseDouble(sd[sd.length-2]),
					Double.parseDouble(sd[sd.length-1])));
		}
		
		return game;
	}
	
	public void pluginClosing(boolean show) {
		if (show) System.out.println("[" + pluginname + "] Saving data.");
		try {
			dataOut = new FileOutputStream(data);
			gamesP.store(dataOut, comment[0]);
			dataOut.close();
			
			dataOut = new FileOutputStream(inv);
			invsP.store(dataOut, comment[1]);
		} catch (Exception e) {
			System.out.println("[" + pluginname + "] Could not save data.");
			System.out.println("[" + pluginname + "] Error: " + e.toString());
		}
	}
	
	public void save(Player p){
		/*
		ItemStack[] inv = p.getInventory().getContents();
		String str = "";
		int i = 0;
		for(ItemStack is : inv){
			if(is == null){
				str += "null:null:null,null"; //ID:durability:amount:data
				continue;
			}
			
			str += is.getTypeId() + ":" + is.getDurability() + ":" + is.getAmount();
			if(++i < inv.length) str += ",";
		}
		
		str += "|";
		
		inv = p.getInventory().getArmorContents();
		i = 0;
		
		for(ItemStack is : inv){
			if(is == null){
				str += "null:null:null"; //ID:durability:amount
				continue;
			}
			
			str += is.getTypeId() + ":" + is.getDurability() + ":" + is.getAmount();
			if(++i < inv.length) str += ",";
		}
		*/
		invsP.setProperty(p.getName(), InventoryStringDeSerializer.InventoryToString(p.getInventory()));
	}
	
	public void loadSavedPlayer(Player player){
		if(!invsP.containsKey(player.getName())) return;
		String str = invsP.getProperty(player.getName());
		Inventory inv = InventoryStringDeSerializer.StringToInventory(str);
		player.getInventory().setContents(inv.getContents());
		/*
		String[] stra = str.split("|");
		String[] invs = stra[0].split(",");
		String[] tmp;
		Vector<ItemStack> s = new Vector<ItemStack>();
		
		for(String inv : invs){
			tmp = inv.split(":");
			if(tmp[0].equals("null")){
				s.add(null);
				continue;
			}
			s.add(new ItemStack(Integer.parseInt(tmp[0]),
					Integer.parseInt(tmp[1]),
					Short.parseShort(tmp[2])));
		}
		
		player.getInventory().setContents(getArray(s));
		*/
	}
	
	public void removePlayerInventory(Player p){
		if(playerInvSaved(p)){
			invsP.remove(p.getName());
			saveData();
		}
	}
	
	public boolean playerInvSaved(Player player){
		return invsP.containsKey(player.getName());
	}
	
	public void save(Game game){
		int slots = game.slots();
		String str = "";
		
		str += slots + ",";
		
		for(int i = 0; i < slots; i++){
			str += game.getLocation(i).getWorld().getName() + ",";
			str += game.getLocation(i).getBlockX() + ",";
			str += game.getLocation(i).getBlockY() + ",";
			str += game.getLocation(i).getBlockZ() + ",";
			
			if(i < game.numberOfPlayers()) str += game.getHgplayer(i).playername() + ",";
			else str += "null,";
		}
		if(game.getRespawnLocation() == null){
			str += "null,null,null,null";
		} else {
			str += game.getRespawnLocation().getWorld().getName() + ",";
			str += game.getRespawnLocation().getBlockX() + ",";
			str += game.getRespawnLocation().getBlockY() + ",";
			str += game.getRespawnLocation().getBlockZ();
		}
		gamesP.put("game1", str);
		
		saveData();
	}
	
	public boolean reloadData() {
		boolean result = false;
		
		try {
			FileInputStream dataSP = new FileInputStream(data);
			
			gamesP.load(dataSP);
			
			dataSP.close();
			result = true;
		} catch (Exception e) {
			result = false;
		}
		
		return result;
	}
	
	public void saveData() {
		pluginClosing(false);
	}
	
	/*private ItemStack[] getArray(Vector<ItemStack> v){
		ItemStack[] items = new ItemStack[v.size()];
		int i = 0;
		for(ItemStack item : v){
			items[i] = item;
			i++;
		}
		
		return items;
	}*/
}
