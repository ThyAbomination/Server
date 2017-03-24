package server.model.players;

import server.Config;
import server.Server;
import server.content.HealingContent;
import server.content.travel.Ladders;
import server.content.travel.RugTravel;
import server.model.minigames.PestControl;
import server.model.minigames.SpiritWarriors;
import server.model.objects.Object;
import server.model.players.packets.JourneyShip;
import server.model.players.skills.Fishing;
import server.model.players.skills.Mining;
import server.model.players.skills.ThievingManager;
import server.model.players.skills.WoodcuttingManager;
import server.util.Misc;
import server.util.ScriptManager;

public class ActionHandler {

	private Client c;

	public ActionHandler(Client Client) {
		this.c = Client;
	}

	public void firstClickObject(int objectType, int obX, int obY) {
		c.clickObjectType = 0;
		c.turnPlayerTo(obX, obY);
		if (Ladders.isAdvancedLadder(objectType))
			Ladders.ladderHandling(c, objectType, obX, obY);
		switch (objectType) {
			
                        case 2403:
                        if (c.Agrith == true) {
                                c.getShops().openShop(65);
                                } else {
                                c.sendMessage("You need to have completed Recipe for Disaster to use this");
                        }
                        break;
						
		case 12356:
			c.getPA().startTeleport(3432, 3573, 0, "modern");
		break;
			

		case 13291: // magic chest
			if (c.objectX == 2559 && c.objectY == 4959) { // first
				if (c.spiritWave == 4 && c.spiritCount >= 16
						&& c.canLoot == true) {
					Server.objectHandler.createAnObject(c, 13292, 2559, 4959);
					c.canLoot = false;
				}
			}
			if (c.objectX == 2560 && c.objectY == 4959) {// second
				if (c.spiritWave == 4 && c.spiritCount >= 16
						&& c.canLoot == true) {
					Server.objectHandler.createAnObject(c, 13292, 2560, 4959);
					c.canLoot = false;
				}
			}
			if (c.objectX == 2560 && c.objectY == 4960) {// third
				if (c.spiritWave == 4 && c.spiritCount >= 16
						&& c.canLoot == true) {
					Server.objectHandler.createAnObject(c, 13292, 2560, 4960);
					c.canLoot = false;
				}
			}
			if (c.objectX == 2559 && c.objectY == 4960) {// fourth
				if (c.spiritWave == 4 && c.spiritCount >= 16
						&& c.canLoot == true) {
					Server.objectHandler.createAnObject(c, 13292, 2559, 4960);
					c.canLoot = false;
				}
			} else {
				if (c.firstWave == false) {
					SpiritWarriors.spiritWave(c);
					c.firstWave = true;
				}
				if (c.secondWave == true) {
					SpiritWarriors.spiritWave(c);
					c.secondWave = false;
				}
				if (c.thirdWave == true) {
					SpiritWarriors.spiritWave(c);
					c.thirdWave = false;
				}
				if (c.fourthWave == true) {
					SpiritWarriors.spiritWave(c);
					c.fourthWave = false;
				}
			}
			break;

		case 13292: // magic chest open
			if (c.objectX == 2559 && c.objectY == 4959) { // first
				if (c.spiritWave == 4 && c.spiritCount >= 16) {
					Server.objectHandler.createAnObject(c, 13291, 2559, 4959);
					c.getItems().addItem(15300, 1);
					c.getItems().addItem(c.getPA().randomRunes2(),
							Misc.random(250) + 250);
					if (Misc.random(2) == 1)
						c.getItems().addItem(c.getPA().randomGear(), 1);
					if (Misc.random(5) == 1)
						c.getItems().addItem(c.getPA().randomShield(), 1);
					c.spiritWave = 0;
					c.spiritCount = 0;
					c.firstWave = false;
				}
			}
			if (c.objectX == 2560 && c.objectY == 4959) {// second
				if (c.spiritWave == 4 && c.spiritCount >= 16) {
					Server.objectHandler.createAnObject(c, 13291, 2560, 4959);
					c.getItems().addItem(15300, 1);
					c.getItems().addItem(c.getPA().randomRunes2(),
							Misc.random(250) + 250);
					if (Misc.random(2) == 1)
						c.getItems().addItem(c.getPA().randomGear(), 1);
					if (Misc.random(10) == 1)
						c.getItems().addItem(c.getPA().randomShield(), 1);
					c.spiritWave = 0;
					c.spiritCount = 0;
					c.firstWave = false;
				}
			}
			if (c.objectX == 2560 && c.objectY == 4960) {// third
				if (c.spiritWave == 4 && c.spiritCount >= 16) {
					Server.objectHandler.createAnObject(c, 13291, 2560, 4960);
					c.getItems().addItem(15300, 1);
					c.getItems().addItem(c.getPA().randomRunes2(),
							Misc.random(250) + 250);
					if (Misc.random(2) == 1)
						c.getItems().addItem(c.getPA().randomGear(), 1);
					if (Misc.random(10) == 1)
						c.getItems().addItem(c.getPA().randomShield(), 1);
					c.spiritWave = 0;
					c.spiritCount = 0;
					c.firstWave = false;
				}
			}
			if (c.objectX == 2559 && c.objectY == 4960) {// fourth
				if (c.spiritWave == 4 && c.spiritCount >= 16) {
					Server.objectHandler.createAnObject(c, 13291, 2559, 4960);
					c.getItems().addItem(15300, 1);
					c.getItems().addItem(c.getPA().randomRunes2(),
							Misc.random(250) + 250);
					if (Misc.random(2) == 1)
						c.getItems().addItem(c.getPA().randomGear(), 1);
					if (Misc.random(10) == 1)
						c.getItems().addItem(c.getPA().randomShield(), 1);
					c.spiritWave = 0;
					c.spiritCount = 0;
					c.firstWave = false;
				}
			}
			break;

		case 4006: // donator cave
			if (c.playerRights > 0 || c.playerDonator >= 1) {
				c.getPA().movePlayer(2830, 9522, 0);
			} else {
				c.sendMessage("You need to be a donator or staff member to access this area.");
			}
			break;

		case 2585: // donator exit
			if (c.playerRights > 0 || c.playerDonator >= 1) {
				c.getPA().movePlayer(2528, 4777, 0);
			} else {
				c.sendMessage("You need to be a donator or staff member to access this area.");
			}
			break;

		case 10556:
			if (c.enterChallenge == true) {
				c.enterChallenge = false;
				c.vengOn = false;
				c.getCurse().resetCurse();
				c.getCombat().resetPrayers();
				c.getPA().enterChallenge();
			} else if (c.enterChallenge == false) {
				c.sendMessage("You need to arrange a challenge with Larxus before entering the arena.");
			}
			break;
		case 2295: // log balance
			if (c.absX == 2474 && c.absY == 3436 && c.gnomeObsticle == 0) {
				c.getAgility().obsticle(762, 0, -7, 5000, 500);
				c.gnomeObsticle = 1;
			} else if (c.absX == 2474 && c.absY == 3436) {
				c.getAgility().obsticle(762, 0, -7, 5000, 500);
			}
			break;
		case 2285: // net
			if (c.gnomeObsticle == 1) {
				c.getAgility().agilityDelay(828, 2473, 3424, 1, 1, 250);
				c.gnomeObsticle = 2;
			} else {
				c.getAgility().agilityDelay(828, 2473, 3424, 1, 1, 250);
			}
			break;
		case 2313: // tree
			if (c.gnomeObsticle == 2) {
				c.getAgility().agilityDelay(828, 2473, 3420, 2, 1, 150);
				c.gnomeObsticle = 3;
			} else {
				c.getAgility().agilityDelay(828, 2473, 3420, 2, 1, 150);
			}
			break;

		case 2312: // rope
			if (c.absX == 2477 && c.absY == 3420 && c.gnomeObsticle == 3) {
				c.getAgility().obsticle(762, 7, 0, 5000, 500);
				c.gnomeObsticle = 4;
			}
			if (c.absX == 2477 && c.absY == 3420) {
				c.getAgility().obsticle(762, 7, 0, 5000, 500);
			}
			break;

		case 2314: // tree branch
			if (c.gnomeObsticle == 4) {
				c.getAgility().agilityDelay(828, 2487, 3421, 0, 0, 550);
				c.gnomeObsticle = 5;
			} else {
				c.getAgility().agilityDelay(828, 2487, 3421, 0, 0, 550);
			}
			break;
		case 2286: // obstacle net
			if (c.absX >= 2483 && c.absX <= 2488 && c.absY == 3425) {
				c.getAgility()
						.agilityDelay(3063, c.absX, c.absY + 2, 0, 1, 150);
				c.turnPlayerTo(c.objectX, c.objectY);
			}
			if (c.absX >= 2483 && c.absX <= 2488 && c.absY == 3425
					&& c.gnomeObsticle == 5) {
				c.getAgility()
						.agilityDelay(3063, c.absX, c.absY + 2, 0, 1, 150);
				c.turnPlayerTo(c.objectX, c.objectY);
				c.gnomeObsticle = 6;
			}
			break;
		case 154: // pipe
			if (c.absX == 2484 && c.absY == 3430 && c.gnomeObsticle == 6) {
				c.getAgility().obsticle(844, 0, 7, 5000, 500);
				c.getItems().addItem(2996, 10);
				c.getPA().addSkillXP(500 * c.playerLevel[Player.playerAgility],
						Player.playerAgility);
				c.gnomeObsticle = 0;
			}
			if (c.absX == 2484 && c.absY == 3430) {
				c.getAgility().obsticle(844, 0, 7, 5000, 500);
			}
			break;
		case 4058:// pipe
			if (c.absX == 2487 && c.absY == 3430 && c.gnomeObsticle == 6) {
				c.getAgility().obsticle(844, 0, 7, 5000, 500);
				c.getItems().addItem(2996, 10);
				c.getPA().addSkillXP(500 * c.playerLevel[Player.playerAgility],
						Player.playerAgility);
				c.gnomeObsticle = 0;
			}
			if (c.absX == 2487 && c.absY == 3430) {
				c.getAgility().obsticle(844, 0, 7, 5000, 500);
			}
			break;

		case 4031:
			if (c.absY > c.objectY) {
				c.getPA().movePlayer(3304, 3115, 0);
			} else if (c.absY < c.objectY) {
				c.getPA().movePlayer(3304, 3117, 0);
			}
			break;

		case 11666:
		case 3044:
		case 3994:
		case 2781:
		c.getPA().sendFrame126("", 4158);
		c.getSmithing().sendSmelting();
   break;

		case 12163:
		case 12165:
			WoodcuttingManager.handleCanoe(c, c.objectId);
			break;

		case 2492:
			c.getPA().startTeleport(3254, 3402, 0, "modern");
			break;

		case 2092:
		case 2091:
		case 2095:
		case 2093:
		case 2094:
		case 2090:
		case 2102:
		case 2103:
		case 2096:
		case 2097:
		case 2104:
		case 2105:
		case 2101:
		case 2100:
		case 2098:
		case 2099:
		case 2109:
		case 2108:
		case 2106:
		case 2107:
		case 11933:
		case 11934:
		case 11935:
		case 11931:
		case 11932:
		case 11930:
			Mining.checkRequirments(c, c.objectId, c.objectX, c.objectY);
			break;

		case 1276:
		case 1277:
		case 1278:
		case 1279:
		case 1280:
		case 1330:
		case 1332:
		case 3033:
		case 3034:
		case 3035:
		case 3036:
		case 3879:
		case 3881:
		case 3882:
		case 3883:
			// Normal Tree
		case 1315:
		case 1316:
		case 1318:
		case 1319:
		case 21273:
			// Evergreen
		case 1282:
		case 1283:
		case 1284:
		case 1285:
		case 1286:
		case 1287:
		case 1289:
		case 1290:
		case 1291:
		case 1365:
		case 1383:
		case 1384:
		case 5902:
		case 5903:
		case 5904:
		case 2023:
		case 1281:
		case 3037:
		case 1308:
		case 5551:
		case 5552:
		case 5553:
		case 1292:
		case 1307:
		case 4674:
		case 2289:
		case 4060:
		case 1309:
		case 1306:
			// c.getWood().checkTree(objectType);
			WoodcuttingManager.cutTree(c, objectType);
			break;
		case 8689:
			c.getDH().sendOption2("Milk cow?", "Leave?");
			c.dialogueAction = 105;
			break;

		case 8143:
			if (c.farm[0] > 0 && c.farm[1] > 0) {
				c.getFarming().pickHerb();
			}
			break;

		case 2557:
			if (c.getItems().playerHasItem(1523, 1) && c.absX == 3190
					&& c.absY == 3957) {
				c.getPA().movePlayer(3190, 3958, 0);
			} else if (c.getItems().playerHasItem(1523, 1) && c.absX == 3190
					&& c.absY == 3958) {
				c.getPA().movePlayer(3190, 3957, 0);
			}
			break;

		case 2995:
			c.getPA().startTeleport2(2717, 9801, 0);
			c.sendMessage("Welcome to the dragon lair, be aware. It's very dangerous.");
			break;

		case 1814:
			// ardy lever
			c.getPA().startTeleport(3153, 3923, 0, "modern");
			break;

		case 2882:
		case 2883:
			if (c.objectX == 3268) {
				if (c.absX < c.objectX) {
					c.getPA().walkTo(1, 0);
				} else {
					c.getPA().walkTo(-1, 0);
				}
			}
			break;

		case 3489:
		case 3490:
			if (c.absX < c.objectX) {

				c.sendMessage("I should knock first before entering.");
			}
			break;

		case 1765:
			c.getPA().movePlayer(3067, 10256, 0);
			break;

		case 3432:
			c.getPA().movePlayer(3405, 3505, 0);
			break;

		case 272:
			c.getPA().movePlayer(c.absX, c.absY, 1);
			break;

		case 273:
			c.getPA().movePlayer(c.absX, c.absY, 0);
			break;

		case 245:
			c.getPA().movePlayer(c.absX, c.absY + 2, 2);
			break;

		case 246:
			c.getPA().movePlayer(c.absX, c.absY - 2, 1);
			break;

		case 1766:
			c.getPA().movePlayer(3016, 3849, 0);
			break;

		case 6552:
			if (c.onAuto) {
				c.sendMessage("You can't switch spellbooks with autocast on.");
				return;
			}
			if (c.playerMagicBook == 0) {
				if (c.playerEquipment[Player.playerWeapon] == 4675
						|| c.playerEquipment[Player.playerWeapon] == 15486
						|| c.playerEquipment[Player.playerWeapon] == 18355
						|| c.playerEquipment[Player.playerWeapon] == 13867
						|| c.playerEquipment[Player.playerWeapon] == 6563) {
					c.setSidebarInterface(0, 328);
				}
				c.playerMagicBook = 1;
				c.setSidebarInterface(6, 12855);
				c.sendMessage("An ancient wisdomin fills your mind.");
				c.getPA().resetAutoCast();
			} else {
				if (c.playerEquipment[Player.playerWeapon] == 4675
						|| c.playerEquipment[Player.playerWeapon] == 15486
						|| c.playerEquipment[Player.playerWeapon] == 18355
						|| c.playerEquipment[Player.playerWeapon] == 13867
						|| c.playerEquipment[Player.playerWeapon] == 6563) {
					c.setSidebarInterface(0, 328);
				}
				c.setSidebarInterface(6, 1151); // modern
				c.playerMagicBook = 0;
				c.sendMessage("You feel a drain on your memory.");
				c.getPA().resetAutoCast();
				c.autocastId = -1;
			}
			break;

		case 8749:
			if (c.onAuto) {
				c.sendMessage("You can't switch spellbooks with autocast on.");
				return;
			}
			if (c.playerMagicBook == 0) {
				c.playerMagicBook = 2;
				c.setSidebarInterface(6, 29999);
				c.sendMessage("A lunar wisdomin fills your mind.");
				c.getPA().resetAutoCast();
			} else {
				c.setSidebarInterface(6, 1151); // modern
				c.playerMagicBook = 0;
				c.sendMessage("You feel a drain on your memory.");
				c.autocastId = -1;
				c.getPA().resetAutoCast();
			}
			break;

		case 1733:
			c.getPA().movePlayer(c.absX, c.absY + 6393, 0);
			break;

		case 1734:
			c.getPA().movePlayer(c.absX, c.absY - 6396, 0);
			break;

		case 8959:
			if (c.getX() == 2490 && (c.getY() == 10146 || c.getY() == 10148)) {
				if (c.getPA().checkForPlayer(2490,
						c.getY() == 10146 ? 10148 : 10146)) {
					new Object(6951, c.objectX, c.objectY, c.heightLevel, 1,
							10, 8959, 15, false);
				}
			}
			break;

		case 2213:
		case 14367:
		case 11758:
		case 10517:
		case 3193:
		case 5276:
		case 2693:
			c.isBanking = true;
			c.getPA().openUpBank();
			break;

		case 10177:
			c.getPA().movePlayer(1890, 4407, 0);
			break;
		case 10230:
			c.getPA().movePlayer(2900, 4449, 0);
			break;
		case 10229:
			c.getPA().movePlayer(1912, 4367, 0);
			break;

		case 2623:
			if (c.absX >= c.objectX)
				c.getPA().walkTo(-1, 0);
			else
				c.getPA().walkTo(+1, 0);
			break;

		case 1599:
		case 1598:
			if (c.absX < c.objectX)
				c.getPA().walkTo(1, 0);
			else
				c.getPA().walkTo(-1, 0);
			break;
		case 14315:
			if (c.absX == 2657 && c.absY == 2639) {
				if (!PestControl.waitingBoat.containsKey(c)) {
					PestControl.addToWaitRoom(c);
				} else {
					c.getPA().movePlayer(2661, 2639, 0);
				}
			}
			break;
		case 14314:
			if (c.inPcBoat()) {
				if (PestControl.waitingBoat.containsKey(c))
					PestControl.leaveWaitingBoat(c);
				else
					c.getPA().movePlayer(2657, 2639, 0);
			}
			break;
		case 1596:
		case 1597:
			if (c.getY() >= c.objectY)
				c.getPA().walkTo(0, -1);
			else
				c.getPA().walkTo(0, 1);
			break;

		case 14235:
		case 14233:
			if (c.objectX == 2670)
				if (c.absX <= 2670)
					c.absX = 2671;
				else
					c.absX = 2670;
			if (c.objectX == 2643)
				if (c.absX >= 2643)
					c.absX = 2642;
				else
					c.absX = 2643;
			if (c.absX <= 2585)
				c.absY += 1;
			else
				c.absY -= 1;
			c.getPA().movePlayer(c.absX, c.absY, 0);
			break;

		case 14829:
		case 14830:
		case 14827:
		case 14828:
		case 14826:
		case 14831:
			// Server.objectHandler.startObelisk(objectType);
			Server.objectManager.startObelisk(objectType);
			break;

		// barrows
		// Chest
		case 10284:
			if (c.barrowsKillCount < 5) {
				c.sendMessage("You haven't killed all the barrows brothers yet.");
			}
			if (c.barrowsKillCount == 5
					&& c.barrowsNpcs[c.randomCoffin][1] == 1) {
				c.sendMessage("You have already killed this barrow brother.");
			}
			if (c.barrowsNpcs[c.randomCoffin][1] == 0
					&& c.barrowsKillCount >= 5) {
				Server.npcHandler.spawnNpc(c, c.barrowsNpcs[c.randomCoffin][0],
						3551, 9694 - 1, 0, 0, 120, 25, 400, 400, true, true);
				c.barrowsNpcs[c.randomCoffin][1] = 1;
			}
			if ((c.barrowsKillCount > 5 || c.barrowsNpcs[c.randomCoffin][1] == 2)
					&& c.getItems().freeSlots() >= 2) {
				c.getPA().resetBarrows();
				c.getItems().addItem(c.getPA().randomRunes(),
						Misc.random(250) + 200);
				if (Misc.random(2) == 1)
					c.getItems().addItem(c.getPA().randomBarrows(), 1);
					c.getPA().startTeleport(3564, 3288, 0, "modern");
			} else if (c.barrowsKillCount > 5 && c.getItems().freeSlots() <= 1) {
				c.sendMessage("You need at least 2 inventory slots.");
			}
			break;
		// doors
		case 6749:
			if (obX == 3562 && obY == 9678) {
				c.getPA().object(3562, 9678, 6749, -3, 0);
				c.getPA().object(3562, 9677, 6730, -1, 0);
			} else if (obX == 3558 && obY == 9677) {
				c.getPA().object(3558, 9677, 6749, -1, 0);
				c.getPA().object(3558, 9678, 6730, -3, 0);
			}
			break;
		case 6730:
			if (obX == 3558 && obY == 9677) {
				c.getPA().object(3562, 9678, 6749, -3, 0);
				c.getPA().object(3562, 9677, 6730, -1, 0);
			} else if (obX == 3558 && obY == 9678) {
				c.getPA().object(3558, 9677, 6749, -1, 0);
				c.getPA().object(3558, 9678, 6730, -3, 0);
			}
			break;
		case 6727:
			if (obX == 3551 && obY == 9684) {
				c.sendMessage("You cant open this door..");
			}
			break;
		case 6746:
			if (obX == 3552 && obY == 9684) {
				c.sendMessage("You cant open this door..");
			}
			break;
		case 6748:
			if (obX == 3545 && obY == 9678) {
				c.getPA().object(3545, 9678, 6748, -3, 0);
				c.getPA().object(3545, 9677, 6729, -1, 0);
			} else if (obX == 3541 && obY == 9677) {
				c.getPA().object(3541, 9677, 6748, -1, 0);
				c.getPA().object(3541, 9678, 6729, -3, 0);
			}
			break;
		case 6729:
			if (obX == 3545 && obY == 9677) {
				c.getPA().object(3545, 9678, 6748, -3, 0);
				c.getPA().object(3545, 9677, 6729, -1, 0);
			} else if (obX == 3541 && obY == 9678) {
				c.getPA().object(3541, 9677, 6748, -1, 0);
				c.getPA().object(3541, 9678, 6729, -3, 0);
			}
			break;
		case 6726:
			if (obX == 3534 && obY == 9684) {
				c.getPA().object(3534, 9684, 6726, -4, 0);
				c.getPA().object(3535, 9684, 6745, -2, 0);
			} else if (obX == 3535 && obY == 9688) {
				c.getPA().object(3535, 9688, 6726, -2, 0);
				c.getPA().object(3534, 9688, 6745, -4, 0);
			}
			break;
		case 6745:
			if (obX == 3535 && obY == 9684) {
				c.getPA().object(3534, 9684, 6726, -4, 0);
				c.getPA().object(3535, 9684, 6745, -2, 0);
			} else if (obX == 3534 && obY == 9688) {
				c.getPA().object(3535, 9688, 6726, -2, 0);
				c.getPA().object(3534, 9688, 6745, -4, 0);
			}
			break;
		case 6743:
			if (obX == 3545 && obY == 9695) {
				c.getPA().object(3545, 9694, 6724, -1, 0);
				c.getPA().object(3545, 9695, 6743, -3, 0);
			} else if (obX == 3541 && obY == 9694) {
				c.getPA().object(3541, 9694, 6724, -1, 0);
				c.getPA().object(3541, 9695, 6743, -3, 0);
			}
			break;
		case 6724:
			if (obX == 3545 && obY == 9694) {
				c.getPA().object(3545, 9694, 6724, -1, 0);
				c.getPA().object(3545, 9695, 6743, -3, 0);
			} else if (obX == 3541 && obY == 9695) {
				c.getPA().object(3541, 9694, 6724, -1, 0);
				c.getPA().object(3541, 9695, 6743, -3, 0);
			}
			break;

		case 6707: // verac
			c.getPA().movePlayer(3556, 3298, 0);
			break;

		case 6823:
			if (server.model.minigames.Barrows.selectCoffin(c, objectType)) {
				return;
			}
			if (c.barrowsNpcs[0][1] == 0) {
				Server.npcHandler.spawnNpc(c, 2030, c.getX(), c.getY() - 1, 7,
						0, 120, 25, 400, 400, true, true);
				c.barrowsNpcs[0][1] = 1;
			} else {
				c.sendMessage("You have already searched in this sarcophagus.");
			}
			break;

		case 6706: // torag
			c.getPA().movePlayer(3553, 3283, 0);
			break;

		case 6772:
			if (server.model.minigames.Barrows.selectCoffin(c, objectType)) {
				return;
			}
			if (c.barrowsNpcs[1][1] == 0) {
				Server.npcHandler.spawnNpc(c, 2029, c.getX() + 1, c.getY(), 7,
						0, 120, 26, 400, 400, true, true);
				c.barrowsNpcs[1][1] = 1;
			} else {
				c.sendMessage("You have already searched in this sarcophagus.");
			}
			break;

		case 6705: // karil stairs
			c.getPA().movePlayer(3565, 3276, 0);
			break;
		case 6822:
			if (server.model.minigames.Barrows.selectCoffin(c, objectType)) {
				return;
			}
			if (c.barrowsNpcs[2][1] == 0) {
				Server.npcHandler.spawnNpc(c, 2028, c.getX(), c.getY() - 1, 7,
						0, 100, 20, 400, 400, true, true);
				c.barrowsNpcs[2][1] = 1;
			} else {
				c.sendMessage("You have already searched in this sarcophagus.");
			}
			break;

		case 6704: // guthan stairs
			c.getPA().movePlayer(3578, 3284, 0);
			break;
		case 6773:
			if (server.model.minigames.Barrows.selectCoffin(c, objectType)) {
				return;
			}
			if (c.barrowsNpcs[3][1] == 0) {
				Server.npcHandler.spawnNpc(c, 2027, c.getX(), c.getY() - 1, 7,
						0, 120, 24, 400, 400, true, true);
				c.barrowsNpcs[3][1] = 1;
			} else {
				c.sendMessage("You have already searched in this sarcophagus.");
			}
			break;

		case 6703: // dharok stairs
			c.getPA().movePlayer(3574, 3298, 0);
			break;
		case 6771:
			if (server.model.minigames.Barrows.selectCoffin(c, objectType)) {
				return;
			}
			if (c.barrowsNpcs[4][1] == 0) {
				Server.npcHandler.spawnNpc(c, 2026, c.getX(), c.getY() - 1, 7,
						0, 120, 68, 450, 450, true, true);
				c.barrowsNpcs[4][1] = 1;
			} else {
				c.sendMessage("You have already searched in this sarcophagus.");
			}
			break;

		case 6702: // ahrim stairs
			c.getPA().movePlayer(3565, 3290, 0);
			break;
		case 6821:
			if (server.model.minigames.Barrows.selectCoffin(c, objectType)) {
				return;
			}
			if (c.barrowsNpcs[5][1] == 0) {
				Server.npcHandler.spawnNpc(c, 2025, c.getX(), c.getY() - 1, 7,
						0, 100, 20, 200, 200, true, true);
				c.barrowsNpcs[5][1] = 1;
			} else {
				c.sendMessage("You have already searched in this sarcophagus.");
			}
			break;

		case 9319:
			if (c.heightLevel == 0)
				c.getPA().movePlayer(c.absX, c.absY, 1);
			else if (c.heightLevel == 1)
				c.getPA().movePlayer(c.absX, c.absY, 2);
			break;

		case 9320:
			if (c.heightLevel == 1)
				c.getPA().movePlayer(c.absX, c.absY, 0);
			else if (c.heightLevel == 2)
				c.getPA().movePlayer(c.absX, c.absY, 1);
			break;

		case 4496:
		case 4494:
			if (c.heightLevel == 2) {
				c.getPA().movePlayer(c.absX - 5, c.absY, 1);
			} else if (c.heightLevel == 1) {
				c.getPA().movePlayer(c.absX + 5, c.absY, 0);
			}
			break;

		case 4493:
			if (c.heightLevel == 0) {
				c.getPA().movePlayer(c.absX - 5, c.absY, 1);
			} else if (c.heightLevel == 1) {
				c.getPA().movePlayer(c.absX + 5, c.absY, 2);
			}
			break;

		case 4495:
			if (c.heightLevel == 1) {
				c.getPA().movePlayer(c.absX + 5, c.absY, 2);
			}
			break;

		case 5126:
			if (c.absY == 3554)
				c.getPA().walkTo(0, 1);
			else
				c.getPA().walkTo(0, -1);
			break;

		case 1759:
			if (c.objectX == 2884 && c.objectY == 3397)
				c.getPA().movePlayer(c.absX, c.absY + 6400, 0);
			break;
		case 1558:
			if (c.absX == 3041 && c.absY == 10308) {
				c.getPA().movePlayer(3040, 10308, 0);
			} else if (c.absX == 3040 && c.absY == 10308) {
				c.getPA().movePlayer(3041, 10308, 0);
			} else if (c.absX == 3040 && c.absY == 10307) {
				c.getPA().movePlayer(3041, 10307, 0);
			} else if (c.absX == 3041 && c.absY == 10307) {
				c.getPA().movePlayer(3040, 10307, 0);
			} else if (c.absX == 3044 && c.absY == 10341) {
				c.getPA().movePlayer(3045, 10341, 0);
			} else if (c.absX == 3045 && c.absY == 10341) {
				c.getPA().movePlayer(3044, 10341, 0);
			} else if (c.absX == 3044 && c.absY == 10342) {
				c.getPA().movePlayer(3045, 10342, 0);
			} else if (c.absX == 3045 && c.absY == 10342) {
				c.getPA().movePlayer(3044, 10343, 0);
			}
			break;
		case 1557:
			if (c.absX == 3023 && c.absY == 10312) {
				c.getPA().movePlayer(3022, 10312, 0);
			} else if (c.absX == 3022 && c.absY == 10312) {
				c.getPA().movePlayer(3023, 10312, 0);
			} else if (c.absX == 3023 && c.absY == 10311) {
				c.getPA().movePlayer(3022, 10311, 0);
			} else if (c.absX == 3022 && c.absY == 10311) {
				c.getPA().movePlayer(3023, 10311, 0);
			}
			break;
		/*
		 * case 3203: // dueling forfeit if (c.duelCount > 0) {
		 * c.sendMessage("You may not forfeit yet."); break; } Client o =
		 * (Client) PlayerHandler.players[c.duelingWith]; if (o == null) {
		 * c.getTradeAndDuel().resetDuel(); c.getPA().movePlayer(
		 * Config.DUELING_RESPAWN_X +
		 * (Misc.random(Config.RANDOM_DUELING_RESPAWN)),
		 * Config.DUELING_RESPAWN_Y +
		 * (Misc.random(Config.RANDOM_DUELING_RESPAWN)), 0); break; } if
		 * (c.duelRule[0]) {
		 * c.sendMessage("Forfeiting the duel has been disabled!"); break; } {
		 * o.getPA().movePlayer( Config.DUELING_RESPAWN_X +
		 * (Misc.random(Config.RANDOM_DUELING_RESPAWN)),
		 * Config.DUELING_RESPAWN_Y +
		 * (Misc.random(Config.RANDOM_DUELING_RESPAWN)), 0);
		 * c.getPA().movePlayer( Config.DUELING_RESPAWN_X +
		 * (Misc.random(Config.RANDOM_DUELING_RESPAWN)),
		 * Config.DUELING_RESPAWN_Y +
		 * (Misc.random(Config.RANDOM_DUELING_RESPAWN)), 0); o.duelStatus = 6;
		 * o.getTradeAndDuel().duelVictory(); c.getTradeAndDuel().resetDuel();
		 * c.getTradeAndDuel().resetDuelItems();
		 * o.sendMessage("The other player has forfeited the duel!");
		 * c.sendMessage("You forfeit the duel!"); break; }
		 */

		case 409:
			if (c.playerLevel[5] < c.getPA().getLevelForXP(c.playerXP[5])) {
				c.startAnimation(645);
				c.playerLevel[5] = c.getPA().getLevelForXP(c.playerXP[5]);
				c.sendMessage("You recharge your prayer points.");
				c.getPA().refreshSkill(5);
			} else {
				c.sendMessage("You already have full prayer points.");
			}
			break;
		case 61:
			if (c.altarPrayed == 0) {
				c.altarPrayed = 1;
				c.setSidebarInterface(5, 22500);
				c.sendMessage("You sense a surge of power flow through your body!");
				c.getCombat().resetPrayers();
				c.getPA().resetPrayer();
			} else {
				c.altarPrayed = 0;
				c.setSidebarInterface(5, 5608);
				c.sendMessage("You sense a surge of purity flow through your body!");
				c.getCurse().resetCurse();
			}
			break;
		case 2873:
			if (!c.getItems().ownsCape()) {
				c.startAnimation(645);
				c.sendMessage("Saradomin blesses you with a cape.");
				c.getItems().addItem(2412, 1);
			}
			break;
		case 2875:
			if (!c.getItems().ownsCape()) {
				c.startAnimation(645);
				c.sendMessage("Guthix blesses you with a cape.");
				c.getItems().addItem(2413, 1);
			}
			break;
		case 2874:
			if (!c.getItems().ownsCape()) {
				c.startAnimation(645);
				c.sendMessage("Zamorak blesses you with a cape.");
				c.getItems().addItem(2414, 1);
			}
			break;
		case 2879:
			c.getPA().movePlayer(2538, 4716, 0);
			break;
		case 2878:
			c.getPA().movePlayer(2509, 4689, 0);
			break;
		case 5960:
			c.getPA().startTeleport2(3090, 3956, 0);
			break;

		case 1815:
			c.getPA().startTeleport2(Config.EDGEVILLE_X, Config.EDGEVILLE_Y, 0);
			break;

		case 9706:
			c.getPA().startTeleport2(3105, 3951, 0);
			break;
		case 9707:
			c.getPA().startTeleport2(3105, 3956, 0);
			break;

		case 5959:
			c.getPA().startTeleport2(2539, 4712, 0);
			break;

		case 2558:
			c.sendMessage("This door is locked.");
			break;

		case 9294:
			if (c.absX < c.objectX) {
				c.getPA().movePlayer(c.objectX + 1, c.absY, 0);
			} else if (c.absX > c.objectX) {
				c.getPA().movePlayer(c.objectX - 1, c.absY, 0);
			}
			break;

		case 9293:
			if (c.absX < c.objectX) {
				c.getPA().movePlayer(2892, 9799, 0);
			} else {
				c.getPA().movePlayer(2886, 9799, 0);
			}
			c.sendMessage("@red@Warning: Wear an anti-dragon shield to protect from the dragon's fire.");
			c.sendMessage("@red@You can buy the shield at Falador's shield store.");
			break;
		case 10529:
		case 10527:
			if (c.absY <= c.objectY)
				c.getPA().walkTo(0, 1);
			else
				c.getPA().walkTo(0, -1);
			break;

		/*
		 * case 733: c.startAnimation(451);
		 * 
		 * if (c.objectX == 3158 && c.objectY == 3951) { new Object(734,
		 * c.objectX, c.objectY, c.heightLevel, 1, 10, 733, 50, false); } else {
		 * new Object(734, c.objectX, c.objectY, c.heightLevel, 0, 10, 733, 50,
		 * false); } break;
		 */

		case 10596:// mid level dungeon
			c.getPA().movePlayer(3056, 9555, 0);
			break;

		case 10595:// mid level dungeon
			c.getPA().movePlayer(3056, 9562, 0);
			break;

		case 2467:// single
			if (c.teleTimer > 0) {
				return;
			}
			if (System.currentTimeMillis() - c.portalDelay >= 1500) {
				c.getPA().movePlayer(3087 + (Misc.random(2)),
						3516 + (Misc.random(2)), 0);
				c.getPA().requestUpdates();
			}
			break;

		case 2470:// multi
			if (c.teleTimer > 0) {
				return;
			}
			if (System.currentTimeMillis() - c.portalDelay >= 1500) {
				c.getPA().movePlayer(3028 + (Misc.random(12)),
						3687 + (Misc.random(12)), 0);
			}
			break;

		case 2469:// back home
			if (c.teleTimer > 0) {
				return;
			}
			if (System.currentTimeMillis() - c.portalDelay >= 1500) {
				c.getPA().movePlayer(3429 + (Misc.random(6)),
						3569 + (Misc.random(6)), 0);
				c.getPA().requestUpdates();
			} else if (c.objectX == 3093 && c.objectY == 3933) {
						c.getPA().spellTeleport(3434, 3573, 0);
			} else if (c.objectX == 3093 && c.objectY == 3934) {
						c.getPA().spellTeleport(3434, 3574, 0);
			}
			break;

		case 3831:// kalphite rope down
			c.getPA().movePlayer(3508, 9493, 0);
			break;

		case 3832:// kalphite rope back up
			c.getPA().movePlayer(3509, 9496, 2);
			break;
			
		case 2466:// home portals
			if (c.teleTimer > 0) {
				return;
			}
			if (System.currentTimeMillis() - c.portalDelay >= 1500) {
				if (c.objectX == 3450 && c.objectY == 3576) {// duel
					c.getPA().movePlayer(3367 + (Misc.random(2)),
							3267 + (Misc.random(2)), 0);
				} else if (c.objectX == 3423 && c.objectY == 3576) {// range
					c.getPA().movePlayer(2869, 9836, 0);
				} else if (c.objectX == 3444 && c.objectY == 3555) {// fish
					c.getPA().movePlayer(2590, 3415, 0);
				} else if (c.objectX == 3420 && c.objectY == 3576) {// lowlev
					c.getPA().movePlayer(2583, 9449, 0);
				} else if (c.objectX == 3417 && c.objectY == 3575) {// midlev
					c.getPA().movePlayer(3004, 9548, 0);
				} else if (c.objectX == 3414 && c.objectY == 3576) {// highlev
					c.getPA().movePlayer(2336, 9798, 0);
				} else if (c.objectX == 3411 && c.objectY == 3576) {// boss
					c.getPA().movePlayer(3158, 9573, 0);
				} else if (c.objectX == 3448 && c.objectY == 3576) {// fun pk
					c.getPA().movePlayer(2717, 9803, 0);
				} else if (c.objectX == 3446 && c.objectY == 3555) {// WC
					c.getPA().movePlayer(2608, 4775, 0);
				} else if (c.objectX == 3440 && c.objectY == 3555) {// agility
					c.getPA().movePlayer(2470, 3436, 0);
				} else if (c.objectX == 3442 && c.objectY == 3555) {// runecrafting
					c.getDH().sendDialogues(17, 0);
				} else if (c.objectX == 3448 && c.objectY == 3555) {// farming
					c.getPA().movePlayer(2817, 3460, 0);
				} else if (c.objectX == 3409 && c.objectY == 3576) {// kalphite
					c.getPA().movePlayer(3485, 9509, 2);
				} else if (c.objectX == 3451 && c.objectY == 3574) {// champions
					c.getPA().movePlayer(3189, 9758, 0);
				} else if (c.objectX == 3407 && c.objectY == 3576) {// slayer dungeon
					c.getPA().movePlayer(3207, 9379, 0);
				} else if (c.objectX == 3285 && c.objectY == 2776) {
					c.getPA().movePlayer(3312, 2800, 0);// Unholy Cursebearer
				} else if (c.objectX == 3406 && c.objectY == 3574) {// jad
					// c.enterCaves = true;
					c.getPA().enterCaves();
				} else if (c.objectX == 3406 && c.objectY == 3572) {// barrelchest
					c.getPA().movePlayer(2974, 9515, 1);
				} else if (c.objectX == 3408 && c.objectY == 3570) {// barrows
					c.getPA().movePlayer(3565, 3308, 0);
				} else if (c.objectX == 3451 && c.objectY == 3572) {// barb
					Server.barbDefence.enter(c);
				} else if (c.objectX == 3449 && c.objectY == 3570) {// pc
					c.getPA().movePlayer(2658, 2649, 0);
				} else if (c.objectX == 3410 && c.objectY == 3569) {// godwars
					c.getDH().sendDialogues(25, 0);
				} else if (c.objectX == 3413 && c.objectY == 3569) {// sw
					c.getPA().movePlayer(2559, 4945, c.playerId * 4);
				} else if (c.objectX == 3415 && c.objectY == 3569) {// portal
																	// room
					c.getPA().movePlayer(3281, 2766, 0);
				} else if (c.objectX == 3277 && c.objectY == 2767) {// bork
					c.getPA().movePlayer(3228, 3945, 0);
				} else if (c.objectX == 3277 && c.objectY == 2770) {// corp
					c.getPA().movePlayer(2515, 4633, 0);
				} else if (c.objectX == 3285 && c.objectY == 2770) {// avatars,
																	// demons
					c.getPA().movePlayer(2710, 9466, 0);
				} else if (c.objectX == 3285 && c.objectY == 2767) {// frost
																	// dragons
					c.getPA().movePlayer(2979, 3959, 0);
				} else if (c.objectX == 3277 && c.objectY == 2773) {// nomad
					c.getPA().movePlayer(2740, 5098, 0);
				} else if (c.objectX == 3285 && c.objectY == 2773) {// demons
					c.getPA().movePlayer(2272, 4680, 0);
				} else if (c.objectX == 3277 && c.objectY == 2776) {// nex
					c.getPA().movePlayer(2900, 3617, 16);
				} else if (c.objectX == 3417 && c.objectY == 3566) {// mining
					c.getPA().movePlayer(3045, 9780, 0);
				} else if (c.objectX == 3045 && c.objectY == 9782) {// mining
					c.getPA().movePlayer(3423, 3567, 0);
				} else if (c.objectX == 2031 && c.objectY == 4539) {// max guild to skilling
					c.getPA().movePlayer(3444, 3551, 0);	
				} else if (c.objectX == 2033 && c.objectY == 4539) {// max guild to mining
					c.getPA().movePlayer(3053, 9773, 0);
					
						
				}

			}
			c.portalDelay = System.currentTimeMillis();
			break;

		default:
			ScriptManager.callFunc("objectClick1_" + objectType, c, objectType,
					obX, obY);
			break;

		}
	}

