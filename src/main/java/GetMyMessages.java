import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.services.discovery.model.JsonSchema.Variant.Map;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.cloud.datastore.StructuredQuery.OrderBy;

/**
 * Servlet implementation class Transaction
 */
@WebServlet("/Myposts")
public class GetMyMessages extends HttpServlet implements Servlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
			
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		UserService userService = UserServiceFactory.getUserService();

		
		//Prendre tous les posts
		Query q = new Query("Posts");
		q.setKeysOnly();
		q.addSort("date", SortDirection.DESCENDING);

		
		PreparedQuery pq = datastore.prepare(q);
		FetchOptions fetchOptions = FetchOptions.Builder.withLimit(10);
		List<Entity> result = pq.asList(fetchOptions);
		
		ArrayList<Key> keys=new ArrayList<Key>();
		for (Entity entity : result) {
			keys.add(entity.getKey());
		}
		
		
		//On cherche la liste des gens qu'on suit

	    Filter followsFilter = new FilterPredicate("ID", FilterOperator.EQUAL, userService.getCurrentUser().getUserId());
		Query qFollows = new Query("Users").setFilter(followsFilter);
		
		PreparedQuery pqFollows = datastore.prepare(qFollows);
		Entity userEntity = pqFollows.asSingleEntity();
		
		ArrayList<String> listeFollows = (ArrayList<String>) userEntity.getProperty("follows");
		
		//On cherche la liste des posts likés
		ArrayList<String> listeLikes = (ArrayList<String>) userEntity.getProperty("postsLiked");
		java.util.Map<Key, Entity> msgs = datastore.get(keys); // Get all keys in parallel
		
		response.getWriter().println("<button onclick=window.location.href='https://mysecondproject-290312.ey.r.appspot.com/tinyinstahome'>Retour</button><br/>");
		int i = 1;
		for (Entity msg : msgs.values()) {
			if((msg.getProperty("owner").equals(userService.getCurrentUser().getUserId()))) {

			response.getWriter().println("<div id='"+ i +"'>Auteur : " + msg.getProperty("owner"));
			response.getWriter().println("<img src='" + msg.getProperty("url") + "' height='150'>" +
					"<br/> Message : "+msg.getProperty("body") + 
					"<br/> Nombre de likes : " + msg.getProperty("nbLike") + 
					"<br/> Date de publication : " + msg.getProperty("date") + 
					"<br/>");
			
			response.getWriter().println("</div><br/>");
			i = i + 1;
			}
		}
		
	}
}