package koh.patterns.handler.api;

import koh.patterns.handler.context.Context;

public interface HandlerEmitter {

    void setHandlerContext(Context context);
    Context getHandlerContext();

}
