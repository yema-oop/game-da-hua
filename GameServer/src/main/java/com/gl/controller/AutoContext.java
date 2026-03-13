package com.gl.controller;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutor;

import java.net.SocketAddress;
import java.util.Date;

/**
 * 这是一个用于“伪造”Netty的 ChannelHandlerContext 的类。
 * 在没有真实网络连接的情况下，可以使用 AutoContext 在服务器内部执行逻辑。
 */
public class AutoContext implements ChannelHandlerContext {



    @Override
    public Channel channel() {
        // 没有真实连接，返回 null
        return null;
    }

    @Override
    public EventExecutor executor() {
        return null;
    }

    @Override
    public String name() {
        return "AutoContext";
    }

    @Override
    public ChannelHandler handler() {
        return null;
    }

    @Override
    public boolean isRemoved() {
        return false;
    }


    @Override
    public ChannelHandlerContext fireChannelRegistered() {
        return this;
    }

    @Override
    public ChannelHandlerContext fireChannelUnregistered() {
        return this;
    }

    @Override
    public ChannelHandlerContext fireChannelActive() {
        return this;
    }

    @Override
    public ChannelHandlerContext fireChannelInactive() {
        return this;
    }

    @Override
    public ChannelHandlerContext fireExceptionCaught(Throwable cause) {
        return this;
    }

    @Override
    public ChannelHandlerContext fireUserEventTriggered(Object evt) {
        return this;
    }

    @Override
    public ChannelHandlerContext fireChannelRead(Object msg) {
        return this;
    }

    @Override
    public ChannelHandlerContext fireChannelReadComplete() {
        return this;
    }

    @Override
    public ChannelHandlerContext fireChannelWritabilityChanged() {
        return this;
    }


    @Override
    public ChannelFuture bind(SocketAddress localAddress) {
        return null;
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress) {
        return null;
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
        return null;
    }

    @Override
    public ChannelFuture disconnect() {
        return null;
    }

    @Override
    public ChannelFuture close() {
        return null;
    }

    @Override
    public ChannelFuture deregister() {
        return null;
    }

    @Override
    public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
        return null;
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
        return null;
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
        return null;
    }

    @Override
    public ChannelFuture disconnect(ChannelPromise promise) {
        return null;
    }

    @Override
    public ChannelFuture close(ChannelPromise promise) {
        return null;
    }

    @Override
    public ChannelFuture deregister(ChannelPromise promise) {
        return null;
    }

    @Override
    public ChannelHandlerContext read() {
        return this;
    }

    @Override
    public ChannelFuture write(Object msg) {
        return null;
    }

    @Override
    public ChannelFuture write(Object msg, ChannelPromise promise) {
        return null;
    }

    @Override
    public ChannelHandlerContext flush() {
        return this;
    }

    @Override
    public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
        return null;
    }

    @Override
    public ChannelFuture writeAndFlush(Object msg) {
        return null;
    }

    /** Promise / Future 相关都返回 null */

    @Override
    public ChannelPromise newPromise() {
        return null;
    }

    @Override
    public ChannelProgressivePromise newProgressivePromise() {
        return null;
    }

    @Override
    public ChannelFuture newSucceededFuture() {
        return null;
    }

    @Override
    public ChannelFuture newFailedFuture(Throwable cause) {
        return null;
    }

    @Override
    public ChannelPromise voidPromise() {
        return null;
    }

    @Override
    public ChannelPipeline pipeline() {
        return null;
    }

    @Override
    public ByteBufAllocator alloc() {
        return null;
    }


    @Override
    public <T> Attribute<T> attr(AttributeKey<T> key) {
        return null;
    }

    @Override
    public <T> boolean hasAttr(AttributeKey<T> key) {
        return false;
    }
}
