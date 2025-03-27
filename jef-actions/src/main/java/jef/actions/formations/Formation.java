package jef.actions.formations;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Formation implements Iterable<FormationPosition>
{
	private final String name;
	private final Map<String, FormationPosition> formationPositions = new HashMap<>();

	public Formation(final String name)
	{
		this.name = name;
	}

	public Formation addPosition(final FormationPosition pos)
	{
		this.formationPositions.put(pos.getName(), pos);
		return this;
	}

	@Override
	public Iterator<FormationPosition> iterator()
	{
		return this.formationPositions.values().iterator();
	}

	public Collection<FormationPosition> getFormationPositions()
	{
		return Collections.unmodifiableCollection(this.formationPositions.values());
	}

	public FormationPosition getPosition(String name)
	{
		return this.formationPositions.get(name);
	}
	
	public String getName()
	{
		return this.name;
	}
}