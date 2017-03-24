package server.model.players.skills;

import server.Config;
import server.Server;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.objects.Object;
import server.model.players.Client;
import server.model.players.Player;
import server.model.players.PlayerHandler;
import server.util.Misc;

public class WoodcuttingManager {

	private static final int AXE_DELAY = 1;

	public static int[][] TREES = { { 1278, 1, 25, 1511 },
			{ 1286, 1, 25, 1511 }, { 1282, 1, 25, 1511 },
			{ 1276, 1, 25, 1511 }, { 1277, 1, 25, 1511 },
			{ 1278, 1, 25, 1511 }, { 1279, 1, 25, 1511 },
			{ 1280, 1, 25, 1511 }, { 1330, 1, 25, 1511 },
			{ 1332, 1, 25, 1511 }, { 3033, 1, 25, 1511 },
			{ 3034, 1, 25, 1511 }, { 3035, 1, 25, 1511 },
			{ 3036, 1, 25, 1511 }, { 3879, 1, 25, 1511 },
			{ 3881, 1, 25, 1511 }, { 3882, 1, 25, 1511 },
			{ 3883, 1, 25, 1511 }, { 1308, 30, 67, 1519 },
			{ 5551, 30, 67, 1519 }, { 5552, 30, 67, 1519 },
			{ 5553, 30, 67, 1519 }, { 1309, 60, 175, 1515 },
			{ 1307, 45, 100, 1517 }, { 1281, 15, 37, 1521 },
			{ 1306, 75, 250, 1513 }, };

	public static int[][] AXES = { { 1351, 1, 879, 1 }, { 1353, 6, 875, 1 },
			{ 1355, 21, 871, 1 }, { 1357, 31, 869, 1 }, { 1359, 41, 867, 2 },
			{ 1361, 6, 870, 1 }, { 1349, 1, 877, 1 },

	};

	public static int[][] FIX_AXE = { { 492, 508, 1351 }, { 492, 510, 1349 },
			{ 492, 512, 1353 }, { 492, 514, 1361 }, { 492, 516, 1355 },
			{ 492, 518, 1357 }, { 492, 520, 1359 }, };

	public static int[][] CANOES = {};

	/**
	 * Adds the tree stump which re-spawns randomly.
	 * 
	 * @param client
	 */
	public static void addEmptyStump(Client client) {
		for (int[] element : TREES) {
			int tree = element[0];
			// int emptyTree = element[3];
			if (tree == client.objectId) {
				for (int j = 0; j < PlayerHandler.players.length; j++) {
					if (PlayerHandler.players[j] != null) {
						new Object(1341, client.objectX, client.objectY, 0, 0,
								10, tree, 20 + Misc.random(40), false);
						client.isWoodcutting = false;
						// appendAnimation(client);
					}
				}
			}
		}
	}

	/**
	 * Checks for player has axe in inventory or weapon space.
	 * 
	 * @param c
	 * @param type
	 * @return
	 */
	public static int hasAxe(Client c, int type) {
		for (int[] axe : AXES) {
			int id = axe[0];
			int level = axe[1];
			if (c.getItems().playerHasItem(id)
					&& c.playerLevel[c.playerWoodcutting] >= level
					|| c.playerEquipment[Player.playerWeapon] == id
					&& c.playerLevel[c.playerWoodcutting] >= level) {
				return axe[type];
			}
		}
		return -1;
	}
	
	public void birdNests(Client c) {
		if (Misc.random(100) < 5) {
			c.getItems().addItem(5070, 1);
		}
	}

