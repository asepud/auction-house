package com.asdin.test_rest.integration;
/** Outbound contract to the separately deployed notification service. */
public interface NotificationClient { void outbid(Long userId, String message); void winner(Long userId, String message); void ended(Long userId, String message); }
