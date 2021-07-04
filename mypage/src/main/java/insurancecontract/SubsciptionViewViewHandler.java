package insurancecontract;

import insurancecontract.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class SubsciptionViewViewHandler {


    @Autowired
    private SubsciptionViewRepository subsciptionViewRepository;

    
    ///////////////////////////////////////////////////
    /// 보험가입-청약이 신청되었을 때 INSERT -> subscriptionView table
    ///////////////////////////////////////////////////
    @StreamListener(KafkaProcessor.INPUT)
    public void whenSubscriptionCreated_then_CREATE_1 (@Payload SubscriptionCreated subscriptionCreated) {
        try {

            if (!subscriptionCreated.validate()) return;

            // view 객체 생성
            SubsciptionView subsciptionView = new SubsciptionView();
            // view 객체에 이벤트의 Value 를 set 함
            subsciptionView.setSubscriptionId(subscriptionCreated.getSubscriptionId());
            subsciptionView.setSubscriptionStatus(subscriptionCreated.getSubscriptionStatus());
            subsciptionView.setProductName(subscriptionCreated.getProductName());
            // view 레파지 토리에 save
            subsciptionViewRepository.save(subsciptionView);
        
        }catch (Exception e){
            e.printStackTrace();
        }
    }


 
    @StreamListener(KafkaProcessor.INPUT)
    public void whenSubscriptionCancelled_then_UPDATE_1(@Payload SubscriptionCancelled subscriptionCancelled) {
        try {
            if (!subscriptionCancelled.validate()) return;
                // view 객체 조회
            Optional<SubsciptionView> subsciptionViewOptional = subsciptionViewRepository.findById(subscriptionCancelled.getSubscriptionId());
            if( subsciptionViewOptional.isPresent()) {
                SubsciptionView subsciptionView = subsciptionViewOptional.get();
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                    subsciptionView.setSubscriptionStatus(subscriptionCancelled.getSubscriptionStatus());
                // view 레파지 토리에 save
                subsciptionViewRepository.save(subsciptionView);
            }
            
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenPaymentApproved_then_UPDATE_2(@Payload PaymentApproved paymentApproved) {
        try {
            if (!paymentApproved.validate()) return;
                // view 객체 조회
        
            System.out.println("\n\n##### listener ApprovedPayment : " + paymentApproved.toJson() + "\n\n");

            Optional<SubsciptionView> subsciptionViewOptional = subsciptionViewRepository.findById(paymentApproved.getSubscriptionId());
            if( subsciptionViewOptional.isPresent()) {
                SubsciptionView subsciptionView = subsciptionViewOptional.get();
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                    subsciptionView.setPaymentId(paymentApproved.getPaymentId());
                    subsciptionView.setPaymentStatus(paymentApproved.getPaymentStatus());
                // view 레파지 토리에 save
                subsciptionViewRepository.save(subsciptionView);
            }
            
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenPaymentCancelled_then_UPDATE_3(@Payload PaymentCancelled paymentCancelled) {
        try {
            if (!paymentCancelled.validate()) return;
                // view 객체 조회
            Optional<SubsciptionView> subsciptionViewOptional = subsciptionViewRepository.findById(paymentCancelled.getSubscriptionId());
            if( subsciptionViewOptional.isPresent()) {
                SubsciptionView subsciptionView = subsciptionViewOptional.get();
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                    subsciptionView.setPaymentStatus(paymentCancelled.getPaymentStatus());
                // view 레파지 토리에 save
                subsciptionViewRepository.save(subsciptionView);
            }
            
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    ///////////////////////////////////////////////////
    /// 청약 접수되어 심사자가 배정되었을 때 UPDATE -> subscriptionView table
    ///////////////////////////////////////////////////
    @StreamListener(KafkaProcessor.INPUT)
    public void whenUnderwriterAssignned_then_UPDATE_4(@Payload UnderwriterAssignned underwriterAssignned) {
        try {
            if (!underwriterAssignned.validate()) return;
                // view 객체 조회
            Optional<SubsciptionView> subsciptionViewOptional = subsciptionViewRepository.findById(underwriterAssignned.getSubscriptionId());
            if( subsciptionViewOptional.isPresent()) {
                SubsciptionView subsciptionView = subsciptionViewOptional.get();
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                    subsciptionView.setUnderwritingId(underwriterAssignned.getUnderwritingId());
                    subsciptionView.setUnderwritingStatus(underwriterAssignned.getUnderwritingStatus());
                    subsciptionView.setUnderwriterId(underwriterAssignned.getUnderwriterId());
                // view 레파지 토리에 save
                subsciptionViewRepository.save(subsciptionView);
            }
            
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenSubscriptionContracted_then_UPDATE_5(@Payload SubscriptionContracted subscriptionContracted) {
        try {
            if (!subscriptionContracted.validate()) return;
                // view 객체 조회
            Optional<SubsciptionView> subsciptionViewOptional = subsciptionViewRepository.findById(subscriptionContracted.getSubscriptionId());
            if( subsciptionViewOptional.isPresent()) {
                SubsciptionView subsciptionView = subsciptionViewOptional.get();
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                    subsciptionView.setUnderwritingStatus(subscriptionContracted.getUnderwritingStatus());
                // view 레파지 토리에 save
                subsciptionViewRepository.save(subsciptionView);
            }
            
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenSubscriptionRefused_then_UPDATE_6(@Payload SubscriptionRefused subscriptionRefused) {
        try {
            if (!subscriptionRefused.validate()) return;
                // view 객체 조회
            Optional<SubsciptionView> subsciptionViewOptional = subsciptionViewRepository.findById(subscriptionRefused.getSubscriptionId());
            if( subsciptionViewOptional.isPresent()) {
                SubsciptionView subsciptionView = subsciptionViewOptional.get();
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                    subsciptionView.setSubscriptionStatus(subscriptionRefused.getSubscriptionStatus());
                // view 레파지 토리에 save
                subsciptionViewRepository.save(subsciptionView);
            }
            
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenCancelRequested_then_UPDATE_7(@Payload CancelRequested cancelRequested) {
        try {
            if (!cancelRequested.validate()) return;
                // view 객체 조회
            Optional<SubsciptionView> subsciptionViewOptional = subsciptionViewRepository.findById(cancelRequested.getSubscriptionId());
            if( subsciptionViewOptional.isPresent()) {
                SubsciptionView subsciptionView = subsciptionViewOptional.get();
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                    subsciptionView.setSubscriptionStatus(cancelRequested.getSubscriptionStatus());
                // view 레파지 토리에 save
                subsciptionViewRepository.save(subsciptionView);
            }
            
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenUnderwriterIdConfirmed_then_UPDATE_8(@Payload UnderwriterIdConfirmed underwriterIdConfirmed) {
        try {
            if (!underwriterIdConfirmed.validate()) return;
                // view 객체 조회
            Optional<SubsciptionView> subsciptionViewOptional = subsciptionViewRepository.findById(underwriterIdConfirmed.getSubscriptionId());
            if( subsciptionViewOptional.isPresent()) {
                SubsciptionView subsciptionView = subsciptionViewOptional.get();
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                subsciptionView.setSubscriptionStatus(underwriterIdConfirmed.getSubscriptionStatus());
                // view 레파지 토리에 save
                subsciptionViewRepository.save(subsciptionView);
            }
            
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenPaymentIdConfirmed_then_UPDATE_9(@Payload PaymentIdConfirmed paymentIdConfirmed) {
        try {
            //System.out.println("\n\n##### listener PaymentIdConfirmed 111");
            if (!paymentIdConfirmed.validate()) return;
            System.out.println("\n\n##### listener PaymentIdConfirmed : " + paymentIdConfirmed.toJson() + "\n\n");

                // view 객체 조회
            Optional<SubsciptionView> subsciptionViewOptional = subsciptionViewRepository.findById(paymentIdConfirmed.getSubscriptionId());
            
            if( subsciptionViewOptional.isPresent()) {
                SubsciptionView subsciptionView = subsciptionViewOptional.get();
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                // subsciptionView.setSubscriptionId(paymentIdConfirmed.getSubscriptionId());
                subsciptionView.setPaymentId(paymentIdConfirmed.getPaymentId());
                subsciptionView.setPaymentStatus("approvePayment");
                subsciptionView.setSubscriptionStatus(paymentIdConfirmed.getSubscriptionStatus());
                // view 레파지 토리에 save
                subsciptionViewRepository.save(subsciptionView);
            }
            
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenPaymentApproved_then_DELETE_1(@Payload PaymentApproved paymentApproved) {
        try {
            if (!paymentApproved.validate()) return;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}