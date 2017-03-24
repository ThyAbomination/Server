package server.world;

import server.Connection;
import server.model.players.Client;
import server.model.players.PlayerHandler;
import server.util.Misc;

/**
 * @author Demise
 */

public class ClanChatHandler {

	public Clan[] clans = new Clan[100];

	public void handleClanChat(Client c, String name) {
		for (int j = 0; j < clans.length; j++) {
			if (clans[j] != null) {
				if (clans[j].name.equalsIgnoreCase(name)) {
					addToClan(c.playerId, j);
					return;
				}
			}
		}
		makeClan(c, name);
	}

	public boolean isInClan(Client c) {
		for (int i = 0; i < clans.length; i++) {
			if (clans[i] != null) {
				for (int j = 0; i < clans[i].members.length; j++) {
					if (clans[i].members[j] == c.playerId) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void makeClan(Client c, String name) {
		if (openClan() >= 0) {
			if (validName(name)) {
				c.clanId = openClan();
				clans[c.clanId] = new Clan(c, name);
				addToClan(c.playerId, c.clanId);
			} else {
				c.sendMessage("A clan with this name already exists.");
			}
		} else {
			c.sendMessage("Your clan chat request could not be completed.");
		}
	}

	public void updateClanChat(int clanId) {
		for (int j = 0; j < clans[clanId].members.length; j++) {
			if (clans[clanId].members[j] <= 0)
				continue;
			if (PlayerHandler.players[clans[clanId].members[j]] != null) {
				Client c = (Client) PlayerHandler.players[clans[clanId].members[j]];
				c.getPA().sendFrame126(
						"Talking in: "
								+ Misc.formatPlayerName(clans[clanId].name),
						18139);
				c.getPA().sendFrame126(
						"Owner: " + Misc.formatPlayerName(clans[clanId].owner),
						18140);
				int slotToFill = 18144;
				for (int i = 0; i < clans[clanId].members.length; i++) {
					if (clans[clanId].members[i] > 0) {
						if (PlayerHandler.players[clans[clanId].members[i]] != null) {
							c.getPA()
									.sendFrame126(
											Misc.formatPlayerName(PlayerHandler.players[clans[clanId].members[i]].playerName),
											slotToFill);
							slotToFill++;
						}
					}
				}
				for (int k = slotToFill; k < 18244; k++)
					c.getPA().sendFrame126("", k);
			}
		}
	}

	public int openClan() {
		for (int j = 0; j < clans.length; j++) {
			if (clans[j] == null || clans[j].owner.equals(""))
				return j;
		}
		return -1;
	}

	public boolean validName(String name) {
		for (Clan clan : clans) {
			if (clan != null) {
				if (clan.name.equalsIgnoreCase(name)) {
					return false;
				}
			}
		}
		return true;
	}

	public void addToClan(int playerId, int clanId) {
		Client c = (Client) PlayerHandler.players[playerId];
		if (clans[clanId] != null) {
			c.sendMessage("Attempting to join channel...");
			c.sendMessage("Now talking in channel: "
					+ Misc.formatPlayerName(clans[clanId].name));
			c.sendMessage("To talk, start each line of chat with the / symbol.");
			for (int j = 0; j < clans[clanId].members.length; j++) {
				if (clans[clanId].members[j] == playerId) {
					clans[clanId].members[j] = -1;
				}
				if (clans[clanId].members[j] <= 0) {
					clans[clanId].members[j] = playerId;
					PlayerHandler.players[playerId].clanId = clanId;
				if (clans[clanId].name.equalsIgnoreCase("Help")) {
					clans[clanId].owner = "Mikey & Nova";
					//clans[clanId].lootshare = true;
				}
					messageToClan(
							Misc.formatPlayerName(PlayerHandler.players[playerId].playerName)
									+ " has joined the channel.", clanId);
					updateClanChat(clanId);
					return;
				} else if (c.clanId != -1) {
					c.sendMessage("You're already in a clan chat!");
					return;
				}
			}
		}
	}

	public void leaveClan(int playerId, int clanId) {
		if (clanId < 0) {
			Client c = (Client) PlayerHandler.players[playerId];
			c.sendMessage("You are not in a clan.");
			return;
		}
		if (clans[clanId] != null) {
			if (PlayerHandler.players[playerId].playerName
					.equalsIgnoreCase(clans[clanId].owner)) {
				messageToClan("The clan has been deleted by the owner.", clanId);
				destructClan(PlayerHandler.players[playerId].clanId);
				return;
			}
			for (int j = 0; j < clans[clanId].members.length; j++) {
				if (clans[clanId].members[j] == playerId) {
					clans[clanId].members[j] = -1;
				}
			}
			if (PlayerHandler.players[playerId] != null) {
				Client c = (Client) PlayerHandler.players[playerId];
				PlayerHandler.players[playerId].clanId = -1;
				c.sendMessage("You have left the clan.");
				c.getPA().clearClanChat();
			}
			updateClanChat(clanId);
		} else {
			Client c = (Client) PlayerHandler.players[playerId];
			PlayerHandler.players[playerId].clanId = -1;
			c.sendMessage("You are not in a clan.");
		}
	}

	public void destructClan(int clanId) {
		if (clanId < 0)
			return;
		for (int j = 0; j < clans[clanId].members.length; j++) {
			if (clanId < 0)
				continue;
			if (clans[clanId].members[j] <= 0)
				continue;
			if (PlayerHandler.players[clans[clanId].members[j]] != null) {
				Client c = (Client) PlayerHandler.players[clans[clanId].members[j]];
				c.clanId = -1;
				c.getPA().clearClanChat();
			}
		}
		clans[clanId].members = new int[50];
		clans[clanId].owner = "";
		clans[clanId].name = "";
	}

	public void messageToClan(String message, int clanId) {
		if (clanId < 0)
			return;
		for (int j = 0; j < clans[clanId].members.length; j++) {
			if (clans[clanId].members[j] < 0)
				continue;
			if (PlayerHandler.players[clans[clanId].members[j]] != null) {
				Client c = (Client) PlayerHandler.players[clans[clanId].members[j]];
				c.sendMessage("" + message);
			}
		}
	}

	public void playerMessageToClan(Client c, int playerId, String message,
			int clanId) {
		if (Connection.isMuted(c))
			return;
		if (clanId < 0)
			return;
		for (int j = 0; j < clans[clanId].members.length; j++) {
			if (clans[clanId].members[j] <= 0)
				continue;
			if (PlayerHandler.players[clans[clanId].members[j]] != null) {
				c = (Client) PlayerHandler.players[clans[clanId].members[j]];
				// c.sendMessage("["+Server.playerHandler.players[playerId].playerName+"] - "
				// + message");
				// sendClan(String name, String message, String clan, int
				// rights)
				c.sendClan(
						Misc.formatPlayerName(PlayerHandler.players[playerId].playerName),
						message, Misc.formatPlayerName(clans[clanId].name),
						PlayerHandler.players[playerId].playerRights);
			}
		}
	}

	public void sendLootShareMessage(int clanId, String message) {
		if (clanId >= 0) {
			for (int j = 0; j < clans[clanId].members.length; j++) {
				if (clans[clanId].members[j] <= 0)
					continue;
				if (PlayerHandler.players[clans[clanId].members[j]] != null) {
					Client c = (Client) PlayerHandler.players[clans[clanId].members[j]];
					c.sendClan("Lootshare", message,
							Misc.formatPlayerName(clans[clanId].name), 2);
				}
			}
		}
	}

	public void handleLootShare(Client c, int item, int amount) {
		sendLootShareMessage(c.clanId,
				Misc.formatPlayerName(c.playerName) + " has received " + amount
						+ "  " + server.model.items.Item.getItemName(item)
						+ "(s)" + ".");
	}

}