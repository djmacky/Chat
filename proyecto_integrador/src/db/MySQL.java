package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;
import ui.AuthInterface;

public class MySQL {
	
	String url;
	String dbName;
	String driver;
	String userName;
	String password;
	Connection conn;
	
	PreparedStatement userExist,createUser,connect,disconn,passMatch,getUser,getAll,getBlocked,getBlockedString,blockUser,unblockUser,email,status,getOnline;
	
	public MySQL(String hostname) {
		this.url = "jdbc:mysql://"+ hostname + ":3306/"; 
		this.dbName = "distribuidos";
		this.driver = "com.mysql.jdbc.Driver"; 
		this.userName = "armandm"; 
		this.password = "admin4us"; 	
		this.conn = connectToMySQL();
	}
	
	
	public Connection connectToMySQL() {
		try { 
			Class.forName(driver).newInstance(); 
			conn = DriverManager.getConnection(url+dbName,userName,password);
		} catch (Exception e) { 
			e.printStackTrace(); 
		} 

		return conn;
	}
	
	public String getEmail(String username) {
		String query = "select email from chat_users where username = ?";
		String emailString = "";
		try {
			this.email = (PreparedStatement) conn.prepareStatement(query);
			email.setString(1, username);
			ResultSet rs = email.executeQuery();
			while(rs.next()) {
				emailString = rs.getString("email");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return emailString;
	}
	
	public String[] getBlockedUsers(String username) {
		String[] users = {};
		String query = "select blocked_users from chat_users where username = ?";
		String blocked = "";
		try {
			this.getBlocked = (PreparedStatement) conn.prepareStatement(query);
			getBlocked.setString(1, username);
			ResultSet rs = getBlocked.executeQuery();
			while(rs.next()) {
				blocked = rs.getString(1);
				//System.out.println(blocked);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		if(!blocked.equals("")) {
			users = blocked.split(",");
		}
		
		return users;
	}
	
	public ArrayList<String> getUnblockedUsers(String username) {
		ArrayList<String> users = new ArrayList<String>();
		ArrayList<String> allUsers = getAllUsers(username);
		String[] blockedUsers = getBlockedUsers(username);
		HashMap<String,String> map = new HashMap<String,String>();
		
		for(String str: allUsers) {
			map.put(str, "unblocked");
			System.out.println( "unblocked" + " " + str );
		}
		
		for(String str: blockedUsers) {
			if(map.containsKey(str)){
				map.put(str, "blocked");
				System.out.println( "blocked" + " " + str );
			}
		}
		
		for(Entry<String,String> entry: map.entrySet()){
			if(entry.getValue().equals("unblocked")){
				users.add(entry.getKey());
				System.out.println(entry.getKey() +  " " +  entry.getValue());
			}
				
		}
		
		
		return users;
	}
	
	public String getBlockedUsersString(String username) {
		String query = "select blocked_users from chat_users where username = ?";
		String blocked = "";
		try {
			this.getBlockedString = (PreparedStatement) conn.prepareStatement(query);
			getBlockedString.setString(1, username);
			ResultSet rs = getBlockedString.executeQuery();
			while(rs.next()) {
				blocked = rs.getString(1);
				//System.out.println(blocked);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
		return blocked;
	}
	
	public String getUnblockedUsersString(String username) {
		String unblocked = "";
		ArrayList<String> unblockedList = getUnblockedUsers(username);
		for(String str: unblockedList) {
			unblocked += str + ",";
		}
		
		return unblocked;
	}
	
	public void blockUser(String username,String target) {
		String query = "update chat_users set blocked_users = ? where username = ?";
		String blocked = getBlockedUsersString(username) + target + ",";
		System.out.println("blocked users string " + blocked);
		try {
			this.blockUser = (PreparedStatement) conn.prepareStatement(query);
			blockUser.setString(1, blocked);
			blockUser.setString(2, username);
			blockUser.execute();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void unblockUser(String username, String target) {
		String query = "update chat_users set blocked_users = ? where username = ?";
		target = target + ",";
		Pattern p = Pattern.compile(target);
		String blocked = getBlockedUsersString(username);
		Matcher matcher = p.matcher(blocked);
		String unblocked = null;
		while(matcher.find()) {
			//System.out.println(matcher.start());
			//System.out.println(matcher.end());
			unblocked = blocked.substring(0, matcher.start()) + blocked.substring(matcher.end(), blocked.length());
		}
		
		try {
			this.unblockUser = (PreparedStatement) conn.prepareStatement(query);
			unblockUser.setString(1, unblocked);
			unblockUser.setString(2, username);
			unblockUser.execute();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public ArrayList<String> getAllUsers(String username) {
		String query = "select username from chat_users where username != ? order by username asc";
		//String[] users = new String[50];
		ArrayList<String> users = new ArrayList<String>();
		try {
			this.getAll = (PreparedStatement) conn.prepareStatement(query);
			getAll.setString(1, username);
			ResultSet rs = getAll.executeQuery();
			while(rs.next()) {
				users.add(rs.getString("username"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return users;
	}
	
	public ArrayList<String> getOnlineUsers(String username) {
		String query = "select username from chat_users where username != ? and status = 'online' order by username asc";
		//String[] users = new String[50];
		ArrayList<String> users = new ArrayList<String>();
		try {
			this.getOnline = (PreparedStatement) conn.prepareStatement(query);
			getOnline.setString(1, username);
			ResultSet rs = getOnline.executeQuery();
			while(rs.next()) {
				users.add(rs.getString("username"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return users;
	}
	
	public boolean isReachable(String currentUser,String target) {
		boolean reachable = true;
		String [] blockedUsers = getBlockedUsers(target);
		for(String str: blockedUsers) {
			if(str.equals(currentUser))
				return false;
		}
		
		return reachable;
	}
	
	public boolean isOnline(String target) {
		boolean online = false;
		String query = "select status from chat_users where username = ?";
		try {
			this.status = (PreparedStatement) conn.prepareStatement(query);
			status.setString(1, target);
			ResultSet rs = status.executeQuery();
			while(rs.next()) {
				String userStatus = rs.getString("status");
				if(userStatus.equals("online"))
					return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return online;
	}
	
	
	public boolean passwordMatches(String username, String password) {
		String query = "select count(*) from chat_users where username = ? and password = ? ";
		boolean retval = false;
		int count = 0;
		try {
			this.passMatch = (PreparedStatement) conn.prepareStatement(query);
			passMatch.setString(1, username);
			passMatch.setString(2, password);
			ResultSet rs = passMatch.executeQuery();
			if(rs.next()) 
				count = rs.getInt(1);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(count >= 1)
			retval = true;
		
		return retval;
	}
	
	public void login(String username) {
		String query = "update chat_users set status='online' where username = ?";
		try { 
			this.connect = (PreparedStatement) conn.prepareStatement(query);
			connect.setString(1, username);
			connect.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void logout(String username) {
		String query = "update chat_users set status='offline' where username = ?";
		try { 
			this.connect = (PreparedStatement) conn.prepareStatement(query);
			connect.setString(1, username);
			connect.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean userExists(String username) {
		String query = "select count(*) as count from chat_users where username = ?"; 
		int count = -1;
		try {
			this.userExist = (PreparedStatement) conn.prepareStatement(query);
			userExist.setString(1, username);
			ResultSet rs = userExist.executeQuery();
			while (rs.next()) {
	            count = rs.getInt("count");
	            //System.out.println("the count is "+ count);
	        }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(count >= 1)
			return true;
		else
			return false;
		
	}
	
	public void createNewUser(String username, String password, String email, String twitter_username) {
		String query = "insert into distribuidos.chat_users (id,username,password,email,twitter_username,blocked_users,status, last_login) values (default,?,?,?,?,'','offline', null)"; 
		if(username.length() >= 4) {
			try {
				this.createUser = (PreparedStatement) conn.prepareStatement(query);
				createUser.setString(1, username);
				createUser.setString(2, password);
				createUser.setString(3, email);
				createUser.setString(4, twitter_username);
				createUser.execute();
			
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			JOptionPane.showMessageDialog(null, "Username must be 4 characters long","Error",JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public User getUserData(String username) {
		String query = "select id,username,password,email,twitter_username,status from chat_users where username = ?";
		User userDB = null;
		try {
			this.getUser = (PreparedStatement) conn.prepareStatement(query);
			getUser.setString(1, username);
			ResultSet rs = getUser.executeQuery();
			//MySQL mysql = new MySQL();
			while(rs.next()) {
				userDB = new User(rs.getInt("id"),rs.getString("username"),rs.getString("password"),
						rs.getString("email"),rs.getString("twitter_username"),rs.getString("status"),this);
			}
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userDB;
	}
	
	public void closeConn() {
		try {
			this.conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
