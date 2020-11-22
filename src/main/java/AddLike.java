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
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
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
    name = "addLike",
    urlPatterns = {"/addLike"}
)
public class AddLike extends HttpServlet {

public void doGet(HttpServletRequest request, HttpServletResponse response) 
      throws IOException {

	UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	  
	
	String key = request.getParameter("key"); //Clé du post
	long keyLong = Long.parseLong(key);
	
	String divID = request.getParameter("divid"); //Id du div où retourner
	
	Key keyPost = null;
	
	try {
		keyPost = KeyFactory.createKey("Posts", keyLong);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
    response.setContentType("text/html");
    response.setCharacterEncoding("UTF-8");
     
	if(key == null) {
		System.out.println("key est null");
		if(divID != null) {
			System.out.println("divID n'est pas null");
			response.sendRedirect("/posts#"+divID);
		}else {
			response.sendRedirect("/posts");
		}
	}else {
		
		//Recherche du post liké
		Filter likedFilter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, keyPost);
		Query qliked = new Query("Posts").setFilter(likedFilter);
				
		PreparedQuery pqLiked = datastore.prepare(qliked);
		Entity ePostLiked = pqLiked.asSingleEntity();
		
		//on récupère les likes du post et on update l'arrayList
		ArrayList<String> arrayLikes = new ArrayList<String>();
		if(ePostLiked == null) {
			response.sendRedirect("/posts");
		}else {
			if(ePostLiked.getProperty("listeLikes") != null) {
				arrayLikes = (ArrayList<String>) ePostLiked.getProperty("listeLikes");

			}
			if(!arrayLikes.contains(userService.getCurrentUser().getUserId())) {
				arrayLikes.add(userService.getCurrentUser().getUserId());
				ePostLiked.setProperty("listeLikes", arrayLikes);
				ePostLiked.setProperty("nbLike", (long) ePostLiked.getProperty("nbLike")+1);
			}
			
			//Recherche du user
			Filter userFilter = new FilterPredicate("ID", FilterOperator.EQUAL, userService.getCurrentUser().getUserId());
			Query qUser = new  Query("Users").setFilter(userFilter);
			
			PreparedQuery pqUser = datastore.prepare(qUser);
			Entity eUser = pqUser.asSingleEntity();
						
			ArrayList<String> arrayPostsLiked = new ArrayList<String>();
			//Ajout du post dans la liste du User
			if(eUser != null) {

				if(eUser.getProperty("postsLiked") != null) {
					arrayPostsLiked = (ArrayList<String>) eUser.getProperty("postsLiked");
				}
				if(!arrayPostsLiked.contains(key)) {
					arrayPostsLiked.add(key);
					eUser.setProperty("postsLiked", arrayPostsLiked);

				}
			}else {
			    eUser = new Entity("Users", user.getUserId());

			    eUser.setProperty("ID", user.getUserId());
			    eUser.setProperty("mail", user.getEmail());
			    eUser.setProperty("name", user.getNickname());
				arrayPostsLiked.add(key);
				eUser.setProperty("postsLiked", arrayPostsLiked);
			}
			Transaction txn = datastore.beginTransaction();
			datastore.put(ePostLiked);
			txn.commit();
			datastore.put(eUser);
			txn = datastore.beginTransaction();
			datastore.put(eUser);
			txn.commit();
		}
		response.sendRedirect("/posts");
	}    
  }
}