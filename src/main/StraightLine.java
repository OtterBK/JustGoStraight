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

public class StraightLine { //직진선

	private LineDirection direction; //진행방향
	private Player owner; //직진선 소유자

	private double radius = 0.5; //행동 반경
	private Location rail; //현재 위치(블럭단위)
	private Listener myEvent;
	
	private ArrayList<EntityShulker> glowingBlockList = new ArrayList<EntityShulker>();
	private int timerID = -1;
	
	private boolean allowTeleport = false;
	
	private ArrayList<Material> exceptTypes = new ArrayList<Material>();
	
	public StraightLine(Player owner, LineDirection direction, double radius) {
		
		this.owner = owner;
		this.radius = radius; //가로 반경
		
		this.rail = owner.getLocation();
		rail.setX(rail.getX() < 0 ? (int)rail.getX() - radius : (int)rail.getX() + radius); //
		rail.setZ(rail.getZ() < 0 ? (int)rail.getZ() - radius : (int)rail.getZ() + radius); //레일 초기화
		
		this.direction = direction;
		
		owner.teleport(rail); //설정된 레일로 이동
		
		myEvent= new StraightEvent(); //이벤트 생성
		
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
	
		allowTeleport = Main.pluginInstance.getConfig().getBoolean("allowTeleport"); //텔레포트 허용여부
		
		
		//glowingBlockTimer();
		particleTimer();
	}
	
	public Location getRailPos() {
		return this.rail;
	}
	
	public LineDirection direction() {
		return this.direction;
	}
	
