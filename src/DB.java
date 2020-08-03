import java.sql.Connection;
import java.sql.DriverManager;

public class DB {
	public static Connection getConnection(){
		Connection con=null;
		try{
			Class.forName("org.postgresql.Driver");
			con=DriverManager.getConnection("jdbc:postgresql://localhost:5432/LibraryDb","postgres","zxcvbnm");
		}catch(Exception e){System.out.println(e);}
		return con;
	}

}
