package server.content.travel;

import server.Config;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.players.Client;

public class TeleTabs {

	public static boolean teleport(final Client c, final int itemId) {
		int x = 0;
		int y = 0;
		switch (itemId) {
		case 8010:// camy
			x = 2757;
			y = 3477;
			break;
		case 8009:// fally
			x = 2964;
			y = 3378;
			break;
		case 8008:// lumby
			x = 3222;
			y = 3221;
			break;
		case 8007:// varrock
			x = 3210;
			y = 3424;
			break;

		case 8011:// ardy
			x = Config.ARDOUGNE_X;
			y = Config.ARDOUGNE_Y;
			break;
		}
		final int teleX = x;
		final int teleY = y;
		if (c.duelStatus == 5) {
			c.sendMessage("You can't teleport during a duel!");
			return true;
		}
		if (c.inWild() && c.wildLevel > Config.NO_TELEPORT_WILD_LEVEL) {
			c.sendMessage("You can't teleport above level "
					+ Config.NO_TELEPORT_WILD_LEVEL + " in the wilderness.");
			return true;
		}
		if (System.currentTimeMillis() - c.teleBlockDelay < c.teleBlockLength) {
			c.sendMessage("You are teleblocked and can't teleport.");
			return true;
		}

		// the event
		if (c.checkBusy()) {
			return true;
		}
		c.setBusy(true);
		c.setCanWalk(false);
		c.sendMessage("You break the teleport tablet.");
		CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {

			@Override
			public void execute(CycleEventContainer container) {
				// TODO Auto-generated method stub
				c.getItems().deleteItem2(itemId, 1);
				c.getPA().startTeleport(teleX, teleY, 0, "modern");
				c.setBusy(false);
				c.setCanWalk(true);
				container.stop();

			}

			@Override
			public void stop() {
				// TODO Auto-generated method stub

			}

		}, 1);
		return true;

	}

}
