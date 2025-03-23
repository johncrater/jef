package jef.core.apps;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import jef.actions.formations.Formation;
import jef.actions.formations.OffensiveFormations;
import jef.core.AngularVelocity;
import jef.core.Conversions;
import jef.core.Field;
import jef.core.Location;
import jef.core.Player;
import jef.core.PlayerPosition;
import jef.core.PlayerState;
import jef.core.Players;
import jef.core.movement.Posture;
import jef.core.movement.player.Path;
import jef.core.movement.player.Waypoint.DestinationAction;
import jef.core.ui.swt.utils.UIUtils;

public class FormationTestViewer extends TestViewer
{
	public static void main(String[] args)
	{
		new FormationTestViewer().messageLoop();
	}

	private MinnesotaVikings1976 vikings;
	private Location ballSpot = Field.MIDFIELD;

	public FormationTestViewer()
	{
		super("Formation TestViewer");
	}

	@Override
	protected void drawPlayer(final FieldTransformStack fts, final PlayerState player)
	{
		final int offset = (int) Conversions.yardsToInches((Player.SIZE) / 2.0);

		final GC gc = fts.getGC();
		gc.setFont(getPlayerFont());
		final Point p = UIUtils.locationToPoint(player.getLoc());

		gc.setForeground(this.getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_MAGENTA));
		gc.setBackground(this.getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		gc.fillOval(p.x - offset, p.y - offset, offset * 2, offset * 2);

		fts.push();
		fts.translate(p);
		fts.rotate(player.getAV().getOrientation());
		fts.set();

		final int lineWidth = 3;
		gc.fillPolygon(new int[]
		{ 0, -offset + lineWidth, 0, offset - lineWidth, 2 * offset - 2 * lineWidth, 0 });

		fts.pop();

		final String playerNumber = "" + player.getPlayer().getFirstName().charAt(0)
				+ player.getPlayer().getLastName().charAt(0);
		final Point extent = gc.textExtent(playerNumber);

		gc.drawText(playerNumber, p.x - (extent.x / 2), p.y - (extent.y / 2), true);

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
//		public final Player brentMcClanahan;
//		public final Player franTarkenton;
//		public final Player ahmadRashad;
//		public final Player sammyWhite;
//		public final Player stuVoigt;
//		public final Player mickTinglehoff;
//		public final Player ronYary;
//		public final Player edWhite;
//		public final Player steveRiley;
//		public final Player charlesGoodrum;

		private OffensiveFormations offensiveFormations = new OffensiveFormations();

		public MinnesotaVikings1976(Players players)
		{
			Player pl = new Player(PlayerPosition.RB);
			pl.setFirstName("Chuck");
			pl.setLastName("Foreman");
			pl.setWeight(210);
			PlayerState playerState = new PlayerState(pl, null, new Location(), new AngularVelocity(Math.PI, 0, 0),
					Posture.upright);
			players.addPlayer(playerState);
			chuckForeman = pl;
//
//			pl = new Player(PlayerPosition.FB);
//			pl.setFirstName("Brent");
//			pl.setLastName("McClanahan");
//			pl.setWeight(202);
//			playerState = new PlayerState(pl, null, new Location(), new AngularVelocity(Math.PI, 0, 0),
//					Posture.upright);
//			players.addPlayer(playerState);
//			brentMcClanahan = pl;
//
//			pl = new Player(PlayerPosition.QB);
//			pl.setFirstName("Fran");
//			pl.setLastName("Tarkenton");
//			pl.setWeight(190);
//			playerState = new PlayerState(pl, null, new Location(), new AngularVelocity(Math.PI, 0, 0),
//					Posture.upright);
//			players.addPlayer(playerState);
//			franTarkenton = pl;
//
//			pl = new Player(PlayerPosition.FL);
//			pl.setFirstName("Ahmad");
//			pl.setLastName("Rashad");
//			pl.setWeight(205);
//			playerState = new PlayerState(pl, null, new Location(), new AngularVelocity(Math.PI, 0, 0),
//					Posture.upright);
//			players.addPlayer(playerState);
//			ahmadRashad = pl;
//
//			pl = new Player(PlayerPosition.WR);
//			pl.setFirstName("Sammy");
//			pl.setLastName("White");
//			pl.setWeight(190);
//			playerState = new PlayerState(pl, null, new Location(), new AngularVelocity(Math.PI, 0, 0),
//					Posture.upright);
//			players.addPlayer(playerState);
//			sammyWhite = pl;
//
//			pl = new Player(PlayerPosition.TE);
//			pl.setFirstName("Stu");
//			pl.setLastName("Voigt");
//			pl.setWeight(223);
//			playerState = new PlayerState(pl, null, new Location(), new AngularVelocity(Math.PI, 0, 0),
//					Posture.upright);
//			players.addPlayer(playerState);
//			stuVoigt = pl;
//
//			pl = new Player(PlayerPosition.RG);
//			pl.setFirstName("Ed");
//			pl.setLastName("White");
//			pl.setWeight(269);
//			playerState = new PlayerState(pl, null, new Location(), new AngularVelocity(Math.PI, 0, 0),
//					Posture.upright);
//			players.addPlayer(playerState);
//			edWhite = pl;
//
//			pl = new Player(PlayerPosition.C);
//			pl.setFirstName("Mick");
//			pl.setLastName("Tinglehoff");
//			pl.setWeight(237);
//			playerState = new PlayerState(pl, null, new Location(), new AngularVelocity(Math.PI, 0, 0),
//					Posture.upright);
//			players.addPlayer(playerState);
//			mickTinglehoff = pl;
//
//			pl = new Player(PlayerPosition.RT);
//			pl.setFirstName("Ron");
//			pl.setLastName("Yary");
//			pl.setWeight(255);
//			playerState = new PlayerState(pl, null, new Location(), new AngularVelocity(Math.PI, 0, 0),
//					Posture.upright);
//			players.addPlayer(playerState);
//			ronYary = pl;
//
//			pl = new Player(PlayerPosition.LT);
//			pl.setFirstName("Steve");
//			pl.setLastName("Riley");
//			pl.setWeight(258);
//			playerState = new PlayerState(pl, null, new Location(), new AngularVelocity(Math.PI, 0, 0),
//					Posture.upright);
//			players.addPlayer(playerState);
//			steveRiley = pl;
//
//			pl = new Player(PlayerPosition.LG);
//			pl.setFirstName("Charles");
//			pl.setLastName("Goodrum");
//			pl.setWeight(256);
//			playerState = new PlayerState(pl, null, new Location(), new AngularVelocity(Math.PI, 0, 0),
//					Posture.upright);
//			players.addPlayer(playerState);
//			charlesGoodrum = pl;
		}

		public OffensiveFormations getOffensiveFormations()
		{
			return this.offensiveFormations;
		}

		public void lineup(Formation formation, Location ballSpot)
		{
			getPlayers().setPath(chuckForeman, new Path(formation.getPosition("RB").place(ballSpot),
					chuckForeman.getSpeedMatrix().getRunningSpeed(), DestinationAction.normalStop));

//			getPlayers().setPath(brentMcClanahan, new Path(formation.getPosition("FB").place(ballSpot),
//					brentMcClanahan.getSpeedMatrix().getRunningSpeed(), DestinationAction.normalStop));
//
//			getPlayers().setPath(franTarkenton, new Path(formation.getPosition("QB").place(ballSpot),
//					franTarkenton.getSpeedMatrix().getRunningSpeed(), DestinationAction.normalStop));
//
//			getPlayers().setPath(ahmadRashad, new Path(formation.getPosition("Z").place(ballSpot),
//					ahmadRashad.getSpeedMatrix().getRunningSpeed(), DestinationAction.normalStop));
//
//			getPlayers().setPath(sammyWhite, new Path(formation.getPosition("X").place(ballSpot),
//					sammyWhite.getSpeedMatrix().getRunningSpeed(), DestinationAction.normalStop));
//
//			getPlayers().setPath(stuVoigt, new Path(formation.getPosition("TE").place(ballSpot),
//					stuVoigt.getSpeedMatrix().getRunningSpeed(), DestinationAction.normalStop));
//
//			getPlayers().setPath(edWhite, new Path(formation.getPosition("RG").place(ballSpot),
//					edWhite.getSpeedMatrix().getRunningSpeed(), DestinationAction.normalStop));
//
//			getPlayers().setPath(mickTinglehoff, new Path(formation.getPosition("C").place(ballSpot),
//					mickTinglehoff.getSpeedMatrix().getRunningSpeed(), DestinationAction.normalStop));
//
//			getPlayers().setPath(ronYary, new Path(formation.getPosition("RT").place(ballSpot),
//					ronYary.getSpeedMatrix().getRunningSpeed(), DestinationAction.normalStop));
//
//			getPlayers().setPath(steveRiley, new Path(formation.getPosition("LT").place(ballSpot),
//					steveRiley.getSpeedMatrix().getRunningSpeed(), DestinationAction.normalStop));
//
//			getPlayers().setPath(charlesGoodrum, new Path(formation.getPosition("LG").place(ballSpot),
//					charlesGoodrum.getSpeedMatrix().getRunningSpeed(), DestinationAction.normalStop));
		}
	}
}
