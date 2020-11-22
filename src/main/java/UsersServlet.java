import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//With @WebServlet annotation the webapp/WEB-INF/web.xml is no longer required.
@WebServlet(
   name = "UserAPI",
   description = "UserAPI: Login / Logout with UserService",
   urlPatterns = "/login"
)
public class UsersServlet extends HttpServlet {

 @Override
 public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
   UserService userService = UserServiceFactory.getUserService();

   String thisUrl = req.getRequestURI();

   resp.getWriter().println("<button onclick=window.location.href='https://mysecondproject-290312.ey.r.appspot.com/'>Retour</button><br/>");

   resp.setContentType("text/html");
   if (req.getUserPrincipal() != null) {
     resp.getWriter()
         .println(
             "<p>Hello, "
                 + req.getUserPrincipal().getName()
                 + "!  You can <a href=\""
                 + userService.createLogoutURL(thisUrl)
                 + "\">sign out</a>.</p>");
     resp.getWriter().println(req.getUserPrincipal().toString());
     
     resp.getWriter().println("Is admin ? "+userService.isUserAdmin());
   } else {
     resp.getWriter()
         .println(
             "<p>Please <a href=\"" + userService.createLoginURL("/tinyinstahome") + "\">Se connecter</a>.</p>");
   }
 }
}