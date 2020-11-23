import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Transaction;

@WebServlet("/fullbdd")
public class fullBDD extends HttpServlet {

public void doGet(HttpServletRequest request, HttpServletResponse response) 
      throws IOException {
	
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	for(int i = 1; i <= 500; i++) {
		Entity e = new Entity("Users");
		e.setProperty("ID", i);
		e.setProperty("mail", (i+"@gmail.com"));
	    e.setProperty("name", i);
	    e.setProperty("follows", new ArrayList<String>());
        Transaction txn = datastore.beginTransaction();
        datastore.put(e);
        txn.commit();
        response.getWriter().println("Utilisateur " + i + " ajouté !");
	}
	Calendar date = Calendar.getInstance();

	for(int i = 1; i <=500; i++) {
		Entity newMsg = new Entity("Posts");
		newMsg.setProperty("date", date.getTime());
		newMsg.setProperty("owner", i);
		newMsg.setProperty("body", ""+i+" "+2*i+" "+3*i+" "+4*i+" "+5*i+" "+6*i+" ");
		newMsg.setProperty("url", "https://geo.img.pmdstatic.net/fit/http.3A.2F.2Fprd2-bone-image.2Es3-website-eu-west-1.2Eamazonaws.2Ecom.2Fgeo.2F2019.2F02.2F22.2F1c637c86-19b6-4a75-aab9-50817008413d.2Ejpeg/1120x630/background-color/ffffff/quality/70/en-islande-une-incroyable-aurore-boreale-en-forme-de-dragon.jpg");
		newMsg.setProperty("nbLike", 0);
		newMsg.setProperty("listeLikes", new ArrayList<String>());
		
        Transaction txn = datastore.beginTransaction();
		datastore.put(newMsg);
		txn.commit();
	}
	
		
	}
}
