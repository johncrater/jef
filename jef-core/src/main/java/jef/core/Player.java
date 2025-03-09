package jef.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jef.core.movement.player.SpeedMatrix;

public class Player
{
	public enum DecelerationRate
	{
		INSTANT(Conversions.metersToYards(Double.MIN_VALUE)),
		MAXIMUM(Conversions.metersToYards(-6)),
		RAPID(Conversions.metersToYards(-5)),
		NORMAL(Conversions.metersToYards(-4)),
		LEISURELY(Conversions.metersToYards(-3)),
		SLOW(Conversions.metersToYards(-2)), 
		MINIMAL(Conversions.metersToYards(-1)), 
		NONE(Conversions.metersToYards(0));

		private double rate;

		private DecelerationRate(double rate)
		{
			this.rate = rate;
		}

		public double getRate()
		{
			return this.rate;
		}

	}

	// turning speed in milliseconds for changing orientation. A total guess 180
	// degree turn in .25 seconds
	public static final double MAXIMUM_ANGULAR_VELOCITY = 180 / .25;

	// suspect players are faster. But this is a general idea
	public static final double VISUAL_REACTION_TIME = .200;
	public static final double AUDITORY_REACTION_TIME = .150;

	public static final double SIZE = 1.0;

	// Grab him
	public static final double PLAYER_TACKLE_RADIUS_LOW_ODDS = SIZE / 2;

	// Arm Tackle
	public static final double PLAYER_TACKLE_RADIUS_MEDIUM_ODDS = SIZE / 3;

	// slam into him
	public static final double PLAYER_TACKLE_RADIUS_HIGH_ODDS = SIZE / 4;

	// hold him off
	public static final double PLAYER_BLOCKING_RANGE_MAXIMUM = SIZE * 3;

	// push him
	public static final double PLAYER_BLOCKING_RANGE_NORMAL = SIZE * 2;

	// slam into him
	public static final double PLAYER_BLOCKING_RANGE_MINIMAL = SIZE;

	// standard distance to run with the runner while blocking
	public static final double PLAYER_STANDARD_BLOCKING_DISTANCE = 3;

	public static final double PLAYER_CATCH_BALL_RADIUS_LOW_ODDS = SIZE * 2;
	public static final double PLAYER_CATCH_BALL_RADIUS_MEDIUM_ODDS = SIZE;
	public static final double PLAYER_CATCH_BALL_RADIUS_HIGH_ODDS = SIZE / 2;

	public static final double PLAYER_CATCH_BALL_IDEAL_HEIGHT = 1.0f;

	// suspect players are faster. But this is a general idea
	public static final double PLAYER_AVG_HUMAN_VISUAL_REACTION_TIME = .200f;
	public static final double PLAYER_AVG_HUMAN_AUDITORY_REACTION_TIME = .150f;

	private int weight;
	private String id;
	private String firstName;
	private String lastName;
	private int height;
	private int age;
	private int number;

	private PlayerRatings ratings;

	private PlayerPosition currentPosition;
	private final List<PlayerPosition> positions = new ArrayList<>();

	public Player(PlayerPosition currentPosition)
	{
		this.setCurrentPosition(currentPosition);
	}

	public Player(String firstName, String lastName, PlayerPosition currentPosition)
	{
		this.setFirstName(firstName);
		this.setLastName(lastName);
		this.setCurrentPosition(currentPosition);
	}

	public SpeedMatrix getSpeedMatrix()
	{
		return new SpeedMatrix(this.currentPosition);
	}
	
	public int getAge()
	{
		return this.age;
	}

	public String getFirstName()
	{
		return this.firstName;
	}

	
	public int getHeight()
	{
		return this.height;
	}

	
	public String getLastName()
	{
		return this.lastName;
	}

	
	public int getNumber()
	{
		return this.number;
	}

	
	public String getPlayerID()
	{
		if (this.id == null)
			return lastName + firstName;
		
		return this.id;
	}

	
	public List<PlayerPosition> getPositions()
	{
		return Collections.unmodifiableList(this.positions);
	}

	
	public PlayerPosition getPrimaryPosition()
	{
		if (this.positions.size() > 0)
			return this.positions.getFirst();

		return null;
	}

	
	public PlayerRatings getRatings()
	{
		return this.ratings;
	}

	
	public PlayerPosition getSecondaryPosition()
	{
		if (this.positions.size() > 1)
			return this.positions.get(1);

		return null;
	}

	
	public PlayerPosition getTertiaryPosition()
	{
		if (this.positions.size() > 2)
			return this.positions.get(2);

		return null;
	}

	
	public int getWeight()
	{
		return this.weight;
	}

	
	public void setAge(final int age)
	{
		this.age = age;
	}

	
	public void setFirstName(final String firstName)
	{
		this.firstName = firstName;
	}

	
	public void setHeight(final int height)
	{
		this.height = height;
	}

	public void setLastName(final String lastName)
	{
		this.lastName = lastName;
	}

	
	public void setNumber(final int number)
	{
		this.number = number;
	}

	public void setRatings(final PlayerRatings ratings)
	{
		this.ratings = ratings;
	}

	
	public void setWeight(final int weight)
	{
		this.weight = weight;
	}

	
	public PlayerPosition getCurrentPosition()
	{
		return this.currentPosition;
	}

	
	public void setCurrentPosition(PlayerPosition pos)
	{
		this.currentPosition = pos;
	}

	
	public String toString()
	{
		return this.firstName + " " + this.lastName;
	}

}
