package net.mmm.survival.commands;

import java.util.Date;

import net.mmm.survival.SurvivalData;
import net.mmm.survival.player.Complaint;
import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.util.CommandUtils;
import net.mmm.survival.util.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Alice on 22.09.2018.
 */
public class Complain implements CommandExecutor {
  private static final long HALBE_STUNDE = 1_800_000; //1_800_000 2000
  private static final long EIN_TAG = 86_400_000; //86_400_000 4000
  private static final int MINDESTANZ_ZEICHEN = 10;

  @Override
  public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] args) {
    if (CommandUtils.checkPlayer(commandSender)) {
      if (args.length < 1) {
        info(commandSender);
        return true;
      }
      final String commandToExecute = args[0];
      if (commandToExecute.equals("add")) {
        add((Player) commandSender, args);
      } else if (commandToExecute.equals("list")) {
        list((Player) commandSender, args);
      } else if (commandToExecute.equals("delete")) {
        delete((Player) commandSender, args);
      } else { //info
        info(commandSender);
      }
    }
    return true;
  }

  private void info(final CommandSender sender) {
    sender.sendMessage("§f Verfügbare Commands:");
    sender.sendMessage("§f add player reason: §8 Beschwerde hinzufuegen");
    sender.sendMessage("§f list [player/all]: §8 Beschwerden auflisten");
    sender.sendMessage("§f delete player: §8 Beschwerden eines Spielers loeschen");
  }

  private void delete(final Player sender, final String[] args) {
    if (args.length == 2) {
      final SurvivalPlayer targetSurvivalPlayer = SurvivalPlayer.findSurvivalPlayer(sender, args[1]);
      if (targetSurvivalPlayer != null) {
        targetSurvivalPlayer.getComplaints().clear();
        sender.sendMessage(Messages.PREFIX + "§fDie Beschwerden des Spielers §e" +
            targetSurvivalPlayer.getPlayer().getDisplayName() + " §f wurden gelöscht.");
      }
    } else {
      info(sender);
    }
  }

  private void list(final Player sender, final String[] args) {
    if (args.length == 1 || !CommandUtils.isOperator(sender)) {
      final SurvivalPlayer executor = SurvivalPlayer.findSurvivalPlayer(sender);
      sender.sendMessage(Messages.COMPLAINT_INFO);
      if (executor.getComplaints().size() > 0) {
        for (final Complaint complaint : executor.getComplaints()) {
          executor.outputComplaint(complaint);
        }
      }
    } else if (args[1].equals("all")) {
      for (final SurvivalPlayer survivalPlayer : SurvivalData.getInstance().getPlayers().values()) {
        if (survivalPlayer.getComplaints().size() > 0) {
          outputComplaint(survivalPlayer, sender);
        }
      }
    } else {
      final SurvivalPlayer target = SurvivalPlayer.findSurvivalPlayer(sender);
      if (target != null) {
        outputComplaint(target, sender);
      }
    }
  }

  private void outputComplaint(final SurvivalPlayer survivalPlayer, final Player sender) {
    if (survivalPlayer.getComplaints().size() > 0) {
      final Player player = survivalPlayer.getPlayer();
      if (player != null) {
        sender.sendMessage(Messages.PREFIX + "§fÜber den Spieler §e" + player.getDisplayName() +
            " §f liegen §e" + survivalPlayer.getComplaints().size() + " §f Beschwerden vor:");
        for (final Complaint complaint : survivalPlayer.getComplaints()) {
          survivalPlayer.outputComplaint(complaint);
        }
      }
    }
  }

  private void add(final Player sender, final String[] args) {
    if (args.length == 3) {
      final SurvivalPlayer executor = SurvivalPlayer.findSurvivalPlayer(sender);
      final SurvivalPlayer target = SurvivalPlayer.findSurvivalPlayer(sender, args[1]);
      if (target != null) {
        final String reason = args[2];
        if (checkComplaint(executor, target, reason)) {
          target.addComplaint(new Complaint(target.getUuid(), target.getComplaints().size() + 1,
              reason, executor.getUuid(), new Date()));
          sender.sendMessage(Messages.PREFIX + "§fDie Beschwerden über den Spielers §e" + target
              .getPlayer().getDisplayName() + " §fwurden gespeichert.");
        }
      }
    } else {
      info(sender);
    }
  }

  private boolean checkComplaint(final SurvivalPlayer executor, final SurvivalPlayer target, final String reason) {
    //nur jede halbe Stunde reporten
    final Date now = new Date();
    if ((now.getTime() - executor.getLastComplaint().getTime()) < HALBE_STUNDE) {
      executor.getPlayer().sendMessage(Messages.COMPLAINT_TOO_FAST);
      return false;
    }

    //einen Spieler nur einmal pro Tag reporten
    for (final SurvivalPlayer targetCheck : SurvivalData.getInstance().getPlayers().values()) {
      if (targetCheck.getUuid().equals(target.getUuid())) {
        for (final Complaint complaint : targetCheck.getComplaints()) {
          if (complaint.getExecutor().equals(executor.getUuid()) && now.getTime() - complaint.getDate().getTime() < EIN_TAG) {
            executor.getPlayer().sendMessage(Messages.COMPLAINT_TOO_FAST_PLAYER);
            return false;
          }
        }
      }
    }

    //Grund mindestens X Zeichen
    if (reason.length() < MINDESTANZ_ZEICHEN) {
      executor.getPlayer().sendMessage(Messages.COMPLAINT_TOOSHORT);
      return false;
    }

    return true;
  }
}
