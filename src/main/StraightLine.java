package main;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_12_R1.EntityShulker;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_12_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_12_R1.PlayerConnection;

public class StraightLine { //������

	private LineDirection direction; //�������
	private Player owner; //������ ������
	
	private Location nowPos; //���� ��ġ(������)
	private Listener myEvent;
	
	private ArrayList<EntityShulker> glowingBlockList = new ArrayList<EntityShulker>();
	private int timerID = -1;
	
	private ArrayList<Material> exceptTypes = new ArrayList<Material>();
	
	public StraightLine(Player owner, LineDirection direction) {
		
		this.owner = owner;
		
		this.nowPos = owner.getLocation();
		nowPos.setX(nowPos.getX() < 0 ? (int)nowPos.getX() - 0.5 : (int)nowPos.getX() + 0.5); //�߾� ����
		nowPos.setZ(nowPos.getZ() < 0 ? (int)nowPos.getZ() - 0.5 : (int)nowPos.getZ() + 0.5); //�߾� ����
		
		this.direction = direction;
		
		owner.teleport(nowPos); //�߾��������� �̵�
		
		myEvent= new StraightEvent(); //�̺�Ʈ ����
		
		Main.pluginInstance.getServer().getPluginManager().registerEvents(myEvent, Main.pluginInstance);
		
		exceptTypes.add(Material.AIR);
		exceptTypes.add(Material.YELLOW_FLOWER);
		exceptTypes.add(Material.CHORUS_FLOWER);
		exceptTypes.add(Material.RED_ROSE);
		exceptTypes.add(Material.CHORUS_PLANT);
		exceptTypes.add(Material.DOUBLE_PLANT);
		exceptTypes.add(Material.LADDER);
		exceptTypes.add(Material.RED_MUSHROOM);
		exceptTypes.add(Material.SAPLING);
		exceptTypes.add(Material.GRASS_PATH);
		exceptTypes.add(Material.LONG_GRASS);
		exceptTypes.add(Material.TORCH);
		exceptTypes.add(Material.VINE);
		exceptTypes.add(Material.WHEAT);
		exceptTypes.add(Material.SEEDS);
		exceptTypes.add(Material.CHORUS_PLANT);
	
		//glowingBlockTimer();
		particleTimer();
	}
	
	public Location getNowPos() {
		return this.nowPos;
	}
	
	public LineDirection direction() {
		return this.direction;
	}
	
	private EntityShulker sendGlowingBlock(Plugin server, Player p, Location loc) { // �� �������ϱ�
		PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
		// Scoreboard nmsScoreBoard = ((CraftScoreboard)
		// Bukkit.getScoreboardManager().getMainScoreboard()).getHandle();

		EntityShulker shulker = new EntityShulker(((CraftWorld) loc.getWorld()).getHandle());
		shulker.setLocation(loc.getX(), loc.getY(), loc.getZ(), 0, 0);
		shulker.setFlag(6, true); // Glow
		shulker.setFlag(5, true); // Invisibility

		PacketPlayOutSpawnEntityLiving spawnPacket = new PacketPlayOutSpawnEntityLiving(shulker);
		connection.sendPacket(spawnPacket);

		return shulker;
	}

	private void removeGlowingBlock(Player p, EntityShulker shulker) {
		PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;

		PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(shulker.getId());
		connection.sendPacket(destroyPacket);

	}
	
	public void clearGlowingBlock() { //������ ǥ�ÿ� ������ ��� �ʱ�ȭ 
		for(EntityShulker gb : glowingBlockList) {
			removeGlowingBlock(this.owner, gb);
		}
	}
	
	public void showLineAsBlock(Location l) { //������ ������ ���� �˷���
		EntityShulker gb = sendGlowingBlock(Main.pluginInstance, this.owner, l);
		this.glowingBlockList.add(gb);
	}
	
