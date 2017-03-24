package server.model.players;

import java.util.HashMap;

public class Food {

	private Client c;

	public Food(Client c) {
		this.c = c;
	}

	public static enum FoodToEat {
		Easter_Egg(1961, 12, "Easter Egg", 0, "Food", false), Pumpkin(1959, 14,
				"Pumpkin", 0, "Food", false), Half_Jug_of_Wine(1989, 7,
				"Half Full Wine Jug", 1935, "Drink", false), MANTA(391, 22,
				"Manta Ray", 0, "Food", false), SHARK(385, 20, "Shark", 0,
				"Food", false), LOBSTER(379, 12, "Lobster", 0, "Food", false), BEER(
				1917, 1, "Beer", 1919, "Drink", true), GREENMANS_ALE(1909, 1,
				"Greenman's Ale", 1919, "Drink", true), TROUT(333, 7, "Trout",
				0, "Food", false), SALMON(329, 9, "Salmon", 0, "Food", false), SWORDFISH(
				373, 14, "Swordfish", 0, "Food", false), TUNA(361, 10, "Tuna",
				0, "Food", false), MONKFISH(7946, 16, "Monkfish", 0, "Food",
				false), SEA_TURTLE(397, 21, "Sea Turtle", 0, "Food", false), CABBAGE(
				1965, 1, "Cabbage", 0, "Food", false), CAKE(1891, 4, "Cake", 0,
				"Food", false), BASS(365, 13, "Bass", 0, "Food", false), COD(
				339, 7, "Cod", 0, "Food", false), POTATO(1942, 1, "Potato", 0,
				"Food", false), BAKED_POTATO(6701, 4, "Baked Potato", 0,
				"Food", false), POTATO_WITH_CHEESE(6705, 16,
				"Potato with Cheese", 0, "Food", false), EGG_POTATO(7056, 16,
				"Egg Potato", 0, "Food", false), CHILLI_POTATO(7054, 14,
				"Chilli Potato", 0, "Food", false), MUSHROOM_POTATO(7058, 20,
				"Mushroom Potato", 0, "Food", false), TUNA_POTATO(7060, 22,
				"Tuna Potato", 0, "Food", false), SHRIMPS(315, 3, "Shrimps", 0,
				"Food", false), HERRING(347, 5, "Herring", 0, "Food", false), SARDINE(
				325, 4, "Sardine", 0, "Food", false), CHOCOLATE_CAKE(1897, 5,
				"Chocolate Cake", 1899, "Food", false), HALF_CHOCOLATE_CAKE(
				1899, 5, "2/3 Chocolate Cake", 1901, "Food", false), CHOCOLATE_SLICE(
				1901, 5, "Chocolate Slice", 0, "Food", false), ANCHOVIES(319,
				1, "Anchovies", 0, "Food", false), PLAIN_PIZZA(2289, 7,
				"Plain Pizza", 2291, "Food", false), HALF_PLAIN_PIZZA(2291, 7,
				"1/2 Plain pizza", 0, "Food", false), MEAT_PIZZA(2293, 8,
				"Meat Pizza", 2295, "Food", false), HALF_MEAT_PIZZA(2295, 8,
				"1/2 Meat Pizza", 0, "Food", false), ANCHOVY_PIZZA(2297, 9,
				"Anchovy Pizza", 2299, "Food", false), HALF_ANCHOVY_PIZZA(2299,
				9, "1/2 Anchovy Pizza", 0, "Food", false), PINEAPPLE_PIZZA(
				2301, 11, "Pineapple Pizza", 2303, "Food", false), HALF_PINEAPPLE_PIZZA(
				2303, 11, "1/2 Pineapple Pizza", 0, "Food", false), BREAD(2309,
				5, "Bread", 0, "Food", false), APPLE_PIE(2323, 7, "Apple Pie",
				2335, "Food", false), HALF_APPLE_PIE(2335, 7, "Half Apple Pie",
				2313, "Food", false), REDBERRY_PIE(2325, 5, "Redberry Pie",
				2333, "Food", false), HALF_REDBERRY_PIE(2333, 5,
				"Half Redberry Pie", 2313, "Food", false), Ugthanki_kebab(1883,
				2, "Ugthanki kebab", 0, "Food", false), MEAT_PIE(2327, 6,
				"Meat Pie", 2331, "Food", false), HALF_MEAT_PIE(2331, 6,
				"Half Meat Pie", 2313, "Food", false), SUMMER_PIE(7218, 11,
				"Summer Pie", 7220, "Food", false), HALF_SUMMER_PIE(7220, 11,
				"Half Summer Pie", 2313, "Food", false), PIKE(351, 8, "Pike",
				0, "Food", false), POTATO_WITH_BUTTER(6703, 14,
				"Potato with Butter", 0, "Food", false), BANANA(1963, 2,
				"Banana", 0, "Food", false), PEACH(6883, 8, "Peach", 0, "Food",
				false), ORANGE(2108, 2, "Orange", 0, "Food", false), PINEAPPLE_RINGS(
				2118, 2, "Pineapple Rings", 0, "Food", false), PINEAPPLE_CHUNKS(
				2116, 2, "Pineapple Chunks", 0, "Food", false), EASTER_EGG(
				7928, 1, "Easter Egg", 0, "Food", false), EASTER_EGG2(7929, 1,
				"Easter Egg", 0, "Food", false), EASTER_EGG3(7930, 1,
				"Easter Egg", 0, "Food", false), EASTER_EGG4(7931, 1,
				"Easter Egg", 0, "Food", false), EASTER_EGG5(7932, 1,
				"Easter Egg", 0, "Food", false), EASTER_EGG6(7933, 1,
				"Easter Egg", 0, "Food", false), PURPLE_SWEETS(10476, 9,
				"Purple Sweets", 0, "Food", false), ROCKTAIL(15272, 23,
				"Rocktail", 0, "Food", true);

