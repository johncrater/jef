package jef.core.steering;

import jef.core.units.AngularVelocity;
import jef.core.units.LinearVelocity;
import jef.core.units.Location;

public interface Moveable
{
	void adjustAngularVelocity(Double currentAngleInDegrees, Double radiansPerSecond);
	void adjustLinearVelocity(Double x, Double y, Double z);
	void adjustLocation(Double x, Double y, Double z);

	AngularVelocity getAngularVelocity();
	LinearVelocity getLinearVelocity();
	Location getLocation();

	void setAngularVelocity(AngularVelocity angularVelocity);
	void setAngularVelocity(Double currentAngleInRadians, Double radiansPerSecond);
	void setLinearVelocity(Double x, Double y, Double z);
	void setLocation(Double x, Double y, Double z);
	void setLinearVelocity(LinearVelocity lv);
	void setLocation(Location location);

}