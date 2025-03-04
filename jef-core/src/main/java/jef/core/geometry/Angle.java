package jef.core.geometry;

import jef.core.Location;

public class Angle
{
	private Location vertex;
	private Vector vector1;
	private Vector vector2;
	
	public Angle(Location vertex, Vector vector1, Vector vector2)
	{
		super();
		this.vertex = vertex;
		this.vector1 = vector1;
		this.vector2 = vector2;
	}

	public Location getVertex()
	{
		return this.vertex;
	}

	public Vector getVector1()
	{
		return this.vector1;
	}

	public Vector getVector2()
	{
		return this.vector2;
	}

	public double getAngle()
	{
		double cosTheta = vector1.dot(vector2) / (vector1.getDistance() * vector2.getDistance());
		return Math.acos(cosTheta);
	}
	
	public Vector bisect()
	{
		double angle1 = Math.max(this.vector1.getAzimuth(), this.vector2.getAzimuth());
		double angle2 = Math.min(this.vector1.getAzimuth(), this.vector2.getAzimuth());
		
		if (Math.abs(angle1 - angle2) > Math.PI)
		{
			if (angle2 < 0)
				angle2 = angle2 + 2 * Math.PI;
			else 
				angle1 = angle1 - 2 * Math.PI;
		}
		
		double azimuth = (angle1 + angle2) / 2;
		double elevation = this.vector1.getElevation() + (this.vector2.getElevation() - this.vector1.getElevation()) / 2;
		double distance = this.vector1.getDistance() + (this.vector2.getDistance() - this.vector1.getDistance()) / 2;
		return Vector.fromPolarCoordinates(azimuth, elevation, distance);
	}
}
