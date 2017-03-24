package server.model.minigames;

import server.Server;
import server.model.npcs.NPCHandler;
import server.model.players.Client;

public class FightCaves {

	public static final int TZ_KIH = 2627, TZ_KEK_SPAWN = 2738, TZ_KEK = 2630,
			TOK_XIL = 2631, YT_MEJKOT = 2741, KET_ZEK = 2743, TZTOK_JAD = 2745;

	/**
	 * Holds the data for the 63 waves fight cave.
	 */
	private final int[][] WAVES = { { TZ_KIH },// 1
			{ TZ_KIH, TZ_KIH },// 2
			// {TZ_KEK},
			// {TZ_KEK,TZ_KIH},
			{ TZ_KEK, TZ_KIH, TZ_KIH },// 3
			// {TZ_KEK,TZ_KEK},
			// {TOK_XIL},
			// {TOK_XIL,TZ_KIH},
			{ TOK_XIL, TZ_KIH, TZ_KIH },// 4
			// {TOK_XIL,TZ_KEK},
			// {TOK_XIL,TZ_KEK,TZ_KIH},
			{ TOK_XIL, TZ_KEK, TZ_KIH, TZ_KIH },// 5
			// {TOK_XIL,TZ_KEK,TZ_KEK},
			// {TOK_XIL,TOK_XIL},
			// {YT_MEJKOT},
			// {YT_MEJKOT,TZ_KIH},
			{ YT_MEJKOT, TZ_KIH, TZ_KIH },// 6
			// {YT_MEJKOT,TZ_KEK},
			// {YT_MEJKOT,TZ_KEK,TZ_KIH},
			{ YT_MEJKOT, TZ_KEK, TZ_KIH, TZ_KIH },// 7
			// {YT_MEJKOT,TZ_KEK,TZ_KEK},
			// {YT_MEJKOT,TOK_XIL},
			{ YT_MEJKOT, TOK_XIL, TZ_KIH },// 8
			{ YT_MEJKOT, TOK_XIL, TZ_KIH, TZ_KIH },// 9
			// {YT_MEJKOT,TOK_XIL,TZ_KEK},
			{ YT_MEJKOT, TOK_XIL, TZ_KEK, TZ_KIH },// 10
			// { YT_MEJKOT, TOK_XIL, TZ_KEK, TZ_KIH, TZ_KIH },// 11
			{ YT_MEJKOT, TOK_XIL, TZ_KEK, TZ_KEK },// 11
			{ YT_MEJKOT, TOK_XIL, TOK_XIL },// 12
			// {YT_MEJKOT,YT_MEJKOT},
			// {KET_ZEK},
			// {KET_ZEK,TZ_KIH},
			// { KET_ZEK, TZ_KIH, TZ_KIH },// 13
			{ KET_ZEK, TZ_KEK },// 13
			// {KET_ZEK,TZ_KEK,TZ_KIH},
			// { KET_ZEK, TZ_KEK, TZ_KIH, TZ_KIH },// 14
			{ KET_ZEK, TZ_KEK, TZ_KEK },// 14
			// {KET_ZEK,TOK_XIL},
			{ KET_ZEK, TOK_XIL, TZ_KIH },// 15
			// { KET_ZEK, TOK_XIL, TZ_KIH, TZ_KIH },
			{ KET_ZEK, TOK_XIL, TZ_KEK },// 16
			{ KET_ZEK, TOK_XIL, TZ_KEK, TZ_KIH },// 17
			// { KET_ZEK, TOK_XIL, TZ_KEK, TZ_KIH, TZ_KIH },
			{ KET_ZEK, TOK_XIL, TZ_KEK, TZ_KEK },// 18
			{ KET_ZEK, TOK_XIL, TOK_XIL },// 19
			// { KET_ZEK, YT_MEJKOT },
			// { KET_ZEK, YT_MEJKOT, TZ_KIH },
			// { KET_ZEK, YT_MEJKOT, TZ_KIH, TZ_KIH },
			{ KET_ZEK, YT_MEJKOT, TZ_KEK },// 20
			{ KET_ZEK, YT_MEJKOT, TZ_KEK, TZ_KIH },// 21
			// { KET_ZEK, YT_MEJKOT, TZ_KEK, TZ_KIH, TZ_KIH },// 22
			{ KET_ZEK, YT_MEJKOT, TZ_KEK, TZ_KEK },// 22
			{ KET_ZEK, YT_MEJKOT, TOK_XIL },// 23
			{ KET_ZEK, YT_MEJKOT, TOK_XIL, TZ_KIH },// 24
			// { KET_ZEK, YT_MEJKOT, TOK_XIL, TZ_KIH, TZ_KIH },
			{ KET_ZEK, YT_MEJKOT, TOK_XIL, TZ_KEK },// 25
			// { KET_ZEK, YT_MEJKOT, TOK_XIL, TZ_KEK, TZ_KIH },
			// { KET_ZEK, YT_MEJKOT, TOK_XIL, TZ_KEK, TZ_KIH, TZ_KIH },
			{ KET_ZEK, YT_MEJKOT, TOK_XIL, TZ_KEK, TZ_KEK },// 26
			{ KET_ZEK, YT_MEJKOT, TOK_XIL, TOK_XIL },// 27
			{ KET_ZEK, YT_MEJKOT, YT_MEJKOT },// 28
			{ KET_ZEK, KET_ZEK },// 29
			{ TZTOK_JAD } };// 30

