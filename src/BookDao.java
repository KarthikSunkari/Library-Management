import java.sql.Connection;
import java.sql.PreparedStatement;

public class BookDao {
public static int save(String bno,String name,String author,String publisher,int quantity){
	int status=0;
	try{
		Connection con=DB.getConnection();
		PreparedStatement ps=con.prepareStatement("insert into book(book_id,bname,author,publisher,copies,totalcopies) values(?,?,?,?,?,?)");
		ps.setInt(1,Integer.parseInt(bno));
		ps.setString(2,name);
		ps.setString(3,author);
		ps.setString(4,publisher);
		ps.setInt(5,quantity);
		ps.setInt(6, quantity);
		status=ps.executeUpdate();
		con.close();
	}catch(Exception e){System.out.println(e);}
	return status;
}
}
