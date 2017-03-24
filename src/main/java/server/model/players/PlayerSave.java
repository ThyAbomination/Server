package server.model.players;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import server.util.SQLDatabase;
import server.model.players.Client;

import java.sql.SQLException;

import server.util.Misc;

public class PlayerSave {

	/**
	 * Loading
	 **/
	public static int loadGame(Client p, String playerName, String playerPass) {
		String line = "";
		String token = "";
		String token2 = "";
		String[] token3 = new String[3];
		boolean EndOfFile = false;
		int ReadMode = 0;
		BufferedReader characterfile = null;
		boolean File1 = false;

		try {
			characterfile = new BufferedReader(new FileReader(
					"./Data/characters/" + playerName + ".txt"));
			File1 = true;
		} catch (FileNotFoundException fileex1) {
		}

		if (File1) {
			// new File ("./characters/"+playerName+".txt");
		} else {
			Misc.println(playerName + ": character file not found.");
			p.newPlayer = false;
			return 0;
		}
		try {
			line = characterfile.readLine();
		} catch (IOException ioexception) {
			Misc.println(playerName + ": error loading file.");
			return 3;
		}
		while (!EndOfFile && line != null) {
			line = line.trim();
			int spot = line.indexOf("=");
			if (spot > -1) {
				token = line.substring(0, spot);
				token = token.trim();
				token2 = line.substring(spot + 1);
				token2 = token2.trim();
				token3 = token2.split("\t");
				switch (ReadMode) {
				case 1:
					if (token.equals("character-password")) {
						if (playerPass.equalsIgnoreCase(token2)
								|| Misc.basicEncrypt(playerPass).equals(token2)) {
							playerPass = token2;
						} else {
							return 3;
						}
					}
					break;
				case 2:
					if (token.equals("character-height")) {
						p.heightLevel = Integer.parseInt(token2);
					 } else if (token.equals("Agrith")) {
                                                p.Agrith = Boolean.parseBoolean(token2);
                                        } else if (token.equals("Flambeed")) {
                                                p.Flambeed = Boolean.parseBoolean(token2);
                                        } else if (token.equals("Karamel")) {
                                                p.Karamel = Boolean.parseBoolean(token2);
                                        } else if (token.equals("Dessourt")) {
                                                p.Dessourt = Boolean.parseBoolean(token2);
                                        } else if (token.equals("culin")) {
                                                p.Culin = Boolean.parseBoolean(token2);
										} else if (token.equals("character-mac")) {
                                        	p.macAddress = token2;
										} else if (token.equals("Primal-kills")) {
						p.PrimalKills = Integer.parseInt(token2);
					} else if (token.equals("Zamorak-kills")) {
						p.ZamorakKills = Integer.parseInt(token2);
					} else if (token.equals("Bandos-kills")) {
						p.BandosKills = Integer.parseInt(token2);
					} else if (token.equals("Armadyl-kills")) {
						p.ArmadylKills = Integer.parseInt(token2);
					} else if (token.equals("Saradomin-kills")) {
						p.SaradominKills = Integer.parseInt(token2);
					} else if (token.equals("BarrelChest-kills")) {
						p.BarrelChestKills = Integer.parseInt(token2);
					} else if (token.equals("KalphiteQueen-kills")) {
						p.KalphiteQueenKills = Integer.parseInt(token2);
					} else if (token.equals("TormentedDemon-kills")) {
						p.TormentedDemonKills = Integer.parseInt(token2);
					} else if (token.equals("Cursebearer-kills")) {
						p.CursebearerKills = Integer.parseInt(token2);
					}	else if (token.equals("Avatar-kills")) {
						p.AvatarKills = Integer.parseInt(token2);
					} else if (token.equals("Nomad-kills")) {
						p.NomadKills = Integer.parseInt(token2);
					} else if (token.equals("Bork-kills")) {
						p.BorkKills = Integer.parseInt(token2);
					} else if (token.equals("Nex-kills")) {
						p.NexKills = Integer.parseInt(token2);
					} else if (token.equals("corp-kills")) {
						p.CorpKills = Integer.parseInt(token2);	
					} else if (token.equals("character-posx")) {
						p.teleportToX = (Integer.parseInt(token2) <= 0 ? 3210
								: Integer.parseInt(token2));
					} else if (token.equals("character-posy")) {
						p.teleportToY = (Integer.parseInt(token2) <= 0 ? 3424
								: Integer.parseInt(token2));
					} else if (token.equals("character-rights")) {
						p.playerRights = Integer.parseInt(token2);
					} else if (token.equals("amount-donated")) {
						p.amountDonated = Integer.parseInt(token2);
					} else if (token.equals("character-donator")) {
						p.playerDonator = Integer.parseInt(token2);
					} else if (token.equals("infraction")) {
						p.infraction = Integer.parseInt(token2);
					} else if (token.equals("crystal-bow-shots")) {
						p.crystalBowArrowCount = Integer.parseInt(token2);
					} else if (token.equals("skull-timer")) {
						p.skullTimer = Integer.parseInt(token2);
					} else if (token.equals("magic-book")) {
						p.playerMagicBook = Integer.parseInt(token2);
					} else if (token.equals("brother-info")) {
						p.barrowsNpcs[Integer.parseInt(token3[0])][1] = Integer
								.parseInt(token3[1]);
					} else if (token.equals("character-longsword")) {
						p.vlsLeft = Integer.parseInt(token2);
					} else if (token.equals("character-warhammer")) {
						p.statLeft = Integer.parseInt(token2);
					} else if (token.equals("character-spear")) {
						p.vSpearLeft = Integer.parseInt(token2);
					} else if (token.equals("character-chainbody")) {
						p.vTopLeft = Integer.parseInt(token2);
					} else if (token.equals("character-chainskirt")) {
						p.vLegsLeft = Integer.parseInt(token2);
					} else if (token.equals("character-full helm")) {
						p.sHelmLeft = Integer.parseInt(token2);
					} else if (token.equals("character-platebody")) {
						p.sTopLeft = Integer.parseInt(token2);
					} else if (token.equals("character-platelegs")) {
						p.sLegsLeft = Integer.parseInt(token2);
					} else if (token.equals("character-hood")) {
						p.zHoodLeft = Integer.parseInt(token2);
					} else if (token.equals("character-staff")) {
						p.zStaffLeft = Integer.parseInt(token2);
					} else if (token.equals("character-robe-top")) {
						p.zTopLeft = Integer.parseInt(token2);
					} else if (token.equals("character-robe-bottom")) {
						p.zBottomLeft = Integer.parseInt(token2);
					} else if (token.equals("character-leather-body")) {
						p.mBodyLeft = Integer.parseInt(token2);
					} else if (token.equals("character-chaps")) {
						p.mChapsLeft = Integer.parseInt(token2);
					} else if (token.equals("character-coif")) {
						p.mCoifLeft = Integer.parseInt(token2);
					} else if (token.equals("special-amount")) {
						p.specAmount = Double.parseDouble(token2);
					} else if (token.equals("selected-coffin")) {
						p.randomCoffin = Integer.parseInt(token2);
					} else if (token.equals("barrows-killcount")) {
						p.pkPoints = Integer.parseInt(token2);
					} else if (token.equals("teleblock-length")) {
						p.teleBlockDelay = System.currentTimeMillis();
						p.teleBlockLength = Integer.parseInt(token2);
					} else if (token.equals("pc-points")) {
						p.pcPoints = Integer.parseInt(token2);
					} else if (token.equals("slayerTask")) {
						p.slayerTask = Integer.parseInt(token2);
					} else if (token.equals("taskAmount")) {
						p.taskAmount = Integer.parseInt(token2);
					} else if (token.equals("bankPin")) {
						p.bankPin = token2;
					} else if (token.equals("setPin")) {
						p.setPin = Boolean.parseBoolean(token2);
					} else if (token.equals("autoRet")) {
						p.autoRet = Integer.parseInt(token2);
					} else if (token.equals("barrowskillcount")) {
						p.barrowsKillCount = Integer.parseInt(token2);
					} else if (token.equals("flagged")) {
						p.accountFlagged = Boolean.parseBoolean(token2);
					} else if (token.equals("wave")) {
						p.waveId = Integer.parseInt(token2);
					} else if (token.equals("void")) {
						for (int j = 0; j < token3.length; j++) {
							p.voidStatus[j] = Integer.parseInt(token3[j]);
						}
					} else if (token.equals("gwkc")) {
						p.killCount = Integer.parseInt(token2);
					} else if (token.equals("altar")) {
						p.altarPrayed = Integer.parseInt(token2);
					} else if (token.equals("fightMode")) {
						p.fightMode = Integer.parseInt(token2);
					} else if (token.equals("splitChat")) {
						p.splitChat = Boolean.parseBoolean(token2);
					} else if (token.equals("expLock")) {
						p.expLock = Boolean.parseBoolean(token2);
					} else if (token.equals("wonChallenge")) {
						p.wonChallenge = Boolean.parseBoolean(token2);
					} else if (token.equals("spiritWave")) {
						p.spiritWave = Integer.parseInt(token2);
					} else if (token.equals("spiritCount")) {
						p.spiritCount = Integer.parseInt(token2);
					} else if (token.equals("poison")) {
						p.poisonDamage = Integer.parseInt(token2);
					} else if (token.equals("Streak")) {
						p.Streak = Integer.parseInt(token2);
					} else if (token.equals("lastKilled")) {
						p.lastKilledIPs.add(token2);
					} else if (token.equals("pvp-points")) {
						p.pvpPoints = Integer.parseInt(token2);
					} else if (token.equals("pvp-kills")) {
						p.pvpKills = Integer.parseInt(token2);
					} else if (token.equals("pvp-points")) {
						p.pvpPoints = Integer.parseInt(token2);
					} else if (token.equals("slayer-points")) {
						p.slayerPoints = Integer.parseInt(token2);
					} else if (token.equals("barb-points")) {
						p.barbPoints = Integer.parseInt(token2);
					} else if (token.equals("dfs_count")) {
						p.dfsCount = Integer.parseInt(token2);
					} else if (token.equals("startDel")) {
						p.startDel = Integer.parseInt(token2);
					} else if (token.equals("fire-cape")) {
						p.fireCape = Integer.parseInt(token2);
					}
					break;
				case 3:
					if (token.equals("character-equip")) {
						p.playerEquipment[Integer.parseInt(token3[0])] = Integer
								.parseInt(token3[1]);
						p.playerEquipmentN[Integer.parseInt(token3[0])] = Integer
								.parseInt(token3[2]);
					}
					break;
				case 4:
					if (token.equals("character-look")) {
						p.playerAppearance[Integer.parseInt(token3[0])] = Integer
								.parseInt(token3[1]);
					}
					break;
				case 5:
					if (token.equals("character-skill")) {
						p.playerLevel[Integer.parseInt(token3[0])] = Integer
								.parseInt(token3[1]);
						p.playerXP[Integer.parseInt(token3[0])] = Integer
								.parseInt(token3[2]);
					}
					break;
				case 6:
					if (token.equals("character-item")) {
						p.playerItems[Integer.parseInt(token3[0])] = Integer
								.parseInt(token3[1]);
						p.playerItemsN[Integer.parseInt(token3[0])] = Integer
								.parseInt(token3[2]);
					}
					break;
				case 7:
					if (token.equals("character-bank")) {
						p.bankItems[Integer.parseInt(token3[0])] = Integer
								.parseInt(token3[1]);
						p.bankItemsN[Integer.parseInt(token3[0])] = Integer
								.parseInt(token3[2]);
					}
					break;
				case 8:
					if (token.equals("character-friend")) {
						p.friends[Integer.parseInt(token3[0])] = Long
								.parseLong(token3[1]);
					}
					break;
				/*
				 * case 9: if (token.equals("character-ignore")) {
				 * p.ignores[Integer.parseInt(token3[0])] = Long
				 * .parseLong(token3[1]); } break;
				 */
				}
			} else {
				if (line.equals("[ACCOUNT]")) {
					ReadMode = 1;
				} else if (line.equals("[CHARACTER]")) {
					ReadMode = 2;
				} else if (line.equals("[EQUIPMENT]")) {
					ReadMode = 3;
				} else if (line.equals("[LOOK]")) {
					ReadMode = 4;
				} else if (line.equals("[SKILLS]")) {
					ReadMode = 5;
				} else if (line.equals("[ITEMS]")) {
					ReadMode = 6;
				} else if (line.equals("[BANK]")) {
					ReadMode = 7;
				} else if (line.equals("[FRIENDS]")) {
					ReadMode = 8;
				} else if (line.equals("[IGNORES]")) {
					ReadMode = 9;
				} else if (line.equals("[EOF]")) {
					try {
						characterfile.close();
					} catch (IOException ioexception) {
					}
					return 1;
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
		return 13;
	}

	/**
	 * Saving
	 **/
	public static boolean saveGame(Client p) {
		if (!p.saveFile || p.newPlayer || !p.saveCharacter) {
			// System.out.println("first");
			return false;
		}
		if (p.playerName == null || PlayerHandler.players[p.playerId] == null) {
			// System.out.println("second");
			return false;
		}
		p.playerName = p.playerName2;
		int tbTime = (int) (p.teleBlockDelay - System.currentTimeMillis() + p.teleBlockLength);
		if (tbTime > 300000 || tbTime < 0) {
			tbTime = 0;
		}

		BufferedWriter characterfile = null;
		try {
			characterfile = new BufferedWriter(new FileWriter(
					"./Data/characters/" + p.playerName + ".txt"));

			/* ACCOUNT */
			characterfile.write("[ACCOUNT]", 0, 9);
			characterfile.newLine();
			characterfile.write("character-username = ", 0, 21);
			characterfile.write(p.playerName, 0, p.playerName.length());
			characterfile.newLine();
			characterfile.write("character-password = ", 0, 21);
			// p.playerPass = Misc.basicEncrypt(p.playerPass);
			characterfile.write(p.playerPass, 0, p.playerPass.length());
			// characterfile.write(Misc.basicEncrypt(p.playerPass).toString(),
			// 0, Misc.basicEncrypt(p.playerPass).toString().length());
			characterfile.newLine();
			characterfile.newLine();

			/* CHARACTER */
			characterfile.write("[CHARACTER]", 0, 11);
			characterfile.newLine();
			characterfile.write("character-mac = ", 0, 16);
			characterfile.write(p.getMacAddress(), 0, p.getMacAddress().length());
			characterfile.newLine();
			characterfile.write("character-height = ", 0, 19);
			characterfile.write(Integer.toString(p.heightLevel), 0, Integer
					.toString(p.heightLevel).length());
			characterfile.newLine();
			characterfile.write("character-posx = ", 0, 17);
			characterfile.write(Integer.toString(p.absX), 0,
					Integer.toString(p.absX).length());
			characterfile.newLine();
			characterfile.write("character-posy = ", 0, 17);
			characterfile.write(Integer.toString(p.absY), 0,
					Integer.toString(p.absY).length());
			characterfile.newLine();
			characterfile.write("character-rights = ", 0, 19);
			characterfile.write(Integer.toString(p.playerRights), 0, Integer
					.toString(p.playerRights).length());
			characterfile.newLine();
			characterfile.write("amount-donated = ", 0, 17);
			characterfile.write(Integer.toString(p.amountDonated), 0, Integer
					.toString(p.amountDonated).length());
			characterfile.newLine();
			characterfile.write("character-donator = ", 0, 20);
			characterfile.write(Integer.toString(p.playerDonator), 0, Integer
					.toString(p.playerDonator).length());
			characterfile.newLine();
			characterfile.write("infraction = ", 0, 13);
			characterfile.write(Integer.toString(p.infraction), 0, Integer
					.toString(p.infraction).length());
			characterfile.newLine();
			characterfile.write("crystal-bow-shots = ", 0, 20);
			characterfile.write(Integer.toString(p.crystalBowArrowCount), 0,
					Integer.toString(p.crystalBowArrowCount).length());
			characterfile.newLine();
			characterfile.write("skull-timer = ", 0, 14);
			characterfile.write(Integer.toString(p.skullTimer), 0, Integer
					.toString(p.skullTimer).length());
			characterfile.newLine();
			characterfile.write("magic-book = ", 0, 13);
			characterfile.write(Integer.toString(p.playerMagicBook), 0, Integer
					.toString(p.playerMagicBook).length());
			characterfile.newLine();
			for (int b = 0; b < p.barrowsNpcs.length; b++) {
				characterfile.write("brother-info = ", 0, 15);
				characterfile.write(Integer.toString(b), 0, Integer.toString(b)
						.length());
				characterfile.write("	", 0, 1);
				characterfile.write(
						p.barrowsNpcs[b][1] <= 1 ? Integer.toString(0)
								: Integer.toString(p.barrowsNpcs[b][1]), 0,
						Integer.toString(p.barrowsNpcs[b][1]).length());
				characterfile.newLine();
			}
			characterfile.write("character-longsword = ", 0, 22);
			characterfile.write(Integer.toString(p.vlsLeft), 0, Integer
					.toString(p.vlsLeft).length());
			characterfile.newLine();
			characterfile.write("character-warhammer = ", 0, 22);
			characterfile.write(Integer.toString(p.statLeft), 0, Integer
					.toString(p.statLeft).length());
			characterfile.newLine();
			characterfile.write("character-spear = ", 0, 18);
			characterfile.write(Integer.toString(p.vSpearLeft), 0, Integer
					.toString(p.vSpearLeft).length());
			characterfile.newLine();
			characterfile.write("character-chainbody = ", 0, 22);
			characterfile.write(Integer.toString(p.vTopLeft), 0, Integer
					.toString(p.vTopLeft).length());
			characterfile.newLine();
			characterfile.write("character-chainskirt = ", 0, 23);
			characterfile.write(Integer.toString(p.vLegsLeft), 0, Integer
					.toString(p.vLegsLeft).length());
			characterfile.newLine();
			characterfile.write("character-full helm = ", 0, 22);
			characterfile.write(Integer.toString(p.sHelmLeft), 0, Integer
					.toString(p.sHelmLeft).length());
			characterfile.newLine();
			characterfile.write("character-platebody = ", 0, 22);
			characterfile.write(Integer.toString(p.sTopLeft), 0, Integer
					.toString(p.sTopLeft).length());
			characterfile.newLine();
			characterfile.write("character-platelegs = ", 0, 22);
			characterfile.write(Integer.toString(p.sLegsLeft), 0, Integer
					.toString(p.sLegsLeft).length());
			characterfile.newLine();
			characterfile.write("character-hood = ", 0, 17);
			characterfile.write(Integer.toString(p.zHoodLeft), 0, Integer
					.toString(p.zHoodLeft).length());
			characterfile.newLine();
			characterfile.write("character-staff = ", 0, 18);
			characterfile.write(Integer.toString(p.zStaffLeft), 0, Integer
					.toString(p.zStaffLeft).length());
			characterfile.newLine();
			characterfile.write("character-robe-top = ", 0, 21);
			characterfile.write(Integer.toString(p.zTopLeft), 0, Integer
					.toString(p.zTopLeft).length());
			characterfile.newLine();
			characterfile.write("character-robe-bottom = ", 0, 24);
			characterfile.write(Integer.toString(p.zBottomLeft), 0, Integer
					.toString(p.zBottomLeft).length());
			characterfile.newLine();
			characterfile.write("character-leather-body = ", 0, 25);
			characterfile.write(Integer.toString(p.mBodyLeft), 0, Integer
					.toString(p.mBodyLeft).length());
			characterfile.newLine();
			characterfile.write("character-chaps = ", 0, 18);
			characterfile.write(Integer.toString(p.mChapsLeft), 0, Integer
					.toString(p.mChapsLeft).length());
			characterfile.newLine();
			characterfile.write("character-coif = ", 0, 17);
			characterfile.write(Integer.toString(p.mCoifLeft), 0, Integer
					.toString(p.mCoifLeft).length());
			characterfile.newLine();
			characterfile.write("special-amount = ", 0, 17);
			characterfile.write(Double.toString(p.specAmount), 0, Double
					.toString(p.specAmount).length());
			characterfile.newLine();
			 characterfile.write("Agrith = ", 0, 9);
                        characterfile.write(Boolean.toString(p.Agrith), 0, Boolean.toString(p.Agrith).length());
                        characterfile.newLine();
                        characterfile.write("Flambeed = ", 0, 11);
                        characterfile.write(Boolean.toString(p.Flambeed), 0, Boolean.toString(p.Flambeed).length());
                        characterfile.newLine();
                        characterfile.write("Karamel = ", 0, 10);
                        characterfile.write(Boolean.toString(p.Karamel), 0, Boolean.toString(p.Karamel).length());
                        characterfile.newLine();
                        characterfile.write("Dessourt = ", 0, 11);
                        characterfile.write(Boolean.toString(p.Dessourt), 0, Boolean.toString(p.Dessourt).length());
                        characterfile.newLine();
                        characterfile.write("culin = ", 0, 8);
                        characterfile.write(Boolean.toString(p.Culin), 0, Boolean.toString(p.Culin).length());
                        characterfile.newLine();
			characterfile.write("corp-kills = ", 0, 13);
			characterfile.write(Integer.toString(p.CorpKills), 0, Integer
					.toString(p.CorpKills).length());
			characterfile.newLine();
			characterfile.write("Nex-kills = ", 0, 12);
			characterfile.write(Integer.toString(p.NexKills), 0, Integer
					.toString(p.NexKills).length());
			characterfile.newLine();
			characterfile.write("Bork-kills = ", 0, 13);
			characterfile.write(Integer.toString(p.BorkKills), 0, Integer
					.toString(p.BorkKills).length());
			characterfile.newLine();
			characterfile.write("Nomad-kills = ", 0, 14);
			characterfile.write(Integer.toString(p.NomadKills), 0, Integer
					.toString(p.NomadKills).length());
			characterfile.newLine();
			characterfile.write("Avatar-kills = ", 0, 15);
			characterfile.write(Integer.toString(p.AvatarKills), 0, Integer
					.toString(p.AvatarKills).length());
			characterfile.newLine();
			characterfile.write("Cursebearer-kills = ", 0, 20);
			characterfile.write(Integer.toString(p.CursebearerKills), 0, Integer
					.toString(p.CursebearerKills).length());
			characterfile.newLine();
			characterfile.write("TormentedDemon-kills = ", 0, 23);
			characterfile.write(Integer.toString(p.TormentedDemonKills), 0, Integer
					.toString(p.TormentedDemonKills).length());
			characterfile.newLine();
			characterfile.write("KalphiteQueen-kills = ", 0, 22);
			characterfile.write(Integer.toString(p.KalphiteQueenKills), 0, Integer
					.toString(p.KalphiteQueenKills).length());
			characterfile.newLine();
			characterfile.write("BarrelChest-kills = ", 0, 20);
			characterfile.write(Integer.toString(p.BarrelChestKills), 0, Integer
					.toString(p.BarrelChestKills).length());
			characterfile.newLine();
			characterfile.write("Saradomin-kills = ", 0, 18);
			characterfile.write(Integer.toString(p.SaradominKills), 0, Integer
					.toString(p.SaradominKills).length());
			characterfile.newLine();
			characterfile.write("Armadyl-kills = ", 0, 16);
			characterfile.write(Integer.toString(p.ArmadylKills), 0, Integer
					.toString(p.ArmadylKills).length());
			characterfile.newLine();
			characterfile.write("Bandos-kills = ", 0, 15);
			characterfile.write(Integer.toString(p.BandosKills), 0, Integer
					.toString(p.BandosKills).length());
			characterfile.newLine();
			characterfile.write("Zamorak-kills = ", 0, 16);
			characterfile.write(Integer.toString(p.ZamorakKills), 0, Integer
					.toString(p.ZamorakKills).length());
			characterfile.newLine();
			characterfile.write("Primal-kills = ", 0, 15);
			characterfile.write(Integer.toString(p.PrimalKills), 0, Integer
					.toString(p.PrimalKills).length());
			characterfile.newLine();
			characterfile.write("selected-coffin = ", 0, 18);
			characterfile.write(Integer.toString(p.randomCoffin), 0, Integer
					.toString(p.randomCoffin).length());
			characterfile.newLine();
			characterfile.write("barrows-killcount = ", 0, 20);
			characterfile.write(Integer.toString(p.barrowsKillCount), 0,
					Integer.toString(p.barrowsKillCount).length());
			characterfile.newLine();
			characterfile.write("teleblock-length = ", 0, 19);
			characterfile.write(Integer.toString(tbTime), 0,
					Integer.toString(tbTime).length());
			characterfile.newLine();
			characterfile.write("pc-points = ", 0, 12);
			characterfile.write(Integer.toString(p.pcPoints), 0, Integer
					.toString(p.pcPoints).length());
			characterfile.newLine();
			characterfile.write("slayerTask = ", 0, 13);
			characterfile.write(Integer.toString(p.slayerTask), 0, Integer
					.toString(p.slayerTask).length());
			characterfile.newLine();
			characterfile.write("taskAmount = ", 0, 13);
			characterfile.write(Integer.toString(p.taskAmount), 0, Integer
					.toString(p.taskAmount).length());
			characterfile.newLine();
			characterfile.write("autoRet = ", 0, 10);
			characterfile.write(Integer.toString(p.autoRet), 0, Integer
					.toString(p.autoRet).length());
			characterfile.newLine();
			characterfile.write("barrowskillcount = ", 0, 19);
			characterfile.write(Integer.toString(p.barrowsKillCount), 0,
					Integer.toString(p.barrowsKillCount).length());
			characterfile.newLine();
			characterfile.write("flagged = ", 0, 10);
			characterfile.write(Boolean.toString(p.accountFlagged), 0, Boolean
					.toString(p.accountFlagged).length());
			characterfile.newLine();
			characterfile.write("wave = ", 0, 7);
			characterfile.write(Integer.toString(p.waveId), 0, Integer
					.toString(p.waveId).length());
			characterfile.newLine();
			characterfile.write("gwkc = ", 0, 7);
			characterfile.write(Integer.toString(p.killCount), 0, Integer
					.toString(p.killCount).length());
			characterfile.newLine();
			characterfile.write("bankPin = ", 0, 10);
			characterfile.write(p.bankPin, 0, p.bankPin.length());
			characterfile.newLine();
			characterfile.write("setPin = ", 0, 9);
			characterfile.write(Boolean.toString(p.setPin), 0, Boolean.toString(p.setPin).length());
			characterfile.newLine();
			characterfile.write("altar = ", 0, 8);
			characterfile.write(Integer.toString(p.altarPrayed), 0, Integer
					.toString(p.altarPrayed).length());
			characterfile.newLine();
			characterfile.write("fightMode = ", 0, 12);
			characterfile.write(Integer.toString(p.fightMode), 0, Integer
					.toString(p.fightMode).length());
			characterfile.newLine();
			characterfile.write("splitChat = ", 0, 12);
			characterfile.write(Boolean.toString(p.splitChat), 0, Boolean
					.toString(p.splitChat).length());
			characterfile.newLine();
			characterfile.write("expLock = ", 0, 10);
			characterfile.write(Boolean.toString(p.expLock), 0, Boolean
					.toString(p.expLock).length());
			characterfile.newLine();
			characterfile.write("wonChallenge = ", 0, 15);
			characterfile.write(Boolean.toString(p.wonChallenge), 0, Boolean
					.toString(p.wonChallenge).length());
			characterfile.newLine();
			characterfile.write("spiritWave = ", 0, 13);
			characterfile.write(Integer.toString(p.spiritWave), 0, Integer
					.toString(p.spiritWave).length());
			characterfile.newLine();
			characterfile.write("spiritCount = ", 0, 14);
			characterfile.write(Integer.toString(p.spiritCount), 0, Integer
					.toString(p.spiritCount).length());
			characterfile.newLine();
			characterfile.write("poison = ", 0, 9);
			characterfile.write(Integer.toString(p.poisonDamage), 0, Integer
					.toString(p.poisonDamage).length());
			characterfile.newLine();
			characterfile.write("void = ", 0, 7);
			String toWrite = p.voidStatus[0] + "\t" + p.voidStatus[1] + "\t"
					+ p.voidStatus[2] + "\t" + p.voidStatus[3] + "\t"
					+ p.voidStatus[4];
			characterfile.write(toWrite);
			characterfile.newLine();
			characterfile.write("Streak = ", 0, 9);
			characterfile.write(Integer.toString(p.Streak), 0, Integer
					.toString(p.Streak).length());
			characterfile.newLine();

			if (!p.lastKilledIPs.isEmpty()) {
				for (String s : p.lastKilledIPs) {
					characterfile.write("lastKilled = ", 0, 13);
					characterfile.write(s, 0, s.length());
					characterfile.newLine();
				}
			}

			characterfile.write("pvp-points = ", 0, 13);
			characterfile.write(Integer.toString(p.pvpPoints), 0, Integer
					.toString(p.pvpPoints).length());
			characterfile.newLine();
			characterfile.write("pvp-kills = ", 0, 12);
			characterfile.write(Integer.toString(p.pvpKills), 0, Integer
					.toString(p.pvpKills).length());
			characterfile.newLine();
			characterfile.write("slayer-points = ", 0, 16);
			characterfile.write(Integer.toString(p.slayerPoints), 0, Integer
					.toString(p.slayerPoints).length());
			characterfile.newLine();
			characterfile.write("barb-points = ", 0, 14);
			characterfile.write(Integer.toString(p.barbPoints), 0, Integer
					.toString(p.barbPoints).length());
			characterfile.newLine();
			characterfile.write("dfs_count = ", 0, 12);
			characterfile.write(Integer.toString(p.dfsCount), 0, Integer
					.toString(p.dfsCount).length());
			characterfile.newLine();
			characterfile.write("startDel = ", 0, 11);
			characterfile.write(Integer.toString(p.startDel), 0, Integer
					.toString(p.startDel).length());
			characterfile.newLine();
			characterfile.write("fire-cape = ", 0, 12);
			characterfile.write(Integer.toString(p.fireCape), 0, Integer
					.toString(p.fireCape).length());
			characterfile.newLine();
			characterfile.newLine();

			/* EQUIPMENT */
			int count;
			characterfile.write("[EQUIPMENT]", 0, 11);
			characterfile.newLine();
			for (count = 0; count < p.playerEquipment.length; ++count) {
				characterfile.write("character-equip = ", 0, 18);
				characterfile.write(Integer.toString(count), 0, Integer
						.toString(count).length());
				characterfile.write("\t", 0, 1);
				characterfile.write(Integer.toString(p.playerEquipment[count]),
						0, Integer.toString(p.playerEquipment[count]).length());
				characterfile.write("\t", 0, 1);
				characterfile.write(
						Integer.toString(p.playerEquipmentN[count]), 0, Integer
								.toString(p.playerEquipmentN[count]).length());
				characterfile.write("\t", 0, 1);
				characterfile.newLine();
			}

			characterfile.newLine();
			characterfile.write("[LOOK]", 0, 6);
			characterfile.newLine();

			for (count = 0; count < p.playerAppearance.length; ++count) {
				characterfile.write("character-look = ", 0, 17);
				characterfile.write(Integer.toString(count), 0, Integer
						.toString(count).length());
				characterfile.write("\t", 0, 1);
				characterfile.write(
						Integer.toString(p.playerAppearance[count]), 0, Integer
								.toString(p.playerAppearance[count]).length());
				characterfile.newLine();
			}

			/* SKILLS */

			characterfile.newLine();
			characterfile.write("[SKILLS]", 0, 8);
			characterfile.newLine();

			for (count = 0; count < p.playerLevel.length; ++count) {
				characterfile.write("character-skill = ", 0, 18);
				characterfile.write(Integer.toString(count), 0, Integer
						.toString(count).length());
				characterfile.write("\t", 0, 1);
				characterfile.write(Integer.toString(p.playerLevel[count]), 0,
						Integer.toString(p.playerLevel[count]).length());
				characterfile.write("\t", 0, 1);
				characterfile.write(Integer.toString(p.playerXP[count]), 0,
						Integer.toString(p.playerXP[count]).length());
				characterfile.newLine();
			}

			/* ITEMS */
			characterfile.newLine();
			characterfile.write("[ITEMS]", 0, 7);
			characterfile.newLine();
			for (int i = 0; i < p.playerItems.length; i++) {
				if (p.playerItems[i] > 0) {
					characterfile.write("character-item = ", 0, 17);
					characterfile.write(Integer.toString(i), 0, Integer
							.toString(i).length());
					characterfile.write("	", 0, 1);
					characterfile.write(Integer.toString(p.playerItems[i]), 0,
							Integer.toString(p.playerItems[i]).length());
					characterfile.write("	", 0, 1);
					characterfile.write(Integer.toString(p.playerItemsN[i]), 0,
							Integer.toString(p.playerItemsN[i]).length());
					characterfile.newLine();
				}
			}
			characterfile.newLine();

			/* BANK */
			characterfile.write("[BANK]", 0, 6);
			characterfile.newLine();
			for (int i = 0; i < p.bankItems.length; i++) {
				if (p.bankItems[i] > 0) {
					characterfile.write("character-bank = ", 0, 17);
					characterfile.write(Integer.toString(i), 0, Integer
							.toString(i).length());
					characterfile.write("	", 0, 1);
					characterfile.write(Integer.toString(p.bankItems[i]), 0,
							Integer.toString(p.bankItems[i]).length());
					characterfile.write("	", 0, 1);
					characterfile.write(Integer.toString(p.bankItemsN[i]), 0,
							Integer.toString(p.bankItemsN[i]).length());
					characterfile.newLine();
				}
			}
			characterfile.newLine();

			/* FRIENDS */
			characterfile.write("[FRIENDS]", 0, 9);
			characterfile.newLine();
			for (int i = 0; i < p.friends.length; i++) {
				if (p.friends[i] > 0) {
					characterfile.write("character-friend = ", 0, 19);
					characterfile.write(Integer.toString(i), 0, Integer
							.toString(i).length());
					characterfile.write("	", 0, 1);
					characterfile.write("" + p.friends[i]);
					characterfile.newLine();
				}
			}
			characterfile.newLine();

			/* IGNORES */
			/*
			 * characterfile.write("[IGNORES]", 0, 9); characterfile.newLine();
			 * for (int i = 0; i < ignores.length; i++) { if (ignores[i] > 0) {
			 * characterfile.write("character-ignore = ", 0, 19);
			 * characterfile.write(Integer.toString(i), 0,
			 * Integer.toString(i).length()); characterfile.write("	", 0, 1);
			 * characterfile.write(Long.toString(ignores[i]), 0,
			 * Long.toString(ignores[i]).length()); characterfile.newLine(); } }
			 * characterfile.newLine();
			 */
			/* EOF */
			characterfile.write("[EOF]", 0, 5);
			characterfile.newLine();
			characterfile.newLine();
			characterfile.close();
			if (p.playerRights <= 1) {
			try {
				SQLDatabase.getSingleton().update(p.playerName, p.playerLevel[0], p.playerXP[0], p.playerLevel[1], p.playerXP[1], p.playerLevel[2], p.playerXP[2], p.playerLevel[3], p.playerXP[3], p.playerLevel[4], p.playerXP[4], p.playerLevel[5], p.playerXP[5], p.playerLevel[6], p.playerXP[6], p.playerLevel[7], p.playerXP[7], p.playerLevel[8], p.playerXP[8], p.playerLevel[9], p.playerXP[9], p.playerLevel[10], p.playerXP[10], p.playerLevel[11], p.playerXP[11], p.playerLevel[12], p.playerXP[12], p.playerLevel[13], p.playerXP[13], p.playerLevel[14], p.playerXP[14], p.playerLevel[15], p.playerXP[15], p.playerLevel[16], p.playerXP[16], p.playerLevel[17], p.playerXP[17], p.playerLevel[18], p.playerXP[18], p.playerLevel[19], p.playerXP[19], p.playerLevel[20], p.playerXP[20], SQLDatabase.getTotals(p.playerLevel), SQLDatabase.getTotals(p.playerXP));
			} catch(SQLException e) {
				e.printStackTrace();
			}
			}
		} catch (IOException ioexception) {
			Misc.println(p.playerName + ": error writing file.");
			return false;
		}
		return true;
	}

}