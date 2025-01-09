package jef.core.movement.index;

import org.junit.jupiter.api.Test;

import jef.core.TestPlayer;
import jef.core.movement.DefaultLinearVelocity;

class DefaultLocationIndexTest
{
	@Test
	public void testUpdate()
	{
		DefaultLocationIndex index = new DefaultLocationIndex(.05, 20);
		
		TestPlayer player = new TestPlayer("Fran", "Tarkenton");
		player.setLV(new DefaultLinearVelocity(10, 10, 0));
		index.update(player);
	}
}
