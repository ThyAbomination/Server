package server.model.players.skills;

import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.objects.Object;
import server.model.players.Client;
import server.model.players.PlayerHandler;
import server.util.Misc;

public class ThievingManager {

	public enum NpcData {
		MAN(new int[] { 1, 2, 3, 4 }, 1, new int[] { 995 }, 30, 50), FARMER(
				new int[] { 7 }, 10, new int[] { 995, 313, 314 }, 15, 100), ALKHARID_WARRIOR(
				new int[] { 18 }, 25, new int[] { 995 }, 100, 150), GUARD(
				new int[] { 9, 32 }, 40, new int[] { 995 }, 150, 200), HAM_FEMALE(
				new int[] { 1715 }, 15, new int[] { 4298, 4300, 4302, 4304,
						4308, 4310 }, 1, 50), HAM_MALE(new int[] { 1714 }, 20,
				new int[] { 4298, 4300, 4302, 4304, 4308, 4310 }, 1, 100), VILLAGER(
				new int[] { 1888 }, 45, new int[] { 995 }, Misc.random(2000),
				100), BANDIT(new int[] { 1880 }, 55, new int[] { 995 }, Misc
				.random(5000), 200), MASTER_FARMER(new int[] { 2234 }, 38,
				new int[] { 5291, 5291, 5291, 5293, 5293, 5294, 5294, 5294,
						5294, 5295, 5296, 5297, 5298, 5300, 5303, 5304, 5291,
						5291, 5291, 5291, 5293, 5294, 5294, 5291, 5291, 5292,
						5292, 5292 }, Misc.random(5), 150), FREMENNIK_PERSON(
				new int[] { 1307, 1305, 1306, 1311, 1310, 1308, 1314 }, 45,
				new int[] { 995 }, Misc.random(200), 200), WATCHMAN(
				new int[] { 34 }, 65, new int[] { 995 }, Misc.random(50), 300), ARDOUGNE_KNIGHT(
				new int[] { 26, 23 }, 55, new int[] { 995 }, Misc.random(100),
				250), PALADIN(new int[] { 20 }, 70, new int[] { 995 }, Misc
				.random(500), 325), HERO(new int[] { 21 }, 80,
				new int[] { 995 }, Misc.random(800), 400);
		int requirements, amount, xp;
		int[] items;
		int[] npcId;

		private NpcData(int[] npcId, int requirements, int[] items, int amount,
				int xp) {
			this.npcId = npcId;
			this.requirements = requirements;
			this.items = items;
			this.amount = amount;
			this.xp = xp;
		}

		public int[] getNpcId() {
			return npcId;
		}

		public int getRequirements() {
			return requirements;
		}

		public int getAmount() {
			return amount;
		}

		public int getXP() {
			return xp;
		}

		public int[] getItems() {
			return items;
		}
	}

	public static NpcData forNpcData(int npcId) {
		for (NpcData npcData : NpcData.values()) {
			for (int npc : npcData.getNpcId()) {
				if (npc == npcId) {
					if (npcData != null) {
						return npcData;
					}
				}
			}
		}
		return null;
	}