	private final static int[][] COORDINATES = { { 2403, 5094 },
			{ 2390, 5096 }, { 2392, 5077 }, { 2408, 5080 }, { 2413, 5108 },
			{ 2381, 5106 }, { 2379, 5072 }, { 2420, 5082 } };

	/**
	 * Handles spawning the next fightcave wave.
	 * 
	 * @param c
	 *            The player.
	 */

	public void spawnNextWave(Client c) {
		if (c != null) {
			if (c.waveId >= WAVES.length) {
				c.waveId = 0;
				return;
			}
			if (c.waveId < 0)
				return;
			int npcAmount = WAVES[c.waveId].length;
			for (int j = 0; j < npcAmount; j++) {
				int npc = WAVES[c.waveId][j];
				int X = COORDINATES[j][0];
				int Y = COORDINATES[j][1];
				int H = c.heightLevel;
				int hp = getHp(npc);
				int max = getMax(npc);
				int atk = getAtk(npc);
				int def = getDef(npc);
				Server.npcHandler.spawnNpc(c, npc, X, Y, H, 0, hp, max, atk,
						def, false, false);
			}
			c.tzhaarToKill = npcAmount;
			c.tzhaarKilled = 0;
			if (c.waveId > 0)
				c.sendMessage("@dre@Wave " + c.waveId + " out of 30 completed.");
		}
	}

	/**
	 * Handles the correct tz-kih effect; prayer is drained by the formula:
	 * drain = damage + 1
	 * 
	 * @param c
	 *            The player
	 * @param i
	 *            The npcId
	 * @param damage
	 *            What the npchit
	 */
	public static void tzKihEffect(Client c, int i, int damage) {
		if (NPCHandler.npcs[i].npcType == TZ_KIH) {
			if (c != null) {
				if (c.playerLevel[5] > 0) {
					c.playerLevel[5] -= 1 + damage;
					c.getPA().refreshSkill(5);
				}
			}
		}
	}

	/**
	 * Handles the correct tz-kek effect; when you kill tz-kek it splits into 2
	 * smaller version of itself.
	 * 
	 * @param c
	 *            The player.
	 * @param i
	 *            The npcId.
	 */
	public static void tzKekEffect(Client c, int i) {
		if (NPCHandler.npcs[i].npcType == TZ_KEK) {

			int x = NPCHandler.npcs[i].absX + 2;
			int y = NPCHandler.npcs[i].absY + 2;
			int x1 = NPCHandler.npcs[i].absX - 2;
			int y1 = NPCHandler.npcs[i].absY - 2;

			int hp = getHp(TZ_KEK_SPAWN);
			int max = getMax(TZ_KEK_SPAWN);
			int atk = getAtk(TZ_KEK_SPAWN);
			int def = getDef(TZ_KEK_SPAWN);

			if (c != null) {
				if (c.tzKekTimer == 0) {
					if (NPCHandler.npcs[i].isDead) {
						Server.npcHandler.spawnNpc(c, TZ_KEK_SPAWN, x, y,
								c.heightLevel, 0, hp, max, atk, def, true,
								false);
						Server.npcHandler.spawnNpc(c, TZ_KEK_SPAWN, x1, y1,
								c.heightLevel, 0, hp, max, atk, def, true,
								false);
					}
				}
			}
		}
	}

	public static int getHp(int npc) {
		switch (npc) {
		case TZ_KIH:
		case TZ_KEK_SPAWN:
			return 10;
		case TZ_KEK:
			return 20;
		case TOK_XIL:
			return 40;
		case YT_MEJKOT:
			return 80;
		case KET_ZEK:
			return 150;
		case TZTOK_JAD:
			return 255;
		}
		return 100;
	}

	public static int getMax(int npc) {
		switch (npc) {
		case TZ_KIH:
		case TZ_KEK_SPAWN:
			return 4;
		case TZ_KEK:
			return 7;
		case TOK_XIL:
			return 17;
		case YT_MEJKOT:
			return 28;
		case KET_ZEK:
			return 44;
		case TZTOK_JAD:
			return 97;
		}
		return 5;
	}

	public static int getAtk(int npc) {
		switch (npc) {
		case TZ_KIH:
		case TZ_KEK_SPAWN:
			return 60;
		case TZ_KEK:
			return 100;
		case TOK_XIL:
			return 200;
		case YT_MEJKOT:
			return 300;
		case KET_ZEK:
			return 450;
		case TZTOK_JAD:
			return 10000;
		}
		return 100;
	}

	public static int getDef(int npc) {
		switch (npc) {
		case TZ_KIH:
		case TZ_KEK_SPAWN:
			return 60;
		case TZ_KEK:
			return 100;
		case TOK_XIL:
			return 200;
		case YT_MEJKOT:
			return 300;
		case KET_ZEK:
			return 450;
		case TZTOK_JAD:
			return 600;
		}
		return 100;
	}
}
