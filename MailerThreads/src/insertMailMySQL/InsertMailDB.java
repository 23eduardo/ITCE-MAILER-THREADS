package insertMailMySQL;


import java.io.File;
import java.io.FileInputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import mx.com.itce.utils.ACCESOS;
import mx.com.itce.utils.KonexionUtil;
import mx.com.itce.utils.Mail;

/***
 * 
 * @author itce
 * Esta clase es para insertar un mail en la base de datos, ya sea blob o codificacion ascii
 * La clase debe ya recibir un objeto clase MAIL en el constructor para que tome todos los parámetros e inserte en MYSQL 
 * NOTA: Solo usar un metodo, ASCII o BLOB por que el correo se repetiría en MYSQL 
 * NOTA2: Si llega a faltar algun dato como CC o BCC o asunto incluso, la clase pondrá null en automatico al campo
 */
public class InsertMailDB {
	Mail mail;
	private int prioridad;
	static StringBuilder cadenaAscii = new StringBuilder();
	public static KonexionUtil conexion; 
	public StringBuilder sbSQL = new StringBuilder();
	private String proceso;
	static Date fechaActual;
	static SimpleDateFormat sdf;
	static String hora; 
	static String fecha;
	PreparedStatement ps;
	
	
	public InsertMailDB(){
		
	}
	
	public InsertMailDB(Mail mail, int prioridad,String proceso){
		this.mail=mail;
		this.prioridad = prioridad;
		this.proceso=proceso;
		//cada vez que se crea un objeto se revisa si la conexion sigue viva
		if(conexion==null){
			conexion = new KonexionUtil(ACCESOS.IP_CLOUD,ACCESOS.DB_CLOUD,ACCESOS.DB_MAILER_USER,ACCESOS.DB_MAILER_PASS);
//			conexion = new KonexionUtil(ACCESOS.DB_IP_LOCAL,ACCESOS.DB_SAAI,ACCESOS.DB_ROOT_USER,ACCESOS.DB_ROOT_PASS);
		}else{
			conexion.checkConnection();
		}
	}
	
	/***
	 * Inserta en MYSQL un correo que se desee guardar con codificacion ASCII
	 * @param mail
	 * @param prioridad
	 * @param proceso
	 */
	public void insertarMailASCII(){
		//encode message content
		encodeASCII();
		
	}
	/***
	 * Inserta en MYSQL un correo que su cuerpo se requiera en archivo BLOB
	 * @param mail
	 * @param prioridad
	 * @param proceso
	 */
	public void insertarMailBLOB() throws SQLException{
		//el encode del BLOB se realiza en el mismo método de insertar
		registrarMysqlBLOB();
		
	}
	
	private void encodeASCII() {
		cadenaAscii.setLength(0);
		char[] caracteres = mail.getText().toCharArray();
		for(char letra: caracteres){
			//System.out.print(letra+ " ");
			cadenaAscii.append((int)letra+"-");
		}
		//System.out.print("Cadena codificada en ASCII: "+cadenaAscii);
		//System.out.println();
		registrarMysqlASCII();
		
		
	}
	

	@SuppressWarnings("static-access")
	private void registrarMysqlASCII() {
		//limpiar la cadena antes de hacer una nueva
		sbSQL.setLength(0);
		fechaActual= new Date();
		System.out.println(fechaActual); 
		//HH en mayuscula te da el formato 24 horas en lugar de 12
		sdf= new SimpleDateFormat("HH:mm:ss");
		hora=sdf.format(fechaActual);
		System.out.println(hora); 
		sdf=null;
		sdf=new SimpleDateFormat("yyyy-MM-dd");
		fecha= sdf.format(fechaActual.getTime());
		System.out.println(fecha); 
		
		//check if some are null for DB 
		String getTo= isNullOrEmpty(mail.getTo())? null: "'"+mail.getTo()+"'";
		String getCc= isNullOrEmpty(mail.getCc())? null: "'"+mail.getCc()+"'";
		String getBcc=isNullOrEmpty(mail.getBcc())? null: "'"+mail.getBcc()+"'";
		String getSubject= isNullOrEmpty(mail.getSubject())? null: "'"+mail.getSubject()+"'";
		int ishtml = mail.isHtml()? 1: 0;
		String remitente=mail.getMyName(); 
		
		sbSQL.append("INSERT into filtro_alertas_historico(proceso,fecha,hora,frecuencia,estado,destinatario,destinatario_copia,destinatario_copia_oculta,asunto,body_text,isHTML,remitente) ");
		sbSQL.append("VALUES ('"+this.proceso + "','"+this.fecha+ "','"+this.hora+ "',"+this.prioridad+", 0,"+getTo+","+getCc+","+getBcc+","+getSubject+",'"+cadenaAscii+"',"+ishtml +",'"+remitente+"')");
		//System.out.println("ESTA es la cadena MYSQL "+sbSQL.toString() );
		
		//mail.send();
		try{
		conexion.aplicar(sbSQL.toString());
		System.out.println("Correo registrado exitosamente en DB");
		}catch(Exception e){System.out.println("No se registro el mail ppr que .. "+e.getMessage());}
	}
	
