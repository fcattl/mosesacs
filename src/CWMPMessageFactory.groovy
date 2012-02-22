
import groovy.xml.XmlUtil
import groovy.xml.StreamingMarkupBuilder

class CWMPMessageFactory {

    String InformResponse() {
        def builder = new StreamingMarkupBuilder()
        builder.encoding = 'UTF-8'

        def msg = builder.bind {
            mkp.xmlDeclaration()
            mkp.declareNamespace('cwmp': 'urn:dslforum-org:cwmp-1-0')
            mkp.declareNamespace('soap': 'http://schemas.xmlsoap.org/soap/envelope/')
            mkp.declareNamespace('soapenc': 'http://schemas.xmlsoap.org/soap/encoding/')
            mkp.declareNamespace('xsd': 'http://www.w3.org/2001/XMLSchema')
            mkp.declareNamespace('xsi': 'http://www.w3.org/2001/XMLSchema-instance')
            mkp.declareNamespace('schemaLocation': 'urn:dslforum-org:cwmp-1-0 ..\\schemas\\wt121.xsd')
            soap.Envelope {
                soap.Header {}
                soap.Body ('soap:encodingStyle': 'http://schemas.xmlsoap.org/soap/encoding/') {
                    cwmp.InformResponse {
                        MaxEnvelopes 1
                    }
                }
            }
        }

        return XmlUtil.serialize( msg )
    }

    String TransferCompleteResponse() {

    }

    String GetRPCResponse() {

    }

    String GetParameterValues(ArrayList<String> leaves) {
        def builder = new StreamingMarkupBuilder()
        builder.encoding = 'UTF-8'

        def msg = builder.bind {
            mkp.xmlDeclaration()
            mkp.declareNamespace('cwmp': 'urn:dslforum-org:cwmp-1-0')
            mkp.declareNamespace('soap': 'http://schemas.xmlsoap.org/soap/envelope/')
            mkp.declareNamespace('soapenc': 'http://schemas.xmlsoap.org/soap/encoding/')
            mkp.declareNamespace('xsd': 'http://www.w3.org/2001/XMLSchema')
            mkp.declareNamespace('xsi': 'http://www.w3.org/2001/XMLSchema-instance')
            mkp.declareNamespace('schemaLocation': 'urn:dslforum-org:cwmp-1-0 ..\\schemas\\wt121.xsd')
            soap.Envelope {
                soap.Header {}
                soap.Body ('soap:encodingStyle': 'http://schemas.xmlsoap.org/soap/encoding/') {
                    cwmp.GetParameterValues {
                        ParameterNames {
                            leaves.each { leaf ->
                                Value "${leaf}"
                            }
                        }
                    }
                }
            }
        }

        return XmlUtil.serialize( msg )
    }

    String SetParameterValues(ArrayList<String> leaves) {
        def builder = new StreamingMarkupBuilder()
        builder.encoding = 'UTF-8'

        def msg = builder.bind {
            mkp.xmlDeclaration()
            mkp.declareNamespace('cwmp': 'urn:dslforum-org:cwmp-1-0')
            mkp.declareNamespace('soap': 'http://schemas.xmlsoap.org/soap/envelope/')
            mkp.declareNamespace('soapenc': 'http://schemas.xmlsoap.org/soap/encoding/')
            mkp.declareNamespace('xsd': 'http://www.w3.org/2001/XMLSchema')
            mkp.declareNamespace('xsi': 'http://www.w3.org/2001/XMLSchema-instance')
            mkp.declareNamespace('schemaLocation': 'urn:dslforum-org:cwmp-1-0 ..\\schemas\\wt121.xsd')
            soap.Envelope {
                soap.Header {}
                soap.Body ('soap:encodingStyle': 'http://schemas.xmlsoap.org/soap/encoding/') {
                    cwmp.SetParameterValues {
                        ParameterNames {
                            leaves.each { leaf ->
                                Value "${leaf}"
                            }
                        }
                    }
                }
            }
        }

        return XmlUtil.serialize( msg )
    }

    String Reboot (String commandKey) {
        def builder = new StreamingMarkupBuilder()
        builder.encoding = 'UTF-8'

        def msg = builder.bind {
            mkp.xmlDeclaration()
            mkp.declareNamespace('cwmp': 'urn:dslforum-org:cwmp-1-0')
            mkp.declareNamespace('soap': 'http://schemas.xmlsoap.org/soap/envelope/')
            mkp.declareNamespace('soapenc': 'http://schemas.xmlsoap.org/soap/encoding/')
            mkp.declareNamespace('xsd': 'http://www.w3.org/2001/XMLSchema')
            mkp.declareNamespace('xsi': 'http://www.w3.org/2001/XMLSchema-instance')
            mkp.declareNamespace('schemaLocation': 'urn:dslforum-org:cwmp-1-0 ..\\schemas\\wt121.xsd')
            soap.Envelope {
                soap.Header {}
                soap.Body ('soap:encodingStyle': 'http://schemas.xmlsoap.org/soap/encoding/') {
                    cwmp.Reboot {
                        CommandKey "${commandKey}"
                    }
                }
            }
        }

        return XmlUtil.serialize( msg )
    }



}
