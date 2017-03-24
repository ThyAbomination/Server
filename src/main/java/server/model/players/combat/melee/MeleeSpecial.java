package server.model.players.combat.melee;

import server.model.npcs.NPCHandler;
import server.model.players.Client;
import server.model.players.Player;
import server.model.players.PlayerHandler;
import server.util.Misc;

/**
 * 
 * Weapon Specials
 * 
 * Class MeleeData
 * 
 * @author 2012
 * 
 */

public class MeleeSpecial {

	public static boolean checkSpecAmount(Client c, int weapon) {
		switch (weapon) {
		case 1249:
		case 1215:
		case 1231:
		case 5680:
		case 5698:
		case 1305:
		case 1434:
		case 13899:
		case 13976:
		case 13982:
		case 13985:
		case 13923:
		case 13925:
			if (c.playerEquipment[c.playerRing] == 19669
					&& c.specAmount >= 2.5 * .8) {// ring of vigour
				c.specAmount -= 2.5 * .8;
				c.getItems().addSpecialBar(weapon);
				return true;
			} else if (c.specAmount >= 2.5) {
				c.specAmount -= 2.5;
				c.getItems().addSpecialBar(weapon);
				return true;
			}
			return false;

		case 4151:
		case 15441:
		case 15442:
		case 15443:
		case 15444:
		case 11694:
		case 14484:
		case 11698:
		case 4153:
		case 13905:
		case 13929:
		case 13931:
		case 15016:
		case 15015:
		case 10887:
		case 15241:
			if (c.playerEquipment[c.playerRing] == 19669
					&& c.specAmount >= 5 * .8) {// ring of vigour
				c.specAmount -= 5 * .8;
				c.getItems().addSpecialBar(weapon);
				return true;
			} else if (c.specAmount >= 5) {
				c.specAmount -= 5;
				c.getItems().addSpecialBar(weapon);
				return true;
			}
			return false;

		case 3204:
			if (c.playerEquipment[c.playerRing] == 19669
					&& c.specAmount >= 3 * .8) {// ring of vigour
				c.specAmount -= 3 * .8;
				c.getItems().addSpecialBar(weapon);
				return true;
			} else if (c.specAmount >= 3) {
				c.specAmount -= 3;
				c.getItems().addSpecialBar(weapon);
				return true;
			}
			return false;

		case 13902: // Statius warhammer
		case 13926: // corrupt
		case 13928: // deg corrupt
			if (c.playerEquipment[c.playerRing] == 19669
					&& c.specAmount >= 3.5 * .8) {// ring of vigour
				c.specAmount -= 3.5 * .8;
				c.getItems().addSpecialBar(weapon);
				return true;
			} else if (c.specAmount >= 3.5) {
				c.specAmount -= 3.5;
				c.getItems().addSpecialBar(weapon);
				return true;
			}
			return false;

		case 1377:
		case 11696:
		case 11730:
		case 13973:
		case 15486:
			if (c.playerEquipment[c.playerRing] == 19669
					&& c.specAmount >= 10 * .8) {// ring of vigour
				c.specAmount -= 10 * .8;
				c.getItems().addSpecialBar(weapon);
				return true;
			} else if (c.specAmount >= 10) {
				c.specAmount -= 10;
				c.getItems().addSpecialBar(weapon);
				return true;
			}
			return false;

		case 4587:
		case 859:
		case 861:
		case 11235:
		case 15701:
		case 15702:
		case 15703:
		case 15704:
		case 13979:
			if (c.playerEquipment[c.playerRing] == 19669
					&& c.specAmount >= 5.5 * .8) {// ring of vigour
				c.specAmount -= 5.5 * .8;
				c.getItems().addSpecialBar(weapon);
				return true;
			} else if (c.specAmount >= 5.5) {
				c.specAmount -= 5.5;
				c.getItems().addSpecialBar(weapon);
				return true;
			}
			return false;

		case 11700:
		case 19780:
			if (c.playerEquipment[c.playerRing] == 19669
					&& c.specAmount >= 6.0 * .8) {// ring of vigour
				c.specAmount -= 6.0 * .8;
				c.getItems().addSpecialBar(weapon);
				return true;
			} else if (c.specAmount >= 6.0) {
				c.specAmount -= 6.0;
				c.getItems().addSpecialBar(weapon);
				return true;
			}
			return false;

		default:
			return true; // incase u want to test a weapon
		}
	}

