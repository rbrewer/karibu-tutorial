package cs.karibu.quickstart;

import java.nio.charset.Charset;

/**
 * Fake serializer for SmartAMM data, used to test deserializer only. Not for production use.
 * 
 * @author Robert Stephen Brewer, Aarhus University
 */
public class SAMRE001Serializer implements dk.au.cs.karibu.serialization.Serializer<SAMReadingV001> {

  @Override
  public byte[] serialize(SAMReadingV001 myData) {
    // Specify charset explicitly to avoid default platform encoding (which Findbugs complains
    // about). http://stackoverflow.com/a/10705897/140430
    return myData.toString().getBytes(Charset.forName("UTF-8"));
  }
}
