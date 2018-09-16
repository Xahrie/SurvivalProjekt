package de.mmm.survival.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Hotbar ermoeglicht das Senden einer Hotbar
 */
public class Hotbar {

  private static Class<?> craftPlayerClass;
  private static Field playerConnection;
  private static Method getHandle, sendPacket;
  private static Constructor<?> packetPlayerChatConstructor, chatmessageConstructor;

  /**
   * setup der Hotbar
   */
  public static void setup() {
    String sServerVersion = Bukkit.getServer().getClass().getName();
    sServerVersion = sServerVersion.substring(sServerVersion.indexOf("craftbukkit.") + "craftbukkit.".length());
    sServerVersion = sServerVersion.substring(0, sServerVersion.indexOf("."));
    try {
      final Class<?> PACKET_CLASS = Class.forName("net.minecraft.server." + sServerVersion + ".Packet");
      craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + sServerVersion + ".entity.CraftPlayer");
      getHandle = craftPlayerClass.getMethod("getHandle");
      playerConnection = getHandle.getReturnType().getField("playerConnection");
      sendPacket = playerConnection.getType().getMethod("sendPacket", PACKET_CLASS);
      //setup Chatmessagetype
      setupChatMesageType(sServerVersion);
      //setup Chatmessage
      final Class<?> CHATMESSAGE = Class.forName("net.minecraft.server." + sServerVersion + ".ChatMessage");
      chatmessageConstructor = CHATMESSAGE.getConstructor(String.class, Object[].class);

    } catch (final NoSuchMethodException | NoSuchFieldException | ClassNotFoundException ex) {
      ex.printStackTrace();
    }
  }

  private static void setupChatMesageType(final String sServerVersion) throws NoSuchMethodException, ClassNotFoundException {
    final Class<?> PACKET_PLAYER_CHAT_CLASS = Class.forName("net.minecraft.server." + sServerVersion + ".PacketPlayOutChat");
    final Class<?> ICHATCOMP = Class.forName("net.minecraft.server." + sServerVersion + ".IChatBaseComponent");
    try {
      packetPlayerChatConstructor = PACKET_PLAYER_CHAT_CLASS.getConstructor(ICHATCOMP, byte.class);
    } catch (final NoSuchMethodException e) {
      final Class<?> CHAT_MESSAGE_TYPE_CLASS = Class.forName("net.minecraft.server." + sServerVersion + ".ChatMessageType");
      packetPlayerChatConstructor = PACKET_PLAYER_CHAT_CLASS.getConstructor(ICHATCOMP, CHAT_MESSAGE_TYPE_CLASS);
    }
  }

  /**
   * Senden einer Hotbar-Nachricht an einen Spieler
   *
   * @param p Spieler
   */
  public static void send(final Player p) {
    try {
      final Object icb = chatmessageConstructor.newInstance("§7Du hast erfolgreich deine Zone erstellt.", new
              Object[0]);
      final Object packet;
      packet = packetPlayerChatConstructor.newInstance(icb, (byte) 2);
      final Object craftplayerInst = craftPlayerClass.cast(p);
      final Object methodhHandle = getHandle.invoke(craftplayerInst);
      final Object playerConnection = Hotbar.playerConnection.get(methodhHandle);

      sendPacket.invoke(playerConnection, packet);
    } catch (final IllegalAccessException | InstantiationException | InvocationTargetException ex) {
      ex.printStackTrace();
    }
  }

}
