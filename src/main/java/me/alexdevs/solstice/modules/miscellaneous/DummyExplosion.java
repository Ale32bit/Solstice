package me.alexdevs.solstice.modules.miscellaneous;

import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

public class DummyExplosion {
    public static void spawn(ServerWorld world, Vec3d pos, float power) {
        world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.2F) * 0.7F);
        DefaultParticleType particle;
        if(power >= 2.0) {
            particle = ParticleTypes.EXPLOSION_EMITTER;
        } else {
            particle = ParticleTypes.EXPLOSION;
        }
        world.spawnParticles(particle, pos.x, pos.y, pos.z, 1, 1, 0, 0, 1);

    }
}
