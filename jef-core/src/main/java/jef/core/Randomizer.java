package jef.core;

import java.util.Random;

public class Randomizer
{
	private static Random random = new Random();
	
	public static double nextDouble()
	{
		return random.nextDouble();
	}
}
