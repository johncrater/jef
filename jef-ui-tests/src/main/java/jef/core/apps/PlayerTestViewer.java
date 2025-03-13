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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
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

import jef.actions.pathfinding.Pathfinder;
import jef.actions.pathfinding.Players;
import jef.actions.pathfinding.blocking.BlockNearestThreat;
import jef.actions.pathfinding.blocking.BlockerWaypointPathfinder;
import jef.actions.pathfinding.defenders.DefaultPursueRunner;
import jef.actions.pathfinding.defenders.DefenderWaypointPathfinder;
import jef.actions.pathfinding.runners.DefaultEvadeInterceptors;
import jef.actions.pathfinding.runners.RunForGlory;
import jef.actions.pathfinding.runners.RunnerWaypointPathfinder;
import jef.core.AngularVelocity;
import jef.core.Conversions;
import jef.core.Direction;
import jef.core.Field;
import jef.core.Football;
import jef.core.Location;
import jef.core.Performance;
import jef.core.Player;
import jef.core.PlayerPosition;
import jef.core.PlayerState;
import jef.core.events.DebugShape;
import jef.core.events.Messages;
import jef.core.geometry.LineSegment;
import jef.core.movement.Posture;
import jef.core.movement.player.Path;
import jef.core.movement.player.Waypoint;
import jef.core.movement.player.Waypoint.DestinationAction;
import jef.core.ui.swt.utils.DebugMessageHandler;
import jef.core.ui.swt.utils.GIFMarkup;
import jef.core.ui.swt.utils.UIUtils;

public class PlayerTestViewer implements Runnable
{
	private static final double TIMER_INTERVAL = .04;

	private static final Color white = new Color(255, 255, 255);
	private static final Color yellow = new Color(255, 255, 0);
	private static final Color black = new Color(0, 0, 0);

	private static final Color red = new Color(255, 0, 0);
	private static FontData playerFontData = new FontData("Courier New", 16, SWT.NORMAL);
	private static FontData playerDataFontData = new FontData("Courier New", 8, SWT.NORMAL);
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
	private Player currentPlayer;

	private Player runner;
	private final Map<String, Player> defenders = new HashMap<>();
	private final Map<String, Player> blockers = new HashMap<>();
	private Players players = new Players(50, Performance.frameInterval);
	
	private DestinationAction nextDestinationAction = DestinationAction.fastStop;

	private final DebugMessageHandler debugMessageHandler = new DebugMessageHandler();

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

				players.process();
				
