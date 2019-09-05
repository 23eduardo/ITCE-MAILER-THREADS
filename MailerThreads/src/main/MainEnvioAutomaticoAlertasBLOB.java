package main;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;

import mx.com.itce.utils.JustOneLock;
 
/***
 * clase que se ocupa para invocar a los threads y poolExecutor que controlan la búsqueda y envio automatico de alertas
 * En esta versión encuentra los correos urgentes (frecuencia 0 ) y los correos por hora(frecuencia 1) cada que sea el ultimo minuto de dicha hora ejemplo (13:59) -- busca de 13:00 a 13:59 
 * @author ITCE
 *
 */
public class MainEnvioAutomaticoAlertasBLOB
{
	//transport y session
	static Transport transport; 
	static Session session;
	
	static InitialConfig smeSOIA;
	static InitialConfig smeREPORTES;
	
	//variables globales, para que solo se cree una insancia y no localmente cada que se ejecuta el while
	//Threads de timming
	static ScheduledExecutorService scheduler2 = Executors .newScheduledThreadPool(1); 
	static ScheduledExecutorService scheduler = Executors .newScheduledThreadPool(2); 
	//Threads para proceso de pendientes 
	static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
	//sysnchronized queue para productor-consumidor correos
	static BlockingQueue<MailCallable> queue = new LinkedBlockingQueue<>();
	//Hashmap para bloquear que no se repitan en el queue, un solo hashmap que haga el blocking de las listas
	public static final HashMap <String,MailCallable> current = new HashMap<>();
	static Date fecha;
	static String fechaMysql;
	static String horabase,horaInicio,horaFinal;
	static String horaTrigger,fechaMySQL;
	static SimpleDateFormat sdf;
	static SimpleDateFormat sdfminutos;
	static SimpleDateFormat sdfhoras;
	static int counter=0; 
	static long counter2=0;
	static int counterInit=0;
	public static int mailsEnviadosSOIA =0;
	public static int mailsEnviadosREPORTES=0;
	private static final String system_class = "MainEnvioAutomaticoAlertasBLOB";
	static List <Runnable> lista;
	
