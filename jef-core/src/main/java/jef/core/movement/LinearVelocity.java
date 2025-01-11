package jef.core.movement;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.util.Precision;

/* @formatter:off */
public interface LinearVelocity
{
	public static final double EPSILON = .02;

	public static boolean closeEnoughTo(final double v1, final double v2)
	{
		return Precision.equals(v1, v2, LinearVelocity.EPSILON);
	}

	public LinearVelocity add(double speed);
	public LinearVelocity add(double elevation, double azimuth, double speed);
	public LinearVelocity add(LinearVelocity lv);
	public LinearVelocity subtract(LinearVelocity lv);
	public LinearVelocity multiply(double scalar);
	public LinearVelocity normalize();
	public LinearVelocity newFrom(Double elevation, Double azimuth, Double speed);

	public double getElevation();
	public double getAzimuth();
	public double getSpeed();

	public double getX();
	public double getY();
	public double getZ();

	public double getXYSpeed();
	public double getXZSpeed();
	public double getYZSpeed();

	public boolean isNotMoving();
	public boolean movingLeft();
	public boolean movingRight();

	public boolean closeEnoughTo(LinearVelocity lv);
	
	public Vector3D toVector3D();
	public Vector2D toVector2D();

}