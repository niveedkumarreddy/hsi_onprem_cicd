package AIAEngageEligibility.v1;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import org.apache.commons.jexl3.*;
// --- <<IS-END-IMPORTS>> ---

public final class javaServices

{
	// ---( internal utility methods )---

	final static javaServices _instance = new javaServices();

	static javaServices _newInstance() { return new javaServices(); }

	static javaServices _cast(Object o) { return (javaServices)o; }

	// ---( server methods )---




	public static final void evaluateDependency (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(evaluateDependency)>> ---
		// @sigtype java 3.5
		// [i] field:0:required dependencyFormula
		// [o] field:0:required dependencyStatus
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		String dependencyFormula = IDataUtil.getString(pipelineCursor, "dependencyFormula");
		String dependencyStatus = "notCompleted";
		pipelineCursor.destroy();
		
		try {
		    JexlExpression expression = new JexlBuilder().create().createExpression(dependencyFormula);
		    Object result = expression.evaluate(new MapContext());
		
		    if (Boolean.TRUE.equals(result)) {
		        dependencyStatus = "completed";
		    }
		
		} catch (Exception e) {
			
			dependencyStatus = "notCompleted";
		}
		
		// pipeline
		IDataCursor pipelineCursor_1 = pipeline.getCursor();
		IDataUtil.put(pipelineCursor_1, "dependencyStatus", dependencyStatus);
		pipelineCursor_1.destroy();
			
		// --- <<IS-END>> ---

                
	}
}

