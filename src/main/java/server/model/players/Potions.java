package server.model.players;

/**
 * @author Sanity
 */

public class Potions {

	private Client c;

	public Potions(Client c) {
		this.c = c;
	}

	public void handlePotion(int itemId, int slot) {
		if (c.isDead || c.playerLevel[3] <= 0) {
			return;
		}
		if (c.duelRule[5]) {
			c.sendMessage("You may not drink potions in this duel.");
			return;
		}
		if (System.currentTimeMillis() - c.potDelay >= 1400) {
			c.potDelay = System.currentTimeMillis();
			c.foodDelay = c.potDelay;
			c.getCombat().resetPlayerAttack();
			// c.attackTimer++;
			c.sendMessage("You drink some of your "
					+ server.model.items.Item.getItemName(itemId) + ".");
			String item = server.model.items.Item.getItemName(itemId);
			if (item.endsWith("(6)")) {
				c.sendMessage("You have 5 doses of potion left.");
			} else if (item.endsWith("(5)")) {
				c.sendMessage("You have 4 doses of potion left.");
			} else if (item.endsWith("(4)")) {
				c.sendMessage("You have 3 doses of potion left.");
			} else if (item.endsWith("(3)")) {
				c.sendMessage("You have 2 doses of potion left.");
			} else if (item.endsWith("(2)")) {
				c.sendMessage("You have 1 dose of potion left.");
			} else if (item.endsWith("(1)")) {
				c.sendMessage("You have finished your potion.");
			}
			switch (itemId) {
			case 14200:
				drinkPrayerPot(itemId, 14198, slot, false);// prayer flask
				break;
			case 14198:
				drinkPrayerPot(itemId, 14196, slot, false);
				break;
			case 14196:
				drinkPrayerPot(itemId, 14194, slot, false);
				break;
			case 14194:
				drinkPrayerPot(itemId, 14192, slot, false);
				break;
			case 14192:
				drinkPrayerPot(itemId, 14190, slot, false);
				break;
			case 14190:
				drinkPrayerPot(itemId, 14207, slot, false);
				break;
			case 14188: // super attack flask
				drinkStatPotion(itemId, 14186, slot, 0, true);
				break;
			case 14186:
				drinkStatPotion(itemId, 14184, slot, 0, true);
				break;
			case 14184:
				drinkStatPotion(itemId, 14182, slot, 0, true);
				break;
			case 14182:
				drinkStatPotion(itemId, 14180, slot, 0, true);
				break;
			case 14180:
				drinkStatPotion(itemId, 14178, slot, 0, true);
				break;
			case 14178:
				drinkStatPotion(itemId, 14207, slot, 0, true);
				break;
			case 14176:// sup str flask
				drinkStatPotion(itemId, 14174, slot, 2, true);
				break;
			case 14174:
				drinkStatPotion(itemId, 14172, slot, 2, true);
				break;
			case 14172:
				drinkStatPotion(itemId, 14170, slot, 2, true);
				break;
			case 14170:
				drinkStatPotion(itemId, 14168, slot, 2, true);
				break;
			case 14168:
				drinkStatPotion(itemId, 14166, slot, 2, true);
				break;
			case 14166:
				drinkStatPotion(itemId, 14207, slot, 2, true);
				break;
			case 14164: // super def
				drinkStatPotion(itemId, 14162, slot, 1, true);
				break;
			case 14162:
				drinkStatPotion(itemId, 14160, slot, 1, true);
				break;
			case 14160:
				drinkStatPotion(itemId, 14158, slot, 1, true);
				break;
			case 14158:
				drinkStatPotion(itemId, 14156, slot, 1, true);
				break;
			case 14156:
				drinkStatPotion(itemId, 14154, slot, 1, true);
				break;
			case 14154:
				drinkStatPotion(itemId, 14207, slot, 1, true);
				break;
			case 14152: // range flask
				drinkStatPotion(itemId, 14150, slot, 4, false);
				break;
			case 14150:
				drinkStatPotion(itemId, 14148, slot, 4, false);
				break;
			case 14148:
				drinkStatPotion(itemId, 14146, slot, 4, false);
				break;
			case 14146:
				drinkStatPotion(itemId, 14144, slot, 4, false);
				break;
			case 14144:
				drinkStatPotion(itemId, 14142, slot, 4, false);
				break;
			case 14142:
				drinkStatPotion(itemId, 14207, slot, 4, false);
				break;
			case 14140: // super anti
				drinkAntiPoison(itemId, 14138, slot, 300000);
				break;
			case 14138:
				drinkAntiPoison(itemId, 14136, slot, 300000);
				break;
			case 14136:
				drinkAntiPoison(itemId, 14134, slot, 300000);
				break;
			case 14134:
				drinkAntiPoison(itemId, 14132, slot, 300000);
				break;
			case 14132:
				drinkAntiPoison(itemId, 14130, slot, 300000);
				break;
			case 14130:
				drinkAntiPoison(itemId, 14207, slot, 300000);
				break;
			case 14128: // brew flasks
				doTheBrew(itemId, 14126, slot);
				break;
			case 14126:
				doTheBrew(itemId, 14124, slot);
				break;
			case 14124:
				doTheBrew(itemId, 14122, slot);
				break;
			case 14122:
				doTheBrew(itemId, 14419, slot);
				break;
			case 14419:
				doTheBrew(itemId, 14417, slot);
				break;
			case 14417:
				doTheBrew(itemId, 14207, slot);
				break;
			case 14415:// supres flasks
				drinkPrayerPot(itemId, 14413, slot, true);
				break;
			case 14413:
				drinkPrayerPot(itemId, 14411, slot, true);
				break;
			case 14411:
				drinkPrayerPot(itemId, 14409, slot, true);
				break;
			case 14409:
				drinkPrayerPot(itemId, 14407, slot, true);
				break;
			case 14407:
				drinkPrayerPot(itemId, 14405, slot, true);
				break;
			case 14405:
				drinkPrayerPot(itemId, 14207, slot, true);
				break;
			case 14403:// mage flasks
				drinkStatPotion(itemId, 14401, slot, 6, false);
				break;
			case 14401:
				drinkStatPotion(itemId, 14399, slot, 6, false);
				break;
			case 14399:
				drinkStatPotion(itemId, 14397, slot, 6, false);
				break;
			case 14397:
				drinkStatPotion(itemId, 14395, slot, 6, false);
				break;
			case 14395:
				drinkStatPotion(itemId, 14393, slot, 6, false);
				break;
			case 14393:
				drinkStatPotion(itemId, 14207, slot, 6, false);
				break;
			case 14385:// spec rec flasks
				recoverSpecial(itemId, 14383, slot);
				break;
			case 14383:
				recoverSpecial(itemId, 14381, slot);
				break;
			case 14381:
				recoverSpecial(itemId, 14379, slot);
				break;
			case 14379:
				recoverSpecial(itemId, 14377, slot);
				break;
			case 14377:
				recoverSpecial(itemId, 14375, slot);
				break;
			case 14375:
				recoverSpecial(itemId, 14207, slot);
				break;
			case 14373:// extreme attk flsks
				drinkExtremePotion(itemId, 14371, slot, 0, false);
				break;
			case 14371:
				drinkExtremePotion(itemId, 14369, slot, 0, false);
				break;
			case 14369:
				drinkExtremePotion(itemId, 14367, slot, 0, false);
				break;
			case 14367:
				drinkExtremePotion(itemId, 14365, slot, 0, false);
				break;
			case 14365:
				drinkExtremePotion(itemId, 14363, slot, 0, false);
				break;
			case 14363:
				drinkExtremePotion(itemId, 14207, slot, 0, false);
				break;
			case 14361:// extreme str flsks
				drinkExtremePotion(itemId, 14359, slot, 2, false);
				break;
			case 14359:
				drinkExtremePotion(itemId, 14357, slot, 2, false);
				break;
			case 14357:
				drinkExtremePotion(itemId, 14355, slot, 2, false);
				break;
			case 14355:
				drinkExtremePotion(itemId, 14353, slot, 2, false);
				break;
			case 14353:
				drinkExtremePotion(itemId, 14351, slot, 2, false);
				break;
			case 14351:
				drinkExtremePotion(itemId, 14207, slot, 2, false);
				break;
			case 14349:// extreme def flsks
				drinkExtremePotion(itemId, 14347, slot, 1, false);
				break;
			case 14347:
				drinkExtremePotion(itemId, 14345, slot, 1, false);
				break;
			case 14345:
				drinkExtremePotion(itemId, 14343, slot, 1, false);
				break;
			case 14343:
				drinkExtremePotion(itemId, 14341, slot, 1, false);
				break;
			case 14341:
				drinkExtremePotion(itemId, 14339, slot, 1, false);
				break;
			case 14339:
				drinkExtremePotion(itemId, 14207, slot, 1, false);
				break;
			case 14337:// extreme magic flsks
				drinkExtremePotion(itemId, 14335, slot, 6, false);
				break;
			case 14335:
				drinkExtremePotion(itemId, 14333, slot, 6, false);
				break;
			case 14333:
				drinkExtremePotion(itemId, 14331, slot, 6, false);
				break;
			case 14331:
				drinkExtremePotion(itemId, 14329, slot, 6, false);
				break;
			case 14329:
				drinkExtremePotion(itemId, 14327, slot, 6, false);
				break;
			case 14327:
				drinkExtremePotion(itemId, 14207, slot, 6, false);
				break;
			case 14325:// extreme range flsks
				drinkExtremePotion(itemId, 14323, slot, 4, false);
				break;
			case 14323:
				drinkExtremePotion(itemId, 14321, slot, 4, false);
				break;
			case 14321:
				drinkExtremePotion(itemId, 14319, slot, 4, false);
				break;
			case 14319:
				drinkExtremePotion(itemId, 14317, slot, 4, false);
				break;
			case 14317:
				drinkExtremePotion(itemId, 14315, slot, 4, false);
				break;
			case 14315:
				drinkExtremePotion(itemId, 14207, slot, 4, false);
				break;
			case 14313: // Super Prayer
				drinkExtremePrayer(itemId, 14311, slot, true);
				break;
			case 14311:
				drinkExtremePrayer(itemId, 14309, slot, true);
				break;
			case 14309:
				drinkExtremePrayer(itemId, 14307, slot, true);
				break;
			case 14307:
				drinkExtremePrayer(itemId, 14305, slot, true);
				break;
			case 14305:
				drinkExtremePrayer(itemId, 14303, slot, true);
				break;
			case 14303:
				drinkExtremePrayer(itemId, 229, slot, true);
				break;
			case 14301: // ovl flask
				doOverload(itemId, 14299, slot);
				break;
			case 14299:
				doOverload(itemId, 14297, slot);
				break;
			case 14297:
				doOverload(itemId, 14295, slot);
				break;
			case 14295:
				doOverload(itemId, 14293, slot);
				break;
			case 14293:
				doOverload(itemId, 14291, slot);
				break;
			case 14291:
				doOverload(itemId, 14207, slot);
				break;
			case 6685: // brews
				doTheBrew(itemId, 6687, slot);
				break;
			case 6687:
				doTheBrew(itemId, 6689, slot);
				break;
			case 6689:
				doTheBrew(itemId, 6691, slot);
				break;
			case 6691:
				doTheBrew(itemId, 229, slot);
				break;

			case 15332:
				doOverload(itemId, 15333, slot);
				break;
			case 15333:
				doOverload(itemId, 15334, slot);
				break;
			case 15334:
				doOverload(itemId, 15335, slot);
				break;
			case 15335:
				doOverload(itemId, 229, slot);
			case 2436:
				drinkStatPotion(itemId, 145, slot, 0, true); // sup attack
				break;
			case 145:
				drinkStatPotion(itemId, 147, slot, 0, true);
				break;
			case 147:
				drinkStatPotion(itemId, 149, slot, 0, true);
				break;
			case 149:
				drinkStatPotion(itemId, 229, slot, 0, true);
				break;
			case 2440:
				drinkStatPotion(itemId, 157, slot, 2, true); // sup str
				break;
			case 157:
				drinkStatPotion(itemId, 159, slot, 2, true);
				break;
			case 159:
				drinkStatPotion(itemId, 161, slot, 2, true);
				break;
			case 161:
				drinkStatPotion(itemId, 229, slot, 2, true);
				break;
			case 2444:
				drinkStatPotion(itemId, 169, slot, 4, false); // range pot
				break;
			case 169:
				drinkStatPotion(itemId, 171, slot, 4, false);
				break;
			case 171:
				drinkStatPotion(itemId, 173, slot, 4, false);
				break;
			case 173:
				drinkStatPotion(itemId, 229, slot, 4, false);
				break;
			case 2432:
				drinkStatPotion(itemId, 133, slot, 1, false); // def pot
				break;
			case 133:
				drinkStatPotion(itemId, 135, slot, 1, false);
				break;
			case 135:
				drinkStatPotion(itemId, 137, slot, 1, false);
				break;
			case 137:
				drinkStatPotion(itemId, 229, slot, 1, false);
				break;
			case 113:
				drinkStatPotion(itemId, 115, slot, 2, false); // str pot
				break;
			case 115:
				drinkStatPotion(itemId, 117, slot, 2, false);
				break;
			case 117:
				drinkStatPotion(itemId, 119, slot, 2, false);
				break;
			case 119:
				drinkStatPotion(itemId, 229, slot, 2, false);
				break;
			case 2428:
				drinkStatPotion(itemId, 121, slot, 0, false); // attack pot
				break;
			case 121:
				drinkStatPotion(itemId, 123, slot, 0, false);
				break;
			case 123:
				drinkStatPotion(itemId, 125, slot, 0, false);
				break;
			case 125:
				drinkStatPotion(itemId, 229, slot, 0, false);
				break;
			case 2442:
				drinkStatPotion(itemId, 163, slot, 1, true); // super def pot
				break;
			case 163:
				drinkStatPotion(itemId, 165, slot, 1, true);
				break;
			case 165:
				drinkStatPotion(itemId, 167, slot, 1, true);
				break;
			case 167:
				drinkStatPotion(itemId, 229, slot, 1, true);
				break;
			case 3040: // magic pot
				drinkStatPotion(itemId, 3042, slot, 6, false);
				break;
			case 3042:
				drinkStatPotion(itemId, 3044, slot, 6, false);
				break;
			case 3044:
				drinkStatPotion(itemId, 3046, slot, 6, false);
				break;
			case 3046:
				drinkStatPotion(itemId, 229, slot, 6, false);
				break;
			case 3024:
				drinkPrayerPot(itemId, 3026, slot, true); // sup restore
				break;
			case 3026:
				drinkPrayerPot(itemId, 3028, slot, true);
				break;
			case 3028:
				drinkPrayerPot(itemId, 3030, slot, true);
				break;
			case 3030:
				drinkPrayerPot(itemId, 229, slot, true);
				break;
			case 10925:
				drinkPrayerPot(itemId, 10927, slot, true); // sanfew serums
				curePoison(300000);
				break;
			case 10927:
				drinkPrayerPot(itemId, 10929, slot, true);
				curePoison(300000);
				break;
			case 10929:
				drinkPrayerPot(itemId, 10931, slot, true);
				curePoison(300000);
				break;
			case 10931:
				drinkPrayerPot(itemId, 229, slot, true);
				curePoison(300000);
				break;
			case 2434:
				drinkPrayerPot(itemId, 139, slot, false); // pray pot
				break;
			case 139:
				drinkPrayerPot(itemId, 141, slot, false);
				break;
			case 141:
				drinkPrayerPot(itemId, 143, slot, false);
				break;
			case 143:
				drinkPrayerPot(itemId, 229, slot, false);
				break;
			case 2446:
				drinkAntiPoison(itemId, 175, slot, 30000); // anti poisons
				break;
			case 175:
				drinkAntiPoison(itemId, 177, slot, 30000);
				break;
			case 177:
				drinkAntiPoison(itemId, 179, slot, 30000);
				break;
			case 179:
				drinkAntiPoison(itemId, 229, slot, 30000);
				break;
			case 2448:
				drinkAntiPoison(itemId, 181, slot, 300000); // anti poisons
				break;
			case 181:
				drinkAntiPoison(itemId, 183, slot, 300000);
				break;
			case 183:
				drinkAntiPoison(itemId, 185, slot, 300000);
				break;
			case 185:
				drinkAntiPoison(itemId, 229, slot, 300000);
				break;
			case 15312: // Extreme Strength
				drinkExtremePotion(itemId, 15313, slot, 2, false);
				break;
			case 15313: // Extreme Strength
				drinkExtremePotion(itemId, 15314, slot, 2, false);
				break;
			case 15314: // Extreme Strength
				drinkExtremePotion(itemId, 15315, slot, 2, false);
				break;
			case 15315: // Extreme Strength
				drinkExtremePotion(itemId, 229, slot, 2, false);
				break;
			case 15308: // Extreme Attack
				drinkExtremePotion(itemId, 15309, slot, 0, false);
				break;
			case 15309: // Extreme Attack
				drinkExtremePotion(itemId, 15310, slot, 0, false);
				break;
			case 15310: // Extreme Attack
				drinkExtremePotion(itemId, 15311, slot, 0, false);
				break;
			case 15311: // Extreme Attack
				drinkExtremePotion(itemId, 229, slot, 0, false);
				break;
			case 15316: // Extreme Defense
				drinkExtremePotion(itemId, 15317, slot, 1, false);
				break;
			case 15317: // Extreme Defense
				drinkExtremePotion(itemId, 15318, slot, 1, false);
				break;
			case 15318: // Extreme Defense
				drinkExtremePotion(itemId, 15319, slot, 1, false);
				break;
			case 15319: // Extreme Defense
				drinkExtremePotion(itemId, 229, slot, 1, false);
				break;
			case 15324: // Extreme Ranging
				drinkExtremePotion(itemId, 15325, slot, 4, false);
				break;
			case 15325: // Extreme Ranging
				drinkExtremePotion(itemId, 15326, slot, 4, false);
				break;
			case 15326: // Extreme Ranging
				drinkExtremePotion(itemId, 15327, slot, 4, false);
				break;
			case 15327: // Extreme Ranging
				drinkExtremePotion(itemId, 229, slot, 4, false);
				break;
			case 15320: // Extreme Magic
				drinkExtremePotion(itemId, 15321, slot, 6, false);
				break;
			case 15321: // Extreme Magic
				drinkExtremePotion(itemId, 15322, slot, 6, false);
				break;
			case 15322: // Extreme Magic
				drinkExtremePotion(itemId, 15323, slot, 6, false);
				break;
			case 15323: // Extreme Magic
				drinkExtremePotion(itemId, 229, slot, 6, false);
				break;
			case 15328: // Super Prayer
				drinkExtremePrayer(itemId, 15329, slot, true);
				break;
			case 15329: // Super Prayer
				drinkExtremePrayer(itemId, 15330, slot, true);
				break;
			case 15330: // Super Prayer
				drinkExtremePrayer(itemId, 15331, slot, true);
				break;
			case 15331: // Super Prayer
				drinkExtremePrayer(itemId, 229, slot, true);
				break;
			case 15300: // Recover Special
				recoverSpecial(itemId, 15301, slot);
				break;
			case 15301: // Recover Special
				recoverSpecial(itemId, 15302, slot);
				break;
			case 15302: // Recover Special
				recoverSpecial(itemId, 15303, slot);
				break;
			case 15303: // Recover Special
				recoverSpecial(itemId, 229, slot);
				break;
			}
		}
	}

