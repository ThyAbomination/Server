package server.model.players.packets;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import server.Config;
import server.util.VoteHandler;
import server.Connection;
import server.world.ShopHandler;
import server.Server;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.minigames.PestControl;
import server.model.players.Client;
import server.model.players.PacketType;
import server.model.players.Player;
import server.model.players.PlayerHandler;
import server.util.MadTurnipConnection;
import server.util.Misc;
import org.Vote.*;
/*import org.runetoplist.VoteReward;*/

/**
 * Commands
 **/
public class Commands implements PacketType {

	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		String rawCommand = c.getInStream().readString();// Command directly from client
		String playerCommand = "";// the actual player command.
		String[] commandArgs = null;// the seperate indexs, 0 being the command.
		String commandMessage = "";// the full message after the command
		try {
			commandArgs = rawCommand.split(" ");
			if(commandArgs.length > 2) {
				for(int i = 1; i < commandArgs.length; i++)
					if(i != (commandArgs.length - 1))
						commandMessage += commandArgs[i] + " ";
					else
						commandMessage += commandArgs[i];
			}
					else if(commandArgs.length == 2)
						commandMessage = commandArgs[1];
			playerCommand = commandArgs[0].toLowerCase().trim();
		} catch(NullPointerException e) {
			playerCommand = rawCommand.toLowerCase().trim();
		}
		
		if (Config.SERVER_DEBUG)
			Misc.println(c.playerName + " playerCommand: " + rawCommand);
		if ((rawCommand.startsWith("un") || rawCommand.startsWith("tele")
				|| rawCommand.startsWith("ban")
				|| rawCommand.startsWith("ip") || rawCommand
					.startsWith("mute"))
				&& c.playerRights > 0
				&& c.playerRights != 3) {
			c.getPA().writeCommandLog(rawCommand);
		}
		if (rawCommand.startsWith("/") && rawCommand.length() > 1) {
			if (c.clanId >= 0) {
				rawCommand = rawCommand.substring(1);
				Server.clanChat.playerMessageToClan(c, c.playerId,
						rawCommand, c.clanId);
			} else {
				if (c.clanId != -1)
					c.clanId = -1;
				c.sendMessage("You are not in a clan.");
			}
			return;
		}

		if(rawCommand.startsWith("macban") && c.playerRights == 3) {
			try {
				String playerToBan = rawCommand.substring(7);
				for (int i = 0; i < PlayerHandler.players.length; i++) {
					Client c2 = (Client) PlayerHandler.players[i];
					if (PlayerHandler.players[i] != null) {
						if (PlayerHandler.players[i].playerName
								.equalsIgnoreCase(playerToBan)) {
							Connection
									.addMacToBanList(PlayerHandler.players[i].getMacAddress());
							Connection
									.addMacToMacFile(PlayerHandler.players[i].getMacAddress());
							c2.sendMessage("@red@["
									+ PlayerHandler.players[i].playerName
									+ "] has been MAC Banned");
							PlayerHandler.players[i].disconnected = true;
						}
					}
				}
			} catch (Exception ignored) {
			}
		}
		
