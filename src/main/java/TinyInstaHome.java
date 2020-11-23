import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@WebServlet(
    name = "Home - Tiny insta",
    urlPatterns = {"/tinyinstahome"}
)
public class TinyInstaHome extends HttpServlet {	
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) 
      throws IOException {
	  
	  UserService userService = UserServiceFactory.getUserService();
	  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	  
	  String thisUrl = request.getRequestURI();
	  
	  response.setContentType("text/html");
	  response.setCharacterEncoding("UTF-8");
	   
	  if(request.getUserPrincipal() == null) {
		  response.getWriter().println("Veuillez vous connecter : <a href=\""+userService.createLoginURL(thisUrl)+"\">sign in</a>");
	  }else {
			response.getWriter().println("<button onclick=window.location.href='https://mysecondproject-290312.ey.r.appspot.com/'>Retour</button>");

		    response.getWriter().println("<br/><p><a href=\"/tinyinstaprofile\">Profil perso</a></p>");
		    
		    response.getWriter().println("<table><tr>\r\n"
		    		+ "      <td><a href='/newpost'>Partagez votre humeur avec un post !</a> </td>\r\n"
		    		+ "      </tr>\r\n"
		    		+ "      <tr>\r\n"
		    		+ "      	<td><a href='/posts'>Liste des postes existants</a> </td>\r\n"
		    		+ "      </tr>"
		    		+ "      <tr>\r\n"
		    		+ "      	<td><a href='/Myposts'>Liste de vos postes</a> </td>\r\n"
		    		+ "      </tr></table>");
		    User user = userService.getCurrentUser();
		    
		    Filter userFilter = new FilterPredicate("ID", FilterOperator.EQUAL, user.getUserId());
		    Query q = new Query("Users").setFilter(userFilter);		    
		    q.setKeysOnly();
			
			PreparedQuery pq = datastore.prepare(q);
			List<Entity> result = pq.asList(FetchOptions.Builder.withDefaults());
			
			if(result.size() == 0) {
			    Entity e = new Entity("Users", user.getUserId());

			    e.setProperty("ID", user.getUserId());
			    e.setProperty("mail", user.getEmail());
			    e.setProperty("name", user.getNickname());
			    e.setProperty("follows", new ArrayList<String>());
			    
				Transaction txn = datastore.beginTransaction();
				datastore.put(e);
				txn.commit();
			}
	  }
  }
}