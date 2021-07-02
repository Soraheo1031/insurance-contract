package insurancecontract;

public class UnderwriterIdConfirmed extends AbstractEvent {

    private Long subscriptionId;
    private String subscriptionStatus;
    private Long paymentId;
    private Long underwritingId;
    private String productName;

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