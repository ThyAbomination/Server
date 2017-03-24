package server.model.players;

import server.model.players.Client;

public class DialogueHandler {

	private Client c;

	public DialogueHandler(Client client) {
		this.c = client;
	}

	/**
	 * Handles all talking
	 * 
	 * @param dialogue
	 *            The dialogue you want to use
	 * @param npcId
	 *            The npc id that the chat will focus on during the chat
	 */
	public void sendDialogues(int dialogue, int npcId) {
		c.talkingNpc = npcId;
		switch (dialogue) {
		case 0:
			c.talkingNpc = -1;
			c.getPA().removeAllWindows();
			c.nextChat = 0;
			break;
		case 1:
			sendStatement("You found a hidden tunnel, do you want to enter it?");
			c.nextChat = 2;
			break;
		case 2:
			sendOption2("Yeah I'm fearless!", "No way, that looks scary!");
			c.dialogueAction = 7;
			c.nextChat = 0;
			break;

		case 3:
			sendNpcChat1("Is there something I can help you with?",
					c.talkingNpc, "Larxus");
			c.nextChat = 4;
			break;
		case 4:
			sendOption3("I was given a challenge, what now?",
					"What is this place?", "Nothing, thanks.");
			c.dialogueAction = 1;
			break;
		case 5:
			sendNpcChat1("Well pass it here and we'll get you started.",
					c.talkingNpc, "Larxus");
			c.nextChat = 0;
			break;
		case 6:
			sendNpcChat4(
					"Here you can fight powerful Champions of different races.",
					"You will be rewarded with PvP points for each Champion",
					"that you defeat. You will also recieve a special reward",
					"for defeating all the Champions... if you can that is.",
					c.talkingNpc, "Larxus");
			c.nextChat = 0;
			break;
		case 7:
			sendNpcChat4(
					"So you want to accept the challenge huh? Well there ",
					"are some specific rules for these Champion fights. You",
					"are not allowed to use any magic or ranged attacks.",
					"The use of prayers is also not allowed.", c.talkingNpc,
					"Larxus");
			c.nextChat = 10;
			break;
		case 8:
			sendOption2("Yes, let me at them!", "No thanks, I'll pass.");
			c.dialogueAction = 2;
			break;
		case 9:
			sendNpcChat2("Your challengers are ready, please go down through",
					"the trapdoor when you're ready.", c.talkingNpc, "Larxus");
			c.nextChat = 0;
			break;
		case 10:
			sendNpcChat1("Do you still want to proceed?", c.talkingNpc,
					"Larxus");
			c.nextChat = 8;
			break;
		case 11:
			sendNpcChat1("Do you need me to fix your barrows equipment?",
					c.talkingNpc, "Ghost");
			c.nextChat = 12;
			break;
		case 12:
			sendOption2("Yes", "No");
			c.dialogueAction = 3;
			break;
		case 17:
			sendOption5("Air", "Mind", "Water", "Earth", "More");
			c.dialogueAction = 10;
			c.dialogueId = 17;
			break;
		case 18:
			sendOption5("Fire", "Body", "Cosmic", "Mind", "More");
			c.dialogueAction = 11;
			c.dialogueId = 18;
			break;
		case 19:
			sendOption5("Nature", "Law", "Death", "Blood", "Back");
			c.dialogueAction = 12;
			c.dialogueId = 19;
			break;
		case 20:
			sendNpcChat3(
					"Hello.",
					"Would you like me to recolour an Abyssal whip or Dark bow",
					"for you in exchange for 50 Honour points?", c.talkingNpc,
					"Sigmund The Merchant");
			c.nextChat = 21;
			break;
		case 21:
			sendOption2("Yes", "No");
			c.dialogueAction = 4;
			break;
		case 22:
			sendOption2("Abyssal whip", "Dark bow");
			c.dialogueAction = 8;
			break;
		case 23:
			sendOption4("Yellow", "Blue", "White", "Green");
			c.dialogueAction = 9;
			break;
		case 24:
			sendOption4("Yellow", "Blue", "White", "Green");
			c.dialogueAction = 10;
			break;
		case 25:
			sendOption4("Saradomin Boss", "Armadyl Boss", "Bandos Boss",
					"Zamorak Boss");
			c.dialogueAction = 13;
			break;
		case 51:
                        sendPlayerChat1("What are you doing here?");
                        c.nextChat = 52;
                        c.dialogueAction = 100;
                break;
                case 52:
                        sendNpcChat4("I have sets of powerful gloves. You can", "unlock differnt pairs if you participate", "in my game. Are you interested?", 
                        "", c.talkingNpc, "Gypsy");
                        c.nextChat = 53;
                break;
                case 53:
                        sendOption2("Yes, I'll play your game.", "No, thanks.");
                        c.dialogueAction = 101;
                break;
                case 54:
                        sendPlayerChat1("Yes, I'll play your game.");
                        c.nextChat = 55;
                break;
                case 55://Action after case 4
                        c.getPA().enterRFD();
                        c.talkingNpc = -1;
                        c.getPA().removeAllWindows();
                        for(int p = 0; p < c.PRAYER.length; p++) { 
                                c.prayerActive[p] = false;
                                c.getPA().sendFrame36(c.PRAYER_GLOW[p], 0);     
                        }
                break;
 

		case 76:
			sendNpcChat1("I can heal you if your injured.", c.talkingNpc,
					"Healer");
			c.nextChat = 0;
			break;
		case 77:
			sendNpcChat4("" + c.playerName + " you have failed.",
					"You did participate enough to take down", "the portals. ",
					"Try Harder next time.", c.talkingNpc, "Void Knight");
			break;
		case 78:
			sendNpcChat4("All is Lost!",
					"You could not take down the portals in time.", " ",
					"Try Harder next time.", c.talkingNpc, "Void Knight");
			break;
		case 79:
			sendNpcChat4("Congratulations " + c.playerName + "!",
					"You took down all the portals whilst keeping",
					"the void knight alive.", "You been awarded, well done.",
					c.talkingNpc, "Void Knight");
			break;

		case 80:
			sendNpcChat1("I buy PVP artifacts. Come back when you have some.",
					c.talkingNpc, "Sin Seer");
			c.nextChat = 0;
			if (c.getItems().playerHasItem(14876, 1)) {
				sendNpcChat1("A Ancient statuette! I'll buy that for 20M!",
						c.talkingNpc, "Sin Seer");
				c.nextChat = 81;
			} else if (c.getItems().playerHasItem(14877, 1)) {
				sendNpcChat1("A Seren statuette! I'll buy that for 4M!",
						c.talkingNpc, "Sin Seer");
				c.nextChat = 81;
			} else if (c.getItems().playerHasItem(14878, 1)) {
				sendNpcChat1("A Armadyl statuette! I'll buy that for 3M!",
						c.talkingNpc, "Sin Seer");
				c.nextChat = 81;
			} else if (c.getItems().playerHasItem(14879, 1)) {
				sendNpcChat1("A Zamorak statuette! I'll buy that for 2M",
						c.talkingNpc, "Sin Seer");
				c.nextChat = 81;
			} else if (c.getItems().playerHasItem(14880, 1)) {
				sendNpcChat1("A Saradomin statuette! I'll buy that for 1.6M!",
						c.talkingNpc, "Sin Seer");
				c.nextChat = 81;
			} else if (c.getItems().playerHasItem(14881, 1)) {
				sendNpcChat1("A Bandos statuette! I'll buy that for 1.2M!",
						c.talkingNpc, "Sin Seer");
				c.nextChat = 81;
			} else if (c.getItems().playerHasItem(14882, 1)) {
				sendNpcChat1("A Ruby chalice! I'll buy that for 1M!",
						c.talkingNpc, "Sin Seer");
				c.nextChat = 81;
			} else if (c.getItems().playerHasItem(14883, 1)) {
				sendNpcChat1("A Guthixian brazier! I'll buy that for 800K!",
						c.talkingNpc, "Sin Seer");
				c.nextChat = 81;
			} else if (c.getItems().playerHasItem(14884, 1)) {
				sendNpcChat1("A Armadyl totem! I'll buy that for 600K!",
						c.talkingNpc, "Sin Seer");
				c.nextChat = 81;
			} else if (c.getItems().playerHasItem(14885, 1)) {
				sendNpcChat1("A Zamorak medallion! I'll buy that for 400K!",
						c.talkingNpc, "Sin Seer");
				c.nextChat = 81;
			} else if (c.getItems().playerHasItem(14886, 1)) {
				sendNpcChat1("A Saradomin carving! I'll buy that for 300K!",
						c.talkingNpc, "Sin Seer");
				c.nextChat = 81;
			} else if (c.getItems().playerHasItem(14887, 1)) {
				sendNpcChat1("A Bandos scrimshaw I'll buy that for 200K!",
						c.talkingNpc, "Sin Seer");
				c.nextChat = 81;
			} else if (c.getItems().playerHasItem(14888, 1)) {
				sendNpcChat1("A Saradomin amphora! I'll buy that for 160K!",
						c.talkingNpc, "Sin Seer");
				c.nextChat = 81;
			} else if (c.getItems().playerHasItem(14889, 1)) {
				sendNpcChat1(
						"A Ancient psaltery bridge! I'll buy that for 120K!",
						c.talkingNpc, "Sin Seer");
				c.nextChat = 81;
			} else if (c.getItems().playerHasItem(14890, 1)) {
				sendNpcChat1("A Bronzed dragon claw! I'll buy that for 80K!",
						c.talkingNpc, "Sin Seer");
				c.nextChat = 81;
			} else if (c.getItems().playerHasItem(14891, 1)) {
				sendNpcChat1("A Third age carafe! I'll buy that for 40K!",
						c.talkingNpc, "Sin Seer");
				c.nextChat = 81;
			} else if (c.getItems().playerHasItem(14892, 1)) {
				sendNpcChat1(
						"A Broken statue headdress! I'll buy that for 20K!",
						c.talkingNpc, "Sin Seer");
				c.nextChat = 81;
			}
			break;
		case 81:
			c.sendMessage("The old hag buys your artifact for a nice amount of gold.");
			if (c.getItems().playerHasItem(14876, 1)) {
				c.getItems().deleteItem(14876, 1);
				c.getItems().addItem(995, 20000000);
			} else if (c.getItems().playerHasItem(14877, 1)) {
				c.getItems().deleteItem(14877, 1);
				c.getItems().addItem(995, 4000000);
			} else if (c.getItems().playerHasItem(14878, 1)) {
				c.getItems().deleteItem(14878, 1);
				c.getItems().addItem(995, 3000000);
			} else if (c.getItems().playerHasItem(14879, 1)) {
				c.getItems().deleteItem(14879, 1);
				c.getItems().addItem(995, 2000000);
			} else if (c.getItems().playerHasItem(14880, 1)) {
				c.getItems().deleteItem(14880, 1);
				c.getItems().addItem(995, 1600000);
			} else if (c.getItems().playerHasItem(14881, 1)) {
				c.getItems().deleteItem(14881, 1);
				c.getItems().addItem(995, 1200000);
			} else if (c.getItems().playerHasItem(14882, 1)) {
				c.getItems().deleteItem(14882, 1);
				c.getItems().addItem(995, 1000000);
			} else if (c.getItems().playerHasItem(14883, 1)) {
				c.getItems().deleteItem(14883, 1);
				c.getItems().addItem(995, 800000);
			} else if (c.getItems().playerHasItem(14884, 1)) {
				c.getItems().deleteItem(14884, 1);
				c.getItems().addItem(995, 600000);
			} else if (c.getItems().playerHasItem(14885, 1)) {
				c.getItems().deleteItem(14885, 1);
				c.getItems().addItem(995, 400000);
			} else if (c.getItems().playerHasItem(14886, 1)) {
				c.getItems().deleteItem(14886, 1);
				c.getItems().addItem(995, 300000);
			} else if (c.getItems().playerHasItem(14887, 1)) {
				c.getItems().deleteItem(14887, 1);
				c.getItems().addItem(995, 200000);
			} else if (c.getItems().playerHasItem(14888, 1)) {
				c.getItems().deleteItem(14888, 1);
				c.getItems().addItem(995, 160000);
			} else if (c.getItems().playerHasItem(14889, 1)) {
				c.getItems().deleteItem(14889, 1);
				c.getItems().addItem(995, 120000);
			} else if (c.getItems().playerHasItem(14890, 1)) {
				c.getItems().deleteItem(14890, 1);
				c.getItems().addItem(995, 80000);
			} else if (c.getItems().playerHasItem(14891, 1)) {
				c.getItems().deleteItem(14891, 1);
				c.getItems().addItem(995, 40000);
			} else if (c.getItems().playerHasItem(14892, 1)) {
				c.getItems().deleteItem(14892, 1);
				c.getItems().addItem(995, 20000);
			}
			c.getPA().closeAllWindows();
			break;

		case 106:
			sendOption5("One 6-sided die", "Two 6-sided dice",
					"One 4-sided die", "One 8-sided die", "More...");
			c.dialogueAction = 106;
			c.nextChat = 0;
			break;

		case 107:
			sendOption5("One 10-sided die", "One 12-sided die",
					"One 20-sided die", "Two 10-sided dice for 1-100",
					"Back...");
			c.dialogueAction = 107;
			c.nextChat = 0;
			break;
			
		case 108:
			sendOption4("Barbarian Assault", "Pest Control", "Barrows", "Next Page");
			c.dialogueAction = 108;
			c.nextChat = 0;
		break;
			
		case 109:
			sendOption4("Jad", "Spirit Warriors", "Champions Challenge", "Previous");
			c.dialogueAction = 109;
			c.nextChat = 0;
		break;
		
		case 190:
                        sendOption4("Low Level Dungeon", "Mid Level Dungeon", "High Level Dungeon", "Next Page");
                        c.dialogueAction = 120;
                        c.nextChat = 0;
                break;
        case 191:
                        sendOption4("Range & Magic Training", "Slayer Dungeon", "Previous Page", "");
                        c.dialogueAction = 121;                    
                        c.nextChat = 0;
                break;
				
		case 200:
				sendOption4("Boss Portal Room", "Revenants Dungeon", "Godwars", "Next Page");
				c.dialogueAction = 200;
		break;
		
		case 201:
				sendOption5("Kalphite Queen", "Barrelchest Chest", "Primal Warriors[@red@WILDY]", "Primal Weapons Boss [@red@WILDY]", "Previous Page");
				c.dialogueAction = 201;
		break;
		
		case 202:
			sendOption4("Agility", "Runecrafting", "Fishing", "Next Page");
			c.dialogueAction = 202;
		break;
		
		case 203:
			sendOption4("Woodcutting", "Farming", "Mining", "Previous Page");
			c.dialogueAction = 203;
		break;
		
		case 204:
			sendOption4("Single PvP Area", "Multi PvP Area", "Fun PK", "");
			c.dialogueAction = 204;
		break;

		case 99:
			sendNpcChat4("Welcome to my PVP exchange shop. I add new",
					"items to my shop depending on how many kills",
					"you have made. Every kill you make I add",
					"a new item to the shop. Would you like to access it?",
					c.talkingNpc, "King Lathas");
			c.nextChat = 100;
			break;

		case 100:
			sendOption2("Yes, please.", "No, thanks.");
			c.dialogueAction = 121;
			c.nextChat = 0;
			break;

		case 209:
			sendNpcChat4(
					"Hello!",
					"My name is Mazchna and I am a master of the slayer skill.",
					"I can assign you a slayer task suitable to your combat level.",
					"Would you like a slayer task?", c.talkingNpc, "Mazchna");
			c.nextChat = 210;
			break;
		case 210:
			c.dialogueAction = 5;
			sendOption2("Yes I would like a slayer task.",
					"No I would not like a slayer task.");
			c.nextChat = 0;
			break;
		case 211:
			sendNpcChat4(
					"Hello!",
					"My name is Mazchna and I am a master of the slayer skill.",
					"I see I have already assigned you a task to complete.",
					"Would you like me to give you an easier task?",
					c.talkingNpc, "Mazchna");
			c.nextChat = 212;
			break;
		case 212:
			c.dialogueAction = 6;
			sendOption2("Yes I would like an easier task.",
					"No I would like to keep my task.");
			c.nextChat = 0;
			break;

		}
	}

