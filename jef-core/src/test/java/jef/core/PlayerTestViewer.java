package jef.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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

import jef.core.movement.DefaultLinearVelocity;
import jef.core.movement.DefaultLocation;
import jef.core.movement.Location;
import jef.core.movement.player.PlayerTracker;
import jef.core.movement.player.Steering;
import jef.core.movement.player.Waypoint;
import jef.core.movement.player.Waypoint.DestinationAction;

public class PlayerTestViewer implements Runnable
{
	private static final double TIMER_INTERVAL = .05f;
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

	private TestPlayer player;
	private List<TestPlayer> players = new ArrayList<TestPlayer>();
	private DestinationAction nextDestinationAction = DestinationAction.fastStop;
	
	public PlayerTestViewer()
	{
		this.shell = new Shell();
		shell.setMaximized(true);
		shell.setText("Player Test Viewer");

		this.lastMilliseconds = System.currentTimeMillis();

		shell.setLayout(new GridLayout(1, false));
		createButtons();
		createCanvas();

		playerFont = new Font(shell.getDisplay(), playerFontData);
		playerDataFont = new Font(shell.getDisplay(), playerDataFontData);
		
		createPlayers();
		
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
		
	}

	private void createPlayers()
	{
		player = new TestPlayer();
		player.setNumber(44);
		this.players.add(player);
	}

	private void createCanvas()
	{
		canvas = new Canvas(shell, SWT.DOUBLE_BUFFERED);
		canvas.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.FILL_VERTICAL));
		canvas.setBackground(black);
		canvas.layout(true);
		
		field = new Image(shell.getDisplay(),
				this.getClass().getResourceAsStream("/field-4500x2124.png"));

		canvas.addPaintListener(e ->
		{
			try (TransformStack ts = new TransformStack(e.gc))
			{
				float scaleX = (float)(canvas.getClientArea().width / totalLength);
				float scaleY = (float)(canvas.getClientArea().height / totalWidth);
				float scale = Math.min(scaleX, scaleY);
				ts.scale(scale, scale);
				ts.set();
				
				e.gc.drawImage(field, 0, 0);
				
				for (TestPlayer player : players)
				{
					drawPlayer(e.gc, player);
				}
				
				player = players.getFirst();

				drawPerformance(e.gc);
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
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
					float scaleX = (float)(canvas.getClientArea().width / totalLength);
					float scaleY = (float)(canvas.getClientArea().height / totalWidth);
					float scale = Math.min(scaleX, scaleY);
					ts.scale(1 / scale, 1 / scale);
					ts.set();

					p = ts.transform(p);
					p.y = (int)(totalWidth - p.y);
					Location loc = pointToLocation(p);
					
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
		int offset = (int) Conversions.yardsToInches(Player.size / 2);

		gc.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		gc.setFont(playerFont);
		
		Point p = locationToPoint(player.getLoc());
		gc.fillOval(p.x - offset, p.y - offset, offset * 2, offset * 2);

		String playerNumber = "" + player.getNumber();
		Point extent = gc.textExtent(playerNumber);
		
		gc.drawText(playerNumber, p.x - extent.x / 2, p.y - extent.y  / 2);
		
		StringBuilder str = new StringBuilder();
		str.append(String.format("Name            : %s %s\n", player.getFirstName(), player.getLastName()));
		str.append(String.format("Location        : %s\n", player.getLoc()));
		str.append(String.format("Linear velocity : %s\n", player.getLV()));
		str.append(String.format("Angular velocity: %s\n", player.getAV()));
		str.append(String.format("\n"));
		
		for (Waypoint wp : player.getPath().getWaypoints())
			str.append(String.format("       waypoint : %s - Dist: %.2f\n", wp, player.getLoc().distanceBetween(wp.getDestination())));
		
		gc.setFont(playerDataFont);
		gc.setForeground(yellow);
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
		for (TestPlayer player : players)
		{
			Steering steering = new Steering(player);
			PlayerTracker tracker = new PlayerTracker(player.getPath(), player, TIMER_INTERVAL);

			steering.next(tracker);
			
			player.setLV(tracker.getLV());
			player.setAV(tracker.getAV());
			player.setLoc(tracker.getLoc());
			player.setPath(tracker.getPath());
		}
		Performance.processTime.endCycle();
		
		Performance.drawTime.beginCycle();
		canvas.redraw();
		Performance.drawTime.endCycle();

		this.lastMilliseconds = System.currentTimeMillis();
		shell.getDisplay().asyncExec(this);
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
		msg.append(String.format("Process Rate: %.1f%% (%.1f ns)\n", processRate * 100 / cycleTimePerFrame, processRate));
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
