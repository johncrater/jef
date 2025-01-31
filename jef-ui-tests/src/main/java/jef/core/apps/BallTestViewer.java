package jef.core.apps;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import jef.core.Conversions;
import jef.core.DefaultFootball;
import jef.core.Football;
import jef.core.Performance;
import jef.core.movement.DefaultAngularVelocity;
import jef.core.movement.DefaultLinearVelocity;
import jef.core.movement.DefaultLocation;
import jef.core.movement.Location;
import jef.core.movement.ball.BallPhysics;
import jef.core.movement.ball.BallTracker;
import jef.core.ui.swt.utils.TransformStack;

public class BallTestViewer implements Runnable
{
	private static final double totalLength = Conversions.yardsToInches(125);
	private static final double totalWidth = Conversions.yardsToInches(54 + 5);
	private static final double totalHeight = Conversions.yardsToInches(80);
	private static final double screenLength = 1300D;
	private static final double screenHeight = 600D;

	private static final Color fieldGreen = new Color(70, 137, 68);
	private static final Color white = new Color(255, 255, 255);
	private static final Color yellow = new Color(255, 255, 0);
	private static final Color black = new Color(0, 0, 0);
	private static final Color red = new Color(255, 0, 0);

	private static Shell shell;
	private static Canvas canvasXY;
	private static Canvas canvasXZ;

	private static Image footballBig;
	private static Image football;
	private static Image footballSmall;

	public static FontData playerFontData = new FontData("Courier New", 16, SWT.BOLD);
	private static Font playerDataFont;
	public static Font trackingFont;

	public static DefaultFootball ball;

	private static long hangTime = 0;
	private static double ballDistance;
	private static double ballHeight;
	private static boolean hangTimeRunning = false;
	private static List<Location> path = new ArrayList<>();

