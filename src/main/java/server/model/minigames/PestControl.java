package server.model.minigames;

import java.util.HashMap;

import server.Server;
import server.model.npcs.NPCHandler;
import server.model.players.Client;
import server.model.players.Player;
import server.util.Misc;

/**
 * @author Harlan Credits to Sanity
 */

public class PestControl {

	/**
	 * how long before were put into the game from lobby
	 */
	private static final int WAIT_TIMER = 30;
	/**
	 * How many players we need to start a game
	 */
	private final static int PLAYERS_REQUIRED = 1;
	/**
	 * Hashmap for the players in lobby
	 */
	public static HashMap<Client, Integer> waitingBoat = new HashMap<Client, Integer>();
	private static HashMap<Client, Integer> gamePlayers = new HashMap<Client, Integer>();

	private int gameTimer = -1;
	private static int waitTimer = 20;
	public static boolean gameStarted = false;
	private int properTimer = 0;
	public static int KNIGHTS_HEALTH = -1;

	/**
	 * Array used for storing the portals health
	 */
	public static int[] portalHealth = { 200, 200, 200, 200 };
	public static int[] portals = { 6142, 6143, 6144, 6145 };
	/**
	 * array used for storing the npcs used in the minigame
	 * 
	 * @order npcId, xSpawn, ySpawn, health
	 */
	public int shifter = 3732 + Misc.random(9);
	public int brawler = 3772 + Misc.random(4);
	public int defiler = 3762 + Misc.random(9);
	public int ravager = 3742 + Misc.random(4);
	public int torcher = 3752 + Misc.random(7);

	private int[][] pcNPCData = { { 6142, 2628, 2591 }, // portal
			{ 6143, 2680, 2588 }, // portal
			{ 6144, 2669, 2570 }, // portal
			{ 6145, 2645, 2569 }, // portal
			{ 3782, 2656, 2592 }, // knight
	};
	private int[][] voidMonsterData = { { shifter, 2675, 2589 },
			{ brawler, 2679, 2593 }, { defiler, 2679, 2585 },
			{ ravager, 2675, 2585 }, { torcher, 2675, 2593 },
			{ shifter, 2670, 2576 }, { brawler, 2674, 2573 },
			{ defiler, 2666, 2573 }, { ravager, 2666, 2577 },
			{ torcher, 2674, 2577 }, { shifter, 2646, 2576 },
			{ brawler, 2650, 2572 }, { defiler, 2642, 2572 },
			{ ravager, 2642, 2576 }, { torcher, 2650, 2576 },
			{ shifter, 2635, 2592 }, { brawler, 2631, 2588 },
			{ defiler, 2631, 2596 }, { ravager, 2635, 2596 },
			{ torcher, 2635, 2588 }, };

	public void process() {
		try {
			if (properTimer > 0) {
				properTimer--;
				return;
			} else {
				properTimer = 1;
			}
			setBoatInterface();
			if (waitTimer > 0)
				waitTimer--;
			else if (waitTimer == 0)
				startGame();
			if (KNIGHTS_HEALTH == 0)
				endGame(false);
			if (gameStarted && playersInGame() < 1) {
				endGame(false);
			}
			if (gameTimer > 0 && gameStarted) {
				gameTimer--;
				KNIGHTS_HEALTH--;
				setGameInterface();
				if (allPortalsDead() || allPortalsDead3())
					endGame(true);
			} else if (gameTimer <= 0 && gameStarted)
				endGame(false);
		} catch (RuntimeException e) {
			System.out.println("Failed to set process");
			e.printStackTrace();
		}
	}

	/**
	 * Method we use for removing a player from the pc game
	 * 
	 * @param player
	 *            The Player.
	 */
	public static void removePlayerGame(Client player) {
		if (gamePlayers.containsKey(player)) {
			player.teleportToX = 2657;
			player.teleportToY = 2639;
			player.height = 0;
			// player.getPA().movePlayer(2657, 2639, 0);
			gamePlayers.remove(player);
		}
	}

