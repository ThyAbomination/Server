package server.model.players;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Future;

import org.apache.mina.common.IoSession;

import server.model.minigames.RFD;
import server.Config;
import server.Server;
import server.model.players.Highscores;
import server.content.skill.Prayer;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.event.Event;
import server.event.EventContainer;
import server.event.EventManager;
import server.model.items.ItemAssistant;
import server.model.minigames.PestControl;
import server.model.npcs.NPC;
import server.model.npcs.NPCHandler;
import server.model.players.skills.Agility;
import server.model.players.skills.Cooking;
import server.model.players.skills.Crafting;
import server.model.players.skills.Farming;
import server.model.players.skills.Fletching;
import server.model.players.skills.Runecrafting;
import server.model.players.skills.SkillInterfaces;
import server.model.players.skills.Slayer;
import server.model.players.skills.Smithing;
import server.model.players.skills.SmithingInterface;
import server.model.players.skills.Thieving;
import server.model.shops.ShopAssistant;
import server.net.HostList;
import server.net.Packet;
import server.net.StaticPacketBuilder;
import server.util.Misc;
import server.util.Stream;

public class Client extends Player {

	public byte buffer[] = null;
	public Stream inStream = null, outStream = null;
	private IoSession session;
	private ItemAssistant itemAssistant = new ItemAssistant(this);
	private ShopAssistant shopAssistant = new ShopAssistant(this);
	private TradeAndDuel tradeAndDuel = new TradeAndDuel(this);
	private PlayerAssistant playerAssistant = new PlayerAssistant(this);
	private CombatAssistant combatAssistant = new CombatAssistant(this);
	private Pins pins = new Pins(this);
	private ActionHandler actionHandler = new ActionHandler(this);
	private Slayer slayer = new Slayer(this);
	private DialogueHandler dialogueHandler = new DialogueHandler(this);
	private Queue<Packet> queuedPackets = new LinkedList<Packet>();
	private SmithingInterface smithInt = new SmithingInterface(this);
	private Smithing smith = new Smithing(this);
	private Potions potions = new Potions(this);
	private PotionMixing potionMixing = new PotionMixing(this);
	private Food food = new Food(this);
	private Curse curse = new Curse(this);
	private Thieving thieving = new Thieving(this);
	private Farming farming = new Farming(this);
	private TradeLog tradeLog = new TradeLog(this);

	private SkillInterfaces skillInterfaces = new SkillInterfaces(this);

	/**
	 * Skill instances
	 */
	private Runecrafting runecrafting = new Runecrafting(this);
	private Agility agility = new Agility(this);
	private Cooking cooking = new Cooking(this);
	private Crafting crafting = new Crafting(this);
	private Fletching fletching = new Fletching(this);
	private Prayer prayer = new Prayer(this);
	// private Herblore herblore = new Herblore(this);

	public int cannonTimer = 0;
	public int startDel = 0;
	public int lowMemoryVersion = 0;
	public int timeOutCounter = 0;
	public int returnCode = 2;
	private Future<?> currentTask;
	public int currentRegion = 0;

	public Client(IoSession s, int _playerId) {
		super(_playerId);
		this.session = s;
		// synchronized (this) {
		outStream = new Stream(new byte[Config.BUFFER_SIZE]);
		outStream.currentOffset = 0;

		inStream = new Stream(new byte[Config.BUFFER_SIZE]);
		inStream.currentOffset = 0;
		buffer = new byte[Config.BUFFER_SIZE];
		// }
	}

	public void flushOutStream() {
		if (disconnected || outStream.currentOffset == 0)
			return;
		// synchronized (this) {
		StaticPacketBuilder out = new StaticPacketBuilder().setBare(true);
		byte[] temp = new byte[outStream.currentOffset];
		System.arraycopy(outStream.buffer, 0, temp, 0, temp.length);
		out.addBytes(temp);
		session.write(out.toPacket());
		outStream.currentOffset = 0;
		// }
	}

	public void sendClan(String name, String message, String clan, int rights) {
		outStream.createFrameVarSizeWord(217);
		outStream.writeString(name);
		outStream.writeString(message);
		outStream.writeString(clan);
		outStream.writeWord(rights);
		outStream.endFrameVarSize();
	}

	public static final int PACKET_SIZES[] = { 0, 0, 0, 1, -1, 0, 0, 0, 0, 0, // 0
			0, 0, 0, 0, 8, 0, 6, 2, 2, 0, // 10
			0, 2, 0, 6, 0, 12, 0, 0, 0, 0, // 20
			0, 0, 0, 0, 0, 8, 4, 0, 0, 2, // 30
			2, 6, 0, 6, 0, -1, 0, 0, 0, 0, // 40
			0, 0, 0, 12, 0, 0, 0, 8, 8, 12, // 50
			8, 8, 0, 0, 0, 0, 0, 0, 0, 0, // 60
			6, 0, 2, 2, 8, 6, 0, -1, 0, 6, // 70
			0, 0, 0, 0, 0, 1, 4, 6, 0, 0, // 80
			0, 0, 0, 0, 0, 3, 0, 0, -1, 0, // 90
			0, 13, 0, -1, 0, 0, 0, 0, 0, 0,// 100
			0, 0, 0, 0, 0, 0, 0, 6, 0, 0, // 110
			1, 0, 6, 0, 0, 0, -1, 0, 2, 6, // 120
			0, 4, 6, 8, 0, 6, 0, 0, 0, 2, // 130
			0, 0, 0, 0, 0, 6, 0, 0, 0, 0, // 140
			0, 0, 1, 2, 0, 2, 6, 0, 0, 0, // 150
			0, 0, 0, 0, -1, -1, 0, 0, 0, 0,// 160
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // 170
			0, 8, 0, 3, 0, 2, 0, 0, 8, 1, // 180
			0, 0, 12, 0, 0, 0, 0, 0, 0, 0, // 190
			2, 0, 0, 0, 0, 0, 0, 0, 4, 0, // 200
			4, 0, 0, 0, 7, 8, 0, 0, 10, 0, // 210
			0, 0, 0, 0, 0, 0, -1, 0, 6, 0, // 220
			1, 0, 0, 0, 6, 0, 6, 8, 1, 0, // 230
			0, 4, 0, 0, 0, 0, -1, 0, -1, 4,// 240
			0, 0, 6, 6, 0, 0, 0 // 250
	};

