package uk.joshiejack.simplyseasons.world.season;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import uk.joshiejack.penguinlib.network.PenguinNetwork;
import uk.joshiejack.penguinlib.util.helpers.TimeHelper;
import uk.joshiejack.simplyseasons.api.ISeasonProvider;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;
import uk.joshiejack.simplyseasons.network.SeasonChangedPacket;
import uk.joshiejack.simplyseasons.world.CalendarDate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractSeasonsProvider implements ISeasonProvider {
    private final LazyOptional<AbstractSeasonsProvider> capability;

    public AbstractSeasonsProvider() {
        this.capability = LazyOptional.of(() -> this);
    }
    private Season previousSeason;
    private long previousElapsed;

    @Override
    public int getDay(World world) {
        return 1 + CalendarDate.getDay(world);
    }

    @Override
    public void recalculate(World world) {
        if (!world.isClientSide) {
            Season season = getSeason(world);
            long elapsed = TimeHelper.getElapsedDays(world.getDayTime());
            if (elapsed != previousElapsed || previousSeason != season)
                PenguinNetwork.sendToDimension(new SeasonChangedPacket(season, previousSeason != season), world.dimension());
            previousElapsed = elapsed;
            previousSeason = getSeason(world);
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == SSeasonsAPI.SEASONS_CAPABILITY ? capability.cast() : LazyOptional.empty();
    }

    //Stay down here out of my way!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    @SuppressWarnings("rawtypes, unchecked")
    public static class Storage implements Capability.IStorage<ISeasonProvider> {
        @Override
        public void readNBT(Capability<ISeasonProvider> capability, ISeasonProvider instance, Direction side, INBT nbt) {
            if (instance instanceof INBTSerializable)
                ((INBTSerializable) instance).deserializeNBT(nbt);
        }

        @Nullable
        @Override
        public INBT writeNBT(Capability<ISeasonProvider> capability, ISeasonProvider instance, Direction side) {
            return instance instanceof INBTSerializable ? ((INBTSerializable)instance).serializeNBT() : new CompoundNBT();
        }
    }
}
