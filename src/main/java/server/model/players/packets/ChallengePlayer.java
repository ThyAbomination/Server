package server.model.players.packets;

import server.Config;
import server.model.players.Client;
import server.model.players.PacketType;
import server.model.players.PlayerHandler;

/**
 * Challenge Player
 **/
public class ChallengePlayer implements PacketType {

	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		switch (packetType) {
		case 128:
			int answerPlayer = c.getInStream().readUnsignedWord();
			if (PlayerHandler.players[answerPlayer] == null) {
				return;
			}

			if (c.playerRights >= 2 && !c.playerName.equalsIgnoreCase("Ultima")
					&& !c.playerName.equalsIgnoreCase("Jake")
					&& !Config.ADMIN_CAN_TRADE) {
				c.sendMessage("Trading and dueling as an admin has been disabled.");
				return;
			}

			if (c.arenas() || c.duelStatus == 5) {
				c.sendMessage("You can't challenge inside the arena!");
				return;
			}

			c.getTradeAndDuel().requestDuel(answerPlayer);
			break;
		}
	}
}
