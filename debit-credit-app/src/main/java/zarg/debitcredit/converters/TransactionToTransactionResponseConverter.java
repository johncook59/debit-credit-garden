package zarg.debitcredit.converters;

import org.springframework.core.convert.converter.Converter;
import zarg.debitcredit.controllers.TransactionResponse;
import zarg.debitcredit.domain.Transaction;

public class TransactionToTransactionResponseConverter implements Converter<Transaction, TransactionResponse> {
    @Override
    public TransactionResponse convert(Transaction transaction) {
        return TransactionResponse.builder()
                .bid(transaction.getBid())
                .direction(transaction.getDirection().name())
                .amount(transaction.getAmount())
                .userId(transaction.getUserBid())
                .balance(transaction.getBalance())
                .accountId(transaction.getAccountBid())
                .processed(transaction.getProcessed())
                .build();
    }
}
