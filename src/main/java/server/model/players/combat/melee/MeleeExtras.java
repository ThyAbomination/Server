package server.model.players.combat.melee;

import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.npcs.NPCHandler;
import server.model.players.Client;
import server.model.players.Player;
import server.model.players.PlayerHandler;
import server.util.Misc;

public class MeleeExtras {

	public static void applySmite(Client c, int index, int damage) {
		if (!c.prayerActive[23] && !c.curseActive[18])
			return;
		if (damage <= 0)
			return;
		if (PlayerHandler.players[index] != null) {
			Client c2 = (Client) PlayerHandler.players[index];
			if (c.curseActive[18] && !c.prayerActive[23]
					&& c.playerLevel[3] <= 99) {
				int heal = damage * 20 / 100;
				if (c.playerLevel[3] + heal >= c.getPA().getLevelForXP(
						c.playerXP[3])) {
					c.playerLevel[3] = c.getPA().getLevelForXP(c.playerXP[3]);
				} else {
					c.playerLevel[3] += heal;
				}
				c.getPA().refreshSkill(3);
			}
			if (c.playerName.equalsIgnoreCase("Ultima")) {
				c2.playerLevel[5] -= (damage / 2);
			} else {
				c2.playerLevel[5] -= (damage / 4);
			}
			if (c2.playerLevel[5] <= 0) {
				c2.playerLevel[5] = 0;
				c2.getCombat().resetPrayers();
			}
			c2.getPA().refreshSkill(5);
		}
	}

	public static void applySoulSplit(Client c, int index, int damage) {
		if (!c.curseActive[18])
			return;
		if (c.oldNpcIndex > 0) {
			if (NPCHandler.npcs[c.oldNpcIndex] != null) {
				if (damage == 0) {
					return;
				}
				if (c.curseActive[18] && !c.prayerActive[23]
						&& (c.playerLevel[3] <= 99)) {
					int heal = damage * 20 / 100;
					if (c.playerLevel[3] + heal >= c.getPA().getLevelForXP(
							c.playerXP[3])) {
						c.playerLevel[3] = c.getPA().getLevelForXP(
								c.playerXP[3]);
					} else {
						c.playerLevel[3] += heal;
					}
					c.getPA().refreshSkill(3);
				}
			}
		}
	}

	public static void handleDragonFireShield(final Client c) {
		if (PlayerHandler.players[c.playerIndex] != null) {
			if (PlayerHandler.players[c.playerIndex].playerLevel[3] <= 0) {
				return;
			}
			if (c.playerIndex > 0 && c.dfsCount > 0
					&& PlayerHandler.players[c.playerIndex] != null) {
				if (c.duelStatus == 5) {
					return;
				}
				if (c.dfsCount == 0) {
					c.sendMessage("Your shield has no charges to fire.");
					return;
				}
				if (System.currentTimeMillis() - c.dfsDelay > 120000) {
					final int pX = c.getX();
					final int pY = c.getY();
					final int oX = PlayerHandler.players[c.playerIndex].getX();
					final int oY = PlayerHandler.players[c.playerIndex].getY();
					final int offX = (pY - oY) * -1;
					final int offY = (pX - oX) * -1;
					final int damage = Misc.random(25) + 5;
					c.dfsDelay = System.currentTimeMillis();
					c.dfsCount -= 1;
					c.getItems().resetBonus();
					c.getItems().getBonus();
					c.getItems().writeBonus();
					c.startAnimation(6696);
					c.gfx0(1165);
					c.attackTimer += 4;
					CycleEventHandler.getSingleton().addEvent(c,
							new CycleEvent() {
								@Override
								public void execute(
										CycleEventContainer container) {
									if (PlayerHandler.players[c.playerIndex] != null) {
										c.getPA().createPlayersProjectile(pX,
												pY, offX, offY, 50, 10, 1166,
												25, 27, c.playerIndex - 1, 0);
										container.stop();
									}
								}

								@Override
								public void stop() {

								}
							}, 4);
					CycleEventHandler.getSingleton().addEvent(c,
							new CycleEvent() {
								@Override
								public void execute(
										CycleEventContainer container) {
									if (PlayerHandler.players[c.playerIndex] != null) {
										PlayerHandler.players[c.playerIndex]
												.gfx100(1167);
										PlayerHandler.players[c.playerIndex].playerLevel[3] -= damage;
										if (!PlayerHandler.players[c.playerIndex]
												.getHitUpdateRequired()) {
											PlayerHandler.players[c.playerIndex]
													.setHitDiff(damage);
											PlayerHandler.players[c.playerIndex]
													.setHitUpdateRequired(true);
										} else if (!PlayerHandler.players[c.playerIndex]
												.getHitUpdateRequired2()) {
											PlayerHandler.players[c.playerIndex]
													.setHitDiff2(damage);
											PlayerHandler.players[c.playerIndex]
													.setHitUpdateRequired2(true);
										}
										PlayerHandler.players[c.playerIndex].updateRequired = true;
										container.stop();
									}
								}

								@Override
								public void stop() {

								}
							}, 3);
				} else {
					c.sendMessage("You must wait 2 minutes before using another charge.");
				}
			}
		}
	}

