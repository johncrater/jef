package jef.core;

import jef.core.units.AngularVelocity;
import jef.core.units.LinearVelocity;
import jef.core.units.Location;

public interface Football
{
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
	public void setLocation(Double x, Double y, Double z);
}
