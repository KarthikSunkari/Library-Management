
import java.sql.*;

public class AdminDoe {
	
	public static boolean login(String username, String password) {
		boolean status = false;
		try{
			Connection con=DB.getConnection();
			PreparedStatement ps=con.prepareStatement("select * from admins where username =  ? and password = ?");
			ps.setString(1,username);
			ps.setString(2,password);
		    ResultSet rs=ps.executeQuery();
			status=rs.next();
			con.close();
		}catch(Exception e){System.out.println(e);}
		return status;
	}
	
	public static String signUp(String username, String password) {
		try{
			Connection con=DB.getConnection();
			PreparedStatement ps=con.prepareStatement("select * from admins where username =  ?");
			ps.setString(1,username);
		    ResultSet rs=ps.executeQuery();
		    if(rs.next()) {
		    	con.close();
		    	return "Admin with username already exists";
		    }else {
		    	ps = con.prepareStatement("insert into admins(username,password) values(?,?)");
		    	ps.setString(1, username);
		    	ps.setString(2,password);
		    	ps.executeQuery();
		    }
			con.close();
		}catch(Exception e){System.out.println(e);}
		
		return "Admin Created Successfully";
	}
}