	public void recoverSpecial(int itemId, int replaceItem, int slot) {
		if (c.inWild()) {
			c.sendMessage("You are unable to restore special in the wilderness.");
			return;
		} else if (c.specAmount >= 7.5) {
			c.sendMessage("You are unable to drink the potion as your special is above 75%.");
		} else {
			if (System.currentTimeMillis() - c.restoreDelay >= 30000) {
				c.specAmount += 2.5;
				c.getItems().addSpecialBar(
						c.playerEquipment[Player.playerWeapon]);
				c.startAnimation(829);
				c.sendMessage("As you drink the potion you feel your special attack slightly regenerate.");
				c.playerItems[slot] = replaceItem + 1;
				c.getItems().resetItems(3214);
				c.getItems().updateSpecialBar();
				c.restoreDelay = System.currentTimeMillis();
			} else {
				c.sendMessage("You can only restore your special once every 30 seconds.");
			}
		}
	}

	public void drinkExtremePotion(int itemId, int replaceItem, int slot,
			int stat, boolean sup) {
		c.startAnimation(829);
		c.playerItems[slot] = replaceItem + 1;
		c.getItems().resetItems(3214);
		enchanceStat2(stat, sup);
	}

	public void drinkExtremePrayer(int itemId, int replaceItem, int slot,
			boolean rest) {
		c.startAnimation(829);
		c.playerItems[slot] = replaceItem + 1;
		c.getItems().resetItems(3214);
		c.playerLevel[5] += (c.getLevelForXP(c.playerXP[5]) * .38);
		if (rest)
			c.playerLevel[5] += 1;
		if (c.playerLevel[5] > c.getLevelForXP(c.playerXP[5]))
			c.playerLevel[5] = c.getLevelForXP(c.playerXP[5]);
		c.getPA().refreshSkill(5);
		if (rest)
			restoreStats();
	}

