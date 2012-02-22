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

        WebSocket ws = new WebSocket(9999)
        ws.start();
        
        server.start()
        server.join()
    }
    
}

class WebSocket extends WebSocketServer {

    public WebSocket(int port) {
        super (new InetSocketAddress(port))
    }

    public void onClientOpen(net.tootallnate.websocket.WebSocket conn, Handshakedata data) {
        println conn.toString() + " entered the room!"
    }

    public void onError(net.tootallnate.websocket.WebSocket conn, Exception e) {
        println "error on " + conn.toString()
    }

    public void onClientClose(net.tootallnate.websocket.WebSocket conn, int a, String b, boolean c) {
        println conn.toString() + " has left the room!"
    }

    public void onClientMessage(net.tootallnate.websocket.WebSocket conn, String message) {
        println "new incoming message: <${message}>"
        conn.send message.reverse()
    }

}