package server.model.players.skills;

import server.Config;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.items.Item;
import server.model.players.Client;
import server.util.Misc;

public class Fishing {

	private enum Spot {
		LURE(309, new int[] { 335, 331 }, 309, 314, new int[] { 15, 25 },
				false, new int[] { 70, 90 }, 623), // trout, salmon
		CAGE(312, new int[] { 377 }, 301, -1, new int[] { 40 }, false,
				new int[] { 120 }, 619), // lobsters
		SMALLNET2(313, new int[] { 353, 341, 363 }, 303, -1, new int[] { 10,
				18, 43 }, false, new int[] { 60, 75, 80 }, 620), // mackeral,
																	// cod,
																	// bass
		SMALLNET(316, new int[] { 317, 321 }, 303, -1, new int[] { 1, 5 },
				false, new int[] { 30, 30 }, 621), // shrimps, anchovies
		MONKNET(326, new int[] { 7944 }, 305, -1, new int[] { 62 }, true,
				new int[] { 150 }, 621), // monkfish
		LURE2(309, new int[] { 349 }, 307, 313, new int[] { 20 }, true,
				new int[] { 80 }, 623), // pike
		HARPOON(312, new int[] { 359, 371 }, 311, -1, new int[] { 30, 45 },
				true, new int[] { 100, 140 }, 618), // tuna, swordfish
		HARPOON2(313, new int[] { 383 }, 311, -1, new int[] { 80 }, true,
				new int[] { 210 }, 618), // sharks
		HARPOON3(322, new int[] { 15270 }, 311, -1, new int[] { 93 }, true,
				new int[] { 225 }, 618), // rocktails
		BAIT(316, new int[] { 327, 345 }, 303, -1, new int[] { 1, 5 }, true,
				new int[] { 40, 50 }, 623);// sardine, herring

		int npcId, equipment, bait, anim;
		int[] rawFish, fishingReqs, xp;
		boolean second;

		private Spot(int npcId, int[] rawFish, int equipment, int bait,
				int[] fishingReqs, boolean second, int[] xp, int anim) {
			this.npcId = npcId;
			this.rawFish = rawFish;
			this.equipment = equipment;
			this.bait = bait;
			this.fishingReqs = fishingReqs;
			this.second = second;
			this.xp = xp;
			this.anim = anim;
		}

		public int getNPCId() {
			return npcId;
		}

		public int[] getRawFish() {
			return rawFish;
		}

		public int getEquipment() {
			return equipment;
		}

		public int getBait() {
			return bait;
		}

		public int[] getLevelReq() {
			return fishingReqs;
		}

		public boolean getSecond() {
			return second;
		}

		public int[] getXp() {
			return xp;
		}

		public int getAnim() {
			return anim;
		}
	}

	public static Spot forSpot(int npcId, boolean secondClick) {
		for (Spot s : Spot.values()) {
			if (secondClick) {
				if (s.getSecond()) {
					if (s.getNPCId() == npcId) {
						if (s != null)
							return s;
					}
				}
			} else {
				if (s.getNPCId() == npcId) {
					if (s != null)
						return s;
				}
			}
		}
		return null;
	}

	public static void setupFishing(Client c, Spot s) {
		if (c.playerLevel[c.playerFishing] >= s.getLevelReq()[0]) {
			if (c.getItems().playerHasItem(s.getEquipment())) {
				c.isFishing = true;
				if (c.isFishing == false) {
					return;
				}
				if (s.getBait() != -1) {
					if (c.getItems().playerHasItem(s.getBait(), 1)) {
						startFishing(c, s);
					} else {
						c.sendMessage("You don't have enough bait to fish here.");

						c.startAnimation(65535);
					}
				} else {
					startFishing(c, s);
				}
			} else {
				c.sendMessage("You need a "
						+ Item.getItemName(s.getEquipment()).toLowerCase()
						+ " to fish here.");
			}
		} else {
			c.sendMessage("You need a fishing level of at least "
					+ s.getLevelReq()[0] + " to fish here.");
		}
	}

	public static void startFishing(final Client c, final Spot s) {
		final int wat = Misc.random(100) >= 70 ? getMax(c, s.fishingReqs)
				: (getMax(c, s.fishingReqs) != 0 ? getMax(c, s.fishingReqs) - 1
						: 0);
		c.startAnimation(s.getAnim());
		if (c.checkBusy()) {
			return;
		}
		c.setBusy(true);
		c.isFishing = true;
		CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
			int cycle = c.getItems().freeSlots();

			@Override
			public void execute(CycleEventContainer container) {
				if (c.isFishing == false) {
					c.setBusy(false);
					container.stop();
					return;
				}
				c.startAnimation(s.getAnim());
				if (cycle != 0) {
					cycle--;
					c.sendMessage("You catch a "
							+ Item.getItemName(s.getRawFish()[wat])
									.toLowerCase().replace("_", " ") + ".");
					if (s.getBait() != -1) {
						c.getItems().deleteItem(s.getBait(),
								c.getItems().getItemSlot(s.getBait()), 1);
					}
					c.getItems().addItem(s.getRawFish()[wat], 1);
					c.getPA().addSkillXP(
							s.getXp()[wat] * Config.FISHING_EXPERIENCE,
							c.playerFishing);
					setupFishing(c, s);

				} else if (cycle == 0) {
					c.setBusy(false);
					container.stop();
				}
			}

			@Override
			public void stop() {
				// TODO Auto-generated method stub
				// c.setBusy(false);

			}
		}, 2);
	}

	public static int getMax(Client c, int[] reqs) {
		int tempInt = -1;
		for (int i : reqs) {
			if (c.playerLevel[c.playerFishing] >= i) {
				tempInt++;
			}
		}
		return tempInt;
	}

}
