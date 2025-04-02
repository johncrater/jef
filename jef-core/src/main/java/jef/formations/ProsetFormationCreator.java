package jef.formations;

import jef.geometry.Direction;
import jef.geometry.Field;
import jef.geometry.Location;

public class ProsetFormationCreator extends OffensiveFormationCreator
{
	public static final String PROSET = "Pro Set";

	public ProsetFormationCreator()
	{
		super(PROSET, OFFENSIVE_LINE_WIDTH_STANDARD_SPLIT);
	}

	@Override
	public Formation createFormation(Location scrimmage, Direction direction)
	{
		Formation ret = super.createFormation(scrimmage, direction);
		this.addFiveManLine(ret);
		this.addQuarterBackBehindCenter(ret);
		this.addTightEnd(ret, Side.RIGHT);
		this.addSplitBacks(ret, "G", Side.RIGHT);
		this.addXReceiver(ret);
		this.addZReceiver(ret);
		return ret;
	}

	protected void addXReceiver(Formation formation)
	{
		Side side = formation.getPosition("LTE") != null ? Side.RIGHT : Side.LEFT;
		this.addPlayer(formation, "X", formation.getPosition("C").getLocation(),
				createLocation(side, IFormationPlayer.SIZE / 2.0, 15.0), PlayerPosition.WR);
	}

	protected void addZReceiver(Formation formation)
	{
		Side side = formation.getPosition("LTE") != null ? Side.LEFT : Side.RIGHT;
		this.addPlayer(formation, "Z", formation.getPosition("C").getLocation(),
				createLocation(side, 3.0 * IFormationPlayer.SIZE / 2.0, 15.0), PlayerPosition.WR);
	}

	protected double getSidelineY(Direction direction, Side side)
	{
		return direction == Direction.west ? (side == Side.LEFT ? Field.SOUTH_SIDELINE_Y : Field.NORTH_SIDELINE_Y)
				: (side == Side.RIGHT ? Field.SOUTH_SIDELINE_Y : Field.NORTH_SIDELINE_Y);
	}
}
