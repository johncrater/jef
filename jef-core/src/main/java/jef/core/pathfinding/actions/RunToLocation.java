package jef.core.pathfinding.actions;

import jef.core.Player;
import jef.core.movement.Location;

public class RunToLocation
{
	private Player player;
	private Location destination;
	private double speed;
	
	public RunToLocation(Player player, Location destination, double speed)
	{
		this.player = player;
		this.destination = destination;
		this.speed = speed;
	}

	
}
