package jef.core;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import jef.core.movement.Collision;
import jef.core.movement.DefaultLinearVelocity;
import jef.core.movement.DefaultLocation;
import jef.core.movement.LinearVelocity;
import jef.core.movement.Location;
import jef.core.movement.index.DefaultLocationIndex;
import jef.core.movement.index.LocationIndex;
import jef.core.movement.player.DefaultPath;
import jef.core.movement.player.Path;
import jef.core.movement.player.PlayerTracker;
import jef.core.movement.player.Steering;
import jef.core.movement.player.Waypoint;
import jef.core.movement.player.Waypoint.DestinationAction;
import jef.core.pathfinding.Direction;
import jef.core.pathfinding.InterceptPlayer;
import jef.core.pathfinding.Pathfinder;
import jef.core.pathfinding.RunForGlory;

public class PlayerTestViewer implements Runnable
{
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

	private Shell shell;
	private Canvas canvas;
	private long lastMilliseconds;
	private Image field;
	private Image playerImage;
	private GIFMarkup player1Gif;
	private boolean pause;

	private TestPlayer player;
	private Map<String, TestPlayer> players = new HashMap<>();
	private Map<String, Pathfinder> pathfinders = new HashMap<>();
	private DestinationAction nextDestinationAction = DestinationAction.fastStop;
	private LocationIndex index = new DefaultLocationIndex(TIMER_INTERVAL, (int) (2 / TIMER_INTERVAL)); // two seconds

	public PlayerTestViewer()
	{
		this.shell = new Shell();
		shell.setMaximized(true);
		shell.setText("Player Test Viewer");

		this.lastMilliseconds = System.currentTimeMillis();

		shell.setLayout(new GridLayout(1, false));

		createPlayers();
		createButtons();
		createCanvas();

		playerFont = new Font(shell.getDisplay(), playerFontData);
		playerDataFont = new Font(shell.getDisplay(), playerDataFontData);

		try
		{
			final FileInputStream fis = new FileInputStream(new File("FieldTestViewer.props"));
			final Properties props = new Properties();
			props.load(fis);
			final Rectangle rect = new Rectangle(Integer.parseInt(props.getProperty("bounds.x")),
					Integer.parseInt(props.getProperty("bounds.y")),
					Integer.parseInt(props.getProperty("bounds.width")),
					Integer.parseInt(props.getProperty("bounds.height")));
			shell.setBounds(rect);
		}
		catch (final Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		shell.addListener(SWT.Close, e ->
		{
			final Rectangle bounds = shell.getBounds();
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

	private void createButtons()
	{
		Composite c = new Composite(shell, SWT.NONE);
		c.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		c.setLayout(new FillLayout(SWT.HORIZONTAL));

		Combo combo = new Combo(c, SWT.DROP_DOWN);
		for (DestinationAction action : Waypoint.DestinationAction.values())
			combo.add(action.name(), action.ordinal());

		combo.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				nextDestinationAction = DestinationAction.values()[combo.getSelectionIndex()];
			}

		});

		Button testButton = new Button(c, SWT.PUSH);
		testButton.setText("Run For Glory");
		testButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				pathfinders.put(player.getId(), new RunForGlory(player, Direction.west));
			}
		});

		testButton = new Button(c, SWT.PUSH);
		testButton.setText("Intercept");
		testButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				pathfinders.put(player.getId(), new InterceptPlayer(player,
						players.values().stream().filter(p -> p != player).findFirst().orElse(null), Direction.west));
			}
		});

		testButton = new Button(c, SWT.PUSH);
		testButton.setText("Stop");
		testButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				pathfinders.put(player.getId(), null);
			}
		});

		for (TestPlayer p : this.players.values())
		{
			Button b = new Button(c, SWT.PUSH);
			b.setText("" + p.getFirstName().charAt(0) + p.getLastName().charAt(0));
			b.setData(p);
			b.addSelectionListener(new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					player = (TestPlayer) b.getData();
				}
			});
		}
	}

	private void createPlayers()
	{
		player = new TestPlayer("Chuck", "Foreman");
		Path path = new DefaultPath();
		path.addWaypoint(new Waypoint(Field.MIDFIELD, 10, DestinationAction.fastStop));
		player.setPath(path);
		this.players.put(player.getId(), player);

		TestPlayer p = new TestPlayer("Fran", "Tarkenton");
		p.setLoc(Field.MIDFIELD);
		path = new DefaultPath();
		path.addWaypoint(new Waypoint(Field.MIDFIELD, 10, DestinationAction.fastStop));
		p.setPath(path);
		this.players.put(p.getId(), p);
	}

	public static int colorStringToColor(String colorString)
	{
		if (colorString == null)
			return 0;

		return Integer.parseInt(colorString.substring(1), 16);
	}

	private void createCanvas()
	{
		canvas = new Canvas(shell, SWT.DOUBLE_BUFFERED);
		canvas.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.FILL_VERTICAL));
		canvas.setBackground(black);
		canvas.layout(true);

		field = new Image(shell.getDisplay(), this.getClass().getResourceAsStream("/field-4500x2124.png"));

