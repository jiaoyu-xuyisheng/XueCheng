package com.rabbitmq.produce2;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@SpringBootTest
class Produce2ApplicationTests {

    private static final String QUEUE = "hello world";

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
            //param1:队列名称,param2:是否持久化,param3:队列是否独占此连接 , param4:队列不再使用时是否自动删除此队列 ,param5:队列参数)
            channel.queueDeclare(QUEUE,true,false,false,null);
            String message = "hello world 小明"+System.currentTimeMillis();
            channel.basicPublish("",QUEUE,null,message.getBytes());
            System.out.println("send message is success");


        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
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
