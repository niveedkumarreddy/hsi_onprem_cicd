package AIACommonUtilities;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.wm.app.b2b.server.InvokeState;
import com.wm.app.b2b.server.ServerAPI;
import com.wm.app.b2b.server.ServiceThread;
// --- <<IS-END-IMPORTS>> ---

public final class services

{
	// ---( internal utility methods )---

	final static services _instance = new services();

	static services _newInstance() { return new services(); }

	static services _cast(Object o) { return (services)o; }

	// ---( server methods )---




	public static final void appendQueryParamsToURL (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(appendQueryParamsToURL)>> ---
		// @sigtype java 3.5
		// [i] record:1:required params
		// [i] - field:0:required key
		// [i] - field:0:required value
		// [i] field:0:required url
		// [o] field:0:required url
		IDataCursor i_cur = pipeline.getCursor();
		IData [] paramsArray = IDataUtil.getIDataArray(i_cur, "params");
		String url = IDataUtil.getString(i_cur, "url");
		if(url.equals("") || url == null) {
			return;
		}
		else {
			url = url.concat("?");
		}
		int count = 0;
		for(IData param:paramsArray) {
			IDataCursor paramCur = param.getCursor();
			String key = IDataUtil.getString(paramCur, "key");
			if(key.equals("") || key == null) {
				return;
			}
			String value = IDataUtil.getString(paramCur, "value");
			url = url.concat(key+"="+value);
			if(count == paramsArray.length-1){
				paramCur.destroy();
			}
			else {
				url = url.concat("&");
				count= count+1;
			}
		}
		IDataUtil.put(i_cur, "url", url);
		i_cur.destroy();
		// --- <<IS-END>> ---

                
	}



	public static final void captureMissingFields (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(captureMissingFields)>> ---
		// @sigtype java 3.5
		// [i] record:0:required fromDoc
		// [o] field:0:required missingFields
		IDataCursor pipelineCursor = pipeline.getCursor();
		
		IData fromDoc = IDataUtil.getIData( pipelineCursor, "fromDoc" );
		
		if ( fromDoc == null)
			
		{
		    	return;
		}
		
		pipelineCursor.destroy();
		IDataCursor fromCursor = fromDoc.getCursor();
		String missingFields="", flag="false";
		
		while(fromCursor.next())
		{  			
			String key = fromCursor.getKey();			
		    Object obj = fromCursor.getValue();
			
		    if (obj != null)
			{
				String value = fromCursor.getValue().toString().trim();
				if(value.equals("") || value==null )
				{	
					if(flag.equals("false"))
					{
						missingFields = missingFields+key;	
						flag="true";
					}
					else
					{	
						missingFields = missingFields +", "+key;
						
					}
				}
				
			}
			else
			{
				
				if(flag.equals("false"))
				{
					missingFields = missingFields+key;	
					flag="true";
				}
				else
				{	
					missingFields = missingFields +", "+key;
					
				}
			}
		}
		
		fromCursor.destroy();
		
		IDataCursor pipelineCursor_1 = pipeline.getCursor();
		IDataUtil.put( pipelineCursor_1, "missingFields", missingFields );
		pipelineCursor_1.destroy();
		
			
		// --- <<IS-END>> ---

                
	}



	public static final void extractDocValues (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(extractDocValues)>> ---
		// @sigtype java 3.5
		// [i] field:0:required delimiter
		// [i] record:0:required doc
		// [o] field:0:required output
		IDataCursor pc = pipeline.getCursor();
		IData doc = IDataUtil.getIData(pc, "doc");
		String delimiter = IDataUtil.getString(pc,"delimiter");
		StringBuffer buffer = new StringBuffer();
		if (doc != null){
			
			IDataCursor dc = doc.getCursor();
			while (dc.next()){
				
				String value = (String)dc.getValue();
				buffer.append(value).append(delimiter);
			}
			dc.destroy();
		}
		IDataUtil.put(pc, "output", buffer.toString());
			
			pc.destroy();
		// --- <<IS-END>> ---

                
	}