	public static void setupNPCData(final Client c, final NpcData data) {
		if (c.getItems().freeSlots() == 0) {
			c.getDH().sendStatement(
					"You do not have enough space in your inventory.");
			return;
		}
		if (c.playerLevel[c.playerThieving] < data.getRequirements()) {
			c.getDH().sendStatement(
					"You need a Thieving level of " + data.getRequirements()
							+ ".");
			return;
		}
		if (c.playerLevel[c.playerThieving] >= data.getRequirements()) {
			if (c.checkBusy()) {
				return;
			}
			c.setBusy(true);
			CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
				public int cycle = 0;

				@Override
				public void execute(CycleEventContainer container) {
					// TODO Auto-generated method stub
					int chance = 2 * c.playerLevel[c.playerThieving]
							- data.getRequirements();
					// *c.playerLevel[c.playerThieving]-data.getRequirements();
					if (Misc.random(chance) == 1) {
						cycle++;
						c.sendMessage("You have been stunned.");
						// Server.npcHandler.attackPlayer(c, c.playerIndex);
						c.gfx0(80);
						c.startAnimation(65535);
						c.setBusy(false);
						container.stop();
						return;
					} else if (cycle == 0) {
						c.getPA().addSkillXP(data.getXP(), c.playerThieving);
						c.startAnimation(881);
						c.getItems()
								.addItem(
										data.getItems()[Misc.random(data
												.getItems().length - 1)],
										Misc.random(data.getAmount()));
						c.getPA().refreshSkill(c.playerThieving);
						c.setBusy(false);
						container.stop();
					}
				}

				@Override
				public void stop() {
					// TODO Auto-generated method stub

				}

			}, 1);
		}
	}

	// public static void addEmptyStall2(Client client) {
	// for (int j = 0; j < PlayerHandler.players.length; j++) {
	// if (PlayerHandler.players[j] != null) {
	// Object o = (Object) Server.objectManager.objectExists(client.objectX,
	// client.objectY, 0);
	// o.newId=client.objectId;
	// Server.objectManager.updateObject(o);
	// o.newId = 634;
	// o.tick = 20;
	// // new Object(634, client.objectX, client.objectY, 0, 0,
	// // 10, client.objectId, 20 + Misc.random(40), false);
	//
	// // appendAnimation(client);
	// }
	// }
	// }

	public static void addEmptyStall(Client client) {
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null) {
				new Object(634, client.objectX, client.objectY, 0, -1, 10,
						client.objectId, 20 + Misc.random(40), false);

				// appendAnimation(client);
			}
		}
	}

	public enum Stall {
		TEA(635, new int[] { 712 }, 30, 5), BAKER(2561,
				new int[] { 1897, 1891 }, 50, 5), SILK(2560, new int[] { 950 },
				24, 20), FUR(2563, new int[] { 948, 958 }, 36, 35), FUR2(4278,
				new int[] { 948, 958 }, 36, 35), FISH_STALL(4277, new int[] {
				333, 335, 325, 319, 321, 317, 315, 351 }, 75, 42), SPICE(2564,
				new int[] { 2007 }, 81, 65), GEM(2562, new int[] { 1623, 1621,
				1619, 1617, 1631 }, 160, 75);
		int objectId, xp, level;
		int[] items;

		private Stall(int objectId, int[] items, int xp, int level) {
			this.objectId = objectId;
			this.items = items;
			this.xp = xp;
			this.level = level;
		}

		public int getObjectId() {
			return objectId;
		}

		public int getXP() {
			return xp;
		}

		public int getLevel() {
			return level;
		}

		public int[] getItems() {
			return items;
		}
	}

	public static Stall forStall(int id) {
		for (Stall s : Stall.values()) {
			if (s.getObjectId() == id) {
				return s;
			}
		}
		return null;
	}

	public static void setupStallData(final Client c, final Stall s) {
		if (c.getItems().freeSlots() == 0) {
			return;
		}
		if (c.playerLevel[c.playerThieving] < s.getLevel()) {
			c.getDH().sendStatement(
					"You need a Thieving level of " + s.getLevel()
							+ " to do this.");
			return;
		}
		if (c.playerLevel[c.playerThieving] >= s.getLevel()) {
			if (c.checkBusy()) {
				return;
			}
			c.setBusy(true);
			CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {

				@Override
				public void execute(CycleEventContainer container) {
					// TODO Auto-generated method stub
					c.getItems().addItem(
							s.getItems()[Misc.random(s.getItems().length - 1)],
							1);
					c.getPA().addSkillXP(s.getXP(), c.playerThieving);
					c.startAnimation(832);
					addEmptyStall(c);
					c.sendMessage("You loot the stall.");
					c.setBusy(false);
					container.stop();
				}

				@Override
				public void stop() {
					// TODO Auto-generated method stub

				}

			}, 1 + Misc.random(2));
		}
	}

}