	@Override
	public void destruct() {
		if (session == null)
			return;
		Server.panel.removeEntity(playerName);

		// PlayerSaving.getSingleton().requestSave(playerId);
		if (PestControl.isInPcBoat(this)) {
			PestControl.removePlayerGame(this);
			getPA().movePlayer(2440, 3089, 0);
		}
		if (duelStatus >= 1 && duelStatus <= 5) {
			getTradeAndDuel().bothDeclineDuel();
			saveCharacter = true;
			return;
		}
		if (disconnected == true) {
			getTradeAndDuel().declineTrade();
		}
		if (duelStatus == 6) {
			getTradeAndDuel().claimStakedItems();
		}
		if (clanId >= 0)
			Server.clanChat.leaveClan(playerId, clanId);
		Misc.println("[DEREGISTERED]: " + playerName + "");
		CycleEventHandler.getSingleton().stopEvents(this);
		HostList.getHostList().remove(session);
		disconnected = true;
		session.close();
		session = null;
		inStream = null;
		outStream = null;
		isActive = false;
		buffer = null;
		super.destruct();
	}

	public void sendMessage(String s) {
		// synchronized (this) {
		if (getOutStream() != null) {
			outStream.createFrameVarSize(253);
			outStream.writeString(s);
			outStream.endFrameVarSize();
		}
		// }
	}

	public void setSidebarInterface(int menuId, int form) {
		// synchronized (this) {
		if (getOutStream() != null) {
			outStream.createFrame(71);
			outStream.writeWord(form);
			outStream.writeByteA(menuId);
			// }
		}
	}

	public void FetchDice() {
		CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				int rnd;
				String Message = "";
				if (cDice == 0
						|| (System.currentTimeMillis() - diceDelay <= 1000)) {
					return;
				}
				switch (cDice) {
				// Dice
				case 15096:
					rnd = Misc.random(19) + 1;
					Message = ("rolled @dre@" + rnd + "@bla@ on a twenty-sided die.");
					break;
				case 15094:
					rnd = Misc.random(11) + 1;
					Message = ("rolled @dre@" + rnd + "@bla@ on a twelve-sided die.");
					break;
				case 15092:
					rnd = Misc.random(9) + 1;
					Message = ("rolled @dre@" + rnd + "@bla@ on a ten-sided die.");
					break;
				case 15090:
					rnd = Misc.random(7) + 1;
					Message = ("rolled @dre@" + rnd + "@bla@ on an eight-sided die.");
					break;
				case 15100:
					rnd = Misc.random(3) + 1;
					Message = ("rolled @dre@" + rnd + "@bla@ on a four-sided die.");
					break;
				case 15086:
					rnd = Misc.random(5) + 1;
					Message = ("rolled @dre@" + rnd + "@bla@ on a six-sided die.");
					break;
				case 15088:
					rnd = Misc.random(11) + 1;
					Message = ("rolled @dre@" + rnd + "@bla@ on two six-sided dice.");
					break;
				case 15098:
					rnd = Misc.random(99) + 1;
					Message = ("rolled @dre@" + rnd + "@bla@ on the percentile dice.");
					break;
				}
				sendMessage("You " + Message);
				if (clanDice) {
					if (clanId >= 0) {
						Server.clanChat.messageToClan(
								"Clan Chat channel-mate@dre@ "
										+ Misc.optimizeText(playerName)
										+ "@bla@ " + Message, clanId);
					}
				}
				cDice = 0;
				container.stop();
			}

