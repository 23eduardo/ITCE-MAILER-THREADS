package pruebas;

import java.sql.SQLException;
import java.util.Date;

import main.EmailUrgenteProducer;
import main.InitialConfig;

public class MainPruebaa  {
	private static final Object lock = new Object();
	static InitialConfig sme = new InitialConfig(); 
	static EmailUrgenteProducer eup = new EmailUrgenteProducer();
	
	
	public static void main (String args []){
		 Thread thread = new Thread(runnable);
		 thread.start();
	}

		 static Runnable runnable = () -> {
	            System.out.println("Inside : " + Thread.currentThread().getName());

	            try {
					eup.pruebaMultiMail(0);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        };
	          
	        


}
