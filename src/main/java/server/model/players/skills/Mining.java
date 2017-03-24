package server.model.players.skills;

import server.Config;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.event.Event;
import server.event.EventContainer;
import server.event.EventManager;
import server.model.items.Item;
import server.model.objects.Object;
import server.model.players.Client;
import server.model.players.Player;
import server.model.players.PlayerHandler;
import server.util.Misc;

/**
 * 
 * @author Dylan <iz3 Legend, Reptile>
 * 
 *         Handles The Mining Skill
 * 
 */

public class Mining {

	public static int[][] PICKAXE_DATA = { { 1265, 1 }, { 1267, 1 },
			{ 1269, 5 }, { 1273, 20 }, { 1271, 30 }, { 1275, 40 } };

	public static int[][] ROCK_DATA = {
			{ 2090, 1, 17, 450, 30, 436, 2119 },
			{ 2091, 1, 17, 451, 30, 436, 2120 },
			// { 11933, 1, 13000, 11629, 30, 438, 2123 },
			// { 11934, 1, 13000, 11629, 30, 438, 2123 },
			// { 11935, 1, 13000, 11629, 30, 438, 2123 },
			{ 2094, 1, 17, 450, 30, 438, 2123 },
			{ 2095, 1, 17, 451, 30, 438, 2124 },
			{ 2109, 1, 5, 451, 10, 434, 2138 },
			{ 2108, 1, 5, 450, 10, 434, 2137 },
			{ 2093, 15, 35, 451, 15, 440, 2122 },
			{ 2092, 15, 35, 450, 15, 440, 2121 },
			{ 2101, 20, 40, 451, 15, 442, 2124 },
			{ 2100, 20, 40, 450, 15, 442, 2123 },
			{ 2096, 30, 50, 450, 20, 453, 2121 },
			{ 2097, 30, 50, 451, 20, 453, 2122 },
			// { 11931, 30, 79000, 11629, 20, 453, 2121 },
			// { 11932, 30, 79000, 11629, 20, 453, 2121 },
			// { 11930, 30, 79000, 11629, 20, 453, 2121 },
			{ 2102, 55, 80, 450, 15, 447, 2131 },
			{ 2103, 55, 80, 451, 15, 447, 2132 },
			{ 2098, 65, 65, 450, 13, 444, 2127 },
			{ 2099, 65, 65, 451, 13, 444, 2128 },
			{ 2104, 70, 95, 450, 10, 449, 2133 },
			{ 2105, 70, 95, 451, 10, 449, 2134 },
			{ 2106, 85, 125, 450, 8, 451, 2135 },
			{ 2107, 85, 125, 451, 8, 451, 2136 },

	};

	public static int hasPick(Client client, int type) {
		for (int[] data : PICKAXE_DATA) {
			int pickaxe = data[0];
			int level = data[1];
			if ((client.playerEquipment[Player.playerWeapon] == pickaxe || client
					.getItems().playerHasItem(pickaxe)
					&& client.playerLevel[14] >= level)) {
				return data[type];
			}
		}
		return 0;
	}

	public static boolean hasRequiredLevel(Client client) {
		for (int[] element : ROCK_DATA) {
			int level = element[1];
			return client.playerLevel[14] >= level;
		}
		return false;
	}

	public static boolean doesntHaveRequiredSpace(Client client) {
		return client.getItems().freeSlots() < 1;
	}

	public static int getChance(Client client) {
		for (int[] element : ROCK_DATA) {
			int power = element[4];
			return 3000 + Misc.random(4000) + Misc.random(4000)
					/ client.playerLevel[14] / power;
		}
		return 0;
	}

	public static void checkRequirments(Client client, int objectId,
			int objectX, int objectY) {
		for (int[] element : ROCK_DATA) {
			int rock = element[0];
			int rockLevel = element[1];
			if (objectId == rock) {
				if (client.playerLevel[14] < rockLevel) {
					client.getDH().sendStatement(
							"You need a Mining level of @blu@" + rockLevel
									+ " @bla@to mine this ore.");
					client.isMining = false;
					return;
				}
				if (doesntHaveRequiredSpace(client)) {
					client.getDH().sendStatement(
							"You do not have the required inventory space.");
					client.isMining = false;
					return;
				}
				if (hasPick(client, 0) == 0) {
					client.getDH()
							.sendStatement(
									"You do not have a pickaxe or the required level to use it.");
					client.isMining = false;
					return;
				}
				startMining(client, client.objectId);
				client.getMiningEmote();
			}
		}
	}

