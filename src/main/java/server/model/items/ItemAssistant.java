package server.model.items;

import server.Config;
import server.Server;
import server.model.npcs.NPCHandler;
import server.model.players.Client;
import server.model.players.Player;
import server.model.players.PlayerHandler;
import server.util.Misc;

public class ItemAssistant {

	private Client c;

	public ItemAssistant(Client client) {
		this.c = client;
	}
	
	public int s = 607;

	public int[][] skillcapes = { { 9747, 9748 }, // attack
			{ 9753, 9754 }, // defence
			{ 9750, 9751 }, // strength
			{ 9768, 9769 }, // hitpoints
			{ 9756, 9757 }, // range
			{ 9759, 9760 }, // prayer
			{ 9762, 9763 }, // magic
			{ 9801, 9802 }, // cooking
			{ 9807, 9808 }, // woodcutting
			{ 9783, 9784 }, // fletching
			{ 9798, 9799 }, // fishing
			{ 9804, 9805 }, // firemaking
			{ 9780, 9781 }, // crafting
			{ 9795, 9796 }, // smithing
			{ 9792, 9793 }, // mining
			{ 9774, 9775 }, // herblore
			{ 9771, 9772 }, // agility
			{ 9777, 9778 }, // thieving
			{ 9786, 9787 }, // slayer
			{ 9810, 9811 }, // farming
			{ 9765, 9766 } // runecraft
	};

	/**
	 * Items
	 **/

	public static int[][] brokenBarrows = new int[][] { { 4708, 4860 },
			{ 4710, 4866 }, { 4712, 4872 }, { 4714, 4878 }, { 4716, 4884 },
			{ 4720, 4896 }, { 4718, 4890 }, { 4720, 4896 }, { 4722, 4902 },
			{ 4732, 4932 }, { 4734, 4938 }, { 4736, 4944 }, { 4738, 4950 },
			{ 4724, 4908 }, { 4726, 4914 }, { 4728, 4920 }, { 4730, 4926 },
			{ 4745, 4956 }, { 4747, 4962 }, { 4749, 4968 }, { 4751, 4974 },
			{ 4753, 4980 }, { 4755, 4986 }, { 4757, 4992 }, { 4759, 4998 } };

	public void resetItems(int WriteFrame) {
		if (c != null) {
			if (c.getOutStream() != null && c != null) {
				c.getOutStream().createFrameVarSizeWord(53);
				c.getOutStream().writeWord(WriteFrame);
				c.getOutStream().writeWord(c.playerItems.length);
				for (int i = 0; i < c.playerItems.length; i++) {
					if (c.playerItemsN[i] > 254) {
						c.getOutStream().writeByte(255);
						c.getOutStream().writeDWord_v2(c.playerItemsN[i]);
					} else {
						c.getOutStream().writeByte(c.playerItemsN[i]);
					}
					c.getOutStream().writeWordBigEndianA(c.playerItems[i]);
				}
				c.getOutStream().endFrameVarSizeWord();
				c.flushOutStream();
			}
		}
	}

	public int getItemCount(int itemID) {
		int count = 0;
		for (int j = 0; j < c.playerItems.length; j++) {
			if (c.playerItems[j] == itemID + 1) {
				count += c.playerItemsN[j];
			}
		}
		return count;
	}

	public void writeBonus() {
		int offset = 0;
		String send = "";
		for (int i = 0; i < c.playerBonus.length; i++) {
			if (c.playerBonus[i] >= 0) {
				send = BONUS_NAMES[i] + ": +" + c.playerBonus[i];
			} else {
				send = BONUS_NAMES[i] + ": -"
						+ java.lang.Math.abs(c.playerBonus[i]);
			}

			if (i == 10) {
				offset = 1;
			}
			c.getPA().sendFrame126(send, (1675 + i + offset));
		}

	}

	public int getTotalCount(int itemID) {
		int count = 0;
		for (int j = 0; j < c.playerItems.length; j++) {
			if (Item.itemIsNote[itemID + 1]) {
				if (itemID + 2 == c.playerItems[j])
					count += c.playerItemsN[j];
			}
			if (!Item.itemIsNote[itemID + 1]) {
				if (itemID + 1 == c.playerItems[j]) {
					count += c.playerItemsN[j];
				}
			}
		}
		for (int j = 0; j < c.bankItems.length; j++) {
			if (c.bankItems[j] == itemID + 1) {
				count += c.bankItemsN[j];
			}
		}
		return count;
	}

	public void sendItemsKept() {
		if (c != null) {
			if (c.getOutStream() != null && c != null) {
				c.getOutStream().createFrameVarSizeWord(53);
				c.getOutStream().writeWord(6963);
				c.getOutStream().writeWord(c.itemKeptId.length);
				for (int i = 0; i < c.itemKeptId.length; i++) {
					if (c.playerItemsN[i] > 254) {
						c.getOutStream().writeByte(255);
						c.getOutStream().writeDWord_v2(1);
					} else {
						c.getOutStream().writeByte(1);
					}
					if (c.itemKeptId[i] > 0) {
						c.getOutStream().writeWordBigEndianA(
								c.itemKeptId[i] + 1);
					} else {
						c.getOutStream().writeWordBigEndianA(0);
					}
				}
				c.getOutStream().endFrameVarSizeWord();
				c.flushOutStream();
			}
		}
	}

	/**
	 * Item kept on death
	 **/

	public void keepItem(int keepItem, boolean deleteItem) {
		int value = 0;
		int item = 0;
		int slotId = 0;
		boolean itemInInventory = false;
		for (int i = 0; i < c.playerItems.length; i++) {
			if (c.playerItems[i] - 1 > 0) {
				int inventoryItemValue = c.getShops().getItemShopValue(
						c.playerItems[i] - 1);
				if (inventoryItemValue > value && (!c.invSlot[i])) {
					value = inventoryItemValue;
					item = c.playerItems[i] - 1;
					slotId = i;
					itemInInventory = true;
				}
			}
		}
		for (int i1 = 0; i1 < c.playerEquipment.length; i1++) {
			if (c.playerEquipment[i1] > 0) {
				int equipmentItemValue = c.getShops().getItemShopValue(
						c.playerEquipment[i1]);
				if (equipmentItemValue > value && (!c.equipSlot[i1])) {
					value = equipmentItemValue;
					item = c.playerEquipment[i1];
					slotId = i1;
					itemInInventory = false;
				}
			}
		}
		if (itemInInventory) {
			c.invSlot[slotId] = true;
			if (deleteItem) {
				deleteItem(c.playerItems[slotId] - 1,
						getItemSlot(c.playerItems[slotId] - 1), 1);
			}
		} else {
			c.equipSlot[slotId] = true;
			if (deleteItem) {
				deleteEquipment(item, slotId);
			}
		}
		c.itemKeptId[keepItem] = item;
	}

	/**
	 * Reset items kept on death
	 **/

	public void resetKeepItems() {
		for (int i = 0; i < c.itemKeptId.length; i++) {
			c.itemKeptId[i] = -1;
		}
		for (int i1 = 0; i1 < c.invSlot.length; i1++) {
			c.invSlot[i1] = false;
		}
		for (int i2 = 0; i2 < c.equipSlot.length; i2++) {
			c.equipSlot[i2] = false;
		}
	}

	/**
	 * delete all items
	 **/

	public void deleteAllItems() {
		for (int i1 = 0; i1 < c.playerEquipment.length; i1++) {
			deleteEquipment(c.playerEquipment[i1], i1);
		}
		for (int i = 0; i < c.playerItems.length; i++) {
			deleteItem(c.playerItems[i] - 1, getItemSlot(c.playerItems[i] - 1),
					c.playerItemsN[i]);
		}
	}

	/**
	 * Drop all items for your killer
	 **/

	public void dropAllItems() {
		Client o = (Client) PlayerHandler.players[c.killerId];

		for (int i = 0; i < c.playerItems.length; i++) {
			if (o != null) {
				if (tradeable(c.playerItems[i] - 1)) {
					Server.itemHandler.createGroundItem(o,
							c.playerItems[i] - 1, c.getX(), c.getY(),
							c.playerItemsN[i], c.killerId);
				} else {
					if (specialCase(c.playerItems[i] - 1))
						Server.itemHandler.createGroundItem(o, 995, c.getX(),
								c.getY(),
								getUntradePrice(c.playerItems[i] - 1),
								c.killerId);
					Server.itemHandler.createGroundItem(c,
							c.playerItems[i] - 1, c.getX(), c.getY(),
							c.playerItemsN[i], c.playerId);
				}
			} else {
				Server.itemHandler.createGroundItem(c, c.playerItems[i] - 1,
						c.getX(), c.getY(), c.playerItemsN[i], c.playerId);
			}
		}
		for (int e = 0; e < c.playerEquipment.length; e++) {
			if (o != null) {
				if (tradeable(c.playerEquipment[e])) {
					Server.itemHandler.createGroundItem(o,
							c.playerEquipment[e], c.getX(), c.getY(),
							c.playerEquipmentN[e], c.killerId);
				} else {
					if (specialCase(c.playerEquipment[e]))
						Server.itemHandler.createGroundItem(o, 995, c.getX(),
								c.getY(),
								getUntradePrice(c.playerEquipment[e]),
								c.killerId);
					Server.itemHandler.createGroundItem(c,
							c.playerEquipment[e], c.getX(), c.getY(),
							c.playerEquipmentN[e], c.playerId);
				}
			} else {
				Server.itemHandler.createGroundItem(c, c.playerEquipment[e],
						c.getX(), c.getY(), c.playerEquipmentN[e], c.playerId);
			}
		}
		if (o != null) {
			Server.itemHandler.createGroundItem(o, 526, c.getX(), c.getY(), 1,
					c.killerId);
		}
	}

	/*public void addPVP() {
		Client o = (Client) PlayerHandler.players[c.killerId];
		if (o.lastKilledIPs.contains(c.connectedFrom)
				|| o.connectedFrom.equals(c.connectedFrom)) {
			return;
		}
		if (o != null) {
			if (Misc.random(3) == 1 && c.inWild() && !c.inFunPK()
					&& c.duelStatus < 1) {
				Server.itemHandler.createGroundItem(o, pvpDrop(), c.getX(),
						c.getY(), 1, c.killerId);
				o.sendMessage("@dre@Congratulations! You have received a PvP item drop for this kill. Check the loot!");
			}
			if (Misc.random(5) == 1 && c.inWild() && !c.inFunPK()
					&& c.duelStatus < 1) {
				Server.itemHandler.createGroundItem(o, pvpDrop2(), c.getX(),
						c.getY(), 1, c.killerId);
				o.sendMessage("@dre@Congratulations! You have received a PvP item drop for this kill. Check the loot!");
			}
		}
	}

	public int pvpDrop() {
		return PvPDrops[(Misc.random(9))];
	}

	public int pvpDrop2() {
		return PvPDrops2[(Misc.random(17))];
	}*/

	int[] PvPDrops = { 13976, 13985, 13979, 13982, 13973, 13961, 13964, 13967,
			13970, 13958 };

	int[] PvPDrops2 = { 13876, 13873, 13864, 13861, 13867, 13858, 13870, 13905,
			13896, 13884, 13890, 13887, 13893, 13902, 13899, 13929, 13926,
			13923 };

	public int getUntradePrice(int item) {
		switch (item) {
		case 2518:
		case 2524:
		case 2526:
			return 100000;
		case 2520:
		case 2522:
			return 150000;
		}
		return 0;
	}
	
	public boolean updateInventory = false;

	public void updateInventory() {
		updateInventory = false;
		resetItems(3214);
	}

	public boolean specialCase(int itemId) {
		switch (itemId) {
		case 2518:
		case 2520:
		case 2522:
		case 2524:
		case 2526:
			return true;
		}
		return false;
	}

	public void handleSpecialPickup(int itemId) {
		// c.sendMessage("My " + getItemName(itemId) +
		// " has been recovered. I should talk to the void knights to get it back.");
		// c.getItems().addToVoidList(itemId);
	}

	public void addToVoidList(int itemId) {
		switch (itemId) {
		case 2518:
			c.voidStatus[0]++;
			break;
		case 2520:
			c.voidStatus[1]++;
			break;
		case 2522:
			c.voidStatus[2]++;
			break;
		case 2524:
			c.voidStatus[3]++;
			break;
		case 2526:
			c.voidStatus[4]++;
			break;
		}
	}