	public static void cutTree(final Client c, int objectId) {
		for (int tree[] : TREES) {
			final int treeId = tree[0];
			final int treeLevel = tree[1];
			final int treeXP = tree[2];
			final int treeLogs = tree[3];
			for (int axe[] : AXES) {
				final int axeId = axe[0];
				final int axeAnimation = axe[2];
				final int axePerformance = axe[3];
				if (objectId == treeId && hasAxe(c, 0) == axeId
						&& c.playerLevel[c.playerWoodcutting] >= treeLevel) {
					if (c.getItems().freeBankSlots() == 0) {
						c.getDH().sendStatement(
								"Not enough space in your inventory.");
						return;
					}

					if (c.checkBusy()) {
						return;
					}
					c.setBusy(true);
					c.setCanWalk(false);
					c.isWoodcutting = true;
					c.turnPlayerTo(c.objectX, c.objectY);
					c.sendMessage("You swing your axe at the tree.");

					if (c.isWoodcutting == true) {
						CycleEventHandler.getSingleton().addEvent(c,
								new CycleEvent() {

									@Override
									public void execute(
											CycleEventContainer container) {
										// TODO Auto-generated method stub
										if (c.isWoodcutting == false) {
											c.setBusy(false);
											c.setCanWalk(true);
											container.stop();
											return;
										} else if (c.isWoodcutting == true) {
											c.startAnimation(axeAnimation);
										}
									}

									@Override
									public void stop() {
										// TODO Auto-generated method stub

									}

								}, AXE_DELAY);
					}
					CycleEventHandler.getSingleton().addEvent(c,
							new CycleEvent() {
								public int cycle = c.getItems().freeSlots();
								public int stumpCycle = 15 + Misc.random(5);

								@Override
								public void execute(
										CycleEventContainer container) {
									// TODO Auto-generated method stub
									if (c.getItems().freeSlots() == 0) {
										c.setBusy(false);
										c.setCanWalk(true);
										c.isWoodcutting = false;
										c.getDH()
												.sendStatement(
														"Not enough space in your inventory.");
										container.stop();
										return;
									}
									if (c.isWoodcutting == false) {
										c.setBusy(false);
										c.setCanWalk(true);
										// c.sendMessage("You have stopped cutting down the tree.");
										container.stop();
										return;
									}
									if (hasAxe(c, 0) != axeId) {
										c.setBusy(false);
										c.setCanWalk(true);
										c.isWoodcutting = false;
										c.getDH()
												.sendStatement(
														"You need an axe to cut this tree.");
										container.stop();
										return;
									}

									if (cycle != 0) {
										cycle--;
										stumpCycle--;
										c.getItems().addItem(treeLogs, 1);
										BirdsNests.dropNest(c);
										c.getPA()
												.addSkillXP(
														treeXP
																* Config.WOODCUTTING_EXPERIENCE,
														c.playerWoodcutting);
										c.sendMessage("You get some logs.");
										c.getPA().refreshSkill(
												c.playerWoodcutting);
										// c.startAnimation(axeAnimation);
										for (int fix[] : FIX_AXE) {
											int brokenAxe = fix[0];
											int brokenHead = fix[1];
											int hatchet = fix[2];
											int chance = Misc.random(200);
											if (chance == 1
													&& c.getItems()
															.playerHasItem(
																	hatchet)) {

												c.getItems().deleteItem(
														hatchet, 1);
												Server.itemHandler
														.createGroundItem(c,
																brokenHead,
																c.getX(),
																c.getY(), 1,
																c.getId());
												c.getItems().addItem(brokenAxe,
														1);
												c.sendMessage("Your axe's head has broken and dropped to the ground.");
												c.setBusy(false);
												c.setCanWalk(true);
												c.isWoodcutting = false;
												c.getDH()
														.sendStatement(
																"You have broken your axe.");
												container.stop();
											}
										}
									} else if (cycle == 0) {
										c.setBusy(false);
										c.setCanWalk(true);
										c.isWoodcutting = false;
										// c
										// .sendMessage("You need an axe to cut this tree.");
										container.stop();
									}
									if (stumpCycle == Misc.random(10) + 3) {
										addEmptyStump(c);
										c.setBusy(false);
										c.setCanWalk(true);
										c.isWoodcutting = false;
										c.getDH()
												.sendStatement(
														"The tree has run out of logs.");
										container.stop();
									}
								}

								@Override
								public void stop() {
									// TODO Auto-generated method stub

								}

							}, ((10) / axePerformance - Misc.random(2 + 3)));
				}

			}
		}
	}

	public static void fixAxe(final Client c) {
		for (int fix[] : FIX_AXE) {
			int axeHandle = fix[0];
			int axeHead = fix[1];
			final int fixedAxe = fix[2];
			if (c.getItems().playerHasItem(axeHandle)
					&& c.getItems().playerHasItem(axeHead)) {
				if (c.checkBusy()) {
					return;
				}
				c.setBusy(true);
				c.getItems().deleteItem(axeHandle, 1);
				c.getItems().deleteItem(axeHead, 1);
				c.getPA().removeAllWindows();
				c.sendMessage("Your axe handle and axe head have been taken.");
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {

					@Override
					public void execute(CycleEventContainer container) {
						// TODO Auto-generated method stub
						c.getItems().addItem(fixedAxe, 1);
						c.sendMessage("Your axe has been fixed.");
						c.setBusy(false);
						container.stop();
					}

					@Override
					public void stop() {
						// TODO Auto-generated method stub

					}

				}, 1);
			}
		}
	}

	public static void addFallenTree(Client client, int canoe) {

		// int emptyTree = element[3];
		if (canoe == client.objectId) {
			for (int j = 0; j < PlayerHandler.players.length; j++) {
				if (PlayerHandler.players[j] != null) {
					new Object(1296, client.objectX, client.objectY, 0, 0, 10,
							canoe, 20 + Misc.random(40), false);
					// client.isWoodcutting = false;
					// appendAnimation(client);
				}
			}
		}

	}

	public static void handleCanoe(final Client c, final int objectId) {
		for (int axes[] : AXES) {
			int type = axes[0];
			int level = axes[1];
			int anim = axes[2];
			if (c.playerLevel[c.playerWoodcutting] >= level
					&& c.getItems().playerHasItem(type)
					|| c.playerLevel[c.playerWoodcutting] >= level
					&& c.playerEquipment[Player.playerWeapon] == type) {
				if (c.checkBusy()) {
					return;
				}
				c.setBusy(true);
				c.turnPlayerTo(c.objectX, c.objectY);
				c.startAnimation(anim);
				c.sendMessage("You swing your axe at the station.");
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {

					@Override
					public void execute(CycleEventContainer container) {
						// TODO Auto-generated method stub
						addFallenTree(c, objectId);
						CycleEventHandler.getSingleton().addEvent(c,
								new CycleEvent() {

									@Override
									public void execute(
											CycleEventContainer container) {
										// TODO Auto-generated method stub
										c.dialogueAction = 122;
										c.getDH()
												.sendOption3(
														"Travel using Log Canoe to enter Barbarian Village.",
														"Travel using Waka Canoe to enter King Black Dragon Wilderness.",
														"Do Nothing.");
										container.stop();
										c.setBusy(false);
									}

									@Override
									public void stop() {
										// TODO Auto-generated method stub

									}

								}, 1);
						c.setBusy(false);
						c.sendMessage("You cut down the canoe. Please wait...");
						container.stop();
					}

					@Override
					public void stop() {
						// TODO Auto-generated method stub

					}

				}, 4);

			}
		}
	}

}
