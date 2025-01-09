package jef.core.movement;

public enum Posture
{

	// Player activities effected by Posture include: moving, tackling, blocking,
	// passing, catching (including intercepting), kicking, fumbling

	// normal operation. could be running or just standing. All activities are
	// normal
	upright,

	// falling but won't hit ground necessarily depending on events
	// if a player is stumbling and he exceeds his maximum velocity, he will
	// transition to fallingDown
	// A STUMBLING player can still move, tackle, block, pass, catch at reduced
	// effectiveness. But they may not kick.
	stumbling,

	// as a result of contact player has lost balance. He will hit the ground
	// momentarily. While fallingDown
	// a player can still move, tackle, block, pass, catch at reduced effectiveness.
	// But they may not kick.
	fallingDown,

	// fallen and not moving. Once a player hits the ground, they cannot move,
	// tackle, block, pass, catch, kick, or anything else other than transition to
	// STANDING. However.
	// they may trip an opponent depending on conditions that may cause him to
	// transition to stumbling.
	onTheGround,

	// a player is transitioning from onTheGround to upright. During this transition, the
	// player cannot move, kick, pass. They may block or tackling can reduced
	// effectiveness.
	standingUp,
}