package net.mmm.survival.player;

import java.lang.reflect.Constructor;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Hotbar ermoeglicht das Senden einer Hotbar
 */
public class Hotbar {
  /**
   * Senden einer Hotbar-Nachricht an einen Spieler
   *
   * @param p Spieler
   */
  public static void send(final Player p) {
    try {
      Constructor<?> packetPlayerChatConstructor = null;
      String version = Bukkit.getServer().getClass().getName();
      version = version.substring(version.indexOf("craftbukkit.") + "craftbukkit.".length()).substring(0, version.indexOf("."));
      try {
        packetPlayerChatConstructor = Class.forName("net.minecraft.server." + version + ".PacketPlayOutChat").getConstructor(Class.forName(
            "net.minecraft.server." + version + ".IChatBaseComponent"), byte.class);
      } catch (final NoSuchMethodException e) {
        packetPlayerChatConstructor = Class.forName("net.minecraft.server." + version + ".PacketPlayOutChat").getConstructor(Class.forName(
            "net.minecraft.server." + version + ".IChatBaseComponent"), Class.forName("net.minecraft.server." + version + ".ChatMessageType"));
      } catch (final ClassNotFoundException ex) {
        ex.printStackTrace();
      }
      final Object icb = Class.forName("net.minecraft.server." + version + ".ChatMessage").getConstructor(String.class, Object[].class)
          .newInstance("§7Du hast erfolgreich deine Zone erstellt.", new Object[0]);
      final Object connection = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer").getMethod("getHandle")
          .getReturnType().getField("playerConnection").get(Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer")
              .getMethod("getHandle").invoke(Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer").cast(p)));
      Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer").getMethod("getHandle").getReturnType().getField(
          "playerConnection").getType().getMethod("sendPacket", Class.forName("net.minecraft.server." + version + ".Packet"))
          .invoke(connection, Objects.requireNonNull(packetPlayerChatConstructor).newInstance(icb, (byte) 2));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
