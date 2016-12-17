package uk.co.wehavecookies56.bonfires.packets;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import uk.co.wehavecookies56.bonfires.BonfireRegistry;
import uk.co.wehavecookies56.bonfires.LocalStrings;
import uk.co.wehavecookies56.bonfires.blocks.BlockAshBonePile;
import uk.co.wehavecookies56.bonfires.tiles.TileEntityBonfire;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Toby on 06/11/2016.
 */
public class LightBonfire extends AbstractMessage.AbstractServerMessage<LightBonfire> {

    String name;
    int x, y, z;
    boolean isPublic;

    public LightBonfire() {}

    public LightBonfire(String name, TileEntityBonfire bonfire, boolean isPublic) {
        this.name = name;
        this.x = bonfire.getPos().getX();
        this.y = bonfire.getPos().getY();
        this.z = bonfire.getPos().getZ();
        this.isPublic = isPublic;
    }

    @Override
    protected void read(PacketBuffer buffer) throws IOException {
        this.name = buffer.readString(20);
        this.x = buffer.readInt();
        this.y = buffer.readInt();
        this.z = buffer.readInt();
        this.isPublic = buffer.readBoolean();
    }

    @Override
    protected void write(PacketBuffer buffer) throws IOException {
        buffer.writeString(name);
        buffer.writeInt(x);
        buffer.writeInt(y);
        buffer.writeInt(z);
        buffer.writeBoolean(isPublic);
    }

    @Override
    public void process(EntityPlayer player, Side side) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntityBonfire te = (TileEntityBonfire) player.world.getTileEntity(pos);
        te.setLit(true);
        UUID id = UUID.randomUUID();
        te.createBonfire(name, id, player.getPersistentID(), true);
        te.setID(id);
        player.world.setBlockState(pos, player.world.getBlockState(pos).withProperty(BlockAshBonePile.LIT, true), 2);
        player.sendMessage(new TextComponentTranslation(LocalStrings.TEXT_LIT));
        PacketDispatcher.sendToAll(new SyncBonfire(te.isBonfire(), te.isLit(), te.getID(), te));
        PacketDispatcher.sendToAll(new SyncSaveData(BonfireRegistry.INSTANCE.getBonfires()));
    }
}
