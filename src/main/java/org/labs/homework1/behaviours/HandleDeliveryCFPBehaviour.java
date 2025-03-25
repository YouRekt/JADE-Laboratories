//package org.labs.homework1.behaviours;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.json.JsonMapper;
//import jade.core.Agent;
//import jade.core.behaviours.CyclicBehaviour;
//import jade.core.behaviours.DataStore;
//import jade.lang.acl.ACLMessage;
//import jade.proto.AchieveREInitiator;
//import org.labs.homework1.agents.DeliveryAgent;
//
//import java.util.List;
//
//import static jade.lang.acl.ACLMessage.PROPOSE;
//
//public class HandleDeliveryCFPBehaviour extends AchieveREInitiator {
//    private final DeliveryAgent deliveryAgent;
//    static final ObjectMapper mapper = new JsonMapper();
//
//    public HandleDeliveryCFPBehaviour(DeliveryAgent a) {
//        super(a, );
//        deliveryAgent = a;
//    }
//
//    private
//
//    @Override
//    public void action() {
////        System.out.printf("[%s] Cyclic CFP Handle %n", deliveryAgent.getLocalName());
//        DataStore dataStore = getDataStore();
//        ACLMessage cfp = (ACLMessage) dataStore.get(CFP_KEY);
//        if (!deliveryAgent.isOrderReceived()) {
//            List<String> order = null;
//            try {
//                //noinspection unchecked
//                order = (List<String>) mapper.readValue(cfp.getContent(), List.class);
//            } catch (JsonProcessingException e) {
//                throw new RuntimeException(e);
//            }
//            System.out.printf("[%s] received order: %s %n", deliveryAgent.getLocalName(), order);
//            deliveryAgent.setOrder(order);
//            deliveryAgent.setOrderReceived(true);
//
//            myAgent.addBehaviour(new SearchMarketsBehaviour(deliveryAgent));
//        }
//        if (deliveryAgent.isPriceReceived() && !deliveryAgent.isUserNotified()) {
//            Double finalCost = deliveryAgent.getMarketPrice() + deliveryAgent.getDeliveryFee();
//            ACLMessage propose = cfp.createReply();
//            propose.setPerformative(PROPOSE);
//            propose.setContent(String.valueOf(finalCost));
//            System.out.printf("[%s] proposes a cost: %.2f %n", deliveryAgent.getLocalName(), finalCost);
//            deliveryAgent.setUserNotified(true);
//            dataStore.put(REPLY_KEY,propose);
//            myAgent.send(propose);
//
//            //myAgent.removeBehaviour(this);
//        }
//    }
//}
