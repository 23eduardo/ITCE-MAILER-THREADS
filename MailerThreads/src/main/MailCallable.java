package main;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.Executor;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;

import com.sun.mail.smtp.SMTPTransport;

import mx.com.itce.utils.ACCESOS;
import mx.com.itce.utils.KonexionUtil;

public class MailCallable implements Runnable
{
	private String myName; 
    private String myMail; 
    private String to; 
    private String subject;  
    private String cc; 
    private String bcc; 
    private String smtp;
    private String user;  
    private String password; 
    private boolean auth; 
    private String text; 
    private String[] file;
    private String filesName;
	private String port;
	private boolean html=false;
	private String id; 
	private int prueba;
	private String horaEnvio;
	private int status;
	private int hayError;
	private String errorEnvio;
	private  String from;
	public static KonexionUtil conexion;
	public StringBuilder sbSQL = new StringBuilder();
	public MainEnvioAutomaticoAlertasBLOB control2 = new MainEnvioAutomaticoAlertasBLOB();
	public int contadorenvios=0;
	public InputStream is;
	public byte[] blobAttach;
	public String nameAttach;
	public InitialConfig sme;
	private Transport transport;
	private Session session;
	public String rutaAdjunto;
	private int EnProcesoDeActualizacion;
	
	
	
	
	
	public int getEnProcesoDeActualizacion() {
		return EnProcesoDeActualizacion;
	}


	public void setEnProcesoDeActualizacion(int enProcesoDeActualizacion) {
		EnProcesoDeActualizacion = enProcesoDeActualizacion;
	}


	public Transport getTransport() {
		return transport;
	}


	public void setTransport(Transport transport) {
		this.transport = transport;
	}


	public Session getSession() {
		return session;
	}


	public void setSession(Session session) {
		this.session = session;
	}

	public HashMap<String,MailCallable> current = new HashMap<>();
	
	public String  getId() {
		return id;
	}

	
	public String[] getFile() {
		return file;
	}


	public void setFile(String[] file) {
		this.file = file;
	}


	public String getFilesName() {
		return filesName;
	}


	public void setFilesName(String filesName) {
		this.filesName = filesName;
	}


	public boolean isHtml() {
		return html;
	}


	public void setHtml(boolean html) {
		this.html = html;
	}


	public String getFrom() {
		return from;
	}


	public void setFrom(String from) {
		this.from = from;
	}


	public static KonexionUtil getConexion() {
		return conexion;
	}


	public static void setConexion(KonexionUtil conexion) {
		MailCallable.conexion = conexion;
	}


	public StringBuilder getSbSQL() {
		return sbSQL;
	}


	public void setSbSQL(StringBuilder sbSQL) {
		this.sbSQL = sbSQL;
	}


	public MainEnvioAutomaticoAlertasBLOB getControl2() {
		return control2;
	}


	public void setControl2(MainEnvioAutomaticoAlertasBLOB control2) {
		this.control2 = control2;
	}


	public int getContadorenvios() {
		return contadorenvios;
	}


	public void setContadorenvios(int contadorenvios) {
		this.contadorenvios = contadorenvios;
	}


	public InputStream getIs() {
		return is;
	}


	public void setIs(InputStream is) {
		this.is = is;
	}


	public byte[] getBlobAttach() {
		return blobAttach;
	}


	public void setBlobAttach(byte[] blobAttach) {
		this.blobAttach = blobAttach;
	}


	public String getNameAttach() {
		return nameAttach;
	}


	public void setNameAttach(String nameAttach) {
		this.nameAttach = nameAttach;
	}


	public HashMap<String, MailCallable> getCurrent() {
		return current;
	}


	public void setCurrent(HashMap<String, MailCallable> current) {
		this.current = current;
	}


	public int getPrueba() {
		return prueba;
	}


	public void setId(String  id) {
		this.id = id;
	}

	/**
	 * smtp="smtp.gmail.com"; <br>
	 * port="587";
	 * **/
	public MailCallable ()
	{
		this.smtp="smtp.gmail.com"; 
	    this.port="587";
	   
	}
	
	public MailCallable (String smtp, String port) 
	{ 
		this.smtp=smtp; 
	    this.port=port; 
	} 
    
    public MailCallable(MailCallable mailCallable) {
		// TODO Auto-generated constructor stub
    	
    	text=mailCallable.getText();
    	id=mailCallable.getId();
    	to=mailCallable.getTo();
    	subject=mailCallable.getSubject();
 
	}

