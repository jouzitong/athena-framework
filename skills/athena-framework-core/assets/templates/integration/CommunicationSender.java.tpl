package {{PACKAGE}}.communication;

import org.athena.framework.communication.api.ChannelType;
import org.athena.framework.communication.api.CommunicationService;
import org.athena.framework.communication.api.Receiver;
import org.athena.framework.communication.api.ReceiverType;
import org.athena.framework.communication.api.SendRequest;
import org.athena.framework.communication.api.SendResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class {{NAME}}CommunicationSender {

    private final CommunicationService communicationService;

    public {{NAME}}CommunicationSender(CommunicationService communicationService) {
        this.communicationService = communicationService;
    }

    public SendResult send(ChannelType channel, ReceiverType receiverType, String target,
                           String templateCode, Map<String, Object> parameters) {
        SendRequest request = new SendRequest();
        request.setBizType("{{NAME_LOWER}}");
        request.setChannel(channel);
        request.setTemplateCode(templateCode);
        request.setReceivers(List.of(new Receiver(receiverType, target, null)));
        request.setTemplateParams(parameters);
        return communicationService.send(request);
    }
}
