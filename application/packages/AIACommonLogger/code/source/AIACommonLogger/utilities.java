package AIACommonLogger;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.wm.lang.ns.NSName;
import com.wm.util.Config;
import com.wm.app.b2b.server.*;
import com.wm.app.b2b.client.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
// --- <<IS-END-IMPORTS>> ---

public final class utilities

{
	// ---( internal utility methods )---

	final static utilities _instance = new utilities();

	static utilities _newInstance() { return new utilities(); }

	static utilities _cast(Object o) { return (utilities)o; }

	// ---( server methods )---




	public static final void delimitValues (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(delimitValues)>> ---
		// @sigtype java 3.5
		// [i] field:0:required delimiter
		// [i] record:0:required doc
		// [o] field:0:required output
		IDataCursor pc = pipeline.getCursor();
		IData doc = IDataUtil.getIData(pc, "doc");
		String delimiter = IDataUtil.getString(pc,"delimiter");
		StringBuffer buffer = new StringBuffer();
		if (doc != null){
			//pipeline details name
			IDataCursor dc = doc.getCursor();
			while (dc.next()){
				
				String value = (String)dc.getValue();
				if(value.isEmpty()){
					value ="NA";
				}
				buffer.append(value).append(delimiter);
			}
			dc.destroy();
		}
		IDataUtil.put(pc, "output", buffer.toString());
			// pipeline
			pc.destroy();
			
		// --- <<IS-END>> ---

                
	}



	public static final void trimValues (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(trimValues)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] field:0:required type {"blank","null","datetime"}
		// [i] field:0:required value
		// [i] field:0:required length
		// [o] object:0:required outputValue
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
			String	type = IDataUtil.getString( pipelineCursor, "type" );
			String	value = IDataUtil.getString( pipelineCursor, "value" );
			String	length = IDataUtil.getString( pipelineCursor, "length" );
			
			if (length ==null) length="8000";
			
			int len=Integer.parseInt(length);
					
			 
			Object outputValue = new Object();
			
			if(value == null || value.equals("")){
				
				if(type.equals("blank")){
					outputValue = "";
				}
				if(type.equals("null")){
					outputValue = null;
				}
				if(type.equals("datetime")){
					outputValue = null;
				}
				
				
			}else{
				if(type.equals("datetime")){
					
					
					try {
																	
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
						Date outDate =  format.parse(value); 
						outputValue = outDate;
						
					} catch (Exception ex) {
						
					
							outputValue = null;
						
					}					
										
					
				} else {
					
					   if(value.length()>=len)				
						   outputValue = value.substring(0,Integer.parseInt(length));
					   else
						   outputValue = value;
				}
			}
		
		// pipeline
		
		IDataUtil.put( pipelineCursor, "outputValue", outputValue );
		pipelineCursor.destroy();
			
			
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---

  
	
	// --- <<IS-END-SHARED>> ---
}

