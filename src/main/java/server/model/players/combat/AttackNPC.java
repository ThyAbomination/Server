package server.model.players.combat;

import server.Config;
import server.Server;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.minigames.PestControl;
import server.model.npcs.NPCHandler;
import server.model.players.Client;
import server.model.players.Player;
import server.model.players.PlayerHandler;
import server.model.players.combat.range.RangeData;
import server.util.Misc;

public class AttackNPC {

	public static void addNPCHit(final int i, final Client c) {
		if (c.projectileStage == 0 && !c.usingMagic) {
			c.stopPlayerSkill = false;
			if (!c.usingClaws) {
				c.getCombat().applyNpcMeleeDamage(i, 1,
						Misc.random(c.getCombat().calculateMeleeMaxHit()));
				if (c.doubleHit) {
					if (!c.oldSpec) {
						c.getCombat().applyNpcMeleeDamage(
								i,
								2,
								Misc.random(c.getCombat()
										.calculateMeleeMaxHit()));
					} else {
						CycleEventHandler.getSingleton().addEvent(c,
								new CycleEvent() {
									@Override
									public void execute(
											CycleEventContainer container) {
										c.getCombat()
												.applyNpcMeleeDamage(
														i,
														2,
														Misc.random(c
																.getCombat()
																.calculateMeleeMaxHit()));
										container.stop();
									}

									@Override
									public void stop() {

									}
								}, 1);
					}
				}
			} else {
				c.getPA().hitDragonClaws(c,
						Misc.random(c.getCombat().calculateMeleeMaxHit()));
			}
		}
	}

	public static boolean kalphite1(int i) {
		switch (NPCHandler.npcs[i].npcType) {
		case 1158:
			return true;
		}
		return false;
	}

	public static boolean kalphite2(int i) {
		switch (NPCHandler.npcs[i].npcType) {
		case 1160:
			return true;
		}
		return false;
	}