	public void secondClickObject(int objectType, int obX, int obY) {
		c.clickObjectType = 0;
		c.turnPlayerTo(obX, obY);
		switch (objectType) {

		// home stalls
		case 2564:
			c.getThieving().stealFromStall(995, 25000 + Misc.random(25000), 75,
					1);

			break;
		case 2563:
			c.getThieving().stealFromStall(995, 50000 + Misc.random(50000),
					115, 25);

			break;
		case 2560:
			c.getThieving().stealFromStall(995, 75000 + Misc.random(75000),
					150, 50);

			break;
		case 2565:
			c.getThieving().stealFromStall(995, 100000 + Misc.random(100000),
					200, 75);

			break;
		case 2562:
			c.getThieving().stealFromStall(995, 125000 + Misc.random(125000),
					275, 95);

			break;

		case 31080:
		case 31081:
		case 31082:
		case 31077:
		case 31078:
		case 31071:
		case 31072:
		case 31073:
		case 31068:
		case 31069:
		case 31070:
		case 31065:
		case 31066:
		case 31086:
		case 31088:
		case 31083:
		case 31085:
		case 14859:
		case 2091:
		case 2090:
		case 2093:
		case 2092:
		case 2095:
		case 2094:
		case 450:
			Mining.startProspecting(c, Mining.forId(objectType));
			break;

		case 11666:
		case 3044:
		case 3994:
		case 2781:
			c.getSmithing().sendSmelting();
			break;

		case 4031:
			c.sendMessage("Nothing interesting happens... You just look at it!");
			break;

		/*
		 * case 2561: case 2564: case 2562: case 2563: case 2560: case 635: case
		 * 4277: case 4278: ThievingManager.setupStallData(c,
		 * ThievingManager.forStall(objectType)); break;
		 */

		case 2213:
		case 14367:
		case 5276:
		case 11758:
		case 10517:
			c.isBanking = true;
			c.getPA().openUpBank();
			break;
		case 2558:
			if (System.currentTimeMillis() - c.lastLockPick < 3000
					|| c.freezeTimer > 0)
				break;
			if (c.getItems().playerHasItem(1523, 1)) {
				c.lastLockPick = System.currentTimeMillis();
				if (Misc.random(10) <= 3) {
					c.sendMessage("You fail to pick the lock.");
					break;
				}
				if (c.objectX == 3044 && c.objectY == 3956) {
					if (c.absX == 3045) {
						c.getPA().walkTo2(-1, 0);
					} else if (c.absX == 3044) {
						c.getPA().walkTo2(1, 0);
					}

				} else if (c.objectX == 3038 && c.objectY == 3956) {
					if (c.absX == 3037) {
						c.getPA().walkTo2(1, 0);
					} else if (c.absX == 3038) {
						c.getPA().walkTo2(-1, 0);
					}
				} else if (c.objectX == 3041 && c.objectY == 3959) {
					if (c.absY == 3960) {
						c.getPA().walkTo2(0, -1);
					} else if (c.absY == 3959) {
						c.getPA().walkTo2(0, 1);
					}
				}
			} else {
				c.sendMessage("I need a lockpick to pick this lock.");
			}
			break;
		default:
			ScriptManager.callFunc("objectClick2_" + objectType, c, objectType,
					obX, obY);
			break;
		}
	}