	public boolean tradeable(int itemId) {
		for (int j = 0; j < Config.ITEM_TRADEABLE.length; j++) {
			if (itemId == Config.ITEM_TRADEABLE[j])
				return false;
		}
		return true;
	}

	/**
	 * Add Item
	 **/
	public boolean addItem(int item, int amount) {
		if (c != null) {
			if (amount < 1) {
				amount = 1;
			}
			if (item <= 0) {
				return false;
			}
			if ((((freeSlots() >= 1) || playerHasItem(item, 1)) && Item.itemStackable[item])
					|| ((freeSlots() > 0) && !Item.itemStackable[item])) {
				for (int i = 0; i < c.playerItems.length; i++) {
					if ((c.playerItems[i] == (item + 1))
							&& Item.itemStackable[item]
							&& (c.playerItems[i] > 0)) {
						c.playerItems[i] = (item + 1);
						if (((c.playerItemsN[i] + amount) < Config.MAXITEM_AMOUNT)
								&& ((c.playerItemsN[i] + amount) > -1)) {
							c.playerItemsN[i] += amount;
						} else {
							c.playerItemsN[i] = Config.MAXITEM_AMOUNT;
						}
						if (c.getOutStream() != null && c != null) {
							c.getOutStream().createFrameVarSizeWord(34);
							c.getOutStream().writeWord(3214);
							c.getOutStream().writeByte(i);
							c.getOutStream().writeWord(c.playerItems[i]);
							if (c.playerItemsN[i] > 254) {
								c.getOutStream().writeByte(255);
								c.getOutStream().writeDWord(c.playerItemsN[i]);
							} else {
								c.getOutStream().writeByte(c.playerItemsN[i]);
							}
							c.getOutStream().endFrameVarSizeWord();
							c.flushOutStream();
						}
						i = 30;
						return true;
					}
				}
				for (int i = 0; i < c.playerItems.length; i++) {
					if (c.playerItems[i] <= 0) {
						c.playerItems[i] = item + 1;
						if ((amount < Config.MAXITEM_AMOUNT) && (amount > -1)) {
							c.playerItemsN[i] = 1;
							if (amount > 1) {
								c.getItems().addItem(item, amount - 1);
								return true;
							}
						} else {
							c.playerItemsN[i] = Config.MAXITEM_AMOUNT;
						}
						/*
						 * if(c.getOutStream() != null && c != null ) {
						 * c.getOutStream().createFrameVarSizeWord(34);
						 * c.getOutStream().writeWord(3214);
						 * c.getOutStream().writeByte(i);
						 * c.getOutStream().writeWord(c.playerItems[i]); if
						 * (c.playerItemsN[i] > 254) {
						 * c.getOutStream().writeByte(255);
						 * c.getOutStream().writeDWord(c.playerItemsN[i]); }
						 * else { c.getOutStream().writeByte(c.playerItemsN[i]);
						 * } c.getOutStream().endFrameVarSizeWord();
						 * c.flushOutStream(); }
						 */
						resetItems(3214);
						i = 30;
						return true;
					}
				}
				return false;
			} else {
				resetItems(3214);
				c.sendMessage("Not enough space in your inventory.");
				return false;
			}
		}
		return false;
	}

	public String itemType(int item) {
		if (Item.playerCape(item)) {
			return "cape";
		}
		if (Item.playerBoots(item)) {
			return "boots";
		}
		if (Item.playerGloves(item)) {
			return "gloves";
		}
		if (Item.playerShield(item)) {
			return "shield";
		}
		if (Item.playerAmulet(item)) {
			return "amulet";
		}
		if (Item.playerArrows(item)) {
			return "arrows";
		}
		if (Item.playerRings(item)) {
			return "ring";
		}
		if (Item.playerHats(item)) {
			return "hat";
		}
		if (Item.playerLegs(item)) {
			return "legs";
		}
		if (Item.playerBody(item)) {
			return "body";
		}
		return "weapon";
	}

	/**
	 * Bonuses
	 **/

	public final String[] BONUS_NAMES = { "Stab", "Slash", "Crush", "Magic",
			"Range", "Stab", "Slash", "Crush", "Magic", "Range", "Strength",
			"Prayer" };

	public void resetBonus() {
		for (int i = 0; i < c.playerBonus.length; i++) {
			c.playerBonus[i] = 0;
		}
	}

	public void getBonus() {
		for (int i = 0; i < c.playerEquipment.length; i++) {
			if (c.playerEquipment[i] > -1) {
				for (int j = 0; j < Config.ITEM_LIMIT; j++) {
					if (Server.itemHandler.ItemList[j] != null) {
						if (Server.itemHandler.ItemList[j].itemId == c.playerEquipment[i]) {
							for (int k = 0; k < c.playerBonus.length; k++) {
								c.playerBonus[k] += Server.itemHandler.ItemList[j].Bonuses[k];
							}
							break;
						}
					}
				}
			}
		}
		if (c.dfsCount > 0 && c.playerEquipment[Player.playerShield] == 11283
				|| c.playerEquipment[Player.playerShield] == 11284) {
			c.playerBonus[5] += 1 * c.dfsCount;
			c.playerBonus[6] += 1 * c.dfsCount;
			c.playerBonus[7] += 1 * c.dfsCount;
			c.playerBonus[9] += 1 * c.dfsCount;
		}
	}

	/**
	 * Wear Item
	 **/

	public void sendWeapon(int Weapon, String WeaponName) {
		String WeaponName2 = WeaponName.replaceAll("Bronze", "");
		WeaponName2 = WeaponName2.replaceAll("Iron", "");
		WeaponName2 = WeaponName2.replaceAll("Steel", "");
		WeaponName2 = WeaponName2.replaceAll("Black", "");
		WeaponName2 = WeaponName2.replaceAll("Mithril", "");
		WeaponName2 = WeaponName2.replaceAll("Adamant", "");
		WeaponName2 = WeaponName2.replaceAll("Rune", "");
		WeaponName2 = WeaponName2.replaceAll("Granite", "");
		WeaponName2 = WeaponName2.replaceAll("Dragon", "");
		WeaponName2 = WeaponName2.replaceAll("Drag", "");
		WeaponName2 = WeaponName2.replaceAll("Crystal", "");
		WeaponName2 = WeaponName2.trim();
		// c.sendMessage(WeaponName2);
		if (WeaponName.equals("Unarmed")) {
			c.setSidebarInterface(0, 5855); // punch, kick, block
			c.getPA().sendFrame126(WeaponName, 5857);
		} else if (WeaponName.endsWith("whip")) {
			c.setSidebarInterface(0, 12290); // flick, lash, deflect
			c.getPA().sendFrame246(12291, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 12293);
		} else if (WeaponName.endsWith("bow") || WeaponName.endsWith("10")
				|| WeaponName.endsWith("full")
				|| WeaponName.startsWith("seercull")
				|| WeaponName.contains("cannon")) {
			c.setSidebarInterface(0, 1764); // accurate, rapid, longrange
			c.getPA().sendFrame246(1765, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 1767);
		} else if (!WeaponName2.contains("Staff of light")
				&& WeaponName.startsWith("Staff")
				|| WeaponName.endsWith("staff") || WeaponName.endsWith("wand")) {
			c.setSidebarInterface(0, 328); // spike, impale, smash, block
			c.getPA().sendFrame246(329, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 331);
		} else if (WeaponName2.startsWith("dart")
				|| WeaponName2.startsWith("knife")
				|| WeaponName2.startsWith("javelin")
				|| WeaponName.equalsIgnoreCase("toktz-xil-ul")) {
			c.setSidebarInterface(0, 4446); // accurate, rapid, longrange
			c.getPA().sendFrame246(4447, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 4449);
		} else if (WeaponName2.contains("dagger")
				|| WeaponName2.contains("sword")
				&& !WeaponName2.contains("godsword")
				|| WeaponName2.contains("Staff of light")) {
			c.setSidebarInterface(0, 2276); // stab, lunge, slash, block
			c.getPA().sendFrame246(2277, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 2279);
		} else if (WeaponName2.startsWith("pickaxe")) {
			c.setSidebarInterface(0, 5570); // spike, impale, smash, block
			c.getPA().sendFrame246(5571, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 5573);
		} else if (WeaponName2.contains("axe")) {
			c.setSidebarInterface(0, 1698); // chop, hack, smash, block
			c.getPA().sendFrame246(1699, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 1701);
		} else if (WeaponName2.contains("claws")) {
			c.setSidebarInterface(0, 7762);
			c.getPA().sendFrame246(7763, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 7765);
		} else if (WeaponName2.startsWith("halberd")) {
			c.setSidebarInterface(0, 8460); // jab, swipe, fend
			c.getPA().sendFrame246(8461, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 8463);
		} else if (WeaponName2.startsWith("Scythe")) {
			c.setSidebarInterface(0, 8460); // jab, swipe, fend
			c.getPA().sendFrame246(8461, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 8463);
		} else if (WeaponName2.contains("spear")) {
			c.setSidebarInterface(0, 4679); // lunge, swipe, pound, block
			c.getPA().sendFrame246(4680, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 4682);
		} else if (WeaponName2.toLowerCase().contains("mace")) {
			c.setSidebarInterface(0, 3796);
			c.getPA().sendFrame246(3797, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 3799);

		} else if (WeaponName2.contains("maul")
				|| WeaponName2.contains("hammer")) {
			c.setSidebarInterface(0, 425); // war hamer equip.
			c.getPA().sendFrame246(426, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 428);
		} else {
			c.setSidebarInterface(0, 2423); // chop, slash, lunge, block
			c.getPA().sendFrame246(2424, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 2426);
		}

	}

	/**
	 * Weapon Requirements
	 **/

