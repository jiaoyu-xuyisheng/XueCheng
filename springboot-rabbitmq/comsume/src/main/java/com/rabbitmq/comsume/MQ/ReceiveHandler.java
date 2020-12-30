package com.rabbitmq.comsume.MQ;


import com.rabbitmq.client.Channel;
import com.rabbitmq.comsume.config.ConsumeConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ReceiveHandler {

    @RabbitListener(queues = {ConsumeConfig.QUEUE_INFORM_EMS})
    public void receiveEmail(String msg, Message message, Channel channel){
        System.out.println(msg);
    }

    @RabbitListener(queues = {ConsumeConfig.QUEUE_INFORM_SMS})
    public void receiveSms(String msg, Message message, Channel channel){
        System.out.println(msg);
    }
}
