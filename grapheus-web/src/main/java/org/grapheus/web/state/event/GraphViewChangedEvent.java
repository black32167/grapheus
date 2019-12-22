package org.grapheus.web.state.event;

import lombok.Value;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;

@Value
public class GraphViewChangedEvent {
    private final IPartialPageRequestHandler target;
}
