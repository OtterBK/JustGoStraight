package main;

import org.bukkit.Location;
import org.bukkit.event.Listener;

public class StraightLine { //������

	private Location startPos;
	private LineDirection direction;
	
	public StraightLine(Location startPos, LineDirection direction) {
		
		this.startPos = startPos;
		this.direction = direction;
		
	}
	
	public Location getStartPos() {
		return this.startPos;
	}
	
	public LineDirection direction() {
		return this.direction;
	}
	
	//�̺�Ʈ
	private class straightEvent implements Listener{
		
	}
	
}