	/*
	 * Information Box
	 */

	public void sendStartInfo(String text, String text1, String text2,
			String text3, String title) {
		c.getPA().sendFrame126(title, 6180);
		c.getPA().sendFrame126(text, 6181);
		c.getPA().sendFrame126(text1, 6182);
		c.getPA().sendFrame126(text2, 6183);
		c.getPA().sendFrame126(text3, 6184);
		c.getPA().sendFrame164(6179);
	}

	public static void sendOption(Client c, String s, String s1) {
		c.getPA().sendFrame126("Select an Option", 2460);
		c.getPA().sendFrame126(s, 2461);
		c.getPA().sendFrame126(s1, 2462);
		c.getPA().sendFrame164(2459);
	}

	/*
	 * Options
	 */

	public void sendOption(String s) {
		c.getPA().sendFrame126("Select an Option", 2470);
		c.getPA().sendFrame126(s, 2471);
		c.getPA().sendFrame126("Click here to continue", 2473);
		c.getPA().sendFrame164(13758);
	}

	public void sendOption2(String s, String s1) {
		c.getPA().sendFrame126("Select an Option", 2460);
		c.getPA().sendFrame126(s, 2461);
		c.getPA().sendFrame126(s1, 2462);
		c.getPA().sendFrame164(2459);
	}

