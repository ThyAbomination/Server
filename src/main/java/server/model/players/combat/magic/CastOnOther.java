package server.model.players.combat.magic;

import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.minigames.PestControl;
import server.model.players.Client;
import server.model.players.PlayerHandler;

public class CastOnOther extends MagicRequirements {

	public static boolean castOnOtherSpells(Client c) {
		int[] spells = { 12435, 12455, 12425, 30298, 30290, 30282, };
		for (int spell : spells) {
			if (c.castingSpellId == spell) {
				return true;
			}
		}
		return false;
	}

	public static void healOther(Client c, int i) {
		Client p = (Client) PlayerHandler.players[i];
		double hpPercent = c.playerLevel[3] * 0.75;
		if (!PlayerHandler.players[c.playerIndex].inMulti()) {
			if (PlayerHandler.players[c.playerIndex].underAttackBy != c.playerId
					&& PlayerHandler.players[c.playerIndex].underAttackBy != 0) {
				c.sendMessage("That player is already in combat.");
				return;
			}
		}
		if (!hasRequiredLevel(c, 92)) {
			c.sendMessage("You need to have a magic level of 92 to cast this spell.");
			return;
		}
		if (!hasRunes(c, new int[] { ASTRAL, LAW, BLOOD },
				new int[] { 3, 3, 1 })) {
			return;
		}
		if (p.playerLevel[3] < 1) {
			return;
		}

		if (p.playerLevel[3] == p.getLevelForXP(p.playerXP[3])) {
			c.sendMessage("" + p.playerName + " already has full hitpoints.");
			return;
		}
		c.faceUpdate(i + 32768);
		deleteRunes(c, new int[] { ASTRAL, LAW, BLOOD }, new int[] { 3, 3, 1 });

		if (p.playerLevel[3] + (int) hpPercent > p.getLevelForXP(p.playerXP[3])) {
			p.playerLevel[3] = p.getLevelForXP(c.playerXP[3]);
		}
		c.getCombat().resetPlayerAttack();
		c.handleHitMask((int) hpPercent);
		c.playerLevel[3] -= (int) hpPercent;

		p.getPA().refreshSkill(3);
		c.getPA().refreshSkill(3);

		c.startAnimation(4411);
		c.gfx100(727);
	}

	public static void specialEnergyTransfer(Client c, int i) {
		Client p = (Client) PlayerHandler.players[i];
		if (!PlayerHandler.players[c.playerIndex].inMulti()) {
			if (PlayerHandler.players[c.playerIndex].underAttackBy != c.playerId
					&& PlayerHandler.players[c.playerIndex].underAttackBy != 0) {
				c.sendMessage("That player is already in combat.");
				return;
			}
		}
		if (!hasRequiredLevel(c, 91)) {
			c.sendMessage("You need to have a magic level of 91 to cast this spell.");
			return;
		}
		if (p.specAmount >= 5.0) {
			c.sendMessage("You can't transfer special energy to "
					+ p.playerName + "!");
			return;
		}
		if (!hasRunes(c, new int[] { ASTRAL, LAW, NATURE },
				new int[] { 3, 2, 1 })) {
			return;
		}
		deleteRunes(c, new int[] { ASTRAL, LAW, NATURE }, new int[] { 3, 2, 1 });
		c.faceUpdate(i + 32768);
		c.startAnimation(4411);
		c.getCombat().resetPlayerAttack();
		p.gfx0(1069);
		p.specAmount += 5;
		c.specAmount -= 5;
		c.getItems().updateSpecialBar();
		p.getItems().updateSpecialBar();
		p.sendMessage("Your special energy has been restored by 50%!");
		c.sendMessage("You transfer 50% of your energy to " + p.playerName
				+ ".");
	}

