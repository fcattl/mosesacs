
class Registry {
    private static final INSTANCE = new Registry()
    public WebSocket ws
    static getInstance(){ return INSTANCE }
    private Registry() {}

}