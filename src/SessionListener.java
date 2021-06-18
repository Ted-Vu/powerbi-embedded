package com.openqms.session;

import com.openqms.portlet.dashboard.utilities.LiferayUtility;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

public class SessionListener implements HttpSessionListener, java.io.Serializable {

  private static final Logger log = Logger.getLogger(SessionListener.class);

  @Override
  public void sessionCreated(HttpSessionEvent arg0) {
    // TODO Auto-generated method stub

    // DebugUtility.debug("session created:"+arg0.getSession().getId());
    SessionManager.instance().add(arg0.getSession());
  }

  @Override
  public void sessionDestroyed(HttpSessionEvent arg0) {
    // TODO Auto-generated method stub

    // Close documents
    String sessionID = arg0.getSession().getId();

    try {
      String user = SessionManager.instance().getUserFromSession(sessionID);
      Jedis jedis = new Jedis("localhost");
      jedis.del(user);
      jedis.del("PBE" + user);
      jedis.del(sessionID);
      LiferayUtility.closeAllDocumentsForSession(sessionID);

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // DebugUtility.debug("session destroyed:"+arg0.getSession().getId());
    SessionManager.instance().remove(arg0.getSession());
  }
}