	//�����̼��� ���� ��ġ
	private Location getGroundLocation(Location l) {
		Location tmpL = new Location(l.getWorld(), l.getX(), l.getY(), l.getZ());
		while(tmpL.getY() > 0) {
			
			Material blockType = tmpL.getBlock().getType();
	
			if(exceptTypes.contains(blockType)) { //���� ��
				tmpL.setY(tmpL.getY()-1);
			} else {
				break;
			}
			
		}
		return tmpL.add(0,0f,0);
	}
	
	//�����̼��� ���尡������� ��ġ
	public Location getUpLocation(Location l) {
		Location tmpL = new Location(l.getWorld(), l.getX(), l.getY(), l.getZ());
		while(true) {
			if(tmpL.getY() >= 255) {
				return l;
			} else {
				
				Material blockType = tmpL.getBlock().getType();
				
				if(exceptTypes.contains(blockType)) { //���� ��
					tmpL.setY(tmpL.getY()+1);
				} else {
					break;
				}
				
			}
		}
		return tmpL.add(0,0f,0);
	}
	
	public void destroy() { //������ ����
		this.owner = null;
	}
	
	public void glowingBlockTimer() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.pluginInstance, new Runnable() {
			public void run() {
    			clearGlowingBlock();
    			
    			Vector vec = new Vector(0,0,0);
				
    			if(direction == LineDirection.X) {//�̵������� x���̶��
    				vec.setX(vec.getX() + 1);
    			} else { //z���̶��
    				vec.setZ(vec.getZ() + 1);
    			}
    			 
    			Location pos1 = nowPos.clone().add(vec); //���� ��
    			Location pos2 = nowPos.clone().subtract(vec); //�Ĺ� ��
    			
    			pos1 = getGroundLocation(pos1).subtract(0,2,0); //�� ��ġ
    			pos2 = getGroundLocation(pos2).subtract(0,2,0); //�� ��ġ
    			
    			showLineAsBlock(pos1); //��ġ ǥ��
    			showLineAsBlock(pos2);
			}
		}, 0l, 2l);
	}
	
	public void particleTimer() {
		timerID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.pluginInstance, new Runnable() {
			public void run() {
				if(owner == null) { 
					if(timerID != -1)
						Bukkit.getScheduler().cancelTask(timerID);
				}
				else {
					clearGlowingBlock();
	    			
	    			Vector vec = new Vector(0,0,0);
	    			Vector side = new Vector(0,0,0);
					
	    			if(direction == LineDirection.X) {//�̵������� x���̶��
	    				vec.setX(vec.getX() + 1);
	    				side.setZ(vec.getZ() + 0.45);
	    			} else { //z���̶��
	    				vec.setZ(vec.getZ() + 1);
	    				side.setX(vec.getX() + 0.45);
	    			}    			 
	    			
	    			for(int i = -30; i <= 30; i++) { //���� ǥ��
	    				if(i == 0) continue; //�ڱ� ��ġ�� �н�
	    				
	    				Location pos = nowPos.clone().add(vec.clone().multiply(i)); //���� ��
	    				
	    				Material blockType = pos.getBlock().getType();
	    				
	    				if(exceptTypes.contains(blockType)) { //�ڽ��� ���ӿ� ���� �ʴٸ�
	    					pos = getGroundLocation(pos); //���� ��ġ
	    					pos.add(0,1.01,0);
	    				} else { //�ڽ��� ���ӿ� �ִٸ�
	    					pos = getUpLocation(pos); //���� ���� ��ġ
	    					pos.add(0,1.01,0);
	    				}	 
	    				
	    				//showLineAsBlock(pos); //��ġ ǥ��
	    				pos.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, pos.clone().add(side), 1, 0.0F, 0.0f, 0.0f, 0.0f);
	    				pos.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, pos.clone().subtract(side), 1, 0.0F, 0.0f, 0.0f, 0.0f);
	    			}
				}		  
			}
		}, 0l, 3l);
	}
	
	//�̺�Ʈ
	private class StraightEvent implements Listener{
		
		@EventHandler
		public void onPlayerMove(PlayerMoveEvent e) {
			Player p = e.getPlayer();
			
			if(owner == null) {
				PlayerMoveEvent.getHandlerList().unregister(this); //�̺�Ʈ ����
				return; 
			}
			
			if(!p.getUniqueId().toString().equals(owner.getUniqueId().toString())) return; //�ش� �������� ������ �����ΰ� �ƴϸ� return
			
			if(e.getFrom().getBlockX() == e.getTo().getBlockX() && e.getFrom().getBlockY() == e.getTo().getBlockY()
					&& e.getFrom().getBlockZ() == e.getTo().getBlockZ()) return; //��ġ�̵� �ƴϸ� ĵ��
			
			double basePos = 0;
			double newPos = 0;
	    	if(direction == LineDirection.X) { //�������� X���̸�
	    		basePos = nowPos.getZ();//z������ �̵��� �Ұ���
	    		newPos = e.getTo().getZ();
	    	} else if(direction == LineDirection.Z) { //�������� z���̸�
	    		basePos = nowPos.getX();//x������ �̵��� �Ұ���
	    		newPos = e.getTo().getX();
	    	}
	    	
	    	if(Math.abs(basePos - newPos) >= 0.5) { //�̵� �Ұ������� 1ĭ �̻� �̵��ߴٸ�
	    		e.setTo(e.getFrom());//�̵� ���
	    	} else {
	    		if(nowPos.distance(e.getTo()) >= 0.5) { //�̵� ���ɼ����� 1ĭ�̻� �̵���			
	    			nowPos = e.getTo().clone(); //���� ��ġ ����
	    			nowPos.setX(nowPos.getX() < 0 ? (int)nowPos.getX() - 0.5 : (int)nowPos.getX() + 0.5); //�߾� ����
	    			nowPos.setZ(nowPos.getZ() < 0 ? (int)nowPos.getZ() - 0.5 : (int)nowPos.getZ() + 0.5); //�߾� ����
	    				
	    		}
	    	}	
		}		
		
		@EventHandler
		public void onPlayerRespawn(PlayerRespawnEvent e) {
			Player p = e.getPlayer();

			if (owner == null) {
				PlayerRespawnEvent.getHandlerList().unregister(this); // �̺�Ʈ ����
				return;
			}
			
			if (!p.getUniqueId().toString().equals(owner.getUniqueId().toString()))
				return; // �ش� �������� ������ �������Ѱ� �ƴϸ� return
			
			nowPos = e.getRespawnLocation().clone(); // ���� ��ġ ����
			nowPos.setX(nowPos.getX() < 0 ? (int) nowPos.getX() - 0.5 : (int) nowPos.getX() + 0.5); // �߾� ����
			nowPos.setZ(nowPos.getZ() < 0 ? (int) nowPos.getZ() - 0.5 : (int) nowPos.getZ() + 0.5); // �߾� ����
		}
		
		@EventHandler
		public void onPlayerTeleport(PlayerTeleportEvent e) {
			Player p = e.getPlayer();

			if (owner == null) {
				PlayerTeleportEvent.getHandlerList().unregister(this); // �̺�Ʈ ����
				return;
			}
			
			if (!p.getUniqueId().toString().equals(owner.getUniqueId().toString()))
				return; // �ش� �������� ������ �ڷ���Ʈ�Ѱ� �ƴϸ� return
			nowPos = e.getTo().clone(); // ���� ��ġ ����
			nowPos.setX(nowPos.getX() < 0 ? (int) nowPos.getX() - 0.5 : (int) nowPos.getX() + 0.5); // �߾� ����
			nowPos.setZ(nowPos.getZ() < 0 ? (int) nowPos.getZ() - 0.5 : (int) nowPos.getZ() + 0.5); // �߾� ����
		}

	}
	
}

