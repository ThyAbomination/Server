package server.model.players.packets;

import server.model.players.Client;
import server.model.players.PacketType;

/**
 * Bank X Items
 **/
public class BankX1 implements PacketType {

	public static final int PART1 = 135;
	public static final int PART2 = 208;
	public int XremoveSlot, XinterfaceID, XremoveID, Xamount;

	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		if (packetType == 135) {
			c.xRemoveSlot = c.getInStream().readSignedWordBigEndian();
			c.xInterfaceId = c.getInStream().readUnsignedWordA();
			c.xRemoveId = c.getInStream().readSignedWordBigEndian();
		}
		if(c.xInterfaceId == 5064) {
			c.setBankX(true);
		}
		if (c.xInterfaceId == 3900) {
			c.buyingX = true;
			c.outStream.createFrame(27);
			return;
		} else {
			if (c.xInterfaceId == 7423) {
				c.getItems().bankItem(c.xRemoveId, c.xRemoveSlot, Xamount);// Depo
																			// 1
				c.getItems().resetItems(7423);
			}
		}

		if (packetType == PART1) {
			synchronized (c) {
				c.getOutStream().createFrame(27);
			}
		}

	}
}