	public static void castOtherVengeance(Client c, int i) {
		Client p = (Client) PlayerHandler.players[i];
		if (!PlayerHandler.players[c.playerIndex].inMulti()) {
			if (PlayerHandler.players[c.playerIndex].underAttackBy != c.playerId
					&& PlayerHandler.players[c.playerIndex].underAttackBy != 0) {
				c.sendMessage("That player is already in combat.");
				return;
			}
		}
		if (!hasRequiredLevel(c, 93)) {
			c.sendMessage("You need to have a magic level of 93 to cast this spell.");
			return;
		}
		if (c.getItems().playerHasItem(557, 10)
				&& c.getItems().playerHasItem(9075, 3)
				&& c.getItems().playerHasItem(560, 2)) {

			if (p.vengOn) {
				c.sendMessage("This player has already casted vengeance!");
				return;
			}
			if (p.duelStatus == 5) {
				c.sendMessage("You can't use this on this player");
				return;
			}
			if (System.currentTimeMillis() - p.lastVeng < 30000) {
				c.sendMessage("You must wait 30 seconds before casting this again.");
				return;
			}
			c.getItems().deleteItem(557, c.getItems().getItemSlot(557), 10);
			c.getItems().deleteItem(9075, c.getItems().getItemSlot(9075), 3);
			c.getItems().deleteItem(560, c.getItems().getItemSlot(560), 2);
			c.faceUpdate(i + 32768);
			c.startAnimation(4411);
			c.getCombat().resetPlayerAttack();
			p.vengOn = true;
			p.lastVeng = System.currentTimeMillis();
			p.gfx100(725);
			p.sendMessage("You have the power of vengeance!");
		} else {
			c.sendMessage("You don't have the required runes to cast this spell.");
		}
	}

	public static void teleOtherDistance(Client c, int type, int i) {
		Client castOn = (Client) PlayerHandler.players[i];
		int[][] data = { { 74, SOUL, LAW, EARTH, 1, 1, 1 },
				{ 82, SOUL, LAW, WATER, 1, 1, 1 },
				{ 90, SOUL, LAW, -1, 2, 1, -1 }, };
		if (!PlayerHandler.players[c.playerIndex].inMulti()) {
			if (PlayerHandler.players[c.playerIndex].underAttackBy != c.playerId
					&& PlayerHandler.players[c.playerIndex].underAttackBy != 0) {
				c.sendMessage("That player is already in combat.");
				return;
			}
		}
		if (!castOn.acceptAid) {
			c.sendMessage("This player does not have Accept Aid on.");
			return;
		}
		if (!hasRequiredLevel(c, data[type][0])) {
			c.sendMessage("You need to have a magic level of " + data[type][0]
					+ " to cast this spell.");
			return;
		}
		if (PestControl.isInPcBoat(c) || PestControl.isInPcBoat(castOn)
				|| PestControl.isInGame(c) || PestControl.isInGame(castOn)
				|| castOn.duelStatus > 0 || c.duelStatus > 0 || c.inWild()
				|| castOn.inWild()) {
			c.sendMessage("You can't do that here!");
			return;
		}

		if (!hasRunes(c, new int[] { data[type][1], data[type][2],
				data[type][3] }, new int[] { data[type][4], data[type][5],
				data[type][6] })) {
			return;
		}
		deleteRunes(c,
				new int[] { data[type][1], data[type][2], data[type][3] },
				new int[] { data[type][4], data[type][5], data[type][6] });
		String[] location = { "Lumbridge", "Falador", "Camelot", };
		if (c.acceptAid = false) {
			return;
		}
		c.faceUpdate(i + 32768);
		c.startAnimation(1818);
		c.gfx0(343);
		c.getCombat().resetPlayerAttack();
		if (castOn != null) {
			if (castOn.distanceToPoint(c.getX(), c.getY()) <= 15) {
				if (c.heightLevel == castOn.heightLevel) {
					castOn.getPA().sendFrame126(location[type], 12560);
					castOn.getPA().sendFrame126(c.playerName, 12558);
					castOn.getPA().showInterface(12468);
					castOn.teleotherType = type;
				}
			}
		}
	}

	public static void teleOtherLocation(final Client c, final int i,
			boolean decline) {
		c.getPA().removeAllWindows();
		if (c.duelStatus > 0 || PestControl.isInPcBoat(c)
				|| PestControl.isInGame(c)) {
			return;
		}
		if (!decline) {
			CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					getTeleOtherCoords(c, i);
					c.startAnimation(715);
					container.stop();
				}

				@Override
				public void stop() {

				}
			}, 3);
			c.startAnimation(1816);
			c.gfx100(342);
		}
	}

	private static void getTeleOtherCoords(Client c, int i) {
		int[][] coords = { { 3222, 3218 }, // LUMBRIDGE
				{ 2964, 3378 }, // FALADOR
				{ 2757, 3477 }, // CAMELOT
		};

		if (i >= 0) {
			c.teleportToX = coords[i][0];
			c.teleportToY = coords[i][1];
			c.teleotherType = -1;
		}
	}

}