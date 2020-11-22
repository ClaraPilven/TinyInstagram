import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.Transaction;

/**
 * Servlet implementation class Transaction
 */
@WebServlet("/posts/my")
public class MyMessages extends HttpServlet implements Servlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
			
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		User user = UserServiceFactory.getUserService().getCurrentUser();

		//Prendre tous les posts
		Filter filter = new FilterPredicate("owner", FilterOperator.EQUAL, user.getUserId());
		Query q = new Query("Posts").setFilter(filter);
		q.setKeysOnly();
		
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> result = pq.asList(FetchOptions.Builder.withDefaults());

		ArrayList<Key> keys=new ArrayList<Key>();
		for (Entity entity : result) {
			keys.add(entity.getKey());
		}
		
		java.util.Map<Key, Entity> msgs = datastore.get(keys); // Get all keys in parallel
		for (Entity msg : msgs.values()) {
			response.getWriter().append("<li> OP : "+msg.getProperty("owner") +
					"<br/>Message : "+msg.getProperty("body") + 
					"<br/> Nombre de likes : " + msg.getProperty("nbLike") + 
					"<br/> Date : " + msg.getProperty("date") + 
					"<br/><br/>");
		}
	}
}