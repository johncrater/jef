package jef.core.movement.player;

import java.util.List;

import jef.core.Location;

public interface Path
{
	List<Waypoint> getWaypoints();
	Waypoint getCurrentWaypoint();
	Location getDestination();
}