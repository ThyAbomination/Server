package server.model.players.packets;

import server.model.players.Client;
import server.model.players.PacketType;

/**
 * Wear Item
 **/
public class WearItem implements PacketType {

	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		c.wearId = c.getInStream().readUnsignedWord();
		c.wearSlot = c.getInStream().readUnsignedWordA();
		c.interfaceId = c.getInStream().readUnsignedWordA();
		if (!c.getItems().playerHasItem(c.wearId, 1, c.wearSlot)) {
			return;
		}
		if (c.playerIndex > 0 || c.npcIndex > 0)
			c.getCombat().resetPlayerAttack();
		c.getItems().wearItem(c.wearId, c.wearSlot);
	}
}
