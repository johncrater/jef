package jef.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.dyn4j.geometry.Vector2;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
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

import com.synerset.unitility.unitsystem.common.Angle;

import jef.core.physics.BallPhysics;
import jef.core.units.AngularVelocity;
import jef.core.units.LinearVelocity;
import jef.core.units.Location;

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
	private static Canvas canvasYZ;

	private static Image footballBig;
	private static Image football;
	private static Image footballSmall;

	public static FontData playerFontData = new FontData("Courier New", 16, SWT.BOLD);
	public static Font trackingFont;

	public static Football ball;

	private static long hangTime = 0;
	private static double ballDistance;
	private static double ballHeight;
	private static boolean hangTimeRunning = false;
	private static List<Location> path = new ArrayList<>();
	private static List<LinearVelocity> tests = new ArrayList<>();

	static
	{
		for (int y = 80; y < 240; y += 20)
			for (int z = 80; z < 400; z += 20)
				BallTestViewer.tests.add(new LinearVelocity(0, y, z));
	}

	public static void main(final String[] args) throws IOException
	{
		BallTestViewer.createShell();

		BallTestViewer.footballBig = new Image(BallTestViewer.shell.getDisplay(),
				BallTestViewer.class.getResourceAsStream("/football-1125x672.png"));
		BallTestViewer.football = new Image(BallTestViewer.shell.getDisplay(),
				BallTestViewer.class.getResourceAsStream("/football-68x68.png"));
		BallTestViewer.footballSmall = new Image(BallTestViewer.shell.getDisplay(),
				BallTestViewer.class.getResourceAsStream("/football-34x34.png"));

		BallTestViewer.ball = new TestBall();
		BallTestViewer.ball.setLocation(27.0, 10.0, 0.0);

		final Composite c = new Composite(BallTestViewer.shell, SWT.NONE);
		final GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.heightHint = 30;
		gd.horizontalSpan = 2;
		gd.horizontalAlignment = SWT.FILL;
		c.setLayoutData(gd);

		c.setLayout(new FillLayout());

		final Button dropButton = new Button(c, SWT.PUSH);
		dropButton.setText("Drop Ball");
		dropButton.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				BallTestViewer.ball.setLinearVelocity(new LinearVelocity());
				BallTestViewer.ball.setAngularVelocity(new AngularVelocity());
				BallTestViewer.ball.setLocation(27.0, 10.0, 30.0);
				BallTestViewer.path.clear();
			}
		});

		final Button puntButton = new Button(c, SWT.PUSH);
		puntButton.setText("Punt");
		puntButton.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				BallTestViewer.ball.setLinearVelocity(new LinearVelocity(4, 20, 60));
				BallTestViewer.ball.setAngularVelocity(new AngularVelocity(0, -20));
				BallTestViewer.ball.setLocation(27.0, 10.0, 2.0);

				BallTestViewer.hangTimeRunning = true;
				BallTestViewer.hangTime = 0;
				BallTestViewer.ballDistance = 0;
				BallTestViewer.ballHeight = 0;
				BallTestViewer.path.clear();
			}
		});


		BallTestViewer.createCanvasXY(BallTestViewer.ball);
		BallTestViewer.createCanvasYZ(BallTestViewer.ball);

		BallTestViewer.trackingFont = new Font(BallTestViewer.shell.getDisplay(), BallTestViewer.playerFontData);

		BallTestViewer.shell.open();
		new BallTestViewer().run();

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

				for (int i = 10; i <= 100; i += 10)
					e.gc.drawLine(offset, offset + (int) Conversions.yardsToInches(i),
							offset + (int) BallTestViewer.totalWidth, offset + (int) Conversions.yardsToInches(i));

				final Location location = ball.getLocation();
				final Point xy = new Point((int) Conversions.yardsToInches(location.getX()),
						(int) Conversions.yardsToInches(location.getY()));

				ts.translate(xy.x, xy.y);
				ts.rotate(90);
				ts.set();
				e.gc.drawImage(BallTestViewer.football, 0 - 34, 0 - 34);
			}
			catch (final Exception e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
	}

	private static void createCanvasYZ(final Football ball)
	{
		BallTestViewer.canvasYZ = new Canvas(BallTestViewer.shell, SWT.DOUBLE_BUFFERED);
		final GridData gd = new GridData();
		gd.grabExcessVerticalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.verticalAlignment = SWT.FILL;
		gd.horizontalAlignment = SWT.FILL;
		gd.widthHint = 1300;
		BallTestViewer.canvasYZ.setLayoutData(gd);

		BallTestViewer.canvasYZ.setBackground(BallTestViewer.black);
		BallTestViewer.canvasYZ.addPaintListener(e ->
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
				for (int i = 0; i < 10; i++)
				{
					e.gc.drawRectangle((int) Conversions.yardsToInches(i * 10), 0, (int) Conversions.yardsToInches(10),
							(int) BallTestViewer.totalHeight);
					e.gc.drawRectangle(0, (int) Conversions.yardsToInches(i * 10), (int) BallTestViewer.totalLength,
							(int) Conversions.yardsToInches(10));
				}

				final Location location = ball.getLocation();

				Point xy = BallTestViewer.toYZPoint(location);
				System.out.println(location + " - " + ball.getLinearVelocity());

				try (TransformStack ts2 = new TransformStack(e.gc))
				{
					ts2.translate(xy.x, xy.y);
					ts2.rotate(Angle.ofRadians(ball.getAngularVelocity().getCurrentAngleInRadians()));
					ts2.set();
					e.gc.drawImage(BallTestViewer.footballSmall, -17, -17);
				}

				e.gc.setBackground(BallTestViewer.white);
				for (final Location l : BallTestViewer.path)
					e.gc.fillOval((int) Conversions.yardsToInches(l.getY()), (int) Conversions.yardsToInches(l.getZ()),
							7, 7);
			}
			catch (final Exception e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			final Location location = ball.getLocation();

			final StringBuilder text = new StringBuilder();
			text.append(
					String.format("Location  : %.2f, %.2f, %.2f\n", location.getX(), location.getY(), location.getZ()));
			text.append(String.format("Velocity  : %.2f, %.2f, %.2f\n", ball.getLinearVelocity().getX(),
					ball.getLinearVelocity().getY(), ball.getLinearVelocity().getZ()));
			text.append(String.format("Angular   : %.2f rad, %.2f rps\n",
					ball.getAngularVelocity().getCurrentAngleInRadians(),
					ball.getAngularVelocity().getRadiansPerSecond()));
			text.append(String.format("Hang Time : %.2f\n", BallTestViewer.hangTime / 1000.0));
			text.append(String.format("Distance  : %.0f\n", BallTestViewer.ballDistance));
			text.append(String.format("Height    : %.0f\n", BallTestViewer.ballHeight));

			e.gc.setFont(BallTestViewer.trackingFont);
			e.gc.setBackground(BallTestViewer.black);
			e.gc.drawText(text.toString(), 0, 0);
			
			try (TransformStack ts = new TransformStack(e.gc))
			{
				ts.translate(canvasYZ.getBounds().width - 400, canvasYZ.getBounds().y + 200);
				ts.scale(.15f, .15f);
				ts.rotate(Angle.ofRadians(Math.PI / 2.0 + ball.getAngularVelocity().getCurrentAngleInRadians() * -1));
				ts.set();
				
				e.gc.drawImage(BallTestViewer.footballBig, (int)(-footballBig.getImageData().width / 2.0) + 1, (int)(-footballBig.getImageData().height / 2.0) + 1);
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}
			

		});
		
		Canvas canvasFootball = new Canvas(canvasYZ, SWT.NONE);
		canvasFootball.setBounds(canvasYZ.getBounds().width - 200, canvasYZ.getBounds().y, 200, 200);
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

	private static Point toYZPoint(final Location location)
	{
		return new Point((int) Conversions.yardsToInches(location.getY()),
				(int) Conversions.yardsToInches(location.getZ()));
	}

	private static Point toYZPoint(final Vector2 v)
	{
		return new Point((int) Conversions.yardsToInches(Conversions.metersToYards(v.x)),
				(int) Conversions.yardsToInches(Conversions.metersToYards(v.y)));
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

		new BallPhysics(ball).update(.04f);
		BallTestViewer.canvasXY.redraw();
		BallTestViewer.canvasYZ.redraw();

		this.lastMilliseconds = System.currentTimeMillis();

		if (BallTestViewer.hangTimeRunning)
		{
			if (BallTestViewer.ball.getLocation().getZ() < 1)
				BallTestViewer.hangTimeRunning = false;

			BallTestViewer.ballDistance = Math.max(BallTestViewer.ball.getLocation().getY() - 10,
					BallTestViewer.ballDistance);
			BallTestViewer.ballHeight = Math.max(BallTestViewer.ball.getLocation().getZ() - 0,
					BallTestViewer.ballHeight);

			BallTestViewer.hangTime += interval;
		}

		if (!BallTestViewer.ball.getLinearVelocity().isCloseToZero())
			BallTestViewer.path.add(BallTestViewer.ball.getLocation());

		BallTestViewer.shell.getDisplay().asyncExec(this);
	}

}
