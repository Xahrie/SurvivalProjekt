package net.mmm.survival.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

/**
 * Description
 *
 * @author Abgie on 07.10.2018 22:11
 * project SurvivalProjekt
 * @version 1.0
 * @since JDK 8
 */
public final class ObjectBuilder {

  public static String getStringOf(final Object object) throws IOException {
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    final ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

    objectOutputStream.writeObject(object);
    objectOutputStream.close();
    outputStream.close();

    final Base64.Encoder encoder = Base64.getEncoder();
    return encoder.encodeToString(outputStream.toByteArray());
  }

  public static Object getObjectOf(final String basic) {
    final Base64.Decoder decoder = Base64.getDecoder();
    final byte[] data = decoder.decode(basic);
    final ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(data);
    ObjectInputStream objectInputStream = null;
    try {
      objectInputStream = new ObjectInputStream(arrayInputStream);
    } catch (final IOException ex) {
      ex.printStackTrace();
    }

    Object result = null;
    try {
      if (objectInputStream != null) {
        result = objectInputStream.readObject();
      }
    } catch (final IOException | ClassNotFoundException ex) {
      ex.printStackTrace();
    }
    try {
      if (objectInputStream != null) {
        objectInputStream.close();
      }
      arrayInputStream.close();
    } catch (final IOException ex) {
      ex.printStackTrace();
    }

    return result;
  }
}
