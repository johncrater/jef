package jef.history;

public class Conversions
{
	public static String avgToPctString(final float avg)
	{
		return String.format("%2.0f%s", avg * 100, "%");
	}

	public static String avgToString(final float avg)
	{
		return String.format("%3.1f", avg);
	}

}
