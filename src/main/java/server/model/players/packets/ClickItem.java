package server.model.players.packets;

import server.content.BirdsNest;
import server.content.travel.TeleTabs;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.minigames.TreasureTrails;
import server.model.players.Client;
import server.model.players.PacketType;
import server.model.players.PlayerHandler;
import server.model.players.skills.BirdsNests;
import server.model.players.skills.Herblore;
import server.util.Misc;

/**
 * Clicking an item, bury bone, eat food etc
 **/
public class ClickItem implements PacketType {

	@Override
	public void processPacket(final Client c, int packetType, int packetSize) {
		final BirdsNests nest = new BirdsNests();
		int frame = c.getInStream().readSignedShortLittleEndianA(); // use to be
																	// readSignedWordBigEndianA();
		int itemSlot = c.getInStream().readSignedWordA(); // use to be
															// readUnsignedWordA();
		int itemId = c.getInStream().readSignedWordBigEndian(); // us to be
																// unsigned.
		int cashAmount = c.getItems().getItemAmount(995);
		int CSlot = c.getItems().getItemSlot(995);
		if (itemId != c.playerItems[itemSlot] - 1) {
			return;
		}
		if (itemId > 199 || itemId < 219 || itemId == 12147 || itemId == 14836 || itemId == 2485 || itemId == 3051 || itemId == 3049) {
		Herblore.checkHerb(c, itemId);
		}

		if (itemId > 15085 && itemId < 15102) {
			c.useDice(itemId, false);
		}
		if (itemId == 15084) {// dice bag
			c.diceID = itemId;
			c.getDH().sendDialogues(106, 0);
		}
		
		if (BirdsNest.isNest(itemId)) {
			BirdsNest.searchNest(c, itemId);
		}
		
		if (itemId == 607){
			c.sendMessage("You can imbue Berserker, Archer, Seers & Warrior Rings with this scroll.");
			c.sendMessage("Just use it with any of the rings.");
		}

		if (itemId == 7956) {
			int[] reward = { 500000, 1000000, 2000000, 3000000, 5000000 };
			int won = reward[Misc.random(reward.length - 1)];
			c.getItems().deleteItem(7956, itemSlot, 1);
			c.getItems().addItem(995, won);
			c.sendMessage("You open the casket... and receive "
					+ Misc.format(won) + " coins!");
		}

		if (itemId == 6199) {
			int randomBox = c.getPA().randomBox();
			c.getItems().deleteItem(6199, itemSlot, 1);
			c.getItems().addItem(randomBox, 1);
			PlayerHandler.yell("@red@[News]: "
					+ Misc.formatPlayerName(c.playerName) + " has received a "
					+ c.getItems().getItemName(randomBox)
					+ " from a mystery box.");
		}

		if (itemId == 10831) {
			if (cashAmount > 1999999999) {
				c.getItems().deleteItem(995, CSlot, 2000000000);
				c.getItems().deleteItem(10831, 1);
				c.getItems().addItem(10835, 1);
				server.model.players.PlayerSave.saveGame(c);
				c.sendMessage("You fill the bag with 2B GP.");
			} else if (cashAmount < 1999999999) {
				c.sendMessage("You need 2B GP to fill this bag.");
			}
		}

		if (itemId == 10835) {
			c.getItems().deleteItem(10835, 1);
			c.getItems().addItem(995, 2000000000);
			c.getItems().addItem(10831, 1);
			server.model.players.PlayerSave.saveGame(c);
			c.sendMessage("You empty the bag and receive 2B GP.");
		}
		if (itemId == 7237) {
			c.getItems().deleteItem(itemId, 1);
			TreasureTrails.addClueReward(c, 0);

		} else if (itemId == 7287) {
			c.getItems().deleteItem(itemId, 1);
			TreasureTrails.addClueReward(c, 1);

		} else if (itemId == 7257) {
			c.getItems().deleteItem(itemId, 1);
			TreasureTrails.addClueReward(c, 2);
		}
		if (c.getFood().isFood(itemId))
			c.getFood().eat(itemId, itemSlot);
		if (c.getPrayer().IsABone(itemId))
			c.getPrayer().buryBone(itemId);
		// ScriptManager.callFunc("itemClick_"+itemId, c, itemId, itemSlot);
		if (c.getPotions().isPotion(itemId))
			c.getPotions().handlePotion(itemId, itemSlot);
		if (itemId == 952) {
			if (c.inArea(3553, 3301, 3561, 3294)) {
				if (c != null) {
					c.startAnimation(831);
					CycleEventHandler.getSingleton().addEvent(c,
							new CycleEvent() {
								@Override
								public void execute(
										CycleEventContainer container) {
									c.teleTimer = 3;
									c.newLocation = 1;
									c.sendMessage("You've broken into a crypt!");
									c.startAnimation(65535);
									container.stop();
								}

								@Override
								public void stop() {
								}
							}, 1);
				}
			} else if (c.inArea(3550, 3287, 3557, 3278)) {
				if (c != null) {
					c.startAnimation(831);
					CycleEventHandler.getSingleton().addEvent(c,
							new CycleEvent() {
								@Override
								public void execute(
										CycleEventContainer container) {
									c.teleTimer = 3;
									c.newLocation = 2;
									c.sendMessage("You've broken into a crypt!");
									c.startAnimation(65535);
									container.stop();
								}

								@Override
								public void stop() {
								}
							}, 1);
				}
			} else if (c.inArea(3561, 3292, 3568, 3285)) {
				if (c != null) {
					c.startAnimation(831);
					CycleEventHandler.getSingleton().addEvent(c,
							new CycleEvent() {
								@Override
								public void execute(
										CycleEventContainer container) {
									c.teleTimer = 3;
									c.newLocation = 3;
									c.sendMessage("You've broken into a crypt!");
									c.startAnimation(65535);
									container.stop();
								}

								@Override
								public void stop() {
								}
							}, 1);
				}
			} else if (c.inArea(3570, 3302, 3579, 3293)) {
				if (c != null) {
					c.startAnimation(831);
					CycleEventHandler.getSingleton().addEvent(c,
							new CycleEvent() {
								@Override
								public void execute(
										CycleEventContainer container) {
									c.teleTimer = 3;
									c.newLocation = 4;
									c.sendMessage("You've broken into a crypt!");
									c.startAnimation(65535);
									container.stop();
								}

								@Override
								public void stop() {
								}
							}, 1);
				}
			} else if (c.inArea(3571, 3285, 3582, 3278)) {
				if (c != null) {
					c.startAnimation(831);
					CycleEventHandler.getSingleton().addEvent(c,
							new CycleEvent() {
								@Override
								public void execute(
										CycleEventContainer container) {
									c.teleTimer = 3;
									c.newLocation = 5;
									c.sendMessage("You've broken into a crypt!");
									c.startAnimation(65535);
									container.stop();
								}

								@Override
								public void stop() {
								}
							}, 1);
				}
			} else if (c.inArea(3562, 3279, 3569, 3273)) {
				if (c != null) {
					c.startAnimation(831);
					CycleEventHandler.getSingleton().addEvent(c,
							new CycleEvent() {
								@Override
								public void execute(
										CycleEventContainer container) {
									c.teleTimer = 3;
									c.newLocation = 6;
									c.sendMessage("You've broken into a crypt!");
									c.startAnimation(65535);
									container.stop();
								}

								@Override
								public void stop() {
								}
							}, 1);
				}
			}
		}

		switch (itemId) {
		case 8010:// camy
		case 8009:// fally
		case 8008:// lumby
		case 8007:// varrock
		case 8011:
			TeleTabs.teleport(c, itemId);
			break;

		}
	}

}
