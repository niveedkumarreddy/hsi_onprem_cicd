<META http-equiv='Content-Type' content='text/html; charset=UTF-8'>
<HTML><BODY BGCOLOR="#FFFFCC"><CENTER>
<h1>Common Configurations</h1>
%invoke AIACommonLogger.services.configuration:getCommonConfiguration%
<Form name="input" action="save.dsp" method="post">
				<textarea name="configStr" style="width:1024px;height:100%;">%value xmlStr%</textarea>
				<input type="submit" value="Common Configurations Save">
</form>
%endinvoke%
<h1>Log4j2 Configurations</h1>
%invoke AIACommonLogger.services.configuration:getLog4j2Configuration%
<Form name="input" action="save1.dsp" method="post">
				<textarea name="configStr" style="width:1024px;height:100%;">%value xmlStr%</textarea>
				<input type="submit" value="Log4j2 Configurations Save">
</form>
%endinvoke%
</CENTER></BODY></HTML>