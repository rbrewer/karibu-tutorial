package cs.karibu.quickstart;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Generates an example JSON object from SmartAMM server, to be used for testing. Not for production
 * use.
 * 
 * @author Robert Stephen Brewer, Aarhus University
 */
public class SAMReadingV001 {

  // JSON provided by SmartAMM looks like:
  // {"Time":"2014-07-03T11:55:17","Device":"0015BC0099999988","registers":[{"value":"16","attribute":1024,
  // "type":"Wh_W","channel":102},{"value":"280374","attribute":0,"type":"Wh_W","channel":100}]}

  private Date timestamp;
  private String dateString;
  private static final String DEVICE = "0015BC0099999988";
  private RegisterEntry[] registers;

  private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

  /**
   * Creates new SAMReadingV001 object initialized to the provided timestamp.
   * 
   * @param timestamp Timestamp to be used for new object.
   */
  public SAMReadingV001(Date timestamp) {
    // Cloning to prevent mutability
    // http://stackoverflow.com/q/13473155/140430
    this.timestamp = timestamp == null ? null : (Date) timestamp.clone();
    this.dateString = dateFormat.format(this.timestamp);

    this.registers = new RegisterEntry[2];
    registers[0] = new RegisterEntry(16, 1024, "Wh_W", 102);
    registers[1] = new RegisterEntry(280374, 0, "Wh_W", 100);
  }

  /**
   * Getter for timestamp.
   * 
   * @return the timestamp
   */
  public Date getTimestamp() {
    return this.timestamp == null ? null : (Date) this.timestamp.clone();
  }

  /**
   * Getter for registers.
   * 
   * @return the registers
   */
  public RegisterEntry[] getRegisters() {
    // Cloning to keep Findbugs happy. Really should just suppress the warning in this case.
    return registers.clone();
  }
  
  @Override
  public String toString() {
    return String.format("{\"Time\":\"%s\",\"Device\":\"%s\",\"registers\":[%s,%s]}", this.dateString,
        DEVICE, this.registers[0], this.registers[1]);
  }

  /**
   * Internal class to represent each entry in the register array.
   * 
   * @author Robert Stephen Brewer, Aarhus University
   */
  static class RegisterEntry {

    public int value;
    public int attribute;
    public String type;
    public int channel;

    /**
     * Represents a single register entry as provided by SmartAMM.
     * 
     * @param value The value of the measured attribute, assumed to be an integer.
     * @param attribute The attribute ID of the value.
     * @param type The unit the value is expressing (like "Wh_W" for watt-hour or watt).
     * @param channel The channel number (not clear what this means).
     */
    public RegisterEntry(int value, int attribute, String type, int channel) {
      this.value = value;
      this.attribute = attribute;
      this.type = type;
      this.channel = channel;
    }

    @Override
    public String toString() {
      return String.format("{\"value\":\"%d\",\"attribute\":%d,\"type\":\"%s\",\"channel\":%d}",
          value, attribute, type, channel);
    }
  }
}
