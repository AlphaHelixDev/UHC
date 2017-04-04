package de.alphahelix.alphaapi.netty.channel;

import de.alphahelix.alphaapi.netty.IPacketListener;
import de.alphahelix.alphaapi.reflection.ReflectionUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import java.net.SocketAddress;
import java.util.ArrayList;

public class INCChannel extends ChannelAbstract {

    private static final ReflectionUtil.SaveField channelField = ReflectionUtil.getFirstType(Channel.class, ReflectionUtil.getNmsClass("NetworkManager"));

    public INCChannel(IPacketListener iPacketListener) {
        super(iPacketListener);
    }

    public void addChannel(final Player player) {
        try {
            final Channel channel = getChannel(player);
            this.addChannelExecutor.execute(new Runnable() {
                public void run() {
                    try {
                        channel.pipeline().addBefore(KEY_HANDLER, KEY_PLAYER, INCChannel.this.new ChannelHandler(player));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to add channel for " + player, e);
        }
    }

    public void removeChannel(Player player) {
        try {
            final Channel channel = getChannel(player);
            this.removeChannelExecutor.execute(() -> {
                try {
                    if (channel.pipeline().get(KEY_PLAYER) != null) {
                        channel.pipeline().remove(KEY_PLAYER);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to remove channel for " + player, e);
        }
    }

    Channel getChannel(Player player)
            throws ReflectiveOperationException {
        Object handle = ReflectionUtil.getDeclaredMethod("getHandle", ReflectionUtil.getCraftBukkitClass("entity.CraftPlayer")).invoke(player, false);
        Object connection = playerConnection.get(handle);
        return (Channel) channelField.get(networkManager.get(connection));
    }

    public ChannelAbstract.IListenerList newListenerList() {
        return new ListenerList();
    }

    class ListenerList<E> extends ArrayList<E> implements ChannelAbstract.IListenerList<E> {

        public boolean add(E paramE) {
            try {
                final E a = paramE;
                INCChannel.this.addChannelExecutor.execute(new Runnable() {
                    public void run() {
                        try {
                            Channel channel = null;
                            while (channel == null) {
                                channel = (Channel) INCChannel.channelField.get(a);
                            }
                            if (channel.pipeline().get(KEY_SERVER) == null) {
                                channel.pipeline().addBefore(KEY_HANDLER, KEY_SERVER, INCChannel.this.new ChannelHandler(INCChannel.this.new INCChannelWrapper(channel)));
                            }
                        } catch (Exception ignored) {
                        }
                    }
                });
            } catch (Exception ignored) {
            }
            return super.add(paramE);
        }

        public boolean remove(Object arg0) {
            try {
                final Object a = arg0;
                INCChannel.this.removeChannelExecutor.execute(new Runnable() {
                    public void run() {
                        try {
                            Channel channel = null;
                            while (channel == null) {
                                channel = (Channel) INCChannel.channelField.get(a);
                            }
                            channel.pipeline().remove(KEY_SERVER);
                        } catch (Exception e) {
                        }
                    }
                });
            } catch (Exception e) {
            }
            return super.remove(arg0);
        }
    }

    class ChannelHandler extends ChannelDuplexHandler implements ChannelAbstract.IChannelHandler {

        private Object owner;

        public ChannelHandler(Player player) {
            this.owner = player;
        }

        public ChannelHandler(ChannelWrapper channelWrapper) {
            this.owner = channelWrapper;
        }

        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            Cancellable cancellable = new de.alphahelix.alphaapi.netty.Cancellable();
            Object pckt = msg;
            if (ChannelAbstract.Packet.isAssignableFrom(msg.getClass())) {
                pckt = INCChannel.this.onPacketSend(this.owner, msg, cancellable);
            }

            if (cancellable.isCancelled()) {
                return;
            }
            super.write(ctx, pckt, promise);
        }

        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            Cancellable cancellable = new de.alphahelix.alphaapi.netty.Cancellable();
            Object pckt = msg;
            if (ChannelAbstract.Packet.isAssignableFrom(msg.getClass())) {
                pckt = INCChannel.this.onPacketReceive(this.owner, msg, cancellable);
            }
            if (cancellable.isCancelled()) {
                return;
            }
            super.channelRead(ctx, pckt);
        }
    }

    class INCChannelWrapper extends ChannelWrapper<Channel> implements ChannelAbstract.IChannelWrapper {
        public INCChannelWrapper(Channel channel) {
            super(channel);
        }

        public SocketAddress getRemoteAddress() {
            return channel().remoteAddress();
        }

        public SocketAddress getLocalAddress() {
            return channel().localAddress();
        }
    }
}
