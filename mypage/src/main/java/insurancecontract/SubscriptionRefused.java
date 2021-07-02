package insurancecontract;

public class SubscriptionRefused extends AbstractEvent {

    private Long underwritingId;
    private String underwritingStatus;
    private Long underwriterId;
    private Long subscriptionId;
    private Long paymentId;

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