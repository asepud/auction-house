package com.asdin.test_rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.Instant;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Verifies registration, authenticated item creation, and a valid bid via HTTP. */
@SpringBootTest
@AutoConfigureMockMvc
class AuctionFlowIntegrationTests {
    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper json;

    @Test
    void sellerCanCreateOngoingItemAndBidderCanBid() throws Exception {
        String suffix = String.valueOf(System.nanoTime());
        String sellerToken = register("seller" + suffix + "@test.local", "SELLER");
        String bidderToken = register("bidder" + suffix + "@test.local", "BIDDER");
        String item = "{\"title\":\"Camera\",\"description\":\"Test item\",\"categoryId\":1,"
                + "\"startingPrice\":100,\"startTime\":\"" + Instant.now().minusSeconds(30)
                + "\",\"endTime\":\"" + Instant.now().plusSeconds(3600) + "\"}";
        String itemBody = mvc.perform(post("/api/items").header("Authorization", "Bearer " + sellerToken)
                .contentType(MediaType.APPLICATION_JSON).content(item)).andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ONGOING")).andReturn().getResponse().getContentAsString();
        long itemId = json.readTree(itemBody).get("id").asLong();
        mvc.perform(get("/api/items?status=ONGOING"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(itemId));
        mvc.perform(post("/api/items/{id}/bids", itemId).header("Authorization", "Bearer " + bidderToken)
                .contentType(MediaType.APPLICATION_JSON).content("{\"amount\":105}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.amount").value(105));
    }

    private String register(String email, String role) throws Exception {
        String body = "{\"name\":\"Test User\",\"email\":\"" + email
                + "\",\"password\":\"Password123!\",\"role\":\"" + role + "\"}";
        String response = mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn().getResponse().getContentAsString();
        return json.readTree(response).get("token").asText();
    }
}
