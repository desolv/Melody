package gg.desolve.melody.command.param;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.exception.CommandErrorException;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

public final class OnlinePlayerType implements ParameterType<BukkitCommandActor, Player> {

    @Override
    public Player parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<BukkitCommandActor> context) {
        String arg = input.readString();

        if (arg.startsWith("@")) {
            throw new CommandErrorException("Selectors are not allowed. Use an online player name.");
        }

        Player target = Bukkit.getPlayerExact(arg);
        if (target == null || !target.isOnline()) {
            throw new CommandErrorException("That player is not online.");
        }
        return target;
    }

    @Override
    public @NotNull SuggestionProvider<@NotNull BukkitCommandActor> defaultSuggestions() {
        return context -> Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .toList();
    }
}
