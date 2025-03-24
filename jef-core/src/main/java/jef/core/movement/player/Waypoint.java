package jef.core.movement.player;

import java.util.Objects;

import jef.core.Location;

public class Waypoint
{
	public enum DestinationAction { instant, fastStop, normalStop, slowStop, noStop, rounded}
	
	private Location destination;
	private double maxSpeed;
	private double minTurnSpeed;
	private DestinationAction destinationAction;
	private Double destinationOrientation;
	
	public Waypoint(Location destination, double maxSpeed, DestinationAction destinationAction, double minTurnSpeed, Double destinationOrientation)
	{
		super();
		this.destination = destination;
		this.minTurnSpeed = minTurnSpeed;
		this.maxSpeed = maxSpeed;
		this.destinationAction = destinationAction;
		this.destinationOrientation = destinationOrientation;
	}

	public Waypoint(Location destination, double maxSpeed, DestinationAction destinationAction)
	{
		super();
		this.destination = destination;
		this.maxSpeed = maxSpeed;
		this.destinationAction = destinationAction;
	}

	public Double getDestinationOrientation()
	{
		return this.destinationOrientation;
	}

	public Location getWaypointDestination()
	{
		return this.destination;
	}

	public double getMinTurnSpeed()
	{
		return this.minTurnSpeed;
	}

	public double getMaxSpeed()
	{
		return this.maxSpeed;
	}

	public DestinationAction getDestinationAction()
	{
		return this.destinationAction;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(destination, destinationAction, maxSpeed, minTurnSpeed);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Waypoint other = (Waypoint) obj;
		return Objects.equals(this.destination, other.destination) && this.destinationAction == other.destinationAction
				&& Double.doubleToLongBits(this.maxSpeed) == Double.doubleToLongBits(other.maxSpeed)
				&& Double.doubleToLongBits(this.minTurnSpeed) == Double.doubleToLongBits(other.minTurnSpeed);
	}

	@Override
	public String toString()
	{
		return String.format("%s %3.1f %s", this.destination, this.maxSpeed, this.destinationAction);
	}

}
