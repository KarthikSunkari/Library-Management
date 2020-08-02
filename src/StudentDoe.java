
import java.sql.*;

public class StudentDoe {
	
	public static boolean login(String RollNo, String password) {
		boolean status = false;
		try{
			Connection con=DB.getConnection();
			PreparedStatement ps=con.prepareStatement("select * from customer where customer_id =  ? and passwordHash = ?");
			ps.setInt(1,Integer.parseInt(RollNo));
			ps.setString(2,password);
		    ResultSet rs=ps.executeQuery();
			status=rs.next();
			con.close();
		}catch(Exception e){System.out.println(e);}
		return status;
	}
	
	public static String signUp(String username, String password,String email,String RollNo) {
		try{
			Connection con=DB.getConnection();
			PreparedStatement ps=con.prepareStatement("select * from customer where customer_id =  ?");
			ps.setInt(1,Integer.parseInt(RollNo));
		    ResultSet rs=ps.executeQuery();
		    if(rs.next()) {
		    	con.close();
		    	return "Student with Roll Number already exists";
		    }else {
		    	ps = con.prepareStatement("insert into customer (cname,passwordHash,email,customer_id) values(?,?,?,?)");
		    	ps.setString(1, username);
		    	ps.setString(2,password);
		    	ps.setString(3, email);
		    	ps.setInt(4,Integer.parseInt(RollNo));
		    	ps.executeQuery();
		    }
			con.close();
		}catch(Exception e){System.out.println(e);}
		
		return "Student Created Successfully";
	}
}
