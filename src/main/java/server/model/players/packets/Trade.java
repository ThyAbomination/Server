package server.model.players.packets;

import server.Config;
import server.model.players.Client;
import server.model.players.PacketType;

/**
 * Trading
 */
public class Trade implements PacketType {

	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		int tradeId = c.getInStream().readSignedWordBigEndian();
		c.getPA().resetFollow();

		if (c.arenas()) {
			c.sendMessage("You can't trade inside the arena!");
			return;
		}

		if (c.playerRights >= 2 && !c.playerName.equalsIgnoreCase("Ultima")
				&& !c.playerName.equalsIgnoreCase("Avenger")
				&& !Config.ADMIN_CAN_TRADE) {
			c.sendMessage("Trading and dueling as an admin has been disabled.");
			return;
		}
		if (tradeId != c.playerId)
			c.getTradeAndDuel().requestTrade(tradeId);
	}

}
