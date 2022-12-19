package zarg.debitcredit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Random;

import static zarg.debitcredit.controllers.ControllerTestUtils.REGISTER_CUSTOMER_REQUEST;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final Random random = new Random();

    private CustomerResponse customer;

    @BeforeEach
    public void setUp() throws Exception {
        String request = String.format(REGISTER_CUSTOMER_REQUEST, "the", "customer", "email" + random.nextInt(1000) + "@somewhere.com");
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.post("/customer")
                .content(request)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        customer = objectMapper.readValue(result.getResponse().getContentAsString(), CustomerResponse.class);
    }

    @Test
    void findByCustomerId() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders.get("/customer")
                .param("customerId", customer.bid())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
}