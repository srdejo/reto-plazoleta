package co.com.srdejo.plazoleta.domain.spi;

public interface INotificationPort {

    void notifyOrderReady(Long customerId, Long orderId, String pin);
}