	public static void startMining(final Client client, final int objectId) {
		for (int[] element : ROCK_DATA) {
			final int rock = element[0];
			final int exp = element[2];
			final int ore = element[5];
			for (int[] data : PICKAXE_DATA) {
				int pickaxe = data[0];
				if (objectId == rock && hasPick(client, 0) == pickaxe) {
					if (client.checkBusy()) {
						return;
					}
					client.setBusy(true);
					client.turnPlayerTo(client.objectX, client.objectY);
					client.sendMessage("You swing your pick at the rock.");
					client.isMining = true;
					client.getMiningEmote();
					EventManager.getSingleton().addEvent(new Event() {
						@Override
						public void execute(EventContainer p) {
							if (doesntHaveRequiredSpace(client)) {
								client.getDH()
										.sendStatement(
												"You do not have the required inventory space.");
								client.isMining = false;
								client.setBusy(false);
								p.stop();
								return;
							}
							if (client.isMining == false) {
								p.stop();
								client.setBusy(false);
								client.startAnimation(65535);
								return;
							}
							if (Misc.random(15) == 1 && client.objectId == rock) {
								addRandomGem(client);
							}
							if (client.isMining == true
									&& client.objectId == rock) {
								client.sendMessage("You manage to mine some "
										+ client.getItems().getItemName(ore)
										+ ".");
								client.getItems().addItem(ore, 1);
								client.getPA().addSkillXP(
										exp * Config.MINING_EXPERIENCE,
										client.playerMining);
								client.getPA()
										.refreshSkill(client.playerMining);
								addEmptyRock(client);
								client.setBusy(false);
								p.stop();
								client.isMining = false;
							}
							if (client.isMining == false) {
								p.stop();
								client.setBusy(false);
								client.startAnimation(65535);
								return;
							}
						}
					}, getChance(client));
				}
			}
		}
	}

	public static void addEmptyRock(Client client) {
		for (int[] element : ROCK_DATA) {
			int rock = element[0];
			int emptyRock = element[3];
			if (rock == client.objectId) {
				for (int j = 0; j < PlayerHandler.players.length; j++) {
					if (PlayerHandler.players[j] != null) {
						new Object(emptyRock, client.objectX, client.objectY,
								0, 0, 10, rock, 3, false);
						client.isMining = false;
						appendAnimation(client);
					}
				}
			}
		}
	}

	public static void addExplosiveRock(Client client) {
		for (int[] element : ROCK_DATA) {
			int rock = element[0];
			int explosiveRock = element[6];
			if (rock == client.objectId) {
				new Object(explosiveRock, client.objectX, client.objectY, 0, 0,
						10, rock, 10 + Misc.random(20), false);
				client.sendMessage("The rock begins to steem.");
			}
		}
	}

	public static void addRandomGem(Client client) {
		int random = Misc.random(5);
		if (random == 1) {
			client.getItems().addItem(1623, 1);
			client.sendMessage("You find a sapphire whilst mining.");
		} else if (random == 2) {
			client.getItems().addItem(1621, 1);
			client.sendMessage("You find a emerald whilst mining.");
		}
	}

	public static void appendAnimation(Client client) {
		for (int[] element : ROCK_DATA) {
			int rock = element[0];
			for (int j = 0; j < PlayerHandler.players.length; j++) {
				if (PlayerHandler.players[j] != null) {
					Client c = (Client) PlayerHandler.players[j];
					if (c.distanceToPoint(client.objectX, client.objectY) <= 1
							&& c.isMining == true && rock == client.objectId) {
						c.isMining = false;
					}
				}
			}
		}
	}

	public enum Prospect {
		COPPER(new int[] { 2091, 2090 }, 436), TIN(new int[] { 2094, 2095 },
				438), NOTHING(new int[] { 450 }, 10477), IRON(new int[] { 2092,
				2093 }, 440);
		int ore;
		int[] object;

		private Prospect(int[] object, int ore) {
			this.object = object;
			this.ore = ore;
		}

		public int getOre() {
			return ore;
		}

		public int[] getObject() {
			return object;
		}
	}

	public static Prospect forId(int object) {
		for (Prospect p : Prospect.values()) {
			for (int objectId : p.getObject()) {
				if (object == objectId) {
					return p;
				}
			}
		}
		return null;
	}

	/**
	 * Manages mining rock prospecting.
	 * 
	 * @param c
	 * @param p
	 */
	public static void startProspecting(final Client c, final Prospect p) {
		if (c.checkBusy()) {
			return;
		}
		c.setBusy(true);
		c.sendMessage("You prospect the rock.");
		CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {

			@Override
			public void execute(CycleEventContainer container) {
				// TODO Auto-generated method stub
				c.sendMessage("The rock contains "
						+ Item.getItemName(p.getOre()) + ".");
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