//		playerImage= new Image(shell.getDisplay(), this.getClass().getResourceAsStream("/Blocker-72x72-Top.png"));

		int helmetColor = colorStringToColor("#ED1C24");
		int helmetStripe = colorStringToColor("#22B14C");
		int shirt = colorStringToColor("#880015");
		int pants = colorStringToColor("#00A2E8");
		int socks = colorStringToColor("#FF7F27");
		int skin = colorStringToColor("#FFAEC9");

		try
		{
			// #ED1C24 Helmet
			// #22B14C Helmet stripe
			// #880015 shirt
			// #00A2E8 pants
			// #FF7F27 socks
			// #FFAEC9 skin
			BufferedImage bi = ImageIO.read(this.getClass().getResourceAsStream("/Steeler-72x72.png"));
			for (int x = 0; x < bi.getWidth(); x++)
			{
				for (int y = 0; y < bi.getHeight(); y++)
				{
					int rgb = bi.getRGB(x, y);
//					System.out.println(String.format("(%d, %d) - %8X", x, y, rgb));
					if ((rgb & 0x00FFFFFF) == helmetColor)
					{
						bi.setRGB(x, y, 0xFF4F2683);
					}
					else if ((rgb & 0x00FFFFFF) == helmetStripe)
					{
						bi.setRGB(x, y, 0xFF4F2683);
					}
					else if ((rgb & 0x00FFFFFF) == shirt)
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

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(bi, "png", os);
			InputStream is = new ByteArrayInputStream(os.toByteArray());
			playerImage = new Image(shell.getDisplay(), is);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ImageLoader imageLoader = new ImageLoader();
		imageLoader.load(this.getClass().getResourceAsStream("/untitled.gif"));
		this.player1Gif = new GIFMarkup(imageLoader);
		canvas.addPaintListener(e ->
		{
			Performance.drawTime.beginCycle();
			try (TransformStack ts = new TransformStack(e.gc))
			{
				float scaleX = (float) (canvas.getClientArea().width / totalLength);
				float scaleY = (float) (canvas.getClientArea().height / totalWidth);
				float scale = Math.min(scaleX, scaleY);
				ts.scale(scale, scale);
				ts.set();

				e.gc.drawImage(field, 0, 0);

				for (TestPlayer player : players.values())
				{
					drawPlayer(e.gc, player);
				}

				drawPerformance(e.gc);
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
			Performance.drawTime.endCycle();
		});

		canvas.addMouseListener(new MouseAdapter()
		{

			@Override
			public void mouseUp(MouseEvent e)
			{
				super.mouseUp(e);

				Point p = new Point(e.x, e.y);

				GC gc = new GC(shell.getDisplay());
				try (TransformStack ts = new TransformStack(gc))
				{
					float scaleX = (float) (canvas.getClientArea().width / totalLength);
					float scaleY = (float) (canvas.getClientArea().height / totalWidth);
					float scale = Math.min(scaleX, scaleY);
					ts.scale(1 / scale, 1 / scale);
					ts.set();

					p = ts.transform(p);
					p.y = (int) (totalWidth - p.y);
					Location loc = pointToLocation(p);

					player.getPath().clear();
					player.getPath().addWaypoint(new Waypoint(loc, player.getDesiredSpeed(), nextDestinationAction));
				}
				catch (Exception e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				gc.dispose();
			}

		});
	}

	public void messageLoop()
	{
		shell.open();

		shell.getDisplay().addFilter(SWT.KeyUp, new Listener()
		{
			@Override
			public void handleEvent(final Event event)
			{
				if (event.character == ' ')
					pause = !pause;
			}
		});

		run();

		Performance.cycleTime.beginCycle();
		while (!shell.isDisposed())
			try
			{
				if (!shell.getDisplay().readAndDispatch())
					shell.getDisplay().sleep();
			}
			catch (final Throwable t)
			{
				t.printStackTrace();
			}
	}

	private void drawPlayer(GC gc, TestPlayer player)
	{
		int offset = (int) Conversions.yardsToInches(Player.size * 3.0 / 4.0);

		if (player == this.player)
		{
			gc.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
			gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		}
		else
		{
			gc.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
			gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		}

		gc.setFont(playerFont);

		Point p = locationToPoint(player.getLoc());
		gc.fillOval(p.x - offset, p.y - offset, offset * 2, offset * 2);

		String playerNumber = "" + player.getFirstName().charAt(0) + player.getLastName().charAt(0);
		Point extent = gc.textExtent(playerNumber);

		gc.drawText(playerNumber, p.x - extent.x / 2, p.y - extent.y / 2);

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
		StringBuilder str = new StringBuilder();
		str.append(String.format("Name            : %s %s\n", this.player.getFirstName(), this.player.getLastName()));
		str.append(String.format("Location        : %s\n", this.player.getLoc()));
		str.append(String.format("Linear velocity : %s\n", this.player.getLV()));
		str.append(String.format("Angular velocity: %s\n", this.player.getAV()));
		str.append(String.format("\n"));

		for (Waypoint wp : this.player.getPath().getWaypoints())
			str.append(String.format("       waypoint : %s - Dist: %.2f\n", wp,
					this.player.getLoc().distanceBetween(wp.getDestination())));

		gc.setFont(playerDataFont);
		gc.setForeground(yellow);
		gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		gc.drawText(str.toString(), 3000, 20);
	}

	private Point locationToPoint(Location loc)
	{
		int x = (int) Conversions.yardsToInches(loc.getX());
		int y = (int) (Conversions.yardsToInches(Field.FIELD_TOTAL_WIDTH) - Conversions.yardsToInches(loc.getY()));
		return new Point(x, y);
	}

	private Location pointToLocation(Point p)
	{
		return new DefaultLocation(Conversions.inchesToYards(p.x), Conversions.inchesToYards(p.y), 0.0);
	}

	public void run()
	{
		final long interval = System.currentTimeMillis() - this.lastMilliseconds;
		if (interval < 24)
		{
			shell.getDisplay().asyncExec(this);
			return;
		}

		Performance.cycleTime.endCycle();
		Performance.cycleTime.beginCycle();
		Performance.processTime.beginCycle();

		if (!pause)
		{
			try
			{
				for (TestPlayer player : players.values())
					this.index.update(player);

				List<Collision> collisions = this.index.getCollisions(0);
				for (Collision collision : collisions)
					handleCollision(collision);

				for (TestPlayer player : players.values())
				{
					Pathfinder pf = this.pathfinders.get(player.getId());
					if (pf != null)
					{
						Path path = pf.findPath();
						if (path != null)
							player.setPath(path);
					}

					Steering steering = new Steering(player);
					PlayerTracker tracker = new PlayerTracker(player, TIMER_INTERVAL);

					List<Collision> playerCollisions = collisions.stream().filter(
							c -> c.getOccupier1().getId().equals(player) || c.getOccupier2().getId().equals(player))
							.toList();

					steering.next(tracker, playerCollisions);

					player.setLV(tracker.getLV());
					player.setAV(tracker.getAV());
					player.setLoc(tracker.getLoc());
					player.setPath(tracker.getPath());
				}

				this.index.advance();

				Performance.processTime.endCycle();
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		canvas.redraw();

		this.lastMilliseconds = System.currentTimeMillis();
		shell.getDisplay().asyncExec(this);
	}

	private void handleCollision(Collision collision)
	{
		Player p1 = collision.getOccupier1();
		Player p2 = collision.getOccupier2();

		// m1 * v1 + m2 * v2 = (m1 + m2) * Vf
		// Vf = (m1 * v1 + m2 * v2) / (m1 + m2);
		double p1Mvx = p1.getMassInKilograms() * p1.getLV().getX();
		double p2Mvx = p2.getMassInKilograms() * p2.getLV().getX();
		double vxF = (p1Mvx + p2Mvx) / (p1.getMassInKilograms() + p2.getMassInKilograms());

		double p1Mvy = p1.getMassInKilograms() * p1.getLV().getY();
		double p2Mvy = p2.getMassInKilograms() * p2.getLV().getY();
		double vyF = (p1Mvy + p2Mvy) / (p1.getMassInKilograms() + p2.getMassInKilograms());

		Player player1 = players.get(p1.getId());
		Player player2 = players.get(p2.getId());

		LinearVelocity lv1 = new DefaultLinearVelocity(new Vector3D(vxF, vyF, 0));
		LinearVelocity lv2 = new DefaultLinearVelocity(new Vector3D(vxF, vyF, 0));

		player1.setLV(lv1);
		player2.setLV(lv2);
	}

	double cycleRate = Performance.cycleTime.getFrameRate();
	double cycleTimePerFrame = Performance.cycleTime.getAvgTime();
	double processRate = Performance.processTime.getAvgTime();
	double drawRate = Performance.cycleTime.getAvgTime();
	double otherRate = cycleTimePerFrame - processRate - drawRate;
	long refreshCycleCount = System.currentTimeMillis();
	long freeMemory = Runtime.getRuntime().freeMemory();
	long totalMemory = Runtime.getRuntime().totalMemory();
	long maxMemory = Runtime.getRuntime().totalMemory();

	private void drawPerformance(final GC gc)
	{
		gc.setFont(playerDataFont);
		gc.setForeground(yellow);

		long current = System.currentTimeMillis();
		if (current - refreshCycleCount > 1000)
		{
			cycleRate = Performance.cycleTime.getFrameRate();
			cycleTimePerFrame = Performance.cycleTime.getAvgTime();
			if (cycleTimePerFrame == 0)
				return;

			processRate = Performance.processTime.getAvgTime();
			drawRate = Performance.drawTime.getAvgTime();
			otherRate = cycleTimePerFrame - processRate - drawRate;
			refreshCycleCount = current;

			freeMemory = Runtime.getRuntime().freeMemory();
			totalMemory = Runtime.getRuntime().totalMemory();
			maxMemory = Runtime.getRuntime().totalMemory();
		}

		StringBuilder msg = new StringBuilder();
		msg.append(String.format("Tick Count  : %d\n", Performance.processTime.getTickCount()));
		msg.append(String.format("Frame Rate  : %.1f fps\n", cycleRate));
		msg.append(
				String.format("Process Rate: %.1f%% (%.1f ns)\n", processRate * 100 / cycleTimePerFrame, processRate));
		msg.append(String.format("Draw Rate   : %.1f%% (%.1f ns)\n", drawRate * 100 / cycleTimePerFrame, drawRate));
		msg.append(String.format("Other Rate  : %.1f%%\n", otherRate * 100 / cycleTimePerFrame));
		msg.append("\n");
		msg.append(String.format("Max Memory  : %d MB\n", maxMemory / 1000000));
		msg.append(String.format("Total Memory: %d MB\n", totalMemory / 1000000));
		msg.append(String.format("Free Memory : %d MB \n", freeMemory / 1000000));
		msg.append("\n");

		gc.drawText(msg.toString(), 10, 10, false);
	}

	public static void main(String[] args)
	{
		new PlayerTestViewer().messageLoop();
	}

}
