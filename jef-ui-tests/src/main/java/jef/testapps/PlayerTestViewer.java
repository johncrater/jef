package jef.testapps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.core.events.Messages;
import jef.formations.IFormationPlayer;
import jef.formations.Performance;
import jef.formations.PlayerPosition;
import jef.geometry.AngularVelocity;
import jef.geometry.Conversions;
import jef.geometry.Direction;
import jef.geometry.Field;
import jef.geometry.LineSegment;
import jef.geometry.LinearVelocity;
import jef.geometry.Location;
import jef.movement.DebugShape;
import jef.movement.PlayerId;
import jef.movement.TeamId;
import jef.movement.ball.FootballState;
import jef.movement.player.Path;
import jef.movement.player.PlayerState;
import jef.movement.player.Posture;
import jef.movement.player.Waypoint;
import jef.movement.player.Waypoint.DestinationAction;
import jef.pathfinding.IPathfinder;
import jef.pathfinding.IPlayerSteps;
import jef.pathfinding.PathfindingState;
import jef.pathfinding.blocking.BlockNearestThreat;
import jef.pathfinding.blocking.BlockPlayer;
import jef.pathfinding.blocking.BlockerWaypointPathfinder;
import jef.pathfinding.blocking.BlockersAction;
import jef.pathfinding.blocking.IBlockerPathfinder;
import jef.pathfinding.defenders.DefaultPursueRunner;
import jef.pathfinding.defenders.DefenderWaypointPathfinder;
import jef.pathfinding.defenders.IDefenderPathfinder;
import jef.pathfinding.runners.DefaultEvadeInterceptors;
import jef.pathfinding.runners.IRunnerPathfinder;
import jef.pathfinding.runners.RunForGlory;
import jef.pathfinding.runners.RunnerWaypointPathfinder;
import jef.ui.swt.utils.TransformStack;
import jef.ui.swt.utils.UIUtils;

public class PlayerTestViewer extends AbstractFieldTestViewer<TestViewerPlayer>
{
	private static FontData playerDataFontData = new FontData("Courier New", 8, SWT.NORMAL);
	private static Font playerDataFont;

	private static Font playerFont;
	private static FontData playerFontData = new FontData("Courier New", 16, SWT.NORMAL);

	public static int colorStringToColor(final String colorString)
	{
		if (colorString == null)
			return 0;

		return Integer.parseInt(colorString.substring(1), 16);
	}

	public static void main(final String[] args)
	{
		new PlayerTestViewer().messageLoop();
	}

	private TestViewerPlayer currentPlayer;

	private TestViewerPlayer runner;
	private final Map<PlayerId, TestViewerPlayer> defenders = new HashMap<>();
	private final Map<PlayerId, TestViewerPlayer> blockers = new HashMap<>();
	private final Map<TestViewerPlayer, IPathfinder<TestViewerPlayer>> iPathfinders = new HashMap<>();

	private final DestinationAction nextDestinationAction = DestinationAction.fastStop;

	public PlayerTestViewer()
	{
		super("Player Test Viewer",
				AbstractFieldTestViewer.OPTIONS_SHOW_MOUSE_LOCATION | AbstractFieldTestViewer.OPTIONS_SHOW_PLAYERS
						| AbstractFieldTestViewer.OPTIONS_SHOW_PERFORMANCE
						| AbstractFieldTestViewer.OPTIONS_SHOW_DEBUG_SHAPES);
		PlayerTestViewer.playerFont = new Font(this.getShell().getDisplay(), PlayerTestViewer.playerFontData);
		PlayerTestViewer.playerDataFont = new Font(this.getShell().getDisplay(), PlayerTestViewer.playerDataFontData);

	}

	@Override
	protected Composite createButtons()
	{
		final Composite buttonRow = super.createButtons();
		for (final TestViewerPlayer p : this.getTestState().getPlayers())
		{
			final Composite composite = new Composite(buttonRow, SWT.NONE);
			composite.setLayout(new FillLayout(SWT.VERTICAL));

			final Button b = new Button(composite, SWT.PUSH);
			b.setText("" + p.getFirstName().charAt(0) + p.getLastName().charAt(0));
			b.setData(p);
			b.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(final SelectionEvent e)
				{
					PlayerTestViewer.this.currentPlayer = (TestViewerPlayer) b.getData();
				}
			});

			final Combo strategyCombo = new Combo(composite, SWT.DROP_DOWN);

