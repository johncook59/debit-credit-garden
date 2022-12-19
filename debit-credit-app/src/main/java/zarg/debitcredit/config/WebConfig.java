package zarg.debitcredit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import zarg.debitcredit.converters.CustomerToCustomerResponseConverter;
import zarg.debitcredit.converters.TransactionToTransactionResponseConverter;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new CustomerToCustomerResponseConverter());
        registry.addConverter(new TransactionToTransactionResponseConverter());
    }
}