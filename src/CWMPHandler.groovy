import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.Request;


import org.mortbay.jetty.handler.AbstractHandler
import cwmp.CWMPMessage;

class CWMPHandler extends AbstractHandler
{
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException, ServletException
    {
        Request base_request = (request instanceof Request) ? (Request)request : HttpConnection.getCurrentConnection().getRequest()
        base_request.setHandled(true)

        InputStream body = base_request.getInputStream();
        String post = body.toString()

        try {
            CWMPMessage msg = this.parse(post)

        } catch(Exception e) {
            // non si parsa, errore, invio 500
        }


        def mf = new MessageFactoryService()

        response.setContentType("text/xml")
        response.setStatus(HttpServletResponse.SC_OK)
        response.getWriter().println( mf.InformResponse() )
    }
}