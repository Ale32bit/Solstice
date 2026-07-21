package me.alexdevs.solstice.modules.miscellaneous;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public class DummyExplosion {
    public static void spawn(ServerLevel world, Vec3 pos, float power) {
        world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F, (1.0F + (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.2F) * 0.7F);
        SimpleParticleType particle;
        if(power >= 2.0) {
            particle = ParticleTypes.EXPLOSION_EMITTER;
        } else {
            particle = ParticleTypes.EXPLOSION;
        }
        world.sendParticles(particle, pos.x, pos.y, pos.z, 1, 1, 0, 0, 1);

    }
}
