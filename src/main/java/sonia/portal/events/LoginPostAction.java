/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



package sonia.portal.events;

//~--- non-JDK imports --------------------------------------------------------

import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import sonia.portal.system.UserCredentials;

/**
 *
 * @author th
 */
public class LoginPostAction extends Action
{

  /** Field description */
  private static final Log logger =
    LogFactoryUtil.getLog(LoginPostAction.class);

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   *
   * @param request
   * @param response
   *
   * @throws com.liferay.portal.kernel.events.ActionException
   */
  @Override
  public void run(HttpServletRequest request, HttpServletResponse response)
    throws com.liferay.portal.kernel.events.ActionException
  {
    try
    {
      HttpSession session = request.getSession();
      long companyId = PortalUtil.getCompanyId(request);
      long userId = PortalUtil.getUserId(request);
      User user = UserLocalServiceUtil.getUserById(companyId, userId);
      
      logger.info(">>> LOGIN : " + user.getScreenName() + " / "
        + user.getFullName() + " / " + user.getEmailAddress() + " / " + request.getRemoteAddr());
      
      UserCredentials.putSession(request);
      
    }
    catch (Exception e)
    {
      throw new ActionException(e);
    }
  }
}
