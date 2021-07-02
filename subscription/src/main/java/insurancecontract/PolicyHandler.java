package insurancecontract;

import insurancecontract.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{
    @Autowired SubscriptionRepository subscriptionRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaymentCancelled_ConfirmCancel(@Payload PaymentCancelled paymentCancelled){

        if(!paymentCancelled.validate()) return;

        System.out.println("\n\n##### listener ConfirmCancel : " + paymentCancelled.toJson() + "\n\n");

        // Sample Logic //
        Subscription subscription = new Subscription();
        subscriptionRepository.save(subscription);
            
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverUnderwriterAssignned_ConfirmUnderwriterId(@Payload UnderwriterAssignned underwriterAssignned){

        if(!underwriterAssignned.validate()) return;

        System.out.println("\n\n##### listener ConfirmUnderwriterId : " + underwriterAssignned.toJson() + "\n\n");

        // Sample Logic //
        Subscription subscription = new Subscription();
        subscriptionRepository.save(subscription);
            
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaymentApproved_ConfirmPaymentId(@Payload PaymentApproved paymentApproved){

        if(!paymentApproved.validate()) return;

        System.out.println("\n\n##### listener ConfirmPaymentId : " + paymentApproved.toJson() + "\n\n");

        // Sample Logic //
        Subscription subscription = new Subscription();
        subscriptionRepository.save(subscription);
            
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverSubscriptionRefused_ConfirmRefuse(@Payload SubscriptionRefused subscriptionRefused){

        if(!subscriptionRefused.validate()) return;

        System.out.println("\n\n##### listener ConfirmRefuse : " + subscriptionRefused.toJson() + "\n\n");

        // Sample Logic //
        Subscription subscription = new Subscription();
        subscriptionRepository.save(subscription);
            
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
