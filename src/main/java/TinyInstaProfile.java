import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@WebServlet(
    name = "Profile - Tiny insta",
    urlPatterns = {"/tinyinstaprofile"}
)
public class TinyInstaProfile extends HttpServlet {	
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) 
      throws IOException {
	  
	  UserService userService = UserServiceFactory.getUserService();
	  
	  String thisUrl = request.getRequestURI();
	  
	  response.setContentType("text/html");
	  response.setCharacterEncoding("UTF-8");
	   
	  if(request.getUserPrincipal() == null) {
		  response.getWriter().println("Veuillez vous connecter : <a href=\""+userService.createLoginURL(thisUrl)+"\">sign in</a>");
	  }else {

		    response.getWriter().println("<h1>Voici vos informations personnelles venant de votre compte google</h1>");
		    response.getWriter().println("<ul>");
		    response.getWriter().println("<li>Votre identifiant google est : "+ userService.getCurrentUser().getUserId()+"</li>");
		    response.getWriter().println("<li>Votre Nickname google est : "+ userService.getCurrentUser().getNickname()+"</li>");
		    response.getWriter().println("<li>Votre email google est : "+ userService.getCurrentUser().getEmail()+"</li>");
		    response.getWriter().println("<li>Votre Domaine google est : "+ userService.getCurrentUser().getAuthDomain()+"</li>");
		    response.getWriter().println("</ul>");
	  }
  }
}