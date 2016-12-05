/**
 * OSTFALIA CONFIDENTIAL
 *
 * 2010 - 2013 Ostfalia University of Applied Sciences All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains the property of
 * Ostfalia University of Applied Sciences and its suppliers, if any. The
 * intellectual and technical concepts contained herein are proprietary to
 * Ostfalia University of Applied Sciences and its suppliers and may be covered
 * by U.S. and Foreign Patents, patents in process, and are protected by trade
 * secret or copyright law. Dissemination of this information or reproduction of
 * this material is strictly forbidden unless prior written permission is
 * obtained from Ostfalia University of Applied Sciences.
 */



package sonia.portal.filter;

//~--- non-JDK imports --------------------------------------------------------

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.lang.reflect.Method;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.filter.ActionFilter;
import javax.portlet.filter.FilterChain;
import javax.portlet.filter.FilterConfig;
import javax.portlet.filter.RenderFilter;
import javax.portlet.filter.ResourceFilter;

/**
 *
 * @author th
 */
public class LdapConnectionReleaseFilter
  implements RenderFilter, ActionFilter, ResourceFilter
{

  /** Field description */
  private final static Log logger =
    LogFactoryUtil.getLog(LdapConnectionReleaseFilter.class);

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   */
  @Override
  public void destroy()
  {
    logger.trace("destroy");
  }

  /**
   * Method description
   *
   *
   * @param request
   * @param response
   * @param chain
   *
   * @throws IOException
   * @throws PortletException
   */
  @Override
  public void doFilter(RenderRequest request, RenderResponse response,
    FilterChain chain)
    throws IOException, PortletException
  {
    logger
      .trace("render - doFilter "
        + request.getPortletSession().getPortletContext()
          .getPortletContextName());
    chain.doFilter(request, response);
    releaseConnection();
    logger
      .trace("render - release connection "
        + request.getPortletSession().getPortletContext()
          .getPortletContextName());
  }

  /**
   * Method description
   *
   *
   * @param request
   * @param response
   * @param chain
   *
   * @throws IOException
   * @throws PortletException
   */
  @Override
  public void doFilter(ActionRequest request, ActionResponse response,
    FilterChain chain)
    throws IOException, PortletException
  {
    logger
      .trace("action - doFilter "
        + request.getPortletSession().getPortletContext()
          .getPortletContextName());
    chain.doFilter(request, response);
    releaseConnection();
    logger
      .trace("action - released connection "
        + request.getPortletSession().getPortletContext()
          .getPortletContextName());
  }

  /**
   * Method description
   *
   *
   * @param request
   * @param response
   * @param chain
   *
   * @throws IOException
   * @throws PortletException
   */
  @Override
  public void doFilter(ResourceRequest request, ResourceResponse response,
    FilterChain chain)
    throws IOException, PortletException
  {
    logger
      .trace("resource - doFilter "
        + request.getPortletSession().getPortletContext()
          .getPortletContextName());
    chain.doFilter(request, response);
    releaseConnection();
    logger
      .trace("resource - released connection "
        + request.getPortletSession().getPortletContext()
          .getPortletContextName());
  }

  /**
   * Method description
   *
   *
   * @param fc
   *
   * @throws PortletException
   */
  @Override
  public void init(FilterConfig fc) throws PortletException
  {
    logger.trace("init");
  }

  /**
   * Method description
   *
   */
  private void releaseConnection()
  {
    try
    {
      Class soniaLiferayLdapUtilClass =
        Class.forName("sonia.commons.ldap.SoniaLiferayLdapUtil");
      Method releaseConnectionMethod =
        soniaLiferayLdapUtilClass.getDeclaredMethod("releaseConnection");

      releaseConnectionMethod.invoke(null);
    }
    catch (Exception e)
    {
      logger.error(e);
    }
  }
}