	public void getRequirements(String itemName, int itemId) {
		c.attackLevelReq = c.defenceLevelReq = c.strengthLevelReq = c.rangeLevelReq = c.prayerLevelReq = c.magicLevelReq = 0;
		if (itemName.contains("mystic") || itemName.contains("nchanted")) {
			if (itemName.contains("staff")) {
				c.magicLevelReq = 20;
				c.attackLevelReq = 40;
			} else {
				c.magicLevelReq = 20;
				c.defenceLevelReq = 20;
			}
		}
		if (itemName.contains("infinity")) {
			c.magicLevelReq = 50;
			c.defenceLevelReq = 25;
		}
		if (itemName.contains("rune c'bow")) {
			c.rangeLevelReq = 61;
		}
		if (itemName.contains("splitbark")) {
			c.magicLevelReq = 40;
			c.defenceLevelReq = 40;
		}
		if (itemName.contains("Green")) {
			if (itemName.contains("hide")) {
				c.rangeLevelReq = 40;
				if (itemName.contains("body"))
					c.defenceLevelReq = 40;
				return;
			}
		}
		if (itemName.contains("Blue")) {
			if (itemName.contains("hide")) {
				c.rangeLevelReq = 50;
				if (itemName.contains("body"))
					c.defenceLevelReq = 40;
				return;
			}
		}
		if (itemName.contains("Red")) {
			if (itemName.contains("hide")) {
				c.rangeLevelReq = 60;
				if (itemName.contains("body"))
					c.defenceLevelReq = 40;
				return;
			}
		}
		if (itemName.contains("Black")) {
			if (itemName.contains("hide")) {
				c.rangeLevelReq = 70;
				if (itemName.contains("body"))
					c.defenceLevelReq = 40;
				return;
			}
		}
		if (itemName.contains("bronze")) {
			if (!itemName.contains("knife") && !itemName.contains("dart")
					&& !itemName.contains("javelin")
					&& !itemName.contains("thrownaxe")) {
				c.attackLevelReq = c.defenceLevelReq = 1;
			}
			return;
		}
		if (itemName.contains("iron")) {
			if (!itemName.contains("knife") && !itemName.contains("dart")
					&& !itemName.contains("javelin")
					&& !itemName.contains("thrownaxe")) {
				c.attackLevelReq = c.defenceLevelReq = 1;
			}
			return;
		}
		if (itemName.contains("steel")) {
			if (!itemName.contains("knife") && !itemName.contains("dart")
					&& !itemName.contains("javelin")
					&& !itemName.contains("thrownaxe")) {
				c.attackLevelReq = c.defenceLevelReq = 5;
			}
			return;
		}
		/*
		 * if(itemName.contains("black")) { if(!itemName.contains("knife") &&
		 * !itemName.contains("dart") && !itemName.contains("javelin") &&
		 * !itemName.contains("thrownaxe") && !itemName.contains("vamb") &&
		 * !itemName.contains("chap")) { c.attackLevelReq = c.defenceLevelReq =
		 * 10; } return; }
		 */
		if (itemName.contains("mithril")) {
			if (!itemName.contains("knife") && !itemName.contains("dart")
					&& !itemName.contains("javelin")
					&& !itemName.contains("thrownaxe")) {
				c.attackLevelReq = c.defenceLevelReq = 20;
			}
			return;
		}
		if (itemName.contains("adamant")) {
			if (!itemName.contains("knife") && !itemName.contains("dart")
					&& !itemName.contains("javelin")
					&& !itemName.contains("thrownaxe")) {
				c.attackLevelReq = c.defenceLevelReq = 30;
			}
			return;
		}
		if (itemName.contains("rune") || itemName.contains("gilded")
				|| itemName.contains("decorative")) {
			if (!itemName.contains("hood") && !itemName.contains("knife")
					&& !itemName.contains("dart")
					&& !itemName.contains("javelin")
					&& !itemName.contains("thrownaxe")
					&& !itemName.contains("'bow")) {
				c.attackLevelReq = c.defenceLevelReq = 40;
			}
			return;
		}
		if (itemName.contains("dragon")) {
			if (!itemName.contains("nti-") && !itemName.contains("fire")
					&& !itemName.contains("corrupt")) {
				c.attackLevelReq = c.defenceLevelReq = 60;
				return;
			}
		}
		if (itemName.contains("dragon") && itemName.contains("corrupt")) {
			if (!itemName.contains("nti-") && !itemName.contains("fire")) {
				c.attackLevelReq = c.defenceLevelReq = 40;
				return;
			}
		}
		if (itemName.contains("crystal")) {
			if (itemName.contains("shield")) {
				c.defenceLevelReq = 70;
			} else {
				c.rangeLevelReq = 70;
			}
			return;
		}
		if (itemName.contains("ahrim")) {
			if (itemName.contains("staff")) {
				c.attackLevelReq = 70;
			} else {
				c.defenceLevelReq = 70;
			}
			c.magicLevelReq = 70;
		}
		if (itemName.contains("dagon")) {
			c.magicLevelReq = 40;
			c.defenceLevelReq = 20;
		}
		if (itemName.contains("stream")) {
			c.magicLevelReq = 70;
		}
		if (itemName.contains("initiate")) {
			c.defenceLevelReq = 20;
			c.prayerLevelReq = 10;
		}
		if (itemName.contains("proselyte")) {
			c.defenceLevelReq = 30;
			c.prayerLevelReq = 20;
		}
		if (itemName.contains("chaotic") && !itemName.contains("staff")
				&& !itemName.contains("c'bow")) {
			if (itemName.contains("shield")) {
				c.defenceLevelReq = 80;
			} else {
				c.attackLevelReq = 80;
			}
		}
		if (itemName.contains("chaotic")) {
			if (itemName.contains("c'bow")) {
				c.rangeLevelReq = 80;
			} else {
				c.magicLevelReq = 80;
			}
		}
		if (itemName.contains("vesta") && itemName.contains("corrupt")) {
			if (itemName.contains("longsword") || itemName.contains("spear")) {
				c.attackLevelReq = 20;
			} else {
				c.defenceLevelReq = 20;
			}
		}
		if (itemName.contains("vesta") && itemName.contains("c.")) {
			if (itemName.contains("longsword") || itemName.contains("spear")) {
				c.attackLevelReq = 20;
			} else {
				c.defenceLevelReq = 20;
			}
		}
		if (itemName.contains("vesta") && !itemName.contains("corrupt")) {
			if (itemName.contains("longsword") || itemName.contains("spear")) {
				c.attackLevelReq = 78;
			} else {
				c.defenceLevelReq = 78;
			}
		}
		if (itemName.contains("statius") && itemName.contains("corrupt")) {
			if (itemName.contains("warhammer")) {
				c.attackLevelReq = 20;
			} else {
				c.defenceLevelReq = 20;
			}
		}
		if (itemName.contains("statius") && itemName.contains("c.")) {
			if (itemName.contains("warhammer")) {
				c.attackLevelReq = 20;
			} else {
				c.defenceLevelReq = 20;
			}
		}
		if (itemName.contains("statius") && !itemName.contains("corrupt")) {
			if (itemName.contains("warhammer")) {
				c.attackLevelReq = 78;
			} else {
				c.defenceLevelReq = 78;
			}
		}
		if (itemName.contains("zuriel") && itemName.contains("corrupt")) {
			if (itemName.contains("staff")) {
				c.attackLevelReq = 20;
			} else {
				c.defenceLevelReq = 20;
			}
			c.magicLevelReq = 20;
		}
		if (itemName.contains("zuriel") && itemName.contains("c.")) {
			if (itemName.contains("staff")) {
				c.attackLevelReq = 20;
			} else {
				c.defenceLevelReq = 20;
			}
			c.magicLevelReq = 20;
		}
		if (itemName.contains("zuriel") && !itemName.contains("corrupt")) {
			if (itemName.contains("staff")) {
				c.attackLevelReq = 78;
			} else {
				c.defenceLevelReq = 78;
			}
			c.magicLevelReq = 78;
		}
		if (itemName.contains("morrigan") && itemName.contains("corrupt")) {
			if (itemName.contains("javelin")) {
				c.rangeLevelReq = 20;
			} else {
				c.rangeLevelReq = 20;
				c.defenceLevelReq = 20;
			}
		}
		if (itemName.contains("morrigan") && itemName.contains("c.")) {
			if (itemName.contains("javelin")) {
				c.rangeLevelReq = 20;
			} else {
				c.rangeLevelReq = 20;
				c.defenceLevelReq = 20;
			}
		}
		if (itemName.contains("morrigan") && !itemName.contains("corrupt")) {
			if (itemName.contains("javelin")) {
				c.rangeLevelReq = 78;
			} else {
				c.rangeLevelReq = 78;
				c.defenceLevelReq = 78;
			}
		}
		if (itemName.contains("karil")) {
			if (itemName.contains("crossbow")) {
				c.rangeLevelReq = 70;
			} else {
				c.rangeLevelReq = 70;
				c.defenceLevelReq = 70;
			}
		}
		if (itemName.contains("elite black")) {
			c.defenceLevelReq = 40;
		}
		if (itemName.contains("godsword")) {
			c.attackLevelReq = 75;
		}
		if (itemName.contains("3rd age") && !itemName.contains("amulet")) {
			c.defenceLevelReq = 60;
		}
		if (itemName.contains("primal")) {
			if (itemName.contains("primal maul")) {
				c.strengthLevelReq = 99;
			} else {
				c.attackLevelReq = 99;
			}
		}
		if (itemName.endsWith("spirit shield")) {
			if (itemName.startsWith("elysian")) {
				c.defenceLevelReq = 75;
				c.prayerLevelReq = 70;
			} else if (itemName.startsWith("divine")) {
				c.defenceLevelReq = 75;
				c.prayerLevelReq = 70;
			} else if (itemName.startsWith("spectral")) {
				c.defenceLevelReq = 75;
				c.prayerLevelReq = 70;
				c.magicLevelReq = 65;
			} else if (itemName.startsWith("arcane")) {
				c.defenceLevelReq = 75;
				c.prayerLevelReq = 70;
				c.magicLevelReq = 65;
			} else if (itemName.startsWith("blessed")) {
				c.defenceLevelReq = 70;
				c.prayerLevelReq = 60;
			} else {
				c.defenceLevelReq = 40;
				c.prayerLevelReq = 55;
			}
			return;
		}
		if (itemName.contains("torva")) {
			c.defenceLevelReq = 80;
			c.strengthLevelReq = 80;
		}
		if (itemName.contains("pernix")) {
			c.defenceLevelReq = 80;
			c.rangeLevelReq = 80;
		}
		if (itemName.contains("virtus")) {
			c.defenceLevelReq = 80;
			c.magicLevelReq = 80;
		}
		if (itemName.contains("ganodermic")) {
			c.defenceLevelReq = 85;
			c.magicLevelReq = 85;
		}
		if (itemName.contains("verac") || itemName.contains("guthan")
				|| itemName.contains("dharok") || itemName.contains("torag")) {

			if (itemName.contains("hammers")) {
				c.attackLevelReq = 70;
				c.strengthLevelReq = 70;
			} else if (itemName.contains("axe")) {
				c.attackLevelReq = 70;
				c.strengthLevelReq = 70;
			} else if (itemName.contains("warspear")) {
				c.attackLevelReq = 70;
				c.strengthLevelReq = 70;
			} else if (itemName.contains("flail")) {
				c.attackLevelReq = 70;
				c.strengthLevelReq = 70;
			} else {
				c.defenceLevelReq = 70;
			}
		}

		switch (itemId) {
		case 8839:
		case 8840:
		case 8842:
		case 11663:
		case 11664:
		case 11665:
			c.attackLevelReq = 42;
			c.rangeLevelReq = 42;
			c.strengthLevelReq = 42;
			c.magicLevelReq = 42;
			c.defenceLevelReq = 42;
			return;
		case 17283:
			c.rangeLevelReq = 83;
			c.defenceLevelReq = 83;
			return;
		case 17287:
			c.defenceLevelReq = 94;
			return;
		case 16755:
		case 16865:
		case 16931:
		case 17171:
		case 17237:
			c.magicLevelReq = 99;
			c.defenceLevelReq = 99;
			return;
		case 17061:
		case 17193:
		case 17215:
		case 17317:
		case 17339:
		case 17361:
			c.rangeLevelReq = 99;
			c.defenceLevelReq = 99;
			return;
		case 16293:
		case 16359:
		case 16689:
		case 16711:
		case 17259:
			c.defenceLevelReq = 99;
			return;
		case 15241:
			c.rangeLevelReq = 75;
			return;
		case 18335:
			c.magicLevelReq = 70;// WHAT THE FUCK WHY AM I STILL ABLE TO WIELD
									// WITH 1 MAGE
			return;
		case 11283:
			c.defenceLevelReq = 75;
			return;
		case 4675:
			c.attackLevelReq = 50;
			c.magicLevelReq = 50;
			return;
		case 15486:
			c.attackLevelReq = 75;
			c.magicLevelReq = 75;
			return;
		case 11730:
		case 11716:
			c.attackLevelReq = 70;
			return;
		case 10548:
		case 10551:
		case 2501:
		case 2499:
		case 1135:
			c.defenceLevelReq = 40;
			return;
		case 6524:
			c.defenceLevelReq = 60;
			return;
		case 11284:
			c.defenceLevelReq = 75;
			return;
		case 6889:
		case 6914:
			c.magicLevelReq = 60;
			return;
		case 861:
		case 859:
			c.rangeLevelReq = 50;
			return;
		case 10828:
			c.defenceLevelReq = 55;
			return;
		case 11724:
		case 11726:
		case 11728:
			c.defenceLevelReq = 65;
			return;
		case 3751:
		case 3749:
		case 3755:
		case 3753:
			c.defenceLevelReq = 45;
			return;
		case 2497:
			c.rangeLevelReq = 70;
			break;
		case 2412:
		case 2413:
		case 2414:
			c.magicLevelReq = 60;
			return;
		case 9185:
			c.rangeLevelReq = 61;
			return;
		case 2503:
			c.defenceLevelReq = 40;
			c.rangeLevelReq = 70;
			return;
		case 8846:
			c.defenceLevelReq = 5;
			return;
		case 8847:
			c.defenceLevelReq = 10;
			return;
		case 8848:
			c.defenceLevelReq = 20;
			return;
		case 8849:
			c.defenceLevelReq = 30;
			return;
		case 8850:
			c.defenceLevelReq = 40;
			return;
		case 7459:
			c.defenceLevelReq = 10;
			return;
		case 7460:
			c.defenceLevelReq = 20;
			return;
		case 7462:
			c.defenceLevelReq = 40;
			return;
		case 7461:
			c.defenceLevelReq = 30;
			return;
		case 837:
			c.rangeLevelReq = 61;
			return;
		case 9672:
		case 9674:
		case 9676:
		case 9678:
			c.defenceLevelReq = 30;
			c.prayerLevelReq = 20;
			return;
		case 12915:
			c.defenceLevelReq = 35;
			return;
		case 12929:
			c.defenceLevelReq = 45;
			return;
		case 2653:
		case 2655:
		case 2657:
		case 2659:
		case 2661:
		case 2663:
		case 2665:
		case 2667:
		case 2669:
		case 2671:
		case 2673:
		case 2675:
		case 19398:
		case 19401:
		case 19404:
		case 19407:
		case 19410:
		case 19413:
		case 19416:
		case 19419:
		case 19422:
		case 19428:
		case 19431:
		case 19434:
		case 19437:
		case 19440:
			c.defenceLevelReq = 40;
			return;
		case 10368:
		case 10372:
		case 10374:
		case 10376:
		case 10380:
		case 10382:
		case 10384:
		case 10388:
		case 10390:
		case 19443:
		case 19447:
		case 19449:
		case 19451:
		case 19455:
		case 19457:
		case 19459:
		case 19463:
		case 19465:
			c.rangeLevelReq = 70;
			return;
		case 19445:
		case 19453:
		case 19461:
		case 10370:
		case 10386:
		case 10378:
			c.rangeLevelReq = 70;
			c.defenceLevelReq = 40;
			return;
		case 11235:
		case 15701: // dark bow
		case 15702: // dark bow
		case 15703: // dark bow
		case 15704: // dark bow
		case 6522:
			c.rangeLevelReq = 60;
			return;
		case 4151:
		case 15445:
		case 15444:
		case 15443:
		case 15442:
		case 15441:
			c.attackLevelReq = 70;
			return;
		case 6724: // seercull
			c.rangeLevelReq = 60; // idk if that is correct
			return;
		case 4153:
			c.attackLevelReq = 50;
			c.strengthLevelReq = 50;
			return;
		case 17271:
			c.defenceLevelReq = 54;
			return;
		case 17279:
			c.defenceLevelReq = 45;
			return;
		case 19780:
			c.attackLevelReq = 78;
			c.strengthLevelReq = 78;
			return;
		}
	}

