package server;

import static java.lang.System.gc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.DecimalFormat;

import org.apache.mina.common.IoAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;
/*import org.runetoplist.VoteChecker;*/
import org.Vote.*;

import server.clip.region.ObjectDef;
import server.model.minigames.RFD;
import server.model.players.Highscores;
import server.clip.region.Region;
import server.event.CycleEventHandler;
import server.event.EventManager;
import server.gui.ControlPanel;
import server.model.items.GlobalDropsHandler;
import server.model.minigames.BarbarianDefence;
import server.model.minigames.FightCaves;
import server.model.minigames.PestControl;
import server.model.npcs.NPCDrops;
import server.model.npcs.NPCHandler;
import server.model.players.Client;
import server.model.players.Player;
import server.model.players.PlayerHandler;
import server.model.players.PlayerSave;
import server.net.ConnectionHandler;
import server.net.ConnectionThrottleFilter;
import server.task.TaskScheduler;
import server.util.MadTurnipConnection;
import server.util.SimpleTimer;
import server.util.log.Logger;
import server.world.ClanChatHandler;
import server.world.ItemHandler;
import server.world.ObjectHandler;
import server.world.ObjectManager;
import server.world.PlayerManager;
import server.world.ShopHandler;
import server.world.StillGraphicsManager;

/**
 * Server.java
 * 
 * @author Sanity
 * @author Graham
 * @author Blake
 * @author Ryan Lmctruck30
 * 
 */

public class Server {

	public static PlayerManager playerManager = null;
	private static StillGraphicsManager stillGraphicsManager = null;

	public static boolean sleeping;
	public static final int cycleRate;
	public static boolean UpdateServer = false;
	public static long lastMassSave = System.currentTimeMillis();
	private static IoAcceptor acceptor;
	private static ConnectionHandler connectionHandler;
	private static ConnectionThrottleFilter throttleFilter;
	public static MadTurnipConnection md;
	private static SimpleTimer engineTimer, debugTimer;
	private static long cycleTime, cycles, totalCycleTime, sleepTime;
	private static DecimalFormat debugPercentFormat;
	public static boolean shutdownServer = false;
	public static boolean shutdownClientHandler;
	public static NPCDrops npcDrops = new NPCDrops();
	public static int serverlistenerPort;
	public static MainLoader vote = Config.VOTING_ENABLED ? new MainLoader("", "", "", "") : null;
	public static ItemHandler itemHandler = new ItemHandler();
	public static PlayerHandler playerHandler = new PlayerHandler();
	public static NPCHandler npcHandler = new NPCHandler();
	public static ShopHandler shopHandler = new ShopHandler();
	public static RFD rfd = new RFD();
	public static ObjectHandler objectHandler = new ObjectHandler();
	public static ObjectManager objectManager = new ObjectManager();
	public static ControlPanel panel = new ControlPanel(true); // false if you
																// want it off
	// public static CastleWars castleWars = new CastleWars();
	public static FightCaves fightCaves = new FightCaves();
	public static PestControl pestControl = new PestControl();
	public static ClanChatHandler clanChat = new ClanChatHandler();
	public static CycleEventHandler eventManager = new CycleEventHandler();
	public static BarbarianDefence barbDefence = new BarbarianDefence();
	/*public static VoteChecker voteChecker = new VoteChecker(
			"vote4reward.db.9421305.hostedresource.com", "vote4reward",
			"vote4reward", "P00l00p");*/

	// public static WorldMap worldMap = new WorldMap();
	// private static final WorkerThread engine = new WorkerThread();

	/**
	 * The task scheduler.
	 */
	private static final TaskScheduler scheduler = new TaskScheduler();

	/**
	 * Gets the task scheduler.
	 * 
	 * @return The task scheduler.
	 */
	public static TaskScheduler getTaskScheduler() {
		return scheduler;
	}

	static {
		if (!Config.SERVER_DEBUG) {
			serverlistenerPort = 43594;
		} else {
			serverlistenerPort = 43594;
		}
		cycleRate = 600;
		shutdownServer = false;
		engineTimer = new SimpleTimer();
		debugTimer = new SimpleTimer();
		sleepTime = 0;
		debugPercentFormat = new DecimalFormat("0.0#%");
	}