	public static final void flattenDocFields (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(flattenDocFields)>> ---
		// @sigtype java 3.5
		// [i] record:0:required doc
		// [o] field:0:required output
		IDataCursor pc = pipeline.getCursor();
		IData doc = IDataUtil.getIData(pc, "doc");
		StringBuffer buffer = new StringBuffer();
		if (doc != null){
			
			IDataCursor dc = doc.getCursor();
			while (dc.next())
			{
				String key    = dc.getKey();
				String value = (String)dc.getValue();
				buffer.append(key).append(":").append(value).append(" ");
			}
			dc.destroy();
		}
		IDataUtil.put(pc, "output", buffer.toString());
			
			pc.destroy();
			
		// --- <<IS-END>> ---

                
	}



	public static final void formUrlWithKeyValuePair (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(formUrlWithKeyValuePair)>> ---
		// @sigtype java 3.5
		// [i] record:0:optional fromDoc
		// [i] field:0:optional url
		// [o] field:0:required finalUrl
		IDataCursor pipelineCursor = pipeline.getCursor();
		
		IData fromDoc = IDataUtil.getIData( pipelineCursor, "fromDoc" );
		String url = IDataUtil.getString(pipelineCursor, "url");
		
		if ( fromDoc == null || url.equals("") || url==null)
		
		{
		    	return;
		}
		else 
		{
				url = url.concat("?");
		}
				pipelineCursor.destroy();
				IDataCursor fromCursor = fromDoc.getCursor();
				String flag="false";	
		while(fromCursor.next())
		{ 
			Object obj = fromCursor.getValue();
			if (obj != null)
			{
			    String key = fromCursor.getKey();
					
				String value = fromCursor.getValue().toString().trim();
		
				if(!value.equals("") && flag.equals("false"))	
				{
					url = url.concat(key+"="+value);
					flag="true";
				}
				else
				
				{
					if(!value.equals(""))
					{
						
					url = url.concat("&"+key+"="+value);
				
					}
				
				}
			  }
		}
			
				fromCursor.destroy();
					
				IDataCursor pipelineCursor_1 = pipeline.getCursor();
				IDataUtil.put( pipelineCursor_1, "finalUrl", url );
				pipelineCursor_1.destroy();
				
		// --- <<IS-END>> ---

                
	}



