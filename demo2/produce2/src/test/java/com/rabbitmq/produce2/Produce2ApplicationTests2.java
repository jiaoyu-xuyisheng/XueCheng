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
class Produce2ApplicationTests2 {
    //两通道的名字
    private static final String QUEUE_INFORM_EMS = "queue_inform_email";
    private static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    //交换机的名字
    private static final String EXCHANGE_FANOUT_INFORM = "exchange_fanout_inform";
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
            channel.exchangeDeclare(EXCHANGE_FANOUT_INFORM, BuiltinExchangeType.FANOUT);
           //声明队列
            channel.queueDeclare(QUEUE_INFORM_EMS,true,false,false,null);
            channel.queueDeclare(QUEUE_INFORM_SMS,true,false,false,null);
            //绑定队列
            channel.queueBind(QUEUE_INFORM_EMS,EXCHANGE_FANOUT_INFORM,"");
            channel.queueBind(QUEUE_INFORM_SMS,EXCHANGE_FANOUT_INFORM,"");
            //发5条消息
            for (int i=0;i<5;i++) {
                String message="inform to user"+i;
                //向交换机发送消息
                //* 1、交换机名称，不指令使用默认交换机名称 Default Exchange *
                // 2、routingKey（路由key），根据key名称将消息转发到具体的队列，这里填写队列名称表示消 息将发到此队列
                // 3、消息属性 * 4、消息内容
                channel.basicPublish(EXCHANGE_FANOUT_INFORM,"",null,message.getBytes());
                System.out.println("send message is : "+message);
            }
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
