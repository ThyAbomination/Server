package server.model.items;

import server.Config;
import server.Server;
import server.model.players.Client;
import server.model.players.skills.Firemaking;
import server.model.players.skills.Herblore;
import server.model.players.skills.crafting.GemCuttingManager;
import server.util.Misc;

/**
 * 
 * @author Ryan / Lmctruck30
 * 
 */

public class UseItem {

	public static void ItemonObject(Client c, int objectID, int objectX,
			int objectY, int itemId) {
		if (!c.getItems().playerHasItem(itemId, 1))
			return;
		switch (objectID) {

		case 3830:
			if (itemId == 954) {
				Server.objectHandler.createAnObject(c, 3831, 3509, 9497);
				c.getItems().deleteItem(954, 1);
			}
			break;

		case 2783:
			c.getSmithingInt().showSmithInterface(itemId);
			break;

		case 8151:
		case 8389:
		case 8174:
			c.getFarming().checkItemOnObject(itemId);
			break;

		case 409:
			if (c.getPrayer().IsABone(itemId))
				c.getPrayer().bonesOnAltar(itemId);
			break;

		case 2728:
		case 2732:
		case 114:
		case 12269:
			c.getCooking().cookFish(itemId);
			break;
		default:
			if (c.playerRights == 3)
				Misc.println("Player At Object id: " + objectID
						+ " with Item id: " + itemId);
			break;
		}

	}