	public static final void getServerHostAndPort (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getServerHostAndPort)>> ---
		// @sigtype java 3.5
		// [o] field:0:required host
		// [o] field:0:required port
		// [o] field:0:required ipAddr
		IDataCursor i_cur = pipeline.getCursor();
		try{
			IDataUtil.put(i_cur, "host", ServerAPI.getServerName());
			IDataUtil.put(i_cur, "port", Integer.toString(ServerAPI.getCurrentPort()));
			IDataUtil.put(i_cur, "ipAddr", InetAddress.getLocalHost().getHostAddress().toString());
			i_cur.destroy();
		}
		catch (Exception e) {
			i_cur.destroy();
			throw new ServiceException(e);
		}
		// --- <<IS-END>> ---

                
	}



	public static final void getServicePipeline (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getServicePipeline)>> ---
		// @sigtype java 3.5
		// [o] record:0:required svcPipeline
		IDataCursor i_cur = pipeline.getCursor();
		try{
			IDataUtil.put(i_cur, "svcPipeline", IDataUtil.deepClone(pipeline));
		}
		catch (Exception e) {
			IData svcPipeline = IDataFactory.create();
			IDataCursor svc_cur = svcPipeline.getCursor();
			IDataUtil.put(svc_cur, "failure", "Failed to capture pipeline "+e.getMessage());
			IDataUtil.put(i_cur, "svcPipeline", svcPipeline);
			svc_cur.destroy();
		}
		finally{
			i_cur.destroy();
		}
		// --- <<IS-END>> ---

                
	}



	public static final void getSessionId (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getSessionId)>> ---
		// @sigtype java 3.5
		// [o] field:0:required sessionId
		IDataCursor i_cur = pipeline.getCursor();
		String sessionId = InvokeState.getCurrentSession().getSessionID();
		IDataUtil.put(i_cur, "sessionId", sessionId);
		i_cur.destroy();
		// --- <<IS-END>> ---

                
	}



	public static final void getSubString (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getSubString)>> ---
		// @sigtype java 3.5
		// [i] field:0:required inString
		// [i] field:0:required beginString
		// [i] field:0:required endString
		// [o] field:1:required value
		IDataCursor pipelineCursor = pipeline.getCursor();
		String	inString = IDataUtil.getString( pipelineCursor, "inString" );
		String	beginString = IDataUtil.getString( pipelineCursor, "beginString" );
		String	endString = IDataUtil.getString( pipelineCursor, "endString" );
		ArrayList<String> arrlist = new ArrayList<String>();
		pipelineCursor.destroy();
		
		if(inString!=null){
		
			Pattern pattern = Pattern.compile(beginString+"(.*?)"+endString, Pattern.DOTALL);
			Matcher matcher = pattern.matcher(inString);
			
			while (matcher.find()) {
				arrlist.add(matcher.group(1));
			}
		
			String value[] = new String[arrlist.size()];
			Object[] objArr = arrlist.toArray(); 
			  
		    // Iterating and converting to String 
		    int i = 0; 
		    for (Object obj : objArr) { 
		    	value[i++] = (String)obj; 
		    } 
		    
		    // pipeline
			IDataCursor pipelineCursor_1 = pipeline.getCursor();
			IDataUtil.put( pipelineCursor_1, "value", value);
			pipelineCursor_1.destroy();
			
		}
		// --- <<IS-END>> ---

                
	}



	public static final void lookupString (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(lookupString)>> ---
		// @sigtype java 3.5
		// [i] field:0:required inputString
		// [i] field:0:required lookupStrings
		// [o] field:0:required isAvailable
		IDataCursor pipelineCursor = pipeline.getCursor();
		String	inputString = IDataUtil.getString( pipelineCursor, "inputString" );
		String	lookupStrings = IDataUtil.getString( pipelineCursor, "lookupStrings" );
		String isAvailable="false";
		pipelineCursor.destroy();
		
		try{
			if(!inputString.trim().isEmpty()&&inputString!=null&&!lookupStrings.trim().isEmpty()&&lookupStrings!=null){
				inputString = inputString.toLowerCase();
				lookupStrings = lookupStrings.toLowerCase();
				String lookupStringsList[] = lookupStrings.split(",");
				for(String value: lookupStringsList){  
					int index=inputString.indexOf(value);
					if (index!=-1){
						isAvailable="true";
						break;
					}
				}  
			}
		}
		catch(Exception e){
		
		}
		
		IDataCursor pipelineCursor_1 = pipeline.getCursor();
		IDataUtil.put( pipelineCursor_1, "isAvailable", isAvailable );
		pipelineCursor_1.destroy();	
			
		// --- <<IS-END>> ---

                
	}



	public static final void minInt (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(minInt)>> ---
		// @sigtype java 3.5
		// [i] field:1:required numList
		// [o] field:0:required minValue
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
			String[] numList = IDataUtil.getStringArray( pipelineCursor, "numList" );
			if(numList==null || numList.length==0){
				pipelineCursor.destroy();
				return;
			}
			int[] intList = Arrays.asList(numList).stream().mapToInt(Integer::parseInt).toArray();
			Arrays.sort(intList);
		pipelineCursor.destroy();
		
		// pipeline
		IDataCursor pipelineCursor_1 = pipeline.getCursor();
		IDataUtil.put( pipelineCursor_1, "minValue", String.valueOf(intList[0]) );
		pipelineCursor_1.destroy();
		
			
		// --- <<IS-END>> ---

                
	}


    public static final Values multiConcat (Values in)
    {
        Values out = in;
		// --- <<IS-START(multiConcat)>> ---
		// @sigtype java 3.0
		// [i] field:0:required inStr1
		// [i] field:0:required inStr2
		// [i] field:0:required inStr3
		// [i] field:0:required inStr4
		// [i] field:0:required inStr5
		// [i] field:0:required inStr6
		// [i] field:0:required inStr7
		// [i] field:0:required inStr8
		// [i] field:0:required inStr9
		// [i] field:0:required inStr10
		// [o] field:0:required outStr
		/** Service takes in up to ten strings, checks them for null (see Shared tab method checkNull), and
		  * concatenates all of them together. checkNull returns a "" if the String is null, effectively
		  * cancelling out its effect on the concatenation. Returns the concatenated String as "outStr".
		  *
		  * @author Tom Tan, Professional Services, webMethods, Inc.
		  * @version 1.0
		  */
		
		String str1 = checkNull(in.getString("inStr1"));
		String str2 = checkNull(in.getString("inStr2"));
		String str3 = checkNull(in.getString("inStr3"));
		String str4 = checkNull(in.getString("inStr4"));
		String str5 = checkNull(in.getString("inStr5"));
		String str6 = checkNull(in.getString("inStr6"));
		String str7 = checkNull(in.getString("inStr7"));
		String str8 = checkNull(in.getString("inStr8"));
		String str9 = checkNull(in.getString("inStr9"));
		String str10 = checkNull(in.getString("inStr10"));
		
		String outStr = str1 + str2 + str3 + str4 + str5 + str6 + str7 + str8 + str9 + str10;
		
		out.put( "outStr", outStr );
			
		// --- <<IS-END>> ---
        return out;
                
	}



	public static final void threadedInvoke (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(threadedInvoke)>> ---
		// @sigtype java 3.5
		// [i] field:0:required svcName
		// [i] field:0:required ifcName
		// [i] record:0:required pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		String	svcName = IDataUtil.getString( pipelineCursor, "svcName" );
		String	ifcName = IDataUtil.getString( pipelineCursor, "ifcName" );
		if(!pipelineCursor.first("ifcName") || !pipelineCursor.first("svcName") ){
			
			throw new ServiceException("package or service name cannot be null"); 
		}
		
		try{
			
			
		     // Invoke service
			 
			 IData svcInput = IDataUtil.getIData(pipelineCursor, "pipeline");
		     Service.doThreadInvoke(ifcName, svcName, svcInput);
		
		    
		
		   }catch (Exception e){
		
		     throw new ServiceException(e.getMessage());
		
		   }finally{
		
			   pipelineCursor.destroy();
		
		   }
			
		// --- <<IS-END>> ---

                
	}



	public static final void throwException (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(throwException)>> ---
		// @sigtype java 3.5
		// [i] field:0:required exceptionCode
		// [i] field:0:optional additionalDetails
		IDataCursor i_cur = pipeline.getCursor();
		String exceptionCode = IDataUtil.getString(i_cur, "exceptionCode");
		String additionalDetails = IDataUtil.getString(i_cur, "additionalDetails");
		
		if(exceptionCode.equals("")) { 
			return;
		}
		ServiceException svcException = new ServiceException(exceptionCode);
		
		IData errorDetails = IDataFactory.create();
		IDataCursor e_cur = errorDetails.getCursor();
		if( additionalDetails != null && !additionalDetails.equals("")) {
			IDataUtil.put(e_cur, "additionalDetails", additionalDetails);
		}
		IDataUtil.put(e_cur, "errorCode", exceptionCode);
		svcException.setErrorDetails(errorDetails);
		
		throw svcException;
			
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	private static String checkNull(String inputString) 
	{
	  if (inputString == null)
	    return "";
	  else
	    return inputString;
	}
	// --- <<IS-END-SHARED>> ---
}

