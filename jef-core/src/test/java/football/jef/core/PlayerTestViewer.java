package football.jef.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Shell;

import football.jef.core.physics.Field;
import football.jef.core.physics.PhysicsWorld;
import football.jef.core.units.Location;

public class PlayerTestViewer implements Runnable
{
	private static final double totalLength = Conversions.yardsToInches(125);
	private static final double totalWidth = Conversions.yardsToInches(54 + 5);
	private static final double totalHeight = Conversions.yardsToInches(50);

	private static FontData playerFontData = new FontData("Courier New", 5, SWT.NORMAL);
	private static Font playerFont;

	private Shell shell;
	private Canvas canvas;
	private long lastMilliseconds;
	private Image field;

	private PhysicsWorld physicsWorld = new PhysicsWorld();
	private List<Player> players = new ArrayList<>();

	public PlayerTestViewer()
	{
		this.shell = new Shell();
		shell.setMaximized(true);
		shell.setText("Player Test Viewer");

		this.lastMilliseconds = System.currentTimeMillis();

		shell.setLayout(new FillLayout());
		createCanvas();

		playerFont = new Font(shell.getDisplay(), playerFontData);
		field = new Image(shell.getDisplay(),
				this.getClass().getResourceAsStream("/field.png"));

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

	private void createPlayers()
	{
		TestPlayer player = new TestPlayer();
		player.setNumber(44);
		player.setLocation(50.0, 27.0, 0.0);
		this.players.add(player);
	}

	private void createCanvas()
	{
		canvas = new Canvas(shell, SWT.DOUBLE_BUFFERED);
		canvas.addPaintListener(e ->
		{
			try (TransformStack ts = new TransformStack(e.gc))
			{
				e.gc.drawImage(field, 0, 0);
				
				for (Player player : players)
				{
					drawPlayer(e.gc, player);
				}
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

		gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		gc.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		gc.setFont(playerFont);
		
		Point p = locationToPoint(player.getLocation());
		gc.fillOval(p.x - offset, p.y - offset, offset * 2, offset * 2);

		String playerNumber = "" + player.getNumber();
		Point extent = gc.textExtent(playerNumber);
		gc.drawText(playerNumber, p.x - offset + 2 * offset - extent.x / 2, p.y - offset + 2 * offset - extent.y / 2);
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