	public static void applyNpcMeleeDamage(Client c, int i, int damageMask,
			int damage) {
		if (NPCHandler.npcs[i] != null) {
			if (!c.usingClaws) {
				damage = Misc.random(c.getCombat().calculateMeleeMaxHit());
			}
			c.previousDamage = damage;

			boolean fullVeracsEffect = c.getPA().fullVeracs()
					&& Misc.random(3) == 1;
			if (NPCHandler.npcs[i].HP - damage < 0
					&& NPCHandler.npcs[i] != null) {
				damage = NPCHandler.npcs[i].HP;
			}

			if (!fullVeracsEffect) {
				if (Misc.random(NPCHandler.npcs[i].defence) > 10 + Misc
						.random(c.getCombat().calculateMeleeAttack())) {
					damage = 0;
				} else if (NPCHandler.npcs[i].npcType == 2882
						|| NPCHandler.npcs[i].npcType == 2883 || kalphite2(i)
						|| NPCHandler.npcs[i].npcType == 8349) {
					damage = 0;
				}
			}

			boolean guthansEffect = false;
			if (c.getPA().fullGuthans()) {
				if (Misc.random(3) == 1) {
					guthansEffect = true;
				}
			}

			if (Server.npcHandler.getNPCs()[i].attackTimer <= 3)
				NPCHandler.startAnimation(c.getCombat().npcDefenceAnim(i), i);

			if (NPCHandler.npcs[i].HP - damage < 0) {
				damage = NPCHandler.npcs[i].HP;
			}
			if (c.fightMode == 3) {
				c.getPA().addSkillXP((damage * Config.MELEE_EXP_RATE / 3), 0);
				c.getPA().addSkillXP((damage * Config.MELEE_EXP_RATE / 3), 1);
				c.getPA().addSkillXP((damage * Config.MELEE_EXP_RATE / 3), 2);
				c.getPA().addSkillXP((damage * Config.MELEE_EXP_RATE / 3), 3);
				c.getPA().refreshSkill(0);
				c.getPA().refreshSkill(1);
				c.getPA().refreshSkill(2);
				c.getPA().refreshSkill(3);
			} else {
				c.getPA().addSkillXP((damage * Config.MELEE_EXP_RATE),
						c.fightMode);
				c.getPA().addSkillXP((damage * Config.MELEE_EXP_RATE / 3), 3);
				c.getPA().refreshSkill(c.fightMode);
				c.getPA().refreshSkill(3);
			}
			if (damage > 0 && PestControl.isInGame(c)) {
				if (NPCHandler.npcs[i].npcType == 6142) {
					c.pcDamage += damage;
					if (damage < PestControl.portalHealth[0])
						PestControl.portalHealth[0] -= damage;
					else
						PestControl.portalHealth[0] = 0;
				}
				if (NPCHandler.npcs[i].npcType == 6143) {
					c.pcDamage += damage;
					if (damage < PestControl.portalHealth[1])
						PestControl.portalHealth[1] -= damage;
					else
						PestControl.portalHealth[1] = 0;
				}
				if (NPCHandler.npcs[i].npcType == 6144) {
					c.pcDamage += damage;
					if (damage < PestControl.portalHealth[2])
						PestControl.portalHealth[2] -= damage;
					else
						PestControl.portalHealth[2] = 0;
				}
				if (NPCHandler.npcs[i].npcType == 6145) {
					c.pcDamage += damage;
					if (damage < PestControl.portalHealth[3])
						PestControl.portalHealth[3] -= damage;
					else
						PestControl.portalHealth[3] = 0;
				}
				if (PestControl.npcIsPCMonster(NPCHandler.npcs[i].npcType)) {
					PestControl.KNIGHTS_HEALTH += damage / 4;
				}
			}
			if (damage > 0 && guthansEffect) {
				c.playerLevel[3] += damage;
				if (c.playerLevel[3] > c.getLevelForXP(c.playerXP[3]))
					c.playerLevel[3] = c.getLevelForXP(c.playerXP[3]);
				c.getPA().refreshSkill(3);
				NPCHandler.npcs[i].gfx0(398);
			}

			c.getCombat().applySoulSplit(i, damage);
			if (c.inBarbDef())
				c.barbDamage += damage;
			NPCHandler.npcs[i].underAttack = true;
			c.killingNpcIndex = c.npcIndex;
			c.lastNpcAttacked = i;

			switch (c.specEffect) {
			case 4:
				if (damage > 0) {
					int heal = (int) (damage * 0.50);
					int prayer = (int) (damage * 0.25);
					if (c.playerLevel[3] + heal > c
							.getLevelForXP(c.playerXP[3])) {
						c.playerLevel[3] = c.getLevelForXP(c.playerXP[3]);
					} else {
						c.playerLevel[3] += heal;
					}
					if (c.playerLevel[5] + prayer > c
							.getLevelForXP(c.playerXP[5])) {
						c.playerLevel[5] = c.getLevelForXP(c.playerXP[5]);
					} else {
						c.playerLevel[5] += prayer;
					}
					c.getPA().refreshSkill(5);
					c.getPA().refreshSkill(3);
				}
				break;
			case 6:
				NPCHandler.npcs[i].gfx100(1194);
				break;
			case 8:
				NPCHandler.npcs[i].gfx100(1248);
				break;
			}
			c.specEffect = 0;

			switch (damageMask) {
			case 1:
				NPCHandler.npcs[i].hitDiff = damage;
				NPCHandler.npcs[i].HP -= damage;
				c.totalDamageDealt += damage;
				NPCHandler.npcs[i].hitUpdateRequired = true;
				NPCHandler.npcs[i].updateRequired = true;
				break;

			case 2:
				NPCHandler.npcs[i].hitDiff2 = damage;
				NPCHandler.npcs[i].HP -= damage;
				c.totalDamageDealt += damage;
				NPCHandler.npcs[i].hitUpdateRequired2 = true;
				NPCHandler.npcs[i].updateRequired = true;
				c.doubleHit = false;
				break;

			}
		}
	}

