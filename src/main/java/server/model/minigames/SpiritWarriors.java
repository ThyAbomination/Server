package server.model.minigames;

import server.Server;
import server.model.players.Client;

public class SpiritWarriors {

	private static final int[][] COORDS = { { 2557, 4962 }, { 2557, 4957 },
			{ 2562, 4957 }, { 2562, 4962 } };

	public static void spiritWave(Client c) {
		if (c != null) {
			int X = COORDS[0][0];
			int Y = COORDS[0][1];
			int X1 = COORDS[1][0];
			int Y1 = COORDS[1][1];
			int X2 = COORDS[2][0];
			int Y2 = COORDS[2][1];
			int X3 = COORDS[3][0];
			int Y3 = COORDS[3][1];
			if (c.spiritWave == 0 && c.spiritCount >= 0) {
				Server.npcHandler.spawnNpc(c, 937, X, Y, c.heightLevel, 0, 200,
						20, 350, 350, true, true);
				Server.npcHandler.spawnNpc(c, 6219, X1, Y1, c.heightLevel, 0,
						125, 15, 100, 100, true, false);
				Server.npcHandler.spawnNpc(c, 6220, X2, Y2, c.heightLevel, 0,
						125, 15, 100, 100, true, false);
				Server.npcHandler.spawnNpc(c, 6221, X3, Y3, c.heightLevel, 0,
						100, 15, 100, 100, true, false);
				c.getPA().stillGfx(1207, X, Y, c.heightLevel, 0);
				c.getPA().stillGfx(1207, X1, Y1, c.heightLevel, 0);
				c.getPA().stillGfx(1207, X2, Y2, c.heightLevel, 0);
				c.getPA().stillGfx(1207, X3, Y3, c.heightLevel, 0);
			} else if (c.spiritWave == 1 && c.spiritCount >= 4) {
				Server.npcHandler.spawnNpc(c, 936, X1, Y1, c.heightLevel, 0,
						225, 23, 400, 400, true, true);
				Server.npcHandler.spawnNpc(c, 6255, X, Y, c.heightLevel, 0,
						150, 16, 150, 150, true, false);
				Server.npcHandler.spawnNpc(c, 6256, X2, Y2, c.heightLevel, 0,
						150, 16, 150, 150, true, false);
				Server.npcHandler.spawnNpc(c, 6257, X3, Y3, c.heightLevel, 0,
						150, 16, 150, 150, true, false);
				c.getPA().stillGfx(1207, X, Y, c.heightLevel, 0);
				c.getPA().stillGfx(1207, X1, Y1, c.heightLevel, 0);
				c.getPA().stillGfx(1207, X2, Y2, c.heightLevel, 0);
				c.getPA().stillGfx(1207, X3, Y3, c.heightLevel, 0);
			} else if (c.spiritWave == 2 && c.spiritCount >= 8) {
				Server.npcHandler.spawnNpc(c, 1913, X2, Y2, c.heightLevel, 0,
						255, 28, 450, 450, true, true);
				Server.npcHandler.spawnNpc(c, 6229, X, Y, c.heightLevel, 0,
						175, 18, 200, 200, true, false);
				Server.npcHandler.spawnNpc(c, 6230, X1, Y1, c.heightLevel, 0,
						175, 18, 200, 200, true, false);
				Server.npcHandler.spawnNpc(c, 6231, X3, Y3, c.heightLevel, 0,
						175, 18, 200, 200, true, false);
				c.getPA().stillGfx(1207, X, Y, c.heightLevel, 0);
				c.getPA().stillGfx(1207, X1, Y1, c.heightLevel, 0);
				c.getPA().stillGfx(1207, X2, Y2, c.heightLevel, 0);
				c.getPA().stillGfx(1207, X3, Y3, c.heightLevel, 0);
			} else if (c.spiritWave == 3 && c.spiritCount >= 12) {
				Server.npcHandler.spawnNpc(c, 1977, X3, Y3, c.heightLevel, 0,
						300, 32, 500, 500, true, true);
				Server.npcHandler.spawnNpc(c, 6276, X, Y, c.heightLevel, 0,
						200, 20, 250, 250, true, false);
				Server.npcHandler.spawnNpc(c, 6277, X1, Y1, c.heightLevel, 0,
						200, 20, 250, 250, true, false);
				Server.npcHandler.spawnNpc(c, 6278, X2, Y2, c.heightLevel, 0,
						200, 20, 250, 250, true, false);
				c.getPA().stillGfx(1207, X, Y, c.heightLevel, 0);
				c.getPA().stillGfx(1207, X1, Y1, c.heightLevel, 0);
				c.getPA().stillGfx(1207, X2, Y2, c.heightLevel, 0);
				c.getPA().stillGfx(1207, X3, Y3, c.heightLevel, 0);
			}
		}
	}
}
