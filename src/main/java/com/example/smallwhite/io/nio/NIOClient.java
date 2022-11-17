package com.example.smallwhite.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * NIO客户端
 * @author yangqiang
 */
public class NIOClient {

    // 通道管理器(Selector)
    private static Selector selector;

    public static void main(String[] args) throws IOException {
    }

}
