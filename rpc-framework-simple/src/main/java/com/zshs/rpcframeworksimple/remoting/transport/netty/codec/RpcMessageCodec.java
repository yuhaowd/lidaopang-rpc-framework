package com.zshs.rpcframeworksimple.remoting.transport.netty.codec;

import com.zshs.rpcframeworksimple.remoting.dto.RpcRequest;
import com.zshs.rpcframeworksimple.remoting.dto.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

@Slf4j
public class RpcMessageCodec extends MessageToMessageCodec<ByteBuf, Object> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> outList) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
        
        // 1. 9 字节的魔数
        out.writeBytes("yhwd".getBytes());
//        out.writeBytes(new byte[]{1,2,3,4});

        // 2. 1 字节的版本
        out.writeByte(1);
        
        // 3. 1 字节的序列化方式 (假设使用JDK序列化)
        out.writeByte(0);
        
        // 4. 1 字节的指令类型
        if (msg instanceof RpcRequest) {
            out.writeByte(MessageType.REQUEST);
        } else if (msg instanceof RpcResponse) {
            out.writeByte(MessageType.RESPONSE);
        }
        
        // 5. 4 个字节的序列号
        out.writeInt(0); // 这里假设序列号为0，你可以根据实际需求设置
        
        // 6. 无意义，对齐填充
        out.writeByte(0xff);
        
        // 7. 获取内容的字节数组
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(msg);
        byte[] bytes = bos.toByteArray();
        
        // 8. 长度
        out.writeInt(bytes.length);
        
        // 9. 写入内容
        out.writeBytes(bytes);
        
        outList.add(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 1. 读取魔数
        int magicNum = in.readInt();
        
        // 2. 读取版本
        byte version = in.readByte();
        
        // 3. 读取序列化方式
        byte serializerType = in.readByte();
        
        // 4. 读取指令类型
        byte messageType = in.readByte();
        
        // 5. 读取序列号
        int sequenceId = in.readInt();
        
        // 6. 跳过填充字节
        in.readByte();
        
        // 7. 读取长度
        int length = in.readInt();
        
        // 8. 读取内容
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);
        // 9. 反序列化
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
        Object message = ois.readObject();
        log.info("message: {}", message);
        // 10. 添加到输出列表
        out.add(message);
    }
}