	public void sendOption3(String s, String s1, String s2) {
		c.getPA().sendFrame126("Select an Option", 2470);
		c.getPA().sendFrame126(s, 2471);
		c.getPA().sendFrame126(s1, 2472);
		c.getPA().sendFrame126(s2, 2473);
		c.getPA().sendFrame164(2469);
	}

	public void sendOption4(String s, String s1, String s2, String s3) {
		c.getPA().sendFrame126("Select an Option", 2481);
		c.getPA().sendFrame126(s, 2482);
		c.getPA().sendFrame126(s1, 2483);
		c.getPA().sendFrame126(s2, 2484);
		c.getPA().sendFrame126(s3, 2485);
		c.getPA().sendFrame164(2480);
	}

	public void sendOption5(String s, String s1, String s2, String s3, String s4) {
		c.getPA().sendFrame126("Select an Option", 2493);
		c.getPA().sendFrame126(s, 2494);
		c.getPA().sendFrame126(s1, 2495);
		c.getPA().sendFrame126(s2, 2496);
		c.getPA().sendFrame126(s3, 2497);
		c.getPA().sendFrame126(s4, 2498);
		c.getPA().sendFrame164(2492);
	}

	/*
	 * Statements
	 */

	public void sendStatement(String s) { // 1 line click here to continue chat
		// box interface
		c.getPA().sendFrame126(s, 357);
		c.getPA().sendFrame126("Click here to continue", 358);
		c.getPA().sendFrame164(356);
	}