				Performance.processTime.endCycle();
			}
			catch (final Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		this.drawPath(players.getState(runner), "#00FF0000");
		this.defenders.values().forEach(p -> drawPath(players.getState(p), "#FF000000"));
		this.blockers.values().forEach(p -> drawPath(players.getState(p), "#0000FF00"));

		this.canvas.redraw();

		this.lastMilliseconds = System.currentTimeMillis();
		this.shell.getDisplay().asyncExec(this);
	}

	private void drawPath(PlayerState player, String color)
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

	@SuppressWarnings("unchecked")
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

		for (final Player p : this.players.getPlayers())
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
					final Class<Pathfinder> pfClass = (Class<Pathfinder>) strategyCombo
							.getData(strategyCombo.getItems()[strategyCombo.getSelectionIndex()]);
					try
					{
						Pathfinder pathfinder = (Pathfinder) pfClass
								.getConstructor(PlayerState.class, Direction.class)
								.newInstance(p, Direction.west);
						players.setPathfinder(pathfinder);
					}
					catch (Exception e1)
					{
						e1.printStackTrace();
					}
				}
			});

			if (p == this.runner)
			{
				strategyCombo.add("Evade Interceptors");
				strategyCombo.setData(strategyCombo.getItem(strategyCombo.getItemCount() - 1),
						DefaultEvadeInterceptors.class);

				strategyCombo.add("Waypoint");
				strategyCombo.setData(strategyCombo.getItem(strategyCombo.getItemCount() - 1),
						RunnerWaypointPathfinder.class);

				strategyCombo.add("Run For Glory");
				strategyCombo.setData(strategyCombo.getItem(strategyCombo.getItemCount() - 1), RunForGlory.class);
			}
			else if (this.defenders.containsValue(p))
			{
				strategyCombo.add("Pursue Runner");
				strategyCombo.setData(strategyCombo.getItem(strategyCombo.getItemCount() - 1),
						DefaultPursueRunner.class);

				strategyCombo.add("Waypoint");
				strategyCombo.setData(strategyCombo.getItem(strategyCombo.getItemCount() - 1),
						DefenderWaypointPathfinder.class);
			}
			else if (this.blockers.containsValue(p))
			{
				strategyCombo.add("Block Nearest Threat");
				strategyCombo.setData(strategyCombo.getItem(strategyCombo.getItemCount() - 1),
						BlockNearestThreat.class);

				strategyCombo.add("Waypoint");
				strategyCombo.setData(strategyCombo.getItem(strategyCombo.getItemCount() - 1),
						BlockerWaypointPathfinder.class);
			}

			strategyCombo.select(0);
			final Class<Pathfinder> pfClass = (Class<Pathfinder>) strategyCombo
					.getData(strategyCombo.getItems()[strategyCombo.getSelectionIndex()]);

			try
			{
				Pathfinder pathfinder = (Pathfinder) pfClass
						.getConstructor(PlayerState.class, Direction.class)
						.newInstance(players.getState(p), Direction.west);
				players.setPathfinder(pathfinder);
			}
			catch (Exception e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	private float scaleAdjustment = 1.0f;
	private Location midfieldLocation = Field.MIDFIELD;

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
			try (FieldTransformStack ts = new FieldTransformStack(canvas, e.gc, midfieldLocation, scaleAdjustment))
			{
				e.gc.drawImage(this.field, 0, 0);
				for (final Player player : this.players.getPlayers())
				{
					this.drawPlayer(ts, this.players.getState(player));
				}

				this.debugMessageHandler.draw(e.gc);
			}
			catch (final Exception e1)
			{
				e1.printStackTrace();
			}

			this.drawPerformance(e.gc);
			this.drawSelectedPlayerData(e.gc);

			Performance.drawTime.endCycle();
		});

		this.canvas.addMouseListener(new MouseAdapter()
		{

			@Override
			public void mouseUp(final MouseEvent e)
			{
				super.mouseUp(e);

				Point p = new Point(e.x, e.y);

				try (FieldTransformStack ts = new FieldTransformStack(canvas, midfieldLocation, scaleAdjustment))
				{
					if ((e.stateMask & SWT.CONTROL) != 0)
					{
						midfieldLocation = ts.transformToLocation(p);
						System.out.print("midfield=" + midfieldLocation);
					}
					else
					{
						Location loc = ts.transformToLocation(p);
						PlayerState playerState = players.getState(PlayerTestViewer.this.currentPlayer);
						Path path = new Path(new Waypoint(loc, playerState.getSpeedMatrix().getJoggingSpeed(),
								playerState.getMaxSpeed(), PlayerTestViewer.this.nextDestinationAction));
						playerState = playerState.newFrom(null, null, null, path, null);
						players.reset(playerState);
					}
				}
				catch (final Exception e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		});

		this.canvas.addMouseWheelListener(new MouseWheelListener()
		{
			@Override
			public void mouseScrolled(MouseEvent e)
			{
				if ((e.stateMask & SWT.CONTROL) != 0)
					scaleAdjustment = (float) Math.max(.25, scaleAdjustment + Math.signum(e.count) * .25);
			}

		});

	}

	private void createPlayers()
	{
		// offense

		double lineOfScrimmage = Field.yardLine(30, Direction.west);

		Player pl = new Player(PlayerPosition.RB);
		pl.setFirstName("Chuck");
		pl.setLastName("Foreman");
		pl.setWeight(215);

		PlayerState playerState = new PlayerState(pl, null, new Location(lineOfScrimmage + 5, Field.MIDFIELD_Y, 0),
				new AngularVelocity(Math.PI, 0, 0), null, Posture.upright);
		this.players.addPlayer(playerState);
		this.runner = this.currentPlayer = pl;
		Football.theFootball.setPlayerInPossession(this.runner);

		pl = new Player(PlayerPosition.RG);
		pl.setFirstName("Ed");
		pl.setLastName("White");
		pl.setWeight(250);
		playerState = new PlayerState(pl, null, new Location(lineOfScrimmage, Field.MIDFIELD_Y - 2, 0),
				new AngularVelocity(Math.PI, 0, 0), null, Posture.upright);
		this.players.addPlayer(playerState);
		this.blockers.put(pl.getPlayerID(), pl);

		pl = new Player(PlayerPosition.C);
		pl.setFirstName("Mick");
		pl.setLastName("Tinglehoff");
		pl.setWeight(270);
		playerState = new PlayerState(pl, null, new Location(lineOfScrimmage, Field.MIDFIELD_Y, 0),
				new AngularVelocity(Math.PI, 0, 0), null, Posture.upright);
		this.players.addPlayer(playerState);
		this.blockers.put(pl.getPlayerID(), pl);

		pl = new Player(PlayerPosition.RT);
		pl.setFirstName("Ron");
		pl.setLastName("Yary");
		pl.setWeight(260);
		playerState = new PlayerState(pl, null, new Location(lineOfScrimmage, Field.MIDFIELD_Y + 2, 0),
				new AngularVelocity(Math.PI, 0, 0), null, Posture.upright);
		this.players.addPlayer(playerState);
		this.blockers.put(pl.getPlayerID(), pl);

		// defense
		pl = new Player(PlayerPosition.DT);
		pl.setFirstName("Alan");
		pl.setLastName("Page");
		pl.setWeight(280);
		playerState = new PlayerState(pl, null, new Location(lineOfScrimmage - 1, Field.MIDFIELD_Y + 2, 0),
				new AngularVelocity(0, 0, 0), null, Posture.upright);
		this.players.addPlayer(playerState);
		this.defenders.put(pl.getPlayerID(), pl);

		pl = new Player(PlayerPosition.DE);
		pl.setFirstName("Carl");
		pl.setLastName("Eller");
		pl.setWeight(280);
		playerState = new PlayerState(pl, null, new Location(lineOfScrimmage - 1, Field.MIDFIELD_Y - 2, 0),
				new AngularVelocity(0, 0, 0), null, Posture.upright);
		this.players.addPlayer(playerState);
		this.defenders.put(pl.getPlayerID(), pl);

		pl = new Player(PlayerPosition.LLB);
		pl.setFirstName("Matt");
		pl.setLastName("Blair");
		pl.setWeight(255);
		playerState = new PlayerState(pl, null, new Location(lineOfScrimmage - 6, Field.MIDFIELD_Y - 2, 0),
				new AngularVelocity(0, 0, 0), null, Posture.upright);
		this.players.addPlayer(playerState);
		this.defenders.put(pl.getPlayerID(), pl);

		pl = new Player(PlayerPosition.RLB);
		pl.setFirstName("Wally");
		pl.setLastName("Hilgenberg");
		pl.setWeight(255);
		playerState = new PlayerState(pl, null, new Location(lineOfScrimmage - 6, Field.MIDFIELD_Y + 2, 0),
				new AngularVelocity(0, 0, 0), null, Posture.upright);
		this.players.addPlayer(playerState);
		this.defenders.put(pl.getPlayerID(), pl);
	}

	private void drawPerformance(final GC gc)
	{
		gc.setFont(PlayerTestViewer.playerDataFont);
		gc.setBackground(black);
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

	private void drawPlayer(final FieldTransformStack fts, final PlayerState player)
	{
		int lineWidth = 3;
		final int offset = (int) Conversions.yardsToInches((Player.SIZE) / 4.0);

		GC gc = fts.getGC();
		gc.setFont(PlayerTestViewer.playerFont);
		final Point p = UIUtils.locationToPoint(player.getLoc());

		if (this.defenders.containsValue(player.getPlayer()))
		{
			gc.setForeground(this.shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
			gc.setBackground(this.shell.getDisplay().getSystemColor(SWT.COLOR_DARK_MAGENTA));
			gc.fillOval(p.x - offset, p.y - offset, offset * 2, offset * 2);
		}
		else if (this.blockers.containsValue(player.getPlayer()) || (this.runner == player.getPlayer()))
		{
			gc.setForeground(this.shell.getDisplay().getSystemColor(SWT.COLOR_DARK_MAGENTA));
			gc.setBackground(this.shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
			gc.fillOval(p.x - offset, p.y - offset, offset * 2, offset * 2);
		}

		if (player.getPlayer() == this.currentPlayer)
		{
			gc.setForeground(this.shell.getDisplay().getSystemColor(SWT.COLOR_RED));
			gc.setLineWidth(lineWidth);
			gc.drawOval(p.x - offset, p.y - offset, offset * 2, offset * 2);
		}

//		fts.push();
//		fts.translate(p);
//		fts.rotate(player.getAV().getOrientation());
//		fts.set();
//		gc.fillPolygon(new int[]
//		{ 0, -offset + lineWidth, 0, offset - lineWidth, 2 * offset - 2 * lineWidth, 0 });
//
//		fts.pop();
//		
//		final String playerNumber = "" + player.getFirstName().charAt(0) + player.getLastName().charAt(0);
//		final Point extent = gc.textExtent(playerNumber);
//
//		gc.drawText(playerNumber, p.x - (extent.x / 2), p.y - (extent.y / 2), true);

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

	private void drawSelectedPlayerData(final GC gc)
	{
		PlayerState playerState = this.players.getState(currentPlayer);
		final StringBuilder str = new StringBuilder();
		str.append(String.format("Name            : %s %s\n", this.currentPlayer.getFirstName(), this.currentPlayer.getLastName()));
		str.append(String.format("Location        : %s\n", playerState.getLoc()));
		str.append(String.format("Linear velocity : %s\n", playerState.getLV()));
		str.append(String.format("Angular velocity: %s\n", playerState.getAV()));
		str.append(String.format("Posture		  : %s\n", playerState.getPosture()));
		str.append(String.format("\n"));

		if (playerState.getPath() != null)
		{
			for (final Waypoint wp : playerState.getPath().getWaypoints())
			{
				str.append(String.format("       waypoint : %s - Dist: %.2f\n", wp,
						playerState.getLoc().distanceBetween(wp.getDestination())));
			}
		}

		gc.setFont(PlayerTestViewer.playerDataFont);
		gc.setForeground(PlayerTestViewer.yellow);
		gc.setBackground(this.shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		gc.drawText(str.toString(), 1400, 20);
	}

}