	private boolean registrarMysqlBLOB() throws SQLException {
		//limpiar statement
		ps=null; 
		sbSQL.setLength(0);
		fechaActual= new Date();
		System.out.println(fechaActual); 
		//HH en mayuscula te da el formato 24 horas en lugar de 12
		sdf= new SimpleDateFormat("HH:mm:ss");
		hora=sdf.format(fechaActual);
		System.out.println(hora); 
		sdf=null;
		sdf=new SimpleDateFormat("yyyy-MM-dd");
		fecha= sdf.format(fechaActual.getTime());
		System.out.println(fecha); 		
		int ishtml = mail.isHtml()? 1: 0;
		conexion.setPrepStmt("INSERT into filtro_alertas_historico(proceso,fecha,hora,frecuencia,estado,destinatario,destinatario_copia,destinatario_copia_oculta,asunto,body_blob,isHTML,remitente,prueba) "
								+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
		ps=conexion.getPrepStmt();
		ps.setString(1, this.proceso);
		ps.setString(2, fecha);
		ps.setString(3, hora);
		ps.setInt(4, prioridad);
		ps.setInt(5, 0);
		ps.setString(6, mail.getTo());
		ps.setString(7, mail.getCc());
		ps.setString(7, mail.getCc());
		ps.setString(8, mail.getBcc());
		ps.setString(9, mail.getSubject());
		//converting to blob
		ps.setBytes(10, mail.getText().getBytes());
		ps.setInt(11, ishtml);
		ps.setString(12, mail.getMyName());
		ps.setInt(13,mail.getIsPrueba());
		//ps.setBinaryStream(10, byteData);
		
		System.out.println("-------El correo se registro de manera exitosa en la Base de datos----");
		return (ps.execute());
		//conexion.setPrepStmt(prepStmt);
		
	}
	
	public boolean insertarMysqlBLOBAdjuntos() throws SQLException {
		//limpiar statement
		ps=null; 
		sbSQL.setLength(0);
		fechaActual= new Date();
		System.out.println(fechaActual); 
		//HH en mayuscula te da el formato 24 horas en lugar de 12
		sdf= new SimpleDateFormat("HH:mm:ss");
		hora=sdf.format(fechaActual);
		System.out.println(hora); 
		sdf=null;
		sdf=new SimpleDateFormat("yyyy-MM-dd");
		fecha= sdf.format(fechaActual.getTime());
		System.out.println(fecha); 		
		int ishtml = mail.isHtml()? 1: 0;
		conexion.setPrepStmt("INSERT into filtro_alertas_historico(proceso,fecha,hora,frecuencia,estado,destinatario,destinatario_copia,destinatario_copia_oculta,asunto,body_blob,isHTML,remitente,prueba,adjunto_nombre,adjunto_blob) "
								+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		ps=conexion.getPrepStmt();
		ps.setString(1, this.proceso);
		ps.setString(2, fecha);
		ps.setString(3, hora);
		ps.setInt(4, prioridad);
		ps.setInt(5, 0);
		ps.setString(6, mail.getTo());
		ps.setString(7, mail.getCc());
		ps.setString(7, mail.getCc());
		ps.setString(8, mail.getBcc());
		ps.setString(9, mail.getSubject());
		//converting to blob
		ps.setBytes(10, mail.getText().getBytes());
		ps.setInt(11, ishtml);
		ps.setString(12, mail.getMyName());
		ps.setInt(13,mail.getIsPrueba());
		ps.setString(14,mail.getFiles());
		ps.setBytes(15, mail.contenidoAdjunto.getBytes());
		System.out.println("Tu archivo adjunto "+mail.contenidoAdjunto.getBytes());
		//ps.setBinaryStream(10, byteData);
		
		System.out.println("-------El correo se registro de manera exitosa en la Base de datos----");
		return (ps.execute());
		//conexion.setPrepStmt(prepStmt);
		
	}
	
	public void setThisMail(Mail mail){
		this.mail=mail;
	}
	
	public void checkThisMailDetails(){
		System.out.println();
		System.out.println("--------DETALLES DEL MENSAJE-----");
		System.out.println("Texto original: "+mail.getText());
		System.out.println("usuario: "+mail.getMyName());
		System.out.println("Asunto: "+mail.getSubject());
		System.out.println();
	}
	
	/***
	 * Revisa si un string es null o está vacia
	 * @param str
	 * @return true si esta vacia o es nula
	 */
	public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.isEmpty())
            return false;
        return true;
    }

}
