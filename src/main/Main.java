package main;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	
	public static final String mainMS = "��f[ ��b���� ��f] ";
	
	private HashMap<String, Location> startPosMap = new HashMap<String, Locaiton>();
	
	public void onEnable() {
		getServer().getLogger().info("���� �÷����� Ȱ��ȭ��");
		getServer().getPluginManager().registerEvents(this, this); //�̺�Ʈ ���
	}
	
	public void onDisable() {
		getServer().getLogger().info("���� �÷����� ��Ȱ��ȭ��");
	}
	
	public void showHelpMessage(Player p) {
		p.sendMessage("��f");
		p.sendMessage(mainMS+"��6/���� <x/z> ��7- ��f�������� �����մϴ�.");
		p.sendMessage(mainMS+"��6/���� ���� ��7- ��f������ ����մϴ�.");
		p.sendMessage("��f");
	}
	
	public void createStraightLine(Player p, LineDirection direction) {
		String strUUID = p.getUniqueId().toString();
		if(startPosMap.containsKey(strUUID)){ //�̹� �������� �ִٸ�
			p.sendMessage(mainMS+"���� ���� �������� ������ּ���. ��7- ��6/���� ����");
		} else {
			
			Location pLoc = p.getLocation();
			
			startPosMap.put(strUUID, pLoc);
			
		}
	}
	
	//�̺�Ʈ
	@EventHandler
	public void onCommandInput(PlayerCommandPreprocessEvent e) {
		String[] args = e.getMessage().split(" ");
		String cmd = args[0];
		
		Player p = e.getPlayer();
		
		if(cmd.equalsIgnoreCase("/����")) {
			if(args.length == 1) {
				showHelpMessage(p);
			} else {
				if(args[1].equalsIgnoreCase("x")) {
					
				} else if(args[1].equalsIgnoreCase("z")) {
					
				} else if(args[1].equals("����")) {
					
				}
			}
		}
	}
	
}
