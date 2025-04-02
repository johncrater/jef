package jef.formations;

public enum Side
{
	LEFT(1, "L"), RIGHT(-1, "R");
	
	private int yMultiplier;
	private String prefix;

	public int getYMultiplier()
	{
		return this.yMultiplier;
	}

	public String getPrefix()
	{
		return this.prefix;
	}

	private Side(int yMultiplier, String prefix)
	{
		this.yMultiplier = yMultiplier;
		this.prefix = prefix;
	}
	
	public Side getOpposite()
	{
		if (this == LEFT)
			return RIGHT;
		else
			return LEFT;
	}
	
}
