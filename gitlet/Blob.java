package gitlet;
import java.io.File;
import java.io.IOException;

/** Converts all of the data to byte code using the SHA algorithm
 *  @author Divya Chandrasekaran and Ilina Bhaya-Grossman
 */
public class Blob {

    public Blob (String name, File file) {
        _name = name;
        _bytecode = Utils.readContents(file);
        _shaid = Utils.sha1(_bytecode);
    }

    public Blob (Blob original) {
        _name = original.getName();
        _bytecode = original.getBytes();
        _shaid = original.getSHA();
    }

    @Override
    public boolean equals(Object obj) {
        return getSHA().equals(((Blob) obj).getSHA());
    }

    /** Accessor method to the SHA ID of this blob.
     *  @return  the SHA ID of this blob */
    public String getSHA () {
        return _shaid;
    }

    /** Accessor method to the name of this blob.
     *  @return  the name of this blob */
    public String getName () {
        return _name;
    }

    /** Accessor method to the bytecode of this blob.
     *  @return  the bytecode of this blob*/
    public byte[] getBytes () {
        return _bytecode;
    }

    /** Writes this blob to the file of this name. */
    public void writeBlob () {
        File file = new File(this.getName());
        try {
            file.createNewFile();
        } catch (IOException io) {
            System.out.println("WRITE BLOB");
        }
        Utils.writeContents(file, _bytecode);
    }

    /** Writes this blob to the specified file. */
    public void writeBlob (File file) {
        Utils.writeContents(file, _bytecode);
    }

    /** The SHA ID that corresponds to this file bytecode. */
    private String _shaid;

    /** The name of this file. */
    private String _name;

    /** The bytecode of the blob. */
    private byte[] _bytecode;

}
