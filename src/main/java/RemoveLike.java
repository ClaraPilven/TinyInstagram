import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
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
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@WebServlet(
    name = "removeLike",
    urlPatterns = {"/removeLike"}
)
public class RemoveLike extends HttpServlet {

public void doGet(HttpServletRequest request, HttpServletResponse response) 
      throws IOException {

	UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	  
	
	String key = request.getParameter("key"); //Clé du post
	long keyLong = Long.parseLong(key);
	String divID = request.getParameter("divid"); //Id du div où retrouner
	Key keyPost = KeyFactory.createKey("Posts", keyLong);
	
    response.setContentType("text/html");
    response.setCharacterEncoding("UTF-8");
    
    
    
    //S'il n'y a pas de parametre
	if(key == null) {
		response.sendRedirect("/posts#" + request.getParameter("divid"));
	}else {
		Filter likedFilter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, keyPost);
		Query qliked = new Query("Posts").setFilter(likedFilter);
				
		PreparedQuery pqLiked = datastore.prepare(qliked);

		Entity ePostLiked = pqLiked.asSingleEntity();
				
		//on récupère les likes du post et on update l'arrayList
		ArrayList<String> arrayLikes = new ArrayList<String>();
		if(ePostLiked != null) {
			if(ePostLiked.getProperty("listeLikes") != null) {
				arrayLikes = (ArrayList<String>) ePostLiked.getProperty("listeLikes");
			}
			if(arrayLikes.contains(userService.getCurrentUser().getUserId())) {
				arrayLikes.remove(userService.getCurrentUser().getUserId());
				ePostLiked.setProperty("listeLikes", arrayLikes);
				ePostLiked.setProperty("nbLike", (long) ePostLiked.getProperty("nbLike")-1);
			}
		}else {
			response.sendRedirect("/posts#" + request.getParameter("divid"));
		}
		
		//Recherche du user
		Filter userFilter = new FilterPredicate("ID", FilterOperator.EQUAL, userService.getCurrentUser().getUserId());
		Query qUser = new  Query("Users").setFilter(userFilter);
		
		PreparedQuery pqUser = datastore.prepare(qUser);
		Entity eUser = pqUser.asSingleEntity();
		
		//Ajout du post dans la liste du User
		ArrayList<String> arrayPostsLiked = new ArrayList<String>();
		if(eUser != null) {
			if(eUser.getProperty("postsLiked") != null) {
				arrayPostsLiked = (ArrayList<String>) eUser.getProperty("postsLiked");
			}
			if(arrayPostsLiked.contains(key)) {
				arrayPostsLiked.remove(key);
				eUser.setProperty("postsLiked", arrayPostsLiked);
			}
		}else {
		    eUser = new Entity("Users", user.getUserId());

		    eUser.setProperty("ID", user.getUserId());
		    eUser.setProperty("mail", user.getEmail());
		    eUser.setProperty("name", user.getNickname());
		    arrayPostsLiked.remove(key);
		    eUser.setProperty("postsLiked", arrayPostsLiked);
		}
				
		Transaction txn = datastore.beginTransaction();
		datastore.put(ePostLiked);
		txn.commit();
		datastore.put(eUser);
		txn = datastore.beginTransaction();
		datastore.put(eUser);
		txn.commit();
		System.out.println("e fin = "+ePostLiked);

		response.sendRedirect("/posts#" + request.getParameter("divid"));
	}    
  }
  
  /*public void doPost(HttpServletRequest request, HttpServletResponse response) 
	      throws IOException {
	  
	  	response.setContentType("text/html");
	    response.setCharacterEncoding("UTF-8");

	    response.getWriter().print("mmm Hello App Engine!\r\n");
	    
	    Enumeration<String> parametres = request.getParameterNames();
	    int taille = request.getParameterMap().size();
	    
	    response.getWriter().println("Taille de la map : "+taille);
	    
	    String body = request.getParameter("body");
		User user = UserServiceFactory.getUserService().getCurrentUser();
		Calendar date = Calendar.getInstance();
		
		PostMessage post = new PostMessage();
		post.owner = user.getUserId();
		post.body = body;
		post.url = user.getUserId()+(Long.MAX_VALUE-(new Date()).getTime());
		
		//Création de l'entité qui sera enregistré dans la bdd
		Entity newMsg = new Entity("Posts", post.url);
		newMsg.setProperty("date", date.getTime());
		newMsg.setProperty("owner", post.owner);
		newMsg.setProperty("body", body);
		newMsg.setProperty("url", post.url);
		newMsg.setProperty("nbLike", 1);
		newMsg.setProperty("listeLikes", new ArrayList<String>());
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		datastore.put(newMsg);
		txn.commit();
		
		System.out.println("Ajout du message dans la bdd");
				
		System.out.println(post.owner+"    "+post.body+"     "+post.url);
		
		response.getWriter().println("</br>"+date.getTime()+"</br>");
		response.getWriter().println("<div><div>OP (id/nom): "+user.getUserId()+"/"+user.getNickname()+"</div>");
	    response.getWriter().println("<label>Contenu du message :</label><input type=\"text\" name=\"message\" value=\""+body+"\" readonly></input>");
	    response.getWriter().println("<div>Date : "+date.get(Calendar.DAY_OF_MONTH)+"/"+(date.get(Calendar.MONTH)+1)+"/"+date.get(Calendar.YEAR)+" : "+date.get(Calendar.HOUR_OF_DAY)+"h"+date.get(Calendar.MINUTE)+"m"+date.get(Calendar.SECOND)+"</div></div>");
  }*/
}