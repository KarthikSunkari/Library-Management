import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

public class ReturnBookDao {
	public static int delete(String bookcallno,int studentid){
		int fine = 0;
		try{
			Connection con=DB.getConnection();
			
			PreparedStatement ps = con.prepareStatement("select issue_id from issue where book_id = ? and customer_id = ?"); 
			ps.setInt(1,Integer.parseInt(bookcallno));
			ps.setInt(2, studentid);
			ResultSet rs = ps.executeQuery();
			int issueId = -1;
			if(rs.next()) {
				issueId = rs.getInt(1);
			}			
			CallableStatement cs;
			cs= con.prepareCall("{ ? = call getFine(?)}");
			cs.registerOutParameter(1,Types.INTEGER);
			cs.setInt(2,issueId);
			cs.execute();
			fine = cs.getInt(1);
			CallableStatement cs1=con.prepareCall("call return_book(?,?) ");
			cs1.setInt(1,Integer.parseInt(bookcallno));
			cs1.setInt(2,studentid);
			cs1.executeUpdate();
			
			
			con.close();
		}catch(Exception e){System.out.println(e); return 0;}
		return fine + 1;
	}
	
}
