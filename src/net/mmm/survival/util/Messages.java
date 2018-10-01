package net.mmm.survival.util;

import net.mmm.survival.player.SurvivalLicence;

/**
 * Speicherort wesentlicher Nachrichten
 */
public final class Messages {
  public static final String PREFIX = "§8┃ §eSurvival §7» ";
  public static final String HOME_SET = "§8┃ §eSurvival §7» Du hast deinen Home-Punkt gesetzt.";
  public static final String NO_HOME_SET = "§8┃ §eSurvival §7» Du hast noch keinen Home-Punkt gesetzt.";
  public static final String NOT_A_NUMBER = "§8┃ §eSurvival §7» §cDu musst eine Zahl eingeben.";
  public static final String PLAYER_NOT_FOUND = "§8┃ §eSurvival §7» §cDer Spieler wurde nicht gefunden.";
  public static final String TAME_DISABLE = "§8┃ §eSurvival §7» Du kannst nun wieder normal mit den Tieren interagieren.";
  public static final String TAME_ENABLE = "§8┃ §eSurvival §7» Klicke auf das Tier, dass du freilassen möchtest.\nZum Abbrechen gebe erneut §e/tame §7ein.";

  /*
  Beschwerden
   */
  public static final String COMPLAINT_TOO_FAST = "§8┃ §eSurvival §7» §4┃ Du kannst dich nur alle 30 Minuten über Spieler beschweren.";
  public static final String COMPLAINT_TOO_FAST_PLAYER = "§8┃ §eSurvival §7» §4┃ Du kannst dich über diesen Spieler nur einmal pro Tag beschweren.";
  public static final String COMPLAINT_TOOSHORT = "§8┃ §eSurvival §7» §4┃ Eine Beschwerde muss mindestens 10 Zeichen lang sein.";
  public static final String COMPLAINT_INFO = "§8┃ §eSurvival §7» §c┃ Es liegen über dich Beschwerden vor:";

  /*
  Economy-System
   */
  public static final String NOT_ENOUGH_MONEY = "§8┃ §eSurvival §7» §cDu hast nicht genuegend Geld auf deinem Konto.";
  public static final String USAGE_ECONOMY_COMMAND = "§8┃ §eSurvival §7» §c/eco [<set|reset|add|take>] <Spieler>";
  public static final String USAGE_MONEY_COMMAND = "§8┃ §eSurvival §7» §c/money";
  public static final String USAGE_MONEY_COMMAND_ADMIN = "§8┃ §eSurvival §7» §c/money <Spieler>";
  public static final String USAGE_PAY_COMMAND = "§8┃ §eSurvival §7» §cBenutze §c/pay <Spieler> <Amount>";

  /*
  Gamemode
   */
  public static final String GAMEMODE_SURVIVAL = "§8┃ §eSurvival §7» Du wurdest in den Spielmodus§8: §eÜberlebensmodus §7§o(Survival) §7gesetzt.";
  public static final String GAMEMODE_ADVENTURE = "§8┃ §eSurvival §7» Du wurdest in den Spielmodus§8: §eAbenteuermodus §7§o(Adventure) §7gesetzt.";
  public static final String GAMEMODE_CREATIVE = "§8┃ §eSurvival §7» Du wurdest in den Spielmodus§8: §eKreativmodus §7§o(Creative) §7gesetzt.";
  public static final String GAMEMODE_SPECTATOR = "§8┃ §eSurvival §7» Du wurdest in den Spielmodus§8: §eZuschauermodus §7§o(Spectatormode) §7gesetzt.";
  public static final String GAMEMODE_UNGUELTIG = "§4┃ Der eingegebene Spielmodus ist ungueltig";
  public static final String USAGE_GAMEMODE_COMMAND = "§8┃ §eSurvival §7» §c/gm <0|1|2|3> <Spieler>";
  public static final String SPAWN_SET = "§8┃ §eSurvival §7» Du hast den §eSpawn §7gesetzt.";
  public static final String TELEPORT_CANCELED = "§8┃ §eSurvival §7» §cDie Teleportation wurde abgebrochen.. §7§o» Du hast dich bewegt.";
  public static final String TELEPORT_DONT_MOVE = "§8┃ §eSurvival §7» Du wirst teleportiert.. §e§o» Bewege dich nicht..";
  public static final String TELEPORT_FARMWELT = "§8┃ §eSurvival §7» §7Du wurdest zum §eFarmwelt-Spawn §7teleportiert.";

  /*
  Vote-System
   */
  public static final String VOTE_PAGES = "§8┃ §eSurvival §7» Unsere Vote-Seiten:";
  public static final String VOTE_REWARD = "§7§oDu kannst diese Münzen beim Markt eintauschen.";

