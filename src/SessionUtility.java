package com.openqms.utilities;

import com.openqms.core.business.BusinessUser;
import com.openqms.core.settings.SystemSettings;
import java.net.URLDecoder;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import redis.clients.jedis.Jedis;

public class SessionUtility {

  private HashMap<String, String> userNames;
  private HashMap<String, String> userSessions;
  private HashMap<String, String> userLocations;

  private static SessionUtility instance;

  /** Creates a new instance of TestJSON_3 */
  public SessionUtility() {
    userNames = new HashMap<String, String>();
    userSessions = new HashMap<String, String>();
  }

  public static SessionUtility getSessionUtility() {
    if (instance == null) {
      instance = new SessionUtility();
    }
    return instance;
  }

  /**
   * @param args the command line arguments
   * @throws Exception
   */
  public String getCurrentUser(String sessionID) throws Exception {
    // DebugUtility.debug("Getting Session ID 1:"+sessionID);
    String userName = null;

    if (sessionID != null) {
      userName = userNames.get(sessionID);
    }

    if (userName == null) {
      userName = connectToLiferayAndExecute("GET_USER_FROM_SESSION", sessionID, "");
      userName = userName.replace("\"", "");

      // validate to allow user group
      boolean isInPBEGroup = false;
      UserManager um = UserManager.getUserManager();
      BusinessUser bu = um.getUser(userName);
      BusinessUser pbeGroup = um.getUser("POWERBIGROUP");
      ArrayList<String> groups = um.getGroups(userName);
      for (String gr : groups) {
        if (gr.equalsIgnoreCase(pbeGroup.getUserID())) {
          System.out.println("MATCH");
          isInPBEGroup = true;
          break;
        }
      }

      if (isInPBEGroup) {
        userNames.put(sessionID, userName);
        userSessions.put(userName, sessionID);
        Jedis jedis = new Jedis("localhost");
        jedis.set(userName, sessionID);
      }
    }

    // DebugUtility.debug("Getting Session ID 2:"+userName);
    return userName;
  }

  public void removeSessionByUser(String userName) {
    if (userName == null) {
      return;
    }

    String sessionID = userSessions.get(userName);
    userNames.remove(sessionID);
    userSessions.remove(userName);
  }

  public String connectToLiferayAndExecute(String command, String param1, String param2)
      throws Exception {

    String portletName = null;

    portletName = "/portal/api/jsonws/OpenQMSPortal-portlet.sessionentity/execute-remote-command";

    return connectToLiferayAndExecute(portletName, command, param1, param2);
  }

  public String connectToLiferayAndExecute(
      String portletName, String command, String param1, String param2) throws Exception {

    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();

    // HttpResponse resp = httpclient.execute(targetHost, post, ctx);
    GetSessionHTTPThread getthread =
        new GetSessionHTTPThread(portletName, httpClient, command, param1, param2);
    getthread.run();
    // getthread.join();
    String responseString = getthread.getResponseString();

    responseString = URLDecoder.decode(responseString, "UTF-8");
    responseString = StringUtils.replaceOnce(responseString, "\"<form", "<form");
    responseString = StringUtils.replaceOnce(responseString, "</form>\"", "</form>");
    responseString = StringUtils.replaceOnce(responseString, "\"<?xml", "<?xml");
    responseString = StringUtils.replace(responseString, "\\", "");
    responseString = StringUtils.replace(responseString, "__", "_");

    return responseString;
  }

  public boolean connectToLiferayAndAuthenticate(String userName, String password) {

    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(cm).build();

    SystemSettings ss = SystemSettings.getSystemSettingsManager();

    String hostName = ss.getCamundaHostName();
    String portNumber = ss.getCamundaHostPort();
    String protocol = ss.getCamundaProtocol();
    String adminUserName = userName;
    String adminUserNamePassword = password;
    String responseString = null;

    String portletName =
        "/portal/api/jsonws/OpenQMSPortal-portlet.sessionentity/execute-remote-command";

    // DebugUtility.debug("PERFORMANCE DEBUG FORMS46 ON "+command+" START:"+new Date()+" time:"+new
    // Date().getTime());
    // If a submission is executed and the filter query is empty then block the request

    try {

      HttpHost targetHost = new HttpHost(hostName, Integer.parseInt(portNumber), protocol);
      // DefaultHttpClient httpclient = new DefaultHttpClient();
      // CloseableHttpClient httpclient = HttpClients.createDefault();
      CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

      credentialsProvider.setCredentials(
          new AuthScope(targetHost.getHostName(), targetHost.getPort()),
          new UsernamePasswordCredentials(adminUserName, adminUserNamePassword));

      HttpClientContext localContext = HttpClientContext.create();
      localContext.setCredentialsProvider(credentialsProvider);

      AuthCache authCache = new BasicAuthCache();
      BasicScheme basicAuth = new BasicScheme();
      authCache.put(targetHost, basicAuth);

      localContext.setAttribute(ClientContext.AUTH_CACHE, authCache);

      HttpPost post = new HttpPost(portletName);
      List<NameValuePair> params = new ArrayList<NameValuePair>();
      params.add(new BasicNameValuePair("command", "AUTHENTICATE"));
      params.add(new BasicNameValuePair("sessionID", ""));
      params.add(new BasicNameValuePair("documentKey", ""));

      UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
      ((HttpPost) post).setEntity(entity);

      CloseableHttpResponse resp = httpclient.execute(targetHost, post, localContext);

      // HttpResponse resp = httpclient.execute(targetHost, post, ctx);
      responseString = EntityUtils.toString(resp.getEntity(), "UTF-8");

    } catch (Exception e) {
      e.printStackTrace();
    }

    if (responseString != null) {
      if (responseString.indexOf("\"<AUTHENTICATED>true") != -1) {

        return true;
      } else {
        return false;
      }
    } else return false;
  }

  /**
   * How many milliseconds an incoming timestamp can be off by before it's decided to be too
   * inaccurate to be legitimate.
   */
  private static final int MAXTIMESTAMPINACCURACY = 10 * 60 * 1000; // Ten Minutes

  /**
   * Checks if the given timestamp is accurate enough to prove that the command it's linked to was
   * indeed recently issued by an actual user.
   *
   * @param timestamp The given timestamp.
   * @return If it's current enough.
   */
  public static boolean checkCurrency(String timestamp) {
    Date currentTime = new Date();
    long currentTimeMillis = currentTime.getTime();
    long timestampMillis = Long.parseLong(timestamp);

    return (Math.abs(currentTimeMillis - timestampMillis) <= MAXTIMESTAMPINACCURACY);
  }

  public boolean connectToLiferayAndAuthenticateWithTimestamp(
      String username, String password, String timestamp) {
    if (timestamp == null) {
      return false;
    } else if (!checkCurrency(timestamp)) {
      return false;
    }

    return connectToLiferayAndAuthenticate(username, password);
  }
}
