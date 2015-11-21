package koh.patterns;

import koh.patterns.event.api.EventListener;
import koh.patterns.handler.api.Handler;

public interface Controller extends Handler, EventListener {
}
