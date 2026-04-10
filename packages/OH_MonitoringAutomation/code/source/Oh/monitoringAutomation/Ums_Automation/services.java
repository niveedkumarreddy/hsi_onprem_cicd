package Oh.monitoringAutomation.Ums_Automation;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
// --- <<IS-END-IMPORTS>> ---

public final class services

{
	// ---( internal utility methods )---

	final static services _instance = new services();

	static services _newInstance() { return new services(); }

	static services _cast(Object o) { return (services)o; }

	// ---( server methods )---




	public static final void intToString (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(intToString)>> ---
		// @sigtype java 3.5
		// [i] object:0:required value
		// [o] field:0:required num
		// pipeline 
		IDataCursor pipelineCursor = pipeline.getCursor();
			Object	value = IDataUtil.get( pipelineCursor, "value" );
		pipelineCursor.destroy();
		
		String num=value.toString();
		// pipeline
		IDataCursor pipelineCursor_1 = pipeline.getCursor();
		IDataUtil.put( pipelineCursor_1, "num", num );
		pipelineCursor_1.destroy();
			
		// --- <<IS-END>> ---

                
	}
}

