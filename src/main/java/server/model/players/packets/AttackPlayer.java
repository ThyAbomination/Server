package server.model.players.packets;

import server.Config;
import server.model.players.Client;
import server.model.players.PacketType;
import server.model.players.Player;
import server.model.players.PlayerHandler;
import server.model.players.combat.magic.CastOnOther;

/**
 * Attack Player
 **/
public class AttackPlayer implements PacketType {

	public static final int ATTACK_PLAYER = 73, MAGE_PLAYER = 249;

	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		c.playerIndex = 0;
		c.npcIndex = 0;
		switch (packetType) {

		/**
		 * Attack player
		 **/
		case ATTACK_PLAYER:
			c.playerIndex = c.getInStream().readSignedWordBigEndian();
			if (PlayerHandler.players[c.playerIndex] == null) {
				break;
			}

			if (c.respawnTimer > 0) {
				break;
			}

			if (c.autocastId > 0)
				c.autocasting = true;

			if (!c.autocasting && c.spellId > 0) {
				c.spellId = 0;
			}
			c.mageFollow = false;
			c.spellId = 0;
			c.usingMagic = false;
			boolean usingBow = false;
			boolean usingOtherRangeWeapons = false;
			boolean usingArrows = false;
			boolean usingCross = c.playerEquipment[Player.playerWeapon] == 9185
					|| c.playerEquipment[Player.playerWeapon] == 18357;
			for (int bowId : c.BOWS) {
				if (c.playerEquipment[Player.playerWeapon] == bowId) {
					usingBow = true;
					for (int arrowId : c.ARROWS) {
						if (c.playerEquipment[Player.playerArrows] == arrowId) {
							usingArrows = true;
						}
					}
				}
			}
			for (int otherRangeId : c.OTHER_RANGE_WEAPONS) {
				if (c.playerEquipment[Player.playerWeapon] == otherRangeId) {
					usingOtherRangeWeapons = true;
				}
			}
			if (c.duelStatus == 5) {
				if (c.duelCount > 0) {
					c.sendMessage("The duel hasn't started yet!");
					c.playerIndex = 0;
					return;
				}
				if (c.duelRule[9]) {
					boolean canUseWeapon = false;
					for (int funWeapon : Config.FUN_WEAPONS) {
						if (c.playerEquipment[Player.playerWeapon] == funWeapon) {
							canUseWeapon = true;
						}
					}
					if (!canUseWeapon) {
						c.sendMessage("You can only use fun weapons in this duel!");
						return;
					}
				}

				if (c.duelRule[2] && (usingBow || usingOtherRangeWeapons)) {
					c.sendMessage("Range has been disabled in this duel!");
					return;
				}
				if (c.duelRule[3] && (!usingBow && !usingOtherRangeWeapons)) {
					c.sendMessage("Melee has been disabled in this duel!");
					return;
				}
			}

			if ((usingBow || c.autocasting)
					&& c.goodDistance(c.getX(), c.getY(),
							PlayerHandler.players[c.playerIndex].getX(),
							PlayerHandler.players[c.playerIndex].getY(), 7)) {
				c.usingBow = true;
				c.stopMovement();
			}

			if (usingOtherRangeWeapons
					&& c.goodDistance(c.getX(), c.getY(),
							PlayerHandler.players[c.playerIndex].getX(),
							PlayerHandler.players[c.playerIndex].getY(), 4)) {
				c.usingRangeWeapon = true;
				c.stopMovement();
			}
			if (!usingBow)
				c.usingBow = false;
			if (!usingOtherRangeWeapons)
				c.usingRangeWeapon = false;

			if (!usingCross && !usingArrows && usingBow
					&& c.playerEquipment[Player.playerWeapon] < 4212
					&& c.playerEquipment[Player.playerWeapon] > 4223) {
				c.sendMessage("You have run out of arrows!");
				return;
			}
			if (c.getCombat().correctBowAndArrows() < c.playerEquipment[Player.playerArrows]
					&& Config.CORRECT_ARROWS
					&& usingBow
					&& !c.getCombat().usingCrystalBow() && !usingCross) {
				c.sendMessage("You can't use "
						+ c.getItems()
								.getItemName(
										c.playerEquipment[Player.playerArrows])
								.toLowerCase()
						+ "s with a "
						+ c.getItems()
								.getItemName(
										c.playerEquipment[Player.playerWeapon])
								.toLowerCase() + ".");
				c.stopMovement();
				c.getCombat().resetPlayerAttack();
				return;
			}
			if (usingCross && !c.getCombat().properBolts()) {
				c.sendMessage("You must use bolts with a crossbow.");
				c.stopMovement();
				c.getCombat().resetPlayerAttack();
				return;
			}
			if (c.getCombat().checkReqs()) {
				c.followId = c.playerIndex;
				if (!c.usingMagic && !usingBow && !usingOtherRangeWeapons) {
					c.followDistance = 1;
					c.getPA().followPlayer();
				}
				if (c.attackTimer <= 0) {
					// c.sendMessage("Tried to attack...");
					// c.getCombat().attackPlayer(c.playerIndex);
					// c.attackTimer++;
				}
			}
			break;

		/**
		 * Attack player with magic
		 **/
		/**
		 * Attack player with magic
		 **/
		case MAGE_PLAYER:
			if (!c.mageAllowed) {
				c.mageAllowed = true;
				break;
			}

			c.playerIndex = c.getInStream().readSignedWordA();
			c.castingSpellId = c.getInStream().readSignedWordBigEndian();
			c.usingMagic = false;

			boolean teleOther = CastOnOther.castOnOtherSpells(c);

			if (PlayerHandler.players[c.playerIndex] == null) {
				break;
			}

			if (c.respawnTimer > 0) {
				break;
			}

			switch (c.castingSpellId) {
			/*
			 * case 12425: CastOnOther.teleOtherDistance(c, 0, c.playerIndex);
			 * break; case 12435: CastOnOther.teleOtherDistance(c, 1,
			 * c.playerIndex); break; case 12455:
			 * CastOnOther.teleOtherDistance(c, 2, c.playerIndex); break; case
			 * 30298: CastOnOther.castOtherVengeance(c, c.playerIndex); break;
			 */
			}

			if (teleOther) {
				c.stopMovement();
				c.getCombat().resetPlayerAttack();
				c.faceUpdate(c.playerIndex + 32768);
				if (c.inTrade) {
					c.getTradeAndDuel().declineTrade(true);
				}
			}

			for (int i = 0; i < Player.MAGIC_SPELLS.length; i++) {
				if (c.castingSpellId == Player.MAGIC_SPELLS[i][0]) {
					c.spellId = i;
					c.usingMagic = true;
					break;
				}
			}

			if (c.autocasting) {
				c.autocasting = false;
			}

			if (!teleOther) {
				if (!c.getCombat().checkReqs()) {
					break;
				}

				for (int r = 0; r < c.REDUCE_SPELLS.length; r++) { // reducing
																	// spells,
																	// confuse
																	// etc
					if (PlayerHandler.players[c.playerIndex].REDUCE_SPELLS[r] == Player.MAGIC_SPELLS[c.spellId][0]) {
						if ((System.currentTimeMillis() - PlayerHandler.players[c.playerIndex].reduceSpellDelay[r]) < PlayerHandler.players[c.playerIndex].REDUCE_SPELL_TIME[r]) {
							c.sendMessage("That player is currently immune to this spell.");
							c.usingMagic = false;
							c.stopMovement();
							c.getCombat().resetPlayerAttack();
						}
						break;
					}
				}

				if (System.currentTimeMillis()
						- PlayerHandler.players[c.playerIndex].teleBlockDelay < PlayerHandler.players[c.playerIndex].teleBlockLength
						&& Player.MAGIC_SPELLS[c.spellId][0] == 12445) {
					c.sendMessage("That player is already affected by this spell.");
					c.usingMagic = false;
					c.stopMovement();
					c.getCombat().resetPlayerAttack();
				}

				if (c.usingMagic) {
					if (c.goodDistance(c.getX(), c.getY(),
							PlayerHandler.players[c.playerIndex].getX(),
							PlayerHandler.players[c.playerIndex].getY(), 7)) {
						c.stopMovement();
					}
					if (c.getCombat().checkReqs()) {
						// c.playerFollowIndex = c.playerIndex;
						c.mageFollow = true;
					}
				}
			}
			break;

		}

	}

}
