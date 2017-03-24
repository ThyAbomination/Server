package server.model.minigames;

import server.Server;
import server.model.npcs.NPC;
import server.model.npcs.NPCHandler;
import server.model.players.Client;
import server.model.players.PlayerHandler;
import server.util.Misc;

/**
 * @author Ultima
 */

public class BarbarianDefence {
	private final int[][] WAVES = {
			{ 5213, 5213, 5230, 5230 }, // Wave 1
			// { 5213, 5213, 5230, 5230, 5213, 5213, 5230, 5230 }, // Wave 2
			{ 5214, 5214, 5231, 5231, 5213, 5213, 5230, 5230 }, // Wave 3
			// { 5214, 5214, 5231, 5231, 5214, 5214, 5231, 5231 }, // Wave 5
			{ 5215, 5215, 5232, 5232, 5214, 5214, 5231, 5231 }, // Wave 4
			// { 5215, 5215, 5232, 5232, 5215, 5215, 5232, 5232 }, // Wave 5
			{ 5216, 5216, 5233, 5233, 5215, 5215, 5232, 5232 }, // Wave 6
			// { 5216, 5216, 5233, 5233, 5216, 5216, 5233, 5233 }, // Wave 7
			{ 5217, 5217, 5234, 5234, 5216, 5216, 5233, 5233 }, // Wave 8
			// { 5217, 5217, 5234, 5234, 5217, 5217, 5234, 5234 }, // Wave 9
			{ 5218, 5218, 5235, 5235, 5217, 5217, 5234, 5234 }, // Wave 10
			// { 5218, 5218, 5235, 5235, 5218, 5218, 5235, 5235 }, // Wave 11
			{ 5219, 5219, 5236, 5236, 5218, 5218, 5235, 5235 }, // Wave 12
			// { 5219, 5219, 5236, 5236, 5219, 5219, 5236, 5236 }, // Wave 13
			{ 5219, 5219, 5237, 5237, 5219, 5219, 5236, 5236 }, // Wave 14
			// { 5219, 5219, 5237, 5237, 5219, 5219, 5237, 5237 }, // Wave 15
			{ 5215, 5215, 5232, 5232, 5215, 5215, 5232, 5232, 5216, 5216, 5233,
					5233 }, // Wave 16
			// { 5216, 5216, 5234, 5234, 5216, 5216, 5234, 5234, 5219, 5219,
			// 5237,
			// 5237 }, // Wave 17
			{ 5217, 5217, 5235, 5235, 5216, 5216, 5234, 5234, 5219, 5219, 5237,
					5237 }, // Wave 18
			// { 5218, 5218, 5236, 5236, 5216, 5216, 5234, 5234, 5219, 5219,
			// 5237,
			// 5237 }, // Wave 19
			{ 5218, 5218, 5236, 5236, 5216, 5216, 5234, 5234, 5219, 5219, 5237,
					5237 }, // Wave 20
			// { 5218, 5218, 5236, 5236, 5217, 5217, 5235, 5235, 5219, 5219,
			// 5237,
			// 5237 }, // Wave 21
			{ 5218, 5218, 5236, 5236, 5218, 5218, 5235, 5235, 5218, 5218, 5236,
					5236 }, // Wave 22
			{ 5219, 5219, 5237, 5237, 5218, 5218, 5236, 5236, 5219, 5219, 5237,
					5237 }, // Wave 23
			{ 5219, 5219, 5237, 5237, 5219, 5219, 5237, 5237, 5219, 5219, 5237,
					5237 }, // Wave 24
			{ 5247 }, // Wave 25 - FINAL WAVE
	};

	private final int[][] COORDS = { { 3344, 3210 }, { 3340, 3211 },
			{ 3339, 3214 }, { 3339, 3218 }, { 3344, 3219 }, { 3350, 3217 },
			{ 3351, 3212 }, { 3347, 3208 }, { 3343, 3208 }, { 3343, 3211 },
			{ 3349, 3212 }, { 3350, 3215 } };

	public boolean killableNpcs(int i) {
		switch (NPCHandler.npcs[i].npcType) {
		case 5213:
		case 5214:
		case 5215:
		case 5216:
		case 5217:
		case 5218:
		case 5219: /* Penance fighters */
		case 5229:
		case 5230:
		case 5231:
		case 5232:
		case 5233:
		case 5234:
		case 5235:
		case 5236:
		case 5237: /* Penance rangers */
		case 5247: /* Penance queen */
			return true;
		}
		return false;
	}

