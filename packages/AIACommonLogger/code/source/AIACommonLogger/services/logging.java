package AIACommonLogger.services;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import com.softwareag.util.IDataMap;
// --- <<IS-END-IMPORTS>> ---

public final class logging

{
	// ---( internal utility methods )---

	final static logging _instance = new logging();

	static logging _newInstance() { return new logging(); }

	static logging _cast(Object o) { return (logging)o; }

	// ---( server methods )---




	public static final void logData (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(logData)>> ---
		// @sigtype java 3.5
		// [i] field:0:required loggerName
		// [i] field:0:required logMessage
		// [i] field:0:optional logLevel {"fatal","error","warn","info","debug","trace"}
			
		IDataMap plMap = new IDataMap(pipeline);
		String logName = plMap.getAsString("loggerName");
		String message = plMap.getAsString("logMessage");
		String level = plMap.getAsString("logLevel");
		
		// To get the logger names
		Logger logger =  LogManager.getLogger(logName);
		
		
		switch (level){	
		
		case "info":{
			logger.info(message);
			break;
		}
		
		case "trace":{
			logger.trace(message);
			break;
		}
		
		case "debug":{
			logger.debug(message);
			break;
		}
		
		case "warn":{
			logger.warn(message);
			break;
		}
		
		case "error":{
			logger.error(message);
			break;
		}
		
		case "fatal":{
			logger.fatal(message);
			break;
		}
		default :
			logger.info(message);
			break;
		}
		
			// pipeline
		// --- <<IS-END>> ---

                
	}



	public static final void reloadConfig (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(reloadConfig)>> ---
		// @sigtype java 3.5
		if(context!=null){
			context.reconfigure();
		}
		// pipeline detail
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	static {	
	    System.setProperty("log4j2.contextSelector",
	        "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
	} 
	
	protected static LoggerContext context= Configurator.initialize("log4j2", null, "packages/AIACommonLogger/config/log4j2.xml");
		
	// --- <<IS-END-SHARED>> ---
}

