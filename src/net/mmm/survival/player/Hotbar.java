package net.mmm.survival.player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Hotbar ermoeglicht das Senden einer Hotbar
 */
public class Hotbar {
  private static Field playerConnection;
  private static Method getHandle, sendPacket;
  private static Constructor<?> chatmessageConstructor;

  /**
   * setup der Hotbar
   */
  public static void setup() {
    try {
      getHandle = Class.forName("org.bukkit.craftbukkit." + Bukkit.getServer().getVersion() + ".entity.CraftPlayer").getMethod("getHandle");
      playerConnection = getHandle.getReturnType().getField("playerConnection");
      sendPacket = playerConnection.getType().getMethod("sendPacket", Class.forName("net.minecraft.server." + Bukkit.getServer().getVersion() + ".Packet"));
      chatmessageConstructor = Class.forName("net.minecraft.server." + Bukkit.getServer().getVersion() + ".ChatMessage").getConstructor(String.class, Object[].class);
    } catch (final ClassNotFoundException | NoSuchMethodException | NoSuchFieldException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Senden einer Hotbar-Nachricht an einen Spieler
   *
   * @param p Spieler
   */
  public static void send(final Player player) {
    try {
      final Object icb = chatmessageConstructor.newInstance("§7Du hast erfolgreich deine Zone erstellt.", new Object[0]);
      final Object craftplayerInst = Class.forName("org.bukkit.craftbukkit." + Bukkit.getServer().getVersion() + ".entity.CraftPlayer").cast(player);
      final Object methodhHandle = getHandle.invoke(craftplayerInst);

      sendPacket.invoke(Hotbar.playerConnection.get(methodhHandle), setupChatMesageType(Bukkit.getServer().getVersion()).newInstance(icb, (byte) 2));
    } catch (final ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException ex) {
      ex.printStackTrace();
    }
  }

  private static Constructor<?> setupChatMesageType(final String version) throws NoSuchMethodException, ClassNotFoundException {
    final Class<?> packetPlayerChatClass = Class.forName("net.minecraft.server." + version + ".PacketPlayOutChat");
    final Class<?> ichatcomp = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent");
    final Class<?> chatMessageTypeClass = Class.forName("net.minecraft.server." + version + ".ChatMessageType");

    return packetPlayerChatClass.getConstructor(ichatcomp, chatMessageTypeClass);
  }

}
