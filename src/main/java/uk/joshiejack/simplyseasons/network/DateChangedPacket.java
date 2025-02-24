package uk.joshiejack.simplyseasons.network;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkDirection;
import uk.joshiejack.penguinlib.network.PenguinPacket;
import uk.joshiejack.penguinlib.util.PenguinLoader;
import uk.joshiejack.simplyseasons.client.SSClient;

@PenguinLoader.Packet(NetworkDirection.PLAY_TO_CLIENT)
public class DateChangedPacket extends PenguinPacket {
    public DateChangedPacket() {}

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleClientPacket() {
        SSClient.HUD.get().recalculateDate(Minecraft.getInstance().level);
    }
}