	private EntityShulker sendGlowingBlock(Plugin server, Player p, Location loc) { // 블럭 빛나게하기
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
	
	public void clearGlowingBlock() { //직진선 표시용 빛나는 블록 초기화 
		for(EntityShulker gb : glowingBlockList) {
			removeGlowingBlock(this.owner, gb);
		}
	}
	
	public void showLineAsBlock(Location l) { //빛나는 블럭으로 방향 알려주
		EntityShulker gb = sendGlowingBlock(Main.pluginInstance, this.owner, l);
		this.glowingBlockList.add(gb);
	}
	
	//로케이션의 땅블럭 위치
	private Location getGroundLocation(Location l) {
		Location tmpL = new Location(l.getWorld(), l.getX(), l.getY(), l.getZ());
		while(tmpL.getY() > 0) {
			
			Material blockType = tmpL.getBlock().getType();
	
			if(exceptTypes.contains(blockType)) { //예외 블럭
				tmpL.setY(tmpL.getY()-1);
			} else {
				break;
			}
			
		}
		return tmpL.add(0,0f,0);
	}
	
	//로케이션의 가장가까운위블럭 위치
	public Location getUpLocation(Location l) {
		Location tmpL = new Location(l.getWorld(), l.getX(), l.getY(), l.getZ());
		while(true) {
			if(tmpL.getY() >= 255) {
				return l;
			} else {
				
				Material blockType = tmpL.getBlock().getType();
				
				if(exceptTypes.contains(blockType)) { //예외 블럭
					tmpL.setY(tmpL.getY()+1);
				} else {
					break;
				}
				
			}
		}
		return tmpL.add(0,0f,0);
	}
	
	public void destroy() { //직진석 삭제
		this.owner = null;
	}
	
	public void glowingBlockTimer() { //사용안함
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.pluginInstance, new Runnable() {
			public void run() {
    			clearGlowingBlock();
    			
    			Vector vec = new Vector(0,0,0);
				
    			if(direction == LineDirection.X) {//이동방향이 x축이라면
    				vec.setX(vec.getX() + 1);
    			} else { //z축이라면
    				vec.setZ(vec.getZ() + 1);
    			}
    			 
    			Location pos1 = rail.clone().add(vec); //전방 블럭
    			Location pos2 = rail.clone().subtract(vec); //후방 블럭
    			
    			pos1 = getGroundLocation(pos1).subtract(0,2,0); //땅 위치
    			pos2 = getGroundLocation(pos2).subtract(0,2,0); //땅 위치
    			
    			showLineAsBlock(pos1); //위치 표시
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
					
	    			if(direction == LineDirection.X) {//이동방향이 x축이라면
	    				vec.setX(vec.getX() + 1);
	    				side.setZ(vec.getZ() + radius);
	    			} else { //z축이라면
	    				vec.setZ(vec.getZ() + 1);
	    				side.setX(vec.getX() + radius);
	    			}    			 
	    			
	    			for(int i = -30; i <= 30; i++) { //라인 표시
	    				if(i == 0) continue; //자기 위치는 패스
	    				
	    				Location pos = rail.clone().add(vec.clone().multiply(i)); //전방 블럭
	    				
	    				Material blockType = pos.getBlock().getType();
	    				
	    				if(exceptTypes.contains(blockType)) { //자신이 블럭속에 있지 않다면
	    					pos = getGroundLocation(pos); //땅블럭 위치
	    					pos.add(0,1.01,0);
	    				} else { //자신이 블럭속에 있다면
	    					pos = getUpLocation(pos); //가장 윗블럭 위치
	    					pos.add(0,1.01,0);
	    				}	 
	    				
	    				//showLineAsBlock(pos); //위치 표시
	    				pos.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, pos.clone().add(side), 1, 0.0F, 0.0f, 0.0f, 0.0f);
	    				pos.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, pos.clone().subtract(side), 1, 0.0F, 0.0f, 0.0f, 0.0f);
	    			}
				}		  
			}
		}, 0l, 3l);
	}
	
	//이벤트
	private class StraightEvent implements Listener{
		
		@EventHandler
		public void onPlayerMove(PlayerMoveEvent e) {
			Player p = e.getPlayer();
			
			if(owner == null) {
				PlayerMoveEvent.getHandlerList().unregister(this); //이벤트 삭제
				return; 
			}
			
			if(!p.getUniqueId().toString().equals(owner.getUniqueId().toString())) return; //해당 직진선의 주인이 움직인게 아니면 return
			
			if(e.getFrom().getBlockX() == e.getTo().getBlockX() && e.getFrom().getBlockY() == e.getTo().getBlockY()
					&& e.getFrom().getBlockZ() == e.getTo().getBlockZ()) return; //위치이동 아니면 캔슬
			
			double noDir = 0;
			double noDir_new = 0;
			double dir = 0;
			double dir_new = 0;
	    	if(direction == LineDirection.X) { //직진선이 X축이면
				noDir = rail.getZ();//z축으로 이동은 불가능
				noDir_new = e.getTo().getZ();

				dir = rail.getX();//x축으로 이동은 가능
				dir_new = e.getTo().getX();
	    	} else if(direction == LineDirection.Z) { //직진선이 z축이면
				noDir = rail.getX();//x축으로 이동은 불가능
				noDir_new = e.getTo().getX();

				dir = rail.getZ();//Z축으로 이동은 가능
				dir_new = e.getTo().getZ();
	    	}
	    	
	    	if(Math.abs(noDir - noDir_new) >= radius) { //이동 불가선으로 행동반경 이상 이동했다면
	    		e.setTo(e.getFrom());//이동 취소
	    	} else {
	    		if(Math.abs(dir - dir_new) >= radius) { //이동 가능선으로 행동반경이상 이동시
	    			if(direction == LineDirection.X){
	    				rail.setX(e.getTo().getX());
					} else {
						rail.setZ(e.getTo().getZ());
					}
					rail.setY(e.getTo().getY());
	    		}
	    	}
		}		
		
		@EventHandler
		public void onPlayerRespawn(PlayerRespawnEvent e) {
			Player p = e.getPlayer();

			if (owner == null) {
				PlayerRespawnEvent.getHandlerList().unregister(this); // 이벤트 삭제
				return;
			}
			
			if (!p.getUniqueId().toString().equals(owner.getUniqueId().toString()))
				return; // 해당 직진선의 주인이 리스폰한게 아니면 return
			
			rail = e.getRespawnLocation().clone(); // 현재 위치 갱신
			rail.setX(rail.getX() < 0 ? (int) rail.getX() - radius : (int) rail.getX() + radius); // 중앙 지점
			rail.setZ(rail.getZ() < 0 ? (int) rail.getZ() - radius : (int) rail.getZ() + radius); // 중앙 지점
		}
		
		@EventHandler
		public void onPlayerTeleport(PlayerTeleportEvent e) {
			Player p = e.getPlayer();

			if (owner == null) {
				PlayerTeleportEvent.getHandlerList().unregister(this); // 이벤트 삭제
				return;
			}
			
			if (!p.getUniqueId().toString().equals(owner.getUniqueId().toString()))
				return; // 해당 직진선의 주인이 텔레포트한게 아니면 return
			
			if(allowTeleport) { //텔로포트 허용
				if(e.getCause() != TeleportCause.PLUGIN) {
					rail = e.getTo().clone(); // 현재 위치 갱신
					rail.setX(rail.getX() < 0 ? (int) rail.getX() - radius : (int) rail.getX() + radius); // 중앙 지점
					rail.setZ(rail.getZ() < 0 ? (int) rail.getZ() - radius : (int) rail.getZ() + radius); // 중앙 지점
				}
			} else { //비허용
				if(e.getCause() != TeleportCause.PLUGIN) {
					p.sendMessage(Main.mainMS+"텔레포트가 비허용 되어있습니다.");
					e.setTo(rail);
				}
			}
			
			
		}

	}
	
}

