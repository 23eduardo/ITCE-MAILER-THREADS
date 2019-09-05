package main;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

import javax.mail.MessagingException;

import mx.com.itce.utils.ACCESOS;
import mx.com.itce.utils.KonexionUtil;


public class EmailUrgenteProducer{
	final int TIME=10;
	int TIME_C=0;
	static Date fechaActual;
	static SimpleDateFormat sdf;
	static String hora; 
	static String fecha;
	static boolean condicion=true;
	static StringBuilder sbSQL = new StringBuilder(); //las sentencias sql que ocupara el programa
	static SimpleDateFormat fechalog = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	KonexionUtil conexion; 
	public HashMap<String,MailCallable> current = new HashMap<>();
	ArrayList <MailCallable>pruebasMail = new ArrayList<MailCallable>();
	private final BlockingQueue<MailCallable> queue;
	boolean isPrueba;
	String destinatariosOriginales,destinatariosCopia,destinatariosOcultos;
	MailCallable mail;
	boolean existeEnThread=false;
	InitialConfig smeSOIA;
	InitialConfig smeREPORTES;
	
	//inyeccion de constructores hacia el queue
	public EmailUrgenteProducer(BlockingQueue<MailCallable> queue2, HashMap <String,MailCallable> current){
		this.queue = queue2;
		this.current=current;
		//nuevo objeto vacio, solo se hace una sola vez.
		mail= new MailCallable();
		
	}
	