    public MailCallable (HashMap<String,MailCallable> current){
    	this.current=current;
    }
	//--------------------------------------------------------------
    //--------------------------------------------------------------
    public boolean isAuth(){	return auth;	} 
    public void setAuth(boolean auth)	{	this.auth = auth;	} 
	    
    public String getBcc(){	return bcc;	} 
    public void setBcc(String bcc){	this.bcc = bcc;	} 
	     
    public String getCc(){	return cc;	} 
    public void setCc(String cc){	this.cc = cc;	} 
	     
    public String getSmtp(){  return smtp;  }  
    public void setSmtp(String smtp){  this.smtp = smtp;  } 
	     
    public String getMyMail(){	return myMail;	} 
    public void setMyMail(String myMail){	this.myMail = myMail;	} 
	     
    public String getMyName(){	return myName;	} 
    public void setMyName(String myName){	this.myName = myName;	} 
	     
    public String getPassword(){	return password;	} 
    public void setPassword(String password){	this.password = password;	} 
	     
    public String getSubject(){	return subject;	} 
    public void setSubject(String subject){	this.subject = subject;	} 
	     
    public String getText(){	return text;	} 
    public void setText(String text){	this.text = text;	}
    public void addText(String text){	this.text = text + this.text;	}
	    
    public String getTo(){	return to;	} 
    public void setTo(String to){	this.to = to;	} 
	    
    public String getUser(){	return user;	} 
    public void setUser(String user){	this.user = user;	} 
	    
    public String[] getAttachmentFile (){	return file;	} 
    public void setAttachmentFile(String[] file){	this.file = file;	}
    
    //sobrecarga para que asigne el array de adjuntos con un String separado por (,)
    public void setAttachmentFile(String file){	this.file=file.split(",");}
    
    public String getFiles(){	return filesName;	}
    public void setFiles(String files){	this.filesName=files;	}
    
    public String getPort(){	return port;	}
    public void setPort(String port){	this.port=port;	}
    
    public void setHTML (boolean html){	this.html=html;	}
    
    public int isPrueba() {return prueba;}

	public void setPrueba(int prueba) {	this.prueba = prueba;}
	
	 public String getHoraEnvio() {
			return horaEnvio;
		}

