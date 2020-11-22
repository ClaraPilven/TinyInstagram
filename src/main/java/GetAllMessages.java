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
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * Servlet implementation class Transaction
 */
@WebServlet("/posts")
public class GetAllMessages extends HttpServlet implements Servlet {
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
		
		for (Entity msg : msgs.values()) {
		
			response.getWriter().append("<li> OP : "+msg.getProperty("owner") +
					"<br/>Message : "+msg.getProperty("body") + 
					"<br/> Nombre de likes : " + msg.getProperty("nbLike") + 
					"<br/> Date : " + msg.getProperty("date") + 
					"<br/>");
			
			//Affichage du bouton follow/unfollow
			if(!(msg.getProperty("owner").equals(userService.getCurrentUser().getUserId()))) {
				//On ne peut pas follow une personne si on est cette personne
				if(listeFollows != null) {
					if(!listeFollows.contains(msg.getProperty("owner").toString())) {
						response.getWriter().println("<form action='/addFollow' method='get' class='form'>"
								+ "<input type='text' name='owner' value='"+msg.getProperty("owner")+"' style='display:none'/>"
								+ "<input type='submit' value='Follow'/><br/>"
								+ "</form>");
					//Si on suit déjà la personne -> bouton pour la unfollow
					}else{
						response.getWriter().println("<form action='/removeFollow' method='get' class='form'>"
								+ "<input type='text' name='owner' value='"+msg.getProperty("owner")+"' style='display:none'/>"
								+ "<input type='submit' value='Unfollow'/><br/>"
								+ "</form>");
					}
				}else {
					response.getWriter().println("<form action='/addFollow' method='get' class='form'>"
							+ "<input type='text' name='owner' value='"+msg.getProperty("owner")+"' style='display:none'/>"
							+ "<input type='submit' value='Follow'/><br/>"
							+ "</form>");
				}
			}			
			
			//Affichage du bouton like/unlike
			if(!(msg.getProperty("owner").equals(userService.getCurrentUser().getUserId()))) {
				//On ne peut pas like ses propres posts
				if(listeLikes != null) {
					if(!listeLikes.contains(Long.toString(msg.getKey().getId()))) {
						response.getWriter().println("<form action='/addLike' method='get' class='form'>"
									+ "<input hidden type='text' name='owner' value='"+msg.getProperty("owner")+"' style='display:none'/>"
									+ "<input hidden type='text' name='key' value='"+msg.getKey().getId()+"'>"
									+ "<input type='submit' value='Like'/><br/>"
									+ "</form>");
					//Si on aime déjà le post on affiche le bouton de unlike
					}else{
						response.getWriter().println("<form action='/removeLike' method='get' class='form'>"
								+ "<input type='text' name='owner' value='"+msg.getProperty("owner")+"' style='display:none'/>"		
								+ "<input hidden type='text' name='key' value='"+msg.getKey().getId()+"'>"
								+ "<input type='submit' value='Unlike'/><br/>"
								+ "</form>");
					}
				}else {
					response.getWriter().println("<form action='/addLike' method='get' class='form'>"
							+ "<input type='text' name='owner' value='"+msg.getProperty("owner")+"' style='display:none'/>"
							+ "<input hidden type='text' name='key' value='"+msg.getKey().getId()+"'>"
							+ "<input type='submit' value='Like'/><br/>"
							+ "</form>");
				}
			}			
			
			response.getWriter().println("<br/>");
		}
	}
}