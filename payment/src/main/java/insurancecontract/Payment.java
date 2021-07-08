package insurancecontract;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Payment_table")
public class Payment {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long paymentId;
    private String paymentStatus;
    private Long subscriptionId;

    @PostPersist
    public void onPostPersist(){
        
        // circuit break 테스트를 위한 임의 부하 처리
        /* try {
            Thread.currentThread().sleep((long) (400 + (Math.random() * 220)));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/ 
        ////////////////////////////
        // 결제 승인 된 경우
        ////////////////////////////
        // 이벤트 발행 -> PaymentApproved
        PaymentApproved paymentApproved = new PaymentApproved();
        BeanUtils.copyProperties(this, paymentApproved);
        paymentApproved.publishAfterCommit();
    }

    @PostUpdate
    public void onPostUpdate(){
        if("cancelled".equals(this.getPaymentStatus())
         || "cancelled_refused".equals(this.getPaymentStatus())) {
        PaymentCancelled paymentCancelled = new PaymentCancelled();
        BeanUtils.copyProperties(this, paymentCancelled);
        paymentCancelled.publishAfterCommit();
        }


    }


    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }
    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }




}
