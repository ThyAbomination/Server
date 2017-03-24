package server.util;

import java.sql.*;

import server.Config;
import server.model.players.Client;

public class MadTurnipConnection extends Thread {

	public static Connection con = null;
	public static Statement stm;

	public static void createConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			con = DriverManager.getConnection("jdbc:mysql://", "", "");
			stm = con.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
			con = null;
			stm = null;
		}
	}
	
	public MadTurnipConnection(){
		
	}
	
	public void run() {
		while(true) {		
			try {
				if (Config.DATABASES_ENABLED) {
					if(con == null)
						createConnection();
					else
						ping();
				}
				Thread.sleep(10000);//10 seconds
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void ping(){
		try {
			String query = "SELECT * FROM donation WHERE username = 'null'";
			query(query);
		} catch (Exception e) {
			e.printStackTrace();
			con = null;
			stm = null;
		}
	}
	
	public static void addDonateItems(final Client c,final String name){
		if(con == null){
			if(stm != null){
				try {
					stm = con.createStatement();
				} catch(Exception e){
					con = null;
					stm = null;
					//put a sendmessage here telling them to relog in 30 seconds
					return;
				}
			} else {
				//put a sendmessage here telling them to relog in 30 seconds
				return;
			}
		}
		new Thread(){
			@Override
			public void run()
			{
				try {
					String name2 = name.replaceAll(" ","_");
					String query = "SELECT * FROM donation WHERE username = '"+name2+"'";
					ResultSet rs = query(query);
					boolean b = false;
					while(rs.next()){
						int prod = Integer.parseInt(rs.getString("productid"));
						int price = Integer.parseInt(rs.getString("price"));
						if(prod == 1 && price == 30){
							c.getItems().addItem(1048,1);
							c.amountDonated += 30;
							b = true;
						} else if(prod == 2 && price == 30){
							c.getItems().addItem(1038,1);						 
							c.amountDonated += 30;
							b = true;
						} else if(prod == 3 && price == 30){
							c.getItems().addItem(1040,1);							 
							c.amountDonated += 30;
							b = true;
						} else if(prod == 4 && price == 30){
							 c.getItems().addItem(1044,1);							 
							c.amountDonated += 30;
							b = true;
						} else if(prod == 5 && price == 30){
							c.getItems().addItem(1042,1);						 
							c.amountDonated += 30;
							b = true;
						} else if(prod == 6 && price == 30){
							c.getItems().addItem(1046,1);							 
							c.amountDonated += 30;
							b = true;
						} else if(prod == 7 && price == 25){
							c.getItems().addItem(1050,1);							 
							c.amountDonated += 25;
							b = true;
						} else if(prod == 8 && price == 25){
							c.getItems().addItem(1057,1);							 
							c.amountDonated += 25;
							b = true;
						} else if(prod == 9 && price == 25){
							c.getItems().addItem(1055,1);							 
							c.amountDonated += 25;
							b = true;
						} else if(prod == 10 && price == 25){
							c.getItems().addItem(1053,1);							 
							c.amountDonated += 25;
							b = true;
						} else if(prod == 11 && price == 10){		//ccb
							c.getItems().addItem(18357,1);							 
							c.amountDonated += 10;
							b = true;
						} else if(prod == 12 && price == 25){
							c.getItems().addItem(19000, 1);
                            c.getItems().addItem(19001, 1);
                            c.getItems().addItem(19002, 1);							 
							c.amountDonated += 25;
							b = true;
						} else if(prod == 13 && price == 25){
							 c.getItems().addItem(19003, 1);
                                c.getItems().addItem(19004, 1);
                                c.getItems().addItem(19005, 1);							 
							c.amountDonated += 25;
							b = true;
						} else if(prod == 14 && price == 25){
							c.getItems().addItem(19006, 1);
                                c.getItems().addItem(19007, 1);
                                c.getItems().addItem(19008, 1);							  
							c.amountDonated += 25;
							b = true;
						} else if(prod == 15 && price == 50){
							c.getItems().addItem(15098,1);							 
							c.amountDonated += 50;
							b = true;
						} else if(prod == 16 && price == 18){
							c.getItems().addItem(19780,1);							 
							c.amountDonated += 18;
							b = true;
						} else if(prod == 17 && price == 15){
							c.getItems().addItem(16955,1);							 
							c.amountDonated += 15;
							b = true;
						} else if(prod == 18 && price == 15){
							c.getItems().addItem(16425,1);							 
							c.amountDonated += 15;
							b = true;
						} else if(prod == 19 && price == 15){
							c.getItems().addItem(17143,1);							 
							c.amountDonated += 15;
							b = true;
						} else if(prod == 20 && price == 15){
							c.getItems().addItem(13740,1);							 
							c.amountDonated += 15;
							b = true;
						} else if(prod == 21 && price == 10){
							c.getItems().addItem(13742,1);							 
							c.amountDonated += 10;
							b = true;
						} else if(prod == 22 && price == 10){
							c.getItems().addItem(13744,1);							
							c.amountDonated += 10;
							b = true;
						} else if(prod == 23 && price == 10){
							c.getItems().addItem(13738,1);							 
							c.amountDonated += 10;
							b = true;
						} else if(prod == 24 && price == 10){
							c.getItems().addItem(18349,1);							 
							c.amountDonated += 10;
							b = true;
						} else if(prod == 25 && price == 10){
							c.getItems().addItem(18353,1);							 
							c.amountDonated += 10;
							b = true;
						} else if(prod == 26 && price == 10){
							c.getItems().addItem(18351,1);					 
							c.amountDonated += 10;
							b = true;
						} else if(prod == 27 && price == 10){
							c.getItems().addItem(18355,1);						 
							c.amountDonated += 10;
							b = true;
						} else if(prod == 28 && price == 10){
							c.getItems().addItem(11694,1);						 
							c.amountDonated += 10;
							b = true;
						} else if(prod == 29 && price == 10){
							c.getItems().addItem(14484,1);			 
							c.amountDonated += 10;
							b = true;
						} else if(prod == 30 && price == 8){
							 c.getItems().addItem(19785, 1);
                              c.getItems().addItem(19786, 1);
                              c.getItems().addItem(19787, 1);
                              c.getItems().addItem(19788, 1);
                              c.getItems().addItem(19789, 1);
                              c.getItems().addItem(19790, 1);
                              c.getItems().addItem(11663, 1);
                              c.getItems().addItem(11664, 1);
                              c.getItems().addItem(11665, 1);
                              c.getItems().addItem(8842, 1);							 
							c.amountDonated += 8;
							b = true;
						} else if(prod == 31 && price == 8){
							c.getItems().addItem(13887, 1);
                              c.getItems().addItem(13893, 1);							
							c.amountDonated += 8;
							b = true;
						} else if(prod == 32 && price == 8){
							 c.getItems().addItem(13896, 1);
                              c.getItems().addItem(13884, 1);
                              c.getItems().addItem(13890, 1);							
							c.amountDonated += 8;
							b = true;
						} else if(prod == 33 && price == 8){
							c.getItems().addItem(13864, 1);
                              c.getItems().addItem(13858, 1);
                              c.getItems().addItem(13861, 1);							
							c.amountDonated += 8;
							b = true;
						} else if(prod == 34 && price == 8){
							c.getItems().addItem(13876, 1);
                              c.getItems().addItem(13870, 1);
                              c.getItems().addItem(13873, 1);
							c.amountDonated += 8;
							b = true;
						} else if(prod == 35 && price == 8){
							c.getItems().addItem(11724, 1);
                            c.getItems().addItem(11726, 1);							
							c.amountDonated += 8;
							b = true;
						} else if(prod == 36 && price == 6){
							c.getItems().addItem(11696,1);							
							c.amountDonated += 6;
							b = true;
						} else if(prod == 37 && price == 6){
							c.getItems().addItem(11700,1);							
							c.amountDonated += 6;
							b = true;
						} else if(prod == 38 && price == 6){
							c.getItems().addItem(11698,1);						
							c.amountDonated += 6;
							b = true;
						} else if(prod == 39 && price == 5){
							c.getItems().addItem(6570,1);						
							c.amountDonated += 5;
							b = true;
						}
					}
					if(b){
						query("DELETE FROM `donation` WHERE `username` = '"+name2+"';");
					}
				} catch (Exception e) {
					e.printStackTrace();
					con = null;
					stm = null;
				}
			}
		}.start();
	}
	
	public static ResultSet query(String s) throws SQLException {
		try {
			if (s.toLowerCase().startsWith("select")) {
				ResultSet rs = stm.executeQuery(s);
				return rs;
			} else {
				stm.executeUpdate(s);
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			con = null;
			stm = null;
		}
		return null;
	}
}