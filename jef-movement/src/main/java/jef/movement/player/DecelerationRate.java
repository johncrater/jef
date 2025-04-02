package jef.movement.player;

public enum DecelerationRate
{
	INSTANT(Double.MIN_VALUE), MAXIMUM(-6), RAPID(-5), NORMAL(-4), LEISURELY(-3), SLOW(-2), MINIMAL(-1), NONE(0);

	private final double rate;

	private DecelerationRate(final double rate)
	{
		this.rate = rate;
	}

	public double getRate()
	{
		return this.rate;
	}

}