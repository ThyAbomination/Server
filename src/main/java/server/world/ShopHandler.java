package server.world;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import server.Config;
import server.model.players.PlayerHandler;
import server.util.Misc;

/**
 * Shops
 **/

public class ShopHandler {

	public static int MaxShops = 101;
	public static int MaxShopItems = 101;
	public static int MaxInShopItems = 20;
	public static int MaxShowDelay = 10;
	public static int MaxSpecShowDelay = 60;
	public static int TotalShops = 0;
	public static int[][] ShopItems = new int[MaxShops][MaxShopItems];
	public static int[][] ShopItemsN = new int[MaxShops][MaxShopItems];
	public static int[][] ShopItemsDelay = new int[MaxShops][MaxShopItems];
	public static int[][] ShopItemsSN = new int[MaxShops][MaxShopItems];
	public static int[] ShopItemsStandard = new int[MaxShops];
	public static String[] ShopName = new String[MaxShops];
	public static int[] ShopSModifier = new int[MaxShops];
	public static int[] ShopBModifier = new int[MaxShops];

	public ShopHandler() {
		for (int i = 0; i < MaxShops; i++) {
			for (int j = 0; j < MaxShopItems; j++) {
				ResetItem(i, j);
				ShopItemsSN[i][j] = 0;
			}
			ShopItemsStandard[i] = 0;
			ShopSModifier[i] = 0;
			ShopBModifier[i] = 0;
			ShopName[i] = "";
		}
		TotalShops = 0;
		loadShops("shops.cfg");
	}

	public static void shophandler() {
		Misc.println("Shop Handler class successfully loaded");
	}

	/*
	 * public static int restockTimeItem(int itemId) { switch (itemId) { case
	 * 882: case 884: case 886: case 888: case 890: case 892: case 11212: case
	 * 9140: case 9141: case 9142: case 9143: case 9144: case 9243: case 9244:
	 * case 868: case 15243: case 15273: case 392: case 7947: case 398: case
	 * 386: case 380: case 374: case 362: case 366: case 356: case 326: case
	 * 330: case 334: case 533: case 554: case 555: case 556: case 557: case
	 * 558: case 559: case 560: case 561: case 562: case 563: case 564: case
	 * 565: case 9075: case 6686: case 3041: case 2445: case 2435: case 3025:
	 * case 2443: case 2437: case 2441: case 228: case 1437: case 5291: case
	 * 5292: case 5293: case 5294: case 5295: case 5296: case 5297: case 5298:
	 * case 5299: case 5300: case 5301: case 5302: case 5303: case 5304: case
	 * 1625: case 1629: case 1623: case 1621: case 1619: case 1617: case 1631:
	 * case 1745: case 2505: case 2507: case 2509: case 199: case 201: case 203:
	 * case 205: case 207: case 3049: case 209: case 211: case 213: case 3051:
	 * case 215: case 2485: case 217: case 219: case 221: case 235: case 225:
	 * case 223: case 1975: case 239: case 2152: case 9736: case 231: case 5004:
	 * case 2970: case 241: case 245: case 3138: case 247: case 6693: case 5972:
	 * case 4621: case 9594: case 12539: case 4255: return 400;
	 * 
	 * case 6737: case 6735: case 6733: case 6731: case 6524: case 4151: case
	 * 2577: case 2581: case 6914: case 6889: case 6918: case 6922: case 6920:
	 * case 6916: case 6924: case 7462: case 7461: case 7460: case 7459: return
	 * 300000;
	 * 
	 * case 6585: case 11128: case 11235: case 10352: case 10350: case 10348:
	 * case 10346: case 10342: case 10340: case 10338: case 10344: case 10336:
	 * case 10334: case 10332: case 10330: return 600000;
	 * 
	 * default: return 10000; }
	 * 
	 * }
	 */

