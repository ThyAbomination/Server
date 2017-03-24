package server.model.players.skills.crafting;

import server.Config;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.players.Client;
import server.util.Misc;

public class GemCuttingManager {

	public static int[][] GEMS = { { 1625, 1609, 1, 891, 15 },
			{ 1627, 1611, 13, 892, 20 }, { 1629, 1613, 16, 892, 25 },
			{ 1623, 1607, 20, 888, 50 }, { 1621, 1605, 27, 889, 67 },
			{ 1619, 1603, 34, 887, 85 }, { 1617, 1601, 43, 890, 107 },
			{ 1631, 1615, 55, 890, 137 }, { 6571, 6573, 67, 2717, 167 }, };

	public static int CHISEL = 1755;

	public static int DELAY = 1;

	public static int hasGem(Client c, int type) {
		for (int[] gems : GEMS) {
			int uncutGem = gems[0];
			int level = gems[2];
			if (c.getItems().playerHasItem(uncutGem)
					&& c.playerLevel[c.playerCrafting] >= level
					&& c.getItems().playerHasItem(CHISEL)) {
				return gems[type];
			}
		}
		return -1;
	}

	public static void startCutting(final Client c, final int itemHas,
			final int itemUsed) {
		for (int gems[] : GEMS) {
			final int uncut = gems[0];
			int level = gems[2];
			final int cut = gems[1];
			final int animation = gems[3];
			final int xp = gems[4];
			if (itemHas == CHISEL && itemUsed == uncut || itemHas == uncut
					&& itemUsed == CHISEL) {
				if (itemHas == 1755 && c.playerLevel[c.playerCrafting] < level
						|| itemUsed == CHISEL
						&& c.playerLevel[c.playerCrafting] < level) {
					c.sendMessage("You need a crafting level of " + level
							+ " to cut this gem.");
					return;
				}
				if (c.duelStatus == 5) {
					c.sendMessage("You can't cut gems during a duel!");
					return;
				}
				if (c.checkBusy()) {
					return;
				}
				c.setBusy(true);
				c.setCanWalk(false);
				c.sendMessage("You begin to cut the gem.");
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
					public int timer = c.playerItems.length;

					@Override
					public void execute(CycleEventContainer container) {
						// TODO Auto-generated method stub
						if (c.canWalk()) {
							c.sendMessage("You have stopped cutting the gem.");
							c.setBusy(false);
							container.stop();
							return;
						}
						if (hasGem(c, 0) != uncut) {
							c.setBusy(false);
							// c.setCanWalk(true);
							c.sendMessage("You have run out of gems to cut.");
							container.stop();
							return;
						} else if (c.getItems().freeSlots() == 0) {
							c.setBusy(false);
							// c.setCanWalk(true);
							container.stop();
							// return;
						}
						if (timer != 0) {
							timer--;
							switch (uncut) {
							case 1629:
							case 1625:
							case 1627:
								int r = Misc.random(10);
								if (r == 2) {
									c.getItems().deleteItem2(uncut, 1);
									c.getItems().deleteItem2(cut, 1);
									c.sendMessage("You crushed the gem.");
								}
								break;
							}
							c.getItems().deleteItem(uncut, 1);
							c.startAnimation(animation);
							c.getItems().addItem(cut, 1);
							c.getPA().addSkillXP(
									xp * Config.CRAFTING_EXPERIENCE, 12);
							c.sendMessage("You cut the gem.");
							c.getPA().refreshSkill(12);

						} else if (timer == 0) {
							c.getPA().refreshSkill(12);
							c.setBusy(false);
							// c.setCanWalk(true);
							container.stop();
						}
					}

					@Override
					public void stop() {
						// TODO Auto-generated method stub

					}

				}, DELAY);
			}
		}
	}

}
