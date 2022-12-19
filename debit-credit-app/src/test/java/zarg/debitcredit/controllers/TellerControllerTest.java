package zarg.debitcredit.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
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
import zarg.debitcredit.domain.TransactionDirection;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static zarg.debitcredit.controllers.ControllerTestUtils.NOT_OWNER;
import static zarg.debitcredit.controllers.ControllerTestUtils.OWNER;
import static zarg.debitcredit.controllers.ControllerTestUtils.REGISTER_CUSTOMER_REQUEST;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class TellerControllerTest {

    private static final String DEBIT_REQUEST = "{\"amount\": 1.00}";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final Random random = new Random();

    @Test
    void shouldReadBalanceForOwnedaccounts() throws Exception {
        CustomerResponse customer = createCustomer(OWNER);
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.get(String.format("/teller/%s/%s/balance", customer.bid(), customer.accounts().get(0))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        BalanceResponse balanceResponse = objectMapper.readValue(result.getResponse().getContentAsString(), BalanceResponse.class);
        assertThat(balanceResponse.accountId()).isEqualTo(customer.accounts().get(0));
        assertThat(balanceResponse.balance()).isEqualTo(new BigDecimal("10.00"));
    }

    @Test
    void shouldFailReadBalanceForAccountByAnotherCustomer() throws Exception {
        CustomerResponse owner = createCustomer(OWNER);
        CustomerResponse mal = createCustomer(NOT_OWNER);
        this.mvc.perform(MockMvcRequestBuilders.get(String.format("/teller/%s/%s/balance", mal.bid(), owner.accounts().get(0))))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void shouldCreditAccountWhenOwnerRequests() throws Exception {
        CustomerResponse customer = createCustomer(OWNER);
        String request = createCreditRequest(customer.accounts().get(0));
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.put(
                String.format("/teller/%s/credit", customer.bid()))
                .content(request)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        TransactionResponse transactionResponse = objectMapper.readValue(result.getResponse().getContentAsString(), TransactionResponse.class);
        assertThat(transactionResponse.direction()).isEqualTo(TransactionDirection.CREDIT.name());
        assertThat(transactionResponse.userId()).isEqualTo(customer.bid());
        assertThat(transactionResponse.accountId()).isEqualTo(customer.accounts().get(0));
        assertThat(transactionResponse.balance()).isEqualTo(new BigDecimal("11.00"));
        assertThat(transactionResponse.amount()).isEqualTo(new BigDecimal("1.00"));
    }

    @Test
    void shouldFailToCreditAccountWhenOwnerAmountisNegative() throws Exception {
        CustomerResponse customer = createCustomer(OWNER);
        String request = String.format("{\"accountId\": \"%s\", \"amount\": -1.00}", (customer.accounts().get(0)));
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.put(
                                String.format("/teller/%s/credit", customer.bid()))
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        assertThat("{\"amount\":\"must be greater than or equal to 0.01\"}")
                .isEqualTo(result.getResponse().getContentAsString());
    }

    @Test
    void shouldCreditAccountWhenAnyoneRequests() throws Exception {
        CustomerResponse customer = createCustomer(OWNER);
        CustomerResponse anyone = createCustomer(NOT_OWNER);
        String request = createCreditRequest(customer.accounts().get(0));

        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.put(String.format("/teller/%s/credit", anyone.bid()))
                .content(request)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        TransactionResponse transactionResponse = objectMapper.readValue(result.getResponse().getContentAsString(), TransactionResponse.class);
        assertThat(transactionResponse.direction()).isEqualTo(TransactionDirection.CREDIT.name());
        assertThat(transactionResponse.userId()).isEqualTo(anyone.bid());
        assertThat(transactionResponse.accountId()).isEqualTo(customer.accounts().get(0));
        assertThat(transactionResponse.balance()).isEqualTo(new BigDecimal("11.00"));
        assertThat(transactionResponse.amount()).isEqualTo(new BigDecimal("1.00"));
    }

    @Test
    void shouldDebitAccountWhenOwnerRequests() throws Exception {
        CustomerResponse owner = createCustomer(OWNER);

        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.put(String.format("/teller/%s/%s/debit", owner.bid(), owner.accounts().get(0)))
                .content(DEBIT_REQUEST)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        TransactionResponse transactionResponse = objectMapper.readValue(result.getResponse().getContentAsString(), TransactionResponse.class);
        assertThat(transactionResponse.direction()).isEqualTo(TransactionDirection.DEBIT.name());
        assertThat(transactionResponse.userId()).isEqualTo(owner.bid());
        assertThat(transactionResponse.accountId()).isEqualTo(owner.accounts().get(0));
        assertThat(transactionResponse.balance()).isEqualTo(new BigDecimal("9.00"));
        assertThat(transactionResponse.amount()).isEqualTo(new BigDecimal("1.00"));
    }

    @Test
    void shouldFailToDebitAccountWhenAnyoneRequests() throws Exception {
        CustomerResponse owner = createCustomer(OWNER);
        CustomerResponse mal = createCustomer(NOT_OWNER);

        this.mvc.perform(MockMvcRequestBuilders.put(String.format("/teller/%s/%s/debit", mal.bid(), owner.accounts().get(0)))
                .content(DEBIT_REQUEST)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void shouldFindTransactions() throws Exception {
        CustomerResponse owner = createCustomer(OWNER);
        String request = createCreditRequest(owner.accounts().get(0));
        this.mvc.perform(MockMvcRequestBuilders.put(
                String.format("/teller/%s/credit", owner.bid()))
                .content(request)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.get("/teller/" + owner.bid() + "/transactions")
                .param("from", dateTimeFormatter.format(now.minus(1, ChronoUnit.DAYS)))
                .param("to", dateTimeFormatter.format(now)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        Map<String, List<TransactionResponse>> transactions = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {});

        String expectedAccountId = owner.accounts().get(0);

        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(expectedAccountId)).hasSize(1);
        assertThat(transactions.get(expectedAccountId).get(0).userId()).isEqualTo(owner.bid());
        assertThat(transactions.get(expectedAccountId).get(0).balance()).isEqualTo(new BigDecimal("11.00"));
        assertThat(transactions.get(expectedAccountId).get(0).amount()).isEqualTo(new BigDecimal("1.00"));
        assertThat(transactions.get(expectedAccountId).get(0).balance()).isEqualTo(new BigDecimal("11.00"));
        assertThat(transactions.get(expectedAccountId).get(0).direction()).isEqualTo(TransactionDirection.CREDIT.name());
    }

    private CustomerResponse createCustomer(String name) throws Exception {
        String request = String.format(REGISTER_CUSTOMER_REQUEST, name, name, name + random.nextInt(1000) + "@somewhere.com");
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.post("/customer")
                .content(request)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(), CustomerResponse.class);
    }

    private String createCreditRequest(String accountId) {
        return String.format("{\"accountId\": \"%s\", \"amount\": 1.00}", accountId);
    }
}