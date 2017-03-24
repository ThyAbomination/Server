package server.net;

import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;

import server.model.players.Client;

public class ConnectionHandler implements IoHandler {

	@Override
	public void exceptionCaught(IoSession arg0, Throwable arg1)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageReceived(IoSession arg0, Object arg1) throws Exception {
		if (arg0.getAttachment() != null) {
			Packet packet = (Packet) arg1;
			Client client = (Client) arg0.getAttachment();
			if (packet.getId() == 41) {
				client.timeOutCounter = 0;
				client.wearId = packet.readUnsignedWord();
				client.wearSlot = packet.readUnsignedWordA();
				client.interfaceId = packet.readUnsignedWordA();
				client.getItems().wearItem(client.wearId, client.wearSlot);
			}

			else if (packet.getId() == 145) {
				client.timeOutCounter = 0;
				int interfaceId = packet.readUnsignedWordA();
				int removeSlot = packet.readUnsignedWordA();
				int removeId = packet.readUnsignedWordA();
				switch (interfaceId) {

				case 1688:
					client.getItems().removeItem(removeId, removeSlot);
					break;

				case 5064:
					client.getItems().bankItem(removeId, removeSlot, 1);
					break;
				case 7423:
					if (client.inTrade) {
						client.getTradeAndDuel().declineTrade(true);
						return;
					}
					client.getItems().bankItem(removeId, removeSlot, 1);
					client.getItems().resetItems(7423);
					break;
				case 5382:
					client.getItems().fromBank(removeId, removeSlot, 1);
					break;

				case 3900:
					client.getShops().buyFromShopPrice(removeId, removeSlot);
					break;

				case 3823:
					client.getShops().sellToShopPrice(removeId, removeSlot);
					break;

				case 3322:
					if (client.duelStatus <= 0) {
						client.getTradeAndDuel().tradeItem(removeId,
								removeSlot, 1);
					} else {
						client.getTradeAndDuel().stakeItem(removeId,
								removeSlot, 1);
					}
					break;

				case 3415:
					if (client.duelStatus <= 0) {
						client.getTradeAndDuel().fromTrade(removeId,
								removeSlot, 1);
					}
					break;

				case 6669:
					client.getTradeAndDuel().fromDuel(removeId, removeSlot, 1);
					break;

				}
			} else {
				client.queueMessage((Packet) arg1);
			}
		}
	}

	@Override
	public void messageSent(IoSession arg0, Object arg1) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void sessionClosed(IoSession arg0) throws Exception {
		if (arg0.getAttachment() != null) {
			Client plr = (Client) arg0.getAttachment();
			plr.disconnected = true;
		}
		HostList.getHostList().remove(arg0);
	}

	@Override
	public void sessionCreated(IoSession arg0) throws Exception {
		if (!HostList.getHostList().add(arg0)) {
			arg0.close();
		} else {
			arg0.setAttribute("inList", Boolean.TRUE);
		}
	}

	@Override
	public void sessionIdle(IoSession arg0, IdleStatus arg1) throws Exception {
		arg0.close();
	}

	@Override
	public void sessionOpened(IoSession arg0) throws Exception {
		arg0.setIdleTime(IdleStatus.BOTH_IDLE, 60);
		arg0.getFilterChain().addLast("protocolFilter",
				new ProtocolCodecFilter(new CodecFactory()));
	}

}
