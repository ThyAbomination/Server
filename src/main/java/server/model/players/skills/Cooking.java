package server.model.players.skills;

import java.security.SecureRandom;

import server.Config;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.players.Client;

public class Cooking {

	SecureRandom cookingRandom = new SecureRandom();
	Client c;

	public Cooking(Client c) {
		this.c = c;
	}

	public enum Cook {

		SHRIMP(317, 315, 7954, 1, 30, 34), SARDINE(327, 325, 369, 1, 40, 38), ANCHOVIES(
				321, 319, 323, 5, 30, 34), HERRING(345, 347, 357, 5, 50, 39), MACKERAL(
				353, 355, 357, 10, 60, 45), COD(341, 339, 343, 18, 75, 39), TROUT(
				335, 333, 343, 15, 70, 50), SALMON(331, 329, 343, 25, 90, 58), PIKE(
				359, 361, 343, 20, 80, 64), LOBSTER(377, 379, 381, 40, 120, 74), BASS(
				363, 365, 367, 43, 130, 80), SWORDFISH(371, 373, 375, 45, 140,
				86), MONKFISH(7944, 7946, 7948, 62, 150, 91), SHARK(383, 385,
				387, 80, 210, 94), MANTA_RAY(389, 391, 393, 91, 216, 99), ROCKTAIL(
				15270, 15272, 15274, 93, 225, 99);

		int raw, cooked, burnt, levelReq, xp, stopBurn;

		private Cook(int raw, int cooked, int burnt, int levelReq, int xp,
				int stopBurn) {
			this.raw = raw;
			this.cooked = cooked;
			this.burnt = burnt;
			this.levelReq = levelReq;
			this.xp = xp;
			this.stopBurn = stopBurn;
		}

		public int getRaw() {
			return raw;
		}

		public int getCooked() {
			return cooked;
		}

		public int getBurnt() {
			return burnt;
		}

		public int getLevelReq() {
			return levelReq;
		}

		public int getXp() {
			return xp;
		}

		public int getStopBurn() {
			return stopBurn;
		}
	}

	public Cook forId(int id) {
		for (Cook co : Cook.values()) {
			if (co.getRaw() == id)
				return co;
		}
		return null;
	}

	private boolean getSuccess(int burnBonus, int levelReq, int stopBurn) {
		if (c.playerLevel[c.playerCooking] >= stopBurn) {
			return false;
		}
		double burn_chance = (50 - burnBonus); // 52
		double cook_level = c.playerLevel[c.playerCooking]; // 99
		double lev_needed = levelReq; // 78
		double burn_stop = stopBurn; //
		double multi_a = (burn_stop - lev_needed);
		double burn_dec = (burn_chance / multi_a);
		double multi_b = (cook_level - lev_needed);
		burn_chance -= (multi_b * burn_dec);
		double randNum = cookingRandom.nextDouble() * 25.0;
		return burn_chance <= randNum;
	}

	public void cookFish(int id) {
		final Cook co = forId(id);
		c.isCooking = true;
		if (co != null)
			if (c.getItems().playerHasItem(co.getRaw(), 1)) {
				if (c.playerLevel[c.playerCooking] >= co.getLevelReq()) {

					if (c.isCooking == false) {
						return;
					}
					if (c.checkBusy()) {
						return;
					}
					c.setBusy(true);
					c.turnPlayerTo(c.objectX, c.objectY);
					CycleEventHandler.getSingleton().addEvent(c,
							new CycleEvent() {
								@Override
								public void execute(
										CycleEventContainer container) {

									if (System.currentTimeMillis()
											- c.cookTimer > 2400
											&& c.getItems().playerHasItem(
													co.getRaw(), 1)) {
										if (c.isCooking == false) {
											c.setBusy(false);
											container.stop();
											return;
										}
										boolean burn = getSuccess(3,
												co.getLevelReq(),
												co.getStopBurn());
										c.getItems().deleteItem(co.getRaw(), 1);
										c.getItems().addItem(
												burn ? co.getBurnt() : co
														.getCooked(), 1);
										c.sendMessage(burn ? "You accidently burnt the fish."
												: "You cooked the fish.");
										c.startAnimation(883);
										c.cookTimer = System
												.currentTimeMillis();
										if (!burn) {
											c.getPA()
													.addSkillXP(
															co.getXp()
																	* Config.FISHING_EXPERIENCE,
															c.playerCooking);
										}
										if (c.getItems().playerHasItem(
												co.getRaw(), 1)) {
											cookFish(co.getRaw());
										} else {
											c.setBusy(false);
											container.stop();
										}
									}
								}

								@Override
								public void stop() {
									// TODO Auto-generated method stub

								}
							}, 1);
				}
			}
	}
}