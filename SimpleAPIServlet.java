import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Maps servlet to the URL "/simple-api"
@WebServlet("/simple-api")
public class SimpleAPIServlet extends HttpServlet {

    // Handles the GET, sending a JSON response
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Sets the content type of the response to JSON
        response.setContentType("application/json");

        // The content of the response
        String jsonResponse = "{\"message\": \"Hello from the server!\"}";

        // Sends the response back to the client
        response.getWriter().write(jsonResponse);
    }
}
