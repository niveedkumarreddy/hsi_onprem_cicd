<META http-equiv='Content-Type' content='text/html; charset=UTF-8'>
<HTML><BODY>
%invoke AIACommonLogger.services.configuration:saveLog4j2Configuration%
%switch isValid%
 %case 'true'%
	<B>Successfully Saved the Configuration</B>
 %case 'noValue'%
	<B>Not saved as no value provided in the configuration</B>	
 %case%
	<B>The configuration file is inValid!</B>
	<br>errorMessage:%value lastError/error% </br>
%end%
%end invoke%

</BODY></HTML>
