package jef.core;

public abstract class Play
{
	private String name;
	private IFormation formation;
	private Unit unit;
	
	public Play(Unit unit, String name, IFormation formation)
	{
		assert unit.getUnitType() == formation.getUnitType();
		
		this.unit = unit;
		this.name = name;
		this.formation = formation;
		this.unit.addPlay(this);
	}

	public UnitType getUnitType()
	{
		return this.unit.getUnitType();
	}
	
	public String getName()
	{
		return this.name;
	}

	public IFormation getFormation()
	{
		return this.formation;
	}

	public Unit getUnit()
	{
		return this.unit;
	}

	public boolean isValid()
	{
		return this.getName() != null && this.getFormation() != null && this.getFormation().isValid() && this.getUnit() != null;
	}
}
