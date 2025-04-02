package jef.movement;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TeamId implements Comparable<TeamId>
{
	public static final TeamId NONE = new TeamId("NONE");
	
	private static Map<String, TeamId> stringToId = new HashMap<>();
	
	public static final TeamId getId(String id)
	{
		TeamId ret = stringToId.get(id);
		if (ret == null)
		{
			ret = new TeamId(id);
			stringToId.put(id, ret);
		}
		
		return ret;
	}
	
	private String id;
	
	private TeamId(String teamId)
	{
		this.id = teamId;
	}

	public String getId()
	{
		return this.id;
	}

	@Override
	public int compareTo(TeamId o)
	{
		return id.compareTo(o.id);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id);
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
		TeamId other = (TeamId) obj;
		return Objects.equals(this.id, other.id);
	}

	@Override
	public String toString()
	{
		return this.id;
	}

}
