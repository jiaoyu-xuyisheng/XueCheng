package com.rabbitmq.produce2;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@SpringBootTest
class Producer_topics {
    //两队列的名字
    private static final String QUEUE_INFORM_EMS = "queue_inform_email";
    private static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    //通配符的规则
    private static final String ROUTING_KEY_EMS = "inform.#.email.#";//#号可以配一个或多个字符串
    private static final String ROUTING_KEY_SMS = "inform.#.sms.#";//#号可以配一个或多个字符串
    //交换机的名字
    private static final String EXCHANGE_TOPICS_INFORM = "exchange_topics_inform";
    @Test
    void contextLoads() throws IOException, TimeoutException {
        Connection connection=null;
        Channel channel =null;
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("23.234.54.106");//rabbitmq所在的服务器的地址
            factory.setPort(5672);//通信端口
            factory.setUsername("guest"); //输入用户名
            factory.setPassword("guest");//输入密码
            factory.setVirtualHost("/"); //rabbitmq默认虚拟机名称为“/”，虚拟机相当于一个独立的mq服务
            connection=factory.newConnection();//创建与rabbitmq的连接
            channel = connection.createChannel();//创建与Exchange的通道，每个连接可以创建多个通道，每个通道代表一个会话任务
            //* 1、交换机名称 * 2、交换机类型，fanout(模式:publish/subscribe)、topic(模式：Topic)、direct(模式：Routing)、headers(模式：headers)
            channel.exchangeDeclare(EXCHANGE_TOPICS_INFORM, BuiltinExchangeType.TOPIC);
           //声明队列
            channel.queueDeclare(QUEUE_INFORM_EMS,true,false,false,null);
            channel.queueDeclare(QUEUE_INFORM_SMS,true,false,false,null);
            //绑定队列
            channel.queueBind(QUEUE_INFORM_EMS,EXCHANGE_TOPICS_INFORM,ROUTING_KEY_EMS);
            channel.queueBind(QUEUE_INFORM_SMS,EXCHANGE_TOPICS_INFORM,ROUTING_KEY_SMS);
            //发5条消息
            for (int i=0;i<5;i++) { String message="inform is email"+i;
                channel.basicPublish(EXCHANGE_TOPICS_INFORM,"inform.email",null,message.getBytes());
                System.out.println("send message is : "+message); }
            //发5条消息
            for (int i=0;i<5;i++) { String message="inform is sms"+i;
                channel.basicPublish(EXCHANGE_TOPICS_INFORM,"inform.sms",null,message.getBytes());
                System.out.println("send message is : "+message); }
            //发5条消息
            for (int i=0;i<5;i++) { String message="inform is sms and email"+i;
                channel.basicPublish(EXCHANGE_TOPICS_INFORM,"inform.sms.email",null,message.getBytes());
                System.out.println("send message is : "+message); }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(channel!=null){
                channel.close();
            }
            if(connection!=null){
                connection.close();
            }
        }
    }

}
