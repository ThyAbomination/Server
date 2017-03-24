package server.model.players.skills;

import server.Config;
import server.model.players.Client;
import server.util.Misc;

public class Thieving {

	private Client c;

	public Thieving(Client c) {
		this.c = c;
	}

	public void stealFromStall(int id, int amount, int xp, int level) {
		if (System.currentTimeMillis() - c.lastThieve < 2500)
			return;
		if (c.playerLevel[c.playerThieving] >= level) {
			if (c.getItems().freeSlots() >= 2) {
				int item = Misc.random(3);
				if (item == 0) {
					c.getItems().addItem(c.getPA().randomThief(), 1);
				}
				if (item == 1) {
					c.getItems().addItem(c.getPA().randomThief2(), 1);
				}
				if (item == 2) {
					c.getItems().addItem(c.getPA().randomRunes2(),
							Misc.random(100));
				}
				if (c.getItems().addItem(id, amount)) {
					c.getPA().addSkillXP(xp * Config.THIEVING_EXPERIENCE,
							c.playerThieving);
					c.lastThieve = System.currentTimeMillis();
					c.startAnimation(832);
					c.sendMessage("You receive " + amount + " coins.");
				}
			} else if (c.getItems().freeSlots() <= 1) {
				c.sendMessage("You need at least 2 inventory slots.");
			}
		} else {
			c.sendMessage("You must have a thieving level of " + level
					+ " to thieve from this stall.");
		}
	}

}