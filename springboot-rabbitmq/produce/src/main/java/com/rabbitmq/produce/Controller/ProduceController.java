package com.rabbitmq.produce.Controller;


import com.rabbitmq.produce.config.RabbitmqConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/produce")
public class ProduceController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @GetMapping("/msg")
    public void SendMsgByTopic(){
        for(int i=0;i<3;i++){
            String message ="sms email inform to user"+i;
            rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_TOPICS_INFORM,"inform.sms.email",message);
            System.out.println("Send Message is:"+message);
        }
    }


}
