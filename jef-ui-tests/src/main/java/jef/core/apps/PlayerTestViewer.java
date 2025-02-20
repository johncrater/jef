package jef.core.apps;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.badlogic.gdx.ai.msg.MessageManager;

import jef.core.Conversions;
import jef.core.DefaultPlayer;
import jef.core.Field;
import jef.core.Football;
import jef.core.Performance;
import jef.core.Player;
import jef.core.PlayerPosition;
import jef.core.collisions.Collision;
import jef.core.collisions.CollisionResolution;
import jef.core.events.DebugShape;
import jef.core.events.Messages;
import jef.core.geometry.LineSegment;
import jef.core.movement.DefaultAngularVelocity;
import jef.core.movement.DefaultLocation;
import jef.core.movement.Location;
import jef.core.movement.Posture;
import jef.core.movement.index.DefaultLocationIndex;
import jef.core.movement.index.LocationIndex;
import jef.core.movement.player.DefaultPath;
import jef.core.movement.player.PlayerTracker;
import jef.core.movement.player.Steering;
import jef.core.movement.player.Waypoint;
import jef.core.movement.player.Waypoint.DestinationAction;
import jef.core.pathfinding.CalculationScheduler;
import jef.core.pathfinding.Direction;
import jef.core.pathfinding.Pathfinder;
import jef.core.pathfinding.WaypointPathfinder;
import jef.core.pathfinding.blocking.BlockNearestThreat;
import jef.core.pathfinding.blocking.BlockerPathfinder;
import jef.core.pathfinding.blocking.BlockerWaypointPathfinder;
import jef.core.pathfinding.blocking.BlockersAction;
import jef.core.pathfinding.defenders.DefaultPursueRunner;
import jef.core.pathfinding.defenders.DefenderPathfinder;
import jef.core.pathfinding.defenders.DefenderWaypointPathfinder;
import jef.core.pathfinding.runners.DefaultEvadeInterceptors;
import jef.core.pathfinding.runners.RunForGlory;
import jef.core.pathfinding.runners.RunnerPathfinder;
import jef.core.pathfinding.runners.RunnerWaypointPathfinder;
import jef.core.ui.swt.utils.DebugMessageHandler;
import jef.core.ui.swt.utils.GIFMarkup;
import jef.core.ui.swt.utils.TransformStack;

public class PlayerTestViewer implements Runnable
{
	private class PathCalculator extends CalculationScheduler
	{
		@Override
		protected boolean calculate(final Pathfinder pf, final RunnerPathfinder runner,
				final List<? extends DefenderPathfinder> defenders, final List<? extends BlockerPathfinder> blockers,
				final long deltaNanos)
		{
			final boolean ret = super.calculate(pf, runner, defenders, blockers, deltaNanos);
			((DefaultPlayer) pf.getPlayer()).setPath(pf.getPath());
			return ret;
		}

	}

	private static final double TIMER_INTERVAL = .04;
	private static final double totalLength = Conversions.yardsToInches(125);
	private static final double totalWidth = Conversions.yardsToInches(54 + 5);

	private static final double totalHeight = Conversions.yardsToInches(50);
	private static final Color fieldGreen = new Color(70, 137, 68);
	private static final Color white = new Color(255, 255, 255);
	private static final Color yellow = new Color(255, 255, 0);
	private static final Color black = new Color(0, 0, 0);

	private static final Color red = new Color(255, 0, 0);
	private static FontData playerFontData = new FontData("Courier New", 16, SWT.NORMAL);
	private static FontData playerDataFontData = new FontData("Courier New", 24, SWT.NORMAL);
	private static Font playerFont;

	private static Font playerDataFont;

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

	private final Shell shell;
	private Canvas canvas;
	private long lastMilliseconds;
	private Image field;

	private Image playerImage;
	private GIFMarkup player1Gif;
	private boolean pause = true;
	private DefaultPlayer player;
	private final Map<String, DefaultPlayer> players = new HashMap<>();
	private DefaultPlayer runner;
	private final Map<String, DefaultPlayer> defenders = new HashMap<>();
	private final Map<String, DefaultPlayer> blockers = new HashMap<>();

	private final Map<Player, Pathfinder> pathfinders = new HashMap<>();

	private DestinationAction nextDestinationAction = DestinationAction.fastStop;

