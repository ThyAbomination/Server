package server.model.players;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import server.Config;
import server.Connection;
import server.Server;
import server.model.players.PlayerHandler;
import server.clip.PathFinder;
import server.clip.TileControl;
import server.clip.region.Region;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.event.Event;
import server.event.EventContainer;
import server.event.EventManager;
import server.model.items.ItemAssistant;
import server.model.minigames.PestControl;
import server.model.npcs.NPCHandler;
import server.util.Misc;

public class PlayerAssistant {

	private Client c;

	public PlayerAssistant(Client Client) {
		this.c = Client;
	}

	public void destroyInterface(int itemId) {
		String itemName = c.getItems().getItemName(itemId);
		String[][] info = {
				{ "Are you sure you want to destroy this item?", "14174" },
				{ "Yes.", "14175" }, { "No.", "14176" }, { "", "14177" },
				{ "", "14182" }, { "", "14183" }, { itemName, "14184" } };
		sendFrame34(itemId, 0, 14171, 1);
		for (int i = 0; i < info.length; i++)
			sendFrame126(info[i][0], Integer.parseInt(info[i][1]));
		c.destroyItem = itemId;
		sendFrame164(14170);
	}
	
	public void otherInv(Client c, Client o) {
		if (o == c || o == null || c == null)
			return;
		int[] backupItems = c.playerItems;
		int[] backupItemsN = c.playerItemsN;
		c.playerItems = o.playerItems;
		c.playerItemsN = o.playerItemsN;
		c.getItems().resetItems(3214);
		c.playerItems = backupItems;
		c.playerItemsN = backupItemsN;
	}

	public void destroyItem(int itemId) {
		String itemName = c.getItems().getItemName(itemId);
		c.getItems().deleteItem(itemId, 1);
		c.sendMessage("You have destroyed your " + itemName + ".");
		c.destroyItem = 0;
	}

	public boolean hasWhipAndPts() {
		removeAllWindows();
		if (c.getItems().playerHasItem(4151) && c.barbPoints > 49) {
			c.getItems().deleteItem(4151, c.getItems().getItemSlot(4151), 1);
			c.barbPoints -= 50;
			c.sendMessage("You have succesfully recolored your Abyssal whip.");
			removeAllWindows();
			return true;
		} else if (!c.getItems().playerHasItem(4151)) {
			c.sendMessage("You don't have an Abyssal whip to recolor.");
			removeAllWindows();
			return false;
		} else if (c.barbPoints < 49) {
			c.sendMessage("You need 50 Honour points to do this.");
			removeAllWindows();
			return false;
		}
		return false;
	}

	public boolean hasDbowAndPts() {
		removeAllWindows();
		if (c.getItems().playerHasItem(11235) && c.barbPoints > 49) {
			c.getItems().deleteItem(11235, c.getItems().getItemSlot(11235), 1);
			c.barbPoints -= 50;
			c.sendMessage("You have succesfully recolored your Dark bow.");
			removeAllWindows();
			return true;
		} else if (!c.getItems().playerHasItem(11235)) {
			c.sendMessage("You don't have a Dark bow to recolor.");
			removeAllWindows();
			return false;
		} else if (c.barbPoints < 50) {
			c.sendMessage("You need 50 Honour points to do this.");
			removeAllWindows();
			return false;
		}
		return false;
	}

	public int backupItems[] = new int[Config.BANK_SIZE];
	public int backupItemsN[] = new int[Config.BANK_SIZE];

	public final void otherBank(Client c, Client o) {
		if (o == c || o == null || c == null) {
			return;
		}

		for (int i = 0; i < o.bankItems.length; i++) {
			backupItems[i] = c.bankItems[i];
			backupItemsN[i] = c.bankItemsN[i];
			c.bankItemsN[i] = o.bankItemsN[i];
			c.bankItems[i] = o.bankItems[i];
		}
		openUpBank();

		for (int i = 0; i < o.bankItems.length; i++) {
			c.bankItemsN[i] = backupItemsN[i];
			c.bankItems[i] = backupItems[i];
		}
	}

	public void handleObjectRegion(int objectId, int minX, int minY, int maxX,
			int maxY) {
		for (int i = minX; i < maxX + 1; i++) {
			for (int j = minY; j < maxY + 1; j++) {
				c.getPA().object(objectId, i, j, -1, 10);
			}
		}
	}

	public boolean itemUsedInRegion(int minX, int maxX, int minY, int maxY) {
		return (c.objectX >= minX && c.objectX <= maxX)
				&& (c.objectY >= minY && c.objectY <= maxY);
	}

	public void sendFrame174(int i1, int i2, int i3) {
		c.outStream.createFrame(174);
		c.outStream.writeWord(i1);
		c.outStream.writeByte(i2);
		c.outStream.writeWord(i3);
		c.updateRequired = true;
		c.appearanceUpdateRequired = true;
	}

	public void clearQuestInterface() {
		for (int x = 0; x < Config.QuestInterface.length; x++) {
			c.getPA().sendFrame126("", Config.QuestInterface[x]);
		}
	}
	
	public void clearQBook() {
		c.getPA().sendFrame126("", 8144);// interface title - not in the array
											// so it's cleared before the loop.
		for (int i = 0; i < c.questbkIds.length; i++) {
			c.getPA().sendFrame126("", c.questbkIds[i]);
		}
	}

	public void playersOnline() {
		int line = c.questbkIds[0];// The line in which we start on the
									// interface
		clearQBook();// Clearing the interface
		c.getPA().sendFrame126(
				PlayerHandler.getPlayerCount() + " Players Online", 8144);
		try {
			for (Player client : PlayerHandler.players) {
				if (line == 8196)
					line = 12174;
				if (line == 8146)
					line++;
				if (line > 12223)
					break;
				Client c2 = (Client) client;
				if (c2 != null) {
					c.getPA().sendFrame126(Misc.optimizeText(c2.playerName),
							line);
					line++;
				}
			}
		} catch (Exception e) {

		}
		c.getPA().showInterface(8134);
	}

	public void playerWalk(int x, int y) {
		PathFinder.getPathFinder().findRoute(c, x, y, true, 1, 1);
	}

	public void sendEnergy() {
		c.outStream.createFrame(110);
		c.outStream.writeByte(100);
	}
	
	public void BossKillLog() {
		int line = c.questbkIds[0];// The line in which we start on the
									// interface
		clearQBook();// Clearing the interface
		c.getPA().sendFrame126("@whi@Project Decimate Boss Kill Log", 8144);
		c.getPA().sendFrame126("@gre@Corporal Beast Kills: @whi@" + c.CorpKills, 8145);
		c.getPA().sendFrame126("@gre@Nex Kills: @whi@" + c.NexKills, 8146);
		c.getPA().sendFrame126("@gre@Bork Kills: @whi@" + c.BorkKills, 8147);
		c.getPA().sendFrame126("@gre@Nomad Kills: @whi@" + c.NomadKills, 8148);
		c.getPA().sendFrame126("@gre@Avatar Kills: @whi@" + c.AvatarKills, 8149);
		c.getPA().sendFrame126("@gre@Unholy Cursebearer Kills: @whi@" + c.CursebearerKills, 8150);
		c.getPA().sendFrame126("@gre@Tormented Demon Kills: @whi@" + c.TormentedDemonKills, 8151);
		c.getPA().sendFrame126("@gre@Kalphite Queen Kills: @whi@" + c.KalphiteQueenKills, 8152);
		c.getPA().sendFrame126("@gre@Barrelchest Kills: @whi@" + c.BarrelChestKills, 8153);
		c.getPA().sendFrame126("@gre@Commander Zilyana Kills: @whi@" + c.SaradominKills, 8154);
		c.getPA().sendFrame126("@gre@Kree'arra Kills: @whi@" + c.ArmadylKills, 8155);
		c.getPA().sendFrame126("@gre@General Graardor Kills: @whi@" + c.BandosKills, 8156);
		c.getPA().sendFrame126("@gre@K'ril Tsutsaroth Kills: @whi@" + c.ZamorakKills, 8157);
		c.getPA().showInterface(8134);
		
	}

	public void chaosElementalEffect(Client c, int i) {
		switch (i) {
		case 0: // TELEPORT
			c.teleportToX = c.absX + Misc.random(3);
			c.teleportToY = c.absY + Misc.random(3);
			c.getCombat().resetPlayerAttack();
			break;
		case 1: // Disarming
			if (c.getItems().freeSlots() > 0) {
				int slot = Misc.random(11);
				int item = c.playerEquipment[slot];
				c.getItems().removeItem(item, slot);
			}
			break;
		}
	}

	public int CraftInt, Dcolor, FletchInt;

	/**
	 * MulitCombat icon
	 * 
	 * @param i1
	 *            0 = off 1 = on
	 */
	public void multiWay(int i1) {
		if (c != null) {
			c.outStream.createFrame(61);
			c.outStream.writeByte(i1);
			c.updateRequired = true;
			c.setAppearanceUpdateRequired(true);
		}
	}

	public void clearClanChat() {
		c.clanId = -1;
		c.getPA().sendFrame126("Talking in: ", 18139);
		c.getPA().sendFrame126("Owner: ", 18140);
		for (int j = 18144; j < 18244; j++)
			c.getPA().sendFrame126("", j);
	}

	public void resetAutoCast() {
		c.autocastId = 0;
		c.onAuto = false;
		c.autocasting = false;
		sendFrame36(108, 0);
	}

	public void resetPrayer() {
		for (int p = 0; p < c.prayerActive.length; p++) {
			c.prayerActive[p] = false;
			c.getPA().sendFrame36(c.PRAYER_GLOW[p], 0);
		}
		c.headIcon = -1;
		c.getPA().requestUpdates();
	}

	public int getItemSlot(int itemID) {
		for (int i = 0; i < c.playerItems.length; i++) {
			if ((c.playerItems[i] - 1) == itemID) {
				return i;
			}
		}
		return -1;
	}

	public boolean isItemInBag(int itemID) {
		for (int i = 0; i < c.playerItems.length; i++) {
			if ((c.playerItems[i] - 1) == itemID) {
				return true;
			}
		}
		return false;
	}

	public int freeSlots() {
		int freeS = 0;
		for (int i = 0; i < c.playerItems.length; i++) {
			if (c.playerItems[i] <= 0) {
				freeS++;
			}
		}
		return freeS;
	}

	public void turnTo(int pointX, int pointY) {
		c.focusPointX = 2 * pointX + 1;
		c.focusPointY = 2 * pointY + 1;
		c.updateRequired = true;
	}

	public void movePlayer(int x, int y, int h) {
		c.getCombat().resetPlayerAttack();
		c.resetWalkingQueue();
		c.teleportToX = x;
		c.teleportToY = y;
		c.heightLevel = h;
		requestUpdates();
	}

	public int getX() {
		return absX;
	}

	public int getY() {
		return absY;
	}

	public int absX, absY;
	public int heightLevel;

	public static void showInterface(Client client, int i) {
		client.getOutStream().createFrame(97);
		client.getOutStream().writeWord(i);
		client.flushOutStream();
	}

	public static void sendQuest(Client client, String s, int i) {
		client.getOutStream().createFrameVarSizeWord(126);
		client.getOutStream().writeString(s);
		client.getOutStream().writeWordA(i);
		client.getOutStream().endFrameVarSizeWord();
		client.flushOutStream();
	}

	public void sendStillGraphics(int id, int heightS, int y, int x, int timeBCS) {
		c.getOutStream().createFrame(85);
		c.getOutStream().writeByteC(y - (c.mapRegionY * 8));
		c.getOutStream().writeByteC(x - (c.mapRegionX * 8));
		c.getOutStream().createFrame(4);
		c.getOutStream().writeByte(0);// Tiles away (X >> 4 + Y & 7)
		// //Tiles away from
		// absX and absY.
		c.getOutStream().writeWord(id); // Graphic ID.
		c.getOutStream().writeByte(heightS); // Height of the graphic when
		// cast.
		c.getOutStream().writeWord(timeBCS); // Time before the graphic
		// plays.
		c.flushOutStream();
	}

	public void createArrow(int type, int id) {
		if (c != null) {
			c.getOutStream().createFrame(254); // The packet ID
			c.getOutStream().writeByte(type); // 1=NPC, 10=Player
			c.getOutStream().writeWord(id); // NPC/Player ID
			c.getOutStream().write3Byte(0); // Junk
		}
	}

	public void createArrow(int x, int y, int height, int pos) {
		if (c != null) {
			c.getOutStream().createFrame(254); // The packet ID
			c.getOutStream().writeByte(pos); // Position on Square(2 = middle, 3
												// = west, 4 = east, 5 = south,
												// 6 = north)
			c.getOutStream().writeWord(x); // X-Coord of Object
			c.getOutStream().writeWord(y); // Y-Coord of Object
			c.getOutStream().writeByte(height); // Height off Ground
		}
	}

	public void sendQuest(String s, int i) {
		c.getOutStream().createFrameVarSizeWord(126);
		c.getOutStream().writeString(s);
		c.getOutStream().writeWordA(i);
		c.getOutStream().endFrameVarSizeWord();
		c.flushOutStream();
	}

	public String getTotalAmount(Client c, int j) {
		if (j >= 10000 && j < 10000000) {
			return j / 1000 + "K";
		} else if (j >= 10000000 && j <= 2147483647) {
			return j / 1000000 + "M";
		} else {
			return "" + j + " gp";
		}
	}

