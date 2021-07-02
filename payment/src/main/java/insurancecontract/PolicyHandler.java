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
    @Autowired PaymentRepository paymentRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverCancelRequested_CancelPayment(@Payload CancelRequested cancelRequested){

        if(!cancelRequested.validate()) return;

        System.out.println("\n\n##### listener CancelPayment : " + cancelRequested.toJson() + "\n\n");

        // Sample Logic //
        Payment payment = new Payment();
        paymentRepository.save(payment);
            
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverSubscriptionRefused_CancelPayment(@Payload SubscriptionRefused subscriptionRefused){

        if(!subscriptionRefused.validate()) return;

        System.out.println("\n\n##### listener CancelPayment : " + subscriptionRefused.toJson() + "\n\n");

        // Sample Logic //
        Payment payment = new Payment();
        paymentRepository.save(payment);
            
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
