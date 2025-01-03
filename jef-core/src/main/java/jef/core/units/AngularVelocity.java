package jef.core.units;

public interface AngularVelocity
{

	boolean isNotRotating();

	AngularVelocity addRotation(double currentAngleInRadians, double radiansPerSecond, double spiralVelocity);
	AngularVelocity addRotation(AngularVelocity av);

	boolean isRotatingClockwise();
	boolean isRotatingCounterClockwise();

	double getOrientation();
	double getRotation();
	double getSpiralVelocity();

	AngularVelocity newFrom(Double currentAngle, Double rotationalVelocity, Double spiralVelocity);
	AngularVelocity multiply(double value);
}