	public void enchanceStat2(int skillID, boolean sup) {
		c.playerLevel[skillID] += getExtremeStat(skillID, sup);
		c.getPA().refreshSkill(skillID);
	}

	public int getExtremeStat(int skill, boolean sup) {
		int increaseBy = 0;
		if (sup)
			increaseBy = (int) (c.getLevelForXP(c.playerXP[skill]) * .25);
		else
			increaseBy = (int) (c.getLevelForXP(c.playerXP[skill]) * .25) + 1;
		if (c.playerLevel[skill] + increaseBy > c
				.getLevelForXP(c.playerXP[skill]) + increaseBy + 1) {
			return c.getLevelForXP(c.playerXP[skill]) + increaseBy
					- c.playerLevel[skill];
		}
		return increaseBy;
	}

	public void drinkAntiPoison(int itemId, int replaceItem, int slot,
			long delay) {
		c.startAnimation(829);
		c.playerItems[slot] = replaceItem + 1;
		c.getItems().resetItems(3214);
		curePoison(delay);
	}

	public void doOverload(int itemId, int replaceItem, int slot) {
		c.startAnimation(829);
		c.playerItems[slot] = replaceItem + 1;
		c.getItems().resetItems(3214);
		c.overloadcounter = 5;
		// c.overloadcounter();
		int[] toIncrease = { 0, 1, 2, 4, 6 };
		c.playerLevel[0] += getBrewStat(0, .27);
		if (c.playerLevel[0] > (c.getLevelForXP(c.playerXP[0]) * 1.27 + 1)) {
			c.playerLevel[0] = (int) (c.getLevelForXP(c.playerXP[0]) * 1.27);
		}
		c.playerLevel[1] += getBrewStat(1, .27);
		if (c.playerLevel[1] > (c.getLevelForXP(c.playerXP[1]) * 1.27 + 1)) {
			c.playerLevel[1] = (int) (c.getLevelForXP(c.playerXP[1]) * 1.27);
		}
		c.playerLevel[2] += getBrewStat(2, .27);
		if (c.playerLevel[2] > (c.getLevelForXP(c.playerXP[2]) * 1.27 + 1)) {
			c.playerLevel[2] = (int) (c.getLevelForXP(c.playerXP[2]) * 1.27);
		}
		c.playerLevel[4] += getBrewStat(4, .237);
		if (c.playerLevel[4] > (c.getLevelForXP(c.playerXP[4]) * 1.237 + 1)) {
			c.playerLevel[4] = (int) (c.getLevelForXP(c.playerXP[4]) * 1.237);
		}
		c.playerLevel[6] += getBrewStat(6, .237);
		if (c.playerLevel[6] > (c.getLevelForXP(c.playerXP[6]) * 1.237 + 1)) {
			c.playerLevel[6] = (int) (c.getLevelForXP(c.playerXP[6]) * 1.237);
		}
		c.getPA().refreshSkill(0);
		c.getPA().refreshSkill(1);
		c.getPA().refreshSkill(2);
		c.getPA().refreshSkill(4);
		c.getPA().refreshSkill(6);
	}