	private final LocationIndex index = new DefaultLocationIndex(PlayerTestViewer.TIMER_INTERVAL,
			(int) (2 / PlayerTestViewer.TIMER_INTERVAL)); // two seconds
	private final DebugMessageHandler debugMessageHandler = new DebugMessageHandler();

	private boolean testRunning;

	private boolean autoPauseActive;

	double cycleRate = Performance.cycleTime.getFrameRate();

	double cycleTimePerFrame = Performance.cycleTime.getAvgTime();

	double processRate = Performance.processTime.getAvgTime();

	double drawRate = Performance.cycleTime.getAvgTime();

	double otherRate = this.cycleTimePerFrame - this.processRate - this.drawRate;

	long refreshCycleCount = System.currentTimeMillis();

	long freeMemory = Runtime.getRuntime().freeMemory();
	long totalMemory = Runtime.getRuntime().totalMemory();

	long maxMemory = Runtime.getRuntime().totalMemory();

	@SuppressWarnings("deprecation")
	public PlayerTestViewer()
	{
		this.shell = new Shell();
		this.shell.setMaximized(true);
		this.shell.setText("Player Test Viewer");

		this.lastMilliseconds = System.currentTimeMillis();

		this.shell.setLayout(new GridLayout(1, false));

		this.createPlayers();
		this.createButtons();
		this.createCanvas();

		PlayerTestViewer.playerFont = new Font(this.shell.getDisplay(), PlayerTestViewer.playerFontData);
		PlayerTestViewer.playerDataFont = new Font(this.shell.getDisplay(), PlayerTestViewer.playerDataFontData);

		try
		{
			final FileInputStream fis = new FileInputStream(new File("FieldTestViewer.props"));
			final Properties props = new Properties();
			props.load(fis);
			final Rectangle rect = new Rectangle(Integer.parseInt(props.getProperty("bounds.x")),
					Integer.parseInt(props.getProperty("bounds.y")),
					Integer.parseInt(props.getProperty("bounds.width")),
					Integer.parseInt(props.getProperty("bounds.height")));
			this.shell.setBounds(rect);
		}
		catch (final Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.shell.addListener(SWT.Close, e ->
		{
			final Rectangle bounds = this.shell.getBounds();
			final Properties properties = new Properties();
			properties.put("bounds.x", "" + bounds.x);
			properties.put("bounds.y", "" + bounds.y);
			properties.put("bounds.width", "" + bounds.width);
			properties.put("bounds.height", "" + bounds.height);

			try
			{
				properties.save(new FileOutputStream(new File("FieldTestViewer.props")), "");
			}
			catch (final FileNotFoundException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});

	}

	public void messageLoop()
	{
		this.shell.open();

//		shell.getDisplay().addFilter(SWT.KeyUp, new Listener()
//		{
//			@Override
//			public void handleEvent(final Event event)
//			{
//				if (event.character == ' ')
//					pause = !pause;
//			}
//		});
//
		this.run();

		Performance.cycleTime.beginCycle();
		while (!this.shell.isDisposed())
		{
			try
			{
				if (!this.shell.getDisplay().readAndDispatch())
				{
					this.shell.getDisplay().sleep();
				}
			}
			catch (final Throwable t)
			{
				t.printStackTrace();
			}
		}
	}

	@Override
	public void run()
	{
		final long interval = System.currentTimeMillis() - this.lastMilliseconds;
		if (interval < 24)
		{
			this.shell.getDisplay().asyncExec(this);
			return;
		}

		Performance.cycleTime.endCycle();
		Performance.cycleTime.beginCycle();
		Performance.processTime.beginCycle();

		if (!this.pause)
		{
			if (this.autoPauseActive)
			{
				this.pause = true;
			}

			try
			{
				this.debugMessageHandler.clear();

				for (final Player player : this.players.values())
				{
					this.index.update(player);
				}

				final List<Collision> collisions = this.index.getCollisions(0);

//				collisions = collisions.stream().filter(
//						c -> c.getOccupier1().getPlayer().equals(player) || c.getOccupier2().getPlayer().equals(player))
//						.map(c -> new Collision(c.getOccupier1(), c.getOccupier2(), c.getTickCountOfcollision()))
//						.toList();

				if (this.pathfinders.size() > 0)
				{
					new HashSet<>(collisions.stream().map(CollisionResolution::createResolution).toList())
							.forEach(resolver ->
							{
								resolver.resolveCollision();

								DefaultPlayer player = this.players
										.get(resolver.getPlayerTracker1().getPlayer().getPlayerID());
								player.setPosture(resolver.getPlayerTracker1().getPosture());
								player.setLV(resolver.getPlayerTracker1().getLV());

								player = this.players.get(resolver.getPlayerTracker2().getPlayer().getPlayerID());
								player.setPosture(resolver.getPlayerTracker2().getPosture());
								player.setLV(resolver.getPlayerTracker2().getLV());
							});
				}

				this.pathfinders.values().forEach(Pathfinder::reset);

//				final PathCalculator calcScheduler = new PathCalculator();
//
//				this.pathfinders.values().stream().filter(RunnerPathfinder.class::isInstance)
//						.forEach(pf -> calcScheduler.addCalculation(pf));
//				this.pathfinders.values().stream().filter(DefenderPathfinder.class::isInstance)
//						.forEach(pf -> calcScheduler.addCalculation(pf));
//				this.pathfinders.values().stream().filter(BlockerPathfinder.class::isInstance)
//						.forEach(pf -> calcScheduler.addCalculation(pf));
//
//				calcScheduler.calculate(
//						this.pathfinders.values().stream().filter(RunnerPathfinder.class::isInstance)
//								.map(rpf -> (RunnerPathfinder) rpf).findFirst().orElse(null),
//						this.pathfinders.values().stream().filter(DefenderPathfinder.class::isInstance)
//								.map(dpf -> (DefenderPathfinder) dpf).toList(),
//						this.pathfinders.values().stream().filter(BlockerPathfinder.class::isInstance)
//								.map(bpf -> (BlockerPathfinder) bpf).toList(),
//						Performance.frameNanos * 100);

				RunnerPathfinder runnerPathfinder = this.pathfinders.values().stream()
						.filter(RunnerPathfinder.class::isInstance).map(rpf -> (RunnerPathfinder) rpf).findFirst()
						.orElse(null);

				if (runnerPathfinder != null)
				{
					List<DefenderPathfinder> defenderPathfinders = this.pathfinders.values().stream()
							.filter(DefenderPathfinder.class::isInstance).map(dpf -> (DefenderPathfinder) dpf).toList();
	
					List<BlockerPathfinder> blockerPathfinders = this.pathfinders.values().stream()
							.filter(BlockerPathfinder.class::isInstance).map(bpf -> (BlockerPathfinder) bpf).toList();
	
					runnerPathfinder.calculate(runnerPathfinder, defenderPathfinders, blockerPathfinders, interval);
					((DefaultPlayer) runnerPathfinder.getPlayer()).setPath(runnerPathfinder.getPath());
	
					defenderPathfinders.forEach(pf -> 
					{
						pf.calculate(runnerPathfinder, defenderPathfinders, blockerPathfinders, interval);
						((DefaultPlayer) pf.getPlayer()).setPath(pf.getPath());
					});
					
					new BlockersAction().move(runnerPathfinder, defenderPathfinders, blockerPathfinders, interval);
				}
				
				for (final DefaultPlayer player : this.players.values())
				{
					final Steering steering = Steering.getInstance();
					final PlayerTracker tracker = new PlayerTracker(player, PlayerTestViewer.TIMER_INTERVAL);
					steering.next(tracker);

					player.setLV(tracker.getLV());
					player.setAV(tracker.getAV());
					player.setLoc(tracker.getLoc());
					player.setPath(tracker.getPath());
					player.setPosture(tracker.getPosture());
				}

				this.index.advance();

				if (this.runner.getPosture() == Posture.onTheGround || runner.getLoc().isInBounds() == false
						|| runner.getLoc().isInEndZone(null))
				{
					this.pathfinders.clear();
					this.players.values().forEach(p -> p.setPath(null));
//					players.values().forEach(p -> p.setPosture(Posture.upright));
				}

				Performance.processTime.endCycle();
			}
			catch (final Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		this.drawPath(this.runner, "#00FF0000");
		this.defenders.values().forEach(p -> drawPath(p, "#FF000000"));
		this.blockers.values().forEach(p -> drawPath(p, "#0000FF00"));

		this.canvas.redraw();

		this.lastMilliseconds = System.currentTimeMillis();
		this.shell.getDisplay().asyncExec(this);
	}

	private void drawPath(Player player, String color)
	{
		if (player.getPath() == null)
			return;

		List<Location> locs = new ArrayList<>(
				player.getPath().getWaypoints().stream().map(wp -> wp.getDestination()).toList());
		locs.addFirst(player.getLoc());
		for (int i = 1; i < locs.size(); i++)
			MessageManager.getInstance().dispatchMessage(Messages.drawDebugShape,
					DebugShape.drawLineSegment(new LineSegment(locs.get(i - 1), locs.get(i)), color));
	}

	private void createButtons()
	{
		final Composite buttonRow = new Composite(this.shell, SWT.NONE);
		buttonRow.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		buttonRow.setLayout(new FillLayout(SWT.HORIZONTAL));

		final Combo combo = new Combo(buttonRow, SWT.DROP_DOWN);
		for (final DestinationAction action : Waypoint.DestinationAction.values())
		{
			combo.add(action.name(), action.ordinal());
		}

		combo.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				PlayerTestViewer.this.nextDestinationAction = DestinationAction.values()[combo.getSelectionIndex()];
			}

		});

		final Composite c2 = new Composite(buttonRow, SWT.NONE);
		c2.setLayout(new FillLayout(SWT.HORIZONTAL));

		final Button testButton = new Button(c2, SWT.PUSH);
		testButton.setText("Test");
		testButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				PlayerTestViewer.this.testRunning = !PlayerTestViewer.this.testRunning;
				testButton.setText(PlayerTestViewer.this.testRunning ? "Stop" : "Test");
			}
		});

		final Button autoPauseButton = new Button(c2, SWT.PUSH);
		autoPauseButton.setText("Auto Pause: Off");
		autoPauseButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				PlayerTestViewer.this.autoPauseActive = !PlayerTestViewer.this.autoPauseActive;
				autoPauseButton.setText(PlayerTestViewer.this.autoPauseActive ? "Auto Pause: On" : "Auto Pause: Off");
			}
		});

		final Button pauseButton = new Button(c2, SWT.PUSH);
		pauseButton.setText("Un Pause");
		pauseButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				PlayerTestViewer.this.pause = !PlayerTestViewer.this.pause;
				pauseButton.setText(PlayerTestViewer.this.pause ? "Un Pause" : "Pause");
			}
		});

		for (final Player p : this.players.values())
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
					PlayerTestViewer.this.player = (DefaultPlayer) b.getData();
				}
			});

			final Combo strategyCombo = new Combo(composite, SWT.DROP_DOWN);

			if (p == this.runner)
			{
				WaypointPathfinder pathfinder = new RunnerWaypointPathfinder(p, Direction.west);
				strategyCombo.add("Waypoint");
				strategyCombo.setData(strategyCombo.getItem(strategyCombo.getItemCount() - 1), pathfinder);
				strategyCombo.select(0);
				pathfinders.put(p, pathfinder);

				strategyCombo.add("Run For Glory");
				strategyCombo.setData(strategyCombo.getItem(strategyCombo.getItemCount() - 1),
						new RunForGlory(p, Direction.west));

				strategyCombo.add("Evade Interceptors");
				strategyCombo.setData(strategyCombo.getItem(strategyCombo.getItemCount() - 1),
						new DefaultEvadeInterceptors(p, Direction.west));
			}
			else if (this.defenders.containsValue(p))
			{
				WaypointPathfinder pathfinder = new DefenderWaypointPathfinder(p, Direction.west);
				strategyCombo.add("Waypoint");
				strategyCombo.setData(strategyCombo.getItem(strategyCombo.getItemCount() - 1), pathfinder);
				strategyCombo.select(0);
				pathfinders.put(p, pathfinder);

				strategyCombo.add("Pursue Runner");
				strategyCombo.setData(strategyCombo.getItem(strategyCombo.getItemCount() - 1),
						new DefaultPursueRunner(p, Direction.west));
			}
			else if (this.blockers.containsValue(p))
			{
				WaypointPathfinder pathfinder = new BlockerWaypointPathfinder(p, Direction.west);
				strategyCombo.add("Waypoint");
				strategyCombo.setData(strategyCombo.getItem(strategyCombo.getItemCount() - 1), pathfinder);
				strategyCombo.select(0);
				pathfinders.put(p, pathfinder);

				strategyCombo.add("Block Nearest Threat");
				strategyCombo.setData(strategyCombo.getItem(strategyCombo.getItemCount() - 1),
						new BlockNearestThreat(p, Direction.west));
			}

			strategyCombo.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(final SelectionEvent e)
				{
					final Pathfinder pf = (Pathfinder) strategyCombo
							.getData(strategyCombo.getItems()[strategyCombo.getSelectionIndex()]);
					PlayerTestViewer.this.pathfinders.put(p, pf);
				}
			});
		}
	}

	private void createCanvas()
	{
		this.canvas = new Canvas(this.shell, SWT.DOUBLE_BUFFERED);
		this.canvas.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.FILL_VERTICAL));
		this.canvas.setBackground(PlayerTestViewer.black);
		this.canvas.layout(true);

		this.field = new Image(this.shell.getDisplay(), this.getClass().getResourceAsStream("/field-4500x2124.png"));

