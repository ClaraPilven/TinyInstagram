import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@WebServlet(
    name = "addPost",
    urlPatterns = {"/addPost"}
)
public class AddPost extends HttpServlet {

 
  public void doPost(HttpServletRequest request, HttpServletResponse response) 
	      throws IOException {
	  
	  	response.setContentType("text/html");
	    response.setCharacterEncoding("UTF-8");

	    response.getWriter().print("mmm Hello App Engine!\r\n");
	    
	    Enumeration<String> parametres = request.getParameterNames();
	    int taille = request.getParameterMap().size();
	    	    
	    String body = request.getParameter("body");
		User user = UserServiceFactory.getUserService().getCurrentUser();
		Calendar date = Calendar.getInstance();
		
		PostMessage post = new PostMessage();
		post.owner = user.getUserId();
		post.body = body;
		post.url = user.getUserId()+(Long.MAX_VALUE-(new Date()).getTime());
		
		//Création de l'entité qui sera enregistré dans la bdd
		Entity newMsg = new Entity("Posts");
		newMsg.setProperty("date", date.getTime());
		newMsg.setProperty("owner", post.owner);
		newMsg.setProperty("body", body);
		newMsg.setProperty("url", post.url);
		newMsg.setProperty("nbLike", 0);
		newMsg.setProperty("listeLikes", new ArrayList<String>());
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		datastore.put(newMsg);
		txn.commit();
		
		System.out.println("Ajout du message dans la bdd");
				
		System.out.println(post.owner+"    "+post.body+"     "+post.url);
		
		response.sendRedirect("/posts");

		/*response.getWriter().println("</br>"+date.getTime()+"</br>");
		response.getWriter().println("<div><div>OP (id/nom): "+user.getUserId()+"/"+user.getNickname()+"</div>");
	    response.getWriter().println("<label>Contenu du message :</label><input type=\"text\" name=\"message\" value=\""+body+"\" readonly></input>");
	    response.getWriter().println("<div>Date : "+date.get(Calendar.DAY_OF_MONTH)+"/"+(date.get(Calendar.MONTH)+1)+"/"+date.get(Calendar.YEAR)+" : "+date.get(Calendar.HOUR_OF_DAY)+"h"+date.get(Calendar.MINUTE)+"m"+date.get(Calendar.SECOND)+"</div></div>");*/
  }
}