package jef.actions.formations.defensive;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jef.actions.formations.Formation;
import jef.actions.formations.FormationPosition;
import jef.core.Location;
import jef.core.Player;
import jef.core.PlayerPosition;

public class DefensiveFormations implements Iterable<Formation>
{
	private final Map<String, Formation> standardFormations = new HashMap<>();
	private Formation currentFormation;

	public DefensiveFormations()
	{
	}

	public Formation getCurrentFormation()
	{
		return this.currentFormation;
	}

	public void setCurrentFormation(Formation currentFormation)
	{
		this.currentFormation = currentFormation;
	}

	@Override
	public Iterator<Formation> iterator()
	{
		return this.standardFormations.values().iterator();
	}

	public Formation create43()
	{
		final Formation formation = new Formation("43", true);

		formation.addPosition(
				new FormationPosition("LDT", new Location(FormationPosition.DIM_DEPTH_DL, 0), PlayerPosition.C));
		formation.addPosition(new FormationPosition("LG",
				new Location(FormationPosition.DIM_DEPTH_OFFSENSIVE_LINE, split), PlayerPosition.LG));
		formation.addPosition(new FormationPosition("LT",
				new Location(FormationPosition.DIM_DEPTH_OFFSENSIVE_LINE, split * 2), PlayerPosition.LT));
		formation.addPosition(new FormationPosition("RG",
				new Location(FormationPosition.DIM_DEPTH_OFFSENSIVE_LINE, -split), PlayerPosition.RG));

		// 4 defensive linemen
		this.addStandardLine(formation, Player.SIZE + FormationPosition.OFFENSIVE_LINE_WIDTH_STANDARD_SPLIT);

		// tight end
		formation.addPosition(new FormationPosition("TE", new Location(FormationPosition.DIM_DEPTH_OFFSENSIVE_LINE,
				-(Player.SIZE + FormationPosition.OFFENSIVE_LINE_WIDTH_STANDARD_SPLIT) * 3), PlayerPosition.TE));

		// X receiver
		formation.addPosition(new FormationPosition("X", new Location(FormationPosition.DIM_DEPTH_OFFSENSIVE_LINE,
				FormationPosition.OFFENSIVE_LINE_WIDTH_X_RECEIVER), PlayerPosition.WR));

		// Z receiver
		formation.addPosition(new FormationPosition("Z",
				new Location(FormationPosition.DIM_DEPTH_SLOT, FormationPosition.OFFENSIVE_LINE_WIDTH_Z_RECEIVER),
				PlayerPosition.FL));

		// QB
		formation.addPosition(new FormationPosition("QB", new Location(FormationPosition.DIM_DEPTH_QB_BEHIND_CENTER, 0),
				PlayerPosition.QB));

		// RB
		formation.addPosition(new FormationPosition("RB",
				new Location(FormationPosition.DIM_DEPTH_FB, Player.SIZE + FormationPosition.OFFENSIVE_LINE_WIDTH_STANDARD_SPLIT),
				PlayerPosition.RB));
		
		formation.addPosition(new FormationPosition("FB",
				new Location(FormationPosition.DIM_DEPTH_FB, -(Player.SIZE + FormationPosition.OFFENSIVE_LINE_WIDTH_STANDARD_SPLIT)),
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
				new FormationPosition("C", new Location(FormationPosition.DIM_DEPTH_CENTER, 0), PlayerPosition.C));
		formation.addPosition(new FormationPosition("LG",
				new Location(FormationPosition.DIM_DEPTH_OFFSENSIVE_LINE, split), PlayerPosition.LG));
		formation.addPosition(new FormationPosition("LT",
				new Location(FormationPosition.DIM_DEPTH_OFFSENSIVE_LINE, split * 2), PlayerPosition.LT));
		formation.addPosition(new FormationPosition("RG",
				new Location(FormationPosition.DIM_DEPTH_OFFSENSIVE_LINE, -split), PlayerPosition.RG));
		formation.addPosition(new FormationPosition("RT",
				new Location(FormationPosition.DIM_DEPTH_OFFSENSIVE_LINE, -split * 2), PlayerPosition.RT));
	}
}
