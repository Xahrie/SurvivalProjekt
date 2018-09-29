package net.mmm.survival.commands;

import net.mmm.survival.util.CommandUtils;
import net.mmm.survival.util.Messages;
import net.mmm.survival.util.UUIDUtils;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /gamemode Command
 */
public class Gamemode implements CommandExecutor {
  private Integer gamemode;
  private Player executor, target;
  private String executorMessage;

  @Override
  public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] args) {
    if (CommandUtils.checkPlayer(commandSender)) {
      final Player executor = (Player) commandSender;

      if (CommandUtils.isOperator(executor)) {
        checkArgumentLength(args, executor);
      }
    }

    return false;
  }

  private void checkArgumentLength(final String[] args, final Player executor) {
    if (checkArguments(args, executor)) {
      argumentLengthValid(args, executor);
    } else {
      argumentLengthNotValid(executor);
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
    checkInput(mode);
  }

  private void evaluateTwoArguments(final String[] args, final Player executor) {
    this.executor = executor;
    final Player target = UUIDUtils.getPlayer(args[1]);
    if (isOnline(target)) {
      final String mode = args[0];
      checkInput(mode);
      sendExecutorMessage();
    }
  }

  private void sendExecutorMessage() {
    this.executor.sendMessage(Messages.PREFIX + " §7Du hast §e" + this.target.getDisplayName() +
        " §7in den Spielmodus§8: " + this.executorMessage + " §7gesetzt.");
  }

  private boolean isOnline(final Player target) {
    if (target.isOnline()) {
      this.target = target;
      return true;
    } else {
      this.executor.sendMessage(Messages.PLAYER_NOT_FOUND);
      return false;
    }
  }

  private void argumentLengthNotValid(final Player executor) {
    executor.sendMessage(Messages.USAGE_GAMEMODE_COMMAND);
  }

  private void checkInput(final String input) {
    isSurvivalExpected(input);
  }

  private void isSurvivalExpected(final String input) {
    if (input.equals("0") || input.equals("s") || input.equals("survival")) {
      this.gamemode = 0;
      this.target.sendMessage(Messages.GAMEMODE_SURVIVAL);
      this.executorMessage = "§eÜberlebensmodus §7§o(Survival)";
    }
    isCreativeExpected(input);
  }

  private void isCreativeExpected(final String input) {
    if (input.equals("1") || input.equals("c") || input.equals("creative")) {
      this.gamemode = 1;
      this.target.sendMessage(Messages.GAMEMODE_CREATIVE);
      this.executorMessage = "§eKreativmodus §7§o(Creative)";
    }
    isAdventureExpected(input);
  }

  private void isAdventureExpected(final String input) {
    if (input.equals("2") || input.equals("a") || input.equals("adventure")) {
      this.gamemode = 2;
      this.target.sendMessage(Messages.GAMEMODE_ADVENTURE);
      this.executorMessage = "§eAbenteuermodus §7§o(Adventure)";
    }
    isSpectatorExpected(input);
  }

  private void isSpectatorExpected(final String input) {
    if (input.equals("0") || input.equals("s") || input.equals("survival")) {
      this.gamemode = 0;
      this.target.sendMessage(Messages.GAMEMODE_SPECTATOR);
      this.executorMessage = "§eZuschauermodus §7§o(Spectatormode)";
    } else {
      notValid();
    }
    updateGamemode();
  }

  private void notValid() {
    this.target.sendMessage(Messages.GAMEMODE_UNGUELTIG);
  }

  private void updateGamemode() {
    final GameMode gamemode = GameMode.values()[this.gamemode];
    this.target.setGameMode(gamemode);
  }

}
