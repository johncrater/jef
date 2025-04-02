package jef.testapps;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import jef.formations.IFormation;
import jef.formations.IFormationCreator;
import jef.formations.IFormationPlayer;
import jef.formations.Performance;
import jef.formations.PlayerPosition;
import jef.formations.ProsetFormationCreator;
import jef.geometry.AngularVelocity;
import jef.geometry.Direction;
import jef.geometry.Field;
import jef.geometry.LinearVelocity;
import jef.geometry.Location;
import jef.movement.player.Path;
import jef.movement.player.PlayerState;
import jef.movement.player.Posture;
import jef.movement.player.Waypoint.DestinationAction;
import jef.pathfinding.PathfindingState;

public class FormationTestViewer extends AbstractFieldTestViewer<TestViewerPlayer>
{
	public static void main(String[] args)
	{
		new FormationTestViewer().messageLoop();
	}

	private static Font playerFont;
	private static FontData playerFontData = new FontData("Courier New", 16, SWT.BOLD);

	private MinnesotaVikings1976 vikings;
	private Location ballSpot = Field.MIDFIELD;

	public FormationTestViewer()
	{
		super("Formation TestViewer", OPTIONS_SHOW_MOUSE_LOCATION | OPTIONS_SHOW_PLAYERS | OPTIONS_SHOW_PERFORMANCE);
		playerFont = new Font(this.getShell().getDisplay(), playerFontData);
	}

	@Override
	protected void drawPlayer(final FieldTransformStack fts, final PlayerState player)
	{
		final double offset = IFormationPlayer.SIZE / 2.0;
		Location playerLoc = player.getLoc().add(0, 2, 0);
		fts.setFont(playerFont);

		fts.push();
		fts.translate(playerLoc);
		fts.rotate(player.getLV().getAzimuth());
		fts.set();

		fts.setBackground(fts.getSystemColor(this.getTestState().getFootballState().getPlayerInPossession() == player.getPlayerId() ? SWT.COLOR_RED : SWT.COLOR_WHITE));
		fts.fillPolygon(new Location(0, -offset), new Location(0, offset), new Location(2 * offset, 0));

		fts.pop();

		fts.setBackground(fts.getSystemColor(SWT.COLOR_WHITE));
		fts.setLineWidth(1);
		fts.fillCircle(playerLoc, (IFormationPlayer.SIZE / 2.0));
		fts.drawCircle(playerLoc, (IFormationPlayer.SIZE / 2.0));

		final String playerNumber = "" + getTestState().getPlayer(player.getPlayerId()).getFirstName().charAt(0)
				+ getTestState().getPlayer(player.getPlayerId()).getLastName().charAt(0);
		final Location extent = fts.textExtent(playerNumber);

		fts.drawText(playerNumber, playerLoc.add(extent.multiply(-.5).getX(), extent.multiply(-.5).getY(), 0), true);

	}

