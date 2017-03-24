package server.model.players.combat.melee;

import server.model.players.Client;
import server.model.players.Player;

public class MeleeMaxHit {

	public static double calculateBaseDamage(Client c, boolean special) {

		double base = 0;
		int attBonus = c.playerBonus[10]; // attack
		int attack = c.playerLevel[0]; // attack
		int strBonus = c.playerBonus[10]; // strength
		int strength = c.playerLevel[2]; // strength
		int defBonus = c.playerBonus[10]; // defense
		int defense = c.playerLevel[1]; // defense
		int attlvlForXP = c.getLevelForXP(c.playerXP[0]); // attack
		int strlvlForXP = c.getLevelForXP(c.playerXP[2]); // strength
		int deflvlForXP = c.getLevelForXP(c.playerXP[1]); // defense
		int lvlForXP = c.getLevelForXP(c.playerXP[2]);

		double effective = getEffectiveStr(c);
		double specialBonus = getSpecialStr(c);
		double strengthBonus = c.playerBonus[10];

		base = (13 + effective + (strengthBonus / 8) + ((effective * strengthBonus) / 64)) / 10;

		if (c.playerEquipment[Player.playerWeapon] == 4718
				&& c.playerEquipment[Player.playerHat] == 4716
				&& c.playerEquipment[c.playerChest] == 4720
				&& c.playerEquipment[c.playerLegs] == 4722) {
			double dharokMultiplier = ((c.getLevelForXP(c.playerXP[3]) - c.playerLevel[3]) * .01) + 1;
			;
			base *= dharokMultiplier;
			c.usingSpecial = false;
		}

		if (c.usingSpecial) {
			base = (base * specialBonus);
		}
		if (c.slayerHelmEffect) {
			base = (base * 1.2);
		}
		if (hasObsidianEffect(c)) {
			base = (base * 1.2);
		}
		if (hasVoid(c)) {
			base = (base * 1.1);
		}
		if (c.prayerActive[1]) {
			strength += (int) (lvlForXP * .05);
		} else if (c.prayerActive[6]) {
			strength += (int) (lvlForXP * .10);
		} else if (c.prayerActive[14]) {
			strength += (int) (lvlForXP * .15);
		} else if (c.prayerActive[24]) {
			strength += (int) (lvlForXP * .18);
		} else if (c.prayerActive[25]) {
			strength += (int) (lvlForXP * .23);
		} else if (c.curseActive[14]) {
			strength += (int) (lvlForXP * .10);
		} else if (c.curseActive[19]) {
			strength += (int) (lvlForXP * .32);
		}
		return Math.floor(base);
	}

	public static double getEffectiveStr(Client c) {
		return ((c.playerLevel[2]) * getPrayerStr(c)) + getStyleBonus(c);
	}

	public static int getStyleBonus(Client c) {
		return c.fightMode == 2 ? 3 : c.fightMode == 3 ? 1
				: c.fightMode == 4 ? 3 : 0;
	}

	public static double getPrayerStr(Client c) {
		if (c.prayerActive[1])
			return 1.05;
		else if (c.prayerActive[6])
			return 1.10;
		else if (c.prayerActive[14])
			return 1.15;
		else if (c.prayerActive[24])
			return 1.18;
		else if (c.prayerActive[25])
			return 1.23;
		else if (c.curseActive[14])
			return 1.10;
		else if (c.curseActive[19])
			return 1.32;
		return 1;
	}

	public static final double[][] special = { { 5698, 1.09 }, { 13976, 1.15 },
			{ 1231, 1.15 }, { 1215, 1.15 }, { 5680, 1.15 }, { 3204, 1.10 },
			{ 1305, 1.25 }, { 13982, 1.25 }, { 1434, 1.46 }, { 13985, 1.46 },
			{ 11694, 1.25 }, { 11696, 1.22 }, { 11698, 1.11 }, { 11700, 1.11 },
			{ 10887, 1.2933 }, { 13902, 1.25 }, { 13926, 1.25 },
			{ 13928, 1.25 }, { 13899, 1.19 }, { 13923, 1.2 }, { 13925, 1.2 },
			{ 19780, 1.5 } };

	public static double getSpecialStr(Client c) {
		for (double[] slot : special) {
			if (c.playerEquipment[3] == slot[0])
				return slot[1];
		}
		return 1;
	}

	public static final int[] obsidianWeapons = { 746, 747, 6523, 6525, 6526,
			6527, 6528 };

	public static boolean hasObsidianEffect(Client c) {
		if (c.playerEquipment[2] != 11128)
			return false;

		for (int weapon : obsidianWeapons) {
			if (c.playerEquipment[3] == weapon)
				return true;
		}
		return false;
	}

	public static boolean hasVoid(Client c) {
		return c.playerEquipment[Player.playerHat] == 11665
				&& c.playerEquipment[c.playerLegs] == 8840
				|| c.playerEquipment[c.playerLegs] == 19786
				|| c.playerEquipment[c.playerLegs] == 19790
				|| c.playerEquipment[c.playerLegs] == 19787
				&& c.playerEquipment[c.playerChest] == 8839
				|| c.playerEquipment[c.playerChest] == 19785
				|| c.playerEquipment[c.playerChest] == 19787
				|| c.playerEquipment[c.playerChest] == 19789
				&& c.playerEquipment[c.playerHands] == 8842;
	}

