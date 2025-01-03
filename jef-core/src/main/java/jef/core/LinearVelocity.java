package jef.core;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public interface LinearVelocity
{
	double EPSILON = .02;
	LinearVelocity add(double distance);
	LinearVelocity add(double elevation, double azimuth, double distance);
	LinearVelocity add(LinearVelocity lv);
	LinearVelocity subtract(LinearVelocity lv);
	LinearVelocity multiply(double scalar);
	LinearVelocity normalize();

	double getElevation();
	double getAzimuth();
	double getDistance();

	double getX();
	double getY();
	double getZ();

	double getXYDistance();
	double getXZDistance();
	double getYZDistance();

	boolean isNotMoving();
	boolean movingLeft();
	boolean movingRight();

	LinearVelocity newFrom(Double elevation, Double azimuth, Double distance);
	Vector3D toVector3D();

}