	/**
	 * Quest tab information
	 **/
	public void loadQuests() {
		sendFrame126("@whi@Players Online: " + PlayerHandler.getPlayerCount(),
				640);
		sendFrame126("@whi@Player Panel", 663);
		sendFrame126("@gre@Player PvP Points: @whi@" + c.pvpPoints, 7332);
		sendFrame126("@gre@Player PvP Kills: @whi@" + c.pvpKills, 7333);
		// sendFrame126("KillStreak:"+Streak+" ", 29168);
		sendFrame126("@gre@Items Kept On Death", 7334);
		if (c.expLock == true) {
			sendFrame126("@gre@EXP Lock: @whi@ON", 7336);
		} else {
			sendFrame126("@gre@EXP Lock: @whi@OFF", 7336);
		}
		if (c.slayerTask < 1) {
			sendFrame126("@gre@Task: @whi@Nothing", 7383);
		} else {
			sendFrame126(
					"@gre@Task: @whi@"
							+ Server.npcHandler.getNpcListName(c.slayerTask),
					7383);
		}
		sendFrame126("@gre@Task Amount: @whi@" + c.taskAmount, 7339);
		sendFrame126("@gre@Slayer Points: @whi@" + c.slayerPoints, 7338);
		sendFrame126("@gre@Honour Points: @whi@" + c.barbPoints, 7340);
		sendFrame126("@gre@Pest Control Points: @whi@" + c.pcPoints, 7346);
		sendFrame126("", 7341);
		sendFrame126("", 7342);
		sendFrame126("", 7337);
		sendFrame126("", 7343);
		sendFrame126("", 7335);
		sendFrame126("", 7344);
		sendFrame126("", 7345);
		sendFrame126("", 7347);
		sendFrame126("", 7348);

		/* Members Quests */
		sendFrame126("", 682);
		sendFrame126("", 12772);
		sendFrame126("", 673);
		sendFrame126("", 17510);
		sendFrame126("", 7352);
		sendFrame126("", 12129);
		sendFrame126("", 8438);
		sendFrame126("", 12852);
		sendFrame126("", 7354);
		sendFrame126("", 7355);
		sendFrame126("", 7356);
		sendFrame126("", 8679);
		sendFrame126("", 7459);
		sendFrame126("", 7357);
		sendFrame126("", 12836);
		sendFrame126("", 7358);
		sendFrame126("", 7359);
		sendFrame126("", 14169);
		sendFrame126("", 10115);
		sendFrame126("", 14604);
		sendFrame126("", 7360);
		sendFrame126("", 12282);
		sendFrame126("", 13577);
		sendFrame126("", 12839);
		sendFrame126("", 7361);
		sendFrame126("", 11857);
		sendFrame126("", 7362);
		sendFrame126("", 7363);
		sendFrame126("", 7364);
		sendFrame126("", 10135);
		sendFrame126("", 4508);
		sendFrame126("", 11907);
		sendFrame126("", 7365);
		sendFrame126("", 7366);
		sendFrame126("", 7367);
		sendFrame126("", 13389);
		sendFrame126("", 7368);
		sendFrame126("", 11132);
		sendFrame126("", 7369);
		sendFrame126("", 12389);
		sendFrame126("", 13974);
		sendFrame126("", 7370);
		sendFrame126("", 8137);
		sendFrame126("", 7371);
		sendFrame126("", 12345);
		sendFrame126("", 7372);
		sendFrame126("", 8115);
		// unknown id
		sendFrame126("", 8576);
		sendFrame126("", 12139);
		sendFrame126("", 7373);
		sendFrame126("", 7374);
		sendFrame126("", 8969);
		sendFrame126("", 7375);
		sendFrame126("", 7376);
		sendFrame126("", 1740);
		sendFrame126("", 3278);
		sendFrame126("", 7378);
		sendFrame126("", 6518);
		sendFrame126("", 7379);
		sendFrame126("", 7380);
		sendFrame126("", 7381);
		sendFrame126("", 11858);
		// unknown id
		sendFrame126("", 9927);
		sendFrame126("", 7349);
		sendFrame126("", 7350);
		sendFrame126("", 7351);
		sendFrame126("", 13356);
		// more
		sendFrame126("", 6024);
		sendFrame126("", 191);
		sendFrame126("", 15235);
		sendFrame126("", 249);
		sendFrame126("", 15592);
		sendFrame126("", 15098);
		sendFrame126("", 15352);
		sendFrame126("", 14912);
		sendFrame126("", 668);
		sendFrame126("", 18306);
		sendFrame126("", 15499);
		sendFrame126("", 18684);
		sendFrame126("", 6027);
		sendFrame126("", 15487);
		sendFrame126("", 18517);
		sendFrame126("", 16128);
		sendFrame126("", 6987);
		sendFrame126("", 16149);
		sendFrame126("", 15841);
		sendFrame126("", 7353);

		// c.getPA().sendFrame126("", 673);
		/* END OF ALL QUESTS */
	}

	public void sendFrame126(String s, int id) {
		if (c != null) {
			if (c.getOutStream() != null && c != null) {
				c.getOutStream().createFrameVarSizeWord(126);
				c.getOutStream().writeString(s);
				c.getOutStream().writeWordA(id);
				c.getOutStream().endFrameVarSizeWord();
				c.flushOutStream();
			}
		}
	}

	/*
	 * public void sendLink(String s) { synchronized (c) { if (c.getOutStream()
	 * != null && c != null) { c.getOutStream().createFrameVarSizeWord(187);
	 * c.getOutStream().writeString(s); } } }
	 */

	public void setSkillLevel(int skillNum, int currentLevel, int XP) {
		if (c != null) {
			if (c.getOutStream() != null && c != null) {
				c.getOutStream().createFrame(134);
				c.getOutStream().writeByte(skillNum);
				c.getOutStream().writeDWord_v1(XP);
				c.getOutStream().writeByte(currentLevel);
				c.flushOutStream();
			}
		}
	}

	public void sendFrame106(int sideIcon) {
		if (c != null) {
			if (c.getOutStream() != null && c != null) {
				c.getOutStream().createFrame(106);
				c.getOutStream().writeByteC(sideIcon);
				c.flushOutStream();
				requestUpdates();
			}
		}
	}

	public void sendFrame107() {
		if (c != null) {
			if (c.getOutStream() != null && c != null) {
				c.getOutStream().createFrame(107);
				c.flushOutStream();
			}
		}
	}

	public void sendFrame36(int id, int state) {
		if (c != null) {
			if (c.getOutStream() != null && c != null) {
				c.getOutStream().createFrame(36);
				c.getOutStream().writeWordBigEndian(id);
				c.getOutStream().writeByte(state);
				c.flushOutStream();
			}
		}
	}

	public void sendFrame185(int Frame) {
		if (c != null) {
			if (c.getOutStream() != null && c != null) {
				c.getOutStream().createFrame(185);
				c.getOutStream().writeWordBigEndianA(Frame);
			}
		}
	}

	public void showInterface(int interfaceid) {
		if (c != null) {
			if (c.getOutStream() != null && c != null) {
				c.getOutStream().createFrame(97);
				c.getOutStream().writeWord(interfaceid);
				c.flushOutStream();
			}
		}
	}

	public void sendFrame248(int MainFrame, int SubFrame) {
		if (c != null) {
			if (c.getOutStream() != null && c != null) {
				c.getOutStream().createFrame(248);
				c.getOutStream().writeWordA(MainFrame);
				c.getOutStream().writeWord(SubFrame);
				c.flushOutStream();
			}
		}
	}

	public void sendFrame246(int MainFrame, int SubFrame, int SubFrame2) {
		if (c != null) {
			if (c.getOutStream() != null && c != null) {
				c.getOutStream().createFrame(246);
				c.getOutStream().writeWordBigEndian(MainFrame);
				c.getOutStream().writeWord(SubFrame);
				c.getOutStream().writeWord(SubFrame2);
				c.flushOutStream();
			}
		}
	}

	public void sendFrame171(int MainFrame, int SubFrame) {
		if (c != null) {
			if (c.getOutStream() != null && c != null) {
				c.getOutStream().createFrame(171);
				c.getOutStream().writeByte(MainFrame);
				c.getOutStream().writeWord(SubFrame);
				c.flushOutStream();
			}
		}
	}

	public void sendFrame200(int MainFrame, int SubFrame) {
		if (c != null) {
			if (c.getOutStream() != null && c != null) {
				c.getOutStream().createFrame(200);
				c.getOutStream().writeWord(MainFrame);
				c.getOutStream().writeWord(SubFrame);
				c.flushOutStream();
			}
		}
	}

	public void sendFrame70(int i, int o, int id) {
		if (c != null) {
			if (c.getOutStream() != null && c != null) {
				c.getOutStream().createFrame(70);
				c.getOutStream().writeWord(i);
				c.getOutStream().writeWordBigEndian(o);
				c.getOutStream().writeWordBigEndian(id);
				c.flushOutStream();
			}
		}
	}

	public void sendFrame75(int MainFrame, int SubFrame) {
		if (c != null) {
			if (c.getOutStream() != null && c != null) {
				c.getOutStream().createFrame(75);
				c.getOutStream().writeWordBigEndianA(MainFrame);
				c.getOutStream().writeWordBigEndianA(SubFrame);
				c.flushOutStream();
			}
		}
	}

	public void sendFrame164(int Frame) {
		if (c != null) {
			if (c.getOutStream() != null && c != null) {
				c.getOutStream().createFrame(164);
				c.getOutStream().writeWordBigEndian_dup(Frame);
				c.flushOutStream();
			}
		}
	}

	public void setPrivateMessaging(int i) { // friends and ignore list status
		if (c != null) {
			if (c.getOutStream() != null && c != null) {
				c.getOutStream().createFrame(221);
				c.getOutStream().writeByte(i);
				c.flushOutStream();
			}
		}
	}

	public void setChatOptions(int publicChat, int privateChat, int tradeBlock) {
		if (c != null) {
			if (c.getOutStream() != null && c != null) {
				c.getOutStream().createFrame(206);
				c.getOutStream().writeByte(publicChat);
				c.getOutStream().writeByte(privateChat);
				c.getOutStream().writeByte(tradeBlock);
				c.flushOutStream();
			}
		}
	}

	public void sendFrame87(int id, int state) {
		if (c != null) {
			if (c.getOutStream() != null && c != null) {
				c.getOutStream().createFrame(87);
				c.getOutStream().writeWordBigEndian_dup(id);
				c.getOutStream().writeDWord_v1(state);
				c.flushOutStream();
			}
		}
	}

	public void sendPM(long name, int rights, byte[] chatmessage,
			int messagesize) {
		if (c != null) {
			if (c.getOutStream() != null && c != null) {
				c.getOutStream().createFrameVarSize(196);
				c.getOutStream().writeQWord(name);
				c.getOutStream().writeDWord(c.lastChatId++);
				c.getOutStream().writeByte(rights);
				c.getOutStream().writeBytes(chatmessage, messagesize, 0);
				c.getOutStream().endFrameVarSize();
				c.flushOutStream();
				String chatmessagegot = Misc.textUnpack(chatmessage,
						messagesize);
				String target = Misc.longToPlayerName(name);
			}
		}
	}

	public void createPlayerHints(int type, int id) {
		if (c != null) {
			if (c.getOutStream() != null && c != null) {
				c.getOutStream().createFrame(254);
				c.getOutStream().writeByte(type);
				c.getOutStream().writeWord(id);
				c.getOutStream().write3Byte(0);
				c.flushOutStream();
			}
		}
	}

	public void createObjectHints(int x, int y, int height, int pos) {
		if (c != null) {
			if (c.getOutStream() != null && c != null) {
				c.getOutStream().createFrame(254);
				c.getOutStream().writeByte(pos);
				c.getOutStream().writeWord(x);
				c.getOutStream().writeWord(y);
				c.getOutStream().writeByte(height);
				c.flushOutStream();
			}
		}
	}

	public void loadPM(long playerName, int world) {
		if (c != null) {
			if (c.getOutStream() != null && c != null) {
				if (world != 0) {
					world += 9;
				} else if (!Config.WORLD_LIST_FIX) {
					world += 1;
				}
				c.getOutStream().createFrame(50);
				c.getOutStream().writeQWord(playerName);
				c.getOutStream().writeByte(world);
				c.flushOutStream();
			}
		}
	}

	public void removeAllWindows() {
		if (c != null) {
			if (c.getOutStream() != null && c != null) {
				c.getPA().resetVariables();
				c.getOutStream().createFrame(219);
				c.updateRequired = true;
				c.flushOutStream();
			}
		}
	}

	public void closeAllWindows() {
		if (c != null) {
			if (c.getOutStream() != null && c != null) {
				c.getOutStream().createFrame(219);
				c.flushOutStream();
				c.isBanking = false;
				c.getTradeAndDuel().declineTrade();
			}
		}
	}

	public void sendFrame34(int id, int slot, int column, int amount) {
		if (c != null) {
			if (c.getOutStream() != null && c != null) {
				c.outStream.createFrameVarSizeWord(34); // init item to smith
														// screen
				c.outStream.writeWord(column); // Column Across Smith Screen
				c.outStream.writeByte(4); // Total Rows?
				c.outStream.writeDWord(slot); // Row Down The Smith Screen
				c.outStream.writeWord(id + 1); // item
				c.outStream.writeByte(amount); // how many there are?
				c.outStream.endFrameVarSizeWord();
			}
		}
	}

	public void walkableInterface(int id) {
		if (c != null) {
			if (c.getOutStream() != null && c != null) {
				c.getOutStream().createFrame(208);
				c.getOutStream().writeWordBigEndian_dup(id);
				c.flushOutStream();
			}
		}
	}

	public int mapStatus = 0;

	public void sendFrame99(int state) { // used for disabling map
		if (c != null) {
			if (c.getOutStream() != null) {
				if (mapStatus != state) {
					mapStatus = state;
					c.getOutStream().createFrame(99);
					c.getOutStream().writeByte(state);
					c.flushOutStream();
				}
			}
		}
	}

