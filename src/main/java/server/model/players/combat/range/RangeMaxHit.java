package server.model.players.combat.range;

import server.model.players.Client;
import server.model.players.Player;

public class RangeMaxHit extends RangeData {

	public static int calculateRangeDefence(Client c) {
		int defenceLevel = c.playerLevel[1];
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
		return (defenceLevel + c.playerBonus[9] + (c.playerBonus[9] / 2));
	}

	public static int calculateRangeAttack(Client c) {
		int rangeLevel = c.playerLevel[4];
		rangeLevel *= c.specAccuracy;
		if (c.fullVoidRange()) {
			rangeLevel += c.getLevelForXP(c.playerXP[c.playerRanged]) * 0.10;
		}
		if (c.prayerActive[3]) {
			rangeLevel *= 1.05;
		} else if (c.prayerActive[11]) {
			rangeLevel *= 1.10;
		} else if (c.prayerActive[19]) {
			rangeLevel *= 1.15;
		}
		if (c.fullVoidRange() && c.specAccuracy > 1.15) {
			rangeLevel *= 1.75;
		}
		if (Player.playerWeapon == 16887) {
			rangeLevel *= 1.20;
		}
		if (Player.playerWeapon == 17295) {
			rangeLevel *= 1.20;
		}
		return (int) (rangeLevel + (c.playerBonus[4] * 3.95));
	}

	public static int maxHit(Client c) {
		int a = c.playerLevel[4];
		int d = getRangeStr(c.usingBow ? c.lastArrowUsed : c.lastWeaponUsed);
		double b = 1.00;
		if (c.prayerActive[3]) {
			b *= 1.05;
		} else if (c.prayerActive[11]) {
			b *= 1.10;
		} else if (c.prayerActive[19]) {
			b *= 1.15;
		}
		if (c.fullVoidRange()) {
			b *= 1.20;
		}
		double e = Math.floor(a * b);
		if (c.fightMode == 0) {
			e = (e + 4.2);
		}
		double darkbow = 1.0;
		if (c.usingSpecial) {
			if (c.playerEquipment[3] == 11235 || c.playerEquipment[3] == 15701
					|| c.playerEquipment[3] == 15702
					|| c.playerEquipment[3] == 15703
					|| c.playerEquipment[3] == 15704) {
				if (c.lastArrowUsed == 11212) {
					darkbow = 1.52;
				} else {
					darkbow = 1.32;
				}
			}
		}
		double max = (1.4 + e / 10 + d / 80 + e * d / 640) * darkbow;
		return (int) max;
	}
}