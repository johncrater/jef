package jef.core.movement.index;

import org.junit.jupiter.api.Test;

import jef.core.DefaultPlayer;
import jef.core.movement.DefaultLinearVelocity;
import jef.core.movement.player.DefaultSteerable;
import jef.core.movement.player.Steerable;

class DefaultLocationIndexTest
{
	@Test
	public void testUpdate()
	{
		DefaultLocationIndex index = new DefaultLocationIndex(.05, 20);
		
		DefaultPlayer player = new DefaultPlayer();
		player.setFirstName("Fran");
		player.setLastName("Tarkenton");
		
		Steerable steerable = new DefaultSteerable(player);
		steerable.setLV(new DefaultLinearVelocity((double) 10, (double) 10, (double) 0));
		index.update(steerable);
	}
}
