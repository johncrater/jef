package jef.core.collisions;

import java.util.Objects;

import jef.core.Location;
import jef.core.movement.DefaultLocation;
import jef.core.movement.player.PlayerTracker;

public class Collision
{
	private PlayerTracker occupier1;
	private PlayerTracker occupier2;
	private int tickCountOfcollision;

	public Collision(PlayerTracker occupier1, PlayerTracker occupier2, int tickCountOfcollision)
	{
		super();
		this.occupier1 = occupier1;
		this.occupier2 = occupier2;
		this.tickCountOfcollision = tickCountOfcollision;
	}

	public Location getCollisionLocation()
	{
		double distance = occupier1.getLoc().distanceBetween(occupier2.getLoc()) / 2;
		double angle = occupier1.getLoc().angleTo(occupier2.getLoc());
		return new DefaultLocation(occupier1.getLoc().getX() + distance * Math.cos(angle),
				occupier1.getLoc().getY() + distance * Math.sin(angle), 0);
	}

	/**
	 * @return The angle the player is moving relative to the other player when the
	 *         impact occurs. 0 means they are moving directly into the other
	 *         player.
	 */
	public double getOccupier1AngleOnImpact()
	{
		return occupier1.getLV().getAzimuth() - occupier1.getLoc().angleTo(occupier2.getLoc());
	}

	/**
	 * @return The angle the player is moving relative to the other player when the
	 *         impact occurs. 0 means they are moving directly into the other
	 *         player.
	 */
	public double getOccupier2AngleOnImpact()
	{
		return occupier2.getLV().getAzimuth() - occupier2.getLoc().angleTo(occupier1.getLoc());
	}

	public PlayerTracker getOccupier1()
	{
		return this.occupier1;
	}

	public PlayerTracker getOccupier2()
	{
		return this.occupier2;
	}

	public int getTickCountOfcollision()
	{
		return this.tickCountOfcollision;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(occupier1, occupier2, tickCountOfcollision);
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

		Collision other = (Collision) obj;
		return ((Objects.equals(this.occupier1, other.occupier1) && Objects.equals(this.occupier2, other.occupier2))
				|| (Objects.equals(this.occupier1, other.occupier2) && Objects.equals(this.occupier2, other.occupier1)))
				&& this.tickCountOfcollision == other.tickCountOfcollision;
	}

	@Override
	public String toString()
	{
		return "Collision [occupier1=" + this.occupier1 + ", occupier2=" + this.occupier2 + ", tickCountOfcollision="
				+ this.tickCountOfcollision + "]";
	}
};
