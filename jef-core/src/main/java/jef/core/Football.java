package jef.core;

import jef.core.units.AngularVelocity;
import jef.core.units.LinearVelocity;
import jef.core.units.Location;

public interface Football
{
	public void adjustAngularVelocity(double currentAngleInDegrees, double radiansPerSecond);
	public AngularVelocity getAngularVelocity();
	public void setAngularVelocity(AngularVelocity angularVelocity);
	public void setAngularVelocity(Double currentAngleInRadians, Double radiansPerSecond);

	public void adjustLinearVelocity(double elevation, double azimuth, double speed);
	public LinearVelocity getLinearVelocity();
	public void setLinearVelocity(LinearVelocity lv);
	public void setLinearVelocity(Double elevation, Double azimuth, Double speed);
	
	public void adjustLocation(double x, double y, double z);
	public Location getLocation();
	public void setLocation(Location location);
	public void setLocation(Double x, Double y, Double z);
}
