package AIACommonLogger.services;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
// --- <<IS-END-IMPORTS>> ---

public final class acl

{
	// ---( internal utility methods )---

	final static acl _instance = new acl();

	static acl _newInstance() { return new acl(); }

	static acl _cast(Object o) { return (acl)o; }

	// ---( server methods )---




	public static final void setExecACL (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(setExecACL)>> ---
		// @sigtype java 3.5
		// [i] field:0:required nsname
		// [i] field:0:required aclGroup
		IDataCursor pc = pipeline.getCursor();
		String nsname = IDataUtil.getString(pc, "nsname"); 
		String group  = IDataUtil.getString(pc, "aclGroup"); 
		com.wm.app.b2b.server.ACLManager.setAclGroup(nsname, group); 
		pc.destroy();
		 
		/**
		 * @param nsname -- The full namespace of the folder or component
		 * @param aclGroup -- A valid IS user group name to grant access to  **/
		  
		
			
		// --- <<IS-END>> ---

                
	}
}

