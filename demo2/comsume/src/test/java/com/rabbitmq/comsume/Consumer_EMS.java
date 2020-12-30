package com.rabbitmq.comsume;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer_EMS {
    private static final String QUEUE_INFORM_EMS = "queue_inform_email"; //队列的名字
    private static final String EXCHANGE_FANOUT_INFORM = "exchange_fanout_inform"; //交换机的名字
    public static void main(String[] args) throws IOException, TimeoutException {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("23.234.54.106");//rabbitmq所在的服务器的地址
            factory.setPort(5672);//通信端口
            factory.setUsername("guest");
            factory.setPassword("guest");
            factory.setVirtualHost("/");
            Connection connection=factory.newConnection();//创建与rabbitmq的连接
            Channel channel = connection.createChannel();//创建与Exchange的通道，每个连接可以创建多个通道，每个通道代表一个会话任务
            channel.exchangeDeclare(EXCHANGE_FANOUT_INFORM,BuiltinExchangeType.FANOUT); //创建一个交换机
            //声明队列;param1:队列名称,param2:是否持久化,param3:队列是否独占此连接 , param4:队列不再使用时是否自动删除此队列 ,param5:队列参数)
            channel.queueDeclare(QUEUE_INFORM_EMS,true,false,false,null);
            channel.queueBind(QUEUE_INFORM_EMS,EXCHANGE_FANOUT_INFORM,"");   //* 1、队列名称 * 2、交换机名称 * 3、路由key

            DefaultConsumer  consumer=  new DefaultConsumer(channel){//实现消费方法
              //* 消费者接收消息调用此方法 * @param consumerTag 消费者的标签，在channel.basicConsume()去指定 * @param envelope 消息包的内容，可从中获取消息id，消息routingkey，交换机，消息和重传标志 (收到消息失败后是否需要重新发送) *
               @Override
             public void handleDelivery(String consuerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)throws IOException
               {   String exchange = envelope.getExchange(); //交换机
                   String msg = new String(body,"utf-8"); //消息内容
                   System.out.println(msg); }
           };
//监听队列* 1、队列名称 * 2、是否自动回复，设置为true为表示消息接收到自动向mq回复接收到了，mq接收到回复会删除消息，设置 为false则需要手动回复 * 3、消费消息的方法，消费者接收到消息后调用此方法
            channel.basicConsume(QUEUE_INFORM_EMS,true,consumer);
    }
}

