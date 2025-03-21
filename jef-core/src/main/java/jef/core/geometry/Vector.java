package jef.core.geometry;

import java.util.Objects;

import jef.core.Conversions;

public class Vector
{
	private double x;
	private double y;
	private double z;

	public static Vector fromCartesianCoordinates(double x, double y, double z)
	{
		Vector ret = new Vector();
		ret.x = x;
		ret.y = y;
		ret.z = z;
		return ret;
	}

	public static Vector fromPolarCoordinates(double azimuth, double elevation, double distance)
	{
		Vector ret = new Vector();
		ret.x = Math.cos(elevation) * Math.cos(azimuth) * distance;
		ret.y = Math.cos(elevation) * Math.sin(azimuth) * distance;
		ret.z = Math.sin(elevation) * distance;
		return ret;
	}

	public static Vector fromVector(Vector v)
	{
		Vector ret = new Vector();
		ret.x = v.x;
		ret.y = v.y;
		ret.z = v.z;
		return ret;
	}

	public Vector normalize()
	{
		double ratio = 1 / getDistance();

		Vector ret = new Vector();
		ret.x = x * ratio;
		ret.y = y * ratio;
		ret.z = z * ratio;
		return ret;
	}

	public Vector add(Vector v)
	{
		Vector ret = new Vector();
		ret.x = x + v.x;
		ret.y = y + v.y;
		ret.z = z + v.z;
		return ret;
	}

	public Vector subtract(Vector v)
	{
		return add(v.negate());
	}

	public Vector add(double x, double y, double z)
	{
		return add(Vector.fromCartesianCoordinates(x, y, z));
	}

	public Vector negate()
	{
		Vector ret = new Vector();
		ret.x = -x;
		ret.y = -y;
		ret.z = -z;
		return ret;
	}

	public double getX()
	{
		return this.x;
	}

	public double getY()
	{
		return this.y;
	}

	public double getZ()
	{
		return this.z;
	}

	public double getElevation()
	{
		double ret = Math.asin(z / getDistance());
		if (Double.isNaN(ret))
			ret = 0;

		return ret;
	}

	public double getAzimuth()
	{
		return Math.atan2(y, x);
	}

	public double getDistance()
	{
		return Math.sqrt(x * x + y * y + z * z);
	}

	public Vector multiply(double scalar)
	{
		Vector ret = new Vector();
		ret.x = x * scalar;
		ret.y = y * scalar;
		ret.z = z * scalar;
		return ret;
	}

	public double distanceBetween(Vector v)
	{
		// √[(x2 - x1)² + (y2 - y1)² + (z2 - z1)²]
		return Math.sqrt(Math.pow(v.x - x, 2) + Math.pow(v.y - y, 2) + Math.pow(v.z - z, 2));
	}

	public double xyAngle(Vector v)
	{
		double deltaX = v.x - x;
		double deltaY = v.y - y;

		return Math.atan2(deltaY, deltaX);
	}

	public Vector orthogonal2D()
	{
		return Vector.fromCartesianCoordinates(-y, x, 0);
	}

	public double dot(Vector v)
	{
		return x * v.x + y * v.y + z * v.z;
	}

	public Vector crossProduct(Vector v)
	{
		/*
		 * cx = aybz − azby cy = azbx − axbz cz = axby − aybx
		 */
		double cx = getY() * v.getZ() - getZ() * v.getY();
		double cy = getZ() * v.getX() - getX() * v.getZ();
		double cz = getX() * v.getY() - getY() * v.getX();
		
		return Vector.fromCartesianCoordinates(cx, cy, cz);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(x, y, z);
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
		Vector other = (Vector) obj;
		return Double.doubleToLongBits(this.x) == Double.doubleToLongBits(other.x)
				&& Double.doubleToLongBits(this.y) == Double.doubleToLongBits(other.y)
				&& Double.doubleToLongBits(this.z) == Double.doubleToLongBits(other.z);
	}

	@Override
	public String toString()
	{
		return "Vector [x=" + this.x + ", y=" + this.y + ", z=" + this.z + "]";
	}
}