	public static void delayedHit(final Client c, final int i) {
		if (NPCHandler.npcs[i] != null) {
			if (NPCHandler.npcs[i].isDead) {
				c.npcIndex = 0;
				return;
			}

			NPCHandler.npcs[i].facePlayer(c.playerId);

			if (NPCHandler.npcs[i].underAttackBy > 0
					&& Server.npcHandler.getsPulled(i)) {
				NPCHandler.npcs[i].killerId = c.playerId;
			} else if (NPCHandler.npcs[i].underAttackBy < 0
					&& !Server.npcHandler.getsPulled(i)) {
				NPCHandler.npcs[i].killerId = c.playerId;
			}

			c.lastNpcAttacked = i;

			c.getCombat().addNPCHit(i, c);
			// c.getCombat().addCharge();

			if (!c.castingMagic && c.projectileStage > 0) { // range hit damage
				int damage = Misc.random(c.getCombat().rangeMaxHit());
				int damage2 = -1;

				if (c.lastWeaponUsed == 11235 || c.lastWeaponUsed == 15701
						|| c.lastWeaponUsed == 15702
						|| c.lastWeaponUsed == 15703
						|| c.lastWeaponUsed == 15704 || c.bowSpecShot == 1)
					damage2 = Misc.random(c.getCombat().rangeMaxHit());
				if (c.playerEquipment[3] == 9185
						|| c.playerEquipment[3] == 18357) {
					if (Misc.random(10) == 1) {
						if (damage > 0) {
							c.boltDamage = damage;
							c.getCombat().crossbowSpecial(c, i);
							damage *= c.crossbowDamage;
						}
					}
				}
				if (Misc.random(NPCHandler.npcs[i].defence) > Misc
						.random(10 + c.getCombat().calculateRangeAttack())
						&& !c.ignoreDefence) {
					damage = 0;
				} else if (NPCHandler.npcs[i].npcType == 2881
						|| NPCHandler.npcs[i].npcType == 2883 || kalphite1(i)
						|| NPCHandler.npcs[i].npcType == 8351
						&& !c.ignoreDefence) {
					damage = 0;
				}
				if (c.lastWeaponUsed == 11235 || c.lastWeaponUsed == 15701
						|| c.lastWeaponUsed == 15702
						|| c.lastWeaponUsed == 15703
						|| c.lastWeaponUsed == 15704 || c.bowSpecShot == 1) {
					if (Misc.random(NPCHandler.npcs[i].defence) > Misc
							.random(10 + c.getCombat().calculateRangeAttack()))
						damage2 = 0;
				}
				if (c.dbowSpec) {
					NPCHandler.npcs[i].gfx100(c.lastArrowUsed == 11212 ? 1100
							: 1103);
					if (damage < 8)
						damage = 8;
					if (damage2 < 8)
						damage2 = 8;
					c.dbowSpec = false;
				}

				if (NPCHandler.npcs[i].HP - damage < 0) {
					damage = NPCHandler.npcs[i].HP;
				}
				if (NPCHandler.npcs[i].HP - damage <= 0 && damage2 > 0) {
					damage2 = 0;
				}

				if (c.fightMode == 3) {
					c.getPA().addSkillXP((damage * Config.RANGE_EXP_RATE / 3),
							4);
					c.getPA().addSkillXP((damage * Config.RANGE_EXP_RATE / 3),
							1);
					c.getPA().addSkillXP((damage * Config.RANGE_EXP_RATE / 3),
							3);
					c.getPA().refreshSkill(1);
					c.getPA().refreshSkill(3);
					c.getPA().refreshSkill(4);
				} else {
					c.getPA().addSkillXP((damage * Config.RANGE_EXP_RATE), 4);
					c.getPA().addSkillXP((damage * Config.RANGE_EXP_RATE / 3),
							3);
					c.getPA().refreshSkill(3);
					c.getPA().refreshSkill(4);
				}
				if (damage > 0 && PestControl.isInGame(c)) {
					if (NPCHandler.npcs[i].npcType == 6142) {
						c.pcDamage += damage;
						if (damage < PestControl.portalHealth[0])
							PestControl.portalHealth[0] -= damage;
						else
							PestControl.portalHealth[0] = 0;
					}
					if (NPCHandler.npcs[i].npcType == 6143) {
						c.pcDamage += damage;
						if (damage < PestControl.portalHealth[1])
							PestControl.portalHealth[1] -= damage;
						else
							PestControl.portalHealth[1] = 0;
					}
					if (NPCHandler.npcs[i].npcType == 6144) {
						c.pcDamage += damage;
						if (damage < PestControl.portalHealth[2])
							PestControl.portalHealth[2] -= damage;
						else
							PestControl.portalHealth[2] = 0;
					}
					if (NPCHandler.npcs[i].npcType == 6145) {
						c.pcDamage += damage;
						if (damage < PestControl.portalHealth[3])
							PestControl.portalHealth[3] -= damage;
						else
							PestControl.portalHealth[3] = 0;
					}
					if (PestControl.npcIsPCMonster(NPCHandler.npcs[i].npcType)) {
						PestControl.KNIGHTS_HEALTH += damage / 4;
					}
				}
				if (c.inBarbDef()) {
					c.barbDamage += damage;
					if (damage2 > 0)
						c.barbDamage += damage2;
				}
				boolean dropArrows = true;

				for (int noArrowId : c.NO_ARROW_DROP) {
					if (c.lastWeaponUsed == noArrowId) {
						dropArrows = false;
						break;
					}
				}
				if (dropArrows) {
					c.getItems().dropArrowNpc();
					if (c.playerEquipment[3] == 11235
							|| c.playerEquipment[3] == 15701
							|| c.playerEquipment[3] == 15702
							|| c.playerEquipment[3] == 15703
							|| c.playerEquipment[3] == 15704) {
						c.getItems().dropArrowNpc();
					}
				}
				if (Server.npcHandler.getNPCs()[i].attackTimer <= 3)
					NPCHandler.startAnimation(c.getCombat().npcDefenceAnim(i),
							i);
				c.rangeEndGFX = RangeData.getRangeEndGFX(c);

				if ((c.playerEquipment[3] == 10034 || c.playerEquipment[3] == 10033)) {
					for (int j = 0; j < NPCHandler.npcs.length; j++) {
						if (NPCHandler.npcs[j] != null
								&& NPCHandler.npcs[j].MaxHP > 0) {
							int nX = NPCHandler.npcs[j].getX();
							int nY = NPCHandler.npcs[j].getY();
							int pX = NPCHandler.npcs[i].getX();
							int pY = NPCHandler.npcs[i].getY();
							if ((nX - pX == -1 || nX - pX == 0 || nX - pX == 1)
									&& (nY - pY == -1 || nY - pY == 0 || nY
											- pY == 1)) {
								if (NPCHandler.npcs[i].inMulti()) {
									Client p = (Client) PlayerHandler.players[c.playerId];
									c.getCombat().appendMutliChinchompa(j);
									Server.npcHandler.attackPlayer(p, j);
								}
							}
						}
					}
				}
				if (!c.multiAttacking) {
					c.getCombat().applySoulSplit(i, damage);
					NPCHandler.npcs[i].underAttack = true;
					NPCHandler.npcs[i].hitDiff = damage;
					NPCHandler.npcs[i].HP -= damage;
					if (damage2 > -1) {
						NPCHandler.npcs[i].hitDiff2 = damage2;
						NPCHandler.npcs[i].HP -= damage2;
						c.totalDamageDealt += damage2;
					}
				}
				c.ignoreDefence = false;
				c.multiAttacking = false;

				if (c.rangeEndGFX > 0) {
					if (c.rangeEndGFXHeight) {
						NPCHandler.npcs[i].gfx100(c.rangeEndGFX);
					} else {
						NPCHandler.npcs[i].gfx0(c.rangeEndGFX);
					}
				}
				if (c.killingNpcIndex != c.oldNpcIndex) {
					c.totalDamageDealt = 0;
				}
				c.killingNpcIndex = c.oldNpcIndex;
				c.totalDamageDealt += damage;
				NPCHandler.npcs[i].hitUpdateRequired = true;
				if (damage2 > -1)
					NPCHandler.npcs[i].hitUpdateRequired2 = true;
				NPCHandler.npcs[i].updateRequired = true;

			} else if (c.projectileStage > 0) { // magic hit damage
				if (NPCHandler.npcs[i].HP <= 0) {
					return;
				}
				int damage = 0;
				c.usingMagic = true;
				if (c.fullVoidMage()
						&& c.playerEquipment[Player.playerWeapon] == 8841) {
					damage = Misc.random(c.getCombat().magicMaxHit() + 10);
				} else {
					damage = Misc.random(c.getCombat().magicMaxHit());
				}
				if (c.getCombat().godSpells()) {
					if (System.currentTimeMillis() - c.godSpellDelay < Config.GOD_SPELL_CHARGE) {
						damage += Misc.random(10);
					}
				}
				boolean magicFailed = false;
				if (Misc.random(NPCHandler.npcs[i].defence) > 10 + Misc
						.random(c.getCombat().mageAtk())) {
					damage = 0;
					magicFailed = true;
				} else if (NPCHandler.npcs[i].npcType == 2881
						|| NPCHandler.npcs[i].npcType == 2882 || kalphite1(i)
						|| NPCHandler.npcs[i].npcType == 8350) {
					damage = 0;
					magicFailed = true;
				}

				for (int j = 0; j < NPCHandler.npcs.length; j++) {
					if (NPCHandler.npcs[j] != null
							&& NPCHandler.npcs[j].MaxHP > 0) {
						int nX = NPCHandler.npcs[j].getX();
						int nY = NPCHandler.npcs[j].getY();
						int pX = NPCHandler.npcs[i].getX();
						int pY = NPCHandler.npcs[i].getY();
						if ((nX - pX == -1 || nX - pX == 0 || nX - pX == 1)
								&& (nY - pY == -1 || nY - pY == 0 || nY - pY == 1)) {
							if (c.getCombat().multis()
									&& NPCHandler.npcs[i].inMulti()) {
								Client p = (Client) PlayerHandler.players[c.playerId];
								c.getCombat().appendMultiBarrageNPC(j,
										c.magicFailed);
								Server.npcHandler.attackPlayer(p, j);
							}
						}
					}
				}

				if (NPCHandler.npcs[i].HP - damage < 0) {
					damage = NPCHandler.npcs[i].HP;
				}

				c.getPA().addSkillXP(
						(Player.MAGIC_SPELLS[c.oldSpellId][7] + damage
								* Config.MAGIC_EXP_RATE), 6);
				c.getPA().addSkillXP(
						(Player.MAGIC_SPELLS[c.oldSpellId][7] + damage
								* Config.MAGIC_EXP_RATE / 3), 3);
				c.getPA().refreshSkill(3);
				c.getPA().refreshSkill(6);
				if (damage > 0 && PestControl.isInGame(c)) {
					if (NPCHandler.npcs[i].npcType == 6142) {
						c.pcDamage += damage;
						if (damage < PestControl.portalHealth[0])
							PestControl.portalHealth[0] -= damage;
						else
							PestControl.portalHealth[0] = 0;
					}
					if (NPCHandler.npcs[i].npcType == 6143) {
						c.pcDamage += damage;
						if (damage < PestControl.portalHealth[1])
							PestControl.portalHealth[1] -= damage;
						else
							PestControl.portalHealth[1] = 0;
					}
					if (NPCHandler.npcs[i].npcType == 6144) {
						c.pcDamage += damage;
						if (damage < PestControl.portalHealth[2])
							PestControl.portalHealth[2] -= damage;
						else
							PestControl.portalHealth[2] = 0;
					}
					if (NPCHandler.npcs[i].npcType == 6145) {
						c.pcDamage += damage;
						if (damage < PestControl.portalHealth[3])
							PestControl.portalHealth[3] -= damage;
						else
							PestControl.portalHealth[3] = 0;
					}
					if (PestControl.npcIsPCMonster(NPCHandler.npcs[i].npcType)) {
						PestControl.KNIGHTS_HEALTH += damage / 4;
					}
				}
				if (c.getCombat().getEndGfxHeight() == 100 && !magicFailed) { // end
																				// GFX
					NPCHandler.npcs[i]
							.gfx100(Player.MAGIC_SPELLS[c.oldSpellId][5]);
				} else if (!magicFailed) {
					NPCHandler.npcs[i]
							.gfx0(Player.MAGIC_SPELLS[c.oldSpellId][5]);
				}

				if (magicFailed) {
					if (Server.npcHandler.getNPCs()[i].attackTimer <= 3) {
						NPCHandler.startAnimation(
								c.getCombat().npcDefenceAnim(i), i);
					}
					NPCHandler.npcs[i].gfx100(85);
				}
				if (!magicFailed) {
					int freezeDelay = c.getCombat().getFreezeTime();// freeze
					if (freezeDelay > 0 && NPCHandler.npcs[i].freezeTimer == 0) {
						NPCHandler.npcs[i].freezeTimer = freezeDelay;
					}
					switch (Player.MAGIC_SPELLS[c.oldSpellId][0]) {
					case 12901:
					case 12919: // blood spells
					case 12911:
					case 12929:
						int heal = Misc.random(damage / 2);
						if (c.playerLevel[3] + heal >= c.getPA().getLevelForXP(
								c.playerXP[3])) {
							c.playerLevel[3] = c.getPA().getLevelForXP(
									c.playerXP[3]);
						} else {
							c.playerLevel[3] += heal;
						}
						c.getPA().refreshSkill(3);
						break;
					}

				}
				c.getCombat().applySoulSplit(i, damage);
				if (c.inBarbDef())
					c.barbDamage += damage;
				NPCHandler.npcs[i].underAttack = true;
				if (Player.MAGIC_SPELLS[c.oldSpellId][6] != 0) {
					NPCHandler.npcs[i].hitDiff = damage;
					NPCHandler.npcs[i].HP -= damage;
					NPCHandler.npcs[i].hitUpdateRequired = true;
					c.totalDamageDealt += damage;
				}
				c.killingNpcIndex = c.oldNpcIndex;
				NPCHandler.npcs[i].updateRequired = true;
				c.usingMagic = false;
				c.castingMagic = false;
				c.oldSpellId = 0;
			}
		}
		if (c.bowSpecShot <= 0) {
			c.oldNpcIndex = 0;
			c.projectileStage = 0;
			c.doubleHit = false;
			c.lastWeaponUsed = 0;
			c.bowSpecShot = 0;
		}
		if (c.bowSpecShot >= 2) {
			c.bowSpecShot = 0;
		}
		if (c.bowSpecShot == 1) {
			c.getCombat().fireProjectileNpc();
			c.hitDelay = 2;
			c.bowSpecShot = 0;
		}
	}

