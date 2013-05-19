package tk.tyzoid.plugins.hungergames.lib;

import java.util.Vector;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import tk.tyzoid.plugins.hungergames.Hungergames;

public class Game {
	private Vector<Hgplayer> players = new Vector<Hgplayer>();
	private Vector<Player> spectators = new Vector<Player>();
	private Vector<Integer> deaths = new Vector<Integer>();
	private Vector<Integer> forfeits = new Vector<Integer>();
	
	private Gameslocations gl = new Gameslocations();
	private boolean gamesOnGoing = false;
	String pluginname;
	Long dispTime;
	Hungergames plugin;
	
	public Game(Hungergames instance){
		pluginname = Hungergames.pluginname;
		plugin = instance;
		
		dispTime = System.currentTimeMillis() + Integer.parseInt(plugin.settings.getProperty("options-timedelay"))*1000;
		
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){
			public void run(){
				if(System.currentTimeMillis() < dispTime || !gamesOnGoing) return;
				
				broadcast(getStatistics());
				dispTime = System.currentTimeMillis() + Integer.parseInt(plugin.settings.getProperty("options-timedelay"))*1000;
			}
		}, 0L, 100L);
	}
	
	public int addStartingLocation(Location loc){
		return gl.nextLocation(loc);
	}
	
	public void setRespawnLocation(Location loc){
		gl.setRespawnLocation(loc);
	}
	
	public void startgames() {
		gamesOnGoing = true;
	}
	
	public boolean areGamesOnGoing() {
		return gamesOnGoing;
	}
	
	public void newMatch(){
		players = new Vector<Hgplayer>();
		deaths = new Vector<Integer>();
		gamesOnGoing = false;
		
		dispTime = System.currentTimeMillis() + Integer.parseInt(plugin.settings.getProperty("options-timedelay"))*1000;
	}
	
	public int addPlayer(Player player){
		if(this.getPlayerId(player) != -1) return -1;
		players.add(new Hgplayer(player));
		return gl.slots() - players.size();
	}
	
	public int addPlayer(String playername){
		if(this.getPlayerId(playername) != -1) return -1;
		players.add(new Hgplayer(playername));
		return gl.slots() - players.size();
	}
	
	public int numberOfPlayers(){
		return players.size();
	}
	
	public int emptySlots(){
		return gl.slots() - players.size();
	}
	
	public boolean emptySlotsAvailable(){
		return emptySlots() > 0;
	}
	
	public Player getPlayer(int index){
		return players.get(index).getPlayer();
	}
	
	public Hgplayer getHgplayer(int index){
		return players.get(index);
	}
	
	public int getPlayerId(String playername){
		for(int i = 0; i < players.size(); i++){
			if(players.get(i).isSameAs(playername)){
				return i;
			}
		}
		return -1;
	}
	
	public int getPlayerId(Player player){
		for(int i = 0; i < players.size(); i++){
			if(players.get(i).isSameAs(player)){
				return i;
			}
		}
		return -1;
	}
	
	public void clearPlayers(){
		players.clear();
	}
	
	public Location getRespawnLocation(){
		return gl.getRespawnLocation();
	}
	
	public Location getLocation(int index){
		return gl.getLocation(index);
	}
	
	public void clearLocations(){
		gl.clearLocations();
	}
	
	public int slots(){
		return gl.slots();
	}
	
	public int playersLeft(){
		int pl = 0;
		for(Hgplayer p : players){
			if(!p.hasDied() && !p.hasForfieted())
				pl++;
		}
		return pl;
	}
	
	public void playerDied(Player player){
		int index = getPlayerId(player);
		getHgplayer(index).die();
		deaths.add(index);
		if(playersLeft() > 1){
			broadcast("§c[" + pluginname + "] " + player.getName() + " has died. There are " + playersLeft() + " players left.");
			broadcast(getStatistics());
		} else {
			GameOver();
		}
	}
	
	public void playerForfeited(Player player){
		int index = getPlayerId(player);
		getHgplayer(index).forfiet();
		forfeits.add(index);
		if(playersLeft() > 1){
			broadcast("§c[" + pluginname + "] " + player.getName() + " has Forfeited. There are " + playersLeft() + " players left.");
			broadcast(getStatistics());
		} else {
			GameOver();
		}
	}
	
	
	
	private void GameOver(){
		if(playersLeft() == 1){
			
			Vector<Player> alivep = getAlivePlayers();
			Player alive = alivep.firstElement();
			Bukkit.getServer().broadcastMessage("§a[" + pluginname + "] A §6Victor §ahas emerged.");
			Bukkit.getServer().broadcastMessage("§a[" + pluginname + "] Join the victor's circle, §6" + alive.getName() + "§a!");
			broadcast("§a[" + pluginname + "] Top places:");
			broadcast("§a[" + pluginname + "] §61. " + alive.getName());
			for(int i = 1; i < Math.min(numberOfPlayers(), 4); i++){
				broadcast("§a[" + pluginname + "] " + (i+1) +  ". " + getPlayer(numberOfPlayers()-i).getName());
			}
			
			if(getRespawnLocation() != null) alive.teleport(this.getRespawnLocation().add(0, 1, 0));
			newMatch();
		} else if(players.size() != 0) {
			//What just happened?
			broadcast("§c[" + pluginname + "] An error was encountered. Unfortunately, the game is lost, and you need to restart. Type §c/hg join §a to join.");
			newMatch();
		}
	}
	
	public String[] getStatistics(){
		Vector<String> lines = new Vector<String>();
		Vector<Player> tmpplayers = getAlivePlayers();
		lines.add("§a[" + pluginname + "] ------ Hunger games standings ------");
		lines.add("§aAlive Players:");
		
		int i = 0;
		String tmp = "";
		for(Player p : tmpplayers){
			if(tmp.length() > 0){
				tmp += ", ";
			}
			tmp += p.getName();
			if(++i > 3){
				lines.add("§a" + tmp);
				i = 0;
				tmp = "";
			}
		}
		
		if(tmp.length() > 0){
			lines.add("§a" + tmp);
		}
		
		if(i == 0){
			lines.add("§8Nobody.");
		}
		
		tmpplayers = getDeadPlayers();
		
		lines.add("§cDead Players:");
		
		i = 0;
		tmp = "";
		for(Player p : tmpplayers){
			if(tmp.length() > 0){
				tmp += ", ";
			}
			tmp += p.getName();
			if(++i > 3){
				lines.add("§c" + tmp);
				i = 0;
				tmp = "";
			}
		}
		
		if(tmp.length() > 0){
			lines.add("§c" + tmp);
		}
		
		
		if(i == 0){
			lines.add("§8Nobody.");
		}
		
		tmpplayers = getForfeitedPlayers();
		
		lines.add("§7Forfeited Players:");
		
		i = 0;
		tmp = "";
		for(Player p : tmpplayers){
			if(tmp.length() > 0){
				tmp += ", ";
			}
			tmp += p.getName();
			if(++i > 3){
				lines.add("§7" + tmp);
				i = 0;
				tmp = "";
			}
		}
		
		if(tmp.length() > 0){
			lines.add("§7" + tmp);
		}
		
		if(i == 0){
			lines.add("§8Nobody.");
		}
		
		return (String[]) lines.toArray(new String[lines.size()]);
	}
	
	public Vector<Player> getAlivePlayers(){
		Vector<Player> aliveplayers = new Vector<Player>();
		for(Hgplayer p : players){
			if(!p.hasDied() && !p.hasForfieted())
				aliveplayers.add(p.getPlayer());
		}
		return aliveplayers;
	}
	
	public Vector<Player> getDeadPlayers(){
		Vector<Player> deadplayers = new Vector<Player>();
		for(Hgplayer p : players){
			if(p.hasDied())
				deadplayers.add(p.getPlayer());
		}
		return deadplayers;
	}
	
	public Vector<Player> getForfeitedPlayers(){
		Vector<Player> forfeitedplayers = new Vector<Player>();
		
		for(Hgplayer p : players){
			if(p.hasForfieted())
				forfeitedplayers.add(p.getPlayer());
		}
		return forfeitedplayers;
	}
	
	public void broadcast(String message){
		for(Hgplayer p : players){
			p.getPlayer().sendMessage(message);
		}
		for(Player p : spectators){
			p.sendMessage(message);
		}
	}
	
	public void broadcast(String[] messages){
		for(Hgplayer p : players){
			p.getPlayer().sendMessage(messages);
		}
		for(Player p : spectators){
			p.sendMessage(messages);
		}
	}
	
	public void broadcastOutside(String message){
		for(Player p : Bukkit.getServer().getOnlinePlayers()){
			if(this.getPlayerId(p) == -1) p.sendMessage(message);
		}
	}
	
	public void spectate(Player player){
		if(spectators.indexOf(player) == -1){
			spectators.add(player);
		}
	}
	public void stopSpectating(Player player){
		spectators.remove(player);
	}
}
