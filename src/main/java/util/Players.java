package util;

import mindustry.gen.Player;
import data.TempData;


public class Players {
	public final Player player;
	public final TempData data;
	public final String[] rest;
	public final boolean found;
	
	
	private Players(TempData d, String args) {
		String[] test = args.strip().split(" ");
		
		if (d == null) this.player = null;
		else this.player = d.player;
		this.data = d;
		this.rest = test.length == 1 && test[0].isBlank() ? new String[]{} : test;
		this.found = this.player != null;
	}
	
	public static void errNotOnline(Player player) {
		err(player, "Player not connected or doesn't exist!");
	}

	public static void errPermDenied(Player player) {
		err(player, "You don't have the permission to use arguments!");
	}
	
	public static boolean errFilterAction(String action, util.filter.FilterSearchReponse filter, boolean type) {
		if (!filter.type.onlyPlayers()) {
			if (type) err(filter.trigger, "@ is only for players!", action);
			else err(filter.trigger, "Can @ only players!", action);
			return true;
		} else return false;
	}
	
	public static void err(Player player, String fmt, Object... msg) {
    	player.sendMessage("[scarlet]Error: " + Strings.format(fmt, msg));
    }
    public static void info(Player player, String fmt, Object... msg) {
    	player.sendMessage("Info: " + Strings.format(fmt, msg));
    }
    public static void warn(Player player, String fmt, Object... msg) {
    	player.sendMessage("[gold]Warning: []" + Strings.format(fmt, msg));
    }
    
    //check the player if admin 
    public static boolean adminCheck(Player player) {
    	if(!player.admin()){
    		player.sendMessage("[scarlet]This command is only for admins!");
            return false;
    	} else return true;
    }
    
    public static Players findByName(String[] args) { return findByName(String.join(" ", args) + " "); }
    public static Players findByName(String arg) {
    	String args = arg + " ";
    	TempData target = TempData.find(p -> args.startsWith(p.realName + " "));
    	byte type = 0;
    	
    	if (target == null) {
    		target = TempData.find(p -> args.startsWith(p.noColorName + " "));
    		type = 1;
    	}
    	if (target == null) {
    		target = TempData.find(p -> args.startsWith(p.stripedName + " "));
    		type = 2;
    	}
    	
    	if (target == null) return new Players(null, args);
    	else return new Players(target, args.substring((type == 0 ? target.realName : type == 1 ? target.noColorName : target.stripedName).length()));
    }
    
    public static Players findByID(String[] args) { return findByID(String.join(" ", args) + " "); }
    public static Players findByID(String arg) {
    	String args = arg + " ";
    	TempData target = TempData.find(p -> args.startsWith(p.player.uuid() + " "));
    	
    	return target == null ? new Players(null, args) : new Players(target, args.substring(target.player.uuid().length()));
    }
    
    public static Players findByNameOrID(String[] args) {
    	Players target = Players.findByName(args);
    	return target == null ? Players.findByID(args) : target;
    }
    
    public static void tpPlayer(mindustry.gen.Unit unit, int x, int y) {
    	arc.util.async.Threads.daemon(() -> {
    		int limit = 30, range = 3;
        	Player player = unit.getPlayer();
        	
        	while ((unit.x < x-range || unit.x > x+range) && (unit.y < y-range || unit.y > y+range)) {
        		if (limit-- == 0) break;
    			else {
        			try { 
	        			unit.set(x, y);
	        			if (player != null) {
		        			player.set(x, y);
			            	mindustry.gen.Call.setPosition(player.con, x, y);
	        			}
		            	Thread.sleep(100);
        			} catch (InterruptedException e) { break; }
    			}
        	};
    	});
    }
}