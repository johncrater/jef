package jef.actions.formations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Formation
{
	private final String name;
	private final boolean isOffense;
	private final List<FormationPosition> formationPositions = new ArrayList<>();

	public Formation(final String name, final boolean isOffense)
	{
		this.name = name;
		this.isOffense = isOffense;
	}

	public Formation(final String name, final boolean isOffense, final FormationPosition... formationPositions)
	{
		this.name = name;
		this.isOffense = isOffense;
		this.formationPositions.addAll(Arrays.asList(formationPositions));
	}

	public Formation addPosition(final FormationPosition pos)
	{
		if (!this.formationPositions.contains(pos))
			this.formationPositions.add(pos);

		return this;
	}

	public Collection<FormationPosition> getFormationPositions()
	{
		return Collections.unmodifiableCollection(this.formationPositions);
	}

	public String getName()
	{
		return this.name;
	}

	public boolean isOffense()
	{
		return this.isOffense;
	}
}