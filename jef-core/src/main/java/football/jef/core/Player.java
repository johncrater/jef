package football.jef.core;

import football.jef.core.units.AngularVelocity;
import football.jef.core.units.LinearVelocity;
import football.jef.core.units.Location;

public interface Player
{
	public static final float size = 1.0f;

	public double getMassInKilograms();
	public String getId();
	public String getFirstName();
	public String getLastName();
	public double getHeightInMeters();
	public int getNumber();
	
	public Player adjustAngularVelocity(Double currentAngleInDegrees, Double radiansPerSecond);
	public Player adjustLinearVelocity(Double x, Double y, Double z);
	public Player adjustLocation(Double x, Double y, Double z);

	public AngularVelocity getAngularVelocity();
	public LinearVelocity getLinearVelocity();
	public Location getLocation();

	public Player setAngularVelocity(AngularVelocity angularVelocity);
	public Player setAngularVelocity(Double currentAngleInRadians, Double radiansPerSecond);
	public Player setLinearVelocity(LinearVelocity lv);
	public Player setLinearVelocity(Double x, Double y, Double z);
	public Player setLocation(Location location);
	public Player setLocation(Double x, Double y, Double z);
}
