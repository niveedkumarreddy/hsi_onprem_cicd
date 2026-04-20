package AIACommonLogger.services;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.InetAddress;
import java.net.UnknownHostException;
import com.wm.util.Debug;
import java.io.*;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.lang.System;
import com.wm.app.b2b.server.*;
import com.wm.util.Table;
import java.text.*;
import com.wm.lang.ns.*;
import com.wm.app.b2b.util.GenUtil;
// --- <<IS-END-IMPORTS>> ---

public final class processThread

{
	// ---( internal utility methods )---

	final static processThread _instance = new processThread();

	static processThread _newInstance() { return new processThread(); }

	static processThread _cast(Object o) { return (processThread)o; }

	// ---( server methods )---




	public static final void getThreadPoolStatus (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getThreadPoolStatus)>> ---
		// @sigtype java 3.5
		// [o] field:0:required poolSize
		// [o] field:0:required maximumPoolSize
		// [o] field:0:required corePoolSize
		// [o] field:0:required activeCount
		// [o] field:0:required completedTaskCount
		// [o] field:0:required taskCount
		// [o] field:0:required isShutdown
		// [o] field:0:required isTerminated
		int intPoolSize=exePool.getPoolSize();
		int intMaximumPoolSize=  exePool.getMaximumPoolSize();
		int intCorePoolSize =exePool.getCorePoolSize();
		int intActiveCount =exePool.getActiveCount();
		long longCompletedTaskCount =exePool.getCompletedTaskCount();
		long longTaskCount =exePool.getTaskCount();
		boolean isShutdown =exePool.isShutdown();
		boolean isTerminated =exePool.isTerminated();
		
		
		
		
		// pipeline
		
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		IDataUtil.put( pipelineCursor, "poolSize", String.valueOf(intPoolSize) );
		IDataUtil.put( pipelineCursor, "maximumPoolSize", String.valueOf(intMaximumPoolSize) );
		IDataUtil.put( pipelineCursor, "corePoolSize", String.valueOf(intCorePoolSize) );
		IDataUtil.put( pipelineCursor, "activeCount", String.valueOf(intActiveCount) );
		IDataUtil.put( pipelineCursor, "completedTaskCount", String.valueOf(longCompletedTaskCount) );
		IDataUtil.put( pipelineCursor, "taskCount", String.valueOf(longTaskCount) );
		IDataUtil.put( pipelineCursor, "isShutdown", String.valueOf(isShutdown) );
		IDataUtil.put( pipelineCursor, "isTerminated", String.valueOf(isTerminated) );
		pipelineCursor.destroy();
		
			
		// --- <<IS-END>> ---

                
	}



	public static final void invokeLogThread (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(invokeLogThread)>> ---
		// @sigtype java 3.5
		// [i] record:0:required Input
		// [i] field:0:required serviceNamespace
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
			// Input
			IData	Input = IDataUtil.getIData( pipelineCursor, "Input" );
			if ( Input == null)
				throw new IllegalArgumentException("Input is null");
		
			String	serviceNamespace = IDataUtil.getString( pipelineCursor, "serviceNamespace" );
			
		pipelineCursor.destroy();
			
		   
		 
		NSName NSServiceName = NSName.create(serviceNamespace);
		
			if (exePool ==null) 
				exePool = new ThreadPoolExecutor(10, 100,
				        60, TimeUnit.SECONDS, new LinkedBlockingDeque<>(200) );
		
		GenCallable callable = new GenCallable(Input, NSServiceName);
		exePool.submit(callable);
		
		//GenRunnable runnable = new GenRunnable(Input, NSServiceName, Service.getSession());
		//	logExecutorService.execute(runnable);
		//logExecutorService.submit(runnable);
			
		// --- <<IS-END>> ---

                
	}



	public static final void invokeThread (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(invokeThread)>> ---
		// @sigtype java 3.5
		// [i] record:0:required Input
		// [i] field:0:required serviceNamespace
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
			// Input
			IData	Input = IDataUtil.getIData( pipelineCursor, "Input" );
			if ( Input == null)
				throw new IllegalArgumentException("Input is null");
		
			String	serviceNamespace = IDataUtil.getString( pipelineCursor, "serviceNamespace" );
			
		pipelineCursor.destroy();
			
		//ExecutorService executor = Executors.newSingleThreadExecutor();
		
		NSName NSServiceName = NSName.create(serviceNamespace);
		
		GenCallable callable = new GenCallable(Input, NSServiceName);
		
		ThreadPoolExecutor exePool = new ThreadPoolExecutor(1, 1,
		        1000, TimeUnit.MICROSECONDS, new LinkedBlockingDeque<>(2),
		        new ThreadPoolExecutor.DiscardPolicy());
		  
			if (logExecutorService ==null) 
				logExecutorService = Executors.newFixedThreadPool(100); 
		
			exePool.submit(callable);
		
		
		//executor.shutdownNow();
		// --- <<IS-END>> ---

                
	}



	public static final void invokeThreads (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(invokeThreads)>> ---
		// @sigtype java 3.5
		// [i] record:1:required Input
		// [i] field:1:required serviceNamespace
		// [i] field:0:required timeoutInMilliSeconds
		// [o] record:1:required Output
		IDataCursor pipelineCursor = pipeline.getCursor();
		
		IData[] Input = IDataUtil.getIDataArray(pipelineCursor, "Input");
		String[] serviceNamespace = IDataUtil.getStringArray(pipelineCursor, "serviceNamespace");
		Long timeoutInMilliSeconds = Long.parseLong(IDataUtil.getString(pipelineCursor, "timeoutInMilliSeconds"));
		
		List<GenCallable> futureList = new ArrayList<GenCallable>();
		
		if (logExecutorService ==null)
			logExecutorService = Executors.newFixedThreadPool(100); 
		
		for (int i=0; i<Input.length; i++)
		{
			NSName NServiceName = NSName.create(serviceNamespace[i]);
			GenCallable callable = new GenCallable(Input[i], NServiceName);
			futureList.add(callable);
		}
		 
		try
		{ 
			List<Future<IData>> futures = logExecutorService.invokeAll(futureList, timeoutInMilliSeconds, TimeUnit.MILLISECONDS);
			int j=0;
			IData[] Output = new IData[Input.length];
			
			for (Future<IData> future: futures)
			{
				IData futureOutput = future.get();
				Output[j++] = futureOutput;
			}
			
			IDataUtil.put(pipelineCursor, "Output", Output);						
			 
		}
		catch (InterruptedException ie)
		{
			throw new ServiceException(ie);
		}
		catch (ExecutionException ee)
		{
			throw new ServiceException(ee);
		}
		catch (CancellationException ce)
		{
			throw new ServiceException(ce);
		}
		finally
		{
			pipelineCursor.destroy();
		}
		
				
		// --- <<IS-END>> ---

                
	}



	public static final void shutdownThreadPool (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(shutdownThreadPool)>> ---
		// @sigtype java 3.5
		exePool.shutdown();  
		//logExecutorService.shutdown(); 			
		// --- <<IS-END>> ---

                
	}



	public static final void startupThreadPool (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(startupThreadPool)>> ---
		// @sigtype java 3.5
		// [i] field:0:required threadPoolSize
		// [i] field:0:required loggingMinimumThreads
		// [i] field:0:required loggingBacklog
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
			String	threadPoolSize = IDataUtil.getString( pipelineCursor, "threadPoolSize" );
			String	loggingBacklog = IDataUtil.getString( pipelineCursor, "loggingBacklog" );
			String	loggingMinimumThreads = IDataUtil.getString( pipelineCursor, "loggingMinimumThreads" );
		pipelineCursor.destroy();
		
		// pipeline
		
		int threadPoolSizeInt =100;   
		if (threadPoolSize != null)
			threadPoolSizeInt = Integer.parseInt(threadPoolSize);		
		
		int loggingBacklogInt =200;   
		if (loggingBacklog != null)
			loggingBacklogInt = Integer.parseInt(loggingBacklog);	
		
		int loggingMinimumThreadsInt =10;   
		if (loggingMinimumThreads != null)
			loggingMinimumThreadsInt = Integer.parseInt(loggingMinimumThreads);	
		
		//logExecutorService = Executors.newFixedThreadPool(threadPoolSizeInt);
		  exePool = new ThreadPoolExecutor(loggingMinimumThreadsInt, threadPoolSizeInt,
		         60, TimeUnit.SECONDS, new LinkedBlockingDeque<>(loggingBacklogInt) );
			
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	
	public static ExecutorService logExecutorService ;
	
	public static ThreadPoolExecutor exePool ;
	
	static class GenCallable implements Callable<IData>
	{
		IData pipeline;
		NSName NSServiceName;
		
		long time = -1;
		
		public GenCallable(IData pipeline, NSName NSServiceName)
		{
			this.pipeline = pipeline;
			this.NSServiceName = NSServiceName;
			
		}
	
		public IData call() throws Exception {
			// TODO Auto-generated method stub
			try 
			{
			   ServiceThread servth = Service.doThreadInvoke(NSServiceName, pipeline, time);			   
			   			   
			   IData Output = servth.getIData();
			   
			   //IData Output =  Service.doInvoke(NSServiceName, sess, pipeline);
			   
			   return Output;
				
			
			   
			}
			catch (Exception e)
			{
				throw new ServiceException("Unable to invoke the service " + NSServiceName +" - "+ e.getMessage());
			}
		}
	}
	
	static class GenRunnable implements Runnable 
	{
		IData pipeline;
		NSName NSServiceName;
		Session sess;
		long time = -1;
		 
		
		public GenRunnable(IData pipeline, NSName NSServiceName, Session sess)
		{
			this.pipeline = pipeline;
			this.NSServiceName = NSServiceName;
			this.sess = sess;
			
		}
	
		@Override
		public void run() {
			// TODO Auto-generated method stub
	 
		 	Service.doThreadInvoke(NSServiceName, sess, pipeline, time);
		    //Service.doInvoke(NSServiceName, pipeline);		
		    
		}
	}	
		
	// --- <<IS-END-SHARED>> ---
}

