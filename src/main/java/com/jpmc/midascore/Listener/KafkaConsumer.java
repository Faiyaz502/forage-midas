package com.jpmc.midascore.Listener;

import com.jpmc.midascore.Dto.Incentive;
import com.jpmc.midascore.component.DatabaseConduit;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KafkaConsumer {


    @Autowired
    private DatabaseConduit databaseConduit;

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @Autowired
    private RestTemplate restTemplate;



    @KafkaListener(
            topics = "${general.kafka-topic}",
            groupId = "transaction"
    )
    @Transactional
    public void receiveTransaction(Transaction ts) {


        //For task 2

        System.out.println("Transaction received: " + ts);
        System.out.println("Amount: " + ts.getAmount());

        //Task Other






        UserRecord sender = databaseConduit.findById(ts.getSenderId());

        UserRecord receiver = databaseConduit.findById(ts.getRecipientId());


        if (sender == null || receiver == null) {
            return;
        }



        if(sender.getBalance() < ts.getAmount()){
            return;
        }



        Transaction request = new Transaction();
        request.setSenderId(ts.getSenderId());
        request.setRecipientId(ts.getRecipientId());
        request.setAmount(ts.getAmount());

        ResponseEntity<Transaction> response =
                restTemplate.postForEntity(
                        "http://localhost:8080/incentive",
                        request,
                        Transaction.class
                );

        float incentiveAmount = 0;

        if (response.getBody() != null) {
            incentiveAmount = response.getBody().getAmount();
        }

        //Valid Transacation

        TransactionRecord transaction = new TransactionRecord(ts.getSenderId(), ts.getRecipientId(), ts.getAmount(),incentiveAmount);



        sender.setBalance(sender.getBalance()-ts.getAmount());

        receiver.setBalance(receiver.getBalance()+ts.getAmount()+incentiveAmount);

        databaseConduit.save(sender);
        databaseConduit.save(receiver);



        transactionRecordRepository.save(transaction);



    }


}
