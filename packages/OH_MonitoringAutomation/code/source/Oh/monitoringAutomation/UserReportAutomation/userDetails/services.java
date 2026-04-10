package Oh.monitoringAutomation.UserReportAutomation.userDetails;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.util.List;
import java.util.Map;
import com.webmethods.sc.directory.*;
import com.webmethods.sc.mws.MWSLibrary;
// --- <<IS-END-IMPORTS>> ---

public final class services

{
	// ---( internal utility methods )---

	final static services _instance = new services();

	static services _newInstance() { return new services(); }

	static services _cast(Object o) { return (services)o; }

	// ---( server methods )---




	public static final void getLDAPGroupMember (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getLDAPGroupMember)>> ---
		// @sigtype java 3.5
		// [i] field:0:required LDAPGroup
		// [o] recref:0:required Users Oh.monitoringAutomation.UserReportAutomation.userDetails.flatFile:userDetailsSchDT
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
		String	LDAPGroup = IDataUtil.getString( pipelineCursor, "LDAPGroup" );
		pipelineCursor.destroy();
		
		// pipeline
		IDataCursor pipelineCursor_1 = pipeline.getCursor();
		
		// Users
		IData	Users = IDataFactory.create();
		IDataCursor UsersCursor = Users.getCursor();
		
		// Users.recordWithNoID
		IData[]	recordWithNoID ;
		
		//Actual code
		
		String	GroupName = LDAPGroup;
		
		String alias="LDAP AD";
		try {
			
		System.setProperty(MWSLibrary.SYSTEM_PROP_JDBC_POOL_ALIAS,alias);
		
		
			MWSLibrary.init();
		
		
		String[] GroupMember = null;
		int m = 0;
				IDirectorySession session = 
						DirectorySystemFactory.getDirectorySystem().createSession();
				
		//Get group details
		IDirectoryPrincipal group =  session.lookupPrincipalByName
						   (GroupName, IDirectoryPrincipal.TYPE_GROUP);
						///java.util.Map<String, Object> attributes = user.getAllAttributes();
				
		//get group id		
		String group_id=group.getID();
		String user_name=null;
		   //get group members 
		List<IDirectoryPrincipal> group_M=session.getMembers(group_id);
		
		GroupMember= new String[group_M.size()];
		recordWithNoID = new IData[group_M.size()];
		
		int i=0;
					
		
		
		
		for(IDirectoryPrincipal g:group_M){
			
			recordWithNoID[i] = IDataFactory.create();
			IDataCursor recordWithNoIDCursor = recordWithNoID[i].getCursor();
			
						user_name=g.getName(); //get username
					    GroupMember[i++]=user_name;
					    String id=g.getID();
					
					IDirectoryUser user = (IDirectoryUser) session.lookupPrincipalByID(id);
					
					String email=	user.getEmail();
					String FirstName=user.getFirstName();
					String LastName = user.getLastName();
						
						
						 List<IDirectoryRole> user_roles =session.getRoleMembership(id);
						 String[] user_Allroles = new String[user_roles.size()];
						 m=0;
						 
						 String roles="";
						for (IDirectoryRole role: user_roles) {
							    
							    String roleName = role.getName();
							    roles=roles+"\n "+roleName;
							    user_Allroles[m++]=roleName;
							}
						//	IDataUtil.put(pc, "membersOfRole", user_Allroles);
						IDataUtil.put( recordWithNoIDCursor, "UserId", user_name );
						IDataUtil.put( recordWithNoIDCursor, "FirstName", FirstName );
						IDataUtil.put( recordWithNoIDCursor, "LastName", LastName );
						IDataUtil.put( recordWithNoIDCursor, "Email", email );
						IDataUtil.put( recordWithNoIDCursor, "Category", "Individual" );
						IDataUtil.put( recordWithNoIDCursor, "CHG_Ticket","");
						IDataUtil.put( recordWithNoIDCursor, "Roles", roles );
						
						recordWithNoIDCursor.destroy();
						
					}
			
				
		
		
		IDataUtil.put( UsersCursor, "recordWithNoID", recordWithNoID );
		UsersCursor.destroy();
		IDataUtil.put( pipelineCursor_1, "Users", Users );
		
		//recordWithNoID[0] = IDataFactory.create();
		//IDataCursor recordWithNoIDCursor = recordWithNoID[0].getCursor();
		//IDataUtil.put( recordWithNoIDCursor, "UserId", "UserId" );
		
		
		
		
		
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			pipelineCursor_1.destroy();
		}
			
		// --- <<IS-END>> ---

                
	}



	public static final void getuserRoles (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getuserRoles)>> ---
		// @sigtype java 3.5
		// [i] field:0:required user
		// [o] field:0:required roles
		// pipeline
		IDataCursor pipelineCursor = pipeline.getCursor();
			String	user = IDataUtil.getString( pipelineCursor, "user" );
		
		
		
		String alias="LDAP AD";
			System.setProperty(MWSLibrary.SYSTEM_PROP_JDBC_POOL_ALIAS,alias);
			
			
				try {
					MWSLibrary.init();
					
					
					IDirectorySession session = 
							DirectorySystemFactory.getDirectorySystem().createSession();
					IDirectoryUser userDetail = (IDirectoryUser) session.lookupPrincipalByName(user,IDirectoryPrincipal.TYPE_USER );
					String id= userDetail.getID();
				//	IDataUtil.put( pipelineCursor, "id", id);
					List<IDirectoryRole> user_roles =session.getRoleMembership(id);
					 String[] user_Allroles = new String[user_roles.size()];
					 int m=0;
					 
					 String roles="";
					for (IDirectoryRole role: user_roles) {
						    
						    String roleName = role.getName();
						    roles=roles+"\n "+roleName;
						    
						}
					
					
					IDataUtil.put( pipelineCursor, "roles", roles);
					
				} catch (Exception e) {
					String error=e.toString();
					IDataUtil.put( pipelineCursor, "error", error);
				}
			
		
		// pipeline
		
		pipelineCursor.destroy();
			
		// --- <<IS-END>> ---

                
	}
}

