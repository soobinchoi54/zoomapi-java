package zoomapi.botAPIs.subscribe;

public abstract class CommandEventHandler{
    // base event observer
    public CommandEventHandler(){

    }

    // call-back method that shouldn't be called directly
    protected void update(){

    }

    abstract protected void execute();
}
