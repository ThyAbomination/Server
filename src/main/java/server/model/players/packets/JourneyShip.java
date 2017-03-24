package server.model.players.packets;

import server.event.Event;
import server.event.EventContainer;
import server.event.EventManager;
import server.model.players.Client;

public class JourneyShip {

	/**
	 * if(c.getItems().playerHasItem(995,3000)) {
	 * JourneyShip.travelToKaramja(c); }
	 **/

	public static void travelToKaramja(final Client client) {
		client.getPA().showInterface(3281);
		if (client.checkBusy()) {
			return;
		}
		client.setBusy(true);
		client.setCanWalk(false);
		EventManager.getSingleton().addEvent(new Event() {

			// @Override
			@Override
			public void execute(EventContainer container) {
				client.getPA().spellTeleport(2956, 3142, 1);
				container.stop();
			}

			// @Override
			public void stop() {
				client.getItems().deleteItem(995, 3000);
				client.setBusy(false);
				client.setCanWalk(true);
				client.getPA().removeAllWindows();
			}

		}, 2100);
	}

	public static void travelToPortSarim(final Client client) {
		client.getPA().showInterface(3281);
		if (client.checkBusy()) {
			return;
		}
		client.setBusy(true);
		client.setCanWalk(false);
		EventManager.getSingleton().addEvent(new Event() {

			// @Override
			@Override
			public void execute(EventContainer container) {
				client.getPA().spellTeleport(3032, 3217, 1);
				container.stop();
			}

			// @Override
			public void stop() {
				client.getItems().deleteItem(995, 3000);
				client.setBusy(false);
				client.setCanWalk(true);
				client.getPA().removeAllWindows();
			}

		}, 2100);
	}

	public static void travelToBrimhaven(final Client client) {
		client.getPA().showInterface(3281);
		if (client.checkBusy()) {
			return;
		}
		client.setBusy(true);
		client.setCanWalk(false);
		EventManager.getSingleton().addEvent(new Event() {

			@Override
			public void execute(EventContainer container) {
				client.getPA().spellTeleport(2772, 3234, 0);
				container.stop();
			}

			// @Override
			public void stop() {
				client.getItems().deleteItem(995, 3000);
				client.setBusy(false);
				client.setCanWalk(true);
				client.getPA().removeAllWindows();
			}

		}, 2100);
	}

	public static void beginnersshit(final Client client) {
		client.getPA().showInterface(18460);
		if (client.checkBusy()) {
			return;
		}
		client.setBusy(true);
		client.setCanWalk(false);
		EventManager.getSingleton().addEvent(new Event() {

			// @Override
			@Override
			public void execute(EventContainer container) {
				// client.getPA().spellTeleport(client.getAbsX, client.getAbsY,
				// 0);
				container.stop();
			}

			// @Override
			public void stop() {

				client.setBusy(false);
				client.setCanWalk(true);
				client.getPA().removeAllWindows();
				client.getPA()
						.sendFrame126(
								"Please talk with Infexis Guide to begin your adventures.",
								357);
				client.getPA().sendFrame164(356);
				// client.getPA().createArrow(1,
				// Server.getNpcManager().TutorId);
			}

		}, 2100);
	}

}
