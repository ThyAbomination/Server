package server.model.npcs;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import server.model.minigames.RFD;
import server.Config;
import server.Server;
import server.clip.region.Region;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.event.Event;
import server.event.EventContainer;
import server.event.EventManager;
import server.model.minigames.FightCaves;
import server.model.minigames.PestControl;
import server.model.players.Client;
import server.model.players.Player;
import server.model.players.PlayerHandler;
import server.util.Misc;

public class NPCHandler {
	public static int maxNPCs = 10000;
	public static int maxListedNPCs = 10000;
	public static int maxNPCDrops = 10000;
	public static NPC npcs[] = new NPC[maxNPCs];
	public static NPCList NpcList[] = new NPCList[maxListedNPCs];

	public NPCHandler() {
		for (int i = 0; i < maxNPCs; i++) {
			npcs[i] = null;
		}
		for (int i = 0; i < maxListedNPCs; i++) {
			NpcList[i] = null;
		}
		loadNPCList("./Data/CFG/npc.cfg");
		loadAutoSpawn("./Data/CFG/spawn-config.cfg");
		// System.out.println("NPC Spawns Loaded");
	}

	public NPC[] getNPCs() {
		return npcs;
	}

	public void multiAttackGfx(int i, int gfx) {
		if (npcs[i].projectileId < 0)
			return;
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null) {
				Client c = (Client) PlayerHandler.players[j];
				if (c.heightLevel != npcs[i].heightLevel)
					continue;
				if (PlayerHandler.players[j].goodDistance(c.absX, c.absY,
						npcs[i].absX, npcs[i].absY, 15)) {
					int nX = NPCHandler.npcs[i].getX() + offset(i);
					int nY = NPCHandler.npcs[i].getY() + offset(i);
					int pX = c.getX();
					int pY = c.getY();
					int offX = (nY - pY) * -1;
					int offY = (nX - pX) * -1;
					c.getPA().createPlayersProjectile(nX, nY, offX, offY, 50,
							getProjectileSpeed(i), npcs[i].projectileId, 43,
							31, -c.getId() - 1, 65);
				}
			}
		}
	}

	public boolean switchesAttackers(int i) {
		switch (npcs[i].npcType) {
		case 10127:
		case 8528:
		case 8133:
		case 8349:
		case 8350:
		case 8351:
		case 6203:
		case 6261:
		case 6263:
		case 6265:
		case 6223:
		case 6225:
		case 6227:
		case 6248:
		case 6250:
		case 6252:
		case 2892:
		case 2894:
		case 50:
		case 3200:
		case 1158:
		case 1160:
		case 6206:
		case 6208:
		case 6204:
		case 9000:
			return true;

		}

		return false;
	}

	public void multiAttackDamage(final int i) {
		int max = getMaxHit(i);
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null) {
				final Client c = (Client) PlayerHandler.players[j];
				if (c.isDead || c.heightLevel != npcs[i].heightLevel)
					continue;
				if (PlayerHandler.players[j].goodDistance(c.absX, c.absY,
						npcs[i].absX, npcs[i].absY, 15)) {

					if (npcs[i].attackType == 2) {
						if (!c.prayerActive[16] && !c.curseActive[7]) {
							if (Misc.random(500) + 200 > Misc.random(c
									.getCombat().mageDef())) {
								int dam = Misc.random(max);
								if (c.playerEquipment[Player.playerShield] == 13740) {
									if (c.playerLevel[5] > 0) {
										dam *= 0.7;
										c.playerLevel[5] -= dam * 0.3 / 2;
										c.getPA().refreshSkill(5);
									}
								}
								if (c.playerEquipment[Player.playerShield] == 13742) {
									if (Misc.random(4) == 3) {
										dam *= 0.75;
									}
								}
								if (npcs[i].npcType == 9000
										&& npcs[i].HP <= 3000
										&& npcs[i].HP > 2400) { // smoke phase
																// effect
									if (dam > 0) {
										if (c.poisonDamage < 0) {
											c.getPA().appendPoison(20);
										}
										c.playerLevel[0] -= dam * 30 / 100;
										if (c.playerLevel[0] <= 0)
											c.playerLevel[0] = 0;
										c.getPA().refreshSkill(0);
										c.playerLevel[4] -= dam * 30 / 100;
										if (c.playerLevel[4] <= 0)
											c.playerLevel[4] = 0;
										c.getPA().refreshSkill(4);
										c.playerLevel[5] -= dam / 4;
										if (c.playerLevel[5] <= 0)
											c.playerLevel[5] = 0;
										c.getPA().refreshSkill(5);
									}
								}
								if (npcs[i].npcType == 9000
										&& npcs[i].HP <= 1800
										&& npcs[i].HP > 1200) { // blood phase
																// effect
									if (dam > 0) {
										npcs[i].HP += dam / 2;
										c.playerLevel[5] -= dam / 4;
										if (c.playerLevel[5] <= 0)
											c.playerLevel[5] = 0;
										c.getPA().refreshSkill(5);
									}
								}
								if (npcs[i].npcType == 9000
										&& npcs[i].HP <= 1200
										&& npcs[i].HP > 600) { // ice phase
									// effect
									if (dam > 0) {
										c.playerLevel[2] -= dam * 40 / 100;
										if (c.playerLevel[2] <= 0)
											c.playerLevel[2] = 0;
										c.getPA().refreshSkill(2);
										c.playerLevel[4] -= dam * 40 / 100;
										if (c.playerLevel[4] <= 0)
											c.playerLevel[4] = 0;
										c.getPA().refreshSkill(4);
										c.playerLevel[5] -= dam / 4;
										if (c.playerLevel[5] <= 0)
											c.playerLevel[5] = 0;
										c.getPA().refreshSkill(5);
									}
								}
								if (npcs[i].npcType == 9000
										&& npcs[i].HP <= 600) { // final phase
																// effect
									if (dam > 0) {
										c.playerLevel[0] -= dam * 40 / 100;
										if (c.playerLevel[0] <= 0)
											c.playerLevel[0] = 0;
										c.getPA().refreshSkill(0);
										c.playerLevel[1] -= dam * 40 / 100;
										if (c.playerLevel[1] <= 0)
											c.playerLevel[1] = 0;
										c.getPA().refreshSkill(1);
										c.playerLevel[2] -= dam * 40 / 100;
										if (c.playerLevel[2] <= 0)
											c.playerLevel[2] = 0;
										c.getPA().refreshSkill(2);
										c.playerLevel[4] -= dam * 40 / 100;
										if (c.playerLevel[4] <= 0)
											c.playerLevel[4] = 0;
										c.getPA().refreshSkill(4);
										c.playerLevel[5] -= dam / 2;
										if (c.playerLevel[5] <= 0)
											c.playerLevel[5] = 0;
										c.getPA().refreshSkill(5);
									}
								}
								c.dealDamage(dam);
								c.handleHitMask(dam);
							} else {
								c.dealDamage(0);
								c.handleHitMask(0);
								CycleEventHandler.getSingleton().addEvent(c,
										new CycleEvent() {
											@Override
											public void execute(
													CycleEventContainer container) {
												if (c != null) {
													if (npcs[i].npcType != 2745
															&& npcs[i].npcType != 8133
															&& npcs[i].npcType != 9000
															&& npcs[i].npcType != 6222
															&& npcs[i].npcType != 6203
															&& npcs[i].npcType != 6247
															&& npcs[i].npcType != 5666) {
														c.gfx100(85);
														container.stop();
													}
												}
											}

											@Override
											public void stop() {

											}
										}, 1);
							}
						}

						if (c.prayerActive[16] || c.curseActive[7]) {
							if (npcs[i].npcType == 9000
									&& Misc.random(500) + 200 > Misc.random(c
											.getCombat().mageDef())) {
								int dam = Misc.random(max) * 40 / 100;
								if (c.playerEquipment[Player.playerShield] == 13740) {
									if (c.playerLevel[5] > 0) {
										dam *= 0.7;
										c.playerLevel[5] -= dam * 0.3 / 2;
										c.getPA().refreshSkill(5);
									}
								}
								if (c.playerEquipment[Player.playerShield] == 13740) {
									if (c.playerLevel[5] > 0) {
										dam *= 0.7;
										c.playerLevel[5] -= dam * 0.3 / 2;
										c.getPA().refreshSkill(5);
									}
								}
								if (c.playerEquipment[Player.playerShield] == 13742) {
									if (Misc.random(4) == 3) {
										dam *= 0.75;
									}
								}
								if (npcs[i].npcType == 9000
										&& npcs[i].HP <= 3000
										&& npcs[i].HP > 2400) { // smoke phase
																// effect
									if (dam > 0) {
										if (c.poisonDamage < 0) {
											c.getPA().appendPoison(20);
										}
										c.playerLevel[0] -= dam * 30 / 100;
										if (c.playerLevel[0] <= 0)
											c.playerLevel[0] = 0;
										c.getPA().refreshSkill(0);
										c.playerLevel[4] -= dam * 30 / 100;
										if (c.playerLevel[4] <= 0)
											c.playerLevel[4] = 0;
										c.getPA().refreshSkill(4);
										c.playerLevel[5] -= dam / 4;
										if (c.playerLevel[5] <= 0)
											c.playerLevel[5] = 0;
										c.getPA().refreshSkill(5);
									}
								}
								if (npcs[i].npcType == 9000
										&& npcs[i].HP <= 1800
										&& npcs[i].HP > 1200) { // blood phase
																// effect
									if (dam > 0) {
										npcs[i].HP += dam / 2;
										c.playerLevel[5] -= dam / 4;
										if (c.playerLevel[5] <= 0)
											c.playerLevel[5] = 0;
										c.getPA().refreshSkill(5);
									}
								}
								if (npcs[i].npcType == 9000
										&& npcs[i].HP <= 1200
										&& npcs[i].HP > 600) { // ice phase
									// effect
									if (dam > 0) {
										c.playerLevel[2] -= dam * 40 / 100;
										if (c.playerLevel[2] <= 0)
											c.playerLevel[2] = 0;
										c.getPA().refreshSkill(2);
										c.playerLevel[4] -= dam * 40 / 100;
										if (c.playerLevel[4] <= 0)
											c.playerLevel[4] = 0;
										c.getPA().refreshSkill(4);
										c.playerLevel[5] -= dam / 4;
										if (c.playerLevel[5] <= 0)
											c.playerLevel[5] = 0;
										c.getPA().refreshSkill(5);
									}
								}
								if (npcs[i].npcType == 9000
										&& npcs[i].HP <= 600) { // final phase
																// effect
									if (dam > 0) {
										c.playerLevel[0] -= dam * 40 / 100;
										if (c.playerLevel[0] <= 0)
											c.playerLevel[0] = 0;
										c.getPA().refreshSkill(0);
										c.playerLevel[1] -= dam * 40 / 100;
										if (c.playerLevel[1] <= 0)
											c.playerLevel[1] = 0;
										c.getPA().refreshSkill(1);
										c.playerLevel[2] -= dam * 40 / 100;
										if (c.playerLevel[2] <= 0)
											c.playerLevel[2] = 0;
										c.getPA().refreshSkill(2);
										c.playerLevel[4] -= dam * 40 / 100;
										if (c.playerLevel[4] <= 0)
											c.playerLevel[4] = 0;
										c.getPA().refreshSkill(4);
										c.playerLevel[5] -= dam / 2;
										if (c.playerLevel[5] <= 0)
											c.playerLevel[5] = 0;
										c.getPA().refreshSkill(5);
									}
								}
								c.dealDamage(dam);
								c.handleHitMask(dam);
							} else {
								c.dealDamage(0);
								c.handleHitMask(0);
								CycleEventHandler.getSingleton().addEvent(c,
										new CycleEvent() {
											@Override
											public void execute(
													CycleEventContainer container) {
												if (c != null) {
													if (npcs[i].npcType != 2745
															&& npcs[i].npcType != 8133
															&& npcs[i].npcType != 9000
															&& npcs[i].npcType != 6222
															&& npcs[i].npcType != 6203
															&& npcs[i].npcType != 6247
															&& npcs[i].npcType != 5666) {
														c.gfx100(85);
														container.stop();
													}
												}
											}

											@Override
											public void stop() {

											}
										}, 1);
							}
						}
					}

					if (npcs[i].attackType == 1) {
						if (!c.prayerActive[17] && !c.curseActive[8]) {
							if (Misc.random(500) + 200 > Misc.random(c
									.getCombat().calculateRangeDefence())) {
								int dam = Misc.random(max);
								if (c.playerEquipment[Player.playerShield] == 13740) {
									if (c.playerLevel[5] > 0) {
										dam *= 0.7;
										c.playerLevel[5] -= dam * 0.3 / 2;
										c.getPA().refreshSkill(5);
									}
								}
								if (c.playerEquipment[Player.playerShield] == 13742) {
									if (Misc.random(4) == 3) {
										dam *= 0.75;
									}
								}
								if (npcs[i].npcType == 9000
										&& npcs[i].HP <= 2400
										&& npcs[i].HP > 1800) {
									if (dam > 0) {
										c.playerLevel[1] -= dam * 30 / 100;
										if (c.playerLevel[1] <= 0)
											c.playerLevel[1] = 0;
										c.getPA().refreshSkill(1);
										c.playerLevel[5] -= dam / 4;
										if (c.playerLevel[5] <= 0)
											c.playerLevel[5] = 0;
										c.getPA().refreshSkill(5);
									}
								}
								c.dealDamage(dam);
								c.handleHitMask(dam);
							} else {
								c.dealDamage(0);
								c.handleHitMask(0);
							}
						}

						if (c.prayerActive[17] || c.curseActive[8]) {
							if (Misc.random(500) + 200 > Misc.random(c
									.getCombat().calculateRangeDefence())) {
								int dam = Misc.random(max);
								if (c.playerEquipment[Player.playerShield] == 13740) {
									if (c.playerLevel[5] > 0) {
										dam *= 0.7;
										c.playerLevel[5] -= dam * 0.3 / 2;
										c.getPA().refreshSkill(5);
									}
								}
								if (c.playerEquipment[Player.playerShield] == 13742) {
									if (Misc.random(4) == 3) {
										dam *= 0.75;
									}
								}
								if (npcs[i].npcType == 9000
										&& npcs[i].HP <= 2400
										&& npcs[i].HP > 1800) {
									if (dam > 0) {
										c.playerLevel[1] -= dam * 30 / 100;
										if (c.playerLevel[1] <= 0)
											c.playerLevel[1] = 0;
										c.getPA().refreshSkill(1);
										c.playerLevel[5] -= dam / 4;
										if (c.playerLevel[5] <= 0)
											c.playerLevel[5] = 0;
										c.getPA().refreshSkill(5);
									}
								}
								c.dealDamage(dam);
								c.handleHitMask(dam);
							} else {
								c.dealDamage(0);
								c.handleHitMask(0);
							}
						}
					}
					if (npcs[i].endGfx > 0) {
						c.gfx0(npcs[i].endGfx);
					}
				}
				c.getPA().refreshSkill(3);
			}
		}
	}

	public int getClosePlayer(int i) {
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null) {
				if (j == npcs[i].spawnedBy)
					return j;
				if (goodDistance(PlayerHandler.players[j].absX,
						PlayerHandler.players[j].absY, npcs[i].absX,
						npcs[i].absY, 2 + distanceRequired(i)
								+ followDistance(i))
						|| isRFDNpc(i) || isFightCaveNpc(i)) {
					if ((PlayerHandler.players[j].underAttackBy <= 0 && PlayerHandler.players[j].underAttackBy2 <= 0)
							|| PlayerHandler.players[j].inMulti())
						if (PlayerHandler.players[j].heightLevel == npcs[i].heightLevel)
							return j;
				}
			}
		}
		return 0;
	}

	public int getCloseRandomPlayer(int i) {
		ArrayList<Integer> players = new ArrayList<Integer>();
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null) {
				if (goodDistance(PlayerHandler.players[j].absX,
						PlayerHandler.players[j].absY, npcs[i].absX,
						npcs[i].absY, 2 + distanceRequired(i)
								+ followDistance(i))
						|| isFightCaveNpc(i)) {
					if ((PlayerHandler.players[j].underAttackBy <= 0 && PlayerHandler.players[j].underAttackBy2 <= 0)
							|| PlayerHandler.players[j].inMulti())
						if (PlayerHandler.players[j].heightLevel == npcs[i].heightLevel)
							players.add(j);
				}
			}
		}
		if (players.size() > 0)
			return players.get(Misc.random(players.size() - 1));
		else
			return 0;
	}

	public int npcSize(int i) {
		switch (npcs[i].npcType) {
		case 2883:
		case 2882:
		case 2881:
			return 3;
		}
		return 0;
	}

	public boolean isAggressive(int i) {
		switch (npcs[i].npcType) {
		case 8133:
		case 8349:
		case 8350:
		case 8351:
		case 2550:
		case 2551:
		case 50:
		case 2892:
		case 2894:
		case 2881:
		case 2882:
		case 2883:
		case 6688:
		case 6689:
		case 6691:
		case 6729:
		case 6730:
		case 3200:
		case 1158:
		case 1160:
		case 1157:
		case 1154:
		case 1156:
		case 6222:
		case 6225:
		case 6227:
		case 6247:
		case 6248:
		case 6250:
		case 6252:
		case 6260:
		case 6261:
		case 6263:
		case 6265:
		case 6203:
		case 6204:
		case 6206:
		case 6208:
		case 8596:
		case 9437:
		case 8597:
		case 8528:
		case 9000:
			return true;
		}
		if (npcs[i].inWild() && npcs[i].MaxHP > 0)
			return true;
		if (isRFDNpc(i))
                        return true;
		if (isFightCaveNpc(i) || isBarbNpc(i)
				|| PestControl.npcIsPCMonster(npcs[i].npcType)
				|| isSpiritNpc(i))
			return true;
		return false;
	}
	
	 public boolean isRFDNpc(int i) {
                switch (npcs[i].npcType) { //after a switch, you add codes that use case
                        case 3493:
                        case 3494:
                        case 3495:
                        case 3496:
                        case 3491:
                        return true;            
                }
                return false;
        }
 
        public boolean isRFDNpc2(int i) {
                switch (npcs[i].npcType) { //after a switch, you add codes that use case
                        case 3495:
                        return true;    //for completing minigames
                }
                return false; //if not completed minigame
        }

	/**
	 * Checks if a tzhaar npc has been killed, if so then it checks if it needs
	 * to do the tz-kek effect. If tzKek spawn has been killed twice or didn't
	 * need to be killed it calls killedTzhaar.
	 * 
	 * @param i
	 *            The npc.
	 */
	private void tzhaarDeathHandler(int i) {
		if (isFightCaveNpc(i) && npcs[i].npcType != FightCaves.TZ_KEK)
			killedTzhaar(i);
		if (npcs[i].npcType == FightCaves.TZ_KEK_SPAWN) {
			int p = npcs[i].killerId;
			if (PlayerHandler.players[p] != null) {
				Client c = (Client) PlayerHandler.players[p];
				c.tzKekSpawn += 1;
				if (c.tzKekSpawn == 2) {
					killedTzhaar(i);
					c.tzKekSpawn = 0;
				}
			}
		}
		if (npcs[i].npcType == FightCaves.TZ_KEK) {
			int p = npcs[i].killerId;
			if (PlayerHandler.players[p] != null) {
				Client c = (Client) PlayerHandler.players[p];
				FightCaves.tzKekEffect(c, i);
			}
		}
	}

	/**
	 * Raises the count of tzhaarKilled, if tzhaarKilled is equal to the amount
	 * needed to kill to move onto the next wave it raises wave id then starts
	 * next wave.
	 * 
	 * @param i
	 *            The npc.
	 */
	private void killedTzhaar(int i) {
		final Client c2 = (Client) PlayerHandler.players[npcs[i].spawnedBy];
		if (c2 != null) {
			c2.tzhaarKilled++;
			if (c2.tzhaarKilled == c2.tzhaarToKill) {
				c2.waveId++;
				EventManager.getSingleton().addEvent(new Event() {
					@Override
					public void execute(EventContainer c) {
						if (c2 != null) {
							Server.fightCaves.spawnNextWave(c2);
						}
						c.stop();
					}
				}, 5000);
			}
		}
	}

	
	/**
	 * Handles the death of tztok-jad by ending the game and rewarding the
	 * player with a fire cape.
	 * 
	 * @param i
	 *            The npc.
	 */
	public void handleJadDeath(int i) {
		Client c = (Client) PlayerHandler.players[npcs[i].spawnedBy];
		if (c != null) {
			if (c.fireCape == 0) {
				c.getItems().addItem(6570, 1);
				c.getItems().addItem(995, Misc.random(25000000));
				c.sendMessage("Congratulations! You defeated all waves of the fight caves mini-game.");
				c.sendMessage("@red@You may now beat all 30 waves again for a TokHaar-Kal cape!");
				c.fireCape = 1;
			} else {
				c.getItems().addItem(19015, 1);
				c.getItems().addItem(995, Misc.random(50000000));
				c.sendMessage("Congratulations! You defeated all waves of the fight caves mini-game.");
			}
			PlayerHandler.yell("@dre@" + Misc.optimizeText(c.playerName)
					+ " has defeated the mighty TzTok-Jad!");
			c.getPA().resetTzhaar();
		}
	}
	
	public void handleAgrithDeath(int i) {
		Client c = (Client)Server.playerHandler.players[npcs[i].spawnedBy];
		c.sendMessage("You have defeated Agrith the next wave will start soon");
		c.Agrith = true;
	}
	
	public void handleFlambeedDeath(int i) {
		Client c = (Client)Server.playerHandler.players[npcs[i].spawnedBy];
		c.sendMessage("You have defeated Flambeed the next wave will start soon");
		c.Flambeed = true;
	}
	
	public void handleKaramelDeath(int i) {
		Client c = (Client)Server.playerHandler.players[npcs[i].spawnedBy];
		c.sendMessage("You have defeated Karamel the next wave will start soon");
		c.Karamel = true;
	}
	
	public void handleDessourtDeath(int i) {
		Client c = (Client)Server.playerHandler.players[npcs[i].spawnedBy];
		c.sendMessage("You have defeated Dessourt the next wave will start soon");
		c.Dessourt = true;
	}
	
	public void handleCulinDeath(int i) {
		Client c = (Client)Server.playerHandler.players[npcs[i].spawnedBy];
		c.sendMessage("You have defeated Culin the next wave will start soon");
		c.Culin = true;
	}
	
	public void handleCorpKills(int i) {
		Client c = (Client)Server.playerHandler.players[npcs[i].killedBy];		
		c.CorpKills += 1;
	}
	
	public void handleNexKills(int i) {
		Client c = (Client)Server.playerHandler.players[npcs[i].killedBy];
		c.NexKills += 1;
	}
	
	public void handleBorkKills(int i) {
		Client c = (Client)Server.playerHandler.players[npcs[i].killedBy];
		c.BorkKills += 1;
	}
	
	public void handleNomadKills(int i) {
		Client c = (Client)Server.playerHandler.players[npcs[i].killedBy];
		c.NomadKills += 1;
	}
	
	public void handleAvatarKills(int i) {
		Client c = (Client)Server.playerHandler.players[npcs[i].killedBy];
		c.AvatarKills += 1;
	}
	
	public void handleCureseBearKills(int i) {
		Client c = (Client)Server.playerHandler.players[npcs[i].killedBy];
		c.CursebearerKills += 1;
	}
	
	public void handleTormentedDemonKills(int i) {
		Client c = (Client)Server.playerHandler.players[npcs[i].killedBy];
		c.TormentedDemonKills += 1;
	}
	
	public void handleKalphiteQueenKills(int i) {
		Client c = (Client)Server.playerHandler.players[npcs[i].killedBy];
		c.KalphiteQueenKills += 1;
	}
	
	public void handleBarrelChestKills(int i) {
		Client c = (Client)Server.playerHandler.players[npcs[i].killedBy];
		c.BarrelChestKills += 1;
	}
	
	public void handleSaradominKills(int i) {
		Client c = (Client)Server.playerHandler.players[npcs[i].killedBy];
		c.SaradominKills += 1;
	}
	
	public void handleArmadylKills(int i) {
		Client c = (Client)Server.playerHandler.players[npcs[i].killedBy];
		c.ArmadylKills += 1;
	}
	
	public void handleBandosKills(int i) {
		Client c = (Client)Server.playerHandler.players[npcs[i].killedBy];
		c.BandosKills += 1;
	}
	
	public void handleZamorakKills(int i) {
		Client c = (Client)Server.playerHandler.players[npcs[i].killedBy];
		c.ZamorakKills += 1;
	}
	
	public void handlePrimalKills(int i) {
		Client c = (Client)Server.playerHandler.players[npcs[i].killedBy];
		/*c.PrimalKills += 1;*/
	}
	
	
	/**
	 * Checks if something is a fight cave npc.
	 * 
	 * @param i
	 *            The npc.
	 * @return Whether or not it is a fight caves npc.
	 */
	public boolean isFightCaveNpc(int i) {
		switch (npcs[i].npcType) {
		case FightCaves.TZ_KIH:
		case FightCaves.TZ_KEK:
		case FightCaves.TOK_XIL:
		case FightCaves.YT_MEJKOT:
		case FightCaves.KET_ZEK:
		case FightCaves.TZTOK_JAD:
			return true;
		}
		return false;
	}

	public boolean isSpiritNpc(int i) {
		switch (npcs[i].npcType) {
		case 6276:
		case 6277:
		case 6278:
		case 1977:
		case 1913:
		case 6255:
		case 6256:
		case 6257:
		case 6219:
		case 6220:
		case 6221:
		case 6229:
		case 6230:
		case 6231:
		case 937:
		case 936:
			return true;
		}
		return false;
	}

	public boolean isBarbNpc(int i) {
		return Server.barbDefence.killableNpcs(i);
	}

	/**
	 * Summon npc, barrows, etc
	 **/
	public void spawnNpc(Client c, int npcType, int x, int y, int heightLevel,
			int WalkingType, int HP, int maxHit, int attack, int defence,
			boolean attackPlayer, boolean headIcon) {
		// first, search for a free slot
		int slot = -1;
		for (int i = 1; i < maxNPCs; i++) {
			if (npcs[i] == null) {
				slot = i;
				break;
			}
		}
		if (slot == -1) {
			// Misc.println("No Free Slot");
			return; // no free slot found
		}
		NPC newNPC = new NPC(slot, npcType);
		newNPC.absX = x;
		newNPC.absY = y;
		newNPC.makeX = x;
		newNPC.makeY = y;
		newNPC.heightLevel = heightLevel;
		newNPC.walkingType = WalkingType;
		newNPC.HP = HP;
		newNPC.MaxHP = HP;
		newNPC.maxHit = maxHit;
		newNPC.attack = attack;
		newNPC.defence = defence;
		newNPC.spawnedBy = c.getId();
		newNPC.dagColor = getDagColor(npcType);
		if (headIcon)
			c.getPA().drawHeadicon(1, slot, 0, 0);
		if (attackPlayer) {
			newNPC.underAttack = true;
			if (c != null) {
				if (server.model.minigames.Barrows.COFFIN_AND_BROTHERS[c.randomCoffin][1] != newNPC.npcType) {
					if (newNPC.npcType == 2025 || newNPC.npcType == 2026
							|| newNPC.npcType == 2027 || newNPC.npcType == 2028
							|| newNPC.npcType == 2029 || newNPC.npcType == 2030) {
						newNPC.forceChat("You dare disturb my rest!");
					}
				}
				if (server.model.minigames.Barrows.COFFIN_AND_BROTHERS[c.randomCoffin][1] == newNPC.npcType) {
					newNPC.forceChat("You dare steal from us!");
				}
				if (newNPC.npcType == 3067) {
					newNPC.forceChat("Time for me to show you why they call me the Champion of Champions!");
				}
				if (newNPC.npcType == 937) {
					newNPC.forceChat("Get away from my treasure!");
				}
				if (newNPC.npcType == 936) {
					newNPC.forceChat("Keep your hands away from my chest you foolish mortal!");
				}
				if (newNPC.npcType == 1913) {
					newNPC.forceChat("I will perish again before I let you take what is mine!");
				}
				if (newNPC.npcType == 1977) {
					newNPC.forceChat("You dare stand between me and my spoils mortal?!");
				}

				newNPC.killerId = c.playerId;
			}
		}
		npcs[slot] = newNPC;
	}

	public String getDagColor(int npcType) {
		int dags[] = { 1351, 1352, 1356, 1353, 1354, 1355 };
		String colors[] = { "white", "blue", "brown", "red", "orange", "green" };
		for (int i = 0; i < dags.length; i++) {
			if (npcType == dags[i]) {
				return colors[i];
			}
		}
		return "";
	}

	public void spawnNpc2(int npcType, int x, int y, int heightLevel,
			int WalkingType, int HP, int maxHit, int attack, int defence) {
		// first, search for a free slot
		int slot = -1;
		for (int i = 1; i < maxNPCs; i++) {
			if (npcs[i] == null) {
				slot = i;
				break;
			}
		}
		if (slot == -1) {
			// Misc.println("No Free Slot");
			return; // no free slot found
		}
		NPC newNPC = new NPC(slot, npcType);
		newNPC.absX = x;
		newNPC.absY = y;
		newNPC.makeX = x;
		newNPC.makeY = y;
		newNPC.heightLevel = heightLevel;
		newNPC.walkingType = WalkingType;
		newNPC.HP = HP;
		newNPC.MaxHP = HP;
		newNPC.maxHit = maxHit;
		newNPC.attack = attack;
		newNPC.defence = defence;
		npcs[slot] = newNPC;
	}

	public void spawnNpc3(Client c, int npcType, int x, int y, int heightLevel,
			int WalkingType, int HP, int maxHit, int attack, int defence,
			boolean attackPlayer, boolean headIcon) {
		int slot = -1;
		for (int i = 1; i < maxNPCs; i++) {
			if (npcs[i] == null) {
				c.barbLeader = slot = i;
				break;
			}
		}
		if (slot == -1) {
			return;
		}
		NPC newNPC = new NPC(slot, npcType);
		newNPC.absX = x;
		newNPC.absY = y;
		newNPC.makeX = x;
		newNPC.makeY = y;
		newNPC.heightLevel = heightLevel;
		newNPC.walkingType = WalkingType;
		newNPC.HP = HP;
		newNPC.MaxHP = HP;
		newNPC.maxHit = maxHit * 10;
		newNPC.attack = attack;
		newNPC.defence = defence;
		newNPC.spawnedBy = c.getId();
		if (headIcon)
			c.getPA().drawHeadicon(1, slot, 0, 0);
		if (attackPlayer) {
			newNPC.underAttack = true;
			if (c != null) {
				if (server.model.minigames.Barrows.COFFIN_AND_BROTHERS[c.randomCoffin][1] != newNPC.npcType) {
					if (newNPC.npcType == 2025 || newNPC.npcType == 2026
							|| newNPC.npcType == 2027 || newNPC.npcType == 2028
							|| newNPC.npcType == 2029 || newNPC.npcType == 2030) {
						newNPC.forceChat("You dare disturb my rest!");
					}
				}
				if (server.model.minigames.Barrows.COFFIN_AND_BROTHERS[c.randomCoffin][1] == newNPC.npcType) {
					newNPC.forceChat("You dare steal from us!");
				}

				newNPC.killerId = c.playerId;
			}
		}
		npcs[slot] = newNPC;
	}

	/**
	 * Emotes
	 **/

	public static int getAttackEmote(int i) {

		if (npcs[i].npcType >= 3732 && npcs[i].npcType <= 3741) {
			return 3901;
		}
		if (npcs[i].npcType >= 3742 && npcs[i].npcType <= 3746) {
			return 3915;
		}
		if (npcs[i].npcType >= 3747 && npcs[i].npcType <= 3751) {
			return 3908;
		}
		if (npcs[i].npcType >= 3752 && npcs[i].npcType <= 3761) {
			return 3880;
		}
		if (npcs[i].npcType >= 3762 && npcs[i].npcType <= 3771) {
			return 3920;
		}
		if (npcs[i].npcType >= 3772 && npcs[i].npcType <= 3776) {
			return 3896;
		}

		switch (npcs[i].npcType) {
		case 9780:
			return 451;
		case 9000:
			if (npcs[i].attackType == 0)
				return 6354;
			if (npcs[i].attackType == 2 || npcs[i].attackType == 1)
				return 6986;
		case 10127:
			if (npcs[i].attackType == 0)
				return 13169;
			if (npcs[i].attackType == 2)
				return 13172;
		case 8989:
			return 12515;
		case 8986:
			return 9449;
		case 8987:
			return 9466;
		case 2627:
			return 9232;
		case 2630:
			return 9233;
		case 2738: // Tz-Kek spawn
			return 9233;
		case 2631:
			return 9243;
		case 2741:
			return 9252;
		case 2743:
			if (npcs[i].attackType == 0)
				return 9265;
			if (npcs[i].attackType == 2)
				return 9266;

		case 8528:
			if (npcs[i].attackType == 0)
				return 12696;
			if (npcs[i].attackType == 2)
				return 12697;
		case 9052:
			return 2975;
		case 8596:
			return 11197;
		case 9437:
		case 8597:
			return 11202;
		case 7133:
			return 8754;
		case 9462:
		case 9463:
		case 9464:
		case 9465:
		case 9466:
		case 9467:
			return 12791;
		case 8349:
		case 8350:
		case 8351:
			if (npcs[i].attackType == 0)
				return 10922;
			if (npcs[i].attackType == 1)
				return 10919;
			if (npcs[i].attackType == 2)
				return 10918;
		case 8133:
			if (npcs[i].attackType == 0)
				return 10057;
			if (npcs[i].attackType == 2)
				return 10053;
			if (npcs[i].attackType == 1)
				return 10053;
		case 6203:
			if (npcs[i].attackType == 0)
				return 6945;
			if (npcs[i].attackType == 2)
				return 6947;
		case 6204:
		case 6206:
			return 64;
		case 6208:
			return 69;
		case 6276:
		case 6277:
		case 6278:
			return 4320;
		case 1977:
		case 1913:
			return 390;
		case 6256:
			return 426;
		case 6257:
			return 811;
		case 6220:
			return 426;
		case 6221:
			return 811;
		case 6229:
			return 6954;
		case 6230:
			return 6953;
		case 6231:
			return 6955;
		case 936:
		case 937:
			return 5485;

		case 5213:
		case 5214:
		case 5215:
		case 5216:
		case 5217:
		case 5218:
		case 5219: // Penance fighter
			return 5097;
		case 5229:
		case 5230:
		case 5231:
		case 5232:
		case 5233:
		case 5234:
		case 5235:
		case 5236:
		case 5237: // Penance ranger
			return 5396;
		case 5247: // Penance queen
			return 5411;
		case 3340:
			return 3312;
		case 5666:// barrelchest
			if (npcs[i].attackType == 0)
				return 5894;
			if (npcs[i].attackType == 1 || npcs[i].attackType == 2)
				return 5895;
		case 7641:// Zombie hand
			return 9125;
		case 7801:// Aberrant spectre
			return 9466;
		case 6106:// Skeleton heavy
			return 7041;
		case 7795:// Insectoid assassin
			return 7607;
		case 6103:// Skeleton hero
			return 2067;
		case 751:// Berserk barbarian spirit
			return 6726;
		case 7798:// Monstrous cave crawler
			return 9414;
		case 7800:// Mightiest turoth
			return 9413;
		case 7797:// Kurask overlord
		case 7805:
			return 9439;
		case 3062:// imp
			return 169;
		case 3065:// skeleton
			return 5507;
		case 3066:// zombie
			return 5571;
		case 3058:// giant
			return 6368;
		case 3059:// ghoul
			return 10505;
		case 7078:// ogress
			return 8636;
		case 3057:// earth
			return 2951;
		case 3064:// demon
			if (npcs[i].attackType == 0)
				return 64;
			if (npcs[i].attackType == 2)
				return 69;
		case 3067:// leon
			if (npcs[i].attackType == 0)
				return 7048;
			if (npcs[i].attackType == 2)
				return 7073;
		case 1158: // kalphite queen
			if (npcs[i].attackType == 2)
				return 6240;
			if (npcs[i].attackType == 1)
				return 6240;
			if (npcs[i].attackType == 0)
				return 6241;
		case 1160: // kalphite queen form 2
			if (npcs[i].attackType == 2)
				return 6234;
			if (npcs[i].attackType == 1)
				return 6234;
			if (npcs[i].attackType == 0)
				return 6235;
		case 1956:// ice wolf
			return 6579;
		case 6730:// revs
			return 7441;
		case 6729:
			return 7411;
		case 6691:
			return 7467;
		case 6689:
			return 7474;
		case 6688:
			return 7460;

		case 120:
			return 98;

		case 49:
			return 158;

		case 1030:
		case 1031:
		case 1032:
		case 1033:
		case 1034:
			return 1083;
		case 190:
			return 729;
		case 113:
			return 4652;
		case 27:
		case 690:
			return 426;

		case 1047:
			return -1;
		case 2550:
			if (npcs[i].attackType == 0)
				return 7060;
			else
				return 7063;
		case 2892:
		case 2894:
			return 2868;

		case 34:
		case 18:
		case 23:
		case 26:
			return 451;

			// bandos gwd
		case 6260:
			if (npcs[i].attackType == 0)
				return 7060;
			else
				return 7063;
		case 6261:
		case 6263:
		case 6265:
			return 6154;

			// saradomin
		case 6247:
			if (npcs[i].attackType == 2)
				return 6967;
			else
				return 6964;
		case 6248:
			return 6376;
		case 6250:
			return 7018;
		case 6252:
			return 7009;

			// armadyl
		case 6222:
			return 6973;
		case 6225:
			return 6953;
		case 6223:
			return 6954;
		case 6227:
			return 6953;

		case 13: // wizards
			return 711;

		case 103:
		case 655:
			return 123;

		case 1624:// dust devil
			return 7183;

		case 1612: // banshee
		case 7793:
			return 9449;

		case 1643: // infernal
			return 7183;

		case 1618: // bloodveld
		case 7643:
			return 9130;

		case 1648: // hand
			return 9125;

		case 2783: // dark beast
			return 2733;

		case 1615: // abby demon
			return 1537;

		case 1613: // nech
			return 9487;

		case 1610:
		case 1611: // garg
			return 9454;

		case 1616: // basilisk
		case 7799:// basilisk boss
			return 260;

		case 90: // skele
			return 260;

		case 50:// drags
		case 53:
		case 54:
		case 55:
		case 941:
		case 4677:
		case 1590:
		case 1591:
		case 1592:
		case 5362:
		case 5363:
			if (npcs[i].attackType == 0)
				return 80;
			else if (npcs[i].attackType == 3)
				return 81;

		case 10219:
		case 10220:
		case 10221:
		case 10222:
		case 10223:
		case 10224:
		case 10604:
		case 10605:
		case 10606:
		case 10607:
		case 10608:
		case 10609:
		case 10770:
		case 10771:
		case 10772:
		case 10773:
		case 10774:
		case 10775:
		case 10776:
		case 10777:
		case 10778:
		case 10779:
		case 10780:
		case 10781:
		case 10815:
		case 10816:
		case 10817:
		case 10818:
		case 10819:
		case 10820:
			if (npcs[i].attackType == 0) {
				return 13151;
			} else if (npcs[i].attackType == 3 || npcs[i].attackType == 2
					|| npcs[i].attackType == 1) {
				return 13155;
			}

		case 124: // earth warrior
			return 390;

		case 803: // monk
			return 422;

		case 52: // baby drag
			return 25;

		case 58: // Shadow Spider
		case 59: // Giant Spider
		case 60: // Giant Spider
		case 61: // Spider
		case 62: // Jungle Spider
		case 63: // Deadly Red Spider
		case 64: // Ice Spider
		case 134:
			return 143;

		case 105: // Bear
		case 106: // Bear
			return 41;

		case 412:
		case 78:
			return 30;

		case 2033: // rat
			return 138;

		case 2031: // bloodworm
			return 2070;

		case 101: // goblin
			return 309;

		case 81: // cow
			return 0x03B;

		case 21: // hero
			return 451;

		case 41: // chicken
			return 55;

		case 199:
			return 451;

		case 12:
		case 17:

			int gunthor = Misc.random(3);
			if (gunthor == 2) {
				npcs[i].forceChat("FOR GUNTHOR!!!");
			}
			return 451;

		case 9: // guard
		case 32: // guard
			// case 12:
			// case 17:
		case 20: // paladin
			return 451;

		case 1338: // dagannoth
		case 1340:
		case 1342:
			return 1341;

		case 19: // white knight
			return 406;

		case 110:
		case 111:
		case 112:
		case 117:
		case 1681:
		case 4686:
		case 4688:
		case 1584:// giants
			return 4666;

		case 2452:
			return 1312;

		case 2889:
			return 2859;

		case 118:
		case 119:
			return 99;

		case 82:// Lesser Demon
		case 83:// Greater Demon
		case 84:// Black Demon
		case 4695:// Lesser Demon
		case 4698:// Greater Demon
		case 4705:// Black Demon
		case 1472:// jungle demon
			return 64;

		case 1267:
		case 1265:
			return 1312;

		case 125: // ice warrior
		case 178:
			return 451;

		case 1153: // Kalphite Worker
		case 1154: // Kalphite Soldier
		case 1155: // Kalphite guardian
		case 1156: // Kalphite worker
		case 1157: // Kalphite guardian
			return 6224;

		case 123:
		case 122:
			return 164;

		case 2028: // karil
			return 2075;

		case 2025: // ahrim
			return 729;

		case 2026: // dharok
			return 2067;

		case 2027: // guthan
			return 2080;

		case 2029: // torag
			return 0x814;

		case 2030: // verac
			return 2062;

		case 2881: // supreme
			return 2855;

		case 2882: // prime
			return 2854;

		case 2883: // rex
			return 2851;

		case 3200:
			return 3146;

		case 2745:
			if (npcs[i].attackType == 2)
				return 9300;
			else if (npcs[i].attackType == 1)
				return 9276;
			else if (npcs[i].attackType == 0)
				return 9277;

		default:
			// return 0x326;
			return 451;
		}
	}

	public int getDeadEmote2(int i) {
		int death = 836; // default
		int[][] getDead = { { 1, 836 } };
		for (int n = 0; n < getDead.length; n++) {
			if (npcs[i].npcType == getDead[n][0]) {
				death = getDead[n][1];
			}
		}
		return death;
	}

	public int getDeadEmote(int i) {

		if (npcs[i].npcType >= 3732 && npcs[i].npcType <= 3741) {
			return 3903;
		}
		if (npcs[i].npcType >= 3742 && npcs[i].npcType <= 3746) {
			return 3917;
		}
		if (npcs[i].npcType >= 3747 && npcs[i].npcType <= 3751) {
			return 3909;
		}
		if (npcs[i].npcType >= 3752 && npcs[i].npcType <= 3761) {
			return 3881;
		}
		if (npcs[i].npcType >= 3762 && npcs[i].npcType <= 3771) {
			return 3922;
		}
		if (npcs[i].npcType >= 3772 && npcs[i].npcType <= 3776) {
			return 3894;
		}

		switch (npcs[i].npcType) {
		case 9780:
		case 10542:
		case 10543:
		case 10544:
		case 10545:
		case 15046:
		case 15047:
		/*handlePrimalKills(i);*/
			return 2304;
		case 9000:
		handleNexKills(i);
			return 6951;
		case 10127:
		handleCureseBearKills(i);
			return 13171;
		case 2745:
			handleJadDeath(i);
			return 9279;
		case 2627:
			return 9230;
		case 2630:
			return 9234;
		case 2738:
			return 9234;
		case 2631:
			return 9239;
		case 2741:
			return 9257;
		case 2743:
			return 9269;
		case 8528:
		handleNomadKills(i);
			return 12694;
		case 9052:
			return 2980;
		case 8596:
		handleAvatarKills(i);
			return 11199;
		case 9437:
		case 8597:
		handleAvatarKills(i);
			return 11204;
		case 7133:
		handleBorkKills(i);
			return 8756;
		case 9462:
		case 9463:
		case 9464:
		case 9465:
		case 9466:
		case 9467:
			return 12793;
		case 8349:
		case 8350:
		case 8351:
		/*handleTormentedDemonKills(i);*/
			return 10924;
		case 8133:
		handleCorpKills(i);
			return 10059;
		case 6203:
		handleZamorakKills(i);
			return 6946;
		case 6204:
		case 6206:
		case 6208:
			return 67;
		case 6276:
		case 6277:
		case 6278:
			return 4321;
		case 1977:
		case 1913:
		case 6255:
		case 6256:
		case 6257:
		case 6219:
		case 6220:
		case 6221:
			return 836;
		case 6229:
		case 6230:
		case 6231:
			return 6956;
		case 936:
		case 937:
			return 5491;

		case 5213:
		case 5214:
		case 5215:
		case 5216:
		case 5217:
		case 5218:
		case 5219: // Penance fighter
			return 5098;
		case 5229:
		case 5230:
		case 5231:
		case 5232:
		case 5233:
		case 5234:
		case 5235:
		case 5236:
		case 5237: // Penance ranger
			return 5397;
		case 5247: // Penance queen
			return 5412;
		case 3340:
			return 3310;
		case 7641:// Zombie hand
			return 9126;
		case 7801:// Aberrant spectre
			return 9467;
		case 6103:// Skeleton warlord
		case 6106:// Skeleton heavy
			return 836;
		case 7795:// Insectoid assassin
			return 7609;
		case 751:// Berserk barbarian spirit
			return 6727;
		case 7798:// Monstrous cave crawler
			return 9418;
		case 7797:// Kurask overlord
		case 7805:
			return 9440;
		case 7800:// Mightiest turoth
			return 9477;
		case 3062:// imp
			spawnSecondChallenge(i);
			return 172;
		case 3065:// skeleton
			spawnThirdChallenge(i);
			return 5514;
		case 3066:// zombie
			spawnFourthChallenge(i);
			return 5575;
		case 3058:// giant
			spawnFifthChallenge(i);
			return 6369;
		case 8986:// banshee
			spawnSixthChallenge(i);
			return 9450;
		case 3059:// ghoul
			spawnSeventhChallenge(i);
			return 836;
		case 7078:// ogress
			spawnEighthChallenge(i);
			return 8640;
		case 8987:// abberant
			spawnNineChallenge(i);
			return 9467;
		case 3057:// earth
			spawnTenthChallenge(i);
			return 2946;
		case 8989:// mummy
			spawnEleventhChallenge(i);
			return 12518;
		case 3064:// demon
			spawnFinalChallenge(i);
			return 67;
		case 3067:// leon
			wonChallenge(i);
			return 836;
		case 1158:
			handleKalphiteQueenKills(i);
			spawnSecondForm(i);
			// npcs[i].gfx0(1055);
			return 6242;
		case 1160:
			handleKalphiteQueenKills(i);
			spawnFirstForm(i);
			return 6233;
		case 5666:// barrelchest
			handleBarrelChestKills(i);
			return 5898;
		case 1956:
			return 6576;
		case 1265:
		case 2452:
			return 1314;
		case 6730:
			return 7439;
		case 6729:
			return 7416;
		case 6691:
			return 7470;
		case 6689:
			return 7475;
		case 6688:
			return 7463;
		case 110:
		case 111:
		case 112:
		case 117:
		case 1681:
		case 4686:
		case 4688:
		case 1584:// giants
			return 4668;
		
		case 3493:// agrith
		handleAgrithDeath(i);
			return 4668;	
		case 3494:// flambeed
		handleFlambeedDeath(i);
			return 4668;
		case 3495:// karamel
		handleKaramelDeath(i);
			return 4668;
		case 3496:// dessourt
		handleDessourtDeath(i);
			return 4668;
		case 3491:// culin
		handleCulinDeath(i);
			return 4668;
		
		
		
		case 2892:
		case 2894:
			return 2865;
		case 1612: // banshee
		case 7793:
			return 1524;
		case 2558:
			return 3503;
		case 2559:
		case 2560:
		case 2561:
			return 6956;

			// Saradomin
		case 6247:
			handleSaradominKills(i);
			return 6965;
		case 6248:
			return 6377;
		case 6250:
			return 7016;
		case 6252:
			return 7011;

			// bandos
		case 6260:
		handleBandosKills(i);
			return 7062;
		case 6261:
		case 6263:
		case 6265:
			return 6156;

			// armadyl
		case 6222:
		handleArmadylKills(i);
			return 6975;
		case 6223:
		case 6225:
		case 6227:
			return 6956;

			// PC
		case 6142:
		case 6143:
		case 6144:
		case 6145:
			return -1;

		case 3200:
			return 3147;

		case 2035: // spider
			return 146;

		case 2033: // rat
			return 141;

		case 2031: // bloodveld
		case 7643:
			return 2073;

		case 101: // goblin
			return 313;

		case 81: // cow
			return 0x03E;

		case 41: // chicken
			return 57;

		case 1338: // dagannoth
		case 1340:
		case 1342:
			return 1342;

		case 2881:
		case 2882:
		case 2883:
			return 2856;

		case 125: // ice warrior
			return 843;

		case 1626:
		case 1627:
		case 1628:
		case 1629:
		case 1630:
		case 1631:
		case 1632: // turoth!
			return 1597;

		case 82:// Lesser Demon
		case 83:// Greater Demon
		case 84:// Black Demon
		case 4695:// Lesser Demon
		case 4698:// Greater Demon
		case 4705:// Black Demon
			return 67;

		case 1605:// abby spec
			return 1508;

		case 51:// baby drags
		case 52:
		case 1589:
		case 3376:
			return 28;

		case 1610:
		case 1611:
			return 9456;

		case 1616: // basilisk
		case 7799:// basilisk boss
			return 264;

		case 1618:
		case 1619:
			return 9131;

		case 1620:
		case 1621:
			return 1563;

		case 2783:
			return 2732;

		case 1615:
			return 1538;

		case 1624:
			return 1558;

		case 1613:
			return 9488;

		case 1643:
			return 7185;

		case 1633:
		case 1634:
		case 1635:
		case 1636:
			return 1580;

		case 1648:
		case 1649:
		case 1650:
		case 1651:
		case 1652:
		case 1653:
		case 1654:
		case 1655:
		case 1656:
		case 1657:
			return 9126;

		case 100:
		case 102:
			return 313;

		case 105:
		case 106:
			return 44;

		case 412:
		case 78:
			return 36;

		case 122:
		case 123:
			return 167;

		case 58:
		case 59:
		case 60:
		case 61:
		case 62:
		case 63:
		case 64:
		case 134:
			return 146;

		case 1153:
		case 1154:
		case 1155:
		case 1156:
		case 1157:
			return 6228;

		case 103:
		case 104:
			return 123;

		case 118:
		case 119:
			return 102;

		case 50:// drags
		case 53:
		case 54:
		case 55:
		case 941:
		case 4677:
		case 1590:
		case 1591:
		case 1592:
		case 5362:
		case 5363:
			return 92;

		case 10219:
		case 10220:
		case 10221:
		case 10222:
		case 10223:
		case 10224:
		case 10604:
		case 10605:
		case 10606:
		case 10607:
		case 10608:
		case 10609:
		case 10770:
		case 10771:
		case 10772:
		case 10773:
		case 10774:
		case 10775:
		case 10776:
		case 10777:
		case 10778:
		case 10779:
		case 10780:
		case 10781:
		case 10815:
		case 10816:
		case 10817:
		case 10818:
		case 10819:
		case 10820:
			return 13153;

		default:
			return -1;
		}
	}

	/**
	 * Time of death animation
	 **/

	public int getDeadTime(int i) {
		switch (npcs[i].npcType) {
		case 1265:
		case 2452:
		case 3057:
		case 3200:
		case 5213:
		case 5214:
		case 5215:
		case 5216:
		case 5217:
		case 5218:
		case 5219: // Penance fighter
		case 5229:
		case 5230:
		case 5231:
		case 5232:
		case 5233:
		case 5234:
		case 5235:
		case 5236:
		case 5237: // Penance ranger
			return 2;
		case 5666:
			return 11;
		default:
			return 4;
		}
	}

	/**
	 * Attack delays
	 **/
	public int getNpcDelay(int i) {
		switch (npcs[i].npcType) {
		case 2025:
		case 2028:
		case 8133:
		case 2745:
			return 7;

		case 6222:
		case 6223:
		case 6225:
		case 6227:
		case 6260:
		case 6203:
		case 6204:
		case 6206:
		case 6208:
		case 8349:
		case 8350:
		case 8351:
		case 9000:
			return 6;
		case 6247:
		case 8528:
			return 3;
		case 1977:
		case 1913:
		case 937:
		case 936:
		case 10127:
			return 4;

		default:
			return 5;
		}
	}

	/**
	 * Hit delays
	 **/
	public int getHitDelay(int i) {
		switch (npcs[i].npcType) {

		case 2881:
		case 2882:
		case 3200:
		case 2892:
		case 2894:
		case 1158:
		case 1160:
		case 5229:
		case 5230:
		case 5231:
		case 5232:
		case 5233:
		case 5234:
		case 5235:
		case 5236:
		case 5237:
		case 2743:
		case 2631:
		case 6222:
		case 6225:
		case 6250:
		case 6252:
		case 6263:
		case 6265:
		case 8349:
		case 8350:
		case 8351:
		case 9463:
			return 3;

		case 9000:
			return 4;

		case 8133:
		case 2025:
			return 4;

		case 2745:
			return 5;

		case 2028:
			return 3;

		default:
			return 2;
		}
	}

	/**
	 * Npc respawn time
	 **/
	public int getRespawnTime(int i) {
		switch (npcs[i].npcType) {
		case 2881:
		case 2882:
		case 2883:
		case 50:
		case 3200:
		case 5666:
		case 6222:
		case 6225:
		case 6227:
		case 6247:
		case 6248:
		case 6250:
		case 6252:
		case 6260:
		case 6261:
		case 6263:
		case 6265:
		case 6203:
		case 6204:
		case 6206:
		case 6208:
		case 8349:
		case 8350:
		case 8351:
		case 8133:
		case 7133:
		case 8596:
		case 9437:
		case 8597:
		case 8528:
		case 10127:
		case 9000:
			return 60;
		case 6142:
		case 6143:
		case 6144:
		case 6145:
		case 1158:
		case 1160:
			return -1;
		default:
			return 25;
		}
	}

	public void newNPC(int npcType, int x, int y, int heightLevel,
			int WalkingType, int HP, int maxHit, int attack, int defence) {
		// first, search for a free slot
		int slot = -1;
		for (int i = 1; i < maxNPCs; i++) {
			if (npcs[i] == null) {
				slot = i;
				break;
			}
		}

		if (slot == -1)
			return; // no free slot found

		NPC newNPC = new NPC(slot, npcType);
		newNPC.absX = x;
		newNPC.absY = y;
		newNPC.makeX = x;
		newNPC.makeY = y;
		newNPC.heightLevel = heightLevel;
		newNPC.walkingType = WalkingType;
		newNPC.HP = HP;
		newNPC.MaxHP = HP;
		newNPC.maxHit = maxHit;
		newNPC.attack = attack;
		newNPC.defence = defence;
		npcs[slot] = newNPC;
	}

	public void newNPCList(int npcType, String npcName, int combat, int HP) {
		// first, search for a free slot
		int slot = -1;
		for (int i = 0; i < maxListedNPCs; i++) {
			if (NpcList[i] == null) {
				slot = i;
				break;
			}
		}

		if (slot == -1)
			return; // no free slot found

		NPCList newNPCList = new NPCList(npcType);
		newNPCList.npcName = npcName;
		newNPCList.npcCombat = combat;
		newNPCList.npcHealth = HP;
		NpcList[slot] = newNPCList;
	}

	public String[] voidKnightTalk = { "We must not fail!",
			"Take down the portals", "The Void Knights will not fall!",
			"Hail the Void Knights!", "We are beating these scum!",
			"Don't let these creatures leech my health!!",
			"Do not let me die!!!", "Please....help me!",
			"For the knights we shall prevail!" };

	public void process() {
		for (int i = 0; i < maxNPCs; i++) {
			if (npcs[i] == null)
				continue;
			npcs[i].clearUpdateFlags();

		}

		for (int i = 0; i < maxNPCs; i++) {
			if (npcs[i] != null) {
				if (npcs[i].actionTimer > 0) {
					npcs[i].actionTimer--;
				}

				if (npcs[i].lastX != npcs[i].getX()
						|| npcs[i].lastY != npcs[i].getY()) {
					npcs[i].lastX = npcs[i].getX();
					npcs[i].lastY = npcs[i].getY();
				}

				if (npcs[i].freezeTimer > 0) {
					npcs[i].freezeTimer--;
				}

				if (npcs[i].hitDelayTimer > 0) {
					npcs[i].hitDelayTimer--;
				}

				if (npcs[i].hitDelayTimer == 1) {
					npcs[i].hitDelayTimer = 0;
					applyDamage(i);
				}

				if (npcs[i].attackTimer > 0) {
					npcs[i].attackTimer--;
				}

				if (npcs[i].spawnedBy > 0) { // delete summons npc
					if (PlayerHandler.players[npcs[i].spawnedBy] == null
							|| PlayerHandler.players[npcs[i].spawnedBy].heightLevel != npcs[i].heightLevel
							|| PlayerHandler.players[npcs[i].spawnedBy].respawnTimer > 0
							|| !PlayerHandler.players[npcs[i].spawnedBy]
									.goodDistance(
											npcs[i].getX(),
											npcs[i].getY(),
											PlayerHandler.players[npcs[i].spawnedBy]
													.getX(),
											PlayerHandler.players[npcs[i].spawnedBy]
													.getY(), 100)) {

						if (PlayerHandler.players[npcs[i].spawnedBy] != null) {
							for (int o = 0; o < PlayerHandler.players[npcs[i].spawnedBy].barrowsNpcs.length; o++) {
								if (npcs[i].npcType == PlayerHandler.players[npcs[i].spawnedBy].barrowsNpcs[o][0]) {
									if (PlayerHandler.players[npcs[i].spawnedBy].barrowsNpcs[o][1] == 1)
										PlayerHandler.players[npcs[i].spawnedBy].barrowsNpcs[o][1] = 0;
								}
							}
						}
						npcs[i] = null;
					}
				}
				if (npcs[i] == null)
					continue;

				/**
				 * Attacking player
				 **/
				if (isAggressive(i) && !npcs[i].underAttack && !npcs[i].isDead
						&& !switchesAttackers(i)) {
					npcs[i].killerId = getCloseRandomPlayer(i);
				} else if (isAggressive(i) && !npcs[i].underAttack
						&& !npcs[i].isDead && switchesAttackers(i)) {
					npcs[i].killerId = getCloseRandomPlayer(i);
				}

				if (System.currentTimeMillis() - npcs[i].lastDamageTaken > 5000)
					npcs[i].underAttackBy = 0;

				if ((npcs[i].killerId > 0 || npcs[i].underAttack)
						&& !npcs[i].walkingHome && retaliates(npcs[i].npcType)) {
					if (!npcs[i].isDead) {
						int p = npcs[i].killerId;
						if (PlayerHandler.players[p] != null) {
							Client c = (Client) PlayerHandler.players[p];
							followPlayer(i, c.playerId);
							if (npcs[i] == null)
								continue;
							if (npcs[i].attackTimer == 0) {
								attackPlayer(c, i);
							}
						} else {
							npcs[i].killerId = 0;
							npcs[i].underAttack = false;
							npcs[i].facePlayer(0);
						}
					}
				}

				/**
				 * Random walking and walking home
				 **/
				if (npcs[i] == null)
					continue;
				if ((!npcs[i].underAttack || npcs[i].walkingHome)
						&& npcs[i].randomWalk && !npcs[i].isDead) {
					npcs[i].facePlayer(0);
					npcs[i].killerId = 0;
					if (npcs[i].spawnedBy == 0) {
						if ((npcs[i].absX > npcs[i].makeX
								+ Config.NPC_RANDOM_WALK_DISTANCE)
								|| (npcs[i].absX < npcs[i].makeX
										- Config.NPC_RANDOM_WALK_DISTANCE)
								|| (npcs[i].absY > npcs[i].makeY
										+ Config.NPC_RANDOM_WALK_DISTANCE)
								|| (npcs[i].absY < npcs[i].makeY
										- Config.NPC_RANDOM_WALK_DISTANCE)) {
							npcs[i].walkingHome = true;
						}
					}

					if (npcs[i].walkingHome && npcs[i].absX == npcs[i].makeX
							&& npcs[i].absY == npcs[i].makeY) {
						npcs[i].walkingHome = false;
					} else if (npcs[i].walkingHome) {
						npcs[i].moveX = GetMove(npcs[i].absX, npcs[i].makeX);
						npcs[i].moveY = GetMove(npcs[i].absY, npcs[i].makeY);
						handleClipping(i);
						npcs[i].getNextNPCMovement(i);
						npcs[i].updateRequired = true;
					}
					if (npcs[i].npcType == 3782 && PestControl.gameStarted) {
						if (Misc.random(10) == 5)
							npcs[i].forceChat(voidKnightTalk[Misc
									.random3(voidKnightTalk.length)]);
					}
					if (npcs[i].npcType == 2286) {
						npcs[i].forceChat("Single PvP area here!");
					}
					if (npcs[i].npcType == 2282) {
						npcs[i].forceChat("Multi PvP area here!");
					}
					if (npcs[i].npcType == 2284) {
						npcs[i].forceChat("Fun pk area here!");
					}
					if (npcs[i].npcType == 2287) {
						npcs[i].forceChat("Duel arena here!");
					}
					if (npcs[i].npcType == 7121) {
						npcs[i].forceChat("Champions challenge mini-game here!");
					}
					if (npcs[i].npcType == 2576) {
						npcs[i].forceChat("Range & mage training here!");
					}
					if (npcs[i].npcType == 2579) {
						npcs[i].forceChat("Low level dungeon here!");
					}
					if (npcs[i].npcType == 2575) {
						npcs[i].forceChat("Mid level dungeon here!");
					}
					if (npcs[i].npcType == 2577) {
						npcs[i].forceChat("High level dungeon here!");
					}
					if (npcs[i].npcType == 2578) {
						npcs[i].forceChat("Revenants & bosses dungeon here!");
					}
					if (npcs[i].npcType == 2643) {
						npcs[i].forceChat("Kalphite lair here!");
					}
					if (npcs[i].npcType == 7665) {
						npcs[i].forceChat("Slayer dungeon here!");
					}
					if (npcs[i].npcType == 2618) {
						npcs[i].forceChat("Fight caves mini-game here!");
					}
					if (npcs[i].npcType == 1395) {
						npcs[i].forceChat("Woodcutting area here!");
					}
					if (npcs[i].npcType == 220) {
						npcs[i].forceChat("Fishing area here!");
					}
					if (npcs[i].npcType == 2258) {
						npcs[i].forceChat("Runecrafting area here!");
					}
					if (npcs[i].npcType == 291) {
						npcs[i].forceChat("Farming area here!");
					}
					if (npcs[i].npcType == 1661) {
						npcs[i].forceChat("Agility course here!");
					}
					if (npcs[i].npcType == 7664) {
						npcs[i].forceChat("Barrelchest boss here!");
					}
					if (npcs[i].npcType == 1686) {
						npcs[i].forceChat("Barrows mini-game here!");
					}
					if (npcs[i].npcType == 5478) {
						npcs[i].forceChat("Barbarian assualt mini-game here!");
					}
					if (npcs[i].npcType == 3788) {
						npcs[i].forceChat("Pest control mini-game here!");
					}
					if (npcs[i].npcType == 6731) {
						npcs[i].forceChat("God wars bosses here!");
					}
					if (npcs[i].npcType == 650) {
						npcs[i].forceChat("Spirit warriors mini-game here!");
					}
					if (npcs[i].npcType == 9316) {
						npcs[i].forceChat("Portal room here!");
					}
					if (npcs[i].npcType == 9403) {
						npcs[i].forceChat("Wilderness ice area here!");
					}
					if (npcs[i].npcType == 9371) {
						npcs[i].forceChat("The giant ork bork here!");
					}
					if (npcs[i].npcType == 8121) {
						npcs[i].forceChat("The corporal beast here!");
					}
					if (npcs[i].npcType == 9623) {
						npcs[i].forceChat("Avatars and tormented demons here!");
					}
					if (npcs[i].npcType == 8591) {
						npcs[i].forceChat("The feirce nomad here!");
					}
					if (npcs[i].npcType == 273) {
						npcs[i].forceChat("Tormented demon lair here!");
					}
					if (npcs[i].npcType == 9047) {
						npcs[i].forceChat("The mighty general of zaros nex here!");
					}
					
					if (npcs[i].npcType == 212) {
						npcs[i].forceChat("Unholy Cursebearer Teleport Here!");
					}
						
					if (npcs[i].npcType == 232) {
						npcs[i].forceChat("Mining Here!");
					}
					
					if (npcs[i].npcType == 1337) {
						npcs[i].forceChat("Skilling Area");
					}
					
					if (npcs[i].walkingType == 2) {
						npcs[i].turnNpc(npcs[i].absX, npcs[i].absY + 1);
					}

					if (npcs[i].walkingType == 3) {
						npcs[i].turnNpc(npcs[i].absX, npcs[i].absY - 1);
					}

					if (npcs[i].walkingType == 4) {
						npcs[i].turnNpc(npcs[i].absX + 1, npcs[i].absY);
					}

					if (npcs[i].walkingType == 5) {
						npcs[i].turnNpc(npcs[i].absX - 1, npcs[i].absY);
					}

					if (npcs[i].walkingType == 1) {
						if (Misc.random(3) == 1 && !npcs[i].walkingHome) {
							int MoveX = 0;
							int MoveY = 0;
							int Rnd = Misc.random(9);
							if (Rnd == 1) {
								MoveX = 1;
								MoveY = 1;
							} else if (Rnd == 2) {
								MoveX = -1;
							} else if (Rnd == 3) {
								MoveY = -1;
							} else if (Rnd == 4) {
								MoveX = 1;
							} else if (Rnd == 5) {
								MoveY = 1;
							} else if (Rnd == 6) {
								MoveX = -1;
								MoveY = -1;
							} else if (Rnd == 7) {
								MoveX = -1;
								MoveY = 1;
							} else if (Rnd == 8) {
								MoveX = 1;
								MoveY = -1;
							}

							if (MoveX == 1) {
								if (npcs[i].absX + MoveX < npcs[i].makeX + 1) {
									npcs[i].moveX = MoveX;
								} else {
									npcs[i].moveX = 0;
								}
							}

							if (MoveX == -1) {
								if (npcs[i].absX - MoveX > npcs[i].makeX - 1) {
									npcs[i].moveX = MoveX;
								} else {
									npcs[i].moveX = 0;
								}
							}

							if (MoveY == 1) {
								if (npcs[i].absY + MoveY < npcs[i].makeY + 1) {
									npcs[i].moveY = MoveY;
								} else {
									npcs[i].moveY = 0;
								}
							}

							if (MoveY == -1) {
								if (npcs[i].absY - MoveY > npcs[i].makeY - 1) {
									npcs[i].moveY = MoveY;
								} else {
									npcs[i].moveY = 0;
								}
							}

							int x = (npcs[i].absX + npcs[i].moveX);
							int y = (npcs[i].absY + npcs[i].moveY);
							handleClipping(i);
							npcs[i].getNextNPCMovement(i);
							npcs[i].updateRequired = true;
						}
					}
				}

				if (npcs[i].isDead == true) {
					if (npcs[i].actionTimer == 0 && npcs[i].applyDead == false
							&& npcs[i].needRespawn == false) {
						Client c = (Client) PlayerHandler.players[NPCHandler.npcs[i].killedBy];
						npcs[i].updateRequired = true;
						npcs[i].facePlayer(0);
						npcs[i].killedBy = getNpcKillerId(i);
						npcs[i].animNumber = getDeadEmote(i); // dead emote
						npcs[i].animUpdateRequired = true;
						npcs[i].freezeTimer = 0;
						npcs[i].applyDead = true;
						npcs[i].actionTimer = getDeadTime(i); // delete time
						killedBarrow(i);
						if (isBarbNpc(i))
							killedBarb(i);
						 if (isRFDNpc(i))
                            killedRFD(i);
						if (isSpiritNpc(i))
							killedSpirit(i);
						resetPlayersInCombat(i);
						npcs[i].dagColor = "";

					} else if (npcs[i].actionTimer == 0
							&& npcs[i].applyDead == true
							&& npcs[i].needRespawn == false) {
						npcs[i].needRespawn = true;
						npcs[i].actionTimer = getRespawnTime(i); // respawn time
						if (!npcs[i].inBarbDef())
							dropItems(i); // npc drops items!
						if (npcs[i].npcType == 3491) {
                                                        handleRFDDeath(i);
                                                }
						appendSlayerExperience(i);
						tzhaarDeathHandler(i);
						npcs[i].absX = npcs[i].makeX;
						npcs[i].absY = npcs[i].makeY;
						npcs[i].HP = npcs[i].MaxHP;
						npcs[i].animNumber = 0x328;
						npcs[i].updateRequired = true;
						npcs[i].animUpdateRequired = true;
						if (npcs[i].npcType >= 2440 && npcs[i].npcType <= 2446) {
							Server.objectManager.removeObject(npcs[i].absX,
									npcs[i].absY);
						}
					} else if (npcs[i].actionTimer == 0
							&& npcs[i].needRespawn == true
							&& npcs[i].npcType != 1158) {
						Client player = (Client) PlayerHandler.players[npcs[i].spawnedBy];
						if (player != null) {
							npcs[i] = null;
						} else {
							int old1 = npcs[i].npcType;
							int old2 = npcs[i].makeX;
							int old3 = npcs[i].makeY;
							int old4 = npcs[i].heightLevel;
							int old5 = npcs[i].walkingType;
							int old6 = npcs[i].MaxHP;
							int old7 = npcs[i].maxHit;
							int old8 = npcs[i].attack;
							int old9 = npcs[i].defence;

							npcs[i] = null;
							newNPC(old1, old2, old3, old4, old5, old6, old7,
									old8, old9);
						}
					}
				}
			}
		}
	}

	public boolean getsPulled(int i) {
		switch (npcs[i].npcType) {
		case 6260:
			if (npcs[i].firstAttacker > 0)
				return false;
			break;
		}
		return true;
	}

	public boolean multiAttacks(int i) {
		switch (npcs[i].npcType) {
		case 1158: // kq
			if (npcs[i].attackType == 1 || npcs[i].attackType == 2)
				return true;
		case 1160: // kq 2
			if (npcs[i].attackType == 1 || npcs[i].attackType == 2)
				return true;
		case 6222:// kree
			if (npcs[i].attackType == 1 || npcs[i].attackType == 2)
				return true;
		case 6247:// zilyana
			if (npcs[i].attackType == 2)
				return true;
		case 6260:// bandos
			if (npcs[i].attackType == 1)
				return true;
		case 6203:// zammy
			if (npcs[i].attackType == 2)
				return true;
		case 8133:// corp
			if (npcs[i].attackType == 2 || npcs[i].attackType == 1)
				return true;
		case 9000:// nex
			if (npcs[i].attackType == 2 || npcs[i].attackType == 1)
				return true;
		default:
			return false;
		}

	}

	/**
	 * Npc killer id?
	 **/

	public int getNpcKillerId(int npcId) {
		int oldDamage = 0;
		int killerId = 0;
		for (int p = 1; p < PlayerHandler.players.length; p++) {
			if (PlayerHandler.players[p] != null) {
				if (PlayerHandler.players[p].lastNpcAttacked == npcId) {
					if (PlayerHandler.players[p].totalDamageDealt > oldDamage) {
						oldDamage = PlayerHandler.players[p].totalDamageDealt;
						killerId = p;
					}
					PlayerHandler.players[p].totalDamageDealt = 0;
				}
			}
		}
		return killerId;
	}
	
	 private void killedRFD(int i) {
                final Client c2 = (Client)Server.playerHandler.players[npcs[i].spawnedBy];
                c2.RFDKilled++;
                //System.out.println("To kill: " + c2.RFDToKill + " killed: " + c2.RFDKilled); //erase the // if you want cmd prompt to say this.
                if (c2.RFDKilled == c2.RFDToKill) { //I think this is if you have killed one monster.
                        //c2.sendMessage("STARTING EVENT"); //erase the // if you want it to say this to player.
                        c2.waveId++;
                        EventManager.getSingleton().addEvent(new Event() { //next event
                                public void execute(EventContainer c) {
                                        if (c2 != null) {
                                                Server.rfd.spawnNextWave(c2); //goes to next level
                                        }       
                                        c.stop();
                                }
                        }, 7500);
                        
                }
        }
		
		public void handleRFDDeath(int i) {
                Client c = (Client)Server.playerHandler.players[npcs[i].spawnedBy];
                c.Agrith = true;
                c.sendMessage("Congratulations you have completed the RFD minigame!");
                c.getPA().resetRFD();
                c.waveId = 300;
        }

	private void killedBarb(int i) {
		final Client c2 = (Client) PlayerHandler.players[npcs[i].spawnedBy];
		if (c2 != null) {
			c2.barbsKilled++;
			if (c2.barbsKilled == c2.barbsToKill) {
				c2.barbWave++;
				EventManager.getSingleton().addEvent(new Event() {
					@Override
					public void execute(EventContainer c) {
						if (c2 != null) {
							Server.barbDefence.spawnWave(c2);
						}
						c.stop();
					}
				}, 2500);
			}
		}
	}

	private void killedSpirit(int i) {
		final Client c = (Client) PlayerHandler.players[npcs[i].killedBy];
		if (c != null) {
			c.spiritCount++;
			if (c.spiritCount == 4) {
				c.spiritWave = 1;
				c.secondWave = true;
			}
			if (c.spiritCount == 8) {
				c.spiritWave = 2;
				c.thirdWave = true;
			}
			if (c.spiritCount == 12) {
				c.spiritWave = 3;
				c.fourthWave = true;
			}
			if (c.spiritCount == 16) {
				c.spiritWave = 4;
				c.canLoot = true;
			}
		}
	}

	/**
	 * Champions challenge
	 */
	public void spawnSecondChallenge(final int i) { // skeleton
		Client c = (Client) PlayerHandler.players[npcs[i].killedBy];
		if (c != null) {
			CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
				Client c = (Client) PlayerHandler.players[npcs[i].killedBy];

				@Override
				public void execute(CycleEventContainer container) {
					if (c != null) {
						c.slayerPoints += 2;
						c.sendMessage("You have received 2 Slayer points for deafeating this champion.");
						spawnNpc(c, 3065, 3164, 9758, c.playerId * 4, 0, 116,
								15, 100, 50, true, true);
						container.stop();
					}
				}

				@Override
				public void stop() {

				}
			}, 4);
		}
	}

	public void spawnThirdChallenge(final int i) { // zombie
		Client c = (Client) PlayerHandler.players[npcs[i].killedBy];
		if (c != null) {
			CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
				Client c = (Client) PlayerHandler.players[npcs[i].killedBy];

				@Override
				public void execute(CycleEventContainer container) {
					if (c != null) {
						c.slayerPoints += 4;
						c.sendMessage("You have received 4 Slayer points for deafeating this champion.");
						spawnNpc(c, 3066, 3164, 9758, c.playerId * 4, 0, 120,
								18, 75, 75, true, true);
						container.stop();
					}
				}

				@Override
				public void stop() {

				}
			}, 4);
		}
	}

	public void spawnFourthChallenge(final int i) { // giant
		Client c = (Client) PlayerHandler.players[npcs[i].killedBy];
		if (c != null) {
			CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
				Client c = (Client) PlayerHandler.players[npcs[i].killedBy];

				@Override
				public void execute(CycleEventContainer container) {
					if (c != null) {
						c.slayerPoints += 6;
						c.sendMessage("You have received 6 Slayer points for deafeating this champion.");
						spawnNpc(c, 3058, 3164, 9758, c.playerId * 4, 0, 140,
								20, 100, 100, true, true);
						container.stop();
					}
				}

				@Override
				public void stop() {

				}
			}, 4);
		}
	}

	public void spawnFifthChallenge(final int i) { // banshee
		Client c = (Client) PlayerHandler.players[npcs[i].killedBy];
		if (c != null) {
			CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
				Client c = (Client) PlayerHandler.players[npcs[i].killedBy];

				@Override
				public void execute(CycleEventContainer container) {
					if (c != null) {
						c.slayerPoints += 8;
						c.sendMessage("You have received 8 Slayer points for deafeating this champion.");
						spawnNpc(c, 8986, 3164, 9758, c.playerId * 4, 0, 200,
								26, 150, 150, true, true);
						container.stop();
					}
				}

				@Override
				public void stop() {

				}
			}, 4);
		}
	}

	public void spawnSixthChallenge(final int i) { // ghoul
		Client c = (Client) PlayerHandler.players[npcs[i].killedBy];
		if (c != null) {
			CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
				Client c = (Client) PlayerHandler.players[npcs[i].killedBy];

				@Override
				public void execute(CycleEventContainer container) {
					if (c != null) {
						c.slayerPoints += 10;
						c.sendMessage("You have received 10 Slayer points for deafeating this champion.");
						spawnNpc(c, 3059, 3164, 9758, c.playerId * 4, 0, 200,
								26, 200, 200, true, true);
						container.stop();
					}
				}

				@Override
				public void stop() {

				}
			}, 4);
		}
	}

	public void spawnSeventhChallenge(final int i) { // ogress
		Client c = (Client) PlayerHandler.players[npcs[i].killedBy];
		if (c != null) {
			CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
				Client c = (Client) PlayerHandler.players[npcs[i].killedBy];

				@Override
				public void execute(CycleEventContainer container) {
					if (c != null) {
						c.slayerPoints += 12;
						c.sendMessage("You have received 12 Slayer points for deafeating this champion.");
						spawnNpc(c, 7078, 3164, 9758, c.playerId * 4, 0, 222,
								28, 250, 250, true, true);
						container.stop();
					}
				}

				@Override
				public void stop() {

				}
			}, 4);
		}
	}

	public void spawnEighthChallenge(final int i) { // aberrant
		Client c = (Client) PlayerHandler.players[npcs[i].killedBy];
		if (c != null) {
			CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
				Client c = (Client) PlayerHandler.players[npcs[i].killedBy];

				@Override
				public void execute(CycleEventContainer container) {
					if (c != null) {
						c.slayerPoints += 14;
						c.sendMessage("You have received 14 Slayer points for deafeating this champion.");
						spawnNpc(c, 8987, 3164, 9758, c.playerId * 4, 0, 222,
								28, 300, 300, true, true);
						container.stop();
					}
				}

				@Override
				public void stop() {

				}
			}, 4);
		}
	}

	public void spawnNineChallenge(final int i) { // earth warrior
		Client c = (Client) PlayerHandler.players[npcs[i].killedBy];
		if (c != null) {
			CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
				Client c = (Client) PlayerHandler.players[npcs[i].killedBy];

				@Override
				public void execute(CycleEventContainer container) {
					if (c != null) {
						c.slayerPoints += 16;
						c.sendMessage("You have received 16 Slayer points for deafeating this champion.");
						spawnNpc(c, 3057, 3164, 9758, c.playerId * 4, 0, 216,
								30, 350, 350, true, true);
						container.stop();
					}
				}

				@Override
				public void stop() {

				}
			}, 4);
		}
	}

	public void spawnTenthChallenge(final int i) { // mummy
		Client c = (Client) PlayerHandler.players[npcs[i].killedBy];
		if (c != null) {
			CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
				Client c = (Client) PlayerHandler.players[npcs[i].killedBy];

				@Override
				public void execute(CycleEventContainer container) {
					if (c != null) {
						c.slayerPoints += 18;
						c.sendMessage("You have received 18 Slayer points for deafeating this champion.");
						spawnNpc(c, 8989, 3164, 9758, c.playerId * 4, 0, 255,
								35, 400, 400, true, true);
						container.stop();
					}
				}

				@Override
				public void stop() {

				}
			}, 4);
		}
	}

	public void spawnEleventhChallenge(final int i) { // lesser
		Client c = (Client) PlayerHandler.players[npcs[i].killedBy];
		if (c != null) {
			CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
				Client c = (Client) PlayerHandler.players[npcs[i].killedBy];

				@Override
				public void execute(CycleEventContainer container) {
					if (c != null) {
						c.slayerPoints += 20;
						c.sendMessage("You have received 20 Slayer points for deafeating this champion.");
						spawnNpc(c, 3064, 3164, 9758, c.playerId * 4, 0, 255,
								35, 450, 450, true, true);
						container.stop();
					}
				}

				@Override
				public void stop() {

				}
			}, 4);
		}
	}

	public void spawnFinalChallenge(final int i) { // leon
		Client c = (Client) PlayerHandler.players[npcs[i].killedBy];
		if (c != null) {
			CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
				Client c = (Client) PlayerHandler.players[npcs[i].killedBy];

				@Override
				public void execute(CycleEventContainer container) {
					if (c != null) {
						c.slayerPoints += 22;
						c.sendMessage("You have received 22 Slayer points for deafeating this champion.");
						spawnNpc(c, 3067, 3164, 9758, c.playerId * 4, 0, 300,
								40, 500, 500, true, true);
						container.stop();
					}
				}

				@Override
				public void stop() {

				}
			}, 4);
		}
	}

	/**
	 * KQ spawn
	 */

	private void spawnSecondForm(final int i) {
		EventManager.getSingleton().addEvent(new Event() {
			@Override
			public void execute(EventContainer e) {

				spawnNpc2(1160, npcs[i].absX, npcs[i].absY, 0, 1, 255, 31, 600,
						300);
				e.stop();
			}
		}, 4000);
	}

	private void spawnFirstForm(final int i) {
		EventManager.getSingleton().addEvent(new Event() {
			@Override
			public void execute(EventContainer e) {

				spawnNpc2(1158, npcs[i].absX, npcs[i].absY, 0, 1, 255, 31, 600,
						300);
				e.stop();

			}

		}, 30000);
	}

	private void wonChallenge(final int i) {
		Client c = (Client) PlayerHandler.players[npcs[i].killedBy];
		if (c != null) {
			CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
				Client c = (Client) PlayerHandler.players[npcs[i].killedBy];

				@Override
				public void execute(CycleEventContainer container) {
					if (c != null) {
						c.getPA().wonChallenge();
						PlayerHandler.yell("@dre@"
								+ Misc.optimizeText(c.playerName)
								+ " has defeated the Champion of Champions!");
						container.stop();
					}
				}

				@Override
				public void stop() {

				}
			}, 5);
		}
	}

	private void killedBarrow(int i) {
		Client c = (Client) PlayerHandler.players[npcs[i].killedBy];
		if (c != null) {
			for (int o = 0; o < c.barrowsNpcs.length; o++) {
				if (npcs[i].npcType == c.barrowsNpcs[o][0]) {
					c.barrowsNpcs[o][1] = 2; // 2 for dead
					c.barrowsKillCount++;

				}
			}
		}
	}

	public void healRevenants(final int i) {
		if (npcs[i].npcType == 6688 || npcs[i].npcType == 6689
				|| npcs[i].npcType == 6729 || npcs[i].npcType == 6691
				|| npcs[i].npcType == 6730) {
			int r = Misc.random(2);
			if (r == 1) {
				if (npcs[i].HP < 50) {
					npcs[i].HP += 25;
					npcs[i].updateRequired = true;

				}
			}
		}
	}

	public boolean isBoss(int i) {
		switch (NPCHandler.npcs[i].npcType) {
		case 6688:
		case 6689:
		case 6729:
		case 6691:
		case 6730:
		case 7133:
		case 8349:
		case 8133:
		case 8528:
		case 8596:
		case 8597:
		case 9437:
		case 6247:
		case 6222:
		case 6260:
		case 6203:
		case 2883:
		case 2882:
		case 2881:
		case 50:
		case 1160:
		case 3200:
		case 5666:
		case 10127:
			return true;
		}
		return false;
	}

	/**
	 * Dropping Items!
	 **/

	public void dropItems(int i) {
		int npc = 0;
		Client c = (Client) PlayerHandler.players[npcs[i].killedBy];
		if (c != null) {
			if (npcs[i].npcType == 912 || npcs[i].npcType == 913
					|| npcs[i].npcType == 914)
				c.magePoints += 1;
			// int herbRandom = Misc.random(20);
			// if(herbRandom==3 || herbRandom==5){
			// Server.itemHandler.createGroundItem(c,
			// Config.HERBS[Misc.random(14)], npcs[i].absX,
			// npcs[i].absY, Config.NPC_DROPS[npc][2],
			// c.playerId);
			for (npc = 0; npc < Config.NPC_DROPS.length; npc++) {
				if (npcs[i].npcType == Config.NPC_DROPS[npc][0]) {
					if (Misc.random(Config.NPC_DROPS[npc][3] / 2) == 0) {
						if (c.clanId >= 0)
						if (Config.NPC_DROPS[npc][3] >= 50) {
									Server.clanChat.handleLootShare(c,
									Config.NPC_DROPS[npc][1],
									Config.NPC_DROPS[npc][2]);
								
								}
						Server.itemHandler.createGroundItem(c,
								Config.NPC_DROPS[npc][1], npcs[i].absX,
								npcs[i].absY, Config.NPC_DROPS[npc][2],
								c.playerId);
					}
				}
			}
		}
	}

	// id of bones dropped by npcs
	public int boneDrop(int type) {
		switch (type) {
		case 1:// normal bones
		case 9:
		case 100:
		case 12:
		case 17:
		case 803:
		case 18:
		case 81:
		case 101:
		case 41:
		case 19:
		case 90:
		case 75:
		case 86:
		case 78:
		case 912:
		case 913:
		case 914:
		case 1648:
		case 1643:
		case 1618:
		case 1624:
		case 181:
		case 119:
		case 49:
		case 26:
		case 1341:
			return 526;
		case 117:
			return 532;// big bones
		case 50:// drags
		case 53:
		case 54:
		case 55:
		case 941:
		case 1590:
		case 1591:
		case 1592:
		case 5362:
		case 5363:
			return 536;
		case 84:
		case 1615:
		case 1613:
		case 82:
		case 3200:
			return 592;
		case 2881:
		case 2882:
		case 2883:
			return 6729;
		default:
			return -1;
		}
	}

	public int getStackedDropAmount(int itemId, int npcId) {
		switch (itemId) {
		case 995:
			switch (npcId) {
			case 1:
				return 50 + Misc.random(50);
			case 9:
				return 133 + Misc.random(100);
			case 1624:
				return 1000 + Misc.random(300);
			case 1618:
				return 1000 + Misc.random(300);
			case 1643:
				return 1000 + Misc.random(300);
			case 1610:
				return 1000 + Misc.random(1000);
			case 1613:
				return 1500 + Misc.random(1250);
			case 1615:
				return 3000;
			case 18:
				return 500;
			case 101:
				return 60;
			case 913:
			case 912:
			case 914:
				return 750 + Misc.random(500);
			case 1612:
				return 250 + Misc.random(500);
			case 1648:
				return 250 + Misc.random(250);
			case 90:
				return 200;
			case 82:
				return 1000 + Misc.random(455);
			case 52:
				return 400 + Misc.random(200);
			case 49:
				return 1500 + Misc.random(2000);
			case 1341:
				return 1500 + Misc.random(500);
			case 26:
				return 500 + Misc.random(100);
			case 20:
				return 750 + Misc.random(100);
			case 21:
				return 890 + Misc.random(125);
			case 117:
				return 500 + Misc.random(250);
			case 2607:
				return 500 + Misc.random(350);
			}
			break;
		case 11212:
			return 10 + Misc.random(4);
		case 565:
		case 561:
			return 10;
		case 560:
		case 563:
		case 562:
			return 15;
		case 555:
		case 554:
		case 556:
		case 557:
			return 20;
		case 892:
			return 40;
		case 886:
			return 100;
		case 6522:
			return 6 + Misc.random(5);

		}

		return 1;
	}

	/**
	 * Slayer Experience
	 **/
	public void appendSlayerExperience(int i) {
		int npc = 0;
		Client c = (Client) PlayerHandler.players[npcs[i].killedBy];
		if (c != null) {
			if (c.slayerTask == npcs[i].npcType) {
				c.taskAmount--;
				c.getPA().addSkillXP(npcs[i].MaxHP * Config.SLAYER_EXPERIENCE,
						18);
				if (c.taskAmount <= 0) {
					if (npcs[i].npcType == 1265 || npcs[i].npcType == 4688
							|| npcs[i].npcType == 4686
							|| npcs[i].npcType == 1584) {
						c.getPA().addSkillXP(
								(npcs[i].MaxHP * 4) * Config.SLAYER_EXPERIENCE,
								18);
						c.slayerPoints += 5;
						c.slayerTask = -1;
						c.sendMessage("You completed your easy slayer task. Please see the slayer master to get a new one.");
						c.sendMessage("You received 5 slayer points for completing this task.");
					}
					if (npcs[i].npcType == 1645 || npcs[i].npcType == 7641
							|| npcs[i].npcType == 7793
							|| npcs[i].npcType == 1643
							|| npcs[i].npcType == 1618
							|| npcs[i].npcType == 7801
							|| npcs[i].npcType == 1624
							|| npcs[i].npcType == 7805
							|| npcs[i].npcType == 4695
							|| npcs[i].npcType == 4698
							|| npcs[i].npcType == 1956
							|| npcs[i].npcType == 4677 || npcs[i].npcType == 55
							|| npcs[i].npcType == 6106) {
						c.getPA().addSkillXP(
								(npcs[i].MaxHP * 6) * Config.SLAYER_EXPERIENCE,
								18);
						c.slayerPoints += 10;
						c.slayerTask = -1;
						c.sendMessage("You completed your medium slayer task. Please see the slayer master to get a new one.");
						c.sendMessage("You received 10 slayer points for completing this task.");
					}
					if (npcs[i].npcType == 1610 || npcs[i].npcType == 7643
							|| npcs[i].npcType == 1613
							|| npcs[i].npcType == 1615
							|| npcs[i].npcType == 7798
							|| npcs[i].npcType == 7800
							|| npcs[i].npcType == 2783
							|| npcs[i].npcType == 7797
							|| npcs[i].npcType == 7799
							|| npcs[i].npcType == 2452
							|| npcs[i].npcType == 4705
							|| npcs[i].npcType == 6103
							|| npcs[i].npcType == 7795
							|| npcs[i].npcType == 1590 || npcs[i].npcType == 53
							|| npcs[i].npcType == 1591
							|| npcs[i].npcType == 5362
							|| npcs[i].npcType == 1592
							|| npcs[i].npcType == 5363
							|| npcs[i].npcType == 10773) {
						c.getPA().addSkillXP(
								(npcs[i].MaxHP * 8) * Config.SLAYER_EXPERIENCE,
								18);
						c.slayerPoints += 15;
						c.slayerTask = -1;
						c.sendMessage("You completed your hard slayer task. Please see the slayer master to get a new one.");
						c.sendMessage("You received 15 slayer points for completing this task.");
					}

				}

			}
		}
	}

	/**
	 * Resets players in combat
	 */

	public void resetPlayersInCombat(int i) {
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null)
				if (PlayerHandler.players[j].underAttackBy2 == i)
					PlayerHandler.players[j].underAttackBy2 = 0;
		}
	}

	/**
	 * Npc names
	 **/

	public String getNpcName(int npcId) {
		for (int i = 0; i < maxNPCs; i++) {
			if (NPCHandler.NpcList[i] != null) {
				if (NPCHandler.NpcList[i].npcId == npcId) {
					return NPCHandler.NpcList[i].npcName;
				}
			}
		}
		return "-1";
	}

	/**
	 * Npc Follow Player
	 **/

	public int GetMove(int Place1, int Place2) {
		if ((Place1 - Place2) == 0) {
			return 0;
		} else if ((Place1 - Place2) < 0) {
			return 1;
		} else if ((Place1 - Place2) > 0) {
			return -1;
		}
		return 0;
	}

	public boolean followPlayer(int i) {
		switch (npcs[i].npcType) {
		case 2892:
		case 2894:
		case 9463:
			return false;
		}
		return true;
	}

	public void followPlayer(int i, int playerId) {
		if (PlayerHandler.players[playerId] == null) {
			return;
		}
		if (PlayerHandler.players[playerId].respawnTimer > 0) {
			npcs[i].facePlayer(0);
			npcs[i].randomWalk = true;
			npcs[i].underAttack = false;
			return;
		}

		if (!followPlayer(i)) {
			npcs[i].facePlayer(playerId);
			return;
		}

		int playerX = PlayerHandler.players[playerId].absX;
		int playerY = PlayerHandler.players[playerId].absY;
		npcs[i].randomWalk = false;
		if (goodDistance(npcs[i].getX(), npcs[i].getY(), playerX, playerY,
				distanceRequired(i)))
			return;
		if ((npcs[i].spawnedBy > 0)
				|| ((npcs[i].absX < npcs[i].makeX + Config.NPC_FOLLOW_DISTANCE)
						&& (npcs[i].absX > npcs[i].makeX
								- Config.NPC_FOLLOW_DISTANCE)
						&& (npcs[i].absY < npcs[i].makeY
								+ Config.NPC_FOLLOW_DISTANCE) && (npcs[i].absY > npcs[i].makeY
						- Config.NPC_FOLLOW_DISTANCE))) {
			if (npcs[i].heightLevel == PlayerHandler.players[playerId].heightLevel) {
				if (PlayerHandler.players[playerId] != null && npcs[i] != null) {
					if (playerY < npcs[i].absY) {
						npcs[i].moveX = GetMove(npcs[i].absX, playerX);
						npcs[i].moveY = GetMove(npcs[i].absY, playerY);
					} else if (playerY > npcs[i].absY) {
						npcs[i].moveX = GetMove(npcs[i].absX, playerX);
						npcs[i].moveY = GetMove(npcs[i].absY, playerY);
					} else if (playerX < npcs[i].absX) {
						npcs[i].moveX = GetMove(npcs[i].absX, playerX);
						npcs[i].moveY = GetMove(npcs[i].absY, playerY);
					} else if (playerX > npcs[i].absX) {
						npcs[i].moveX = GetMove(npcs[i].absX, playerX);
						npcs[i].moveY = GetMove(npcs[i].absY, playerY);
					} else if (playerX == npcs[i].absX
							|| playerY == npcs[i].absY) {
						int o = Misc.random(3);
						switch (o) {
						case 0:
							npcs[i].moveX = GetMove(npcs[i].absX, playerX);
							npcs[i].moveY = GetMove(npcs[i].absY, playerY + 1);
							break;

						case 1:
							npcs[i].moveX = GetMove(npcs[i].absX, playerX);
							npcs[i].moveY = GetMove(npcs[i].absY, playerY - 1);
							break;

						case 2:
							npcs[i].moveX = GetMove(npcs[i].absX, playerX + 1);
							npcs[i].moveY = GetMove(npcs[i].absY, playerY);
							break;

						case 3:
							npcs[i].moveX = GetMove(npcs[i].absX, playerX - 1);
							npcs[i].moveY = GetMove(npcs[i].absY, playerY);
							break;
						}
					}
					int x = (npcs[i].absX + npcs[i].moveX);
					int y = (npcs[i].absY + npcs[i].moveY);
					npcs[i].facePlayer(playerId);
					if (npcs[i] == null)
						return;
					handleClipping(i);
					npcs[i].getNextNPCMovement(i);
					npcs[i].facePlayer(playerId);
					npcs[i].updateRequired = true;
				}
			}
		} else {
			npcs[i].facePlayer(0);
			npcs[i].killerId = 0;
			npcs[i].randomWalk = true;
			npcs[i].underAttack = false;
		}
	}

	public void handleClipping(int i) {
		try {
			NPC npc = npcs[i];
			if (npcs[i] == null)
				return;
			if (npc.moveX == 1 && npc.moveY == 1) {
				if ((Region.getClipping(npc.absX + 1, npc.absY + 1,
						npc.heightLevel) & 0x12801e0) != 0) {
					npc.moveX = 0;
					npc.moveY = 0;
					if ((Region.getClipping(npc.absX, npc.absY + 1,
							npc.heightLevel) & 0x1280120) == 0)
						npc.moveY = 1;
					else
						npc.moveX = 1;
				}
			} else if (npc.moveX == -1 && npc.moveY == -1) {
				if ((Region.getClipping(npc.absX - 1, npc.absY - 1,
						npc.heightLevel) & 0x128010e) != 0) {
					npc.moveX = 0;
					npc.moveY = 0;
					if ((Region.getClipping(npc.absX, npc.absY - 1,
							npc.heightLevel) & 0x1280102) == 0)
						npc.moveY = -1;
					else
						npc.moveX = -1;
				}
			} else if (npc.moveX == 1 && npc.moveY == -1) {
				if ((Region.getClipping(npc.absX + 1, npc.absY - 1,
						npc.heightLevel) & 0x1280183) != 0) {
					npc.moveX = 0;
					npc.moveY = 0;
					if ((Region.getClipping(npc.absX, npc.absY - 1,
							npc.heightLevel) & 0x1280102) == 0)
						npc.moveY = -1;
					else
						npc.moveX = 1;
				}
			} else if (npc.moveX == -1 && npc.moveY == 1) {
				if ((Region.getClipping(npc.absX - 1, npc.absY + 1,
						npc.heightLevel) & 0x128013) != 0) {
					npc.moveX = 0;
					npc.moveY = 0;
					if ((Region.getClipping(npc.absX, npc.absY + 1,
							npc.heightLevel) & 0x1280120) == 0)
						npc.moveY = 1;
					else
						npc.moveX = -1;
				}
			} // Checking Diagonal movement.

			if (npc.moveY == -1) {
				if ((Region
						.getClipping(npc.absX, npc.absY - 1, npc.heightLevel) & 0x1280102) != 0)
					npc.moveY = 0;
			} else if (npc.moveY == 1) {
				if ((Region
						.getClipping(npc.absX, npc.absY + 1, npc.heightLevel) & 0x1280120) != 0)
					npc.moveY = 0;
			} // Checking Y movement.
			if (npc.moveX == 1) {
				if ((Region
						.getClipping(npc.absX + 1, npc.absY, npc.heightLevel) & 0x1280180) != 0)
					npc.moveX = 0;
			} else if (npc.moveX == -1) {
				if ((Region
						.getClipping(npc.absX - 1, npc.absY, npc.heightLevel) & 0x1280108) != 0)
					npc.moveX = 0;
			} // Checking X movement.
		} catch (Exception e) {
			if (npcs[i] != null)
				System.out.println("npc clipping error by npc "
						+ npcs[i].npcType + " x" + npcs[i].absX + " y"
						+ npcs[i].absY);
			return;
		}
	}

	/**
	 * load spell
	 **/
	public void loadSpell2(int i) {
		npcs[i].attackType = 3;
		int random = Misc.random(3);
		if (random == 0) {
			npcs[i].projectileId = 393; // red
			npcs[i].endGfx = 430;
		} else if (random == 1) {
			npcs[i].projectileId = 394; // green
			npcs[i].endGfx = 429;
		} else if (random == 2) {
			npcs[i].projectileId = 395; // white
			npcs[i].endGfx = 431;
		} else if (random == 3) {
			npcs[i].projectileId = 396; // blue
			npcs[i].endGfx = 428;
		}
	}

	public void loadSpell(Client c, final int i) {
		switch (npcs[i].npcType) {
		case 8528: // Nomad
			int nomad;
			if (goodDistance(npcs[i].absX, npcs[i].absY,
					PlayerHandler.players[npcs[i].killerId].absX,
					PlayerHandler.players[npcs[i].killerId].absY, 1))
				nomad = Misc.random(5);
			else
				nomad = 5;
			if (nomad == 5) {
				npcs[i].projectileId = 88;
				npcs[i].endGfx = 89;
				npcs[i].attackType = 2;
			} else if (nomad < 5) {
				npcs[i].projectileId = -1;
				npcs[i].attackType = 0;
			}
			break;
		case 8349:
		case 8350:
		case 8351:
			int r3;
			if (goodDistance(npcs[i].absX, npcs[i].absY,
					PlayerHandler.players[npcs[i].killerId].absX,
					PlayerHandler.players[npcs[i].killerId].absY, 1))
				r3 = Misc.random(2);
			else
				r3 = Misc.random(1);
			if (r3 == 0) {
				npcs[i].attackType = 2;
				npcs[i].gfx0(1885);
				npcs[i].projectileId = 1884;
				npcs[i].endGfx = 1883;
			} else if (r3 == 1) {
				npcs[i].attackType = 1;
				npcs[i].projectileId = 1887;
			} else if (r3 == 2) {
				npcs[i].attackType = 0;
				npcs[i].gfx100(1886);
				npcs[i].projectileId = -1;
			}
			break;
		case 8133:
			if (goodDistance(npcs[i].absX, npcs[i].absY,
					PlayerHandler.players[npcs[i].killerId].absX,
					PlayerHandler.players[npcs[i].killerId].absY, 2))
				r3 = Misc.random(2);
			else
				r3 = Misc.random(1);
			if (r3 == 0) {
				npcs[i].attackType = 2;
				npcs[i].endGfx = -1;
				npcs[i].projectileId = 1825;
			} else if (r3 == 1) {
				npcs[i].attackType = 1;
				npcs[i].endGfx = -1;
				npcs[i].projectileId = 1824;
			} else if (r3 == 2) {
				npcs[i].attackType = 0;
				npcs[i].gfx100(1834);
				npcs[i].projectileId = -1;
			}
			break;
		/* 0 - melee, 1 - range, 2 - mage */
		case 5247: // Penance queen
			int penance;
			if (goodDistance(npcs[i].absX, npcs[i].absY,
					PlayerHandler.players[npcs[i].killerId].absX,
					PlayerHandler.players[npcs[i].killerId].absY, 1))
				penance = Misc.random(1);
			else
				penance = 0;
			if (penance == 0) {
				npcs[i].projectileId = 871;
				npcs[i].endGfx = 872;
				npcs[i].attackType = 1;
			} else {
				npcs[i].projectileId = -1;
				npcs[i].endGfx = -1;
				npcs[i].attackType = 0;
			}
			break;
		case 5229:
		case 5230:
		case 5231:
		case 5232:
		case 5233:
		case 5234:
		case 5235:
		case 5236:
		case 5237: // Penance ranger (ranged)
			npcs[i].projectileId = 866;
			npcs[i].endGfx = 865;
			npcs[i].attackType = 1;
			break;
		// lesser champ
		case 3064:
			for (int j = 0; j < PlayerHandler.players.length; j++) {
				if (PlayerHandler.players[j] != null) {
					int mage;
					if (goodDistance(npcs[i].absX, npcs[i].absY,
							PlayerHandler.players[npcs[i].killerId].absX,
							PlayerHandler.players[npcs[i].killerId].absY, 1))
						mage = Misc.random(1);
					else
						mage = 0;
					if (mage == 0) {
						npcs[i].attackType = 2; // flames
						npcs[i].endGfx = 346;
					} else if (mage == 1) {
						npcs[i].attackType = 0; // melee
						npcs[i].projectileId = -1;
					}
				}
			}
			break;
		// champion leon
		case 3067:
			for (int j = 0; j < PlayerHandler.players.length; j++) {
				if (PlayerHandler.players[j] != null) {
					int spec;
					spec = Misc.random(1);
					if (spec == 0) {
						npcs[i].attackType = 0; // melee
						npcs[i].gfx0(-1);
					} else if (spec == 1) {
						npcs[i].attackType = 2; // bgs spec
						npcs[i].gfx0(1223);
					}
				}
			}
			break;
		case 5666:
			for (int j = 0; j < PlayerHandler.players.length; j++) {
				if (PlayerHandler.players[j] != null) {
					int spec;
					spec = Misc.random(2);
					if (spec == 0) {
						npcs[i].attackType = 0; // melee
					} else if (spec == 1) {
						npcs[i].attackType = 2; // special
					} else if (spec == 2) {
						npcs[i].attackType = 1; // special
					}
				}
			}
			break;
		// kalphite queen form 1
		case 1158:
			for (int j = 0; j < PlayerHandler.players.length; j++) {
				if (PlayerHandler.players[j] != null) {
					int kq1;
					if (goodDistance(npcs[i].absX, npcs[i].absY,
							PlayerHandler.players[npcs[i].killerId].absX,
							PlayerHandler.players[npcs[i].killerId].absY, 1))
						kq1 = Misc.random(2);
					else
						kq1 = Misc.random(1);
					if (kq1 == 0) {
						npcs[i].attackType = 2; // mage
						npcs[i].gfx0(278);
						npcs[i].projectileId = 280;
						npcs[i].endGfx = 281;
					} else if (kq1 == 1) {
						npcs[i].attackType = 1; // range
						npcs[i].gfx0(-1);
						npcs[i].endGfx = -1;
						npcs[i].projectileId = 473;
					} else if (kq1 == 2) {
						npcs[i].attackType = 0; // melee
						npcs[i].projectileId = -1;
					}
				}
			}
			break;
		// kalphite queen form 2
		case 1160:
			for (int j = 0; j < PlayerHandler.players.length; j++) {
				if (PlayerHandler.players[j] != null) {
					int kq1;
					if (goodDistance(npcs[i].absX, npcs[i].absY,
							PlayerHandler.players[npcs[i].killerId].absX,
							PlayerHandler.players[npcs[i].killerId].absY, 1))
						kq1 = Misc.random(2);
					else
						kq1 = Misc.random(1);
					if (kq1 == 0) {
						npcs[i].attackType = 2; // mage
						npcs[i].gfx0(279);
						npcs[i].projectileId = 280;
						npcs[i].endGfx = 281;
					} else if (kq1 == 1) {
						npcs[i].attackType = 1; // range
						npcs[i].gfx0(-1);
						npcs[i].endGfx = -1;
						npcs[i].projectileId = 473;
					} else if (kq1 == 2) {
						npcs[i].attackType = 0; // melee
						npcs[i].projectileId = -1;
					}
				}
			}
			break;
		case 10127:
			int drain = Misc.random(10);
			if (drain == 0 && c.playerLevel[0] > 1) {
				c.gfx0(2253);
				c.playerLevel[0] = c.getLevelForXP(c.playerXP[0]) / 2;
				c.getPA().refreshSkill(0);
				c.sendMessage("The cursebearer drains half your attack.");
				npcs[i].attackType = 2;
			} else if (drain == 1 && c.playerLevel[1] > 1) {
				c.gfx0(2245);
				c.playerLevel[1] = c.getLevelForXP(c.playerXP[1]) / 2;
				c.getPA().refreshSkill(1);
				c.sendMessage("The cursebearer drains half your defense.");
				npcs[i].attackType = 2;
			} else if (drain == 2 && c.playerLevel[2] > 1) {
				c.gfx0(2249);
				c.playerLevel[2] = c.getLevelForXP(c.playerXP[2]) / 2;
				c.getPA().refreshSkill(2);
				c.sendMessage("The cursebearer drains half your strength.");
				npcs[i].attackType = 2;
			} else {
				npcs[i].attackType = 0;
			}
			break;
		case 9000: // nexnpc
			int nex = -1;
			if (npcs[i].HP <= 3000 && npcs[i].HP > 2400) {
				nex = 0;
			}
			if (npcs[i].HP <= 2400 && npcs[i].HP > 1800) {
				nex = 1;
			}
			if (npcs[i].HP <= 1800 && npcs[i].HP > 1200) {
				nex = 2;
			}
			if (npcs[i].HP <= 1200 && npcs[i].HP > 600) {
				nex = 3;
			}
			if (npcs[i].HP <= 600) {
				nex = 4;
			}
			if (nex == 0) { // smoke
				npcs[i].projectileId = 386;
				npcs[i].endGfx = 391;
				npcs[i].attackType = 2;
			} else if (nex == 1) { // shadow
				npcs[i].projectileId = 380;
				npcs[i].endGfx = 383;
				npcs[i].attackType = 1;
			} else if (nex == 2) { // blood
				npcs[i].projectileId = 372;
				npcs[i].endGfx = 375;
				npcs[i].attackType = 2;
			} else if (nex == 3) { // ice
				if (c.freezeTimer <= -3) {
					c.freezeTimer = 15;
					c.sendMessage("You have been frozen.");
					c.stopMovement();
				}
				npcs[i].endGfx = 369;
				npcs[i].projectileId = 368;
				npcs[i].attackType = 2;
			} else if (nex == 4) { // final
				int nexFinal = Misc.random(1);
				if (nexFinal == 0) {
					npcs[i].projectileId = -1;
					npcs[i].attackType = 0;
				} else {
					npcs[i].projectileId = 2183;
					npcs[i].endGfx = 2189;
					npcs[i].attackType = 2;
				}

			}
			break;
		case 27:
		case 690:
			npcs[i].gfx100(21);
			npcs[i].projectileId = 21;
			npcs[i].endGfx = 92;
			npcs[i].attackType = 1;
			break;
		case 2892: // spinolyp
			int r1 = Misc.random(1);
			if (r1 == 0) {
				npcs[i].attackType = 1;
				npcs[i].projectileId = 298;
			} else {
				npcs[i].attackType = 2;
				npcs[i].projectileId = 94;
				npcs[i].endGfx = 95;
			}
			break;

		case 9463: // ice strykewyrm
			for (int j = 0; j < PlayerHandler.players.length; j++) {
				if (PlayerHandler.players[j] != null) {
					int ice;
					if (goodDistance(npcs[i].absX, npcs[i].absY,
							PlayerHandler.players[npcs[i].killerId].absX,
							PlayerHandler.players[npcs[i].killerId].absY, 1))
						ice = Misc.random(1);
					else
						ice = 0;
					;
					if (ice == 0) {
						npcs[i].attackType = 2; // mage
						npcs[i].projectileId = 368;
					} else if (ice == 1) {
						npcs[i].attackType = 0; // melee
						npcs[i].projectileId = -1;
					}
				}
			}
			break;

		case 50: // dragons
		case 52:
		case 53:
		case 54:
		case 55:
		case 941:
		case 4677:
		case 1590:
		case 1591:
		case 1592:
		case 5362:
		case 5363:
		case 10773:
			int randomfire = Misc.random(3);
			if (randomfire == 0) {
				npcs[i].attackType = 3; // fire
				npcs[i].projectileId = 393;
				npcs[i].endGfx = 430;
			} else if (randomfire > 0) {
				npcs[i].attackType = 0; // melee
				npcs[i].projectileId = -1;
			}
			break;
		/*
		 * God wars npcs
		 */
		case 6203:
			int zammy = Misc.random(2);
			if (zammy == 0 || zammy == 1) {
				npcs[i].attackType = 0;
				npcs[i].endGfx = -1;
			} else {
				npcs[i].attackType = 2;
				npcs[i].projectileId = 1211;
			}
			break;
		case 6227:// Flight Kilisa
			npcs[i].attackType = 0;
			break;
		case 6223:// Wingman Skree
			npcs[i].attackType = 2;
			npcs[i].projectileId = 1199;
			break;
		case 6225:// Flockleader Geerin
		case 6230:
			npcs[i].attackType = 1;
			npcs[i].projectileId = 1190;
			break;
		case 6239:// Aviansie
			npcs[i].attackType = 1;
			npcs[i].projectileId = 1191;
			break;
		case 6232:// Aviansie
			npcs[i].attackType = 1;
			npcs[i].projectileId = 1191;
			break;
		case 6276:// Spiritual ranger
			npcs[i].attackType = 1;
			npcs[i].projectileId = 1195;
			break;
		case 6278:// Spiritual ranger
			npcs[i].attackType = 2;
			npcs[i].projectileId = 165;
			npcs[i].endGfx = 166;
			break;
		case 6257:// Spiritual mage
			npcs[i].attackType = 2;
			npcs[i].endGfx = 76;
			break;
		case 6221:// Spiritual mage
			npcs[i].attackType = 2;
			npcs[i].endGfx = 78;
			break;
		case 6231:// Spiritual mage
			npcs[i].attackType = 2;
			npcs[i].projectileId = 1199;
			break;
		case 6206:
			npcs[i].attackType = 1;
			npcs[i].projectileId = 1209;
			break;
		case 6208:
			npcs[i].attackType = 2;
			npcs[i].projectileId = 1213;
			npcs[i].gfx0(1212);
			break;
		case 6256:
			npcs[i].attackType = 1;
			npcs[i].projectileId = 16;
			break;
		case 6220:
			npcs[i].attackType = 1;
			npcs[i].projectileId = 17;
			break;
		case 6222:// Kree'arra
			int random = Misc.random(1);
			npcs[i].attackType = 1 + random;
			if (npcs[i].attackType == 1) {
				npcs[i].projectileId = 1197;
			} else {
				npcs[i].attackType = 2;
				npcs[i].projectileId = 1196;
			}
			break;
		case 6247: // Commander Zilyana
			random = Misc.random(1);
			if (random == 0) {
				npcs[i].attackType = 2;
				npcs[i].endGfx = 1194;
				npcs[i].projectileId = -1;
			} else if (random == 1)
				npcs[i].attackType = 0;
			break;
		case 6248: // Starlight
			npcs[i].attackType = 0;
			break;
		case 6250: // Growler
			npcs[i].attackType = 2;
			npcs[i].projectileId = 1203;
			break;
		case 6252: // Bree
			npcs[i].attackType = 1;
			npcs[i].projectileId = 9;
			break;
		// bandos npcs
		case 6260:// General Graardor
			random = Misc.random(2);
			if (random == 0 || random == 1) {
				npcs[i].attackType = 0;
				npcs[i].gfx0(-1);
			} else {
				npcs[i].attackType = 1;
				npcs[i].gfx0(1219);
			}
			break;
		case 6261:// strongstack
			npcs[i].attackType = 0;
			break;
		case 6263:// steelwill
			npcs[i].attackType = 2;
			npcs[i].gfx0(1202);
			npcs[i].projectileId = 1203;
			npcs[i].endGfx = 163;
			break;
		case 6265:// grimspike
			npcs[i].attackType = 1;
			npcs[i].projectileId = 1206;
			break;
		/*
		 * End godwars npcs
		 */
		case 2025:
			npcs[i].attackType = 2;
			int r = Misc.random(3);
			if (r == 0) {
				npcs[i].gfx100(158);
				npcs[i].projectileId = 159;
				npcs[i].endGfx = 160;
			}
			if (r == 1) {
				npcs[i].gfx100(161);
				npcs[i].projectileId = 162;
				npcs[i].endGfx = 163;
			}
			if (r == 2) {
				npcs[i].gfx100(164);
				npcs[i].projectileId = 165;
				npcs[i].endGfx = 166;
			}
			if (r == 3) {
				npcs[i].gfx100(155);
				npcs[i].projectileId = 156;
			}
			break;
		case 2881:// supreme
			npcs[i].attackType = 1;
			npcs[i].projectileId = 298;
			break;

		case 2882:// prime
			npcs[i].attackType = 2;
			npcs[i].projectileId = 162;
			npcs[i].endGfx = 477;
			break;

		case 2028:
			npcs[i].attackType = 1;
			npcs[i].projectileId = 27;
			break;

		case 3200:
			int r2 = Misc.random(5);
			if (r2 == 0) {
				npcs[i].attackType = 1;
				npcs[i].gfx100(550);
				npcs[i].projectileId = 551;
				npcs[i].endGfx = 552;
				c.getPA().chaosElementalEffect(c, 1);
			} else if (r2 == 1) {
				npcs[i].attackType = 2;
				npcs[i].gfx100(553);
				npcs[i].projectileId = 554;
				npcs[i].endGfx = 555;
				c.getPA().chaosElementalEffect(c, 0);
			} else {
				npcs[i].attackType = 0;
				npcs[i].gfx100(556);
				npcs[i].projectileId = 557;
				npcs[i].endGfx = 558;
			}
			break;

		case 2745:
			if (goodDistance(npcs[i].absX, npcs[i].absY,
					PlayerHandler.players[npcs[i].spawnedBy].absX,
					PlayerHandler.players[npcs[i].spawnedBy].absY, 1))
				r3 = Misc.random(2);
			else
				r3 = Misc.random(1);
			if (r3 == 0) {
				npcs[i].attackType = 2;
				npcs[i].endGfx = 157;
				npcs[i].projectileId = 1627;
			} else if (r3 == 1) {
				npcs[i].attackType = 1;
				npcs[i].endGfx = 1628;
				npcs[i].gfx100(1625);
				npcs[i].projectileId = -1;
			} else if (r3 == 2) {
				npcs[i].attackType = 0;
				npcs[i].projectileId = -1;
			}
			break;

		case 2631:
			npcs[i].attackType = 1;
			npcs[i].projectileId = 443;
			break;

		case 2743:
			if (goodDistance(npcs[i].absX, npcs[i].absY,
					PlayerHandler.players[npcs[i].spawnedBy].absX,
					PlayerHandler.players[npcs[i].spawnedBy].absY, 1))
				r3 = Misc.random(1);
			else
				r3 = 0;
			if (r3 == 0) {
				npcs[i].attackType = 2;
				npcs[i].projectileId = 445;
				npcs[i].endGfx = 446;
				break;
			} else if (r3 == 1) {
				npcs[i].attackType = 0;
				npcs[i].projectileId = -1;
			}
		}
	}

	/**
	 * Distanced required to attack
	 **/
	public int distanceRequired(int i) {
		switch (npcs[i].npcType) {
		case 2025:
		case 2028:
		case 27:
		case 690:
		case 190:
		case 1047:
		case 13:
		case 2881:// dag kings
		case 2882:
		case 3200:// chaos ele
		case 2743:
		case 2631:
		case 2562:
		case 1158:
		case 1160:
		case 50:
		case 52:
		case 53:
		case 54:
		case 55:
		case 941:
		case 4677:
		case 1590:
		case 1591:
		case 1592:
		case 5362:
		case 5363: // dragons
		case 10773:
		case 3064:
		case 8133:
		case 8349:
		case 8350:
		case 8351:
		case 9463:
			return 7;
		case 2883:// rex
			return 1;
		case 2745:
		case 6220:
		case 6221:
		case 6230:
		case 6231:
		case 6256:
		case 6257:
		case 6276:
		case 6278:
		case 6208:
			return 8;
		case 2892:
		case 2894:
			return 12;
		case 5229:
		case 5230:
		case 5231:
		case 5232:
		case 5233:
		case 5234:
		case 5235:
		case 5236:
		case 5237: // Penance rangers
			return 10;
		case 6222:
		case 6225:
		case 6250:
		case 6252:
		case 6263:
		case 6265:
			return 20;
		case 9000:
			if (npcs[i].attackType == 1 || npcs[i].attackType == 2)
				return 8;
			else
				return 1;
		default:
			return 1;
		}
	}

	public int followDistance(int i) {
		if (isFightCaveNpc(i))
			return 1000;
		switch (npcs[i].npcType) {
		case 6260:
		case 6261:
		case 6247:
		case 6248:
		case 6223:
		case 6225:
		case 6227:
		case 6203:
		case 6204:
		case 6206:
		case 6208:
		case 6250:
		case 6252:
		case 6263:
		case 6265:
		case 8133:
		case 9000:
			return 25;
		case 8596:
		case 9437:
		case 8597:
		case 3247:
		case 6270:
		case 6219:
		case 6255:
		case 6229:
		case 6277:
		case 6233:
		case 6232:
		case 6218:
		case 6269:
		case 3248:
		case 6212:
		case 6220:
		case 6276:
		case 6256:
		case 6230:
		case 6239:
		case 6221:
		case 6231:
		case 6257:
		case 6278:
		case 6272:
		case 6274:
		case 6254:
		case 4291: // Cyclops
		case 4292: // Ice cyclops
		case 6258:
		case 10141:
		case 7133:
			return 7;
		case 2883:
			return 4;
		case 2881:
		case 2882:
		case 10127:
			return 1;
		}
		return 0;

	}

	public int getProjectileSpeed(int i) {
		switch (npcs[i].npcType) {
		case 1158:
		case 1160:
		case 50:
			return 90;

		case 2881:
		case 2882:
		case 3200:
			return 85;

		case 6222:
		case 6225:
		case 6227:
		case 6247:
		case 6248:
		case 6250:
		case 6252:
		case 6260:
		case 6261:
		case 6263:
		case 6265:
		case 6203:
		case 6208:
		case 8349:
		case 8350:
		case 8351:
		case 8133:
		case 2025:
		case 9463:
		case 9000:
			return 105;

		case 2745:
			return 115;

		case 2028:
		case 27:
		case 690:
			return 80;

		default:
			return 85;
		}
	}

	/**
	 * NPC Attacking Player
	 **/

	public void attackPlayer(Client c, int i) {
		if (npcs[i] != null) {
			if (npcs[i].isDead)
				return;
			if (npcs[i].lastX != npcs[i].getX()
					|| npcs[i].lastY != npcs[i].getY()) {
				return;
			}
			if (!npcs[i].inMulti() && npcs[i].underAttackBy > 0
					&& npcs[i].underAttackBy != c.playerId) {
				npcs[i].killerId = 0;
				return;
			}
			if (!npcs[i].inMulti()
					&& (c.underAttackBy > 0 || (c.underAttackBy2 > 0 && c.underAttackBy2 != i))) {
				npcs[i].killerId = 0;
				return;
			}
			if (npcs[i].heightLevel != c.heightLevel) {
				npcs[i].killerId = 0;
				return;
			}
			npcs[i].facePlayer(c.playerId);
			boolean special = false;// specialCase(c,i);
			if (goodDistance(npcs[i].getX(), npcs[i].getY(), c.getX(),
					c.getY(), distanceRequired(i)) || special) {
				if (c.respawnTimer <= 0) {
					npcs[i].facePlayer(c.playerId);
					if (npcs[i].npcType != 8528) {
						npcs[i].attackTimer = getNpcDelay(i);
						npcs[i].hitDelayTimer = getHitDelay(i);
						npcs[i].attackType = 0;
					} else if (npcs[i].npcType == 8528) {
						if (npcs[i].attackType == 0)
							npcs[i].attackTimer = 3;
						npcs[i].hitDelayTimer = 1;
						if (npcs[i].attackType == 2)
							npcs[i].attackTimer = 5;
						npcs[i].hitDelayTimer = 2;
						if (npcs[i].attackType == 4)
							npcs[i].hitDelayTimer = 0;
					}
					if (special)
						loadSpell2(i);
					else
						loadSpell(c, i);
					if (npcs[i].attackType == 3)
						npcs[i].hitDelayTimer += 2;
					if (multiAttacks(i)) {
						multiAttackGfx(i, npcs[i].projectileId);
						startAnimation(getAttackEmote(i), i);
						npcs[i].oldIndex = c.playerId;
						return;
					}
					if (npcs[i].projectileId > 0) {
						int nX = NPCHandler.npcs[i].getX() + offset(i);
						int nY = NPCHandler.npcs[i].getY() + offset(i);
						int pX = c.getX();
						int pY = c.getY();
						int offX = (nY - pY) * -1;
						int offY = (nX - pX) * -1;
						c.getPA().createPlayersProjectile(nX, nY, offX, offY,
								50, getProjectileSpeed(i),
								npcs[i].projectileId, 43, 31, -c.getId() - 1,
								65);
					}
					healRevenants(i);
					c.underAttackBy2 = i;
					c.singleCombatDelay2 = System.currentTimeMillis();
					npcs[i].oldIndex = c.playerId;
					startAnimation(getAttackEmote(i), i);
					c.getPA().removeAllWindows();
				}
			}
		}
	}

	public boolean checkSlayerHelm(Client c, int i) {
		return c.slayerTask == npcs[i].npcType
				&& c.playerEquipment[Player.playerHat] == 13263;
	}

	public int offset(int i) {
		switch (npcs[i].npcType) {
		case 50:
		case 52:
		case 53:
		case 54:
		case 55:
		case 941:
		case 4677:
		case 1590:
		case 1591:
		case 1592:
		case 5362:
		case 5363: // dragons
		case 10773:
			return 2;
		case 2881:
		case 2882:
		case 2745:
		case 2743:
		case 8349:
		case 8350:
		case 8351:
		case 8133:
			// case 8528:
			// return 1;
		case 1158:
		case 1160:
			return 2;
		case 9000:
			return 1;
		}
		return 0;
	}

	public boolean specialCase(Client c, int i) { // responsible for npcs that
		// much
		if (goodDistance(npcs[i].getX(), npcs[i].getY(), c.getX(), c.getY(), 8)
				&& !goodDistance(npcs[i].getX(), npcs[i].getY(), c.getX(),
						c.getY(), distanceRequired(i)))
			return true;
		return false;
	}

	public boolean retaliates(int npcType) {
		return npcType < 6142 || npcType > 6145
				&& !(npcType >= 2440 && npcType <= 2446);
	}

	public void applyDamage(final int i) {
		if (npcs[i] != null) {
			if (PlayerHandler.players[npcs[i].oldIndex] == null) {
				return;
			}
			if (npcs[i].isDead)
				return;
			if (npcs[i].npcType == 10127 && npcs[i].attackType == 2)
				return;
			final Client c = (Client) PlayerHandler.players[npcs[i].oldIndex];
			if (multiAttacks(i)) {
				multiAttackDamage(i);
				return;
			}
			if (c.playerIndex <= 0 && c.npcIndex <= 0)
				if (c.autoRet == 1)
					c.npcIndex = i;

			if (c.attackTimer <= 3 || c.attackTimer == 0 && c.npcIndex == 0
					&& c.oldNpcIndex == 0) {
				c.startAnimation(c.getCombat().getBlockEmote());
			}
			if (c.respawnTimer <= 0) {
				int damage = 0;
				if (npcs[i].attackType == 0) {
					damage = Misc.random(npcs[i].maxHit);
					if (10 + Misc.random(c.getCombat().calculateMeleeDefence()) > Misc
							.random(NPCHandler.npcs[i].attack)) {
						damage = 0;
					}
					if (npcs[i].npcType == 5666) {
						if (damage > 0) {
							c.playerLevel[1] -= damage * 0.3;
							if (c.playerLevel[1] <= 1)
								c.playerLevel[1] = 1;
							c.getPA().refreshSkill(1);
							c.playerLevel[5] -= damage * 0.4;
							if (c.playerLevel[5] <= 0)
								c.playerLevel[5] = 0;
							c.getPA().refreshSkill(5);
						}
					}
					if (c.prayerActive[18] || c.curseActive[9]) { // protect
																	// from
																	// melee
						if (npcs[i].npcType == 936 || npcs[i].npcType == 937
								|| npcs[i].npcType == 1977
								|| npcs[i].npcType == 1913) {
							c.headIcon = -1;
							c.getPA().sendFrame36(c.PRAYER_GLOW[18], 0);
							c.getPA().sendFrame36(c.CURSE_GLOW[9], 0);
							c.prayerActive[18] = false;
							c.curseActive[9] = false;
							c.getPA().requestUpdates();
							damage = Misc.random(npcs[i].maxHit);
							npcs[i].forceChat("The gods cannot protect you from me!");
						} else if (npcs[i].npcType == 6688
								|| npcs[i].npcType == 6689
								|| npcs[i].npcType == 6691
								|| npcs[i].npcType == 6729
								|| npcs[i].npcType == 6730
								|| npcs[i].npcType == 2030) {
							damage = damage / 2;
						} else if (npcs[i].npcType == 6203) {
							if (Misc.random(5) == 1) {
								damage = Misc.random(49);
								c.playerLevel[5] -= damage / 4;
								if (c.playerLevel[5] <= 0)
									c.playerLevel[5] = 0;
								c.getPA().refreshSkill(5);
							}
						} else if (npcs[i].npcType == 8596
								|| npcs[i].npcType == 8597
								|| npcs[i].npcType == 9437
								|| npcs[i].npcType == 10127) {
							damage *= 0.8;
							c.playerLevel[5] -= damage / 2;
							if (c.playerLevel[5] <= 0)
								c.playerLevel[5] = 0;
							c.getPA().refreshSkill(5);
						} else if (npcs[i].npcType == 10542) {
							damage *= 0.6;
						} else if (npcs[i].npcType == 9780) {
							damage *= 0.5;
						} else {
							damage = 0;
						}
					}
					if (c.playerEquipment[Player.playerShield] == 13740) {
						if (c.playerLevel[5] > 0) {
							damage *= 0.7;
							c.playerLevel[5] -= damage * 0.3 / 2;
							c.getPA().refreshSkill(5);
						}
					}
					if (c.playerEquipment[Player.playerShield] == 13742) {
						if (Misc.random(4) == 3) {
							damage *= 0.75;
						}
					}
					if (c.SolProtect >= 1) { // protect from melee
						damage = damage / 2;
					}
					if (c.playerLevel[3] - damage < 0) {
						damage = c.playerLevel[3];
					}
				}

				if (npcs[i].attackType == 1) { // range
					damage = Misc.random(npcs[i].maxHit);
					if (10 + Misc.random(c.getCombat().calculateRangeDefence()) > Misc
							.random(NPCHandler.npcs[i].attack)) {
						damage = 0;
					}
					if (npcs[i].npcType == 5666) {
						if (damage > 0) {
							c.playerLevel[1] -= damage * 0.3;
							if (c.playerLevel[1] <= 1)
								c.playerLevel[1] = 1;
							c.getPA().refreshSkill(1);
							c.playerLevel[5] -= damage * 0.3;
							if (c.playerLevel[5] <= 0)
								c.playerLevel[5] = 0;
							c.getPA().refreshSkill(5);
						}
					}
					if (c.prayerActive[17] || c.curseActive[8]) { // protect
																	// from
																	// range
						damage = 0;
					}
					if (c.playerEquipment[Player.playerShield] == 13740) {
						if (c.playerLevel[5] > 0) {
							damage *= 0.7;
							c.playerLevel[5] -= damage * 0.3 / 2;
							c.getPA().refreshSkill(5);
						}
					}
					if (c.playerEquipment[Player.playerShield] == 13742) {
						if (Misc.random(4) == 3) {
							damage *= 0.75;
						}
					}
					if (c.playerLevel[3] - damage < 0) {
						damage = c.playerLevel[3];
					}
				}

				if (npcs[i].attackType == 2) { // magic
					boolean magicFailed = false;
					damage = Misc.random(npcs[i].maxHit);
					if (10 + Misc.random(c.getCombat().mageDef()) > Misc
							.random(NPCHandler.npcs[i].attack)) {
						damage = 0;
						magicFailed = true;
					}
					if (damage == 0 && !isRFDNpc2(i)) { 
                    c.gfx100(85);
                    }
					if (npcs[i].npcType == 5666) {
						if (damage > 0) {
							c.playerLevel[1] -= damage * 0.3;
							if (c.playerLevel[1] <= 1)
								c.playerLevel[1] = 1;
							c.getPA().refreshSkill(1);
							c.playerLevel[5] -= damage * 0.3;
							if (c.playerLevel[5] <= 0)
								c.playerLevel[5] = 0;
							c.getPA().refreshSkill(5);
						}
					}
					if (npcs[i].npcType == 9463) {
						damage = Misc.random(30);
						if (damage > 0) {
							if (c.freezeTimer <= -3) {
								c.freezeTimer = 10;
								c.gfx0(369);
								c.sendMessage("You have been frozen.");
								c.stopMovement();
							}
						}
					}
					if (npcs[i].npcType == 8528) {
						damage = Misc.random(75);
					}
					if (c.prayerActive[16] || c.curseActive[7]) { // protect
																	// from
																	// magic
						damage = 0;
						magicFailed = true;
					}
					if (c.playerEquipment[Player.playerShield] == 13740) {
						if (c.playerLevel[5] > 0) {
							damage *= 0.7;
							c.playerLevel[5] -= damage * 0.3 / 2;
							c.getPA().refreshSkill(5);
						}
					}
					if (c.playerEquipment[Player.playerShield] == 13742) {
						if (Misc.random(4) == 3) {
							damage *= 0.75;
						}
					}
					if (c.playerLevel[3] - damage < 0) {
						damage = c.playerLevel[3];
					}
					if (npcs[i].endGfx > 0
							&& (!magicFailed || isFightCaveNpc(i))) {
						if (npcs[i].endGfx == 369) {
							c.gfx0(npcs[i].endGfx);
						} else {
							c.gfx100(npcs[i].endGfx);
						}
					} else if (npcs[i].npcType != 3067
							&& npcs[i].npcType != 5666
							&& npcs[i].npcType != 9463
							&& npcs[i].npcType != 8528
							&& npcs[i].npcType != 2745
							&& npcs[i].npcType != 8133
							&& npcs[i].npcType != 9000
							&& npcs[i].npcType != 6222
							&& npcs[i].npcType != 6203
							&& npcs[i].npcType != 6247) {
						CycleEventHandler.getSingleton().addEvent(c,
								new CycleEvent() {
									@Override
									public void execute(
											CycleEventContainer container) {
										if (c != null) {
											c.gfx100(85);
											container.stop();
										}
									}

									@Override
									public void stop() {

									}
								}, 2);
					}
				}

				if (npcs[i].attackType == 3) { // fire breath
					int anti = c.getPA().antiFire();
					if (anti == 0) {
						damage = Misc.random(40) + 10;
						c.sendMessage("You are badly burnt by the dragon fire!");
					} else if (anti == 1)
						damage = Misc.random(10);
					else if (anti == 2)
						damage = Misc.random(5);
					if (c.playerLevel[3] - damage < 0)
						damage = c.playerLevel[3];
					c.gfx100(npcs[i].endGfx);
				}
				handleSpecialEffects(c, i, damage);
				FightCaves.tzKihEffect(c, i, damage);
				c.logoutDelay = System.currentTimeMillis(); // logout delay
				// c.setHitDiff(damage);
				c.handleHitMask(damage);
				c.playerLevel[3] -= damage;
				if (c.vengOn) {
					appendNPCVeng(c, i, damage);
				}
				c.getPA().refreshSkill(3);
				c.updateRequired = true;
				// c.setHitUpdateRequired(true);
				if (npcs[i].attackType == 4) { // nomad soul blast
					damage = (c.getLevelForXP(c.playerXP[3]) - 1);
				}
			}
		}
	}

	public void handleSpecialEffects(Client c, int i, int damage) {
		if (npcs[i].npcType == 2892 || npcs[i].npcType == 2894
				|| npcs[i].npcType == 1158 || npcs[i].npcType == 1160
				|| npcs[i].npcType == 3059) {
			if (damage > 0) {
				if (c != null) {
					if (c.playerLevel[5] > 0) {
						c.playerLevel[5]--;
						c.getPA().refreshSkill(5);
						c.getPA().appendPoison(12);
					}
				}
			}
		}
		if ((npcs[i].npcType == 50) || (npcs[i].npcType == 53)
				|| (npcs[i].npcType == 54) || (npcs[i].npcType == 55)
				|| (npcs[i].npcType == 941) || (npcs[i].npcType == 1590)
				|| (npcs[i].npcType == 1591) || (npcs[i].npcType == 1592)
				|| (npcs[i].npcType == 5362) || (npcs[i].npcType == 5363)
				|| (npcs[i].npcType == 4677) || (npcs[i].npcType == 10773)) {
			if (npcs[i].attackType == 3) {
				if (c.playerEquipment[Player.playerShield] == 11283
						|| c.playerEquipment[Player.playerShield] == 11284) {
					if (c.dfsCount < 50) {
						c.sendMessage("Your shield absorbs most of the dragon's fiery breath.");
						c.getCombat().addCharge();
					} else if (c.dfsCount > 50) {
						c.sendMessage("Your dragonfire shield is already fully charged.");
						c.dfsCount = 50;
					}
				}
			}
		}
	}

	public static void startAnimation(int animId, int i) {
		npcs[i].animNumber = animId;
		npcs[i].animUpdateRequired = true;
		npcs[i].updateRequired = true;
	}

	public boolean goodDistance(int objectX, int objectY, int playerX,
			int playerY, int distance) {
		return ((objectX - playerX <= distance && objectX - playerX >= -distance) && (objectY
				- playerY <= distance && objectY - playerY >= -distance));
	}

	public int getMaxHit(int i) {
		switch (npcs[i].npcType) {
		case 2881:
			return 30;
		case 2882:
			return 61;
		case 2883:
			return 28;
		case 3200:
			return 28;
		case 50:
			return 25;
		case 1158:
		case 1160:
			return 31;
		case 2892:
			return 10;
		case 6247:
			return 31;
		case 6260:
			if (npcs[i].attackType == 1)
				return 35;
			else
				return 61;
		case 6222:
			if (npcs[i].attackType == 1)
				return 71;
			else
				return 21;
		case 6203:
			if (npcs[i].attackType == 2)
				return 30;
			else
				return 46;
		case 8133:
			if (npcs[i].attackType == 2)
				return 69;
			if (npcs[i].attackType == 1)
				return 58;
			if (npcs[i].attackType == 0)
				return 51;
		case 9000:
			if (npcs[i].attackType == 2)
				return 90;
			if (npcs[i].attackType == 1)
				return 70;
		}
		return 1;
	}

	public boolean loadAutoSpawn(String FileName) {
		String line = "";
		String token = "";
		String token2 = "";
		String token2_2 = "";
		String[] token3 = new String[10];
		boolean EndOfFile = false;
		int ReadMode = 0;
		BufferedReader characterfile = null;
		try {
			characterfile = new BufferedReader(new FileReader("./" + FileName));
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
				if (token.equals("spawn")) {
					newNPC(Integer.parseInt(token3[0]),
							Integer.parseInt(token3[1]),
							Integer.parseInt(token3[2]),
							Integer.parseInt(token3[3]),
							Integer.parseInt(token3[4]),
							getNpcListHP(Integer.parseInt(token3[0])),
							Integer.parseInt(token3[5]),
							Integer.parseInt(token3[6]),
							Integer.parseInt(token3[7]));

				}
			} else {
				if (line.equals("[ENDOFSPAWNLIST]")) {
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

	public int getNpcListHP(int npcId) {
		for (int i = 0; i < maxListedNPCs; i++) {
			if (NpcList[i] != null) {
				if (NpcList[i].npcId == npcId) {
					return NpcList[i].npcHealth;
				}
			}
		}
		return 0;
	}

	public String getNpcListName(int npcId) {
		for (int i = 0; i < maxListedNPCs; i++) {
			if (NpcList[i] != null) {
				if (NpcList[i].npcId == npcId) {
					return NpcList[i].npcName.replaceAll("_", " ");
				}
			}
		}
		return "nothing";
	}

	public boolean loadNPCList(String FileName) {
		String line = "";
		String token = "";
		String token2 = "";
		String token2_2 = "";
		String[] token3 = new String[10];
		boolean EndOfFile = false;
		int ReadMode = 0;
		BufferedReader characterfile = null;
		try {
			characterfile = new BufferedReader(new FileReader("./" + FileName));
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
				if (token.equals("npc")) {
					newNPCList(Integer.parseInt(token3[0]), token3[1],
							Integer.parseInt(token3[2]),
							Integer.parseInt(token3[3]));
				}
			} else {
				if (line.equals("[ENDOFNPCLIST]")) {
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

	/**
	 * @param c
	 *            , the Client , the player that got hit.
	 * @param i
	 *            , the NPC that hit the player and will receive the rebound
	 *            damage.
	 * @param damage
	 *            , the damage the NPC has dealt to the player.
	 */
	public void appendNPCVeng(Client c, int i, int damage) {
		if (damage <= 0)
			return;
		c.forcedText = "Taste Vengeance!";
		c.forcedChatUpdateRequired = true;
		c.updateRequired = true;
		c.vengOn = false;
		if ((c.playerLevel[3] - damage) > 0) {
			int rebound = (int) (damage * 0.75);
			if (rebound > npcs[i].HP) {
				rebound = npcs[i].HP;
			}
			npcs[i].HP -= rebound;
			npcs[i].hitDiff2 = rebound;
			npcs[i].hitUpdateRequired2 = true;
		}
		npcs[i].updateRequired = true;
	}

}
