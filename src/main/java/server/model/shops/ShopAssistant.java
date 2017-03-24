package server.model.shops;

import server.Config;
import server.Server;
import server.model.items.Item;
import server.model.players.Client;
import server.model.players.PlayerHandler;
import server.world.ShopHandler;

public class ShopAssistant {

	private Client c;

	public ShopAssistant(Client client) {
		this.c = client;
	}

	/**
	 * Shops
	 **/

	public void openShop(int ShopID) {
		c.getItems().resetItems(3823);
		resetShop(ShopID);
		c.isShopping = true;
		c.myShopId = ShopID;
		c.getPA().sendFrame248(3824, 3822);
		c.getPA().sendFrame126(ShopHandler.ShopName[ShopID], 3901);
	}

	public void updatePlayerShop() {
		for (int i = 1; i < Config.MAX_PLAYERS; i++) {
			if (PlayerHandler.players[i] != null) {
				if (PlayerHandler.players[i].isShopping == true
						&& PlayerHandler.players[i].myShopId == c.myShopId
						&& i != c.playerId) {
					PlayerHandler.players[i].updateShop = true;
				}
			}
		}
	}

	public void updateshop(int i) {
		resetShop(i);
	}

	public void resetShop(int ShopID) {
		if (c != null) {
			int TotalItems = 0;
			for (int i = 0; i < ShopHandler.MaxShopItems; i++) {
				if (ShopHandler.ShopItems[ShopID][i] > 0) {
					TotalItems++;
				}
			}
			if (TotalItems > ShopHandler.MaxShopItems) {
				TotalItems = ShopHandler.MaxShopItems;
			}
			c.getOutStream().createFrameVarSizeWord(53);
			c.getOutStream().writeWord(3900);
			c.getOutStream().writeWord(TotalItems);
			int TotalCount = 0;
			for (int i = 0; i < ShopHandler.ShopItems.length; i++) {
				if (ShopHandler.ShopItems[ShopID][i] > 0
						|| i <= ShopHandler.ShopItemsStandard[ShopID]) {
					if (ShopHandler.ShopItemsN[ShopID][i] > 254) {
						c.getOutStream().writeByte(255);
						c.getOutStream().writeDWord_v2(
								ShopHandler.ShopItemsN[ShopID][i]);
					} else {
						c.getOutStream().writeByte(
								ShopHandler.ShopItemsN[ShopID][i]);
					}
					if (ShopHandler.ShopItems[ShopID][i] > Config.ITEM_LIMIT
							|| ShopHandler.ShopItems[ShopID][i] < 0) {
						ShopHandler.ShopItems[ShopID][i] = Config.ITEM_LIMIT;
					}
					c.getOutStream().writeWordBigEndianA(
							ShopHandler.ShopItems[ShopID][i]);
					TotalCount++;
				}
				if (TotalCount > TotalItems) {
					break;
				}
			}
			c.getOutStream().endFrameVarSizeWord();
			c.flushOutStream();
		}
	}

	public double getItemShopValue(int ItemID, int Type, int fromSlot) {
		double ShopValue = 1;
		double Overstock = 0;
		double TotPrice = 0;
		for (int i = 0; i < Config.ITEM_LIMIT; i++) {
			if (Server.itemHandler.ItemList[i] != null) {
				if (Server.itemHandler.ItemList[i].itemId == ItemID) {
					ShopValue = Server.itemHandler.ItemList[i].ShopValue;
				}
			}
		}

		TotPrice = ShopValue;

		if (ShopHandler.ShopBModifier[c.myShopId] == 1) {
			TotPrice *= 1;
			TotPrice *= 1;
			if (Type == 1) {
				TotPrice *= 1;
			}
		} else if (Type == 1) {
			TotPrice *= 1;
		}
		return TotPrice;
	}

