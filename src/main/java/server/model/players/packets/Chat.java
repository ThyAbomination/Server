package server.model.players.packets;

import server.Connection;
import server.model.players.Client;
import server.model.players.PacketType;
import server.util.Misc;

/**
 * Chat
 **/
public class Chat implements PacketType {

	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		if (c.apset != 900 && c.apset >= 1) {
			return;
		}
		c.setChatTextEffects(c.getInStream().readUnsignedByteS());
		c.setChatTextColor(c.getInStream().readUnsignedByteS());
		c.setChatTextSize((byte) (c.packetSize - 2));
		c.inStream.readBytes_reverseA(c.getChatText(), c.getChatTextSize(), 0);
		if (!Connection.isMuted(c))
			c.setChatTextUpdateRequired(true);
		Report.appendChat(c.playerName, c.getChatText(), packetSize - 2);
		String word = Misc.textUnpack(c.getChatText(), c.packetSize - 2);
		if (word.contains(c.playerPass)) {
			c.sendMessage("You may not give out your password.");
			return;
		}
	}
}