	public void thirdClickObject(int objectType, int obX, int obY) {
		c.clickObjectType = 0;
		c.sendMessage("Object type: " + objectType);
		switch (objectType) {
		default:
			ScriptManager.callFunc("objectClick3_" + objectType, c, objectType,
					obX, obY);
			break;
		}
	}

	public void firstClickNpc(int npcType) {
		c.clickNpcType = 0;
		c.npcClickIndex = 0;
		c.faceNPC(npcType);
		c.faceUpdate(npcType);
		switch (npcType) {
			
		case 3386:
                        if (c.Culin == true) {
                                c.sendMessage("You have already finished this minigame!");
                        return;
                        } else {
                                c.getDH().sendDialogues(51, npcType);
                        }
                        break;

		case 7601:
			if (c.playerRights > 0 || c.playerDonator >= 1) {
				c.getShops().openShop(62);
			} else {
				c.sendMessage("You need to be a donator or staff member to access this shop.");
			}
			break;

		case 1750:
			c.getShops().openShop(66);
			break;

		case 5447:
			c.getPA().showInterface(8292);
			c.getPA().sendFrame126("Agility Ticket Exchange", 8383);
			break;

		case 1282:
			c.getDH().sendDialogues(20, npcType);
			break;

		case 5571:
			c.getDH().sendDialogues(80, npcType);
			break;

		case 5572:
			c.getDH().sendDialogues(11, npcType);
			break;

		case 561:
			c.getShops().openShop(59);
			break;
			
		case 560:
			c.getShops().openShop(68);
			break;			
			
		case 358:
			c.getShops().openShop(56);
			break;

		case 3092:
			c.getShops().openShop(42);
			break;

		case 651:
			c.getShops().openShop(43);
			break;

		case 216:
			c.getShops().openShop(55);
			break;

		case 2732:
			c.getShops().openShop(44);
			break;

		case 6165:
			c.getShops().openShop(45);
			break;

		case 1289:
			c.getShops().openShop(46);
			break;

		case 550:
			c.getShops().openShop(48);
			break;

		case 5834:
			c.getShops().openShop(49);
			break;

		case 6158:
			c.getShops().openShop(50);
			break;

		case 6160:
			c.getShops().openShop(51);
			break;

		case 4294:
			c.getShops().openShop(52);
			break;

		case 1370:
			c.getShops().openShop(54);
			break;

		case 410:
			c.getShops().openSkillCape();
			break;

		case 3050:
			c.getDH().sendDialogues(3, npcType);
			break;

		case 8274:
			if (c.slayerTask <= 0) {
				c.getDH().sendDialogues(209, npcType);
			} else {
				c.getDH().sendDialogues(211, npcType);
			}
			break;

		case 2291:
		case 2292:

			RugTravel.firstClickNpc(c, npcType);
			break;

		case 962:
			c.getDH().sendDialogues(76, npcType);
			break;

		case 364:
			c.getDH().sendDialogues(99, npcType);
			break;

		case 219:
			c.getShops().openShop(53);
			break;

		case 309:
		case 312:
		case 313:
		case 316:
		case 322:
		case 326:
			Fishing.setupFishing(c, Fishing.forSpot(npcType, true));
			break;

		case 200:
			if (c.getItems().playerHasItem(995, 3000)) {
				JourneyShip.travelToKaramja(c);
			}
			break;

		case 599:
			c.getPA().showInterface(3559);
			c.canChangeAppearance = true;
			break;

		default:
			c.sendMessage("They do not seem to take any interest in talking.");
			// c.getDH().sendDialogues(144, npcType);
			ScriptManager.callFunc("npcClick1_" + npcType, c, npcType);
			if (c.playerRights == 3)
				Misc.println("First Click Npc : " + npcType);
			break;
		}
	}

