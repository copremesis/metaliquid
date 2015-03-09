package com.metaliquid;

import java.io.File;

public class AppServices {
	Process p;
	boolean debug = false;
	
	public boolean restartApplication( ) throws InterruptedException  
	{  
	    String javaBin = System.getProperty("java.home") + "/bin/java";  
	    File jarFile; 
	    File profiler;
	    Main.getServer().stop();
	    try{  
	        jarFile = new File(ClassLoader.getSystemClassLoader().getResource(".") + "rest.jar");
	        profiler = new File(ClassLoader.getSystemClassLoader().getResource(".") + "\\plumbr\\plumbr.jar");
	        System.out.println("Closing "+jarFile.getPath().replace("file:\\", ""));
	    } catch(Exception e) {  
	        return false;  
	    }  
	  
	    /* is it a jar file? */  
	    if ( !jarFile.getName().endsWith(".jar") )  
	    return false;   //no, it's a .class probably  
	  
	    String  toExec[] = new String[] { javaBin, "-jar", jarFile.getPath() };  
	    String debugLib = "-javaagent:"+profiler.getPath().replace("file:\\", "");
	    String toStart = "java ";
	    if (debug)
	    	toStart += debugLib;
	    toStart += "-jar "+jarFile.getPath().replace("file:\\", "");
	    
	    try{  
	    	System.out.println("Starting "+toStart);
	        p = Runtime.getRuntime().exec( toStart );  
	        StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "ERROR"); 
	        
	        // any output?
	        StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT");
	            
	        // kick them off
	        errorGobbler.start();
	        outputGobbler.start();
	                                
	        // any error???
	        int exitVal = p.waitFor();
	    } catch(Exception e) {  
	        e.printStackTrace();  
	        //return false;  
	    }  
	    
	    System.exit(0);  
	  
	    return true;  
	}  
	
	public boolean startApplication( String jar ) throws InterruptedException  
	{  
	    String javaBin = System.getProperty("java.home") + "/bin/java";  
	    File jarFile; 
	    File profiler;
	    //Main.getServer().stop();
	    try{  
	        jarFile = new File(ClassLoader.getSystemClassLoader().getResource(".") + jar);
	        profiler = new File(ClassLoader.getSystemClassLoader().getResource(".") + "\\plumbr\\plumbr.jar");
	        System.out.println("Closing "+jarFile.getPath().replace("file:\\", ""));
	    } catch(Exception e) {  
	        return false;  
	    }  
	  
	    /* is it a jar file? */  
	    if ( !jarFile.getName().endsWith(".jar") )  
	    return false;   //no, it's a .class probably  
	  
	    String  toExec[] = new String[] { javaBin, "-jar", jarFile.getPath() };  
	    String debugLib = "-javaagent:"+profiler.getPath().replace("file:\\", "");
	    String toStart = "java ";
	    if (debug)
	    	toStart += debugLib;
	    toStart += "-jar "+jarFile.getPath().replace("file:\\", "");
	    
	    try{  
	    	System.out.println("Starting "+toStart);
	        p = Runtime.getRuntime().exec( toStart );  
	        StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "ERROR"); 
	        
	        // any output?
	        StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT");
	            
	        // kick them off
	        errorGobbler.start();
	        outputGobbler.start();
	                                
	        // any error???
	        int exitVal = p.waitFor();
	    } catch(Exception e) {  
	        e.printStackTrace();  
	        //return false;  
	    }  
	    
	    //System.exit(0);  
	  
	    return true;  
	}  
}
