
class Registry {

    def cpes = [:]
    def cookies = [:]

    private static final INSTANCE = new Registry()
    public WebSocket ws
    static getInstance(){ return INSTANCE }
    private Registry() {}

}