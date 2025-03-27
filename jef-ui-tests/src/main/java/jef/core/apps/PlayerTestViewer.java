package jef.core.apps;

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

import jef.core.Player;
import jef.core.PlayerPosition;
import jef.core.events.Messages;
import jef.geometry.AngularVelocity;
import jef.geometry.Conversions;
import jef.geometry.Direction;
import jef.geometry.Field;
import jef.geometry.LineSegment;
import jef.geometry.Location;
import jef.movement.DebugShape;
import jef.movement.ball.Football;
import jef.movement.player.Path;
import jef.movement.player.PlayerState;
import jef.movement.player.Waypoint;
import jef.movement.player.Waypoint.DestinationAction;
import jef.pathfinding.Pathfinder;
import jef.pathfinding.Players;
import jef.pathfinding.Players.PlayerSteps;
import jef.pathfinding.blocking.BlockNearestThreat;
import jef.pathfinding.blocking.BlockPlayer;
import jef.pathfinding.blocking.BlockerPathfinder;
import jef.pathfinding.blocking.BlockerWaypointPathfinder;
import jef.pathfinding.blocking.BlockersAction;
import jef.pathfinding.defenders.DefaultPursueRunner;
import jef.pathfinding.defenders.DefenderPathfinder;
import jef.pathfinding.defenders.DefenderWaypointPathfinder;
import jef.pathfinding.runners.DefaultEvadeInterceptors;
import jef.pathfinding.runners.RunForGlory;
import jef.pathfinding.runners.RunnerPathfinder;
import jef.pathfinding.runners.RunnerWaypointPathfinder;
import jef.ui.swt.utils.TransformStack;
import jef.ui.swt.utils.UIUtils;

