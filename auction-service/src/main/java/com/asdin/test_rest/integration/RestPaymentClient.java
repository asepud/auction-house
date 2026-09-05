package com.asdin.test_rest.integration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.math.BigDecimal;
import java.util.Map;
/** REST implementation of the payment-service contract. */
@Component
public class RestPaymentClient implements PaymentClient {
 private final RestClient client; private final String internalKey;
 public RestPaymentClient(RestClient.Builder builder,@Value("${services.payment.url}") String url,@Value("${services.internal-key}") String key){client=builder.baseUrl(url).build();internalKey=key;}
 public void createInvoice(Long itemId,Long winnerId,BigDecimal amount){client.post().uri("/api/invoices").header("X-Internal-Key",internalKey).body(Map.of("itemId",itemId,"winnerId",winnerId,"amount",amount)).retrieve().toBodilessEntity();}
}
