package p1a;

import java.io.*;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;


@WebServlet("/Handler")
public class Handler extends HttpServlet {

	private static final long serialVersionUID = 1L;
	protected static ConcurrentHashMap<String, Session> sessionTable = new ConcurrentHashMap<String, Session>();
	
	protected static Timer cleanTimer = new Timer();
	protected static Cleanup cleaner = new Cleanup();
	private static String cookieName = "CS5300PROJ1SESSION";
	private String message = "";

	public Handler() {
		super();
		// start cleanup method after 2.5 minnutes then, run the cleanup every 10 minutes
		cleanTimer.schedule(cleaner, 150000, 600000);
	}

		
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		Session curSession = new Session();
		String action = (String) request.getParameter("command");
		String thisSessionID = "";
		String cookieData = "";
		Cookie myCookie = null;
		String[] locations = {request.getLocalAddr().toString()};
		boolean curSessionLoggedOut = false;
		
		boolean flag = false;
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			message = "Welcome!";
			curSession = new Session();
			curSession.getSession(message, request.getRemoteAddr());
			thisSessionID = curSession.getSessionID();
			myCookie = new Cookie(cookieName, cookieData);
					} else {
			for (Cookie retrievedCookie : cookies) {
				String name = retrievedCookie.getName();
				String value = retrievedCookie.getValue();
				if (name.equals(cookieName)) {
					myCookie = retrievedCookie;
					curSession= Handler.sessionTable.get(value.split("#")[0]);
					curSession.parseCookieData(value);
					thisSessionID = curSession.getSessionID();
					curSession.fetchSession(thisSessionID);
					message = curSession.readData();
					flag = true;
				}
			}
		}
		if (action != null && action != "") {
			if (action.equals("Replace")) {
				message = request.getParameter("message");
			} else if (action.equals("Logout") && myCookie != null) {
				myCookie.setMaxAge(0);
				response.addCookie(myCookie);
				message = "You have been logged out.";
				curSessionLoggedOut = true;
			} else {
				
//				if (action.equals("Refresh") && myCookie != null){
//					curSession.increaseVersion();
//				}
			}
		}
		
		if (! curSessionLoggedOut) {
			curSession.writeData(message);
			myCookie.setMaxAge(curSession.getExpTime());
			cookieData = curSession.createCookieData(locations);
			myCookie.setValue(cookieData);
			response.addCookie(myCookie);
		}
		
		
		
		out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
		out.println("<html><head></head><body>");
		out.println("<h1>" + message + "</h1>");	
		out.println("<form action=\"Handler\" method=\"get\">");
		out.println("<input type=\"submit\" name=\"command\" value=\"Replace\" />");
		out.println("<input type=\"text\" maxlength=\"256\" name=\"message\" />");
		out.println("<br /><br />");
		out.println("<input type=\"submit\" name=\"command\" value=\"Refresh\" />");
		out.println("<br /><br />");
		out.println("<input type=\"submit\" name=\"command\" value=\"Logout\" />");
		out.println("<br /><br />");
		out.println("</form>");
		out.println("<p>Current_ID"+curSession.getSessionID()+"</p>");
		out.println("<p>VersionNum: " + curSession.getVersionNumber() + "</p>");
		out.println("</body></html>");
		//if (curSessionLoggedOut){
		//	Handler.sessionTable.remove(thisSessionID);
		//}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	

	public void destroy() {
		// do nothing
	}

}
