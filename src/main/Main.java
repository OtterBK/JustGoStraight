package main;

import java.io.File;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	
	public static final String mainMS = "§f[ §b직진 §f] ";
	public static Plugin pluginInstance;
	
	private HashMap<String, StraightLine> lineMap = new HashMap<String, StraightLine>();

	public void onEnable() {
		this.pluginInstance = this;

		getServer().getLogger().info("직진 플러그인 활성화됨");
		getServer().getPluginManager().registerEvents(this, this); //이벤트 등록

		File file = new File(getDataFolder() + File.separator + "config.yml");
		if (!file.exists()) { //Config없으면 기본 config
			this.saveDefaultConfig();
		}

	}
	
	public void onDisable() {
		getServer().getLogger().info("직진 플러그인 비활성화됨");
	}
	
	public void showHelpMessage(Player p) {
		p.sendMessage("§f");
		p.sendMessage(mainMS+"§6/직진 <x/z> <행동반경> §7- §f직진선을 생성합니다.");
		p.sendMessage(mainMS+"§6/직진 중지 §7- §f직진을 취소합니다.");
		p.sendMessage("§f");
	}
	
	public void createStraightLine(Player p, LineDirection direction, double radius) {
		String strUUID = p.getUniqueId().toString();
		if(lineMap.containsKey(strUUID)){ //이미 직진선이 있다면
			p.sendMessage(mainMS+"먼저 기존 직진선을 취소해주세요. §7- §6/직진 중지");
		} else {
			StraightLine newLine = new StraightLine(p, direction, radius); //새로운 라인 생성
			lineMap.put(strUUID, newLine);
			p.sendMessage(mainMS+"직진을 시작합니다.");
		}
	}
	
	public void removeStraightLine(Player p) {
		String strUUID = p.getUniqueId().toString();
		if(lineMap.containsKey(strUUID)){ //이미 직진선이 있다면
			StraightLine line = lineMap.get(strUUID);
			line.destroy();
			lineMap.remove(strUUID);
			p.sendMessage(mainMS+"직진선을 삭제했습니다.");
		} else {
			p.sendMessage(mainMS+"삭제할 직진선이 존재하지 않습니다.");
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

				if(args[1].equals("중지")) {
					removeStraightLine(p);
				} else{

					double radius = 0.5;

					try{
						radius = Double.parseDouble(args[2]); //행동반경 가져오기
					}catch (Exception except) {
						p.sendMessage(mainMS + "행동반경 값은 숫자만 입력해주세요. (소수 가능)");
						return;
					}

					if(radius >= 15){
						p.sendMessage(mainMS + " 최대값은 15입니다.");
						return;
					}

					if(args[1].equalsIgnoreCase("x")) {
						createStraightLine(p, LineDirection.X, radius);
					} else if(args[1].equalsIgnoreCase("z")) {
						createStraightLine(p, LineDirection.Z, radius);
					}
				}
			}
		}
	}
	
}
