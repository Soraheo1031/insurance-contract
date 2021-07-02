package insurancecontract;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Subscription_table")
public class Subscription {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long subscriptionId;
    private String subscriptionStatus;
    private Long paymentId;
    private Long underwritingId;
    private String productName;

    @PostPersist
    public void onPostPersist(){
        // SubscriptionCreated subscriptionCreated = new SubscriptionCreated();
        // BeanUtils.copyProperties(this, subscriptionCreated);
        // subscriptionCreated.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.
        //----------------------------
        // PAYMENT 결제 진행 (POST방식)
        //----------------------------
        insurancecontract.external.Payment payment = new insurancecontract.external.Payment();

        payment.setSubscriptionId(this.getSubscriptionId());
        //payment.setProductName(this.getProductName());
        payment.setPaymentStatus("paid");

        // mappings goes here
        SubscriptionApplication.applicationContext.getBean(insurancecontract.external.PaymentService.class)
            .approvePayment(payment);

        //----------------------------------
        // 이벤트 발행 --> SubscriptionCreated
        //----------------------------------
        SubscriptionCreated subscriptionCreated = new SubscriptionCreated();
        BeanUtils.copyProperties(this, subscriptionCreated);
        subscriptionCreated.publishAfterCommit();
    }

    @PostUpdate
    public void onPostUpdate(){
        
        if("confirmCancel".equals(this.getSubscriptionStatus())) {
            SubscriptionCancelled subscriptionCancelled = new SubscriptionCancelled();
            BeanUtils.copyProperties(this, subscriptionCancelled);
            subscriptionCancelled.publishAfterCommit();
        }

        if("reqCancel".equals(this.getSubscriptionStatus())) {
            CancelRequested cancelRequested = new CancelRequested();
            BeanUtils.copyProperties(this, cancelRequested);
            cancelRequested.publishAfterCommit();
        }

        
        if("confirmUnderwriterId".equals(this.getSubscriptionStatus())) {
            UnderwriterIdConfirmed underwriterIdConfirmed = new UnderwriterIdConfirmed();
            BeanUtils.copyProperties(this, underwriterIdConfirmed);
            underwriterIdConfirmed.publishAfterCommit();
        }

        
        if("confirmPaymentId".equals(this.getSubscriptionStatus())) {
            PaymentIdConfirmed paymentIdConfirmed = new PaymentIdConfirmed();
            BeanUtils.copyProperties(this, paymentIdConfirmed);
            paymentIdConfirmed.publishAfterCommit();
        }

        
        if("refuseRefuse".equals(this.getSubscriptionStatus())) {
            SubscriptionRefused subscriptionRefused = new SubscriptionRefused();
            BeanUtils.copyProperties(this, subscriptionRefused);
            subscriptionRefused.publishAfterCommit();
        }

    }

    @PrePersist
    public void onPrePersist(){
    }


    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }
    public String getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(String subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }
    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }
    public Long getUnderwritingId() {
        return underwritingId;
    }

    public void setUnderwritingId(Long underwritingId) {
        this.underwritingId = underwritingId;
    }
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }




}
