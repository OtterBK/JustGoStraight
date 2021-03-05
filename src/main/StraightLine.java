package main;

import org.bukkit.Location;
import org.bukkit.event.Listener;

public class StraightLine { //직진선

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
	
	//이벤트
	private class straightEvent implements Listener{
		
	}
	
}
