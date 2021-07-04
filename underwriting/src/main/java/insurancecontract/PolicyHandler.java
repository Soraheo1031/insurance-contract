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
    public void wheneverPaymentIdConfirmed_RegisterUnderwriting(@Payload PaymentIdConfirmed paymentIdConfirmed){

        if(!paymentIdConfirmed.validate()) return;

        System.out.println("\n\n##### listener RegisterUnderwriting : " + paymentIdConfirmed.toJson() + "\n\n");

        long subscriptionId = paymentIdConfirmed.getSubscriptionId(); // 심사자배정할 청약Id
        long paymentId = paymentIdConfirmed.getPaymentId(); // 결제Id
        long underwriterId = Long.valueOf("77001520"); // 심사자Id

        Underwriting underwriting = new Underwriting();

        underwriting.setSubscriptionId(subscriptionId); // 청약Id
        underwriting.setPaymentId(paymentId); // 결제Id
        underwriting.setUnderwritingStatus("underwriterAssiged"); // 심사자 배정됨
        underwriting.setUnderwriterId(underwriterId); // 심사자 배정됨

        // DB Update
        underwritingRepository.save(underwriting);        
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
