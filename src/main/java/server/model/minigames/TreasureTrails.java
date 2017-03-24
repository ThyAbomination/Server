package server.model.minigames;

import server.model.players.Client;
import server.util.Misc;

public class TreasureTrails {

	public static int lowLevelReward[] = { 1077, 1089, 1107, 1125, 1131, 1129,
			1133, 1511, 1168, 1165, 1179, 1195, 1217, 1283, 1297, 1313, 1327,
			1341, 1361, 1367, 1426, 2633, 2635, 2637, 7388, 7386, 7392, 7390,
			7396, 7394, 2631, 7364, 7362, 7368, 7366, 2583, 2585, 2587, 2589,
			2591, 2593, 2595, 2597, 7332, 7338, 7350, 7356, 10404, 10406,
			10408, 10410, 10412, 10414, 10424, 10426, 10428, 10430, 10432,
			10434, 10458, 10460, 10462, 10464, 10466, 10468 };
	public static int mediemLevelReward[] = { 2599, 2601, 2603, 2605, 2607,
			2609, 2611, 2613, 7334, 7340, 7346, 7352, 7358, 7319, 7321, 7323,
			7325, 7327, 7372, 7370, 7380, 7378, 2645, 2647, 2649, 2577, 2579,
			1073, 1091, 1099, 1111, 1135, 1124, 1145, 1161, 1169, 1183, 1199,
			1211, 1245, 1271, 1287, 1301, 1317, 1332, 1357, 1371, 1430, 6916,
			6918, 6920, 6922, 6924, 10400, 10402, 10416, 10418, 10420, 10422,
			10436, 10438, 10446, 10448, 10450, 10452, 10454, 10456, 6889 };
	public static int highLevelReward[] = { 1079, 1093, 1113, 1127, 1147, 1163,
			1185, 1201, 1275, 1303, 1319, 1333, 1359, 1373, 2491, 2497, 2503,
			861, 859, 2581, 2651, 1079, 1093, 1113, 1127, 1147, 1163, 1185,
			1201, 1275, 1303, 1319, 1333, 1359, 1373, 2491, 2497, 2503, 861,
			859, 2581, 2651, 2615, 2617, 2619, 2621, 2623, 2625, 2627, 2629,
			2639, 2641, 2643, 2651, 2653, 2655, 2657, 2659, 2661, 2663, 2665,
			2667, 2669, 2671, 2673, 2675, 2581, 7342, 7348, 7454, 7460, 7374,
			7376, 7382, 7384, 7398, 7399, 7400, 3481, 3483, 3485, 3486, 3488,
			1079, 1093, 1113, 1127, 1148, 1164, 1185, 1201, 1213, 1247, 1275,
			1289, 1303, 1319, 1333, 1347, 1359, 1374, 1432, 2615, 2617, 2619,
			2621, 2623, 2625, 2627, 2629, 2639, 2641, 2643, 2651, 2653, 2655,
			2657, 2659, 2661, 2663, 2665, 2667, 2669, 2671, 2673, 2675, 2581,
			7342, 7348, 7454, 7460, 7374, 7376, 7382, 7384, 7398, 7399, 7400,
			3481, 3483, 3485, 3486, 3488, 1079, 1093, 1113, 1127, 1148, 1164,
			1185, 1201, 1213, 1247, 1275, 1289, 1303, 1319, 1333, 1347, 1359,
			1374, 1432, 10330, 10338, 10348, 10332, 10340, 10346, 10334, 10342,
			10350, 10336, 10344, 10352, 10368, 10376, 10384, 10370, 10378,
			10386, 10372, 10380, 10388, 10374, 10382, 10390, 10470, 10472,
			10474, 10440, 10442, 10444, 6914 };

	public static int lowLevelStacks[] = { 995, 380, 561, 886, };
	public static int mediumLevelStacks[] = { 995, 374, 561, 563, 890, };
	public static int highLevelStacks[] = { 995, 386, 561, 563, 560, 892 };

	public static void addClueReward(Client c, int clueLevel) {
		int chanceReward = Misc.random(2);
		if (clueLevel == 0) {
			switch (chanceReward) {
			case 0:
				displayReward(
						c,
						lowLevelReward[(int) (Math.random() * lowLevelReward.length)],
						1,
						lowLevelReward[(int) (Math.random() * lowLevelReward.length)],
						1, lowLevelStacks[Misc.random(3)],
						1 + Misc.random(1500));
				break;
			case 1:
				displayReward(
						c,
						lowLevelReward[(int) (Math.random() * lowLevelReward.length)],
						1, lowLevelStacks[Misc.random(3)],
						1 + Misc.random(1500), -1, 1);
				break;
			case 2:
				displayReward(
						c,
						lowLevelReward[(int) (Math.random() * lowLevelReward.length)],
						1, -1, 1, -1, 1);
				break;
			}
		} else if (clueLevel == 1) {
			switch (chanceReward) {
			case 0:
				displayReward(
						c,
						mediemLevelReward[(int) (Math.random() * mediemLevelReward.length)],
						1,
						mediemLevelReward[(int) (Math.random() * mediemLevelReward.length)],
						1, mediumLevelStacks[Misc.random(4)],
						1 + Misc.random(2000));
				break;
			case 1:
				displayReward(
						c,
						mediemLevelReward[(int) (Math.random() * mediemLevelReward.length)],
						1, mediumLevelStacks[Misc.random(4)],
						1 + Misc.random(2000), -1, 1);
				break;
			case 2:
				displayReward(
						c,
						mediemLevelReward[(int) (Math.random() * mediemLevelReward.length)],
						1, -1, 1, -1, 1);
				break;
			}
		} else if (clueLevel == 2) {
			switch (chanceReward) {
			case 0:
				displayReward(
						c,
						highLevelReward[(int) (Math.random() * highLevelReward.length)],
						1,
						highLevelReward[(int) (Math.random() * highLevelReward.length)],
						1, highLevelStacks[Misc.random(5)],
						1 + Misc.random(3500));
				break;
			case 1:
				displayReward(
						c,
						highLevelReward[(int) (Math.random() * highLevelReward.length)],
						1, highLevelStacks[Misc.random(5)],
						1 + Misc.random(3500), -1, 1);
				break;
			case 2:
				displayReward(
						c,
						highLevelReward[(int) (Math.random() * highLevelReward.length)],
						1, -1, 1, -1, 1);
				break;
			}
		}
	}

	public static void displayReward(Client c, int item, int amount, int item2,
			int amount2, int item3, int amount3) {
		int[] items = { item, item2, item3 };
		int[] amounts = { amount, amount2, amount3 };
		c.outStream.createFrameVarSizeWord(53);
		c.outStream.writeWord(6963);
		c.outStream.writeWord(items.length);
		for (int i = 0; i < items.length; i++) {
			if (c.playerItemsN[i] > 254) {
				c.outStream.writeByte(255);
				c.outStream.writeDWord_v2(amounts[i]);
			} else {
				c.outStream.writeByte(amounts[i]);
			}
			if (items[i] > 0) {
				c.outStream.writeWordBigEndianA(items[i] + 1);
			} else {
				c.outStream.writeWordBigEndianA(0);
			}
		}
		c.outStream.endFrameVarSizeWord();
		c.flushOutStream();
		c.getItems().addItem(item, amount);
		c.getItems().addItem(item2, amount2);
		c.getItems().addItem(item3, amount3);
		c.getPA().showInterface(6960);
	}

}