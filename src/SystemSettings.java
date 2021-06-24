package com.openqms.core.settings;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;
import org.xmldb.api.base.ResourceSet;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.openqms.core.business.Application;
import com.openqms.core.business.ApplicationManager;
import com.openqms.core.business.BusinessUser;
import com.openqms.core.business.Document;
import com.openqms.core.business.DocumentKey;
import com.openqms.core.business.customevents.SYS_FORM_SETTINGEventHandler;
import com.openqms.core.business.timers.ConnectionScheduler;
import com.openqms.core.business.timers.TimerManager;
import com.openqms.core.db.View;
import com.openqms.core.db.ViewRow;
import com.openqms.utilities.DatabaseUtility;
import com.openqms.utilities.DebugUtility;
import com.openqms.utilities.EncryptionUtility;
import com.openqms.utilities.HTML2PDFUtility;
import com.openqms.utilities.PDFConversionUtility;
import com.openqms.utilities.SessionUtility;
import com.openqms.utilities.UserManager;
import com.aspose.words.License;

public class SystemSettings {
	private static SystemSettings instance;

	private boolean isSystemReady;
	
	private String AUTOGENLOCATION;
	private String FORMCUSTOMEVENTLOCATION;
	
	private String installationPath;
	private String installationPathExist;
	private String tomcatInstallationPath;
	private String srcPath;
	private String classesPath;
	private String templatesPath;
	private String hostName;
	private String portNumber;
	private String hostprotocol;
	private String formsDeliveryWebapp;
	private String formsServiceWebapp;
	private String attachmentsDirectory;

	private String mySQLurl;
	private String mySQLusername;
	private String mySQLpassword;
	private String mySQLhome;
	private String mySQLdriver;

	private String adminUserName;
	private String adminUserPassword;

	private String camundaHostName;
	private String camundaHostPort;
	private String camundaProtocol;

	private String existHostName;
	private String existHostPort;
	private String existProtocol;
	private String existUserName;
	private String existUserPassword;

	private long creatorUserId;
	private long companyId;
	private long groupId;
	private long roleId;
	private String defaultPassword;

	private String mailProtocol;
	private String mailHost;
	private String mailUser;
	private String mailFromDisplayName;
	private String mailFromEmail;
	private int assignmentEmailToHistory;

	// powerbi embedded
	private String pbeHostName;


	// sharepoint
	private String sharepointDomain;
	private String siteName;
	private String siteDocumentLibraryName;	
	
	private String clientID;
    private String clientSecret;
    private String tenantID;
    private String resource;


 


	public String getMailFromEmail() {
		return mailFromEmail;
	}

	private void setMailFromEmail(String mailFromEmail) {
		this.mailFromEmail = mailFromEmail;
	}

	private String mailPassword;
	private int mailTimeout;
	private int mailMessageTimeout;
	private String mailPort;
	private int mailSSLEncryptionEnabled;
	private int mailTLSEncryptionEnabled;
	private int mailAuthenticationEnabled;

	private int pdfPort;
	private String pdfHome;
	private String wkh2pHome;
	private int pdfMaxThreads;
	private boolean useMSoffice;
	private String windowsDrive;
	private String pdfRemoteHostURL;

	private String indexDirectory;

	private int loadAllFormsOnStartup;
	private int commentRequired;
	private int enablePDFViewer;
	private int publishAllFieldSettings;
	private int devMode;

	private int startTimeHour;
	private int startTimeMinute;
	private int mobileTimeout;
	private int imageMaxheight;

	private String dateFieldFormat;
	private String timeFieldFormat;
	private boolean skipWeekends;
	private int initialViewRecords;
	
	private boolean importExisting = true;

	private String[] versionControlForms;
	private String[] pagedViews;
	private String[] singleThreadedViews;
	private String[] mobileForms;

	private Map<String, String> versionControlReviewMap; 

	private int maxSearchResults;

	private ArrayList<String> sqlCacheForms;

	public static SystemSettings getNewSystemSettingsManager() {
		instance = null;
		return getSystemSettingsManager();
	}