  /*
  Zone-System
   */
  public static final String NO_DUPLICATE_ZONE = "§8┃ §eSurvival §7» Du kannst keine Zone in einer bereits bestehenden Zone erstellen.";
  public static final String SPAWNZONE_FOUND = "§8┃ §eSurvival §7» Es wurde die Zone §eSpawnzone §7gefunden.";
  public static final String USAGE_NAVI_COMMAND = "§8┃ §eSurvival §7» §cBenutze §c/navi <Spieler>";
  public static final String ZONE_ALREADY_EXIST = "§8┃ §eSurvival §7» Du hast bereits eine Zone.";
  public static final String ZONE_CREATED = "§8┃ §eSurvival §7» §7Du hast erfolgreich deine Zone erstellt.";
  public static final String ZONE_EXPLAINATION = "§8┃ §eSurvival §7» Klicke mit einem Stock auf die erste Ecke deiner Zone, danach klicke auf die gegenüber liegende Ecke.";
  public static final String ZONE_HELP = "§8┃ §eSurvival §7» Zonenhilfe§8:\n§e/zone create §8┃ §7Erstellt eine Zone\n§e/zone search §8┃ §7Sucht nach Zonen\n§e/zone add <Spieler> §8┃ §7Fügt einen Spieler auf deine Zone hinzu\n§e/zone remove <Spieler> §8┃ §7Entfernt einen Spieler von deiner Zone\n§e/zone delete §8┃ §7Löscht deine Zone";
  public static final String ZONE_HELP_ADMIN = "§c/zone setlength <Spieler> <Anzahl> §8┃ §7Setzt die Max-Länge der Zone des Spielers §c/zone info <Spieler> §8┃ §7Zeigt Informationen über den Spieler an§c/zone info §8┃ §7Zeigt Informationen über die Zone in der du stehst an";
  public static final String ZONE_NOT_FOUND = "§8┃ §eSurvival §7» §cDu stehst in keiner Zone.";
  public static final String ZONE_NOT_SET = "§8┃ §eSurvival §7» §cDu hast keine Zone.";
  public static final String ZONE_REMOVED = "§8┃ §eSurvival §7» §7Du hast deine Zone gelöscht.";
  public static final String ZONE_SEARCH_ENABLE = "§8┃ §eSurvival §7» Zonen-Suchmodus§a betreten§7, klicke mit einem Stock auf den Boden um nach Zonen zu suchen.";
  public static final String ZONE_SEARCH_DISABLE = "§88┃ §eSurvival §7» Du hast den Zonen-Suchmodus §cverlassen§7.";
  public static final String ZONE_UNGUELTIG = "§8┃ §eSurvival §7» §4┃ Zu dem eingegebenen Spieler wurde keine Zone gefunden.";
  public static final String NOT_A_PLAYER = "Du musst ein Spieler sein.";
  public static final String NOT_ENOUGH_PERMISSIONS = "§8┃ §eSurvival §7» §cDu hast nicht die benötigten Rechte dafür.";

  /*
  Teleport
   */
  static final String ALREADY_TELEPORTED = "§8┃ §eSurvival §7» Du wirst bereits teleportiert.";
  static final String NO_VALID_WORLD = "§8┃ §eSurvival §7» Du musst in die Hauptwelt kommen, um diesen Command nutzen zu können.";

  /*
  Lizenz System
   */
  public static final String NO_VALID_LICENCE = "§8┃ §eSurvival §7» Diese Lizenz exestiert nicht. Es exestieren nur die §cNETHER §7und die §5END �7Lizenz.";
  public static final String ALREADY_BOUGHT_LICENCE = "§8┃ §eSurvival §7» Diese Lizenz hast du bereits erworben.";
  public static final String LICENCE_BUYING_NETHER = "§8┃ §eSurvival §7» Du hast die §cNETHER §7Lizenz f�r §e" + SurvivalLicence.NETHERLIZENZ.getPrice() + " §7erfolgreich erworben.";
  public static final String LICENCE_BUYING_END = "§8┃ §eSurvival §7» Du hast die §5END §7Lizenz f�r §e " + SurvivalLicence.ENDLIZENZ.getPrice() + " §7erfolgreich erworben.";
  public static final String LICENCE_SYNTAX = "§8┃ §eSurvival §7» §c/licence buy <NETHER|END>";
  public static final String LICENCE_SYNTAX_ERROR = "§8┃ §eSurvival §7» §cSyntax Error. Benutze: /licence buy <NETHER|END>";
  
  /**
   * Instanz
   */
  private Messages() {
    //Util
  }
}
