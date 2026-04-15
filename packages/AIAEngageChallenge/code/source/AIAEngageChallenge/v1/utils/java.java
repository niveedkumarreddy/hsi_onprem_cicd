package AIAEngageChallenge.v1.utils;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
// --- <<IS-END-IMPORTS>> ---

public final class java

{
	// ---( internal utility methods )---

	final static java _instance = new java();

	static java _newInstance() { return new java(); }

	static java _cast(Object o) { return (java)o; }

	// ---( server methods )---




	public static final void excelCeiling (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(excelCeiling)>> ---
		// @sigtype java 3.5
		// [i] field:0:required number
		// [i] field:0:required significance
		// [o] field:0:required ceilingVal
		IDataCursor pipelineCursor = pipeline.getCursor();
		
		double	number = Double.parseDouble(IDataUtil.getString( pipelineCursor, "number" ));
		double	significance = Double.parseDouble(IDataUtil.getString( pipelineCursor, "significance" ));
		
		if(significance==0)significance=1;
		
		double x = Math.round((double)number/significance)*significance;
		if(x<number)x=x+significance;
		int xint = (int)x;
		
		IDataCursor pipelineCursor1 = pipeline.getCursor();
		IDataUtil.put( pipelineCursor1, "ceilingVal", String.valueOf(xint));
		pipelineCursor.destroy();
		// --- <<IS-END>> ---

                
	}



	public static final void excelFloor (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(excelFloor)>> ---
		// @sigtype java 3.5
		// [i] field:0:required number
		// [i] field:0:required significance
		// [o] field:0:required floorVal
		IDataCursor pipelineCursor = pipeline.getCursor();
		
		double	number = Double.parseDouble(IDataUtil.getString( pipelineCursor, "number" ));
		double	significance = Double.parseDouble(IDataUtil.getString( pipelineCursor, "significance" ));
		
		if(significance==0)significance=1;
		
		double x = (double)number/significance;
		int xint = (int)x;
		int sig = (int)significance;
		
		xint = xint * sig;
		
		IDataCursor pipelineCursor1 = pipeline.getCursor();
		IDataUtil.put( pipelineCursor1, "floorVal", String.valueOf(xint));
		pipelineCursor.destroy();
		// --- <<IS-END>> ---

                
	}



	public static final void excelMRound (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(excelMRound)>> ---
		// @sigtype java 3.5
		// [i] field:0:required number
		// [i] field:0:required multiple
		// [o] field:0:required mroundVal
		IDataCursor pipelineCursor = pipeline.getCursor();
		
		double	number = Double.parseDouble(IDataUtil.getString( pipelineCursor, "number" ));
		double	multiple = Double.parseDouble(IDataUtil.getString( pipelineCursor, "multiple" ));
		
		double ceil = Math.round((double)number/multiple)*multiple;
		if (ceil<number) ceil=ceil+multiple;
		int intCeil = (int)ceil;
		
		double flr = (double)number/multiple;
		int intFlr = (int)flr;
		int sig = (int)multiple;
		intFlr = intFlr * sig;
		
		int no = (int)number;
		
		int diffNoFloor = no - intFlr;
		int diffNoCeiling = intCeil - no;
		
		
		if (diffNoFloor>diffNoCeiling)
		{
		IDataCursor pipelineCursor1 = pipeline.getCursor();
		IDataUtil.put( pipelineCursor1, "mroundVal", String.valueOf(intCeil));
		pipelineCursor.destroy();
		}
		
		if (diffNoFloor<diffNoCeiling)
		{
		IDataCursor pipelineCursor1 = pipeline.getCursor();
		IDataUtil.put( pipelineCursor1, "mroundVal", String.valueOf(intFlr));
		pipelineCursor.destroy();
		}
		
		if (diffNoFloor==diffNoCeiling)
		{
		IDataCursor pipelineCursor1 = pipeline.getCursor();
		IDataUtil.put( pipelineCursor1, "mroundVal", String.valueOf(intCeil));
		pipelineCursor.destroy();
		}
			
		// --- <<IS-END>> ---

                
	}