      public static void main(String[] args) throws Exception
      {

    	  //inicializando sessión y transport
    	  InitPropsSOIAyREPORTES();
    	  SimpleDateFormat fechalog = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  		
  		JustOneLock ua = new JustOneLock("MainEnvioAutomaticoAlertasBLOB");
  		if (ua.isAppActive())
          {
              System.out.println(system_class+": "+fechalog.format(Calendar.getInstance().getTime())+" Process is Already active ¬¬'");
              System.exit(1);    
          }
          else
          {
          	System.out.println(system_class+": "+fechalog.format(Calendar.getInstance().getTime()) + " Iniciando el proceso ... ");
          	
    	  EmailUrgenteProducer  producerm =new EmailUrgenteProducer(queue,current);
    	  
    	  //timing tarea 1, "¿Hay nuevos correos en la base?"
    	  scheduler.scheduleWithFixedDelay(new Runnable() {
    		  public void run() {
    			  try {
    				  //producerm.checkCorreosAtrasadosBLOB();
    				  producerm.pruebaMultiMail(counter2);
    				  counter2=(counter2>10)?counter2:counter2++;
    				  fecha = new Date();
    				  sdf = new SimpleDateFormat("YYYY-MM-dd");
    				  sdfminutos=new SimpleDateFormat("mm");
    				  sdfhoras=new SimpleDateFormat("hh");
    				  fechaMySQL = sdf.format(fecha);
    				  String minutos =sdfminutos.format(fecha);
    				  String horas =sdfhoras.format(fecha);

    				  if(minutos.equals("59")&& counter == 0){

    					  //si alguna hora termina en 59 ejemplo 13:59, verificará los correos de 13:00 a 13:59 una sola vez
    					  horaInicio=horas+":00";
    					  horaFinal = horas+":59";
    					  System.out.println("Los datos a mandar en mysql son "+fechaMySQL+ " "+horas+":00"+ "hora final "+horas+":59" + "y los minutos son "+ minutos) ;
    					  //llamar al thread a ver si hay mensajes pendientes en esa fecha y a esas horas
    					  producerm.checkCorreosAtrasadosPorHora(fechaMySQL,horaInicio,horaFinal);
    					  counter++;

    				  }

    				  if(minutos.equals("00")){
    					  //al siguiente minuto desbloquear el counter para la sigiente hora
    					  counter=0;
    				  }

    			  } catch (Exception e) {
    				  System.err.println("Ocurrió una excepción en el Thread productor "+e.getMessage());
    			  }
    			  
    			  try {
    				  //finaliza el thread cuando ya acabo (esto es paa monitorearlo)
					this.finalize();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		  }
    	  }, 0, 25, TimeUnit.SECONDS);
    	  
    	 lista = scheduler.shutdownNow();
    	  //observer 
    	  Thread t = new Thread(runnable);
    	  t.start();
    	  
    	  
      //timing tarea 2, enviar del queue y actualizar status mysql 
      scheduler2.scheduleWithFixedDelay(new Runnable() {
		    public void run() {
		        try {
		        	MailCallable mc = new MailCallable();
		        	 String message =queue.isEmpty()? "No hay mensajes pendientes para enviar": "Enviando "+queue.size() +" correos pendientes";
		        	 System.out.println("--THREAD DE ENVIO COMIENZA ---"+message);
		        	 while (!queue.isEmpty())
		        	 {
		        		//en proceso de thread para que otro thread lo sepa
		        		mc=queue.peek();
		        		mc.setEnProcesoDeActualizacion(1);
		        		current.replace(mc.getId(), mc);
		        		executor.execute(queue.poll());
		        		  System.out.println("tamaño del queue "+queue.size());
		            } 
		        } catch (Exception e) {
		        	System.err.println("Ocurrió una excepción en el Thread de envio de correos "+e.getMessage());
		        }
		    }
		}, 8, 25, TimeUnit.SECONDS);
    
    
      }
  		
  		//executor.awaitTermination(60, TimeUnit.SECONDS);
      }	
      
      
     
      /***
       * Una vez que el mail ya se proceso, no hay necesidad de tenerlo en el HashMap, se desbloquea para que el HashMap no crezca infinitamente
       * @param id
       */
      public void quitarLockHash(String id){
    	  System.out.println("El hash tenia" +current.size());
    	  try{
    		 this.current.remove(id); 
    	  }catch(Exception e){ System.out.println("Error al quitar el correo con id "+id+ " ya no se procesará adelante");}
    	  System.out.println("El hash ahora tiene" +current.size());
      }
      
      
      public static void SessionChecker(int counter,int correosenviadosSOIA,int correosenviadosREPORTES){
    	  System.out.println("Este es el metodo de session checker ++correos SOIA"+correosenviadosSOIA+"++CORREOS REPORTES++"+correosenviadosREPORTES);
    		//abriendo sesiones y transport 
    	if(correosenviadosSOIA>3){
    		System.out.println("Límite alcanzado de correos, refrescando la sesión");
    		smeSOIA.refreshSession("SOIA - AQ");
    		
    	}
    	if(correosenviadosREPORTES>3){
    		System.out.println("Límite alcanzado de correos, refrescando la sesión");
    		smeSOIA.refreshSession("REPORTES - AQ");
    		
    	}
    		
    	
    
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
      }
      
      public static void SessionCheckerSimple(int correosenviados, String remitente){
    		//abriendo sesiones y transport 
    	if(correosenviados>=2 && remitente.equals("SOIA - AQ")){
//    		System.out.println("Límite alcanzado de correos, refrescando la sesión, enviados por "+remitente+ " en el mismo transport");
//    		smeSOIA.refreshSession("SOIA - AQ");
    		
    	}else
    	if(correosenviados>=2){
    		System.out.println("Límite alcanzado de correos, refrescando la sesión");
    		smeSOIA.refreshSession("REPORTES - AQ");
    		
    	}
   
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
      
      public static  void InitPropsSOIAyREPORTES(){
    	  smeSOIA = new InitialConfig(); 
			smeSOIA.InitialConfigAndProps("SOIA - AQ");
			smeREPORTES = new InitialConfig(); 
			smeREPORTES.InitialConfigAndProps("reportes");	
      }
      
      public static  void InitPropsSOIA(){
    	  smeSOIA = new InitialConfig(); 
			smeSOIA.InitialConfigAndProps("SOIA - AQ");
		
      }
      
      public static  void InitPropsREPORTES(){
			smeREPORTES = new InitialConfig(); 
			smeREPORTES.InitialConfigAndProps("reportes");	
      }
      
      public static  void closeTransportSOIA(){
			try {
				System.out.println("límite de correos con el mismo transport alcanzado, cerrando transport ");
				smeSOIA.getTransport().close();
			} catch (MessagingException e) {
				System.out.println("El transport ya fue cerrado anteriormente por un correo que alcanzó el límite");
				e.printStackTrace();
			}
    }
      
      public static  void closeTransportREPORTES(){
			try {
				System.out.println("límite de correos con el mismo transport alcanzado, cerrando transport ");
				smeREPORTES.getTransport().close();
			} catch (MessagingException e) {
				System.out.println("El transport ya fue cerrado anteriormente por un correo que alcanzó el límite");
				e.printStackTrace();
			}
  }
      static Runnable runnable = () -> { 
    	 for(Runnable r : lista){
    		System.out.println( r.toString());
    	 }
		  System.out.println("el thread productor sigue"+executor.isTerminated() + lista);
		};
	
     
}