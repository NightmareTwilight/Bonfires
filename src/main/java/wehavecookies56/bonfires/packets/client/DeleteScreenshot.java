package wehavecookies56.bonfires.packets.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.DistExecutor;
import wehavecookies56.bonfires.client.ClientPacketHandler;
import wehavecookies56.bonfires.packets.Packet;

import java.util.UUID;

public class DeleteScreenshot extends Packet<DeleteScreenshot> {

    public DeleteScreenshot(FriendlyByteBuf buffer) {
        super(buffer);
    }

    UUID uuid;
    String name;

    public DeleteScreenshot(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    @Override
    public void decode(FriendlyByteBuf buffer) {
        uuid = buffer.readUUID();
        name = buffer.readUtf();
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(uuid);
        buffer.writeUtf(name);
    }

    @Override
    public void handle(CustomPayloadEvent.Context context) {
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientPacketHandler.deleteScreenshot(uuid, name));
    }
}
