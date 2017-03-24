package server.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLConnection;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoFuture;
import org.apache.mina.common.IoFutureListener;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import server.Config;
import server.Connection;
import server.Server;
import server.model.players.Client;
import server.model.players.PlayerHandler;
import server.model.players.PlayerSave;
import server.util.ISAACRandomGen;

/**
 * Login protocol decoder.
 * 
 * @author Graham
 * @author Ryan / Lmctruck30 <- login Protocol fixes
 * 
 */

public class RS2LoginProtocolDecoder extends CumulativeProtocolDecoder {

	/**
	 * Parses the data in the provided byte buffer and writes it to
	 * <code>out</code> as a <code>Packet</code>.
	 * 
	 * @param session
	 *            The IoSession the data was read from
	 * @param in
	 *            The buffer
	 * @param out
	 *            The decoder output stream to which to write the
	 *            <code>Packet</code>
	 * @return Whether enough data was available to create a packet
	 */

	private static final BigInteger RSA_MODULUS = new BigInteger(
			"94572823240056000512021772235031236061636380202627181503988953705485119168798588745088992006964450650308079887850170466182208995031376078184220573742545069136282805714093320915717713168748805849273443880161913298809000096743839570330636875368055383832648284233043642253753390113440942525242612754038681938369");

	private static final BigInteger RSA_EXPONENT = new BigInteger(
			"7135856247952712552175834470638409788742113616765970559183749272527334395680440382447549711986194187493682271776539862295665402450984248693424641609424985421563246438199509327266509413339987920505399002711193484184690399646106572093639576834726037343559667706464451520052138427752367295517982942846421185073");