	public static void activateSpecial(Client c, int weapon, int i) {
		if (NPCHandler.npcs[i] == null && c.npcIndex > 0) {
			return;
		}
		if (PlayerHandler.players[i] == null && c.playerIndex > 0) {
			return;
		}
		c.isUsingSpecial = true;
		c.doubleHit = false;
		c.specEffect = 0;
		c.projectileStage = 0;
		c.specMaxHitIncrease = 2;
		if (!c.specEvent && c.specAmount < 10) {
			c.specialRestoreEvent(c);
		}
		if (c.npcIndex > 0) {
			c.oldNpcIndex = i;
		} else if (c.playerIndex > 0) {
			c.oldPlayerIndex = i;
			PlayerHandler.players[i].underAttackBy = c.playerId;
			PlayerHandler.players[i].logoutDelay = System.currentTimeMillis();
			PlayerHandler.players[i].singleCombatDelay = System
					.currentTimeMillis();
			PlayerHandler.players[i].killerId = c.playerId;
		}
		if (c.playerIndex > 0) {
			c.getPA().followPlayer();
		} else if (c.npcIndex > 0) {
			c.getPA().followNpc();
		}
		switch (weapon) {
		case 10887:
			c.gfx0(1027);
			c.startAnimation(5870);
			c.hitDelay = c
					.getCombat()
					.getHitDelay(
							i,
							c.getItems()
									.getItemName(
											c.playerEquipment[Player.playerWeapon])
									.toLowerCase());
			c.specDamage = 1.20;
			c.specAccuracy = 5.0;
			break;

		case 1305: // dragon long
		case 13982:
			c.gfx100(248);
			c.startAnimation(1058);
			c.hitDelay = c
					.getCombat()
					.getHitDelay(
							i,
							c.getItems()
									.getItemName(
											c.playerEquipment[Player.playerWeapon])
									.toLowerCase());
			c.specAccuracy = 5.0;
			c.specDamage = 1.25;
			break;

		case 1215: // dragon daggers
		case 1231:
		case 5680:
		case 5698:
		case 13976:
			c.gfx100(252);
			c.startAnimation(1062);
			c.hitDelay = c
					.getCombat()
					.getHitDelay(
							i,
							c.getItems()
									.getItemName(
											c.playerEquipment[Player.playerWeapon])
									.toLowerCase());
			c.doubleHit = true;
			c.specAccuracy = 2.5;
			c.specDamage = 1.15;
			break;

		case 11730: // Sara sword
			c.gfx100(1224);
			c.startAnimation(7072);
			c.hitDelay = c
					.getCombat()
					.getHitDelay(
							i,
							c.getItems()
									.getItemName(
											c.playerEquipment[Player.playerWeapon])
									.toLowerCase());
			c.doubleHit = true;
			c.ssSpec = true;
			c.specAccuracy = 5.0;
			c.specEffect = 6;
			break;

		case 14484: // Dragon claws
			c.startAnimation(10961);
			c.gfx0(1950);
			c.specAccuracy = 15.0;
			c.usingClaws = true;
			c.doubleHit = true;
			c.hitDelay = c
					.getCombat()
					.getHitDelay(
							i,
							c.getItems()
									.getItemName(
											c.playerEquipment[Player.playerWeapon])
									.toLowerCase());
			break;

		case 4151: // whip
		case 15441:
		case 15442:
		case 15443:
		case 15444:
			if (NPCHandler.npcs[i] != null) {
				NPCHandler.npcs[i].gfx100(341);
			}
			c.specAccuracy = 5.0;
			c.startAnimation(1658);
			c.hitDelay = c
					.getCombat()
					.getHitDelay(
							i,
							c.getItems()
									.getItemName(
											c.playerEquipment[Player.playerWeapon])
									.toLowerCase());
			c.specEffect = 5;
			break;

		case 13899: // vls
		case 13923: // corrupt
		case 13925: // deg corrupt
			c.startAnimation(10502);
			c.hitDelay = c
					.getCombat()
					.getHitDelay(
							i,
							c.getItems()
									.getItemName(
											c.playerEquipment[Player.playerWeapon])
									.toLowerCase() + 1);
			c.specDamage = 1.2;
			c.specAccuracy = 9999;
			break;

		case 13905: // Vesta spear
		case 13929: // corrupt
		case 13931: // deg corrupt
			c.startAnimation(10499);
			c.gfx0(1835);
			c.specAccuracy = 10.0;
			c.hitDelay = c
					.getCombat()
					.getHitDelay(
							i,
							c.getItems()
									.getItemName(
											c.playerEquipment[Player.playerWeapon])
									.toLowerCase());
			break;

		case 13902: // Statius warhammer
		case 13926: // corrupt
		case 13928: // deg corrupt
			c.startAnimation(10505);
			c.gfx0(1840);
			c.hitDelay = c
					.getCombat()
					.getHitDelay(
							i,
							c.getItems()
									.getItemName(
											c.playerEquipment[Player.playerWeapon])
									.toLowerCase() + 1);
			c.specDamage = 1.25;
			c.specAccuracy = 15.0;
			c.specEffect = 7;
			break;

		case 11694: // ags
			c.startAnimation(7074);
			c.gfx0(1222);
			c.specDamage = 1.25;
			c.specAccuracy = 25.0;
			c.hitDelay = c
					.getCombat()
					.getHitDelay(
							i,
							c.getItems()
									.getItemName(
											c.playerEquipment[Player.playerWeapon])
									.toLowerCase());
			break;

		case 11700: // zgs
			c.startAnimation(7070);
			c.gfx0(1221);
			c.specAccuracy = 20.0;
			c.specDamage = 1.1;
			c.hitDelay = c
					.getCombat()
					.getHitDelay(
							i,
							c.getItems()
									.getItemName(
											c.playerEquipment[Player.playerWeapon])
									.toLowerCase());
			c.specEffect = 2;
			break;

		case 11696:// bgs
			c.startAnimation(7073);
			c.gfx0(1223);
			c.specDamage = 1.22;
			c.specAccuracy = 20.0;
			c.hitDelay = c
					.getCombat()
					.getHitDelay(
							i,
							c.getItems()
									.getItemName(
											c.playerEquipment[Player.playerWeapon])
									.toLowerCase());
			c.specEffect = 3;
			break;

		case 11698:// sgs
			c.startAnimation(7071);
			c.gfx0(1220);
			c.specAccuracy = 20.0;
			c.specDamage = 1.1;
			c.specEffect = 4;
			c.hitDelay = c
					.getCombat()
					.getHitDelay(
							i,
							c.getItems()
									.getItemName(
											c.playerEquipment[Player.playerWeapon])
									.toLowerCase());
			break;

		case 1249: // d spear
		case 13988:
			c.startAnimation(405);
			c.gfx100(253);
			if (c.playerIndex > 0) {
				Client o = (Client) PlayerHandler.players[i];
				o.getPA().getSpeared(c.absX, c.absY);
			}
			break;

		case 3204: // d hally
			c.gfx100(282);
			c.startAnimation(1203);
			c.hitDelay = c
					.getCombat()
					.getHitDelay(
							i,
							c.getItems()
									.getItemName(
											c.playerEquipment[Player.playerWeapon])
									.toLowerCase());
			if (NPCHandler.npcs[i] != null && c.npcIndex > 0) {
				if (!c.goodDistance(c.getX(), c.getY(),
						NPCHandler.npcs[i].getX(), NPCHandler.npcs[i].getY(), 1)) {
					c.doubleHit = true;
				}
			}
			if (PlayerHandler.players[i] != null && c.playerIndex > 0) {
				if (!c.goodDistance(c.getX(), c.getY(),
						PlayerHandler.players[i].getX(),
						PlayerHandler.players[i].getY(), 1)) {
					c.doubleHit = true;
					c.delayedDamage2 = Misc.random(c.getCombat()
							.calculateMeleeMaxHit());
				}
			}
			break;

		case 4153: // maul
			c.startAnimation(1667);
			c.hitDelay = c
					.getCombat()
					.getHitDelay(
							i,
							c.getItems()
									.getItemName(
											c.playerEquipment[Player.playerWeapon])
									.toLowerCase());
			/*
			 * if (c.playerIndex > 0) gmaulPlayer(i); else gmaulNpc(i);
			 */
			c.gfx100(337);
			break;

		case 4587: // dscimmy
		case 13979:
			c.gfx100(347);
			c.specEffect = 1;
			c.startAnimation(1872);
			c.hitDelay = c
					.getCombat()
					.getHitDelay(
							i,
							c.getItems()
									.getItemName(
											c.playerEquipment[Player.playerWeapon])
									.toLowerCase());
			break;

		case 1434: // mace
		case 13985:
			c.startAnimation(1060);
			c.gfx100(251);
			c.specMaxHitIncrease = 3;
			c.hitDelay = c
					.getCombat()
					.getHitDelay(
							i,
							c.getItems()
									.getItemName(
											c.playerEquipment[Player.playerWeapon])
									.toLowerCase()) + 1;
			c.specDamage = 1.46;
			c.specAccuracy = 5.0;
			break;

		case 859: // magic long
			c.usingBow = true;
			c.bowSpecShot = 3;
			c.rangeItemUsed = c.playerEquipment[Player.playerArrows];
			c.getItems().deleteArrow();
			c.lastWeaponUsed = weapon;
			c.startAnimation(426);
			c.gfx100(250);
			c.hitDelay = c
					.getCombat()
					.getHitDelay(
							i,
							c.getItems()
									.getItemName(
											c.playerEquipment[Player.playerWeapon])
									.toLowerCase());
			c.projectileStage = 1;
			if (c.fightMode == 2)
				c.attackTimer--;
			break;

		case 861: // magic short
			c.usingBow = true;
			c.bowSpecShot = 1;
			c.rangeItemUsed = c.playerEquipment[Player.playerArrows];
			c.getItems().deleteArrow();
			c.getItems().deleteArrow();
			c.lastWeaponUsed = weapon;
			c.startAnimation(1074);
			c.hitDelay = 3;
			c.projectileStage = 1;
			c.hitDelay = c
					.getCombat()
					.getHitDelay(
							i,
							c.getItems()
									.getItemName(
											c.playerEquipment[Player.playerWeapon])
									.toLowerCase());
			if (c.fightMode == 2)
				c.attackTimer--;
			if (c.playerIndex > 0)
				c.getCombat().fireProjectilePlayer();
			else if (c.npcIndex > 0)
				c.getCombat().fireProjectileNpc();
			break;

		case 11235: // dark bow
		case 15701: // dark bow
		case 15702: // dark bow
		case 15703: // dark bow
		case 15704: // dark bow
			c.usingBow = true;
			c.dbowSpec = true;
			c.rangeItemUsed = c.playerEquipment[Player.playerArrows];
			c.getItems().deleteArrow();
			c.getItems().deleteArrow();
			if (c.playerIndex > 0) {
				c.getItems().dropArrowPlayer();
			} else if (c.npcIndex > 0) {
				c.getItems().dropArrowNpc();
			}
			c.lastWeaponUsed = weapon;
			c.hitDelay = 3;
			c.startAnimation(426);
			c.projectileStage = 1;
			c.gfx100(c.getCombat().getRangeStartGFX());
			c.hitDelay = c
					.getCombat()
					.getHitDelay(
							i,
							c.getItems()
									.getItemName(
											c.playerEquipment[Player.playerWeapon])
									.toLowerCase());
			if (c.fightMode == 2)
				c.attackTimer--;
			if (c.playerIndex > 0)
				c.getCombat().fireProjectilePlayer();
			else if (c.npcIndex > 0)
				c.getCombat().fireProjectileNpc();
			c.specAccuracy = 2.5;
			c.specDamage = 1.60;
			break;

		case 15241: // hand cannon
			c.usingBow = true;
			c.rangeItemUsed = c.playerEquipment[Player.playerArrows];
			c.lastWeaponUsed = weapon;
			c.startAnimation(12175);
			c.getCombat().handCannon();
			break;

		case 19780: // korasi
			c.startAnimation(4000);
			c.gfx0(1247);
			c.specDamage = 1.5;
			c.specAccuracy = 9999;
			c.hitDelay = c
					.getCombat()
					.getHitDelay(
							i,
							c.getItems()
									.getItemName(
											c.playerEquipment[Player.playerWeapon])
									.toLowerCase());
			c.specEffect = 8;
			break;
		}
		c.delayedDamage = Misc.random(c.getCombat().calculateMeleeMaxHit());
		c.delayedDamage2 = Misc.random(c.getCombat().calculateMeleeMaxHit());
		c.usingSpecial = false;
		c.getItems().updateSpecialBar();
	}
}