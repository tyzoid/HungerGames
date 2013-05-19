package tk.tyzoid.plugins.hungergames.listeners;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import tk.tyzoid.plugins.hungergames.Hungergames;
import tk.tyzoid.plugins.hungergames.lib.Game;
import tk.tyzoid.plugins.hungergames.lib.Hungergamestool;

public class Playerlistener implements Listener {
	Hungergames plugin;
	String pluginname;
	private final HashMap<String, Hungergamestool> tools = new HashMap<String, Hungergamestool>();
	private Game game;
	public Playerlistener(Hungergames instance){
		this.plugin = instance;
		this.pluginname = Hungergames.pluginname;
		game = new Game(plugin);
		pluginLoading();
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		String[] split = event.getMessage().split(" ");
		//String command = event.getMessage();
		Player player = event.getPlayer();
		
		if(split[0].equalsIgnoreCase("/hg")){
			if(split.length == 1){
				//TODO: output help menu
				if(player.hasPermission("hungergames.tool")) player.sendMessage("§a/hg tool §b[start/respawn] - §eSets starting locations/respawn location with whatever you have in your hand. ");
				if(player.hasPermission("hungergames.tool")) player.sendMessage("");
				if(player.hasPermission("hungergames.tool")) player.sendMessage("");
				if(player.hasPermission("hungergames.tool")) player.sendMessage("");
				if(player.hasPermission("hungergames.tool")) player.sendMessage("");
				if(player.hasPermission("hungergames.tool")) player.sendMessage("");
			} else {
				if(split[1].equalsIgnoreCase("tool")){
					if(!player.hasPermission("hungergames.tool")){
						player.sendMessage("§c[" + pluginname + "] You do not have permission to do that");
						event.setCancelled(true);
						return;
					}
					if(tools.containsKey(player.getName().toLowerCase())){
						player.sendMessage("§a[" + pluginname + "] Tool un-bound");
						tools.remove(player.getName().toLowerCase());
						event.setCancelled(true);
						return;
					}
					
					if(split.length == 2 || split[2].equalsIgnoreCase("start")){
						player.sendMessage("§a[" + pluginname + "] Player starting locations tool bound to " + player.getItemInHand().getType().toString().toLowerCase());
						tools.put(player.getName().toLowerCase(), new Hungergamestool(player.getItemInHand().getTypeId(), 0));
						event.setCancelled(true);
					} else if(split[2].equalsIgnoreCase("respawn")) {
						player.sendMessage("§a[" + pluginname + "] Player respawn location tool bound to " + player.getItemInHand().getType().toString().toLowerCase());
						tools.put(player.getName().toLowerCase(), new Hungergamestool(player.getItemInHand().getTypeId(), 1));
						event.setCancelled(true);
					}
				} else if(split[1].equalsIgnoreCase("join")) {
					if(!player.hasPermission("hungergames.join")){
						player.sendMessage("§c[" + pluginname + "] You don't have permission to do that.");
						event.setCancelled(true);
						return;
					}
					if(game.emptySlotsAvailable() && !game.areGamesOnGoing()){
						int pos = game.addPlayer(player);
						if(pos == -1){
							player.sendMessage("§c[" + pluginname + "] You have already joined the match.");
							event.setCancelled(true);
							return;
						}
						player.teleport(game.getLocation(pos).add(0, 1, 0));
						player.sendMessage("§a[" + pluginname + "] You have joined the hunger games. Please wait for an admin to start the match.");
						plugin.data.save(player);
						player.getInventory().clear();
						player.updateInventory();
						event.setCancelled(true);
					} else if(!game.emptySlotsAvailable()){
						player.sendMessage("§a[" + pluginname + "] There is no room in the upcoming match. Please wait for the next one.");
						event.setCancelled(true);
					} else {
						player.sendMessage("§a[" + pluginname + "] There is currently a match in progress. Please wait for the next one.");
						event.setCancelled(true);
					}
				} else if(split[1].equalsIgnoreCase("clear")) {
					event.setCancelled(true);
					if(!player.hasPermission("hungergames.clear")){
						player.sendMessage("§c[" + pluginname + "] You don't have permission to do that.");
						return;
					}
					game.clearLocations();
					player.sendMessage("§a[" + pluginname + "] Player starting locations cleared.");
				} else if(split[1].equalsIgnoreCase("start")){
					event.setCancelled(true);
					if(!player.hasPermission("hungergames.start")){
						player.sendMessage("§c[" + pluginname + "] You don't have permission to do that.");
						return;
					} else if(game.numberOfPlayers() < 2){
						player.sendMessage("§c[" + pluginname + "] There aren't enough players to start a game.");
						return;
					} else if(game.areGamesOnGoing()){
						player.sendMessage("§c[" + pluginname + "] Games are already ongoing.");
						return;
					}
					game.broadcastOutside("§a[" + pluginname + "] A hunger games match is starting. Spectate with §c/hg spectate");
					game.broadcast("§a[" + pluginname + "] Happy Hunger Games.");
					game.broadcast("§a[" + pluginname + "] May the odds be ever in your favor.");
					game.startgames();
				} else if(split[1].equalsIgnoreCase("spectate")){
					if(player.hasPermission("hungergames.spectate")){
						game.spectate(player);
						player.sendMessage("§a[" + pluginname + "] You are now spectating. Type §c/hg ss §ato stop spectating.");
					} else {
						player.sendMessage("§c[" + pluginname + "] You don't have permission to do that.");
					}
					event.setCancelled(true);
				} else if(split[1].equalsIgnoreCase("stopspectating") || split[1].equalsIgnoreCase("ss")){
					if(player.hasPermission("hungergames.spectate")){
						game.stopSpectating(player);
					} else {
						player.sendMessage("§c[" + pluginname + "] You don't have permission to do that.");
					}
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		Player player = event.getPlayer();
		String playername = player.getName().toLowerCase();
		if(!tools.containsKey(playername)) return;
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK){
			if(tools.get(player.getName().toLowerCase()).id != player.getItemInHand().getTypeId()) return;
			
			if(tools.get(playername).type == 0){
				int pl = game.addStartingLocation(event.getClickedBlock().getLocation().add(.5, 0, .5));
				player.sendMessage("§a[" + pluginname + "] Starting point for player " + (pl+1) + " is set.");
				event.setCancelled(true);
			} else if(tools.get(playername).type == 1){
				game.setRespawnLocation(event.getClickedBlock().getLocation().add(.5, 0, .5));
				player.sendMessage("§a[" + pluginname + "] Player respawn point set.");
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event){
		int index = game.getPlayerId(event.getEntity());
		if(index == -1 || !game.areGamesOnGoing()) return;
		game.playerDied(event.getEntity());
	}
	
	public void onEntityDamage(EntityDamageEvent event){
		if(!(event.getEntity() instanceof Player)) return;
		Player p = (Player)event.getEntity();
		
		int index = game.getPlayerId(p);
		if(index == -1 || game.areGamesOnGoing()) return;
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event){
		int index = game.getPlayerId(event.getPlayer());
		if(index == -1 || game.areGamesOnGoing()) return;
		Location loc = event.getTo();
		Location loc1 = game.getLocation(index);
		
		if(loc.getX() == loc1.getX() && loc.getZ() == loc1.getZ() 
				&& loc.getWorld().getName().equals(loc1.getWorld().getName())
				&& loc.getY()>loc1.getY() && loc1.getY()+2>loc.getY()) return;
		
		loc.setX(game.getLocation(index).getX());
		loc.setY(game.getLocation(index).getY()+1);
		loc.setZ(game.getLocation(index).getZ());
		loc.setWorld(game.getLocation(index).getWorld());
		
		event.setFrom(loc);
		event.setTo(loc);
		event.getPlayer().teleport(loc);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event){
		int index = game.getPlayerId(event.getPlayer());
		if(index == -1 || !game.areGamesOnGoing()) return;
		game.getHgplayer(index).forfiet();
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event){
		int index = game.getPlayerId(event.getPlayer());
		if(index == -1 || !game.getHgplayer(index).hasForfieted()
				|| game.getHgplayer(index).hasDied() || game.getRespawnLocation() == null) return;
		if(!game.areGamesOnGoing()){
			event.setRespawnLocation(game.getLocation(index));
			return;
		}
		if(!plugin.data.playerInvSaved(event.getPlayer())) return;
		System.out.println("[" + pluginname + "] Inventory Restored.");
		plugin.data.loadSavedPlayer(event.getPlayer());
		if(game.getRespawnLocation() != null) event.getPlayer().teleport(game.getRespawnLocation().add(0, 1, 0));
		plugin.data.removePlayerInventory(event.getPlayer());
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		int index = game.getPlayerId(event.getPlayer());
		if(index == -1 || !game.getHgplayer(index).hasForfieted()
				|| !game.getHgplayer(index).hasDied() || game.getRespawnLocation() == null) return;
		if(!plugin.data.playerInvSaved(event.getPlayer())) return;
		plugin.data.loadSavedPlayer(event.getPlayer());
		if(game.getRespawnLocation() != null) event.getPlayer().teleport(game.getRespawnLocation().add(0, 1, 0));
		plugin.data.removePlayerInventory(event.getPlayer());
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		int index = game.getPlayerId(event.getPlayer());
		if(index == -1 || game.getHgplayer(index).hasForfieted()
				|| game.getHgplayer(index).hasDied()) return;
		
		if(game.areGamesOnGoing()){
			String[] ids = plugin.settings.getProperty("options-blocks-allowed").split(",");
			for(String s : ids){
				if(Integer.parseInt(s) == event.getBlock().getTypeId()){
					return;
				}
			}
		}
		
		event.getPlayer().sendMessage("§c[" + pluginname + "] You aren't allowed to break that.");
		event.setCancelled(true);
	}
	
	public void pluginClosing(){
		plugin.data.save(game);
	}
	
	public void pluginLoading(){
		game = plugin.data.getSavedGame();
	}
}