		public void setHoraEnvio(String horaEnvio) {
			this.horaEnvio = horaEnvio;
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public String getErrorEnvio() {
			return errorEnvio;
		}

		public void setErrorEnvio(String errorEnvio) {
			this.errorEnvio = errorEnvio;
		}
		
		public int getHayError() {
			return hayError;
		}

		public void setHayError(int hayError) {
			this.hayError = hayError;
		}
		
		
    //--------------------------------------------------------------
		//GETTERS N SETTERS
    //--------------------------------------------------------------
    
    
	//send 2
	public void sendMailOneSession() throws MessagingException{
		
		//se verifican los estados de los transports antes de mandar un correo
		String remitente=this.myName;
		if(remitente.equals("SOIA - AQ")||this.getMyName().equals("PRUEBA - SOIA - AQ"))
		{
			boolean statusTransport=control2.smeSOIA.getTransport().isConnected();
			System.out.println("Estado Actual del transport "+statusTransport);
			if(statusTransport==false){
				//si el transport no esta conectado, por que GSUITE cerro la conexion,la levanta de nuevo  
				MainEnvioAutomaticoAlertasBLOB.InitPropsSOIA();
				
			}
			
			this.transport=control2.smeSOIA.getTransport();
			this.session=control2.smeSOIA.getSession();
			
		}else{
			boolean statusTransport=control2.smeREPORTES.getTransport().isConnected();
			System.out.println("Estado Actual del transport "+statusTransport);
			if(statusTransport==false){
				//si el transport no esta conectado 
				MainEnvioAutomaticoAlertasBLOB.InitPropsREPORTES();
			}
			this.transport=control2.smeREPORTES.getTransport();
			this.session=control2.smeREPORTES.getSession();
			
		}
		
        Message msg = new MimeMessage(getSession());
        msg.setFrom(new InternetAddress(getFrom()));
        System.out.println("MAIL:FROM:"+getFrom());
        
        
        if ( getTo()!= null)
        {	
        	try{
        	msg.setRecipients(Message.RecipientType.TO,InternetAddress.parse(getTo(), false)); 
        	System.out.println("MAIL:TO:"+getTo());
        	}catch (Exception e ){
        		e.printStackTrace();
        	}
        }
        
        //si hay algo en el campo de CC lo asignamos a los destinatarios
        if ( getCc() != null) 
        {	
        	msg.setRecipients(Message.RecipientType.CC,InternetAddress.parse(getCc(), false));
        	System.out.println("MAIL:CC:"+getCc() );
        }
        //otra copia 
        if (getBcc() != null)
        {	
        	msg.setRecipients(Message.RecipientType.BCC,InternetAddress.parse(getBcc(), false));
        	System.out.println("MAIL:BCC:"+getBcc());
        }
 
        msg.setSubject(getSubject());//asignamos el asunto del mensaje 
        msg.setHeader("X-Mailer", "smtpsend"); 
        msg.setSentDate(new Date());//la fecha de envio del mensaje 
    
        if(isHtml())
        	msg.setContent(getText(),"text/html");
        else
        	msg.setText(getText()); 
        
        
        
       if(this.filesName!=null)
       {
    	//tiene un archivo adjunto
       		BodyPart messageBodyPart = new MimeBodyPart();//Create the message part
           messageBodyPart.setText(text);// Fill the message
           System.out.println("MAIL:TEXT:"+text);
           
           Multipart multipart = new MimeMultipart();
           multipart.addBodyPart(messageBodyPart);
           
           for (int i = 0; i < 1; i++) {
        	   File f = new File(this.rutaAdjunto);
        	  messageBodyPart = new MimeBodyPart();
           	DataSource source = new FileDataSource(f);
           	messageBodyPart.setDataHandler(new DataHandler(source));
           	
           	System.out.println("MAIL:Attachs:"+f.toString());
           	messageBodyPart.setFileName(this.getFiles());
      			
           	multipart.addBodyPart(messageBodyPart);
            msg.setContent(multipart);
           }
       }
          
        try {
        	Transport transport=getTransport();
        	try{
        		//tratando de enviar, si el transport no esta correcto, va a fallar
        		transport.sendMessage(msg, msg.getAllRecipients()); 
        	}catch(java.lang.IllegalStateException e ){
        		System.out.println("Se esta reconectando el transport, se enviará en el siguiente thread");
        	}
        	System.out.println("MAIL:"+"Mail enviado con id "+this.id+"---------ENVIO EXITOSO-------------"+this.transport.getURLName() + "--"); 
        	System.out.println("Tratando de actualizar estatus en MYSQL--"+this.id); 
        	
        	//tratando de que el transport no alcance el límite de GSUITE
        	if(this.getMyName().equals("SOIA - AQ") ||this.getMyName().equals("PRUEBA - SOIA - AQ"))
        		{
        		MainEnvioAutomaticoAlertasBLOB.mailsEnviadosSOIA++;
        		System.out.println("Se han enviado un total de "+MainEnvioAutomaticoAlertasBLOB.mailsEnviadosSOIA+ "mails con el mismo transport");
        		//MainEnvioAutomaticoAlertasBLOB.SessionCheckerSimple(MainEnvioAutomaticoAlertasBLOB.mailsEnviadosSOIA,this.getMyName());
        		if(MainEnvioAutomaticoAlertasBLOB.mailsEnviadosSOIA>25){
        			//cierra el transport para evitar bloqueo GSUITE
        			MainEnvioAutomaticoAlertasBLOB.closeTransportSOIA();
        			MainEnvioAutomaticoAlertasBLOB.mailsEnviadosSOIA=0;
        		}
        		}
        	else 
        		{
        		MainEnvioAutomaticoAlertasBLOB.mailsEnviadosREPORTES++;
        		System.out.println("Se han enviado un total de "+MainEnvioAutomaticoAlertasBLOB.mailsEnviadosREPORTES+ "mails con el mismo transport");
        		if(MainEnvioAutomaticoAlertasBLOB.mailsEnviadosREPORTES>25){
        			//cierra el transport para evitar bloqueo GSUITE
        			MainEnvioAutomaticoAlertasBLOB.closeTransportREPORTES();
        			MainEnvioAutomaticoAlertasBLOB.mailsEnviadosREPORTES=0;
        		}
        		}
        	
        	//tratando de actualizar status en MYSQL 
        	 try{
        		
        		 //a este punto ya se envió el archivo sin errores
        		//tratando de actualizar en mysql  
            	String hora=definirHoraEnvio();
            	
            	this.setHayError(0);
            	this.setStatus(1);
            	this.horaEnvio=hora;
            	UpdateStatusMysql2();
            	BorrarAfterSending(this.rutaAdjunto);
   
        	 }
        	 catch(Exception e)
        	 {  
            	String hora=definirHoraEnvio();
            	//enviado sin errores
            	this.setHayError(0);
            	this.setStatus(1);
            	this.horaEnvio=hora;
            	System.err.println("Error al actualizar estatus de MYSQL, se reintentará mas tarde --"+this.id+ " Hora de incidencia "+hora);
            	e.printStackTrace();
        	 } 
        
        	
		} catch (Exception e) {
			//si sale mal en este punto solo lo va a quitar del hash para que se reenvíe
			System.out.println("Algo salió mal con el envió múltiple"+e.getMessage());
			e.printStackTrace();
			//por alguna razón, no se envió el mensaje, quitar del hash sin actualizar para reenvío
    		String hora=definirHoraEnvio();
    		this.setHoraEnvio(hora);
    		this.horaEnvio=hora;
    		int Reenvios=this.hayError;
    		Reenvios++;
    		this.setHayError(Reenvios);
    		current.replace(this.id,this);
    		control2.quitarLockHash(this.getId());
    		System.out.println("Por alguna razon, no se ha podido enviar --"+this.getId()+ "Se tratará de enviar mas tarde "+this.getHoraEnvio()+ "intento de reenvio #"+this.getHayError());
		}
        //System.out.println("Estado del transport "+getTransport().isConnected());
}
		
	
	//end send2
	
	
	
	
	
    
  
	/**
     * Pone datos para reportes@alvaroquintana.com
     * **/
    public void setPropsReportes()
    {	
    	setAuth(true);
    	
    	setUser("reportes@alvaroquintana.com");
    	setPassword("2k!BQ.r(!U<.JKB1");
    	setSmtp("smtp.gmail.com");
    	setPort("587");
    	
    	setMyName("Reportes - AQ");
    	setMyMail("reportes@alvaroquintana.com");
    }
    

    
    /**
     * @param Le cambia el nombre del destinatario.
     * Pone datos para reportes@alvaroquintana.com
     * **/
    public void setPropsReportes(String nombre)
    {	
    	setAuth(true);
    	
    	setUser("reportes@alvaroquintana.com");
    	setPassword("2k!BQ.r(!U<.JKB1");
    	setSmtp("smtp.gmail.com");
    	setPort("587");
    	
    	setMyName(nombre);
    	setMyMail("reportes@alvaroquintana.com");
    }
    
    /**
     * Pone datos para soia@alvaroquintana.com
     * **/
    public void setPropsSoia()
    {	
    	setAuth(true);
    	
    	setUser("soia@alvaroquintana.com");
    	setPassword("fx6GUxJE6&%,GNGH");
    	setSmtp("smtp.gmail.com");
    	setPort("587");
    	setMyName("SOIA - AQ");
    	setMyMail("soia@alvaroquintana.com");
    }
    
    
    
  
	@Override
	public void run(){ 
		try{
		//this.send();	
			this.sendMailOneSession();
		}catch (Exception e) 
			{
			System.out.println("No se logro enviar el correo por "+e.getMessage()+" Se quitará para su futuro reenvío");
			//liberando archivo por si cae en esta excepción para que se procese de nuevo
			current.remove(this.id);
			}
		//Thread.sleep(3000);
		
	}
	
	
	public boolean UpdateStatusMysql2(){
		boolean resultado=false;
		
		//hashmap update para que el productor sepa que esta en proceso de actualización
    	this.EnProcesoDeActualizacion=1;
    	current.replace(this.id,this);
    	
		try{
				try{
				conexion = new KonexionUtil(ACCESOS.IP_CLOUD,ACCESOS.DB_CLOUD,ACCESOS.DB_MAILER_USER,ACCESOS.DB_MAILER_PASS);
//				conexion = new  KonexionUtil(ACCESOS.DB_IP_LOCAL,ACCESOS.DB_SAAI,ACCESOS.DB_ROOT_USER,ACCESOS.DB_ROOT_PASS);
//				conexion = new  KonexionUtil(ACCESOS.DB_IP_LOCAL,ACCESOS.DB_SAAI,ACCESOS.DB_ROOT_USER,".....");
				//conexion.getConexion();
				}catch (Exception e){
					System.out.println("Error al actualizar estatus de mail"+e.getMessage());
					e.printStackTrace();
				}
			//limpiar sbSQL sentencia;
			sbSQL.setLength(0);
			if(getHayError()==0){
				
				String instruccion="UPDATE filtro_alertas_historico set  estado="+getStatus()+",hora_envio ='"+getHoraEnvio()+"' WHERE c_filtro_alertas_historico="+id+";";
//				String instruccion="UPDATE filtro_alertas_historico set  estado='ascd'"+",hora_envio ='"+hora+"' WHERE c_filtro_alertas_historico="+id+";";
				System.out.println(instruccion);
				sbSQL.append(instruccion);
			}else {
	
				String instruccion="UPDATE filtro_alertas_historico set  estado="+getStatus()+",errores='"+getErrorEnvio()+ "'  WHERE c_filtro_alertas_historico="+id;
				sbSQL.append(instruccion);
			}
			resultado = conexion.aplicar(sbSQL.toString());
			if(resultado){
				//liberar la conexion
				conexion.releaseConexion();
				//quitar el lock del hash hasta que todos los pasos anteriores se hayan ejecutado
				control2.quitarLockHash(this.getId());	
				System.out.println("MAIL:"+"Mail enviado con id "+this.id+"---------ACTUALIZADO EXITOSAMENTE EN MYSQL-----------PROCESO COMPLETO ------"); 
			}else {
				//no se logró actualizar, quitar bloqueo para que lo intente mas tarde
				this.EnProcesoDeActualizacion=0;
				current.replace(this.id,this);
				
				System.out.println("Hubo un error al actualizar status del correo en la sentencia Aplicar");
			}
			
		}catch(Exception e)
			{
			//excepción al actualizar reemplaza para que sepa que no esta en actualizacion 
			this.EnProcesoDeActualizacion=0;
			current.replace(this.id,this);
			System.out.println("Error en el Thread de envio, no se puede Actualizar Status en MYSQL "+e.getMessage());
			}
		
		conexion.releaseConexion();
		return resultado;
		
	}

	public String definirHoraEnvio(){
		Date fechaActual= new Date();
		SimpleDateFormat sdf= new SimpleDateFormat("HH:mm:ss");
		String hora=sdf.format(fechaActual);
		return hora; 
	}
	
	  public void InitialConfigAndProps(String username)
	  {
	    	if(username.equals("SOIA - AQ")) 
	    		setPropsSoia();
	    	else 
	    		setPropsReportes(username);
	    	
	    	 from = myName+"<"+myMail+">"; 
			 Properties props = System.getProperties();
			 props.setProperty("mail.smtp.host","smtp.gmail.com");
			 props.setProperty("mail.smtp.port", "25");
			 props.setProperty("mail.smtp.auth", "true");
			 props.put("mail.smtp.starttls.enable", "true");
			 props.setProperty("mail.smtp.localhost", "127.0.0.1");
			 Session session = Session.getInstance(props);
			try {
				transport = session.getTransport("smtp");
				} 
			catch (NoSuchProviderException e1) {System.out.println("Algo salió mal con el transport"+e1.getMessage());}
			
			 try 
			 	{
				this.transport.connect(this.smtp, this.user, this.password);
			 	}
			 catch (MessagingException e) {System.out.println("Algo salió mal con la conexión del transport"+e.getMessage());}

	    }
	    
	    public void refreshSession(String username)
	    {
	    	try 
	    		{
				this.transport.close();
				transport.connect();
	    		} 
	    	catch (MessagingException e)
	    		{
				// TODO Auto-generated catch block
				e.printStackTrace();
				InitialConfigAndProps(username);
	    		}
	    	
	    }

	    private void BorrarAfterSending(String ruta){
	    	if(ruta!=null)
	    	{
	    	File file = new File(ruta); 
	          
	        if(file.delete()) 
	        	{ 
	            System.out.println("File deleted successfully"); 
	        	} 
	        	else
	        	{ 
	            System.out.println("Failed to delete the file"); 
	        	} 
	    	}
	    	else
	    		{
	    		System.out.println("procesado bien");
	    		}
	    }
	    
	    	
	


}//class
