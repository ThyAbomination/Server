package server.model.npcs;

import server.model.players.Client;
import server.util.Misc;
import server.util.Stream;

public class NPC {

	public String dagColor = "";
	public int lastX, lastY;
	public int npcId;
	public int npcType;
	public int absX, absY;
	public int heightLevel;
	public int makeX, makeY, maxHit, defence, attack, moveX, moveY, direction,
			walkingType;
	public int spawnX, spawnY;
	public int viewX, viewY;
	public int hp, maxHP;
	public long singleCombatDelay = 0;

	/**
	 * attackType: 0 = melee, 1 = range, 2 = mage
	 */
	public int attackType, projectileId, endGfx, spawnedBy, hitDelayTimer, HP,
			MaxHP, hitDiff, animNumber, actionTimer, enemyX, enemyY;
	public boolean applyDead, isDead, needRespawn, respawns;
	public boolean walkingHome, underAttack;
	public int freezeTimer, attackTimer, killerId, killedBy, oldIndex,
			underAttackBy;
	public long lastDamageTaken;
	public boolean randomWalk;
	public boolean dirUpdateRequired;
	public boolean animUpdateRequired;
	public boolean hitUpdateRequired;
	public boolean updateRequired;
	public boolean forcedChatRequired;
	public boolean faceToUpdateRequired;
	public int firstAttacker;
	public String forcedText;

	public NPC(int _npcId, int _npcType) {
		npcId = _npcId;
		npcType = _npcType;
		direction = -1;
		isDead = false;
		applyDead = false;
		actionTimer = 0;
		randomWalk = true;
	}

	public void faceTo(int playerId) {
		// this.rawDirection = 32768 + playerId;
		this.dirUpdateRequired = true;
		this.updateRequired = true;
	}

	public void updateNPCMovement(Stream str) {
		if (direction == -1) {

			if (updateRequired) {

				str.writeBits(1, 1);
				str.writeBits(2, 0);
			} else {
				str.writeBits(1, 0);
			}
		} else {

			str.writeBits(1, 1);
			str.writeBits(2, 1);
			str.writeBits(3, Misc.xlateDirectionToClient[direction]);
			if (updateRequired) {
				str.writeBits(1, 1);
			} else {
				str.writeBits(1, 0);
			}
		}
	}

	/**
	 * Text update
	 **/

	public void forceChat(String text) {
		forcedText = text;
		forcedChatRequired = true;
		updateRequired = true;
	}

	/**
	 * @return the npcId
	 */
	public int getNpcId() {
		return npcId;
	}

	/**
	 * Graphics
	 **/

	public int mask80var1 = 0;
	public int mask80var2 = 0;
	protected boolean mask80update = false;

	public void appendMask80Update(Stream str) {
		str.writeWord(mask80var1);
		str.writeDWord(mask80var2);
	}

	public void gfx100(int gfx) {
		mask80var1 = gfx;
		mask80var2 = 6553600;
		mask80update = true;
		updateRequired = true;
	}

	public void gfx0(int gfx) {
		mask80var1 = gfx;
		mask80var2 = 65536;
		mask80update = true;
		updateRequired = true;
	}

	public void appendAnimUpdate(Stream str) {
		str.writeWordBigEndian(animNumber);
		str.writeByte(1);
	}

	/**
	 * 
	 Face
	 * 
	 **/

	public int FocusPointX = -1, FocusPointY = -1;
	public int face = 0;

	private void appendSetFocusDestination(Stream str) {
		str.writeWordBigEndian(FocusPointX);
		str.writeWordBigEndian(FocusPointY);
	}

	public void turnNpc(int i, int j) {
		FocusPointX = 2 * i + 1;
		FocusPointY = 2 * j + 1;
		updateRequired = true;

	}

	public void appendFaceEntity(Stream str) {
		str.writeWord(face);
	}

	public void facePlayer(int player) {
		face = player + 32768;
		dirUpdateRequired = true;
		updateRequired = true;
	}

	public void appendFaceToUpdate(Stream str) {
		str.writeWordBigEndian(viewX);
		str.writeWordBigEndian(viewY);
	}

	public void appendNPCUpdateBlock(Stream str) {
		if (!updateRequired)
			return;
		int updateMask = 0;
		if (animUpdateRequired)
			updateMask |= 0x10;
		if (hitUpdateRequired2)
			updateMask |= 8;
		if (mask80update)
			updateMask |= 0x80;
		if (dirUpdateRequired)
			updateMask |= 0x20;
		if (forcedChatRequired)
			updateMask |= 1;
		if (hitUpdateRequired)
			updateMask |= 0x40;
		if (FocusPointX != -1)
			updateMask |= 4;

		str.writeByte(updateMask);

		if (animUpdateRequired)
			appendAnimUpdate(str);
		if (hitUpdateRequired2)
			appendHitUpdate2(str);
		if (mask80update)
			appendMask80Update(str);
		if (dirUpdateRequired)
			appendFaceEntity(str);
		if (forcedChatRequired) {
			str.writeString(forcedText);
		}
		if (hitUpdateRequired)
			appendHitUpdate(str);
		if (FocusPointX != -1)
			appendSetFocusDestination(str);

	}

	public void clearUpdateFlags() {
		updateRequired = false;
		forcedChatRequired = false;
		hitUpdateRequired = false;
		hitUpdateRequired2 = false;
		animUpdateRequired = false;
		dirUpdateRequired = false;
		mask80update = false;
		forcedText = null;
		moveX = 0;
		moveY = 0;
		direction = -1;
		FocusPointX = -1;
		FocusPointY = -1;
	}

