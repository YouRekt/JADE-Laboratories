package org.labs.homework1.behaviours;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
import org.labs.homework1.agents.MarketAgent;

import java.util.Objects;

import static jade.lang.acl.ACLMessage.*;

public class SellProductsBehaviour extends AchieveREResponder {
    private final MarketAgent marketAgent;
    static final ObjectMapper mapper = new JsonMapper();

    public SellProductsBehaviour( MarketAgent a) {
        super(a, MessageTemplate.MatchPerformative(REQUEST));
        marketAgent = a;
    }

    @Override
    protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
        ACLMessage inform = request.createReply();
        if (Objects.equals(request.getContent(), "Show me your wares.")) {
            inform.setPerformative(INFORM);
            try {
                inform.setContent(mapper.writeValueAsString(marketAgent.getProducts()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            inform.setPerformative(FAILURE);
            inform.setContent("???");
        }
        return inform;
    }
}
