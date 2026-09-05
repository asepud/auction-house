package com.asdin.test_rest.integration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.util.Map;
/** REST implementation of the notification-service contract. */
@Component
public class RestNotificationClient implements NotificationClient {
 private final RestClient client; private final String internalKey;
 public RestNotificationClient(RestClient.Builder builder,@Value("${services.notification.url}") String url,@Value("${services.internal-key}") String key){client=builder.baseUrl(url).build();internalKey=key;}
 public void outbid(Long userId,String message){send("/api/notify/outbid",userId,message);} public void winner(Long userId,String message){send("/api/notify/winner",userId,message);} public void ended(Long userId,String message){send("/api/notify/ended",userId,message);}
 private void send(String path,Long userId,String message){client.post().uri(path).header("X-Internal-Key",internalKey).body(Map.of("userId",userId,"message",message)).retrieve().toBodilessEntity();}
}
