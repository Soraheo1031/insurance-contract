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
    @Autowired SubscriptionRepository subscriptionRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaymentCancelled_ConfirmCancel(@Payload PaymentCancelled paymentCancelled){

        if(!paymentCancelled.validate()) return;

        System.out.println("\n\n##### listener ConfirmCancel : " + paymentCancelled.toJson() + "\n\n");

        long subscriptionId = paymentCancelled.getSubscriptionId(); // 결제취소된 청약Id

        Optional<Subscription> res = subscriptionRepository.findById(subscriptionId);
        Subscription subscription = res.get();

        subscription.setSubscriptionStatus("confirmCancel"); // 취소 상태

        // DB Update
        subscriptionRepository.save(subscription);   
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverUnderwriterAssignned_ConfirmUnderwriterId(@Payload UnderwriterAssignned underwriterAssignned){

        if(!underwriterAssignned.validate()) return;

        System.out.println("\n\n##### listener ConfirmUnderwriterId : " + underwriterAssignned.toJson() + "\n\n");

        long subscriptionId = underwriterAssignned.getSubscriptionId(); // 심사자배정된 청약Id
        long underwritingId = underwriterAssignned.getUnderwritingId(); // 심사Id

        Optional<Subscription> res = subscriptionRepository.findById(subscriptionId);
        Subscription subscription = res.get();

        subscription.setUnderwritingId(underwritingId); // 심사자배정 상태
        subscription.setSubscriptionStatus("confirmUnderwriterId"); // 심사자배정 상태

        // DB Update
        subscriptionRepository.save(subscription);  
            
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaymentApproved_ConfirmPaymentId(@Payload PaymentApproved paymentApproved){

        if(!paymentApproved.validate()) return;

        System.out.println("\n\n##### listener ConfirmPaymentId : " + paymentApproved.toJson() + "\n\n");

        long subscriptionId = paymentApproved.getSubscriptionId(); // 결제승인된 청약Id

        Optional<Subscription> res = subscriptionRepository.findById(subscriptionId);
        Subscription subscription = res.get();

        subscription.setPaymentId(paymentApproved.getPaymentId()); // 결제완료
        subscription.setSubscriptionStatus("confirmPaymentId"); // 결제완료 상태

        // DB Update
        subscriptionRepository.save(subscription);   
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverSubscriptionRefused_ConfirmRefuse(@Payload SubscriptionRefused subscriptionRefused){

        if(!subscriptionRefused.validate()) return;

        System.out.println("\n\n##### listener ConfirmRefuse : " + subscriptionRefused.toJson() + "\n\n");

        long subscriptionId = subscriptionRefused.getSubscriptionId(); // 심사거절된 청약Id

        Optional<Subscription> res = subscriptionRepository.findById(subscriptionId);
        Subscription subscription = res.get();

        subscription.setSubscriptionStatus("confirmRefuse"); // 심사거절 상태

        // DB Update
        subscriptionRepository.save(subscription);  
            
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}

}
