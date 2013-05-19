package tk.tyzoid.plugins.hungergames.lib;

import java.util.Vector;

import org.bukkit.Location;

public class Gameslocations {
	private Vector<Location> locs = new Vector<Location>();
	Location respawnLocation;
	
	public int nextLocation(Location loc){
		locs.add(loc);
		return locs.size()-1;
	}
	
	public void setRespawnLocation(Location loc){
		respawnLocation = loc;
	}
	
	public Location getRespawnLocation(){
		return respawnLocation;
	}
	
	public Location getLocation(int index){
		return locs.get(index);
	}
	
	public void clearLocations(){
		locs.clear();
	}
	
	public int slots(){
		return locs.size();
	}
}