	/**
	 * two handed weapon check
	 **/
	public boolean is2handed(String itemName, int itemId) {
		if (itemName.contains("ahrim") || itemName.contains("karil")
				|| itemName.contains("verac") || itemName.contains("guthan")
				|| itemName.contains("dharok") || itemName.contains("torag")) {
			return true;
		}
		if (itemName.contains("longbow") || itemName.contains("shortbow")
				|| itemName.contains("ark bow")) {
			return true;
		}
		if (itemName.contains("crystal")) {
			return true;
		}
		if (itemName.contains("godsword")
				|| itemName.contains("aradomin sword")
				|| itemName.contains("2h") || itemName.contains("spear")
				|| itemName.contains("maul") || itemName.contains("anchor")) {
			return true;
		}
		switch (itemId) {
		case 6724: // seercull
		case 11730:
		case 4153:
		case 6528:
		case 14484:
		case 15241:
			return true;
		}
		return false;
	}

	/**
	 * Weapons special bar, adds the spec bars to weapons that require them and
	 * removes the spec bars from weapons which don't require them
	 **/

	public void addSpecialBar(int weapon) {
		switch (weapon) {
		case 14484: // Dragon claws
			c.getPA().sendFrame171(0, 7800);
			specialAmount(weapon, c.specAmount, 7812);
			break;
		case 15441: // whip
		case 15442: // whip
		case 15443: // whip
		case 15444: // whip
		case 4151: // whip
			c.getPA().sendFrame171(0, 12323);
			specialAmount(weapon, c.specAmount, 12335);
			break;

		case 859: // Magic bows
		case 861:
		case 11235: // Dark bow
		case 15701: // dark bow
		case 15702: // dark bow
		case 15703: // dark bow
		case 15704: // dark bow
		case 15241: // dark bow
			c.getPA().sendFrame171(0, 7549);
			specialAmount(weapon, c.specAmount, 7561);
			break;

		case 4587: // d scimmy
		case 10887:
		case 11694:
		case 11698:
		case 11700:
		case 11696:
		case 13979: // corrupt scimmy
		case 19780: // korasi
			c.getPA().sendFrame171(0, 7599);
			specialAmount(weapon, c.specAmount, 7611);
			break;

		case 3204: // d hally
			c.getPA().sendFrame171(0, 8493);
			specialAmount(weapon, c.specAmount, 8505);
			break;

		case 1377: // d battleaxe
		case 13973:
			c.getPA().sendFrame171(0, 7499);
			specialAmount(weapon, c.specAmount, 7511);
			break;

		case 4153: // gmaul
		case 13902: // statius hammer
		case 13926: // corrupt statius hammer
		case 13928: // deg statius hammer
			c.getPA().sendFrame171(0, 7474);
			specialAmount(weapon, c.specAmount, 7486);
			break;

		case 1249: // dspear
		case 13905: // vesta spear
		case 13988: // corrupt spear
		case 13929: // corrupt vesta spear
		case 13931: // deg vesta spear
			c.getPA().sendFrame171(0, 7674);
			specialAmount(weapon, c.specAmount, 7686);
			break;

		case 15486: // SOL
		case 1215:// dragon dagger
		case 1231:
		case 5680:
		case 5698:
		case 1305: // dragon long
		case 11730:
		case 13976: // corrupt dd
		case 13982: // corrupt d long
		case 13899: // vls
		case 13923: // corrupt vls
		case 13925: // deg vls
			c.getPA().sendFrame171(0, 7574);
			specialAmount(weapon, c.specAmount, 7586);
			break;

		case 1434: // dragon mace
		case 13985: // corrupt d mace
			c.getPA().sendFrame171(0, 7624);
			specialAmount(weapon, c.specAmount, 7636);
			break;

		default:
			c.getPA().sendFrame171(1, 7624); // mace interface
			c.getPA().sendFrame171(1, 7474); // hammer, gmaul
			c.getPA().sendFrame171(1, 7499); // axe
			c.getPA().sendFrame171(1, 7549); // bow interface
			c.getPA().sendFrame171(1, 7574); // sword interface
			c.getPA().sendFrame171(1, 7599); // scimmy sword interface
			c.getPA().sendFrame171(1, 8493);
			c.getPA().sendFrame171(1, 12323); // whip interface
			break;
		}
	}

	/**
	 * Specials bar filling amount
	 **/

	public void specialAmount(int weapon, double specAmount, int barId) {
		c.specBarId = barId;
		c.getPA().sendFrame70(specAmount >= 10 ? 500 : 0, 0, (--barId));
		c.getPA().sendFrame70(specAmount >= 9 ? 500 : 0, 0, (--barId));
		c.getPA().sendFrame70(specAmount >= 8 ? 500 : 0, 0, (--barId));
		c.getPA().sendFrame70(specAmount >= 7 ? 500 : 0, 0, (--barId));
		c.getPA().sendFrame70(specAmount >= 6 ? 500 : 0, 0, (--barId));
		c.getPA().sendFrame70(specAmount >= 5 ? 500 : 0, 0, (--barId));
		c.getPA().sendFrame70(specAmount >= 4 ? 500 : 0, 0, (--barId));
		c.getPA().sendFrame70(specAmount >= 3 ? 500 : 0, 0, (--barId));
		c.getPA().sendFrame70(specAmount >= 2 ? 500 : 0, 0, (--barId));
		c.getPA().sendFrame70(specAmount >= 1 ? 500 : 0, 0, (--barId));
		updateSpecialBar();
		sendWeapon(weapon, getItemName(weapon));
	}

	/**
	 * Special attack text and what to highlight or blackout
	 **/

	public void updateSpecialBar() {
		if (c.usingSpecial && c.playerEquipment[Player.playerWeapon] != 15050) {
			c.getPA().sendFrame126(
					"@yel@ Special Attack (" + (int) c.specAmount * 10 + "%)",
					c.specBarId);
		} else {
			c.getPA().sendFrame126(
					"@bla@ Special Attack (" + (int) c.specAmount * 10 + "%)",
					c.specBarId);
		}
	}

	/**
	 * Wear Item
	 **/

