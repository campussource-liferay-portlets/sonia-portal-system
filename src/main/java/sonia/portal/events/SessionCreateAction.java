/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



package sonia.portal.events;

//~--- non-JDK imports --------------------------------------------------------

import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.SessionAction;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;


//~--- JDK imports ------------------------------------------------------------

import javax.servlet.http.HttpSession;

/**
 *
 * @author th
 */
public class SessionCreateAction extends SessionAction
{

  /** Field description */
  private static final Log logger =
    LogFactoryUtil.getLog(SessionCreateAction.class);

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param session
   *
   * @throws ActionException
   */
  @Override
  public void run(HttpSession session) throws ActionException
  {
    logger.debug("*** SessionCreateAction " + session.getId());
  }
}
