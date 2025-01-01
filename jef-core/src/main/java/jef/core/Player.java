package jef.core;

import com.synerset.unitility.unitsystem.common.Velocity;

import jef.core.units.VUnits;

public interface Player 
{
	// deceleration is in YPY^2. It is not a velocity
	public static final double maximumDecelerationRate = (double) Velocity.ofMetersPerSecond(-6).getInUnit(VUnits.YPS);
	public static final double normalDecelerationRate = (double) Velocity.ofMetersPerSecond(-3).getInUnit(VUnits.YPS);
	// turning speed in milliseconds for changing orientation. A total guess 180
	// degree turn in .25 seconds
	public static final double maximumAngularVelocity = 180 / .25;

	// suspect players are faster. But this is a general idea
	public static final double visualReactionTime = .200;
	public static final double auditoryReactionTime = .150;

	public static final double size = 1.0;

	public double getMassInKilograms();
	public String getId();
	public String getFirstName();
	public String getLastName();
	public double getHeightInMeters();
	public int getNumber();
}