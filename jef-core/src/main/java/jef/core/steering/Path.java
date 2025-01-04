package jef.core.steering;

import java.util.List;

import jef.core.Location;

public interface Path
{

	void addWaypoint(Waypoint waypoint);

	List<Waypoint> getWaypoints();

	void clear();

	Waypoint getCurrentWaypoint();

	Location getDestination();

	void removeWaypoint(Waypoint waypoint);

}