	public static void ItemonItem(Client c, int itemUsed, int useWith) {
		GemCuttingManager.startCutting(c, itemUsed, useWith);
		if (itemUsed == 590 || useWith == 590) {
			Firemaking.attemptFire(c, itemUsed, useWith, c.absX, c.absY, false);
		}
		Herblore.checkUnfinished(c, useWith, itemUsed);
		Herblore.checkPotion(c, useWith, itemUsed);
		if ((itemUsed == 1540 && useWith == 11286) || (itemUsed == 11286 && useWith == 1540)) {
			c.getItems().makeDFS();
		}
		if (itemUsed == 13734 && useWith == 13754 || itemUsed == 13754
				&& useWith == 13734) {
			if (c.playerLevel[c.playerPrayer] >= 85) {
				c.getItems().deleteItem(useWith,
						c.getItems().getItemSlot(useWith), 1);
				c.getItems().deleteItem(itemUsed,
						c.getItems().getItemSlot(itemUsed), 1);
				c.getItems().addItem(13736, 1);
			} else {
				c.sendMessage("You need a prayer level of 85 to make a blessed spiritshield.");
			}
		}
		//Making Fury
		if (itemUsed == 1755 && useWith == 6573) {
			c.getItems().makeFury();
		}
		if (itemUsed == 6573 && useWith == 1755) {
			c.getItems().makeFury();
		}
		//Making Imbued Rings
		//B-Ring(i)
		if (itemUsed == 607 && useWith == 6737) {
			c.getItems().makeBringImbued();
		}
		if (itemUsed == 6737 && useWith == 607) {
			c.getItems().makeBringImbued();
		}
		//Archer Ring (i)
		if (itemUsed == 607 && useWith == 6733) {
			c.getItems().makeArchersImbued();
		}
		if (itemUsed == 6733 && useWith == 607) {
			c.getItems().makeArchersImbued();
		}
		//Seers Ring (i)
		if (itemUsed == 607 && useWith == 6731) {
			c.getItems().makeSeersImbued();
		}
		if (itemUsed == 6731 && useWith == 607) {
			c.getItems().makeSeersImbued();
		}
		//Warriors Ring (i)
		if (itemUsed == 607 && useWith == 6735) {
			c.getItems().makeWarriorImbued();
		}
		if (itemUsed == 6735 && useWith == 607) {
			c.getItems().makeWarriorImbued();
		}
		//End Of Imbues
		if (itemUsed == 13736 && useWith == 13752 || itemUsed == 13752
				&& useWith == 13736) {
			if (c.playerLevel[c.playerPrayer] >= 90) {
				c.getItems().deleteItem(useWith,
						c.getItems().getItemSlot(useWith), 1);
				c.getItems().deleteItem(itemUsed,
						c.getItems().getItemSlot(itemUsed), 1);
				c.getItems().addItem(13744, 1);
			} else {
				c.sendMessage("You need a prayer level of 90 to make a spectral spiritshield.");
			}
		}
		if (itemUsed == 13736 && useWith == 13746 || itemUsed == 13746
				&& useWith == 13736) {
			if (c.playerLevel[c.playerPrayer] >= 90) {
				c.getItems().deleteItem(useWith,
						c.getItems().getItemSlot(useWith), 1);
				c.getItems().deleteItem(itemUsed,
						c.getItems().getItemSlot(itemUsed), 1);
				c.getItems().addItem(13738, 1);
			} else {
				c.sendMessage("You need a prayer level of 90 to make a arcane spiritshield.");
			}
		}
		if (itemUsed == 13736 && useWith == 13748 || itemUsed == 13748
				&& useWith == 13736) {
			if (c.playerLevel[c.playerPrayer] >= 90) {
				c.getItems().deleteItem(useWith,
						c.getItems().getItemSlot(useWith), 1);
				c.getItems().deleteItem(itemUsed,
						c.getItems().getItemSlot(itemUsed), 1);
				c.getItems().addItem(13740, 1);
			} else {
				c.sendMessage("You need a prayer level of 90 to make a divine spiritshield.");
			}
		}
		if (itemUsed == 13736 && useWith == 13750 || itemUsed == 13750
				&& useWith == 13736) {
			if (c.playerLevel[c.playerPrayer] >= 90) {
				c.getItems().deleteItem(useWith,
						c.getItems().getItemSlot(useWith), 1);
				c.getItems().deleteItem(itemUsed,
						c.getItems().getItemSlot(itemUsed), 1);
				c.getItems().addItem(13742, 1);
			} else {
				c.sendMessage("You need a prayer level of 90 to make a elysian spiritshield.");
			}
		}
		if (itemUsed == 6686 && useWith == 6685) {
			return;
		}
		if (itemUsed == 6686 && useWith == 6689) {
			return;
		}
		if (itemUsed == 6686 && useWith == 6687) {
			return;
		}
		if (itemUsed == 6686 && useWith == 6691) {
			return;
		}
		if (itemUsed == 2434 && useWith == 2435) {
			return;
		}
		if (itemUsed == 2434 && useWith == 139) {
			return;
		}
		if (itemUsed == 2434 && useWith == 141) {
			return;
		}
		if (itemUsed == 2434 && useWith == 143) {
			return;
		}
		if (itemUsed == 2441 && useWith == 2440) {
			return;
		}
		if (itemUsed == 2441 && useWith == 157) {
			return;
		}
		if (itemUsed == 2441 && useWith == 159) {
			return;
		}
		if (itemUsed == 2441 && useWith == 161) {
			return;
		}
		if (itemUsed == 2437 && useWith == 2436) {
			return;
		}
		if (itemUsed == 2437 && useWith == 145) {
			return;
		}
		if (itemUsed == 2437 && useWith == 147) {
			return;
		}
		if (itemUsed == 2437 && useWith == 149) {
			return;
		}
		if (itemUsed == 2443 && useWith == 2442) {
			return;
		}
		if (itemUsed == 2443 && useWith == 163) {
			return;
		}
		if (itemUsed == 2443 && useWith == 165) {
			return;
		}
		if (itemUsed == 2443 && useWith == 167) {
			return;
		}
		if (itemUsed == 2445 && useWith == 2444) {
			return;
		}
		if (itemUsed == 2445 && useWith == 169) {
			return;
		}
		if (itemUsed == 2445 && useWith == 171) {
			return;
		}
		if (itemUsed == 2445 && useWith == 173) {
			return;
		}
		if (itemUsed == 3025 && useWith == 3024) {
			return;
		}
		if (itemUsed == 3025 && useWith == 3026) {
			return;
		}
		if (itemUsed == 3025 && useWith == 3028) {
			return;
		}
		if (itemUsed == 3025 && useWith == 3030) {
			return;
		}
		if (itemUsed == 3041 && useWith == 3040) {
			return;
		}
		if (itemUsed == 3041 && useWith == 3042) {
			return;
		}
		if (itemUsed == 3041 && useWith == 3044) {
			return;
		}
		if (itemUsed == 3041 && useWith == 3046) {
			return;
		}
		if (itemUsed == 6686 && useWith == 6685 || itemUsed == 6685
				&& useWith == 6686) {
			return;
		}
		if (itemUsed == 6686 && useWith == 6689 || itemUsed == 6689
				&& useWith == 6686) {
			return;
		}
		if (itemUsed == 6686 && useWith == 6687 || itemUsed == 6687
				&& useWith == 6686) {
			return;
		}
		if (itemUsed == 6686 && useWith == 6691 || itemUsed == 6691
				&& useWith == 6686) {
			return;
		}
		if (itemUsed == 6688 && useWith == 6686 || itemUsed == 6686
				&& useWith == 6688) {
			return;
		}
		if (itemUsed == 6688 && useWith == 6687 || itemUsed == 6687
				&& useWith == 6688) {
			return;
		}
		if (itemUsed == 6690 && useWith == 6688 || itemUsed == 6688
				&& useWith == 6690) {
			return;
		}
		if (itemUsed == 6692 && useWith == 6688 || itemUsed == 6688
				&& useWith == 6692) {
			return;
		}
		if (itemUsed == 6692 && useWith == 6685 || itemUsed == 6685
				&& useWith == 6692) {
			return;
		}
		if (itemUsed == 6692 && useWith == 6690 || itemUsed == 6690
				&& useWith == 6692) {
			return;
		}
		if (itemUsed == 6692 && useWith == 6689 || itemUsed == 6689
				&& useWith == 6692) {
			return;
		}
		if (itemUsed == 6692 && useWith == 6691 || itemUsed == 6691
				&& useWith == 6692) {
			return;
		}
		if (itemUsed == 6692 && useWith == 6687 || itemUsed == 6687
				&& useWith == 6692) {
			return;
		}
		if (itemUsed == 6688 && useWith == 6689 || itemUsed == 6689
				&& useWith == 6688) {
			return;
		}
		if (itemUsed == 6688 && useWith == 6685 || itemUsed == 6685
				&& useWith == 6688) {
			return;
		}
		if (itemUsed == 6688 && useWith == 6691 || itemUsed == 6691
				&& useWith == 6688) {
			return;
		}
		if (itemUsed == 6686 && useWith == 6692 || itemUsed == 6692
				&& useWith == 6686) {
			return;
		}
		if (itemUsed == 6690 && useWith == 6685 || itemUsed == 6685
				&& useWith == 6690) {
			return;
		}
		if (itemUsed == 6690 && useWith == 6689 || itemUsed == 6689
				&& useWith == 6690) {
			return;
		}
		if (itemUsed == 6690 && useWith == 6691 || itemUsed == 6691
				&& useWith == 6690) {
			return;
		}
		if (itemUsed == 6690 && useWith == 6687 || itemUsed == 6687
				&& useWith == 6690) {
			return;
		}
		if (itemUsed == 2434 && useWith == 2435 || itemUsed == 2435
				&& useWith == 2434) {
			return;
		}
		if (itemUsed == 2435 && useWith == 139 || itemUsed == 139
				&& useWith == 2435) {
			return;
		}
		if (itemUsed == 2435 && useWith == 141 || itemUsed == 141
				&& useWith == 2435) {
			return;
		}
		if (itemUsed == 2435 && useWith == 143 || itemUsed == 143
				&& useWith == 2435) {
			return;
		}
		if (itemUsed == 2434 && useWith == 139 || itemUsed == 139
				&& useWith == 2434) {
			return;
		}
		if (itemUsed == 2434 && useWith == 141 || itemUsed == 141
				&& useWith == 2434) {
			return;
		}
		if (itemUsed == 2434 && useWith == 140 || itemUsed == 140
				&& useWith == 2434) {
			return;
		}
		if (itemUsed == 142 && useWith == 140 || itemUsed == 140
				&& useWith == 142) {
			return;
		}
		if (itemUsed == 141 && useWith == 140 || itemUsed == 140
				&& useWith == 141) {
			return;
		}
		if (itemUsed == 143 && useWith == 140 || itemUsed == 140
				&& useWith == 143) {
			return;
		}
		if (itemUsed == 142 && useWith == 2435 || itemUsed == 2435
				&& useWith == 142) {
			return;
		}
		if (itemUsed == 144 && useWith == 2435 || itemUsed == 2435
				&& useWith == 144) {
			return;
		}
		if (itemUsed == 144 && useWith == 140 || itemUsed == 140
				&& useWith == 144) {
			return;
		}
		if (itemUsed == 143 && useWith == 140 || itemUsed == 140
				&& useWith == 143) {
			return;
		}
		if (itemUsed == 139 && useWith == 140 || itemUsed == 140
				&& useWith == 139) {
			return;
		}
		if (itemUsed == 140 && useWith == 2435 || itemUsed == 2435
				&& useWith == 140) {
			return;
		}
		if (itemUsed == 144 && useWith == 143 || itemUsed == 143
				&& useWith == 144) {
			return;
		}
		if (itemUsed == 2434 && useWith == 143 || itemUsed == 143
				&& useWith == 2434) {
			return;
		}
		if (itemUsed == 2434 && useWith == 142 || itemUsed == 142
				&& useWith == 2434) {
			return;
		}
		if (itemUsed == 2434 && useWith == 144 || itemUsed == 144
				&& useWith == 2434) {
			return;
		}
		if (itemUsed == 139 && useWith == 142 || itemUsed == 142
				&& useWith == 139) {
			return;
		}
		if (itemUsed == 141 && useWith == 142 || itemUsed == 142
				&& useWith == 141) {
			return;
		}
		if (itemUsed == 142 && useWith == 143 || itemUsed == 143
				&& useWith == 142) {
			return;
		}
		if (itemUsed == 142 && useWith == 139 || itemUsed == 139
				&& useWith == 142) {
			return;
		}
		if (itemUsed == 142 && useWith == 143 || itemUsed == 143
				&& useWith == 142) {
			return;
		}
		if (itemUsed == 144 && useWith == 142 || itemUsed == 142
				&& useWith == 144) {
			return;
		}
		if (itemUsed == 142 && useWith == 141 || itemUsed == 141
				&& useWith == 142) {
			return;
		}
		if (itemUsed == 144 && useWith == 139 || itemUsed == 139
				&& useWith == 144) {
			return;
		}
		if (itemUsed == 144 && useWith == 141 || itemUsed == 141
				&& useWith == 144) {
			return;
		}
		if (itemUsed == 2441 && useWith == 2440 || itemUsed == 2440
				&& useWith == 2441) {
			return;
		}
		if (itemUsed == 2441 && useWith == 158 || itemUsed == 158
				&& useWith == 2441) {
			return;
		}
		if (itemUsed == 158 && useWith == 160 || itemUsed == 160
				&& useWith == 158) {
			return;
		}
		if (itemUsed == 158 && useWith == 162 || itemUsed == 162
				&& useWith == 158) {
			return;
		}
		if (itemUsed == 162 && useWith == 160 || itemUsed == 160
				&& useWith == 162) {

			return;
		}
		if (itemUsed == 2441 && useWith == 160 || itemUsed == 160
				&& useWith == 2441) {
			return;
		}
		if (itemUsed == 162 && useWith == 160 || itemUsed == 160
				&& useWith == 162) {
			return;
		}
		if (itemUsed == 162 && useWith == 161 || itemUsed == 161
				&& useWith == 162) {
			return;
		}
		if (itemUsed == 162 && useWith == 159 || itemUsed == 159
				&& useWith == 162) {
			return;
		}
		if (itemUsed == 2440 && useWith == 158 || itemUsed == 158
				&& useWith == 2440) {
			return;
		}
		if (itemUsed == 2440 && useWith == 160 || itemUsed == 160
				&& useWith == 2440) {
			return;
		}
		if (itemUsed == 2441 && useWith == 162 || itemUsed == 162
				&& useWith == 2441) {
			return;
		}
		if (itemUsed == 160 && useWith == 159 || itemUsed == 159
				&& useWith == 160) {
			return;
		}
		if (itemUsed == 160 && useWith == 157 || itemUsed == 157
				&& useWith == 160) {
			return;
		}
		if (itemUsed == 160 && useWith == 161 || itemUsed == 161
				&& useWith == 160) {
			return;
		}
		if (itemUsed == 2440 && useWith == 157 || itemUsed == 157
				&& useWith == 2440) {
			return;
		}
		if (itemUsed == 158 && useWith == 157 || itemUsed == 157
				&& useWith == 158) {
			return;
		}
		if (itemUsed == 158 && useWith == 159 || itemUsed == 159
				&& useWith == 158) {
			return;
		}
		if (itemUsed == 158 && useWith == 161 || itemUsed == 161
				&& useWith == 158) {
			return;
		}
		if (itemUsed == 2441 && useWith == 159 || itemUsed == 159
				&& useWith == 2441) {
			return;
		}
		if (itemUsed == 2441 && useWith == 157 || itemUsed == 157
				&& useWith == 2441) {
			return;
		}
		if (itemUsed == 2441 && useWith == 161 || itemUsed == 161
				&& useWith == 2441) {
			return;
		}
		if (itemUsed == 162 && useWith == 2440 || itemUsed == 2440
				&& useWith == 162) {
			return;
		}
		if (itemUsed == 162 && useWith == 157 || itemUsed == 157
				&& useWith == 162) {
			return;
		}
		if (itemUsed == 2436 && useWith == 150 || itemUsed == 150
				&& useWith == 2436) {
			return;
		}
		if (itemUsed == 2436 && useWith == 146 || itemUsed == 146
				&& useWith == 2436) {
			return;
		}
		if (itemUsed == 2436 && useWith == 148 || itemUsed == 148
				&& useWith == 2436) {
			return;
		}
		if (itemUsed == 150 && useWith == 145 || itemUsed == 145
				&& useWith == 150) {
			return;
		}
		if (itemUsed == 150 && useWith == 147 || itemUsed == 147
				&& useWith == 150) {
			return;
		}
		if (itemUsed == 150 && useWith == 149 || itemUsed == 149
				&& useWith == 150) {
			return;
		}
		if (itemUsed == 145 && useWith == 146 || itemUsed == 146
				&& useWith == 145) {
			return;
		}
		if (itemUsed == 145 && useWith == 148 || itemUsed == 148
				&& useWith == 145) {
			return;
		}
		if (itemUsed == 147 && useWith == 148 || itemUsed == 148
				&& useWith == 147) {
			return;
		}
		if (itemUsed == 149 && useWith == 148 || itemUsed == 148
				&& useWith == 149) {
			return;
		}
		if (itemUsed == 149 && useWith == 146 || itemUsed == 146
				&& useWith == 149) {
			return;
		}
		if (itemUsed == 147 && useWith == 146 || itemUsed == 146
				&& useWith == 147) {
			return;
		}
		if (itemUsed == 2437 && useWith == 146 || itemUsed == 146
				&& useWith == 2437) {
			return;
		}
		if (itemUsed == 2437 && useWith == 148 || itemUsed == 148
				&& useWith == 2437) {
			return;
		}
		if (itemUsed == 148 && useWith == 146 || itemUsed == 146
				&& useWith == 148) {
			return;
		}
		if (itemUsed == 146 && useWith == 150 || itemUsed == 150
				&& useWith == 146) {
			return;
		}
		if (itemUsed == 148 && useWith == 150 || itemUsed == 150
				&& useWith == 148) {
			return;
		}
		if (itemUsed == 2437 && useWith == 150 || itemUsed == 150
				&& useWith == 2437) {
			return;
		}
		if (itemUsed == 2437 && useWith == 2436 || itemUsed == 2436
				&& useWith == 2437) {
			return;
		}
		if (itemUsed == 2437 && useWith == 145 || itemUsed == 145
				&& useWith == 2437) {
			return;
		}
		if (itemUsed == 2437 && useWith == 147 || itemUsed == 147
				&& useWith == 2437) {
			return;
		}
		if (itemUsed == 2437 && useWith == 149 || itemUsed == 149
				&& useWith == 2437) {
			return;
		}
		if (itemUsed == 2442 && useWith == 164 || itemUsed == 164
				&& useWith == 2442) {
			return;
		}
		if (itemUsed == 2443 && useWith == 164 || itemUsed == 164
				&& useWith == 2443) {
			return;
		}
		if (itemUsed == 2443 && useWith == 166 || itemUsed == 166
				&& useWith == 2443) {
			return;
		}
		if (itemUsed == 2443 && useWith == 168 || itemUsed == 168
				&& useWith == 2443) {
			return;
		}
		if (itemUsed == 2443 && useWith == 2442 || itemUsed == 2442
				&& useWith == 2443) {
			return;
		}
		if (itemUsed == 2443 && useWith == 163 || itemUsed == 163
				&& useWith == 2443) {
			return;
		}
		if (itemUsed == 2443 && useWith == 165 || itemUsed == 165
				&& useWith == 2443) {
			return;
		}
		if (itemUsed == 2443 && useWith == 167 || itemUsed == 167
				&& useWith == 2443) {
			return;
		}
		if (itemUsed == 164 && useWith == 163 || itemUsed == 163
				&& useWith == 164) {
			return;
		}
		if (itemUsed == 164 && useWith == 165 || itemUsed == 165
				&& useWith == 164) {
			return;
		}
		if (itemUsed == 164 && useWith == 167 || itemUsed == 167
				&& useWith == 164) {
			return;
		}
		if (itemUsed == 2442 && useWith == 166 || itemUsed == 166
				&& useWith == 2442) {
			return;
		}
		if (itemUsed == 2442 && useWith == 168 || itemUsed == 168
				&& useWith == 2442) {
			return;
		}
		if (itemUsed == 165 && useWith == 166 || itemUsed == 166
				&& useWith == 165) {
			return;
		}
		if (itemUsed == 168 && useWith == 163 || itemUsed == 163
				&& useWith == 168) {
			return;
		}
		if (itemUsed == 168 && useWith == 165 || itemUsed == 165
				&& useWith == 168) {
			return;
		}
		if (itemUsed == 168 && useWith == 166 || itemUsed == 166
				&& useWith == 168) {
			return;
		}
		if (itemUsed == 164 && useWith == 166 || itemUsed == 166
				&& useWith == 164) {
			return;
		}
		if (itemUsed == 164 && useWith == 168 || itemUsed == 168
				&& useWith == 164) {
			return;
		}
		if (itemUsed == 166 && useWith == 168 || itemUsed == 168
				&& useWith == 166) {
			return;
		}
		if (itemUsed == 168 && useWith == 167 || itemUsed == 167
				&& useWith == 168) {
			return;
		}
		if (itemUsed == 163 && useWith == 166 || itemUsed == 166
				&& useWith == 163) {
			return;
		}
		if (itemUsed == 167 && useWith == 166 || itemUsed == 166
				&& useWith == 167) {
			return;
		}
		if (itemUsed == 2445 && useWith == 2444 || itemUsed == 2444
				&& useWith == 2445) {
			return;
		}
		if (itemUsed == 172 && useWith == 2444 || itemUsed == 2444
				&& useWith == 172) {
			return;
		}
		if (itemUsed == 174 && useWith == 2444 || itemUsed == 2444
				&& useWith == 174) {
			return;
		}
		if (itemUsed == 172 && useWith == 169 || itemUsed == 169
				&& useWith == 172) {
			return;
		}
		if (itemUsed == 174 && useWith == 169 || itemUsed == 169
				&& useWith == 174) {
			return;
		}
		if (itemUsed == 172 && useWith == 171 || itemUsed == 171
				&& useWith == 172) {
			return;
		}
		if (itemUsed == 174 && useWith == 171 || itemUsed == 171
				&& useWith == 174) {
			return;
		}
		if (itemUsed == 172 && useWith == 173 || itemUsed == 173
				&& useWith == 172) {
			return;
		}
		if (itemUsed == 174 && useWith == 173 || itemUsed == 173
				&& useWith == 174) {
			return;
		}
		if (itemUsed == 170 && useWith == 2444 || itemUsed == 2444
				&& useWith == 170) {
			return;
		}
		if (itemUsed == 2445 && useWith == 170 || itemUsed == 170
				&& useWith == 2445) {
			return;
		}
		if (itemUsed == 2445 && useWith == 172 || itemUsed == 172
				&& useWith == 2445) {
			return;
		}
		if (itemUsed == 170 && useWith == 172 || itemUsed == 172
				&& useWith == 170) {
			return;
		}
		if (itemUsed == 170 && useWith == 169 || itemUsed == 169
				&& useWith == 170) {
			return;
		}
		if (itemUsed == 170 && useWith == 171 || itemUsed == 171
				&& useWith == 170) {
			return;
		}
		if (itemUsed == 170 && useWith == 173 || itemUsed == 173
				&& useWith == 170) {
			return;
		}
		if (itemUsed == 174 && useWith == 172 || itemUsed == 172
				&& useWith == 174) {
			return;
		}
		if (itemUsed == 170 && useWith == 174 || itemUsed == 174
				&& useWith == 170) {
			return;
		}
		if (itemUsed == 2445 && useWith == 174 || itemUsed == 174
				&& useWith == 2445) {
			return;
		}
		if (itemUsed == 2445 && useWith == 169 || itemUsed == 169
				&& useWith == 2445) {
			return;
		}
		if (itemUsed == 2445 && useWith == 171 || itemUsed == 171
				&& useWith == 2445) {
			return;
		}
		if (itemUsed == 2445 && useWith == 173 || itemUsed == 173
				&& useWith == 2445) {
			return;
		}
		if (itemUsed == 3025 && useWith == 3024 || itemUsed == 3024
				&& useWith == 3025) {
			return;
		}
		if (itemUsed == 3027 && useWith == 3025 || itemUsed == 3025
				&& useWith == 3027) {
			return;
		}
		if (itemUsed == 3029 && useWith == 3025 || itemUsed == 3025
				&& useWith == 3029) {
			return;
		}
		if (itemUsed == 3031 && useWith == 3025 || itemUsed == 3025
				&& useWith == 3031) {
			return;
		}
		if (itemUsed == 3031 && useWith == 3027 || itemUsed == 3027
				&& useWith == 3031) {
			return;
		}
		if (itemUsed == 3031 && useWith == 3029 || itemUsed == 3029
				&& useWith == 3031) {
			return;
		}
		if (itemUsed == 3027 && useWith == 3029 || itemUsed == 3029
				&& useWith == 3027) {
			return;
		}
		if (itemUsed == 3027 && useWith == 3024 || itemUsed == 3024
				&& useWith == 3027) {
			return;
		}
		if (itemUsed == 3027 && useWith == 3026 || itemUsed == 3026
				&& useWith == 3027) {
			return;
		}
		if (itemUsed == 3027 && useWith == 3028 || itemUsed == 3028
				&& useWith == 3027) {
			return;
		}
		if (itemUsed == 3027 && useWith == 3030 || itemUsed == 3030
				&& useWith == 3027) {
			return;
		}
		if (itemUsed == 3029 && useWith == 3027 || itemUsed == 3027
				&& useWith == 3029) {
			return;
		}
		if (itemUsed == 3025 && useWith == 3026 || itemUsed == 3026
				&& useWith == 3025) {
			return;
		}
		if (itemUsed == 3025 && useWith == 3028 || itemUsed == 3028
				&& useWith == 3025) {
			return;
		}
		if (itemUsed == 3025 && useWith == 3030 || itemUsed == 3030
				&& useWith == 3025) {
			return;
		}
		if (itemUsed == 3029 && useWith == 3024 || itemUsed == 3024
				&& useWith == 3029) {
			return;
		}
		if (itemUsed == 3029 && useWith == 3026 || itemUsed == 3026
				&& useWith == 3029) {
			return;
		}
		if (itemUsed == 3029 && useWith == 3028 || itemUsed == 3028
				&& useWith == 3029) {
			return;
		}
		if (itemUsed == 3029 && useWith == 3030 || itemUsed == 3030
				&& useWith == 3029) {
			return;
		}
		if (itemUsed == 3031 && useWith == 3024 || itemUsed == 3024
				&& useWith == 3031) {
			return;
		}
		if (itemUsed == 3031 && useWith == 3026 || itemUsed == 3026
				&& useWith == 3031) {
			return;
		}
		if (itemUsed == 3031 && useWith == 3028 || itemUsed == 3028
				&& useWith == 3031) {
			return;
		}
		if (itemUsed == 3031 && useWith == 3030 || itemUsed == 3030
				&& useWith == 3031) {
			return;
		}
		if (itemUsed == 3041 && useWith == 3043 || itemUsed == 3043
				&& useWith == 3041) {
			return;
		}
		if (itemUsed == 3041 && useWith == 3045 || itemUsed == 3045
				&& useWith == 3041) {
			return;
		}
		if (itemUsed == 3041 && useWith == 3047 || itemUsed == 3047
				&& useWith == 3041) {
			return;
		}
		if (itemUsed == 3043 && useWith == 3040 || itemUsed == 3040
				&& useWith == 3043) {
			return;
		}
		if (itemUsed == 3043 && useWith == 3046 || itemUsed == 3046
				&& useWith == 3043) {
			return;
		}
		if (itemUsed == 3043 && useWith == 3044 || itemUsed == 3044
				&& useWith == 3043) {
			return;
		}
		if (itemUsed == 3043 && useWith == 3042 || itemUsed == 3042
				&& useWith == 3043) {
			return;
		}
		if (itemUsed == 3043 && useWith == 3045 || itemUsed == 3045
				&& useWith == 3043) {
			return;
		}
		if (itemUsed == 3043 && useWith == 3047 || itemUsed == 3047
				&& useWith == 3043) {
			return;
		}
		if (itemUsed == 3045 && useWith == 3045 || itemUsed == 3045
				&& useWith == 3045) {
			return;
		}
		if (itemUsed == 3045 && useWith == 3047 || itemUsed == 3047
				&& useWith == 3045) {
			return;
		}
		if (itemUsed == 3041 && useWith == 3040 || itemUsed == 3040
				&& useWith == 3041) {
			return;
		}
		if (itemUsed == 3041 && useWith == 3042 || itemUsed == 3042
				&& useWith == 3041) {
			return;
		}
		if (itemUsed == 3041 && useWith == 3044 || itemUsed == 3044
				&& useWith == 3041) {
			return;
		}
		if (itemUsed == 3041 && useWith == 3046 || itemUsed == 3046
				&& useWith == 3041) {
			return;
		}
		if (itemUsed == 3045 && useWith == 3046 || itemUsed == 3046
				&& useWith == 3045) {
			return;
		}
		if (itemUsed == 3045 && useWith == 3044 || itemUsed == 3044
				&& useWith == 3045) {
			return;
		}
		if (itemUsed == 3045 && useWith == 3042 || itemUsed == 3042
				&& useWith == 3045) {
			return;
		}
		if (itemUsed == 3045 && useWith == 3040 || itemUsed == 3040
				&& useWith == 3045) {
			return;
		} //
		if (itemUsed == 3047 && useWith == 3046 || itemUsed == 3046
				&& useWith == 3047) {
			return;
		}
		if (itemUsed == 3047 && useWith == 3044 || itemUsed == 3044
				&& useWith == 3047) {
			return;
		}
		if (itemUsed == 3047 && useWith == 3042 || itemUsed == 3042
				&& useWith == 3047) {
			return;
		}
		if (itemUsed == 3047 && useWith == 3040 || itemUsed == 3040
				&& useWith == 3047) {
			return;
		}
		String name = c.getItems().getItemName(itemUsed);
		if (name.contains("(4)") || name.contains("(3)")
				|| name.contains("(2)") || name.contains("(1)"))
			c.getPotMixing().mixPotion2(itemUsed, useWith);
		if (itemUsed == 1733 || useWith == 1733)
			c.getCrafting().handleLeather(itemUsed, useWith);
		// if (itemUsed == 1755 || useWith == 1755)
		// c.getCrafting().handleChisel(itemUsed, useWith);
		if (itemUsed == 946 || useWith == 946)
			c.getFletching().handleLog(itemUsed, useWith);
		if (itemUsed == 53 || useWith == 53 || itemUsed == 52 || useWith == 52)
			c.getFletching().makeArrows(itemUsed, useWith);
		if (itemUsed == 9142 && useWith == 9190 || itemUsed == 9190
				&& useWith == 9142) {
			if (c.playerLevel[c.playerFletching] >= 58) {
				int boltsMade = c.getItems().getItemAmount(itemUsed) > c
						.getItems().getItemAmount(useWith) ? c.getItems()
						.getItemAmount(useWith) : c.getItems().getItemAmount(
						itemUsed);
				c.getItems().deleteItem(useWith,
						c.getItems().getItemSlot(useWith), boltsMade);
				c.getItems().deleteItem(itemUsed,
						c.getItems().getItemSlot(itemUsed), boltsMade);
				c.getItems().addItem(9241, boltsMade);
				c.getPA().addSkillXP(
						boltsMade * 6 * Config.FLETCHING_EXPERIENCE,
						c.playerFletching);
			} else {
				c.sendMessage("You need a fletching level of 58 to fletch this item.");
			}
		}
		if (itemUsed == 9143 && useWith == 9191 || itemUsed == 9191
				&& useWith == 9143) {
			if (c.playerLevel[c.playerFletching] >= 63) {
				int boltsMade = c.getItems().getItemAmount(itemUsed) > c
						.getItems().getItemAmount(useWith) ? c.getItems()
						.getItemAmount(useWith) : c.getItems().getItemAmount(
						itemUsed);
				c.getItems().deleteItem(useWith,
						c.getItems().getItemSlot(useWith), boltsMade);
				c.getItems().deleteItem(itemUsed,
						c.getItems().getItemSlot(itemUsed), boltsMade);
				c.getItems().addItem(9242, boltsMade);
				c.getPA().addSkillXP(
						boltsMade * 7 * Config.FLETCHING_EXPERIENCE,
						c.playerFletching);
			} else {
				c.sendMessage("You need a fletching level of 63 to fletch this item.");
			}
		}
		if (itemUsed == 9143 && useWith == 9192 || itemUsed == 9192
				&& useWith == 9143) {
			if (c.playerLevel[c.playerFletching] >= 65) {
				int boltsMade = c.getItems().getItemAmount(itemUsed) > c
						.getItems().getItemAmount(useWith) ? c.getItems()
						.getItemAmount(useWith) : c.getItems().getItemAmount(
						itemUsed);
				c.getItems().deleteItem(useWith,
						c.getItems().getItemSlot(useWith), boltsMade);
				c.getItems().deleteItem(itemUsed,
						c.getItems().getItemSlot(itemUsed), boltsMade);
				c.getItems().addItem(9243, boltsMade);
				c.getPA().addSkillXP(
						boltsMade * 7 * Config.FLETCHING_EXPERIENCE,
						c.playerFletching);
			} else {
				c.sendMessage("You need a fletching level of 65 to fletch this item.");
			}
		}
		if (itemUsed == 9144 && useWith == 9193 || itemUsed == 9193
				&& useWith == 9144) {
			if (c.playerLevel[c.playerFletching] >= 71) {
				int boltsMade = c.getItems().getItemAmount(itemUsed) > c
						.getItems().getItemAmount(useWith) ? c.getItems()
						.getItemAmount(useWith) : c.getItems().getItemAmount(
						itemUsed);
				c.getItems().deleteItem(useWith,
						c.getItems().getItemSlot(useWith), boltsMade);
				c.getItems().deleteItem(itemUsed,
						c.getItems().getItemSlot(itemUsed), boltsMade);
				c.getItems().addItem(9244, boltsMade);
				c.getPA().addSkillXP(
						boltsMade * 10 * Config.FLETCHING_EXPERIENCE,
						c.playerFletching);
			} else {
				c.sendMessage("You need a fletching level of 71 to fletch this item.");
			}
		}
		if (itemUsed == 9144 && useWith == 9194 || itemUsed == 9194
				&& useWith == 9144) {
			if (c.playerLevel[c.playerFletching] >= 58) {
				int boltsMade = c.getItems().getItemAmount(itemUsed) > c
						.getItems().getItemAmount(useWith) ? c.getItems()
						.getItemAmount(useWith) : c.getItems().getItemAmount(
						itemUsed);
				c.getItems().deleteItem(useWith,
						c.getItems().getItemSlot(useWith), boltsMade);
				c.getItems().deleteItem(itemUsed,
						c.getItems().getItemSlot(itemUsed), boltsMade);
				c.getItems().addItem(9245, boltsMade);
				c.getPA().addSkillXP(
						boltsMade * 13 * Config.FLETCHING_EXPERIENCE,
						c.playerFletching);
			} else {
				c.sendMessage("You need a fletching level of 58 to fletch this item.");
			}
		}
		if (itemUsed == 1601 && useWith == 1755 || itemUsed == 1755
				&& useWith == 1601) {
			if (c.playerLevel[c.playerFletching] >= 63) {
				c.getItems()
						.deleteItem(1601, c.getItems().getItemSlot(1601), 1);
				c.getItems().addItem(9192, 15);
				c.getPA().addSkillXP(8 * Config.FLETCHING_EXPERIENCE,
						c.playerFletching);
			} else {
				c.sendMessage("You need a fletching level of 63 to fletch this item.");
			}
		}
		if (itemUsed == 1607 && useWith == 1755 || itemUsed == 1755
				&& useWith == 1607) {
			if (c.playerLevel[c.playerFletching] >= 65) {
				c.getItems()
						.deleteItem(1607, c.getItems().getItemSlot(1607), 1);
				c.getItems().addItem(9189, 15);
				c.getPA().addSkillXP(8 * Config.FLETCHING_EXPERIENCE,
						c.playerFletching);
			} else {
				c.sendMessage("You need a fletching level of 65 to fletch this item.");
			}
		}
		if (itemUsed == 1605 && useWith == 1755 || itemUsed == 1755
				&& useWith == 1605) {
			if (c.playerLevel[c.playerFletching] >= 71) {
				c.getItems()
						.deleteItem(1605, c.getItems().getItemSlot(1605), 1);
				c.getItems().addItem(9190, 15);
				c.getPA().addSkillXP(8 * Config.FLETCHING_EXPERIENCE,
						c.playerFletching);
			} else {
				c.sendMessage("You need a fletching level of 71 to fletch this item.");
			}
		}
		if (itemUsed == 1603 && useWith == 1755 || itemUsed == 1755
				&& useWith == 1603) {
			if (c.playerLevel[c.playerFletching] >= 73) {
				c.getItems()
						.deleteItem(1603, c.getItems().getItemSlot(1603), 1);
				c.getItems().addItem(9191, 15);
				c.getPA().addSkillXP(8 * Config.FLETCHING_EXPERIENCE,
						c.playerFletching);
			} else {
				c.sendMessage("You need a fletching level of 73 to fletch this item.");
			}
		}
		if (itemUsed == 1615 && useWith == 1755 || itemUsed == 1755
				&& useWith == 1615) {
			if (c.playerLevel[c.playerFletching] >= 73) {
				c.getItems()
						.deleteItem(1615, c.getItems().getItemSlot(1615), 1);
				c.getItems().addItem(9193, 15);
				c.getPA().addSkillXP(8 * Config.FLETCHING_EXPERIENCE,
						c.playerFletching);
			} else {
				c.sendMessage("You need a fletching level of 73 to fletch this item.");
			}
		}
		if (itemUsed >= 11710 && itemUsed <= 11714 && useWith >= 11710
				&& useWith <= 11714) {
			if (c.getItems().hasAllShards()) {
				c.getItems().makeBlade();
			}
		}
		if (itemUsed == 2368 && useWith == 2366 || itemUsed == 2366
				&& useWith == 2368) {
			c.getItems().deleteItem(2368, c.getItems().getItemSlot(2368), 1);
			c.getItems().deleteItem(2366, c.getItems().getItemSlot(2366), 1);
			c.getItems().addItem(1187, 1);
		}

		if (c.getItems().isHilt(itemUsed) || c.getItems().isHilt(useWith)) {
			int hilt = c.getItems().isHilt(itemUsed) ? itemUsed : useWith;
			int blade = c.getItems().isHilt(itemUsed) ? useWith : itemUsed;
			if (blade == 11690) {
				c.getItems().makeGodsword(hilt);
			}
		}

		switch (itemUsed) {
		// case 1511:
		// case 1521:
		// case 1519:
		// case 1517:
		// case 1515:
		// case 1513:
		// case 590:
		// c.sendMessage("Firemaking is disabled.");
		// break;

		default:
			if (c.playerRights == 3)
				Misc.println("Player used Item id: " + itemUsed
						+ " with Item id: " + useWith);
			break;
		}
	}

	public static void ItemonNpc(Client c, int itemId, int npcType, int slot) {
		c.clickNpcType = 0;
		c.npcClickIndex = 0;
		c.faceNPC(npcType);
		c.faceUpdate(npcType);
		switch (itemId) {
		case 2842:
			if (npcType == 3050)
				c.getDH().sendDialogues(7, 0);
			break;

		default:
			if (c.playerRights == 3)
				Misc.println("Player used Item id: " + itemId
						+ " with Npc id: " + npcType + " With Slot : " + slot);
			break;
		}

	}

}
