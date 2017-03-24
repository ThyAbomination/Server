package server.content.travel;

import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.players.Client;

public class RugTravel {

	public static void firstClickNpc(final Client c, int npcType) {
		switch (npcType) {
		case 2291:
			c.getDH().sendDialogues(130, npcType);
			break;

		case 2292:
			c.getDH().sendDialogues(132, npcType);
			break;
		}
	}

	public enum Location {
		POL(3222, 3221, new int[][] { { 3201, 3222 }, { 3222, 3225 }, }, 0);
		int x, y, id;
		int[][] setup;

		private Location(int x, int y, int[][] setup, int id) {
			this.x = x;
			this.y = y;
			this.setup = setup;
			this.id = id;
		}

		public int getId() {
			return id;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public int[][] getSetup() {
			return setup;
		}
	}

	public static Location forLocation(int type) {
		for (Location loc : Location.values()) {
			if (type == loc.getId()) {
				return loc;
			}
		}
		return null;
	}

	public static void travel(final Client c, final Location l) {
		if (c.checkBusy()) {
			return;
		}
		c.setBusy(true);
		CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
			public int cycle = 30;

			@Override
			public void execute(CycleEventContainer container) {
				// TODO Auto-generated method stub
				if (cycle != 0) {
					cycle--;
					int x = l.getSetup()[0][0];
					int y = l.getSetup()[0][1];
					c.getPA().movePlayer(x++, y++, 0);

				} else if (cycle == 0) {
					c.getPA().movePlayer(l.getX(), l.getY(), 0);
					c.setBusy(false);
					container.stop();
				}
			}

			@Override
			public void stop() {
				// TODO Auto-generated method stub

			}

		}, 1);
	}

}
