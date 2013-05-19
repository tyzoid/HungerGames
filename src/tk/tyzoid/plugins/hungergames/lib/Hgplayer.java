package tk.tyzoid.plugins.hungergames.lib;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import tk.tyzoid.plugins.hungergames.Hungergames;

public class Hgplayer implements Listener{
	private Player player;
	private boolean hasDied = false;
	private boolean hasForfieted = false;
	private boolean hasbeenteleportedout = false;
	private String playername;
	@SuppressWarnings("unused")
	private long timestamp;
	
	public Hgplayer(Player player){
		this.player = player;
		if(player != null) playername = player.getName();
		if(player == null) System.out.println("[" + Hungergames.pluginname + "] Player is null. This could pose a problem in the long run.");
		doStuff();
	}
	
	public Hgplayer(String playername){
		this.playername = playername;
		doStuff();
	}
	
	private void doStuff(){
		Hungergames.registerEvents(this);
		timestamp = System.currentTimeMillis();
	}
	
	public boolean isSameAs(String playername){
		return this.playername.equals(playername);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		if(event.getPlayer().getName().equals(this.playername)) this.player = event.getPlayer();
	}
	
	public boolean isSameAs(Player player){
		return this.player.getName().equals(player.getName());
	}

	public void die() {
		this.hasDied = true;
	}

	public boolean hasDied() {
		return hasDied;
	}
	
	public String playername(){
		if(player != null) return player.getName();
		return playername;
	}
	
	public Player getPlayer(){
		return player;
	}

	public void forfiet() {
		hasForfieted = true;
	}

	public boolean hasForfieted() {
		return hasForfieted;
	}

	public void teleportOut(boolean hasbeenteleportedout) {
		this.hasbeenteleportedout = hasbeenteleportedout;
	}

	public boolean hasBeenTeleportedOut() {
		return hasbeenteleportedout;
	}
}
