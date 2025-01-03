package jef.core;

public interface AngularVelocity
{

	double EPSILON_ANGLE = .02;
	double EPSILON_ROTATIONS = .02;
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