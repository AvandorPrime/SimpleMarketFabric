package com.avandortools.simplemarket.util;

import net.minecraft.world.World;

import static net.minecraft.SharedConstants.TICKS_PER_SECOND;

public class AvandorTimeUtils {
    public static class TimeConstants {
        public static final int TICKS_PER_IRL_SECOND = 20;
        public static final int TICKS_PER_IRL_MINUTE = TICKS_PER_SECOND * 60;
        public static final int TICKS_PER_MC_DAY = 24000;
        public static final int TICKS_PER_MC_HOUR = TICKS_PER_MC_DAY/24;

    }

    public enum MinecraftTimeOfDay {
        DAY,
        SUNSET,
        NIGHT,
        SUNRISE
    }

    public static MinecraftTimeOfDay getMinecraftTimeOfDay(World world) {
        // Adjust timeOfDay to be within the 0-23999 range (handle overflow)
        final long timeOfDay = world.getTimeOfDay() % 24000;

        if (timeOfDay >= 0 && timeOfDay <= 11999) {
            return MinecraftTimeOfDay.DAY;
        } else if (timeOfDay >= 12000 && timeOfDay <= 12999) {
            return MinecraftTimeOfDay.SUNSET;
        } else if (timeOfDay >= 13000 && timeOfDay <= 22999) {
            return MinecraftTimeOfDay.NIGHT;
        } else if (timeOfDay >= 23000) {
            return MinecraftTimeOfDay.SUNRISE;
        }
        throw new IllegalStateException("Unexpected time of day: " + timeOfDay);  // If something goes wrong
    }

    public static boolean worldIsNight(World world){
        return AvandorTimeUtils.getMinecraftTimeOfDay(world) == AvandorTimeUtils.MinecraftTimeOfDay.NIGHT;
    }
}