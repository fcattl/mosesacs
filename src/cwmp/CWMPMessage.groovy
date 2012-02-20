package cwmp


abstract class CWMPMessage {

    private String body
    private String requestName

    CWMPMessage(String body)  {
        this.body = body

    }

}
