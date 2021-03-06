package draylar.identity.ability.impl;

import draylar.identity.Identity;
import draylar.identity.ability.IdentityAbility;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class GhastAbility extends IdentityAbility<GhastEntity> {

    @Override
    public void onUse(PlayerEntity player, GhastEntity identity, World world) {
        FireballEntity fireball = new FireballEntity(
                world,
                player.getX(),
                player.getEyeY(),
                player.getZ(),
                player.getRotationVector().x,
                player.getRotationVector().y,
                player.getRotationVector().z
        );

        fireball.setOwner(player);
        world.spawnEntity(fireball);
        world.playSoundFromEntity(null, player, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.HOSTILE, 10.0F, (world.random.nextFloat() - world.random.nextFloat()) * 0.2F + 1.0F);
        world.playSoundFromEntity(null, player, SoundEvents.ENTITY_GHAST_WARN, SoundCategory.HOSTILE, 10.0F, (world.random.nextFloat() - world.random.nextFloat()) * 0.2F + 1.0F);
    }

    @Override
    public int getCooldown() {
        return Identity.CONFIG.ghastAbilityCooldown;
    }

    @Override
    public Item getIcon() {
        return Items.FIRE_CHARGE;
    }
}
