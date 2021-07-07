package insurancecontract.external;

public class PaymentServiceFallback implements PaymentService{
    @Override
    public boolean approvePayment(Payment payment) {
        System.out.println("Circuit breaker has been opened. Fallback returned instead.");
        return false;
    }
}