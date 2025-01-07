package jef.core.movement;

/* @formatter:off */
public interface AngularVelocity
{
	public static final double EPSILON_ANGLE = .02;
	public static final double EPSILON_ROTATIONS = .02;

	public boolean isNotRotating();

	public AngularVelocity addRotation(double currentAngleInRadians, double radiansPerSecond, double spiralVelocity);
	public AngularVelocity addRotation(AngularVelocity av);

	public boolean isRotatingClockwise();
	public boolean isRotatingCounterClockwise();

	public double getOrientation();
	public double getRotation();
	public double getSpiralVelocity();

	public AngularVelocity newFrom(Double currentAngle, Double rotationalVelocity, Double spiralVelocity);
	public AngularVelocity multiply(double value);
}