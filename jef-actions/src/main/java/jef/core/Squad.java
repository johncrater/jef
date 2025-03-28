package jef.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Squad
{
	private Unit unit;
	private String name;
	
	private Map<String, Player> positionNameToPlayer = new HashMap<>();
	private Map<Player, String> playerToPositionName = new HashMap<>();
	
	public Squad(Unit unit, String name)
	{
		super();
		this.unit = unit;
		this.name = name;
		this.unit.addSquad(this);
	}
	
	public Unit getUnit()
	{
		return this.unit;
	}

	public String getName()
	{
		return this.name;
	}

	public Map<String, Player> getPositionNameToPlayer()
	{
		return Collections.unmodifiableMap(this.positionNameToPlayer);
	}

	public Map<Player, String> getPlayerToPositionName()
	{
		return Collections.unmodifiableMap(this.playerToPositionName);
	}

	public void addPlayer(String positionName, Player player)
	{
		positionNameToPlayer.put(positionName, player);
		playerToPositionName.put(player, positionName);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(name, unit);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Squad other = (Squad) obj;
		return Objects.equals(this.name, other.name) && Objects.equals(this.unit, other.unit);
	}

	@Override
	public String toString()
	{
		return "Squad [name=" + this.name + ", unit=" + this.unit + "]";
	}
	
	
}
