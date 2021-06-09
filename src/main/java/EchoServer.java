import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class EchoServer {
    private final int port;

    public EchoServer() {
        this.port = 8888;
    }

    public static void main(String[] args) throws Exception {
        new EchoServer().start();   // 서버의 start() 메소드 호출
    }

    private void start() throws Exception {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        EventLoopGroup group = new NioEventLoopGroup(); // EventLoopGroup 생성
        try {
            ServerBootstrap b = new ServerBootstrap();  // ServerBootstrap 생성
            b.group(group)
                    .channel(NioServerSocketChannel.class)  // NIO 전송채널을 이용하도록 지정
                    .localAddress(new InetSocketAddress(port))  // 지정된 포트로 소켓 주소 설정
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception { // EchoServerHandler 하나를 채널의 Channel Pipeline 으로 추가
                            ch.pipeline().addLast(serverHandler);
                        }
                    });
            ChannelFuture f = b.bind().sync();  // 서버를 비동기식으로 바인딩
            f.channel().closeFuture().sync();   // 채널의 CloseFuture를 얻고 완료될 때까지 현재 스레드를 블로킹
        } finally {
            group.shutdownGracefully().sync();  // EventLoopGroup을 종료하고 모든 리소스 해제
        }
    }
}