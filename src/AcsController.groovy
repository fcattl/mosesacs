
import javax.servlet.http.Cookie

class AcsController {

    static allowedMethods = [index:'POST']
    def SemaphoreService
    def MessageFactoryService

    def index = {

        String post = request.reader.text
        if (request.getContentLength() > 0) {
            def xml = new XmlSlurper().parseText(post).declareNamespace(soap: 'http://schemas.xmlsoap.org/soap/envelope/', cwmp: 'urn:dslforum-org:cwmp-1-0')

            String RPCMethod = xml.Body.children()[0].name()

            // println "Got <${RPCMethod}> from CPE at IP " + request.getRemoteAddr()

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

                CPE cpe = CPE.findBySerial(serialNumber)
                if (!cpe) {
                    // non trovata
                    cpe = new CPE()
                    cpe.serial = serialNumber
                }
                cpe.cookie = (new Date().toString() + serialNumber).encodeAsMD5()
                cpe.manufacturer = manufacturer
                cpe.IPAddress = request.getRemoteAddr()
                cpe.lastEventCodes = eventCodes
                cpe.lastInformTS = new Date()
                cpe.softwareVersion = softwareVersion
                cpe.hardwareVersion = hardwareVersion
                cpe.connectionRequestURL = connectionRequestURL
                cpe.save()

                def cookie = new Cookie('mosesacssession', cpe.cookie)
                cookie.path = '/'
                cookie.maxAge = 60*60*24*365*5  // 5 anni
                response.addCookie( cookie)

                SemaphoreService.aggiungiValue(serialNumber, ['ip': request.getRemoteAddr()])

                println "New connection from CPE (sn ${cpe.serial}) with sw ${cpe.softwareVersion} and eventCodes [${cpe.lastEventCodes}]"

                // build response
                String InformResponse = MessageFactoryService.InformResponse()
                render InformResponse
            } else if (RPCMethod == "TransferComplete") {

            } else if (RPCMethod == "GetRPC") {

            } else if (RPCMethod == "GetParameterValuesResponse") {
                response.setStatus( 204 )
                println post
                render ''
            }
        } else {
            //println "Got Empty Post from CPE at IP " + request.getRemoteAddr()
            // parlo se ho da dire qualcosa altrimenti chiudo
            String GetParameterValues = MessageFactoryService.GetParameterValues(['Device.DeviceInfo', 'Blabla'])
            //println GetParameterValues

            // got empty post, answer with 204 no content
            response.setStatus( 204 )
            response.setHeader( "Content-Length", "0" )

            render ''
            //render GetParameterValues
        }
    }

    def list = {
        Map c = SemaphoreService.getCpes()

        [cpes: c]
    }

    def test = {
        //String msg = MessageFactoryService.Reboot('cmd1234')
        String msg = MessageFactoryService.GetParameterValues(['Device.'])
        println msg
        render "OK"
    }

}


