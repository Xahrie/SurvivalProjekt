package net.mmm.survival.player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Deprecated
public class Hotbar {

  private static Class<?> craftplayerclass;
  private static Field playerconnection;
  private static Method gethandle, sendpacket;
  private static Constructor<?> packetPlayerChatConstructor, chatmessageConstructor;
  private static Object chatMessageTypeEnumObject;

  public static void setup() {
    String name = Bukkit.getServer().getClass().getName();
    name = name.substring(name.indexOf("craftbukkit.") + "craftbukkit.".length());
    final String SERVER_VERSION = name.substring(0, name.indexOf("."));
    try {
      craftplayerclass = Class.forName("org.bukkit.craftbukkit." + SERVER_VERSION + ".entity.CraftPlayer");
      final Class<?> PACKET_PLAYER_CHAT_CLASS = Class.forName("net.minecraft.server." + SERVER_VERSION + ".PacketPlayOutChat");
      Class<?> PACKET_CLASS = Class.forName("net.minecraft.server." + SERVER_VERSION + ".Packet");
      Class<?> ICHATCOMP = Class.forName("net.minecraft.server." + SERVER_VERSION + ".IChatBaseComponent");
      gethandle = craftplayerclass.getMethod("getHandle");
      playerconnection = gethandle.getReturnType().getField("playerConnection");
      sendpacket = playerconnection.getType().getMethod("sendPacket", PACKET_CLASS);
      try {
        packetPlayerChatConstructor = PACKET_PLAYER_CHAT_CLASS.getConstructor(ICHATCOMP, byte.class);
      } catch (NoSuchMethodException e) {
        Class<?> CHAT_MESSAGE_TYPE_CLASS = Class.forName("net.minecraft.server." + SERVER_VERSION + ".ChatMessageType");
        chatMessageTypeEnumObject = CHAT_MESSAGE_TYPE_CLASS.getEnumConstants()[2];

        packetPlayerChatConstructor = PACKET_PLAYER_CHAT_CLASS.getConstructor(ICHATCOMP, CHAT_MESSAGE_TYPE_CLASS);
      }
      Class<?> CHATMESSAGE = Class.forName("net.minecraft.server." + SERVER_VERSION + ".ChatMessage");

      chatmessageConstructor = CHATMESSAGE.getConstructor(String.class, Object[].class);
    } catch (Exception ex) {
    }
  }

  public static void send(Player p, String msg) {
    try {
      Object icb = chatmessageConstructor.newInstance(msg, new Object[0]);
      Object packet;
      try {
        packet = packetPlayerChatConstructor.newInstance(icb, (byte) 2);
      } catch (Exception e) {
        packet = packetPlayerChatConstructor.newInstance(icb, chatMessageTypeEnumObject);
      }
      Object craftplayerInst = craftplayerclass.cast(p);
      Object methodhHandle = gethandle.invoke(craftplayerInst);
      Object playerConnection = playerconnection.get(methodhHandle);

      sendpacket.invoke(playerConnection, packet);
    } catch (Exception e) {
    }
  }

}
