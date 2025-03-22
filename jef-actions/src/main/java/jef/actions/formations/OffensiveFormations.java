package jef.actions.formations;

import java.util.HashMap;
import java.util.Map;

import jef.core.Location;
import jef.core.PlayerPosition;

public class OffensiveFormations
{
	private final Map<String, Formation> standardFormations = new HashMap<>();

	public OffensiveFormations()
	{
		this.createStandardFormations();
	}

	public Formation createProSet()
	{
		final Formation formation = new Formation("Pro Set", true);

		// 5 offensive linemen
		this.addStandardLine(formation, FormationPosition.OFFENSIVE_LINE_WIDTH_STANDARD_SPLIT);

		// tight end
		formation.addPosition(new FormationPosition(new Location(FormationPosition.DIM_DEPTH_OFFSENSIVE_LINE,
				-FormationPosition.OFFENSIVE_LINE_WIDTH_STANDARD_SPLIT * 3), PlayerPosition.TE));

		// X receiver
		formation.addPosition(new FormationPosition(new Location(FormationPosition.DIM_DEPTH_OFFSENSIVE_LINE,
				FormationPosition.OFFENSIVE_LINE_WIDTH_X_RECEIVER), PlayerPosition.SE));

		// Z receiver
		formation.addPosition(new FormationPosition(
				new Location(FormationPosition.DIM_DEPTH_SLOT, FormationPosition.OFFENSIVE_LINE_WIDTH_Z_RECEIVER),
				PlayerPosition.SE));

		// QB
		formation.addPosition(new FormationPosition(new Location(FormationPosition.DIM_DEPTH_QB_BEHIND_CENTER, 0),
				PlayerPosition.QB));

		// RB
		formation.addPosition(new FormationPosition(
				new Location(FormationPosition.DIM_DEPTH_TB, FormationPosition.OFFENSIVE_LINE_WIDTH_STANDARD_SPLIT),
				PlayerPosition.RB));
		formation.addPosition(new FormationPosition(
				new Location(FormationPosition.DIM_DEPTH_TB, -FormationPosition.OFFENSIVE_LINE_WIDTH_STANDARD_SPLIT),
				PlayerPosition.RB));

		return formation;
	}

	public Formation getFormation(final String name)
	{
		return this.standardFormations.get(name);
	}

	private void createStandardFormations()
	{
		final Formation formation = this.createProSet();
		this.standardFormations.put(formation.getName(), formation);
	}

	protected void addStandardLine(final Formation formation, final double split)
	{
		formation.addPosition(
				new FormationPosition(new Location(FormationPosition.DIM_DEPTH_CENTER, 0), PlayerPosition.C));
		formation.addPosition(new FormationPosition(new Location(FormationPosition.DIM_DEPTH_OFFSENSIVE_LINE, split),
				PlayerPosition.LG));
		formation.addPosition(new FormationPosition(
				new Location(FormationPosition.DIM_DEPTH_OFFSENSIVE_LINE, split * 2), PlayerPosition.LT));
		formation.addPosition(new FormationPosition(new Location(FormationPosition.DIM_DEPTH_OFFSENSIVE_LINE, -split),
				PlayerPosition.RG));
		formation.addPosition(new FormationPosition(
				new Location(FormationPosition.DIM_DEPTH_OFFSENSIVE_LINE, -split * 2), PlayerPosition.RT));
	}
}
