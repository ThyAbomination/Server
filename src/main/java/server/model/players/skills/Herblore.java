package server.model.players.skills;

import server.Config;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.players.Client;

public class Herblore {

	private static int EMPTY_FLASK = 14207;
	private static int EMPTY_VIAL = 229;

	public enum Decanting {
		SUPER_ATTACK(14188, 145, 147, 149, 2436),
		SUPER_STRENGTH(14176, 157, 159, 161, 2440),
		SUPER_DEFENSE(14164, 163, 165, 167, 2442),
		SUPER_RESTORE(14415, 3026, 2028, 3030, 3024),
		PRAYER(14200, 2434, 139, 141, 143),
		EXTREME_RANGING(14325, 15325, 15326, 15327, 15324),
		EXTREME_MAGIC(14337, 15321, 15322, 15323, 15320),
		EXTREME_DEFENSE(14349, 15317, 15318, 15319, 15316),
		EXTREME_STRENGTH(14361, 15313, 15314, 15315, 15312),
		EXTREME_ATTACK(14373, 15309, 15310, 15311, 150308),
		SARA_BREW(14128, 6687, 6689, 6691, 6685),
		OVERLOAD(14301, 15335, 15334, 15333, 15332);
		private int product, potionOne, potionTwo, potionThree, potionFour;

		/**
		 * Adding potions into flasks.
		 * 
		 * @param product
		 *            the flask that will be made
		 * @param potion_one
		 *            the potion with 1 dose left
		 * @param potion_two
		 *            the potion with 2 doses left
		 * @param potion_three
		 *            the potion with 3 doses left
		 * @param potion_four
		 *            the potion with 4 doses left
		 */
		Decanting(int product, int potionOne, int potionTwo, int potionThree,
				int potionFour) {
			this.setProduct(product);
			this.setPotionOne(potionOne);
			this.setPotionTwo(potionTwo);
			this.setPotionThree(potionThree);
			this.setPotionFour(potionFour);
		}

		private static Decanting getByPotion(int potion) {
			for (Decanting d : Decanting.values()) {
				if (d.potionOne == potion || d.potionTwo == potion
						|| d.potionThree == potion || d.potionFour == potion)
					return d;
			}
			return null;
		}

		public static boolean decant(final Client c, int itemUsed,
				int itemUsedWith) {
			final Decanting product = getByPotion(itemUsed == EMPTY_FLASK ? itemUsedWith
					: itemUsed);
			if(c.playerLevel[c.playerHerblore] < 95) {
				c.sendMessage("You need 95 herblore to do this.");
				return false;
			}
			if (product == null)
				return false;
			int totalDoses = 0;
			for (int i = 0; i < c.playerItems.length; i++) {
				if ((c.playerItems[i] - 1) == product.potionOne)
					totalDoses++;
				else if ((c.playerItems[i] - 1) == product.potionTwo)
					totalDoses += 2;
				else if ((c.playerItems[i] - 1) == product.potionThree)
					totalDoses += 3;
				else if ((c.playerItems[i] - 1) == product.potionFour)
					totalDoses += 4;
			}
			if (totalDoses < 6) {
				c.sendMessage("You need 6 doses in order to create a "
						+ c.getItems().getItemName(product.getProduct()) + ".");
				return false;
			}
			CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {

				@Override
				public void execute(CycleEventContainer container) {
					int totalDoses = 0;
					int remainder = 0;
					int totalEmptyPots = 0;
					if (c.getItems().playerHasItem(product.getPotionFour())) {
						totalDoses += (4 * c.getItems().getItemAmount(
								product.getPotionFour()));
						totalEmptyPots += c.getItems().getItemAmount(
								product.getPotionFour());
						c.getItems().deleteItem2(
								product.getPotionFour(),
								c.getItems().getItemAmount(
										product.getPotionFour()));
					}
					if (c.getItems().playerHasItem(product.getPotionThree())) {
						totalDoses += (3 * c.getItems().getItemAmount(
								product.getPotionThree()));
						totalEmptyPots += c.getItems().getItemAmount(
								product.getPotionThree());
						c.getItems().deleteItem2(
								product.getPotionThree(),
								c.getItems().getItemAmount(
										product.getPotionThree()));
					}
					if (c.getItems().playerHasItem(product.getPotionTwo())) {
						totalDoses += (2 * c.getItems().getItemAmount(
								product.getPotionTwo()));
						totalEmptyPots += c.getItems().getItemAmount(
								product.getPotionTwo());
						c.getItems().deleteItem2(
								product.getPotionTwo(),
								c.getItems().getItemAmount(
										product.getPotionTwo()));
					}
					if (c.getItems().playerHasItem(product.getPotionOne())) {
						totalDoses += (1 * c.getItems().getItemAmount(
								product.getPotionOne()));
						totalEmptyPots += c.getItems().getItemAmount(
								product.getPotionOne());
						c.getItems().deleteItem2(
								product.getPotionOne(),
								c.getItems().getItemAmount(
										product.getPotionOne()));
					}
					if (totalDoses > 0) {
						if (totalDoses >= 6)
							c.getItems().addItem(product.getProduct(),
									totalDoses / 6);
						else if (totalDoses == 4)
							c.getItems().addItem(product.getPotionFour(), 1);
						else if (totalDoses == 3)
							c.getItems().addItem(product.getPotionThree(), 1);
						else if (totalDoses == 2)
							c.getItems().addItem(product.getPotionTwo(), 1);
						else if (totalDoses == 1)
							c.getItems().addItem(product.getPotionOne(), 1);
						if ((totalDoses % 6) != 0) {
							totalEmptyPots -= 1;
							remainder = totalDoses % 6;
							if (remainder == 4)
								c.getItems()
										.addItem(product.getPotionFour(), 1);
							if (remainder == 3)
								c.getItems().addItem(product.getPotionThree(),
										1);
							else if (remainder == 2)
								c.getItems().addItem(product.getPotionTwo(), 1);
							else if (remainder == 1)
								c.getItems().addItem(product.getPotionOne(), 1);
						}
						totalEmptyPots -= (totalDoses / 4);
						c.getItems().addItem(EMPTY_VIAL, totalEmptyPots);
						c.sendMessage("You make "
								+ (totalDoses / 6)
								+ " "
								+ c.getItems()
										.getItemName(product.getProduct())
								+ " for " + ((totalDoses / 6) * 150) +
								" Herblore EXP.");
						c.getPA().addSkillXP( ((totalDoses / 6) * 150), c.playerHerblore);
						container.stop();
					}
				}

				@Override
				public void stop() {
					reset(c);
				}

			}, 2);
			return true;
		}

