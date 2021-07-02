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
    @Autowired UnderwritingRepository underwritingRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverSubscriptionCreated_RegisterUnderwriting(@Payload SubscriptionCreated subscriptionCreated){

        if(!subscriptionCreated.validate()) return;

        System.out.println("\n\n##### listener RegisterUnderwriting : " + subscriptionCreated.toJson() + "\n\n");

        // Sample Logic //
        Underwriting underwriting = new Underwriting();
        underwritingRepository.save(underwriting);
            
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
