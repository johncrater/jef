package jef.core.movement.player;

import java.util.List;

import jef.core.movement.Location;

public interface Path
{

	void addWaypoint(Waypoint waypoint);

	List<Waypoint> getWaypoints();

	void clear();

	Waypoint getCurrentWaypoint();

	Location getDestination();

	void removeWaypoint(Waypoint waypoint);

}