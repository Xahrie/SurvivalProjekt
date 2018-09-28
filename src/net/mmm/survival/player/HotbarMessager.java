package net.mmm.survival.player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Manager fuer das Senden von Hotbar-Packets an einen Spieler (Nachricht in der
 * Actionbar).
 *
 * @author Abgie on 27.09.2018 17:26
 * project SurvivalProjekt
 * @version 1.0
 * @see net.mmm.survival.player.SurvivalPlayer
 * @since JDK 8
 */
class HotbarMessager {
  private static Class<?> packetClass = null, craftplayerclass = null; // Class instances, used to get fields or methods for classes
  private static Constructor<?> chatmessageConstructor, packetPlayerChatConstructor = null; // Constructors for those classes

  static {
    String name = Bukkit.getServer().getClass().getName();
    name = name.substring(name.indexOf("craftbukkit.") + "craftbukkit.".length());
    final String SERVER_VERSION = name.substring(0, name.indexOf("."));
    try {
      // This here sets the class fields.
      craftplayerclass = Class.forName("org.bukkit.craftbukkit." + SERVER_VERSION + ".entity.CraftPlayer");
      final Class<?> packetPlayerChatClass = Class.forName("net.minecraft.server." + SERVER_VERSION + ".PacketPlayOutChat");
      packetClass = Class.forName("net.minecraft.server." + SERVER_VERSION + ".Packet");
      final Class<?> ichatcomp = Class.forName("net.minecraft.server." + SERVER_VERSION + ".IChatBaseComponent");
      packetPlayerChatConstructor = Optional.of(packetPlayerChatClass.getConstructor(ichatcomp, byte.class)).get();
      final Class<?> chatmessage = Class.forName("net.minecraft.server." + SERVER_VERSION + ".ChatMessage");
      // If it cannot find the constructor one way, we try to get the declared constructor.
      try {
        chatmessageConstructor = Optional.of(chatmessage.getConstructor(String.class, Object[].class)).get();
      } catch (final NoSuchMethodException e) {
        chatmessageConstructor = Optional.of(chatmessage.getDeclaredConstructor(String.class, Object[].class)).get();
      }

    } catch (final ClassNotFoundException | NoSuchMethodException | SecurityException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Sends the hotbar message 'message' to the player 'player'
   *
   * @param player Player
   * @param message Message
   */
  static void sendHotBarMessage(final Player player, final String message) {
    try {
      final Object iChatComponentBase = chatmessageConstructor.newInstance(message, new Object[0]); // IChatComponentBase instance
      final Object packet = packetPlayerChatConstructor.newInstance(iChatComponentBase, (byte) 2); // Packet
      final Object craftplayerInst = craftplayerclass.cast(player); // casts to a craftplayer
      final Optional<Method> methodOptional = Optional.of(craftplayerclass.getMethod("getHandle")); // method for craftplayer's handle
      final Object methodhHandle = methodOptional.get().invoke(craftplayerInst); // invokes the method above.
      final Object playerConnection = methodhHandle.getClass().getField("playerConnection").get(methodhHandle); // gets player's connection
      Optional.of(playerConnection.getClass().getMethod("sendPacket", packetClass)).get().invoke(playerConnection, packet); // sends the packet.

    } catch (final InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException |
        NoSuchMethodException | SecurityException | NoSuchFieldException ex) {
      ex.printStackTrace();
    }
  }

}
