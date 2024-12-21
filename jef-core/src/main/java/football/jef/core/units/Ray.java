package football.jef.core.units;

import java.util.Objects;

public class Ray
{
	private final Location location;
	private final LinearVelocity linearVelocity;

	public Ray()
	{
		this.location = new Location();
		this.linearVelocity = new LinearVelocity();
	}

	public Ray(final Location location, final LinearVelocity linearVelocity)
	{
		this.location = location;
		this.linearVelocity = linearVelocity;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;

		if ((obj == null) || (this.getClass() != obj.getClass()))
			return false;

		final Ray other = (Ray) obj;
		return Objects.equals(this.linearVelocity, other.linearVelocity)
				&& Objects.equals(this.location, other.location);
	}

	public LinearVelocity getLinearVelocity()
	{
		return this.linearVelocity;
	}

	public Location getLocation()
	{
		return this.location;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(this.linearVelocity, this.location);
	}

	@Override
	public String toString()
	{
		return "Ray [location=" + this.location + ", linearVelocity=" + this.linearVelocity + "]";
	}

}
