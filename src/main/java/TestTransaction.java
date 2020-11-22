import java.io.IOException;
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
import com.google.appengine.api.datastore.Transaction;

/**
 * Servlet implementation class Transaction
 */
@WebServlet("/TestTransaction")
public class TestTransaction extends HttpServlet implements Servlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());

		Entity e = new Entity("Post"); // quelle est la clef ?? non specifiÃ© -> clef automatique
		Entity f = new Entity("Post 2", 2559955);
		e.setProperty("owner", "test owner");
		e.setProperty("url", "test url");
		e.setProperty("body", "test body");
		e.setProperty("likec", 0);
		e.setProperty("date", new Date());
		
		
		f.setProperty("Propriété", "Valeur de propiété");
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		datastore.put(f);
		txn.commit();
		
		txn = datastore.beginTransaction();
		try {
			Entity ent = datastore.get(f.getKey());
		} catch (EntityNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
				
		response.getWriter().println("Ajout dans la bdd 'normalement'");
		response.getWriter().println("Données de l'entité f :");
		java.util.Map<String, Object> map = f.getProperties();
		response.getWriter().println(map.get("Propriété"));
	}
}
