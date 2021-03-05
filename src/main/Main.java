package main;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	
	public static final String mainMS = "��f[ ��b���� ��f] ";
	public static Plugin pluginInstance;
	
	private HashMap<String, StraightLine> lineMap = new HashMap<String, StraightLine>();
	
	public void onEnable() {
		this.pluginInstance = this;
		
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
		if(lineMap.containsKey(strUUID)){ //�̹� �������� �ִٸ�
			p.sendMessage(mainMS+"���� ���� �������� ������ּ���. ��7- ��6/���� ����");
		} else {
			StraightLine newLine = new StraightLine(p, direction); //���ο� ���� ����
			lineMap.put(strUUID, newLine);
			p.sendMessage(mainMS+"������ �����մϴ�.");
		}
	}
	
	public void removeStraightLine(Player p) {
		String strUUID = p.getUniqueId().toString();
		if(lineMap.containsKey(strUUID)){ //�̹� �������� �ִٸ�
			StraightLine line = lineMap.get(strUUID);
			line.destroy();
			lineMap.remove(strUUID);
			p.sendMessage(mainMS+"�������� �����߽��ϴ�.");
		} else {
			p.sendMessage(mainMS+"������ �������� �������� �ʽ��ϴ�.");
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
					createStraightLine(p, LineDirection.X);
				} else if(args[1].equalsIgnoreCase("z")) {
					createStraightLine(p, LineDirection.Z);
				} else if(args[1].equals("����")) {
					removeStraightLine(p);
				}
			}
		}
	}
	
}
