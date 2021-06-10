import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class NettyNioServer {
    public static void main(String[] args) throws Exception{
        int port = 8888;
        final ByteBuf buf = Unpooled.copiedBuffer("Hi! \n", Charset.forName("UTF-8"));
        EventLoopGroup group = new NioEventLoopGroup();    // 논블로킹 모드를 위해 NioEventGroup을 이용
        try {
            ServerBootstrap b = new ServerBootstrap();    // ServerBootstrap 생성
            b.group(group).channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {    // 연결이 수락될 때마다 호출될 ChannelInitializer를 지정
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){    // 이벤트를 수신하고 처리할 ChannelInboundHandlerAdapter를 추가
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    ctx.writeAndFlush(buf.duplicate())
                                            .addListener(ChannelFutureListener.CLOSE);
                                }
                            });
                        }
                    });
            ChannelFuture f = b.bind().sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}