	public static final void getFirstDay (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getFirstDay)>> ---
		// @sigtype java 3.5
		// [o] field:0:required startYear
		// [o] field:0:required startMonth
		// [o] field:0:required startWeek
		// [o] field:0:required startQuarter
		LocalDate today = LocalDate.now(); 
		LocalDate firstDayOfMonth = today.withDayOfMonth(1);
		
		LocalDate firstDayOfYear = today.with(TemporalAdjusters.firstDayOfYear());
		LocalDate firstDayOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		
		LocalDate firstDayOfQuarter = today.with(IsoFields.DAY_OF_QUARTER, 1L);
		
		// pipeline
		
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		IDataUtil.put( pipelineCursor, "startYear", firstDayOfYear.toString() );
		IDataUtil.put( pipelineCursor, "startMonth", firstDayOfMonth.toString() );
		IDataUtil.put( pipelineCursor, "startWeek", firstDayOfWeek.toString() );
		IDataUtil.put( pipelineCursor, "startQuarter", firstDayOfQuarter.toString() );
		
		pipelineCursor.destroy();
			
		// --- <<IS-END>> ---

                
	}



	public static final void getFirstDayWithInputDate (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getFirstDayWithInputDate)>> ---
		// @sigtype java 3.5
		// [i] field:0:required inputDate
		// [o] field:0:required startYear
		// [o] field:0:required startMonth
		// [o] field:0:required startWeek
		// [o] field:0:required startQuarter
		IDataCursor pipelineCursor = pipeline.getCursor();
		LocalDate inputDate = LocalDate.parse(IDataUtil.getString( pipelineCursor, "inputDate" ));
		LocalDate firstDayOfMonth = inputDate.withDayOfMonth(1);
		
		LocalDate firstDayOfYear = inputDate.with(TemporalAdjusters.firstDayOfYear());
		LocalDate firstDayOfWeek = inputDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		
		LocalDate firstDayOfQuarter = inputDate.with(IsoFields.DAY_OF_QUARTER, 1L);
		
		// pipeline
		IDataUtil.put( pipelineCursor, "startYear", firstDayOfYear.toString() );
		IDataUtil.put( pipelineCursor, "startMonth", firstDayOfMonth.toString() );
		IDataUtil.put( pipelineCursor, "startWeek", firstDayOfWeek.toString() );
		IDataUtil.put( pipelineCursor, "startQuarter", firstDayOfQuarter.toString() );
		
		pipelineCursor.destroy();
			
		// --- <<IS-END>> ---

                
	}



	public static final void ping (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(ping)>> ---
		// @sigtype java 3.5
		// [i] field:0:required ip
		// [o] field:0:required result
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
			String	ip = IDataUtil.getString( pipelineCursor, "ip" );
		pipelineCursor.destroy();
		
		// pipeline
		IDataCursor pipelineCursor_1 = pipeline.getCursor();
		
		
		InetAddress inet;
		try {
			inet = InetAddress.getByName(ip);
			if (inet.isReachable(5000)) 
				IDataUtil.put( pipelineCursor_1, "result", "success" ); 
			else
				IDataUtil.put( pipelineCursor_1, "result", "failed" );
		} catch (IOException e) {
			IDataUtil.put( pipelineCursor_1, "result", "error"+e.getCause() );
		} 
		
		
		
		pipelineCursor_1.destroy();
		// --- <<IS-END>> ---

                
	}



	public static final void roundUp (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(roundUp)>> ---
		// @sigtype java 3.5
		// [i] field:0:required input
		// [i] field:0:required roundTo
		// [o] field:0:required output
		IDataCursor pipelineCursor = pipeline.getCursor();
		
		double	input = Double.parseDouble(IDataUtil.getString( pipelineCursor, "input" ));
		double	roundTo = Double.parseDouble(IDataUtil.getString( pipelineCursor, "roundTo" ));
		
		
		double x = Math.round((double)input/roundTo)*roundTo;
		if(x<input)x=x+roundTo;
		int xint = (int)x;
		
		// pipeline
		IDataCursor pipelineCursor1 = pipeline.getCursor();
		IDataUtil.put( pipelineCursor1, "output", String.valueOf(xint));
		pipelineCursor.destroy();
			
		// --- <<IS-END>> ---

                
	}
}