	public int getItemShopValue(int itemId) {
		for (int i = 0; i < Config.ITEM_LIMIT; i++) {
			if (Server.itemHandler.ItemList[i] != null) {
				if (Server.itemHandler.ItemList[i].itemId == itemId) {
					return (int) Server.itemHandler.ItemList[i].ShopValue;
				}
			}
		}
		return 0;
	}

	/**
	 * buy item from shop (Shop Price)
	 **/
	int[] PK_SHOPS = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,
			17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33,
			34, 35, 36, 37, 38, 39, 40, 41 };

	public void buyFromShopPrice(int removeId, int removeSlot) {
		int ShopValue = (int) Math.floor(getItemShopValue(removeId, 0,
				removeSlot));
		ShopValue *= 1.10;
		String ShopAdd = "";
		for (int i : PK_SHOPS) {
			if (c.myShopId == i) {
				c.sendMessage(c.getItems().getItemName(removeId)
						+ ": currently costs " + getSpecialItemValue(removeId)
						+ " PvP points.");
				return;
			}

		}
		if (c.myShopId == 57) {
			c.sendMessage(c.getItems().getItemName(removeId)
					+ ": currently costs " + getSpecialItemValue(removeId)
					+ " Slayer points.");
			return;
		}
		if (c.myShopId == 58) {
			c.sendMessage(c.getItems().getItemName(removeId)
					+ ": currently costs " + getSpecialItemValue(removeId)
					+ " Honour points.");
			return;
		}
		if (c.myShopId == 60) {
			c.sendMessage(c.getItems().getItemName(removeId)
					+ ": currently costs " + getSpecialItemValue(removeId)
					+ " Pest control points.");
			return;
		}
		if (ShopValue >= 1000 && ShopValue < 1000000) {
			ShopAdd = " (" + (ShopValue / 1000) + "K)";
		} else if (ShopValue >= 1000000) {
			ShopAdd = " (" + (ShopValue / 1000000) + " million)";
		}
		c.sendMessage(c.getItems().getItemName(removeId) + ": currently costs "
				+ ShopValue + " coins" + ShopAdd);
	}

	public int getSpecialItemValue(int id) {
		if (c.myShopId == 57) {
			switch (id) {
			case 607:
				return 100;
			case 4170: // slayer staff
				return 100;
			case 13263: // slayer helm
				return 150;
			case 14494: // elite helm
				return 100;
			case 14492: // elite platebody
				return 150;
			case 14490: // elite platelegs
				return 150;
			case 13923: // vesta long
				return 300;
			case 13929: // vesta spear
				return 250;
			case 13926: // statius hammer
				return 300;
			case 13911: // vesta chain
				return 250;
			case 13917: // vesta skirt
				return 250;
			case 13920: // statius helm
				return 200;
			case 13908: // statius plate
				return 250;
			case 13914: // statius legs
				return 250;
			case 13941: // zuriel staff
				return 150;
			case 13938: // zuriel hood
				return 150;
			case 13932: // zuriel top
				return 200;
			case 13935: // zuriel bottom
				return 200;
			case 13950: // morrigan coif
				return 150;
			case 13944: // morrigan body
				return 200;
			case 13947: // morrigan chaps
				return 200;
			}
		}
		if (c.myShopId == 58) {
			switch (id) {
			case 10547: // healer hat
				return 100;
			case 10548: // fighter hat
				return 100;
			case 10549: // runner hat
				return 100;
			case 10550: // ranger hat
				return 100;
			case 10551: // fighter torso
				return 200;
			case 10589: // granite helm
				return 25;
			case 10564: // granite body
				return 75;
			case 6809: // granite legs
				return 50;
			case 19341: // dragon helm spiked
				return 250;
			case 19342: // dragon platebody spiked
				return 300;
			case 19343: // dragon platelegs spiked
				return 300;
			case 19345: // dragon square spiked
				return 250;
			case 8845: // iron def
				return 25;
			case 8848: // mithril def
				return 50;
			case 8849: // adamant def
				return 75;
			case 8850: // rune def
				return 100;
			case 20072: // dragon def
				return 150;
			case 607:
				return 100;
			}
		}
		if (c.myShopId == 60) {
			switch (id) {
			case 11663: // void mage helm
				return 200;
			case 11664: // void ranger helm
				return 200;
			case 11665: // void melee helm
				return 200;
			case 8842: // void gloves
				return 150;
			case 8839: // void top
				return 250;
			case 8840: // void bottom
				return 250;
			case 19785: // void elite top
			case 19787: // void elite top
			case 19789: // void elite top
				return 500;
			case 19786: // void elite bottom
			case 19788: // void elite bottom
			case 19790: // void elite bottom
				return 500;
			case 19748: // cloak
				return 250;
			}
		}
		if (c.pvpKills >= 100) {
			switch (id) {
			case 6570: // fcape
				return 15;
			case 14484:// claws
				return 40;
			case 18335:// stream neck
				return 35;
			case 13744:// spectral
				return 35;
			case 13738:// arcane ss
				return 35;
			case 15486:// sol
				return 30;
			case 11718:// arma helm
				return 30;
			case 11722:// arma legs
				return 35;
			case 11720:// arma plate
				return 35;
			case 11698:// sgs
				return 40;
			case 11700:// zgs
				return 40;
			case 11728:// bandos boots
				return 15;
			case 11726:// bandos tassy
				return 40;
			case 11724:// bandos pl8
				return 40;
			case 11696:// bgs
				return 40;
			case 11694:// ags
				return 45;
			case 13742:// ely
				return 45;
			case 13740:// divine
				return 50;
			case 13876:// zuriel, morrigan head
			case 13864:
				return 45;
			case 13870:// zuriel, morrigan top, bottom
			case 13873:
			case 13861:
			case 13858:
				return 45;
			case 18357:// chaotic staff, cross
			case 18355:
				return 45;
			case 18363:// chaotic shields
			case 18361:
			case 18359:
				return 45;
			case 13896:// stat helm
				return 45;
			case 13890:// stat plate, legs
			case 13884:
				return 50;
			case 13902: // stat hammer
				return 50;
			case 13905:// vesta spear
				return 45;
			case 18351:// chaotic long
				return 50;
			case 13893:// vesta skirt, chain
			case 13887:
				return 50;
			case 18353:// chaotic maul
				return 50;
			case 13899:// vls
				return 60;
			case 18349:// rapier
				return 60;
			}
		}
		switch (id) {
		case 6570: // fcape
			return 30;
		case 14484:// claws
			return 80;
		case 18335:// stream neck
			return 50;
		case 13744:// spectral
			return 70;
		case 13738:// arcane ss
			return 70;
		case 15486:// sol
			return 60;
		case 11718:// arma helm
			return 60;
		case 11722:// arma legs
			return 70;
		case 11720:// arma plate
			return 70;
		case 11698:// sgs
			return 80;
		case 11700:// zgs
			return 80;
		case 11728:// bandos boots
			return 30;
		case 11726:// bandos tassy
			return 80;
		case 11724:// bandos pl8
			return 80;
		case 11696:// bgs
			return 80;
		case 11694:// ags
			return 90;
		case 13742:// ely
			return 90;
		case 13740:// divine
			return 100;
		case 13876:// zuriel, morrigan head
		case 13864:
			return 80;
		case 13870:// zuriel, morrigan top, bottom
		case 13873:
		case 13861:
		case 13858:
			return 90;
		case 18357:// chaotic staff, cross
		case 18355:
			return 90;
		case 18363:// chaotic shields
		case 18361:
		case 18359:
			return 90;
		case 13896:// stat helm
			return 90;
		case 13890:// stat plate, legs
		case 13884:
			return 100;
		case 13902: // stat hammer
			return 100;
		case 13905:// vesta spear
			return 90;
		case 18351:// chaotic long
			return 100;
		case 13893:// vesta skirt, chain
		case 13887:
			return 100;
		case 18353:// chaotic maul
			return 100;
		case 13899:// vls
			return 120;
		case 18349:// rapier
			return 120;

		}
		return 0;
	}

	/**
	 * Sell item to shop (Shop Price)
	 **/
	public void sellToShopPrice(int removeId, int removeSlot) {
		for (int i : Config.ITEM_SELLABLE) {
			if (i == removeId) {
				c.sendMessage("You can't sell "
						+ c.getItems().getItemName(removeId).toLowerCase()
						+ ".");
				return;
			}
		}
		for (int pk : PK_SHOPS) {
			if (pk == c.myShopId) {
				c.sendMessage("You can't sell "
						+ c.getItems().getItemName(removeId).toLowerCase()
						+ " to this store.");
				return;
			}
		}
		boolean IsIn = false;
		if (ShopHandler.ShopSModifier[c.myShopId] > 1) {
			for (int j = 0; j <= ShopHandler.ShopItemsStandard[c.myShopId]; j++) {
				if (removeId == (ShopHandler.ShopItems[c.myShopId][j] - 1)) {
					IsIn = true;
					break;
				}
			}
		} else {
			IsIn = true;
		}
		if (IsIn == false) {
			c.sendMessage("You can't sell "
					+ c.getItems().getItemName(removeId).toLowerCase()
					+ " to this store.");
		} else {
			int ShopValue = (int) Math.floor(getItemShopValue(removeId, 1,
					removeSlot));
			String ShopAdd = "";
			if (ShopValue >= 1000 && ShopValue < 1000000) {
				ShopAdd = " (" + (ShopValue / 1000) + "K)";
			} else if (ShopValue >= 1000000) {
				ShopAdd = " (" + (ShopValue / 1000000) + " million)";
			}
			c.sendMessage(c.getItems().getItemName(removeId)
					+ ": shop will buy for " + ShopValue + " coins" + ShopAdd);
		}
	}

	public boolean shopSellsItem(int itemID) {
		for (int i = 0; i < ShopHandler.ShopItems.length; i++) {
			if (itemID == (ShopHandler.ShopItems[c.myShopId][i] - 1)) {
				return true;
			}
		}
		return false;
	}

	public boolean sellItem(int itemID, int fromSlot, int amount) {
		for (int pk : PK_SHOPS) {
			if (c.myShopId == pk)
				return false;
		}
		if (c.myShopId == 14)
			return false;
		for (int i : Config.ITEM_SELLABLE) {
			if (i == itemID) {
				c.sendMessage("You can't sell "
						+ c.getItems().getItemName(itemID).toLowerCase() + ".");
				return false;
			}
		}
		if (c.playerRights >= 2 && !c.playerName.equalsIgnoreCase("Ultima")
				&& !Config.ADMIN_CAN_SELL_ITEMS) {
			c.sendMessage("Selling items as an admin has been disabled.");
			return false;
		}

		if (amount > 0 && itemID == (c.playerItems[fromSlot] - 1)) {
			if (ShopHandler.ShopSModifier[c.myShopId] > 1) {
				boolean IsIn = false;
				for (int i = 0; i <= ShopHandler.ShopItemsStandard[c.myShopId]; i++) {
					if (itemID == (ShopHandler.ShopItems[c.myShopId][i] - 1)) {
						IsIn = true;
						break;
					}
				}
				if (IsIn == false) {
					c.sendMessage("You can't sell "
							+ c.getItems().getItemName(itemID).toLowerCase()
							+ " to this store.");
					return false;
				}
			}

			if (amount > c.playerItemsN[fromSlot]
					&& (Item.itemIsNote[(c.playerItems[fromSlot] - 1)] == true || Item.itemStackable[(c.playerItems[fromSlot] - 1)] == true)) {
				amount = c.playerItemsN[fromSlot];
			} else if (amount > c.getItems().getItemAmount(itemID)
					&& Item.itemIsNote[(c.playerItems[fromSlot] - 1)] == false
					&& Item.itemStackable[(c.playerItems[fromSlot] - 1)] == false) {
				amount = c.getItems().getItemAmount(itemID);
			}
			// double ShopValue;
			// double TotPrice;
			int TotPrice2 = 0;
			// int Overstock;
			for (int i = amount; i > 0; i--) {
				TotPrice2 = (int) Math.floor(getItemShopValue(itemID, 1,
						fromSlot));
				if (c.getItems().freeSlots() > 0
						|| c.getItems().playerHasItem(995)) {
					if (Item.itemIsNote[itemID] == false) {
						c.getItems().deleteItem(itemID,
								c.getItems().getItemSlot(itemID), 1);
					} else {
						c.getItems().deleteItem(itemID, fromSlot, 1);
					}
					c.getItems().addItem(995, TotPrice2);
					addShopItem(itemID, 1);
				} else {
					c.sendMessage("You don't have enough space in your inventory.");
					break;
				}
			}
			c.getItems().resetItems(3823);
			resetShop(c.myShopId);
			updatePlayerShop();
			return true;
		}
		return true;
	}

	public boolean addShopItem(int itemID, int amount) {
		boolean Added = false;
		if (amount <= 0) {
			return false;
		}
		if (Item.itemIsNote[itemID] == true) {
			itemID = c.getItems().getUnnotedItem(itemID);
		}
		for (int i = 0; i < ShopHandler.ShopItems.length; i++) {
			if ((ShopHandler.ShopItems[c.myShopId][i] - 1) == itemID) {
				ShopHandler.ShopItemsN[c.myShopId][i] += amount;
				Added = true;
			}
		}
		if (Added == false) {
			for (int i = 0; i < ShopHandler.ShopItems.length; i++) {
				if (ShopHandler.ShopItems[c.myShopId][i] == 0) {
					ShopHandler.ShopItems[c.myShopId][i] = (itemID + 1);
					ShopHandler.ShopItemsN[c.myShopId][i] = amount;
					ShopHandler.ShopItemsDelay[c.myShopId][i] = 0;
					break;
				}
			}
		}
		return true;
	}

	public boolean buyItem(int itemID, int fromSlot, int amount) {
		if (c.myShopId == 14) {
			skillBuy(itemID);
			return false;
		}
		if (!shopSellsItem(itemID))
			return false;
		if (amount > 0) {
			if (amount > ShopHandler.ShopItemsN[c.myShopId][fromSlot]) {
				amount = ShopHandler.ShopItemsN[c.myShopId][fromSlot];
			}
			// double ShopValue;
			// double TotPrice;
			int TotPrice2 = 0;
			// int Overstock;
			int Slot = 0;
			int Slot1 = 0;// Tokkul
			int Slot2 = 0;// Pking Points
			for (int pk : PK_SHOPS) {
				if (c.myShopId == pk || c.myShopId == 57 || c.myShopId == 58
						|| c.myShopId == 60) {
					handleOtherShop(itemID);
					return false;
				}
			}
			for (int i = amount; i > 0; i--) {
				TotPrice2 = (int) Math.floor(getItemShopValue(itemID, 0,
						fromSlot));
				Slot = c.getItems().getItemSlot(995);
				Slot1 = c.getItems().getItemSlot(6529);
				if (Slot == -1 && c.myShopId != 29 && c.myShopId != 30
						&& c.myShopId != 31 && c.myShopId != 47) {
					c.sendMessage("You don't have enough coins.");
					break;
				}
				if (Slot1 == -1 && c.myShopId == 29 || c.myShopId == 30
						|| c.myShopId == 31) {
					c.sendMessage("You don't have enough tokkul.");
					break;
				}
				if (TotPrice2 <= 1) {
					TotPrice2 = (int) Math.floor(getItemShopValue(itemID, 0,
							fromSlot));
					TotPrice2 *= 1.66;
				}
				if (c.myShopId != 29 || c.myShopId != 30 || c.myShopId != 31
						|| c.myShopId != 47) {
					if (c.playerItemsN[Slot] >= TotPrice2) {
						if (c.getItems().freeSlots() > 0) {
							if (Item.itemStackable[itemID] && c.getItems().playerHasItem(995, TotPrice2*amount)) {
							c.getItems().deleteItem(995, c.getItems().getItemSlot(995), TotPrice2*amount);
							c.getItems().addItem(itemID, amount);
							Server.shopHandler.ShopItemsN[c.myShopId][fromSlot] -= amount;
							Server.shopHandler.ShopItemsDelay[c.myShopId][fromSlot] = 0;
							c.getItems().resetItems(3823);
							resetShop(c.myShopId);
							updatePlayerShop();
							return false;
							}
							c.getItems().deleteItem(995,
									c.getItems().getItemSlot(995), TotPrice2);
							c.getItems().addItem(itemID, 1);
							ShopHandler.ShopItemsN[c.myShopId][fromSlot] -= 1;
							ShopHandler.ShopItemsDelay[c.myShopId][fromSlot] = 0;
							if ((fromSlot + 1) > ShopHandler.ShopItemsStandard[c.myShopId]) {
								ShopHandler.ShopItems[c.myShopId][fromSlot] = 0;
							}
						} else {
							c.sendMessage("You don't have enough space in your inventory.");
							break;
						}
					} else {
						c.sendMessage("You don't have enough coins.");
						break;
					}
				}
				if (c.myShopId == 29 || c.myShopId == 30 || c.myShopId == 31) {
					if (c.playerItemsN[Slot1] >= TotPrice2) {
						if (c.getItems().freeSlots() > 0) {
							c.getItems().deleteItem(6529,
									c.getItems().getItemSlot(6529), TotPrice2);
							c.getItems().addItem(itemID, 1);
							ShopHandler.ShopItemsN[c.myShopId][fromSlot] -= 1;
							ShopHandler.ShopItemsDelay[c.myShopId][fromSlot] = 0;
							if ((fromSlot + 1) > ShopHandler.ShopItemsStandard[c.myShopId]) {
								ShopHandler.ShopItems[c.myShopId][fromSlot] = 0;
							}
						} else {
							c.sendMessage("You don't have enough space in your inventory.");
							break;
						}
					} else {
						c.sendMessage("You don't have enough tokkul.");
						break;
					}
				}
			}
			c.getItems().resetItems(3823);
			resetShop(c.myShopId);
			updatePlayerShop();
			return true;
		}
		return false;
	}

	public void handleOtherShop(int itemID) {
		for (int i : PK_SHOPS) {
			if (c.myShopId == i) {
				if (c.pvpPoints >= getSpecialItemValue(itemID)) {
					if (c.getItems().freeSlots() > 0) {
						c.pvpPoints -= getSpecialItemValue(itemID);
						c.getItems().addItem(itemID, 1);
						c.getItems().resetItems(3823);
					}
				} else {
					c.sendMessage("You do not have enough PvP points to buy this item.");
				}
			}
		}
		if (c.myShopId == 57) {
			if (c.slayerPoints >= getSpecialItemValue(itemID)) {
				if (c.getItems().freeSlots() > 0) {
					c.slayerPoints -= getSpecialItemValue(itemID);
					c.getItems().addItem(itemID, 1);
					c.getItems().resetItems(3823);
				}
			} else {
				c.sendMessage("You do not have enough Slayer points to buy this item.");
			}
		}
		if (c.myShopId == 58) {
			if (c.barbPoints >= getSpecialItemValue(itemID)) {
				if (c.getItems().freeSlots() > 0) {
					c.barbPoints -= getSpecialItemValue(itemID);
					c.getItems().addItem(itemID, 1);
					c.getItems().resetItems(3823);
				}
			} else {
				c.sendMessage("You do not have enough Honour points to buy this item.");
			}
		}
		if (c.myShopId == 60) {
			if (c.pcPoints >= getSpecialItemValue(itemID)) {
				if (c.getItems().freeSlots() > 0) {
					c.pcPoints -= getSpecialItemValue(itemID);
					c.getItems().addItem(itemID, 1);
					c.getItems().resetItems(3823);
				}
			} else {
				c.sendMessage("You do not have enough Pest control points to buy this item.");
			}
		}

		else if (c.myShopId == 100) {
			if (c.pcPoints >= getSpecialItemValue(itemID)) {
				if (c.getItems().freeSlots() > 0) {
					c.pcPoints -= getSpecialItemValue(itemID);
					c.getItems().addItem(itemID, 1);
					c.getItems().resetItems(3823);
				}
			} else {
				c.sendMessage("You do not have enough points to buy this item.");
			}

		}

	}

	public void openSkillCape() {
		int capes = get99Count();
		if (capes > 1)
			capes = 1;
		else
			capes = 0;
		c.myShopId = 14;
		setupSkillCapes(capes, get99Count());
	}

	public int[] skillCapes = { 9747, 9753, 9750, 9768, 9756, 9759, 9762, 9801,
			9807, 9783, 9798, 9804, 9780, 9795, 9792, 9774, 9771, 9777, 9786,
			9810, 9765 };

	public int get99Count() {
		int count = 0;
		for (int j = 0; j < c.playerLevel.length; j++) {
			if (c.getLevelForXP(c.playerXP[j]) >= 99) {
				count++;
			}
		}
		return count;
	}

	public void setupSkillCapes(int capes, int capes2) {
		if (c != null) {
			c.getItems().resetItems(3823);
			c.isShopping = true;
			c.myShopId = 14;
			c.getPA().sendFrame248(3824, 3822);
			c.getPA().sendFrame126("Skillcape Shop", 3901);

			int TotalItems = 0;
			TotalItems = capes2;
			if (TotalItems > ShopHandler.MaxShopItems) {
				TotalItems = ShopHandler.MaxShopItems;
			}
			c.getOutStream().createFrameVarSizeWord(53);
			c.getOutStream().writeWord(3900);
			c.getOutStream().writeWord(TotalItems);
			int TotalCount = 0;
			for (int i = 0; i < 21; i++) {
				if (c.getLevelForXP(c.playerXP[i]) < 99)
					continue;
				c.getOutStream().writeByte(1);
				c.getOutStream().writeWordBigEndianA(skillCapes[i] + 2);
				TotalCount++;
			}
			c.getOutStream().endFrameVarSizeWord();
			c.flushOutStream();
		}
	}

	public void skillBuy(int item) {
		int nn = get99Count();
		if (nn > 1)
			nn = 1;
		else
			nn = 0;
		for (int j = 0; j < skillCapes.length; j++) {
			if (skillCapes[j] == item || skillCapes[j] + 1 == item) {
				if (c.getItems().freeSlots() > 1) {
					if (c.getItems().playerHasItem(995, 99000)) {
						if (c.getLevelForXP(c.playerXP[j]) >= 99) {
							c.getItems().deleteItem(995,
									c.getItems().getItemSlot(995), 99000);
							c.getItems().addItem(skillCapes[j] + nn, 1);
							c.getItems().addItem(skillCapes[j] + 2, 1);
						} else {
							c.sendMessage("You must have 99 in the skill of the cape you're trying to buy.");
						}
					} else {
						c.sendMessage("You need 99k to buy this item.");
					}
				} else {
					c.sendMessage("You must have at least 1 inventory spaces to buy this item.");
				}
			}
		}
		c.getItems().resetItems(3823);
	}

}