			strategyCombo.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(final SelectionEvent e)
				{
					PlayerTestViewer.this.addPathfinder(p, strategyCombo.getItem(strategyCombo.getSelectionIndex()));
				}
			});

			if (p == this.runner)
			{
				strategyCombo.add("Evade Interceptors");
				strategyCombo.add("Waypoint");
				strategyCombo.add("Run For Glory");
				strategyCombo.add("No Action");
			}
			else if (this.defenders.containsValue(p))
			{
				strategyCombo.add("Pursue Runner");
				strategyCombo.add("Waypoint");
				strategyCombo.add("No Action");
			}
			else if (this.blockers.containsValue(p))
			{
				strategyCombo.add("Group Action");
				strategyCombo.add("Nearest dist");
				strategyCombo.add("Nearest int");
				strategyCombo.add("Nearest runr");
				strategyCombo.add("Waypoint");

				for (final TestViewerPlayer defender : this.defenders.values())
				{
					strategyCombo.add("Block " + defender.getId());
				}
			}
			else
			{
				continue;
			}

			strategyCombo.select(0);
			this.addPathfinder(p, strategyCombo.getItem(0));
		}

		return buttonRow;
	}

	@Override
	protected void createCanvas()
	{
		super.createCanvas();

		final Canvas canvas = this.getCanvas();
		canvas.addMouseListener(new MouseAdapter()
		{

			@Override
			public void mouseUp(final MouseEvent e)
			{
				super.mouseUp(e);

				final Point p = new Point(e.x, e.y);

				try (FieldTransformStack ts = new FieldTransformStack(canvas,
						PlayerTestViewer.this.getMidfieldLocation(), PlayerTestViewer.this.getScaleAdjustment()))
				{
					if ((e.stateMask & SWT.CONTROL) == 0)
					{
						final Location loc = ts.transformToLocation(p);
						final PlayerState playerState = PlayerTestViewer.this.getTestState()
								.getPlayerState(PlayerTestViewer.this.currentPlayer.getId());
						final Path path = new Path(loc, playerState.getMaxSpeed(),
								PlayerTestViewer.this.nextDestinationAction,
								playerState.getSpeedMatrix().getJoggingSpeed(), null);
						PlayerTestViewer.this.getTestState().setPath(PlayerTestViewer.this.currentPlayer.getId(), path);
					}
				}
				catch (final Exception e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		});

	}

	@Override
	protected PathfindingState<TestViewerPlayer> createTestState()
	{
		return new TestPlayers(Performance.frameInterval);
	}

	public void init()
	{
		// offense

		TeamId vikingsOffense = TeamId.getId("1976 Minnesota Vikings Offense");
		TeamId vikingsDefense = TeamId.getId("1976 Minnesota Vikings Defense");

		final Location scrimmage = new Location(Field.yardLine(30, Direction.west), Field.MIDFIELD_Y);

		TestViewerPlayer pl = new TestViewerPlayer(PlayerPosition.RB);
		pl.setFirstName("Chuck");
		pl.setLastName("Foreman");
		pl.setWeight(210);
		pl.setTeamId(vikingsOffense);
		PlayerState playerState = new PlayerState(pl.getId(), pl.getSpeedMatrix(), new LinearVelocity(Math.PI, 0, 0),
				scrimmage.add(5, 0, 0), new AngularVelocity(Math.PI, 0, 0),
				Posture.upright);
		getTestState().addPlayer(pl, playerState);
		this.runner = this.currentPlayer = pl;

		this.getTestState().addFootball(FootballState.beginGame()
				.turnover(vikingsOffense, this.runner.getId(), Direction.west));

		pl = new TestViewerPlayer(PlayerPosition.RG);
		pl.setFirstName("Ed");
		pl.setLastName("White");
		pl.setWeight(250);
		pl.setTeamId(vikingsOffense);
		playerState = new PlayerState(pl.getId(), pl.getSpeedMatrix(), new LinearVelocity(),
				scrimmage.add(IFormationPlayer.SIZE / 2, -2, 0),
				new AngularVelocity(Math.PI, 0, 0), Posture.upright);
		getTestState().addPlayer(pl, playerState);
		this.blockers.put(pl.getId(), pl);

		pl = new TestViewerPlayer(PlayerPosition.C);
		pl.setFirstName("Mick");
		pl.setLastName("Tinglehoff");
		pl.setWeight(270);
		pl.setTeamId(vikingsOffense);
		playerState = new PlayerState(pl.getId(), pl.getSpeedMatrix(), new LinearVelocity(),
				scrimmage.add(IFormationPlayer.SIZE / 2, 0, 0),
				new AngularVelocity(Math.PI, 0, 0), Posture.upright);
		getTestState().addPlayer(pl, playerState);
		this.blockers.put(pl.getId(), pl);

		pl = new TestViewerPlayer(PlayerPosition.RT);
		pl.setFirstName("Ron");
		pl.setLastName("Yary");
		pl.setWeight(260);
		pl.setTeamId(vikingsOffense);
		playerState = new PlayerState(pl.getId(), pl.getSpeedMatrix(), new LinearVelocity(),
				scrimmage.add(IFormationPlayer.SIZE / 2, 2, 0),
				new AngularVelocity(Math.PI, 0, 0), Posture.upright);
		getTestState().addPlayer(pl, playerState);
		this.blockers.put(pl.getId(), pl);

		// defense
		pl = new TestViewerPlayer(PlayerPosition.DT);
		pl.setFirstName("Alan");
		pl.setLastName("Page");
		pl.setWeight(280);
		pl.setTeamId(vikingsDefense);
		playerState = new PlayerState(pl.getId(), pl.getSpeedMatrix(), new LinearVelocity(),
				scrimmage.add(- 1 - (IFormationPlayer.SIZE / 2), 2, 0),
				new AngularVelocity(0, 0, 0), Posture.upright);
		getTestState().addPlayer(pl, playerState);
		this.defenders.put(pl.getId(), pl);

		pl = new TestViewerPlayer(PlayerPosition.DE);
		pl.setFirstName("Carl");
		pl.setLastName("Eller");
		pl.setWeight(280);
		pl.setTeamId(vikingsDefense);
		playerState = new PlayerState(pl.getId(), pl.getSpeedMatrix(), new LinearVelocity(), 
				scrimmage.add(-1 - TestViewerPlayer.SIZE / 2, -2, 0),
				new AngularVelocity(0, 0, 0), Posture.upright);
		getTestState().addPlayer(pl, playerState);
		this.defenders.put(pl.getId(), pl);

		pl = new TestViewerPlayer(PlayerPosition.LLB);
		pl.setFirstName("Matt");
		pl.setLastName("Blair");
		pl.setWeight(255);
		pl.setTeamId(vikingsDefense);
		playerState = new PlayerState(pl.getId(), pl.getSpeedMatrix(), new LinearVelocity(),
				scrimmage.add(-6, -2, 0), new AngularVelocity(0, 0, 0),
				Posture.upright);
		getTestState().addPlayer(pl, playerState);
		this.defenders.put(pl.getId(), pl);

		pl = new TestViewerPlayer(PlayerPosition.RLB);
		pl.setFirstName("Wally");
		pl.setLastName("Hilgenberg");
		pl.setWeight(255);
		pl.setTeamId(vikingsDefense);
		playerState = new PlayerState(pl.getId(), pl.getSpeedMatrix(), new LinearVelocity(), 
				scrimmage.add(-6, 2, 0),
				new AngularVelocity(0, 0, 0), Posture.upright);
		getTestState().addPlayer(pl, playerState);
		this.defenders.put(pl.getId(), pl);
	}

	@Override
	protected void drawPlayer(final FieldTransformStack fts, final PlayerState playerState)
	{
		TestViewerPlayer player = this.getTestState().getPlayer(playerState.getPlayerId());

		final int lineWidth = 3;
		final int offset = (int) Conversions.yardsToInches((IFormationPlayer.SIZE) / 2.0);

		final GC gc = fts.getGC();
		gc.setFont(PlayerTestViewer.playerFont);
		final Point p = UIUtils.locationToPoint(playerState.getLoc());

		if (this.defenders.containsKey(playerState.getPlayerId()))
		{
			gc.setForeground(this.getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
			gc.setBackground(this.getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_MAGENTA));
			gc.fillOval(p.x - offset, p.y - offset, offset * 2, offset * 2);
		}
		else if (this.blockers.containsKey(playerState.getPlayerId())
				|| (this.runner.getId() == playerState.getPlayerId()))
		{
			gc.setForeground(this.getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_MAGENTA));
			gc.setBackground(this.getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
			gc.fillOval(p.x - offset, p.y - offset, offset * 2, offset * 2);
		}

		if (playerState.getPlayerId() == this.currentPlayer.getId())
		{
			gc.setForeground(this.getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
			gc.setLineWidth(lineWidth);
			gc.drawOval(p.x - offset, p.y - offset, offset * 2, offset * 2);
		}

		fts.push();
		fts.translate(p);
		fts.rotate(playerState.getAV().getOrientation());
		fts.set();
		gc.fillPolygon(new int[]
		{ 0, -offset + lineWidth, 0, offset - lineWidth, (2 * offset) - (2 * lineWidth), 0 });

		fts.pop();

		final String playerNumber = "" + player.getFirstName().charAt(0) + player.getLastName().charAt(0);
		final Point extent = gc.textExtent(playerNumber);

		gc.drawText(playerNumber, p.x - (extent.x / 2), p.y - (extent.y / 2), true);
	}

	@Override
	protected void drawPostTransformedCanvas(final TransformStack ts)
	{
		super.drawPostTransformedCanvas(ts);
		this.drawSelectedPlayerData(ts);
	}

	@Override
	protected void process()
	{
		if (this.iPathfinders.size() > 0)
		{
			final IPathfinder<TestViewerPlayer> iRunnerPathfinder = this.getIRunnerPathfinder();

			if (iRunnerPathfinder != null)
			{
				this.getTestState().advance(this.getTestState().getFootballState());

				final PlayerState runnerState = this.getTestState().getPlayerState(this.runner.getId());
				if ((runnerState.getPosture() == Posture.onTheGround) || !runnerState.getLoc().isInBounds()
						|| runnerState.getLoc().isInEndZone(null))
				{
					this.iPathfinders.clear();
				}
			}

			this.drawPath(this.runner, "#00FF0000");
			this.defenders.values().forEach(p -> this.drawPath(p, "#FF000000"));
			this.blockers.values().forEach(p -> this.drawPath(p, "#0000FF00"));
		}
	}

	private void addPathfinder(final TestViewerPlayer player, final String pathfinderName)
	{
		this.iPathfinders.remove(player);

		if (player == this.runner)
		{
			if ("Evade Interceptors".equals(pathfinderName))
			{
				this.iPathfinders.put(player, new DefaultEvadeInterceptors<TestViewerPlayer>(this.getTestState(),
						player, this.defenders.values(), this.blockers.values()));
			}
			else if ("Waypoint".equals(pathfinderName))
			{
				this.iPathfinders.put(player,
						new RunnerWaypointPathfinder<TestViewerPlayer>(this.getTestState(), player));
			}
			else if ("Run For Glory".equals(pathfinderName))
			{
				this.iPathfinders.put(player, new RunForGlory<TestViewerPlayer>(this.getTestState(), player));
			}
		}
		else if (this.defenders.containsValue(player))
		{
			if ("Pursue Runner".equals(pathfinderName))
			{
				this.iPathfinders.put(player,
						new DefaultPursueRunner<TestViewerPlayer>(this.getTestState(), player, this.runner));
			}
			else if ("Waypoint".equals(pathfinderName))
			{
				this.iPathfinders.put(player,
						new DefenderWaypointPathfinder<TestViewerPlayer>(this.getTestState(), player));
			}
		}
		else if (this.blockers.containsValue(player))
		{
			if ("GroupAction".equals(pathfinderName))
			{

			}
			else if ("Nearest dist".equals(pathfinderName))
			{
				this.iPathfinders.put(player, new BlockNearestThreat<TestViewerPlayer>(this.getTestState(), this.runner,
						player, this.defenders.values(), BlockNearestThreat.Option.distance));
			}
			else if ("Nearest int".equals(pathfinderName))
			{
				this.iPathfinders.put(player, new BlockNearestThreat<TestViewerPlayer>(this.getTestState(), this.runner,
						player, this.defenders.values(), BlockNearestThreat.Option.interception));
			}
			else if ("Nearest runr".equals(pathfinderName))
			{
				this.iPathfinders.put(player, new BlockNearestThreat<TestViewerPlayer>(this.getTestState(), this.runner,
						player, this.defenders.values(), BlockNearestThreat.Option.distanceToRunner));
			}
			else if ("Waypoint".equals(pathfinderName))
			{
				this.iPathfinders.put(player,
						new BlockerWaypointPathfinder<TestViewerPlayer>(this.getTestState(), player));
			}
			else if (pathfinderName.startsWith("Block "))
			{
				this.iPathfinders.put(player, new BlockPlayer<TestViewerPlayer>(this.getTestState(), player,
						this.defenders.get(PlayerId.getId(pathfinderName.substring(6).trim()))));
			}
		}
	}

	private void drawPath(final TestViewerPlayer formationPlayer, final String color)
	{
		final Path playerPath = this.getTestState().getPath(formationPlayer.getId());
		if (playerPath == null)
			return;

		final List<Location> locs = new ArrayList<>(
				playerPath.getWaypoints().stream().map(Waypoint::getWaypointDestination).toList());
		locs.addFirst(this.getTestState().getPlayerState(formationPlayer.getId()).getLoc());
		for (int i = 1; i < locs.size(); i++)
		{
			MessageManager.getInstance().dispatchMessage(Messages.drawDebugShape,
					DebugShape.drawLineSegment(new LineSegment(locs.get(i - 1), locs.get(i)), color));
		}
	}

	private void drawSelectedPlayerData(final TransformStack ts)
	{
		final PlayerState playerState = this.getTestState().getPlayerState(this.currentPlayer.getId());
		final StringBuilder str = new StringBuilder();
		str.append(String.format("Name            : %s %s\n", this.currentPlayer.getFirstName(),
				this.currentPlayer.getLastName()));
		str.append(String.format("Location        : %s\n", playerState.getLoc()));
		str.append(String.format("Linear velocity : %s\n", playerState.getLV()));
		str.append(String.format("Angular velocity: %s\n", playerState.getAV()));
		str.append(String.format("Posture		  : %s\n", playerState.getPosture()));
		str.append(String.format("\n"));

		final Path path = this.getTestState().getPath(this.currentPlayer.getId());
		if (path != null)
		{
			final IPlayerSteps playerSteps = this.getTestState().getPlayerSteps(this.currentPlayer.getId());
			for (final Waypoint wp : path.getWaypoints())
			{
				str.append(String.format("       waypoint : %s - Steps: %d\n", wp,
						playerSteps.getStepsToLocation(wp.getWaypointDestination())));
			}
		}

		ts.setFont(PlayerTestViewer.playerDataFont);
		ts.setForeground(SWT.COLOR_YELLOW);
		ts.setBackground(this.getShell().getDisplay().getSystemColor(SWT.COLOR_BLACK));
		ts.drawText(str.toString(), ts.transformToLocation(new Point(1400, 20)), false);
	}

	private List<IPathfinder<TestViewerPlayer>> getIBlockerPathfinders()
	{
		return this.iPathfinders.values().stream().filter(IBlockerPathfinder.class::isInstance).toList();
	}

	private List<IPathfinder<TestViewerPlayer>> getIDefenderPathfinders()
	{
		return this.iPathfinders.values().stream().filter(IDefenderPathfinder.class::isInstance).toList();
	}

	private List<TestViewerPlayer> getGroupBlockingPlayers()
	{
		final List<TestViewerPlayer> pathfinderPlayers = this.getIBlockerPathfinders().stream()
				.map(pf -> pf.getPlayer()).toList();
		return this.blockers.values().stream().filter(blocker -> !pathfinderPlayers.contains(blocker)).toList();
	}

	private IPathfinder<TestViewerPlayer> getIRunnerPathfinder()
	{
		return this.iPathfinders.values().stream().filter(IRunnerPathfinder.class::isInstance).findFirst().orElse(null);
	}

	private class TestPlayers extends PathfindingState<TestViewerPlayer>
	{

		public TestPlayers(double timeInterval)
		{
			super(timeInterval);
		}

		@Override
		protected void determinePaths()
		{
			final IPathfinder<TestViewerPlayer> iRunnerPathfinder = PlayerTestViewer.this.getIRunnerPathfinder();
			final List<IPathfinder<TestViewerPlayer>> iDefenderPathfinders = PlayerTestViewer.this
					.getIDefenderPathfinders();

			final Path newRunnerPath = iRunnerPathfinder.calculatePath();
			this.setPath(PlayerTestViewer.this.runner.getId(), newRunnerPath);

			for (final IPathfinder<TestViewerPlayer> pf : iDefenderPathfinders)
			{
				final Path path = pf.calculatePath();
				this.setPath(pf.getPlayer().getId(), path);
			}

			final List<IPathfinder<TestViewerPlayer>> iBlockerPathfinders = PlayerTestViewer.this
					.getIBlockerPathfinders();

			for (final IPathfinder<TestViewerPlayer> pf : iBlockerPathfinders)
			{
				final Path path = pf.calculatePath();
				this.setPath(pf.getPlayer().getId(), path);
			}

			final BlockersAction<TestViewerPlayer> blockersAction = new BlockersAction<TestViewerPlayer>(this,
					PlayerTestViewer.this.runner, PlayerTestViewer.this.defenders.values(),
					PlayerTestViewer.this.getGroupBlockingPlayers());
			blockersAction.move();

			PlayerTestViewer.this.getGroupBlockingPlayers().stream()
					.filter(blocker -> blockersAction.getPath(blocker) != null)
					.forEach(blocker -> this.setPath(blocker.getId(), blockersAction.getPath(blocker)));
		}

	}
}
