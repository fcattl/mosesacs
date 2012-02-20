

class CPE {

    String  serial
    //String  macAddress
    String  manufacturer
    String  softwareVersion
    String  hardwareVersion
    String  IPAddress
    Date    lastInformTS
    String  cookie
    String  connectionRequestURL
    String  lastEventCodes

    static constraints = {
        serial(unique: true)
    }
}
