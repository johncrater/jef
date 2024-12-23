package jef.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Properties;

import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import jef.core.Conversions;
import jef.core.Player;
import jef.core.physics.PhysicsPlayer;
import jef.core.physics.PhysicsWorld;
import jef.core.units.Field;
import jef.core.units.Location;

public class PlayerTestViewer implements Runnable
{
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

	private PhysicsWorld physicsWorld = new PhysicsWorld();
	private TestPlayer player;
	
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
		
		field = new Image(shell.getDisplay(),
				this.getClass().getResourceAsStream("/field-4500x2124.png"));

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
		
		Button btn = new Button(c, SWT.PUSH);
		btn.setText("Linear Dampening");
		btn.addSelectionListener(new SelectionAdapter() 
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				player.setLocation(50.0, 27.0, 0.0);
				player.setLinearVelocity(10.0, 10.0, 0.0);
			}
		});
	}

	private void createPlayers()
	{
		player = new TestPlayer();
		player.setNumber(44);
		player.setLocation(50.0, 27.0, 0.0);
		this.physicsWorld.addPlayer(new PhysicsPlayer(player));
	}

	private void createCanvas()
	{
		canvas = new Canvas(shell, SWT.DOUBLE_BUFFERED);
		canvas.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.FILL_VERTICAL));
		canvas.setBackground(black);
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
				
				for (PhysicsPlayer player : physicsWorld.getPlayers())
				{
					drawPlayer(e.gc, player.getPlayer());
				}
				
				Player player = physicsWorld.getPlayers().getFirst().getPlayer();
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
		});
	}

	public void messageLoop()
	{
		shell.open();
		run();

		while (!shell.isDisposed())
			try
			{
				if (shell.getDisplay().readAndDispatch())
					if (!shell.isDisposed())
						shell.getDisplay().sleep();
			}
			catch (final Throwable t)
			{
				t.printStackTrace();
			}
	}

	private void drawPlayer(GC gc, Player player)
	{
		int offset = (int) Conversions.yardsToInches(Player.size / 2);

		gc.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		gc.setFont(playerFont);
		
		Point p = locationToPoint(player.getLocation());
		gc.fillOval(p.x - offset, p.y - offset, offset * 2, offset * 2);

		String playerNumber = "" + player.getNumber();
		Point extent = gc.textExtent(playerNumber);
		
		gc.drawText(playerNumber, p.x - extent.x / 2, p.y - extent.y  / 2);
		
		StringBuilder str = new StringBuilder();
		str.append(String.format("Name            : %s %s\n", player.getFirstName(), player.getLastName()));
		str.append(String.format("Location        : %f %f\n", player.getLocation().getX(), player.getLocation().getY()));
		str.append(String.format("Linear velocity : %f %f y/s\n", player.getLinearVelocity().getX(), player.getLinearVelocity().getY()));
		str.append(String.format("Angular velocity: %.2f %.2f r/s\n", player.getAngularVelocity().getCurrentAngleInRadians(), player.getAngularVelocity().getRadiansPerSecond()));
		
		gc.setFont(playerDataFont);
		gc.drawText(str.toString(), 20, 20);
	}

	private Point locationToPoint(Location loc)
	{
		int x = (int) Conversions.yardsToInches(loc.getX() + Field.FIELD_BORDER_WIDTH + Field.FIELD_END_ZONE_DEPTH);
		int y = (int) Conversions.yardsToInches(loc.getY() + Field.FIELD_BORDER_WIDTH);
		return new Point(x, y);
	}

	public void run()
	{
		final long interval = System.currentTimeMillis() - this.lastMilliseconds;
		if (interval < 25)
		{
			shell.getDisplay().asyncExec(this);
			return;
		}

		physicsWorld.update(.04f);
		canvas.redraw();

		this.lastMilliseconds = System.currentTimeMillis();
		shell.getDisplay().asyncExec(this);
	}

	public static void main(String[] args)
	{
		new PlayerTestViewer().messageLoop();
	}

}
