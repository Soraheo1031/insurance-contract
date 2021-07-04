package insurancecontract;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Underwriting_table")
public class Underwriting {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long underwritingId;
    private String underwritingStatus;
    private Long underwriterId;
    private Long subscriptionId;
    private Long paymentId;

    @PostPersist
    public void onPostPersist(){
        if("underwriterAssiged".equals(this.getUnderwritingStatus())) {
        UnderwriterAssignned underwriterAssignned = new UnderwriterAssignned();
        BeanUtils.copyProperties(this, underwriterAssignned);
        underwriterAssignned.publishAfterCommit();
        }
    }

    @PostUpdate
    public void onPostUpdate(){
        
        if("underwriterAssiged".equals(this.getUnderwritingStatus())) {
            SubscriptionContracted subscriptionContracted = new SubscriptionContracted();
            BeanUtils.copyProperties(this, subscriptionContracted);
            subscriptionContracted.publishAfterCommit();
        }

        if("refuseSubscription".equals(this.getUnderwritingStatus())) {
            SubscriptionRefused subscriptionRefused = new SubscriptionRefused();
            BeanUtils.copyProperties(this, subscriptionRefused);
            subscriptionRefused.publishAfterCommit();
        }


    }

    @PrePersist
    public void onPrePersist(){
    }


    public Long getUnderwritingId() {
        return underwritingId;
    }

    public void setUnderwritingId(Long underwritingId) {
        this.underwritingId = underwritingId;
    }
    public String getUnderwritingStatus() {
        return underwritingStatus;
    }

    public void setUnderwritingStatus(String underwritingStatus) {
        this.underwritingStatus = underwritingStatus;
    }
    public Long getUnderwriterId() {
        return underwriterId;
    }

    public void setUnderwriterId(Long underwriterId) {
        this.underwriterId = underwriterId;
    }
    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }
    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }




}
