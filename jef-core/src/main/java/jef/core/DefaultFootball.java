package jef.core;

public class DefaultFootball implements Football
{
	private Location loc;
	private LinearVelocity lv;
	private AngularVelocity av;
	private Player playerInPossession;
	
	public DefaultFootball()
	{
		this.loc = new Location(Field.MIDFIELD_X, Field.MIDFIELD_Y, 0);
		this.lv = new LinearVelocity();
		this.av = new AngularVelocity();
	}

	public DefaultFootball(Football football)
	{
		this.loc = football.getLoc();
		this.lv = football.getLV();
		this.av = football.getAV();
		this.playerInPossession = football.getPlayerInPossession();
	}
	
	@Override
	public AngularVelocity getAV()
	{
		return this.av;
	}

	public void setAV(AngularVelocity angularVelocity)
	{
		this.av = angularVelocity;
	}

	@Override
	public Location getLoc()
	{
		return this.loc;
	}

	public void setLoc(Location location)
	{
		this.loc = location;
	}

	@Override
	public LinearVelocity getLV()
	{
		return this.lv;
	}

	public void setLV(LinearVelocity lv)
	{
		this.lv = lv;
	}

	@Override
	public Player getPlayerInPossession()
	{
		return this.playerInPossession;
	}

	@Override
	public void setPlayerInPossession(Player player)
	{
		this.playerInPossession = player;
	}

}
