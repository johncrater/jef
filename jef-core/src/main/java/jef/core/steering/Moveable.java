package jef.core.steering;

import jef.core.units.AngularVelocity;
import jef.core.units.LinearVelocity;
import jef.core.units.Location;

public interface Moveable
{
	public AngularVelocity getAngularVelocity();
	public void setAngularVelocity(AngularVelocity angularVelocity);

	public Location getLocation();
	public void setLocation(Location location);

	public LinearVelocity getLinearVelocity();
	public void setLinearVelocity(LinearVelocity lv);
	
	public void turn(double angle);
	public void move(double distance);
	public void move(LinearVelocity lv);
	public void adjustSpeed(double speedDelta);
	public void setSpeed(double newSpeed);
	public double getSpeed();
}