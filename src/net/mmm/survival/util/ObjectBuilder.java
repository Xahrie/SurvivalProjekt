package net.mmm.survival.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

import net.mmm.survival.util.logger.Logger;

/**
 * Wandle Objekte, die Serialize implementieren in Strings um und wieder zurueck.
 *
 * @author Abgie on 07.10.2018 22:11
 * project SurvivalProjekt
 * @version 1.0
 * @since JDK 8
 */
public final class ObjectBuilder {
  private static final Logger logger = new Logger(ObjectBuilder.class.getName());

  /**
   * Wandle ein Objekt in einen String um
   *
   * @param object zu serialisierendes Objekt
   * @return serialisierter String
   */
  public static String getStringOf(final Object object) {
    try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         final ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
      objectOutputStream.writeObject(object);
      final Base64.Encoder encoder = Base64.getEncoder();
      return encoder.encodeToString(outputStream.toByteArray());
    } catch (final IOException ex) {
      logger.error(ex);
    } finally {
      logger.exit();
    }
    return null;
  }

  /**
   * Wandle einen serialisierten String in ein Objekt um
   *
   * @param basic serialisierter String
   * @return Objekt
   */
  public static Object getObjectOf(final String basic) {
    final Base64.Decoder decoder = Base64.getDecoder();
    final byte[] data = decoder.decode(basic);
    try (final ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(data);
         final ObjectInputStream objectInputStream = new ObjectInputStream(arrayInputStream)) {
      return objectInputStream.readObject();
    } catch (final IOException ex) {
      logger.error(ex);
    } catch (final ClassNotFoundException ex) {
      logger.error("Klasse konnte nicht gefunden werden.", ex);
    } finally {
      logger.exit();
    }
    return null;
  }
}