public class PlayerTestViewer extends AbstractFieldTestViewer
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

	private Player currentPlayer;

	private Player runner;
	private final Map<String, Player> defenders = new HashMap<>();
	private final Map<String, Player> blockers = new HashMap<>();
	private final Map<Player, Pathfinder> pathfinders = new HashMap<>();

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
		for (final Player p : this.getPlayers().getPlayers())
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
					PlayerTestViewer.this.currentPlayer = (Player) b.getData();
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

				for (final Player defender : this.defenders.values())
				{
					strategyCombo.add("Block " + defender.getPlayerID());
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
						final PlayerState playerState = PlayerTestViewer.this.getPlayers()
								.getState(PlayerTestViewer.this.currentPlayer);
						final Path path = new Path(loc, playerState.getPlayer().getMaxSpeed(),
								PlayerTestViewer.this.nextDestinationAction,
								playerState.getPlayer().getSpeedMatrix().getJoggingSpeed(), null);
						PlayerTestViewer.this.getPlayers().setPath(PlayerTestViewer.this.currentPlayer, path);
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
	protected Players createPlayers()
	{
		final Players players = new TestPlayers();
		// offense

		final double lineOfScrimmage = Field.yardLine(30, Direction.west);

		Player pl = new Player(PlayerPosition.RB);
		pl.setFirstName("Chuck");
		pl.setLastName("Foreman");
		pl.setWeight(215);

		PlayerState playerState = new PlayerState(pl, null, new Location(lineOfScrimmage + 5, Field.MIDFIELD_Y, 0),
				new AngularVelocity(Math.PI, 0, 0), PlayerState.Posture.upright);
		players.addPlayer(playerState);
		this.runner = this.currentPlayer = pl;
		Football.theFootball.setPlayerInPossession(this.runner);

		pl = new Player(PlayerPosition.RG);
		pl.setFirstName("Ed");
		pl.setLastName("White");
		pl.setWeight(250);
		playerState = new PlayerState(pl, null,
				new Location(lineOfScrimmage + (Player.SIZE / 2), Field.MIDFIELD_Y - 2, 0),
				new AngularVelocity(Math.PI, 0, 0), PlayerState.Posture.upright);
		players.addPlayer(playerState);
		this.blockers.put(pl.getPlayerID(), pl);

		pl = new Player(PlayerPosition.C);
		pl.setFirstName("Mick");
		pl.setLastName("Tinglehoff");
		pl.setWeight(270);
		playerState = new PlayerState(pl, null, new Location(lineOfScrimmage + (Player.SIZE / 2), Field.MIDFIELD_Y, 0),
				new AngularVelocity(Math.PI, 0, 0), PlayerState.Posture.upright);
		players.addPlayer(playerState);
		this.blockers.put(pl.getPlayerID(), pl);

		pl = new Player(PlayerPosition.RT);
		pl.setFirstName("Ron");
		pl.setLastName("Yary");
		pl.setWeight(260);
		playerState = new PlayerState(pl, null,
				new Location(lineOfScrimmage + (Player.SIZE / 2), Field.MIDFIELD_Y + 2, 0),
				new AngularVelocity(Math.PI, 0, 0), PlayerState.Posture.upright);
		players.addPlayer(playerState);
		this.blockers.put(pl.getPlayerID(), pl);

		// defense
		pl = new Player(PlayerPosition.DT);
		pl.setFirstName("Alan");
		pl.setLastName("Page");
		pl.setWeight(280);
		playerState = new PlayerState(pl, null,
				new Location(lineOfScrimmage - 1 - (Player.SIZE / 2), Field.MIDFIELD_Y + 2, 0),
				new AngularVelocity(0, 0, 0), PlayerState.Posture.upright);
		players.addPlayer(playerState);
		this.defenders.put(pl.getPlayerID(), pl);

//		pl = new Player(PlayerPosition.DE);
//		pl.setFirstName("Carl");
//		pl.setLastName("Eller");
//		pl.setWeight(280);
//		playerState = new PlayerState(pl, null, new Location(lineOfScrimmage - 1 - Player.SIZE / 2, Field.MIDFIELD_Y - 2, 0),
//				new AngularVelocity(0, 0, 0), PlayerState.Posture.upright);
//		players.addPlayer(playerState);
//		this.defenders.put(pl.getPlayerID(), pl);
//
		pl = new Player(PlayerPosition.LLB);
		pl.setFirstName("Matt");
		pl.setLastName("Blair");
		pl.setWeight(255);
		playerState = new PlayerState(pl, null, new Location(lineOfScrimmage - 6, Field.MIDFIELD_Y - 2, 0),
				new AngularVelocity(0, 0, 0), PlayerState.Posture.upright);
		players.addPlayer(playerState);
		this.defenders.put(pl.getPlayerID(), pl);

//		pl = new Player(PlayerPosition.RLB);
//		pl.setFirstName("Wally");
//		pl.setLastName("Hilgenberg");
//		pl.setWeight(255);
//		playerState = new PlayerState(pl, null, new Location(lineOfScrimmage - 6, Field.MIDFIELD_Y + 2, 0),
//				new AngularVelocity(0, 0, 0), PlayerState.Posture.upright);
//		players.addPlayer(playerState);
//		this.defenders.put(pl.getPlayerID(), pl);

		return players;
	}

	@Override
	protected void drawPlayer(final FieldTransformStack fts, final PlayerState player)
	{
		final int lineWidth = 3;
		final int offset = (int) Conversions.yardsToInches((Player.SIZE) / 2.0);

		final GC gc = fts.getGC();
		gc.setFont(PlayerTestViewer.playerFont);
		final Point p = UIUtils.locationToPoint(player.getLoc());

		if (this.defenders.containsValue(player.getPlayer()))
		{
			gc.setForeground(this.getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
			gc.setBackground(this.getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_MAGENTA));
			gc.fillOval(p.x - offset, p.y - offset, offset * 2, offset * 2);
		}
		else if (this.blockers.containsValue(player.getPlayer()) || (this.runner == player.getPlayer()))
		{
			gc.setForeground(this.getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_MAGENTA));
			gc.setBackground(this.getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
			gc.fillOval(p.x - offset, p.y - offset, offset * 2, offset * 2);
		}

		if (player.getPlayer() == this.currentPlayer)
		{
			gc.setForeground(this.getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
			gc.setLineWidth(lineWidth);
			gc.drawOval(p.x - offset, p.y - offset, offset * 2, offset * 2);
		}

//		fts.push();
//		fts.translate(p);
//		fts.rotate(player.getAV().getOrientation());
//		fts.set();
//		gc.fillPolygon(new int[]
//		{ 0, -offset + lineWidth, 0, offset - lineWidth, (2 * offset) - (2 * lineWidth), 0 });
//
//		fts.pop();

		final String playerNumber = "" + player.getPlayer().getFirstName().charAt(0)
				+ player.getPlayer().getLastName().charAt(0);
		final Point extent = gc.textExtent(playerNumber);

		gc.drawText(playerNumber, p.x - (extent.x / 2), p.y - (extent.y / 2), true);

//		try (TransformStack ts = new TransformStack(gc))
//		{
//			ts.translate(p.x, p.y);
//			ts.rotate(Angle.ofDegrees(-90 + Math.toDegrees(player.getAV().getOrientation())));
//			ts.set();
//
//			this.player1Gif.setDisplayPoint(p);
//			this.player1Gif.draw(gc);
////			gc.drawImage(playerImage, -playerImage.getImageData().width / 2, -playerImage.getImageData().width / 2);
//		}
//		catch (Exception e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
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
		if (this.pathfinders.size() > 0)
		{
			final RunnerPathfinder runnerPathfinder = this.getRunnerPathfinder();

			if (runnerPathfinder != null)
			{
				this.getPlayers().advance();

				final PlayerState runnerState = this.getPlayers().getState(this.runner);
				if ((runnerState.getPosture() == PlayerState.Posture.onTheGround) || !runnerState.getLoc().isInBounds()
						|| runnerState.getLoc().isInEndZone(null))
				{
					this.pathfinders.clear();
				}
			}

			this.drawPath(this.runner, "#00FF0000");
			this.defenders.values().forEach(p -> this.drawPath(p, "#FF000000"));
			this.blockers.values().forEach(p -> this.drawPath(p, "#0000FF00"));
		}
	}

	private void addPathfinder(final Player player, final String pathfinderName)
	{
		this.pathfinders.remove(player);

		if (player == this.runner)
		{
			if ("Evade Interceptors".equals(pathfinderName))
			{
				this.pathfinders.put(player, new DefaultEvadeInterceptors(this.getPlayers(), player, Direction.west,
						this.defenders.values(), this.blockers.values()));
			}
			else if ("Waypoint".equals(pathfinderName))
			{
				this.pathfinders.put(player, new RunnerWaypointPathfinder(this.getPlayers(), player, Direction.west));
			}
			else if ("Run For Glory".equals(pathfinderName))
			{
				this.pathfinders.put(player, new RunForGlory(this.getPlayers(), player, Direction.west));
			}
		}
		else if (this.defenders.containsValue(player))
		{
			if ("Pursue Runner".equals(pathfinderName))
			{
				this.pathfinders.put(player,
						new DefaultPursueRunner(this.getPlayers(), player, Direction.west, this.runner));
			}
			else if ("Waypoint".equals(pathfinderName))
			{
				this.pathfinders.put(player, new DefenderWaypointPathfinder(this.getPlayers(), player, Direction.west));
			}
		}
		else if (this.blockers.containsValue(player))
		{
			if ("GroupAction".equals(pathfinderName))
			{

			}
			else if ("Nearest dist".equals(pathfinderName))
			{
				this.pathfinders.put(player, new BlockNearestThreat(this.getPlayers(), this.runner, player,
						this.defenders.values(), BlockNearestThreat.Option.distance, Direction.west));
			}
			else if ("Nearest int".equals(pathfinderName))
			{
				this.pathfinders.put(player, new BlockNearestThreat(this.getPlayers(), this.runner, player,
						this.defenders.values(), BlockNearestThreat.Option.interception, Direction.west));
			}
			else if ("Nearest runr".equals(pathfinderName))
			{
				this.pathfinders.put(player, new BlockNearestThreat(this.getPlayers(), this.runner, player,
						this.defenders.values(), BlockNearestThreat.Option.distanceToRunner, Direction.west));
			}
			else if ("Waypoint".equals(pathfinderName))
			{
				this.pathfinders.put(player, new BlockerWaypointPathfinder(this.getPlayers(), player, Direction.west));
			}
			else if (pathfinderName.startsWith("Block "))
			{
				this.pathfinders.put(player, new BlockPlayer(this.getPlayers(), player, Direction.west,
						this.defenders.get(pathfinderName.substring(6).trim())));
			}
		}
	}

	private void drawPath(final Player player, final String color)
	{
		final Path playerPath = this.getPlayers().getPath(player);
		if (playerPath == null)
			return;

		final List<Location> locs = new ArrayList<>(
				playerPath.getWaypoints().stream().map(Waypoint::getWaypointDestination).toList());
		locs.addFirst(this.getPlayers().getState(player).getLoc());
		for (int i = 1; i < locs.size(); i++)
		{
			MessageManager.getInstance().dispatchMessage(Messages.drawDebugShape,
					DebugShape.drawLineSegment(new LineSegment(locs.get(i - 1), locs.get(i)), color));
		}
	}

	private void drawSelectedPlayerData(final TransformStack ts)
	{
		final PlayerState playerState = this.getPlayers().getState(this.currentPlayer);
		final StringBuilder str = new StringBuilder();
		str.append(String.format("Name            : %s %s\n", this.currentPlayer.getFirstName(),
				this.currentPlayer.getLastName()));
		str.append(String.format("Location        : %s\n", playerState.getLoc()));
		str.append(String.format("Linear velocity : %s\n", playerState.getLV()));
		str.append(String.format("Angular velocity: %s\n", playerState.getAV()));
		str.append(String.format("Posture		  : %s\n", playerState.getPosture()));
		str.append(String.format("\n"));

		final Path path = this.getPlayers().getPath(this.currentPlayer);
		if (path != null)
		{
			final PlayerSteps playerSteps = this.getPlayers().getSteps(this.currentPlayer);
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

	private List<BlockerPathfinder> getBlockerPathfinders()
	{
		return this.pathfinders.values().stream().filter(BlockerPathfinder.class::isInstance)
				.map(pf -> (BlockerPathfinder) pf).toList();
	}

	private List<DefenderPathfinder> getDefenderPathfinders()
	{
		return this.pathfinders.values().stream().filter(DefenderPathfinder.class::isInstance)
				.map(pf -> (DefenderPathfinder) pf).toList();
	}

	private List<Player> getGroupBlockingPlayers()
	{
		final List<Player> pathfinderPlayers = this.getBlockerPathfinders().stream().map(BlockerPathfinder::getPlayer)
				.toList();
		return this.blockers.values().stream().filter(blocker -> !pathfinderPlayers.contains(blocker)).toList();
	}

	private RunnerPathfinder getRunnerPathfinder()
	{
		return (RunnerPathfinder) this.pathfinders.values().stream().filter(RunnerPathfinder.class::isInstance)
				.findFirst().orElse(null);
	}

	private class TestPlayers extends Players
	{

		@Override
		protected void determinePaths()
		{
			final RunnerPathfinder runnerPathfinder = PlayerTestViewer.this.getRunnerPathfinder();
			final List<DefenderPathfinder> defenderPathfinders = PlayerTestViewer.this.getDefenderPathfinders();

			final Path newRunnerPath = runnerPathfinder.calculatePath();
			this.setPath(PlayerTestViewer.this.runner, newRunnerPath);

			for (final Pathfinder pf : defenderPathfinders)
			{
				final Path path = pf.calculatePath();
				this.setPath(pf.getPlayer(), path);
			}

			final List<BlockerPathfinder> blockerPathfinders = PlayerTestViewer.this.getBlockerPathfinders();

			for (final Pathfinder pf : blockerPathfinders)
			{
				final Path path = pf.calculatePath();
				this.setPath(pf.getPlayer(), path);
			}

			final BlockersAction blockersAction = new BlockersAction(this, PlayerTestViewer.this.runner,
					PlayerTestViewer.this.defenders.values(), PlayerTestViewer.this.getGroupBlockingPlayers(),
					Direction.west);
			blockersAction.move();

			PlayerTestViewer.this.getGroupBlockingPlayers().stream()
					.filter(blocker -> blockersAction.getPath(blocker) != null)
					.forEach(blocker -> this.setPath(blocker, blockersAction.getPath(blocker)));
		}

	}
}
