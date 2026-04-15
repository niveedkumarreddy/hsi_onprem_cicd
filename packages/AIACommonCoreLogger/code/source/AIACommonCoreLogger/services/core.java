package AIACommonCoreLogger.services;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
// --- <<IS-END-IMPORTS>> ---

public final class core

{
	// ---( internal utility methods )---

	final static core _instance = new core();

	static core _newInstance() { return new core(); }

	static core _cast(Object o) { return (core)o; }

	// ---( server methods )---




	public static final void log (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(log)>> ---
		// @sigtype java 3.5
		// [i] field:0:required loggerName
		// [i] field:0:required logMessage
		// [i] field:0:required logLevel {"CRITICAL","ERROR","WARN","INFO","DEBUG","TRACE"}
		IDataCursor pc= pipeline.getCursor();
		String logMessage =IDataUtil.getString(pc,"logMessage");
		String loggerName =IDataUtil.getString(pc,"loggerName");		
		Logger logger = Logger.getLogger(loggerName);
		
		logger.info(logMessage);
			
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	static String propertiesFilename = "./packages/"+ "AIACommonCoreLogger" + "/config/log4j.properties";
	/*static {
	    long millis = System.currentTimeMillis();
	    System.setProperty("log4jFileName", millis+"-"+Math.round(Math.random()*1000));
	}*/
	static
	{
	       PropertyConfigurator.configure(propertiesFilename);
	}
	
		
	// --- <<IS-END-SHARED>> ---
}