	/**
	 * Setting the interfaces for the waiting lobby
	 */
	private void setBoatInterface() {
		try {
			for (Client c : waitingBoat.keySet()) {
				if (c != null) {
					try {
						if (gameStarted) {
							c.getPA().sendFrame126(
									"Next Departure: "
											+ (waitTimer + gameTimer) / 60
											+ " minutes", 21120);
						} else {
							c.getPA().sendFrame126(
									"Next Departure: " + waitTimer + "", 21120);
						}
						c.getPA()
								.sendFrame126(
										"Players Ready: " + playersInBoat()
												+ "", 21121);
						c.getPA()
								.sendFrame126(
										"(Need " + PLAYERS_REQUIRED
												+ " to 25 players)", 21122);
						c.getPA().sendFrame126("Points: " + c.pcPoints + "",
								21123);
						switch (waitTimer) {
						case 60:
							c.sendMessage("Next game will start in: 60 seconds.");
							break;
						}
					} catch (RuntimeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} catch (RuntimeException e) {
			System.out.println("Failed to set interfaces");
			e.printStackTrace();
		}
	}

	/**
	 * Setting the interface for in game players
	 */
	private void setGameInterface() {
		for (Client player : gamePlayers.keySet()) {
			if (player != null) {
				for (int i = 0; i < portalHealth.length; i++) {
					if (portalHealth[i] > 0) {
						player.getPA().sendFrame126("" + portalHealth[i] + "",
								21111 + i);
					} else
						player.getPA().sendFrame126("Dead", 21111 + i);

				}
				player.getPA().sendFrame126("" + KNIGHTS_HEALTH, 21115);
				player.getPA().sendFrame126("" + player.pcDamage, 21116);
				if (gameTimer > 60) {
					player.getPA().sendFrame126(
							"Time remaining: " + (gameTimer / 60) + " minutes",
							21117);
				} else {
					player.getPA().sendFrame126(
							"Time remaining: " + gameTimer + " seconds", 21117);
				}

			}
		}
	}

	/***
	 * Moving players to arena if there's enough players
	 */
	private void startGame() {
		if (playersInBoat() < PLAYERS_REQUIRED) {
			waitTimer = WAIT_TIMER;
			return;
		}
		for (int i = 0; i < portalHealth.length; i++)
			portalHealth[i] = 200;
		gameTimer = 300;
		KNIGHTS_HEALTH = 200;
		waitTimer = -1;
		spawnNPC();
		gameStarted = true;
		for (Client player : waitingBoat.keySet()) {
			int team = waitingBoat.get(player);
			if (player == null) {
				continue;
			}
			player.teleportToX = 2656 + Misc.random3(3);
			player.teleportToY = 2614 - Misc.random3(4);
			player.height = 0;
			// player.getPA().movePlayer(2656 + Misc.random3(3),
			// 2614 - Misc.random3(4), 0);
			player.sendMessage("@red@The Pest Control game has begun!");
			gamePlayers.put(player, team);
		}

		waitingBoat.clear();
	}

	/**
	 * Checks how many players are in the waiting lobby
	 * 
	 * @return players waiting
	 */
	private int playersInBoat() {
		int players = 0;
		for (Client player : waitingBoat.keySet()) {
			if (player != null) {
				players++;
			}
			if (player == null) {
				players--;
			}
		}
		return players;
	}

	/**
	 * Checks how many players are in the game
	 * 
	 * @return players in the game
	 */
	private int playersInGame() {
		int players = 0;
		for (Client player : gamePlayers.keySet()) {
			if (player != null) {
				players++;
			}
			if (player == null) {
				players--;
			}
		}
		return players;
	}

	/**
	 * Ends the game
	 * 
	 * @param won
	 *            Did you win?
	 */
	private void endGame(boolean won) {
		for (Client player : gamePlayers.keySet()) {
			if (player == null) {
				continue;
			}
			// player.getPA().movePlayer(2657, 2639, 0);
			player.teleportToX = 2657;
			player.teleportToY = 2639;
			player.height = 0;
			if (won && player.pcDamage > 50) {
				// player.getDH().sendDialogues(79, 3790);
				int POINT_REWARD = 10;
				player.sendMessage("You have won the pest control game and have been awarded "
						+ POINT_REWARD + " Pest control points.");
				player.pcPoints += POINT_REWARD;
				player.getItems().addItem(995, player.combatLevel * 1000);
			} else if (won) {
				// player.getDH().sendDialogues(77, 3790);
				// int POINT_REWARD2 = 4;
				// player.pcPoints += POINT_REWARD2;
				player.sendMessage("The void knights notice your lack of zeal. You receive no points.");
			} else {
				// player.getDH().sendDialogues(78, 3790);
				player.sendMessage("The failed to keep the Void Knight alive! You receive no points.");
			}
		}
		cleanUpPlayer();
		cleanUp();
	}

	/**
	 * Resets the game variables and map
	 */
	private void cleanUp() {
		gameTimer = -1;
		KNIGHTS_HEALTH = -1;
		waitTimer = WAIT_TIMER;
		gameStarted = false;
		gamePlayers.clear();
		/*
		 * Removes the npcs from the game if any left over for whatever reason
		 */
		for (int[] aPcNPCData : pcNPCData) {
			for (int j = 0; j < NPCHandler.npcs.length; j++) {
				if (NPCHandler.npcs[j] != null) {
					if (NPCHandler.npcs[j].npcType == aPcNPCData[0])
						NPCHandler.npcs[j] = null;
				}
			}
		}
		for (int[] aPcNPCData : voidMonsterData) {
			for (int j = 0; j < NPCHandler.npcs.length; j++) {
				if (NPCHandler.npcs[j] != null) {
					if (NPCHandler.npcs[j].npcType == aPcNPCData[0])
						NPCHandler.npcs[j] = null;
				}
			}
		}
	}

	/**
	 * Cleans the player of any damage, loss they may of received
	 */
	private void cleanUpPlayer() {
		for (Client player : gamePlayers.keySet()) {
			player.poisonDamage = 0;
			player.getCombat().resetPrayers();
			for (int i = 0; i < 24; i++) {
				player.playerLevel[i] = player.getPA().getLevelForXP(
						player.playerXP[i]);
				player.getPA().refreshSkill(i);
			}
			player.specAmount = 10;
			player.pcDamage = 0;
			player.getItems().addSpecialBar(
					player.playerEquipment[Player.playerWeapon]);
		}
	}

	/**
	 * Checks if the portals are dead
	 * 
	 * @return players dead
	 */
	private static boolean allPortalsDead() {
		int count = 0;
		for (int aPortalHealth : portalHealth) {
			if (aPortalHealth <= 0)
				count++;
			// System.out.println("Portal Health++" + count);
		}
		return count >= 4;
	}

	public boolean allPortalsDead3() {
		int count = 0;
		for (int j = 0; j < NPCHandler.npcs.length; j++) {
			if (NPCHandler.npcs[j] != null) {
				if (NPCHandler.npcs[j].npcType > 6141
						&& NPCHandler.npcs[j].npcType < 6146)
					if (NPCHandler.npcs[j].needRespawn)
						count++;
			}
		}
		return count >= 4;
	}

	/**
	 * Moves a player out of the waiting boat
	 * 
	 * @param c
	 *            Client c
	 */
	public static void leaveWaitingBoat(Client c) {
		if (waitingBoat.containsKey(c)) {
			waitingBoat.remove(c);
			c.teleportToX = 2657;
			c.teleportToY = 2639;
			c.height = 0;
			// c.getPA().movePlayer(2657, 2639, 0);
		}
	}

	/**
	 * Moves a player into the hash and into the lobby
	 * 
	 * @param player
	 *            The player
	 */
	public static void addToWaitRoom(Client player) {
		if (player != null) {
			waitingBoat.put(player, 1);
			player.sendMessage("You board the lander.");
			player.teleportToX = 2661;
			player.teleportToY = 2639;
			player.height = 0;
			// player.getPA().movePlayer(2661, 2639, 0);
		}
	}

	/**
	 * Checks if a player is in the game
	 * 
	 * @param player
	 *            The player
	 * @return return
	 */
	public static boolean isInGame(Client player) {
		return gamePlayers.containsKey(player);
	}

	/**
	 * Checks if a player is in the pc boat (lobby)
	 * 
	 * @param player
	 *            The player
	 * @return return
	 */
	public static boolean isInPcBoat(Client player) {
		return waitingBoat.containsKey(player);
	}

	public static boolean npcIsPCMonster(int npcType) {
		return npcType >= 3727 && npcType <= 3776;
	}

	private void spawnNPC() {
		for (int[] aPcNPCData : pcNPCData) {
			Server.npcHandler.spawnNpc2(aPcNPCData[0], aPcNPCData[1],
					aPcNPCData[2], 0, 0, 200, 0, 0, playersInBoat() * 100);
		}
		for (int[] voidMonsters : voidMonsterData) {
			Server.npcHandler.spawnNpc2(voidMonsters[0], voidMonsters[1],
					voidMonsters[2], 0, 1, 100, 10, 200, 200);
		}
	}
}