	public static void sendStatement(Client c, String s) { // 1 line click here
		// to continue chat
		// box interface
		c.getPA().sendFrame126(s, 357);
		c.getPA().sendFrame126("Click here to continue", 358);
		c.getPA().sendFrame164(356);
	}

	/*
	 * Npc Chatting
	 */

	public void sendNpcChat1(String s, int ChatNpc, String name) {
		c.getPA().sendFrame200(4883, 9847);
		c.getPA().sendFrame126(name, 4884);
		c.getPA().sendFrame126(s, 4885);
		c.getPA().sendFrame75(ChatNpc, 4883);
		c.getPA().sendFrame164(4882);
	}

	public void sendNpcChat2(String s, String s1, int ChatNpc, String name) {
		c.getPA().sendFrame200(4888, 9847);
		c.getPA().sendFrame126(name, 4889);
		c.getPA().sendFrame126(s, 4890);
		c.getPA().sendFrame126(s1, 4891);
		c.getPA().sendFrame75(ChatNpc, 4888);
		c.getPA().sendFrame164(4887);
	}

	public void sendNpcChat3(String s, String s1, String s2, int ChatNpc,
			String name) {
		c.getPA().sendFrame200(4894, 9847); // Was 591
		c.getPA().sendFrame126(name, 4895);
		c.getPA().sendFrame126(s, 4896);
		c.getPA().sendFrame126(s1, 4897);
		c.getPA().sendFrame126(s2, 4898);
		c.getPA().sendFrame75(ChatNpc, 4894);
		c.getPA().sendFrame164(4893);
	}

