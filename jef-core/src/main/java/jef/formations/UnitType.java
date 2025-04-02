package jef.formations;

public enum UnitType
{
	OFFENSE("Offense"), DEFENSE("Defense"), KICKOFF("Kickoff"), KICKOFF_RETURN("Kickoff Return");

	private final String name;

	private UnitType(final String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return this.name;
	}

}
