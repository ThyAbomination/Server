package server.content.travel;

import server.Config;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.players.Client;
import server.util.Misc;

public class Teleport {

	public static boolean teleport(final Client c, int buttonId) {
		int x = 0;
		int y = 0;
		int laws = 0;
		int airs = 0;
		int fires = 0;
		int earths = 0;
		int levelId = 0;
		int waters = 0;
		int xp = 0;
		switch (buttonId) {
		case 4140:
			x = Config.VARROCK_X;
			y = Config.VARROCK_Y;
			// c.getPA().startTeleport(Config.VARROCK_X, Config.VARROCK_Y, 0,
			// "modern");
			levelId = 0;
			laws = 0;
			airs = 0;
			fires = 0;
			xp = 0;
			break;

		case 4143:
			x = Config.LUMBY_X;
			y = Config.LUMBY_Y;
			// c.getPA().startTeleport(Config.LUMBY_X, Config.LUMBY_Y, 0,
			// "modern");
			levelId = 0;
			laws = 0;
			airs = 0;
			earths = 0;
			xp = 0;
			break;

		case 6004:
			x = Config.ARDOUGNE_X;
			y = Config.ARDOUGNE_Y;
			levelId = 0;
			waters = 0;
			laws = 0;
			xp = 0;
			break;

		case 6005:
			x = Config.WATCHTOWER_X;
			y = Config.WATCHTOWER_Y;
			levelId = 0;
			earths = 0;
			laws = 0;
			xp = 0;
			break;

		case 4146:
			x = Config.FALADOR_X;
			y = Config.FALADOR_Y;
			levelId = 0;
			laws = 0;
			airs = 0;
			waters = 0;
			xp = 0;
			break;

		case 4150:
			x = Config.CAMELOT_X;
			y = Config.CAMELOT_Y;
			levelId = 0;
			laws = 0;
			airs = 0;
			xp = 0;
			break;

		case 29031:
			x = Config.TROLLHEIM_X;
			y = Config.TROLLHEIM_Y;
			levelId = 0;
			laws = 0;
			airs = 0;
			xp = 0;
			break;
		//
		// case 50253:
		// case 4146:
		// c.getPA().startTeleport(Config.FALADOR_X, Config.FALADOR_Y, 0,
		// "modern");
		// break;
		}
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
		if (c.checkBusy()) {
			return true;
		}
		c.setBusy(true);
		c.setCanWalk(false);
		final int teleX = x;
		final int teleY = y;
		final int levelID = levelId;
		final int LAWS = laws;
		final int AIRS = airs;
		final int FIRES = fires;
		final int XP = xp;
		final int EARTHS = earths;
		final int WATERS = waters;
		// c.sendMessage("You break the teleport tablet.");
		CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {

			@Override
			public void execute(CycleEventContainer container) {
				// TODO Auto-generated method stub
				if (c.playerLevel[c.playerMagic] >= levelID
						&& c.getItems().playerHasItem(563, LAWS)
						&& c.getItems().playerHasItem(554, FIRES)
						&& c.getItems().playerHasItem(556, AIRS)
						&& c.getItems().playerHasItem(557, EARTHS)
						&& c.getItems().playerHasItem(555, WATERS)) {
					c.getItems().deleteItem2(563, LAWS);
					c.getItems().deleteItem2(554, FIRES);
					c.getItems().deleteItem2(556, AIRS);
					c.getItems().deleteItem2(557, EARTHS);
					c.getItems().deleteItem2(555, WATERS);
					c.getPA().addSkillXP(XP, c.playerMagic);
					c.getPA().startTeleport(teleX, teleY, 0, "modern");
				} else {
					c.sendMessage("You need a magic level of " + levelID + ".");
				}
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

	public static int[][] DUEL_RING = { { 2552, 2554 }, { 2554, 2556 },
			{ 2556, 2558 }, { 2558, 2560 }, { 2560, 2562 }, { 2562, 2564 },
			{ 2564, 2566 }, { 2566, -1 }, };

	public static void handleDuelRing(final Client c, int itemId) {
		for (int ring[] : DUEL_RING) {
			final int init = ring[0];
			final int newId = ring[1];
			if (itemId == init) {
				if (c.checkBusy()) {
					c.sendMessage("You are already doing this.");
					return;
				}
				c.setBusy(true);
				c.resetWalkingQueue();
				c.sendMessage("You rub the ring.");
				c.getItems().deleteItem(init, 1);
				c.dialogueAction = 107;
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {

					@Override
					public void execute(CycleEventContainer container) {
						// TODO Auto-generated method stub
						c.getDH().sendOption3("Duel Arena",
								"Barbarian Village", "Tzhaar");
						c.setBusy(false);
						c.getItems().addItem(newId, 1);
						container.stop();
					}

					@Override
					public void stop() {
						// TODO Auto-generated method stub

					}

				}, 2);
			}
		}
	}

	public static int[][] GLORY_DATA = { { 1712, 1710 }, { 1710, 1708 },
			{ 1708, 1706 }, { 1706, 1704 }, };

	public static void handleGlory(final Client c, int itemId) {
		for (int glory[] : GLORY_DATA) {
			final int init = glory[0];
			final int newId = glory[1];
			if (itemId == init) {
				if (c.checkBusy()) {
					c.sendMessage("You are already doing this.");
					return;
				}
				c.setBusy(true);
				c.resetWalkingQueue();
				c.sendMessage("You rub the ring.");
				c.getItems().deleteItem(init, 1);
				c.dialogueAction = 120;
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {

					@Override
					public void execute(CycleEventContainer container) {
						// TODO Auto-generated method stub
						c.getDH().sendOption4("Edgeville", "Al Kharid",
								"Draynor", "Port Sarim");
						c.setBusy(false);
						c.getItems().addItem(newId, 1);
						container.stop();
					}

					@Override
					public void stop() {
						// TODO Auto-generated method stub

					}

				}, 2);
			}
		}
	}

	public static boolean home(final Client c, int actionButtonID) {
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
		if (c.checkBusy()) {
			return true;
		}
		c.setBusy(true);
		c.setCanWalk(false);
		int x = -1;
		int y = -1;
		// c.startAnimation(2241);
		switch (actionButtonID) {
		case 4171:
		case 50056:
		case 117048:
			x = 3432 + (Misc.random(3));
			y = 3572 + (Misc.random(3));
			break;
		}
		final int teleX = x;
		final int teleY = y;
		CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {

			@Override
			public void execute(CycleEventContainer container) {
				c.getPA().resetAnimation();
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
