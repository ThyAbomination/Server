package server.model.players.packets;

import server.model.players.Client;
import server.model.players.PacketType;

/**
 * Bank X Items
 **/
public class BankX2 implements PacketType {
	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		int Xamount = c.getInStream().readDWord();
		if (Xamount < 0)// this should work fine
		{
			Xamount = c.getItems().getItemAmount(c.xRemoveId);
		}
		if (Xamount == 0) {
			Xamount = 1;
		}
		if (c.buyingX) {
			if (Xamount <= 1000) {
				c.getShops().buyItem(c.xRemoveId, c.xRemoveSlot, Xamount);
			} else {
				c.sendMessage("You cannot buy more than 1000 at a time.");
			}
			c.xRemoveSlot = 0;
			c.xInterfaceId = 0;
			c.xRemoveId = 0;
			c.buyingX = false;
		}
		switch (c.xInterfaceId) {
		case 5064:
			if(c.isBankX()) {
				c.getItems().bankItem(c.playerItems[c.xRemoveSlot], c.xRemoveSlot,
					Xamount);
				c.setBankX(false);
			}
			break;

		case 7423:
			if (c.storing) {
				return;
			}
			c.getItems().bankItem(c.playerItems[c.xRemoveSlot], c.xRemoveSlot,
					Xamount);
			c.getItems().resetItems(7423);
			break;

		case 5382:
			c.getItems().fromBank(c.bankItems[c.xRemoveSlot], c.xRemoveSlot,
					Xamount);
			break;

		case 3322:
			if (!c.getItems().playerHasItem(c.xRemoveId, Xamount))
				return;
			if (c.duelStatus <= 0) {
				c.getTradeAndDuel().tradeItem(c.xRemoveId, c.xRemoveSlot,
						Xamount);
			} else {
				c.getTradeAndDuel().stakeItem(c.xRemoveId, c.xRemoveSlot,
						Xamount);
			}
			break;

		case 3415:
			if (!c.getItems().playerHasItem(c.xRemoveId, Xamount))
				return;
			if (c.duelStatus <= 0) {
				c.getTradeAndDuel().fromTrade(c.xRemoveId, c.xRemoveSlot,
						Xamount);
			}
			break;

		case 6669:
			if (!c.getItems().playerHasItem(c.xRemoveId, Xamount))
				return;
			c.getTradeAndDuel().fromDuel(c.xRemoveId, c.xRemoveSlot, Xamount);
			break;
		}
	}
}