	public void spawnWave(Client c) {
		if (c != null && c.inBarbDef) {
			c.getCombat().resetPrayers();
			if (c.barbWave >= WAVES.length) {
				endGame(c, true);
				c.barbWave = 0;
				return;
			}
			if (c.barbWave < 0)
				return;
			int npcAmount = WAVES[c.barbWave].length;
			for (int j = 0; j < npcAmount; j++) {
				int npc = WAVES[c.barbWave][j];
				int X = COORDS[j][0];
				int Y = COORDS[j][1];
				int H = c.heightLevel;
				int hp = getHp(npc);
				int max = getMax(npc);
				int atk = getAtk(npc);
				int def = getDef(npc);
				Server.npcHandler.spawnNpc(c, npc, X, Y, H, 0, hp, max, atk,
						def, true, npc == 5247);
				if (npc == 5247)
					spawnHelpers(c, COORDS);
				c.getPA().stillGfx(86, X, Y, c.heightLevel, 0);
			}
			if (c.barbLeader <= 0)
				Server.npcHandler.spawnNpc3(c, 751, 3345, 3213, c.heightLevel,
						0, 0, 0, 0, 0, false, false);
			NPC n = NPCHandler.npcs[c.barbLeader];
			n.requestAnimation(6728, 0);
			n.gfx0(1176);
			n.forceChat(n.barbRandom(c, Misc.random(5)));
			c.barbsToKill = npcAmount;
			c.barbsKilled = 0;
			if (c.barbWave > 0)
				c.sendMessage("@dre@Wave " + c.barbWave
						+ " out of 15 completed.");
		}
	}

	public void endGame(Client c, boolean victory) {
		int pointsToAdd = (c.barbDamage / 100) + (victory ? 36 : 0);
		c.barbPoints += pointsToAdd;
		c.sendMessage(victory ? "Congratulations, you've completed all "
				+ WAVES.length + " waves of Barbarian Assault!"
				: "Oh dear, you have failed to survive all the waves!");
		c.sendMessage("You receive " + pointsToAdd
				+ " Honour points for your efforts.");
		if (victory) {
			c.sendMessage("Congratulations! You defeated all waves of the barbarian assault mini-game.");
			PlayerHandler.yell("@dre@" + Misc.optimizeText(c.playerName)
					+ " has defeated all waves of Barbarian Assault!");
		}
		c.barbDamage = c.barbLeader = 0;
		NPC n = NPCHandler.npcs[c.barbLeader];
		c.getPA().movePlayer(3431, 3572, 0);
		c.barbWave = 0;
		c.inBarbDef = false;
	}

	public void enter(Client c) {
		c.inBarbDef = true;
		c.getCurse().resetCurse();
		c.getCombat().resetPrayers();
		c.getPA().movePlayer(3346, 3215, c.playerId * 4);
		spawnWave(c);
	}

	public void spawnHelpers(Client c, int[][] coords) {
		for (int j = 1; j < coords.length; j++)
			Server.npcHandler.spawnNpc(c, 5248, coords[j][0], coords[j][1],
					c.heightLevel, 0, 45, 7, 100, 50, true, false);
	}

	public int getHp(int npc) {
		switch (npc) {
		/* Penance fighters */
		case 5213:
			return 32;
		case 5214:
			return 37;
		case 5215:
			return 38;
		case 5216:
			return 49;
		case 5217:
			return 50;
		case 5218:
			return 56;
		case 5219:
			return 57;
			/* Penance rangers */
		case 5229:
			return 20;
		case 5230:
			return 28;
		case 5231:
			return 29;
		case 5232:
			return 34;
		case 5233:
			return 41;
		case 5234:
			return 50;
		case 5235:
			return 50;
		case 5236:
			return 55;
		case 5237:
			return 58;
			/* Penance queen */
		case 5247:
			return 250;
		}
		return 10;
	}

	public int getMax(int npc) {
		switch (npc) {
		/* Penance fighters */
		case 5213:
			return 5;
		case 5214:
			return 5;
		case 5215:
			return 6;
		case 5216:
			return 6;
		case 5217:
			return 7;
		case 5218:
			return 8;
		case 5219:
			return 9;
			/* Penance rangers */
		case 5229:
			return 3;
		case 5230:
			return 4;
		case 5231:
			return 4;
		case 5232:
			return 5;
		case 5233:
			return 5;
		case 5234:
			return 6;
		case 5235:
			return 7;
		case 5236:
			return 8;
		case 5237:
			return 9;
			/* Penance queen */
		case 5247:
			return 30;
		}
		return 5;
	}

	public int getAtk(int npc) {
		switch (npc) {
		/* Penance fighters */
		case 5213:
		case 5214:
		case 5215:
			return 145;
		case 5216:
		case 5217:
		case 5218:
			return 165;
		case 5219:
			return 180;
			/* Penance rangers */
		case 5229:
		case 5230:
		case 5231:
			return 125;
		case 5232:
		case 5233:
		case 5234:
			return 135;
		case 5235:
		case 5236:
		case 5237:
			return 150;
			/* Penance queen */
		case 5247:
			return 500;
		}
		return 100;
	}

	public int getDef(int npc) {
		switch (npc) {
		/* Penance fighters */
		case 5213:
		case 5214:
		case 5215:
			return 110;
		case 5216:
		case 5217:
		case 5218:
			return 120;
		case 5219:
			return 140;
			/* Penance rangers */
		case 5229:
		case 5230:
		case 5231:
			return 90;
		case 5232:
		case 5233:
		case 5234:
			return 100;
		case 5235:
		case 5236:
		case 5237:
			return 120;
			/* Penance queen */
		case 5247:
			return 500;
		}
		return 100;
	}

}