	public void process() {
		boolean DidUpdate = false;
		for (int i = 1; i <= TotalShops; i++) {
			for (int j = 0; j < MaxShopItems; j++) {
				if (ShopItems[i][j] > 0) {
					if (ShopItemsDelay[i][j] >= MaxShowDelay) {
						if (j <= ShopItemsStandard[i]
								&& ShopItemsN[i][j] <= ShopItemsSN[i][j]) {
							if (ShopItemsN[i][j] < ShopItemsSN[i][j]) {
								ShopItemsN[i][j] += 1;
								DidUpdate = true;
								ShopItemsDelay[i][j] = 1;
								ShopItemsDelay[i][j] = 0;
								DidUpdate = true;
							}
						} else if (ShopItemsDelay[i][j] >= MaxSpecShowDelay) {
							DiscountItem(i, j);
							ShopItemsDelay[i][j] = 0;
							DidUpdate = true;
						}
					}
					ShopItemsDelay[i][j]++;
				}
			}
			if (DidUpdate == true) {
				for (int k = 1; k < Config.MAX_PLAYERS; k++) {
					if (PlayerHandler.players[k] != null) {
						if (PlayerHandler.players[k].isShopping == true
								&& PlayerHandler.players[k].myShopId == i) {
							PlayerHandler.players[k].updateShop = true;
							DidUpdate = false;
							PlayerHandler.players[k].updateshop(i);
						}
					}
				}
				DidUpdate = false;
			}
		}
	}

	public void DiscountItem(int ShopID, int ArrayID) {
		ShopItemsN[ShopID][ArrayID] -= 1;
		if (ShopItemsN[ShopID][ArrayID] <= 0) {
			ShopItemsN[ShopID][ArrayID] = 0;
			ResetItem(ShopID, ArrayID);
		}
	}

	public void ResetItem(int ShopID, int ArrayID) {
		ShopItems[ShopID][ArrayID] = 0;
		ShopItemsN[ShopID][ArrayID] = 0;
		ShopItemsDelay[ShopID][ArrayID] = 0;
	}

	public boolean loadShops(String FileName) {
		String line = "";
		String token = "";
		String token2 = "";
		String token2_2 = "";
		String[] token3 = new String[(MaxShopItems * 2)];
		boolean EndOfFile = false;
		int ReadMode = 0;
		BufferedReader characterfile = null;
		try {
			characterfile = new BufferedReader(new FileReader("./Data/CFG/"
					+ FileName));
			// System.out.println("Shops Loaded");
		} catch (FileNotFoundException fileex) {
			Misc.println(FileName + ": file not found.");
			return false;
		}
		try {
			line = characterfile.readLine();
		} catch (IOException ioexception) {
			Misc.println(FileName + ": error loading file.");
			return false;
		}
		while (EndOfFile == false && line != null) {
			line = line.trim();
			int spot = line.indexOf("=");
			if (spot > -1) {
				token = line.substring(0, spot);
				token = token.trim();
				token2 = line.substring(spot + 1);
				token2 = token2.trim();
				token2_2 = token2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token3 = token2_2.split("\t");
				if (token.equals("shop")) {
					int ShopID = Integer.parseInt(token3[0]);
					ShopName[ShopID] = token3[1].replaceAll("_", " ");
					ShopSModifier[ShopID] = Integer.parseInt(token3[2]);
					ShopBModifier[ShopID] = Integer.parseInt(token3[3]);
					for (int i = 0; i < ((token3.length - 4) / 2); i++) {
						if (token3[(4 + (i * 2))] != null) {
							ShopItems[ShopID][i] = (Integer
									.parseInt(token3[(4 + (i * 2))]) + 1);
							ShopItemsN[ShopID][i] = Integer
									.parseInt(token3[(5 + (i * 2))]);
							ShopItemsSN[ShopID][i] = Integer
									.parseInt(token3[(5 + (i * 2))]);
							ShopItemsStandard[ShopID]++;
						} else {
							break;
						}
					}
					TotalShops++;
				}
			} else {
				if (line.equals("[ENDOFSHOPLIST]")) {
					try {
						characterfile.close();
					} catch (IOException ioexception) {
					}
					return true;
				}
			}
			try {
				line = characterfile.readLine();
			} catch (IOException ioexception1) {
				EndOfFile = true;
			}
		}
		try {
			characterfile.close();
		} catch (IOException ioexception) {
		}
		return false;
	}
}
