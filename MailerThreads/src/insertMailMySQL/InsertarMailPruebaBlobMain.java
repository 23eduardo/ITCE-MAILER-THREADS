package insertMailMySQL;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import mx.com.itce.utils.Mail;

/***
 * 
 * @author ITCE
 * Esta clase solo es una demostración de como hacer uso de la clase InsertMailDB para insertar ya sea  
 *
  */
public class InsertarMailPruebaBlobMain {
	
	public static void main (String args[]) throws SQLException{
		System.out.println("Hola mundo");
		//pruebainsertarMailGrande();
		//pruebainsertarMailText();
		pruebainsertarMailAttach();
		
	}

	private static void pruebainsertarMailGrande() throws SQLException {
		int prioridad=2;
		String proceso ="PA2018-4";
		Mail mail = new Mail ();   
		
		String mensaje =
				"<p><strong> </strong></p>"
				+ "<table style=\"text-align:left\" cellpadding=\"2\" cellspacing=\"2\">"
				+ "<tbody>"
				+ " <tr>"
				+ " <td class=\"m_7839309302390080854ped\"><small>Pedimento</small></td>"
				+ "<td class=\"m_7839309302390080854ped\" style=\"font-weight:bold\"><small>9034065</small></td>"
				+ " </tr>"
				+ " <tr>"
				+ " <td class=\"m_7839309302390080854ped\"><small>Patente</small></td>"
				+ "<td class=\"m_7839309302390080854ped\" style=\"font-weight:bold\"><small>3989</small></td>"
				+ " </tr>"
				+ " <tr>"
				+ "<td class=\"m_7839309302390080854ped\"><small>Aduana</small></td>"
				+ " <td class=\"m_7839309302390080854ped\" style=\"font-weight:bold\"><small>07</small></td>"
				+ " </tr><tr>"
				+ "<td class=\"m_7839309302390080854ped\"><small>RFC</small></td>"
				+ " <td class=\"m_7839309302390080854ped\" style=\"font-weight:bold\"><small>ADC990428KNA</small></td>"
				+ " </tr>"
				+ " <tr>"
				+ "<td class=\"m_7839309302390080854ped\"><small>Empresa</small></td>"
				+ " <td class=\"m_7839309302390080854ped\" style=\"font-weight:bold\"><small></small></td>"
				+ "</tr>"
				+ "</tbody> </table><br><br><br>"
				+ "<small style=\"font-weight:bold\"><span>HISTORIA DEL PEDIMENTO</span></small><br> <br>"
				+ "<table id=\"m_7839309302390080854infoPed\" style=\"text-align:left\"><tbody>"
				+ "<tr class=\"m_7839309302390080854titulo\" align=\"center\">"
				+ "<td><small><b>Estado</b></small></td>"
				+ " <td><small><b>Fecha y Hora</b></small></td>"
				+ "</tr>"
				+ "<tr class=\"m_7839309302390080854contenido\">"
				+ " <td><small>VALIDACION DE PREVIO</small></td>"
				+ "<td><small>2019-08-02&nbsp; 11:15:27</small></td></tr>"
				+ "</tbody>"
				+ "</table><br>"
				+ "<small><br>"
				+ "<span style=\"font-weight:bold\">REMESAS</span><br style=\"font-family:Verdana;font-weight:bold\">"
				+ "<br style=\"font-weight:bold\"></small>"
				+ " <table id=\"m_7839309302390080854remesas\" style=\"text-align:center;width:50%\" cellpadding=\"2\" cellspacing=\"2\">"
				+ "<tbody>"
				+ "<tr class=\"m_7839309302390080854titulo\">"
				+ "<td style=\"font-weight:bold;width:10%\"><small>Secuencia</small></td>"
				+ "<td style=\"font-weight:bold;width:10%\"><small>Factura</small></td>"
				+ "<td style=\"font-weight:bold\"><small>Estado</small></td>"
				+ "<td style=\"font-weight:bold\"><small>Valor</small></td>"
				+ " <td style=\"font-weight:bold\"><small>Unidades</small></td>"
				+ " <td><small><span style=\"font-weight:bold\">Fecha de entrada</span></small></td>"
				+ "</tr>"
				+ "<tr class=\"m_7839309302390080854contenido\">"
				+ "<td><small>1</small></td><td><small>0001</small></td>"
				+ "<td><small>DESADUANADO</small></td>"
				+ "<td><small>63919</small></td>"
				+ "<td><small>1900</small></td>"
				+ " <td><small>2019-08-06&nbsp;10:42:07</small></td>"
				+ " </tr> <tr class=\"m_7839309302390080854contenido\"><td><small>1</small></td> <td><small>0001</small></td>"
				+ "<td><small>VERDE EN PRIMERA SELECCION</small></td>"
				+ " <td><small>63919</small></td>"
				+ "<td><small>1900</small></td>"
				+ "   <td><small>2019-08-06&nbsp;10:42:07</small></td>"
				+ "</tr>"
				+ " <tr class=\"m_7839309302390080854contenido\">"
				+ "<td><small>2</small></td>"
				+ " <td><small>0002</small></td>"
				+ "<td><small>DESADUANADO</small></td>"
				+ "<td><small>66014</small></td>"
				+ "<td><small>4119</small></td>"
				+ "  <td><small>2019-08-06&nbsp;12:10:33</small></td>"
				+ "</tr>"
				+ "<tr class=\"m_7839309302390080854contenido\"> <td><small>2</small></td>"
				+ " <td><small>0002</small></td>"
				+ "<td><small>VERDE EN PRIMERA SELECCION</small></td>"
				+ " <td><small>66014</small></td>"
				+ " <td><small>4119</small></td>"
				+ "<td><small>2019-08-06&nbsp;12:10:33</small></td></tr>"
				+ "<tr class=\"m_7839309302390080854contenido\"><td><small>3</small></td>"
				+ "<td><small>0003</small></td>"
				+ "<td><small>DESADUANADO</small></td>"
				+ "<td><small>61789</small></td>"
				+ " <td><small>785</small></td>"
				+ " <td><small>2019-08-06&nbsp;14:07:54</small></td>"
				+ " </tr>    </tbody></table>";
		
		mail.setText(mensaje);
		//mail.setPropsReportes();
		mail.setPropsSoia();
		mail.setMyName("Pruebas Mailer Proceso 2");
		mail.setTo("eduardo@alvaroquintana.com");
		mail.setSubject("PRUEBA PROCESO-archivos adjuntos");
		mail.setHTML(true);
		mail.setPrueba(1);
		
		//aqui instancia lista oara insertar
		for(int i = 0 ; i<1;i++){
		InsertMailDB im = new InsertMailDB(mail,prioridad,proceso);
		//im.checkThisMailDetails();
		//im.insertarMailASCII();
		im.insertarMailBLOB();
		
		}
	}
	
	
	private static void pruebainsertarMailText() throws SQLException {
		int prioridad=2;
		String proceso ="PA2018-4";
		Mail mail = new Mail ();   
		
		String mensaje ="Este es un mensaje prueba de un correo que no es HTML";
		
		mail.setText(mensaje);
		//mail.setPropsReportes();
		mail.setPropsSoia();
		mail.setMyName("Pruebas Mailer Proceso 2");
		mail.setTo("eduardo@alvaroquintana.com");
		mail.setSubject("PRUEBA PROCESO-PEDIMENTO EN :ROJO EN PRIMERA SELECCION");
		//mail.setHTML(true);		
		//aqui instancia lista oara insertar
		for(int i = 0 ; i<10;i++){
		InsertMailDB im = new InsertMailDB(mail,prioridad,proceso);
		//im.checkThisMailDetails();
		//im.insertarMailASCII();
		im.insertarMailBLOB();
		
		}
	}
	
