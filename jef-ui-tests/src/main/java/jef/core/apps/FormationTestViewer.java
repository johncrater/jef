package jef.core.apps;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import jef.actions.formations.OffensiveFormations;
import jef.core.Football;
import jef.core.Formation;
import jef.core.Player;
import jef.core.PlayerPosition;
import jef.geometry.AngularVelocity;
import jef.geometry.Direction;
import jef.geometry.Field;
import jef.geometry.Location;
import jef.movement.player.Path;
import jef.movement.player.PlayerState;
import jef.movement.player.Waypoint.DestinationAction;
import jef.pathfinding.Players;

public class FormationTestViewer extends AbstractFieldTestViewer
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
		final double offset = Player.SIZE / 2.0;
		Location playerLoc = player.getLoc().add(0, 2, 0);
		fts.setFont(playerFont);

		fts.push();
		fts.translate(playerLoc);
		fts.rotate(player.getLV().getAzimuth());
		fts.set();

		fts.setBackground(fts.getSystemColor(
				Football.theFootball.getPlayerInPossession() == player.getPlayer() ? SWT.COLOR_RED : SWT.COLOR_WHITE));
		fts.fillPolygon(new Location(0, -offset), new Location(0, offset), new Location(2 * offset, 0));

		fts.pop();

		fts.setBackground(fts.getSystemColor(SWT.COLOR_WHITE));
		fts.setLineWidth(1);
		fts.fillCircle(playerLoc, (Player.SIZE / 2.0));
		fts.drawCircle(playerLoc, (Player.SIZE / 2.0));

		final String playerNumber = "" + player.getPlayer().getFirstName().charAt(0)
				+ player.getPlayer().getLastName().charAt(0);
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
		for (Formation formation : this.vikings.getOffensiveFormations())
			formationCombo.add(formation.getName());

		formationCombo.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				vikings.lineup(vikings.getOffensiveFormations()
						.getFormation(formationCombo.getItem(formationCombo.getSelectionIndex())), ballSpot);
			}
		});

		return buttonRow;
	}

	@Override
	protected void process()
	{
		this.getPlayers().advance();
	}

	@Override
	protected Players createPlayers()
	{
		Players players = new Players();
		this.vikings = new MinnesotaVikings1976(players);
		return players;
	}

	protected MinnesotaVikings1976 getVikings()
	{
		return this.vikings;
	}

	public class MinnesotaVikings1976
	{
		public final Player chuckForeman;
		public final Player brentMcClanahan;
		public final Player franTarkenton;
		public final Player ahmadRashad;
		public final Player sammyWhite;
		public final Player stuVoigt;
		public final Player mickTinglehoff;
		public final Player ronYary;
		public final Player edWhite;
		public final Player steveRiley;
		public final Player charlesGoodrum;

		private OffensiveFormations offensiveFormations = new OffensiveFormations();

		public MinnesotaVikings1976(Players players)
		{
			Player pl = new Player(PlayerPosition.RB);
			pl.setFirstName("Chuck");
			pl.setLastName("Foreman");
			pl.setWeight(210);
			PlayerState playerState = new PlayerState(pl, null, new Location(), new AngularVelocity(Math.PI, 0, 0),
					PlayerState.Posture.upright);
			players.addPlayer(playerState);
			chuckForeman = pl;

			pl = new Player(PlayerPosition.FB);
			pl.setFirstName("Brent");
			pl.setLastName("McClanahan");
			pl.setWeight(202);
			playerState = new PlayerState(pl, null, new Location(), new AngularVelocity(Math.PI, 0, 0),
					PlayerState.Posture.upright);
			players.addPlayer(playerState);
			brentMcClanahan = pl;

			pl = new Player(PlayerPosition.QB);
			pl.setFirstName("Fran");
			pl.setLastName("Tarkenton");
			pl.setWeight(190);
			playerState = new PlayerState(pl, null, new Location(), new AngularVelocity(Math.PI, 0, 0),
					PlayerState.Posture.upright);
			players.addPlayer(playerState);
			franTarkenton = pl;

			pl = new Player(PlayerPosition.FL);
			pl.setFirstName("Ahmad");
			pl.setLastName("Rashad");
			pl.setWeight(205);
			playerState = new PlayerState(pl, null, new Location(), new AngularVelocity(Math.PI, 0, 0),
					PlayerState.Posture.upright);
			players.addPlayer(playerState);
			ahmadRashad = pl;

			pl = new Player(PlayerPosition.WR);
			pl.setFirstName("Sammy");
			pl.setLastName("White");
			pl.setWeight(190);
			playerState = new PlayerState(pl, null, new Location(), new AngularVelocity(Math.PI, 0, 0),
					PlayerState.Posture.upright);
			players.addPlayer(playerState);
			sammyWhite = pl;

			pl = new Player(PlayerPosition.TE);
			pl.setFirstName("Stu");
			pl.setLastName("Voigt");
			pl.setWeight(223);
			playerState = new PlayerState(pl, null, new Location(), new AngularVelocity(Math.PI, 0, 0),
					PlayerState.Posture.upright);
			players.addPlayer(playerState);
			stuVoigt = pl;

			pl = new Player(PlayerPosition.RG);
			pl.setFirstName("Ed");
			pl.setLastName("White");
			pl.setWeight(269);
			playerState = new PlayerState(pl, null, new Location(), new AngularVelocity(Math.PI, 0, 0),
					PlayerState.Posture.upright);
			players.addPlayer(playerState);
			edWhite = pl;

			pl = new Player(PlayerPosition.C);
			pl.setFirstName("Mick");
			pl.setLastName("Tinglehoff");
			pl.setWeight(237);
			playerState = new PlayerState(pl, null, new Location(), new AngularVelocity(Math.PI, 0, 0),
					PlayerState.Posture.upright);
			players.addPlayer(playerState);
			mickTinglehoff = pl;

			pl = new Player(PlayerPosition.RT);
			pl.setFirstName("Ron");
			pl.setLastName("Yary");
			pl.setWeight(255);
			playerState = new PlayerState(pl, null, new Location(), new AngularVelocity(Math.PI, 0, 0),
					PlayerState.Posture.upright);
			players.addPlayer(playerState);
			ronYary = pl;

			pl = new Player(PlayerPosition.LT);
			pl.setFirstName("Steve");
			pl.setLastName("Riley");
			pl.setWeight(258);
			playerState = new PlayerState(pl, null, new Location(), new AngularVelocity(Math.PI, 0, 0),
					PlayerState.Posture.upright);
			players.addPlayer(playerState);
			steveRiley = pl;

			pl = new Player(PlayerPosition.LG);
			pl.setFirstName("Charles");
			pl.setLastName("Goodrum");
			pl.setWeight(256);
			playerState = new PlayerState(pl, null, new Location(), new AngularVelocity(Math.PI, 0, 0),
					PlayerState.Posture.upright);
			players.addPlayer(playerState);
			charlesGoodrum = pl;
		}

		public OffensiveFormations getOffensiveFormations()
		{
			return this.offensiveFormations;
		}

		public void lineup(Formation formation, Location ballSpot)
		{
			getPlayers().setPath(chuckForeman,
					new Path(formation.getPosition("RB").place(ballSpot),
							chuckForeman.getSpeedMatrix().getRunningSpeed(), DestinationAction.normalStop, 0,
							Direction.west.getAngle()));

			getPlayers().setPath(brentMcClanahan, new Path(formation.getPosition("FB").place(ballSpot),
					brentMcClanahan.getSpeedMatrix().getRunningSpeed(), DestinationAction.normalStop, 0,
					Direction.west.getAngle()));

			getPlayers().setPath(franTarkenton, new Path(formation.getPosition("QB").place(ballSpot),
					franTarkenton.getSpeedMatrix().getRunningSpeed(), DestinationAction.normalStop, 0,
					Direction.west.getAngle()));

			getPlayers().setPath(ahmadRashad, new Path(formation.getPosition("Z").place(ballSpot),
					ahmadRashad.getSpeedMatrix().getRunningSpeed(), DestinationAction.normalStop, 0,
					Direction.west.getAngle()));

			getPlayers().setPath(sammyWhite, new Path(formation.getPosition("X").place(ballSpot),
					sammyWhite.getSpeedMatrix().getRunningSpeed(), DestinationAction.normalStop, 0,
					Direction.west.getAngle()));

			getPlayers().setPath(stuVoigt, new Path(formation.getPosition("TE").place(ballSpot),
					stuVoigt.getSpeedMatrix().getRunningSpeed(), DestinationAction.normalStop, 0,
					Direction.west.getAngle()));

			getPlayers().setPath(edWhite, new Path(formation.getPosition("RG").place(ballSpot),
					edWhite.getSpeedMatrix().getRunningSpeed(), DestinationAction.normalStop, 0,
					Direction.west.getAngle()));

			getPlayers().setPath(mickTinglehoff, new Path(formation.getPosition("C").place(ballSpot),
					mickTinglehoff.getSpeedMatrix().getRunningSpeed(), DestinationAction.normalStop, 0,
					Direction.west.getAngle()));

			getPlayers().setPath(ronYary, new Path(formation.getPosition("RT").place(ballSpot),
					ronYary.getSpeedMatrix().getRunningSpeed(), DestinationAction.normalStop, 0,
					Direction.west.getAngle()));

			getPlayers().setPath(steveRiley, new Path(formation.getPosition("LT").place(ballSpot),
					steveRiley.getSpeedMatrix().getRunningSpeed(), DestinationAction.normalStop, 0,
					Direction.west.getAngle()));

			getPlayers().setPath(charlesGoodrum, new Path(formation.getPosition("LG").place(ballSpot),
					charlesGoodrum.getSpeedMatrix().getRunningSpeed(), DestinationAction.normalStop, 0,
					Direction.west.getAngle()));
		}
	}
}
