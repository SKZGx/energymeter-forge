package dev.rlnt.energymeter.network;

import dev.rlnt.energymeter.meter.SideConfiguration;
import dev.rlnt.energymeter.network.PacketHandler.SyncFlags;
import dev.rlnt.energymeter.util.TypeEnums.MODE;
import dev.rlnt.energymeter.util.TypeEnums.NUMBER_MODE;
import dev.rlnt.energymeter.util.TypeEnums.STATUS;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class ClientSyncPacket {

    private BlockPos pos;
    private int flags;
    private int[] sideConfig;
    private float transferRate;
    private STATUS status;
    private NUMBER_MODE numberMode;
    private MODE mode;

    public ClientSyncPacket(
        final BlockPos pos,
        final int flags,
        final SideConfiguration sideConfig,
        final float transferRate,
        final STATUS status,
        final NUMBER_MODE numberMode,
        final MODE mode
    ) {
        this.pos = pos;
        this.flags = flags;
        this.sideConfig = sideConfig.serialize();
        this.transferRate = transferRate;
        this.status = status;
        this.numberMode = numberMode;
        this.mode = mode;
    }

    private ClientSyncPacket() {}

    static ClientSyncPacket decode(final PacketBuffer buffer) {
        final ClientSyncPacket packet = new ClientSyncPacket();
        packet.pos = buffer.readBlockPos();
        packet.flags = buffer.readInt();
        if ((packet.flags & SyncFlags.SIDE_CONFIG) != 0) packet.sideConfig = buffer.readVarIntArray();
        if ((packet.flags & SyncFlags.TRANSFER_RATE) != 0) packet.transferRate = buffer.readFloat();
        if ((packet.flags & SyncFlags.STATUS) != 0) packet.status = STATUS.values()[buffer.readInt()];
        if ((packet.flags & SyncFlags.NUMBER_MODE) != 0) packet.numberMode = NUMBER_MODE.values()[buffer.readInt()];
        if ((packet.flags & SyncFlags.MODE) != 0) packet.mode = MODE.values()[buffer.readInt()];
        return packet;
    }

    static void handle(final ClientSyncPacket packet, final Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> handlePacket(packet));
        context.get().setPacketHandled(true);
    }

    private static void handlePacket(final ClientSyncPacket packet) {
        ClientHandler.handleClientSyncPacket(packet);
    }

    public BlockPos getPos() {
        return pos;
    }

    public int getFlags() {
        return flags;
    }

    public int[] getSideConfig() {
        return sideConfig;
    }

    public float getTransferRate() {
        return transferRate;
    }

    public STATUS getStatus() {
        return status;
    }

    public NUMBER_MODE getNumberMode() {
        return numberMode;
    }

    public MODE getMode() {
        return mode;
    }

    public void setMode(final MODE mode) {
        this.mode = mode;
    }

    void encode(final PacketBuffer buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeInt(flags);
        if ((flags & SyncFlags.SIDE_CONFIG) != 0) buffer.writeVarIntArray(sideConfig);
        if ((flags & SyncFlags.TRANSFER_RATE) != 0) buffer.writeFloat(transferRate);
        if ((flags & SyncFlags.STATUS) != 0) buffer.writeInt(status.ordinal());
        if ((flags & SyncFlags.NUMBER_MODE) != 0) buffer.writeInt(numberMode.ordinal());
        if ((flags & SyncFlags.MODE) != 0) buffer.writeInt(mode.ordinal());
    }
}
