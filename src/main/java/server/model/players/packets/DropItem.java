package server.model.players.packets;

import server.Config;
import server.Server;
import server.model.players.Client;
import server.model.players.PacketType;

/**
 * Drop Item
 **/
public class DropItem implements PacketType {

	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		int itemId = c.getInStream().readUnsignedWordA();
		c.getInStream().readUnsignedByte();
		c.getInStream().readUnsignedByte();
		int slot = c.getInStream().readUnsignedWordA();
		c.alchDelay = System.currentTimeMillis();
		if (c.arenas()) {
			c.sendMessage("You can't drop items inside the arena!");
			return;
		}
		if (!c.getItems().playerHasItem(itemId, 1, slot)) {
			return;
		}
		if (c.inChallenge() || c.inBarbDef() || c.inSW() || c.inFightCaves()) {
			c.getItems().deleteItem(itemId, slot, c.playerItemsN[slot]);
			server.model.players.PlayerSave.saveGame(c);
		}

		boolean droppable = true;
		for (int i : Config.UNDROPPABLE_ITEMS) {
			if (i == itemId) {
				droppable = false;
				break;
			}
		}
		if (c.playerItemsN[slot] != 0 && itemId != -1
				&& c.playerItems[slot] == itemId + 1) {
			if (droppable) {
				if (c.underAttackBy > 0) {
					if (c.getShops().getItemShopValue(itemId) > 1000) {
						return;
					}
				}
				if (c.playerRights >= 2
						&& !c.playerName.equalsIgnoreCase("Ultima")
						&& !c.playerName.equalsIgnoreCase("Avenger")
						&& !Config.ADMIN_DROP_ITEMS) {
					c.sendMessage("Dropping items as an admin has been disabled.");
					return;
				}
				boolean destroyable = false;
				for (int i : Config.DESTROYABLES) {
					if (i == itemId) {
						destroyable = true;
						break;
					}
				}
				if (destroyable) {
					c.getPA().destroyInterface(itemId);
					return;
				}
				Server.itemHandler.createGroundItem(c, itemId, c.getX(),
						c.getY(), c.playerItemsN[slot], c.getId());
				c.getItems().deleteItem(itemId, slot, c.playerItemsN[slot]);
				server.model.players.PlayerSave.saveGame(c);
			} else {
				c.sendMessage("This item cannot be dropped.");
			}
		}

	}
}
