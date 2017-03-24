package server.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;


public class SQLDatabase {

	private static SQLDatabase singleton;
	
	public Connection getConnection(final String user, final String pass, final String host) throws SQLException {

	    Connection conn = null;
	    Properties connectionProps = new Properties();
	    connectionProps.put("user", user);
	    connectionProps.put("password", pass);
	    conn = DriverManager.getConnection(
	             "jdbc:mysql://" +
	             host + "/",
	             connectionProps);
	    return conn;
	}
	
	public static long getTotals(int... values) {
		long total = 0;
		for(final int i : values) {
			total += i;
		}
		return total;
	}
	
	public void update(final String username, final int attackLevel, final int attackExperience, final int defenceLevel, final int defenceExperience, final int strengthLevel, final int strengthExperience,
			final int hitpointsLevel, final int hitpointsExperience, final int rangedLevel, final int rangedExperience, final int prayerLevel, final int prayerExperience, 
			final int magicLevel, final int magicExperience, final int cookingLevel, final int cookingExperience, final int woodcuttingLevel, final int woodcuttingExperience,
			final int fletchingLevel, final int fletchingExperience, final int fishingLevel, final int fishingExperience, final int firemakingLevel, final int firemakingExperience,
			final int craftingLevel, final int craftingExperience, final int smithingLevel, final int smithingExperience, final int miningLevel, final int miningExperience,
			final int herbloreLevel, final int herbloreExperience, final int agilityLevel, final int agilityExperience, final int thievingLevel, final int thievingExperience,
			final int slayerLevel, final int slayerExperience, final int farmingLevel, final int farmingExperience, final int runecraftingLevel, final int runecraftingExperience,
			final long totalLevel, final long totalExperience) throws SQLException {
			Thread thread = new Thread(new Runnable() {
				public void run() {
					Connection connection = null;
					Statement statement = null;
					ResultSet resultSet = null;

					try{
						connection = getConnection("", "", "");
						statement = connection.createStatement();
						statement.executeUpdate("DELETE FROM `projecde_highscores`.`highscores` WHERE `Name` = '"+username+"';");
						statement.close();
						
						statement = connection.createStatement();
						statement.executeUpdate("INSERT INTO `projecde_highscores`.`highscores` "
								+ "(`Name`, `0l`, `0xp`, `1l`, `1xp`, `2l`, `2xp`, `3l`, `3xp`, `4l`, `4xp`,"
								+ " `5l`, `5xp`, `6l`, `6xp`, `7l`, `7xp`, `8l`, `8xp`,"
								+ " `9l`, `9xp`, `10l`, `10xp`, `11l`, `11xp`, `12l`, `12xp`,"
								+ " `13l`, `13xp`, `14l`, `14xp`, `15l`, `15xp`, `16l`, `16xp`,"
								+ " `17l`, `17xp`, `18l`, `18xp`, `19l`, `19xp`, `20l`, `20xp`,"
								+ " `21l`, `21xp`, `total_level`, `total_exp`)"
								+ " VALUES "
								+ "('"+username+"', '"+attackLevel+"', '"+attackExperience+"', '"+defenceLevel+"', '"+defenceExperience+"', '"+strengthLevel+"', '"+strengthExperience+"', '"+hitpointsLevel+"', '"+hitpointsExperience+"', '"+rangedLevel+"', '"+rangedExperience+"',"
								+ " '"+prayerLevel+"', '"+prayerExperience+"', '"+magicLevel+"', '"+magicExperience+"', '"+cookingLevel+"', '"+cookingExperience+"', '"+woodcuttingLevel+"', '"+woodcuttingExperience+"',"
								+ " '"+fletchingLevel+"', '"+fletchingExperience+"', '"+fishingLevel+"', '"+fishingExperience+"', '"+firemakingLevel+"', '"+firemakingExperience+"', '"+craftingLevel+"', '"+craftingExperience+"',"
								+ " '"+smithingLevel+"', '"+smithingExperience+"', '"+miningLevel+"', '"+miningExperience+"', '"+herbloreLevel+"', '"+herbloreExperience+"', '"+agilityLevel+"', '"+agilityExperience+"',"
								+ " '"+thievingLevel+"', '"+thievingExperience+"', '"+slayerLevel+"', '"+slayerExperience+"', '"+farmingLevel+"', '"+farmingExperience+"', '"+runecraftingLevel+"', '"+runecraftingExperience+"',"
								+ " '0', '0', '"+totalLevel+"', '"+totalExperience+"');");
					} catch(SQLException e) {} finally {
						if (resultSet != null) try { resultSet.close(); } catch (SQLException ignore) {}
						if (statement != null) try { statement.close(); } catch (SQLException ignore) {}
						if (connection != null) try { connection.close(); } catch (SQLException ignore) {}
					}    
				}
			});
			
			thread.start();
	      

	}
	
	public static SQLDatabase getSingleton() {
		if(singleton == null) {
			singleton = new SQLDatabase();
		}
		return singleton;
	}
	
}
