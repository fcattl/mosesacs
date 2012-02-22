import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector
import net.tootallnate.websocket.Handshakedata
import net.tootallnate.websocket.WebSocketServer
import org.mortbay.jetty.security.Constraint

class MosesACS {

    public static void main(String[] args) {
        Server server = new Server()
        Connector connector = new SocketConnector()
        connector.setPort(8080)

        def connectors = [connector] as Connector[]

        server.setConnectors(connectors)

        Handler handler = new CWMPHandler()
        server.setHandler(handler)

        def registry = Registry.getInstance()

        registry.ws = new WebSocket(9999)
        registry.ws.start();
        
        server.start()
        server.join()
    }
    
}

