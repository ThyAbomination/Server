package server.model.players.skills;

import server.model.players.Client;

public class SkillHandler {

	public static boolean[] isSkilling = new boolean[25];

	public static long lastSkillingAction;

	public static void resetSkillingVariables() {
		for (int skill = 0; skill < isSkilling.length; skill++) {
			isSkilling[skill] = false;
		}
	}

	public static boolean noInventorySpace(Client c, String skill) {
		if (c.getItems().freeSlots() == 0) {
			c.sendMessage("You don't have enough inventory space.");
			// c.getPA().sendStatement("You don't have enough inventory space to continue "+skill+".");
			return false;
		}
		return true;
	}
}