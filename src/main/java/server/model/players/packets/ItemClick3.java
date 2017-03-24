package server.model.players.packets;

import server.model.players.Client;
import server.model.players.PacketType;
import server.util.Misc;

/**
 * Item Click 3 Or Alternative Item Option 1
 * 
 * @author Ryan / Lmctruck30
 * 
 *         Proper Streams
 */

public class ItemClick3 implements PacketType {

	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		int itemId11 = c.getInStream().readSignedWordBigEndianA();
		int itemId1 = c.getInStream().readSignedWordA();
		int itemId = c.getInStream().readSignedWordA();
		// Teleport.handleDuelRing(c, itemId);
		// Teleport.handleGlory(c, itemId);
		switch (itemId) {

		case 15086:
			c.getItems().deleteItem(15086, 1);
			c.getItems().addItem(15084, 1);
			c.sendMessage("You put away the dies from the dice bag.");
			break;

		case 15088:
			c.getItems().deleteItem(15088, 1);
			c.getItems().addItem(15084, 1);
			c.sendMessage("You put away the dies from the dice bag.");
			break;

		case 15090:
			c.getItems().deleteItem(15090, 1);
			c.getItems().addItem(15084, 1);
			c.sendMessage("You put away the dies from the dice bag.");
			break;

		case 15092:
			c.getItems().deleteItem(15092, 1);
			c.getItems().addItem(15084, 1);
			c.sendMessage("You put away the dies from the dice bag.");
			break;

		case 15094:
			c.getItems().deleteItem(15094, 1);
			c.getItems().addItem(15084, 1);
			c.sendMessage("You put away the dies from the dice bag.");
			break;

		case 15096:
			c.getItems().deleteItem(15096, 1);
			c.getItems().addItem(15084, 1);
			c.sendMessage("You put away the dies from the dice bag.");
			break;

		case 15098:
			c.getItems().deleteItem(15098, 1);
			c.getItems().addItem(15084, 1);
			c.sendMessage("You put away the dies from the dice bag.");
			break;

		case 15100:
			c.getItems().deleteItem(15100, 1);
			c.getItems().addItem(15084, 1);
			c.sendMessage("You put away the dies from the dice bag.");
			break;

		default:
			if (c.playerRights == 3)
				Misc.println(c.playerName + " - Item3rdOption: " + itemId
						+ " : " + itemId11 + " : " + itemId1);
			break;
		}

	}

}
