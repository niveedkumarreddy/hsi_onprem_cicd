package AIACommonUtilities.security;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
// --- <<IS-END-IMPORTS>> ---

public final class java

{
	// ---( internal utility methods )---

	final static java _instance = new java();

	static java _newInstance() { return new java(); }

	static java _cast(Object o) { return (java)o; }

	// ---( server methods )---




	public static final void encryptDecryptString (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(encryptDecryptString)>> ---
		// @sigtype java 3.5
		// [i] field:0:required strToEncrypt
		// [i] field:0:required strToDecrypt
		// [i] field:0:required secretKey
		// [i] field:0:required saltRandomValue
		// [i] field:0:required operation {"encrypt","decrypt"}
		// [o] field:0:required encryptedString
		// [o] field:0:required decryptedString
		// [o] field:0:required returnMessage
		// [o] field:0:required returnCode
		// pipeline
				IDataCursor pipelineCursor = pipeline.getCursor();
					String	strToEncrypt = IDataUtil.getString( pipelineCursor, "strToEncrypt" );
					String	strToDecrypt = IDataUtil.getString( pipelineCursor, "strToDecrypt" );
					String	secretKey = IDataUtil.getString( pipelineCursor, "secretKey" );
					String	saltRandomValue = IDataUtil.getString( pipelineCursor, "saltRandomValue" );
					String	operation = IDataUtil.getString( pipelineCursor, "operation" );
					
				pipelineCursor.destroy();
				
			  
				String encryptedString=null;
				String decryptedString=null;
				String returnMessage=null;
				String returnCode=null;
				
				
				IDataCursor outputCursor = pipeline.getCursor();
				
				try{			
					   if (operation.equals("encrypt"))
						{
								if (strToEncrypt!=null && !strToEncrypt.trim().isEmpty() && secretKey!=null && !secretKey.trim().isEmpty() && saltRandomValue!=null && !saltRandomValue.trim().isEmpty() )
								{
									//String salt=getSalt();
									
									IvParameterSpec ivspec=getIVSpec();				
									SecretKeySpec sksVal = getSecretKeySpec(secretKey,saltRandomValue);
				    
									//Cipher Initialization
									Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
									cipher.init(Cipher.ENCRYPT_MODE, sksVal, ivspec);
				    
							
									encryptedString=Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
									returnCode="0";
									returnMessage="Encryption : SUCCESS";
							
									byte[] byteaes=sksVal.getEncoded();
									String decryptKeyVal=Base64.getEncoder().encodeToString(byteaes);		
				    
									IDataUtil.put( outputCursor, "encryptedString", encryptedString );
									IDataUtil.put( outputCursor, "returnMessage", returnMessage );
									IDataUtil.put( outputCursor, "returnCode", returnCode );
									IDataUtil.put( outputCursor, "SALTSecretKey", decryptKeyVal );
				    			    
								}
							    else
							    {
							    	returnCode="1";
									returnMessage="Encryption : FAILURE - Please supply both PlainText, EncryptKey & SALT RandomValue";
							    	IDataUtil.put( outputCursor, "returnMessage", returnMessage );
									IDataUtil.put( outputCursor, "returnCode", returnCode );
							    }
						} 
					   else if (operation.equals("decrypt"))
					   {	
						
						    if (strToDecrypt!=null && !strToDecrypt.trim().isEmpty() && secretKey!=null && !secretKey.trim().isEmpty() && saltRandomValue!=null && !saltRandomValue.trim().isEmpty())
						       {
								 
						    		//String salt=getSalt();
						    		IvParameterSpec ivspec=getIVSpec();	
												
									SecretKeySpec sksVal = getSecretKeySpec(secretKey,saltRandomValue);
								
								 	// Cipher Initialization
								 	Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
								 	cipher.init(Cipher.DECRYPT_MODE, sksVal, ivspec);
							
								 	byte[] encryptTextBytes = Base64.getDecoder().decode(strToDecrypt);
								 	byte[] decyrptTextBytes = cipher.doFinal(encryptTextBytes);
		
								 	decryptedString = new String(decyrptTextBytes);
								 	returnCode="0";
								 	returnMessage="Decryption : SUCCESS";
											    
								 	IDataUtil.put( outputCursor, "decryptedString", decryptedString );
								 	IDataUtil.put( outputCursor, "returnMessage", returnMessage );
								 	IDataUtil.put( outputCursor, "returnCode", returnCode );							 	
							
							 		}
							 else{
								 	returnCode="1";
									returnMessage="Decryption : FAILURE - Please supply  CipherText , DecryptKey & SALT RandomValue ";
							    	IDataUtil.put( outputCursor, "returnMessage", returnMessage );
									IDataUtil.put( outputCursor, "returnCode", returnCode );
								 
							 	 }
						}
					   else
					   {
						   	returnCode="1";
							returnMessage="FAILURE - Please select the Operation to perform ";
					    	IDataUtil.put( outputCursor, "returnMessage", returnMessage );
							IDataUtil.put( outputCursor, "returnCode", returnCode );
					   }
				}
						catch (Exception e)
						      {	
								IDataUtil.put( outputCursor, "returnMessage", e.getMessage() );
								IDataUtil.put( outputCursor, "returnCode", "1" );			
								}
					
				
				outputCursor.destroy();
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	private static String getSalt(){
	    SecureRandom random = new SecureRandom();
	    byte bytes[] = new byte[32];
	    random.nextBytes(bytes);
	    String salt = new String(bytes);
	    return salt;
	}
	
	private static IvParameterSpec getIVSpec()
	{
		byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	    IvParameterSpec ivspec = new IvParameterSpec(iv);
		return ivspec;
	
	}
	
	private static SecretKeySpec getSecretKeySpec(String secretKey, String salt) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException
	{
		
		byte[] saltBytes = salt.getBytes("UTF-8");
		
		SecretKeyFactory skfactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		PBEKeySpec spec = new PBEKeySpec(secretKey.toCharArray(), saltBytes, 65536, 256);
		SecretKey secretKeyObj = skfactory.generateSecret(spec);
		SecretKeySpec sksVal = new SecretKeySpec(secretKeyObj.getEncoded(), "AES");
		return sksVal;
	}
	// --- <<IS-END-SHARED>> ---
}

