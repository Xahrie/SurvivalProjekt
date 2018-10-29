package net.mmm.survival.commands;

import net.mmm.survival.util.CommandUtils;
import net.mmm.survival.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /gamemode Command
 */
public class Gamemode implements CommandExecutor {
  private int gamemode;
  private Player executor, target;
  private String executorMessage;

  @Override
  public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] args) {
    if (CommandUtils.checkPlayer(commandSender)) {
      final Player executor = (Player) commandSender;

      if (CommandUtils.isOperator(executor)) {
        evaluateCommandLength(args, executor);
      }
    }

    return false;
  }

  private void evaluateCommandLength(final String[] args, final Player executor) {
    if (checkArguments(args, executor)) {
      argumentLengthValid(args, executor);
    } else {
      executor.sendMessage(Messages.USAGE_GAMEMODE_COMMAND);
    }

  }

  private boolean checkArguments(final String[] args, final Player executor) {
    if (!(args.length == 1 || args.length == 2)) {
      executor.sendMessage(Messages.USAGE_GAMEMODE_COMMAND);
      return false;
    }

    return true;
  }

  private void argumentLengthValid(final String[] args, final Player executor) {
    final String mode = args[0];

    if (args.length == 1) {
      evaluateOneArgument(mode, executor);
    } else {
      evaluateTwoArguments(args, executor);
    }
  }

  private void evaluateOneArgument(final String mode, final Player target) {
    this.target = target;
    evaluateInput(mode);
  }

  private void evaluateTwoArguments(final String[] args, final Player executor) {
    this.executor = executor;
    final Player target = Bukkit.getPlayer(args[1]);
    if (isOnline(target)) {
      final String mode = args[0];
      evaluateInput(mode);
      sendExecutorMessage();
    }
  }

  private boolean isOnline(final Player target) {
    if (target.isOnline()) {
      this.target = target;
      return true;
    } else {
      executor.sendMessage(Messages.PLAYER_NOT_FOUND);
      return false;
    }
  }

  private void evaluateInput(final String input) {
    if ("0".equals(input) || "s".equals(input) || "survival".equals(input)) {
      evaluateSurvival();
    } else if ("1".equals(input) || "c".equals(input) || "creative".equals(input)) {
      evaluateCreative();
    } else if ("2".equals(input) || "a".equals(input) || "adventure".equals(input)) {
      evaluateAdventure();
    } else if ("3".equals(input) || "spec".equals(input) || "spectator".equals(input)) {
      evaluateSpectator();
    } else { // Keine gueltige Eingabe
      target.sendMessage(Messages.GAMEMODE_UNGUELTIG);

    }
  }

  private void sendExecutorMessage() {
    executor.sendMessage(Messages.PREFIX + " §7Du hast §e" + target.getDisplayName() +
        " §7in den Spielmodus§8: " + executorMessage + " §7gesetzt.");
  }

  private void evaluateSurvival() {
    this.gamemode = 0;
    this.executorMessage = "§eÜberlebensmodus §7§o(Survival)";
    target.sendMessage(Messages.GAMEMODE_SURVIVAL);
    updateGamemode();
  }

  private void evaluateCreative() {
    this.gamemode = 1;
    this.executorMessage = "§eKreativmodus §7§o(Creative)";
    target.sendMessage(Messages.GAMEMODE_CREATIVE);
    updateGamemode();
  }

  private void evaluateAdventure() {
    this.gamemode = 2;
    this.executorMessage = "§eAbenteuermodus §7§o(Adventure)";
    target.sendMessage(Messages.GAMEMODE_ADVENTURE);
    updateGamemode();
  }

  private void evaluateSpectator() {
    this.gamemode = 3;
    this.executorMessage = "§eZuschauermodus §7§o(Spectatormode)";
    target.sendMessage(Messages.GAMEMODE_SPECTATOR);
    updateGamemode();
  }

  private void updateGamemode() {
    final GameMode gamemode = GameMode.getByValue(this.gamemode);
    target.setGameMode(gamemode);
  }

}
