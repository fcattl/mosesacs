package org.mosesacs

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.handler.AbstractHandler
import javax.servlet.http.Cookie
import java.security.MessageDigest


class CWMPHandler extends AbstractHandler {

    public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException, ServletException {

        Request base_request = (request instanceof Request) ? (Request)request : HttpConnection.getCurrentConnection().getRequest()
        base_request.setHandled(true)

        if (target != '/acs') {
            response.setHeader('Server', 'org.mosesacs.MosesACS 0.1 by Luca Cervasio')
            response.setContentType("text/html")
            response.setStatus(HttpServletResponse.SC_NOT_FOUND)
            response.getWriter().println( 'Not Found' )
            return
        }

        String post = request.reader.text
        def xml
        def RPCMethod
        if (request.getContentLength() > 0) {
            xml = new XmlSlurper().parseText(post).declareNamespace(soap: 'http://schemas.xmlsoap.org/soap/envelope/', cwmp: 'urn:dslforum-org:cwmp-1-0')
            RPCMethod = xml.Body.children()[0].name()
        }
        
        if (RPCMethod in ['Inform', 'TransferComplete', 'GetRPC']) {
            println "Got <${RPCMethod}> from org.mosesacs.CPE at IP " + request.getRemoteAddr()

            if (RPCMethod == "Inform") {

                String serialNumber
                String manufacturer
                String softwareVersion
                String hardwareVersion
                String connectionRequestURL
                String eventCodes = ""
                String parameterKey

                // parse Inform Message
                xml.Body.'cwmp:Inform'.DeviceId.children().each {
                    //println it.name()+": "+it.text()
                    if (it.name() == "SerialNumber") {
                        serialNumber = it.text()
                    }
                    if (it.name() == "Manufacturer") {
                        manufacturer = it.text()
                    }
                }

                xml.Body.Inform.Event.children().each {
                    eventCodes += it
                    //println it
                }

                xml.Body.Inform.ParameterList.children().each {
                    String name = it.Name
                    String val = it.Value
                    //println "${name}, ${val}"
                    if (name == "InternetGatewayDevice.DeviceInfo.SoftwareVersion") {
                        softwareVersion = val
                    }
                    if (name == "InternetGatewayDevice.DeviceInfo.HardwareVersion") {
                        hardwareVersion = val
                    }
                    if (name == "InternetGatewayDevice.ManagementServer.ConnectionRequestURL") {
                        connectionRequestURL = val
                    }
                    if (name == "InternetGatewayDevice.ManagementServer.ParameterKey") {
                        parameterKey = val
                    }
                }
                Registry registry = Registry.getInstance()
                def cpe

                if (registry.cpes.containsKey(serialNumber)) {
                    cpe = registry.cpes[serialNumber]
                } else {
                    // non trovata
                    cpe = new CPE()
                    cpe.serial = serialNumber
                }

                MessageDigest digest = MessageDigest.getInstance("MD5")
                digest.update((new Date().toString() + serialNumber).bytes)
                BigInteger big = new BigInteger(1,digest.digest())

                cpe.cookie = big.toString(16).padLeft(32,"0")
                cpe.manufacturer = manufacturer
                cpe.IPAddress = request.getRemoteAddr()
                cpe.lastEventCodes = eventCodes
                cpe.lastInformTS = new Date()
                cpe.softwareVersion = softwareVersion
                cpe.hardwareVersion = hardwareVersion
                cpe.connectionRequestURL = connectionRequestURL

                registry.cpes[cpe.serial] = cpe

                def cookie = new Cookie('MosesAcsSession', cpe.cookie)
                cookie.path = '/'
                cookie.maxAge = 60*60*24*365*5  // 5 anni

                println "New connection from org.mosesacs.CPE (sn ${cpe.serial}) with sw ${cpe.softwareVersion} and eventCodes [${cpe.lastEventCodes}]"

                // build response
                CWMPMessageFactory mf = new CWMPMessageFactory()

                println "Sending InformResponse, cookie = <${cpe.cookie}>"
                registry.ws.sendToAll "New connection from org.mosesacs.CPE (sn ${cpe.serial}) with sw ${cpe.softwareVersion} and eventCodes [${cpe.lastEventCodes}]"
                registry.ws.sendToAll post

                registry.cookies[cpe.cookie] = cpe.serial

                response.addCookie(cookie)
                response.setHeader('Server', 'org.mosesacs.MosesACS 0.1 by Luca Cervasio')
                response.setContentType("text/xml")
                response.setStatus(HttpServletResponse.SC_OK)
                response.getWriter().println( mf.InformResponse() )

            } else if (RPCMethod == "TransferComplete") {

            } else if (RPCMethod == "GetRPC") {

            }
        } else {
            Registry registry = Registry.getInstance()

            if (RPCMethod == "GetParameterValuesResponse") {
                println post
                registry.ws.sendToAll(post)
            } else if (request.getContentLength() == 0) {
                println "Got Empty Post from org.mosesacs.CPE at IP " + request.getRemoteAddr()
            }

            // Got Empty Post or a Response. Now check for any event to send, otherwise 204
            def cookies = request.getCookies()
            def cookie
            cookies.each {
                if (it.getName() == "MosesAcsSession")
                    cookie = it
            }

            def serial = registry.cookies[cookie.getValue()]
            println "trovato serial " + serial
            def cpe = registry.cpes[serial]
            println cpe
            println cpe['queue']

            String[] roots = ['/Users/lc/devel/test/org/mosesacs/adbb']
            def engine = new GroovyScriptEngine(roots)

            // Load the class and create an instance
            def pluginClass = engine.loadScriptByName('swisscom.groovy')
            def plugin = pluginClass.newInstance()

            // Use the object
            try {
                plugin.prova()
            } catch (Exception e) {
                println "plugin has errors " + e.getMessage()
            }

            println "queue size "+ cpe['queue'].size()

            if (cpe['queue'].size() > 0) {
                Event event = cpe['queue'].remove(0)
                CWMPMessageFactory mf = new CWMPMessageFactory()

                println "Sending GetParameterValues"
                response.setHeader('Server', 'org.mosesacs.MosesACS 0.1 by Luca Cervasio')
                cookie.path = '/'
                cookie.maxAge = 60*60*24*365*5  // 5 anni
                response.addCookie(cookie)
                response.setContentType("text/xml")
                response.setStatus(HttpServletResponse.SC_OK)
                response.getWriter().println( mf.GetParameterValues(['ciao', 'turbo']) )
            } else {
                println "Got nothing to say, sending 204 No Content"
                response.setHeader('Server', 'org.mosesacs.MosesACS 0.1 by Luca Cervasio')
                cookie.path = '/'
                cookie.maxAge = 60*60*24*365*5  // 5 anni
                response.addCookie(cookie)
                // response.setHeader( "Content-Length", "0" )
                response.setContentType("text/xml")
                response.setStatus(HttpServletResponse.SC_NO_CONTENT)
            }
        }
    }




}