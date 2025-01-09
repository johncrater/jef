package jef.core.movement;

import java.util.Objects;

import jef.core.Player;

public class Collision
{
	private Player occupier1;
	private Player occupier2;
	private int tickCountOfcollision;
	
	public Collision(Player occupier1, Player occupier2, int tickCountOfcollision)
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
		return new DefaultLocation(occupier1.getLoc().getX() + distance * Math.cos(angle), occupier1.getLoc().getY() + distance * Math.sin(angle), 0);
	}

	public Player getOccupier1()
	{
		return this.occupier1;
	}

	public Player getOccupier2()
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