		public static void reset(Client c) {
			c.totalDoses = 0;
		}

		/**
		 * @return the product
		 */
		public int getProduct() {
			return product;
		}

		/**
		 * @param product
		 *            the product to set
		 */
		public void setProduct(int product) {
			this.product = product;
		}

		/**
		 * @return the potion_one
		 */
		public int getPotionOne() {
			return potionOne;
		}

		/**
		 * @param potion_one
		 *            the potion_one to set
		 */
		public void setPotionOne(int potion_one) {
			this.potionOne = potion_one;
		}

		/**
		 * @return the potion_two
		 */
		public int getPotionTwo() {
			return potionTwo;
		}

		/**
		 * @param potion_two
		 *            the potion_two to set
		 */
		public void setPotionTwo(int potion_two) {
			this.potionTwo = potion_two;
		}

		/**
		 * @return the potion_three
		 */
		public int getPotionThree() {
			return potionThree;
		}

		/**
		 * @param potion_three
		 *            the potion_three to set
		 */
		public void setPotionThree(int potion_three) {
			this.potionThree = potion_three;
		}

		/**
		 * @return the potion_four
		 */
		public int getPotionFour() {
			return potionFour;
		}

		/**
		 * @param potion_four
		 *            the potion_four to set
		 */
		public void setPotionFour(int potion_four) {
			this.potionFour = potion_four;
		}

	}

	private static int[][] grimyHerbs = {

			// GRIMY HERB || CLEAN HERB || LEVEL || XP //

			{ 199, 249, 1, 2 }, // Guam
			{ 201, 251, 5, 3 }, // Marrentill
			{ 203, 253, 11, 5 }, // Tarromin
			{ 205, 255, 20, 6 }, // Harralander
			{ 207, 257, 25, 7 }, // Ranarr
			{ 3049, 2998, 30, 7 }, // Toadflax
			{ 12174, 12172, 35, 8 }, // Spirit weed
			{ 209, 259, 40, 8 }, // Irit
			{ 14836, 14854, 41, 9 }, // Wergali
			{ 211, 261, 48, 10 }, // Avantoe
			{ 213, 263, 54, 11 }, // Kwuarm
			{ 3051, 3000, 59, 11 }, // Snapdragon
			{ 215, 265, 65, 12 }, // Candatine
			{ 2485, 2481, 67, 13 }, // Lantadyme
			{ 217, 267, 70, 13 }, // Dwarf Weed
			{ 219, 269, 75, 15 }, // Tortsol

	};

	private static int[][] unfinishedPotions = {

			// PRODUCT || CLEAN HERB || LEVEL //

			{ 91, 249, 1 }, // Guam
			{ 93, 251, 5 }, // Marrentill
			{ 95, 253, 12 }, // Tarromin
			{ 97, 255, 22 }, // Harralander
			{ 99, 257, 30 }, // Ranarr
			{ 3002, 2998, 34 }, // Toadflax
			{ 12181, 12172, 40 }, // Spirit Weed
			{ 101, 259, 45 }, // Irit
			{ 14856, 14854, 1 }, // Wergali
			{ 103, 261, 50 }, // Avantoe
			{ 105, 263, 55 }, // Kwuarm
			{ 3004, 3000, 63 }, // Snapdragon
			{ 107, 265, 66 }, // Cadantine
			{ 2483, 2481, 69 }, // Lantadyme
			{ 109, 267, 72 }, // Dwarf weed
			{ 111, 269, 78 } // Tortsol

	};

	private static int[][] finishedPotions = {

			// PRODUCT || UNFINISHED POTION || SECONDARY || LEVEL || EXP //

			{ 121, 91, 221, 1, 25 }, // Attack Potion
			{ 175, 93, 235, 5, 37 }, // Antipoison
			{ 115, 95, 225, 12, 50 }, // Strength Potion
			{ 127, 97, 223, 22, 62 }, // Restore Potion
			{ 3010, 97, 1975, 26, 67 }, // Energy Potion
			{ 133, 99, 239, 30, 75 }, // Defence Potion
			{ 3034, 3002, 2152, 34, 80 }, // Agility Potion
			{ 9741, 97, 9736, 36, 84 }, // Combat Potion
			{ 139, 99, 231, 38, 87 }, // Prayer Potion
			{ 12142, 12181, 12109, 40, 92 }, // Summoning Potion
			{ 14840, 14856, 5004, 42, 92 }, // Crafting Potion
			{ 145, 101, 221, 45, 100 }, // Super Attack
			{ 18661, 101, 1871, 46, 0 }, // Vial of Stench
			{ 181, 101, 235, 48, 106 }, // Super Antipoison
			{ 151, 103, 231, 50, 112 }, // Fishing Potion
			{ 3018, 103, 2970, 52, 117 }, // Super Energy
			{ 157, 105, 225, 55, 125 }, // Super Strength
			{ 187, 105, 241, 60, 137 }, // Weapon Poison
			{ 3026, 3004, 223, 63, 142 }, // Super Restore
			{ 163, 107, 239, 66, 150 }, // Super Defence
			{ 2454, 2483, 241, 69, 157 }, // Antifire
			{ 169, 109, 245, 72, 162 }, // Ranging Potion
			{ 3042, 2483, 3138, 76, 172 }, // Magic Potion
			{ 189, 111, 247, 78, 175 }, // Zamorak Brew
			{ 6687, 3002, 6693, 81, 180 }, // Saradomin Brew
			{ 15301, 3018, 5972, 84, 200 }, // Recover Special
			{ 15305, 2454, 4621, 85, 210 }, // Super Antifire
			{ 15309, 145, 261, 88, 220 }, // Extreme Attack
			{ 15313, 157, 267, 89, 230 }, // Extreme Strength
			{ 15317, 163, 2481, 90, 240 }, // Extreme Defence
			{ 15321, 3042, 9594, 91, 250 }, // Extreme Magic
			{ 15325, 169, 12539, 92, 260 }, // Extreme Ranging
			{ 15329, 139, 4255, 94, 270 } // Super Prayer

	};

	private static boolean cleaningHerb = false;
	private static boolean makingPotion = false;
	private static boolean makingUnfinished = false;

	public static void checkHerb(final Client c, int itemId) {
		for (int i = 0; i < grimyHerbs.length; i++) {
			if (itemId == grimyHerbs[i][0]) {
				cleanHerb(c, grimyHerbs[i][0], grimyHerbs[i][1],
						grimyHerbs[i][2], grimyHerbs[i][3]);
			}
		}
	}

	public static void cleanHerb(final Client c, final int grimy,
			final int clean, final int level, final int xp) {
		cleaningHerb = true;
		CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (c.playerLevel[15] < level) {
					c.sendMessage("You need an herblore level of " + level
							+ " to clean this herb.");
					container.stop();
				} else if (!c.getItems().playerHasItem(grimy)) {
					container.stop();
				} else if (cleaningHerb == true) {
					cleaningHerb(c, grimy, clean, level, xp);
					container.stop();
				}
			}

			@Override
			public void stop() {
				cleaningHerb = false;
			}
		}, 1);
	}

	public static void cleaningHerb(final Client c, final int grimy,
			final int clean, final int level, final int xp) {
		CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				cleaningHerb = true;
				if (cleaningHerb = true) {
					c.getItems().deleteItem(grimy, 1);
					c.getItems().addItem(clean, 1);
					c.getPA().addSkillXP(xp * Config.HERBLORE_EXPERIENCE, 15);
					container.stop();
				}
			}

			@Override
			public void stop() {
				cleanHerb(c, grimy, clean, level, xp);
				cleaningHerb = false;
			}
		}, 1);
	}

	public static void checkUnfinished(final Client c, int useWith, int itemUsed) {
		for (int i = 0; i < unfinishedPotions.length; i++) {
			if (itemUsed == unfinishedPotions[i][1] && useWith == 227
					|| itemUsed == 227 && useWith == unfinishedPotions[i][1]) {
				createUnfinishedPotion(c, unfinishedPotions[i][0],
						unfinishedPotions[i][1], unfinishedPotions[i][2]);
			}
		}
	}

	public static void createUnfinishedPotion(final Client c,
			final int finished, final int herb, final int level) {
		makingUnfinished = true;
		for (int i = 0; i < unfinishedPotions.length; i++) {
			CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					if (c.playerLevel[15] < level) {
						c.sendMessage("You need an herblore level of " + level
								+ " to create this potion.");
						container.stop();
					} else if (!c.getItems().playerHasItem(227)) {
						container.stop();
					} else if (!c.getItems().playerHasItem(herb)) {
						container.stop();
					} else if (makingUnfinished == true) {
						makeUnfinishedPotion(c, finished, herb, level);
						container.stop();
					}
				}

				@Override
				public void stop() {
					makingUnfinished = false;
				}
			}, 1);
		}
	}

	public static void makeUnfinishedPotion(final Client c, final int finished,
			final int herb, final int level) {
		CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				c.startAnimation(363);
				c.getItems().deleteItem(227, 1);
				c.getItems().deleteItem(herb, 1);
				c.getItems().addItem(finished, 1);
				container.stop();
			}

			@Override
			public void stop() {
				createUnfinishedPotion(c, finished, herb, level);
			}
		}, 3);
	}

	public static void checkPotion(final Client c, int useWith, int itemUsed) {
		for (int i = 0; i < finishedPotions.length; i++) {
			if (itemUsed == finishedPotions[i][1]
					&& useWith == finishedPotions[i][2]
					|| itemUsed == finishedPotions[i][2]
					&& useWith == finishedPotions[i][1]) {
				createPotion(c, finishedPotions[i][1], finishedPotions[i][2],
						finishedPotions[i][0], finishedPotions[i][3],
						finishedPotions[i][4]);
			}
		}
	}

	public static void createPotion(final Client c, final int primary,
			final int secondary, final int finished, final int level,
			final int xp) {
		makingPotion = true;
		CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (c.playerLevel[15] < level) {
					c.sendMessage("You need an herblore level of " + level
							+ " to create this potion.");
					container.stop();
				} else if (!c.getItems().playerHasItem(primary)) {
					container.stop();
				} else if (!c.getItems().playerHasItem(secondary)) {
					container.stop();
				} else if (makingPotion == true) {
					makePotion(c, primary, secondary, finished, level, xp);
					container.stop();
				}
			}

			@Override
			public void stop() {
				makingPotion = false;
			}
		}, 1);
	}

	public static void makePotion(final Client c, final int primary,
			final int secondary, final int finished, final int level,
			final int xp) {
		CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				c.startAnimation(363);
				c.getItems().deleteItem(primary, 1);
				c.getItems().deleteItem(secondary, 1);
				c.getItems().addItem(finished, 1);
				c.getPA().addSkillXP(xp * Config.HERBLORE_EXPERIENCE, 15);
				container.stop();
			}

			@Override
			public void stop() {
				createPotion(c, primary, secondary, finished, level, xp);
			}
		}, 3);
	}
}