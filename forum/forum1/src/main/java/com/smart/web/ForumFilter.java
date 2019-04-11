package com.smart.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import static com.smart.cons.CommonConstant.*;
import com.smart.domain.User;

public class ForumFilter implements Filter {
	private static final String FILTERED_REQUEST = "@@session_context_filtered_request";

	// ① 不需要登录即可访问的URI资源
	private static final String[] INHERENT_ESCAPE_URIS = { "/index.jsp",
			"/index.html", "/login.jsp", "/login/doLogin.html",
			"/register.jsp", "/register.html", "/board/listBoardTopics-",
			"/board/listTopicPosts-" };

	// ② 执行过滤
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		// ②-1 保证该过滤器在一次请求中只被调用一次
		if (request != null && request.getAttribute(FILTERED_REQUEST) != null) {//表示已经被过过滤
			chain.doFilter(request, response);//请求对象 或者 响应对象 没有加新的东西
			//也就是请求对象 或 响应对象 没有设置新的属性
		} else {//还没被过滤过

			// ②-2 设置过滤标识，防止一次请求多次过滤
			request.setAttribute(FILTERED_REQUEST, Boolean.TRUE);
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			User userContext = getSessionUser(httpRequest);//调用自己的方法 方法在下面
			//上面两句是为了获得userContext

			// ②-3 用户未登录, 且当前URI资源需要登录才能访问
			if (userContext == null
					&& !isURILogin(httpRequest.getRequestURI(), httpRequest)) {//userContext == null表示用户未登录
						//isURILogin是自己的方法
						//上面是不是多了个感叹号

						//isURILogin true 当前uri 就是不需要登录的

				String toUrl = httpRequest.getRequestURL().toString();
				//查api
				//Reconstructs the URL the client used to make the request. The returned URL contains a protocol, server name, port number, and server path, but it does not include query string parameters.
				if (!StringUtils.isEmpty(httpRequest.getQueryString())) {//StringUtils.isEmpty(...)看上面导入的类
					//上面的判断的意思是 httpRequest.getQueryString() 不是空才执行
					//或者说有请求串才执行
					toUrl += "?" + httpRequest.getQueryString();
				}
				//上面的结果是在
				//有请求串的请求的url中请求串前加了一个问号

				// ②-4将用户的请求URL保存在session中，用于登录成功之后，跳到目标URL
				httpRequest.getSession().setAttribute(LOGIN_TO_URL, toUrl);//登录成功后，应该还有一个获得会话属性的步骤

				// ②-5转发到登录页面
				request.getRequestDispatcher("/login.jsp").forward(request,
						response);//设置会话属性 并转发到登录界面
				return;//这里return 下面的 chain.doFilter(request, response)就不会执行
				//这个可以
				//因为上面转发到登录界面
				//不会按照原有路线走


			}//这个if不会执行 下面一句就会执行

			//里边的if没有对应的else 但是下面这一句 可以看成 里边的else 表示的条件是
			//userContext != null||isURILogin(httpRequest.getRequestURI(), httpRequest))
			//用户登录了 或者？
			chain.doFilter(request, response);//这个是else中(最外边的else中)
		}
	}

	public void init(FilterConfig filterConfig) throws ServletException {
	}//要实现的一个方法 用空方法体实现
   /**
    * 当前URI资源是否需要登录才能访问
    * @param requestURI
    * @param request
    * @return
    */
	private boolean isURILogin(String requestURI, HttpServletRequest request) {
		if (request.getContextPath().equalsIgnoreCase(requestURI)
				|| (request.getContextPath() + "/").equalsIgnoreCase(requestURI))//request.getContextPath().equalsIgnoreCase(requestURI)
			//访问根路径就要登录？
																				//这句的意思是？
			return true;
		for (String uri : INHERENT_ESCAPE_URIS) {
			if (requestURI != null && requestURI.indexOf(uri) >= 0) {//也就是请求URI包含了了数组中的一部分就行
																	//或者说请求URI很长，中间有一部分出现在数组中就行
				return true;
			}
		}
		return false;
	}

	protected User getSessionUser(HttpServletRequest request) {
		return (User) request.getSession().getAttribute(USER_CONTEXT);//获得会话属性
		//在BaseController里设置了这个属性
	}

	public void destroy() {
	}
}
