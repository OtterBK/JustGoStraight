package main;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	
	public static final String mainMS = "§f[ §b직진 §f] ";
	
	private HashMap<String, Location> startPosMap = new HashMap<String, Locaiton>();
	
	public void onEnable() {
		getServer().getLogger().info("직진 플러그인 활성화됨");
		getServer().getPluginManager().registerEvents(this, this); //이벤트 등록
	}
	
	public void onDisable() {
		getServer().getLogger().info("직진 플러그인 비활성화됨");
	}
	
	public void showHelpMessage(Player p) {
		p.sendMessage("§f");
		p.sendMessage(mainMS+"§6/직진 <x/z> §7- §f직진선을 생성합니다.");
		p.sendMessage(mainMS+"§6/직진 중지 §7- §f직진을 취소합니다.");
		p.sendMessage("§f");
	}
	
	public void createStraightLine(Player p, LineDirection direction) {
		String strUUID = p.getUniqueId().toString();
		if(startPosMap.containsKey(strUUID)){ //이미 직진선이 있다면
			p.sendMessage(mainMS+"먼저 기존 직진선을 취소해주세요. §7- §6/직진 중지");
		} else {
			
			Location pLoc = p.getLocation();
			
			startPosMap.put(strUUID, pLoc);
			
		}
	}
	
	//이벤트
	@EventHandler
	public void onCommandInput(PlayerCommandPreprocessEvent e) {
		String[] args = e.getMessage().split(" ");
		String cmd = args[0];
		
		Player p = e.getPlayer();
		
		if(cmd.equalsIgnoreCase("/직진")) {
			if(args.length == 1) {
				showHelpMessage(p);
			} else {
				if(args[1].equalsIgnoreCase("x")) {
					
				} else if(args[1].equalsIgnoreCase("z")) {
					
				} else if(args[1].equals("중지")) {
					
				}
			}
		}
	}
	
}