	private void sendNpcChat4(String s, String s1, String s2, String s3,
			int ChatNpc, String name) {
		c.getPA().sendFrame200(4901, 9847);
		c.getPA().sendFrame126(name, 4902);
		c.getPA().sendFrame126(s, 4903);
		c.getPA().sendFrame126(s1, 4904);
		c.getPA().sendFrame126(s2, 4905);
		c.getPA().sendFrame126(s3, 4906);
		c.getPA().sendFrame75(ChatNpc, 4901);
		c.getPA().sendFrame164(4900);
	}

	/*
	 * Player Chating Back
	 */

	private void sendPlayerChat1(String s) {
		c.getPA().sendFrame200(969, 591);
		c.getPA().sendFrame126(c.playerName, 970);
		c.getPA().sendFrame126(s, 971);
		c.getPA().sendFrame185(969);
		c.getPA().sendFrame164(968);
	}

	private void sendPlayerChat2(String s, String s1) {
		c.getPA().sendFrame200(974, 591);
		c.getPA().sendFrame126(c.playerName, 975);
		c.getPA().sendFrame126(s, 976);
		c.getPA().sendFrame126(s1, 977);
		c.getPA().sendFrame185(974);
		c.getPA().sendFrame164(973);
	}

	private void sendPlayerChat3(String s, String s1, String s2) {
		c.getPA().sendFrame200(980, 591);
		c.getPA().sendFrame126(c.playerName, 981);
		c.getPA().sendFrame126(s, 982);
		c.getPA().sendFrame126(s1, 983);
		c.getPA().sendFrame126(s2, 984);
		c.getPA().sendFrame185(980);
		c.getPA().sendFrame164(979);
	}

	private void sendPlayerChat4(String s, String s1, String s2, String s3) {
		c.getPA().sendFrame200(987, 591);
		c.getPA().sendFrame126(c.playerName, 988);
		c.getPA().sendFrame126(s, 989);
		c.getPA().sendFrame126(s1, 990);
		c.getPA().sendFrame126(s2, 991);
		c.getPA().sendFrame126(s3, 992);
		c.getPA().sendFrame185(987);
		c.getPA().sendFrame164(986);
	}
}