	@Override
	protected Composite createButtons()
	{
		Composite buttonRow = super.createButtons();

		Composite offense = new Composite(buttonRow, SWT.NONE);
		offense.setLayout(new FillLayout(SWT.VERTICAL));

		Label label = new Label(offense, SWT.NONE);
		label.setText("1976 Minnesota Vikings");

		final Combo formationCombo = new Combo(offense, SWT.DROP_DOWN);
		for (String formationName : this.vikings.getOffensiveFormationNames())
			formationCombo.add(formationName);

		formationCombo.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				vikings.lineup(vikings.getCreator(formationCombo.getItem(formationCombo.getSelectionIndex())), ballSpot);
			}
		});

		return buttonRow;
	}

	@Override
	protected void process()
	{
		this.getTestState().advance(null);
	}

	@Override
	protected PathfindingState<TestViewerPlayer> createTestState()
	{
		PathfindingState<TestViewerPlayer> pathfindingState = new PathfindingState<TestViewerPlayer>(Performance.frameInterval);
		return pathfindingState;
	}

	@Override
	public void init()
	{
		this.vikings = new MinnesotaVikings1976();
		this.getTestState().addFootball(null);
	}

	protected MinnesotaVikings1976 getVikings()
	{
		return this.vikings;
	}

	public class MinnesotaVikings1976
	{
		public final TestViewerPlayer chuckForeman;
		public final TestViewerPlayer brentMcClanahan;
		public final TestViewerPlayer franTarkenton;
		public final TestViewerPlayer ahmadRashad;
		public final TestViewerPlayer sammyWhite;
		public final TestViewerPlayer stuVoigt;
		public final TestViewerPlayer mickTinglehoff;
		public final TestViewerPlayer ronYary;
		public final TestViewerPlayer edWhite;
		public final TestViewerPlayer steveRiley;
		public final TestViewerPlayer charlesGoodrum;

		private Map<String, IFormationCreator> offensiveFormations = new HashMap<>();

		public MinnesotaVikings1976()
		{
			TestViewerPlayer pl = new TestViewerPlayer(PlayerPosition.RB);
			pl.setFirstName("Chuck");
			pl.setLastName("Foreman");
			pl.setWeight(210);
			PlayerState playerState = new PlayerState(pl.getId(), pl.getSpeedMatrix(), new LinearVelocity(), new Location(), new AngularVelocity(Math.PI, 0, 0),
					Posture.upright);
			getTestState().addPlayer(pl, playerState);
			chuckForeman = pl;

			pl = new TestViewerPlayer(PlayerPosition.FB);
			pl.setFirstName("Brent");
			pl.setLastName("McClanahan");
			pl.setWeight(202);
			playerState = new PlayerState(pl.getId(), pl.getSpeedMatrix(), new LinearVelocity(), new Location(), new AngularVelocity(Math.PI, 0, 0),
					Posture.upright);
			getTestState().addPlayer(pl, playerState);
			brentMcClanahan = pl;

			pl = new TestViewerPlayer(PlayerPosition.QB);
			pl.setFirstName("Fran");
			pl.setLastName("Tarkenton");
			pl.setWeight(190);
			playerState = new PlayerState(pl.getId(), pl.getSpeedMatrix(), new LinearVelocity(), new Location(), new AngularVelocity(Math.PI, 0, 0),
					Posture.upright);
			getTestState().addPlayer(pl, playerState);
			franTarkenton = pl;

			pl = new TestViewerPlayer(PlayerPosition.FL);
			pl.setFirstName("Ahmad");
			pl.setLastName("Rashad");
			pl.setWeight(205);
			playerState = new PlayerState(pl.getId(), pl.getSpeedMatrix(), new LinearVelocity(), new Location(), new AngularVelocity(Math.PI, 0, 0),
					Posture.upright);
			getTestState().addPlayer(pl, playerState);
			ahmadRashad = pl;

			pl = new TestViewerPlayer(PlayerPosition.WR);
			pl.setFirstName("Sammy");
			pl.setLastName("White");
			pl.setWeight(190);
			playerState = new PlayerState(pl.getId(), pl.getSpeedMatrix(), new LinearVelocity(), new Location(), new AngularVelocity(Math.PI, 0, 0),
					Posture.upright);
			getTestState().addPlayer(pl, playerState);
			sammyWhite = pl;

			pl = new TestViewerPlayer(PlayerPosition.TE);
			pl.setFirstName("Stu");
			pl.setLastName("Voigt");
			pl.setWeight(223);
			playerState = new PlayerState(pl.getId(), pl.getSpeedMatrix(), new LinearVelocity(), new Location(), new AngularVelocity(Math.PI, 0, 0),
					Posture.upright);
			getTestState().addPlayer(pl, playerState);
			stuVoigt = pl;

			pl = new TestViewerPlayer(PlayerPosition.RG);
			pl.setFirstName("Ed");
			pl.setLastName("White");
			pl.setWeight(269);
			playerState = new PlayerState(pl.getId(), pl.getSpeedMatrix(), new LinearVelocity(), new Location(), new AngularVelocity(Math.PI, 0, 0),
					Posture.upright);
			getTestState().addPlayer(pl, playerState);
			edWhite = pl;

			pl = new TestViewerPlayer(PlayerPosition.C);
			pl.setFirstName("Mick");
			pl.setLastName("Tinglehoff");
			pl.setWeight(237);
			playerState = new PlayerState(pl.getId(), pl.getSpeedMatrix(), new LinearVelocity(), new Location(), new AngularVelocity(Math.PI, 0, 0),
					Posture.upright);
			getTestState().addPlayer(pl, playerState);
			mickTinglehoff = pl;

			pl = new TestViewerPlayer(PlayerPosition.RT);
			pl.setFirstName("Ron");
			pl.setLastName("Yary");
			pl.setWeight(255);
			playerState = new PlayerState(pl.getId(), pl.getSpeedMatrix(), new LinearVelocity(), new Location(), new AngularVelocity(Math.PI, 0, 0),
					Posture.upright);
			getTestState().addPlayer(pl, playerState);
			ronYary = pl;

			pl = new TestViewerPlayer(PlayerPosition.LT);
			pl.setFirstName("Steve");
			pl.setLastName("Riley");
			pl.setWeight(258);
			playerState = new PlayerState(pl.getId(), pl.getSpeedMatrix(), new LinearVelocity(), new Location(), new AngularVelocity(Math.PI, 0, 0),
					Posture.upright);
			getTestState().addPlayer(pl, playerState);
			steveRiley = pl;

			pl = new TestViewerPlayer(PlayerPosition.LG);
			pl.setFirstName("Charles");
			pl.setLastName("Goodrum");
			pl.setWeight(256);
			playerState = new PlayerState(pl.getId(), pl.getSpeedMatrix(), new LinearVelocity(), new Location(), new AngularVelocity(Math.PI, 0, 0),
					Posture.upright);
			getTestState().addPlayer(pl, playerState);
			charlesGoodrum = pl;
			
			this.offensiveFormations.put("Pro Set", new ProsetFormationCreator());
		}

		public IFormationCreator getCreator(String name)
		{
			return this.offensiveFormations.get(name);
		}
		
		public Collection<String> getOffensiveFormationNames()
		{
			return this.offensiveFormations.keySet();
		}

		public void lineup(IFormationCreator formationCreator, Location ballSpot)
		{
			Direction direction = Direction.west;
			IFormation formation = formationCreator.createFormation(ballSpot, direction);
			
			getTestState().setPath(chuckForeman.getId(),
					new Path(formation.getPosition("HB").getLocation(),
							chuckForeman.getSpeedMatrix().getRunningSpeed(), DestinationAction.normalStop, 0,
							direction.getAngle()));

			getTestState().setPath(brentMcClanahan.getId(), new Path(formation.getPosition("FB").getLocation(),
					brentMcClanahan.getSpeedMatrix().getRunningSpeed(), DestinationAction.normalStop, 0,
					direction.getAngle()));

			getTestState().setPath(franTarkenton.getId(), new Path(formation.getPosition("QB").getLocation(),
					franTarkenton.getSpeedMatrix().getRunningSpeed(), DestinationAction.normalStop, 0,
					direction.getAngle()));

			getTestState().setPath(ahmadRashad.getId(), new Path(formation.getPosition("Z").getLocation(),
					ahmadRashad.getSpeedMatrix().getRunningSpeed(), DestinationAction.normalStop, 0,
					direction.getAngle()));

			getTestState().setPath(sammyWhite.getId(), new Path(formation.getPosition("X").getLocation(),
					sammyWhite.getSpeedMatrix().getRunningSpeed(), DestinationAction.normalStop, 0,
					direction.getAngle()));

			getTestState().setPath(stuVoigt.getId(), new Path(formation.getPosition("RTE").getLocation(),
					stuVoigt.getSpeedMatrix().getRunningSpeed(), DestinationAction.normalStop, 0,
					direction.getAngle()));

			getTestState().setPath(edWhite.getId(), new Path(formation.getPosition("RG").getLocation(),
					edWhite.getSpeedMatrix().getRunningSpeed(), DestinationAction.normalStop, 0,
					direction.getAngle()));

			getTestState().setPath(mickTinglehoff.getId(), new Path(formation.getPosition("C").getLocation(),
					mickTinglehoff.getSpeedMatrix().getRunningSpeed(), DestinationAction.normalStop, 0,
					direction.getAngle()));

			getTestState().setPath(ronYary.getId(), new Path(formation.getPosition("RT").getLocation(),
					ronYary.getSpeedMatrix().getRunningSpeed(), DestinationAction.normalStop, 0,
					direction.getAngle()));

			getTestState().setPath(steveRiley.getId(), new Path(formation.getPosition("LT").getLocation(),
					steveRiley.getSpeedMatrix().getRunningSpeed(), DestinationAction.normalStop, 0,
					direction.getAngle()));

			getTestState().setPath(charlesGoodrum.getId(), new Path(formation.getPosition("LG").getLocation(),
					charlesGoodrum.getSpeedMatrix().getRunningSpeed(), DestinationAction.normalStop, 0,
					direction.getAngle()));
		}
	}
}
