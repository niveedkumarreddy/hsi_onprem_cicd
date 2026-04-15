package oh.ops.internal;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.wm.app.b2b.server.InvokeState;
import com.wm.app.b2b.server.UserManager;
import com.softwareag.util.IDataMap;
import java.util.StringTokenizer;
import java.util.Arrays;
import com.wm.app.b2b.server.Server;
// --- <<IS-END-IMPORTS>> ---

public final class is

{
	// ---( internal utility methods )---

	final static is _instance = new is();

	static is _newInstance() { return new is(); }

	static is _cast(Object o) { return (is)o; }

	// ---( server methods )---




	public static final void IntegerToString (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(IntegerToString)>> ---
		// @sigtype java 3.5
		// [i] object:0:required input
		// [o] field:0:required output
		IDataCursor idatacursor = pipeline.getCursor(); 
		Integer integer = (Integer)IDataUtil.get(idatacursor, "input"); 
		idatacursor.destroy(); 
		String s = null; 
		if(integer != null) 
		s = integer.toString(); 
		idatacursor = pipeline.getCursor(); 
		IDataUtil.put(idatacursor, "output", s); 
		idatacursor.destroy();
		// --- <<IS-END>> ---

                
	}



	public static final void StringToInteger (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(StringToInteger)>> ---
		// @sigtype java 3.5
		IDataCursor idatacursor = pipeline.getCursor(); 
		String str = (String)IDataUtil.get(idatacursor, "str_input"); 
		idatacursor.destroy(); 
		Integer i = null; 
		if(str != null) 
		i = Integer.parseInt(str);
		idatacursor = pipeline.getCursor(); 
		IDataUtil.put(idatacursor, "int_output", i); 
		idatacursor.destroy();
			
		// --- <<IS-END>> ---

                
	}



	public static final void setExtendedSetting (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(setExtendedSetting)>> ---
		// @sigtype java 3.5
		// [i] field:0:required name
		// [i] field:0:required value
		// [i] field:0:optional overwrite {"true","false"}
		// [i] field:0:optional show {"true","false"}
		IDataCursor cursor = pipeline.getCursor();
		String name = IDataUtil.getString(cursor, "name");
		String value = IDataUtil.getString(cursor, "value");
		String overwriteStr = IDataUtil.getString(cursor, "overwrite");
		String showStr = IDataUtil.getString(cursor, "show");
		Boolean overwrite = overwriteStr != null ? new Boolean(overwriteStr) : true;
		Boolean show = showStr != null ? new Boolean(showStr) : true;
		
		
		if (!name.substring(0, 5).equals("watt.")) {
			throw new ServiceException("Setting name must begin with \"watt.\"");
		}
		
		try {
			// Get existing value (if one exists)
			String currentValue = System.getProperty(name);
			
			if (currentValue == null || overwrite == true) {
				System.setProperty(name, value);					
		
				if (show) {
					String visibleSettingsList = System.getProperty("watt.server.extendedSettingsList");
					StringTokenizer st = new StringTokenizer(visibleSettingsList, ";");
					boolean found = false;
					while (st.hasMoreTokens()) {
						String visibleSetting = st.nextToken();
						if (visibleSetting.equals(name)) {
							found = true;
						}
					}
					if (!found) {
						visibleSettingsList = visibleSettingsList + ";" + name;
						System.setProperty("watt.server.extendedSettingsList", visibleSettingsList);
					}
				}
				Server.saveConfiguration();
				
				
			}
				
		} catch (Exception e) {
			throw new ServiceException(e);
		}
			
		// --- <<IS-END>> ---

                
	}
}

