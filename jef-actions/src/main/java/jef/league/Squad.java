package jef.league;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Squad
{
	private Unit unit;
	private String name;
	
	private Map<String, List<Player>> positionNameToPlayers = new HashMap<>();
	
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

	public Collection<Formation> getValidFormations()
	{
		HashSet<Formation> ret = new HashSet<>();
		for (Formation formation : this.getUnit().formations())
		{
			if (this.isValidFormation(formation))
				ret.add(formation);
		}
		
		return ret;
	}
	
	public boolean isValidFormation(IFormation formation)
	{
		if (formation.isValid() == false)
			return false;
		
		for (String positionName : formation.positionNames())
		{
			List<Player> players = this.positionNameToPlayers.get(positionName);
			if (players.stream().filter(p -> p!= null).count() == 0)
				return false;
		}
		
		return true;
	}
	
	public Collection<Play> getValidPlays()
	{
		HashSet<Play> ret = new HashSet<>();
		for (Play play : this.getUnit().plays())
		{
			if (this.isValidPlay(play))
				ret.add(play);
		}
		
		return ret;
	}
	
	public boolean isValidPlay(Play play)
	{
		if (play.isValid() == false)
			return false;

		return this.isValidFormation(play.getFormation());
	}
	
	public List<Player> getPlayers(String positionName)
	{
		return Collections.unmodifiableList(this.positionNameToPlayers.get(positionName));
	}

	public void addPlayer(String positionName, int rank, Player player)
	{
		List<Player> players = this.positionNameToPlayers.get(positionName);
		if (players == null)
		{
			players = new ArrayList<>();
			this.positionNameToPlayers.put(positionName, players);
		}
		
		players.add(rank, player);
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
