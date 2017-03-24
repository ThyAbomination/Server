package server.content;

import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.players.Client;
import server.model.players.Player;

public class HealingContent {

	public static void produceHealing(final Client c) {
		if (c.checkBusy()) {
			return;
		}
		c.setBusy(true);
		CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {

			@Override
			public void execute(CycleEventContainer container) {
				// TODO Auto-generated method stub
				c.playerLevel[3] = c.getLevelForXP(c.playerXP[3]);
				c.getPA().refreshSkill(3);
				c.sendMessage("You have been healed.");
				c.startAnimation(1500);
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {

					@Override
					public void execute(CycleEventContainer container) {
						// TODO Auto-generated method stub
						c.startAnimation(1914);
						c.setBusy(false);
						container.stop();
					}

					@Override
					public void stop() {
						// TODO Auto-generated method stub

					}

				}, 2);
				c.gfx0(444);
				c.playerLevel[5] = c.getPA().getLevelForXP(c.playerXP[5]);
				c.sendMessage("You recharge your prayer points.");
				c.getPA().refreshSkill(5);
				c.specAmount = 10.0;
				c.getItems().updateSpecialBar();
				c.getItems().addSpecialBar(
						c.playerEquipment[Player.playerWeapon]);
				c.sendMessage("Your special attack has been restored.");
				c.setBusy(false);
				container.stop();
			}

			@Override
			public void stop() {
				// TODO Auto-generated method stub

			}

		}, 2);
	}

}
