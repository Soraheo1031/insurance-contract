package insurancecontract;

import insurancecontract.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class PolicyHandler{
    @Autowired UnderwritingRepository underwritingRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverSubscriptionCreated_RegisterUnderwriting(@Payload SubscriptionCreated subscriptionCreated){

        if(!subscriptionCreated.validate()) return;

        System.out.println("\n\n##### listener RegisterUnderwriting : " + subscriptionCreated.toJson() + "\n\n");

        long subscriptionId = subscriptionCreated.getSubscriptionId(); // 심사자배정할 청약Id
        long underwriterId = Long.valueOf("77001520"); // 심사자Id

        Optional<Underwriting> res = underwritingRepository.findById(subscriptionId);
        Underwriting underwriting = res.get();

        underwriting.setUnderwritingStatus("assigedUnderwriter"); // 심사자 배정됨
        underwriting.setUnderwriterId(underwriterId); // 심사자 배정됨

        // DB Update
        underwritingRepository.save(underwriting);        
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
