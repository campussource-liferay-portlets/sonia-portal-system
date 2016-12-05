/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



package sonia.portal.events;

//~--- non-JDK imports --------------------------------------------------------

import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;

//~--- JDK imports ------------------------------------------------------------

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author th
 */
public class LogoutPostAction extends Action
{

  /** Field description */
  private static final Log logger =
    LogFactoryUtil.getLog(LogoutPostAction.class);

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
      long companyId = PortalUtil.getCompanyId(request);
      long userId = PortalUtil.getUserId(request);

      if (userId != 0)
      {
        User user = UserLocalServiceUtil.getUserById(companyId, userId);

        logger.info("<<< LOGOUT : " + user.getScreenName() + " / "
          + user.getFullName() + " / " + user.getEmailAddress() + " / "
          + request.getRemoteAddr());
      }
      else
      {
        logger.info("<<< LOGOUT : invalid user session");
      }
    }
    catch (PortalException ex)
    {
      logger.error("PortalException", ex);
    }
    catch (SystemException ex)
    {
      logger.error("SystemException", ex);
    }
  }
}
