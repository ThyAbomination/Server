package server.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import server.model.players.Client;

/**
 * 
 * @author Randon / Jerba
 *
 *
 */

public class VoteHandler {

	public static boolean Vote = true;
	
	final static int[][] itemData = {{6199, 1}}; // {{itemID, amount}, {itemID, amount}}
	
	private static final String DB = "";
	private static final String URL = "";
	private static final String USER = "";
	private static final String PASS = "";
	private static final Properties prop;
	static {
		prop = new Properties();
		prop.put("user", USER);
		prop.put("password", PASS);
		//prop.put("autoReconnect", "true");
		//prop.put("maxReconnects", "4");
	}

	public static Connection conn = null;

	/**
	 * Connects to the database
	 */
	public static synchronized void connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://" + URL + "/" + DB, prop);
			System.out.println("Vote Handler: Success");
		} catch (Exception e) {
			System.out.println("Vote Handler Error: "+ e);
			System.out.println("Setting vote to false to help not cause anymore errors.");
			Vote = false;
		}
	}
	
	public static synchronized Connection getConnection() {
		try {
			if (conn == null || conn.isClosed()) {
				conn = DriverManager.getConnection("jdbc:mysql://" + URL + "/"+ DB, prop);
			}
		} catch (SQLException e) {
			System.out.println(e);
			e.printStackTrace();
			Vote = false;
		}
		return conn;
	}
	
	
	/**
	 * giveItems, does a loop to give the player all of the items in the array
	 */
	public static synchronized void giveItems(Client c) {
		if(c.getItems().freeSlots() > itemData.length - 1) {
			for (int i = 0; i < itemData.length; i++) {
				c.getItems().addItem(itemData[i][0], itemData[i][1]);
			}
			c.sendMessage("Thanks for voting!");
		} else {
			c.sendMessage("You must have "+ itemData.length +" item slots to get your reward.");
		}
	}

	/**
	 * checkVote, will return true or false depending if the player has voted
	 */
	public static synchronized boolean checkVote(String auth) {
		try {
			ResultSet res = getConnection().createStatement().executeQuery("SELECT `authcode` FROM `authcodes` WHERE `authcode`= '"+ auth + "' AND `recieved` =  0");
			if (res.next())
				return true;
			else
				return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Updates the users vote in the database
	 */
	public static synchronized void updateVote(String auth) {
		try {
			getConnection().createStatement().execute("UPDATE `authcodes` SET `recieved` = 1 WHERE `authcode` = '"+ auth + "'");
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
}