package jef.actions.pathfinding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jef.core.Field;
import jef.core.Performance;
import jef.core.Player;
import jef.core.PlayerPosition;
import jef.core.PlayerState;
import jef.core.movement.player.Path;
import jef.core.movement.player.Waypoint;
import jef.core.movement.player.Waypoint.DestinationAction;

class PlayerStepsTest
{
	PlayerState state;
	PlayerSteps steps;
	
	@BeforeEach
	void beforeTest()
	{
		state = new PlayerState(new Player("Chuck", "Foreman", PlayerPosition.RB));
		state = state
				.newFrom(null, Field.MIDFIELD.add(-50, 0, 0), null,
						new Path(new Waypoint(Field.MIDFIELD.add(50, 0, 0),
								state.getPlayer().getSpeedMatrix().getSprintingSpeed(), DestinationAction.noStop)),
						null);

		steps = new PlayerSteps(state, 50, Performance.frameInterval, 0);
	}
	
	@Test
	void testGetCapacity()
	{
		assertEquals(50, steps.getCapacity());
	}
	
	@Test	
	void testGetState()
	{
		PlayerState testState = steps.getState(0);
		System.out.println(testState);
		assertEquals(state, testState);
		
		PlayerState testState1 = steps.getState(1);
		System.out.println(testState1);
		assertNotEquals(state, testState1);

		for (int i = 2; i < steps.getCapacity(); i++)
		{
			PlayerState testStateTmp = steps.getState(i);
			System.out.println(testStateTmp);
			assertTrue(testState.getLoc().getX() < testStateTmp.getLoc().getX());
		}
	}

	@Test
	void testDestinationReached()
	{
		for (int i = 0; i < 185; i++)
		{
			steps.advance();
//			System.out.println(i + " - " + steps.getDestinationReachedSteps() + " - " + steps.getLast().getLoc());
			assertTrue(!steps.hasReachedDestination());
		}
		
		steps.advance();
		assertTrue(steps.hasReachedDestination());
	}
	
	@Test
	void testAdvance()
	{
		PlayerState testState = steps.getState(0);
		System.out.println(testState);
		assertEquals(state, testState);
		
		steps.advance();

		PlayerState testState1 = steps.getState(0);
		System.out.println(testState1);
		assertNotEquals(state, testState1);

		steps.advance();
		steps.advance();
		steps.advance();
		steps.advance();
		steps.advance();

		for (int i = 1; i < steps.getCapacity(); i ++)
		{
			PlayerState testStateTmp = steps.getState(i);
			System.out.println(testStateTmp);
			assertTrue(testState.getLoc().getX() < testStateTmp.getLoc().getX());
		}
	}

}
