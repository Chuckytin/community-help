package com.help.community.controller.listener;

import com.help.community.controller.event.RequestStatusChangedEvent;
import com.help.community.model.Request;
import com.help.community.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RequestStatusListener {

    private final SmsService smsService;

    @EventListener
    public void handleRequestStatusChanged(RequestStatusChangedEvent event) {
        Request request = event.getRequest();
        String oldStatus = event.getOldStatus();

        if (!oldStatus.equals(request.getStatus())) {
            String message = String.format(
                    "Estado de tu solicitud '%s' ha cambiado de %s a %s",
                    request.getTitle(),
                    oldStatus,
                    request.getStatus()
            );

            if (request.getCreator().getPhoneNumber() != null) {
                smsService.sendSms(
                        request.getCreator().getPhoneNumber(),
                        message
                );
            }
        }
    }
}
