package AIACommonUtilities.VECommon.services;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.security.MessageDigest;
import com.wm.app.b2b.server.*;
import com.wm.lang.ns.NSName;
import com.wm.app.b2b.server.ns.Namespace;
import com.wm.app.b2b.server.ostore.CacheStore;
import com.wm.app.b2b.server.ostore.ServerCache;
import com.wm.app.b2b.server.ostore.ServiceCache;
import com.wm.app.b2b.server.stats.Statistics;
import com.wm.util.*;
import com.wm.app.b2b.server.InvokeState;
import com.wm.app.b2b.server.ServerAPI;
import com.wm.app.b2b.server.ISRuntimeException;
import com.wm.lang.ns.NSService;
import java.net.InetAddress;
// --- <<IS-END-IMPORTS>> ---

public final class java

{
	// ---( internal utility methods )---

	final static java _instance = new java();

	static java _newInstance() { return new java(); }

	static java _cast(Object o) { return (java)o; }

	// ---( server methods )---




	public static final void setExecACL (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(setExecACL)>> ---
		// @subtype unknown
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

	// --- <<IS-START-SHARED>> ---
	private static Random random = new Random();
	// --- <<IS-END-SHARED>> ---
}

