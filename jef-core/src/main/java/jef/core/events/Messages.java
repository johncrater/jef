package jef.core.events;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

//@formatter:off
public class Messages
{
	private static final int userPrefix 					= 0x00010000;
	private static final int gamePrefix 					= 0x00020000;
	private static final int ballPrefix 					= 0x00030000;
	private static final int playerPrefix 					= 0x00040000;
	private static final int scoreboardPrefix 				= 0x00050000;
	private static final int debugPrefix 					= 0xFFFF0000;

	// game messages
	public static final int gameStarted 					= 1 | gamePrefix;
	public static final int offensiveTeamLinedUp 			= 2 | gamePrefix;
	public static final int defensiveTeamLinedUp 			= 3 | gamePrefix;
	public static final int playCompleted					= 4 | gamePrefix;
	public static final int intervalBegins					= 5 | gamePrefix;
	public static final int intervalEnds					= 6 | gamePrefix;
	public static final int ballSwitchedSides				= 7 | gamePrefix;
	public static final int teamsSwitchEndsOfTheField		= 8 | gamePrefix;
	
	// user messages
	public static final int offensivePlaySelected 			= 1 | userPrefix;
	public static final int defensivePlaySelected 			= 2 | userPrefix;
	public static final int playStarted 					= 3 | userPrefix;
	public static final int fieldLocationSelected			= 4 | userPrefix;
	public static final int displayText						= 5 | userPrefix;
	public static final int pause							= 6 | userPrefix;
	public static final int unpause							= 7 | userPrefix;
	public static final int step							= 8 | userPrefix;
	public static final int drawFieldLevelItem				= 9 | userPrefix;
	public static final int drawPlayerLevelItem				= 10 | userPrefix;
	public static final int drawOverlayLevelItem			= 11 | userPrefix;
	public static final int fieldAngleChanged				= 12 | userPrefix;
	public static final int fieldScaleChanged				= 13 | userPrefix;
	public static final int fieldOffsetChanged				= 14 | userPrefix;
	public static final int playerSelected					= 15 | userPrefix;
	public static final int toggleGrid						= 16 | userPrefix;
	
	// ball messages
	public static final int ballHitsGround 					= 1 | ballPrefix;
	public static final int ballKicked 						= 2 | ballPrefix;
	public static final int ballLocationChanged				= 3 | ballPrefix;
	public static final int ballSnapped 					= 4 | ballPrefix;
	public static final int ballThrown 						= 5 | ballPrefix;
	public static final int ballCaught 						= 6 | ballPrefix;
	public static final int ballLateralled 					= 7 | ballPrefix;
	public static final int ballDropped 					= 8 | ballPrefix;
	public static final int ballHandedOff 					= 9 | ballPrefix;
	public static final int ballOutOfPlay 					= 10 | ballPrefix;
	public static final int ballTeed 						= 11 | ballPrefix;
	public static final int ballPickedUp 					= 12 | ballPrefix;
	public static final int ballChangedPossession			= 13 | ballPrefix;
	public static final int ballThroughUprights				= 14 | ballPrefix;
	public static final int ballHitsPlayer 					= 15 | ballPrefix;

	// player messages
	public static final int playerLinearVelocityChanged 	= 1 | playerPrefix;
	public static final int runnerEntersEndZone			 	= 2 | playerPrefix;
	public static final int runnerSignalsFairCatch			= 3 | playerPrefix;
	public static final int runnerOutOfBounds				= 4 | playerPrefix;
	public static final int runnerTackledBy					= 5 | playerPrefix;
	public static final int runnerSignalsTouchback			= 6 | playerPrefix;
	public static final int playerPostureChanged			= 7 | playerPrefix;
	public static final int playerOrientationChanged		= 8 | playerPrefix;
	public static final int playerLocationChanged			= 9 | playerPrefix;
	public static final int pathFinder						= 10 | playerPrefix;
	public static final int playerMover						= 11 | playerPrefix;
	public static final int collision						= 12 | playerPrefix;

	// score board messages
	public static final int scoreboardChanged 				= 1 | scoreboardPrefix;
	public static final int twoMinuteWarning 				= 2 | scoreboardPrefix;
	public static final int quarterChanged	 				= 3 | scoreboardPrefix;
	public static final int homeTeamTimeoutChanged			= 4 | scoreboardPrefix;
	public static final int visitingTeamTimeoutChanged		= 5 | scoreboardPrefix;
	public static final int downChanged		 				= 6 | scoreboardPrefix;
	public static final int lineOfScrimmageChanged			= 7 | scoreboardPrefix;
	public static final int timeChanged		 				= 8 | scoreboardPrefix;
	
	// debug messages
	public static final int drawIntercepterPath							= 1 	| debugPrefix;
	public static final int drawIntercepterDestination					= 3 	| debugPrefix;
	public static final int drawRunnerPath								= 4 	| debugPrefix;
	public static final int drawRunnerDestination						= 5 	| debugPrefix;
	public static final int drawBlockerDestination						= 11	| debugPrefix;
	public static final int drawBlockerPath								= 12	| debugPrefix;

	public static final int drawRunnerIntercepterBoundingSegments		= 6 	| debugPrefix;
	public static final int drawBlockerIntercepterBoundingSegments		= 7 	| debugPrefix;
	
	public static final int drawDebugShape								= 100 	| debugPrefix;

	public static <T> void dispatchEvent(List<T> listeners, Consumer<T> event)
	{
		List<T> copy = new ArrayList<T>(listeners);
		copy.forEach(event);
	}
	
	public static <T, U> void dispatchEvent(List<T> listeners, BiConsumer<T, U> event, U arg)
	{
		List<T> copy = new ArrayList<T>(listeners);
		copy.forEach(t -> event.accept(t, arg));
	}
	
}