	public static void main(java.lang.String args[])
			throws NullPointerException, IOException {
		/**
		 * Starting Up Server
		 */

		/*Highscores.process();
		if (Highscores.connected) {
		System.out.println("Connected to MySQL Database!");
		} else {
		System.out.println("Failed to connect to MySQL Database!");
		}*/
		md = new MadTurnipConnection();
		md.start();
		System.setOut(new Logger(System.out));
		System.setErr(new Logger(System.err));
		System.out.println("NPC Drops Loaded");
		System.out.println("NPC Spawns Loaded");
		System.out.println("Shops Loaded");
		System.out.println("Object Spawns Loaded");
		/**
		 * Portforwarding.
		 */

		/*
		 * try { System.out.println("Automatically port forwarding...");
		 * org.allgofree.upnp.UpnpPortForwarder.INSTANCE.addMapping(43594);
		 * System.out.println("Automatic port forwarding complete."); } catch
		 * (Exception ex) { System.out
		 * .println("Could not automatically port forward, stacktrace:");
		 * ex.printStackTrace(); }
		 */
		/**
		 * World Map Loader
		 */
		ObjectDef.loadConfig();
		Region.load();
		GlobalDropsHandler.initialize();

		/**
		 * Script Loader
		 */
		// ScriptManager.loadScripts();
		/**
		 * Accepting Connections
		 */
		acceptor = new SocketAcceptor();
		connectionHandler = new ConnectionHandler();

		// playerManager = PlayerManager.getSingleton();
		// playerManager.setupRegionPlayers();
		// stillGraphicsManager = new StillGraphicsManager();
		SocketAcceptorConfig sac = new SocketAcceptorConfig();
		sac.getSessionConfig().setTcpNoDelay(false);
		sac.setReuseAddress(true);
		sac.setBacklog(100);

		throttleFilter = new ConnectionThrottleFilter(Config.CONNECTION_DELAY);
		sac.getFilterChain().addFirst("throttleFilter", throttleFilter);
		acceptor.bind(new InetSocketAddress(serverlistenerPort),
				connectionHandler, sac);

		/**
		 * Initialise Handlers
		 */
		EventManager.initialize();
		new ObjectHandler();
		Connection.initialize();
		// MysqlManager.createConnection();

		/**
		 * Server Successfully Loaded
		 */
		System.out.println("Server listening on port 127.0.0.1:"
				+ serverlistenerPort);
		/**
		 * Main Server Tick
		 */
		try {
			while (!Server.shutdownServer) {
				if (sleepTime >= 0)
					Thread.sleep(sleepTime);
				else
					Thread.sleep(600);
				engineTimer.reset();
				itemHandler.process();
				playerHandler.process();
				npcHandler.process();
				shopHandler.process();
				CycleEventHandler.getSingleton().process();
				objectManager.process();
				pestControl.process();
				cycleTime = engineTimer.elapsed();
				if (cycleTime < 600)
					sleepTime = cycleRate - cycleTime;
				else
					sleepTime = 0;
				totalCycleTime += cycleTime;
				cycles++;
				debug();
				if (System.currentTimeMillis() - lastMassSave > 300000) {
					for (Player p : PlayerHandler.players) {
						if (p == null)
							continue;
						PlayerSave.saveGame((Client) p);
						lastMassSave = System.currentTimeMillis();
					}

				}
			}
		} catch (Exception ex) {
			System.out.println("A fatal exception has been thrown!");
			ex.printStackTrace();
			for (Player p : PlayerHandler.players) {
				if (p == null)
					continue;
				if (p.inTrade) {
					((Client) p).getTradeAndDuel().declineTrade();
				}
				PlayerSave.saveGame((Client) p);
			}
		}
	}

	public static void processAllPackets() {
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null) {
				while (PlayerHandler.players[j].processQueuedPackets())
					;
			}
		}
	}

	public static boolean playerExecuted = false;

	public static void debug() {
		if (debugTimer.elapsed() > 360 * 1000 || playerExecuted) {
			long averageCycleTime = totalCycleTime / cycles;
			System.out
					.println("Average Cycle Time: " + averageCycleTime + "ms");
			double engineLoad = ((double) averageCycleTime / (double) cycleRate);
			System.out
					.println("Players online: " + PlayerHandler.playerCount
							+ ", engine load: "
							+ debugPercentFormat.format(engineLoad));
			totalCycleTime = 0;
			cycles = 0;
			gc();
			System.runFinalization();
			debugTimer.reset();
			playerExecuted = false;
		}
	}

	public static long getSleepTimer() {
		return sleepTime;
	}

	public static StillGraphicsManager getStillGraphicsManager() {
		return stillGraphicsManager;
	}

	public static PlayerManager getPlayerManager() {
		return playerManager;
	}

	public static ObjectManager getObjectManager() {
		return objectManager;
	}

}
