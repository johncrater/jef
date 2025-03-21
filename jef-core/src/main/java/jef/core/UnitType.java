package jef.core;

public enum UnitType
{
	LOS_OFFENSE(true, false), LOS_DEFENSE(false, false), ST_OFFENSE(true, true), ST_DEFENSE(true, false);

	private final boolean isOffense;
	private final boolean isSpecialTeams;

	private UnitType(final boolean isOffense, final boolean isSpecialTeams)
	{
		this.isOffense = isOffense;
		this.isSpecialTeams = isSpecialTeams;
	}

	public boolean isDefense()
	{
		return !this.isOffense;
	}

	public boolean isOffense()
	{
		return this.isOffense;
	}

	public boolean isSpecialTeams()
	{
		return this.isSpecialTeams;
	}

}
