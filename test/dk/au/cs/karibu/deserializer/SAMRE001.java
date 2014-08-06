package dk.au.cs.karibu.deserializer;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSONParseException;
import dk.au.cs.karibu.serialization.Deserializer;

/**
 * Deserializer for data sent by Develco's SmartAMM server. Based on the GKFRE003 deserializer.
 * 
 * @author Robert Stephen Brewer, Aarhus University
 * 
 */
public class SAMRE001 implements Deserializer {

  /** Date format used to parse dates from SmartAMM. */
  private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
  /**
   * Version number of output of deserializer. SmartAMM data does not include a version, but
   * deserializer could store the same received data in smarter format in the future, distinguished
   * by different versions.
   */
  private static final int VERSION = 1;

  @Override
  public BasicDBObject buildDocumentFromByteArray(byte[] payload) {
    Date theTimestamp;
    // The binary payload is actually a string in JSON format
    // Specify charset explicitly to avoid default platform encoding (which Findbugs complains
    // about). http://stackoverflow.com/a/10705897/140430
    String asJSON = new String(payload, Charset.forName("UTF-8"));

    // and Mongo has utils to convert that :)
    BasicDBObject dbo = (BasicDBObject) com.mongodb.util.JSON.parse(asJSON);

    // Read out the timestamp as string and write it back as Date object
    String asISODate = dbo.getString("Time");
    try {
      theTimestamp = dateFormat.parse(asISODate);
      dbo.put("Time", theTimestamp);
    }
    catch (ParseException e) {
      // If the received date cannot be parsed, then maybe the data format changed or it was
      // corrupted somehow. Need to throw an exception so that Karibu will store it in the
      // wrong format collection. For now, Karibu only looks for the JSONParseException, so
      // we throw that. Would be better to have a custom exception for this.
      throw new JSONParseException("Problems parsing date format", 0, e);
    }

    // Current data format from SmartAMM sends values as strings, not integers. So we
    // iterate over all values in the register array and convert the values from strings to
    // integers.
    @SuppressWarnings("unchecked")
    ArrayList<BasicDBObject> registers = (ArrayList<BasicDBObject>) dbo.get("registers");
    for (BasicDBObject dboForOneReading : registers) {
      String valueString = dboForOneReading.getString("value");
      try {
        int value = Integer.parseInt(valueString);
        dboForOneReading.put("value", value);
      }
      catch (NumberFormatException e) {
        // If the received value cannot be parsed, then maybe the data format changed or it was
        // corrupted somehow. Need to throw an exception so that Karibu will store it in the
        // wrong format collection. For now, Karibu only looks for the JSONParseException, so
        // we throw that. Would be better to have a custom exception for this.
        throw new JSONParseException("Problems parsing data value", 0, e);
      }
    }

    // Add current server time as well.
    dbo.put("servertimestamp", new Date());

    // Add deserializer version to document.
    dbo.put("dsversion", VERSION);

    return dbo;
  }

}