	public void curePoison(long delay) {
		c.poisonDamage = 0;
		c.poisonImmune = delay;
		c.lastPoisonSip = System.currentTimeMillis();
	}

	public void drinkStatPotion(int itemId, int replaceItem, int slot,
			int stat, boolean sup) {
		c.startAnimation(829);
		c.playerItems[slot] = replaceItem + 1;
		c.getItems().resetItems(3214);
		enchanceStat(stat, sup);
	}

	public void drinkPrayerPot(int itemId, int replaceItem, int slot,
			boolean rest) {
		c.startAnimation(829);
		c.playerItems[slot] = replaceItem + 1;
		c.getItems().resetItems(3214);
		c.playerLevel[5] += (c.getLevelForXP(c.playerXP[5]) * .33);
		if (rest)
			c.playerLevel[5] += 1;
		if (c.playerLevel[5] > c.getLevelForXP(c.playerXP[5]))
			c.playerLevel[5] = c.getLevelForXP(c.playerXP[5]);
		c.getPA().refreshSkill(5);
		if (rest)
			restoreStats();
	}

	public void restoreStats() {
		for (int j = 0; j <= 6; j++) {
			if (j == 5 || j == 3)
				continue;
			if (c.playerLevel[j] < c.getLevelForXP(c.playerXP[j])) {
				c.playerLevel[j] += (c.getLevelForXP(c.playerXP[j]) * .33);
				if (c.playerLevel[j] > c.getLevelForXP(c.playerXP[j])) {
					c.playerLevel[j] = c.getLevelForXP(c.playerXP[j]);
				}
				c.getPA().refreshSkill(j);
				c.getPA().setSkillLevel(j, c.playerLevel[j], c.playerXP[j]);
			}
		}
	}