	public int getNextWalkingDirection() {
		int dir;
		dir = Misc.direction(absX, absY, (absX + moveX), (absY + moveY));
		if (dir == -1)
			return -1;

		dir >>= 1;
		absX += moveX;
		absY += moveY;
		return dir;
	}

	public void getNextNPCMovement(int i) {
		direction = -1;
		if (NPCHandler.npcs[i].freezeTimer == 0) {
			direction = getNextWalkingDirection();
		}
	}

	public void appendHitUpdate(Stream str) {
		if (HP <= 0) {
			isDead = true;
		}
		str.writeByteC(hitDiff);
		if (hitDiff > 0) {
			str.writeByteS(1);
		} else {
			str.writeByteS(0);
		}
		str.writeByteS(Misc.getCurrentHP(HP, MaxHP, 100));
		str.writeByteC(100);
	}

	public int hitDiff2 = 0;
	public boolean hitUpdateRequired2 = false;

	public void appendHitUpdate2(Stream str) {
		if (HP <= 0) {
			isDead = true;
		}
		str.writeByteA(hitDiff2);
		if (hitDiff2 > 0) {
			str.writeByteC(1);
		} else {
			str.writeByteC(0);
		}
		str.writeByteA(HP);
		str.writeByte(MaxHP);
	}

	public void handleHitMask(int damage) {
		if (!hitUpdateRequired) {
			hitUpdateRequired = true;
			hitDiff = damage;
		} else if (!hitUpdateRequired2) {
			hitUpdateRequired2 = true;
			hitDiff2 = damage;
		}
		updateRequired = true;
	}

	public int getX() {
		return absX;
	}

	public int getY() {
		return absY;
	}

	/*
	 * public NPCDefinition getDefinition() { return definition; }
	 */

	public boolean inMulti() {
		if ((absX >= 3136 && absX <= 3327 && absY >= 3519 && absY <= 3607)
				|| (absX >= 3190 && absX <= 3327 && absY >= 3648 && absY <= 3839)
				|| (absX >= 3200 && absX <= 3390 && absY >= 3840 && absY <= 3967)
				|| (absX >= 2992 && absX <= 3007 && absY >= 3912 && absY <= 3967)
				|| (absX >= 2946 && absX <= 2959 && absY >= 3816 && absY <= 3831)
				|| (absX >= 3008 && absX <= 3199 && absY >= 3856 && absY <= 3903)
				|| (absX >= 3008 && absX <= 3071 && absY >= 3600 && absY <= 3711)
				|| (absX >= 3072 && absX <= 3327 && absY >= 3608 && absY <= 3647)
				|| (absX >= 2624 && absX <= 2690 && absY >= 2550 && absY <= 2619)
				|| (absX >= 2371 && absX <= 2422 && absY >= 5062 && absY <= 5117)
				|| (absX >= 2896 && absX <= 2927 && absY >= 3595 && absY <= 3630)
				|| (absX >= 2892 && absX <= 2932 && absY >= 4435 && absY <= 4464)
				|| (absX >= 3131 && absX <= 3273 && absY >= 9520 && absY <= 9602)
				|| (absX >= 2850 && absX <= 2878 && absY >= 9825 && absY <= 9860)
				|| (absX >= 3460 && absX <= 3517 && absY >= 9475 && absY <= 9532)
				|| (absX >= 2971 && absX <= 2986 && absY >= 9512 && absY <= 9523)
				|| (absX >= 2256 && absX <= 2287 && absY >= 4680 && absY <= 4711)
				|| (absX >= 2538 && absX <= 2590 && absY >= 4932 && absY <= 4987)
				|| (absX >= 2690 && absX <= 2750 && absY >= 9410 && absY <= 9470)
				|| (absX >= 2501 && absX <= 2545 && absY >= 4623 && absY <= 4666)
				|| (absX >= 2820 && absX <= 2889 && absY >= 9484 && absY <= 9549)
				|| coordsCheck(3332, 3358, 3206, 3220)) {
			return true;
		}
		return false;
	}

	public boolean inWild() {
		if (absX > 2941 && absX < 3392 && absY > 3518 && absY < 3966
				|| absX > 2941 && absX < 3392 && absY > 9918 && absY < 10366) {
			return true;
		}
		return false;
	}

	public boolean coordsCheck(int X1, int X2, int Y1, int Y2) {
		return absX >= X1 && absX <= X2 && absY >= Y1 && absY <= Y2;
	}

	public boolean inBarbDef() {
		return (coordsCheck(3332, 3358, 3206, 3220));
	}

	public String barbRandom(Client c, int type) {
		switch (type) {
		case 0:
			return "KILL THAT MONGREL!";
		case 1:
			return "YOU'RE MINE, " + c.playerName.toUpperCase() + "!";
		case 2:
			return "YOU REALLY THINK YOU CAN BEAT ME?";
		case 3:
			return "IS THAT ALL YOU'VE GOT WEAKLING?";
		case 4:
			return "FAILURE IS NOT AN OPTION MY MINIONS! ATTACK!";
		}
		return "";
	}

	public void requestAnimation(int animId, int i) {
		animNumber = animId;
		animUpdateRequired = true;
		updateRequired = true;
	}

	public void dealDamage(int damage) {
		if (damage > HP) {
			damage = HP;
		}
		HP -= damage;
	}
}
