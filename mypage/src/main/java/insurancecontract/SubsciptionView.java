package insurancecontract;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="SubsciptionView_table")
public class SubsciptionView {

        @Id
        @GeneratedValue(strategy=GenerationType.AUTO)
        private Long subscriptionId;
        private String subscriptionStatus;
        private String productName;
        private Long paymentId;
        private String paymentStatus;
        private Long underwritingId;
        private String underwritingStatus;
        private Long underwriterId;


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
        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
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

}
