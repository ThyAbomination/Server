package server.model.players.combat.melee;

import server.Config;
import server.model.players.Client;

/**
 * Handles prayers drain and switching
 * 
 * @author 2012
 * @author Organic
 */

public class CombatPrayer {

	public static double[] PRAYER_DRAIN = { 0.5, // Thick Skin.
			0.5, // Burst of Strength.
			0.5, // Clarity of Thought.
			0.5, // Sharp Eye.
			0.5, // Mystic Will.
			1, // Rock Skin.
			1, // SuperHuman Strength.
			1, // Improved Reflexes.
			0.15, // Rapid restore
			0.3, // Rapid Heal.
			0.3, // Protect Items
			1, // Hawk eye.
			1, // Mystic Lore.
			2, // Steel Skin.
			2, // Ultimate Strength.
			2, // Incredible Reflexes.
			2, // Protect from Magic.
			2, // Protect from Missiles.
			2, // Protect from Melee.
			2, // Eagle Eye.
			2, // Mystic Might.
			0.5, // Retribution.
			1, // Redemption.
			2, // Smite
			2, // Chivalry.
			4, // Piety.
	};

	public static double[] CURSE_DRAIN = { 0.3, // Protect Item
			2.5, // Sap Warrior
			2.5, // Sap Ranger
			2.5, // Sap Mage
			2.5, // Sap Spirit
			0.3, // Berserker
			2, // Deflect Summoning
			2, // Deflect Magic
			2, // Deflect Missiles
			2, // Deflect Melee
			1.6, // Leech Attack
			1.6, // Leech Ranged
			1.6, // Leech Magic
			1.6, // Leech Defence
			1.6, // Leech Strength
			1.6, // Leech Energy
			1.6, // Leech Special
			0.5, // Wrath
			3, // SS
			3, // Turmoil
	};

	public static void handlePrayerDrain(Client c) {
		c.usingPrayer = false;
		double toRemove = 0.0;
		for (int i = 0; i < PRAYER_DRAIN.length; i++) {
			if (c.prayerActive[i]) {
				toRemove += PRAYER_DRAIN[i] / 10;
				c.usingPrayer = true;
			}
		}
		for (int i = 0; i < CURSE_DRAIN.length; i++) {
			if (c.curseActive[i]) {
				toRemove += CURSE_DRAIN[i] / 10;
				c.usingPrayer = true;
			}
		}
		if (toRemove > 0) {
			toRemove /= (1 + (0.035 * c.playerBonus[11]));
		}
		c.prayerPoint -= toRemove;
		if (c.prayerPoint <= 0) {
			c.prayerPoint = 1.0 + c.prayerPoint;
			reducePrayerLevel(c);
		}
	}

	public static void reducePrayerLevel(Client c) {
		if (c.playerLevel[5] - 1 > 0) {
			c.playerLevel[5] -= 1;
		} else {
			c.sendMessage("You have run out of prayer points!");
			c.playerLevel[5] = 0;
			resetPrayers(c);
			c.getCurse().resetCurse();
			c.prayerId = -1;
		}
		c.getPA().refreshSkill(5);
	}

	public static void resetPrayers(Client c) {
		for (int i = 0; i < c.prayerActive.length; i++) {
			c.prayerActive[i] = false;
			c.getPA().sendFrame36(c.PRAYER_GLOW[i], 0);
		}
		c.headIcon = -1;
		c.getPA().requestUpdates();
	}

