package org.grapheus.web.state.event;

import org.apache.wicket.Session;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEventSource;

public final class WebEventUtil {
    public static void send(IEventSource source, Object payload) {
        Session session = Session.get();
        source.send(session.getApplication(), Broadcast.BREADTH, payload);
    }

    private WebEventUtil() {}
}