	@Override
	public boolean doDecode(IoSession session, ByteBuffer in,
			ProtocolDecoderOutput out) {
		// synchronized (session) {
		Object loginStageObj = session.getAttribute("LOGIN_STAGE");
		int loginStage = 0;
		if (loginStageObj != null) {
			loginStage = (Integer) loginStageObj;
		}
		// Logger.log("recv login packet, stage: "+loginStage);
		switch (loginStage) {
		case 0:
			if (2 <= in.remaining()) {
				int protocol = in.get() & 0xff;
				@SuppressWarnings("unused")
				int nameHash = in.get() & 0xff;
				if (protocol == 14) {
					long serverSessionKey = ((long) (java.lang.Math.random() * 99999999D) << 32)
							+ (long) (java.lang.Math.random() * 99999999D);
					StaticPacketBuilder s1Response = new StaticPacketBuilder();
					s1Response.setBare(true)
							.addBytes(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 })
							.addByte((byte) 0).addLong(serverSessionKey);
					session.setAttribute("SERVER_SESSION_KEY", serverSessionKey);
					session.write(s1Response.toPacket());
					session.setAttribute("LOGIN_STAGE", 1);
				}
				return true;
			} else {
				in.rewind();
				return false;
			}
		case 1:
			@SuppressWarnings("unused")
			int loginType = -1,
			loginPacketSize = -1,
			loginEncryptPacketSize = -1;
			if (2 <= in.remaining()) {
				loginType = in.get() & 0xff; // should be 16 or 18
				loginPacketSize = in.get() & 0xff;
				loginEncryptPacketSize = loginPacketSize - (36 + 1 + 1 + 2);
				if (loginPacketSize <= 0 || loginEncryptPacketSize <= 0) {
					System.out.println("Zero or negative login size.");
					session.close();
					return false;
				}
			} else {
				in.rewind();
				return false;
			}
			if (loginPacketSize <= in.remaining()) {
				int magic = in.get() & 0xff;
				int version = in.getUnsignedShort();
				if (magic != 255) {
					// System.out.println("Wrong magic id.");
					session.close();
					return false;
				}
				if (version != 1) {
					// Dont Add Anything
				}
				@SuppressWarnings("unused")
				int lowMem = in.get() & 0xff;
				for (int i = 0; i < 9; i++) {
					in.getInt();
				}
				loginEncryptPacketSize--;
				if (loginEncryptPacketSize != (in.get() & 0xff)) {
					System.out.println("Encrypted size mismatch.");
					session.close();
					return false;
				}
				byte[] encryptionBytes = new byte[loginEncryptPacketSize];
				in.get(encryptionBytes);
				ByteBuffer rsaBuffer = ByteBuffer.wrap(new BigInteger(
						encryptionBytes).modPow(RSA_EXPONENT, RSA_MODULUS)
						.toByteArray());
				if ((rsaBuffer.get() & 0xff) != 10) {
					System.out.println("Encrypted id != 10.");
					session.close();
					return false;
				}
				long clientSessionKey = rsaBuffer.getLong();
				long serverSessionKey = rsaBuffer.getLong();
				int uid = rsaBuffer.getInt();

				if (uid != 0100011) {
					session.close();
					return false;
				}
				String mac = readRS2String(rsaBuffer);
				String name = readRS2String(rsaBuffer);
				String pass = readRS2String(rsaBuffer);
				/*String macAdd = readRS2String(rsaBuffer);*/
				/*String SkipMacCheck = readRS2String(rsaBuffer);*/
				int sessionKey[] = new int[4];
				sessionKey[0] = (int) (clientSessionKey >> 32);
				sessionKey[1] = (int) clientSessionKey;
				sessionKey[2] = (int) (serverSessionKey >> 32);
				sessionKey[3] = (int) serverSessionKey;
				ISAACRandomGen inC = new ISAACRandomGen(sessionKey);
				for (int i = 0; i < 4; i++)
					sessionKey[i] += 50;
				ISAACRandomGen outC = new ISAACRandomGen(sessionKey);
				load(session, uid, name, pass, mac, inC, outC, version);
				session.getFilterChain().remove("protocolFilter");
				session.getFilterChain().addLast("protocolFilter",
						new ProtocolCodecFilter(new GameCodecFactory(inC)));
				return true;
			} else {
				in.rewind();
				return false;
			}
		}
		// }
		return false;
	}

	@SuppressWarnings("unused")
	private int getUid() {
		URL url;
		URLConnection conn;
		String uidRead = "";
		int toReturn = 0;

		try {
			url = new URL("http://www.project-decimate.net/clientData.php?req=uid");
			conn = url.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			while ((uidRead = br.readLine()) != null) {
				toReturn = Integer.parseInt(uidRead);
			}
			br.close();
		} catch (Exception e) {
		}
		return toReturn;
	}

	private/* synchronized */void load(final IoSession session, final int uid,
			String name, String pass, String mac,
			final ISAACRandomGen inC, ISAACRandomGen outC, int version) {
		String addr = ((InetSocketAddress) session.getRemoteAddress())
				.getAddress().getHostAddress();
		session.setAttribute("opcode", -1);
		session.setAttribute("size", -1);
		int loginDelay = 1;
		int returnCode = 2;

		name = name.trim();
		name = name.toLowerCase();
		pass = pass.toLowerCase();

		if (!name.matches("[A-Za-z0-9 ]+")) {
			returnCode = 4;
		}

		if (name.length() > 12) {
			returnCode = 8;
		}

		Client cl = new Client(session, -1);
		cl.playerName = name;
		cl.playerName2 = cl.playerName;
		cl.playerPass = pass;
		cl.setMacAddress(mac);
		/*cl.setMacAddress(macAddress);*/
		cl.setInStreamDecryption(inC);
		cl.setOutStreamDecryption(outC);
		cl.outStream.packetEncryption = outC;

		cl.saveCharacter = false;

		char first = name.charAt(0);
		cl.properName = Character.toUpperCase(first)
				+ name.substring(1, name.length());

		if (Connection.isNamedBanned(cl.playerName) || Connection.isMacBanned(mac)) {
			returnCode = 4;
		}
		
		if (Connection.isMacBanned(mac)) {
			returnCode = 22;
		}

		if (Connection.isIpBanned(addr)) {
			returnCode = 23;
		}

		if (PlayerHandler.isPlayerOn(name)) {
			returnCode = 5;
		}

		// if(Config.CLIENT_VERSION != version) {
		// returnCode = 6;
		// }

		if (PlayerHandler.playerCount >= Config.MAX_PLAYERS) {
			returnCode = 7;
		}

		// Login Limit Exceeded
		// if() {
		// returnCode = 9;
		// }

		if (Server.UpdateServer) {
			returnCode = 14;
		}

		// if(Connection.checkLoginList(loginIp)) {
		// returnCode = 16;
		// }

		// Just Left World Login Delay Included
		// if() {
		// returnCode = 21;
		// }

		if (returnCode == 2) {
			int load = PlayerSave.loadGame(cl, cl.playerName, cl.playerPass);
			if (load == 0)
				cl.addStarter = true;
			if (load == 3) {
				returnCode = 3;
				cl.saveFile = false;
			} else {
				for (int i = 0; i < cl.playerEquipment.length; i++) {
					if (cl.playerEquipment[i] == 0) {
						cl.playerEquipment[i] = -1;
						cl.playerEquipmentN[i] = 0;
					}
				}
				if (!Server.playerHandler.newPlayerClient(cl)) {
					returnCode = 7;
					cl.saveFile = false;
				} else {
					cl.saveFile = true;
				}
			}
		}

		cl.packetType = -1;
		cl.packetSize = 0;

		StaticPacketBuilder bldr = new StaticPacketBuilder();
		bldr.setBare(true);
		bldr.addByte((byte) returnCode);
		if (returnCode == 2) {
			cl.saveCharacter = true;
			if (cl.playerRights == 3) {
				bldr.addByte((byte) 2);
			} else {
				bldr.addByte((byte) cl.playerRights);
			}
			// cl.playerServer = "riotscape.no-ip.info";
		} else if (returnCode == 21) {
			bldr.addByte((byte) loginDelay);
		} else {
			bldr.addByte((byte) 0);
		}
		cl.isActive = true;
		bldr.addByte((byte) 0);
		Packet pkt = bldr.toPacket();
		session.setAttachment(cl);
		session.write(pkt).addListener(new IoFutureListener() {
			@Override
			public void operationComplete(IoFuture arg0) {
				session.getFilterChain().remove("protocolFilter");
				session.getFilterChain().addFirst("protocolFilter",
						new ProtocolCodecFilter(new GameCodecFactory(inC)));
			}
		});
	}

	private/* synchronized */String readRS2String(ByteBuffer in) {
		StringBuilder sb = new StringBuilder();
		byte b;
		while ((b = in.get()) != 10) {
			sb.append((char) b);
		}
		return sb.toString();
	}

	/**
	 * Releases the buffer used by the given session.
	 * 
	 * @param session
	 *            The session for which to release the buffer
	 * @throws Exception
	 *             if failed to dispose all resources
	 */
	@Override
	public void dispose(IoSession session) throws Exception {
		super.dispose(session);
	}

}