	/***
	 * Este método inserta un archivo con un documento adjunto 
	 * @throws SQLException
	 */
	private static void pruebainsertarMailAttach() throws SQLException {
		int prioridad=2;
		String proceso ="Adjuntos Archivos";
		Mail mail = new Mail();   
		String mensaje ="Este texto no importa solo importa ver que si lleguen los archivos adjuntos";
		mail.setText(mensaje);
		mail.setPropsSoia();
		mail.setMyName("Pruebas Adjunto");
		mail.setTo("eduardo@alvaroquintana.com");
		mail.setSubject("PRUEBA PROCESO ADJUNTOS");
		
		//para insertar el archivo blob
		//ruta archivo
		String ruta="/Users/Eduardo/Downloads/adjuntos/9999089.227";
		
		//este for solo es para referencia, solo soporta un archivo 
		for(int i = 0 ; i<1;i++)
			{
			//cargando archivo, solo se necesita nombre y convertir su contenido
			mail.setFiles("m9999089.227");
			mail.contenidoAdjunto=convertFileToString(ruta);
			InsertMailDB im = new InsertMailDB(mail,prioridad,proceso);
			im.insertarMysqlBLOBAdjuntos();
			}

	  
		
		
}
	
	public static byte[] convertFileToChar(String ruta){
		  Path path = Paths.get(ruta);
		  StringBuilder contenido = new StringBuilder();
		  byte[] bArray=null;  
		  try {
			  bArray = Files.readAllBytes(path);
	            // reading content from byte array
	            for (int i = 0; i < bArray.length; i++){
	                  System.out.print((char) bArray[i]);
	                  contenido.append((char) bArray[i]);
	            }
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } 
	        
	        return bArray;
	}
	
	public static String convertFileToString(String ruta){
		  Path path = Paths.get(ruta);
		  StringBuilder contenido = new StringBuilder();
		  byte[] bArray=null;  
		  try {
			  bArray = Files.readAllBytes(path);
	            // reading content from byte array
	            for (int i = 0; i < bArray.length; i++){
	                  System.out.print((char) bArray[i]);
	                  contenido.append((char) bArray[i]);
	            }
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } 
	        
	        return contenido.toString();
	}

	 
}	
