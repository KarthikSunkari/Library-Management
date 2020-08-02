import java.sql.*;
public class IssueBookDao {
	


public static int save(String bookcallno,int studentid){
	
	try{
		Connection con=DB.getConnection();
		
		
		
		
		CallableStatement ps=con.prepareCall(" call issue_book(?,?,?) ");
		ps.setInt(1,Integer.parseInt(bookcallno));
		ps.setInt(2,studentid);
		ps.setInt(3,30);
		ps.executeUpdate();
		
		
		con.close();
	}catch(Exception e){System.out.println(e); return 0;}
	return 1;
}

}
