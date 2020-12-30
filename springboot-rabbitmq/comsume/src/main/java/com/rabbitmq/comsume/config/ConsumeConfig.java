package com.rabbitmq.comsume.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

public class ConsumeConfig {

    //两队列的名字
    public static final String QUEUE_INFORM_EMS = "queue_inform_email";
    public static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    //通配符的规则
    public static final String ROUTING_KEY_EMS = "inform.#.email.#";//#号可以配一个或多个字符串
    public static final String ROUTING_KEY_SMS = "inform.#.sms.#";//#号可以配一个或多个字符串
    //交换机的名字
    public static final String EXCHANGE_TOPICS_INFORM = "exchange_topics_inform";


    //声明交换机
    @Bean(EXCHANGE_TOPICS_INFORM)
    public Exchange ExchangeTopic(){
        return ExchangeBuilder.topicExchange(EXCHANGE_TOPICS_INFORM).durable(true).build();
    };


    //声明队列 email
    @Bean(QUEUE_INFORM_EMS)
    public Queue QUEUE_EMS(){
        return new Queue(QUEUE_INFORM_EMS);
    };

    //声明队列 sms
    @Bean(QUEUE_INFORM_SMS)
    public Queue QUEUE_SMS(){
        return new Queue(QUEUE_INFORM_SMS);
    };


    //绑定交换机和对列
    @Bean
    public Binding BING_QUEUE_SMS(@Qualifier(QUEUE_INFORM_SMS) Queue queue,
                                  @Qualifier(EXCHANGE_TOPICS_INFORM) Exchange exchange
    )
    {
        return BindingBuilder.bind(queue).to(exchange)
                .with("inform.#.sms.#").noargs();
    }

    //绑定交换机和对列
    @Bean
    public Binding BING_QUEUE_EMS(@Qualifier(QUEUE_INFORM_EMS) Queue queue,
                                  @Qualifier(EXCHANGE_TOPICS_INFORM) Exchange exchange
    )
    {
        return BindingBuilder.bind(queue).to(exchange)
                .with("inform.#.email.#").noargs();
    }


}
