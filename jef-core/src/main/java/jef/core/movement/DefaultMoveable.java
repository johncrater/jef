package jef.core.movement;

public class DefaultMoveable implements Moveable
{
	private LinearVelocity lv;
	private Location loc;
	private AngularVelocity av;

	public DefaultMoveable(LinearVelocity lv, Location loc, AngularVelocity av)
	{
		super();
		this.lv = lv;
		this.loc = loc;
		this.av = av;
	}

	public DefaultMoveable(Moveable m)
	{
		this.av = m.getAV();
		this.lv = m.getLV();
		this.loc = m.getLoc();
	}
	
	public DefaultMoveable()
	{
		this.av = new DefaultAngularVelocity();
		this.lv = new DefaultLinearVelocity();
		this.loc = new DefaultLocation();
	}

	@Override
	public AngularVelocity getAV()
	{
		return av;
	}

	@Override
	public void setAV(AngularVelocity angularVelocity)
	{
		this.av = angularVelocity;
	}

	@Override
	public Location getLoc()
	{
		return this.loc;
	}

	@Override
	public void setLoc(Location location)
	{
		this.loc = location;
	}

	@Override
	public LinearVelocity getLV()
	{
		return this.lv;
	}

	@Override
	public void setLV(LinearVelocity lv)
	{
		this.lv = lv;
	}

}
