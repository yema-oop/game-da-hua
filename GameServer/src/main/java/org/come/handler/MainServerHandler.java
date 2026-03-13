package org.come.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

import org.come.action.IAction;
import org.come.action.pay.Payreturn;
import org.come.action.sys.ChangeRoleAction;
import org.come.entity.Keju;
import org.come.protocol.AgreementUtil;
import org.come.protocol.ParamTool;
import org.come.server.GameServer;
import org.come.tool.NewAESUtil;
import org.come.tool.WriteOut;

import javax.enterprise.inject.New;


public class MainServerHandler extends SimpleChannelInboundHandler<String> {
    private int lossConnectCount = 0;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // System.out.println("已经 8 秒未收到客户端的消息了！");
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                // 修复：检查是否为 Tab 切换状态
                // Tab 切换后，原 ctx 可能不再接收消息，但不应判定为掉线
                // 检查当前 ctx 是否在 currentLoginMap 中作为 key 存在（表示已切换到其他角色）
                ChannelHandlerContext targetCtx = org.come.action.sys.ChangeRoleAction.currentLoginMap.get(ctx);
                if (targetCtx != null && targetCtx != ctx) {
                    // 当前 ctx 已切换到其他角色，不增加掉线计数
                    lossConnectCount = 0;
                    return;
                }
                
                lossConnectCount++;
                if (lossConnectCount > 6) {// 关闭通道
                    GameServer.userDown(ctx);
                    ctx.close();
                    WriteOut.addtxt("心跳关闭连接", 9999);
                }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }

    }

    static final String jiu = "发送旧协议头:";
    static final String tm = "转时间报错:";
    static final String ys = "疑是重复发包:";
    static final String CL = "处理报错:";
    static final String JS = "旧时间包:";
    static final String QG = "//";
    private long time = 0;

    @Override
// 防止攻击代码 注释1 空数据修复
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
//		InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
//		String IP = insocket.getAddress().getHostAddress();
//		System.err.println(IP);
//-----------------------------------------------------------------------------------------------------
//		System.out.println("进入之前");
        // 收到消息直接打印输出