	public static void main(final String[] args) throws IOException
	{
		BallTestViewer.createShell();

		playerDataFont = new Font(shell.getDisplay(), playerFontData);

		BallTestViewer.footballBig = new Image(BallTestViewer.shell.getDisplay(),
				BallTestViewer.class.getResourceAsStream("/football-1125x672.png"));
		BallTestViewer.football = new Image(BallTestViewer.shell.getDisplay(),
				BallTestViewer.class.getResourceAsStream("/football-68x68.png"));
		BallTestViewer.footballSmall = new Image(BallTestViewer.shell.getDisplay(),
				BallTestViewer.class.getResourceAsStream("/football-34x34.png"));

		BallTestViewer.ball = new DefaultFootball();
		BallTestViewer.ball.setLoc(new DefaultLocation(10.0, 27.0, 0.0));

		final Composite c = new Composite(BallTestViewer.shell, SWT.NONE);
		final GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.heightHint = 30;
		gd.horizontalSpan = 2;
		gd.horizontalAlignment = SWT.FILL;
		c.setLayoutData(gd);

		c.setLayout(new FillLayout());

		Label l = new Label(c, SWT.None);
		l.setText("Phi");
		Text phi = new Text(c, SWT.None);
		phi.setText("0");
		
		l = new Label(c, SWT.NONE);
		l.setText("Theta");
		Text theta = new Text(c, SWT.NONE);
		theta.setText("-90");
		
		l = new Label(c, SWT.NONE);
		l.setText("Speed");
		Text speed = new Text(c, SWT.NONE);
		speed.setText("25");
		
		l = new Label(c, SWT.NONE);
		l.setText("Omega");
		Text omega = new Text(c, SWT.NONE);
		omega.setText("0");
		
		l = new Label(c, SWT.NONE);
		l.setText("Height");
		Text height = new Text(c, SWT.NONE);
		height.setText("1.0");
		
		l = new Label(c, SWT.NONE);
		l.setText("Azimuth");
		Text azimuth = new Text(c, SWT.NONE);
		azimuth.setText("0");
		
		l = new Label(c, SWT.NONE);
		l.setText("YardLine");
		Text yardLine = new Text(c, SWT.NONE);
		yardLine.setText("50");
		
		Button b = new Button(c, SWT.PUSH);
		b.setText("Go!");
		b.addSelectionListener(new SelectionAdapter() 
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				double thetaInDegrees = Double.parseDouble(theta.getText());
				double speedInYPS = Double.parseDouble(speed.getText());
				double azimuthInRadians = Double.parseDouble(azimuth.getText());
				BallTestViewer.ball.setLV(new DefaultLinearVelocity(azimuthInRadians, Math.toRadians(thetaInDegrees), speedInYPS));

				double phiInDegrees = Double.parseDouble(phi.getText());
				double omegaInRadiansPerSecond = Double.parseDouble(omega.getText());
				BallTestViewer.ball.setAV(new DefaultAngularVelocity(Math.toRadians(phiInDegrees), omegaInRadiansPerSecond));

				double heightInYards =  Double.parseDouble(height.getText());
				double yardLineValue =  Double.parseDouble(yardLine.getText()) + 10;
				
				BallTestViewer.ball.setLoc(new DefaultLocation(yardLineValue, 27.0, heightInYards));
				BallTestViewer.path.clear();
			}
			
		});

		BallTestViewer.createCanvasXY(BallTestViewer.ball);
		BallTestViewer.createCanvasXZ(BallTestViewer.ball);

		BallTestViewer.trackingFont = new Font(BallTestViewer.shell.getDisplay(), BallTestViewer.playerFontData);

		BallTestViewer.shell.open();
		new BallTestViewer().run();
		Performance.cycleTime.beginCycle();

		while (!BallTestViewer.shell.isDisposed())
			try
			{
				if (!BallTestViewer.shell.getDisplay().readAndDispatch())
					BallTestViewer.shell.getDisplay().sleep();
			}
			catch (final Throwable t)
			{
				t.printStackTrace();
			}

	}

	private static void createCanvasXY(final Football ball)
	{
		BallTestViewer.canvasXY = new Canvas(BallTestViewer.shell, SWT.DOUBLE_BUFFERED);
		final GridData gd = new GridData();
		gd.grabExcessVerticalSpace = true;
		gd.verticalAlignment = SWT.FILL;
		gd.horizontalAlignment = SWT.FILL;
		gd.widthHint = (int) BallTestViewer.screenHeight;
		BallTestViewer.canvasXY.setLayoutData(gd);

		BallTestViewer.canvasXY.addPaintListener(e ->
		{
			final int offset = 20;

			try (TransformStack ts = new TransformStack(e.gc))
			{
				ts.scale((float) (BallTestViewer.canvasXY.getClientArea().height / BallTestViewer.totalLength),
						(float) (BallTestViewer.canvasXY.getClientArea().height / BallTestViewer.totalLength));
				ts.set();

				e.gc.setBackground(BallTestViewer.fieldGreen);
				e.gc.setForeground(BallTestViewer.white);
				e.gc.setLineWidth(4);
				e.gc.fillRectangle(offset, offset, (int) BallTestViewer.totalWidth, (int) BallTestViewer.totalLength);

				for (int i = 10; i < 120; i += 10)
					e.gc.drawLine(offset, 3 * offset + (int) Conversions.yardsToInches(i),
							offset + (int) BallTestViewer.totalLength, offset + (int) Conversions.yardsToInches(i));

				final Location location = ball.getLoc();
				final Point xy = new Point((int) Conversions.yardsToInches(location.getY()),
						(int) Conversions.yardsToInches(location.getX()));

				ts.translate(xy.x + offset, xy.y + 3 * offset);
				ts.rotate(90);
				ts.set();
				e.gc.drawImage(BallTestViewer.football, 0 - 34, 0 - 34);
				
			}
			catch (final Exception e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			BallTestViewer.drawPerformance(e.gc);
		});
	}

	private static void createCanvasXZ(final Football ball)
	{
		BallTestViewer.canvasXZ = new Canvas(BallTestViewer.shell, SWT.DOUBLE_BUFFERED);
		final GridData gd = new GridData();
		gd.grabExcessVerticalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.verticalAlignment = SWT.FILL;
		gd.horizontalAlignment = SWT.FILL;
		gd.widthHint = 1300;
		BallTestViewer.canvasXZ.setLayoutData(gd);

		BallTestViewer.canvasXZ.setBackground(BallTestViewer.black);
		BallTestViewer.canvasXZ.addPaintListener(e ->
		{
			final int xOffset = 20;
			final int yOffset = 1000;

			try (TransformStack ts = new TransformStack(e.gc))
			{
				ts.setElements(1, 0, 0, -1, 0, 0);
				ts.scale((float) (BallTestViewer.screenLength / BallTestViewer.totalLength),
						(float) (BallTestViewer.screenLength / BallTestViewer.totalLength));
//				ts.scale(.5f, .5f);
				ts.translate(xOffset, (float) -BallTestViewer.totalWidth - yOffset);
				ts.set();

				e.gc.setForeground(BallTestViewer.yellow);
				for (int i = 0; i < 11; i++)
				{
					e.gc.drawRectangle((int) Conversions.yardsToInches(i * 10), 0, (int) Conversions.yardsToInches(10),
							(int) BallTestViewer.totalHeight);
					e.gc.drawRectangle(0, (int) Conversions.yardsToInches(i * 10), (int) BallTestViewer.totalLength,
							(int) Conversions.yardsToInches(10));
				}

				final Location location = ball.getLoc();

				Point xy = BallTestViewer.toXZPoint(location);

				try (TransformStack ts2 = new TransformStack(e.gc))
				{
					ts2.translate(xy.x, xy.y);
					ts2.rotate(ball.getAV().getOrientation() * -1);
					ts2.set();
					e.gc.drawImage(BallTestViewer.footballSmall, -17, -17);
				}

				e.gc.setBackground(BallTestViewer.white);
				for (final Location l : BallTestViewer.path)
					e.gc.fillOval((int) Conversions.yardsToInches(l.getX()), (int) Conversions.yardsToInches(l.getZ()),
							7, 7);
			}
			catch (final Exception e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			final Location location = ball.getLoc();

			final StringBuilder text = new StringBuilder();
			text.append(
					String.format("Location  : %.2f, %.2f, %.2f\n", location.getX(), location.getY(), location.getZ()));
			text.append(String.format("Velocity  : %s\n", ball.getLV()));
			text.append(String.format("Angular   : %.0f\u00B0, %.2f r/s\n",
					Math.toDegrees(ball.getAV().getOrientation()),
					ball.getAV().getRotation()));
			text.append(String.format("Hang Time : %.2f\n", BallTestViewer.hangTime / 1000.0));
			text.append(String.format("Distance  : %.0f\n", BallTestViewer.ballDistance));
			text.append(String.format("Height    : %.0f\n", BallTestViewer.ballHeight));

			e.gc.setFont(BallTestViewer.trackingFont);
			e.gc.setBackground(BallTestViewer.black);
			e.gc.drawText(text.toString(), 0, 0);
			
			try (TransformStack ts = new TransformStack(e.gc))
			{
				ts.translate(canvasXZ.getBounds().width - 400, canvasXZ.getBounds().y + 200);
				ts.scale(.15f, .15f);
				ts.rotate(Math.PI / 2.0 + ball.getAV().getOrientation());
				ts.set();
				
				e.gc.drawImage(BallTestViewer.footballBig, (int)(-footballBig.getImageData().width / 2.0) + 1, (int)(-footballBig.getImageData().height / 2.0) + 1);
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
			

		});
		
		Canvas canvasFootball = new Canvas(canvasXZ, SWT.NONE);
		canvasFootball.setBounds(canvasXZ.getBounds().width - 200, canvasXZ.getBounds().y, 200, 200);
		canvasFootball.addPaintListener(e -> 
		{
			try (TransformStack ts = new TransformStack(e.gc))
			{
				ts.scale(.15f, .15f);
				ts.set();
				
				e.gc.drawImage(footballBig, 0, 0);
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
		});
	}

	@SuppressWarnings("deprecation")
	private static void createShell()
	{
		BallTestViewer.shell = new Shell();
		BallTestViewer.shell.setMaximized(true);
		BallTestViewer.shell.setText("Ball Test Viewer");
		BallTestViewer.shell.setLayout(new GridLayout(2, false));
		BallTestViewer.shell.setBackground(new Color(0, 0, 0));

		try
		{
			final FileInputStream fis = new FileInputStream(new File("BallTestViewer.props"));
			final Properties props = new Properties();
			props.load(fis);
			final Rectangle rect = new Rectangle(Integer.parseInt(props.getProperty("bounds.x")),
					Integer.parseInt(props.getProperty("bounds.y")),
					Integer.parseInt(props.getProperty("bounds.width")),
					Integer.parseInt(props.getProperty("bounds.height")));
			BallTestViewer.shell.setBounds(rect);
		}
		catch (final Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		BallTestViewer.shell.addListener(SWT.Close, e ->
		{
			final Rectangle bounds = BallTestViewer.shell.getBounds();
			final Properties properties = new Properties();
			properties.put("bounds.x", "" + bounds.x);
			properties.put("bounds.y", "" + bounds.y);
			properties.put("bounds.width", "" + bounds.width);
			properties.put("bounds.height", "" + bounds.height);

			try
			{
				properties.save(new FileOutputStream(new File("BallTestViewer.props")), "");
			}
			catch (final FileNotFoundException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
	}

	private static Point toXZPoint(final Location location)
	{
		return new Point((int) Conversions.yardsToInches(location.getX()),
				(int) Conversions.yardsToInches(location.getZ()));
	}

	private long lastMilliseconds = System.currentTimeMillis();

	@Override
	public void run()
	{
		final long interval = System.currentTimeMillis() - this.lastMilliseconds;
		if (interval < 25)
		{
			BallTestViewer.shell.getDisplay().asyncExec(this);
			return;
		}

		try
		{
			Performance.cycleTime.endCycle();
			Performance.cycleTime.beginCycle();
			Performance.processTime.beginCycle();

			BallTracker tracker = new BallTracker(ball, .04);
			new BallPhysics().update(tracker);
			ball.setAV(tracker.getAV());
			ball.setLV(tracker.getLV());
			ball.setLoc(tracker.getLoc());
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Performance.processTime.endCycle();
		
		Performance.drawTime.beginCycle();
		BallTestViewer.canvasXY.redraw();
		BallTestViewer.canvasXZ.redraw();
		Performance.drawTime.endCycle();

		
		this.lastMilliseconds = System.currentTimeMillis();

		if (BallTestViewer.hangTimeRunning)
		{
			if (BallTestViewer.ball.getLoc().getZ() < 1)
				BallTestViewer.hangTimeRunning = false;

			BallTestViewer.ballDistance = Math.max(BallTestViewer.ball.getLoc().getX() - 10,
					BallTestViewer.ballDistance);
			BallTestViewer.ballHeight = Math.max(BallTestViewer.ball.getLoc().getZ() - 0,
					BallTestViewer.ballHeight);

			BallTestViewer.hangTime += interval;
		}

		if (!BallTestViewer.ball.getLV().isNotMoving())
			BallTestViewer.path.add(BallTestViewer.ball.getLoc());

		BallTestViewer.shell.getDisplay().asyncExec(this);
	}

	static double cycleRate = Performance.cycleTime.getFrameRate();
	static double cycleTimePerFrame = Performance.cycleTime.getAvgTime();
	static double processRate = Performance.processTime.getAvgTime();
	static double drawRate = Performance.cycleTime.getAvgTime();
	static double otherRate = cycleTimePerFrame - processRate - drawRate;
	static long refreshCycleCount = System.currentTimeMillis();
	static long freeMemory = Runtime.getRuntime().freeMemory();
	static long totalMemory = Runtime.getRuntime().totalMemory();
	static long maxMemory = Runtime.getRuntime().totalMemory();

	private static void drawPerformance(final GC gc)
	{
		gc.setFont(playerDataFont);
		gc.setForeground(black);

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
		
		gc.drawText(msg.toString(), 10, 10, true);
	}
}
