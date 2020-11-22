import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(
    name = "NewPost",
    urlPatterns = {"/newpost"}
)
public class NewPost extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) 
      throws IOException {

    response.setContentType("text/html");
    response.setCharacterEncoding("UTF-8");
	response.getWriter().println("<button onclick=window.location.href='https://mysecondproject-290312.ey.r.appspot.com/tinyinstahome'>Retour</button>");

    response.getWriter().print("<br/><h1>Partagez votre humeur !</h1>");
    
    response.getWriter().print("<form action=\"/addPost\" method=\"post\" class=\"form\">\n" + 
    		"<div class=\"form-example\">\n" + 
    		"  <label for=\"name\">Contenu du message </label>\n" + 
    		"  <input type=\"text\" name=\"body\" id=\"body\" required>\n" + 
    		"</div>\n" + 
    		"<div class=\"form-example\">\n" + 
    		"  <label for=\"email\">Ajouter un fichier</label>\n" + 
    		"  <input type=\"file\" name=\"file\">\n" + 
    		"</div>\n" + 
    		"<div class=\"form-example\">\n" + 
    		"  <input type=\"submit\" value=\"Poster !\">\n" + 
    		"</div>\n" + 
    		"</form>");

  }
}

