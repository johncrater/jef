package jef.movement;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PlayerId implements Comparable<PlayerId>
{
	public static final PlayerId NONE = new PlayerId("NONE");

	private static Map<String, PlayerId> stringToId = new HashMap<>();
	
	public static final PlayerId getId(String id)
	{
		PlayerId ret = stringToId.get(id);
		if (ret == null)
		{
			ret = new PlayerId(id);
			stringToId.put(id, ret);
		}
		
		return ret;
	}
	
	private String id;
	
	private PlayerId(String id)
	{
		this.id = id;
	}

	public String toString()
	{
		return this.id;
	}

	@Override
	public int compareTo(PlayerId o)
	{
		return this.id.compareTo(o.id);
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
		PlayerId other = (PlayerId) obj;
		return Objects.equals(this.id, other.id);
	}
}
