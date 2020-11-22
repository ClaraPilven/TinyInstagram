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
    name = "removeFollow",
    urlPatterns = {"/removeFollow"}
)
public class RemoveFollow extends HttpServlet {

public void doGet(HttpServletRequest request, HttpServletResponse response) 
      throws IOException {

	UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	  
	String idFollow = request.getParameter("owner");
	
    response.setContentType("text/html");
    response.setCharacterEncoding("UTF-8");
    
    
    
    //S'il n'y a pas de parametre
	if(idFollow == null) {
		response.sendRedirect("/posts");
	}else {
	    Filter followedFilter = new FilterPredicate("followed", FilterOperator.EQUAL, idFollow);
		Query qFollow = new Query("Follows").setFilter(followedFilter);
		
		PreparedQuery pqFollowed = datastore.prepare(qFollow);
		Entity e = pqFollowed.asSingleEntity();
		
		ArrayList<String> arrayFollowers = new ArrayList<String>();
		if(e.getProperty("followers") != null) {
			arrayFollowers = (ArrayList<String>) e.getProperty("followers");
		}else{
			response.sendRedirect("/posts");
		}
		
		arrayFollowers.remove(user.getUserId());
		e.setProperty("followers", arrayFollowers);
		
		Filter followerFilter = new FilterPredicate("ID", FilterOperator.EQUAL, user.getUserId());
		Query qFollower = new Query("Users").setFilter(followerFilter);
		
		PreparedQuery pqFollower = datastore.prepare(qFollower);
		Entity eUser = pqFollower.asSingleEntity();		
		
		System.out.println(eUser);
		ArrayList<String> arrayFollows = new ArrayList<String>();
		arrayFollows = (ArrayList<String>)eUser.getProperty("follows");
		arrayFollows.remove(idFollow);
		
		Transaction txn = datastore.beginTransaction();
		datastore.put(e);
		txn.commit();
		txn = datastore.beginTransaction();
		datastore.put(eUser);
		txn.commit();
		
		response.sendRedirect("/posts");
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