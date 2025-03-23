package jef.core.apps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import jef.core.Field;
import jef.core.Location;
import jef.core.Performance;
import jef.core.Player;
import jef.core.PlayerState;
import jef.core.Players;
import jef.core.ui.swt.utils.DebugMessageHandler;

public abstract class TestViewer implements Runnable
{
	public static final Color black = new Color(0, 0, 0);

	private static Font playerFont;
	private static FontData playerFontData = new FontData("Courier New", 16, SWT.NORMAL);

	protected static Font getPlayerFont()
	{
		return TestViewer.playerFont;
	}

	private final Shell shell;
	private Canvas canvas;

	private Image field;

	private final DebugMessageHandler debugMessageHandler = new DebugMessageHandler();
	// field scaling and centering
	private Location midfieldLocation = Field.MIDFIELD;

	private float scaleAdjustment = 1.0f;

	private Players players;
	private long lastMilliseconds;
	private boolean paused = true;

	private boolean autoPauseActive;

	@SuppressWarnings("deprecation")
	public TestViewer(final String title)
	{
		this.shell = new Shell();
		this.shell.setMaximized(true);
		this.shell.setText(title);

		this.shell.setLayout(new GridLayout(1, false));

		TestViewer.playerFont = new Font(this.getShell().getDisplay(), TestViewer.playerFontData);

		try
		{
			final FileInputStream fis = new FileInputStream(new File(title + ".props"));
			final Properties props = new Properties();
			props.load(fis);
			final Rectangle rect = new Rectangle(Integer.parseInt(props.getProperty("bounds.x")),
					Integer.parseInt(props.getProperty("bounds.y")),
					Integer.parseInt(props.getProperty("bounds.width")),
					Integer.parseInt(props.getProperty("bounds.height")));
			this.getShell().setBounds(rect);
		}
		catch (final Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.getShell().addListener(SWT.Close, e ->
		{
			final Rectangle bounds = this.getShell().getBounds();
			final Properties properties = new Properties();
			properties.put("bounds.x", "" + bounds.x);
			properties.put("bounds.y", "" + bounds.y);
			properties.put("bounds.width", "" + bounds.width);
			properties.put("bounds.height", "" + bounds.height);

			try
			{
				properties.save(new FileOutputStream(new File(title + ".props")), "");
			}
			catch (final FileNotFoundException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});

	}

	public boolean isAutoPauseActive()
	{
		return this.autoPauseActive;
	}

	public boolean isPaused()
	{
		return this.paused;
	}

	public void messageLoop()
	{
		this.lastMilliseconds = System.currentTimeMillis();

		this.players = this.createPlayers();
		this.createButtons();
		this.createCanvas();

		this.shell.open();

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
			this.getShell().getDisplay().asyncExec(this);
			return;
		}

		Performance.cycleTime.endCycle();
		Performance.cycleTime.beginCycle();
		Performance.processTime.beginCycle();

		if (!this.paused)
		{
			if (this.autoPauseActive)
			{
				this.paused = true;
			}

			try
			{
				this.debugMessageHandler.clear();

				this.process();

				Performance.processTime.endCycle();
			}
			catch (final Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		this.canvas.redraw();

		this.lastMilliseconds = System.currentTimeMillis();
		this.getShell().getDisplay().asyncExec(this);
	}

	public void setAutoPauseActive(final boolean autoPauseActive)
	{
		this.autoPauseActive = autoPauseActive;
	}

	public void setPaused(final boolean pause)
	{
		this.paused = pause;
	}

	protected Composite createButtons()
	{
		final Composite buttonRow = new Composite(this.getShell(), SWT.NONE);
		buttonRow.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		buttonRow.setLayout(new FillLayout(SWT.HORIZONTAL));

		final Composite c2 = new Composite(buttonRow, SWT.NONE);
		c2.setLayout(new FillLayout(SWT.HORIZONTAL));

		final Button autoPauseButton = new Button(c2, SWT.PUSH);
		autoPauseButton.setText("Auto Pause: Off");
		autoPauseButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				TestViewer.this.setAutoPauseActive(!TestViewer.this.isAutoPauseActive());
				autoPauseButton.setText(TestViewer.this.isAutoPauseActive() ? "Auto Pause: On" : "Auto Pause: Off");
			}
		});

		final Button pauseButton = new Button(c2, SWT.PUSH);
		pauseButton.setText("Un Pause");
		pauseButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				TestViewer.this.setPaused(!TestViewer.this.isPaused());
				pauseButton.setText(TestViewer.this.isPaused() ? "Un Pause" : "Pause");
			}
		});

		return buttonRow;
	}

	protected void createCanvas()
	{
		this.canvas = new Canvas(this.shell, SWT.DOUBLE_BUFFERED);
		this.canvas.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.FILL_VERTICAL));
		this.canvas.setBackground(TestViewer.black);
		this.canvas.layout(true);

		this.field = new Image(this.shell.getDisplay(), this.getClass().getResourceAsStream("/field-4500x2124.png"));

		this.canvas.addPaintListener(e ->
		{
			Performance.drawTime.beginCycle();

			this.drawPreTransformedCanvas(e.gc);

			try (FieldTransformStack ts = new FieldTransformStack(this.canvas, e.gc, this.midfieldLocation,
					this.scaleAdjustment))
			{
				this.drawTransformedCanvas(ts);
			}
			catch (final Exception e1)
			{
				e1.printStackTrace();
			}

			this.drawPostTransformedCanvas(e.gc);

			Performance.drawTime.endCycle();
		});

		this.canvas.addMouseListener(new MouseAdapter()
		{

			@Override
			public void mouseUp(final MouseEvent e)
			{
				super.mouseUp(e);

				final Point p = new Point(e.x, e.y);

				try (FieldTransformStack ts = new FieldTransformStack(TestViewer.this.canvas,
						TestViewer.this.midfieldLocation, TestViewer.this.scaleAdjustment))
				{
					if ((e.stateMask & SWT.CONTROL) != 0)
					{
						TestViewer.this.midfieldLocation = ts.transformToLocation(p);
					}
				}
				catch (final Exception e1)
				{
				}
			}

		});

		this.canvas.addMouseWheelListener(e ->
		{
			if ((e.stateMask & SWT.CONTROL) != 0)
			{
				this.scaleAdjustment = (float) Math.max(.25, this.scaleAdjustment + (Math.signum(e.count) * .25));
			}
		});

	}

	protected abstract Players createPlayers();

	protected abstract void drawPlayer(FieldTransformStack ts, PlayerState playerState);

	protected void drawPostTransformedCanvas(final GC gc)
	{

	}

	protected void drawPreTransformedCanvas(final GC gc)
	{

	}

	protected void drawTransformedCanvas(final FieldTransformStack ts)
	{
		ts.getGC().drawImage(this.field, 0, 0);
		for (final Player player : this.players.getPlayers())
		{
			this.drawPlayer(ts, this.players.getState(player));
		}

		this.debugMessageHandler.draw(ts.getGC());
	}

	protected Canvas getCanvas()
	{
		return this.canvas;
	}

	protected Location getMidfieldLocation()
	{
		return this.midfieldLocation;
	}

	protected Players getPlayers()
	{
		return this.players;
	}

	protected float getScaleAdjustment()
	{
		return this.scaleAdjustment;
	}

	protected Shell getShell()
	{
		return this.shell;
	}

	protected abstract void process();
}
