package com.example.demo.socket;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.dto.User;
import com.example.demo.handler.SimpleClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.AttributeKey;

/**
 * Demo class
 *
 * @author huangjing
 * @date 2019-08-04
 */
public class NettyClient {
    public static void main(String[] args) throws InterruptedException {
        // 首先，netty通过ServerBootstrap启动服务端
        Bootstrap client = new Bootstrap();

        //第1步 定义线程组，处理读写和链接事件，没有了accept事件
        EventLoopGroup group = new NioEventLoopGroup();
        client.group(group );

        //第2步 绑定客户端通道
        client.channel(NioSocketChannel.class);

        //第3步 给NIoSocketChannel初始化handler， 处理读写事件
        client.handler(new ChannelInitializer<NioSocketChannel>() {  //通道是NioSocketChannel
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                // //protobuf支持
                // //采用Base 128 Varints进行编码，在消息头上加上32个整数，来标注数据的长度。
                // ch.pipeline().addLast("protobufVarint32FrameDecoder", new ProtobufVarint32FrameDecoder());
                // ch.pipeline().addLast("protobufDecoder", new ProtobufDecoder(PersonsBook.AddressBook.getDefaultInstance()));
                // //对采用Base 128 Varints进行编码的数据解码
                // ch.pipeline().addLast("protobufVarint32LengthFieldPrepender", new ProtobufVarint32LengthFieldPrepender());
                // ch.pipeline().addLast("protobufEncoder", new ProtobufEncoder());
                //字符串编码器，一定要加在SimpleClientHandler 的上面
                ch.pipeline().addLast(new StringEncoder());
                ch.pipeline().addLast(new DelimiterBasedFrameDecoder(
                        Integer.MAX_VALUE, Delimiters.lineDelimiter()[0]));
                //找到他的管道 增加他的handler
                ch.pipeline().addLast(new SimpleClientHandler());
            }
        });

        //连接服务器
        ChannelFuture future = client.connect("localhost", 8000).sync();

        //发送数据给服务器
        User user = new User();
        user.setAge(12);
        user.setId(1L);
        user.setName("jim");
        future.channel().writeAndFlush(JSONObject.toJSONString(user)+"\r\n");

        for(int i=0;i<5;i++){
            String msg = "ssss"+i+"\r\n";
            future.channel().writeAndFlush(msg);
        }

        //当通道关闭了，就继续往下走
        future.channel().closeFuture().sync();

        //接收服务端返回的数据
        AttributeKey<String> key = AttributeKey.valueOf("ServerData");
        Object result = future.channel().attr(key).get();
        System.out.println(result.toString());
    }
}
