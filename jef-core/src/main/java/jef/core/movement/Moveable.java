package jef.core.movement;

public interface Moveable
{
	public AngularVelocity getAV();
	public void setAV(AngularVelocity angularVelocity);

	public Location getLoc();
	public void setLoc(Location location);

	public LinearVelocity getLV();
	public void setLV(LinearVelocity lv);
}