//		playerImage= new Image(shell.getDisplay(), this.getClass().getResourceAsStream("/Blocker-72x72-Top.png"));

		final int helmetColor = PlayerTestViewer.colorStringToColor("#ED1C24");
		final int helmetStripe = PlayerTestViewer.colorStringToColor("#22B14C");
		final int shirt = PlayerTestViewer.colorStringToColor("#880015");
		final int pants = PlayerTestViewer.colorStringToColor("#00A2E8");
		final int socks = PlayerTestViewer.colorStringToColor("#FF7F27");
		final int skin = PlayerTestViewer.colorStringToColor("#FFAEC9");

		try
		{
			// #ED1C24 Helmet
			// #22B14C Helmet stripe
			// #880015 shirt
			// #00A2E8 pants
			// #FF7F27 socks
			// #FFAEC9 skin
			final BufferedImage bi = ImageIO.read(this.getClass().getResourceAsStream("/Steeler-72x72.png"));
			for (int x = 0; x < bi.getWidth(); x++)
			{
				for (int y = 0; y < bi.getHeight(); y++)
				{
					final int rgb = bi.getRGB(x, y);
//					System.out.println(String.format("(%d, %d) - %8X", x, y, rgb));
					if (((rgb & 0x00FFFFFF) == helmetColor) || ((rgb & 0x00FFFFFF) == helmetStripe)
							|| ((rgb & 0x00FFFFFF) == shirt))
					{
						bi.setRGB(x, y, 0xFF4F2683);
					}
					else if ((rgb & 0x00FFFFFF) == pants)
					{
						bi.setRGB(x, y, 0xFFFFFFFF);
					}
					else if ((rgb & 0x00FFFFFF) == socks)
					{
						bi.setRGB(x, y, 0xFFFFFFFF);
					}
					else if ((rgb & 0x00FFFFFF) == skin)
					{
						bi.setRGB(x, y, 0xFF6C5A57);
					}
				}
			}

			final ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(bi, "png", os);
			final InputStream is = new ByteArrayInputStream(os.toByteArray());
			this.playerImage = new Image(this.shell.getDisplay(), is);
		}
		catch (final IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final ImageLoader imageLoader = new ImageLoader();
		imageLoader.load(this.getClass().getResourceAsStream("/untitled.gif"));
		this.player1Gif = new GIFMarkup(imageLoader);

		this.canvas.addPaintListener(e ->
		{
			Performance.drawTime.beginCycle();
			try (TransformStack ts = new TransformStack(e.gc))
			{
				final float scaleX = (float) (this.canvas.getClientArea().width / PlayerTestViewer.totalLength);
				final float scaleY = (float) (this.canvas.getClientArea().height / PlayerTestViewer.totalWidth);
				final float scale = Math.min(scaleX, scaleY);
				ts.scale(scale, scale);
				ts.set();

				e.gc.drawImage(this.field, 0, 0);
				for (final Player player : this.players.values())
				{
					this.drawPlayer(e.gc, player);
				}

				this.drawPerformance(e.gc);

				this.debugMessageHandler.draw(e.gc);
			}
			catch (final Exception e1)
			{
				e1.printStackTrace();
			}
			Performance.drawTime.endCycle();
		});

		this.canvas.addMouseListener(new MouseAdapter()
		{

			@Override
			public void mouseUp(final MouseEvent e)
			{
				super.mouseUp(e);

				Point p = new Point(e.x, e.y);

				final GC gc = new GC(PlayerTestViewer.this.shell.getDisplay());
				try (TransformStack ts = new TransformStack(gc))
				{
					final float scaleX = (float) (PlayerTestViewer.this.canvas.getClientArea().width
							/ PlayerTestViewer.totalLength);
					final float scaleY = (float) (PlayerTestViewer.this.canvas.getClientArea().height
							/ PlayerTestViewer.totalWidth);
					final float scale = Math.min(scaleX, scaleY);
					ts.scale(1 / scale, 1 / scale);
					ts.set();

					p = ts.transform(p);
					p.y = (int) (PlayerTestViewer.totalWidth - p.y);
					final Location loc = PlayerTestViewer.this.pointToLocation(p);

					PlayerTestViewer.this.player.setPath(new DefaultPath(new Waypoint(loc,
							PlayerTestViewer.this.player.getSpeedMatrix().getJoggingSpeed(),
							PlayerTestViewer.this.player.getMaxSpeed(), PlayerTestViewer.this.nextDestinationAction)));
				}
				catch (final Exception e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				gc.dispose();
			}

		});
	}

	private void createPlayers()
	{
		this.player = new DefaultPlayer(PlayerPosition.RB);
		this.player.setFirstName("Chuck");
		this.player.setLastName("Foreman");
		this.player.setWeight(215);
		this.player.setLoc(new DefaultLocation(Field.yardLine(20, Direction.west), Field.MIDFIELD_Y, 0));
		this.player.setAV(new DefaultAngularVelocity(Math.PI, 0, 0));

		this.players.put(this.player.getPlayerID(), this.player);
		this.runner = this.player;
		Football.theFootball.setPlayerInPossession(this.runner);

		DefaultPlayer pl = new DefaultPlayer(PlayerPosition.DE);
		pl.setFirstName("Carl");
		pl.setLastName("Eller");
		pl.setWeight(280);
		pl.setLoc(Field.MIDFIELD);

		this.players.put(pl.getPlayerID(), pl);
		this.defenders.put(pl.getPlayerID(), pl);

		pl = new DefaultPlayer(PlayerPosition.DT);
		pl.setFirstName("Alan");
		pl.setLastName("Page");
		pl.setWeight(280);
		pl.setLoc(Field.MIDFIELD.add(-5, 0, 0));

		this.players.put(pl.getPlayerID(), pl);
		this.defenders.put(pl.getPlayerID(), pl);

		pl = new DefaultPlayer(PlayerPosition.RT);
		pl.setFirstName("Ron");
		pl.setLastName("Yary");
		pl.setWeight(260);
		pl.setLoc(Field.MIDFIELD.add(20, 5, 0));
		pl.setAV(new DefaultAngularVelocity(Math.PI, 0, 0));

		this.players.put(pl.getPlayerID(), pl);
		this.blockers.put(pl.getPlayerID(), pl);

		pl = new DefaultPlayer(PlayerPosition.RG);
		pl.setFirstName("Ed");
		pl.setLastName("White");
		pl.setWeight(250);
		pl.setLoc(Field.MIDFIELD.add(20, -1, 0));
		pl.setAV(new DefaultAngularVelocity(Math.PI, 0, 0));

		this.players.put(pl.getPlayerID(), pl);
		this.blockers.put(pl.getPlayerID(), pl);

	}

	private void drawPerformance(final GC gc)
	{
		gc.setFont(PlayerTestViewer.playerDataFont);
		gc.setForeground(PlayerTestViewer.yellow);

		final long current = System.currentTimeMillis();
		if ((current - this.refreshCycleCount) > 1000)
		{
			this.cycleRate = Performance.cycleTime.getFrameRate();
			this.cycleTimePerFrame = Performance.cycleTime.getAvgTime();
			if (this.cycleTimePerFrame == 0)
				return;

			this.processRate = Performance.processTime.getAvgTime();
			this.drawRate = Performance.drawTime.getAvgTime();
			this.otherRate = this.cycleTimePerFrame - this.processRate - this.drawRate;
			this.refreshCycleCount = current;

			this.freeMemory = Runtime.getRuntime().freeMemory();
			this.totalMemory = Runtime.getRuntime().totalMemory();
			this.maxMemory = Runtime.getRuntime().totalMemory();
		}

		final StringBuilder msg = new StringBuilder();
		msg.append(String.format("Tick Count  : %d\n", Performance.processTime.getTickCount()));
		msg.append(String.format("Frame Rate  : %.1f fps\n", this.cycleRate));
		msg.append(String.format("Process Rate: %.1f%% (%.1f ns)\n", (this.processRate * 100) / this.cycleTimePerFrame,
				this.processRate));
		msg.append(String.format("Draw Rate   : %.1f%% (%.1f ns)\n", (this.drawRate * 100) / this.cycleTimePerFrame,
				this.drawRate));
		msg.append(String.format("Other Rate  : %.1f%%\n", (this.otherRate * 100) / this.cycleTimePerFrame));
		msg.append("\n");
		msg.append(String.format("Max Memory  : %d MB\n", this.maxMemory / 1000000));
		msg.append(String.format("Total Memory: %d MB\n", this.totalMemory / 1000000));
		msg.append(String.format("Free Memory : %d MB \n", this.freeMemory / 1000000));
		msg.append("\n");

		gc.drawText(msg.toString(), 10, 10, false);
	}

	private void drawPlayer(final GC gc, final Player player)
	{
		final int offset = (int) Conversions.yardsToInches((Player.SIZE * 2.0) / 4.0);

		gc.setFont(PlayerTestViewer.playerFont);
		final Point p = this.locationToPoint(player.getLoc());

		if (this.defenders.containsValue(player))
		{
			gc.setForeground(this.shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
			gc.setBackground(this.shell.getDisplay().getSystemColor(SWT.COLOR_DARK_MAGENTA));
			gc.fillOval(p.x - offset, p.y - offset, offset * 2, offset * 2);
		}
		else if (this.blockers.containsValue(player) || (this.runner == player))
		{
			gc.setForeground(this.shell.getDisplay().getSystemColor(SWT.COLOR_DARK_MAGENTA));
			gc.setBackground(this.shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
			gc.fillOval(p.x - offset, p.y - offset, offset * 2, offset * 2);
		}

		if (player == this.player)
		{
			gc.setForeground(this.shell.getDisplay().getSystemColor(SWT.COLOR_RED));
			gc.setLineWidth(3);
			gc.drawOval(p.x - offset, p.y - offset, offset * 2, offset * 2);
		}

		try (TransformStack ts = new TransformStack(gc))
		{
			ts.translate(p);
			ts.rotate(-player.getAV().getOrientation());
			ts.set();

			gc.fillPolygon(new int[]
			{ 0, -offset, 0, offset, 2 * offset, 0 });
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}

		final String playerNumber = "" + player.getFirstName().charAt(0) + player.getLastName().charAt(0);
		final Point extent = gc.textExtent(playerNumber);

		gc.drawText(playerNumber, p.x - (extent.x / 2), p.y - (extent.y / 2));

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
		final StringBuilder str = new StringBuilder();
		str.append(String.format("Name            : %s %s\n", this.player.getFirstName(), this.player.getLastName()));
		str.append(String.format("Location        : %s\n", this.player.getLoc()));
		str.append(String.format("Linear velocity : %s\n", this.player.getLV()));
		str.append(String.format("Angular velocity: %s\n", this.player.getAV()));
		str.append(String.format("Posture		  : %s\n", this.player.getPosture()));
		str.append(String.format("\n"));

		if (this.player.getPath() != null)
		{
			for (final Waypoint wp : this.player.getPath().getWaypoints())
			{
				str.append(String.format("       waypoint : %s - Dist: %.2f\n", wp,
						this.player.getLoc().distanceBetween(wp.getDestination())));
			}
		}

		gc.setFont(PlayerTestViewer.playerDataFont);
		gc.setForeground(PlayerTestViewer.yellow);
		gc.setBackground(this.shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		gc.drawText(str.toString(), 3000, 20);
	}

	private Point locationToPoint(final Location loc)
	{
		final int x = (int) Conversions.yardsToInches(loc.getX());
		final int y = (int) (Conversions.yardsToInches(Field.FIELD_TOTAL_WIDTH)
				- Conversions.yardsToInches(loc.getY()));
		return new Point(x, y);
	}

	private Location pointToLocation(final Point p)
	{
		return new DefaultLocation(Conversions.inchesToYards(p.x), Conversions.inchesToYards(p.y), 0.0);
	}

}