			@Override
			public void stop() {

			}
		}, 1);
	}

	public void useDice(int itemId, boolean clan) {
		if (System.currentTimeMillis() - diceDelay >= 3000) {
			sendMessage("Rolling...");
			startAnimation(11900);
			diceDelay = System.currentTimeMillis();
			cDice = itemId;
			clanDice = clan;
			FetchDice();
			switch (itemId) {
			// Gfx's
			case 15086:
				gfx0(2072);
				break;
			case 15088:
				gfx0(2074);
				break;
			case 15090:
				gfx0(2071);
				break;
			case 15092:
				gfx0(2070);
				break;
			case 15094:
				gfx0(2073);
				break;
			case 15096:
				gfx0(2068);
				break;
			case 15098:
				gfx0(2075);
				break;
			case 15100:
				gfx0(2069);
				break;
			}
		}

	}

	public void handCannonDestroy() {
		int chance = playerLevel[playerFiremaking] * 5 + 50;
		if (specGfx)
			chance /= 2;
		if (Misc.random(chance) == 1)
			EventManager.getSingleton().addEvent(new Event() {
				@Override
				public void execute(EventContainer c) {
					if (cannonTimer <= 0) {
						gfx0(2140);
						playerEquipment[playerWeapon] = -1;
						sendMessage("Your hand cannon explodes!");
						int damage = Misc.random(15) + 1;
						setHitDiff(damage);
						setHitUpdateRequired(true);
						dealDamage(Misc.random(15) + 1);
						updateRequired = true;
						getItems().sendWeapon(
								playerEquipment[playerWeapon],
								getItems().getItemName(
										playerEquipment[playerWeapon]));
						getCombat().getPlayerAnimIndex(
								getItems().getItemName(
										playerEquipment[playerWeapon])
										.toLowerCase());
						getItems().resetBonus();
						getItems().getBonus();
						getItems().writeBonus();
						getPA().requestUpdates();
						getOutStream().createFrame(34);
						getOutStream().writeWord(6);
						getOutStream().writeWord(1688);
						getOutStream().writeByte(playerWeapon);
						getOutStream().writeWord(0);
						getOutStream().writeByte(0);
						updateRequired = true;
						setAppearanceUpdateRequired(true);
						c.stop();
					} else {
						cannonTimer--;
					}
				}
			}, 500);
	}

	public boolean specGfx = false;

	public void degradeVls() {
		if (playerEquipment[playerWeapon] == 13925 && vlsLeft < 1) {
			playerEquipment[playerWeapon] = -1;
			playerEquipmentN[playerWeapon] = 0;
			getItems().wearItem(-1, 1, 3);
			sendMessage("Your longsword crumbled to dust!");
			vlsLeft = 1500;
		}
	}

	public void degradeVSpear() {
		if (playerEquipment[playerWeapon] == 13931 && vSpearLeft < 1) {
			playerEquipment[playerWeapon] = -1;
			playerEquipmentN[playerWeapon] = 0;
			getItems().wearItem(-1, 1, 3);
			sendMessage("Your spear crumbled to dust!");
			vSpearLeft = 1500;
		}
	}

	public void degradeStat() {
		if (playerEquipment[playerWeapon] == 13928 && statLeft < 1) {
			playerEquipment[playerWeapon] = -1;
			playerEquipmentN[playerWeapon] = 0;
			getItems().wearItem(-1, 1, 3);
			sendMessage("Your warhammer crumbled to dust!");
			statLeft = 1500;
		}
	}

	public void degradeVTop() {
		if (playerEquipment[playerChest] == 13913 && vTopLeft < 1) {
			playerEquipment[playerChest] = -1;
			playerEquipmentN[playerChest] = 0;
			getItems().wearItem(-1, 1, playerChest);
			sendMessage("Your chainbody crumbled to dust!");
			vTopLeft = 1500;
		}
	}

	public void degradeVLegs() {
		if (playerEquipment[playerLegs] == 13919 && vLegsLeft < 1) {
			playerEquipment[playerLegs] = -1;
			playerEquipmentN[playerLegs] = 0;
			getItems().wearItem(-1, 1, playerLegs);
			sendMessage("Your plateskirt crumbled to dust!");
			vLegsLeft = 1500;
		}
	}

	public void degradeSTop() {
		if (playerEquipment[playerChest] == 13910 && sTopLeft < 1) {
			playerEquipment[playerChest] = -1;
			playerEquipmentN[playerChest] = 0;
			getItems().wearItem(-1, 1, playerChest);
			sendMessage("Your platebody crumbled to dust!");
			sTopLeft = 1500;
		}
	}

	public void degradeSLegs() {
		if (playerEquipment[playerLegs] == 13916 && sLegsLeft < 1) {
			playerEquipment[playerLegs] = -1;
			playerEquipmentN[playerLegs] = 0;
			getItems().wearItem(-1, 1, playerLegs);
			sendMessage("Your platelegs crumbled to dust.");
			sLegsLeft = 1500;
		}
	}

	public void degradeSHelm() {
		if (playerEquipment[playerHat] == 13922 && sHelmLeft < 1) {
			playerEquipment[playerHat] = -1;
			playerEquipmentN[playerHat] = 0;
			getItems().wearItem(-1, 1, playerHat);
			sendMessage("Your full helm crumbled to dust!");
			sHelmLeft = 1500;
		}
	}

	public void degradeZHood() {
		if (playerEquipment[playerHat] == 13940 && zHoodLeft < 1) {
			playerEquipment[playerHat] = -1;
			playerEquipmentN[playerHat] = 0;
			getItems().wearItem(-1, 1, playerHat);
			sendMessage("Your hood crumbled to dust!");
			zHoodLeft = 1500;
		}
	}

	public void degradeZTop() {
		if (playerEquipment[playerChest] == 13934 && zTopLeft < 1) {
			playerEquipment[playerChest] = -1;
			playerEquipmentN[playerChest] = 0;
			getItems().wearItem(-1, 1, playerChest);
			sendMessage("Your robe top crumbled to dust!");
			zTopLeft = 1500;
		}
	}

	public void degradeZBottom() {
		if (playerEquipment[playerLegs] == 13937 && zBottomLeft < 1) {
			playerEquipment[playerLegs] = -1;
			playerEquipmentN[playerLegs] = 0;
			getItems().wearItem(-1, 1, playerLegs);
			sendMessage("Your robe bottom crumbled to dust!");
			zBottomLeft = 1500;
		}
	}

	public void degradeZStaff() {
		if (playerEquipment[playerWeapon] == 13943 && zStaffLeft < 1) {
			playerEquipment[playerWeapon] = -1;
			playerEquipmentN[playerWeapon] = 0;
			getItems().wearItem(-1, 1, 3);
			sendMessage("Your staff crumbled to dust!");
			zStaffLeft = 1500;
		}
	}

	public void degradeMBody() {
		if (playerEquipment[playerChest] == 13946 && mBodyLeft < 1) {
			playerEquipment[playerChest] = -1;
			playerEquipmentN[playerChest] = 0;
			getItems().wearItem(-1, 1, playerChest);
			sendMessage("Your leather body crumbled to dust!");
			mBodyLeft = 1500;
		}
	}

	public void degradeMChaps() {
		if (playerEquipment[playerLegs] == 13949 && mChapsLeft < 1) {
			playerEquipment[playerLegs] = -1;
			playerEquipmentN[playerLegs] = 0;
			getItems().wearItem(-1, 1, playerLegs);
			sendMessage("Your chaps crumbled to dust!");
			mChapsLeft = 1500;
		}
	}

	public void degradeMCoif() {
		if (playerEquipment[playerHat] == 13952 && mCoifLeft < 1) {
			playerEquipment[playerHat] = -1;
			playerEquipmentN[playerHat] = 0;
			getItems().wearItem(-1, 1, playerHat);
			sendMessage("Your coif crumbled to dust!");
			mCoifLeft = 1500;
		}
	}

	@Override
	public void initialize() {
		// synchronized (this) {
		// UUID = RS2LoginProtocolDecoder.UUID;
		outStream.createFrame(249);
		outStream.writeByteA(1); // 1 for members, zero for free
		outStream.writeWordBigEndianA(playerId);
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (j == playerId)
				continue;
			if (PlayerHandler.players[j] != null) {
				if (PlayerHandler.players[j].playerName
						.equalsIgnoreCase(playerName))
					disconnected = true;
			}
		}
		for (int i = 0; i < 25; i++) {
			getPA().setSkillLevel(i, playerLevel[i], playerXP[i]);
			getPA().refreshSkill(i);
		}
		for (int p = 0; p < PRAYER.length; p++) { // reset prayer glows
			prayerActive[p] = false;
			getPA().sendFrame36(PRAYER_GLOW[p], 0);
		}
		for (int p = 0; p < CURSE.length; p++) { // reset prayer glows
			curseActive[p] = false;
			getPA().sendFrame36(CURSE_GLOW[p], 0);
		}
		if (duelStatus > 4) {
			getPA().movePlayer(Config.DUELING_RESPAWN_X,
					Config.DUELING_RESPAWN_Y, 0);
		}
		if (splitChat == true) {
			getPA().sendFrame36(502, 1);
			getPA().sendFrame36(287, 1);
		} else {
			getPA().sendFrame36(502, 0);
			getPA().sendFrame36(287, 0);
		}
		Server.panel.addEntity(playerName);
		getPA().handleWeaponStyle();
		getPA().handleLoginText();
		// accountFlagged = getPA().checkForFlags();
		// getPA().sendFrame36(43, fightMode -1);
		getPA().sendEnergy();
		getPA().sendFrame36(504, 1);
		getPA().sendFrame36(173, 1);
		getPA().sendFrame36(505, 0);// options
		getPA().sendFrame36(506, 1);
		getPA().sendFrame36(507, 0);
		getPA().sendFrame36(508, 0);
		getPA().sendFrame36(166, 2);
		getPA().sendFrame36(108, 0);// resets autocast button
		getPA().sendFrame36(172, 1);
		getPA().sendFrame107(); // reset screen
		getPA().setChatOptions(0, 0, 0); // reset private messaging options
		setSidebarInterface(1, 3917);
		setSidebarInterface(2, 638);
		setSidebarInterface(3, 3213);
		setSidebarInterface(4, 1644);
		setSidebarInterface(5, 5608);
		if (playerMagicBook == 0) {
			setSidebarInterface(6, 1151); // modern
		} else {
			if (playerMagicBook == 2) {
				setSidebarInterface(6, 29999); // lunar
			} else {
				setSidebarInterface(6, 12855); // ancient
			}
		}
		if (altarPrayed == 0) {
			setSidebarInterface(5, 5608);
		} else {
			setSidebarInterface(5, 22500);
		}
		correctCoordinates();
		setSidebarInterface(7, 18128);
		setSidebarInterface(8, 5065);
		setSidebarInterface(9, 5715);
		setSidebarInterface(10, 2449);
		// setSidebarInterface(11, 4445); // wrench tab
		setSidebarInterface(11, 904); // wrench tab
		setSidebarInterface(12, 147); // run tab
		setSidebarInterface(13, -1);
		setSidebarInterface(0, 2423);
		if (specAmount < 10) {
			restoreTimer = 30;
			specialRestoreEvent(this);
		}
		sendMessage("Welcome to " + Config.SERVER_NAME + ".");
		sendMessage(Config.WELCOME_MESSAGE);
		sendMessage(Config.UPDATE_MESSAGE);
		sendMessage("@red@Your account has " + infraction + " infractions.");
		sendMessage("@red@Your account will be automatically banned on your tenth infraction.");
		sendMessage("@red@Sign up on the forums today by using ::forums");
		
		if (playerDonator == 1) {
			sendMessage("@mag@Your account has donator status.");
		} else if (playerDonator == 2) {
			sendMessage("@mag@Your account has an extreme donator status.");
		} else {
			sendMessage("@mag@You account is currently a non-donator.");
		}
		
		getPA().showOption(4, 0, "Trade With", 4);
		getPA().showOption(5, 0, "Follow", 3);
		getItems().resetItems(3214);
		getItems().sendWeapon(playerEquipment[playerWeapon],
				getItems().getItemName(playerEquipment[playerWeapon]));
		getItems().resetBonus();
		getItems().getBonus();
		getItems().writeBonus();
		getItems().setEquipment(playerEquipment[playerHat], 1, playerHat);
		getItems().setEquipment(playerEquipment[playerCape], 1, playerCape);
		getItems().setEquipment(playerEquipment[playerAmulet], 1, playerAmulet);
		getItems().setEquipment(playerEquipment[playerArrows],
				playerEquipmentN[playerArrows], playerArrows);
		getItems().setEquipment(playerEquipment[playerChest], 1, playerChest);
		getItems().setEquipment(playerEquipment[playerShield], 1, playerShield);
		getItems().setEquipment(playerEquipment[playerLegs], 1, playerLegs);
		getItems().setEquipment(playerEquipment[playerHands], 1, playerHands);
		getItems().setEquipment(playerEquipment[playerFeet], 1, playerFeet);
		getItems().setEquipment(playerEquipment[playerRing], 1, playerRing);
		getItems().setEquipment(playerEquipment[playerWeapon],
				playerEquipmentN[playerWeapon], playerWeapon);
		getCombat().getPlayerAnimIndex(
				getItems().getItemName(playerEquipment[playerWeapon])
						.toLowerCase());
		getPA().logIntoPM();
		getItems().addSpecialBar(playerEquipment[playerWeapon]);
		saveTimer = Config.SAVE_TIMER;
		saveCharacter = true;
		Misc.println("[REGISTERED]: " + playerName + "");
		handler.updatePlayer(this, outStream);
		handler.updateNPC(this, outStream);
		flushOutStream();
		getPA().clearClanChat();
		getPA().resetFollow();
		spiritWave = 0;
		spiritCount = 0;
		if (addStarter)
			getPA().addStarter();
		Server.clanChat.handleClanChat(this, "Help");
		if (autoRet == 1)
			getPA().sendFrame36(172, 1);
		else
			getPA().sendFrame36(172, 0);
		// }
	}

	@Override
	public void update() {
		// synchronized (this) {
		handler.updatePlayer(this, outStream);
		handler.updateNPC(this, outStream);
		flushOutStream();
		// }
	}

	public void logout() {
		/*if (playerRights <= 2) {
			Highscores.save(this);
		} else {
			System.out.println("Did not save high-scores for this staff member.");
		}*/
		// synchronized (this) {
		if (System.currentTimeMillis() - logoutDelay > 10000) {
			outStream.createFrame(109);
			CycleEventHandler.getSingleton().stopEvents(this);
			properLogout = true;
		} else {
			sendMessage("You must wait a few seconds from being out of combat to logout.");
		}
		// }
	}

	public boolean isOwner() {
		return (playerName.equalsIgnoreCase("mod mikey"));
	}

	public boolean specEvent = false;
	public int restoreTimer;

	public void specialRestoreEvent(final Client c) {

		if (c.specEvent)
			return;
		c.specEvent = true;

		CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				if (restoreTimer > 0 && c.specAmount != 10) {
					restoreTimer--;
				}

				if (restoreTimer == 0) {
					if (specAmount < 10) {
						restoreTimer = 30;
						specAmount += .5;
						if (specAmount > 10) {
							specAmount = 10;
						}
						getItems().addSpecialBar(playerEquipment[playerWeapon]);
					}
				}

				if (specAmount > 10 && !c.isOwner())
					specAmount = 10;

				if (restoreTimer == 0 && specAmount > 9.9) {
					container.stop();
				}
			}

			@Override
			public void stop() {
				c.specEvent = false;
			}
		}, 1);
	}

	public int packetSize = 0, packetType = -1;

	@Override
	public void process() {

		if (startDel > 0) {
			startDel--;
		}

		if (SolProtect > 0) {
			if (playerEquipment[playerWeapon] != 15486) {
				SolProtect = 0;
				return;
			}
			SolProtect--;
			if (SolProtect == 1) {
			}
		}

		if (playerEquipment[playerWeapon] == 13925) {
			vlsLeft -= 1;
			degradeVls();
		}
		if (playerEquipment[playerWeapon] == 13931) {
			vSpearLeft -= 1;
			degradeVSpear();
		}
		if (playerEquipment[playerChest] == 13934) {
			zTopLeft -= 1;
			degradeZTop();
		}
		if (playerEquipment[playerLegs] == 13937) {
			zBottomLeft -= 1;
			degradeZBottom();
		}
		if (playerEquipment[playerWeapon] == 13928) {
			statLeft -= 1;
			degradeStat();
		}
		if (playerEquipment[playerChest] == 13913) {
			vTopLeft -= 1;
			degradeVTop();
		}
		if (playerEquipment[playerLegs] == 13919) {
			vLegsLeft -= 1;
			degradeVLegs();
		}
		if (playerEquipment[playerWeapon] == 13943) {
			zStaffLeft -= 1;
			degradeZStaff();
		}
		if (playerEquipment[playerHat] == 13943) {
			zHoodLeft -= 1;
			degradeZHood();
		}
		if (playerEquipment[playerChest] == 13946) {
			mBodyLeft -= 1;
			degradeMBody();
		}
		if (playerEquipment[playerLegs] == 13877) {
			mChapsLeft -= 1;
			degradeMChaps();
		}
		if (playerEquipment[playerLegs] == 13952) {
			mCoifLeft -= 1;
			degradeMCoif();
		}
		if (playerEquipment[playerChest] == 13949) {
			sTopLeft -= 1;
			degradeSTop();
		}
		if (playerEquipment[playerLegs] == 13916) {
			sLegsLeft -= 1;
			degradeSLegs();
		}
		if (playerEquipment[playerHat] == 13922) {
			sHelmLeft -= 1;
			degradeSHelm();
		}

		// Doors.getSingleton().handleDoor(objectId, objectX, objectY, 0);
		getPA().sendFrame126(
				"@whi@Players Online: " + PlayerHandler.getPlayerCount(), 640);
		getPA().sendFrame126("@gre@Player PvP Points: @whi@" + pvpPoints, 7332);
		getPA().sendFrame126("@gre@Player PvP Kills: @whi@" + pvpKills, 7333);
		// getPA().sendFrame126("KillStreak:"+Streak+" ", 29168);
		if (expLock == true) {
			getPA().sendFrame126("@gre@EXP Lock: @whi@ON", 7336);
		} else {
			getPA().sendFrame126("@gre@EXP Lock: @whi@OFF", 7336);
		}
		if (slayerTask < 1) {
			getPA().sendFrame126("@gre@Task: @whi@Nothing", 7383);
		} else {
			getPA().sendFrame126(
					"@gre@Task: @whi@"
							+ Server.npcHandler.getNpcListName(slayerTask),
					7383);
		}
		getPA().sendFrame126("@gre@Task Amount: @whi@" + taskAmount, 7339);
		getPA().sendFrame126("@gre@Slayer Points: @whi@" + slayerPoints, 7338);
		getPA().sendFrame126("@gre@Honour Points: @whi@" + barbPoints, 7340);
		getPA().sendFrame126("@gre@Pest Control Points: @whi@" + pcPoints, 7346);
		getPA().sendFrame126("@gre@Amount Donated: @whi@" + amountDonated + "$", 7341);
		getPA().sendFrame126("@gre@[View Boss Kill Log]", 7342);
		getPA().sendFrame126("", 17510);

		if (followId > 0) {
			getPA().followPlayer();
		} else if (followId2 > 0) {
			getPA().followNpc();
		}

		if (isMining == true) {
			getMiningEmote();
		}
		getCombat().handlePrayerDrain();
		if (System.currentTimeMillis() - singleCombatDelay > 5000) {
			underAttackBy = 0;
		}
		if (System.currentTimeMillis() - singleCombatDelay2 > 5000) {
			underAttackBy2 = 0;
		}

		if (System.currentTimeMillis() - duelDelay > 800 && duelCount > 0) {
			if (duelCount != 1) {
				forcedChat("" + (--duelCount));
				duelDelay = System.currentTimeMillis();
			} else {
				damageTaken = new int[Config.MAX_PLAYERS];
				forcedChat("FIGHT!");
				duelCount = 0;
			}
		}

		if (System.currentTimeMillis() - lastPoison > 20000 && poisonDamage > 0
				&& duelStatus != 5) {
			int damage = poisonDamage / 2;
			if (damage > 0) {
				sendMessage("The poison damages you.");
				if (!getHitUpdateRequired()) {
					setHitUpdateRequired(true);
					setHitDiff(damage);
					updateRequired = true;
					poisonMask = 1;
				} else if (!getHitUpdateRequired2()) {
					setHitUpdateRequired2(true);
					setHitDiff2(damage);
					updateRequired = true;
					poisonMask = 2;
				}
				lastPoison = System.currentTimeMillis();
				poisonDamage--;
				dealDamage(damage);
			} else {
				poisonDamage = -1;
				sendMessage("You are no longer poisoned.");
			}
		}

		if (System.currentTimeMillis() - restoreStatsDelay > (zerkOn ? 90000
				: 60000)) {
			restoreStatsDelay = System.currentTimeMillis();
			for (int level = 0; level < playerLevel.length; level++) {
				if (playerLevel[level] < getLevelForXP(playerXP[level])) {
					if (level != 5) { // prayer doesn't restore
						playerLevel[level] += 1;
						getPA().setSkillLevel(level, playerLevel[level],
								playerXP[level]);
						getPA().refreshSkill(level);
					}
				} else if (playerLevel[level] > getLevelForXP(playerXP[level])
						&& level != 3) {
					playerLevel[level] -= 1;
					getPA().setSkillLevel(level, playerLevel[level],
							playerXP[level]);
					getPA().refreshSkill(level);
				}
			}
		}

		if (barbLeader > 0 && inBarbDef()) {
			NPC n = NPCHandler.npcs[barbLeader];
			if (n != null) {
				n.facePlayer(playerId);
				if (Misc.random(50) == 0) {
					n.requestAnimation(6728, 0);
					n.forceChat(n.barbRandom(this, Misc.random(5)));
				}
			}
		}

		if (inWild()) {
			int modY = absY > 6400 ? absY - 6400 : absY;
			wildLevel = (((modY - 3520) / 8) + 10);
			getPA().walkableInterface(197);
			if (Config.SINGLE_AND_MULTI_ZONES) {
				if (inMulti()) {
					getPA().sendFrame126("@yel@Level: " + wildLevel, 199);
				} else if (inFunPK()) {
					getPA().sendFrame126("@gre@Fun PK", 199);
				} else {
					getPA().sendFrame126("@yel@Level: " + wildLevel, 199);
				}
			} else {
				getPA().multiWay(-1);
				getPA().sendFrame126("@yel@Level: " + wildLevel, 199);
			}
			getPA().showOption(3, 0, "Attack", 1);
		} else if (inDuelArena() && !inBarbDef()) {
			getPA().walkableInterface(201);
			if (duelStatus == 5) {
				getPA().showOption(3, 0, "Attack", 1);
			} else {
				getPA().showOption(3, 0, "Challenge", 1);
			}
		} else if (inBarrows()) {
			// getPA().sendFrame99(2);
			getPA().sendFrame126("Kill Count: " + barrowsKillCount, 4536);
			getPA().walkableInterface(4535);
		} else if (inPcBoat()) {
			getPA().walkableInterface(21119);
		} else if (inPcGame()) {
			getPA().walkableInterface(21100);
		} else if (inCwGame || inPits || playerName.equalsIgnoreCase("")) {
			getPA().showOption(3, 0, "Attack", 1);
		} else if (getPA().inPitsWait()) {
			getPA().showOption(3, 0, "Null", 1);
		} else if (!inCwWait) {
			getPA().sendFrame99(0);
			getPA().walkableInterface(-1);
			getPA().showOption(3, 0, "Null", 1);
		}

		if (!hasMultiSign && inMulti()) {
			hasMultiSign = true;
			getPA().multiWay(1);
		}

		if (hasMultiSign && !inMulti()) {
			hasMultiSign = false;
			getPA().multiWay(-1);
		}

		if (skullTimer > 0) {
			skullTimer--;
			if (skullTimer == 1) {
				isSkulled = false;
				attackedPlayers.clear();
				headIconPk = -1;
				skullTimer = -1;
				getPA().requestUpdates();
			}
		}

		if (isDead && respawnTimer == -6) {
			getPA().applyDead();
		}

		if (respawnTimer == 7) {
			respawnTimer = -6;
			getPA().giveLife();
		} else if (respawnTimer == 12) {
			respawnTimer--;
			startAnimation(836);
			poisonDamage = -1;
		}

		if (respawnTimer > -6) {
			respawnTimer--;
		}
		if (freezeTimer > -6) {
			freezeTimer--;
			if (frozenBy > 0) {
				if (PlayerHandler.players[frozenBy] == null) {
					freezeTimer = -1;
					frozenBy = -1;
				} else if (!goodDistance(absX, absY,
						PlayerHandler.players[frozenBy].absX,
						PlayerHandler.players[frozenBy].absY, 20)) {
					freezeTimer = -1;
					frozenBy = -1;
				}
			}
		}

		if (hitDelay > 0) {
			hitDelay--;
		}

		if (teleTimer > 0) {
			teleTimer--;
			if (!isDead) {
				if (teleTimer == 1 && newLocation > 0) {
					teleTimer = 0;
					getPA().changeLocation();
				}
				if (teleTimer == 5) {
					teleTimer--;
					getPA().processTeleport();
				}
				if (teleTimer == 9 && teleGfx > 0) {
					teleTimer--;
					gfx100(teleGfx);
				}
			} else {
				teleTimer = 0;
			}
		}

		if (hitDelay == 1) {
			if (oldNpcIndex > 0) {
				getCombat().delayedHit(this, oldNpcIndex);
			}
			if (oldPlayerIndex > 0) {
				getCombat().playerDelayedHit(this, oldPlayerIndex);
			}
		}

		if (attackTimer > 0) {
			attackTimer--;
		}

		if (attackTimer == 1) {
			if (npcIndex > 0 && clickNpcType == 0) {
				getCombat().attackNpc(npcIndex);
			}
			if (playerIndex > 0) {
				getCombat().attackPlayer(playerIndex);
			}
		} else if (attackTimer <= 0 && (npcIndex > 0 || playerIndex > 0)) {
			if (npcIndex > 0) {
				attackTimer = 0;
				getCombat().attackNpc(npcIndex);
			} else if (playerIndex > 0) {
				attackTimer = 0;
				getCombat().attackPlayer(playerIndex);
			}
		}

		if (timeOutCounter > Config.TIMEOUT) {
			disconnected = true;
		}

		timeOutCounter++;
	}

	public void setCurrentTask(Future<?> task) {
		currentTask = task;
	}

	public Future<?> getCurrentTask() {
		return currentTask;
	}

	public/* synchronized */Stream getInStream() {
		return inStream;
	}

	// public/* synchronized */int getPacketType() {
	// return packetType;
	// }

	// public/* synchronized */int getPacketSize() {
	// return packetSize;
	// }

	public/* synchronized */Stream getOutStream() {
		return outStream;
	}

	public ItemAssistant getItems() {
		return itemAssistant;
	}

	public PlayerAssistant getPA() {
		return playerAssistant;
	}

	public DialogueHandler getDH() {
		return dialogueHandler;
	}

	public ShopAssistant getShops() {
		return shopAssistant;
	}

	public TradeAndDuel getTradeAndDuel() {
		return tradeAndDuel;
	}

	public CombatAssistant getCombat() {
		return combatAssistant;
	}

	public ActionHandler getActions() {
		return actionHandler;
	}


	public Pins getBankPin() {
		return pins;
	}

	public IoSession getSession() {
		return session;
	}

	public Potions getPotions() {
		return potions;
	}

	public PotionMixing getPotMixing() {
		return potionMixing;
	}

	public Food getFood() {
		return food;
	}

	public Curse getCurse() {
		return curse;
	}

	public Thieving getThieving() {
		return thieving;
	}

	public TradeLog getTradeLog() {
		return tradeLog;
	}

	private boolean isBusy = false;
	private boolean isBusyHP = false;
	public boolean isBusyFollow = false;

	public boolean checkBusy() {
		/*
		 * if (getCombat().isFighting()) { return true; }
		 */

		if (isBusy) {
			// sendMessage("You are too busy to do that.");
		}
		return isBusy;
	}

	public boolean checkBusyHP() {
		return isBusyHP;
	}

	public boolean checkBusyFollow() {
		return isBusyFollow;
	}

	public void setBusy(boolean isBusy) {
		this.isBusy = isBusy;
	}

	public boolean isBusy() {
		return isBusy;
	}

	public void setBusyFollow(boolean isBusyFollow) {
		this.isBusyFollow = isBusyFollow;
	}

	public void setBusyHP(boolean isBusyHP) {
		this.isBusyHP = isBusyHP;
	}

	public boolean isBusyHP() {
		return isBusyHP;
	}

	public boolean isBusyFollow() {
		return isBusyFollow;
	}

	private boolean canWalk = true;

	public boolean canWalk() {
		return canWalk;
	}

	public void setCanWalk(boolean canWalk) {
		this.canWalk = canWalk;
	}

	public PlayerAssistant getPlayerAssistant() {
		return playerAssistant;
	}

	public SkillInterfaces getSI() {
		return skillInterfaces;
	}

	public Runecrafting getRunecrafting() {
		return runecrafting;
	}

	public Cooking getCooking() {
		return cooking;
	}

	public Agility getAgility() {
		return agility;
	}

	public Crafting getCrafting() {
		return crafting;
	}

	// public Herblore getHerblore() {
	// return herblore;
	// }

	public Fletching getFletching() {
		return fletching;
	}

	public Prayer getPrayer() {
		return prayer;
	}

	/**
	 * End of Skill Constructors
	 */

	/*
	 * public void queueMessage(Packet arg1) { Queue var2 = this.queuedPackets;
	 * synchronized (this.queuedPackets) { this.queuedPackets.add(arg1);
	 * 
	 * } }
	 * 
	 * @Override public synchronized boolean processQueuedPackets() { Packet p =
	 * null; Queue var2 = this.queuedPackets; synchronized (this.queuedPackets)
	 * { p = this.queuedPackets.poll(); }
	 * 
	 * if (p == null) { return false; } else { this.inStream.currentOffset = 0;
	 * this.packetType = p.getId(); this.packetSize = p.getLength();
	 * this.inStream.buffer = p.getData(); if (this.packetType > 0) {
	 * 
	 * PacketHandler.processPacket(this, this.packetType, this.packetSize);
	 * 
	 * } this.timeOutCounter = 0; return true; } }
	 */

	public void queueMessage(Packet arg1) {
		synchronized (queuedPackets) {
			queuedPackets.add(arg1);
		}
	}

	@Override
	public synchronized boolean processQueuedPackets() {
		Packet p;
		synchronized (queuedPackets) {
			p = queuedPackets.poll();
		}
		if (p == null) {
			return false;
		}
		inStream.currentOffset = 0;
		packetType = p.getId();
		packetSize = p.getLength();
		inStream.buffer = p.getData();
		if (packetType > 0) {
			PacketHandler.processPacket(this, packetType, packetSize);
		}
		timeOutCounter = 0;
		return true;
	}

	public boolean processPacket(Packet p) {
		if (p == null) {
			return false;
		}
		inStream.currentOffset = 0;
		packetType = p.getId();
		packetSize = p.getLength();
		inStream.buffer = p.getData();
		if (packetType > 0) {
			PacketHandler.processPacket(this, packetType, packetSize);
		}
		timeOutCounter = 0;
		return true;
	}

	public void correctCoordinates() {
		if (inRFD()) { //what this is is that it will add correct coordinates for minigame.
                        getPA().movePlayer(1899,5363, playerId * 4+2); //indicates next wave and waits 10 secounds
                        sendMessage("Your wave will start in 10 seconds."); //notifies you
                        EventManager.getSingleton().addEvent(new Event() { //will add a new event for the 'new' wave
                                public void execute(EventContainer c) {
                                        Server.rfd.spawnNextWave((Client)Server.playerHandler.players[playerId]); //it'll spawn NPC for next wave
                                        c.stop();
                                }
                                }, 10000);
                
                }
		if (inPcGame()) {
			getPA().movePlayer(2657, 2639, 0);
		}
	}

	private HashMap<String, Object> attributes = new HashMap<String, Object>();
	public long lastLight;

	public void setAttribute(String key, Object value) {
		attributes.put(key, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String key) {
		return (T) attributes.get(key);
	}

	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String key, T fail) {
		T t = (T) attributes.get(key);
		if (t != null) {
			return t;
		}
		return fail;
	}

	public void removeAttribute(String key) {
		attributes.remove(key);
	}

	public int timer = 0;
	public int teleotherType;

	public void getMiningEmote() {
		timer++;
		if (timer >= 3) {
			if (playerEquipment[playerWeapon] == 1265
					|| getItems().playerHasItem(1265)) {
				startAnimation(625);
			} else if (playerEquipment[playerWeapon] == 1267
					|| getItems().playerHasItem(1267)) {
				startAnimation(626);
			} else if (playerEquipment[playerWeapon] == 1269
					|| getItems().playerHasItem(1269)) {
				startAnimation(627);
			} else if (playerEquipment[playerWeapon] == 1273
					|| getItems().playerHasItem(1273)) {
				startAnimation(629);
			} else if (playerEquipment[playerWeapon] == 1271
					|| getItems().playerHasItem(1271)) {
				startAnimation(628);
			} else if (playerEquipment[playerWeapon] == 1275
					|| getItems().playerHasItem(1275)) {
				startAnimation(624);
			}
			timer = 0;
		}
	}

	public static Client getClient(String name) {
		name = name.toLowerCase();
		for (int i = 0; i < Config.MAX_PLAYERS; i++) {
			if (validClient(i)) {
				Client client = getClient(i);
				if (client.playerName.toLowerCase().equalsIgnoreCase(name)) {
					return client;
				}
			}
		}
		return null;
	}

	public static Client getClient(int id) {
		return (Client) PlayerHandler.players[id];
	}

	public static boolean validClient(int id) {
		if (id < 0 || id > Config.MAX_PLAYERS) {
			return false;
		}
		return validClient(getClient(id));
	}

	public boolean validClient(String name) {
		return validClient(getClient(name));
	}

	public static boolean validClient(Client client) {
		return (client != null && !client.disconnected);
	}

	public boolean validNpc(int index) {
		if (index < 0 || index >= Config.MAX_NPCS) {
			return false;
		}
		NPC n = getNpc(index);
		if (n != null) {
			return true;
		}
		return false;
	}

	public NPC getNpc(int index) {
		return NPCHandler.npcs[index];
	}

	public void yell(String s) {
		for (int i = 0; i < Config.MAX_PLAYERS; i++) {
			if (validClient(i)) {
				getClient(i).sendMessage(s);
			}
		}
	}

	public Farming getFarming() {
		return farming;
	}

	public Smithing getSmithing() {
		return smith;
	}

	public SmithingInterface getSmithingInt() {
		return smithInt;
	}

	public Slayer getSlayer() {
		return slayer;
	}

}