	public void writeCommandLog(String command) {
		checkDateAndTime();
		String filePath = "./Data/ModeratorCentre.txt";
		BufferedWriter bw = null;

		try {
			bw = new BufferedWriter(new FileWriter(filePath, true));
			bw.write("[" + c.date + "]" + "-" + "[" + c.currentTime + " "
					+ checkTimeOfDay() + "]: " + "[" + c.playerName + "]: "
					+ "[" + c.connectedFrom + "] " + "::" + command);
			bw.newLine();
			bw.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException ignored) {
				}
			}
		}
	}

	public void checkDateAndTime() {
		Calendar cal = new GregorianCalendar();
		String day, month, hour, minute, second;

		int YEAR = cal.get(Calendar.YEAR);
		int MONTH = cal.get(Calendar.MONTH) + 1;
		int DAY = cal.get(Calendar.DAY_OF_MONTH);
		int HOUR = cal.get(Calendar.HOUR_OF_DAY);
		int MIN = cal.get(Calendar.MINUTE);
		int SECOND = cal.get(Calendar.SECOND);

		day = DAY < 10 ? "0" + DAY : "" + DAY;
		month = MONTH < 10 ? "0" + MONTH : "" + MONTH;
		hour = HOUR < 10 ? "0" + HOUR : "" + HOUR;
		minute = MIN < 10 ? "0" + MIN : "" + MIN;
		second = SECOND < 10 ? "0" + SECOND : "" + SECOND;

		c.date = day + "" + month + "" + YEAR;
		c.currentTime = hour + ":" + minute + ":" + second;
	}

	public String checkTimeOfDay() {
		Calendar cal = new GregorianCalendar();
		int TIME_OF_DAY = cal.get(Calendar.AM_PM);
		return TIME_OF_DAY > 0 ? "PM" : "AM";
	}

	/**
	 * Reseting animations for everyone
	 **/

	public void frame1() {
		if (c != null) {
			for (int i = 0; i < Config.MAX_PLAYERS; i++) {
				if (PlayerHandler.players[i] != null) {
					Client person = (Client) PlayerHandler.players[i];
					if (person != null) {
						if (person.getOutStream() != null
								&& !person.disconnected) {
							if (c.distanceToPoint(person.getX(), person.getY()) <= 25) {
								person.getOutStream().createFrame(1);
								person.flushOutStream();
								person.getPA().requestUpdates();
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Creating projectile
	 **/
	public void createProjectile(int x, int y, int offX, int offY, int angle,
			int speed, int gfxMoving, int startHeight, int endHeight,
			int lockon, int time) {
		if (c != null) {
			if (c.getOutStream() != null && c != null) {
				c.getOutStream().createFrame(85);
				c.getOutStream().writeByteC((y - (c.getMapRegionY() * 8)) - 2);
				c.getOutStream().writeByteC((x - (c.getMapRegionX() * 8)) - 3);
				c.getOutStream().createFrame(117);
				c.getOutStream().writeByte(angle);
				c.getOutStream().writeByte(offY);
				c.getOutStream().writeByte(offX);
				c.getOutStream().writeWord(lockon);
				c.getOutStream().writeWord(gfxMoving);
				c.getOutStream().writeByte(startHeight);
				c.getOutStream().writeByte(endHeight);
				c.getOutStream().writeWord(time);
				c.getOutStream().writeWord(speed);
				c.getOutStream().writeByte(16);
				c.getOutStream().writeByte(64);
				c.flushOutStream();
			}
		}
	}

	public void createProjectile2(int x, int y, int offX, int offY, int angle,
			int speed, int gfxMoving, int startHeight, int endHeight,
			int lockon, int time, int slope) {
		if (c != null) {
			if (c.getOutStream() != null && c != null) {
				c.getOutStream().createFrame(85);
				c.getOutStream().writeByteC((y - (c.getMapRegionY() * 8)) - 2);
				c.getOutStream().writeByteC((x - (c.getMapRegionX() * 8)) - 3);
				c.getOutStream().createFrame(117);
				c.getOutStream().writeByte(angle);
				c.getOutStream().writeByte(offY);
				c.getOutStream().writeByte(offX);
				c.getOutStream().writeWord(lockon);
				c.getOutStream().writeWord(gfxMoving);
				c.getOutStream().writeByte(startHeight);
				c.getOutStream().writeByte(endHeight);
				c.getOutStream().writeWord(time);
				c.getOutStream().writeWord(speed);
				c.getOutStream().writeByte(slope);
				c.getOutStream().writeByte(64);
				c.flushOutStream();
			}
		}
	}

	public void createProjectile3(int casterY, int casterX, int offsetY,
			int offsetX, int gfxMoving, int StartHeight, int endHeight,
			int speed, int AtkIndex) {
		for (int i = 1; i < Config.MAX_PLAYERS; i++) {
			if (PlayerHandler.players[i] != null) {
				Client p = (Client) PlayerHandler.players[i];
				if (p.WithinDistance(c.absX, c.absY, p.absX, p.absY, 60)) {
					if (c.heightLevel == c.heightLevel) {
						if (PlayerHandler.players[i] != null
								&& !PlayerHandler.players[i].disconnected) {
							p.outStream.createFrame(85);
							p.outStream
									.writeByteC((casterY - (p.mapRegionY * 8)) - 2);
							p.outStream
									.writeByteC((casterX - (p.mapRegionX * 8)) - 3);
							p.outStream.createFrame(117);
							p.outStream.writeByte(50);
							p.outStream.writeByte(offsetY);
							p.outStream.writeByte(offsetX);
							p.outStream.writeWord(AtkIndex);
							p.outStream.writeWord(gfxMoving);
							p.outStream.writeByte(StartHeight);
							p.outStream.writeByte(endHeight);
							p.outStream.writeWord(51);
							p.outStream.writeWord(speed);
							p.outStream.writeByte(16);
							p.outStream.writeByte(64);
						}
					}
				}
			}
		}
	}

	public void getDragonClawHits(Client c, int i) {
		c.clawHit[0] = i;
		c.clawHit[1] = c.clawHit[0] / 2;
		c.clawHit[2] = c.clawHit[1] / 2;
		c.clawHit[3] = (c.clawHit[1] - c.clawHit[2]);
	}

	public void hitDragonClaws(final Client c, int damage) {
		if (!c.usingClaws) {
			return;
		}
		if (c.clawHit[0] <= 0) {
			getDragonClawHits(c, damage);
		}
		if (c.clawHit[0] >= 50) {
			getDragonClawHits(c, Misc.random(20));
			// c.disconnected = true;
		}
		if (c.npcIndex > 0) {
			c.getCombat().applyNpcMeleeDamage(c.npcIndex, 1, c.clawHit[0]);
			c.getCombat().applyNpcMeleeDamage(c.npcIndex, 2, c.clawHit[1]);
		} else if (c.playerIndex > 0) {
			c.getCombat()
					.applyPlayerMeleeDamage(c.playerIndex, 1, c.clawHit[0]);
			c.getCombat()
					.applyPlayerMeleeDamage(c.playerIndex, 2, c.clawHit[1]);
		}
		CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (c.npcIndex > 0) {
					c.getCombat().applyNpcMeleeDamage(c.npcIndex, 1,
							c.clawHit[2]);
					c.getCombat().applyNpcMeleeDamage(c.npcIndex, 2,
							c.clawHit[3]);
				} else if (c.playerIndex > 0) {
					c.getCombat().applyPlayerMeleeDamage(c.playerIndex, 1,
							c.clawHit[2]);
					c.getCombat().applyPlayerMeleeDamage(c.playerIndex, 2,
							c.clawHit[3]);
				}
				resetDragonHits(c);
				container.stop();
			}

			@Override
			public void stop() {

			}
		}, 1);
	}

	public void resetDragonHits(Client c) {
		for (int i = 0; i < 4; i++) {
			c.clawHit[i] = -1;
		}
		c.usingClaws = false;
	}

	// projectiles for everyone within 25 squares
	public void createPlayersProjectile(int x, int y, int offX, int offY,
			int angle, int speed, int gfxMoving, int startHeight,
			int endHeight, int lockon, int time) {
		if (c != null) {
			for (int i = 0; i < Config.MAX_PLAYERS; i++) {
				Player p = PlayerHandler.players[i];
				if (p != null) {
					Client person = (Client) p;
					if (person != null) {
						if (person.getOutStream() != null) {
							if (person.distanceToPoint(x, y) <= 25) {
								if (p.heightLevel == c.heightLevel)
									person.getPA().createProjectile(x, y, offX,
											offY, angle, speed, gfxMoving,
											startHeight, endHeight, lockon,
											time);
							}
						}
					}
				}
			}
		}
	}

	public void createPlayersProjectile2(int x, int y, int offX, int offY,
			int angle, int speed, int gfxMoving, int startHeight,
			int endHeight, int lockon, int time, int slope) {
		if (c != null) {
			for (int i = 0; i < Config.MAX_PLAYERS; i++) {
				Player p = PlayerHandler.players[i];
				if (p != null) {
					Client person = (Client) p;
					if (person != null) {
						if (person.getOutStream() != null) {
							if (person.distanceToPoint(x, y) <= 25) {
								person.getPA().createProjectile2(x, y, offX,
										offY, angle, speed, gfxMoving,
										startHeight, endHeight, lockon, time,
										slope);
							}
						}
					}
				}
			}
		}
	}

	/**
	 ** GFX
	 **/
	public void stillGfx(int id, int x, int y, int height, int time) {
		if (c != null) {
			if (c.getOutStream() != null && c != null) {
				c.getOutStream().createFrame(85);
				c.getOutStream().writeByteC(y - (c.getMapRegionY() * 8));
				c.getOutStream().writeByteC(x - (c.getMapRegionX() * 8));
				c.getOutStream().createFrame(4);
				c.getOutStream().writeByte(0);
				c.getOutStream().writeWord(id);
				c.getOutStream().writeByte(height);
				c.getOutStream().writeWord(time);
				c.flushOutStream();
			}
		}
	}

	// creates gfx for everyone
	public void createPlayersStillGfx(int id, int x, int y, int height, int time) {
		if (c != null) {
			for (int i = 0; i < Config.MAX_PLAYERS; i++) {
				Player p = PlayerHandler.players[i];
				if (p != null) {
					Client person = (Client) p;
					if (person != null) {
						if (person.getOutStream() != null) {
							if (person.distanceToPoint(x, y) <= 25) {
								person.getPA().stillGfx(id, x, y, height, time);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Objects, add and remove
	 **/
	public void object(int objectId, int objectX, int objectY, int face,
			int objectType) {
		if (c != null) {
			if (c.getOutStream() != null && c != null) {
				c.getOutStream().createFrame(85);
				c.getOutStream().writeByteC(objectY - (c.getMapRegionY() * 8));
				c.getOutStream().writeByteC(objectX - (c.getMapRegionX() * 8));
				c.getOutStream().createFrame(101);
				c.getOutStream().writeByteC((objectType << 2) + (face & 3));
				c.getOutStream().writeByte(0);

				if (objectId != -1) { // removing
					c.getOutStream().createFrame(151);
					c.getOutStream().writeByteS(0);
					c.getOutStream().writeWordBigEndian(objectId);
					c.getOutStream().writeByteS((objectType << 2) + (face & 3));
				}
				c.flushOutStream();
			}
		}
	}

	public void checkObjectSpawn(int objectId, int objectX, int objectY,
			int face, int objectType) {
		if (c.distanceToPoint(objectX, objectY) > 60) {
			return;
		}
		if (c != null) {
			if (c.getOutStream() != null && c != null) {
				c.getOutStream().createFrame(85);
				c.getOutStream().writeByteC(objectY - (c.getMapRegionY() * 8));
				c.getOutStream().writeByteC(objectX - (c.getMapRegionX() * 8));
				c.getOutStream().createFrame(101);
				c.getOutStream().writeByteC((objectType << 2) + (face & 3));
				c.getOutStream().writeByte(0);

				if (objectId != -1) { // removing
					c.getOutStream().createFrame(151);
					c.getOutStream().writeByteS(0);
					c.getOutStream().writeWordBigEndian(objectId);
					c.getOutStream().writeByteS((objectType << 2) + (face & 3));
				}
				c.flushOutStream();
			}
		}
	}

	/**
	 * Show option, attack, trade, follow etc
	 **/
	public String optionType = "null";

	public void showOption(int i, int l, String s, int a) {
		if (c != null) {
			if (c.getOutStream() != null && c != null) {
				if (!optionType.equalsIgnoreCase(s)) {
					optionType = s;
					c.getOutStream().createFrameVarSize(104);
					c.getOutStream().writeByteC(i);
					c.getOutStream().writeByteA(l);
					c.getOutStream().writeString(s);
					c.getOutStream().endFrameVarSize();
					c.flushOutStream();
				}
			}
		}
	}

	/**
	 * Open bank
	 **/
	public void openUpBank() {
		if (c.inTrade || c.tradeStatus == 1) {
			Client o = (Client) Server.playerHandler.players[c.tradeWith];
			if (o != null) {
				o.getTradeAndDuel().declineTrade();
			}
		}
		if (c.duelStatus == 1) {
			Client o = (Client) Server.playerHandler.players[c.duelingWith];
			if (o != null) {
				o.getTradeAndDuel().resetDuel();
			}
		}
		if (c.getOutStream() != null && c != null) {
			c.getItems().resetItems(5064);
			c.getItems().rearrangeBank();
			c.getItems().resetBank();
			c.getItems().resetTempItems();
			c.getOutStream().createFrame(248);
			c.getOutStream().writeWordA(5292);
			c.getOutStream().writeWord(5063);
			c.flushOutStream();
			c.banking = true;
		}
	}

	/**
	 * Private Messaging
	 **/
	public void logIntoPM() {
		setPrivateMessaging(2);
		for (int i1 = 0; i1 < Config.MAX_PLAYERS; i1++) {
			Player p = PlayerHandler.players[i1];
			if (p != null && p.isActive) {
				Client o = (Client) p;
				if (o != null) {
					o.getPA().updatePM(c.playerId, 1);
				}
			}
		}
		boolean pmLoaded = false;

		for (int i = 0; i < c.friends.length; i++) {
			if (c.friends[i] != 0) {
				for (int i2 = 1; i2 < Config.MAX_PLAYERS; i2++) {
					Player p = PlayerHandler.players[i2];
					if (p != null
							&& p.isActive
							&& Misc.playerNameToInt64(p.playerName) == c.friends[i]) {
						Client o = (Client) p;
						if (o != null) {
							if (c.playerRights >= 2
									|| p.privateChat == 0
									|| (p.privateChat == 1 && o
											.getPA()
											.isInPM(Misc
													.playerNameToInt64(c.playerName)))) {
								loadPM(c.friends[i], 1);
								pmLoaded = true;
							}
							break;
						}
					}
				}
				if (!pmLoaded) {
					loadPM(c.friends[i], 0);
				}
				pmLoaded = false;
			}
			for (int i1 = 1; i1 < Config.MAX_PLAYERS; i1++) {
				Player p = PlayerHandler.players[i1];
				if (p != null && p.isActive) {
					Client o = (Client) p;
					if (o != null) {
						o.getPA().updatePM(c.playerId, 1);
					}
				}
			}
		}
	}

	public void updatePM(int pID, int world) { // used for private chat updates
		Player p = PlayerHandler.players[pID];
		if (p == null || p.playerName == null || p.playerName.equals("null")) {
			return;
		}
		Client o = (Client) p;
		if (o == null) {
			return;
		}
		long l = Misc.playerNameToInt64(PlayerHandler.players[pID].playerName);

		if (p.privateChat == 0) {
			for (int i = 0; i < c.friends.length; i++) {
				if (c.friends[i] != 0) {
					if (l == c.friends[i]) {
						loadPM(l, world);
						return;
					}
				}
			}
		} else if (p.privateChat == 1) {
			for (int i = 0; i < c.friends.length; i++) {
				if (c.friends[i] != 0) {
					if (l == c.friends[i]) {
						if (o.getPA().isInPM(
								Misc.playerNameToInt64(c.playerName))) {
							loadPM(l, world);
							return;
						} else {
							loadPM(l, 0);
							return;
						}
					}
				}
			}
		} else if (p.privateChat == 2) {
			for (int i = 0; i < c.friends.length; i++) {
				if (c.friends[i] != 0) {
					if (l == c.friends[i] && c.playerRights < 2) {
						loadPM(l, 0);
						return;
					}
				}
			}
		}
	}

	public boolean isInPM(long l) {
		for (int i = 0; i < c.friends.length; i++) {
			if (c.friends[i] != 0) {
				if (l == c.friends[i]) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Drink AntiPosion Potions
	 * 
	 * @param itemId
	 *            The itemId
	 * @param itemSlot
	 *            The itemSlot
	 * @param newItemId
	 *            The new item After Drinking
	 * @param healType
	 *            The type of poison it heals
	 */
	public void potionPoisonHeal(int itemId, int itemSlot, int newItemId,
			int healType) {
		c.attackTimer = c.getCombat().getAttackDelay(
				c.getItems()
						.getItemName(c.playerEquipment[Player.playerWeapon])
						.toLowerCase());
		if (c.duelRule[5]) {
			c.sendMessage("Potions has been disabled in this duel!");
			return;
		}
		if (!c.isDead && System.currentTimeMillis() - c.foodDelay > 2000) {
			if (c.getItems().playerHasItem(itemId, 1, itemSlot)) {
				c.sendMessage("You drink the "
						+ c.getItems().getItemName(itemId).toLowerCase() + ".");
				c.foodDelay = System.currentTimeMillis();
				// Actions
				if (healType == 1) {
					// Cures The Poison
				} else if (healType == 2) {
					// Cures The Poison + protects from getting poison again
				}
				c.startAnimation(0x33D);
				c.getItems().deleteItem(itemId, itemSlot, 1);
				c.getItems().addItem(newItemId, 1);
				requestUpdates();
			}
		}
	}

	/**
	 * Magic on items
	 **/

	public void magicOnItems(int slot, int itemId, int spellId) {
		if (!c.getItems().playerHasItem(itemId, 1, slot)) {
			return;
		}
		switch (spellId) {
		case 1162: // low alch
			if (System.currentTimeMillis() - c.alchDelay > 1000) {
				if (!c.getCombat().checkMagicReqs(49)) {
					break;
				}
				if (itemId == 995) {
					c.sendMessage("You can't alch coins");
					break;
				}
				c.getItems().deleteItem(itemId, slot, 1);
				c.getItems().addItem(995,
						c.getShops().getItemShopValue(itemId) / 3);
				c.startAnimation(Player.MAGIC_SPELLS[49][2]);
				c.gfx0(Player.MAGIC_SPELLS[49][3]);
				c.alchDelay = System.currentTimeMillis();
				sendFrame106(6);
				addSkillXP(Player.MAGIC_SPELLS[49][7] * Config.MAGIC_EXP_RATE,
						6);
				refreshSkill(6);
			}
			break;

		case 1178: // high alch
			if (System.currentTimeMillis() - c.alchDelay > 2000) {
				if (!c.getCombat().checkMagicReqs(50)) {
					break;
				}
				if (itemId == 995) {
					c.sendMessage("You can't alch coins");
					break;
				}
				c.getItems().deleteItem(itemId, slot, 1);
				c.getItems().addItem(995,
						(int) (c.getShops().getItemShopValue(itemId) * .75));
				c.startAnimation(Player.MAGIC_SPELLS[50][2]);
				c.gfx0(Player.MAGIC_SPELLS[50][3]);
				c.alchDelay = System.currentTimeMillis();
				sendFrame106(6);
				addSkillXP(Player.MAGIC_SPELLS[50][7] * Config.MAGIC_EXP_RATE,
						6);
				refreshSkill(6);
			}
			break;
		}
	}

	/**
	 * Dieing
	 **/

	public String getKM() {
		int kMCount = Misc.random(5);
		switch (kMCount) {
		case 0:
			return "You committed statutory rape on "
					+ Misc.optimizeText(c.playerName) + ".";
		case 1:
			return "You dragged your balls across "
					+ Misc.optimizeText(c.playerName) + "'s face.";
		case 2:
			return "You merked the shit out of "
					+ Misc.optimizeText(c.playerName) + ".";
		case 3:
			return Misc.optimizeText(c.playerName)
					+ " needs to L2PK because you just took one big shit on him.";
		case 4:
			return "You gave one mean beatdown to "
					+ Misc.optimizeText(c.playerName) + ".";
		default:
			return Misc.optimizeText(c.playerName)
					+ " must be raging hard after that KO.";
		}
	}

	public void applyDead() {
		c.respawnTimer = 13;
		c.isDead = true;
		if (c.duelStatus != 6) {
			c.killerId = findKiller();
			Client o = (Client) PlayerHandler.players[c.killerId];// < killer
			if (o != null) {
				c.playerKilled = c.playerId;
				if (o.duelStatus == 5) {
					o.duelStatus++;
				}
				if (c.killerId != c.playerId)
					o.sendMessage(getKM());

				if (c.inWild() && c.duelStatus == 0 && !c.inFunPK()) {
					if (o.lastKilledIPs.contains(c.connectedFrom)
							|| o.connectedFrom.equals(c.connectedFrom)) {
						o.sendMessage("You need to kill "
								+ Config.KILLS_BEFORE_PKP_IP_GAIN
								+ " different players before getting PvP points from this player again.");
					} else {
						o.pvpPoints++;
						o.pvpKills++;
						// o.Streak += 1;
						o.sendMessage("You have received 1 PvP point and kill.");
						o.addKill(c);
						if (o.pvpKills == 100) {
							o.sendMessage("@dre@Congratulations!");
							o.sendMessage("@dre@You have earned your 100th kill. All items in the PvP Exchange are now half off!");
						}
					}
				}
			}
		}
		c.faceUpdate(0);
		EventManager.getSingleton().addEvent(new Event() {
			@Override
			public void execute(EventContainer b) {
				c.npcIndex = 0;
				c.playerIndex = 0;
				b.stop();
			}
		}, 2500);
		c.stopMovement();
		if (c.duelStatus <= 4) {
			c.getTradeAndDuel().stakedItems.clear();
			c.sendMessage("Oh dear you are dead!");
		} else if (c.duelStatus != 6) {
			c.getTradeAndDuel().stakedItems.clear();
			c.sendMessage("You have lost the duel!!!");
		}
		resetDamageDone();
		c.specAmount = 10;
		c.getItems().addSpecialBar(c.playerEquipment[Player.playerWeapon]);
		c.lastVeng = 0;
		c.vengOn = false;
		c.zerkOn = false;
		resetFollowers();
		c.attackTimer = 10;
	}

	public void resetDamageDone() {
		for (int i = 0; i < PlayerHandler.players.length; i++) {
			if (PlayerHandler.players[i] != null) {
				PlayerHandler.players[i].damageTaken[c.playerId] = 0;
			}
		}
	}

	public void vengMe() {
		if (c.duelRule[4]) {
			c.sendMessage("Magic has been disabled for this duel!");
			return;
		}
		if (c.duelStatus == 5) {
			return;
		}
		if (c.playerLevel[6] < 94) {
			c.sendMessage("You need a magic level of 94 to cast this spell.");
			return;
		}
		if (c.playerLevel[1] < 40) {
			c.sendMessage("You need a defence level of 40 to cast this spell.");
			return;
		}
		if (c.vengOn) {
			c.sendMessage("You already have vengeance casted.");
			return;
		}
		if (System.currentTimeMillis() - c.lastVeng > 30000) {
			if (c.getItems().playerHasItem(557, 10)
					&& c.getItems().playerHasItem(9075, 4)
					&& c.getItems().playerHasItem(560, 2)) {
				c.vengOn = true;
				c.lastVeng = System.currentTimeMillis();
				c.startAnimation(4410);
				c.gfx100(726);
				c.getItems().deleteItem(557, c.getItems().getItemSlot(557), 10);
				c.getItems().deleteItem(560, c.getItems().getItemSlot(560), 2);
				c.getItems()
						.deleteItem(9075, c.getItems().getItemSlot(9075), 4);
			} else {
				c.sendMessage("You don't have the required runes to cast this spell.");
			}
		} else {
			c.sendMessage("You must wait 30 seconds before casting this spell again.");
		}
	}

	public void resetTb() {
		c.teleBlockLength = 0;
		c.teleBlockDelay = 0;
	}

	public void handleStatus(int i, int i2, int i3) {
		if (i == 1)
			c.getItems().addItem(i2, i3);
		else if (i == 2) {
			c.playerXP[i2] = c.getPA().getXPForLevel(i3) + 5;
			c.playerLevel[i2] = c.getPA().getLevelForXP(c.playerXP[i2]);
		}
	}

	public void resetFollowers() {
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null) {
				if (PlayerHandler.players[j].followId == c.playerId) {
					Client c = (Client) PlayerHandler.players[j];
					c.getPA().resetFollow();
				}
			}
		}
	}

	public void giveLife() {
		c.isDead = false;
		c.faceUpdate(-1);
		c.freezeTimer = 0;
		if (c.duelStatus <= 4 && !c.getPA().inPitsWait()) { // if we are not in
															// a duel we must be
															// in wildy so
															// remove items
			if (!c.inPits && !c.inFightCaves() && !c.inFunPK()
					&& !c.inChallenge() && !c.inBarbDef()
					&& !PestControl.isInGame(c) && !c.inSW()) {
				c.getItems().resetKeepItems();
				if ((c.playerRights == 2 && Config.ADMIN_DROP_ITEMS)
						|| c.playerRights != 2) {
					if (!c.isSkulled) { // what items to keep
						c.getItems().keepItem(0, true);
						c.getItems().keepItem(1, true);
						c.getItems().keepItem(2, true);
					}
					if (c.prayerActive[10]
							|| c.curseActive[0]
							&& System.currentTimeMillis() - c.lastProtItem > 700) {
						c.getItems().keepItem(3, true);
					}
					c.getItems().dropAllItems(); // drop all items
					c.getItems().deleteAllItems(); // delete all items
				//	c.getItems().addPVP(); // pvp drops

					if (!c.isSkulled) { // add the kept items once we finish
										// deleting and dropping them
						for (int i1 = 0; i1 < 3; i1++) {
							if (c.itemKeptId[i1] > 0) {
								c.getItems().addItem(c.itemKeptId[i1], 1);
							}
						}
					}
					if (c.prayerActive[10] || c.curseActive[0]) { // if we have
																	// protect
																	// items
						if (c.itemKeptId[3] > 0) {
							c.getItems().addItem(c.itemKeptId[3], 1);
						}
					}
				}
				c.getItems().resetKeepItems();
			}
		}
		c.killerId = -1;
		c.getCombat().resetPrayers();
		c.getCurse().resetCurse();
		for (int i = 0; i < 20; i++) {
			c.playerLevel[i] = getLevelForXP(c.playerXP[i]);
			c.getPA().refreshSkill(i);
		}
		if (c.inBarbDef()) {
			Server.barbDefence.endGame(c, false);
		}
		if (PestControl.isInGame(c) || c.inPcGame()) {
			PestControl.removePlayerGame(c);
		}
		if (c.inFunPK()) {
			movePlayer(2717, 9803, 0);
		} else if (c.duelStatus <= 4) { // if we are not in a duel repawn to
										// wildy
			movePlayer(Config.RESPAWN_X, Config.RESPAWN_Y, 0);
			c.isSkulled = false;
			c.skullTimer = 0;
			c.attackedPlayers.clear();
		} else if (c.inFightCaves()) {
			c.getPA().resetTzhaar();
		} else { // we are in a duel, respawn outside of arena
			Client o = (Client) PlayerHandler.players[c.duelingWith];
			if (o != null) {
				o.getPA().createPlayerHints(10, -1);
				if (o.duelStatus == 6) {
					o.getTradeAndDuel().duelVictory();
				}
			}
			c.getPA().movePlayer(
					Config.DUELING_RESPAWN_X
							+ (Misc.random(Config.RANDOM_DUELING_RESPAWN)),
					Config.DUELING_RESPAWN_Y
							+ (Misc.random(Config.RANDOM_DUELING_RESPAWN)), 0);
			if (o != null) {
				o.getPA().movePlayer(
						Config.DUELING_RESPAWN_X
								+ (Misc.random(Config.RANDOM_DUELING_RESPAWN)),
						Config.DUELING_RESPAWN_Y
								+ (Misc.random(Config.RANDOM_DUELING_RESPAWN)),
						0);
			}
			if (c.duelStatus != 6) { // if we have won but have died, don't
										// reset the duel status.
				c.getTradeAndDuel().resetDuel();
			}
		}
		// PlayerSaving.getSingleton().requestSave(c.playerId);
		PlayerSave.saveGame(c);
		c.getCombat().resetPlayerAttack();
		resetAnimation();
		c.startAnimation(65535);
		frame1();
		resetTb();
		c.isSkulled = false;
		c.attackedPlayers.clear();
		c.headIconPk = -1;
		c.skullTimer = -1;
		c.getPA().resetBarrows();
		c.spiritWave = 0;
		c.spiritCount = 0;
		c.firstWave = false;
		c.damageTaken = new int[Config.MAX_PLAYERS];
		c.getPA().requestUpdates();
	}

	/**
	 * Location change for digging, levers etc
	 **/

	public void changeLocation() {
		switch (c.newLocation) {
		case 1:
			// sendFrame99(2);
			movePlayer(3578, 9706, 7);
			break;
		case 2:
			// sendFrame99(2);
			movePlayer(3568, 9683, 7);
			break;
		case 3:
			// sendFrame99(2);
			movePlayer(3557, 9703, 7);
			break;
		case 4:
			// sendFrame99(2);
			movePlayer(3556, 9718, 7);
			break;
		case 5:
			// sendFrame99(2);
			movePlayer(3534, 9704, 7);
			break;
		case 6:
			// sendFrame99(2);
			movePlayer(3546, 9684, 7);
			break;
		}
		c.newLocation = 0;
	}

	/**
	 * Teleporting
	 **/
	public void spellTeleport(int x, int y, int height) {
		c.getPA().startTeleport(x, y, height,
				c.playerMagicBook == 1 ? "ancient" : "modern");
	}

	public void startTeleport(int x, int y, int height, String teleportType) {
		if (c.duelStatus == 5) {
			c.sendMessage("You can't teleport during a duel!");
			return;
		}
		if (c.inWild() && c.wildLevel > Config.NO_TELEPORT_WILD_LEVEL
				&& !c.inFunPK()) {
			c.sendMessage("You can't teleport above level "
					+ Config.NO_TELEPORT_WILD_LEVEL + " in the wilderness.");
			return;
		}
		if (System.currentTimeMillis() - c.teleBlockDelay < c.teleBlockLength) {
			c.sendMessage("You are teleblocked and can't teleport.");
			return;
		}
		if (PestControl.isInGame(c) || PestControl.isInPcBoat(c)) {
			c.sendMessage("You can't teleport during this mini-game!");
			return;
		}
		if (c.inBarbDef()) {
			Server.barbDefence.endGame(c, false);
		}
		if (c.duelStatus == 6) {
			c.getTradeAndDuel().claimStakedItems();
		}
		if (!c.isDead && c.teleTimer == 0 && c.respawnTimer == -6) {
			c.getCombat().resetPlayerAttack();
			c.stopMovement();
			removeAllWindows();
			c.teleX = x;
			c.teleY = y;
			c.npcIndex = 0;
			c.playerIndex = 0;
			c.faceUpdate(0);
			c.teleHeight = height;
			c.spiritWave = 0;
			c.spiritCount = 0;
			c.barbLeader = 0;
			c.firstWave = false;
			c.getPA().resetBarrows();
			if (teleportType.equalsIgnoreCase("modern")) {
				c.startAnimation(8939);
				c.teleTimer = 9;
				c.gfx0(1576);
				c.teleEndAnimation = 8941;
			}
			if (teleportType.equalsIgnoreCase("ancient")) {
				c.startAnimation(9599);
				c.teleGfx = 0;
				c.teleTimer = 11;
				c.teleEndAnimation = 8941;
				c.gfx0(1681);
			}

		}
	}

	public void startTeleport2(int x, int y, int height) {
		if (c.duelStatus == 5) {
			c.sendMessage("You can't teleport during a duel!");
			return;
		}
		if (c.inBarbDef() || PestControl.isInGame(c)
				|| PestControl.isInPcBoat(c)) {
			c.sendMessage("You can't teleport during this mini-game!");
			return;
		}
		if (System.currentTimeMillis() - c.teleBlockDelay < c.teleBlockLength) {
			c.sendMessage("You are teleblocked and can't teleport.");
			return;
		}
		if (!c.isDead && c.teleTimer == 0) {
			c.stopMovement();
			removeAllWindows();
			c.teleX = x;
			c.teleY = y;
			c.npcIndex = 0;
			c.playerIndex = 0;
			c.faceUpdate(0);
			c.teleHeight = height;
			c.startAnimation(714);
			c.teleTimer = 11;
			c.teleGfx = 308;
			c.teleEndAnimation = 715;
		}
	}

	public void processTeleport() {
		c.teleportToX = c.teleX;
		c.teleportToY = c.teleY;
		c.heightLevel = c.teleHeight;
		if (c.teleEndAnimation > 0) {
			c.startAnimation(c.teleEndAnimation);
		}
	}

	/**
	 * Following
	 **/

	public void followPlayer() {
		if (PlayerHandler.players[c.followId] == null
				|| PlayerHandler.players[c.followId].isDead) {
			c.followId = 0;
			return;
		}
		if (c.freezeTimer > 0) {
			return;
		}
		if (c.isDead || c.playerLevel[3] <= 0)
			return;

		int otherX = PlayerHandler.players[c.followId].getX();
		int otherY = PlayerHandler.players[c.followId].getY();

		boolean hallyDistance = c.goodDistance(otherX, otherY, c.getX(),
				c.getY(), 2);
		boolean bowDistance = c.goodDistance(otherX, otherY, c.getX(),
				c.getY(), 7);
		boolean rangeWeaponDistance = c.goodDistance(otherX, otherY, c.getX(),
				c.getY(), 4);
		boolean castingMagic = (c.mageFollow);
		boolean sameSpot = c.absX == otherX && c.absY == otherY;

		if (sameSpot)
			stepAway();

		if (c.playerIndex > 0) {
			Client o = (Client) PlayerHandler.players[c.playerIndex];
			if (c.usingBow || c.usingMagic || c.autocasting) {
				if (pathBlocked(c, o)) {
					PathFinder.getPathFinder().findRoute(c, o.absX, o.absY,
							false, 7, 7);
					return;
				}
			}
		}

		if (!c.goodDistance(otherX, otherY, c.getX(), c.getY(), 25)) {
			c.followId = 0;
			return;
		}

		if (c.goodDistance(otherX, otherY, c.getX(), c.getY(), 1)) {
			if (otherX != c.getX() && otherY != c.getY()) {
				stopDiagonal(otherX, otherY);
				return;
			}
		}

		if ((c.usingBow || castingMagic || (c.playerIndex > 0 && c.autocastId > 0))
				&& bowDistance && !sameSpot) {
			c.stopMovement();
			return;
		}

		if (c.getCombat().usingHally() && hallyDistance && !sameSpot) {
			c.stopMovement();
			return;
		}

		if (c.usingRangeWeapon && rangeWeaponDistance && !sameSpot) {
			c.stopMovement();
			return;
		} else if (c.isRunning2) {
			if (otherY > c.getY() && otherX == c.getX()) {
				playerWalk(otherX, otherY - 1);
			} else if (otherY < c.getY() && otherX == c.getX()) {
				playerWalk(otherX, otherY + 1);
			} else if (otherX > c.getX() && otherY == c.getY()) {
				playerWalk(otherX - 1, otherY);
			} else if (otherX < c.getX() && otherY == c.getY()) {
				playerWalk(otherX + 1, otherY);
			} else if (otherX < c.getX() && otherY < c.getY()) {
				playerWalk(otherX + 1, otherY + 1);
			} else if (otherX > c.getX() && otherY > c.getY()) {
				playerWalk(otherX - 1, otherY - 1);
			} else if (otherX < c.getX() && otherY > c.getY()) {
				playerWalk(otherX + 1, otherY - 1);
			} else if (otherX > c.getX() && otherY < c.getY()) {
				playerWalk(otherX + 1, otherY - 1);
			}
		} else {
			if (otherY > c.getY() && otherX == c.getX()) {
				playerWalk(otherX, otherY - 1);
			} else if (otherY < c.getY() && otherX == c.getX()) {
				playerWalk(otherX, otherY + 1);
			} else if (otherX > c.getX() && otherY == c.getY()) {
				playerWalk(otherX - 1, otherY);
			} else if (otherX < c.getX() && otherY == c.getY()) {
				playerWalk(otherX + 1, otherY);
			} else if (otherX < c.getX() && otherY < c.getY()) {
				playerWalk(otherX + 1, otherY + 1);
			} else if (otherX > c.getX() && otherY > c.getY()) {
				playerWalk(otherX - 1, otherY - 1);
			} else if (otherX < c.getX() && otherY > c.getY()) {
				playerWalk(otherX + 1, otherY - 1);
			} else if (otherX > c.getX() && otherY < c.getY()) {
				playerWalk(otherX - 1, otherY + 1);
			}
		}
		c.faceUpdate(c.followId + 32768);
	}

	public static boolean pathBlocked(Client attacker, Client victim) {

		double offsetX = Math.abs(attacker.absX - victim.absX);
		double offsetY = Math.abs(attacker.absY - victim.absY);

		int distance = TileControl.calculateDistance(attacker, victim);

		if (distance == 0) {
			return true;
		}

		offsetX = offsetX > 0 ? offsetX / distance : 0;
		offsetY = offsetY > 0 ? offsetY / distance : 0;

		int[][] path = new int[distance][5];

		int curX = attacker.absX;
		int curY = attacker.absY;
		int next = 0;
		int nextMoveX = 0;
		int nextMoveY = 0;

		double currentTileXCount = 0.0;
		double currentTileYCount = 0.0;

		while (distance > 0) {
			distance--;
			nextMoveX = 0;
			nextMoveY = 0;
			if (curX > victim.absX) {
				currentTileXCount += offsetX;
				if (currentTileXCount >= 1.0) {
					nextMoveX--;
					curX--;
					currentTileXCount -= offsetX;
				}
			} else if (curX < victim.absX) {
				currentTileXCount += offsetX;
				if (currentTileXCount >= 1.0) {
					nextMoveX++;
					curX++;
					currentTileXCount -= offsetX;
				}
			}
			if (curY > victim.absY) {
				currentTileYCount += offsetY;
				if (currentTileYCount >= 1.0) {
					nextMoveY--;
					curY--;
					currentTileYCount -= offsetY;
				}
			} else if (curY < victim.absY) {
				currentTileYCount += offsetY;
				if (currentTileYCount >= 1.0) {
					nextMoveY++;
					curY++;
					currentTileYCount -= offsetY;
				}
			}
			path[next][0] = curX;
			path[next][1] = curY;
			path[next][2] = attacker.heightLevel;
			path[next][3] = nextMoveX;
			path[next][4] = nextMoveY;
			next++;
		}
		for (int i = 0; i < path.length; i++) {
			if (!Region.getClipping(path[i][0], path[i][1], path[i][2],
					path[i][3], path[i][4])
					&& !Region.blockedShot(path[i][0], path[i][1], path[i][2])) {
				return true;
			}
		}
		return false;
	}

	public void followNpc() {
		if (NPCHandler.npcs[c.followId2] == null
				|| NPCHandler.npcs[c.followId2].isDead) {
			resetFollow();
			return;
		}

		if (c.freezeTimer > 0) {
			return;
		}
		if (c.isDead || c.playerLevel[3] <= 0)
			return;

		int otherX = NPCHandler.npcs[c.followId2].getX();
		int otherY = NPCHandler.npcs[c.followId2].getY();

		boolean sameSpot = c.absX == otherX && c.absY == otherY;

		if (sameSpot)
			stepAway();

		boolean meleeDistance = c.goodDistance(otherX, otherY, c.getX(),
				c.getY(), 1);
		boolean hallyDistance = c.goodDistance(otherX, otherY, c.getX(),
				c.getY(), 2);
		boolean rangeWeaponDistance = c.goodDistance(otherX, otherY, c.getX(),
				c.getY(), 4);
		boolean bowDistance = c.goodDistance(otherX, otherY, c.getX(),
				c.getY(), 7);
		boolean mageDistance = c.goodDistance(otherX, otherY, c.getX(),
				c.getY(), 7);

		boolean playerUsingMelee = ((!c.usingMagic && !c.usingBow
				&& !c.usingCross && !c.usingOtherRangeWeapons) && meleeDistance);
		boolean castingMagic = (c.usingMagic || c.mageFollow || c.autocasting || c.spellId > 0)
				&& mageDistance;
		boolean playerRanging = (c.usingOtherRangeWeapons || c.usingRangeWeapon)
				&& rangeWeaponDistance;
		boolean playerBowOrCross = (c.usingBow || c.usingCross) && bowDistance;

		if (!c.goodDistance(otherX, otherY, c.getX(), c.getY(), 25)) {
			resetFollow();
			return;
		}

		if (c.goodDistance(otherX, otherY, c.getX(), c.getY(), 1)) {
			if (otherX != c.getX() && otherY != c.getY()) {
				stopDiagonal(otherX, otherY);
				return;
			}
		}

		c.faceUpdate(c.followId2);

		if (!sameSpot) {
			if (c.followId2 > 0) {
				if (playerUsingMelee) {
					c.stopMovement();
					return;
				} else if (c.usingSpecial
						&& (playerBowOrCross || playerRanging)) {
					c.stopMovement();
					return;
				} else if (castingMagic || playerRanging || playerBowOrCross) {
					c.stopMovement();
					return;
				} else if (c.getCombat().usingHally() && hallyDistance) {
					c.stopMovement();
					return;
				}
			}
		}
		if (c.isRunning) {
			if (otherY > c.getY() && otherX == c.getX()) {
				playerWalk(otherX, otherY - 1);
			} else if (otherY < c.getY() && otherX == c.getX()) {
				playerWalk(otherX, otherY + 1);
			} else if (otherX > c.getX() && otherY == c.getY()) {
				playerWalk(otherX - 1, otherY);
			} else if (otherX < c.getX() && otherY == c.getY()) {
				playerWalk(otherX + 1, otherY);
			} else if (otherX < c.getX() && otherY < c.getY()) {
				playerWalk(otherX + 1, otherY + 1);
			} else if (otherX > c.getX() && otherY > c.getY()) {
				playerWalk(otherX - 1, otherY - 1);
			} else if (otherX < c.getX() && otherY > c.getY()) {
				playerWalk(otherX + 1, otherY - 1);
			} else if (otherX > c.getX() && otherY < c.getY()) {
				playerWalk(otherX + 1, otherY - 1);
			}
		} else {
			if (otherY > c.getY() && otherX == c.getX()) {
				playerWalk(otherX, otherY - 1);
			} else if (otherY < c.getY() && otherX == c.getX()) {
				playerWalk(otherX, otherY + 1);
			} else if (otherX > c.getX() && otherY == c.getY()) {
				playerWalk(otherX - 1, otherY);
			} else if (otherX < c.getX() && otherY == c.getY()) {
				playerWalk(otherX + 1, otherY);
			} else if (otherX < c.getX() && otherY < c.getY()) {
				playerWalk(otherX + 1, otherY + 1);
			} else if (otherX > c.getX() && otherY > c.getY()) {
				playerWalk(otherX - 1, otherY - 1);
			} else if (otherX < c.getX() && otherY > c.getY()) {
				playerWalk(otherX + 1, otherY - 1);
			} else if (otherX > c.getX() && otherY < c.getY()) {
				playerWalk(otherX - 1, otherY + 1);
			}
		}
	}

	public void stepAway() {
		if (Region.getClipping(c.getX() - 1, c.getY(), c.heightLevel, -1, 0)) {
			c.getPA().walkTo(-1, 0);
			return;
		} else if (Region.getClipping(c.getX() + 1, c.getY(), c.heightLevel, 1,
				0)) {
			c.getPA().walkTo(1, 0);
			return;
		} else if (Region.getClipping(c.getX(), c.getY() - 1, c.heightLevel, 0,
				-1)) {
			c.getPA().walkTo(0, -1);
			return;
		} else if (Region.getClipping(c.getX(), c.getY() + 1, c.heightLevel, 0,
				1)) {
			c.getPA().walkTo(0, 1);
			return;
		}
		c.getPA().walkTo(-1, 0);
	}

	public int getRunningMove(int i, int j) {
		if (j - i > 2)
			return 2;
		else if (j - i < -2)
			return -2;
		else
			return j - i;
	}

	public void resetFollow() {
		c.followId = 0;
		c.followId2 = 0;
		c.faceUpdate(0);
		c.mageFollow = false;
		c.outStream.createFrame(174);
		c.outStream.writeWord(0);
		c.outStream.writeByte(0);
		c.outStream.writeWord(1);
	}

	public void walkTo(int i, int j) {
		c.newWalkCmdSteps = 0;
		if (++c.newWalkCmdSteps > 50)
			c.newWalkCmdSteps = 0;
		int k = c.getX() + i;
		k -= c.mapRegionX * 8;
		c.getNewWalkCmdX()[0] = c.getNewWalkCmdY()[0] = 0;
		int l = c.getY() + j;
		l -= c.mapRegionY * 8;

		for (int n = 0; n < c.newWalkCmdSteps; n++) {
			c.getNewWalkCmdX()[n] += k;
			c.getNewWalkCmdY()[n] += l;
		}
	}

	public void walkTo2(int i, int j) {
		if (c.freezeDelay > 0)
			return;
		c.newWalkCmdSteps = 0;
		if (++c.newWalkCmdSteps > 50)
			c.newWalkCmdSteps = 0;
		int k = c.getX() + i;
		k -= c.mapRegionX * 8;
		c.getNewWalkCmdX()[0] = c.getNewWalkCmdY()[0] = 0;
		int l = c.getY() + j;
		l -= c.mapRegionY * 8;

		for (int n = 0; n < c.newWalkCmdSteps; n++) {
			c.getNewWalkCmdX()[n] += k;
			c.getNewWalkCmdY()[n] += l;
		}
	}

	public void stopDiagonal(int otherX, int otherY) {
		if (c.freezeDelay > 0)
			return;
		c.newWalkCmdSteps = 1;
		int xMove = otherX - c.getX();
		int yMove = 0;
		if (xMove == 0)
			yMove = otherY - c.getY();
		/*
		 * if (!clipHor) { yMove = 0; } else if (!clipVer) { xMove = 0; }
		 */

		int k = c.getX() + xMove;
		k -= c.mapRegionX * 8;
		c.getNewWalkCmdX()[0] = c.getNewWalkCmdY()[0] = 0;
		int l = c.getY() + yMove;
		l -= c.mapRegionY * 8;

		for (int n = 0; n < c.newWalkCmdSteps; n++) {
			c.getNewWalkCmdX()[n] += k;
			c.getNewWalkCmdY()[n] += l;
		}

	}

	public void walkToCheck(int i, int j) {
		if (c.freezeDelay > 0)
			return;
		c.newWalkCmdSteps = 0;
		if (++c.newWalkCmdSteps > 50)
			c.newWalkCmdSteps = 0;
		int k = c.getX() + i;
		k -= c.mapRegionX * 8;
		c.getNewWalkCmdX()[0] = c.getNewWalkCmdY()[0] = 0;
		int l = c.getY() + j;
		l -= c.mapRegionY * 8;

		for (int n = 0; n < c.newWalkCmdSteps; n++) {
			c.getNewWalkCmdX()[n] += k;
			c.getNewWalkCmdY()[n] += l;
		}
	}

	public int getMove(int place1, int place2) {
		if (System.currentTimeMillis() - c.lastSpear < 4000)
			return 0;
		if ((place1 - place2) == 0) {
			return 0;
		} else if ((place1 - place2) < 0) {
			return 1;
		} else if ((place1 - place2) > 0) {
			return -1;
		}
		return 0;
	}

	public boolean fullVeracs() {
		return c.playerEquipment[Player.playerHat] == 4753
				&& c.playerEquipment[c.playerChest] == 4757
				&& c.playerEquipment[c.playerLegs] == 4759
				&& c.playerEquipment[Player.playerWeapon] == 4755;
	}

	public boolean fullGuthans() {
		return c.playerEquipment[Player.playerHat] == 4724
				&& c.playerEquipment[c.playerChest] == 4728
				&& c.playerEquipment[c.playerLegs] == 4730
				&& c.playerEquipment[Player.playerWeapon] == 4726;
	}

	/**
	 * reseting animation
	 **/
	public void resetAnimation() {
		c.getCombat().getPlayerAnimIndex(
				c.getItems()
						.getItemName(c.playerEquipment[Player.playerWeapon])
						.toLowerCase());
		c.startAnimation(c.playerStandIndex);
		requestUpdates();
	}

	public void requestUpdates() {
		c.updateRequired = true;
		c.setAppearanceUpdateRequired(true);
	}

	public void levelUp(int skill) {
		int totalLevel = (getLevelForXP(c.playerXP[0])
				+ getLevelForXP(c.playerXP[1]) + getLevelForXP(c.playerXP[2])
				+ getLevelForXP(c.playerXP[3]) + getLevelForXP(c.playerXP[4])
				+ getLevelForXP(c.playerXP[5]) + getLevelForXP(c.playerXP[6])
				+ getLevelForXP(c.playerXP[7]) + getLevelForXP(c.playerXP[8])
				+ getLevelForXP(c.playerXP[9]) + getLevelForXP(c.playerXP[10])
				+ getLevelForXP(c.playerXP[11]) + getLevelForXP(c.playerXP[12])
				+ getLevelForXP(c.playerXP[13]) + getLevelForXP(c.playerXP[14])
				+ getLevelForXP(c.playerXP[15]) + getLevelForXP(c.playerXP[16])
				+ getLevelForXP(c.playerXP[17]) + getLevelForXP(c.playerXP[18])
				+ getLevelForXP(c.playerXP[19]) + getLevelForXP(c.playerXP[20]));
		sendFrame126("Total Lvl: " + totalLevel, 3984);
		switch (skill) {
		case 0:
			sendFrame126("Congratulations, you just advanced an attack level!",
					6248);
			sendFrame126("Your attack level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 6249);
			c.sendMessage("Congratulations, you just advanced an attack level.");
			sendFrame164(6247);
			for (int HUH = 0; HUH < 25; HUH++) {
				if (getLevelForXP(c.playerXP[0]) == 99) {
					PlayerHandler.yell("@red@[News]: "
							+ Misc.formatPlayerName(c.playerName)
							+ " has just achieved level 99 Attack!");
					// TutorialIsland.ATTACK(c, HUH);
					return;
				}
			}
			break;

		case 1:
			sendFrame126("Congratulations, you just advanced a defence level!",
					6254);
			sendFrame126("Your defence level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 6255);
			c.sendMessage("Congratulations, you just advanced a defence level.");
			sendFrame164(6253);
			for (int HUH = 0; HUH < 25; HUH++) {
				if (getLevelForXP(c.playerXP[1]) == 99) {
					PlayerHandler.yell("@red@[News]: "
							+ Misc.formatPlayerName(c.playerName)
							+ " has just achieved level 99 Defence!");
					// TutorialIsland.DEFENCE(c, HUH);
					return;
				}
			}
			break;

		case 2:
			sendFrame126(
					"Congratulations, you just advanced a strength level!",
					6207);
			sendFrame126("Your strength level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 6208);
			c.sendMessage("Congratulations, you just advanced a strength level.");
			sendFrame164(6206);
			for (int HUH = 0; HUH < 25; HUH++) {
				if (getLevelForXP(c.playerXP[2]) == 99) {
					PlayerHandler.yell("@red@[News]: "
							+ Misc.formatPlayerName(c.playerName)
							+ " has just achieved level 99 Strength!");
					// TutorialIsland.STRENGTH(c, HUH);
					return;
				}
			}
			break;

		case 3:
			sendFrame126(
					"Congratulations, you just advanced a hitpoints level!",
					6217);
			sendFrame126("Your hitpoints level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 6218);
			c.sendMessage("Congratulations, you just advanced a hitpoints level.");
			sendFrame164(6216);
			// hitpoints
			for (int HUH = 0; HUH < 25; HUH++) {
				if (getLevelForXP(c.playerXP[3]) == 99) {
					PlayerHandler.yell("@red@[News]: "
							+ Misc.formatPlayerName(c.playerName)
							+ " has just achieved level 99 Hitpoints!");
					// TutorialIsland.HITPOINTS(c, HUH);
					return;
				}
			}
			break;

		case 4:
			sendFrame126("Congratulations, you just advanced a ranged level!",
					5453);
			// sendFrame126("Congratulations, you just advanced to "
			// + getLevelForXP(c.playerXP[skill]) + " ranged level!", 5453);
			sendFrame126("Your ranged level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 6114);
			c.sendMessage("Congratulations, you just advanced a ranged level.");
			sendFrame164(4443);
			for (int HUH = 0; HUH < 25; HUH++) {
				if (getLevelForXP(c.playerXP[4]) == 99) {
					PlayerHandler.yell("@red@[News]: "
							+ Misc.formatPlayerName(c.playerName)
							+ " has just achieved level 99 Ranged!");
					// TutorialIsland.RANGING(c, HUH);
					return;
				}
			}
			break;

		case 5:
			sendFrame126("Congratulations, you just advanced a prayer level!",
					6243);
			sendFrame126("Your prayer level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 6244);
			c.sendMessage("Congratulations, you just advanced a prayer level.");
			sendFrame164(6242);
			for (int HUH = 0; HUH < 25; HUH++) {
				if (getLevelForXP(c.playerXP[5]) == 99) {
					PlayerHandler.yell("@red@[News]: "
							+ Misc.formatPlayerName(c.playerName)
							+ " has just achieved level 99 Prayer!");
					// TutorialIsland.PRAYER(c, HUH);
					return;
				}
			}
			break;

		case 6:
			sendFrame126("Congratulations, you just advanced a magic level!",
					6212);
			sendFrame126("Your magic level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 6213);
			c.sendMessage("Congratulations, you just advanced a magic level.");
			sendFrame164(6211);
			for (int HUH = 0; HUH < 25; HUH++) {
				if (getLevelForXP(c.playerXP[6]) == 99) {
					PlayerHandler.yell("@red@[News]: "
							+ Misc.formatPlayerName(c.playerName)
							+ " has just achieved level 99 Magic!");
					// TutorialIsland.MAGIC(c, HUH);
					return;
				}
			}
			break;

		case 7:
			sendFrame126("Congratulations, you just advanced a cooking level!",
					6227);
			sendFrame126("Your cooking level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 6228);
			c.sendMessage("Congratulations, you just advanced a cooking level.");
			sendFrame164(6226);
			for (int HUH = 0; HUH < 25; HUH++) {
				if (getLevelForXP(c.playerXP[7]) == 99) {
					PlayerHandler.yell("@red@[News]: "
							+ Misc.formatPlayerName(c.playerName)
							+ " has just achieved level 99 Cooking!");
					return;
				}
			}
			break;

		case 8:
			sendFrame126(
					"Congratulations, you just advanced a woodcutting level!",
					4273);
			sendFrame126("Your woodcutting level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 4274);
			c.sendMessage("Congratulations, you just advanced a woodcutting level.");
			sendFrame164(4272);
			for (int HUH = 0; HUH < 25; HUH++) {
				if (getLevelForXP(c.playerXP[8]) == 99) {
					PlayerHandler.yell("@red@[News]: "
							+ Misc.formatPlayerName(c.playerName)
							+ " has just achieved level 99 Woodcutting!");
					return;
				}
			}
			break;

		case 9:
			sendFrame126(
					"Congratulations, you just advanced a fletching level!",
					6232);
			sendFrame126("Your fletching level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 6233);
			c.sendMessage("Congratulations, you just advanced a fletching level.");
			sendFrame164(6231);
			for (int HUH = 0; HUH < 25; HUH++) {
				if (getLevelForXP(c.playerXP[9]) == 99) {
					PlayerHandler.yell("@red@[News]: "
							+ Misc.formatPlayerName(c.playerName)
							+ " has just achieved level 99 Fletching!");
					return;
				}
			}
			break;

		case 10:
			sendFrame126("Congratulations, you just advanced a fishing level!",
					6259);
			sendFrame126("Your fishing level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 6260);
			c.sendMessage("Congratulations, you just advanced a fishing level.");
			sendFrame164(6258);
			for (int HUH = 0; HUH < 25; HUH++) {
				if (getLevelForXP(c.playerXP[10]) == 99) {
					PlayerHandler.yell("@red@[News]: "
							+ Misc.formatPlayerName(c.playerName)
							+ " has just achieved level 99 Fishing!");
					return;
				}
			}
			break;

		case 11:
			sendFrame126(
					"Congratulations, you just advanced a firemaking level!",
					4283);
			sendFrame126("Your firemaking level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 4284);
			c.sendMessage("Congratulations, you just advanced a firemaking level.");
			sendFrame164(4282);
			for (int HUH = 0; HUH < 25; HUH++) {
				if (getLevelForXP(c.playerXP[11]) == 99) {
					PlayerHandler.yell("@red@[News]: "
							+ Misc.formatPlayerName(c.playerName)
							+ " has just achieved level 99 Firemaking!");
					return;
				}
			}
			break;

		case 12:
			sendFrame126(
					"Congratulations, you just advanced a crafting level!",
					6264);
			sendFrame126("Your crafting level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 6265);
			c.sendMessage("Congratulations, you just advanced a crafting level.");
			sendFrame164(6263);
			for (int HUH = 0; HUH < 25; HUH++) {
				if (getLevelForXP(c.playerXP[12]) == 99) {
					PlayerHandler.yell("@red@[News]: "
							+ Misc.formatPlayerName(c.playerName)
							+ " has just achieved level 99 Crafting!");
					return;
				}
			}
			break;

		case 13:
			sendFrame126(
					"Congratulations, you just advanced a smithing level!",
					6222);
			sendFrame126("Your smithing level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 6223);
			c.sendMessage("Congratulations, you just advanced a smithing level.");
			sendFrame164(6221);
			for (int HUH = 0; HUH < 25; HUH++) {
				if (getLevelForXP(c.playerXP[13]) == 99) {
					PlayerHandler.yell("@red@[News]: "
							+ Misc.formatPlayerName(c.playerName)
							+ " has just achieved level 99 Smithing!");
					return;
				}
			}
			break;

		case 14:
			sendFrame126("Congratulations, you just advanced a mining level!",
					4417);
			sendFrame126("Your mining level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 4438);
			c.sendMessage("Congratulations, you just advanced a mining level.");
			sendFrame164(4416);
			for (int HUH = 0; HUH < 25; HUH++) {
				if (getLevelForXP(c.playerXP[14]) == 99) {
					PlayerHandler.yell("@red@[News]: "
							+ Misc.formatPlayerName(c.playerName)
							+ " has just achieved level 99 Mining!");
					return;
				}
			}
			break;

		case 15:
			sendFrame126(
					"Congratulations, you just advanced a herblore level!",
					6238);
			sendFrame126("Your herblore level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 6239);
			c.sendMessage("Congratulations, you just advanced a herblore level.");
			sendFrame164(6237);
			for (int HUH = 0; HUH < 25; HUH++) {
				if (getLevelForXP(c.playerXP[15]) == 99) {
					PlayerHandler.yell("@red@[News]: "
							+ Misc.formatPlayerName(c.playerName)
							+ " has just achieved level 99 Herblore!");
					return;
				}
			}
			break;

		case 16:
			sendFrame126("Congratulations, you just advanced a agility level!",
					4278);
			sendFrame126("Your agility level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 4279);
			c.sendMessage("Congratulations, you just advanced an agility level.");
			sendFrame164(4277);
			for (int HUH = 0; HUH < 25; HUH++) {
				if (getLevelForXP(c.playerXP[16]) == 99) {
					PlayerHandler.yell("@red@[News]: "
							+ Misc.formatPlayerName(c.playerName)
							+ " has just achieved level 99 Agility!");
					return;
				}
			}
			break;

		case 17:
			sendFrame126(
					"Congratulations, you just advanced a thieving level!",
					4263);
			sendFrame126("Your theiving level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 4264);
			c.sendMessage("Congratulations, you just advanced a thieving level.");
			sendFrame164(4261);
			for (int HUH = 0; HUH < 25; HUH++) {
				if (getLevelForXP(c.playerXP[17]) == 99) {
					PlayerHandler.yell("@red@[News]: "
							+ Misc.formatPlayerName(c.playerName)
							+ " has just achieved level 99 Thieving!");
					return;
				}
			}
			break;

		case 18:
			sendFrame126("Congratulations, you just advanced a slayer level!",
					12123);
			sendFrame126("Your slayer level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 12124);
			c.sendMessage("Congratulations, you just advanced a slayer level.");
			sendFrame164(12122);
			for (int HUH = 0; HUH < 25; HUH++) {
				if (getLevelForXP(c.playerXP[18]) == 99) {
					PlayerHandler.yell("@red@[News]: "
							+ Misc.formatPlayerName(c.playerName)
							+ " has just achieved level 99 Slayer!");
					return;
				}
			}
			break;

		case 20:
			sendFrame126(
					"Congratulations, you just advanced a runecrafting level!",
					4268);
			sendFrame126("Your runecrafting level is now "
					+ getLevelForXP(c.playerXP[skill]) + ".", 4269);
			c.sendMessage("Congratulations, you just advanced a runecrafting level.");
			sendFrame164(4267);
			for (int HUH = 0; HUH < 25; HUH++) {
				if (getLevelForXP(c.playerXP[20]) == 99) {
					PlayerHandler.yell("@red@[News]: "
							+ Misc.formatPlayerName(c.playerName)
							+ " has just achieved level 99 Runecrafting!");
					return;
				}
			}
			break;
		}
		c.dialogueAction = 0;
		c.nextChat = 0;
	}

	public void refreshSkill(int i) {
		if (c == null || c.getOutStream() == null) {
			return;
		}
		if (c != null) {
			c.getOutStream().createFrame(134);
			c.getOutStream().writeByte(i);
			c.getOutStream().writeDWord_v1(c.playerXP[i]);
			c.getOutStream().writeByte(c.playerLevel[i]);
			c.flushOutStream();
		}
		int[] frame1 = { 4004, 4008, 4006, 4016, 4010, 4012, 4014, 4034, 4038,
				4026, 4032, 4036, 4024, 4030, 4028, 4020, 4018, 4022, 12166,
				13926, 4152 };
		int[] frame2 = { 4005, 4009, 4007, 4017, 4011, 4013, 4015, 4035, 4039,
				4027, 4033, 4037, 4025, 4031, 4029, 4021, 4019, 4023, 12167,
				13927, 4152 };
		int[] frame3 = { 4044, 4056, 4050, 4080, 4062, 4068, 4074, 4134, 4146,
				4110, 4128, 4140, 4104, 4122, 4116, 4092, 4086, 4098, 12171,
				13921, 4157 };
		int[] frame4 = { 4045, 4057, 4051, 4081, 4063, 4069, 4075, 4135, 4147,
				4111, 4129, 4141, 4105, 4123, 4117, 4093, 4087, 4099, 12172,
				13922, 4158 };
		if (i > -1) {
			for (int j = 0; j < 21; j++) {
				if (i == j) {
					sendFrame126("" + c.playerLevel[i] + "", frame1[i]);
					sendFrame126("" + getLevelForXP(c.playerXP[i]) + "",
							frame2[i]);
					sendFrame126("" + c.playerXP[i] + "", frame3[i]);
					sendFrame126(""
							+ getXPForLevel(getLevelForXP(c.playerXP[i]) + 1)
							+ "", frame4[i]);
				}
			}
		}
	}

	public int getXPForLevel(int level) {
		int points = 0;
		int output = 0;

		for (int lvl = 1; lvl <= level; lvl++) {
			points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
			if (lvl >= level)
				return output;
			output = (int) Math.floor(points / 4);
		}
		return 0;
	}

	public int getLevelForXP(int exp) {
		int points = 0;
		int output = 0;
		if (exp > 13034430)
			return 99;
		for (int lvl = 1; lvl <= 99; lvl++) {
			points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
			output = (int) Math.floor(points / 4);
			if (output >= exp) {
				return lvl;
			}
		}
		return 0;
	}

	public boolean addSkillXP(int amount, int skill) {
		if (c.expLock) {
			return false;
		}
		if (amount + c.playerXP[skill] < 0 || c.playerXP[skill] > 200000000) {
			if (c.playerXP[skill] > 200000000) {
				c.playerXP[skill] = 200000000;
			}
			return false;
		}
		amount *= Config.SERVER_EXP_BONUS;
		int oldLevel = getLevelForXP(c.playerXP[skill]);
		c.playerXP[skill] += amount;
		if (oldLevel < getLevelForXP(c.playerXP[skill])) {
			if (c.playerLevel[skill] < c.getLevelForXP(c.playerXP[skill])
					&& skill != 3 && skill != 5)
				c.playerLevel[skill] = c.getLevelForXP(c.playerXP[skill]);
			levelUp(skill);
			c.gfx100(199);
			requestUpdates();
		}
		setSkillLevel(skill, c.playerLevel[skill], c.playerXP[skill]);
		refreshSkill(skill);
		return true;
	}

	public void resetBarrows() {
		c.barrowsNpcs[0][1] = 0;
		c.barrowsNpcs[1][1] = 0;
		c.barrowsNpcs[2][1] = 0;
		c.barrowsNpcs[3][1] = 0;
		c.barrowsNpcs[4][1] = 0;
		c.barrowsNpcs[5][1] = 0;
		c.barrowsKillCount = 0;
		c.randomCoffin = Misc.random(3) + 1;
	}

	public static int Barrows[] = { 4708, 4710, 4712, 4714, 4716, 4718, 4720,
			4722, 4724, 4726, 4728, 4730, 4732, 4734, 4736, 4738, 4745, 4747,
			4749, 4751, 4753, 4755, 4757, 4759 };
	public static int Runes[] = { 4740, 555, 560, 565 };
	public static int Runes2[] = { 555, 557, 560, 565, 9075, 892 };
	public static int Box[] = { 14484, 4566, 15017, 11694, 11698, 11696, 11700,
			11724, 11726, 11720, 11722, 11718, 11728, 11716, 6570, 11283,
			10887, 15241, 20072, 8850, 8849, 8848, 8847, 8846, 8845, 8844,
			4716, 4718, 4720, 4722, 4753, 4755, 4757, 4759, 4745, 4747, 4749,
			4751, 4724, 4726, 4728, 4730, 4708, 4710, 4712, 4714, 4732, 4734,
			4736, 4738, 4224, 15441, 15442, 15443, 15444, 15701, 15702, 15703,
			15704, 11235, 11235, 4151, 4151, 6585, 6585, 6737, 6737, 6731,
			6731, 6733, 6733, 6735, 6735, 2577, 2577, 2577, 2581, 2581, 2581,
			1305, 1305, 1305, 1377, 1377, 1377, 1434, 1434, 1434, 3140, 3140,
			3140, 3204, 3204, 3204, 4087, 4087, 4087, 4587, 4587, 4587, 5698,
			5698, 5698, 7158, 7158, 7158, 1333, 1333, 1333, 1333, 1333, 1323,
			1323, 1323, 1323, 1323, 6721, 6721, 6721, 6721, 6721, 6721, 6721,
			6721, 6721, 6721, 6721, 6721, 6721, 6721, 6721, 6721, 6721, 6721,
			6721, 6721, 6721, 6721, 6721, 6721, 6721, 6721, 6721, 6721, 6721,
			6721, 6721, 6721, 6721, 6721, 6721, 6721, 6721, 6721, 6721, 6721 };

	public static int Thief[] = { 1163, 1127, 1079, 1201 };
	public static int Thief2[] = { 4089, 4091, 4093, 4095, 4097, 4099, 4101,
			4103, 4105, 4107 };

	public static int Shield[] = { 13740, 13742, 13738, 13744, 13736, 13734 };
	public static int Gear[] = { 11730, 11283, 15220, 15020, 19669, 14479,
			10348, 10346, 10352, 10350, 6585, 6737, 6735, 4151 };

	public int randomShield() {
		return Shield[(int) (Math.random() * Shield.length)];
	}

	public int randomGear() {
		return Gear[(int) (Math.random() * Gear.length)];
	}

	public int randomBarrows() {
		return Barrows[(int) (Math.random() * Barrows.length)];
	}

	public int randomRunes() {
		return Runes[(Misc.random(3))];
	}

	public int randomRunes2() {
		return Runes2[(Misc.random(5))];
	}

	public int randomBox() {
		return Box[(int) (Math.random() * Box.length)];
	}

	public int randomThief() {
		return Thief[(Misc.random(3))];
	}

	public int randomThief2() {
		return Thief2[(Misc.random(9))];
	}

	/**
	 * Show an arrow icon on the selected player.
	 * 
	 * @Param i - Either 0 or 1; 1 is arrow, 0 is none.
	 * @Param j - The player/Npc that the arrow will be displayed above.
	 * @Param k - Keep this set as 0
	 * @Param l - Keep this set as 0
	 */
	public void drawHeadicon(int i, int j, int k, int l) {
		if (c != null) {
			c.outStream.createFrame(254);
			c.outStream.writeByte(i);

			if (i == 1 || i == 10) {
				c.outStream.writeWord(j);
				c.outStream.writeWord(k);
				c.outStream.writeByte(l);
			} else {
				c.outStream.writeWord(k);
				c.outStream.writeWord(l);
				c.outStream.writeByte(j);
			}
		}
	}

	public int getNpcId(int id) {
		for (int i = 0; i < NPCHandler.maxNPCs; i++) {
			if (NPCHandler.npcs[i] != null) {
				if (NPCHandler.npcs[i].npcId == id) {
					return i;
				}
			}
		}
		return -1;
	}

	public void removeObject(int x, int y) {
		object(-1, x, x, 10, 10);
	}

	private void objectToRemove(int X, int Y) {
		object(-1, X, Y, 10, 10);
	}

	private void objectToRemove2(int X, int Y) {
		object(-1, X, Y, -1, 0);
	}

	public void removeObjects() {
		objectToRemove(3217, 3219);
		objectToRemove(3216, 3218);
		objectToRemove2(2635, 4693);
		objectToRemove2(2634, 4693);
	}

	public void handleGlory(int gloryId) {
		c.getDH().sendOption4("Edgeville", "Al Kharid", "Karamja", "Mage Bank");
		c.usingGlory = true;
	}

	public void resetVariables() {
		c.getCrafting().resetCrafting();
		c.usingGlory = false;
		c.smeltInterface = false;
		c.smeltType = 0;
		c.smeltAmount = 0;
	}

	public boolean inPitsWait() {
		return c.getX() <= 2404 && c.getX() >= 2394 && c.getY() <= 5175
				&& c.getY() >= 5169;
	}

	public int antiFire() {
		int toReturn = 0;
		if (c.antiFirePot)
			toReturn++;
		if (c.playerEquipment[Player.playerShield] == 1540
				|| c.prayerActive[12]
				|| c.playerEquipment[Player.playerShield] == 11283
				|| c.playerEquipment[Player.playerShield] == 11284)
			toReturn++;
		return toReturn;
	}

	public boolean checkForFlags() {
		int[][] itemsToCheck = { { 995, 100000000 }, { 35, 5 }, { 667, 5 },
				{ 2402, 5 }, { 746, 5 }, { 4151, 150 }, { 565, 100000 },
				{ 560, 100000 }, { 555, 300000 }, { 11235, 10 } };
		for (int j = 0; j < itemsToCheck.length; j++) {
			if (itemsToCheck[j][1] < c.getItems().getTotalCount(
					itemsToCheck[j][0]))
				return true;
		}
		return false;
	}

	public void addStarter() {
		if (!Connection
				.hasRecieved1stStarter(PlayerHandler.players[c.playerId].connectedFrom)) {
			c.getItems().addItem(995, 10000000);
			c.getItems().addItem(1725, 1);
			c.getItems().addItem(554, 100);
			c.getItems().addItem(555, 100);
			c.getItems().addItem(556, 100);
			c.getItems().addItem(558, 100);
			c.getItems().addItem(560, 100);
			c.getItems().addItem(565, 100);
			c.getItems().addItem(1323, 1);
			c.getItems().addItem(841, 1);
			c.getItems().addItem(882, 150);
			c.getItems().addItem(392, 500);
			c.getItems().addItem(542, 1);
			c.getItems().addItem(544, 1);
			c.getItems().addItem(4588, 25);
			c.getItems().addItem(5699, 25);
			c.getItems().addItem(1334, 25);
			c.getItems().addItem(6109, 1);
			c.getItems().addItem(6108, 1);
			c.getItems().addItem(6107, 1);
			c.getItems().addItem(6106, 1);
			c.getItems().addItem(6110, 1);
			c.getItems().addItem(6111, 1);
			c.sendMessage("@blu@Type ::commmands for a list of commands available to you.");
			Connection
					.addIpToStarterList1(PlayerHandler.players[c.playerId].connectedFrom);
			Connection
					.addIpToStarter1(PlayerHandler.players[c.playerId].connectedFrom);
		} else if (Connection
				.hasRecieved1stStarter(PlayerHandler.players[c.playerId].connectedFrom)
				&& !Connection
						.hasRecieved2ndStarter(PlayerHandler.players[c.playerId].connectedFrom)) {
			c.getItems().addItem(995, 10000000);
			c.getItems().addItem(1725, 1);
			c.getItems().addItem(554, 100);
			c.getItems().addItem(555, 100);
			c.getItems().addItem(556, 100);
			c.getItems().addItem(558, 100);
			c.getItems().addItem(560, 100);
			c.getItems().addItem(565, 100);
			c.getItems().addItem(1323, 1);
			c.getItems().addItem(841, 1);
			c.getItems().addItem(882, 150);
			c.getItems().addItem(392, 500);
			c.getItems().addItem(542, 1);
			c.getItems().addItem(544, 1);
			c.getItems().addItem(4588, 25);
			c.getItems().addItem(5699, 25);
			c.getItems().addItem(1334, 25);
			c.getItems().addItem(6109, 1);
			c.getItems().addItem(6108, 1);
			c.getItems().addItem(6107, 1);
			c.getItems().addItem(6106, 1);
			c.getItems().addItem(6110, 1);
			c.getItems().addItem(6111, 1);
			c.sendMessage("@blu@Type ::commmands for a list of commands available to you.");
			Connection
					.addIpToStarterList2(PlayerHandler.players[c.playerId].connectedFrom);
			Connection
					.addIpToStarter2(PlayerHandler.players[c.playerId].connectedFrom);
		} else if (Connection
				.hasRecieved1stStarter(PlayerHandler.players[c.playerId].connectedFrom)
				&& Connection
						.hasRecieved2ndStarter(PlayerHandler.players[c.playerId].connectedFrom)) {
			c.sendMessage("You have already recieved the maximum amount of 2 starters.");
			c.getPA().showInterface(3559);
			c.canChangeAppearance = true;
		}
	}

	public int getWearingAmount() {
		int count = 0;
		for (int j = 0; j < c.playerEquipment.length; j++) {
			if (c.playerEquipment[j] > 0)
				count++;
		}
		return count;
	}

	public void useOperate(int itemId) {
		// Teleport.handleDuelRing(c, itemId);
		// Teleport.handleGlory(c, itemId);
		switch (itemId) {
		// case 1712:
		// case 1710:
		// case 1708:
		// case 1706:
		// handleGlory(itemId);
		// break;

		// case 2552:
		// break;

		case 11283:
		case 11284:
			if (c.playerIndex > 0) {
				c.getCombat().handleDfs(c);
			} else if (c.npcIndex > 0) {
				c.getCombat().handleDfsNPC(c);
			}
			break;

		default:
			c.sendMessage("Nothing interesting happens.");
			break;
		}
	}

	public void getSpeared(int otherX, int otherY) {
		int x = c.absX - otherX;
		int y = c.absY - otherY;
		if (x > 0)
			x = 1;
		else if (x < 0)
			x = -1;
		if (y > 0)
			y = 1;
		else if (y < 0)
			y = -1;
		moveCheck(x, y);
		c.lastSpear = System.currentTimeMillis();
	}

	public void moveCheck(int xMove, int yMove) {
		movePlayer(c.absX + xMove, c.absY + yMove, c.heightLevel);
	}

	public int findKiller() {
		int killer = c.playerId;
		int damage = 0;
		for (int j = 0; j < Config.MAX_PLAYERS; j++) {
			if (PlayerHandler.players[j] == null)
				continue;
			if (j == c.playerId)
				continue;
			if (c.goodDistance(c.absX, c.absY, PlayerHandler.players[j].absX,
					PlayerHandler.players[j].absY, 40)
					|| c.goodDistance(c.absX, c.absY + 9400,
							PlayerHandler.players[j].absX,
							PlayerHandler.players[j].absY, 40)
					|| c.goodDistance(c.absX, c.absY,
							PlayerHandler.players[j].absX,
							PlayerHandler.players[j].absY + 9400, 40))
				if (c.damageTaken[j] > damage) {
					damage = c.damageTaken[j];
					killer = j;
				}
		}
		return killer;
	}

	public void resetTzhaar() {
		c.waveId = -1;
		c.tzhaarToKill = -1;
		c.tzhaarKilled = -1;
		movePlayer(3429 + (Misc.random(6)), 3569 + (Misc.random(6)), 0);
	}
	
	   public void resetRFD() {
                c.waveId = -1;
                c.RFDToKill = -1;
                c.RFDKilled = -1;       
                c.getPA().movePlayer(3432,3572,0);
        }
 
        public void enterRFD() {
                        if (c.Culin == true) {
                        c.sendMessage("You have already finished this minigame!");
                        return;
                        }
                        if (c.Agrith == true && c.Flambeed == false) {
                        c.waveId = 1;
                        c.getPA().movePlayer(1899,5363, c.playerId * 4+2);
                Server.rfd.spawnNextWave(c);
				c.sendMessage("Be Careful this is not a safe mini-game.");
                return;
                        } 
                if(c.Flambeed == true && c.Karamel == false) {
                        c.waveId = 2;
                        c.getPA().movePlayer(1899,5363, c.playerId * 4+2);
                Server.rfd.spawnNextWave(c);
				c.sendMessage("Be Careful this is not a safe mini-game.");
                return;
                        } 
                if(c.Karamel == true && c.Dessourt == false) {
                        c.waveId = 3;
                        c.getPA().movePlayer(1899,5363, c.playerId * 4+2);
                Server.rfd.spawnNextWave(c);
				c.sendMessage("Be Careful this is not a safe mini-game.");
                return;
                        } 
                if(c.Dessourt == true && c.Culin == false) {
                        c.waveId = 4;
                        c.getPA().movePlayer(1899,5363, c.playerId * 4+2);
                Server.rfd.spawnNextWave(c);
				c.sendMessage("Be Careful this is not a safe mini-game.");
                        return;
                        } 
                        if (c.Agrith == false) {
                        c.getPA().movePlayer(1899,5363, c.playerId * 4+2);
						c.sendMessage("Be Careful this is not a safe mini-game.");
                c.waveId = 0;
                c.RFDToKill = -1;
                c.RFDKilled = -1;
                Server.rfd.spawnNextWave(c);
        }
        }
 


	public void enterCaves() {
		c.getPA().movePlayer(2413, 5117, c.playerId * 4);
		c.waveId = 0;
		c.tzhaarToKill = -1;
		c.tzhaarKilled = -1;
		EventManager.getSingleton().addEvent(new Event() {
			@Override
			public void execute(EventContainer e) {
				Server.fightCaves.spawnNextWave((Client) PlayerHandler.players[c.playerId]);
				e.stop();
			}
		}, 10000);
	}

	public void enterChallenge() {
		if (c != null) {
			c.getPA().movePlayer(3181, 9758, c.playerId * 4);
			c.sendMessage("@dre@This is a SAFE mini-game. You will not lose any items upon death.");
			CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					if (c != null) {
						Server.npcHandler.spawnNpc(c, 3062, 3164, 9758,
								c.heightLevel, 0, 80, 8, 50, 50, true, true);
					}
					container.stop();
				}

				@Override
				public void stop() {

				}
			}, 5);
		}
	}

	public void wonChallenge() {
		if (c != null) {
			if (c.wonChallenge == false) {
				c.slayerPoints += 18;
				c.sendMessage("You have received 18 Slayer points for deafeating this champion.");
				showInterface(15831);
				c.getPA().sendFrame126(
						"Well done, you defeated all the Champions!", 15835);
				c.getPA().sendFrame126("Full dragon armour (or)", 15839);
				c.getPA().sendFrame126("Amulet of fury (or)", 15840);
				c.getItems().addItem(19336, 1);
				c.getItems().addItem(19337, 1);
				c.getItems().addItem(19338, 1);
				c.getItems().addItem(19340, 1);
				c.getItems().addItem(19335, 1);
				movePlayer(3185, 9758, 0);
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						if (c != null) {
							if (c.wonChallenge == false) {
								c.wonChallenge = true;
							}
						}
						container.stop();
					}

					@Override
					public void stop() {

					}
				}, 1);
			}
			if (c.wonChallenge == true) {
				c.slayerPoints += 18;
				c.sendMessage("You have received 18 Slayer points for deafeating this champion.");
				showInterface(15831);
				c.getPA().sendFrame126(
						"Well done, you defeated all the Champions!", 15835);
				c.getPA().sendFrame126("Ring of vigour", 15839);
				c.getPA().sendFrame126("25,000,000 coins", 15840);
				c.getItems().addItem(19669, 1);
				c.getItems().addItem(995, 25000000);
				movePlayer(3185, 9758, 0);
			}
		}
	}

	public void appendPoison(int damage) {
		if (System.currentTimeMillis() - c.lastPoisonSip > c.poisonImmune
				&& c.duelStatus != 5) {
			c.poisonDamage = damage;
			c.sendMessage("You have been poisoned!");
		}
	}

	public boolean checkForPlayer(int x, int y) {
		for (Player p : PlayerHandler.players) {
			if (p != null) {
				if (p.getX() == x && p.getY() == y)
					return true;
			}
		}
		return false;
	}

	public void fixAllBarrows() {
		int totalCost = 0;
		int cashAmount = c.getItems().getItemAmount(995);
		for (int j = 0; j < c.playerItems.length; j++) {
			boolean breakOut = false;
			c.getItems();
			for (int i = 0; i < ItemAssistant.brokenBarrows.length; i++) {
				c.getItems();
				if (c.playerItems[j] - 1 == ItemAssistant.brokenBarrows[i][1]) {
					if (totalCost + 800000 > cashAmount) {
						breakOut = true;
						c.sendMessage("You don't have enough coins.");
						break;
					} else {
						totalCost += 800000;
					}
					c.getItems();
					c.playerItems[j] = ItemAssistant.brokenBarrows[i][0] + 1;
				}
			}
			if (breakOut)
				break;
		}
		if (totalCost > 0)
			c.getItems().deleteItem(995, c.getItems().getItemSlot(995),
					totalCost);
	}

	public void handleLoginText() {
		loadQuests();
		c.getPA().sendFrame126("The Bank of Project Decimate - Deposit Box", 7421);
		c.getPA().sendFrame126("The Bank of Project Decimate", 5383);
		// log out text
		c.getPA().sendFrame126("When you are ready to leave", 2450);
		c.getPA().sendFrame126("Project Decimate, use the", 2451);
		c.getPA().sendFrame126("button below to logout safely.", 2452);
		// Log in text
		c.getPA().sendFrame126("Welcome to Project Decimate - Character Design",
				3649);
		c.getPA().sendFrame126("Welcome to Project Decimate.", 15259);
		//Normal
                 c.getPA().sendFrame126("Monsters", 13037);
                 c.getPA().sendFrame126("Minigames", 13047);
                 c.getPA().sendFrame126("Boss Teleports", 13055);
                 c.getPA().sendFrame126("Pking Teleports", 13063);
                 c.getPA().sendFrame126("Skilling Teleports", 13071);
                 //Ancients
                 c.getPA().sendFrame126("Monsters", 1300);
                 c.getPA().sendFrame126("Minigames", 1325);
                 c.getPA().sendFrame126("Boss Teleports", 1350);
                 c.getPA().sendFrame126("Pking Teleports", 1382);
                 c.getPA().sendFrame126("Skilling Teleports", 1415);
                 //lunars
                 c.getPA().sendFrame126("Monsters", 30066);
                 c.getPA().sendFrame126("Minigames", 30077);
                 c.getPA().sendFrame126("Boss Teleports", 30085);
                 c.getPA().sendFrame126("Pking Teleports", 30108);
                 c.getPA().sendFrame126("Skilling Teleports", 30116);
	}

	public void handleWeaponStyle() {
		if (c.fightMode == 0) {
			c.getPA().sendFrame36(43, c.fightMode);
		} else if (c.fightMode == 1) {
			c.getPA().sendFrame36(43, 3);
		} else if (c.fightMode == 2) {
			c.getPA().sendFrame36(43, 1);
		} else if (c.fightMode == 3) {
			c.getPA().sendFrame36(43, 2);
		}
	}

	public boolean banMac(Client macBan) {
		try {
			Connection.addMacToBanList(macBan.getMacAddress());
			Connection.addMacToMacFile(macBan.getMacAddress());
			macBan.disconnected = true;
			return true;
		} catch (Exception e) {
			c.sendMessage("Could not ban MAC!");
			return false;
		}
	}

}
