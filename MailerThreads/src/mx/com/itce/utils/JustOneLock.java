package mx.com.itce.utils;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;


public class JustOneLock 
{
    private String appName;
    private File file;
    private FileChannel channel;
    private FileLock lock;

    public JustOneLock(String appName)
    {
        this.appName = appName;
    }
    
    public boolean isAppActive()
    {
        try
        {
            //file = new File(System.getProperty("user.home"), appName + ".tmp");
        	file = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toString().substring(5), appName + ".tmp");
        	
            channel = new RandomAccessFile(file, "rw").getChannel();

            try 
            {
                lock = channel.tryLock();
            }
            catch (OverlappingFileLockException e)
            {
                // already locked
                closeLock();
                return true;
            }

            if (lock == null)
            {
                closeLock();
                return true;
            }

            Runtime.getRuntime().addShutdownHook(new Thread()
            {
            	// destroy the lock when the JVM is closing
            	public void run()
            	{
            		closeLock();
            		deleteFile();
            	}
            });
            
            return false;
        }
        catch (Exception e)
        {
            closeLock();
            return true;
        }
    }

    private void closeLock()
    {
        try { lock.release();  }catch (Exception e) {  }
        try { channel.close(); }catch (Exception e) {  }
    }

    private void deleteFile()
    {
        try { file.delete(); }	catch (Exception e) { }
    }
    
    
    public static void main(String[] args)
    {
    	JustOneLock ua = new JustOneLock("JustOneId");

        if (ua.isAppActive())
        {
            System.out.println("Already active.");
            System.exit(1);    
        }
        else
        {
            System.out.println("NOT already active.");
            try
            {
                while(true)
                {
                     try { System.out.print("."); Thread.sleep(5 * 60); }
                      catch(Exception e) { e.printStackTrace(); }
                }
             }
            catch (Exception e) {  }
        }
    }

}//class