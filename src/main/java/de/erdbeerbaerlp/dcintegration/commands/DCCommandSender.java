package de.erdbeerbaerlp.dcintegration.commands;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.authlib.GameProfile;
import de.erdbeerbaerlp.dcintegration.DiscordIntegration;
import de.erdbeerbaerlp.dcintegration.Utils;
import de.erdbeerbaerlp.dcintegration.storage.Configuration;
import net.dv8tion.jda.api.entities.User;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


@SuppressWarnings("EntityConstructor")
public class DCCommandSender extends FakePlayer {

    private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat(DCCommandSender.class.getSimpleName()).setDaemon(true).build());
    private static final UUID uuid = UUID.fromString(Configuration.INSTANCE.senderUUID.get());
    private final CommandFromCFG command;
    private String channelID;

    public DCCommandSender(User user, CommandFromCFG command, String channel) {
        super(ServerLifecycleHooks.getCurrentServer().getWorld(World.field_234918_g_), new GameProfile(uuid, "@" + user.getName() + "#" + user.getDiscriminator()));
        this.command = command;
        this.channelID = channel;
    }

    public DCCommandSender(ServerWorld world, String name, CommandFromCFG command, String channel) {
        super(world, new GameProfile(uuid, "@" + name));
        this.command = command;
        this.channelID = channel;
    }

    private static String textComponentToDiscordMessage(ITextComponent component) {
        if (component == null) return "";
        return Utils.convertMCToMarkdown(component.getString());
    }

    @Override
    public void sendMessage(ITextComponent textComponent, UUID uuid) {
        DiscordIntegration.discord_instance.sendMessageFuture(textComponentToDiscordMessage(textComponent), channelID);
    }

    @Override
    public boolean shouldReceiveFeedback() {
        return true;
    }

    @Override
    public boolean shouldReceiveErrors() {
        return true;
    }


    @Override
    public void sendStatusMessage(ITextComponent component, boolean actionBar) {
        Preconditions.checkNotNull(component);
        DiscordIntegration.discord_instance.sendMessageFuture(textComponentToDiscordMessage(component), channelID);
    }
}