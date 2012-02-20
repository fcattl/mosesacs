import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;


class MosesACS {


    
 
    public static void main(String[] args) {
        Server server = new Server()
        Connector connector = new SocketConnector()
        connector.setPort(8080)

        def connectors = [connector] as Connector[]

        server.setConnectors(connectors)

        Handler handler = new CWMPHandler()
        server.setHandler(handler)

        server.start()
        server.join()
    }
    
}