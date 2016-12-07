/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sonia.portal.system;

//~--- non-JDK imports --------------------------------------------------------

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;

//~--- JDK imports ------------------------------------------------------------

import java.io.Serializable;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author th
 */
public class UserCredentials implements Serializable
{

  /** Field description */
  private static final Log logger =
    LogFactoryUtil.getLog(UserCredentials.class);

  /** Field description */
  private static final long serialVersionUID = 2170387646754477057L;

  /** Field description */
  private final static HashMap<String, UserCredentials> httpSessionMap =
    new HashMap<>();

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   * @param uid
   * @param userPassword
   * @param remoteHost
   */
  private UserCredentials(String uid, String userPassword, String remoteHost)
  {
    this.uid = uid;
    this.userPassword = userPassword;
    this.loginTimestamp = System.currentTimeMillis();
    this.remoteHost = remoteHost;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param request
   */
  public static void putSession(HttpServletRequest request)
  {
    if (request != null)
    {
      HttpSession session = request.getSession(true);

      if (session != null)
      {
        UserCredentials userCredentials = getUserCredentials(request);

        logger.debug("putUserHttpSession for user " + userCredentials.uid);
        httpSessionMap.put(session.getId(), userCredentials);
      }
    }
  }

  /**
   * Method description
   *
   *
   * @param session
   */
  public static void removeSession(HttpSession session)
  {
    UserCredentials userCredentials =
      getUserCredentials(session);

    if (userCredentials != null)
    {
      logger.debug("removeUserHttpSession for user " + userCredentials.uid);
      httpSessionMap.remove(session.getId());
    }
    else
    {
      logger.debug("removeUserHttpSession user not found");
    }
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  public static Set<Entry<String, UserCredentials>> getActiveUsers()
  {
    return httpSessionMap.entrySet();
  }

  /**
   * Method description
   *
   *
   * @param request
   *
   * @return
   */
  public static UserCredentials getUserCredentials(PortletRequest request)
  {
    UserCredentials userCredentials = null;

    try
    {
      long companyId = PortalUtil.getCompanyId(request);
      long userId = PortalUtil.getUserId(request);
      User user = UserLocalServiceUtil.getUserById(companyId, userId);
      String remoteHost =
        PortalUtil.getHttpServletRequest(request).getRemoteAddr();
      String uid = user.getScreenName();
      PortletSession session = request.getPortletSession(true);

      if (session != null)
      {
        String password = (String) session.getAttribute("USER_PASSWORD",
                            PortletSession.APPLICATION_SCOPE);

        userCredentials = new UserCredentials(uid, password, remoteHost);
        userCredentials.cn = user.getFullName();
        userCredentials.mail = user.getEmailAddress();
        userCredentials.portalUserId = user.getUserId();
        userCredentials.httpSessionId = session.getId();
        logger.debug("user credentials created for user " + uid);    // + "/" + password);
      }
    }
    catch (PortalException | SystemException ex)
    {
      logger.fatal(ex);
    }

    return userCredentials;
  }

  /**
   * Method description
   *
   *
   * @param request
   *
   * @return
   */
  public static UserCredentials getUserCredentials(HttpServletRequest request)
  {
    UserCredentials userCredentials = null;

    try
    {
      long companyId = PortalUtil.getCompanyId(request);
      long userId = PortalUtil.getUserId(request);

      if ((userId > 0) && (request != null))    // userId = 0 => anonymous
      {
        User user = UserLocalServiceUtil.getUserById(companyId, userId);
        String remoteHost = request.getRemoteHost();
        String uid = user.getScreenName();
        HttpSession session = request.getSession(false);

        if (session != null)
        {
          String password = (String) session.getAttribute("USER_PASSWORD");

          userCredentials = new UserCredentials(uid, password, remoteHost);
          userCredentials.cn = user.getFullName();
          userCredentials.mail = user.getEmailAddress();
          userCredentials.portalUserId = user.getUserId();
          userCredentials.httpSessionId = session.getId();
          logger.debug("user credentials created for user " + uid);    // + "/" + password);
        }
      }
    }
    catch (PortalException | SystemException ex)
    {
      logger.fatal(ex);
    }

    return userCredentials;
  }

  /**
   * Method description
   *
   *
   * @param session
   *
   * @return
   */
  public static UserCredentials getUserCredentials(
    HttpSession session)
  {
    UserCredentials userCredentials = null;

    if ((session != null) && (session.getId() != null))
    {
      userCredentials = httpSessionMap.get(session.getId());
    }

    return userCredentials;
  }

  /**
   * Get the value of cn
   *
   * @return the value of cn
   */
  public String getCn()
  {
    return cn;
  }

  /**
   * Get the value of httpSessionId
   *
   * @return the value of httpSessionId
   */
  public String getHttpSessionId()
  {
    return httpSessionId;
  }

  /**
   * Get the value of LoginTimestamp
   *
   * @return the value of LoginTimestamp
   */
  public long getLoginTimestamp()
  {
    return loginTimestamp;
  }

  /**
   * Get the value of mail
   *
   * @return the value of mail
   */
  public String getMail()
  {
    return mail;
  }

  /**
   * Method description
   *
   *
   * @return
   *
   * @throws PortalException
   * @throws SystemException
   */
  public User getPortalUser() throws PortalException, SystemException
  {
    return UserLocalServiceUtil.getUserById(portalUserId);
  }

  /**
   * Get the value of portalUserId
   *
   * @return the value of portalUserId
   */
  public long getPortalUserId()
  {
    return portalUserId;
  }

  /**
   * Get the value of remoteHost
   *
   * @return the value of remoteHost
   */
  public String getRemoteHost()
  {
    return remoteHost;
  }

  /**
   * Get the value of uid
   *
   * @return the value of uid
   */
  public String getUid()
  {
    return uid;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getUserLocalizedTimestamp()
  {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    try
    {
      User portalUser = getPortalUser();

      dateFormat.setTimeZone(portalUser.getTimeZone());
    }
    catch (PortalException | SystemException e)
    {
      logger.error("getUserLocalizedTimestamp " + e);
    }

    return dateFormat.format(new Date());
  }

  /**
   * Get the value of userPassword
   *
   * @return the value of userPassword
   */
  public String getUserPassword()
  {
    return userPassword;
  }

  //~--- set methods ----------------------------------------------------------

  /**
   * Set the value of httpSessionId
   *
   * @param httpSessionId new value of httpSessionId
   */
  public void setHttpSessionId(String httpSessionId)
  {
    this.httpSessionId = httpSessionId;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private final long loginTimestamp;

  /** Field description */
  private final String remoteHost;

  /** Field description */
  private final String uid;

  /** Field description */
  private final String userPassword;

  /** Field description */
  private String cn;

  /** Field description */
  private String httpSessionId;

  /** Field description */
  private String mail;

  /** Field description */
  private long portalUserId;
}