//接收客户端的所有信息
        try {
            InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
            String clientIp = address.getAddress().getHostAddress();
            if (msg.length() < 6) {
                lossConnectCount = 0;
                return;
            }// 过滤心跳

            if (GameServer.OPEN) {
                return;
            }
            String cd = msg.substring(0, 4);
            String ab = null;
            IAction action = ParamTool.ACTION_MAP2.get(cd);
            if (action != null) {
                ab = msg.substring(6);
//                System.out.println(ab);
//                action.action(ctx, ab);
//                return;
                if (action instanceof Payreturn) {
                    System.out.println("有人在刷玉！！！");
                    return;
                }
                // 修复：早期协议头处理也需要禁止战斗消息转发
                // 原代码只处理了 fightingroundend 和 battlestate，漏掉了其他战斗协议头
                if (cd.equals(AgreementUtil.fightingroundend) || 
                    cd.equals(AgreementUtil.battlestate) ||
                    cd.equals(AgreementUtil.fightround) ||
                    cd.equals(AgreementUtil.fightingforesee) ||
                    cd.equals(AgreementUtil.fightingrounddeal) ||
                    cd.equals(AgreementUtil.fightcaozuo) ||
                    cd.equals(AgreementUtil.fightingend) ||
                    cd.equals(AgreementUtil.battleReport)) {
                    // 战斗消息直接处理，禁止转发
                    action.action(ctx, ab);
                } else {
                    ChannelHandlerContext loggedInCtx = ChangeRoleAction.currentLoginMap.get(ctx);
                    if (loggedInCtx != null) {
                        action.action(loggedInCtx, ab);
                    } else {
                        action.action(ctx, ab);
                    }
                }
                return;
            }
            msg = NewAESUtil.AESJDKDncode(msg);
//            System.out.println("解析后的msg:" + msg);
            // 收到消息直接打印输出
            if (msg == null) {
                System.out.println("空数据");
                return;
            }
//            System.out.println("解析后的msg:" + msg);
            int wz = msg.indexOf(QG);
            if (wz < 13) {
                WriteOut.addtxt(jiu + msg, 9999);
                return;
            }
            try {
                String ef = msg.substring(0, 13);
                long eftime = new Long(ef);
                if (eftime > time) {
                    time = eftime;
                } else {
                    // System.out.println(ys+msg);
                    // WriteOut.addtxt(ys+msg,9999);
                    return;
                }
            } catch (Exception e) {
                // TODO: handle exception
                WriteOut.addtxt(tm + msg, 9999);
                return;
            }
            cd = msg.substring(13, wz);
            ab = msg.substring(wz + 2);
            action = ParamTool.ACTION_MAP.get(cd);

            // 修复：战斗相关消息禁止通过 currentLoginMap 转发，必须直接发送到原角色 ctx
            // 防止 Tab 切换角色时导致战斗状态同步混乱
            ChannelHandlerContext loggedInCtx = ChangeRoleAction.currentLoginMap.get(ctx);
            if (loggedInCtx != null && !cd.equals(AgreementUtil.changeRole)) {
                // 检查是否为战斗相关消息
                boolean isFightMessage = cd.equals(AgreementUtil.fightround) || 
                                         cd.equals(AgreementUtil.fightingforesee) || 
                                         cd.equals(AgreementUtil.fightingroundend) || 
                                         cd.equals(AgreementUtil.fightingrounddeal) ||
                                         cd.equals(AgreementUtil.fightcaozuo) ||
                                         cd.equals(AgreementUtil.fightingend) ||
                                         cd.equals(AgreementUtil.battlestate) ||
                                         cd.equals(AgreementUtil.battleReport);
                
                if (isFightMessage) {
                    // 战斗消息必须发送到原角色，禁止转发
                    action.action(ctx, ab);
                } else {
                    action.action(loggedInCtx, ab);
                }
                if (cd.equals(AgreementUtil.enterGame)) {
                    this.time = System.currentTimeMillis();
                }
            } else {
                action.action(ctx, ab);
                if (cd.equals(AgreementUtil.enterGame)) {
                    this.time = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            try {
                String abc = CL + msg + getErrorMessage(e);
                WriteOut.addtxt(abc, 9999);
            } catch (Exception e2) {
                // TODO: handle exception
                System.out.println("生成日志出现异常");
            }
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

//

    public static String getErrorMessage(Exception e) {
        if (null == e) {
            return "";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * 覆盖 channelActive 方法 在channel被启用的时候触发 (在建立连接的时候)
     * channelActive 和channelInActive 在后面的内容中讲述，这里先不做详细的描述
     */
    public static long a = 0;
    public static long b = 0;

    public static String VS = null;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        a++;
        try {
            InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
            String ip = address != null ? address.getAddress().getHostAddress() : "unknown";
            String log = "[NET] channelActive ip=" + ip + ", ctx=" + ctx.hashCode();
            System.err.println(log);
            // 单独输出到 log/_NET.txt，方便直接查看
            WriteOut.writeTxtFile(log + "\r\n", "_NET");
        } catch (Exception ignored) {
        }
        SendMessage.sendMessageToSlef(ctx, VS);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        try {
            InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
            String ip = address != null ? address.getAddress().getHostAddress() : "unknown";
            String log = "[NET] channelInactive ip=" + ip + ", ctx=" + ctx.hashCode();
            System.err.println(log);
            WriteOut.writeTxtFile(log + "\r\n", "_NET");
        } catch (Exception ignored) {
        }
        // 保存用户信息
        GameServer.userDown(ctx);
        super.handlerRemoved(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        b++;
        try {
            InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
            String ip = address != null ? address.getAddress().getHostAddress() : "unknown";
            String log = "[NET] exceptionCaught ip=" + ip + ", ctx=" + ctx.hashCode()
                    + ", error=" + (cause != null ? cause.toString() : "null");
            System.err.println(log);
            WriteOut.writeTxtFile(log + "\r\n", "_NET");
        } catch (Exception ignored) {
        }
        // 保存用户信息
        GameServer.userDown(ctx);
        ctx.close();
        super.exceptionCaught(ctx, cause);
    }


}