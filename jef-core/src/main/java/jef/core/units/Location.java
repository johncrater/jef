package jef.core.units;

public interface Location
{

	double distanceBetween(Location loc);

	boolean closeEnoughTo(Location loc, double distance);
	boolean closeEnoughTo(Location loc);

	double angleTo(Location loc);

	Location add(double x, double y, double z);
	Location add(LinearVelocity lv);
	Location add(Location loc);

	Location newFrom(Double x, Double y, Double z);

	double getX();
	double getY();
	double getZ();

}