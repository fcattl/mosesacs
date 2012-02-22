import net.tootallnate.websocket.WebSocketServer
import net.tootallnate.websocket.Handshakedata

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