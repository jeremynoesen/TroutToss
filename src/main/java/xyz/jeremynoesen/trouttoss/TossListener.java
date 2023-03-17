package xyz.jeremynoesen.trouttoss;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Salmon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Listener for tossing and hitting of trout
 */
public class TossListener implements Listener {

    /**
     * Throw a trout when a player punches with a raw salmon
     *
     * @param e Player interact event
     */
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if ((e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) &&
                player.getInventory().getItemInMainHand().getType().equals(Material.SALMON)) {
            e.setCancelled(true);
            boolean deplete = player.getGameMode() != GameMode.CREATIVE;
            ItemStack troutItem = new ItemStack(player.getInventory().getItemInMainHand());
            troutItem.setAmount(1);
            if (deplete)
                player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
            Salmon trout = player.getWorld().spawn(player.getEyeLocation().add(player.getLocation().getDirection()), Salmon.class);
            player.getWorld().playSound(trout.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 0.8f);
            trout.setVelocity(player.getLocation().getDirection().multiply(2));
            trout.setInvulnerable(true);
            float yaw = player.getLocation().getYaw();
            float pitch = player.getLocation().getPitch();
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (trout.isDead()) {
                        this.cancel();
                    } else {
                        trout.setRotation(yaw, pitch);
                        trout.getWorld().spawnParticle(Particle.WATER_SPLASH, trout.getLocation(), 1);
                        trout.getWorld().playSound(trout.getLocation(), Sound.ENTITY_BOAT_PADDLE_WATER, 0.1f, 1);
                        boolean end = false;
                        for (Entity entity : trout.getNearbyEntities(0.35, 0.35, 0.35)) {
                            if (!entity.equals(trout) && entity instanceof LivingEntity) {
                                entity.setVelocity(trout.getVelocity().setY(0.000001).normalize().setY(0.5));
                                end = true;
                                break;
                            }
                        }
                        if (end || trout.getLocation().getBlock().isLiquid() || trout.isOnGround() ||
                                !trout.getLocation().add(new Vector(0.35, 0, 0)).getBlock().isPassable() ||
                                !trout.getLocation().add(new Vector(-0.35, 0, 0)).getBlock().isPassable() ||
                                !trout.getLocation().add(new Vector(0, 0.35, 0)).getBlock().isPassable() ||
                                !trout.getLocation().add(new Vector(0, 0, 0.35)).getBlock().isPassable() ||
                                !trout.getLocation().add(new Vector(0, 0, -0.35)).getBlock().isPassable()) {
                            trout.getWorld().playSound(trout.getLocation(), Sound.ENTITY_GUARDIAN_FLOP, 1, 1);
                            trout.getWorld().playSound(trout.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 1);
                            trout.getWorld().spawnParticle(Particle.WATER_SPLASH, trout.getLocation(), 15);
                            if (deplete) trout.getWorld().dropItem(trout.getLocation(), troutItem);
                            trout.remove();
                            this.cancel();
                        }
                    }
                }
            }.runTaskTimer(TroutToss.getInstance(), 2, 1);
        }
    }

    /**
     * Prevent hurting entities when trying to throw a trout
     *
     * @param e Entity damage by entity event
     */
    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player &&
                ((Player) e.getDamager()).getInventory().getItemInMainHand().getType().equals(Material.SALMON)) {
            e.setCancelled(true);
        }
    }
}
