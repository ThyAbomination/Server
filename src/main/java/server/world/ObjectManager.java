package server.world;

import java.util.concurrent.CopyOnWriteArrayList;

import server.Server;
import server.model.objects.Object;
import server.model.players.Client;
import server.model.players.Player;
import server.model.players.PlayerHandler;
import server.util.Misc;

/**
 * @author Sanity
 */

public class ObjectManager {

	public CopyOnWriteArrayList<Object> objects = new CopyOnWriteArrayList<Object>();
	private CopyOnWriteArrayList<Object> toRemove = new CopyOnWriteArrayList<Object>();

	public ObjectManager() {
		objects = new CopyOnWriteArrayList<Object>();
	}

	public void process() {
		for (final Object o : objects) {
			if (o != null) {
				if (o.tick > 0) {
					o.tick--;
				} else {
					updateObject(o);
					toRemove.add(o);
				}
			}
		}
		for (final Object o : toRemove) {
			if (o != null) {
				if (o.objectId == 2732) {
					for (final Player player : PlayerHandler.players) {
						if (player != null) {
							final Client c = (Client) player;
							Server.itemHandler.createGroundItem(c, 592,
									o.objectX, o.objectY, 1, c.playerId);
						}
					}
				}
				if (isObelisk(o.newId)) {
					final int index = getObeliskIndex(o.newId);
					if (activated[index]) {
						activated[index] = false;
						teleportObelisk(index);
					}
				}
				objects.remove(o);
			}
		}
		toRemove.clear();
	}

	public boolean objectExists(final int x, final int y) {
		for (Object o : objects) {
			if (o.objectX == x && o.objectY == y) {
				return true;
			}
		}
		return false;
	}

