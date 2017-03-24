package server.model.players.combat.magic;

import server.model.players.Client;
import server.model.players.Player;

public class MagicMaxHit {

	public static int mageAttack(Client c) {
		int attackLevel = c.playerLevel[6];
		if (c.fullVoidMage()) {
			attackLevel += c.getLevelForXP(c.playerXP[6]) * 0.30;
		}
		if (c.prayerActive[4]) {
			attackLevel *= 1.05;
		} else if (c.prayerActive[12]) {
			attackLevel *= 1.10;
		} else if (c.prayerActive[20]) {
			attackLevel *= 1.15;
		}
		return (attackLevel + (c.playerBonus[3] * 2));
	}

	public static int mageDefefence(Client c) {
		int defenceLevel = c.playerLevel[1] / 2 + c.playerLevel[6] / 2;
		if (c.prayerActive[0]) {
			defenceLevel += c.getLevelForXP(c.playerXP[c.playerDefence]) * 0.05;
		} else if (c.prayerActive[3]) {
			defenceLevel += c.getLevelForXP(c.playerXP[c.playerDefence]) * 0.10;
		} else if (c.prayerActive[9]) {
			defenceLevel += c.getLevelForXP(c.playerXP[c.playerDefence]) * 0.15;
		} else if (c.prayerActive[18]) {
			defenceLevel += c.getLevelForXP(c.playerXP[c.playerDefence]) * 0.20;
		} else if (c.prayerActive[19]) {
			defenceLevel += c.getLevelForXP(c.playerXP[c.playerDefence]) * 0.25;
		}
		return (defenceLevel + c.playerBonus[8] + (c.playerBonus[8] / 3));
	}

	public static int magicMaxHit(Client c) {
		double damage = Player.MAGIC_SPELLS[c.oldSpellId][6];
		double damageMultiplier = 1;
		int level = c.playerLevel[c.playerMagic];
		if (level > c.getLevelForXP(c.playerXP[6])
				&& c.getLevelForXP(c.playerXP[6]) >= 95)
			damageMultiplier += .03 * ((level > 104 ? 104 : level) - 99);
		else
			damageMultiplier = 1;
		switch (c.playerEquipment[Player.playerWeapon]) {
		case 18371: // Gravite Staff
			damageMultiplier += .05;
			break;
		case 4675: // Ancient Staff
		case 4710: // Ahrim's Staff
		case 4862: // Ahrim's Staff
		case 4864: // Ahrim's Staff
		case 4865: // Ahrim's Staff
		case 6914: // Master Wand
		case 8841: // Void Knight Mace
		case 13867: // Zuriel's Staff
		case 13869: // Zuriel's Staff (Deg)
			damageMultiplier += .10;
			break;
		case 15486: // Staff of Light
			damageMultiplier += .15;
			break;
		case 18355: // Chaotic Staff
			damageMultiplier += .20;
			break;
		}
		switch (c.playerEquipment[c.playerAmulet]) {
		case 18333: // Arcane Pulse
			damageMultiplier += .05;
			break;
		case 18334:// Arcane Blast
			damageMultiplier += .10;
			break;
		case 18335:// Arcane Stream
			damageMultiplier += .15;
			break;
		}
		damage *= damageMultiplier;
		// c.sendMessage("Final damage: " + damage + " Damage Multiplier: "
		// + damageMultiplier);
		return (int) damage;
	}
}