	public void secondClickNpc(int npcType) {
		c.clickNpcType = 0;
		c.npcClickIndex = 0;
		switch (npcType) {
		case 7601:
			if (c.playerRights > 0 || c.playerDonator >= 1) {
				c.getShops().openShop(67);
			} else {
				c.sendMessage("You need to be a donator or staff member to access this shop.");
			}
			break;
		case 8948:
			c.isBanking = true;
			c.getPA().openUpBank();
			break;
		case 4294:
			c.getShops().openShop(52);
			break;
		case 962:
			if (c.playerRights > 0 || c.playerDonator >= 1) {
				HealingContent.produceHealing(c);
			} else {
				c.sendMessage("You need to be a donator or a staff member to use this ability.");
			}
			break;
		case 3789:
			c.getShops().openShop(60);
			break;
		case 561:
			c.getShops().openShop(59);
			break;
			
			
		case 1282:
			c.getShops().openShop(58);
			break;
		case 1303:
			c.getShops().openShop(53);
			break;
		case 550:
			c.getShops().openShop(48);
			break;
		case 1370:
			c.getShops().openShop(54);
			break;
		case 1:
		case 2:
		case 3:
		case 7:
		case 4:
		case 9:
		case 1714:
		case 1715:
		case 18:
		case 23:
		case 32:
		case 26:
		case 20:
		case 2234:
		case 21:
		case 34:
		case 1307:
		case 1305:
		case 1306:
		case 1311:
		case 1310:
		case 1308:
		case 1314:
			ThievingManager
					.setupNPCData(c, ThievingManager.forNpcData(npcType));
			break;

		case 309:
		case 312:
		case 313:
		case 316:
		case 322:
		case 326:
			Fishing.setupFishing(c, Fishing.forSpot(npcType, true));
			break;

		case 494:
			c.isBanking = true;
			c.getPA().openUpBank();
			break;

		default:
			// c.getDH().sendDialogues(144, npcType);
			ScriptManager.callFunc("npcClick2_" + npcType, c, npcType);
			if (c.playerRights == 3)
				Misc.println("Second Click Npc : " + npcType);
			break;

		}
	}

	public void thirdClickNpc(int npcType) {
		c.clickNpcType = 0;
		c.npcClickIndex = 0;
		switch (npcType) {

		case 8274:
			c.getShops().openShop(57);
			break;

		default:
			// c.getDH().sendDialogues(144, npcType);
			ScriptManager.callFunc("npcClick3_" + npcType, c, npcType);
			if (c.playerRights == 3)
				Misc.println("Third Click NPC : " + npcType);
			break;

		}
	}

}