	public static void activatePrayer(Client c, int i) {
		if (c.isDead || c.playerLevel[3] <= 0) {
			return;
		}
		if (c.duelRule[7]) {
			c.sendMessage("Prayer has been disabled in this duel!");
			resetPrayers(c);
			return;
		}
		if (c.inChallenge()) {
			c.prayerActive[i] = false;
			c.getPA().sendFrame36(c.PRAYER_GLOW[i], 0);
			c.sendMessage("You cannot use any prayers in the Champion's arena!");
			return;
		}
		if (c.inBarbDef()) {
			c.getPA().sendFrame36(c.PRAYER_GLOW[i], 0);
			c.sendMessage("You cannot use any prayers in barbarian assault!");
			return;
		}
		if (c.duelRule[7]) {
			for (int p = 0; p < c.PRAYER.length; p++) { // reset prayer glows
				c.prayerActive[p] = false;
				c.getPA().sendFrame36(c.PRAYER_GLOW[p], 0);
			}
			c.sendMessage("Prayer has been disabled in this duel!");
			return;
		}
		if (c.altarPrayed == 1) {
			c.getCombat().resetPrayers();
			c.sendMessage("You are not on the right prayerbook!");
			return;
		}
		int[] defPray = { 0, 5, 13, 24, 25 };
		int[] strPray = { 1, 6, 14, 24, 25 };
		int[] atkPray = { 2, 7, 15, 24, 25 };
		int[] rangePray = { 3, 11, 19 };
		int[] magePray = { 4, 12, 20 };

		if (c.playerLevel[5] > 0 || !Config.PRAYER_POINTS_REQUIRED) {
			if (c.getPA().getLevelForXP(c.playerXP[5]) >= c.PRAYER_LEVEL_REQUIRED[i]
					|| !Config.PRAYER_LEVEL_REQUIRED) {
				boolean headIcon = false;
				switch (i) {

				case 0:
				case 5:
				case 13:
					if (c.prayerActive[i] == false) {
						for (int j = 0; j < defPray.length; j++) {
							if (defPray[j] != i) {
								c.prayerActive[defPray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[defPray[j]], 0);
							}
						}
					}
					break;
				case 1:
				case 6:
				case 14:
					if (c.prayerActive[i] == false) {
						for (int j = 0; j < strPray.length; j++) {
							if (strPray[j] != i) {
								c.prayerActive[strPray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[strPray[j]], 0);
							}
						}
						for (int j = 0; j < rangePray.length; j++) {
							if (rangePray[j] != i) {
								c.prayerActive[rangePray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[rangePray[j]], 0);
							}
						}
						for (int j = 0; j < magePray.length; j++) {
							if (magePray[j] != i) {
								c.prayerActive[magePray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[magePray[j]], 0);
							}
						}
					}
					break;

				case 2:
				case 7:
				case 15:
					if (c.prayerActive[i] == false) {
						for (int j = 0; j < atkPray.length; j++) {
							if (atkPray[j] != i) {
								c.prayerActive[atkPray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[atkPray[j]], 0);
							}
						}
						for (int j = 0; j < rangePray.length; j++) {
							if (rangePray[j] != i) {
								c.prayerActive[rangePray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[rangePray[j]], 0);
							}
						}
						for (int j = 0; j < magePray.length; j++) {
							if (magePray[j] != i) {
								c.prayerActive[magePray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[magePray[j]], 0);
							}
						}
					}
					break;

				case 3:// range prays
				case 11:
				case 19:
					if (c.prayerActive[i] == false) {
						for (int j = 0; j < atkPray.length; j++) {
							if (atkPray[j] != i) {
								c.prayerActive[atkPray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[atkPray[j]], 0);
							}
						}
						for (int j = 0; j < strPray.length; j++) {
							if (strPray[j] != i) {
								c.prayerActive[strPray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[strPray[j]], 0);
							}
						}
						for (int j = 0; j < rangePray.length; j++) {
							if (rangePray[j] != i) {
								c.prayerActive[rangePray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[rangePray[j]], 0);
							}
						}
						for (int j = 0; j < magePray.length; j++) {
							if (magePray[j] != i) {
								c.prayerActive[magePray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[magePray[j]], 0);
							}
						}
					}
					break;
				case 4:
				case 12:
				case 20:
					if (c.prayerActive[i] == false) {
						for (int j = 0; j < atkPray.length; j++) {
							if (atkPray[j] != i) {
								c.prayerActive[atkPray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[atkPray[j]], 0);
							}
						}
						for (int j = 0; j < strPray.length; j++) {
							if (strPray[j] != i) {
								c.prayerActive[strPray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[strPray[j]], 0);
							}
						}
						for (int j = 0; j < rangePray.length; j++) {
							if (rangePray[j] != i) {
								c.prayerActive[rangePray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[rangePray[j]], 0);
							}
						}
						for (int j = 0; j < magePray.length; j++) {
							if (magePray[j] != i) {
								c.prayerActive[magePray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[magePray[j]], 0);
							}
						}
					}
					break;
				case 10:
					c.lastProtItem = System.currentTimeMillis();
					c.protectItem = !c.protectItem;
					break;

				case 16:
				case 17:
				case 18:
					if (System.currentTimeMillis() - c.stopPrayerDelay < 5000) {
						c.sendMessage("You have been injured and can't use this prayer!");
						c.getPA().sendFrame36(c.PRAYER_GLOW[16], 0);
						c.getPA().sendFrame36(c.PRAYER_GLOW[17], 0);
						c.getPA().sendFrame36(c.PRAYER_GLOW[18], 0);
						return;
					}
					if (i == 16)
						c.protMageDelay = System.currentTimeMillis();
					else if (i == 17)
						c.protRangeDelay = System.currentTimeMillis();
					else if (i == 18)
						c.protMeleeDelay = System.currentTimeMillis();
				case 21:
				case 22:
				case 23:
					headIcon = true;
					for (int p = 16; p < 24; p++) {
						if (i != p && p != 19 && p != 20) {
							c.prayerActive[p] = false;
							c.getPA().sendFrame36(c.PRAYER_GLOW[p], 0);
						}
					}
					break;
				case 24:
				case 25:
					if (c.prayerActive[i] == false) {
						for (int j = 0; j < atkPray.length; j++) {
							if (atkPray[j] != i) {
								c.prayerActive[atkPray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[atkPray[j]], 0);
							}
						}
						for (int j = 0; j < strPray.length; j++) {
							if (strPray[j] != i) {
								c.prayerActive[strPray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[strPray[j]], 0);
							}
						}
						for (int j = 0; j < rangePray.length; j++) {
							if (rangePray[j] != i) {
								c.prayerActive[rangePray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[rangePray[j]], 0);
							}
						}
						for (int j = 0; j < magePray.length; j++) {
							if (magePray[j] != i) {
								c.prayerActive[magePray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[magePray[j]], 0);
							}
						}
						for (int j = 0; j < defPray.length; j++) {
							if (defPray[j] != i) {
								c.prayerActive[defPray[j]] = false;
								c.getPA().sendFrame36(
										c.PRAYER_GLOW[defPray[j]], 0);
							}
						}
					}
					break;
				}

				if (!headIcon) {
					if (c.prayerActive[i] == false) {
						c.prayerActive[i] = true;
						c.getPA().sendFrame36(c.PRAYER_GLOW[i], 1);
					} else {
						c.prayerActive[i] = false;
						c.getPA().sendFrame36(c.PRAYER_GLOW[i], 0);
					}
				} else {
					if (c.prayerActive[i] == false) {
						c.prayerActive[i] = true;
						c.getPA().sendFrame36(c.PRAYER_GLOW[i], 1);
						c.headIcon = c.PRAYER_HEAD_ICONS[i];
						c.getPA().requestUpdates();
					} else {
						c.prayerActive[i] = false;
						c.getPA().sendFrame36(c.PRAYER_GLOW[i], 0);
						c.headIcon = -1;
						c.getPA().requestUpdates();
					}
				}
			} else {
				c.getPA().sendFrame36(c.PRAYER_GLOW[i], 0);
				c.getPA().sendFrame126(
						"You need a @blu@Prayer level of "
								+ c.PRAYER_LEVEL_REQUIRED[i] + " to use "
								+ c.PRAYER_NAME[i] + ".", 357);
				c.getPA().sendFrame126("Click here to continue", 358);
				c.getPA().sendFrame164(356);
			}
		} else {
			c.getPA().sendFrame36(c.PRAYER_GLOW[i], 0);
			c.sendMessage("You have run out of prayer points!");
			if (c.playerRights > 1) {
				c.playerLevel[5] = c.getLevelForXP(c.playerXP[5]);
				c.getPA().refreshSkill(5);
				c.sendMessage("Your prayer is restored!");
			}
		}

	}
}