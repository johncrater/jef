package football.jef.core;

import football.jef.core.units.AngularVelocity;
import football.jef.core.units.LinearVelocity;
import football.jef.core.units.Location;

public interface Football
{
	public Football adjustAngularVelocity(Double currentAngleInDegrees, Double radiansPerSecond);
	public Football adjustLinearVelocity(Double x, Double y, Double z);
	public Football adjustLocation(Double x, Double y, Double z);

	public AngularVelocity getAngularVelocity();
	public LinearVelocity getLinearVelocity();
	public Location getLocation();

	public Football setAngularVelocity(AngularVelocity angularVelocity);
	public Football setAngularVelocity(Double currentAngleInRadians, Double radiansPerSecond);
	public Football setLinearVelocity(LinearVelocity lv);
	public Football setLinearVelocity(Double x, Double y, Double z);
	public Football setLocation(Location location);
	public Football setLocation(Double x, Double y, Double z);
}