		private int id;
		private int heal;
		private String name;
		private int replace;
		private String type;
		private boolean foodEffect;

		private FoodToEat(int id, int heal, String name, int replaceWith,
				String type, boolean foodEffect) {
			this.id = id;
			this.heal = heal;
			this.name = name;
			this.replace = replaceWith;
			this.type = type;
			this.foodEffect = foodEffect;
		}

		private boolean hasEffect() {
			return foodEffect;
		}

		private int getId() {
			return id;
		}

		private String getType() {
			return type;
		}

		private int getHeal() {
			return heal;
		}

		public String getName() {
			return name;
		}

		public int replaceWith() {
			return replace;
		}

		public static HashMap<Integer, FoodToEat> food = new HashMap<Integer, FoodToEat>();

		static {
			for (FoodToEat f : FoodToEat.values())
				food.put(f.getId(), f);
		}
	}

	public void eat(int id, int slot) {
		if (c.isDead || c.playerLevel[3] <= 0) {
			return;
		}
		if (c.duelRule[6]) {
			c.sendMessage("You may not eat in this duel.");
			return;
		}
		if (System.currentTimeMillis() - c.foodDelay >= 1400
				&& c.playerLevel[3] > 0) {
			c.getCombat().resetPlayerAttack();
			c.attackTimer += 2;
			c.startAnimation(829);
			c.getItems().deleteItem(id, slot, 1);
			FoodToEat f = FoodToEat.food.get(id);
			if (f.hasEffect()) {
				foodEffect(id);
			}
			if (f.replaceWith() > 0) {
				c.getItems().addItem(f.replaceWith(), 1);
			}
			if (c.playerLevel[3] <= c.calculateMaxLifePoints()
					&& !f.hasEffect()) {
				c.playerLevel[3] += f.getHeal();
				if (c.playerLevel[3] >= c.calculateMaxLifePoints()
						&& !f.hasEffect())
					c.playerLevel[3] = c.calculateMaxLifePoints();
			}
			if (f.hasEffect()) {
				if (c.playerLevel[3] < c.calculateMaxLifePoints() + 10) {
					c.playerLevel[3] += f.getHeal();
				}
				if (c.playerLevel[3] >= c.calculateMaxLifePoints() + 10) {
					c.playerLevel[3] = c.calculateMaxLifePoints() + 10;
				}
			}
			c.getPA().refreshSkill(3);
			if (f.getType().equalsIgnoreCase("Food"))
				c.sendMessage("You eat the " + f.getName() + ".");
			else if (f.getType().equalsIgnoreCase("Drink"))
				c.sendMessage("You drink the " + f.getName() + ".");
			c.foodDelay = System.currentTimeMillis();
		}
	}

	public void foodEffect(int id) {
		switch (id) {
		case 1917:
			double beerEffectStrength = (c.getLevelForXP(c.playerXP[2]) * .04)
					+ c.getLevelForXP(c.playerXP[2]);
			double beerEffectAttack = (c.getLevelForXP(c.playerXP[0]) * .07);
			if (c.playerLevel[2] < beerEffectStrength) {
				c.playerLevel[2] = (int) beerEffectStrength;
			}
			if (c.playerLevel[0] > 0)
				c.playerLevel[0] -= beerEffectAttack;
			if (c.playerLevel[0] <= 0)
				c.playerLevel[0] = 1;
			c.getPA().refreshSkill(0);
			c.getPA().refreshSkill(2);
			break;
		case 15272:
			break;
		}
	}

	public boolean isFood(int id) {
		return FoodToEat.food.containsKey(id);
	}
}