	public static SystemSettings getSystemSettingsManager() {
		if (instance == null) {
			instance = new SystemSettings();
			instance.setSystemReady(false);
			try {
				//Because this is probably happening right on startup, let's wait 10 seconds
				//to really make sure we're ready to start accessing the system.
				Thread.sleep(10000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			TimerManager tm = TimerManager.getTimerManager();
			// Initialise Values
			// DISABLE THE LOG
			// DebugUtility.setDebugLog();
			DebugUtility.setInfoLog();


			try {
				instance.setInstallationPath(PrefsPropsUtil.getString("openqms.installation.path"));
				instance.setInstallationPathExist(PrefsPropsUtil.getString("openqms.installation.path.exist"));
				instance.setIndexDirectory(PrefsPropsUtil.getString("openqms.index.directory"));

				instance.setAttachmentsDirectory(PrefsPropsUtil.getString("openqms.installation.attachmentsDirectory"));
				instance.setTomcatInstallationPath(PrefsPropsUtil.getString("openqms.installation.path.tomcat"));
				instance.setSrcPath(PrefsPropsUtil.getString("openqms.installation.path.src"));
				instance.setClassesPath(PrefsPropsUtil.getString("openqms.installation.path.classes"));
				instance.setTemplatesPath(PrefsPropsUtil.getString("openqms.installation.path.templates"));
				
				instance.setMySQLurl(PrefsPropsUtil.getString("jdbc.default.url"));
				instance.setMySQLusername(PrefsPropsUtil.getString("jdbc.default.username"));
				instance.setMySQLpassword(PrefsPropsUtil.getString("jdbc.default.password"));
				instance.setMySQLhome(PrefsPropsUtil.getString("openqms.sql.mysqlhome"));
				instance.setMySQLdriver(PrefsPropsUtil.getString("jdbc.default.driverClassName"));

				instance.setHostName(PrefsPropsUtil.getString("openqms.installation.hostname"));
				instance.setHostprotocol(PrefsPropsUtil.getString("openqms.installation.protocol"));
				instance.setPortNumber(PrefsPropsUtil.getString("openqms.installation.portnumber"));
				instance.setFormsDeliveryWebapp(PrefsPropsUtil.getString("openqms.installation.formsdeliverywebapp"));
				instance.setFormsServiceWebapp(PrefsPropsUtil.getString("openqms.installation.formsservicewebapp"));

				instance.setAdminUserName(PrefsPropsUtil.getString("openqms.adminuser"));
				instance.setAdminUserPassword(PrefsPropsUtil.getString("openqms.adminuser.password"));

				instance.setCamundaHostName(PrefsPropsUtil.getString("openqms.camunda.hostname"));
				instance.setCamundaHostPort(PrefsPropsUtil.getString("openqms.camunda.port"));
				instance.setCamundaProtocol(PrefsPropsUtil.getString("openqms.camunda.protocol"));

				instance.setExistHostName(PrefsPropsUtil.getString("openqms.exist.hostname"));
				instance.setExistHostPort(PrefsPropsUtil.getString("openqms.exist.port"));
				instance.setExistProtocol(PrefsPropsUtil.getString("openqms.exist.protocol"));
				instance.setExistUserName(PrefsPropsUtil.getString("openqms.exist.username"));
				instance.setExistUserPassword(PrefsPropsUtil.getString("openqms.exist.password"));

				instance.setCreatorUserId(PrefsPropsUtil.getLong("openqms.user.creatorUserId"));
				instance.setCompanyId(PrefsPropsUtil.getLong("openqms.user.companyId"));
				instance.setGroupId(PrefsPropsUtil.getLong("openqms.user.groupId"));
				instance.setRoleId(PrefsPropsUtil.getLong("openqms.user.roleId"));
				instance.setDefaultPassword(PrefsPropsUtil.getString("openqms.user.defaultPassword"));

				instance.setMailSSLEncryptionEnabled(PrefsPropsUtil.getInteger("openqms.mail.SSLEncyrptionEnabled"));
				instance.setMailTLSEncryptionEnabled(PrefsPropsUtil.getInteger("openqms.mail.TLSEncyrptionEnabled"));
				instance.setMailAuthenticationEnabled(PrefsPropsUtil.getInteger("openqms.mail.authenticationEnabled"));
				instance.setMailHost(PrefsPropsUtil.getString("openqms.mail.host"));
				instance.setMailPassword(PrefsPropsUtil.getString("openqms.mail.password"));
				instance.setMailPort(PrefsPropsUtil.getString("openqms.mail.port"));
				instance.setMailProtocol(PrefsPropsUtil.getString("openqms.mail.protocol"));
				instance.setMailTimeout(PrefsPropsUtil.getInteger("openqms.mail.timeout"));
				instance.setMailMessageTimeout(PrefsPropsUtil.getInteger("openqms.mail.messageTimeout"));
				instance.setMailUser(PrefsPropsUtil.getString("openqms.mail.user"));
				instance.setMailFromDisplayName(PrefsPropsUtil.getString("openqms.mail.fromDisplayName"));
				instance.setMailFromEmail(PrefsPropsUtil.getString("openqms.mail.fromEmail"));

				instance.setAssignmentEmailToHistory(PrefsPropsUtil.getInteger("openqms.mail.addAssignmentEmailToHistory"));
				
				instance.setPdfPort(PrefsPropsUtil.getInteger("openqms.pdf.port"));
				instance.setPdfHome(PrefsPropsUtil.getString("openqms.pdf.home"));
				instance.setWkh2pHome(PrefsPropsUtil.getString("openqms.pdf.wkhome"));
				instance.setPdfMaxThreads(PrefsPropsUtil.getInteger("openqms.pdf.maxthreads"));
				instance.setMSOffice(PrefsPropsUtil.getBoolean("openqms.pdf.usemsoffice"));
				instance.setWindowsDrive(PrefsPropsUtil.getString("openqms.pdf.windowsdrive"));
				instance.setPdfRemoteHostURL(PrefsPropsUtil.getString("openqms.pdf.remoteHostURL"));

				instance.setLoadAllFormsOnStartup(PrefsPropsUtil.getInteger("openqms.startup.loadAllForms"));
				instance.setgetCommentRequired(PrefsPropsUtil.getInteger("openqms.business.signature.commentrequired"));
				instance.setgetEnablePDFViewer(PrefsPropsUtil.getInteger("openqms.business.search.enablePDFViewer"));
				instance.setPublishAllFieldSettings(PrefsPropsUtil.getInteger("openqms.business.publishAllFieldSettings"));

				instance.setDevMode(PrefsPropsUtil.getInteger("openqms.startup.devMode"));

				instance.setStartTimeHour(PrefsPropsUtil.getInteger("openqms.timers.startTime.hour"));
				instance.setStartTimeMinute(PrefsPropsUtil.getInteger("openqms.timers.startTime.minute"));

				instance.setMobileTimeout(PrefsPropsUtil.getInteger("openqms.timers.mobileTimeout", 60));
				
				instance.setImageMaxheight(PrefsPropsUtil.getInteger("openqms.image.maxHeight"));
				instance.setImportExisting(!PrefsPropsUtil.getBoolean("openqms.import.insertMode"));

				instance.setVersionControlForms(PrefsPropsUtil.getStringArray("openqms.business.versionControl.forms",";"));
				instance.setPagedViews(PrefsPropsUtil.getStringArray("openqms.views.pagedViews", ";"));
				instance.setSingleThreadedViews(PrefsPropsUtil.getStringArray("openqms.views.singleThreadedViews", ";"));
				instance.setInitialViewrecords(PrefsPropsUtil.getInteger("openqms.views.initialRecords", 50));
				instance.setMobileForms(PrefsPropsUtil.getStringArray("openqms.mobile.mobileForms", ";"));

				
				instance.setSqlCacheForms(PrefsPropsUtil.getStringArray("openqms.sqlcache.forms", ";"));
				
				instance.setDateFieldFormat(PrefsPropsUtil.getString("openqms.fields.dateFormat"));
				instance.setTimeFieldFormat(PrefsPropsUtil.getString("openqms.fields.timeFormat"));
				instance.setSkipWeekends(PrefsPropsUtil.getBoolean("openqms.duedate.skipWeekends"));

				instance.setAUTOGENLOCATION(PrefsPropsUtil.getString("openqms.installation.AUTOGENLOCATION"));
				instance.setFORMCUSTOMEVENTLOCATION(PrefsPropsUtil.getString("openqms.installation.FORMCUSTOMEVENTLOCATION"));
				

				instance.setSharepointDomain(PrefsPropsUtil.getString("openqms.sharepoint.domain"));
				instance.setSitename(PrefsPropsUtil.getString("openqms.sharepoint.sitename"));
				instance.setSiteDocumentLibrary(PrefsPropsUtil.getString("openqms.sharepoint.sitedocumentlibrary"));
				instance.setClientID(PrefsPropsUtil.getString("openqms.sharepoint.clientid"));
				instance.setClientSecret(PrefsPropsUtil.getString("openqms.sharepoint.clientsecret"));
				instance.setTenantID(PrefsPropsUtil.getString("openqms.sharepoint.tenantid"));
				instance.setResource(PrefsPropsUtil.getString("openqms.sharepoint.resource"));

				instance.setPowerBIHostName(PrefsPropsUtil.getString("openqms.pbe.hostname"));

				String[] versionControlReviewStrings = PrefsPropsUtil.getStringArray("openqms.business.versionControl.reviewFields", ";");
				Map<String, String> versionControlReviewMap = new HashMap<String, String>();

				for (String vcrString : versionControlReviewStrings) {
					String[] vcrSplit = vcrString.split(":");
					versionControlReviewMap.put(vcrSplit[0], vcrSplit[1]);
				}

				instance.setVersionControlReviewMap(versionControlReviewMap);

				instance.setMaxSearchResults();

				try
				{
					if(instance.getSrcPath() == null || instance.getSrcPath().equals(""))
					{
						instance.setSrcPath(instance.getInstallationPath()+"/plugins/portlets/xmlutilities-portlet/docroot/WEB-INF/src");
											
					}
					if(instance.getClassesPath() == null || instance.getClassesPath().equals(""))
					{
						instance.setClassesPath(instance.getInstallationPath()+"/plugins/portlets/xmlutilities-portlet/docroot/WEB-INF/classes");
											
					}
					if(instance.getTemplatesPath() == null || instance.getTemplatesPath().equals(""))
					{
						instance.setTemplatesPath(instance.getInstallationPath()+"/plugins/portlets/xmlutilities-portlet/docroot/templates");
											
					}
					
					//Autogenlocation
					if(instance.getAUTOGENLOCATION() == null || instance.getAUTOGENLOCATION().equals(""))
					{
						instance.setAUTOGENLOCATION(instance.getClassesPath()+"/com/openqms/core/xmltags/autogenerated");
											
					}
					//formcustomeventslocation
					if(instance.getFORMCUSTOMEVENTLOCATION() == null || instance.getFORMCUSTOMEVENTLOCATION().equals(""))
					{
						instance.setFORMCUSTOMEVENTLOCATION(instance.getClassesPath()+"/com/openqms/core/business/customevents");
											
					}
					
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				//Clean up temp folders
				try
				{


					HTML2PDFUtility.cleanAllDirectories();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

				// Load All forms
				DebugUtility.info("Startup");



				instance.loadSystemForms();


				UserManager um = UserManager.getUserManager();
				try {

					String ldapSynchMethod = PrefsPropsUtil.getString("ldap.import.method");
					String ldapEnabled = PrefsPropsUtil.getString("ldap.import.enabled");

					////DebugUtility.debug("LDAP Synch 1");

					if(ldapEnabled.equals("true"))
					{
						um.deleteAllLDAPUsers();
					}




				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/*
				ApplicationManager am2 = ApplicationManager.getApplicationManager();

				Application systemApplicationTest;
				try {
					systemApplicationTest = am2.getApplication("SYS");

					systemApplicationTest.saveAllDocuments("SYS_PROCESS_DEFINITION");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} 	


				 */
				DebugUtility.info("Startup Starting All Timers");
				//Start Timers
				tm.scheduleTimers(instance.getStartTimeHour(),instance.getStartTimeMinute());

				try {
					
					//Index System Forms (if they have not been done already) 
					DebugUtility.info("Checking Process Def Index");
					instance.indexSystemForm("SYS_PROCESS_DEFINITION", "SYS_PROCESS_DEFINITION_BY_FORM", "/fr/SYS/SYS_FORM_SETTING/edit/13");
					
					DebugUtility.info("Checking Application Setting Index");
					instance.indexSystemForm("SYS_APPLICATION_SETTING", "SYS_PROCESS_DEFINITION_BY_FORM", "/fr/SYS/SYS_FORM_SETTING/edit/14");
					
					DebugUtility.info("Checking Form Setting Index");
					instance.indexSystemForm("SYS_FORM_SETTING", "SYS_PROCESS_DEFINITION_BY_FORM", "/fr/SYS/SYS_FORM_SETTING/edit/8");
					
					DebugUtility.info("Checking User Setting Index");
					instance.indexSystemForm("SYS_USER_SETTING", "SYS_USER_SETTINGS_BY_USERNAME_SQL", "/fr/SYS/SYS_FORM_SETTING/edit/12");
					
					DebugUtility.info("Checking View Setting Index");
					instance.indexSystemForm("SYS_VIEW_SETTING", "SYS_VIEW_SETTINGS_BY_APPLICATION", "/fr/SYS/SYS_FORM_SETTING/edit/9");
					
					DebugUtility.info("Checking Field Setting Index");
					instance.indexSystemForm("SYS_FIELD_SETTING", "SYS_FIELD_SETTING_BY_DETAILS", "/fr/SYS/SYS_FORM_SETTING/edit/5");
					
					DebugUtility.info("Checking Field History Index");
					instance.indexSystemForm("SYS_FIELD_HISTORY", "SYS_FIELD_HISTORY", "/fr/SYS/SYS_FORM_SETTING/edit/19");
					
				
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				if(instance.getLoadAllFormsOnStartup() == 1)
				{
					DebugUtility.info("Startup Loading All Forms");
					instance.loadAllForms();
					DebugUtility.info("Startup Indexing");
					(new SessionUtility()).connectToLiferayAndExecute("REFRESH_ALL_INDEX", "", "");
				}

				
				ConnectionScheduler cs = ConnectionScheduler.getInstance();
				cs.initialise();
				cs.scheduleTasks();
				
				
				

			} catch (Exception e) {

				// TODO Auto-generated catch block
				System.err.println("Startup has failed due to this error:");
				e.printStackTrace();
			}
			
			try {
			
				//initialise pdf
				PDFConversionUtility pdfUtility = PDFConversionUtility.getPDFConversionUtility();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			
			instance.setSystemReady(true);
		}

		instance.setSystemReady(true);
		return instance;
	}

	private void setMySQLdriver(String newDriver) {
		mySQLdriver = newDriver;
	}
	
	public String getMySQLdriver() {
		return mySQLdriver;
	}

	public ArrayList<String> getSqlCacheForms() {
		return sqlCacheForms;
	}
	
	private void setSqlCacheForms(String[] sqlCacheForms) {
		ArrayList<String> newCacheForms = new ArrayList<String>();
		
		for (String sqlCacheForm : sqlCacheForms) {
			newCacheForms.add(sqlCacheForm);
		}
		this.sqlCacheForms = newCacheForms;
	}

	public int getLoadAllFormsOnStartup() {
		return loadAllFormsOnStartup;
	}

	private void setLoadAllFormsOnStartup(int loadAllFormsOnStartup) {
		this.loadAllFormsOnStartup = loadAllFormsOnStartup;
	}

	public int getEnablePDFViewer() {
		return enablePDFViewer;
	}

	private void setPublishAllFieldSettings(int publishAllFieldSettings) {
		this.publishAllFieldSettings = publishAllFieldSettings;
	}
	public int getPublishAllFieldSettings() {
		return publishAllFieldSettings;
	}

	private void setgetEnablePDFViewer(int enablePDFViewer) {
		this.enablePDFViewer = enablePDFViewer;
	}

	public int getCommentRequired() {
		return commentRequired;
	}

	private void setgetCommentRequired(int commentRequired) {
		this.commentRequired = commentRequired;
	}

	public String getInstallationPath() {
		return installationPath;
	}

	private void setInstallationPath(String installationPath) {
		this.installationPath = installationPath;
	}

	public String getTomcatInstallationPath() {
		return tomcatInstallationPath;
	}

	private void setTomcatInstallationPath(String tomcatInstallationPath) {
		this.tomcatInstallationPath = tomcatInstallationPath;
	}

	public String getHostName() {
		return hostName;
	}

	private void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getPortNumber() {
		return portNumber;
	}

	private void setPortNumber(String portNumber) {
		this.portNumber = portNumber;
	}

	public String getFormsDeliveryWebapp() {
		return formsDeliveryWebapp;
	}

	private void setFormsDeliveryWebapp(String formsDeliveryWebapp) {
		this.formsDeliveryWebapp = formsDeliveryWebapp;
	}

	public String getFormsServiceWebapp() {
		return formsServiceWebapp;
	}

	private void setFormsServiceWebapp(String formsServiceWebapp) {
		this.formsServiceWebapp = formsServiceWebapp;
	}

	public String getHostprotocol() {
		return hostprotocol;
	}

	private void setHostprotocol(String hostprotocol) {
		this.hostprotocol = hostprotocol;
	}

	public String getAdminUserName() {
		return adminUserName;
	}

	private void setAdminUserName(String adminUserName) {
		this.adminUserName = adminUserName;
	}

	public String getAdminUserPassword() {
		return adminUserPassword;
	}

	private void setAdminUserPassword(String encryptedPassword) {
		EncryptionUtility eu = EncryptionUtility.getInstance();
		String decryptedPassword = eu.decrypt(encryptedPassword);
		
		if (decryptedPassword == null) {
			//Oh. It's probably already in plaintext.
			decryptedPassword = encryptedPassword;
		}
		
		this.adminUserPassword = decryptedPassword;
	}

	public String getMySQLurl() {
		return mySQLurl;
	}

	private void setMySQLurl(String mySQLurl) {
		this.mySQLurl = mySQLurl;
	}

	public String getMySQLschema() {
		String[] schemaSplit = mySQLurl.split("\\?");
		schemaSplit = schemaSplit[0].split("/");

		return schemaSplit[schemaSplit.length - 1];
	}

	public String getMySQLusername() {
		return mySQLusername;
	}

	private void setMySQLusername(String mySQLusername) {
		this.mySQLusername = mySQLusername;
	}

	public String getMySQLpassword() {
		return mySQLpassword;
	}

	private void setMySQLpassword(String encryptedPassword) {
		EncryptionUtility eu = EncryptionUtility.getInstance();
		String decryptedPassword = eu.decrypt(encryptedPassword);
		
		if (decryptedPassword == null) {
			//Oh. It's probably already in plaintext.
			decryptedPassword = encryptedPassword;
		}
		
		this.mySQLpassword = decryptedPassword;
	}

	public String getMySQLhome() {
		return mySQLhome;
	}

	private void setMySQLhome(String mySQLhome) {
		this.mySQLhome = mySQLhome;
	}

	public String getCamundaHostName() {
		return camundaHostName;
	}

	private void setCamundaHostName(String camundaHostName) {
		this.camundaHostName = camundaHostName;
	}

	public String getCamundaHostPort() {
		return camundaHostPort;
	}

	private void setCamundaHostPort(String camundaHostPort) {
		this.camundaHostPort = camundaHostPort;
	}

	public String getCamundaProtocol() {
		return camundaProtocol;
	}

	private void setCamundaProtocol(String camundaProtocol) {
		this.camundaProtocol = camundaProtocol;
	}

	public String getExistHostName() {
		return existHostName;
	}

	private void setExistHostName(String existHostName) {
		this.existHostName = existHostName;
	}

	public String getExistHostPort() {
		return existHostPort;
	}

	private void setExistHostPort(String existHostPort) {
		this.existHostPort = existHostPort;
	}

	public String getExistProtocol() {
		return existProtocol;
	}

	private void setExistProtocol(String existProtocol) {
		this.existProtocol = existProtocol;
	}

	public String getExistUserName() {
		return existUserName;
	}

	private void setExistUserName(String existUserName) {
		this.existUserName = existUserName;
	}

	public String getExistUserPassword() {
		return existUserPassword;
	}

	private void setExistUserPassword(String encryptedPassword) {
		EncryptionUtility eu = EncryptionUtility.getInstance();
		String decryptedPassword = eu.decrypt(encryptedPassword);
		
		if (decryptedPassword == null) {
			//Oh. It's probably already in plaintext.
			decryptedPassword = encryptedPassword;
		}
		
		this.existUserPassword = decryptedPassword;
	}

	public long getCreatorUserId() {
		return creatorUserId;
	}

	private void setCreatorUserId(long creatorUserId) {
		this.creatorUserId = creatorUserId;
	}

	public long getCompanyId() {
		return companyId;
	}

	private void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public long getGroupId() {
		return groupId;
	}

	private void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	public long getRoleId() {
		return roleId;
	}

	private void setRoleId(long roleId) {
		this.roleId = roleId;
	}

	public String getDefaultPassword() {
		return defaultPassword;
	}

	private void setDefaultPassword(String defaultPassword) {
		this.defaultPassword = defaultPassword;
	}

	public String getMailProtocol() {
		return mailProtocol;
	}

	private void setMailProtocol(String mailProtocol) {
		this.mailProtocol = mailProtocol;
	}
	
	public int getAssignmentEmailToHistory() {
		return assignmentEmailToHistory;
	}

	private void setAssignmentEmailToHistory(int assignmentEmailToHistory) {
		this.assignmentEmailToHistory = assignmentEmailToHistory;
	}
	
	
	public String getMailHost() {
		return mailHost;
	}

	private void setMailHost(String mailHost) {
		this.mailHost = mailHost;
	}

	public String getMailUser() {
		return mailUser;
	}
	public String getMailFromDisplayName() {
		return mailFromDisplayName;
	}

	private void setMailFromDisplayName(String mailFromDisplayName) {
		this.mailFromDisplayName = mailFromDisplayName;
	}
	private void setMailUser(String mailUser) {
		this.mailUser = mailUser;
	}

	public String getMailPassword() {
		return mailPassword;
	}

	private void setMailPassword(String encryptedPassword) {
		EncryptionUtility eu = EncryptionUtility.getInstance();
		String decryptedPassword = eu.decrypt(encryptedPassword);
		
		if (decryptedPassword == null) {
			//Oh. It's probably already in plaintext.
			decryptedPassword = encryptedPassword;
		}
		
		this.mailPassword = decryptedPassword;
	}

	public int getMailTimeout() {
		return mailTimeout;
	}

	private void setMailTimeout(int mailTimeout) {
		this.mailTimeout = mailTimeout;
	}

	public String getMailPort() {
		return mailPort;
	}

	private void setMailPort(String mailPort) {
		this.mailPort = mailPort;
	}

	public int getMailSSLEncryptionEnabled() {
		return mailSSLEncryptionEnabled;
	}

	private void setMailSSLEncryptionEnabled(int mailSSLEncryptionEnabled) {
		this.mailSSLEncryptionEnabled = mailSSLEncryptionEnabled;
	}

	public int getMailTLSEncryptionEnabled() {
		return mailTLSEncryptionEnabled;
	}

	private void setMailTLSEncryptionEnabled(int mailTLSEncryptionEnabled) {
		this.mailTLSEncryptionEnabled = mailTLSEncryptionEnabled;
	}

	public int getMailMessageTimeout() {
		return mailMessageTimeout;
	}

	private void setMailMessageTimeout(int mailMessageTimeout) {
		this.mailMessageTimeout = mailMessageTimeout;
	}

	public int getMailAuthenticationEnabled() {
		return mailAuthenticationEnabled;
	}

	private void setMailAuthenticationEnabled(int mailAuthenticationEnabled) {
		this.mailAuthenticationEnabled = mailAuthenticationEnabled;
	}

	public int getPdfPort() {
		return pdfPort;
	}

	private void setPdfPort(int pdfPort) {
		this.pdfPort = pdfPort;
	}

	public String getPdfHome() {
		return pdfHome;
	}

	private void setPdfHome(String pdfHome) {
		this.pdfHome = pdfHome;
	}

	public int getPdfMaxThreads() {
		return pdfMaxThreads;
	}

	private void setPdfMaxThreads(int pdfMaxThreads) {
		this.pdfMaxThreads = pdfMaxThreads;
	}

	public boolean usesMSOffice() {
		return useMSoffice;
	}

	private void setMSOffice(boolean useMSoffice) {
		this.useMSoffice = useMSoffice;
	}

	public boolean skipWeekends() {
		return skipWeekends;
	}

	private void setSkipWeekends(boolean skipWeekends) {
		this.skipWeekends = skipWeekends;
	}
	public String getWindowsDrive() {
		return windowsDrive;
	}

	private void setWindowsDrive(String windowsDrive) {
		this.windowsDrive = windowsDrive;
	}

	public String getWkh2pHome() {
		return wkh2pHome;
	}

	private void setWkh2pHome(String wkh2pHome) {
		this.wkh2pHome = wkh2pHome;
	}

	public String getInstallationPathExist() {
		return installationPathExist;
	}

	private void setInstallationPathExist(String installationPathExist) {
		this.installationPathExist = installationPathExist;
	}

	public String getIndexDirectory() {
		return indexDirectory;
	}

	private void setIndexDirectory(String indexDirectory) {
		this.indexDirectory = indexDirectory;
	}

	public String getAttachmentsDirectory() {
		return attachmentsDirectory;
	}

	private void setAttachmentsDirectory(String attachmentsDirectory) {
		this.attachmentsDirectory = attachmentsDirectory;
	}

	public int getDevMode() {
		return devMode;
	}

	private void setDevMode(int devMode) {
		this.devMode = devMode;
	}



	public String getDateFieldFormat() {
		return dateFieldFormat;
	}

	private void setDateFieldFormat(String dateFieldFormat) {
		this.dateFieldFormat = dateFieldFormat;
	}

	public String getTimeFieldFormat() {
		return timeFieldFormat;
	}

	private void setTimeFieldFormat(String timeFieldFormat) {
		this.timeFieldFormat = timeFieldFormat;
	}

	public int getStartTimeHour() {
		return startTimeHour;
	}

	private void setStartTimeHour(int startTimeHour) {
		this.startTimeHour = startTimeHour;
	}

	private void setImportExisting(boolean importExisting) {
		this.importExisting = importExisting;
	}

	public boolean getImportExisting() {
		return importExisting;
	}

	public int getStartTimeMinute() {
		return startTimeMinute;
	}

	private void setStartTimeMinute(int startTimeMinute) {
		this.startTimeMinute = startTimeMinute;
	}

	public int getMobileTimeout() {
		return mobileTimeout;
	}

	private void setMobileTimeout(int mobileTimeout) {
		this.mobileTimeout = mobileTimeout;
	}

	public int getImageMaxheight() {
		return imageMaxheight;
	}

	private void setImageMaxheight(int imageMaxheight) {
		this.imageMaxheight = imageMaxheight;
	}

	public String getPdfRemoteHostURL() {
		return pdfRemoteHostURL;
	}

	private void setPdfRemoteHostURL(String pdfRemoteHostURL) {
		this.pdfRemoteHostURL = pdfRemoteHostURL;
	}

	public boolean openPage(String path) {

		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		DefaultHttpClient httpclient = new DefaultHttpClient();


		SystemSettings ss = SystemSettings.getSystemSettingsManager();

		String hostName = ss.getHostName();
		String portNumber = ss.getPortNumber();
		String protocol = ss.getHostprotocol();
		String adminUserName = ss.getAdminUserName();
		String adminUserNamePassword = ss.getAdminUserPassword();
		String responseString = null;

		String portletName = path;

		//If a submission is executed and the filter query is empty then block the request

		try
		{

			HttpHost targetHost = new HttpHost(hostName, Integer.parseInt(portNumber), protocol);

			httpclient.getCredentialsProvider().setCredentials(
					new AuthScope(targetHost.getHostName(), targetHost.getPort()),
					new UsernamePasswordCredentials(adminUserName, adminUserNamePassword));

			AuthCache authCache = new BasicAuthCache();
			BasicScheme basicAuth = new BasicScheme();
			authCache.put(targetHost, basicAuth);
			BasicHttpContext ctx = new BasicHttpContext();
			ctx.setAttribute(ClientContext.AUTH_CACHE,authCache);

			HttpGet get = new HttpGet(portletName);
			//"http://localhost:8899/exist/rest/db/orbeon/fr/mybookshelf/my-book-shelf/form/form.xhtml"

			HttpResponse resp = httpclient.execute(targetHost, get, ctx);

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		if(responseString != null)
		{
			return true;

		}
		else 
			return false;



	}
	public void indexSystemForm(String formNameToIndex, String viewToCheck, String formSettingDocumentPath)
	{

		ApplicationManager am = ApplicationManager.getApplicationManager();
		SettingManager sm = SettingManager.getSettingManager();
		

		
				
				/************************************************************/
				try{
					DatabaseUtility du = DatabaseUtility.getExistUtility();
					
					ArrayList<String> conditions = new ArrayList<String>();
					
					Application sysApplication = am.getApplication("SYS");
					
					View processView = du.getViewUsingConditions(viewToCheck, conditions);
					
					//Index Process Definition if it has not been done
					if(processView == null || processView.getViewRows() == null || processView.getViewRows().size() == 0)
					{
						SYS_FORM_SETTINGEventHandler eventHandler = new SYS_FORM_SETTINGEventHandler();
						//Form Setting's Document
						DocumentKey docKey = new DocumentKey(formSettingDocumentPath);
						
						Document formSettingDoc = sysApplication.getDocument(docKey);
						
						// delete all data
						eventHandler.deleteFromMysqlCache(formNameToIndex);

						// drop all tables
						du.dropMysqlCache(formNameToIndex, formSettingDoc);
						// recreate the tables
						du.createMysqlCache(formNameToIndex, formSettingDoc);

						// re-index all data
						eventHandler.index(formNameToIndex, "SYS");
					}
					
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

				/************************************************************/

		

	
	}
	public void loadAllForms() {
		// Applications
		ApplicationManager am = ApplicationManager.getApplicationManager();
		SettingManager sm = SettingManager.getSettingManager();
		// DOCCON
		try {
			/*
			 * ArrayList<Application> allApplications = new ArrayList();
			 * 
			 * allApplications.add(am.getApplication("DOCCON"));
			 * 
			 * allApplications.add(am.getApplication("TRAINING"));
			 * allApplications.add(am.getApplication("ANALYTICS"));
			 * allApplications.add(am.getApplication("INCIDENT"));
			 * allApplications.add(am.getApplication("HACCP"));
			 * allApplications.add(am.getApplication("MASTER"));
			 * allApplications.add(am.getApplication("DEVIATION"));
			 * allApplications.add(am.getApplication("RISK"));
			 * allApplications.add(am.getApplication("ACTION"));
			 * allApplications.add(am.getApplication("CAPA"));
			 * allApplications.add(am.getApplication("MEETING"));
			 * allApplications.add(am.getApplication("CHANGEMGMT"));
			 * 
			 * 
			 * for(Application app:allApplications ) { ArrayList<String>
			 * formSettingIDs = app.getApplicationSetting().getFormSettingIDs();
			 * if(formSettingIDs != null) for(String formId:formSettingIDs) {
			 * try { FormSetting formSet =
			 * sm.getFormSetting(Long.parseLong(formId));
			 * 
			 * DebugUtility.info("Loading:"+formSet.getDesignName()); Document
			 * loadDoc = app.newDocument(formSet.getDesignName());
			 * loadDoc.close(true); } catch(Exception e) { e.printStackTrace();
			 * } } }
			 */

			try {
				Application loadApplication = null;
				Document loadDoc = null;
				UserManager um = UserManager.getUserManager();

				BusinessUser systemUser = um.getSystemAdminUser();

				DebugUtility.info("Total Apps 13");
				DebugUtility.info("Startup 1");

				//Get all enabled processes 
				/************************************************************/
				try{
					DatabaseUtility du = DatabaseUtility.getExistUtility();

					ArrayList<String> conditions = new ArrayList<String>();

					conditions.add(du.createCondition(DatabaseUtility.CONDITION_AND, "SYS_APPLICATION_DISPLAY_NAME",DatabaseUtility.NOT_EQUAL_TO , "", null));
					conditions.add(du.createCondition(DatabaseUtility.CONDITION_AND, "SYS_PROCESS_DEF_DISABLED",DatabaseUtility.NOT_EQUAL_TO , "true", null));

					View processView = du.getViewUsingConditions("SYS_PROCESS_DEFINITION_BY_FORM", conditions);

					Collection<ViewRow> processViewResults = processView.getViewRows();
					Iterator<ViewRow> processViewResultsItr = processViewResults.iterator();

					int processCount = 0;

					while(processViewResultsItr.hasNext()){

						processCount++;

						ViewRow row = processViewResultsItr.next();

						//Get Process Name
						String processName = row.getColumnValue("SYS_PROCESS_DEF_ID"); 
						//Get Form name
						String formName = row.getColumnValue("SYS_FORM_NAME");
						//Get Application name
						String applicationName = row.getColumnValue("SYS_APPLICATION_DESIGN_NAME");

						//Initialise Process
						
						try
						{
							if(!processName.equals("") &&
									!formName.equals("") &&
									!applicationName.equals(""))
							{
								loadApplication = (am.getApplication(applicationName));
								loadDoc = loadApplication.newDocument(formName, systemUser, processName);

								DebugUtility.info("Startup:"+processCount+" Process:"+loadDoc.getDocumentFullURL()+" "+new Date().getTime());

								//openPage(loadDoc.getDocumentFullURL()+"?processDefinitionID="+processName);

								//Close Process
								loadDoc.close(systemUser);
							}
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
						
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

				/************************************************************/

			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public void loadSystemForms() {
		// Applications
		ApplicationManager am = ApplicationManager.getApplicationManager();
		SettingManager sm = SettingManager.getSettingManager();
		
		System.setProperty("javax.xml.transform.TransformerFactory", "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");

		// DOCCON
		try {


			
				Application loadApplication = null;
				Document loadDoc = null;
				//UserManager um = UserManager.getUserManager();

				//BusinessUser systemUser = um.getUser("test@liferay.com");
				DebugUtility.info("System Form Startup 1");

				//um.getGroups(systemUser.getUserID());

				DebugUtility.info("System Form Startup 2 :"+new Date().getTime());

				sm.getFormSetting("SYS_FORM_SETTING");
				sm.getFormSetting("SYS_SECTION_SETTING");
				sm.getFormSetting("SYS_GRID_SETTING");
				sm.getFormSetting("SYS_FIELD_SETTING");
				
				sm.getFormSetting("SYS_VIEW_SETTING");
				sm.getFormSetting("SYS_PROCESS_DEFINITION");
				sm.getFormSetting("SYS_USER_SETTING");
				sm.getFormSetting("SYS_VIEW");
				sm.getFormSetting("SYS_FIELD_HISTORY");
								
				sm.getFormSetting("SYS_APPLICATION_SETTING");
				sm.getFormSetting("SYS_CONNECTION_SETTING");
				sm.getFormSetting("SYS_TASK_DEFINITION");
				
				

				DebugUtility.info("System Form VIEW Setting loaded");

				try {
					loadApplication = (am.getApplication("SYS"));
					loadDoc = loadApplication.newDocument("SYS_VIEW");
					loadDoc.close(true);
				
					loadDoc = loadApplication.newDocument("SYS_VIEW_SETTING");
					loadDoc.close(true);
					
					loadDoc = loadApplication.newDocument("SYS_FIELD_SETTING");
					
					//loadDoc = loadApplication.newDocument("SYS_USER_SETTING");
					//loadDoc.close(true);
				
				} catch (NoClassDefFoundError  e) {
					e.printStackTrace();
				}
				//Load Analytics
				//loadApplication = (am.getApplication("ANALYTICS"));
				//loadDoc = loadApplication.newDocument("SYS_REPORT_RECORD", systemUser, "SYS_REPORT_RECORD_ID");

				String initialiseForms410 = this.getHostprotocol()+"://"+this.getHostName()+":"+this.getPortNumber()+"/forms410/fr/orbeon/builder/edit/c2b6c2f091e1420e7d95c559cd6ab786e8ae0351"; 
				openPage(initialiseForms410);

				//loadDoc.close(systemUser);

				SessionUtility su = SessionUtility.getSessionUtility();

				su.connectToLiferayAndExecute("LOAD_TASKS_LIST", "", "");

				License lic = new License();
				//DebugUtility.debug("Set license:"+this.getInstallationPath()+"/license/Aspose.Words.Java.lic");
				lic.setLicense(this.getInstallationPath()+"/license/Aspose.Words.Java.lic");
				
				
				DebugUtility.info("System Document VIEW loaded");
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String[] getVersionControlForms() {
		return versionControlForms;
	}


	private void setVersionControlForms(String[] versionControlForms) {
		this.versionControlForms = versionControlForms;
	}

	public Map<String, String> getVersionControlReviewMap() {
		return versionControlReviewMap;
	}

	private void setVersionControlReviewMap(Map<String, String> map) {
		this.versionControlReviewMap = map;
	}

	public String[] getPagedViews() {
		return pagedViews;
	}

	public String[] getSingleThreadViews() {
		return singleThreadedViews;
	}

	public String[] getMobileForms() {
		return mobileForms;
	}

	private void setPagedViews(String[] pagedViews) {
		this.pagedViews = pagedViews;
	}

	private void setSingleThreadedViews(String[] singleThreadedViews) {
		this.singleThreadedViews = singleThreadedViews;
	}

	private void setMobileForms(String[] mobileForms) {
		this.mobileForms = mobileForms;
	}

	public int getMaxSearchResults() {
		return maxSearchResults;
	}
	private void setMaxSearchResults() {

		try {
			SystemSettings ss = SystemSettings.getSystemSettingsManager();
			String formsDelivery = ss.getFormsDeliveryWebapp();
			String tomcatPath = ss.getTomcatInstallationPath();


			File file = new File(tomcatPath+"/webapps/"+formsDelivery+"/WEB-INF/resources/config/properties-local.xml");

			BufferedReader b = new BufferedReader(new FileReader(file));

			String readLine = "";

			//set default value
			maxSearchResults = 30;
			////DebugUtility.debug("Reading file using Buffered Reader");

			while ((readLine = b.readLine()) != null) {
				//DebugUtility.debug(readLine);

				if(readLine.indexOf("oxf.xforms.xbl.fr.autocomplete.max-results-displayed") != -1)
				{
					String[] attributes = readLine.split(" ");

					for(int i=0;i<attributes.length;i++)
					{
						if(attributes[i].indexOf("value=") != -1)
						{
							String maxResults = attributes[i].substring(attributes[i].indexOf("\"")+1, attributes[i].lastIndexOf("\""));
							////DebugUtility.debug("value="+maxResults);
							if(!maxResults.equals(""))
								maxSearchResults = Integer.parseInt(maxResults);

						}
					}

				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public boolean isSystemReady() {
		return isSystemReady;
	}

	private void setSystemReady(boolean newReady) {
		isSystemReady = newReady;
	}

	public int getInitialViewRecords() {
		return initialViewRecords;
	}
	
	private void setInitialViewrecords(int newIVR) {
		initialViewRecords = newIVR;
	}

	public String getSrcPath() {
		return srcPath;
	}

	public void setSrcPath(String srcPath) {
		this.srcPath = srcPath;
	}

	public String getClassesPath() {
		return classesPath;
	}

	public void setClassesPath(String classesPath) {
		this.classesPath = classesPath;
	}

	
	public String getTemplatesPath() {
		return templatesPath;
	}

	public void setTemplatesPath(String templatesPath) {
		this.templatesPath = templatesPath;
	}

	public String getAUTOGENLOCATION() {
		return AUTOGENLOCATION;
	}

	public void setAUTOGENLOCATION(String aUTOGENLOCATION) {
		AUTOGENLOCATION = aUTOGENLOCATION;
	}

	public String getFORMCUSTOMEVENTLOCATION() {
		return FORMCUSTOMEVENTLOCATION;
	}

	public void setFORMCUSTOMEVENTLOCATION(String fORMCUSTOMEVENTLOCATION) {
		FORMCUSTOMEVENTLOCATION = fORMCUSTOMEVENTLOCATION;
	}
	
	public String getSharepointDomain(){
		return sharepointDomain;
	}

	public void setSharepointDomain(String sharepointDomain){
		this.sharepointDomain = sharepointDomain;
	}

	public String getSitename(){
		return siteName;
	}
	
	public void setSitename(String siteName){
		this.siteName = siteName;
	}

	public String getSiteDocumentLibrary(){
		return siteDocumentLibraryName;
	}
	
	public void setSiteDocumentLibrary(String siteDocumentLibraryName){
		this.siteDocumentLibraryName = siteDocumentLibraryName;
	}
	
	public String getClientID()
    {
        return this.clientID;
    }

    public void setClientID(String clientID)
    {
        this.clientID = clientID;
    }

    public String getClientSecret()
    {
        return this.clientSecret;
    }

    public void setClientSecret(String clientSecret)
    {
        this.clientSecret = clientSecret;
    }

    public String getTenantID()
    {
        return this.tenantID;
    }

    public void setTenantID(String tenantID)
    {
        this.tenantID = tenantID;
    }

    public String getResource()
    {
        return this.resource;
    }

    public void setResource(String resource)
    {
        this.resource = resource;
    }

    public void setPowerBIHostName(String pbeHostName){
    	this.pbeHostName = pbeHostName;
    }

    public String getPowerBIHostName(){
    	return pbeHostName;
    }
}