	public void doTheBrew(int itemId, int replaceItem, int slot) {
		if (c.duelRule[6]) {
			c.sendMessage("You may not eat in this duel.");
			return;
		}
		c.startAnimation(829);
		c.playerItems[slot] = replaceItem + 1;
		c.getItems().resetItems(3214);
		int[] toDecrease = { 0, 2, 4, 6 };

		for (int tD : toDecrease) {
			c.playerLevel[tD] -= getBrewStat(tD, .10);
			if (c.playerLevel[tD] < 0)
				c.playerLevel[tD] = 1;
			c.getPA().refreshSkill(tD);
			c.getPA().setSkillLevel(tD, c.playerLevel[tD], c.playerXP[tD]);
		}
		c.playerLevel[1] += getBrewStat(1, .20);
		if (c.playerLevel[1] > (c.getLevelForXP(c.playerXP[1]) * 1.2 + 1)) {
			c.playerLevel[1] = (int) (c.getLevelForXP(c.playerXP[1]) * 1.2);
		}
		c.getPA().refreshSkill(1);

		c.playerLevel[3] += getBrewStat(3, .15);
		if (c.playerLevel[3] > (c.calculateMaxLifePoints() * 1.17 + 1)) {
			c.playerLevel[3] = (int) (c.calculateMaxLifePoints() * 1.17);
		}
		c.getPA().refreshSkill(3);
	}

	public void enchanceStat(int skillID, boolean sup) {
		c.playerLevel[skillID] += getBoostedStat(skillID, sup);
		c.getPA().refreshSkill(skillID);
	}

	public int getBrewStat(int skill, double amount) {
		return (int) (c.getLevelForXP(c.playerXP[skill]) * amount);
	}

	public int getBoostedStat(int skill, boolean sup) {
		int increaseBy = 0;
		if (sup)
			increaseBy = (int) (c.getLevelForXP(c.playerXP[skill]) * .20);
		else
			increaseBy = (int) (c.getLevelForXP(c.playerXP[skill]) * .13) + 1;
		if (c.playerLevel[skill] + increaseBy > c
				.getLevelForXP(c.playerXP[skill]) + increaseBy + 1) {
			return c.getLevelForXP(c.playerXP[skill]) + increaseBy
					- c.playerLevel[skill];
		}
		return increaseBy;
	}

	public boolean isPotion(int itemId) {
		String name = c.getItems().getItemName(itemId);
		return name.contains("(6)") || name.contains("(5)")
				|| name.contains("(4)") || name.contains("(3)")
				|| name.contains("(2)") || name.contains("(1)");
	}
}