package com.help.community.controller.event;

import com.help.community.model.Request;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class RequestStatusChangedEvent extends ApplicationEvent {
    private final Request request;
    private final String oldStatus;

    public RequestStatusChangedEvent(Object source, Request request, String oldStatus) {
        super(source);
        this.request = request;
        this.oldStatus = oldStatus;
    }

}
