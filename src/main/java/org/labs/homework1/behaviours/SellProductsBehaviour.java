package org.labs.homework1.behaviours;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;
import org.labs.homework1.agents.MarketAgent;

import java.util.List;
import java.util.Objects;

import static jade.lang.acl.ACLMessage.*;

public class SellProductsBehaviour extends ContractNetResponder {
    private final MarketAgent marketAgent;
    static final ObjectMapper mapper = new JsonMapper();

    public SellProductsBehaviour(MarketAgent a) {
        super(a, MessageTemplate.MatchPerformative(CFP));
        marketAgent = a;
    }

    @Override
    protected ACLMessage handleCfp(ACLMessage cfp) throws RefuseException, FailureException, NotUnderstoodException {
        ACLMessage propose = cfp.createReply();
        if (Objects.equals(cfp.getContent(), "Show me your wares.")) {
            propose.setPerformative(PROPOSE);
            try {
                propose.setContent(mapper.writeValueAsString(marketAgent.getProducts()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            propose.setPerformative(REFUSE);
            propose.setContent("???");
        }
        return propose;
    }

    @Override
    protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException {
        List<String> products = null;
        try {
            //noinspection unchecked
            products = (List<String>) mapper.readValue(accept.getContent(), List.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Double price = products.stream().map(p -> marketAgent.getProducts().get(p)).reduce(0.0, Double::sum);
        System.out.printf("[%s] %s purchased: %s for %.2f %n", marketAgent.getLocalName(), accept.getSender(), products, price);
        ACLMessage inform = accept.createReply();
        inform.setPerformative(INFORM);
        inform.setContent(String.valueOf(price));
        return inform;
    }

    @Override
    protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
        System.out.printf("[%s] %s didn't make a purchase %n", marketAgent.getLocalName(), reject.getSender());
    }
}
