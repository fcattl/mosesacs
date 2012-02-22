import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.Request;


import org.mortbay.jetty.handler.AbstractHandler
import cwmp.CWMPMessage
import javax.servlet.http.Cookie
import java.security.MessageDigest;

class CWMPHandler extends AbstractHandler
{

    public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException, ServletException {

        Request base_request = (request instanceof Request) ? (Request)request : HttpConnection.getCurrentConnection().getRequest()
        base_request.setHandled(true)

        if (target != '/acs') {
            response.setHeader('Server', 'MosesACS 0.1 by Luca Cervasio')
            response.setContentType("text/html")
            response.setStatus(HttpServletResponse.SC_NOT_FOUND)
            response.getWriter().println( 'Not Found' )
            return
        }

        String post = request.reader.text
        if (request.getContentLength() > 0) {
            def xml = new XmlSlurper().parseText(post).declareNamespace(soap: 'http://schemas.xmlsoap.org/soap/envelope/', cwmp: 'urn:dslforum-org:cwmp-1-0')

            String RPCMethod = xml.Body.children()[0].name()

            println "Got <${RPCMethod}> from CPE at IP " + request.getRemoteAddr()

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

//                CPE cpe = CPE.findBySerial(serialNumber)
                CPE cpe
                if (!cpe) {
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
//                cpe.save()

                def cookie = new Cookie('MosesAcsSession', cpe.cookie)
                cookie.path = '/'
                cookie.maxAge = 60*60*24*365*5  // 5 anni

//                SemaphoreService.aggiungiValue(serialNumber, ['ip': request.getRemoteAddr()])

                println "New connection from CPE (sn ${cpe.serial}) with sw ${cpe.softwareVersion} and eventCodes [${cpe.lastEventCodes}]"

                // build response
                MessageFactoryService mf = new MessageFactoryService()

                println "Sending InformResponse, cookie = <${cpe.cookie}>"
                response.addCookie(cookie)
                response.setHeader('Server', 'MosesACS 0.1 by Luca Cervasio')
                response.setContentType("text/xml")
                response.setStatus(HttpServletResponse.SC_OK)
                response.getWriter().println( mf.InformResponse() )

            } else if (RPCMethod == "TransferComplete") {

            } else if (RPCMethod == "GetRPC") {

            } else if (RPCMethod == "GetParameterValuesResponse") {

            }
        } else {
            println "Got Empty Post from CPE at IP " + request.getRemoteAddr()
            // parlo se ho da dire qualcosa altrimenti chiudo

            println "Got nothing to say, sending 204 No Content"
            response.setHeader('Server', 'MosesACS 0.1 by Luca Cervasio')
//            response.setHeader( "Content-Length", "0" )
            response.setContentType("text/xml")
            response.setStatus(HttpServletResponse.SC_NO_CONTENT)
        }
    }




}