	public void removeObject(int x, int y) {
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null) {
				Client c = (Client) PlayerHandler.players[j];
				c.getPA().object(-1, x, y, 0, 10);
			}
		}
	}

	public Object objectExists(int x, int y, int height) {
		for (Object o : objects) {
			if (o.getX() == x && o.getY() == y && o.getHeight() == height) {
				return o;
			}
		}
		return null;
	}

	public void removeVines(Client c, int x, int y) {
		removeObject(x, y);
		if (objectExists(x, y, 0) == null) {
			Object o = new Object(5108, x, y, 0, -1, 10, 5104, 20, false);
		} else {
			Object o = objectExists(x, y, 0);
			o.newId = 6951;
			updateObject(o);
			o.newId = 5104;
			o.tick = 20;
		}
		c.vineWalk = true;
		switch (c.getX()) {
		case 2689:
		case 2674:
		case 2693:
			c.squares = 2;
			break;

		case 2691:
		case 2676:
		case 2695:
			c.squares = -2;
			break;
		default:
			c.squares = 0;
			break;
		}
	}

	public void updateObject(Object o) {
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null) {
				Client c = (Client) PlayerHandler.players[j];
				c.getPA().object(o.newId, o.objectX, o.objectY, o.face, o.type);
			}
		}
	}

	public void placeObject(Object o) {
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null) {
				Client c = (Client) PlayerHandler.players[j];
				if (c.distanceToPoint(o.objectX, o.objectY) <= 60)
					c.getPA().object(o.objectId, o.objectX, o.objectY, o.face,
							o.type);
			}
		}
	}

	public Object getObject(int x, int y, int height) {
		for (Object o : objects) {
			if (o.objectX == x && o.objectY == y && o.height == height)
				return o;
		}
		return null;
	}

	public void loadObjects(Client c) {
		if (c == null)
			return;
		for (Object o : objects) {
			if (loadForPlayer(o, c))
				c.getPA().object(o.objectId, o.objectX, o.objectY, o.face,
						o.type);

		}
		loadCustomSpawns(c);

	}

	private int[][] customObjects = { {} };

	public void loadCustomSpawns(Client c) {
		c.getPA().checkObjectSpawn(-1, 3104, 3937, -1, 0);
		c.getPA().checkObjectSpawn(-1, 3110, 3939, -1, 0);
		c.getPA().checkObjectSpawn(-1, 3108, 3932, -1, 0);
		c.getPA().checkObjectSpawn(-1, 3110, 3928, -1, 0);
		c.getPA().checkObjectSpawn(-1, 3110, 3927, -1, 0);
		c.getPA().checkObjectSpawn(-1, 3105, 3925, -1, 0);
		c.getPA().checkObjectSpawn(-1, 3099, 3932, -1, 0);
		c.getPA().checkObjectSpawn(-1, 3097, 3938, -1, 0);
		c.getPA().checkObjectSpawn(444, 3092, 3934, 1, 10);
		c.getPA().checkObjectSpawn(444, 3092, 3933, 1, 10);
		c.getPA().checkObjectSpawn(444, 2529, 4645, 1, 10);
		c.getPA().checkObjectSpawn(444, 2529, 4646, 1, 10);
		c.getPA().checkObjectSpawn(444, 2529, 4647, 1, 10);
		c.getPA().checkObjectSpawn(444, 2529, 4649, 1, 10);
		c.getPA().checkObjectSpawn(444, 2530, 4650, 1, 10);
		c.getPA().checkObjectSpawn(444, 2530, 4651, 1, 10);
		c.getPA().checkObjectSpawn(444, 2531, 4652, 1, 10);
		c.getPA().checkObjectSpawn(444, 2531, 4654, 1, 10);
		c.getPA().checkObjectSpawn(444, 2531, 4655, 1, 10);
		c.getPA().checkObjectSpawn(-1, 3207, 3900, -1, 0);
		c.getPA().checkObjectSpawn(2469, 3093, 3933, 1, 10);
		c.getPA().checkObjectSpawn(2469, 3093, 3934, 1, 10);
		//c.getPA().checkObjectSpawn(-1, 3198, 3856, -1, 0);
		//c.getPA().checkObjectSpawn(-1, 3199, 3856, -1, 0);
		//c.getPA().checkObjectSpawn(-1, 3200, 3856, -1, 0);
		//c.getPA().checkObjectSpawn(-1, 3201, 3856, -1, 0);
		//c.getPA().checkObjectSpawn(-1, 3202, 3856, -1, 0);
		//c.getPA().checkObjectSpawn(-1, 3203, 3856, -1, 0);
		//c.getPA().checkObjectSpawn(-1, 3204, 3856, -1, 0);
		//c.getPA().checkObjectSpawn(-1, 3205, 3856, -1, 0);
		//c.getPA().checkObjectSpawn(-1, 3206, 3856, -1, 0);
		//c.getPA().checkObjectSpawn(2469, 3202, 3860, 1, 10);
		c.getPA().checkObjectSpawn(2466, 3285, 2776, 1, 10);//Unholy Cursebearer tele
		c.getPA().checkObjectSpawn(2403, 3435, 3568, 4, 10);//RFD CHEST
		c.getPA().checkObjectSpawn(-1, 2530, 4644, 1, 0);
		c.getPA().checkObjectSpawn(-1, 2529, 4648, 1, 0);
		c.getPA().checkObjectSpawn(-1, 2531, 4653, 1, 0);
		c.getPA().checkObjectSpawn(1596, 3008, 3850, 1, 0);
		c.getPA().checkObjectSpawn(1596, 3008, 3849, -1, 0);
		c.getPA().checkObjectSpawn(1551, 3253, 3266, -1, 0);
		c.getPA().checkObjectSpawn(1551, 3163, 3290, 4, 0);
		c.getPA().checkObjectSpawn(1553, 3236, 3295, -1, 0);
		c.getPA().checkObjectSpawn(1596, 3040, 10307, -1, 0);
		c.getPA().checkObjectSpawn(-1, 3217, 3219, -1, 10);
		c.getPA().checkObjectSpawn(-1, 3216, 3218, -1, 10);
		c.getPA().checkObjectSpawn(-1, 3298, 3203, 1, 10);
		c.getPA().checkObjectSpawn(11993, 3109, 3167, 2, 0);
		c.getPA().checkObjectSpawn(-1, 3107, 3162, 1, 10);
		c.getPA().checkObjectSpawn(1596, 3040, 10308, 1, 0);
		c.getPA().checkObjectSpawn(1596, 3022, 10311, -1, 0);
		c.getPA().checkObjectSpawn(1596, 3022, 10312, 1, 0);
		c.getPA().checkObjectSpawn(1596, 3044, 10341, -1, 0);
		c.getPA().checkObjectSpawn(1596, 3044, 10342, 1, 0);
		c.getPA().checkObjectSpawn(2213, 3047, 9779, 1, 0);
		c.getPA().checkObjectSpawn(2213, 3080, 9502, 1, 0);
		c.getPA().checkObjectSpawn(-1, 3305, 9375, 1, 0);
		c.getPA().checkObjectSpawn(-1, 3305, 9376, 1, 0);
		// doantor
		c.getPA().checkObjectSpawn(4006, 2529, 4776, 1, 10);
		// spirit warriors
		c.getPA().checkObjectSpawn(13291, 2560, 4959, 1, 10);
		c.getPA().checkObjectSpawn(13291, 2560, 4960, 1, 10);
		c.getPA().checkObjectSpawn(13291, 2559, 4959, 1, 10);
		c.getPA().checkObjectSpawn(13291, 2559, 4960, 1, 10);
		// gold ore
		c.getPA().checkObjectSpawn(2098, 2580, 4967, 1, 10);
		c.getPA().checkObjectSpawn(2098, 2580, 4955, 1, 10);
		c.getPA().checkObjectSpawn(2098, 2580, 4954, 1, 10);
		c.getPA().checkObjectSpawn(2098, 2580, 4953, 1, 10);
		c.getPA().checkObjectSpawn(2098, 2580, 4952, 1, 10);
		c.getPA().checkObjectSpawn(2098, 2564, 4939, 1, 10);
		c.getPA().checkObjectSpawn(2098, 2544, 4946, 1, 10);
		c.getPA().checkObjectSpawn(2098, 2544, 4945, 1, 10);
		c.getPA().checkObjectSpawn(2098, 2541, 4952, 1, 10);
		c.getPA().checkObjectSpawn(2098, 2541, 4951, 1, 10);
		c.getPA().checkObjectSpawn(2098, 2539, 4958, 1, 10);
		c.getPA().checkObjectSpawn(2098, 2539, 4959, 1, 10);
		c.getPA().checkObjectSpawn(2098, 2548, 4974, 1, 10);
		c.getPA().checkObjectSpawn(2098, 2549, 4975, 1, 10);

		c.getPA().checkObjectSpawn(436, 2744, 5101, 2, 10);
		c.getPA().checkObjectSpawn(437, 2743, 5101, 2, 10);
		c.getPA().checkObjectSpawn(438, 2742, 5101, 2, 10);
		c.getPA().checkObjectSpawn(439, 2741, 5101, 2, 10);
		c.getPA().checkObjectSpawn(436, 2740, 5101, 2, 10);
		c.getPA().checkObjectSpawn(437, 2739, 5101, 2, 10);
		c.getPA().checkObjectSpawn(438, 2738, 5101, 2, 10);
		c.getPA().checkObjectSpawn(439, 2737, 5101, 2, 10);
		c.getPA().checkObjectSpawn(436, 2728, 5091, 2, 10);
		c.getPA().checkObjectSpawn(437, 2728, 5092, 2, 10);
		c.getPA().checkObjectSpawn(438, 2728, 5093, 2, 10);
		c.getPA().checkObjectSpawn(436, 2728, 5097, 2, 10);
		c.getPA().checkObjectSpawn(437, 2729, 5098, 2, 10);
		c.getPA().checkObjectSpawn(438, 2733, 5074, 2, 10);
		c.getPA().checkObjectSpawn(439, 2733, 5073, 2, 10);
		c.getPA().checkObjectSpawn(436, 2746, 5070, 2, 10);
		c.getPA().checkObjectSpawn(437, 2747, 5070, 2, 10);
		c.getPA().checkObjectSpawn(438, 2744, 5100, 2, 10);
		c.getPA().checkObjectSpawn(439, 2744, 5099, 2, 10);
		c.getPA().checkObjectSpawn(436, 2747, 5095, 2, 10);
		c.getPA().checkObjectSpawn(437, 2748, 5085, 2, 10);
		c.getPA().checkObjectSpawn(438, 2748, 5086, 2, 10);

		if (c.heightLevel == 0) {
			// alters
			c.getPA().checkObjectSpawn(6552, 3437, 3568, 4, 10);
			c.getPA().checkObjectSpawn(8749, 3441, 3568, 4, 10);
			c.getPA().checkObjectSpawn(409, 3429, 3568, 4, 10);
			c.getPA().checkObjectSpawn(61, 3433, 3568, 4, 10);
			c.getPA().checkObjectSpawn(409, 3085, 3510, 1, 10);
			// Home bank booths
			c.getPA().checkObjectSpawn(10517, 3436, 3576, 2, 10);
			c.getPA().checkObjectSpawn(10517, 3435, 3576, 2, 10);
			c.getPA().checkObjectSpawn(3918, 3437, 3576, 2, 10);
			c.getPA().checkObjectSpawn(3918, 3434, 3576, 2, 10);
			// home portals
			c.getPA().checkObjectSpawn(2467, 3444, 3576, 2, 10);// single
			c.getPA().checkObjectSpawn(2470, 3446, 3576, 2, 10);// multi
			c.getPA().checkObjectSpawn(2466, 3448, 3576, 2, 10);// duel
			c.getPA().checkObjectSpawn(2466, 3450, 3576, 2, 10);// range
			c.getPA().checkObjectSpawn(2466, 3423, 3576, 2, 10);// fish
			c.getPA().checkObjectSpawn(2466, 3420, 3576, 2, 10);// lowlev
			c.getPA().checkObjectSpawn(2466, 3417, 3575, 2, 10);// midlev
			c.getPA().checkObjectSpawn(2466, 3414, 3576, 2, 10);// highlev
			c.getPA().checkObjectSpawn(2466, 3411, 3576, 2, 10);// boss
			c.getPA().checkObjectSpawn(2466, 3409, 3576, 2, 10);// kalphite
			c.getPA().checkObjectSpawn(2466, 3407, 3576, 2, 10);// slayer
																// dungeon
			c.getPA().checkObjectSpawn(2466, 3451, 3574, 2, 10);// champions
			c.getPA().checkObjectSpawn(2466, 3406, 3574, 2, 10);// jad
			c.getPA().checkObjectSpawn(2466, 3406, 3572, 2, 10);// barrelchest
			c.getPA().checkObjectSpawn(2466, 3408, 3570, 2, 10);// barrows
			c.getPA().checkObjectSpawn(2466, 3451, 3572, 2, 10);// barb assault
			c.getPA().checkObjectSpawn(2466, 3449, 3570, 2, 10);// pest control
			c.getPA().checkObjectSpawn(2466, 3410, 3569, 2, 10);// godwars
			c.getPA().checkObjectSpawn(2466, 3413, 3569, 2, 10);// sw
			c.getPA().checkObjectSpawn(2466, 3415, 3569, 2, 10);// portal room
			c.getPA().checkObjectSpawn(2466, 3285, 2767, 2, 10);// frost dragons
			c.getPA().checkObjectSpawn(2466, 3277, 2767, 2, 10);// bork
			c.getPA().checkObjectSpawn(2466, 3285, 2770, 2, 10);// avatars and
																// demons
			c.getPA().checkObjectSpawn(2466, 3277, 2770, 2, 10);// corporal
			c.getPA().checkObjectSpawn(2466, 3277, 2773, 2, 10);// nomad
			c.getPA().checkObjectSpawn(2466, 3285, 2773, 2, 10);// demons
			c.getPA().checkObjectSpawn(2466, 3277, 2776, 2, 10);// Nex
			// back home portals
			// c.getPA().checkObjectSpawn(2469, 3108, 3690, 2, 10);// single pvp
			c.getPA().checkObjectSpawn(2469, 3034, 3693, 2, 10);// multi pvp
			c.getPA().checkObjectSpawn(2469, 3565, 3311, 2, 10);// barrows
			c.getPA().checkObjectSpawn(2469, 2979, 3964, 2, 10);// frosts, ice
			c.getPA().checkObjectSpawn(2469, 3223, 3945, 2, 10);// bork
			c.getPA().checkObjectSpawn(2469, 3281, 2765, 2, 10);// portal room
			c.getPA().checkObjectSpawn(2466, 3417, 3566, 2, 10);// minning tele
			c.getPA().checkObjectSpawn(2466, 3045, 9782, 3, 10);// smithing tele from mining
			// object deletion
			c.getPA().checkObjectSpawn(-1, 3419, 3564, 2, 10);
			c.getPA().checkObjectSpawn(-1, 2615, 9450, 2, 10);
			c.getPA().checkObjectSpawn(-1, 2610, 9450, 2, 10);
			c.getPA().checkObjectSpawn(-1, 2612, 9452, 2, 10);
			c.getPA().checkObjectSpawn(-1, 3432, 3568, 4, 10);
			c.getPA().checkObjectSpawn(-1, 3436, 3568, 4, 10);
			c.getPA().checkObjectSpawn(-1, 3440, 3568, 4, 10);
			c.getPA().checkObjectSpawn(-1, 3428, 3568, 4, 10);
			c.getPA().checkObjectSpawn(-1, 3426, 3570, 2, 10);
			c.getPA().checkObjectSpawn(-1, 2608, 4777, 2, 10);
			c.getPA().checkObjectSpawn(-1, 2601, 4774, 2, 10);
			c.getPA().checkObjectSpawn(-1, 3411, 3577, 2, 10);
			c.getPA().checkObjectSpawn(-1, 3248, 9364, 2, 10);
			c.getPA().checkObjectSpawn(-1, 3419, 3563, 2, 10);
			c.getPA().checkObjectSpawn(-1, 3419, 3562, 2, 10);
			c.getPA().checkObjectSpawn(-1, 3417, 3562, 2, 10);
			c.getPA().checkObjectSpawn(-1, 3420, 3562, 2, 10);
			c.getPA().checkObjectSpawn(-1, 3405, 3572, 2, 10);
			c.getPA().checkObjectSpawn(-1, 3444, 3570, 2, 10);
			c.getPA().checkObjectSpawn(-1, 2382, 4456, 2, 10); //max guild
			c.getPA().checkObjectSpawn(-1, 2382, 4456, 1, 0);//  h 1 attempt
			c.getPA().checkObjectSpawn(-1, 2380, 4459, 2, 0); // 
			c.getPA().checkObjectSpawn(-1, 2380, 4460, 2, 0); //
			c.getPA().checkObjectSpawn(-1, 2381, 4461, 2, 0); //
			c.getPA().checkObjectSpawn(-1, 2384, 4460, 2, 0); //
			c.getPA().checkObjectSpawn(-1, 2384, 4460, 1, 0); // h 1 attempt tree north
			c.getPA().checkObjectSpawn(-1, 2380, 4457, 2, 0); //
			c.getPA().checkObjectSpawn(-1, 2380, 4456, 2, 0); // 
			c.getPA().checkObjectSpawn(-1, 2381, 4455, 2, 0); // 
			c.getPA().checkObjectSpawn(-1, 2383, 4454, 2, 0); //  			
			c.getPA().checkObjectSpawn(-1, 2380, 4458, 2, 0); // 
			c.getPA().checkObjectSpawn(-1, 2530, 4652, 2, 0);// corp addy ore ? lol
			c.getPA().checkObjectSpawn(-1, 2530, 4654, 2, 0);// corp addy ore ? lol
			// Farming area
			c.getPA().checkObjectSpawn(10517, 2817, 3463, 3, 10);
			c.getPA().checkObjectSpawn(3918, 2817, 3462, 3, 10);
			c.getPA().checkObjectSpawn(3918, 2817, 3464, 3, 10);
			// Smith & mining area
			c.getPA().checkObjectSpawn(3044, 3422, 3562, 3, 10);
			c.getPA().checkObjectSpawn(2783, 3425, 3566, 3, 10);
			c.getPA().checkObjectSpawn(10517, 3425, 3569, 3, 10);
			c.getPA().checkObjectSpawn(3918, 3425, 3570, 2, 10);
			c.getPA().checkObjectSpawn(3918, 3425, 3568, 2, 10);
			// Ore rocks
			//c.getPA().checkObjectSpawn(2090, 3416, 3566, 2, 10);
			//c.getPA().checkObjectSpawn(2094, 3416, 3564, 2, 10);
			//c.getPA().checkObjectSpawn(2093, 3416, 3562, 2, 10);
			//c.getPA().checkObjectSpawn(2101, 3418, 3562, 2, 10);
			//c.getPA().checkObjectSpawn(2096, 3420, 3562, 2, 10);
			//c.getPA().checkObjectSpawn(2102, 3421, 3565, 2, 10);
			//c.getPA().checkObjectSpawn(2098, 3419, 3567, 2, 10);
			//c.getPA().checkObjectSpawn(2104, 3417, 3567, 2, 10);
			//c.getPA().checkObjectSpawn(2106, 3421, 3567, 2, 10);
			// Home stalls
			c.getPA().checkObjectSpawn(10517, 3436, 3553, 3, 10);
			c.getPA().checkObjectSpawn(10517, 3436, 3554, 3, 10);
			c.getPA().checkObjectSpawn(3918, 3436, 3555, 3, 10);
			c.getPA().checkObjectSpawn(3918, 3436, 3552, 3, 10);
			c.getPA().checkObjectSpawn(2564, 3441, 3566, 2, 10);
			c.getPA().checkObjectSpawn(2563, 3438, 3566, 2, 10);
			c.getPA().checkObjectSpawn(2560, 3435, 3566, 2, 10);
			c.getPA().checkObjectSpawn(2565, 3440, 3557, 4, 10);
			c.getPA().checkObjectSpawn(2562, 3437, 3557, 4, 10);
			c.getPA().checkObjectSpawn(-1, 3441, 3557, 2, 10);
			c.getPA().checkObjectSpawn(-1, 3442, 3557, 2, 10);
			c.getPA().checkObjectSpawn(-1, 3438, 3557, 2, 10);
			// main skilling area
			c.getPA().checkObjectSpawn(114, 3440, 3551, 3, 10);
			c.getPA().checkObjectSpawn(10517, 3437, 3553, 1, 10);
			c.getPA().checkObjectSpawn(10517, 3437, 3554, 1, 10);
			c.getPA().checkObjectSpawn(3918, 3437, 3555, 1, 10);
			c.getPA().checkObjectSpawn(3918, 3437, 3552, 1, 10);
			c.getPA().checkObjectSpawn(2466, 3448, 3555, 2, 10);// ?
			c.getPA().checkObjectSpawn(2466, 3446, 3555, 2, 10);// ?
			c.getPA().checkObjectSpawn(2466, 3444, 3555, 2, 10);// ?
			c.getPA().checkObjectSpawn(2466, 3442, 3555, 2, 10);// ?
			c.getPA().checkObjectSpawn(2466, 3440, 3555, 2, 10);// ?
			c.getPA().checkObjectSpawn(153, 3448, 3549, 2, 10);
			c.getPA().checkObjectSpawn(-1, 3438, 3555, 3, 10);
			c.getPA().checkObjectSpawn(-1, 3438, 3554, 3, 10);
			c.getPA().checkObjectSpawn(-1, 3438, 3556, 3, 10);
			c.getPA().checkObjectSpawn(-1, 3439, 3555, 3, 10);
			// range & mage area
			c.getPA().checkObjectSpawn(436, 2859, 9850, 2, 10);
			c.getPA().checkObjectSpawn(437, 2859, 9851, 2, 10);
			c.getPA().checkObjectSpawn(438, 2859, 9852, 2, 10);
			c.getPA().checkObjectSpawn(439, 2859, 9853, 2, 10);
			c.getPA().checkObjectSpawn(436, 2859, 9854, 2, 10);
			c.getPA().checkObjectSpawn(436, 2871, 9829, 2, 10);
			c.getPA().checkObjectSpawn(437, 2870, 9829, 2, 10);
			c.getPA().checkObjectSpawn(438, 2869, 9829, 2, 10);
			c.getPA().checkObjectSpawn(439, 2868, 9829, 2, 10);
			c.getPA().checkObjectSpawn(-1, 2869, 9841, 2, 10);
			c.getPA().checkObjectSpawn(-1, 2865, 9842, 2, 10);
			c.getPA().checkObjectSpawn(-1, 2872, 9836, 2, 10);
			c.getPA().checkObjectSpawn(10517, 2871, 9842, 3, 10);
			c.getPA().checkObjectSpawn(3918, 2871, 9841, 3, 10);
			c.getPA().checkObjectSpawn(3918, 2871, 9843, 3, 10);

			// bosses and revs
			c.getPA().checkObjectSpawn(10517, 3160, 9571, 4, 10);
			c.getPA().checkObjectSpawn(3918, 3159, 9571, 4, 10);
			// woodcutting
			c.getPA().checkObjectSpawn(1276, 2611, 4771, 4, 10);
			c.getPA().checkObjectSpawn(1276, 2609, 4769, 4, 10);
			c.getPA().checkObjectSpawn(1281, 2604, 4767, 4, 10);
			c.getPA().checkObjectSpawn(1281, 2601, 4769, 4, 10);
			c.getPA().checkObjectSpawn(1307, 2598, 4771, 4, 10);
			c.getPA().checkObjectSpawn(1307, 2595, 4773, 4, 10);
			c.getPA().checkObjectSpawn(1309, 2595, 4777, 4, 10);
			c.getPA().checkObjectSpawn(1309, 2596, 4780, 4, 10);
			c.getPA().checkObjectSpawn(1309, 2599, 4780, 4, 10);
			c.getPA().checkObjectSpawn(1306, 2602, 4780, 4, 10);
			c.getPA().checkObjectSpawn(1306, 2605, 4780, 4, 10);
			c.getPA().checkObjectSpawn(1306, 2608, 4780, 4, 10);
			c.getPA().checkObjectSpawn(10517, 2602, 4775, 3, 10);
			c.getPA().checkObjectSpawn(3918, 2602, 4774, 3, 10);
			c.getPA().checkObjectSpawn(3918, 2602, 4776, 3, 10);
			//minning area
			c.getPA().checkObjectSpawn(10517, 3045, 9776, 2, 10);// bank
			c.getPA().checkObjectSpawn(3918, 3046, 9776, 3, 10);// elven lamp
			c.getPA().checkObjectSpawn(3918, 3044, 9776, 3, 10);// elven lamp
			c.getPA().checkObjectSpawn(2106, 3056, 9772, 2, 10);// rune ore			
			c.getPA().checkObjectSpawn(2106, 3054, 9770, 2, 10);// rune ore			
			c.getPA().checkObjectSpawn(2106, 3052, 9769, 2, 10);// rune ore
			c.getPA().checkObjectSpawn(2106, 3055, 9771, 2, 10);// rune ore
			c.getPA().checkObjectSpawn(2106, 3053, 9770, 2, 10);// rune ore
			c.getPA().checkObjectSpawn(2104, 3035, 9765, 2, 10);// addy ore
			c.getPA().checkObjectSpawn(2104, 3036, 9761, 2, 10);// addy ore			
			c.getPA().checkObjectSpawn(2104, 3035, 9762, 2, 10);// addy ore			
			c.getPA().checkObjectSpawn(2102, 3038, 9770, 2, 10);// mith ore
			c.getPA().checkObjectSpawn(2102, 3036, 9770, 2, 10);// mith ore
			c.getPA().checkObjectSpawn(2098, 3033, 9776, 2, 10);// gold ore
			c.getPA().checkObjectSpawn(2098, 3034, 9777, 2, 10);// gold ore
			c.getPA().checkObjectSpawn(2098, 3035, 9778, 2, 10);// gold ore		
			//max guild
			c.getPA().checkObjectSpawn(10517, 2383, 4462, 2, 10);// bank
			c.getPA().checkObjectSpawn(10517, 2384, 4462, 2, 10);// bank
			c.getPA().checkObjectSpawn(3918, 2382, 4462, 3, 10);// elven lamp
			c.getPA().checkObjectSpawn(3918, 2385, 4462, 3, 10);// elven lamp
			// new max
			c.getPA().checkObjectSpawn(2466, 2033, 4539, 2, 10); // tele to mining
			c.getPA().checkObjectSpawn(2466, 2031, 4539, 2, 10); // tele to skilling 
			c.getPA().checkObjectSpawn(10517, 2036, 4540, 2, 10);// bank
			c.getPA().checkObjectSpawn(10517, 2037, 4540, 2, 10);// bank
			c.getPA().checkObjectSpawn(-1, 2039, 4521, 1, 10); // 1 pillar
			c.getPA().checkObjectSpawn(-1, 2042, 4526, 1, 10); //
			c.getPA().checkObjectSpawn(-1, 2042, 4531, 1, 10); //
			c.getPA().checkObjectSpawn(-1, 2039, 4535, 1, 10); //
			c.getPA().checkObjectSpawn(-1, 2034, 4535, 1, 10); //
			c.getPA().checkObjectSpawn(-1, 2031, 4531, 1, 10); //
			c.getPA().checkObjectSpawn(-1, 2031, 4526, 1, 10); //
			c.getPA().checkObjectSpawn(-1, 2034, 4521, 1, 10); // 8 pillar
			c.getPA().checkObjectSpawn(-1, 2037, 4528, 1, 10); // table 1
			c.getPA().checkObjectSpawn(-1, 2036, 4528, 1, 10); //
			c.getPA().checkObjectSpawn(-1, 2037, 4529, 1, 10); //
			c.getPA().checkObjectSpawn(-1, 2036, 4528, 1, 10); // table 4
			c.getPA().checkObjectSpawn(-1, 2044, 4539, 1, 10); // chair 1
			c.getPA().checkObjectSpawn(-1, 2044, 4538, 1, 10); // wall table
			c.getPA().checkObjectSpawn(-1, 2044, 4537, 1, 10); // wall table
			c.getPA().checkObjectSpawn(-1, 2044, 4536, 1, 10); // chair 2
			c.getPA().checkObjectSpawn(-1, 2044, 4535, 1, 10); // chair 3
			c.getPA().checkObjectSpawn(-1, 2044, 4534, 1, 10); // wall table
			c.getPA().checkObjectSpawn(-1, 2044, 4533, 1, 10); // wall table
			c.getPA().checkObjectSpawn(-1, 2044, 4532, 1, 10); // chair 4
			c.getPA().checkObjectSpawn(-1, 2044, 4526, 1, 10); // chair 5
			c.getPA().checkObjectSpawn(-1, 2044, 4525, 1, 10); // wall table
			c.getPA().checkObjectSpawn(-1, 2044, 4524, 1, 10); // wall table
			c.getPA().checkObjectSpawn(-1, 2044, 4523, 1, 10); // chair 6
			c.getPA().checkObjectSpawn(-1, 2044, 4522, 1, 10); // chair 7
			c.getPA().checkObjectSpawn(-1, 2044, 4521, 1, 10); // wall table
			c.getPA().checkObjectSpawn(-1, 2044, 4520, 1, 10); // wall table
			c.getPA().checkObjectSpawn(-1, 2044, 4519, 1, 10); // chair 8
			c.getPA().checkObjectSpawn(-1, 2029, 4539, 1, 10); // chair 1
			c.getPA().checkObjectSpawn(-1, 2029, 4538, 1, 10); // wall table
			c.getPA().checkObjectSpawn(-1, 2029, 4537, 1, 10); // wall table
			c.getPA().checkObjectSpawn(-1, 2029, 4536, 1, 10); // chair 2
			c.getPA().checkObjectSpawn(-1, 2029, 4535, 1, 10); // chair 3
			c.getPA().checkObjectSpawn(-1, 2029, 4534, 1, 10); // wall table
			c.getPA().checkObjectSpawn(-1, 2029, 4533, 1, 10); // wall table
			c.getPA().checkObjectSpawn(-1, 2029, 4532, 1, 10); // chair 4
			c.getPA().checkObjectSpawn(-1, 2029, 4526, 1, 10); // chair 5
			c.getPA().checkObjectSpawn(-1, 2029, 4525, 1, 10); // wall table
			c.getPA().checkObjectSpawn(-1, 2029, 4524, 1, 10); // wall table
			c.getPA().checkObjectSpawn(-1, 2029, 4523, 1, 10); // chair 6
			c.getPA().checkObjectSpawn(-1, 2029, 4522, 1, 10); // chair 7
			c.getPA().checkObjectSpawn(-1, 2029, 4521, 1, 10); // wall table
			c.getPA().checkObjectSpawn(-1, 2029, 4520, 1, 10); // wall table
			c.getPA().checkObjectSpawn(-1, 2029, 4519, 1, 10); // chair 8
			c.getPA().checkObjectSpawn(-1, 2036, 4522, 1, 10); // throne
			c.getPA().checkObjectSpawn(-1, 2036, 4521, 1, 10); // 
			c.getPA().checkObjectSpawn(-1, 2037, 4522, 1, 10); //  
			c.getPA().checkObjectSpawn(-1, 2037, 4521, 1, 10); // throne end
		
			
		}
	}

	public final int IN_USE_ID = 14825;

	public boolean isObelisk(int id) {
		for (int j = 0; j < obeliskIds.length; j++) {
			if (obeliskIds[j] == id)
				return true;
		}
		return false;
	}

	public int[] obeliskIds = { 14829, 14830, 14827, 14828, 14826, 14831 };
	public int[][] obeliskCoords = { { 3154, 3618 }, { 3225, 3665 },
			{ 3033, 3730 }, { 3104, 3792 }, { 2978, 3864 }, { 3305, 3914 } };
	public boolean[] activated = { false, false, false, false, false, false };

	public void startObelisk(int obeliskId) {
		int index = getObeliskIndex(obeliskId);
		if (index >= 0) {
			if (!activated[index]) {
				activated[index] = true;
				addObject(new Object(14825, obeliskCoords[index][0],
						obeliskCoords[index][1], 0, -1, 10, obeliskId, 16,
						false));
				addObject(new Object(14825, obeliskCoords[index][0] + 4,
						obeliskCoords[index][1], 0, -1, 10, obeliskId, 16,
						false));
				addObject(new Object(14825, obeliskCoords[index][0],
						obeliskCoords[index][1] + 4, 0, -1, 10, obeliskId, 16,
						false));
				addObject(new Object(14825, obeliskCoords[index][0] + 4,
						obeliskCoords[index][1] + 4, 0, -1, 10, obeliskId, 16,
						false));
			}
		}
	}

	public int getObeliskIndex(int id) {
		for (int j = 0; j < obeliskIds.length; j++) {
			if (obeliskIds[j] == id)
				return j;
		}
		return -1;
	}

	public void teleportObelisk(int port) {
		int random = Misc.random(5);
		while (random == port) {
			random = Misc.random(5);
		}
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null) {
				Client c = (Client) PlayerHandler.players[j];
				int xOffset = c.absX - obeliskCoords[port][0];
				int yOffset = c.absY - obeliskCoords[port][1];
				if (c.goodDistance(c.getX(), c.getY(),
						obeliskCoords[port][0] + 2, obeliskCoords[port][1] + 2,
						1)) {
					c.getPA().startTeleport2(
							obeliskCoords[random][0] + xOffset,
							obeliskCoords[random][1] + yOffset, 0);
				}
			}
		}
	}

	public boolean loadForPlayer(Object o, Client c) {
		if (o == null || c == null)
			return false;
		return c.distanceToPoint(o.objectX, o.objectY) <= 60
				&& c.heightLevel == o.height;
	}

	public void addObject(Object o) {
		if (getObject(o.objectX, o.objectY, o.height) == null) {
			objects.add(o);
			placeObject(o);
		}
	}

}