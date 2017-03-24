package server.model.players.packets;

import java.util.Iterator;

import server.Config;
import server.Server;
import server.model.players.TradeAndDuel;
import server.content.travel.Teleport;
import server.model.items.GameItem;
import server.model.players.Client;
import server.model.players.PacketType;
import server.model.players.Player;
import server.model.players.PlayerHandler;
import server.model.players.combat.magic.CastOnOther;
import server.model.players.skills.WoodcuttingManager;
import server.util.Misc;

/**
 * Clicking most buttons
 **/
public class ClickingButtons implements PacketType {

	@Override
	public void processPacket(final Client c, int packetType, int packetSize) {
		int actionButtonId = Misc.hexToInt(c.getInStream().buffer, 0,
				packetSize);
		if (c.isDead)
			return;
		if (c.playerRights == 3)
			Misc.println(c.playerName + " - actionbutton: " + actionButtonId);
		switch (actionButtonId) {
		case 55095:
			c.getPA().destroyItem(c.destroyItem);
		case 55096:
			c.getPA().closeAllWindows();
			break;
		case 89220:// Close bank
			c.getPA().removeAllWindows();
			c.getPA().closeAllWindows();
			c.isBanking = false;
			c.setBankX(false);
			break;
		case 82020: // BANK ALL, YOU MAY NEED TO CHANGE THE ID !!!
			for (int i = 0; i < c.playerItems.length; i++) {
				c.getItems().bankItem(c.playerItems[i] - 1,
						c.getItems().getItemSlot(c.playerItems[i] - 1),
						c.playerItemsN[i]);
			}
			c.sendMessage("You succesfully bank items in your inventory.");
			break;
		case 49022:
			CastOnOther.teleOtherLocation(c, c.teleotherType, false);
			break;
		case 49024:
			CastOnOther.teleOtherLocation(c, c.teleotherType, true);
			break;
			
			case 28174:
			c.getPA().BossKillLog();
		break;
		
		case 32195:// 1
		case 32196:
			if (c.getItems().playerHasItem(2996, 1)) {
				c.getItems().deleteItem2(2996, 1);
				c.getPA().addSkillXP(1000, Player.playerAgility);
				c.getPA().refreshSkill(Player.playerAgility);
				c.sendMessage("You received 1,000 agility exp.");
			} else {
				c.sendMessage("You need more agility tickets.");
				return;
			}
			break;
		case 32203:// 10
		case 32197:
			if (c.getItems().playerHasItem(2996, 10)) {
				c.getItems().deleteItem2(2996, 10);
				c.getPA().addSkillXP(10000, Player.playerAgility);
				c.getPA().refreshSkill(Player.playerAgility);
				c.sendMessage("You received 10,000 agility exp.");
			} else {
				c.sendMessage("You need more agility tickets.");
				return;
			}
			break;
		case 32204:// 25
		case 32198:
			if (c.getItems().playerHasItem(2996, 25)) {
				c.getItems().deleteItem2(2996, 25);
				c.getPA().addSkillXP(25000, Player.playerAgility);
				c.getPA().refreshSkill(Player.playerAgility);
				c.sendMessage("You received 25,000 agility exp.");
			} else {
				c.sendMessage("You need more agility tickets.");
				return;
			}
			break;
		case 32199:// 100
		case 32205:
			if (c.getItems().playerHasItem(2996, 100)) {
				c.getItems().deleteItem2(2996, 100);
				c.getPA().addSkillXP(100000, Player.playerAgility);
				c.getPA().refreshSkill(Player.playerAgility);
				c.sendMessage("You received 100,000 agility exp.");
			} else {
				c.sendMessage("You need more agility tickets.");
				return;
			}
			break;
		case 32200:// 1000
		case 32206:
			if (c.getItems().playerHasItem(2996, 1000)) {
				c.getItems().deleteItem2(2996, 1000);
				c.getPA().addSkillXP(1000000, Player.playerAgility);
				c.getPA().refreshSkill(Player.playerAgility);
				c.sendMessage("You received 1,000,000 agility exp.");
			} else {
				c.sendMessage("You need more agility tickets.");
				return;
			}
			break;
		case 32192:// toadflex
		case 32190:
		case 32202:// snapdragon
		case 32201:
			c.sendMessage("Not available.");
			break;
		case 32193:// piratehook
		case 32189:
			if (c.getItems().playerHasItem(2996, 800)) {
				c.getItems().deleteItem2(2996, 800);
				c.getItems().addItem(2997, 1);
			} else {
				c.sendMessage("You need more agility tickets.");
				return;
			}
			break;
		case 58074:
		c.getBankPin().close();
		break;

	case 58025:
	case 58026:
	case 58027:
	case 58028:
	case 58029:
	case 58030:
	case 58031:
	case 58032:
	case 58033:
	case 58034:
		c.getBankPin().pinEnter(actionButtonId);
		break;
		case 15147:
			if (c.smeltInterface) {
				c.smeltType = 2349;
				c.smeltAmount = 1;
				c.getSmithing().startSmelting(c.smeltType);
			}
			break;

		case 15151:
			if (c.smeltInterface) {
				c.smeltType = 2351;
				c.smeltAmount = 1;
				c.getSmithing().startSmelting(c.smeltType);
			}
			break;

		case 15159:
			if (c.smeltInterface) {
				c.smeltType = 2353;
				c.smeltAmount = 1;
				c.getSmithing().startSmelting(c.smeltType);
			}
			break;

		case 29017:
			if (c.smeltInterface) {
				c.smeltType = 2359;
				c.smeltAmount = 1;
				c.getSmithing().startSmelting(c.smeltType);
			}
			break;

		case 29022:
			if (c.smeltInterface) {
				c.smeltType = 2361;
				c.smeltAmount = 1;
				c.getSmithing().startSmelting(c.smeltType);
			}
			break;

		case 29026:
			if (c.smeltInterface) {
				c.smeltType = 2363;
				c.smeltAmount = 1;
				c.getSmithing().startSmelting(c.smeltType);
			}
			break;

		case 118098:
			c.getPA().vengMe();
			break;

		case 28166: // items kept on death
			if (!c.isSkulled) {
				c.getItems().resetKeepItems();
				c.getItems().keepItem(0, false);
				c.getItems().keepItem(1, false);
				c.getItems().keepItem(2, false);
				c.getItems().keepItem(3, false);
				c.sendMessage("You can keep three items and a fourth if you use the protect item prayer.");
			} else {
				c.getItems().resetKeepItems();
				c.getItems().keepItem(0, false);
				c.sendMessage("You are skulled and will only keep one item if you use the protect item prayer.");
			}
			c.getItems().sendItemsKept();
			c.getPA().showInterface(6960);
			c.getItems().resetKeepItems();
			break;
		case 28168: // exp lock
			if (c.expLock) {
				c.expLock = false;
				c.sendMessage("EXP lock disabled.");
			} else if (c.expLock == false) {
				c.expLock = true;
				c.sendMessage("EXP lock enabled.");
			}
			break;

		case 33206: // attack
			c.getSI().attackComplex(1);
			c.getSI().selected = 0;
			break;
		case 33209: // strength
			c.getSI().strengthComplex(1);
			c.getSI().selected = 1;
			break;
		case 33212: // Defence
			c.getSI().defenceComplex(1);
			c.getSI().selected = 2;
			break;
		case 33215: // range
			c.getSI().rangedComplex(1);
			c.getSI().selected = 3;
			break;
		case 33218: // prayer
			c.getSI().prayerComplex(1);
			c.getSI().selected = 4;
			break;
		case 33221: // mage
			c.getSI().magicComplex(1);
			c.getSI().selected = 5;
			break;
		case 33224: // runecrafting
			c.getSI().runecraftingComplex(1);
			c.getSI().selected = 6;
			break;
		case 33207: // hp
			c.getSI().hitpointsComplex(1);
			c.getSI().selected = 7;
			break;
		case 33210: // agility
			c.getSI().agilityComplex(1);
			c.getSI().selected = 8;
			break;
		case 33213: // herblore
			c.getSI().herbloreComplex(1);
			c.getSI().selected = 9;
			break;
		case 33216: // theiving
			c.getSI().thievingComplex(1);
			c.getSI().selected = 10;
			break;
		case 33219: // crafting
			c.getSI().craftingComplex(1);
			c.getSI().selected = 11;
			break;
		case 33222: // fletching
			c.getSI().fletchingComplex(1);
			c.getSI().selected = 12;
			break;
		case 47130:// slayer
			c.getSI().slayerComplex(1);
			c.getSI().selected = 13;
			break;
		case 33214: // fishing
			c.getSI().fishingComplex(1);
			c.getSI().selected = 16;
			break;
		case 33217: // cooking
			c.getSI().cookingComplex(1);
			c.getSI().selected = 17;
			break;
		case 33220: // firemaking
			c.getSI().firemakingComplex(1);
			c.getSI().selected = 18;
			break;
		case 33223: // woodcut
			c.getSI().woodcuttingComplex(1);
			c.getSI().selected = 19;
			break;
		case 54104: // farming
			c.getSI().farmingComplex(1);
			c.getSI().selected = 20;
			break;

		case 34142: // tab 1
			c.getSI().menuCompilation(1);
			break;

		case 34119: // tab 2
			c.getSI().menuCompilation(2);
			break;

		case 34120: // tab 3
			c.getSI().menuCompilation(3);
			break;

		case 34123: // tab 4
			c.getSI().menuCompilation(4);
			break;

		case 34133: // tab 5
			c.getSI().menuCompilation(5);
			break;

		case 34136: // tab 6
			c.getSI().menuCompilation(6);
			break;

		case 34139: // tab 7
			c.getSI().menuCompilation(7);
			break;

		case 34155: // tab 8
			c.getSI().menuCompilation(8);
			break;

		case 34158: // tab 9
			c.getSI().menuCompilation(9);
			break;

		case 34161: // tab 10
			c.getSI().menuCompilation(10);
			break;

		case 59199: // tab 11
			c.getSI().menuCompilation(11);
			break;

		case 59202: // tab 12
			c.getSI().menuCompilation(12);
			break;
		case 59203: // tab 13
			c.getSI().menuCompilation(13);
			break;

		case 150:
			if (c.autoRet == 0)
				c.autoRet = 1;
			else
				c.autoRet = 0;
			break;
		  case 9190: // 1st tele option          
								if (c.dialogueAction == 106) {
                                if (c.getItems().playerHasItem(c.diceID, 1)) {
                                        c.getItems().deleteItem(c.diceID,
                                                        c.getItems().getItemSlot(c.diceID), 1);
                                        c.getItems().addItem(15086, 1);
                                        c.sendMessage("You get a six-sided die out of the dice bag.");
                                }
                                c.getPA().closeAllWindows();
						} else if (c.dialogueAction == 201) {
								c.getPA().spellTeleport(3485, 9509, 2);
								c.sendMessage("You teleport to the Kalphite queens lair.");
						} else if (c.dialogueAction == 107) {
                                if (c.getItems().playerHasItem(c.diceID, 1)) {
                                        c.getItems().deleteItem(c.diceID,
                                                        c.getItems().getItemSlot(c.diceID), 1);
                                        c.getItems().addItem(15092, 1);
                                        c.sendMessage("You get a ten-sided die out of the dice bag.");
                                }
                                c.getPA().closeAllWindows();
                        }
                        if (c.dialogueAction == 10) {
                                c.getPA().spellTeleport(2845, 4832, 0);
                                c.dialogueAction = -1;
                        } else if (c.dialogueAction == 11) {
                                c.getPA().spellTeleport(2786, 4839, 0);
                                c.dialogueAction = -1;
                        } else if (c.dialogueAction == 12) {
                                c.getPA().spellTeleport(2398, 4841, 0);
                                c.dialogueAction = -1;
                        }
                        break;
                // mining - 3046,9779,0
                // smithing - 3079,9502,0
 
                case 9191: // 2nd tele option
                          
                         if (c.dialogueAction == 106) {
                                if (c.getItems().playerHasItem(c.diceID, 1)) {
                                        c.getItems().deleteItem(c.diceID,
                                                        c.getItems().getItemSlot(c.diceID), 1);
                                        c.getItems().addItem(15088, 1);
                                        c.sendMessage("You get two six-sided dice out of the dice bag.");
                                }
                                c.getPA().closeAllWindows();
						} else if (c.dialogueAction == 201) {
								c.getPA().spellTeleport(2974, 9515, 1);
								c.sendMessage("You teleport to the Barrelchest boss.");
                        } else if (c.dialogueAction == 107) {
                                if (c.getItems().playerHasItem(c.diceID, 1)) {
                                        c.getItems().deleteItem(c.diceID,
                                                        c.getItems().getItemSlot(c.diceID), 1);
                                        c.getItems().addItem(15094, 1);
                                        c.sendMessage("You get a twelve-sided die out of the dice bag.");
                                }
                                c.getPA().closeAllWindows();
                        }
                        if (c.dialogueAction == 10) {
                                c.getPA().spellTeleport(2796, 4818, 0);
                                c.dialogueAction = -1;
                        } else if (c.dialogueAction == 11) {
                                c.getPA().spellTeleport(2527, 4833, 0);
                                c.dialogueAction = -1;
                        } else if (c.dialogueAction == 12) {
                                c.getPA().spellTeleport(2464, 4834, 0);
                                c.dialogueAction = -1;
                        }
                        break;
                case 9192: // 3rd tele option
					if (c.dialogueAction == 106) {
                                if (c.getItems().playerHasItem(c.diceID, 1)) {
                                        c.getItems().deleteItem(c.diceID,
                                                        c.getItems().getItemSlot(c.diceID), 1);
                                        c.getItems().addItem(15100, 1);
                                        c.sendMessage("You get a four-sided die out of the dice bag.");
                                }
                                c.getPA().closeAllWindows();
						} else if (c.dialogueAction == 201) {
								c.getPA().spellTeleport(3106, 3934, 0);
								c.sendMessage("You teleport to the primal warriors.");
                        } else if (c.dialogueAction == 107) {
                                if (c.getItems().playerHasItem(c.diceID, 1)) {
                                        c.getItems().deleteItem(c.diceID,
                                                        c.getItems().getItemSlot(c.diceID), 1);
                                        c.getItems().addItem(15096, 1);
                                        c.sendMessage("You get a twenty-sided die out of the dice bag.");
                                }
                                c.getPA().closeAllWindows();
                        }
                        if (c.dialogueAction == 10) {
                                c.getPA().spellTeleport(2713, 4836, 0);
                                c.dialogueAction = -1;
                        } else if (c.dialogueAction == 11) {
                                c.getPA().spellTeleport(2162, 4833, 0);
                                c.dialogueAction = -1;
                        } else if (c.dialogueAction == 12) {
                                c.getPA().spellTeleport(2207, 4836, 0);
                                c.dialogueAction = -1;
                        }
                        break;
                // 4th tele option
                case 9193:
                           
                        if (c.dialogueAction == 106) {
                                if (c.getItems().playerHasItem(c.diceID, 1)) {
                                        c.getItems().deleteItem(c.diceID,
                                                        c.getItems().getItemSlot(c.diceID), 1);
                                        c.getItems().addItem(15090, 1);
                                        c.sendMessage("You get an eight-sided die out of the dice bag.");
                                }
                                c.getPA().closeAllWindows();
						} else if (c.dialogueAction == 201) {
							c.getPA().spellTeleport(2912, 3612, 20);
							c.sendMessage("You teleport to rammernaut goodluck you're going to need it.");		
                        } else if (c.dialogueAction == 107) {
                                if (c.getItems().playerHasItem(c.diceID, 1)) {
                                        c.getItems().deleteItem(c.diceID,
                                                        c.getItems().getItemSlot(c.diceID), 1);
                                        c.getItems().addItem(15098, 1);
                                        c.sendMessage("You get the percentile dice out of the dice bag.");
                                }
                                c.getPA().closeAllWindows();
                        }
                        if (c.dialogueAction == 10) {
                                c.getPA().spellTeleport(2660, 4839, 0);
                                c.dialogueAction = -1;
                        } else if (c.dialogueAction == 11) {
                                // c.getPA().spellTeleport(2527, 4833, 0); astrals here
                                c.getRunecrafting().craftRunes(2489);
                                c.dialogueAction = -1;
                        } else if (c.dialogueAction == 12) {
                                // c.getPA().spellTeleport(2464, 4834, 0); bloods here
                                c.getRunecrafting().craftRunes(2489);
                                c.dialogueAction = -1;
                        }
 
                        if (c.dialogueAction == 10) {
                                c.getPA().spellTeleport(2660, 4839, 0);
                                c.dialogueAction = -1;
                        } else if (c.dialogueAction == 11) {
                                // c.getPA().spellTeleport(2527, 4833, 0); astrals here
                                c.getRunecrafting().craftRunes(2489);
                                c.dialogueAction = -1;
                        } else if (c.dialogueAction == 12) {
                                // c.getPA().spellTeleport(2464, 4834, 0); bloods here
                                c.getRunecrafting().craftRunes(2489);
                                c.dialogueAction = -1;
                        }
                        break;
                // 5th tele option
                case 9194:
                        if (c.dialogueAction == 106) {
                                c.getDH().sendDialogues(107, 0);
                                break;
						} else if (c.dialogueAction == 201) {
								c.getDH().sendDialogues(200, 0);			
                        } else if (c.dialogueAction == 107) {
                                c.getDH().sendDialogues(106, 0);
                                break;
                        }
                        if (c.dialogueAction == 10 || c.dialogueAction == 11) {
                                c.dialogueId++;
                                c.getDH().sendDialogues(c.dialogueId, 0);
                        } else if (c.dialogueAction == 12) {
                                c.dialogueId = 17;
                                c.getDH().sendDialogues(c.dialogueId, 0);
                        }
                        break;
		case 71074:
			if (c.clanId >= 0) {
				if (Server.clanChat.clans[c.clanId].owner
						.equalsIgnoreCase(c.playerName)) {
					Server.clanChat
							.sendLootShareMessage(
									c.clanId,
									"Lootshare has been toggled to "
											+ (!Server.clanChat.clans[c.clanId].lootShare ? "on"
													: "off")
											+ " by the clan leader.");
					Server.clanChat.clans[c.clanId].lootShare = !Server.clanChat.clans[c.clanId].lootShare;
				} else
					c.sendMessage("Only the owner of the clan has the power to do that.");
			}
			break;
		case 34185:
		case 34184:
		case 34183:
		case 34182:
		case 34189:
		case 34188:
		case 34187:
		case 34186:
		case 34193:
		case 34192:
		case 34191:
		case 34190:
			if (c.craftingLeather)
				c.getCrafting().handleCraftingClick(actionButtonId);
			if (c.getFletching().fletching)
				c.getFletching().handleFletchingClick(actionButtonId);
			break;

		case 58253:
			// c.getPA().showInterface(15106);
			c.getItems().writeBonus();
			break;

		case 59004:
			c.getPA().removeAllWindows();
			break;

		case 70212:
			if (c.clanId > -1)
				Server.clanChat.leaveClan(c.playerId, c.clanId);
			else
				c.sendMessage("You are not in a clan.");
			break;
		case 62137:
			if (c.clanId >= 0) {
				c.sendMessage("You are already in a clan.");
				break;
			}
			if (c.getOutStream() != null) {
				c.getOutStream().createFrame(187);
				c.flushOutStream();
			}
			break;

		case 1093:
		case 1094:
		case 1097:
			c.onAuto = true;
			if (c.autocastId > 0) {
				c.getPA().resetAutoCast();
			} else {
				if (c.playerMagicBook == 1) {
					if (c.playerEquipment[Player.playerWeapon] == 4675)
						c.setSidebarInterface(0, 1689);
					else
						c.sendMessage("You can't autocast ancients without an ancient staff.");
				} else if (c.playerMagicBook == 0) {
					if (c.playerEquipment[Player.playerWeapon] == 4170) {
						c.setSidebarInterface(0, 12050);
					} else {
						c.setSidebarInterface(0, 1829);
					}
				}

			}
			break;

		case 9178:
			if (c.usingGlory)
				c.getPA().startTeleport(Config.EDGEVILLE_X, Config.EDGEVILLE_Y,
						0, "modern");
			if (c.dialogueAction == 2)
				c.getPA().startTeleport(3428, 3538, 0, "modern");
			if (c.dialogueAction == 3)
				c.getPA().startTeleport(Config.EDGEVILLE_X, Config.EDGEVILLE_Y,
						0, "modern");
			 if (c.dialogueAction == 120) {
				c.getPA().spellTeleport(2583, 9449, 0);
				c.sendMessage("You have teleported to the Low Level Dungeon.");
			} else if (c.dialogueAction == 121) {
				c.getPA().spellTeleport(2869, 9836, 0);
                c.sendMessage("You have teleported to Range & Mage Training Area."); 
			} else if (c.dialogueAction == 108) {
				Server.barbDefence.enter(c);
				c.sendMessage("You have teleported to the Barbarian Assault minigame.");
			} else if (c.dialogueAction == 109) {
				c.getPA().enterCaves();
				c.sendMessage("You have teleported to Jad Goodluck.");
				c.getPA().closeAllWindows();
			} else if (c.dialogueAction == 200) {
				c.getPA().spellTeleport(3281, 2768, 0);
				c.sendMessage("You teleport to the Boss portal room.");
			} else if (c.dialogueAction == 202) {
				c.getPA().spellTeleport(2470, 3436, 0);
				c.sendMessage("You teleport to the Agility area.");
			} else if (c.dialogueAction == 203) {
				c.getPA().spellTeleport(2608, 4775, 0);
				c.sendMessage("You teleport to the Woodcutting area.");
			} else if (c.dialogueAction == 204) {
				c.getPA().spellTeleport(3088, 3517, 0);
				c.sendMessage("You teleport to the Single PvP Area");
			}
			switch (c.dialogueAction) {
			case 9:
				if (c.getPA().hasWhipAndPts()) {
					c.getItems().addItem(15441, 1);
				}
				break;
			case 10:
				if (c.getPA().hasDbowAndPts()) {
					c.getItems().addItem(15701, 1);
				}
				break;
			case 13:
				c.getPA().movePlayer(2900, 3617, 0);
				c.getPA().removeAllWindows();
				break;
			}

			break;

		case 9179:
			if (c.usingGlory)
				c.getPA().startTeleport(Config.AL_KHARID_X, Config.AL_KHARID_Y,
						0, "modern");
			if (c.dialogueAction == 120) {
                c.getPA().spellTeleport(3004, 9548, 0);
				c.sendMessage("You have teleported to the Mid Level Dungeon.");
            } else if (c.dialogueAction == 121) {
                 c.getPA().spellTeleport(3207, 9379, 0);
				c.sendMessage("You have teleported to the Slayer Dungeon."); 
			} else if (c.dialogueAction == 108) {
				c.getPA().spellTeleport(2658, 2649, 0);
				c.sendMessage("You have teleported to Pest Control.");
			} else if (c.dialogueAction == 109) {
				c.getPA().spellTeleport(2559, 4945, 0);
				c.sendMessage("You have teleported to Spirit Warriors Minigame.");
			} else if (c.dialogueAction == 200) {
				c.getPA().spellTeleport(3158, 9573, 0);
				c.sendMessage("You have teleported to the Revenant dungeon.");
			} else if (c.dialogueAction == 202) {
				c.getPA().spellTeleport(3442, 3554, 0);
				c.sendMessage("You teleport to the Runecrafting skill master.");
			} else if (c.dialogueAction == 203) {
				c.getPA().spellTeleport(2817, 3460, 0);
				c.sendMessage("You teleport to the Farming area.");
			} else if (c.dialogueAction == 204) {
				c.getPA().spellTeleport(3028, 3693, 0);
				c.sendMessage("You teleport to the Multi PvP Area");
			}
			switch (c.dialogueAction) {
			case 9:
				if (c.getPA().hasWhipAndPts()) {
					c.getItems().addItem(15442, 1);
				}
				break;
			case 10:
				if (c.getPA().hasDbowAndPts()) {
					c.getItems().addItem(15702, 1);
				}
				break;
			case 13:
				c.getPA().movePlayer(2900, 3617, 4);
				c.getPA().removeAllWindows();
				break;
			}

			break;

		case 9180:
			if (c.usingGlory)
				c.getPA().startTeleport(Config.KARAMJA_X, Config.KARAMJA_Y, 0,
						"modern");
			if (c.dialogueAction == 120) { 
                c.getPA().spellTeleport(2336, 9798, 0);
				c.sendMessage("You have teleported to the High Level Dungeon.");
			} else if (c.dialogueAction == 121) {
				c.getDH().sendDialogues(190, 0);
			} else if (c.dialogueAction == 108) {
				c.getPA().spellTeleport(3565, 3308, 0);
				c.sendMessage("You teleport to the barrows minigame.");
			} else if (c.dialogueAction == 109) {
				c.getPA().spellTeleport(3186, 9758, 0);
				c.sendMessage("You teleport to the champions minigame.");	
			} else if (c.dialogueAction == 200) {
				c.getPA().spellTeleport(3410, 3570, 0);
				c.sendMessage("You teleport to the godwars selction portal.");
			} else if (c.dialogueAction == 202) {
				c.getPA().spellTeleport(2590, 3415, 0);
				c.sendMessage("You teleport to the Fishing area.");
			} else if (c.dialogueAction == 203) {
				c.getPA().spellTeleport(3045, 9780, 0);
				c.sendMessage("You teleport to the mining area");
			} else if (c.dialogueAction == 204) {
					c.getPA().spellTeleport(2717, 9803, 0);
					c.sendMessage("You teleport to Fun PK");
			}
			switch (c.dialogueAction) {
			case 9:
				if (c.getPA().hasWhipAndPts()) {
					c.getItems().addItem(15443, 1);
				}
				break;
			case 10:
				if (c.getPA().hasDbowAndPts()) {
					c.getItems().addItem(15703, 1);
				}
				break;
			case 13:
				c.getPA().movePlayer(2900, 3617, 8);
				c.getPA().removeAllWindows();
				break;
			}

			break;

		case 9181:
			if (c.usingGlory)
				c.getPA().startTeleport(Config.MAGEBANK_X, Config.MAGEBANK_Y,
						0, "modern");
			if (c.dialogueAction == 120) {
				c.getDH().sendDialogues(191, 0);
			} else if (c.dialogueAction == 108) {
				c.getDH().sendDialogues(109, 0);
			} else if (c.dialogueAction == 109) {
				c.getDH().sendDialogues(108, 0);
			} else if (c.dialogueAction == 200) {
				c.getDH().sendDialogues(201, 0);
			} else if (c.dialogueAction == 202) {
				c.getDH().sendDialogues(203, 0);
			} else if (c.dialogueAction == 203) {
				c.getDH().sendDialogues(202, 0);
			}
			switch (c.dialogueAction) {
			case 9:
				if (c.getPA().hasWhipAndPts()) {
					c.getItems().addItem(15444, 1);
				}
				break;
			case 10:
				if (c.getPA().hasDbowAndPts()) {
					c.getItems().addItem(15704, 1);
				}
				break;
			case 13:
				c.getPA().movePlayer(2900, 3617, 12);
				c.getPA().removeAllWindows();
				break;
			}

			break;

		case 9157: // first option dialogue
			switch (c.dialogueAction) {
				case 101://RFD
						if (c.Culin == true){
						c.getPA().closeAllWindows();
						c.sendMessage("You have already completed the RFD mini-game.");
						} else 
                        c.getPA().enterRFD();
                        c.talkingNpc = -1;
                        c.getPA().removeAllWindows();
                        for(int p = 0; p < c.PRAYER.length; p++) { 
                                c.prayerActive[p] = false;
                                c.getPA().sendFrame36(c.PRAYER_GLOW[p], 0);     
                        }
                break;
			case 2:
				c.getDH().sendDialogues(9, 0);
				c.getItems().deleteItem(2842, 1);
				c.enterChallenge = true;
				break;
			case 3:
				c.getPA().fixAllBarrows();
				c.getPA().removeAllWindows();
				break;
			case 4:
				c.getDH().sendDialogues(22, 0);
				break;
			case 5:
				c.getSlayer().giveTask();
				c.getPA().removeAllWindows();
				break;
			case 6:
				c.getSlayer().giveTask2();
				c.getPA().removeAllWindows();
				break;
			case 7:
				if (c.barrowsKillCount < 5) {
					c.sendMessage("You haven't killed all the barrows brothers yet.");
					c.getPA().removeAllWindows();
					return;
				}
				// int r = Misc.random(4);
				int r = 4;
				switch (r) {
				case 0:
					c.getPA().movePlayer(3534, 9677, 0);
					c.getPA().removeAllWindows();
					break;

				case 1:
					c.getPA().movePlayer(3534, 9712, 0);
					c.getPA().removeAllWindows();
					break;

				case 2:
					c.getPA().movePlayer(3568, 9712, 0);
					c.getPA().removeAllWindows();
					break;

				case 3:
					c.getPA().movePlayer(3568, 9677, 0);
					c.getPA().removeAllWindows();
					break;

				case 4:
					c.getPA().movePlayer(3552, 9691, 0);
					c.getPA().removeAllWindows();
					break;
				}
				break;
			case 8:
				c.getDH().sendDialogues(23, 0);
				break;
			case 121: // pvp exchange
				if (c.pvpKills == 0) {
					c.getShops().openShop(1);
				} else if (c.pvpKills >= 1 && c.pvpKills < 2) {
					c.getShops().openShop(2);
				} else if (c.pvpKills >= 2 && c.pvpKills < 3) {
					c.getShops().openShop(3);
				} else if (c.pvpKills >= 3 && c.pvpKills < 4) {
					c.getShops().openShop(4);
				} else if (c.pvpKills >= 4 && c.pvpKills < 5) {
					c.getShops().openShop(5);
				} else if (c.pvpKills >= 5 && c.pvpKills < 6) {
					c.getShops().openShop(6);
				} else if (c.pvpKills >= 6 && c.pvpKills < 7) {
					c.getShops().openShop(7);
				} else if (c.pvpKills >= 7 && c.pvpKills < 8) {
					c.getShops().openShop(8);
				} else if (c.pvpKills >= 8 && c.pvpKills < 9) {
					c.getShops().openShop(9);
				} else if (c.pvpKills >= 9 && c.pvpKills < 10) {
					c.getShops().openShop(10);
				} else if (c.pvpKills >= 10 && c.pvpKills < 11) {
					c.getShops().openShop(11);
				} else if (c.pvpKills >= 11 && c.pvpKills < 12) {
					c.getShops().openShop(12);
				} else if (c.pvpKills >= 12 && c.pvpKills < 13) {
					c.getShops().openShop(13);
				} else if (c.pvpKills >= 13 && c.pvpKills < 14) {
					c.getShops().openShop(14);
				} else if (c.pvpKills >= 14 && c.pvpKills < 15) {
					c.getShops().openShop(15);
				} else if (c.pvpKills >= 15 && c.pvpKills < 16) {
					c.getShops().openShop(16);
				} else if (c.pvpKills >= 16 && c.pvpKills < 17) {
					c.getShops().openShop(17);
				} else if (c.pvpKills >= 17 && c.pvpKills < 18) {
					c.getShops().openShop(18);
				} else if (c.pvpKills >= 18 && c.pvpKills < 19) {
					c.getShops().openShop(19);
				} else if (c.pvpKills >= 19 && c.pvpKills < 20) {
					c.getShops().openShop(20);
				} else if (c.pvpKills >= 20 && c.pvpKills < 21) {
					c.getShops().openShop(21);
				} else if (c.pvpKills >= 21 && c.pvpKills < 22) {
					c.getShops().openShop(22);
				} else if (c.pvpKills >= 22 && c.pvpKills < 23) {
					c.getShops().openShop(23);
				} else if (c.pvpKills >= 23 && c.pvpKills < 24) {
					c.getShops().openShop(24);
				} else if (c.pvpKills >= 24 && c.pvpKills < 25) {
					c.getShops().openShop(25);
				} else if (c.pvpKills >= 25 && c.pvpKills < 26) {
					c.getShops().openShop(26);
				} else if (c.pvpKills >= 26 && c.pvpKills < 27) {
					c.getShops().openShop(27);
				} else if (c.pvpKills >= 27 && c.pvpKills < 28) {
					c.getShops().openShop(28);
				} else if (c.pvpKills >= 28 && c.pvpKills < 29) {
					c.getShops().openShop(29);
				} else if (c.pvpKills >= 29 && c.pvpKills < 30) {
					c.getShops().openShop(30);
				} else if (c.pvpKills >= 30 && c.pvpKills < 31) {
					c.getShops().openShop(31);
				} else if (c.pvpKills >= 31 && c.pvpKills < 32) {
					c.getShops().openShop(32);
				} else if (c.pvpKills >= 32 && c.pvpKills < 33) {
					c.getShops().openShop(33);
				} else if (c.pvpKills >= 33 && c.pvpKills < 34) {
					c.getShops().openShop(34);
				} else if (c.pvpKills >= 34 && c.pvpKills < 35) {
					c.getShops().openShop(35);
				} else if (c.pvpKills >= 35 && c.pvpKills < 36) {
					c.getShops().openShop(36);
				} else if (c.pvpKills >= 36 && c.pvpKills < 37) {
					c.getShops().openShop(37);
				} else if (c.pvpKills >= 37 && c.pvpKills < 38) {
					c.getShops().openShop(38);
				} else if (c.pvpKills >= 38 && c.pvpKills < 39) {
					c.getShops().openShop(39);
				} else if (c.pvpKills >= 39 && c.pvpKills < 40) {
					c.getShops().openShop(40);
				} else if (c.pvpKills >= 40) {
					c.getShops().openShop(41);
					if (c.pvpKills >= 40 && c.pvpKills < 100) {
						c.sendMessage("Now that you have unlocked all the items you can try for 100 kills for half off prices.");
					}
				}
			}

			break;

		case 9167: // first dialogue click
			switch (c.dialogueAction) {
			case 1:
				c.getDH().sendDialogues(5, 0);
				break;
			}

			break;
		case 9168: // second dialogue click
			switch (c.dialogueAction) {
			case 1:
				c.getDH().sendDialogues(6, 0);
				break;
			case 2:
				c.getPA().removeAllWindows();
				break;
			case 108:
				WoodcuttingManager.fixAxe(c);
				break;
			}

			break;
		case 9169: // third dialogue click
			switch (c.dialogueAction) {
			case 1:
				c.getPA().removeAllWindows();
				break;
			}

			break;

		case 9158: // fourth dialogue click
			if (c.dialogueAction == 8) {
				c.getDH().sendDialogues(24, 0);
			} else {
				c.getPA().removeAllWindows();
			}

			break;

		/** Specials **/
		case 29188:
			c.specBarId = 7636; // the special attack text - sendframe126(S P E
			// C I A L A T T A C K, c.specBarId);
			c.usingSpecial = !c.usingSpecial;
			c.getItems().updateSpecialBar();
			break;

		case 29163:
			c.specBarId = 7611;
			c.usingSpecial = !c.usingSpecial;
			c.getItems().updateSpecialBar();
			break;

		case 33033:
			c.specBarId = 8505;
			c.usingSpecial = !c.usingSpecial;
			c.getItems().updateSpecialBar();
			break;

		case 29038:
			if (c.playerEquipment[Player.playerWeapon] == 13902
					|| c.playerEquipment[Player.playerWeapon] == 13926
					|| c.playerEquipment[Player.playerWeapon] == 13928) {
				c.specBarId = 7486;
				c.usingSpecial = !c.usingSpecial;
				c.getItems().updateSpecialBar();
			} else {
				c.specBarId = 7486;
				c.getItems().updateSpecialBar();
				c.getCombat().handleGmaul();
			}
			break;

		case 29063:
			if (c.getCombat().checkSpecAmount(
					c.playerEquipment[Player.playerWeapon])) {
				c.gfx0(246);
				c.forcedChat("Raarrrrrgggggghhhhhhh!");
				c.startAnimation(1056);
				c.playerLevel[2] = c.getLevelForXP(c.playerXP[2])
						+ (c.getLevelForXP(c.playerXP[2]) * 15 / 100);
				c.getPA().refreshSkill(2);
				c.getItems().updateSpecialBar();
			} else {
				c.sendMessage("You don't have the required special energy to use this attack.");
			}
			break;

		case 48023:
			c.specBarId = 12335;
			c.usingSpecial = !c.usingSpecial;
			c.getItems().updateSpecialBar();
			break;

		case 29138:
			if (c.playerEquipment[Player.playerWeapon] == 15486) {
				if (c.getCombat().checkSpecAmount(
						c.playerEquipment[Player.playerWeapon])) {
					c.gfx0(2320);
					c.SolProtect = 100;
					c.startAnimation(10518);
					c.getItems().updateSpecialBar();
					c.usingSpecial = !c.usingSpecial;
					c.getPA().sendFrame126("@bla@S P E C I A L  A T T A C K",
							7562);
				} else {
					c.sendMessage("You don't have the required special energy to use this attack.");
				}
			}
			c.specBarId = 7586;
			c.usingSpecial = !c.usingSpecial;
			c.getItems().updateSpecialBar();
			break;

		case 29113:
			c.specBarId = 7561;
			c.usingSpecial = !c.usingSpecial;
			c.getItems().updateSpecialBar();
			break;

		case 29238:
			c.specBarId = 7686;
			c.usingSpecial = !c.usingSpecial;
			c.getItems().updateSpecialBar();
			break;

		case 30108:
			c.specBarId = 7812;
			c.usingSpecial = !c.usingSpecial;
			c.getItems().updateSpecialBar();
			break;

		/** Dueling **/
		case 26065: // no forfeit
		case 26040:
			c.duelSlot = -1;
			c.getTradeAndDuel().selectRule(0);
			break;

		case 26066: // no movement
		case 26048:
			c.duelSlot = -1;
			c.getTradeAndDuel().selectRule(1);
			break;

		case 26069: // no range
		case 26042:
			c.duelSlot = -1;
			c.getTradeAndDuel().selectRule(2);
			break;

		case 26070: // no melee
		case 26043:
			c.duelSlot = -1;
			c.getTradeAndDuel().selectRule(3);
			break;

		case 26071: // no mage
		case 26041:
			c.duelSlot = -1;
			c.getTradeAndDuel().selectRule(4);
			break;

		case 26072: // no drinks
		case 26045:
			c.duelSlot = -1;
			c.getTradeAndDuel().selectRule(5);
			break;

		case 26073: // no food
		case 26046:
			c.duelSlot = -1;
			c.getTradeAndDuel().selectRule(6);
			break;

		case 26074: // no prayer
		case 26047:
			c.duelSlot = -1;
			c.getTradeAndDuel().selectRule(7);
			break;

		case 26076: // obsticals
		case 26075:
			c.duelSlot = -1;
			c.getTradeAndDuel().selectRule(8);
			break;

		case 2158: // fun weapons
		case 2157:
			c.duelSlot = -1;
			c.getTradeAndDuel().selectRule(9);
			break;

		case 30136: // sp attack
		case 30137:
			c.duelSlot = -1;
			c.getTradeAndDuel().selectRule(10);
			break;

		case 53245: // no helm
			c.duelSlot = 0;
			c.getTradeAndDuel().selectRule(11);
			break;

		case 53246: // no cape
			c.duelSlot = 1;
			c.getTradeAndDuel().selectRule(12);
			break;

		case 53247: // no ammy
			c.duelSlot = 2;
			c.getTradeAndDuel().selectRule(13);
			break;

		case 53249: // no weapon.
			c.duelSlot = 3;
			c.getTradeAndDuel().selectRule(14);
			break;

		case 53250: // no body
			c.duelSlot = 4;
			c.getTradeAndDuel().selectRule(15);
			break;

		case 53251: // no shield
			c.duelSlot = 5;
			c.getTradeAndDuel().selectRule(16);
			break;

		case 53252: // no legs
			c.duelSlot = 7;
			c.getTradeAndDuel().selectRule(17);
			break;

		case 53255: // no gloves
			c.duelSlot = 9;
			c.getTradeAndDuel().selectRule(18);
			break;

		case 53254: // no boots
			c.duelSlot = 10;
			c.getTradeAndDuel().selectRule(19);
			break;

		case 53253: // no rings
			c.duelSlot = 12;
			c.getTradeAndDuel().selectRule(20);
			break;

		case 53248: // no arrows
			c.duelSlot = 13;
			c.getTradeAndDuel().selectRule(21);
			break;

		case 26018:
			Client o = (Client) PlayerHandler.players[c.duelingWith];
			if (o == null) {
				c.getTradeAndDuel().declineDuel();
				return;
			}

			if (c.duelRule[2] && c.duelRule[3] && c.duelRule[4]) {
				c.sendMessage("You won't be able to attack the player with the rules you have set.");
				break;
			}
			c.duelStatus = 2;
			if (c.duelStatus == 2) {
				c.getPA().sendFrame126("Waiting for other player...", 6684);
				o.getPA().sendFrame126("Other player has accepted.", 6684);
			}
			if (o.duelStatus == 2) {
				o.getPA().sendFrame126("Waiting for other player...", 6684);
				c.getPA().sendFrame126("Other player has accepted.", 6684);
			}

			if (c.duelStatus == 2 && o.duelStatus == 2) {
				c.canOffer = false;
				o.canOffer = false;
				c.duelStatus = 3;
				o.duelStatus = 3;
				c.getTradeAndDuel().confirmDuel();
				o.getTradeAndDuel().confirmDuel();
			}
			break;

		case 25120:
			if (c.duelStatus == 5) {
				break;
			}
			Client o1 = (Client) PlayerHandler.players[c.duelingWith];
			if (o1 == null) {
				c.getTradeAndDuel().declineDuel();
				return;
			}

			c.duelStatus = 4;
			if (o1.duelStatus == 4 && c.duelStatus == 4) {
				c.getTradeAndDuel().startDuel();
				o1.getTradeAndDuel().startDuel();
				o1.duelCount = 4;
				c.duelCount = 4;
				c.duelDelay = System.currentTimeMillis();
				o1.duelDelay = System.currentTimeMillis();
			} else {
				c.getPA().sendFrame126("Waiting for other player...", 6571);
				o1.getPA().sendFrame126("Other player has accepted", 6571);
			}
			break;

		case 4169: // god spell charge
			c.usingMagic = true;
			if (!c.getCombat().checkMagicReqs(48)) {
				break;
			}

			if (System.currentTimeMillis() - c.godSpellDelay < Config.GOD_SPELL_CHARGE) {
				c.sendMessage("You still feel the charge in your body!");
				break;
			}
			c.godSpellDelay = System.currentTimeMillis();
			c.sendMessage("You feel charged with a magical power!");
			c.gfx100(Player.MAGIC_SPELLS[48][3]);
			c.startAnimation(Player.MAGIC_SPELLS[48][2]);
			c.usingMagic = false;
			break;

		case 9154:
			c.logout();
			break;

		case 82016:
			c.takeAsNote = c.takeAsNote ? false : true;
		break;

		// home teleports
		case 4171:
		case 50056:
		case 117048:
			Teleport.home(c, actionButtonId);
			break;

		
		case 6005:
		case 29031:
			Teleport.teleport(c, actionButtonId);
			break;

		/*
		 * case 50253: if (c.quest_4 == 4) {
		 * c.getPA().startTeleport(Config.KHARYRLL_X, Config.KHARYRLL_Y, 0,
		 * "ancient");
		 * 
		 * } else { c
		 * .sendMessage("You need to have completed Helpless Priest to do this."
		 * ); } break;
		 */

		case 4140:
		case 50235:
				c.getDH().sendDialogues(190, 0);
			break;

		case 4143:
		case 50245:
				c.getDH().sendDialogues(108, 0);
		break;

		case 6004:
		case 51013:
			c.getDH().sendDialogues(202, 0);
			break;
		
		case 4146:
		case 50253:
			c.getDH().sendDialogues(200, 0);
		break;
		
		case 4150:
		case 51005:
			c.getDH().sendDialogues(204, 0);
		break;
		

		case 51023:

			c.getPA().startTeleport(Config.WATCHTOWER_X, Config.WATCHTOWER_Y,
					0, "ancient");
			break;

		case 51031:
			c.getPA().startTeleport(Config.TROLLHEIM_X, Config.TROLLHEIM_Y, 0,
					"ancient");
			break;

		case 72038:
		case 51039:
			// c.getPA().startTeleport(Config.TROLLHEIM_X, Config.TROLLHEIM_Y,
			// 0, "modern");
			// c.teleAction = 8;
			break;

		case 9125: // Accurate
		case 22230:// punch
		case 48010:// flick (whip)
		case 14218:// pound (mace)
		case 33020:// jab (halberd)
		case 21200: // spike (pickaxe)
		case 6168: // chop (axe)
		case 8234: // stab (dagger)
		case 17102: // accurate (darts)
		case 6236: // accurate (long bow)
		case 1080: // bash (staff)
		case 6221: // range accurate
		case 30088: // claws (chop)
		case 1177: // hammer (pound)
			c.fightMode = 0;
			if (c.autocasting) {
				c.getPA().resetAutoCast();
			}
			break;

		// Defence
		case 9126: // Defensive
		case 22228: // block (unarmed)
		case 48008: // deflect (whip)
		case 1175: // block (hammer)
		case 21201: // block (pickaxe)
		case 14219: // block (mace)
		case 1078: // focus - block (staff)
		case 33018: // fend (hally)
		case 6169: // block (axe)
		case 8235: // block (dagger)
		case 18078: // block (spear)
		case 30089: // block (claws)
			c.fightMode = 1;
			if (c.autocasting) {
				c.getPA().resetAutoCast();
			}
			break;
		// All
		case 9127: // Controlled
		case 14220: // Spike (mace)
		case 6234: // longrange (long bow)
		case 6219: // longrange
		case 18077: // lunge (spear)
		case 18080: // swipe (spear)
		case 18079: // pound (spear)
		case 17100: // longrange (darts)
			c.fightMode = 3;
			if (c.autocasting) {
				c.getPA().resetAutoCast();
			}
			break;
		// Strength
		case 9128: // Aggressive
		case 14221: // Pummel(mace)
		case 33019: // Swipe(Halberd)
		case 21203: // impale (pickaxe)
		case 21202: // smash (pickaxe)
		case 6171: // hack (axe)
		case 6170: // smash (axe)
		case 6220: // range rapid
		case 8236: // slash (dagger)
		case 8237: // lunge (dagger)
		case 30090: // claws (lunge)
		case 30091: // claws (Slash)
		case 22229:
		case 1176: // stat hammer
		case 1079: // pound (staff)
		case 6235: // rapid (long bow)
		case 17101: // repid (darts)
			c.fightMode = 2;
			if (c.autocasting) {
				c.getPA().resetAutoCast();
			}
			break;

		/** Prayers **/
		case 87231: // thick skin
			c.getCurse().activateCurse(0);
			return;
		case 87233: // burst of str
			c.getCurse().activateCurse(1);
			break;
		case 87235: // charity of thought
			c.getCurse().activateCurse(2);
			break;
		case 87237: // range
			c.getCurse().activateCurse(3);
			break;
		case 87239: // mage
			c.getCurse().activateCurse(4);
			break;
		case 87241: // berserker
			c.getCurse().activateCurse(5);
			break;
		case 87243: // spirit
			c.getCurse().activateCurse(6);
			break;
		case 87245: // defmage
			c.getCurse().activateCurse(7);
			break;
		case 87247: // defrng
			c.getCurse().activateCurse(8);
			break;
		case 87249:// defmel
			c.getCurse().activateCurse(9);
			break;

		case 87251: // leeech attack
			c.getCurse().activateCurse(10);
			break;
		case 87253: // leech range
			c.getCurse().activateCurse(11);
			break;
		case 87255: // leech magic
			c.getCurse().activateCurse(12);
			break;
		case 88001: // leech def
			c.getCurse().activateCurse(13);
			break;
		case 88003: // leech str
			c.getCurse().activateCurse(14);
			break;
		case 88005: // leech run
			c.getPA().sendFrame36(625, 0);
			break;
		case 88007: // leech spec
			c.getCurse().activateCurse(16);
			break;
		case 88009: // wrath
			c.getCurse().activateCurse(17);
			break;
		case 88011: // soulsplit
			c.getCurse().activateCurse(18);
			break;
		case 88013: // turmoil
			c.getCurse().activateCurse(19);
			break;
		/** End of curse prayers **/

		/** Prayers **/
		case 21233: // thick skin
			c.getCombat().activatePrayer(0);
			break;
		case 21234: // burst of str
			c.getCombat().activatePrayer(1);
			break;
		case 21235: // charity of thought
			c.getCombat().activatePrayer(2);
			break;
		case 70080: // range
			c.getCombat().activatePrayer(3);
			break;
		case 70082: // mage
			c.getCombat().activatePrayer(4);
			break;
		case 21236: // rockskin
			c.getCombat().activatePrayer(5);
			break;
		case 21237: // super human
			c.getCombat().activatePrayer(6);
			break;
		case 21238: // improved reflexes
			c.getCombat().activatePrayer(7);
			break;
		case 21239: // hawk eye
			c.getCombat().activatePrayer(8);
			break;
		case 21240:
			c.getCombat().activatePrayer(9);
			break;
		case 21241: // protect Item
			c.getCombat().activatePrayer(10);
			break;
		case 70084: // 26 range
			c.getCombat().activatePrayer(11);
			break;
		case 70086: // 27 mage
			c.getCombat().activatePrayer(12);
			break;
		case 21242: // steel skin
			c.getCombat().activatePrayer(13);
			break;
		case 21243: // ultimate str
			c.getCombat().activatePrayer(14);
			break;
		case 21244: // incredible reflex
			c.getCombat().activatePrayer(15);
			break;
		case 21245: // protect from magic
			c.getCombat().activatePrayer(16);
			break;
		case 21246: // protect from range
			c.getCombat().activatePrayer(17);
			break;
		case 21247: // protect from melee
			c.getCombat().activatePrayer(18);
			break;
		case 70088: // 44 range
			c.getCombat().activatePrayer(19);
			break;
		case 70090: // 45 mystic
			c.getCombat().activatePrayer(20);
			break;
		case 2171: // retrui
			c.getCombat().activatePrayer(21);
			break;
		case 2172: // redem
			c.getCombat().activatePrayer(22);
			break;
		case 2173: // smite
			c.getCombat().activatePrayer(23);
			break;
		case 70092: // chiv
			c.getCombat().activatePrayer(24);
			break;
		case 70094: // piety
			c.getCombat().activatePrayer(25);
			break;

		case 13092:
			if (System.currentTimeMillis() - c.lastButton < 400) {
				c.lastButton = System.currentTimeMillis();
				break;
			} else {
				c.lastButton = System.currentTimeMillis();
			}
			Client ot = (Client) PlayerHandler.players[c.tradeWith];
			if (ot != null && !ot.disconnected) {
				c.getPA().sendFrame126("Waiting for other player...", 3431);
				ot.getPA().sendFrame126("Other player has accepted", 3431);
				c.goodTrade = true;
				ot.goodTrade = true;
				Iterator var11 = c.getTradeAndDuel().offeredItems.iterator();

				while (var11.hasNext()) {
					GameItem var13 = (GameItem) var11.next();
					if (var13.id > 0) {
						if (ot.getItems().freeSlots() < c.getTradeAndDuel().offeredItems
								.size()) {
							c.sendMessage(ot.playerName
									+ " only has "
									+ ot.getItems().freeSlots()
									+ " free slots, please remove "
									+ (c.getTradeAndDuel().offeredItems.size() - ot
											.getItems().freeSlots())
									+ " items.");
							ot.sendMessage(c.playerName
									+ " has to remove "
									+ (c.getTradeAndDuel().offeredItems.size() - ot
											.getItems().freeSlots())
									+ " items or you could offer them "
									+ (c.getTradeAndDuel().offeredItems.size() - ot
											.getItems().freeSlots())
									+ " items.");
							c.goodTrade = false;
							ot.goodTrade = false;
							c.getPA().sendFrame126(
									"Not enough inventory space...", 3431);
							ot.getPA().sendFrame126(
									"Not enough inventory space...", 3431);
							break;
						}

						c.getPA().sendFrame126("Waiting for other player...",
								3431);
						ot.getPA().sendFrame126("Other player has accepted",
								3431);
						c.goodTrade = true;
						ot.goodTrade = true;
					}
				}

				if (c.inTrade && !c.tradeConfirmed && ot.goodTrade
						&& c.goodTrade) {
					c.tradeConfirmed = true;
					if (ot.tradeConfirmed) {
						c.getTradeAndDuel().confirmScreen();
						ot.getTradeAndDuel().confirmScreen();
					}
				}
			} else {
				c.getTradeAndDuel().declineTrade();
			}
			break;
		case 13218:
				Client ot1 = (Client) PlayerHandler.players[c.tradeWith];
				if (System.currentTimeMillis() - c.lastButton < 400) {
					c.lastButton = System.currentTimeMillis();
					break;
				} else 
					c.lastButton = System.currentTimeMillis();
				if(c.getTradeAndDuel().twoTraders(c, ot1)) {
					c.tradeAccepted = true;
					try{
						if (c.inTrade && c.tradeConfirmed && ot1.tradeConfirmed && !c.tradeConfirmed2) {
							c.tradeConfirmed2 = true;
							if (ot1.tradeConfirmed2) {
								c.acceptedTrade = true;
								ot1.acceptedTrade = true;
								c.getTradeAndDuel().giveItems();
								ot1.getTradeAndDuel().giveItems();
								c.sendMessage("Trade accepted.");
								c.getTradeAndDuel().resetTrade();
								ot1.sendMessage("Trade accepted.");
								ot1.getTradeAndDuel().resetTrade();
								break;
							}
							ot1.getPA().sendFrame126("Other player has accepted.", 3535);
							c.getPA().sendFrame126("Waiting for other player...", 3535);
						}
					} catch (Exception e) {
						c.getTradeAndDuel().declineTrade();
					}
				} else {
					c.getTradeAndDuel().declineTrade();
					c.sendMessage("You can't trade two people at once!");
					ot1.sendMessage("You can't trade two people at once!");
				}
				break;
		/* Rules Interface Buttons */
		case 125011: // Click agree
			if (!c.ruleAgreeButton) {
				c.ruleAgreeButton = true;
				c.getPA().sendFrame36(701, 1);
			} else {
				c.ruleAgreeButton = false;
				c.getPA().sendFrame36(701, 0);
			}
			break;
		case 125003:// Accept
			if (c.ruleAgreeButton) {
				c.getPA().showInterface(3559);
				c.newPlayer = false;
			} else if (!c.ruleAgreeButton) {
				c.sendMessage("You need to click on you agree before you can continue on.");
			}
			break;
		case 125006:// Decline
			c.sendMessage("You have chosen to decline, Client will be disconnected from the server.");
			break;
		/* End Rules Interface Buttons */
		/* Player Options */
		case 3145:
			if (!c.mouseButton) {
				c.mouseButton = true;
				c.getPA().sendFrame36(500, 1);
				c.getPA().sendFrame36(170, 1);
			} else if (c.mouseButton) {
				c.mouseButton = false;
				c.getPA().sendFrame36(500, 0);
				c.getPA().sendFrame36(170, 0);
			}
			break;
		case 3189:
			if (!c.splitChat) {
				c.splitChat = true;
				c.getPA().sendFrame36(502, 1);
				c.getPA().sendFrame36(287, 1);
			} else {
				c.splitChat = false;
				c.getPA().sendFrame36(502, 0);
				c.getPA().sendFrame36(287, 0);
			}
			break;
		case 3147:
			if (!c.chatEffects) {
				c.chatEffects = true;
				c.getPA().sendFrame36(501, 1);
				c.getPA().sendFrame36(171, 0);
			} else {
				c.chatEffects = false;
				c.getPA().sendFrame36(501, 0);
				c.getPA().sendFrame36(171, 1);
			}
			break;
		case 48176:
			if (!c.acceptAid) {
				c.acceptAid = true;
				c.getPA().sendFrame36(503, 1);
				c.getPA().sendFrame36(427, 1);
			} else {
				c.acceptAid = false;
				c.getPA().sendFrame36(503, 0);
				c.getPA().sendFrame36(427, 0);
			}
			break;
		case 152:
		case 153:
			if (!c.isRunning2) {
				c.isRunning2 = true;
				c.getPA().sendFrame36(504, 1);
				c.getPA().sendFrame36(173, 1);
			} else {
				c.isRunning2 = false;
				c.getPA().sendFrame36(504, 0);
				c.getPA().sendFrame36(173, 0);
			}
			break;
		case 3138:// brightness1
			c.getPA().sendFrame36(505, 1);
			c.getPA().sendFrame36(506, 0);
			c.getPA().sendFrame36(507, 0);
			c.getPA().sendFrame36(508, 0);
			c.getPA().sendFrame36(166, 1);
			break;
		case 3140:// brightness2
			c.getPA().sendFrame36(505, 0);
			c.getPA().sendFrame36(506, 1);
			c.getPA().sendFrame36(507, 0);
			c.getPA().sendFrame36(508, 0);
			c.getPA().sendFrame36(166, 2);
			break;

		case 3142:// brightness3
			c.getPA().sendFrame36(505, 0);
			c.getPA().sendFrame36(506, 0);
			c.getPA().sendFrame36(507, 1);
			c.getPA().sendFrame36(508, 0);
			c.getPA().sendFrame36(166, 3);
			break;

		case 3144:// brightness4
			c.getPA().sendFrame36(505, 0);
			c.getPA().sendFrame36(506, 0);
			c.getPA().sendFrame36(507, 0);
			c.getPA().sendFrame36(508, 1);
			c.getPA().sendFrame36(166, 4);
			break;
		case 74206:// area1
			c.getPA().sendFrame36(509, 1);
			c.getPA().sendFrame36(510, 0);
			c.getPA().sendFrame36(511, 0);
			c.getPA().sendFrame36(512, 0);
			break;
		case 74207:// area2
			c.getPA().sendFrame36(509, 0);
			c.getPA().sendFrame36(510, 1);
			c.getPA().sendFrame36(511, 0);
			c.getPA().sendFrame36(512, 0);
			break;
		case 74208:// area3
			c.getPA().sendFrame36(509, 0);
			c.getPA().sendFrame36(510, 0);
			c.getPA().sendFrame36(511, 1);
			c.getPA().sendFrame36(512, 0);
			break;
		case 74209:// area4
			c.getPA().sendFrame36(509, 0);
			c.getPA().sendFrame36(510, 0);
			c.getPA().sendFrame36(511, 0);
			c.getPA().sendFrame36(512, 1);
			break;
		case 168:
			c.startAnimation(855);
			break;
		case 169:
			c.startAnimation(856);
			break;
		case 162:
			c.startAnimation(857);
			break;
		case 164:
			c.startAnimation(858);
			break;
		case 165:
			c.startAnimation(859);
			break;
		case 161:
			c.startAnimation(860);
			break;
		case 170:
			c.startAnimation(861);
			break;
		case 171:
			c.startAnimation(862);
			break;
		case 163:
			c.startAnimation(863);
			break;
		case 167:
			c.startAnimation(864);
			break;
		case 172:
			c.startAnimation(865);
			break;
		case 166:
			c.startAnimation(866);
			break;
		case 52050:
			c.startAnimation(2105);
			break;
		case 52051:
			c.startAnimation(2106);
			break;
		case 52052:
			c.startAnimation(2107);
			break;
		case 52053:
			c.startAnimation(2108);
			break;
		case 52054:
			c.startAnimation(2109);
			break;
		case 52055:
			c.startAnimation(2110);
			break;
		case 52056:
			c.startAnimation(2111);
			break;
		case 52057:
			c.startAnimation(2112);
			break;
		case 52058:
			c.startAnimation(2113);
			break;
		case 43092:
			c.startAnimation(1374);
			break;
		case 2155:
			c.startAnimation(0x46B);
			break;
		case 25103:
			c.startAnimation(0x46A);
			break;
		case 25106:
			c.startAnimation(0x469);
			break;
		case 2154:
			c.startAnimation(0x468);
			break;
		case 52071:
			c.startAnimation(0x84F);
			break;
		case 52072:
			c.startAnimation(0x850);
			break;
		case 59062:
			c.startAnimation(2836);
			break;
		case 72032:
			c.startAnimation(3544);
			break;
		case 72033:
			c.startAnimation(3543);
			break;
		/* END OF EMOTES */

		case 24017:
			c.getPA().resetAutoCast();
			// c.sendFrame246(329, 200, c.playerEquipment[c.playerWeapon]);
			c.getItems().sendWeapon(
					c.playerEquipment[Player.playerWeapon],
					c.getItems().getItemName(
							c.playerEquipment[Player.playerWeapon]));
			// c.setSidebarInterface(0, 328);
			// c.setSidebarInterface(6, c.playerMagicBook == 0 ? 1151 :
			// c.playerMagicBook == 1 ? 12855 : 1151);
			break;
		}
		if (c.isAutoButton(actionButtonId))
			c.assignAutocast(actionButtonId);
	}

}