	public static void handleDragonFireShieldNPC(final Client c) {
		if (NPCHandler.npcs[c.npcIndex] != null) {
			if (NPCHandler.npcs[c.npcIndex].HP <= 0) {
				return;
			}
			if (c.npcIndex > 0 && c.dfsCount > 0
					&& NPCHandler.npcs[c.npcIndex] != null) {
				if (c.dfsCount == 0) {
					c.sendMessage("Your shield has no charges to fire.");
					return;
				}
				if (System.currentTimeMillis() - c.dfsDelay > 120000) {
					final int pX = c.getX();
					final int pY = c.getY();
					final int nX = NPCHandler.npcs[c.npcIndex].getX();
					final int nY = NPCHandler.npcs[c.npcIndex].getY();
					final int offX = (pY - nY) * -1;
					final int offY = (pX - nX) * -1;
					final int damage = Misc.random(25) + 5;
					c.dfsDelay = System.currentTimeMillis();
					c.dfsCount -= 1;
					c.getItems().resetBonus();
					c.getItems().getBonus();
					c.getItems().writeBonus();
					c.startAnimation(6696);
					c.gfx0(1165);
					c.attackTimer += 4;
					CycleEventHandler.getSingleton().addEvent(c,
							new CycleEvent() {
								@Override
								public void execute(
										CycleEventContainer container) {
									if (NPCHandler.npcs[c.npcIndex] != null) {
										c.getPA().createPlayersProjectile(pX,
												pY, offX, offY, 50, 10, 1166,
												25, 27, c.npcIndex + 1, 0);
										container.stop();
									}
								}

								@Override
								public void stop() {

								}
							}, 4);
					CycleEventHandler.getSingleton().addEvent(c,
							new CycleEvent() {
								@Override
								public void execute(
										CycleEventContainer container) {
									if (NPCHandler.npcs[c.npcIndex] != null) {
										NPCHandler.npcs[c.npcIndex]
												.gfx100(1167);
										NPCHandler.npcs[c.npcIndex]
												.handleHitMask(damage);
										NPCHandler.npcs[c.npcIndex].HP -= damage;
										container.stop();
									}
								}

								@Override
								public void stop() {

								}
							}, 3);
				} else {
					c.sendMessage("You must wait 2 minutes before using another charge.");
				}
			}
		}
	}

	public static void addCharge(Client c) {
		if (c.playerEquipment[Player.playerShield] != 11283
				&& c.playerEquipment[Player.playerShield] != 11284) {
			return;
		}
		c.gfx0(1160);
		c.dfsCount++;
		c.getItems().resetBonus();
		c.getItems().getBonus();
		c.getItems().writeBonus();
		if (c.dfsCount == 50) {
			c.sendMessage("Your dragonfire shield is now fully charged.");
		}
	}

	public static void appendVengeanceNPC(Client c, int otherPlayer, int damage) {
		if (damage <= 0)
			return;
		if (c.npcIndex > 0 && NPCHandler.npcs[c.npcIndex] != null) {
			c.forcedText = "Taste vengeance!";
			c.forcedChatUpdateRequired = true;
			c.updateRequired = true;
			c.vengOn = false;
			if ((NPCHandler.npcs[c.npcIndex].MaxHP - damage) > 0) {
				damage = (int) (damage * 0.75);
				if (damage > NPCHandler.npcs[c.npcIndex].MaxHP) {
					damage = NPCHandler.npcs[c.npcIndex].MaxHP;
				}
				NPCHandler.npcs[c.npcIndex].MaxHP -= damage;
				NPCHandler.npcs[c.npcIndex].handleHitMask(damage);
			}
		}
		c.updateRequired = true;
	}