	public boolean wearItem(int wearID, int slot) {
		if (c != null) {
			// boolean torvaChanged = false;
			int[] flasks = { 14207, 14200, 14198, 14196, 14194, 14192, 14190,
					14188, 14186, 14184, 14182, 14180, 14178, 14176, 14174,
					14172, 14170, 14168, 14166, 14164, 14162, 14160, 14158,
					14156, 14154, 14152, 14150, 14148, 14146, 14144, 14142,
					14140, 14138, 14136, 14134, 14132, 14130, 14128, 14126,
					14124, 14122, 14419, 14417, 14415, 14413, 14411, 14409,
					14407, 14405, 14403, 14401, 14399, 14397, 14395, 14393,
					14385, 14383, 14381, 14379, 14377, 14375, 14373, 14371,
					14369, 14367, 14365, 14363, 14361, 14359, 14357, 14355,
					14353, 14351, 14349, 14347, 14345, 14343, 14341, 14339,
					14337, 14335, 14333, 14331, 14329, 14327, 14325, 14323,
					14321, 14319, 14317, 14315, 14313, 14311, 14309, 14307,
					14305, 14303, 14301, 14299, 14297, 14295, 14293, 14291, };
			boolean wear = true;
			for (int i : flasks) {
				if(wearID == 14207)
					return false;
				if (i == wearID) {
					c.getPotions().handlePotion(i, c.wearSlot);
					 return false;
				}
			}
			int targetSlot = 0;
			boolean canWearItem = true;
			if (c.playerItems[slot] == (wearID + 1)) {
				getRequirements(getItemName(wearID).toLowerCase(), wearID);
				targetSlot = Item.targetSlots[wearID];

				/*
				 * if (wearID == 19083 || wearID == 19084 || wearID == 19085 ||
				 * wearID == 19072 || wearID == 19073 || wearID == 19074 ||
				 * wearID == 19075 || wearID == 19076 || wearID == 19076 ||
				 * wearID == 19077) { torvaChanged = true; }
				 */

				if (wearID > 15085 && wearID < 15102) {
					if (c.clanId >= 0) {
						c.useDice(wearID, true);
					} else {
						c.sendMessage("You must be in a clan chat channel to do that.");
					}
					return false;
				}

				if (itemType(wearID).equalsIgnoreCase("cape")) {
					targetSlot = 1;
				} else if (itemType(wearID).equalsIgnoreCase("hat")) {
					targetSlot = 0;
				} else if (itemType(wearID).equalsIgnoreCase("amulet")) {
					targetSlot = 2;
				} else if (itemType(wearID).equalsIgnoreCase("arrows")) {
					targetSlot = 13;
				} else if (itemType(wearID).equalsIgnoreCase("body")) {
					targetSlot = 4;
				} else if (itemType(wearID).equalsIgnoreCase("shield")) {
					targetSlot = 5;
				} else if (itemType(wearID).equalsIgnoreCase("legs")) {
					targetSlot = 7;
				} else if (itemType(wearID).equalsIgnoreCase("gloves")) {
					targetSlot = 9;
				} else if (itemType(wearID).equalsIgnoreCase("boots")) {
					targetSlot = 10;
				} else if (itemType(wearID).equalsIgnoreCase("ring")) {
					targetSlot = 12;
				} else {
					targetSlot = 3;
				}

				if (c.duelRule[11] && targetSlot == 0) {
					c.sendMessage("Wearing hats has been disabled in this duel!");
					return false;
				}
				if (c.duelRule[12] && targetSlot == 1) {
					c.sendMessage("Wearing capes has been disabled in this duel!");
					return false;
				}
				if (c.duelRule[13] && targetSlot == 2) {
					c.sendMessage("Wearing amulets has been disabled in this duel!");
					return false;
				}
				if (c.duelRule[14] && targetSlot == 3) {
					c.sendMessage("Wielding weapons has been disabled in this duel!");
					return false;
				}
				if (c.duelRule[15] && targetSlot == 4) {
					c.sendMessage("Wearing bodies has been disabled in this duel!");
					return false;
				}
				if ((c.duelRule[16] && targetSlot == 5)
						|| (c.duelRule[16] && is2handed(getItemName(wearID)
								.toLowerCase(), wearID))) {
					c.sendMessage("Wearing shield has been disabled in this duel!");
					return false;
				}
				if (c.duelRule[17] && targetSlot == 7) {
					c.sendMessage("Wearing legs has been disabled in this duel!");
					return false;
				}
				if (c.duelRule[18] && targetSlot == 9) {
					c.sendMessage("Wearing gloves has been disabled in this duel!");
					return false;
				}
				if (c.duelRule[19] && targetSlot == 10) {
					c.sendMessage("Wearing boots has been disabled in this duel!");
					return false;
				}
				if (c.duelRule[20] && targetSlot == 12) {
					c.sendMessage("Wearing rings has been disabled in this duel!");
					return false;
				}
				if (c.duelRule[21] && targetSlot == 13) {
					c.sendMessage("Wearing arrows has been disabled in this duel!");
					return false;
				}

				if (Config.itemRequirements) {
					if (targetSlot == 10 || targetSlot == 7 || targetSlot == 5
							|| targetSlot == 4 || targetSlot == 0
							|| targetSlot == 9 || targetSlot == 2) {
						// if (c.attackLevelReq > 0) {
						// if (c.getPA().getLevelForXP(c.playerXP[0]) <
						// c.attackLevelReq) {
						// c.sendMessage("You need an attack level of "
						// + c.attackLevelReq + " to wear this item.");
						// canWearItem = false;
						// }
						// }
						if (c.defenceLevelReq > 0) {
							if (c.getPA().getLevelForXP(c.playerXP[1]) < c.defenceLevelReq) {
								c.sendMessage("You need a defence level of "
										+ c.defenceLevelReq
										+ " to wear this item.");
								canWearItem = false;
							}
						}
						if (c.rangeLevelReq > 0) {
							if (c.getPA().getLevelForXP(c.playerXP[4]) < c.rangeLevelReq) {
								c.sendMessage("You need a range level of "
										+ c.rangeLevelReq
										+ " to wear this item.");
								canWearItem = false;
							}
						}
						if (c.magicLevelReq > 0) {
							if (c.getPA().getLevelForXP(c.playerXP[6]) < c.magicLevelReq) {
								c.sendMessage("You need a magic level of "
										+ c.magicLevelReq
										+ " to wear this item.");
								canWearItem = false;
							}
						}
						if (c.prayerLevelReq > 0) {
							if (c.getPA().getLevelForXP(c.playerXP[5]) < c.prayerLevelReq) {
								c.sendMessage("You need a prayer level of "
										+ c.prayerLevelReq
										+ " to wear this item.");
								canWearItem = false;
							}
						}
						if (c.strengthLevelReq > 0) {
							if (c.getPA().getLevelForXP(c.playerXP[2]) < c.strengthLevelReq) {
								c.sendMessage("You need a strength level of "
										+ c.strengthLevelReq
										+ " to wear this item.");
								canWearItem = false;
							}
						}
					}
					if (targetSlot == 3) {
						if (c.attackLevelReq > 0) {
							if (c.getPA().getLevelForXP(c.playerXP[0]) < c.attackLevelReq) {
								c.sendMessage("You need an attack level of "
										+ c.attackLevelReq
										+ " to wield this weapon.");
								canWearItem = false;
							}
						}
						if (c.rangeLevelReq > 0) {
							if (c.getPA().getLevelForXP(c.playerXP[4]) < c.rangeLevelReq) {
								c.sendMessage("You need a range level of "
										+ c.rangeLevelReq
										+ " to wield this weapon.");
								canWearItem = false;
							}
						}
						if (c.strengthLevelReq > 0) {
							if (c.getPA().getLevelForXP(c.playerXP[2]) < c.strengthLevelReq) {
								c.sendMessage("You need a strength level of "
										+ c.strengthLevelReq
										+ " to wield this weapon.");
								canWearItem = false;
							}
						}
						if (c.magicLevelReq > 0) {
							if (c.getPA().getLevelForXP(c.playerXP[6]) < c.magicLevelReq) {
								c.sendMessage("You need a magic level of "
										+ c.magicLevelReq
										+ " to wield this weapon.");
								canWearItem = false;
							}
						}
					}
				}

				if (!canWearItem) {
					return false;
				}

				int wearAmount = c.playerItemsN[slot];
				if (wearAmount < 1) {
					return false;
				}

				if (targetSlot == Player.playerWeapon) {
					c.autocasting = false;
					c.autocastId = 0;
					c.getPA().sendFrame36(108, 0);
				}

				if (slot >= 0 && wearID >= 0) {
					int toEquip = c.playerItems[slot];
					int toEquipN = c.playerItemsN[slot];
					int toRemove = c.playerEquipment[targetSlot];
					int toRemoveN = c.playerEquipmentN[targetSlot];
					if (toEquip == toRemove + 1 && Item.itemStackable[toRemove]) {
						deleteItem(toRemove, getItemSlot(toRemove), toEquipN);
						c.playerEquipmentN[targetSlot] += toEquipN;
					} else if (targetSlot != 5 && targetSlot != 3) {
						c.playerItems[slot] = 0;
						c.playerItemsN[slot] = 0;
						if (toRemove > 0 && toRemoveN > 0)
							addItem(toRemove, toRemoveN);
						c.playerEquipment[targetSlot] = toEquip - 1;
						c.playerEquipmentN[targetSlot] = toEquipN;
					} else if (targetSlot == 5) {
						boolean wearing2h = is2handed(
								getItemName(
										c.playerEquipment[Player.playerWeapon])
										.toLowerCase(),
								c.playerEquipment[Player.playerWeapon]);
						boolean wearingShield = c.playerEquipment[Player.playerShield] > 0;
						if (wearing2h) {
							toRemove = c.playerEquipment[Player.playerWeapon];
							toRemoveN = c.playerEquipmentN[Player.playerWeapon];
							c.playerEquipment[Player.playerWeapon] = -1;
							c.playerEquipmentN[Player.playerWeapon] = 0;
							updateSlot(Player.playerWeapon);
						}
						c.playerItems[slot] = toRemove + 1;
						c.playerItemsN[slot] = toRemoveN;
						c.playerEquipment[targetSlot] = toEquip - 1;
						c.playerEquipmentN[targetSlot] = toEquipN;
					} else if (targetSlot == 3) {
						boolean is2h = is2handed(getItemName(wearID)
								.toLowerCase(), wearID);
						boolean wearingShield = c.playerEquipment[Player.playerShield] > 0;
						boolean wearingWeapon = c.playerEquipment[Player.playerWeapon] > 0;
						if (is2h) {
							if (wearingShield && wearingWeapon) {
								if (freeSlots() > 0) {
									c.playerItems[slot] = toRemove + 1;
									c.playerItemsN[slot] = toRemoveN;
									c.playerEquipment[targetSlot] = toEquip - 1;
									c.playerEquipmentN[targetSlot] = toEquipN;
									removeItem(
											c.playerEquipment[Player.playerShield],
											Player.playerShield);
								} else {
									c.sendMessage("You do not have enough inventory space to do this.");
									return false;
								}
							} else if (wearingShield && !wearingWeapon) {
								c.playerItems[slot] = c.playerEquipment[Player.playerShield] + 1;
								c.playerItemsN[slot] = c.playerEquipmentN[Player.playerShield];
								c.playerEquipment[targetSlot] = toEquip - 1;
								c.playerEquipmentN[targetSlot] = toEquipN;
								c.playerEquipment[Player.playerShield] = -1;
								c.playerEquipmentN[Player.playerShield] = 0;
								updateSlot(Player.playerShield);
							} else {
								c.playerItems[slot] = toRemove + 1;
								c.playerItemsN[slot] = toRemoveN;
								c.playerEquipment[targetSlot] = toEquip - 1;
								c.playerEquipmentN[targetSlot] = toEquipN;
							}
						} else {
							c.playerItems[slot] = toRemove + 1;
							c.playerItemsN[slot] = toRemoveN;
							c.playerEquipment[targetSlot] = toEquip - 1;
							c.playerEquipmentN[targetSlot] = toEquipN;
						}
					}
					resetItems(3214);
				}
				if (c.playerLevel[3] >= c.calculateMaxLifePoints()) {
					c.playerLevel[3] = c.calculateMaxLifePoints();
					c.getPA().refreshSkill(3);
				}
				if (wearID == 13923) {
					c.playerEquipment[Player.playerWeapon] = 13925;
					c.getItems().wearItem(13925, 1, 3);
				}
				if (wearID == 13929) {
					c.playerEquipment[Player.playerWeapon] = 13931;
					c.getItems().wearItem(13931, 1, 3);
				}
				if (wearID == 13926) {
					c.playerEquipment[Player.playerWeapon] = 13928;
					c.getItems().wearItem(13928, 1, 3);
				}
				if (wearID == 13911) {
					c.playerEquipment[c.playerChest] = 13913;
					c.getItems().wearItem(13913, 1, c.playerChest);
				}
				if (wearID == 13917) {
					c.playerEquipment[c.playerLegs] = 13919;
					c.getItems().wearItem(13919, 1, c.playerLegs);
				}
				if (wearID == 13908) {
					c.playerEquipment[c.playerChest] = 13910;
					c.getItems().wearItem(13910, 1, c.playerChest);
				}
				if (wearID == 13914) {
					c.playerEquipment[c.playerLegs] = 13916;
					c.getItems().wearItem(13916, 1, c.playerLegs);
				}
				if (wearID == 13920) {
					c.playerEquipment[Player.playerHat] = 13922;
					c.getItems().wearItem(13922, 1, Player.playerHat);
				}
				if (wearID == 13938) {
					c.playerEquipment[Player.playerHat] = 13940;
					c.getItems().wearItem(13940, 1, Player.playerHat);
				}
				if (wearID == 13932) {
					c.playerEquipment[c.playerChest] = 13934;
					c.getItems().wearItem(13934, 1, c.playerChest);
				}
				if (wearID == 13935) {
					c.playerEquipment[c.playerLegs] = 13937;
					c.getItems().wearItem(13937, 1, c.playerLegs);
				}
				if (wearID == 13941) {
					c.playerEquipment[Player.playerWeapon] = 13943;
					c.getItems().wearItem(13943, 1, 3);
				}
				if (wearID == 13944) {
					c.playerEquipment[c.playerChest] = 13946;
					c.getItems().wearItem(13946, 1, c.playerChest);
				}
				if (wearID == 13947) {
					c.playerEquipment[c.playerLegs] = 13949;
					c.getItems().wearItem(13949, 1, c.playerLegs);
				}
				if (wearID == 13950) {
					c.playerEquipment[Player.playerHat] = 13952;
					c.getItems().wearItem(13952, 1, Player.playerHat);
				}
				if (targetSlot == 3) {
					c.usingSpecial = false;
					addSpecialBar(wearID);
				}
				if (c.getOutStream() != null && c != null) {
					c.getOutStream().createFrameVarSizeWord(34);
					c.getOutStream().writeWord(1688);
					c.getOutStream().writeByte(targetSlot);
					c.getOutStream().writeWord(wearID + 1);

					if (c.playerEquipmentN[targetSlot] > 254) {
						c.getOutStream().writeByte(255);
						c.getOutStream().writeDWord(
								c.playerEquipmentN[targetSlot]);
					} else {
						c.getOutStream().writeByte(
								c.playerEquipmentN[targetSlot]);
					}

					c.getOutStream().endFrameVarSizeWord();
					c.flushOutStream();
				}
				sendWeapon(c.playerEquipment[Player.playerWeapon],
						getItemName(c.playerEquipment[Player.playerWeapon]));
				resetBonus();
				getBonus();
				writeBonus();
				c.getCombat().getPlayerAnimIndex(
						c.getItems()
								.getItemName(
										c.playerEquipment[Player.playerWeapon])
								.toLowerCase());
				c.getPA().requestUpdates();
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public void wearItem(int wearID, int wearAmount, int targetSlot) {
		if (c != null) {
			if (c.getOutStream() != null && c != null) {
				c.getOutStream().createFrameVarSizeWord(34);
				c.getOutStream().writeWord(1688);
				c.getOutStream().writeByte(targetSlot);
				c.getOutStream().writeWord(wearID + 1);

				if (wearAmount > 254) {
					c.getOutStream().writeByte(255);
					c.getOutStream().writeDWord(wearAmount);
				} else {
					c.getOutStream().writeByte(wearAmount);
				}
				c.getOutStream().endFrameVarSizeWord();
				c.flushOutStream();
				c.playerEquipment[targetSlot] = wearID;
				c.playerEquipmentN[targetSlot] = wearAmount;
				c.getItems().sendWeapon(
						c.playerEquipment[Player.playerWeapon],
						c.getItems().getItemName(
								c.playerEquipment[Player.playerWeapon]));
				c.getItems().resetBonus();
				c.getItems().getBonus();
				c.getItems().writeBonus();
				c.getCombat().getPlayerAnimIndex(
						c.getItems()
								.getItemName(
										c.playerEquipment[Player.playerWeapon])
								.toLowerCase());
				c.updateRequired = true;
				c.setAppearanceUpdateRequired(true);
			}
		}
	}

	public void updateSlot(int slot) {
		if (c != null) {
			if (c.getOutStream() != null && c != null) {
				c.getOutStream().createFrameVarSizeWord(34);
				c.getOutStream().writeWord(1688);
				c.getOutStream().writeByte(slot);
				c.getOutStream().writeWord(c.playerEquipment[slot] + 1);
				if (c.playerEquipmentN[slot] > 254) {
					c.getOutStream().writeByte(255);
					c.getOutStream().writeDWord(c.playerEquipmentN[slot]);
				} else {
					c.getOutStream().writeByte(c.playerEquipmentN[slot]);
				}
				c.getOutStream().endFrameVarSizeWord();
				c.flushOutStream();
			}
		}

	}

	public boolean playerHasEquipped(int slot, int itemID) {
		return c.playerEquipment[slot] == itemID;
	}

	public boolean playerHasEquipped(int itemID) {
		itemID++;
		for (int i = 0; i < c.playerEquipment.length; i++) {
			if (c.playerEquipment[i] == itemID) {
				return true;
			}
		}
		return false;
	}

	public void replaceItem(Client c, int i, int l) {
		for (int k = 0; k < c.playerItems.length; k++) {
			if (playerHasItem(i, 1)) {
				deleteItem(i, getItemSlot(i), 1);
				addItem(l, 1);
			}
		}
	}

	/**
	 * Remove Item
	 **/
	public void removeItem(int wearID, int slot) {
		boolean torvaChanged = false;
		if (c.getOutStream() != null && c != null) {
			if (c.playerEquipment[slot] > -1) {
				if (addItem(c.playerEquipment[slot], c.playerEquipmentN[slot])) {
					if (c.playerEquipment[slot] == 19000
							|| c.playerEquipment[slot] == 19001
							|| c.playerEquipment[slot] == 19002
							|| c.playerEquipment[slot] == 19003
							|| c.playerEquipment[slot] == 19004
							|| c.playerEquipment[slot] == 19005
							|| c.playerEquipment[slot] == 19006
							|| c.playerEquipment[slot] == 19007
							|| c.playerEquipment[slot] == 19008)
						torvaChanged = true;
					c.playerEquipment[slot] = -1;
					c.playerEquipmentN[slot] = 0;
					sendWeapon(c.playerEquipment[Player.playerWeapon],
							getItemName(c.playerEquipment[Player.playerWeapon]));
					resetBonus();
					getBonus();
					writeBonus();
					c.getCombat()
							.getPlayerAnimIndex(
									c.getItems()
											.getItemName(
													c.playerEquipment[Player.playerWeapon])
											.toLowerCase());
					c.getOutStream().createFrame(34);
					c.getOutStream().writeWord(6);
					c.getOutStream().writeWord(1688);
					c.getOutStream().writeByte(slot);
					c.getOutStream().writeWord(0);
					c.getOutStream().writeByte(0);
					c.flushOutStream();
					c.updateRequired = true;
					c.setAppearanceUpdateRequired(true);
					if (torvaChanged
							&& c.playerLevel[3] >= c.calculateMaxLifePoints()) {
						c.playerLevel[3] = c.calculateMaxLifePoints();
						c.getPA().refreshSkill(3);
					}
				}
			}
		}
	}

	/**
	 * BANK
	 */

	public void rearrangeBank() {
		int totalItems = 0;
		int highestSlot = 0;
		for (int i = 0; i < Config.BANK_SIZE; i++) {
			if (c.bankItems[i] != 0) {
				totalItems++;
				if (highestSlot <= i) {
					highestSlot = i;
				}
			}
		}

		for (int i = 0; i <= highestSlot; i++) {
			if (c.bankItems[i] == 0) {
				boolean stop = false;

				for (int k = i; k <= highestSlot; k++) {
					if (c.bankItems[k] != 0 && !stop) {
						int spots = k - i;
						for (int j = k; j <= highestSlot; j++) {
							c.bankItems[j - spots] = c.bankItems[j];
							c.bankItemsN[j - spots] = c.bankItemsN[j];
							stop = true;
							c.bankItems[j] = 0;
							c.bankItemsN[j] = 0;
						}
					}
				}
			}
		}

		int totalItemsAfter = 0;
		for (int i = 0; i < Config.BANK_SIZE; i++) {
			if (c.bankItems[i] != 0) {
				totalItemsAfter++;
			}
		}

		if (totalItems != totalItemsAfter)
			c.disconnected = true;
	}

	public void itemOnInterface(int id, int amount) {
		if (c != null) {
			c.getOutStream().createFrameVarSizeWord(53);
			c.getOutStream().writeWord(2274);
			c.getOutStream().writeWord(1);
			if (amount > 254) {
				c.getOutStream().writeByte(255);
				c.getOutStream().writeDWord_v2(amount);
			} else {
				c.getOutStream().writeByte(amount);
			}
			c.getOutStream().writeWordBigEndianA(id);
			c.getOutStream().endFrameVarSizeWord();
			c.flushOutStream();
		}
	}

	public void resetBank() {
		if (c != null) {
			c.getOutStream().createFrameVarSizeWord(53);
			c.getOutStream().writeWord(5382); // bank
			c.getOutStream().writeWord(Config.BANK_SIZE);
			for (int i = 0; i < Config.BANK_SIZE; i++) {
				if (c.bankItemsN[i] > 254) {
					c.getOutStream().writeByte(255);
					c.getOutStream().writeDWord_v2(c.bankItemsN[i]);
				} else {
					c.getOutStream().writeByte(c.bankItemsN[i]);
				}
				if (c.bankItemsN[i] < 1) {
					c.bankItems[i] = 0;
				}
				if (c.bankItems[i] > Config.ITEM_LIMIT || c.bankItems[i] < 0) {
					c.bankItems[i] = Config.ITEM_LIMIT;
				}
				c.getOutStream().writeWordBigEndianA(c.bankItems[i]);
			}
			c.getOutStream().endFrameVarSizeWord();
			c.flushOutStream();
		}
	}

	public void resetTempItems() {
		if (c != null) {
			int itemCount = 0;
			for (int i = 0; i < c.playerItems.length; i++) {
				if (c.playerItems[i] > -1) {
					itemCount = i;
				}
			}
			c.getOutStream().createFrameVarSizeWord(53);
			c.getOutStream().writeWord(5064);
			c.getOutStream().writeWord(itemCount + 1);
			for (int i = 0; i < itemCount + 1; i++) {
				if (c.playerItemsN[i] > 254) {
					c.getOutStream().writeByte(255);
					c.getOutStream().writeDWord_v2(c.playerItemsN[i]);
				} else {
					c.getOutStream().writeByte(c.playerItemsN[i]);
				}
				if (c.playerItems[i] > Config.ITEM_LIMIT
						|| c.playerItems[i] < 0) {
					c.playerItems[i] = Config.ITEM_LIMIT;
				}
				c.getOutStream().writeWordBigEndianA(c.playerItems[i]);
			}
			c.getOutStream().endFrameVarSizeWord();
			c.flushOutStream();
		}
	}

	public boolean bankItem(int itemID, int fromSlot, int amount) {
		if (!c.isBanking) {
			return false;
		}
		if (c.playerItemsN[fromSlot] <= 0) {
			return false;
		}
		if (!Item.itemIsNote[c.playerItems[fromSlot] - 1]) {
			if (c.playerItems[fromSlot] <= 0) {
				return false;
			}
			if (Item.itemStackable[c.playerItems[fromSlot] - 1]
					|| c.playerItemsN[fromSlot] > 1) {
				int toBankSlot = 0;
				boolean alreadyInBank = false;
				for (int i = 0; i < Config.BANK_SIZE; i++) {
					if (c.bankItems[i] == c.playerItems[fromSlot]) {
						if (c.playerItemsN[fromSlot] < amount)
							amount = c.playerItemsN[fromSlot];
						alreadyInBank = true;
						toBankSlot = i;
						i = Config.BANK_SIZE + 1;
					}
				}

				if (!alreadyInBank && freeBankSlots() > 0) {
					for (int i = 0; i < Config.BANK_SIZE; i++) {
						if (c.bankItems[i] <= 0) {
							toBankSlot = i;
							i = Config.BANK_SIZE + 1;
						}
					}
					c.bankItems[toBankSlot] = c.playerItems[fromSlot];
					if (c.playerItemsN[fromSlot] < amount) {
						amount = c.playerItemsN[fromSlot];
					}
					if ((c.bankItemsN[toBankSlot] + amount) <= Config.MAXITEM_AMOUNT
							&& (c.bankItemsN[toBankSlot] + amount) > -1) {
						c.bankItemsN[toBankSlot] += amount;
					} else {
						c.sendMessage("Bank full!");
						return false;
					}
					deleteItem((c.playerItems[fromSlot] - 1), fromSlot, amount);
					resetTempItems();
					resetBank();
					return true;
				} else if (alreadyInBank) {
					if ((c.bankItemsN[toBankSlot] + amount) <= Config.MAXITEM_AMOUNT
							&& (c.bankItemsN[toBankSlot] + amount) > -1) {
						c.bankItemsN[toBankSlot] += amount;
					} else {
						c.sendMessage("Bank full!");
						return false;
					}
					deleteItem((c.playerItems[fromSlot] - 1), fromSlot, amount);
					resetTempItems();
					resetBank();
					return true;
				} else {
					c.sendMessage("Bank full!");
					return false;
				}
			} else {
				itemID = c.playerItems[fromSlot];
				int toBankSlot = 0;
				boolean alreadyInBank = false;
				for (int i = 0; i < Config.BANK_SIZE; i++) {
					if (c.bankItems[i] == c.playerItems[fromSlot]) {
						alreadyInBank = true;
						toBankSlot = i;
						i = Config.BANK_SIZE + 1;
					}
				}
				if (!alreadyInBank && freeBankSlots() > 0) {
					for (int i = 0; i < Config.BANK_SIZE; i++) {
						if (c.bankItems[i] <= 0) {
							toBankSlot = i;
							i = Config.BANK_SIZE + 1;
						}
					}
					int firstPossibleSlot = 0;
					boolean itemExists = false;
					while (amount > 0) {
						itemExists = false;
						for (int i = firstPossibleSlot; i < c.playerItems.length; i++) {
							if ((c.playerItems[i]) == itemID) {
								firstPossibleSlot = i;
								itemExists = true;
								i = 30;
							}
						}
						if (itemExists) {
							c.bankItems[toBankSlot] = c.playerItems[firstPossibleSlot];
							c.bankItemsN[toBankSlot] += 1;
							deleteItem((c.playerItems[firstPossibleSlot] - 1),
									firstPossibleSlot, 1);
							amount--;
						} else {
							amount = 0;
						}
					}
					resetTempItems();
					resetBank();
					return true;
				} else if (alreadyInBank) {
					int firstPossibleSlot = 0;
					boolean itemExists = false;
					while (amount > 0) {
						itemExists = false;
						for (int i = firstPossibleSlot; i < c.playerItems.length; i++) {
							if ((c.playerItems[i]) == itemID) {
								firstPossibleSlot = i;
								itemExists = true;
								i = 30;
							}
						}
						if (itemExists) {
							c.bankItemsN[toBankSlot] += 1;
							deleteItem((c.playerItems[firstPossibleSlot] - 1),
									firstPossibleSlot, 1);
							amount--;
						} else {
							amount = 0;
						}
					}
					resetTempItems();
					resetBank();
					return true;
				} else {
					c.sendMessage("Bank full!");
					return false;
				}
			}
		} else if (Item.itemIsNote[c.playerItems[fromSlot] - 1]
				&& !Item.itemIsNote[c.playerItems[fromSlot] - 2]) {
			if (c.playerItems[fromSlot] <= 0) {
				return false;
			}
			if (Item.itemStackable[c.playerItems[fromSlot] - 1]
					|| c.playerItemsN[fromSlot] > 1) {
				int toBankSlot = 0;
				boolean alreadyInBank = false;
				for (int i = 0; i < Config.BANK_SIZE; i++) {
					if (c.bankItems[i] == (c.playerItems[fromSlot] - 1)) {
						if (c.playerItemsN[fromSlot] < amount)
							amount = c.playerItemsN[fromSlot];
						alreadyInBank = true;
						toBankSlot = i;
						i = Config.BANK_SIZE + 1;
					}
				}

				if (!alreadyInBank && freeBankSlots() > 0) {
					for (int i = 0; i < Config.BANK_SIZE; i++) {
						if (c.bankItems[i] <= 0) {
							toBankSlot = i;
							i = Config.BANK_SIZE + 1;
						}
					}
					c.bankItems[toBankSlot] = (c.playerItems[fromSlot] - 1);
					if (c.playerItemsN[fromSlot] < amount) {
						amount = c.playerItemsN[fromSlot];
					}
					if ((c.bankItemsN[toBankSlot] + amount) <= Config.MAXITEM_AMOUNT
							&& (c.bankItemsN[toBankSlot] + amount) > -1) {
						c.bankItemsN[toBankSlot] += amount;
					} else {
						return false;
					}
					deleteItem((c.playerItems[fromSlot] - 1), fromSlot, amount);
					resetTempItems();
					resetBank();
					return true;
				} else if (alreadyInBank) {
					if ((c.bankItemsN[toBankSlot] + amount) <= Config.MAXITEM_AMOUNT
							&& (c.bankItemsN[toBankSlot] + amount) > -1) {
						c.bankItemsN[toBankSlot] += amount;
					} else {
						return false;
					}
					deleteItem((c.playerItems[fromSlot] - 1), fromSlot, amount);
					resetTempItems();
					resetBank();
					return true;
				} else {
					c.sendMessage("Bank full!");
					return false;
				}
			} else {
				itemID = c.playerItems[fromSlot];
				int toBankSlot = 0;
				boolean alreadyInBank = false;
				for (int i = 0; i < Config.BANK_SIZE; i++) {
					if (c.bankItems[i] == (c.playerItems[fromSlot] - 1)) {
						alreadyInBank = true;
						toBankSlot = i;
						i = Config.BANK_SIZE + 1;
					}
				}
				if (!alreadyInBank && freeBankSlots() > 0) {
					for (int i = 0; i < Config.BANK_SIZE; i++) {
						if (c.bankItems[i] <= 0) {
							toBankSlot = i;
							i = Config.BANK_SIZE + 1;
						}
					}
					int firstPossibleSlot = 0;
					boolean itemExists = false;
					while (amount > 0) {
						itemExists = false;
						for (int i = firstPossibleSlot; i < c.playerItems.length; i++) {
							if ((c.playerItems[i]) == itemID) {
								firstPossibleSlot = i;
								itemExists = true;
								i = 30;
							}
						}
						if (itemExists) {
							c.bankItems[toBankSlot] = (c.playerItems[firstPossibleSlot] - 1);
							c.bankItemsN[toBankSlot] += 1;
							deleteItem((c.playerItems[firstPossibleSlot] - 1),
									firstPossibleSlot, 1);
							amount--;
						} else {
							amount = 0;
						}
					}
					resetTempItems();
					resetBank();
					return true;
				} else if (alreadyInBank) {
					int firstPossibleSlot = 0;
					boolean itemExists = false;
					while (amount > 0) {
						itemExists = false;
						for (int i = firstPossibleSlot; i < c.playerItems.length; i++) {
							if ((c.playerItems[i]) == itemID) {
								firstPossibleSlot = i;
								itemExists = true;
								i = 30;
							}
						}
						if (itemExists) {
							c.bankItemsN[toBankSlot] += 1;
							deleteItem((c.playerItems[firstPossibleSlot] - 1),
									firstPossibleSlot, 1);
							amount--;
						} else {
							amount = 0;
						}
					}
					resetTempItems();
					resetBank();
					return true;
				} else {
					c.sendMessage("Bank full!");
					return false;
				}
			}
		} else {
			c.sendMessage("Item not supported " + (c.playerItems[fromSlot] - 1));
			return false;
		}
	}

	public int freeBankSlots() {
		int freeS = 0;
		for (int i = 0; i < Config.BANK_SIZE; i++) {
			if (c.bankItems[i] <= 0) {
				freeS++;
			}
		}
		return freeS;
	}

	public void fromBank(int itemID, int fromSlot, int amount) {
		if (!c.isBanking) {
			c.getPA().closeAllWindows();
			for (int j = 0; j < PlayerHandler.players.length; j++) {
				if (PlayerHandler.players[j] != null) {
					Client c2 = (Client) PlayerHandler.players[j];
					c2.sendMessage("@red@[Abuse]: "
							+ Misc.optimizeText(c.playerName)
							+ " Tried to bank hack.");
				}
			}
			return;
		}
		if (amount > 0) {
			if (c.bankItems[fromSlot] > 0) {
				if (!c.takeAsNote) {
					if (Item.itemStackable[c.bankItems[fromSlot] - 1]) {
						if (c.bankItemsN[fromSlot] > amount) {
							if (addItem((c.bankItems[fromSlot] - 1), amount)) {
								c.bankItemsN[fromSlot] -= amount;
								resetBank();
								c.getItems().resetItems(5064);
							}
						} else {
							if (addItem((c.bankItems[fromSlot] - 1),
									c.bankItemsN[fromSlot])) {
								c.bankItems[fromSlot] = 0;
								c.bankItemsN[fromSlot] = 0;
								resetBank();
								c.getItems().resetItems(5064);
							}
						}
					} else {
						while (amount > 0) {
							if (c.bankItemsN[fromSlot] > 0) {
								if (addItem((c.bankItems[fromSlot] - 1), 1)) {
									c.bankItemsN[fromSlot] += -1;
									amount--;
								} else {
									amount = 0;
								}
							} else {
								amount = 0;
							}
						}
						resetBank();
						c.getItems().resetItems(5064);
					}
				} else if (c.takeAsNote
						&& Item.itemIsNote[c.bankItems[fromSlot]]) {
					if (c.bankItemsN[fromSlot] > amount) {
						if (addItem(c.bankItems[fromSlot], amount)) {
							c.bankItemsN[fromSlot] -= amount;
							resetBank();
							c.getItems().resetItems(5064);
						}
					} else {
						if (addItem(c.bankItems[fromSlot],
								c.bankItemsN[fromSlot])) {
							c.bankItems[fromSlot] = 0;
							c.bankItemsN[fromSlot] = 0;
							resetBank();
							c.getItems().resetItems(5064);
						}
					}
				} else {
					c.sendMessage("This item can't be withdrawn as a note.");
					if (Item.itemStackable[c.bankItems[fromSlot] - 1]) {
						if (c.bankItemsN[fromSlot] > amount) {
							if (addItem((c.bankItems[fromSlot] - 1), amount)) {
								c.bankItemsN[fromSlot] -= amount;
								resetBank();
								c.getItems().resetItems(5064);
							}
						} else {
							if (addItem((c.bankItems[fromSlot] - 1),
									c.bankItemsN[fromSlot])) {
								c.bankItems[fromSlot] = 0;
								c.bankItemsN[fromSlot] = 0;
								resetBank();
								c.getItems().resetItems(5064);
							}
						}
					} else {
						while (amount > 0) {
							if (c.bankItemsN[fromSlot] > 0) {
								if (addItem((c.bankItems[fromSlot] - 1), 1)) {
									c.bankItemsN[fromSlot] += -1;
									amount--;
								} else {
									amount = 0;
								}
							} else {
								amount = 0;
							}
						}
						resetBank();
						c.getItems().resetItems(5064);
					}
				}
			}
		}
	}

	public int itemAmount(int itemID) {
		int tempAmount = 0;
		for (int i = 0; i < c.playerItems.length; i++) {
			if (c.playerItems[i] == itemID) {
				tempAmount += c.playerItemsN[i];
			}
		}
		return tempAmount;
	}

	public boolean isStackable(int itemID) {
		return Item.itemStackable[itemID];
	}

	/**
	 * Update Equip tab
	 **/

	public void setEquipment(int wearID, int amount, int targetSlot) {
		if (c != null) {
			c.getOutStream().createFrameVarSizeWord(34);
			c.getOutStream().writeWord(1688);
			c.getOutStream().writeByte(targetSlot);
			c.getOutStream().writeWord(wearID + 1);
			if (amount > 254) {
				c.getOutStream().writeByte(255);
				c.getOutStream().writeDWord(amount);
			} else {
				c.getOutStream().writeByte(amount);
			}
			c.getOutStream().endFrameVarSizeWord();
			c.flushOutStream();
			c.playerEquipment[targetSlot] = wearID;
			c.playerEquipmentN[targetSlot] = amount;
			c.updateRequired = true;
			c.setAppearanceUpdateRequired(true);
		}
	}

	/**
	 * Move Items
	 **/

	public void moveItems(int from, int to, int moveWindow) {
		if (moveWindow == 3724) {
			int tempI;
			int tempN;
			tempI = c.playerItems[from];
			tempN = c.playerItemsN[from];

			c.playerItems[from] = c.playerItems[to];
			c.playerItemsN[from] = c.playerItemsN[to];
			c.playerItems[to] = tempI;
			c.playerItemsN[to] = tempN;
		}

		if (moveWindow == 34453 && from >= 0 && to >= 0
				&& from < Config.BANK_SIZE && to < Config.BANK_SIZE
				&& to < Config.BANK_SIZE) {
			int tempI;
			int tempN;
			tempI = c.bankItems[from];
			tempN = c.bankItemsN[from];

			c.bankItems[from] = c.bankItems[to];
			c.bankItemsN[from] = c.bankItemsN[to];
			c.bankItems[to] = tempI;
			c.bankItemsN[to] = tempN;
		}

		if (moveWindow == 34453) {
			resetBank();
		}
		if (moveWindow == 18579) {
			int tempI;
			int tempN;
			tempI = c.playerItems[from];
			tempN = c.playerItemsN[from];

			c.playerItems[from] = c.playerItems[to];
			c.playerItemsN[from] = c.playerItemsN[to];
			c.playerItems[to] = tempI;
			c.playerItemsN[to] = tempN;
			// //updateInventory = true;
			resetItems(3214);
		}
		resetTempItems();
		if (moveWindow == 3724) {
			// //updateInventory = true;
			resetItems(3214);
		}

	}

	/**
	 * delete Item
	 **/

	public void deleteEquipment(int i, int j) {
		if (c != null) {
			if (PlayerHandler.players[c.playerId] == null) {
				return;
			}
			if (i < 0) {
				return;
			}

			c.playerEquipment[j] = -1;
			c.playerEquipmentN[j] = c.playerEquipmentN[j] - 1;
			c.getOutStream().createFrame(34);
			c.getOutStream().writeWord(6);
			c.getOutStream().writeWord(1688);
			c.getOutStream().writeByte(j);
			c.getOutStream().writeWord(0);
			c.getOutStream().writeByte(0);
			getBonus();
			if (j == Player.playerWeapon) {
				sendWeapon(-1, "Unarmed");
			}
			resetBonus();
			getBonus();
			writeBonus();
			c.updateRequired = true;
			c.setAppearanceUpdateRequired(true);
		}
	}

	public void deleteItem(int id, int amount) {
		deleteItem(id, getItemSlot(id), amount);
	}

	public void deleteItem(int id, int slot, int amount) {
		if (id <= 0 || slot < 0) {
			return;
		}
		if (c.playerItems[slot] == (id + 1)) {
			if (c.playerItemsN[slot] > amount) {
				c.playerItemsN[slot] -= amount;
			} else {
				c.playerItemsN[slot] = 0;
				c.playerItems[slot] = 0;
			}
			resetItems(3214);
		}
	}

	public void deleteItem2(int id, int amount) {
		int am = amount;
		for (int i = 0; i < c.playerItems.length; i++) {
			if (am == 0) {
				break;
			}
			if (c.playerItems[i] == (id + 1)) {
				if (c.playerItemsN[i] > amount) {
					c.playerItemsN[i] -= amount;
					break;
				} else {
					c.playerItems[i] = 0;
					c.playerItemsN[i] = 0;
					am--;
				}
			}
		}
		resetItems(3214);
	}

	/**
	 * Delete Arrows
	 **/
	public void deleteArrow() {
		if (c != null) {
			if (c.playerEquipment[Player.playerCape] == 10499
					&& Misc.random(5) != 1
					&& c.playerEquipment[Player.playerArrows] != 4740)
				return;
			if (c.playerEquipmentN[Player.playerArrows] == 1) {
				c.getItems().deleteEquipment(
						c.playerEquipment[Player.playerArrows],
						Player.playerArrows);
			}
			if (c.playerEquipmentN[Player.playerArrows] != 0) {
				c.getOutStream().createFrameVarSizeWord(34);
				c.getOutStream().writeWord(1688);
				c.getOutStream().writeByte(Player.playerArrows);
				c.getOutStream().writeWord(
						c.playerEquipment[Player.playerArrows] + 1);
				if (c.playerEquipmentN[Player.playerArrows] - 1 > 254) {
					c.getOutStream().writeByte(255);
					c.getOutStream().writeDWord(
							c.playerEquipmentN[Player.playerArrows] - 1);
				} else {
					c.getOutStream().writeByte(
							c.playerEquipmentN[Player.playerArrows] - 1);
				}
				c.getOutStream().endFrameVarSizeWord();
				c.flushOutStream();
				c.playerEquipmentN[Player.playerArrows] -= 1;
			}
			c.updateRequired = true;
			c.setAppearanceUpdateRequired(true);
		}
	}

	public void deleteEquipment() {
		if (c != null) {
			if (c.playerEquipmentN[Player.playerWeapon] == 1) {
				c.getItems().deleteEquipment(
						c.playerEquipment[Player.playerWeapon],
						Player.playerWeapon);
			}
			if (c.playerEquipmentN[Player.playerWeapon] != 0) {
				c.getOutStream().createFrameVarSizeWord(34);
				c.getOutStream().writeWord(1688);
				c.getOutStream().writeByte(Player.playerWeapon);
				c.getOutStream().writeWord(
						c.playerEquipment[Player.playerWeapon] + 1);
				if (c.playerEquipmentN[Player.playerWeapon] - 1 > 254) {
					c.getOutStream().writeByte(255);
					c.getOutStream().writeDWord(
							c.playerEquipmentN[Player.playerWeapon] - 1);
				} else {
					c.getOutStream().writeByte(
							c.playerEquipmentN[Player.playerWeapon] - 1);
				}
				c.getOutStream().endFrameVarSizeWord();
				c.flushOutStream();
				c.playerEquipmentN[Player.playerWeapon] -= 1;
			}
			c.updateRequired = true;
			c.setAppearanceUpdateRequired(true);
		}
	}

	/**
	 * Dropping Arrows
	 **/

	public void dropArrowNpc() {
		if (c.playerEquipment[Player.playerCape] == 10499)
			return;
		if (c.playerEquipment[Player.playerArrows] == 15243)
			return;
		int enemyX = NPCHandler.npcs[c.oldNpcIndex].getX();
		int enemyY = NPCHandler.npcs[c.oldNpcIndex].getY();
		if (Misc.random(10) >= 4) {
			if (Server.itemHandler.itemAmount(c.rangeItemUsed, enemyX, enemyY) == 0) {
				Server.itemHandler.createGroundItem(c, c.rangeItemUsed, enemyX,
						enemyY, 1, c.getId());
			} else if (Server.itemHandler.itemAmount(c.rangeItemUsed, enemyX,
					enemyY) != 0) {
				int amount = Server.itemHandler.itemAmount(c.rangeItemUsed,
						enemyX, enemyY);
				Server.itemHandler.removeGroundItem(c, c.rangeItemUsed, enemyX,
						enemyY, false);
				Server.itemHandler.createGroundItem(c, c.rangeItemUsed, enemyX,
						enemyY, amount + 1, c.getId());
			}
		}
	}

	public void dropArrowPlayer() {
		int enemyX = PlayerHandler.players[c.oldPlayerIndex].getX();
		int enemyY = PlayerHandler.players[c.oldPlayerIndex].getY();
		if (c.playerEquipment[Player.playerCape] == 10499)
			return;
		if (c.playerEquipment[Player.playerArrows] == 15243)
			return;
		if (Misc.random(10) >= 4) {
			if (Server.itemHandler.itemAmount(c.rangeItemUsed, enemyX, enemyY) == 0) {
				Server.itemHandler.createGroundItem(c, c.rangeItemUsed, enemyX,
						enemyY, 1, c.getId());
			} else if (Server.itemHandler.itemAmount(c.rangeItemUsed, enemyX,
					enemyY) != 0) {
				int amount = Server.itemHandler.itemAmount(c.rangeItemUsed,
						enemyX, enemyY);
				Server.itemHandler.removeGroundItem(c, c.rangeItemUsed, enemyX,
						enemyY, false);
				Server.itemHandler.createGroundItem(c, c.rangeItemUsed, enemyX,
						enemyY, amount + 1, c.getId());
			}
		}
	}

	public void removeAllItems() {
		for (int i = 0; i < c.playerItems.length; i++) {
			c.playerItems[i] = 0;
		}
		for (int i = 0; i < c.playerItemsN.length; i++) {
			c.playerItemsN[i] = 0;
		}
		resetItems(3214);
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

	public int findItem(int id, int[] items, int[] amounts) {
		for (int i = 0; i < c.playerItems.length; i++) {
			if (((items[i] - 1) == id) && (amounts[i] > 0)) {
				return i;
			}
		}
		return -1;
	}

	public String getItemName(int ItemID) {
		for (int i = 0; i < Config.ITEM_LIMIT; i++) {
			if (Server.itemHandler.ItemList[i] != null) {
				if (Server.itemHandler.ItemList[i].itemId == ItemID) {
					return Server.itemHandler.ItemList[i].itemName;
				}
			}
		}
		return "Unarmed";
	}

	public int getItemId(String itemName) {
		for (int i = 0; i < Config.ITEM_LIMIT; i++) {
			if (Server.itemHandler.ItemList[i] != null) {
				if (Server.itemHandler.ItemList[i].itemName
						.equalsIgnoreCase(itemName)) {
					return Server.itemHandler.ItemList[i].itemId;
				}
			}
		}
		return -1;
	}

	public int getItemSlot(int ItemID) {
		for (int i = 0; i < c.playerItems.length; i++) {
			if ((c.playerItems[i] - 1) == ItemID) {
				return i;
			}
		}
		return -1;
	}

	public int getItemAmount(int ItemID) {
		int itemCount = 0;
		for (int i = 0; i < c.playerItems.length; i++) {
			if ((c.playerItems[i] - 1) == ItemID) {
				itemCount += c.playerItemsN[i];
			}
		}
		return itemCount;
	}

	public boolean playerHasItem(int itemID, int amt, int slot) {
		itemID++;
		int found = 0;
		if (c.playerItems[slot] == (itemID)) {
			for (int i = 0; i < c.playerItems.length; i++) {
				if (c.playerItems[i] == itemID) {
					if (c.playerItemsN[i] >= amt) {
						return true;
					} else {
						found++;
					}
				}
			}
			if (found >= amt) {
				return true;
			}
			return false;
		}
		return false;
	}

	public boolean playerHasItem(int itemID) {
		itemID++;
		for (int i = 0; i < c.playerItems.length; i++) {
			if (c.playerItems[i] == itemID)
				return true;
		}
		return false;
	}

	public boolean playerHasItem(int itemID, int amt) {
		itemID++;
		int found = 0;
		for (int i = 0; i < c.playerItems.length; i++) {
			if (c.playerItems[i] == itemID) {
				if (c.playerItemsN[i] >= amt) {
					return true;
				} else {
					found++;
				}
			}
		}
		if (found >= amt) {
			return true;
		}
		return false;
	}

	public int getUnnotedItem(int ItemID) {
		int NewID = ItemID - 1;
		String NotedName = "";
		for (int i = 0; i < Config.ITEM_LIMIT; i++) {
			if (Server.itemHandler.ItemList[i] != null) {
				if (Server.itemHandler.ItemList[i].itemId == ItemID) {
					NotedName = Server.itemHandler.ItemList[i].itemName;
				}
			}
		}
		for (int i = 0; i < Config.ITEM_LIMIT; i++) {
			if (Server.itemHandler.ItemList[i] != null) {
				if (Server.itemHandler.ItemList[i].itemName == NotedName) {
					if (Server.itemHandler.ItemList[i].itemDescription
							.startsWith("Swap this note at any bank for a") == false) {
						NewID = Server.itemHandler.ItemList[i].itemId;
						break;
					}
				}
			}
		}
		return NewID;
	}

	/**
	 * Drop Item
	 **/

	public void createGroundItem(int itemID, int itemX, int itemY,
			int itemAmount) {
		if (c != null) {
			c.getOutStream().createFrame(85);
			c.getOutStream().writeByteC((itemY - 8 * c.mapRegionY));
			c.getOutStream().writeByteC((itemX - 8 * c.mapRegionX));
			c.getOutStream().createFrame(44);
			c.getOutStream().writeWordBigEndianA(itemID);
			c.getOutStream().writeWord(itemAmount);
			c.getOutStream().writeByte(0);
			c.flushOutStream();
		}
	}

	/**
	 * Pickup Item
	 **/

	public void removeGroundItem(int itemID, int itemX, int itemY, int Amount) {
		if (c != null) {
			c.getOutStream().createFrame(85);
			c.getOutStream().writeByteC((itemY - 8 * c.mapRegionY));
			c.getOutStream().writeByteC((itemX - 8 * c.mapRegionX));
			c.getOutStream().createFrame(156);
			c.getOutStream().writeByteS(0);
			c.getOutStream().writeWord(itemID);
			c.flushOutStream();
		}
	}

	public boolean ownsCape() {
		if (c.getItems().playerHasItem(2412, 1)
				|| c.getItems().playerHasItem(2413, 1)
				|| c.getItems().playerHasItem(2414, 1))
			return true;
		for (int j = 0; j < Config.BANK_SIZE; j++) {
			if (c.bankItems[j] == 2412 || c.bankItems[j] == 2413
					|| c.bankItems[j] == 2414)
				return true;
		}
		if (c.playerEquipment[Player.playerCape] == 2413
				|| c.playerEquipment[Player.playerCape] == 2414
				|| c.playerEquipment[Player.playerCape] == 2415)
			return true;
		return false;
	}

	public boolean hasAllShards() {
		return playerHasItem(11712, 1) && playerHasItem(11712, 1)
				&& playerHasItem(11714, 1);
	}

	public void makeBlade() {
		deleteItem(11710, 1);
		deleteItem(11712, 1);
		deleteItem(11714, 1);
		addItem(11690, 1);
		c.sendMessage("You combine the shards to make a blade.");
	}

	public void makeGodsword(int i) {
		if (playerHasItem(11690) && playerHasItem(i)) {
			deleteItem(11690, 1);
			deleteItem(i, 1);
			addItem(i - 8, 1);
			c.sendMessage("You combine the hilt and the blade to make a godsword.");
		}
	}
	
	public void makeBringImbued() {
	if (playerHasItem(6737) && playerHasItem(607)) {
		deleteItem(6737, 1);
		deleteItem(607, 1);
		addItem(15220, 1);
		c.sendMessage("You have successfully imbued your Berserker Ring.");
		}
	}
	
	public void makeArchersImbued() {
		if (playerHasItem(6733) && playerHasItem(607)) {
			deleteItem(6733, 1);
			deleteItem(607, 1);
			addItem(15019, 1);
			c.sendMessage("You have successfully imbued your Archers Ring.");
		}
	}
	
	public void makeSeersImbued() {
		if (playerHasItem(6731) && playerHasItem(607)) {
			deleteItem(6731, 1);
			deleteItem(607, 1);
			addItem(15018, 1);
			c.sendMessage("You have successfully imbued your Seers Ring.");
		}
	}
	
	public void makeWarriorImbued() {
		if (playerHasItem(6735, 1) && playerHasItem(607)) {
			deleteItem(6735, 1);
			deleteItem(607, 1);
			addItem(15020, 1);
			c.sendMessage("You have successfully imbued your Warrior Ring.");
		}
	}
	
	public void makeFury(){
		if (c.playerLevel[c.playerCrafting] >=70) {
		if (playerHasItem(1755) && playerHasItem(6573)) {
			deleteItem(6573, 1);
			addItem(6585, 1);
			c.sendMessage("You have successfully made an amulet of fury.");
		}
		} else 
			c.sendMessage("You don't have 70 crafting and can't made the amulet.");
	}
	
	public void makeDFS() {
		if (c.playerLevel[c.playerSmithing] >= 95) {
		if (playerHasItem(11286) && playerHasItem(1540)) {
			deleteItem(11286, 1);
			deleteItem(1540, 1);
			addItem(11284, 1);
			c.sendMessage("You have successfully made a dragonfire shild.");
		}
		} else 
			c.sendMessage("You don't have the required smithing level.");
	}

	public boolean isHilt(int i) {
		return i >= 11702 && i <= 11708 && i % 2 == 0;
	}

}