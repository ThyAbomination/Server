package server.content.travel;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import server.event.Event;
import server.event.EventContainer;
import server.event.EventManager;
import server.model.players.Client;
import server.util.Misc;

public class Ladders {

	public Ladders(Client Client) {
	}

	public static int ADVANCED_LADDERS[] = { 1755, 1747, 1568, 1746, 1752,
			1754, 881, 12964, 12965, 12966, 14879, 5492, 5493, 1749, 1750,
			1755, 133, 1746, 1754 };

	public static boolean isAdvancedLadder(int obId) {
		for (int i = 0; i < ADVANCED_LADDERS.length; i++) {

			if (obId == ADVANCED_LADDERS[i]) {
				return true;
			}
		}
		return false;
	}

	public static boolean ladderHandling(final Client c, final int ladderId,
			final int ladderX, final int ladderY) {
		if (c.checkBusy()) {
			return true;
		}
		c.setBusy(true);
		c.setCanWalk(false);
		c.startAnimation(828);
		EventManager.getSingleton().addEvent(new Event() {

			@Override
			public void execute(EventContainer container) {
				// TODO Auto-generated method stub
				c.resetWalkingQueue();
				for (int i = 0; i < ladders.length; i++) {
					if (ladderId == ladders[i][0] && ladderX == ladders[i][1]
							&& ladderY == ladders[i][2]
							&& c.heightLevel == ladders[i][3]) {
						c.getPA().movePlayer(ladders[i][4], ladders[i][5],
								ladders[i][6]);
						c.setBusy(false);
						c.setCanWalk(true);
						container.stop();
					}
				}
				c.getPA().requestUpdates();
				c.setBusy(false);
				c.setCanWalk(true);
				container.stop();
			}

		}, 600);

		return true;
	}

	public static final int MAX_LADDERS = 100;
	public static int[][] ladders = new int[MAX_LADDERS][7];

	public static boolean loadGlobalLadders(String fileName) {
		String line = "";
		String token = "";
		String token2 = "";
		String token2_2 = "";
		String[] token3 = new String[10];
		boolean EndOfFile = false;
		BufferedReader objectFile = null;
		try {
			objectFile = new BufferedReader(new FileReader("./" + fileName));
		} catch (FileNotFoundException fileex) {
			Misc.println(fileName + ": file not found.");
			return false;
		}
		try {
			line = objectFile.readLine();
		} catch (IOException ioexception) {
			Misc.println(fileName + ": error loading file.");
			return false;
		}
		int ladder = 0;
		while (EndOfFile == false && line != null) {
			line = line.trim(); // remove ends " " space
			int spot = line.indexOf("="); // find the = sign
			if (spot > -1) { // if our = sign is there (not null)
				token = line.substring(0, spot); // cuts off all info after =
													// sign token now is
													// "ladder "
				token = token.trim(); // trims "ladder " to become "ladder"
				token2 = line.substring(spot + 1); // token2 becomes everything
													// after the = sign so
													// " 1755....."
				token2 = token2.trim(); // trims token2 "1755......"
				token2_2 = token2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token3 = token2_2.split("\t");
				if (token.equals("ladder")) {
					ladders[ladder][0] = Integer.parseInt(token3[0]);
					ladders[ladder][1] = Integer.parseInt(token3[1]);
					ladders[ladder][2] = Integer.parseInt(token3[2]);
					ladders[ladder][3] = Integer.parseInt(token3[3]);
					ladders[ladder][4] = Integer.parseInt(token3[4]);
					ladders[ladder][5] = Integer.parseInt(token3[5]);
					ladders[ladder][6] = Integer.parseInt(token3[6]);
					ladder++;
				}
			} else {
				if (line.equals("[End]")) {
					try {
						objectFile.close();
					} catch (IOException ioexception) {
					}
					return true;
				}
			}
			try {
				line = objectFile.readLine();
			} catch (IOException ioexception1) {
				EndOfFile = true;
			}
		}
		try {
			objectFile.close();
		} catch (IOException ioexception) {
		}
		return false;
	}

}
