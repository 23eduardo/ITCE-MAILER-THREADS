package main;

import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class InitialConfig  {
	
	
    private String smtp;
    private String user;  
    private String password; 
	private String port;
	private Transport transport;
	private Session session;
	private String myName; 
	private String myMail;
	private String from;
	
    
	


	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public String getMyName() {
		return myName;
	}

	public void setMyName(String myName) {
		this.myName = myName;
	}

	public String getMyMail() {
		return myMail;
	}

	public void setMyMail(String myMail) {
		this.myMail = myMail;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getSmtp() {
		return smtp;
	}

	public void setSmtp(String smtp) {
		this.smtp = smtp;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public Transport getTransport() {
		return transport;
	}

	public void setTransport(Transport transport) {
		this.transport = transport;
	}

	public void sendMultiple(ArrayList <MailCallable> arr) throws MessagingException{
			
		
		    for (int i = 0; i < arr.size(); i++) {

		    	//de quien 
		        Message msg = new MimeMessage(session);
	            msg.setFrom(new InternetAddress(arr.get(i).getFrom()));
	            System.out.println("MAIL:FROM:"+arr.get(i).getFrom());
	            
		        
		        if ( arr.get(i).getTo()!= null)
	            {	
	            	try{
	            	msg.setRecipients(Message.RecipientType.TO,InternetAddress.parse(arr.get(i).getTo(), false)); 
	            	System.out.println("MAIL:TO:"+arr.get(i).getTo());
	            	}catch (Exception e ){}
	            }
		        
		        //si hay algo en el campo de CC lo asignamos a los destinatarios
	            if ( arr.get(i).getCc() != null) 
	            {	
	            	msg.setRecipients(Message.RecipientType.CC,InternetAddress.parse(arr.get(i).getCc(), false));
	            	System.out.println("MAIL:CC:"+arr.get(i).getCc() );
	            }
	            //otra copia 
	            if (arr.get(i).getBcc() != null)
	            {	
	            	msg.setRecipients(Message.RecipientType.BCC,InternetAddress.parse(arr.get(i).getBcc(), false));
	            	System.out.println("MAIL:BCC:"+arr.get(i).getBcc());
	            }
	     
	            msg.setSubject(arr.get(i).getSubject());//asignamos el asunto del mensaje 
	            msg.setHeader("X-Mailer", "smtpsend"); 
	            msg.setSentDate(new Date());//la fecha de envio del mensaje 
	        
	            if(arr.get(i).isHtml())
                	msg.setContent(arr.get(i).getText(),"text/html");
                else
                	msg.setText(arr.get(i).getText()); 
	            
	            try {
	            	transport.sendMessage(msg, msg.getAllRecipients()); 
				} catch (Exception e) {
					System.out.println("Algo salió mal con el envió múltiple"+e.getMessage());
				}
	            System.out.println("Estado del transport "+transport.isConnected());
		    }

		    //transport.close();
		    while(transport.isConnected())
		    System.out.println("Estado del transport "+transport.isConnected() + " hora "+new Date());
	 }
	
	public void sendMailOneSession(MailCallable mc) throws MessagingException{

	    	//de quien 
	        Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(mc.getFrom()));
            System.out.println("MAIL:FROM:"+mc.getFrom());
            
	        
	        if ( mc.getTo()!= null)
            {	
            	try{
            	msg.setRecipients(Message.RecipientType.TO,InternetAddress.parse(mc.getTo(), false)); 
            	System.out.println("MAIL:TO:"+mc.getTo());
            	}catch (Exception e ){}
            }
	        
	        //si hay algo en el campo de CC lo asignamos a los destinatarios
            if ( mc.getCc() != null) 
            {	
            	msg.setRecipients(Message.RecipientType.CC,InternetAddress.parse(mc.getCc(), false));
            	System.out.println("MAIL:CC:"+mc.getCc() );
            }
            //otra copia 
            if (mc.getBcc() != null)
            {	
            	msg.setRecipients(Message.RecipientType.BCC,InternetAddress.parse(mc.getBcc(), false));
            	System.out.println("MAIL:BCC:"+mc.getBcc());
            }
     
            msg.setSubject(mc.getSubject());//asignamos el asunto del mensaje 
            msg.setHeader("X-Mailer", "smtpsend"); 
            msg.setSentDate(new Date());//la fecha de envio del mensaje 
        
            if(mc.isHtml())
            	msg.setContent(mc.getText(),"text/html");
            else
            	msg.setText(mc.getText()); 
            
            try {
            	transport.sendMessage(msg, msg.getAllRecipients()); 
			} catch (Exception e) {
				System.out.println("Algo salió mal con el envió múltiple"+e.getMessage());
			}
            System.out.println("Estado del transport "+transport.isConnected());
 }
	 
	 public void setPropsReportes()
	    {
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
	    	setUser("soia@alvaroquintana.com");
	    	setPassword("fx6GUxJE6&%,GNGH");
	    	setSmtp("smtp.gmail.com");
	    	setPort("587");
	    	setMyName("SOIA - AQ");
	    	setMyMail("soia@alvaroquintana.com");
	    }
	    
	    public void InitialConfigAndProps(String username){
	    	
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
			} catch (NoSuchProviderException e1) {
				System.out.println("Algo salió mal con el transport"+e1.getMessage());
			}

			 try {
				this.transport.connect(this.smtp, this.user, this.password);
			} catch (MessagingException e) {
				System.out.println("Algo salió mal con la conexión del transport"+e.getMessage());
			}

	    }
	    
	    public void refreshSession(String username){
	    	try {
	    		System.out.println("TRATANDO DE CERRAR EL TRANSPORT --ESTADO ACTUAL-- "+this.transport.isConnected());
				this.transport.close();
				System.out.println("VERIFICANDO QUE EN VERDAD SE CERRO EL TRANSPORT --ESTADO ACTUAL-- "+this.transport.isConnected());
				transport.connect();
				System.out.println("NUEVO TRANSPORT OBTENIDO --ESTADO ACTUAL-- "+this.transport.isConnected()+" "+transport.getURLName());
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				InitialConfigAndProps(username);
				
			}
	    	
	    }
	    
	    public void cerrarSesionSTMP(){
	    	
	    }

	

}