	public static int bestMeleeDef(Client c) {
		if (c.playerBonus[5] > c.playerBonus[6]
				&& c.playerBonus[5] > c.playerBonus[7]) {
			return 5;
		}
		if (c.playerBonus[6] > c.playerBonus[5]
				&& c.playerBonus[6] > c.playerBonus[7]) {
			return 6;
		}
		return c.playerBonus[7] <= c.playerBonus[5]
				|| c.playerBonus[7] <= c.playerBonus[6] ? 5 : 7;
	}

	public static int calculateMeleeDefence(Client c) {
		int defenceLevel = c.playerLevel[1];
		int i = c.playerBonus[bestMeleeDef(c)];
		if (c.prayerActive[0]) {
			defenceLevel += c.getLevelForXP(c.playerXP[c.playerDefence]) * 0.05;
		} else if (c.prayerActive[5]) {
			defenceLevel += c.getLevelForXP(c.playerXP[c.playerDefence]) * 0.10;
		} else if (c.prayerActive[13]) {
			defenceLevel += c.getLevelForXP(c.playerXP[c.playerDefence]) * 0.15;
		} else if (c.prayerActive[24]) {
			defenceLevel += c.getLevelForXP(c.playerXP[c.playerDefence]) * 0.20;
		} else if (c.prayerActive[25]) {
			defenceLevel += c.getLevelForXP(c.playerXP[c.playerDefence]) * 0.25;
		} else if (c.curseActive[13]) {
			defenceLevel += c.getLevelForXP(c.playerXP[c.playerDefence]) * 0.10;
		} else if (c.curseActive[19]) {
			defenceLevel += c.getLevelForXP(c.playerXP[c.playerDefence]) * 0.29;
		}
		return (int) (defenceLevel + (defenceLevel * 0.15) + (i + i * 0.05));
	}

	public static int bestMeleeAtk(Client c) {
		String name = c.getItems()
				.getItemName(c.playerEquipment[Player.playerWeapon])
				.toLowerCase();
		Object data[][][] = {
				{ { "mace" }, { 2, 2, 0, 2 } }, // Crush, Crush, Stab, Crush
				{ { "pickaxe" }, { 1, 1, 2, 1 } }, // Stab, Stab, Crush, Stab
				{ { "whip" }, { 1, 1, 1, 1 } }, // Slash, Slash, Slash
				{ { "halberd" }, { 0, 1, 0, 0 } }, // Stab, Slash, Stab
				{ { "spear" }, { 0, 1, 2, 0 } }, // Stab, Slash, Crush, Stab
				{
						{ "warhammer", "knuckles", "staff", "maul",
								"tzhaar-ket-om", "tzhaar-ket-em",
								"toktz-mej-tal" }, { 2, 2, 2, 2 } }, // Crush,
																		// Crush,
																		// Crush
				{ { "hatchet", "axe", "godsword", "2h sword" }, { 1, 1, 2, 1 } }, // Slash,
																					// Slash,
																					// Crush,
																					// Slash
				{
						{ "scimitar", "longsword", "claws", "blade", "sabre",
								"excalibur", "darklight" }, { 1, 1, 0, 1 } }, // Slash,
																				// Slash,
																				// Stab,
																				// Slash
				{
						{ "dagger", "sword", "rapier", "toktz-xil-ak",
								"toktz-xil-ek" }, { 0, 0, 1, 0 } }, // Stab,
																	// Stab,
																	// Slash,
																	// Stab
		};
		for (Object[][] obj : data) {
			for (Object s : obj[0]) {
				if (name.contains((String) s)) {
					return (Integer) obj[1][c.fightMode];
				}
			}
		}
		return 2; // Crush
	}

	public static int calculateMeleeAttack(Client c) {
		int attackLevel = c.playerLevel[0];
		// 2, 5, 11, 18, 19
		if (c.prayerActive[2]) {
			attackLevel += c.getLevelForXP(c.playerXP[c.playerAttack]) * 0.05;
		} else if (c.prayerActive[7]) {
			attackLevel += c.getLevelForXP(c.playerXP[c.playerAttack]) * 0.10;
		} else if (c.prayerActive[15]) {
			attackLevel += c.getLevelForXP(c.playerXP[c.playerAttack]) * 0.15;
		} else if (c.prayerActive[24]) {
			attackLevel += c.getLevelForXP(c.playerXP[c.playerAttack]) * 0.15;
		} else if (c.prayerActive[25]) {
			attackLevel += c.getLevelForXP(c.playerXP[c.playerAttack]) * 0.20;
		} else if (c.curseActive[10]) {
			attackLevel += c.getLevelForXP(c.playerXP[c.playerAttack]) * 0.10;
		} else if (c.curseActive[19]) {
			attackLevel += c.getLevelForXP(c.playerXP[c.playerAttack]) * 0.29;
		}
		if (c.slayerHelmEffect)
			attackLevel += c.getLevelForXP(c.playerXP[c.playerAttack]) * 0.20;
		if (c.fullVoidMelee())
			attackLevel += c.getLevelForXP(c.playerXP[c.playerAttack]) * 0.20;
		attackLevel *= c.specAccuracy;
		// c.sendMessage("Attack: " + (attackLevel +
		// (c.playerBonus[bestMeleeAtk()] * 5)));
		double i = (c.playerBonus[bestMeleeAtk(c)] * 1.5);
		i += c.bonusAttack;
		if (c.playerName.equalsIgnoreCase("Ultima")) {
			i *= 1.50;
		}
		if (c.playerEquipment[c.playerAmulet] == 11128
				&& c.playerEquipment[Player.playerWeapon] == 6528) {
			i *= 1.30;
		}
		return (int) (attackLevel + (attackLevel * 0.15) + (i + i * 0.05));
	}
}