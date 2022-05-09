package com.spaceman.tport.tpEvents.animations;

import com.spaceman.tport.commandHandler.SubCommand;
import com.spaceman.tport.fancyMessage.Message;
import com.spaceman.tport.keyValueHelper.ExtendedKey;
import com.spaceman.tport.keyValueHelper.KeyValueError;
import com.spaceman.tport.keyValueHelper.KeyValueHelper;
import com.spaceman.tport.keyValueHelper.KeyValueTabArgument;
import com.spaceman.tport.tpEvents.ParticleAnimation;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.spaceman.tport.fancyMessage.colorTheme.ColorTheme.formatInfoTranslation;

public class SimpleAnimation extends ParticleAnimation {
    
    private Particle particle = Particle.EXPLOSION_NORMAL;
    private int amount = 10;
    
    public SimpleAnimation() {
        setEnabled(true);
    }
    
    public SimpleAnimation(boolean b) {
        setEnabled(b);
    }
    
    @Override
    public String getAnimationName() {
        return SubCommand.lowerCaseFirst(this.getClass().getSimpleName());
    }
    
    @Override
    public void show(Player player, Location l) {
        try {
            if (l.getWorld() != null) l.getWorld().spawnParticle(particle, l, amount);
        } catch (IllegalArgumentException ignore) {
        }
    }
    
    public void setParticle(Particle p) {
        this.particle = p;
    }
    
    public void setAmount(int amount) {
        this.amount = amount;
    }
    
    @Override
    public boolean edit(Player player, String[] data) {
        if (data == null || data.length == 0) {
            this.setParticle(Particle.EXPLOSION_NORMAL);
        } else {
            try {
                KeyValueHelper.extendedConstructObject(data[0], this,
                        (ExtendedKey) new ExtendedKey("particle", Particle::valueOf, false,
                                ((o, value) -> ((SimpleAnimation) o).setParticle((Particle) value))).setErrorMessageID("tport.tpEvents.animations.simpleAnimation.notAParticle"),
                        (ExtendedKey) new ExtendedKey("amount", Integer::parseInt, true,
                                (o, value) -> ((SimpleAnimation) o).setAmount((Integer) value)).setErrorMessageID("tport.tpEvents.animations.simpleAnimation.notANumber"));
            } catch (KeyValueError keyValueError) {
                keyValueError.sendMessage(player);
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void save(ConfigurationSection section) {
        section.set("p", particle.name());
        section.set("a", amount);
    }
    
    @Override
    public void load(ConfigurationSection section) {
        this.setParticle(Particle.valueOf(section.getString("p", Particle.EXPLOSION_NORMAL.name()).toUpperCase()));
        this.setAmount(section.getInt("a", 10));
    }
    
    @Override
    public List<String> tabList(Player player, String[] args) {
        return KeyValueHelper.constructTab(args[args.length - 1], Arrays.asList(
                new KeyValueTabArgument("particle", Arrays.stream(Particle.values()).map(Enum::name).collect(Collectors.toList())),
                new KeyValueTabArgument("amount", Collections.singletonList("<X>"))));
    }
    
    @Override
    public Message getDescription() {
        return formatInfoTranslation("tport.tpEvents.animations.simpleAnimation.description");
    }
}