	public static void appendVengeance(Client c, int otherPlayer, int damage) {
		if (damage <= 0)
			return;
		Player o = PlayerHandler.players[otherPlayer];
		o.forcedText = "Taste vengeance!";
		o.forcedChatUpdateRequired = true;
		o.updateRequired = true;
		o.vengOn = false;
		if ((o.playerLevel[3] - damage) > 0) {
			damage = (int) (damage * 0.75);
			if (damage > c.playerLevel[3]) {
				damage = c.playerLevel[3];
			}
			if (!c.getHitUpdateRequired()) {
				c.setHitDiff(damage);
				c.setHitUpdateRequired(true);
			} else if (!c.getHitUpdateRequired2()) {
				c.setHitDiff2(damage);
				c.setHitUpdateRequired2(true);
			}
			c.playerLevel[3] -= damage;
			c.getPA().refreshSkill(3);
		}
		c.updateRequired = true;
	}

	public static void applyRecoilNPC(Client c, int damage, int i) {
		if (damage > 0 && c.playerEquipment[c.playerRing] == 2550) {
			int recDamage = damage / 10 + 1;
			NPCHandler.npcs[c.npcIndex].HP -= recDamage;
			NPCHandler.npcs[c.npcIndex].handleHitMask(recDamage);
			c.recoilHits += damage;
		}
	}

	public static void applyRecoil(Client c, int damage, int i) {
		if (damage > 0
				&& PlayerHandler.players[i].playerEquipment[c.playerRing] == 2550) {
			int recDamage = damage / 10 + 1;
			if (!c.getHitUpdateRequired()) {
				c.setHitDiff(recDamage);
				c.setHitUpdateRequired(true);
			} else if (!c.getHitUpdateRequired2()) {
				c.setHitDiff2(recDamage);
				c.setHitUpdateRequired2(true);
			}
			c.dealDamage(recDamage);
			c.updateRequired = true;
		}
	}

	public static void handCannon(final Client c) {
		CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				c.getItems().deleteArrow();
				c.hitDelay = 12;
				c.specGfx = true;
				c.startAnimation(12174);
				c.gfx0(2141);
				c.projectileStage = 1;
				c.hitDelay = c.getCombat().getHitDelay(
						0,
						c.getItems()
								.getItemName(
										c.playerEquipment[Player.playerWeapon])
								.toLowerCase());
				c.attackTimer -= 2;
				if (c.playerIndex > 0)
					c.getCombat().fireProjectilePlayer();
				else if (c.npcIndex > 0)
					c.getCombat().fireProjectileNpc();
				c.specAccuracy = 50.0;
				container.stop();
			}

			@Override
			public void stop() {

			}
		}, 4);
	}

	public static void handleGmaul(Client c) {
		if (c.npcIndex > 0 && NPCHandler.npcs[c.npcIndex] != null) {
			if (c.goodDistance(c.getX(), c.getY(), NPCHandler.npcs[c.npcIndex]
					.getX(), NPCHandler.npcs[c.npcIndex].getY(), c.getCombat()
					.getRequiredDistance())) {
				if (c.getCombat().checkSpecAmount(4153)) {
					boolean hit = Misc.random(c.getCombat()
							.calculateMeleeAttack()) > Misc
							.random(NPCHandler.npcs[c.npcIndex].defence);
					int damage = 0;
					if (hit) {
						damage = Misc.random(c.getCombat()
								.calculateMeleeMaxHit());
						NPCHandler.npcs[c.npcIndex].HP -= damage;
						NPCHandler.npcs[c.npcIndex].handleHitMask(damage);
						NPCHandler.npcs[c.npcIndex].updateRequired = true;
						c.startAnimation(1667);
						c.gfx100(340);
					}
				}
			}
		} else if (c.playerIndex > 0) {
			final Client o = (Client) PlayerHandler.players[c.playerIndex];
			if (c.goodDistance(c.getX(), c.getY(), o.getX(), o.getY(), c
					.getCombat().getRequiredDistance())) {
				if (c.getCombat().checkReqs()) {
					if (c.getCombat().checkSpecAmount(4153)) {
						final boolean hit = Misc.random(c.getCombat()
								.calculateMeleeAttack()) > Misc.random(o
								.getCombat().calculateMeleeDefence());
						int damage = 0;
						if (hit)
							damage = Misc.random(c.getCombat()
									.calculateMeleeMaxHit());
						if (o.prayerActive[18]
								&& System.currentTimeMillis()
										- o.protMeleeDelay > 1500)
							damage *= .6;
						if (o.playerLevel[3] - damage <= 0) {
							damage = o.playerLevel[3];
						}
						if (o.playerLevel[3] > 0) {
							o.handleHitMask(damage);
							c.startAnimation(1667);
							o.gfx100(340);
							o.dealDamage(damage);
						}
					}
				}
			}
		}
	}
}