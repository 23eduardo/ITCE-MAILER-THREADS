package mx.com.itce.utils;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class KonexionUtil
{
	private String url;
	private String usuario;
	private String password;
	private ResultSet rsResultado;
	private Connection conn;
	private PreparedStatement prepStmt;
	private String bdd;

	
	public KonexionUtil(String bdd) 
	{
		this.bdd=bdd;
		this.url = "jdbc:mysql://127.0.0.1/"+this.bdd+"?zeroDateTimeBehavior=convertToNull&useSSL=false";
		this.conn = null;
		this.usuario = "root";
		this.password = "";
		inicializar();
	}

	public KonexionUtil(String server, String db) 
	{
		this.url = "jdbc:mysql://"+server+"/"+db+"?zeroDateTimeBehavior=convertToNull&useSSL=false";
		this.conn = null;
		this.usuario = "root";
		this.password = "";
		inicializar();
	}
	
	public KonexionUtil(String db, String user, String psw) 
	{
		this.url = "jdbc:mysql://127.0.0.1/"+db+"?zeroDateTimeBehavior=convertToNull&useSSL=false";
		//this.url = "jdbc:mysql://107.180.56.146/"+db+"?zeroDateTimeBehavior=convertToNull&useSSL=false";
		this.conn = null;
		this.usuario = user;
		this.password = psw;
		inicializar();
	}
	
	public KonexionUtil(String host, String db, String user, String psw) 
	{
		//this.url = "jdbc:mysql://"+host+":3306/"+db+"?zeroDateTimeBehavior=convertToNull&useSSL=false";
		//this.url = "jdbc:mysql://107.180.56.146/"+db+"?zeroDateTimeBehavior=convertToNull&useSSL=false";
		this.url = "jdbc:mysql://"+host+"/"+db+"?zeroDateTimeBehavior=convertToNull&useSSL=false";
		this.conn = null;
		this.usuario = user;
		this.password = psw;
		inicializar();
	}

	public void inicializar() 
	{
		try
		{
			//Class.forName("org.gjt.mm.mysql.Driver").newInstance();
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, usuario, password);
			System.out.print("CONEXION EXITOSA");
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			System.out.print("Error respecto al driver: "+ e.getMessage());
		} catch (SQLException e) {
			System.out.print("Error "+ e.getMessage());
		}
	}
	
	/**Es para selects**/
	public ResultSet ejecutar(String s) 
	{
		checkConnection();
		
		try
		{
			prepStmt = conn.prepareStatement(s);
			rsResultado = prepStmt.executeQuery();
			conn.clearWarnings();
			
		}
		catch (SQLException e)
		{
			System.out.println("ERROR EN: " + s);
			e.printStackTrace();
		}
		
		return rsResultado;
	}

	/**Es para inserts y updates..**/
	public boolean aplicar(String s) 
	{
		//checkConnection();
		boolean flag;
		
		try 
		{
			conn.setAutoCommit(false);
			prepStmt = conn.prepareStatement(s);
			prepStmt.executeUpdate();
			//System.out.println("SE MODIFICARON " + prepStmt.getUpdateCount() + " renglones.");
			conn.commit();
			prepStmt.close();
			conn.setAutoCommit(true);
			flag = true;
		} 
		catch (SQLException exception) 
		{
			flag = false;
			//System.out.println("--ERROR EN EL QUERY: " + s);
			System.err.println(exception.getMessage());
		}
		
		return flag;
	}
	
	
	public boolean aplicarStatementPersonalizado(String statement, ArrayList sustituir ) 
	{
		checkConnection();
		boolean flag;
		
		try{ 
			conn.setAutoCommit(false);
			conn.prepareCall(statement);
			//prepStmt=ps;
			
			prepStmt.executeUpdate();
			prepStmt.close();
			conn.commit();
			conn.setAutoCommit(true);
			flag = true;
		} 
		catch (SQLException exception) 
		{
			flag = false;
			//System.out.println("--ERROR EN EL QUERY: " + s);
			System.err.println(exception.getMessage());
		}
		
		return flag;
	}
	
	public Connection getConexion() 
	{
		return conn;
	}
	
	public void cerrar()
	{
		try
		{	
			conn.close();
		} 
		catch (SQLException e)
		{	
			e.printStackTrace();
		}
	}
	
	public void releaseConexion() 
	{

		try 
		{
			conn.close();
		} 
		catch (SQLException sqle) 
		{
			System.out.println("ERROR EN CONEXION " + sqle.getMessage());
		}
	}

	/**
	 * Contamos el numero de renglones en un ResultSet
	 * @param rsDatos El ResultSet cuyos registros se contar�n
	 * @return El n�mero de renglones encontrados en el ResultSet
	 */
	public int cuentaRenglones(ResultSet rsDatos) 
	{
		int numeroRenglones = 0;
		try 
		{
			rsDatos.last();
			numeroRenglones = rsDatos.getRow();
			rsDatos.beforeFirst();
		} 
		catch (SQLException sqle) 
		{
			sqle.printStackTrace();
		}

		return numeroRenglones;
	}
	
	public void checkConnection()
	{
		try 
		{
			if(!conn.isValid(0))
			{
				inicializar();
			}
		} catch (Exception e) {	System.out.println("Excepcion al revisar la conexion" + e.getMessage());	}
	}
	
	/**se repetia mucho esto, asi que lo converti a un peque�o metodo que verifica el insert o el update que se haga..**/
	public void doSaveQuery(String sql, String prefijoDebugLog, boolean debug)
	{
		checkConnection();
		boolean update=aplicar(sql);
		
		if(!update)
		{
			if(!debug)	System.out.println(prefijoDebugLog +update+"\t"+sql);
			//saveHistoryFileDB(variableArcDetalles, 2);
		}
		
		if(debug)	System.out.println(prefijoDebugLog +update+"\t"+sql);
	}

	public PreparedStatement getPrepStmt() {
		return prepStmt;
	}

	public void setPrepStmt(PreparedStatement prepStmt) {
		this.prepStmt = prepStmt;
	}
	
	//hice sobrecarga de este metodo para traer un preparedstatement a una clase externa que se necesite
	public void setPrepStmt(String string) throws SQLException {
		//this.prepStmt = prepStmt;
		this.prepStmt=this.conn.prepareStatement(string);
	}
	
}//class