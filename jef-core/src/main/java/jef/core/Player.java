package jef.core;

import com.synerset.unitility.unitsystem.common.Velocity;

import jef.core.units.AngularVelocity;
import jef.core.units.LinearVelocity;
import jef.core.units.Location;
import jef.core.units.VUnits;

public interface Player 
{
	// deceleration is in YPY^2. It is not a velocity
	public static final float maximumDecelerationRate = (float) Velocity.ofMetersPerSecond(-6).getInUnit(VUnits.YPS);
	public static final float normalDecelerationRate = (float) Velocity.ofMetersPerSecond(-3).getInUnit(VUnits.YPS);
	// turning speed in milliseconds for changing orientation. A total guess 180
	// degree turn in .25 seconds
	public static final float maximumAngularVelocity = 180 / .25f;

	// suspect players are faster. But this is a general idea
	public static final float visualReactionTime = .200f;
	public static final float auditoryReactionTime = .150f;

	public static final double size = 1.0f;

	public double getMassInKilograms();
	public String getId();
	public String getFirstName();
	public String getLastName();
	public double getHeightInMeters();
	public int getNumber();

	public void adjustAngularVelocity(double currentAngleInDegrees, double radiansPerSecond);
	public void adjustLinearVelocity(double x, double y, double z);
	public void adjustLocation(double x, double y, double z);

	public AngularVelocity getAngularVelocity();
	public LinearVelocity getLinearVelocity();
	public Location getLocation();

	public void setAngularVelocity(AngularVelocity angularVelocity);
	public void setAngularVelocity(Double currentAngleInRadians, Double radiansPerSecond);
	public void setLinearVelocity(LinearVelocity lv);
	public void setLinearVelocity(Double x, Double y, Double z);
	public void setLocation(Location location);
	public void setLocation(Double x, Double y, Double z);}