	//unicamente para pruebas, se va a retirar despues
	public EmailUrgenteProducer(){
		//nuevo objeto vacio, solo se hace una sola vez.
		this.queue=null;
		mail= new MailCallable();
		
	}

	
	/***
	 * Este método obtiene los mensajes urgentes y decodifica su cuerpo blob a texto, aún no se ha retirado el método anterior por qu eno se sabe que método se usará
	 * @throws SQLException
	 */
	public void checkCorreosAtrasadosBLOB() throws SQLException{
		//formateo de hora y fecha para mysql
		//limpiar la cadena sql 
		sbSQL.setLength(0);
		fechaActual= new Date();
		//fechaActual=new Date(new Date().getTime()+28800000);
		System.out.println();
		System.out.println(fechaActual); 
		//HH en mayuscula te da el formato 24 horas en lugar de 12
		sdf= new SimpleDateFormat("HH:mm:ss");
		hora=sdf.format(fechaActual);
		//System.out.println(hora); 
		sdf=null;
		sdf=new SimpleDateFormat("yyyy-MM-dd");
		fecha= sdf.format(fechaActual.getTime());
		//System.out.println(fecha); 
		
		try {

			if(this.conexion==null)
			
				{
				conexion=new KonexionUtil(ACCESOS.IP_CLOUD,ACCESOS.DB_CLOUD,ACCESOS.DB_MAILER_USER,ACCESOS.DB_MAILER_PASS); 
//				conexion = new KonexionUtil(ACCESOS.DB_IP_LOCAL,ACCESOS.DB_SAAI,ACCESOS.DB_ROOT_USER,ACCESOS.DB_ROOT_PASS);
//				conexion = new KonexionUtil(ACCESOS.DB_IP_LOCAL,ACCESOS.DB_SAAI,ACCESOS.DB_ROOT_USER,"--");
				}
			else
				{
				this.conexion.checkConnection();
				}
			} 
		catch (Exception e) 
			{
			System.out.println("Excepción en Thread Productor "+e.getMessage());
			}
	

		sbSQL.append("SELECT c_filtro_alertas_historico,destinatario,destinatario_copia,destinatario_copia_oculta,asunto,body_blob,remitente,isHTML,prueba,adjunto_nombre "); 
		sbSQL.append("FROM  filtro_alertas_historico ");
		sbSQL.append("WHERE (((fecha <'"+fecha+"') or (fecha ='"+fecha+"' and hora <'"+hora+"')) AND estado=0 AND frecuencia=0) ");
		sbSQL.append("ORDER BY ");
		sbSQL.append("fecha ASC,");
		sbSQL.append("hora ASC;");

		try { 
			existeEnThread=false;
			//System.out.println(conexion);
			System.out.println("---EL THREAD PRODUCTOR COMIENZA -----");
			ResultSet rs=conexion.ejecutar(sbSQL.toString());
			boolean HtmlContent=false; 
			//RESULTSET de los que estan en la bd
			while (rs.next())
			{
				//clear sbSQL variable to listen other variables.
				sbSQL.setLength(0);
				
				if(current.containsKey(rs.getString("c_filtro_alertas_historico")))
				{
					String idReenvio=rs.getString("c_filtro_alertas_historico");
					//aqui ya contiene el mail en proceso por lo que ni siquiera va a crear un objeto mail, no tiene sentido.
					//tratando nuevamente de hacer un update, puesto que se mando pero no se actualizó en MYSQL 
					
					existeEnThread=true;
					//obteniendo mail registrado para ver a que hora se envió
					mail = current.get(idReenvio);
					if(mail.getHoraEnvio()==null){
						System.out.println("Aun en proceso "+ mail.getId());
					}else{
						//reintento por si no se logra actualizar el status mysql 
						mail.UpdateStatusMysql2();
					}
				}
				
				if(existeEnThread==false){
					//asignación de objeto mail 
					System.out.println("Correo encontrado para enviar con ID -- "+rs.getString("c_filtro_alertas_historico")+"--"+this.fecha + ":" +this.hora);
					//obteniendo y decodificando blob
					java.sql.Blob ablob = rs.getBlob("body_blob");
					String mensaje = new String(ablob.getBytes(1l, (int) ablob.length()));
					//System.out.println(mensaje);

					StringBuffer strOut = new StringBuffer();
					String aux;
					// We access to stream, as this way we don't have to use the CLOB.length() which is slower...
					Blob blob = rs.getBlob("body_blob");
					BufferedReader br = new BufferedReader(new InputStreamReader(blob.getBinaryStream()));
					while ((aux=br.readLine())!=null) {
						strOut.append(aux);
					}
					mail = new MailCallable(current); 
					//soia, reportes, ¿otro? REMITENTE
					if(rs.getString("remitente").equals("Reportes - AQ")) mail.setPropsReportes(); else if (rs.getString("remitente").equals("SOIA - AQ")) mail.setPropsSoia();else mail.setPropsReportes(rs.getString("remitente")); 
					mail.setText(mensaje);
					mail.setSubject(rs.getString("asunto"));
					mail.setId(rs.getString("c_filtro_alertas_historico"));
					//verificando si se trata de un correo de prueba y si es html
					isPrueba=(rs.getInt("prueba")==1)? true:false;
					HtmlContent = (rs.getInt("isHTML")==1)? true: false; 
					mail.setHTML(HtmlContent);
					//getting to variables 
					destinatariosOriginales= rs.getString("destinatario");
					destinatariosCopia= rs.getString("destinatario_copia");
					destinatariosOcultos=rs.getString("destinatario_copia_oculta");
					String archivosAdjuntos=rs.getString("adjunto_nombre");
					
					//revisando si se tiene algún archivo adjunto
					if(archivosAdjuntos==null || archivosAdjuntos.length()<0)
					{
						System.out.println("Correo sin archivos adjuntos");
					}
					else
					{
						//asignando archivos adjuntos
						mail.setFiles(rs.getString("adjunto_nombre"));
						System.out.println("El correo tiene Archivos adjuntos -- "+mail.getFiles());
						Blob blob1 = rs.getBlob("body_blob");
						int blobLength = (int) blob1.length();  
						byte[] blobAsBytes = blob1.getBytes(1, blobLength);
						mail.blobAttach=blobAsBytes;

						//release the blob and free up memory. (since JDBC 4.0)
						
						blob1.free();
						

						
						
					}
					if(isPrueba)
					{

						mail.setTo("eduardo@alvaroquintana.com,alejandro@alvaroquintana.com");
						if(HtmlContent)
						{
							mail.addText("\n<h4>Destinatario(s) con copia oculta: "+destinatariosOcultos+"</h4>");
							mail.addText("\n<h4>Con copia: "+destinatariosCopia+"</h4>");
							mail.addText("\n<h4>Destinatario(s) original(es): "+destinatariosOriginales+"</h4>");	
							mail.addText("<h3>Datos del correo Original</h3>");
						}
						else//es contenido sin html 
						{
							mail.addText("\n");
							mail.addText("\nDestinatario(s) con copia oculta: "+destinatariosOcultos);
							mail.addText("\nCon copia: "+destinatariosCopia);
							mail.addText("\nDestinatario(s) original(es): "+destinatariosOriginales);	
							mail.addText("\nDatos del correo Original");
						}
					}
					else
					{ 

						mail.setTo(destinatariosOriginales);
						mail.setCc(destinatariosCopia);
						mail.setBcc(destinatariosOcultos);
					}//else is prueba
					
		
					current.put(mail.getId(), mail);
					queue.add(mail);
					
				}//end if el correo no existe en 
			}//end while	


		}catch (Exception e)
			{
			//excepción cuando no se logra conectar. El programa termina para que el centinela lo levante
			System.out.println("Error, hay algo malo con la conexión (AUTENTICACIÓN)"+e.getMessage());
			System.exit(1);
			}
		
		conexion.releaseConexion();
		System.out.println("Se cerró la conexión con el servidor -- ");
		System.out.println(this.fecha + ":" +this.hora+"---EL THREAD PRODUCTOR TERMINA  -----");
		

	}
	
	
	public void pruebaMultiMail(long counter) throws SQLException{
		//abriendo sesiones y transport 
		if(counter==0){
			smeSOIA = new InitialConfig(); 
			smeSOIA.InitialConfigAndProps("SOIA - AQ");
			smeREPORTES = new InitialConfig(); 
			smeREPORTES.InitialConfigAndProps("reportes");	
		}else {
			System.out.println("El transport de SOIA tiene estado "+smeSOIA.getTransport().isConnected());
			System.out.println("El transport de REPORTES tiene estado "+smeREPORTES.getTransport().isConnected());
			if(smeSOIA.getTransport().isConnected()==false){
				try {
					//si no esta conectado, cierra la conexion y trata de conectarse de nuevo
					smeSOIA.refreshSession("SOIA - AQ");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(smeREPORTES.getTransport().isConnected()==false){
				try {
					smeREPORTES.refreshSession("REPORTES - AQ");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}
		
		sbSQL.setLength(0);
		fechaActual= new Date();
		//fechaActual=new Date(new Date().getTime()+28800000);
		System.out.println();
		System.out.println(fechaActual); 
		//HH en mayuscula te da el formato 24 horas en lugar de 12
		sdf= new SimpleDateFormat("HH:mm:ss");
		hora=sdf.format(fechaActual);
		//System.out.println(hora); 
		sdf=null;
		sdf=new SimpleDateFormat("yyyy-MM-dd");
		fecha= sdf.format(fechaActual.getTime());
		//System.out.println(fecha); 
		
		try {

			if(this.conexion==null)
			
				{
				conexion=new KonexionUtil(ACCESOS.IP_CLOUD,ACCESOS.DB_CLOUD,ACCESOS.DB_MAILER_USER,ACCESOS.DB_MAILER_PASS); 
//				conexion = new KonexionUtil(ACCESOS.DB_IP_LOCAL,ACCESOS.DB_SAAI,ACCESOS.DB_ROOT_USER,ACCESOS.DB_ROOT_PASS);
//				conexion = new KonexionUtil(ACCESOS.DB_IP_LOCAL,ACCESOS.DB_SAAI,ACCESOS.DB_ROOT_USER,"--");
				}
			else
				{
				this.conexion.checkConnection();
				}
			} 
		catch (Exception e) 
			{
			System.out.println("Excepción en Thread Productor "+e.getMessage());
			}
	

		sbSQL.append("SELECT c_filtro_alertas_historico,destinatario,destinatario_copia,destinatario_copia_oculta,asunto,body_blob,remitente,isHTML,prueba,adjunto_nombre,adjunto_blob "); 
		sbSQL.append("FROM  filtro_alertas_historico ");
		sbSQL.append("WHERE (((fecha <'"+fecha+"') or (fecha ='"+fecha+"' and hora <'"+hora+"')) AND estado=0 AND frecuencia=0"
				+ ") ");
		sbSQL.append("ORDER BY ");
		sbSQL.append("fecha ASC,");
		sbSQL.append("hora ASC limit 23;");
		
		//System.out.println("Esta es la sentencia preparada "+sbSQL.toString());

		try { 
			existeEnThread=false;
			System.out.println("---EL THREAD PRODUCTOR COMIENZA -----");
			ResultSet rs=conexion.ejecutar(sbSQL.toString());
			boolean HtmlContent=false; 
			
	
			while (rs.next())
			{
				//clear sbSQL variable to listen other variables.
				sbSQL.setLength(0);
				isPrueba=(rs.getInt("prueba")==1)? true:false;
				
				if(current.containsKey(rs.getString("c_filtro_alertas_historico")))
				{
					String idReenvio=rs.getString("c_filtro_alertas_historico");
					//aqui ya contiene el mail en proceso por lo que ni siquiera va a crear un objeto mail, no tiene sentido.
					//tratando nuevamente de hacer un update, puesto que se mando pero no se actualizó en MYSQL 
					
					existeEnThread=true;
					//obteniendo mail registrado para ver a que hora se envió
					mail = current.get(idReenvio);
					if(mail.getEnProcesoDeActualizacion()==1){
						System.out.println("Aun en proceso "+ mail.getId());
					}else{
						//reintento por si no se logra actualizar el status mysql 
						mail.UpdateStatusMysql2();
					}
				}
				
				if(existeEnThread==false){
					//asignación de objeto mail 
					System.out.println("Correo encontrado para enviar con ID -- "+rs.getString("c_filtro_alertas_historico")+"--"+this.fecha + ":" +this.hora);
					//obteniendo y decodificando blob
					java.sql.Blob ablob = rs.getBlob("body_blob");
					String mensaje = new String(ablob.getBytes(1l, (int) ablob.length()));
					mail = new MailCallable(current); 
					//soia, reportes, ¿otro? REMITENTE
					if(rs.getString("remitente").equals("Reportes - AQ"))
						{
							if(isPrueba)
							{
							mail.setFrom("PRUEBA - REPORTES - AQ"+"<reportes@alvaroquintana.com>");
							mail.setMyName("PRUEBA - REPORTES - AQ");
							}
							else
								{
								mail.setFrom("REPORTES - AQ"+"<reportes@alvaroquintana.com>");
								mail.setMyName("REPORTES - AQ");
								}
					
						}//remitente reportes
						else if(rs.getString("remitente").equals("SOIA - AQ"))
							{ 
							if(isPrueba)
								{
								mail.setFrom("PRUEBA - SOIA - AQ"+"<soia@alvaroquintana.com>");
								mail.setMyName("PRUEBA - SOIA - AQ");
								
								}
							else{
								mail.setFrom("SOIA - AQ"+"<soia@alvaroquintana.com>");
								mail.setMyName("SOIA - AQ");
							}
							}//remitente SOIA
							else
								{
								String remitente= rs.getString("remitente");
								mail.setMyName(remitente);
								mail.setFrom(remitente+"<reportes@alvaroquintana.com>");
								}
					mail.setText(mensaje);
					mail.setSubject(rs.getString("asunto"));
					mail.setId(rs.getString("c_filtro_alertas_historico"));
					//verificando si se trata de un correo de prueba y si es html
					
					HtmlContent = (rs.getInt("isHTML")==1)? true: false; 
					mail.setHTML(HtmlContent);
					//getting to variables 
					destinatariosOriginales= rs.getString("destinatario");
					destinatariosCopia= rs.getString("destinatario_copia");
					destinatariosOcultos=rs.getString("destinatario_copia_oculta");
					String archivosAdjuntos=rs.getString("adjunto_nombre");
					
					//revisando si se tiene algún archivo adjunto
					if(archivosAdjuntos==null || archivosAdjuntos.length()<0)
					{
						System.out.println("Correo sin archivos adjuntos");
					}
					else
					{
						//asignando archivos adjuntos
						mail.setFiles(rs.getString("adjunto_nombre"));
						System.out.println("El correo tiene Archivos adjuntos -- "+mail.getFiles());
						Blob blob3 = rs.getBlob("adjunto_blob");
						InputStream in = blob3.getBinaryStream();
						String rutaAdjunto="./adjuntosprocesados/"+rs.getString("adjunto_nombre");
						OutputStream out = new FileOutputStream(rutaAdjunto);
						mail.rutaAdjunto=rutaAdjunto;
						
						//creating temporal attachment
						FileOutputStream fop = null;
						File file;
						String content = "This is the text content";

						try {

							file = new File(rutaAdjunto);
							fop = new FileOutputStream(file);

							// if file doesnt exists, then create it
							if (!file.exists()) {
								file.createNewFile();
							}

							// get the content in bytes
							byte[] buff = blob3.getBytes(1,(int)blob3.length());

							fop.write(buff);
							fop.flush();
							fop.close();

							System.out.println("Done");

						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							try {
								if (fop != null) {
									fop.close();
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					
						//
						
						
					}
					if(isPrueba)
					{

						mail.setTo("sistemas@alvaroquintana.com");
						if(HtmlContent)
						{
							//contenido HTML
							mail.addText("\n<h4>Destinatario(s) con copia oculta: "+destinatariosOcultos+"</h4>");
							mail.addText("\n<h4>Con copia: "+destinatariosCopia+"</h4>");
							mail.addText("\n<h4>Destinatario(s) original(es): "+destinatariosOriginales+"</h4>");	
							mail.addText("<h3>Datos del correo Original</h3>");
						}
						else
							//es contenido sin html 
						{
							mail.addText("\n");
							mail.addText("\nDestinatario(s) con copia oculta: "+destinatariosOcultos);
							mail.addText("\nCon copia: "+destinatariosCopia);
							mail.addText("\nDestinatario(s) original(es): "+destinatariosOriginales);	
							mail.addText("\nDatos del correo Original");
						}
					}
					else
					{ 

						mail.setTo(destinatariosOriginales);
						mail.setCc(destinatariosCopia);
						mail.setBcc(destinatariosOcultos);
					}//else is prueba
					
					//setting transport and session
				
					
					
					current.put(mail.getId(), mail);
					pruebasMail.add(mail);
					queue.add(mail);
					
				}//end if el correo no existe en 
			}//end while
			
			if(!rs.next()){
				System.out.println("Nada nuevo por aqui-- SIN CORREOS NUEVOS");
			}


		}catch (Exception e)
			{
			//excepción cuando no se logra conectar. El programa termina para que el centinela lo levante
			System.out.println("Error, hay algo malo con la conexión (AUTENTICACIÓN)"+e.getMessage());
			e.printStackTrace();
			System.exit(1);
			}
		
		conexion.releaseConexion();
		System.out.println("Se cerró la conexión con el servidor -- ");
		System.out.println(this.fecha + ":" +this.hora+"---EL THREAD PRODUCTOR TERMINA  -----");
	
	}
	
	

	public void checkCorreosAtrasadosPorHora(String dateMysql, String horaInicio, String horaFinal) throws SQLException{
		sbSQL.setLength(0);
		//formateo de hora y fecha para mysql
		fechaActual= new Date();
		//fechaActual=new Date(new Date().getTime()+28800000);
		System.out.println();
		System.out.println(fechaActual); 
		//HH en mayuscula te da el formato 24 horas en lugar de 12
		sdf= new SimpleDateFormat("HH:mm:ss");
		hora=sdf.format(fechaActual);
		System.out.println(hora); 
		sdf=null;
		sdf=new SimpleDateFormat("yyyy-MM-dd");
		fecha= sdf.format(fechaActual.getTime());
		System.out.println(fecha); 
		//hora2= 
		sbSQL.append("SELECT c_filtro_alertas_historico,destinatario,destinatario_copia,destinatario_copia_oculta,asunto,body_blob,remitente,isHTML "); 
		sbSQL.append("FROM  filtro_alertas_historico ");
		sbSQL.append("WHERE (((fecha ='"+fecha+"') and (hora >='"+horaInicio+"' AND hora <='"+horaFinal+"')) AND estado=0 AND frecuencia=1) ");
		sbSQL.append("ORDER BY ");
		sbSQL.append("fecha ASC,");
		sbSQL.append("hora ASC;");

		try { 
			System.out.println(sbSQL);
			System.out.println("---EL THREAD PRODUCTOR COMIENZA POR HORA DE  --"+horaInicio + " A "+horaFinal);
			ResultSet rs=conexion.ejecutar(sbSQL.toString());
			boolean HtmlContent=false; 
			//RESULTSET de los que estan en la bd
			while (rs.next())
			{
				//clear sbSQL variable to listen other variables.
				sbSQL.setLength(0);

				System.out.println("Correo encontrado para enviar con ID -- "+rs.getString("c_filtro_alertas_historico"));
				StringBuffer strOut = new StringBuffer();
				String aux;
				// We access to stream, as this way we don't have to use the CLOB.length() which is slower...
				Blob blob = rs.getBlob("body_blob");
				BufferedReader br = new BufferedReader(new InputStreamReader(blob.getBinaryStream()));
				while ((aux=br.readLine())!=null) {
					strOut.append(aux);
				}

				MailCallable mail = new MailCallable(); 
				//autenticación de cuenta de correo para envio posterior
				if(rs.getString("remitente").equals("Reportes - AQ")) mail.setPropsReportes(); 
				else if (rs.getString("remitente").equals("SOIA - AQ")) mail.setPropsSoia();
				else mail.setPropsReportes(rs.getString("remitente")); 

				mail.setTo(rs.getString("destinatario"));
				mail.setText(strOut.toString());
				mail.setSubject(rs.getString("asunto"));
				mail.setId(rs.getString("c_filtro_alertas_historico"));
				//determina si el correo a enviar tiene formato html 
				HtmlContent = (rs.getInt("isHTML")==1)? true: false; 
				mail.setHTML(HtmlContent);
				try{
					if(!current.containsKey(mail.getId())){
						//filtro del hash, si esta en el thread y no se ha enviado, no lo agrega
						current.put(mail.getId(), mail);
						queue.add(mail);
					}	
				}catch (Exception e){System.out.println("Error al recolectar los resultados"+e.getMessage());}

			}



		}catch(Exception e){ System.out.println(e.getMessage());}

		//limpiando sbSQL
		sbSQL.setLength(0);
		System.out.println("Liberando la conexión");
		conexion.releaseConexion();
		System.out.println("---EL THREAD PRODUCTOR DE HORAS TERMINA  -----");
		System.out.println();

	}
		
	
	public KonexionUtil getConexion() {
					return conexion;
				}

	public void setConexion(KonexionUtil conexion) {
					this.conexion = conexion;
				}
}	

	/***
	 * Este método decodifica un mensaje en ASCII a texto normal para mandarlo por correo 
	 * @param decode
	 * @return
	 */
	