	public static void attackNpc(Client c, int i) {
		if (NPCHandler.npcs[i] != null) {
			if (NPCHandler.npcs[i].HP > 0) {
				if (NPCHandler.npcs[i].isDead || NPCHandler.npcs[i].MaxHP <= 0) {
					c.usingMagic = false;
					c.faceUpdate(0);
					c.npcIndex = 0;
					return;
				}
				if (c.respawnTimer > 0) {
					c.npcIndex = 0;
					return;
				}
				if (NPCHandler.npcs[i].underAttackBy > 0
						&& NPCHandler.npcs[i].underAttackBy != c.playerId
						&& !NPCHandler.npcs[i].inMulti()) {
					c.npcIndex = 0;
					c.sendMessage("This monster is already in combat.");
					return;
				}
				if ((c.underAttackBy > 0 || c.underAttackBy2 > 0)
						&& c.underAttackBy2 != i && !c.inMulti()) {
					c.getCombat().resetPlayerAttack();
					c.sendMessage("I am already under attack.");
					return;
				}
				if (!c.getCombat().goodSlayer(i)) {
					c.getCombat().resetPlayerAttack();
					return;
				}
				if (server.Server.npcHandler.checkSlayerHelm(c, i)) {
					c.slayerHelmEffect = true;
				} else {
					c.slayerHelmEffect = false;
				}
				if (NPCHandler.npcs[i].spawnedBy != c.playerId
						&& NPCHandler.npcs[i].spawnedBy > 0) {
					c.getCombat().resetPlayerAttack();
					c.sendMessage("This monster was not spawned for you.");
					return;
				}
				if (c.playerEquipment[3] == 15241
						&& c.playerEquipment[Player.playerArrows] != 15243) {
					c.sendMessage("You need hand cannon shots to fire this.");
					c.getCombat().resetPlayerAttack();
					return;
				}
				if (c.attackTimer <= 0) {
					c.usingBow = false;
					c.usingArrows = false;
					boolean usingOtherRangeWeapons = false;
					c.usingCross = c.playerEquipment[Player.playerWeapon] == 9185
							|| c.playerEquipment[Player.playerWeapon] == 18357;
					c.bonusAttack = 0;
					c.rangeItemUsed = 0;
					c.projectileStage = 0;
					if (c.autocasting) {
						c.spellId = c.autocastId;
						c.usingMagic = true;
					}
					if (c.spellId > 0) {
						c.usingMagic = true;
						c.followDistance = 5;
						c.stopMovement();
					}
					c.attackTimer = c
							.getCombat()
							.getAttackDelay(
									c.getItems()
											.getItemName(
													c.playerEquipment[Player.playerWeapon])
											.toLowerCase());
					c.specAccuracy = 1.0;
					c.specDamage = 1.0;
					if (!c.usingMagic) {
						for (int bowId : c.BOWS) {
							if (c.playerEquipment[Player.playerWeapon] == bowId) {
								c.usingBow = true;
								for (int arrowId : c.ARROWS) {
									if (c.playerEquipment[Player.playerArrows] == arrowId) {
										c.usingArrows = true;
									}
								}
							}
						}

						for (int otherRangeId : c.OTHER_RANGE_WEAPONS) {
							if (c.playerEquipment[Player.playerWeapon] == otherRangeId) {
								usingOtherRangeWeapons = true;
							}
						}
					}
					if (NPCHandler.npcs[i].npcType == 9463
							&& c.playerEquipment[1] != 6570) {
						c.getCombat().resetPlayerAttack();
						c.sendMessage("You must be wearing a fire cape to harm Ice strykewyrms.");
						return;
					}
					if (c.getCombat().armaNpc(i) && !c.usingCross
							&& !c.usingBow && !c.usingMagic
							&& !c.getCombat().usingCrystalBow()
							&& !c.usingOtherRangeWeapons) {
						c.getCombat().resetPlayerAttack();
						c.sendMessage("You must use ranged or magic attacks to harm Kree'arra.");
						return;
					}
					if (c.inChallenge() && c.usingBow) {
						c.getCombat().resetPlayerAttack();
						c.sendMessage("You cannot use ranged attacks in the Champion's arena!");
						return;
					}
					if (c.inSW() && c.usingBow) {
						c.getCombat().resetPlayerAttack();
						c.sendMessage("You cannot use ranged attacks against the Spirit warriors!");
						return;
					}
					if (c.inChallenge() && usingOtherRangeWeapons) {
						c.getCombat().resetPlayerAttack();
						c.sendMessage("You cannot use ranged attacks in the Champion's arena!");
						return;
					}
					if (c.inSW() && usingOtherRangeWeapons) {
						c.getCombat().resetPlayerAttack();
						c.sendMessage("You cannot use ranged attacks against the Spirit warriors!");
						return;
					}
					if (c.inChallenge() && c.usingMagic) {
						c.getCombat().resetPlayerAttack();
						c.sendMessage("You cannot use magic attacks in the Champion's arena!");
						return;
					}
					if (c.inSW() && c.usingMagic) {
						c.getCombat().resetPlayerAttack();
						c.sendMessage("You cannot use magic attacks against the Spirit warriors!");
						return;
					}
					if ((!c.goodDistance(c.getX(), c.getY(),
							NPCHandler.npcs[i].getX(),
							NPCHandler.npcs[i].getY(), 2) && (c.getCombat()
							.usingHally()
							&& !usingOtherRangeWeapons
							&& !c.usingBow && !c.usingMagic))
							|| (!c.goodDistance(c.getX(), c.getY(),
									NPCHandler.npcs[i].getX(),
									NPCHandler.npcs[i].getY(), 4) && (usingOtherRangeWeapons
									&& !c.usingBow && !c.usingMagic))
							|| (!c.goodDistance(c.getX(), c.getY(),
									NPCHandler.npcs[i].getX(),
									NPCHandler.npcs[i].getY(), 1) && (!usingOtherRangeWeapons
									&& !c.getCombat().usingHally()
									&& !c.usingBow && !c.usingMagic))
							|| ((!c.goodDistance(c.getX(), c.getY(),
									NPCHandler.npcs[i].getX(),
									NPCHandler.npcs[i].getY(), 8) && (c.usingBow || c.usingMagic)))) {
						c.attackTimer = 2;
						return;
					}

					if (!c.usingCross
							&& !c.usingArrows
							&& c.usingBow
							&& (c.playerEquipment[Player.playerWeapon] < 4212 || c.playerEquipment[Player.playerWeapon] > 4223)) {
						c.sendMessage("You have run out of arrows!");
						c.stopMovement();
						c.npcIndex = 0;
						return;
					}
					if (c.getCombat().correctBowAndArrows() < c.playerEquipment[Player.playerArrows]
							&& Config.CORRECT_ARROWS
							&& c.usingBow
							&& !c.getCombat().usingCrystalBow()
							&& !c.usingCross) {
						c.sendMessage("You can't use "
								+ c.getItems()
										.getItemName(
												c.playerEquipment[Player.playerArrows])
										.toLowerCase()
								+ "s with a "
								+ c.getItems()
										.getItemName(
												c.playerEquipment[Player.playerWeapon])
										.toLowerCase() + ".");
						c.stopMovement();
						c.npcIndex = 0;
						return;
					}

					if (c.usingCross && !c.getCombat().properBolts()
							&& !c.usingMagic) {
						c.sendMessage("You must use bolts with a crossbow.");
						c.stopMovement();
						c.getCombat().resetPlayerAttack();
						return;
					}

					if (c.usingBow
							|| c.usingMagic
							|| usingOtherRangeWeapons
							|| (c.goodDistance(c.getX(), c.getY(),
									NPCHandler.npcs[i].getX(),
									NPCHandler.npcs[i].getY(), 2) && c
									.getCombat().usingHally())) {
						c.stopMovement();
					}

					if (!c.getCombat().checkMagicReqs(c.spellId)) {
						c.stopMovement();
						c.npcIndex = 0;
						return;
					}

					c.faceUpdate(i);
					// c.specAccuracy = 1.0;
					// c.specDamage = 1.0;
					NPCHandler.npcs[i].underAttackBy = c.playerId;
					NPCHandler.npcs[i].lastDamageTaken = System
							.currentTimeMillis();
					if (c.usingSpecial && !c.usingMagic) {
						if (c.getCombat().checkSpecAmount(
								c.playerEquipment[Player.playerWeapon])) {
							c.lastWeaponUsed = c.playerEquipment[Player.playerWeapon];
							c.lastArrowUsed = c.playerEquipment[Player.playerArrows];
							c.getCombat().activateSpecial(
									c.playerEquipment[Player.playerWeapon], i);
							return;
						} else {
							c.sendMessage("You don't have the required special energy to use this attack.");
							c.usingSpecial = false;
							c.getItems().updateSpecialBar();
							c.npcIndex = 0;
							return;
						}
					}
					c.specMaxHitIncrease = 0;
					if (!c.usingMagic) {
						c.startAnimation(c
								.getCombat()
								.getWepAnim(
										c.getItems()
												.getItemName(
														c.playerEquipment[Player.playerWeapon])
												.toLowerCase()));
					} else {
						c.startAnimation(Player.MAGIC_SPELLS[c.spellId][2]);
					}
					c.lastWeaponUsed = c.playerEquipment[Player.playerWeapon];
					c.lastArrowUsed = c.playerEquipment[Player.playerArrows];

					if (!c.usingBow && !c.usingMagic && !usingOtherRangeWeapons) { // melee
																					// hit
																					// delay
						c.followId2 = NPCHandler.npcs[i].npcId;
						c.getPA().followNpc();
						c.hitDelay = c
								.getCombat()
								.getHitDelay(
										i,
										c.getItems()
												.getItemName(
														c.playerEquipment[Player.playerWeapon])
												.toLowerCase());
						c.projectileStage = 0;
						c.oldNpcIndex = i;
					}

					if (c.usingBow && !usingOtherRangeWeapons && !c.usingMagic
							|| c.usingCross) { // range hit delay
						c.followId2 = NPCHandler.npcs[i].npcId;
						c.getPA().followNpc();
						if (c.usingCross)
							c.usingBow = true;
						if (c.fightMode == 2)
							c.attackTimer--;
						c.lastArrowUsed = c.playerEquipment[Player.playerArrows];
						c.lastWeaponUsed = c.playerEquipment[Player.playerWeapon];
						c.gfx100(c.getCombat().getRangeStartGFX());
						c.hitDelay = c
								.getCombat()
								.getHitDelay(
										i,
										c.getItems()
												.getItemName(
														c.playerEquipment[Player.playerWeapon])
												.toLowerCase());
						c.projectileStage = 1;
						c.oldNpcIndex = i;
						if (c.playerEquipment[Player.playerWeapon] >= 4212
								&& c.playerEquipment[Player.playerWeapon] <= 4223) {
							c.rangeItemUsed = c.playerEquipment[Player.playerWeapon];
							c.crystalBowArrowCount++;
							c.gfx100(c.getCombat().getRangeStartGFX());
							c.lastArrowUsed = 0;
						} else {
							c.rangeItemUsed = c.playerEquipment[Player.playerArrows];
							c.getItems().deleteArrow();
							if (c.playerEquipment[Player.playerWeapon] == 11235
									|| c.playerEquipment[Player.playerWeapon] == 15701
									|| c.playerEquipment[Player.playerWeapon] == 15702
									|| c.playerEquipment[Player.playerWeapon] == 15703
									|| c.playerEquipment[Player.playerWeapon] == 15704)
								c.getItems().deleteArrow();
							c.gfx100(c.getCombat().getRangeStartGFX());
						}
						c.getCombat().fireProjectileNpc();
					}

					if (usingOtherRangeWeapons && !c.usingMagic && !c.usingBow) { // knives,
																					// darts,
																					// etc
																					// hit
																					// delay
						c.followId2 = NPCHandler.npcs[i].npcId;
						c.getPA().followNpc();
						c.rangeItemUsed = c.playerEquipment[Player.playerWeapon];
						c.getItems().deleteEquipment();
						c.gfx100(c.getCombat().getRangeStartGFX());
						c.lastArrowUsed = 0;
						c.hitDelay = c
								.getCombat()
								.getHitDelay(
										i,
										c.getItems()
												.getItemName(
														c.playerEquipment[Player.playerWeapon])
												.toLowerCase());
						c.projectileStage = 1;
						c.oldNpcIndex = i;
						if (c.fightMode == 2)
							c.attackTimer--;
						c.getCombat().fireProjectileNpc();
					}

					if (c.usingMagic) { // magic hit delay
						int pX = c.getX();
						int pY = c.getY();
						int nX = NPCHandler.npcs[i].getX();
						int nY = NPCHandler.npcs[i].getY();
						int offX = (pY - nY) * -1;
						int offY = (pX - nX) * -1;
						c.castingMagic = true;
						c.projectileStage = 2;
						c.followId2 = NPCHandler.npcs[i].npcId;
						c.getPA().followNpc();
						if (Player.MAGIC_SPELLS[c.spellId][3] > 0) {
							if (c.getCombat().getStartGfxHeight() == 100) {
								c.gfx100(Player.MAGIC_SPELLS[c.spellId][3]);
							} else {
								c.gfx0(Player.MAGIC_SPELLS[c.spellId][3]);
							}
						}
						if (Player.MAGIC_SPELLS[c.spellId][4] > 0) {
							c.getPA().createPlayersProjectile(pX, pY, offX,
									offY, 50, 78,
									Player.MAGIC_SPELLS[c.spellId][4],
									c.getCombat().getStartHeight(),
									c.getCombat().getEndHeight(), i + 1, 50);
						}
						c.hitDelay = c
								.getCombat()
								.getHitDelay(
										offY,
										c.getItems()
												.getItemName(
														c.playerEquipment[Player.playerWeapon])
												.toLowerCase());
						c.oldNpcIndex = i;
						c.oldSpellId = c.spellId;
						c.spellId = 0;
						if (!c.autocasting)
							c.npcIndex = 0;
					}

					if (c.usingBow && Config.CRYSTAL_BOW_DEGRADES) { // crystal
																		// bow
																		// degrading
						if (c.playerEquipment[Player.playerWeapon] == 4212) { // new
																				// crystal
																				// bow
																				// becomes
																				// full
																				// bow
																				// on
																				// the
																				// first
																				// shot
							c.getItems().wearItem(4214, 1, 3);
						}

						if (c.crystalBowArrowCount >= 250) {
							switch (c.playerEquipment[Player.playerWeapon]) {

							case 4223: // 1/10 bow
								c.getItems().wearItem(-1, 1, 3);
								c.sendMessage("Your crystal bow has fully degraded.");
								if (!c.getItems().addItem(4207, 1)) {
									Server.itemHandler.createGroundItem(c,
											4207, c.getX(), c.getY(), 1,
											c.getId());
								}
								c.crystalBowArrowCount = 0;
								break;

							default:
								c.getItems()
										.wearItem(
												++c.playerEquipment[Player.playerWeapon],
												1, 3);
								c.sendMessage("Your crystal bow degrades.");
								c.crystalBowArrowCount = 0;
								break;
							}
						}
					}
				}
			}
		}
	}
}