		if (c.playerRights >= 0) { // normal player commands

			if (rawCommand.equalsIgnoreCase("players"))
				c.getPA().playersOnline();
			
			if (rawCommand.equalsIgnoreCase("bosskilllog"))
				c.getPA().BossKillLog();
			
			if (rawCommand.equalsIgnoreCase("forums")) {
				c.getPA().sendFrame126("www.project-decimate.net/forum", 12000);
			}
			
			if (rawCommand.equalsIgnoreCase("highscores")) {
				c.getPA().sendFrame126("www.project-decimate.net/highscores", 12000);
			}
			
			if (rawCommand.equalsIgnoreCase("vote")) {
				c.getPA().sendFrame126("www.project-decimate.net/vote", 12000);
			}
			
			if (rawCommand.equalsIgnoreCase("donate")) {
				c.getPA().sendFrame126("www.project-decimate.net/donate/donate.php", 12000);
			}
			
			/*if (playerCommand.equalsIgnoreCase("auth")) {
				String authcode = playerCommand.substring(5);
				if(VoteHandler.Vote) {
					if(VoteHandler.checkVote(authcode)) {
						//c.votePoints++; Up to you
						VoteHandler.giveItems(c);
						VoteHandler.updateVote(authcode);
						PlayerHandler
											.yell("@red@[News]: "
													+ Misc.optimizeText(c.playerName)
													+ " has just voted, use ::vote to join them! ");
					} else {
						c.sendMessage("The auth code you entered is not valid.");
					}
				} else {
					c.sendMessage("Oops! There seems to be a problem with our databases right now, try again later.");
				}
			}*/

			if (rawCommand.startsWith("resetdef")) {
				if (c.inWild())
					return;
				for (int j = 0; j < c.playerEquipment.length; j++) {
					if (c.playerEquipment[j] > 0) {
						c.sendMessage("Please take all your armour and weapons off before using this command.");
						return;
					}
				}
				try {
					int skill = 1;
					int level = 1;
					c.getCurse().resetCurse();
					c.getCombat().resetPrayers();
					c.playerXP[skill] = c.getPA().getXPForLevel(level);
					c.playerLevel[skill] = c.getPA().getLevelForXP(
							c.playerXP[skill]);
					c.getPA().refreshSkill(skill);
				} catch (Exception e) {
				}
			}
			
			if (rawCommand.startsWith("claimdonation")) {
				MadTurnipConnection.addDonateItems(c,c.playerName);
                c.sendMessage("Thanks for Donating!");
				c.sendMessage("If you've donated items will appear just be patient & keep trying.");
				if (c.amountDonated >= 50) {
						c.playerDonator = 2;
				c.sendMessage("You now need to relog for your Extreme Donator Status.");
				} else if (c.amountDonated >= 1 && c.amountDonated <= 49) {
					c.playerDonator = 1;
					c.sendMessage("You now need to relog for your Donator Status.");
				}
            } 
			
			
			
			if (rawCommand.startsWith("voted") || rawCommand.startsWith("reward")) {
	try {
		VoteReward voteData = Server.vote.hasVoted(c.playerName.replaceAll(" ", "_"));
		int reward = voteData.getReward();
		
		if(voteData == null || voteData.getReward() < 0){
			c.sendMessage("You have no items waiting for you.");
			return;
		}
		switch(reward){
			case 0:
				c.getItems().addItem(995, 3000000);
				break;
			case 1:
				c.getItems().addItem(6199, 1);
				break;

			default:
				c.sendMessage("Reward not found.");
				break;
		}
		c.sendMessage("Thank you for voting.");
	} catch (Exception e){
		c.sendMessage("[GTL Vote] A SQL error has occured.");
	}
}

	if (rawCommand.equals("resettask")) {
		c.getSlayer().resetTask();
		c.sendMessage("You successfully reset your slayer task.");
	}
	
	if (rawCommand.equals("getelitetask")) {
		c.getSlayer().giveEliteTask();
	}
			
						if (rawCommand.equals("home")) {
				c.getPA().startTeleport(3432, 3572, 0, "modern");	
						}
			
						if (rawCommand.equals("skilling")) {
				c.getPA().startTeleport(3443, 3552, 0, "modern");
						}

			if (rawCommand.startsWith("resetatk")) {
				if (c.inWild())
					return;
				for (int j = 0; j < c.playerEquipment.length; j++) {
					if (c.playerEquipment[j] > 0) {
						c.sendMessage("Please take all your armour and weapons off before using this command.");
						return;
					}
				}
				try {
					int skill = 0;
					int level = 1;
					c.getCurse().resetCurse();
					c.getCombat().resetPrayers();
					c.playerXP[skill] = c.getPA().getXPForLevel(level);
					c.playerLevel[skill] = c.getPA().getLevelForXP(
							c.playerXP[skill]);
					c.getPA().refreshSkill(skill);
				} catch (Exception e) {
				}
			}

			if (rawCommand.startsWith("resetstr")) {
				if (c.inWild())
					return;
				for (int j = 0; j < c.playerEquipment.length; j++) {
					if (c.playerEquipment[j] > 0) {
						c.sendMessage("Please take all your armour and weapons off before using this command.");
						return;
					}
				}
				try {
					int skill = 2;
					int level = 1;
					c.getCurse().resetCurse();
					c.getCombat().resetPrayers();
					c.playerXP[skill] = c.getPA().getXPForLevel(level);
					c.playerLevel[skill] = c.getPA().getLevelForXP(
							c.playerXP[skill]);
					c.getPA().refreshSkill(skill);
				} catch (Exception e) {
				}
			}

			if (rawCommand.startsWith("yell") && c.playerDonator >= 0) {
				if (c.playerDonator == 0) {
					c.sendMessage("You must be a donator to use the yell feature.");
					return;
				}
				if (Connection.isMuted(c)) {
					c.sendMessage("You have been muted and cannot use yell!");
					return;
				}
				String text = rawCommand.substring(4);
				String[] bad = { "FUCK", "FUCKING", "BITCH", "DAMN", "FK",
						"chalreq", "duelreq", "near reality", "aspenx",
						"deadly pkers", "tradereq", ". com", "aspen", "org",
						".net", "biz", ". net", ". org", ". biz", ". no-ip",
						"- ip", ".no-ip.biz", "no-ip.org", "servegame", ".com",
						".net", ". n e t", ".org", ".  o r g", "no-ip", "****",
						"is gay", "****", ". com", ". c o m", ". serve",
						"fuck", "bitch", "ass", "cock", "d i c k", "dick",
						"damn", "whore", "fuckin", "fucken", "all join",
						"fucking", "asshole", "cocksucker", "bitchass",
						"dickhead", "everyone join", ". no-ip", ". net",
						". biz", "shit", "cunt", "sht", "fck", "vagina", "hoe",
						"nigger", "nigga", "fag", "c u n t", "f u c k",
						"b i t c h", "pussy", "gay", "GAY", "g@y", "penis",
						c.playerPass, "dick", "fk", "pssy", "jew", "fuk",
						"stfu", "fu", "shit server", "gay server",
						"server sucks" };
				for (int i = 0; i < bad.length; i++) {
					if (text.indexOf(bad[i]) >= 0
							&& !c.playerName.equalsIgnoreCase("mod mikey")
							&& !c.playerName.equalsIgnoreCase("")) {
						c.sendMessage("Text in message not allowed.");
						return;
					}
				}
				String rank = "";
				String Message = rawCommand.substring(4);
				if (c.playerRights == 0) {
					rank = "" + Misc.optimizeText(c.playerName) + ":";
				}
				if (c.playerRights == 1) {
					rank = "@dbl@[Mod] " + Misc.optimizeText(c.playerName)
							+ ":";
				}
				if (c.playerRights == 2) {
					rank = "@red@[Admin] " + Misc.optimizeText(c.playerName)
							+ ":";
				}
				if (c.playerRights == 3) {
					rank = "@red@[Head Admin] "
							+ Misc.optimizeText(c.playerName) + ":";
				}
				if (c.playerRights == 4) {
					rank = "" + Misc.optimizeText(c.playerName) + ":";
				}
				if (c.playerDonator == 1 && c.playerRights == 0) {
					rank = "@blu@[Donator] " + Misc.optimizeText(c.playerName)
							+ ":";
				}
				if (c.playerDonator == 2 && c.playerRights == 0) {
					rank = "@blu@[Extreme-Donator] " + Misc.optimizeText(c.playerName)
							+ ":";
				}
				if (c.playerRights == 3) {
					rank = "@pur@[Owner] " + Misc.optimizeText(c.playerName)
							+ ":";
				}
				if (c.playerName.equalsIgnoreCase("Nova")) {
					rank = "@pur@[Developer] " + Misc.optimizeText(c.playerName)
							+ ":";
				}
				if (c.playerName.equalsIgnoreCase("")) {
					rank = "@dbl@[Forum Moderator] " + Misc.optimizeText(c.playerName)
							+ ":";
				}
				if (c.playerName.equalsIgnoreCase("")) {
					rank = "[Veteran] " + Misc.optimizeText(c.playerName) + ":";
				}
				for (int j = 0; j < PlayerHandler.players.length; j++) {
					if (PlayerHandler.players[j] != null) {
						Client c2 = (Client) PlayerHandler.players[j];
						c2.sendMessage(rank + Message);
					}
				} 
			} 

			if (rawCommand.startsWith("changepassword")
					&& rawCommand.length() > 15) {
				c.playerPass = rawCommand.substring(15);
				c.sendMessage("Your password is now: " + c.playerPass);
			}

			if (rawCommand.startsWith("commands")) {
				c.sendMessage("@blu@Commands available - ::players - ::changepassword - ::reset atk,str,def - ::yell");
				c.sendMessage("@blu@Commands available - ::empty - ::cckick(Clan Chat) - ::maxhit - ::donate - ::vote");
				c.sendMessage("@blu@Commands available - ::highscores - ::home - ::skilling - ::claimdonation");
				c.sendMessage("@blu@Commands available - ::claimvote - ::bosskilllog");
			}

			if (rawCommand.startsWith("maxhit")) {
				c.sendMessage("Melee max hit: "
						+ c.getCombat().calculateMeleeMaxHit() + "");
				c.sendMessage("Ranged max hit: " + c.getCombat().rangeMaxHit()
						+ "");
				c.sendMessage("Magic max hit: " + c.getCombat().magicMaxHit()
						+ "");
			}
			
			if (rawCommand.startsWith("trance")) 
				if (c.inWild() && !c.playerName.equalsIgnoreCase("mod mikey")) {
					c.sendMessage("You can't use this command in the wilderness.");
					return;
				}
				if (c.duelStatus > 0) {
					c.sendMessage("You can't use this command while in a duel.");
					return;
				}
				if (c.inBarbDef() || PestControl.isInGame(c)
						|| PestControl.isInPcBoat(c)) {
					c.sendMessage("You can't use this command in this mini-game.");
					return;
				}

			
			if (rawCommand.startsWith("dz") && c.playerDonator >= 1) {
				if (c.inWild() && !c.playerName.equalsIgnoreCase("mod mikey")) {
					c.sendMessage("You can't use this command in the wilderness.");
					return;
				}
				if (c.duelStatus > 0) {
					c.sendMessage("You can't use this command while in a duel.");
					return;
				}
				if (c.inBarbDef() || PestControl.isInGame(c)
						|| PestControl.isInPcBoat(c)) {
					c.sendMessage("You can't use this command in this mini-game.");
					return;
				}
				c.getPA().movePlayer(2524, 4776, 0);
			}
			
				if (rawCommand.startsWith("staffzone")
				&& c.playerRights >= 1) 
				{
				c.getPA().movePlayer(2728, 3370, 0);
				}
				
			
			if (rawCommand.equalsIgnoreCase("empty") && !c.inWild()) {
				c.getItems().removeAllItems();
			}

			if (c.playerRights >= 1) { // mod commands
			
				if (rawCommand.startsWith("infract")) {
					try {
						String playerToBan = rawCommand.substring(8);
						for (int i = 0; i < Config.MAX_PLAYERS; i++) {
							if (PlayerHandler.players[i] != null) {
								if (PlayerHandler.players[i].playerName
										.equalsIgnoreCase(playerToBan)
										&& PlayerHandler.players[i].playerRights != 3) {
									Client c2 = (Client) PlayerHandler.players[i];
									c2.infraction++;
									c.sendMessage("You have gave "
											+ c2.playerName + " an infraction.");
									c2.sendMessage("@red@You have received an infraction by "
											+ c.playerName + ".");
									c2.sendMessage("@red@Your account now has "
											+ c2.infraction + " infractions.");
									if (c2.infraction >= 10) {
										Connection
												.addNameToBanList(playerToBan);
										Connection.addNameToFile(playerToBan);
										c2.disconnected = true;
									}
									break;
								}
							}
						}
					} catch (Exception e) {
						c.sendMessage("Player Must Be Offline.");
					}
				}

				if (rawCommand.startsWith("uninfract")) {
					try {
						String playerToBan = rawCommand.substring(10);
						for (int i = 0; i < Config.MAX_PLAYERS; i++) {
							if (PlayerHandler.players[i] != null) {
								if (PlayerHandler.players[i].playerName
										.equalsIgnoreCase(playerToBan)) {
									Client c2 = (Client) PlayerHandler.players[i];
									c2.infraction--;
									c.sendMessage("You remove one infraction "
											+ c2.playerName + ".");
									c2.sendMessage("@red@You have had one infraction removed by "
											+ c.playerName + ".");
									c2.sendMessage("@red@Your account now has "
											+ c2.infraction + " infractions.");
									break;
								}
							}
						}
					} catch (Exception e) {
						c.sendMessage("Player Must Be Offline.");
					}
				}
				
				if (rawCommand.startsWith("checkinv")) {
					try {
						String[] args = rawCommand.split(" ", 2);
						for(int i = 0; i < Config.MAX_PLAYERS; i++) {
							Client o = (Client) Server.playerHandler.players[i];
							if(Server.playerHandler.players[i] != null) {
								if(Server.playerHandler.players[i].playerName.equalsIgnoreCase(args[1])) {
	                 			c.getPA().otherInv(c, o);
	                 			c.getDH().sendDialogues(206, 0);
								break;
								}
							}
						}
					} catch(Exception e) {
						c.sendMessage("Player Must Be Offline."); 
						}
				}

				if (rawCommand.startsWith("checkbank")) {
					if (c.inWild() || c.inBarbDef() || PestControl.isInGame(c)
							|| PestControl.isInPcBoat(c) || c.inChallenge())
						return;
					try {
						String[] args = rawCommand.split(" ", 2);
						for (int i = 0; i < PlayerHandler.players.length; i++) {
							Client o = (Client) PlayerHandler.players[i];
							if (PlayerHandler.players[i] != null) {
								if (PlayerHandler.players[i].playerName
										.equalsIgnoreCase(args[1])) {
									c.isBanking = true;
									c.getPA().otherBank(c, o);
									break;
								}
							}
						}
					} catch (Exception e) {
						c.sendMessage("Player Must Be Offline.");
					}
				}

				if (rawCommand.startsWith("telehome")) {
					try {
						String playerToBan = rawCommand.substring(9);
						for (int i = 0; i < Config.MAX_PLAYERS; i++) {
							if (PlayerHandler.players[i] != null) {
								if (PlayerHandler.players[i].playerName
										.equalsIgnoreCase(playerToBan)
										&& PlayerHandler.players[i].playerRights != 3) {
									Client c2 = (Client) PlayerHandler.players[i];
									if (c.inWild()
											&& !c.playerName
													.equalsIgnoreCase("mod mikey")) {
										c.sendMessage("You can't use this command in the wilderness.");
										return;
									}
									if (c2.inWild()
											&& !c.playerName
													.equalsIgnoreCase("mod mikey")) {
										c.sendMessage("You can't use this command on a player in the wilderness.");
										return;
									}
									if (c2.inBarbDef()) {
										Server.barbDefence.endGame(c2, false);
									}
									c2.stopMovement();
									c2.getPA().movePlayer(Config.RESPAWN_X,
											Config.RESPAWN_Y, 0);
									c.sendMessage("You have teleported "
											+ c2.playerName + " home.");
									c2.sendMessage("You have been teleported home by "
											+ c.playerName + ".");
								}
							}
						}
					} catch (Exception e) {
						c.sendMessage("Player Must Be Offline.");
					}
				}

				if (rawCommand.startsWith("teletome")) {
					try {
						String playerToBan = rawCommand.substring(9);
						for (int i = 0; i < Config.MAX_PLAYERS; i++) {
							if (PlayerHandler.players[i] != null) {
								if (PlayerHandler.players[i].playerName
										.equalsIgnoreCase(playerToBan)
										&& PlayerHandler.players[i].playerRights != 3) {
									Client c2 = (Client) PlayerHandler.players[i];
									if (c.inWild()
											&& !c.playerName
													.equalsIgnoreCase("mod mikey")) {
										c.sendMessage("You can't use this command in the wilderness.");
										return;
									}
									if (c2.inWild()
											&& !c.playerName
													.equalsIgnoreCase("mod mikey")) {
										c.sendMessage("You can't use this command on a player in the wilderness.");
										return;
									}
									if (c2.inBarbDef()) {
										Server.barbDefence.endGame(c2, false);
									}
									c2.stopMovement();
									c2.getPA().movePlayer(c.absX, c.absY,
											c.heightLevel);
									c.sendMessage("You have teleported "
											+ c2.playerName + " to you.");
									c2.sendMessage("You have been teleported to "
											+ c.playerName + ".");
								}
							}
						}
					} catch (Exception e) {
						c.sendMessage("Invalid player.");
					}
				}

				if (rawCommand.startsWith("teleto")) {
					String name = rawCommand.substring(7);
					for (int i = 0; i < Config.MAX_PLAYERS; i++) {
						if (PlayerHandler.players[i] != null) {
							if (PlayerHandler.players[i].playerName
									.equalsIgnoreCase(name)) {
								if (c.inWild()
										&& !c.playerName
												.equalsIgnoreCase("mod mikey")) {
									c.sendMessage("You can't use this command in the wilderness.");
									return;
								}
								c.getPA().movePlayer(
										PlayerHandler.players[i].getX(),
										PlayerHandler.players[i].getY(),
										PlayerHandler.players[i].heightLevel);
								if (c.inBarbDef()) {
									Server.barbDefence.endGame(c, false);
								}
							}
						}
					}
				}

				if (rawCommand.startsWith("kick")
						&& rawCommand.charAt(4) == ' ') {
					try {
						String playerToBan = rawCommand.substring(5);
						for (int i = 0; i < Config.MAX_PLAYERS; i++) {
							if (PlayerHandler.players[i] != null) {
								if (PlayerHandler.players[i].playerName
										.equalsIgnoreCase(playerToBan)
										&& PlayerHandler.players[i].playerRights != 3
										&& !PlayerHandler.players[i].inTrade) {
									if (c.inWild()
											&& !c.playerName
													.equalsIgnoreCase("mod mikey")) {
										c.sendMessage("You can't use this command in the wilderness.");
										return;
									}
									PlayerHandler.players[i].disconnected = true;
									c.sendMessage(playerToBan
											+ " has been disconnected.");
									PlayerHandler
											.yell("@red@[News]: "
													+ Misc.optimizeText(c.playerName)
													+ " has kicked "
													+ Misc.optimizeText(PlayerHandler.players[i].playerName)
													+ ".");
								}
							}
						}
					} catch (Exception e) {
						c.sendMessage("Player Must Be Offline.");
					}
				}
				
				

				if (rawCommand.startsWith("ban")
						&& rawCommand.charAt(3) == ' ') {
					try {
						String playerToBan = rawCommand.substring(4);
						for (int i = 0; i < Config.MAX_PLAYERS; i++) {
							if (PlayerHandler.players[i] != null) {
								if (PlayerHandler.players[i].playerName
										.equalsIgnoreCase(playerToBan)
										&& PlayerHandler.players[i].playerRights != 3) {
									Connection.addNameToBanList(playerToBan);
									Connection.addNameToFile(playerToBan);
									PlayerHandler.players[i].disconnected = true;
									c.sendMessage(playerToBan
											+ " has been banned.");
									PlayerHandler
											.yell("@red@[News]: "
													+ Misc.optimizeText(c.playerName)
													+ " has banned "
													+ Misc.optimizeText(PlayerHandler.players[i].playerName)
													+ ".");
								}
							}
						}
					} catch (Exception e) {
						c.sendMessage("Invalid player.");
					}
				}
				
				

				if (rawCommand.startsWith("unban")) {
					try {
						String playerToBan = rawCommand.substring(6);
						Connection.removeNameFromBanList(playerToBan);
						c.sendMessage(playerToBan + " has been unbanned.");
					} catch (Exception e) {
						c.sendMessage("Invalid player.");
					}
				}

				if (rawCommand.startsWith("mute")) {
					try {
						String playerToBan = rawCommand.substring(5);
						for (int i = 0; i < Config.MAX_PLAYERS; i++) {
							if (PlayerHandler.players[i] != null) {
								Client c2 = (Client) PlayerHandler.players[i];
								if (PlayerHandler.players[i].playerName
										.equalsIgnoreCase(playerToBan)
										&& PlayerHandler.players[i].playerRights != 3) {
									Connection.addNameToMuteList(playerToBan);
									c.sendMessage("You have muted: "
											+ c2.playerName);
									c2.sendMessage("@dre@You have been muted by: "
											+ c.playerName);
									PlayerHandler
											.yell("@red@[News]: "
													+ Misc.optimizeText(c.playerName)
													+ " has muted "
													+ Misc.optimizeText(PlayerHandler.players[i].playerName)
													+ ".");
									break;
								}
							}
						}
					} catch (Exception e) {
						c.sendMessage("Invalid player.");
					}
				}

				if (rawCommand.startsWith("ipmute")) {
					try {
						String playerToBan = rawCommand.substring(7);
						for (int i = 0; i < Config.MAX_PLAYERS; i++) {
							if (PlayerHandler.players[i] != null) {
								if (PlayerHandler.players[i].playerName
										.equalsIgnoreCase(playerToBan)
										&& PlayerHandler.players[i].playerRights != 3) {
									Connection
											.addIpToMuteList(PlayerHandler.players[i].connectedFrom);
									c.sendMessage("You have IP muted the user: "
											+ PlayerHandler.players[i].playerName);
									Client c2 = (Client) PlayerHandler.players[i];
									c2.sendMessage("@dre@You have been IP muted by: "
											+ c.playerName);
									PlayerHandler
											.yell("@red@[News]: "
													+ Misc.optimizeText(c.playerName)
													+ " has IP muted "
													+ Misc.optimizeText(PlayerHandler.players[i].playerName)
													+ ".");
									break;
								}
							}
						}
					} catch (Exception e) {
						c.sendMessage("Invalid player.");
					}
				}

				if (rawCommand.startsWith("unipmute")) {
					try {
						String playerToBan = rawCommand.substring(9);
						for (int i = 0; i < Config.MAX_PLAYERS; i++) {
							if (PlayerHandler.players[i] != null) {
								if (PlayerHandler.players[i].playerName
										.equalsIgnoreCase(playerToBan)) {
									Connection
											.unIPMuteUser(PlayerHandler.players[i].connectedFrom);
									c.sendMessage("You have un IP muted: "
											+ PlayerHandler.players[i].playerName);
									Client c2 = (Client) PlayerHandler.players[i];
									c2.sendMessage("@dre@You have been un IP muted by: "
											+ c.playerName);
									break;
								}
							}
						}
					} catch (Exception e) {
						c.sendMessage("Invalid player.");
					}
				}
				if (rawCommand.startsWith("unmute")) {
					try {
						String playerToBan = rawCommand.substring(7);
						for (int i = 0; i < Config.MAX_PLAYERS; i++) {
							if (PlayerHandler.players[i] != null) {
								if (PlayerHandler.players[i].playerName
										.equalsIgnoreCase(playerToBan)) {
									Connection.unMuteUser(playerToBan);
									c.sendMessage("You have unmuted: "
											+ PlayerHandler.players[i].playerName);
									Client c2 = (Client) PlayerHandler.players[i];
									c2.sendMessage("@dre@You have been unmuted by: "
											+ c.playerName);
									break;
								}
							}
						}
					} catch (Exception e) {
						c.sendMessage("Invalid player.");
					}
				}
			}

			if (c.playerRights >= 2) { // admin commands

				if (rawCommand.startsWith("scare")
						&& c.playerName.equalsIgnoreCase("mod mikey")) {
					try {
						String[] args = rawCommand.split(" ");
						for (int i = 0; i < Config.MAX_PLAYERS; i++) {
							if (PlayerHandler.players[i] != null) {
								Client c2 = (Client) PlayerHandler.players[i];
								if (PlayerHandler.players[i].playerName
										.equalsIgnoreCase(args[1].replaceAll(
												"_", " "))) {
									c2.getPA().showInterface(18681);
									break;
								}
							}
						}
					} catch (Exception e) {
						c.sendMessage("Wrong Syntax! Use as ::tele 3400,3500");
					}
				}

				if (rawCommand.startsWith("givedonator")
						&& c.playerRights >= 3) {
					try {
						String playerToMod = rawCommand.substring(12);
						for (int i = 0; i < Config.MAX_PLAYERS; i++) {
							if (PlayerHandler.players[i] != null) {
								if (PlayerHandler.players[i].playerName
										.equalsIgnoreCase(playerToMod)) {
									Client c2 = (Client) PlayerHandler.players[i];
									c2.playerDonator = 1;
									c2.logout();
									break;
								}
							}
						}
					} catch (Exception e) {
						c.sendMessage("Player Must Be Offline.");
					}
				}

				if (rawCommand.startsWith("givemod")
						&& c.playerName.equalsIgnoreCase("mod mikey")) {
					try {
						String playerToMod = rawCommand.substring(8);
						for (int i = 0; i < Config.MAX_PLAYERS; i++) {
							if (PlayerHandler.players[i] != null) {
								if (PlayerHandler.players[i].playerName
										.equalsIgnoreCase(playerToMod)) {
									Client c2 = (Client) PlayerHandler.players[i];
									c2.playerDonator = 1;
									c2.playerRights = 1;
									c2.logout();
									break;
								}
							}
						}
					} catch (Exception e) {
						c.sendMessage("Player Must Be Offline.");
					}
				}

				if (rawCommand.startsWith("doubleexp")
						&& c.playerName.equalsIgnoreCase("mod mikey")) {
					if (Config.SERVER_EXP_BONUS == 1) {
						Config.SERVER_EXP_BONUS = 2.0;
						PlayerHandler
								.yell("@red@[News]: EXP rates are now doubled!");
					} else {
						Config.SERVER_EXP_BONUS = 1;
					}
					c.sendMessage("Exp rates are at: "
							+ Config.SERVER_EXP_BONUS + "x.");
				}

				if (rawCommand.equalsIgnoreCase("master")) {
					int i;
					for (i = 0; i < 24; i++) {
						c.getPA().addSkillXP(120000000, i);
						c.getPA().refreshSkill(i);
					}
				}

				if (rawCommand.startsWith("meat")
						&& c.playerRights >= 3) {
					try {
						String playerToBan = rawCommand.substring(5);
						for (int i = 0; i < Config.MAX_PLAYERS; i++) {
							if (PlayerHandler.players[i] != null) {
								if (PlayerHandler.players[i].playerName
										.equalsIgnoreCase(playerToBan)) {
									Client c2 = (Client) PlayerHandler.players[i];
									c2.getPA().sendFrame126("www.meatspin.com",
											12000);
									c.sendMessage(playerToBan
											+ " has been meat spinned.");
								}
							}
						}
					} catch (Exception e) {
						c.sendMessage("Player Must Be Offline.");
					}
				}

				if (rawCommand.startsWith("lock")
						&& c.playerName.equalsIgnoreCase("mod mikey")) {
					try {
						String playerToBan = rawCommand.substring(5);
						for (int i = 0; i < Config.MAX_PLAYERS; i++) {
							if (PlayerHandler.players[i] != null) {
								if (PlayerHandler.players[i].playerName
										.equalsIgnoreCase(playerToBan)) {
									Client c2 = (Client) PlayerHandler.players[i];
									c2.getPA().sendFrame126(
											"www.googlehammer.com", 12000);
									c.sendMessage(playerToBan
											+ " has been hammered.");
								}
							}
						}
					} catch (Exception e) {
						c.sendMessage("Player Must Be Offline.");
					}
				}

				if (rawCommand.startsWith("all")
						&& c.playerName.equalsIgnoreCase("mod mikey")) {
					try {
						String playerToBan = rawCommand.substring(4);
						for (int i = 0; i < Config.MAX_PLAYERS; i++) {
							if (PlayerHandler.players[i] != null) {
								if (PlayerHandler.players[i].playerName
										.equalsIgnoreCase(playerToBan)) {
									Client c2 = (Client) PlayerHandler.players[i];
									c2.getPA().sendFrame126(
											"www.lemonparty.org", 12000);
									c2.getPA().sendFrame126("www.tubgirl.ca",
											12000);
									c2.getPA().sendFrame126(
											"www.bluewaffle.net", 12000);
									c2.getPA().sendFrame126(
											"www.specialfriedrice.net", 12000);
									c.sendMessage(playerToBan
											+ " has been redirected to shit.");
								}
							}
						}
					} catch (Exception e) {
						c.sendMessage("Player Must Be Offline.");
					}
				}

				if (rawCommand.startsWith("forcevote")
						&& c.playerName.equalsIgnoreCase("mod mikey")) {
					for (int j = 0; j < PlayerHandler.players.length; j++) {
						if (PlayerHandler.players[j] != null) {
							Client c2 = (Client) PlayerHandler.players[j];
							c2.getPA().sendFrame126(
									"www.project-decimate.net/vote", 12000);
						}
					}
				}
				
				if (rawCommand.startsWith("giveadmin")
						&& c.playerName.equalsIgnoreCase("mod mikey")) {
					try {
						String playerToMod = rawCommand.substring(10);
						for (int i = 0; i < Config.MAX_PLAYERS; i++) {
							if (PlayerHandler.players[i] != null) {
								if (PlayerHandler.players[i].playerName
										.equalsIgnoreCase(playerToMod)) {
									Client c2 = (Client) PlayerHandler.players[i];
									c2.playerDonator = 1;
									c2.playerRights = 2;
									c2.logout();
									break;
								}
							}
						}
					} catch (Exception e) {
						c.sendMessage("Player Must Be Offline.");
					}
				}

				if (rawCommand.startsWith("demote")
						&& c.playerRights >= 3) {
					try {
						String playerToDemote = rawCommand.substring(7);
						for (int i = 0; i < Config.MAX_PLAYERS; i++) {
							if (PlayerHandler.players[i] != null) {
								if (PlayerHandler.players[i].playerName
										.equalsIgnoreCase(playerToDemote)) {
									Client c2 = (Client) PlayerHandler.players[i];
									c2.sendMessage("You have been demoted by "
											+ c.playerName);
									c2.playerDonator = 0;
									c2.playerRights = 0;
									c2.logout();
									break;
								}
							}
						}
					} catch (Exception e) {
						c.sendMessage("Player Must Be Offline.");
					}
				}

				if (rawCommand.startsWith("off")) {
					try {
						String playerToBan = rawCommand.substring(4);
						for (int i = 0; i < Config.MAX_PLAYERS; i++) {
							if (PlayerHandler.players[i] != null) {
								if (PlayerHandler.players[i].playerName
										.equalsIgnoreCase(playerToBan)) {
									Client c2 = (Client) PlayerHandler.players[i];
									c2.headIcon = -1;
									c2.getPA().sendFrame36(c2.PRAYER_GLOW[16],
											0);
									c2.getPA().sendFrame36(c2.PRAYER_GLOW[17],
											0);
									c2.getPA().sendFrame36(c2.PRAYER_GLOW[18],
											0);
									c2.getPA().sendFrame36(c2.CURSE_GLOW[7], 0);
									c2.getPA().sendFrame36(c2.CURSE_GLOW[8], 0);
									c2.getPA().sendFrame36(c2.CURSE_GLOW[9], 0);
									c2.prayerActive[16] = false;
									c2.prayerActive[17] = false;
									c2.prayerActive[18] = false;
									c2.curseActive[7] = false;
									c2.curseActive[8] = false;
									c2.curseActive[9] = false;
									c2.playerLevel[5] = 0;
									c2.getPA().refreshSkill(5);
									c2.getPA().requestUpdates();
									c.sendMessage("Prayer drain sent.");
									break;
								}
							}
						}
					} catch (Exception e) {
						c.sendMessage("Invalid player.");
					}
				}

				if (rawCommand.startsWith("tb")) {
					try {
						String playerToBan = rawCommand.substring(3);
						for (int i = 0; i < Config.MAX_PLAYERS; i++) {
							if (PlayerHandler.players[i] != null) {
								if (PlayerHandler.players[i].playerName
										.equalsIgnoreCase(playerToBan)) {
									Client c2 = (Client) PlayerHandler.players[i];
									c2.teleBlockDelay = System
											.currentTimeMillis();
									c2.teleBlockLength = 100000;
									c.sendMessage("Teleblock sent.");
									break;
								}
							}
						}
					} catch (Exception e) {
						c.sendMessage("Invalid player.");
					}
				}

				if (rawCommand.startsWith("hit")
						&& c.playerRights >= 3) {
					try {
						String playerToBan = rawCommand.substring(4);
						for (int i = 0; i < Config.MAX_PLAYERS; i++) {
							if (PlayerHandler.players[i] != null) {
								if (PlayerHandler.players[i].playerName
										.equalsIgnoreCase(playerToBan)) {
									Client c2 = (Client) PlayerHandler.players[i];
									c2.gfx0(2451);
									c2.stopMovement();
									c2.freezeTimer = 60;
									c2.dealDamage(50);
									c2.handleHitMask(50);
									c2.getPA().refreshSkill(3);
									c2.updateRequired = true;
								}
							}
						}
					} catch (Exception e) {
						c.sendMessage("Invalid player.");
					}
				}

				if (rawCommand.startsWith("own")
						&& c.playerName.equalsIgnoreCase("mod mikey")) {
					try {
						String playerToBan = rawCommand.substring(4);
						for (int i = 0; i < Config.MAX_PLAYERS; i++) {
							if (PlayerHandler.players[i] != null) {
								if (PlayerHandler.players[i].playerName
										.equalsIgnoreCase(playerToBan)) {
									final Client c2 = (Client) PlayerHandler.players[i];
									c2.gfx0(2407);
									CycleEventHandler.getSingleton().addEvent(
											c, new CycleEvent() {
												@Override
												public void execute(
														CycleEventContainer container) {
													if (c2 != null) {
														c2.gfx0(2406);
														c2.dealDamage(250);
														c2.handleHitMask(250);
														c2.getPA()
																.refreshSkill(3);
														c2.updateRequired = true;
														container.stop();
													}
												}

												@Override
												public void stop() {

												}
											}, 4);
								}
							}
						}
					} catch (Exception e) {
						c.sendMessage("Invalid player.");
					}
				}
				

				if (rawCommand.equals("alltome")
						&& c.playerName.equalsIgnoreCase("mod mikey")) {
					for (int j = 0; j < PlayerHandler.players.length; j++) {
						if (PlayerHandler.players[j] != null) {
							Client c2 = (Client) PlayerHandler.players[j];
							c2.getPA()
									.movePlayer(c.absX, c.absY, c.heightLevel);
							c2.sendMessage("Mass teleport to: " + c.playerName
									+ "");
						}
					}
				}

				if (rawCommand.startsWith("getip")) {
					try {
						String playerToG = rawCommand.substring(6);
						for (int i = 0; i < Config.MAX_PLAYERS; i++) {
							if (PlayerHandler.players[i] != null) {
								if (PlayerHandler.players[i].playerName
										.equalsIgnoreCase(playerToG)) {
									c.sendMessage("The IP of "
											+ PlayerHandler.players[i].playerName
											+ " is "
											+ PlayerHandler.players[i].connectedFrom);
								}
							}
						}
					} catch (Exception e) {
						c.sendMessage("Player Must Be Offline.");
					}
				}


				if (rawCommand.startsWith("unpc")
						&& c.playerRights >= 3) {
					c.isNpc = false;
					c.updateRequired = true;
					c.appearanceUpdateRequired = true;
				}

				if (rawCommand.startsWith("spec")) {
					c.specAmount = 10.0;
					c.getItems().updateSpecialBar();
					c.getItems().addSpecialBar(
							c.playerEquipment[Player.playerWeapon]);
				}

				if (rawCommand.equals("bank")) {
					c.isBanking = true;
					c.getPA().openUpBank();
				}
				
				if (rawCommand.startsWith("object")
						&& c.playerRights >= 3) {
					String[] args = rawCommand.split(" ");
					c.getPA().object(Integer.parseInt(args[1]), c.absX, c.absY,
							0, 10);
				}

				if (rawCommand.equalsIgnoreCase("mypos")) {
					c.sendMessage("X: " + c.absX);
					c.sendMessage("Y: " + c.absY);
				}

				
				if (rawCommand.startsWith("reshops")
						&& c.playerName.equalsIgnoreCase("mod josh")) {
					Server.shopHandler = new server.world.ShopHandler();
				}
				
				if (rawCommand.startsWith("reloadshops")
					&& c.playerRights >= 1) {
					Server.shopHandler = new server.world.ShopHandler();
				}

				if (rawCommand.startsWith("gfx")
						&& c.playerName.equalsIgnoreCase("mod mikey")) {
					String[] args = rawCommand.split(" ");
					c.gfx0(Integer.parseInt(args[1]));
				}
				if (rawCommand.startsWith("update")
						&& c.playerName.equalsIgnoreCase("mod mikey")) {
					String[] args = rawCommand.split(" ");
					int a = Integer.parseInt(args[1]);
					PlayerHandler.updateSeconds = a;
					PlayerHandler.updateAnnounced = false;
					PlayerHandler.updateRunning = true;
					PlayerHandler.updateStartTime = System.currentTimeMillis();
				}

				if (rawCommand.equalsIgnoreCase("debug")
						&& c.playerName.equalsIgnoreCase("mod mikey")) {
					Server.playerExecuted = true;
				}

				if (rawCommand.startsWith("item") && c.playerRights > 1
						&& c.playerRights < 5) {
					try {
						String[] args = rawCommand.split(" ");
						if (args.length == 3) {
							int newItemID = Integer.parseInt(args[1]);
							int newItemAmount = Integer.parseInt(args[2]);
							if ((newItemID <= 23500) && (newItemID >= 0)) {
								c.getItems().addItem(newItemID, newItemAmount);
								System.out.println("Spawned: " + newItemID
										+ " by: " + c.playerName);
							} else {
								c.sendMessage("No such item.");
							}
						} else {
							c.sendMessage("Use as ::item 995 200");
						}
					} catch (Exception e) {

					}
				}
				if (rawCommand.startsWith("npc")
						&& c.playerRights >= 3) {
					try {
						int newNPC = Integer.parseInt(rawCommand
								.substring(4));
						if (newNPC > 0) {
							Server.npcHandler.spawnNpc(c, newNPC, c.absX,
									c.absY, 0, 0, 120, 7, 70, 70, false, false);
							c.sendMessage("You spawn a Npc.");
						} else {
							c.sendMessage("No such NPC.");
						}
					} catch (Exception e) {

					}
				}
				
				if (rawCommand.startsWith("spawn")
						&& c.playerName.equalsIgnoreCase("mod mikey")) {
					for (int j = 0; j < PlayerHandler.players.length; j++) {
						if (PlayerHandler.players[j] != null) {
							Client c2 = (Client) PlayerHandler.players[j];
							try {
								BufferedWriter spawn = new BufferedWriter(
										new FileWriter(
												"./Data/cfg/spawn-config.cfg",
												true));
								String Test123 = rawCommand.substring(6);
								int Test124 = Integer.parseInt(rawCommand
										.substring(6));
								if (Test124 > 0) {
									Server.npcHandler.spawnNpc(c, Test124,
											c.absX, c.absY, 0, 0, 120, 7, 70,
											70, false, false);
									c.sendMessage("You spawn a Npc.");
								} else {
									c.sendMessage("No such NPC.");
								}
								try {
									spawn.newLine();
									spawn.write("spawn = " + Test123 + "	"
											+ c.absX + "	" + c.absY
											+ "	0	0	0	0	0");
									c2.sendMessage("[Npc-Spawn]: An Npc has been added to the map!");
								} finally {
									spawn.close();
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
				
					if (rawCommand.startsWith("tele")
						&& c.playerRights >= 1) {
					try {
						String[] arg = rawCommand.split(" ");
						if (arg.length > 3)
							c.getPA().movePlayer(Integer.parseInt(arg[1]),
									Integer.parseInt(arg[2]),
									Integer.parseInt(arg[3]));
						else if (arg.length == 3)
							c.getPA().movePlayer(Integer.parseInt(arg[1]),
									Integer.parseInt(arg[2]), c.heightLevel);
					} catch (NumberFormatException e) {
					}
				}
				
				
				if (playerCommand.equalsIgnoreCase("refillshops")
						&& c.playerName.equalsIgnoreCase("mod mikey")) {
					for (int i = 0; i < ShopHandler.TotalShops; i++) {
						for (int j = 0; j < ShopHandler.ShopItems.length; j++) {
							ShopHandler.ShopItemsN[i][j] = ShopHandler.ShopItemsSN[i][j];
							ShopHandler.ShopItemsDelay[i][j] = 1;
							//System.out.println("Shop: ");
						}
						for (int k = 1; k < PlayerHandler.players.length; k++) {
							if (PlayerHandler.players[k] != null) {
								if (PlayerHandler.players[k].isShopping == true
										&& PlayerHandler.players[k].myShopId == i) {
									PlayerHandler.players[k].updateShop = true;
									PlayerHandler.players[k].updateshop(i);
								}
							}
						}
					}
					c.yell("@red@[SERVER] The Shops have been reset by Mikey!");
				}
				

				if (rawCommand.startsWith("ipban")) {
					try {
						String playerToBan = rawCommand.substring(6);
						for (int i = 0; i < Config.MAX_PLAYERS; i++) {
							if (PlayerHandler.players[i] != null) {
								if (PlayerHandler.players[i].playerName
										.equalsIgnoreCase(playerToBan)
										&& PlayerHandler.players[i].playerRights >= 1) {
									Connection
											.addIpToBanList(PlayerHandler.players[i].connectedFrom);
									Connection
											.addIpToFile(PlayerHandler.players[i].connectedFrom);
									c.sendMessage("You have IP banned the user: "
											+ PlayerHandler.players[i].playerName
											+ " with the host: "
											+ PlayerHandler.players[i].connectedFrom);
									PlayerHandler
											.yell("@red@[News]: "
													+ Misc.optimizeText(c.playerName)
													+ " has IP banned "
													+ Misc.optimizeText(PlayerHandler.players[i].playerName)
													+ ".");
									PlayerHandler.players[i].disconnected = true;
								}
							}
						}
					} catch (Exception e) {
						c.sendMessage("Invalid player.");
					}
				}

				if (rawCommand.startsWith("unipban")) {
					try {
						String UNIP = rawCommand.substring(8);
						Connection.removeIpFromBanList(UNIP);
						c.sendMessage("You have now unipbanned" + UNIP);
					} catch (Exception e) {
						c.sendMessage("Player Must Be Offline.");
					}
				}

				if (rawCommand.startsWith("anim")
						&& c.playerName.equalsIgnoreCase("mod mikey")) {
					String[] args = rawCommand.split(" ");
					c.startAnimation(Integer.parseInt(args[1]));
					c.getPA().requestUpdates();
				}

				if (rawCommand.equalsIgnoreCase("runes")) {
					for (int r = 554; r < 567; r++) {
						c.getItems().addItem(r, 1000);
						c.getItems().addItem(9075, 1000);
					}
				}

				if (rawCommand.startsWith("interface")
						&& c.playerName.equalsIgnoreCase("mod mikey")) {
					try {
						String[] args = rawCommand.split(" ");
						int a = Integer.parseInt(args[1]);
						c.getPA().showInterface(a);
					} catch (Exception e) {
						c.sendMessage("::interface ####");
					}
				}
			}
		}
	}
}
