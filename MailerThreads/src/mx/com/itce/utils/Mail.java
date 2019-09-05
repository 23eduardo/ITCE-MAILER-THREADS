package mx.com.itce.utils;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;

import com.sun.mail.smtp.SMTPTransport;

public class Mail
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
    private String[] file=null;
    private String filesName;
	private String port;
	private boolean html=false;
	private int isPrueba;
	private byte[] DocumentBlob;
	public String contenidoAdjunto;
    
	

	/**
	 * smtp="smtp.gmail.com"; <br>
	 * port="587";
	 * **/
	public Mail ()
	{
		this.smtp="smtp.gmail.com"; 
	    this.port="587";
	   
	}
	
	public Mail (String smtp, String port) 
	{ 
		this.smtp=smtp; 
	    this.port=port; 
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
    
    public String getAttachmentFileString(){return formatedFilesMysql();} 
    public void setAttachmentFile(String[] file){	this.file = file;	}
    
    //sobrecarga para que asigne el array de adjuntos con un String separado por (,)
    public void setAttachmentFile(String file){	this.file=file.split(",");}
    
    public String getFiles(){	return filesName;	}
    public void setFiles(String files){	this.filesName=files;	}
    
    public String getPort(){	return port;	}
    public void setPort(String port){	this.port=port;	}
    
    public void setHTML (boolean html){	this.html=html;	}
   
    public int getIsPrueba() {return isPrueba;}
	public void setPrueba(int isPrueba) {this.isPrueba = isPrueba;}
    
	
	 
    
    //--------------------------------------------------------------
    //--------------------------------------------------------------
    
    public byte[] getDocumentBlob() {
		return DocumentBlob;
	}

	public void setDocumentBlob(byte documentBlob[]) {
		DocumentBlob = documentBlob;
	}

	public boolean isHtml() {
		return html;
	}

	public void setHtml(boolean html) {
		this.html = html;
	}

	public void send()
    {	
    	String mailer = "smtpsend"; 
        boolean ssl = false; 
         
        //esto es para establecer el remitente (el que lo envia)
        String from = myName+"<"+myMail+">"; 
         
        try 
        {	
        	System.out.println("\nMAIL:"+"Enviando mail..."); 
        	//tomamos las propiedades del sistema,que es como una estructura de datos que tiene el protocolo para enviar datos a un mail..
            /**TODO**/
        	//Properties props = System.getProperties(); 
            Properties props = new Properties();
            
            /****
            if (auth) 
                props.put("mail.smtp.auth", auth); 
            /****/
            
            props.setProperty("mail.smtp.host", smtp);
            
            if(port != null)
            	props.setProperty("mail.smtp.port", port);
            else
            	props.setProperty("mail.smtp.port", "25");
            
            props.setProperty("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.setProperty("mail.smtp.localhost", "127.0.0.1");
            /****/
            
            
            Session session = Session.getDefaultInstance(props); 
            //creamos el objeto mensaje para enviar 
            Message msg = new MimeMessage(session); 
            
            //asignamos el remitente 
            if (from != null) 
            {	
            	msg.setFrom(new InternetAddress(from));
            	System.out.println("MAIL:FROM:"+from);
            }
            
            //agregamos al destinatario
            if (to != null)
            {	
            	msg.setRecipients(Message.RecipientType.TO,InternetAddress.parse(to, false)); 
            	System.out.println("MAIL:TO:"+to);
            }
            
            //si hay algo en el campo de CC lo asignamos a los destinatarios
            if (cc != null) 
            {	
            	msg.setRecipients(Message.RecipientType.CC,InternetAddress.parse(cc, false));
            	System.out.println("MAIL:CC:"+cc);
            }
            //otra copia 
            if (bcc != null)
            {	
            	msg.setRecipients(Message.RecipientType.BCC,InternetAddress.parse(bcc, false));
            	System.out.println("MAIL:BCC:"+bcc);
            }
     
            msg.setSubject(subject);//asignamos el asunto del mensaje 
            msg.setHeader("X-Mailer", mailer); 
            msg.setSentDate(new Date());//la fecha de envio del mensaje 
                        
            if(file!=null)
            {
            	BodyPart messageBodyPart = new MimeBodyPart();//Create the message part
                messageBodyPart.setText(text);// Fill the message
                System.out.println("MAIL:TEXT:"+text);
                
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messageBodyPart);
                
                //Part two is attachment
                for(int i = 0; i < file.length; i++)
                {	
                	messageBodyPart = new MimeBodyPart();
                	DataSource source = new FileDataSource(file[i]);
                	messageBodyPart.setDataHandler(new DataHandler(source));
                	
                	System.out.println("MAIL:Attachs:"+file[i]);
                	messageBodyPart.setFileName(file[i].split("\\/")[file[i].split("\\/").length-1]);
           			
                	multipart.addBodyPart(messageBodyPart);
                }
                
                System.out.println("MAIL:Attachs:"+file.length);
                // Put parts in message
                msg.setContent(multipart);
            } 
            else
            {
            	//seleccionamos el envio del texto con formato html si quieres que sea texto plano, puedes usar la funcion
            	
                if(html)
                	msg.setContent(text,"text/html");
                else
                	msg.setText(text); 
                
                //System.out.println("MAIL:TEXT:"+text);
            }
            
            //creamos el objeto encargado de enviar el mensaje 
           //SMTPTransport t = (SMTPTransport)session.getTransport(ssl ? "smtps" : "smtp"); 
            SMTPTransport t = (SMTPTransport)session.getTransport("smtp");
            
            try 
            {	//si necesita autentificacion, nos conectamos con el usuario y pass   
                //si no tambien nos conectamos, pero sin autentificarnos 
            	
                //if (auth)	
                    t.connect(smtp, user, password); 
                //else 
                //    t.connect();
                    
                //enviamos el mensaje a todos los destinatarios 
                t.sendMessage(msg, msg.getAllRecipients()); 
            } 
            finally 
            {	
            	//cerramos la conexion con el SMTP 
                t.close(); 
            } 
             
            System.out.println("MAIL:"+"Mail enviado..."); 
        } 
        catch (Exception e){	e.printStackTrace();	} 
    }//send()
    
    
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
    
    public void setPropsPruebas()
    {	
    	setAuth(true);
    	
    	setUser("eduardo@alvaroquintana.com");
	    setPassword("Ajkl2009");
    	setSmtp("smtp.gmail.com");
    	setPort("587");
    	
    	setMyName("Eduardo Álvarez Álvarez");
    	setMyMail("eduardo@alvaroquintana.com");
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
    	
    	setMyName(nombre+" - AQ");
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
    
    private String formatedFilesMysql(){
    	String a="";
    	for(String s : file){
    	a+=s+",";
    	}
    	//quita la ultima coma de archivos
    	a=a.substring(0,a.length()-1);
    	return a;
    }
    
    public static void main(String[] args)
    {
    	//Mail mail= new Mail();
    	
    	//mailer.enviarHTML("smtp.itce.com.mx", "true", "soia@itce.com.mx", to, subject, textoMail, "soia@itce.com.mx", "RgKrJaRa23");
		
		//mailer.enviarHTML("smtp.anmecpreval.com.mx", "true", "soia@anmecpreval.com.mx", to, subject, textoMail, "soia@anmecpreval.com.mx", "dwqdrk7c"); // ANMEC
		//mailer.enviarHTML("smtp.anmecpreval.com.mx", "true", "av_soia@anmecpreval.com.mx", to, subject, textoMail, "av_soia@anmecpreval.com.mx", "dwqdrk7c");
		//mailer.enviarHTML("smtp.prevalaniq.com.mx", "true", "soia@prevalaniq.com.mx", to, subject, textoMail, "soia@prevalaniq.com.mx", "ycd8j1j4"); // ANIQ
    	//em.enviarHTML("smtp.anmecpreval.com.mx", "true", "av_soia@anmecpreval.com.mx", "equipoTI@itce.com.mx", "test", "<html><body><b>Prueba...Hola</b>&nbsp; mundo</body></html>", "av_soia@anmecpreval.com.mx", "dwqdrk7c");
    	//mail.enviarHTML("smtp.itce.com.mx", "true", "soia00@anmecpreval.com.mx", "a.calleja@itce.com.mx,ale_calleja@hotmail.com", "test", "<html><body><b>Prueba...Hola</b>&nbsp; mundo</body></html>", "soia00@anmecpreval.com.mx", "soia123");
    	
    	
    }//main
    
  
    
}//class