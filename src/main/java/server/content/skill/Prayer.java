package server.content.skill;

import java.util.HashMap;

import server.Config;
import server.Server;
import server.model.players.Client;
import server.util.Misc;

/**
 * 
 * @author Tyler Buchanan
 * @version 2.6 Prayer.java
 */
public class Prayer {

	Client c;

	/**
	 * 
	 * @author Tyler Buchanan
	 * @version 2.6 Prayer.java
	 */
	enum Bones {
		REGULAR(526, 10, "Bones"), BIG(532, 30, "Big Bones"), BABY_DRAG(534,
				60, "Baby Dragon Bones"), DRAG(536, 144, "Dragon Bones"), DAG(
				6729, 250, "Dagannoth Bones"), BAT(530, 10, "Bat Bones"), WOLF(
				2859, 10, "Wolf Bones"), MONKEY(3179, 10, "Monkey Bones"), FROST(
				18830, 360, "Frost Dragon Bones"), ANCIENT(15410, 400,
				"Ancient Bones"), JOGRE(3125, 30, "Jogre Bones"), OURG(4834,
				140, "Ourg Bones");

		static HashMap<Integer, Bones> BoneInfo = new HashMap<Integer, Bones>();

		int ID, XP;
		String Name;

		static {
			for (final Bones b : BoneInfo.values())
				Bones.BoneInfo.put(b.XP, b);
		}

		/**
		 * 
		 * @param ID
		 * @param XP
		 * @param Name
		 */
		Bones(final int ID, final int XP, final String Name) {
			this.ID = ID;
			this.XP = XP;
			this.Name = Name;
		}

		/**
		 * 
		 * @return
		 */
		int getID() {
			return ID;
		}

		/**
		 * 
		 * @return
		 */
		int getXP() {
			return XP;
		}

		/**
		 * 
		 * @return
		 */
		String getName() {
			return Name;
		}

	}

	public Prayer(Client c) {
		this.c = c;
	}

	/**
	 * 
	 * @param ID
	 *            The bone Id
	 * @return
	 */
	public boolean IsABone(int ID) {
		for (final Bones b : Bones.values()) {
			if (c.getItems().playerHasItem(b.getID(), 1)) {
				if (b.getID() == ID) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * @param ID
	 *            The bone Id
	 */
	public void buryBone(int ID) {
		if (c.duelStatus == 5) {
			c.sendMessage("You can't bury bones during a duel!");
			return;
		}
		if (System.currentTimeMillis() - c.buryDelay > 1500) {
			for (final Bones b : Bones.values()) {
				if (ID == b.getID()) {
					int doubleExperience = Misc.random(20);
					if (doubleExperience >= 1) {
						int zombie = Misc.random(100);
						if (zombie == 1) {
							Server.npcHandler.spawnNpc(c, 5409, c.absX + 1,
									c.absY, 0, 1, 100, 10, 100, 50, true, true);
							c.sendMessage("A zombie appeared after you buried the bones!");
						}
						c.getItems().deleteItem(ID, 1);
						c.sendMessage("You bury some " + b.getName() + ".");
						c.getPA().addSkillXP(
								b.getXP() * Config.PRAYER_EXPERIENCE, 5);
						c.buryDelay = System.currentTimeMillis();
						c.startAnimation(827);
					} else if (doubleExperience == 0) {
						c.getItems().deleteItem(ID, 1);
						c.sendMessage("You bury some " + b.getName()
								+ " snd get double xp!");
						c.getPA().addSkillXP(
								b.getXP() * 2 * Config.PRAYER_EXPERIENCE, 5);
						c.buryDelay = System.currentTimeMillis();
						c.startAnimation(827);
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param ID
	 *            the bone Id
	 */
	public void bonesOnAltar(int ID) {
		int Failure = Misc.random(40);
		for (final Bones b : Bones.values()) {
			if (ID == b.getID()) {
				if (Failure >= 1) {
					c.getItems().deleteItem(ID, 1);
					c.sendMessage("The God's accept your offering of "
							+ b.getName());
					c.getPA().addSkillXP(
							b.getXP() * 4 * Config.PRAYER_EXPERIENCE, 5);
				} else if (Failure == 0) {
					c.gfx100(287);
					c.startAnimation(3103);
					c.forcedChat("I'm sorry!");
					c.sendMessage("The gods are not satisfied with your offering!");
					c.dealDamage(10);
					c.handleHitMask(10);
					c.getPA().refreshSkill(3);
					c.updateRequired = true;
				}
			}
		}
	}
}