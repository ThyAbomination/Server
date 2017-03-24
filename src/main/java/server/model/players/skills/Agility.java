package server.model.players.skills;

import server.Server;
import server.model.players.Client;
import server.model.players.Player;
import server.task.Task;

public class Agility {

	private Client c;

	public Agility(Client c) {
		this.c = c;
	}

	public void walk(int EndX, int EndY, int Emote) {
		walkToEmote(Emote);
		c.getPA().walkTo2(EndX, EndY);
	}

	public void walkToEmote(int id) {
		c.isRunning2 = false;
		c.getPA().sendFrame36(504, 1);
		c.getPA().sendFrame36(173, 1);
		c.playerWalkIndex = id;
		c.getPA().requestUpdates();
	}

	public void stopEmote() {
		c.playerWalkIndex = 0x333;
		c.agilityEmote = false;
		c.getPA().requestUpdates();
	}

	public void obsticle(int Emote, int newX, int newY, final int agilityTimer,
			int amtEXP) {
		c.agilityEmote = true;
		walk(newX, newY, Emote);
		c.getPA().addSkillXP(amtEXP, Player.playerAgility);
		int timer = (int) Math.floor(agilityTimer / 600);
		Server.getTaskScheduler().addEvent(new Task(timer, false) {
			@Override
			public void execute() {
				stopEmote();
				this.stop();
			}
		});
	}

	public void agilityDelay(int Emote, final int X, final int Y, final int H,
			int Req, int amtEXP) {
		c.startAnimation(Emote);
		c.agilityEmote = true;
		c.getPA().addSkillXP(amtEXP, Player.playerAgility);
		Server.getTaskScheduler().addEvent(new Task(2, false) {
			@Override
			public void execute() {
				c.getPA().movePlayer(X, Y, H);
				c.agilityEmote = false;
				this.stop();
			}
		});
	}

}