package net.i2p.data;
/*
 * free (adj.): unencumbered; not under the control of others
 * Written by jrandom in 2003 and released into the public domain 
 * with no warranty of any kind, either expressed or implied.  
 * It probably won't make your computer catch on fire, or eat 
 * your children, but it might.  Use at your own risk.
 *
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.i2p.util.Log;

/**
 * Defines the actual payload of a message being delivered, including the 
 * standard encryption wrapping, as defined by the I2P data structure spec.<p />
 *
 * @author jrandom
 */
public class Payload extends DataStructureImpl {
    private final static Log _log = new Log(Payload.class);
    private byte[] _encryptedData;
    private byte[] _unencryptedData;
    
    public Payload() {
        setUnencryptedData(null);
	setEncryptedData(null);
    }
    
    /**
     * Retrieve the unencrypted body of the message.  
     *
     * @return body of the message, or null if the message has either not been
     *          decrypted yet or if the hash is not correct
     */
    public byte[] getUnencryptedData() { 
        return _unencryptedData; 
    }
    /**
     * Populate the message body with data.  This does not automatically encrypt
     * yet.
     * 
     */
    public void setUnencryptedData(byte[] data) { _unencryptedData = data; }
    public byte[] getEncryptedData() { return _encryptedData; } 
    public void setEncryptedData(byte[] data) { _encryptedData = data; }
    
    public int getSize() { 
	if (_unencryptedData != null)
	    return _unencryptedData.length;
	else if (_encryptedData != null)
	    return _encryptedData.length;
	else
	    return 0;
    }
    
    public void readBytes(InputStream in) throws DataFormatException, IOException {
        int size = (int)DataHelper.readLong(in, 4);
        _encryptedData = new byte[size];
        int read = read(in, _encryptedData);
        if (read != size)
            throw new DataFormatException("Incorrect number of bytes read in the payload structure");
        _log.debug("read payload: " + read + " bytes");
    }
    
    public void writeBytes(OutputStream out) throws DataFormatException, IOException {
        if (_encryptedData == null)
            throw new DataFormatException("Not yet encrypted.  Please set the encrypted data");
        DataHelper.writeLong(out, 4, _encryptedData.length);
        out.write(_encryptedData);
        _log.debug("wrote payload: " + _encryptedData.length);
    }
 
    
    public boolean equals(Object object) {
        if ( (object == null) || !(object instanceof Payload) )
            return false;
        Payload p = (Payload)object;
        return DataHelper.eq(getUnencryptedData(), p.getUnencryptedData()) &&
	       DataHelper.eq(getEncryptedData(), p.getEncryptedData());
    }
    
    public int hashCode() {
        return DataHelper.hashCode(getUnencryptedData());
    }
    
    public String toString() {
        StringBuffer buf = new StringBuffer(128);
        buf.append("[Payload: ");
        if (getUnencryptedData() != null)
            buf.append("\n\tData: ").append(DataHelper.toString(getUnencryptedData(), 16));
        else
            buf.append("\n\tData: *encrypted* = [").append(DataHelper.toString(getEncryptedData(), 16)).append("]");
        buf.append("]");
        return buf.toString();
    }
}
