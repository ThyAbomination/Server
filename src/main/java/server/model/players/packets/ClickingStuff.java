package server.model.players.packets;

import server.model.players.Client;
import server.model.players.PacketType;
import server.model.players.PlayerHandler;
import server.util.Misc;

/**
 * Clicking stuff (interfaces)
 **/
public class ClickingStuff implements PacketType {

	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		if (c.inTrade && !c.acceptedTrade) {
			Misc.println("trade reset");
			c.getTradeAndDuel().declineTrade();
		}
		if(c.isBanking) {
			c.setBankX(false);
			c.isBanking = false;
		}
		if (c.inTrade) {
			if (!c.acceptedTrade) {
				Client o = (Client) PlayerHandler.players[c.tradeWith];
				o.tradeAccepted = false;
				c.tradeAccepted = false;
				o.tradeStatus = 0;
				c.tradeStatus = 0;
				c.tradeConfirmed = false;
				c.tradeConfirmed2 = false;
				c.sendMessage("@red@Trade has been declined.");
				o.sendMessage("@red@Other player has declined the trade.");
				Misc.println("trade reset");
				c.getTradeAndDuel().declineTrade();
			}
		}
		if (c.storing)
			c.storing = false;
		if (c.isShopping)
			c.isShopping = false;

		Client o = (Client) PlayerHandler.players[c.duelingWith];
		if (o != null) {
			if (c.duelStatus >= 1 && c.duelStatus <= 4) {
				c.getTradeAndDuel().declineDuel();
				o.getTradeAndDuel().declineDuel();
			}
		}

		if (c.duelStatus == 6) {
			c.getTradeAndDuel().claimStakedItems();
		}

	}

}
