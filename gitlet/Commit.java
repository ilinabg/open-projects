package gitlet;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

/** The commit class that saves all metadata that corresponds
 *  to a particular commit and the respective blobs. This commit
 *  represents an 'in progress' commit.
 *  @author Divya Chandrasekaran and Ilina Bhaya-Grossman
 */
public class Commit {

    public Commit (Commit parent) {
        _blobs = new ArrayList<Blob>();
        _message = null;
        _time = null;
        _shaid = null;
        _parent = parent;
        if (_parent != null) {
            for (Blob b: _parent._blobs) {
                Blob copy = new Blob(b);
                _blobs.add(copy);
            }
        } else {
            _message = "initial commit";
        }
    }

    public Commit (Commit parent, String message) {
        this(parent);
        _message = message;
    }

    @Override
    public boolean equals(Object obj) {
        return getSHA().equals(((Commit) obj).getSHA());
    }

    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
        String time = dateFormat.format(_time);
        String c = "===\n";
        c += "Commit " + _shaid + "\n";
        c += time + "\n";
        c += _message + "\n";
        return c;
    }

    /** Accessor method to the SHA ID of this commit.
     *  @return  the SHA ID of this commit */
    public String getSHA () {
        return _shaid;
    }

    /** Accessor method to the message of this commit.
     *  @return  the message of this commit */
    public String getMessage () {
        return _message;
    }

    /** Accessor for all my blobs.
     *  @return   My array list of blobs */
    public ArrayList<Blob> getBlobs() {
        return _blobs;
    }

    /** Mutator method to the message of this commit.
     *  @param  message  the new message */
    public void setMessage (String message) {
        _message = message;
    }

    /** Writes all blobs to files in the working directory. */
    public void writeAllFiles () {
        for (Blob b: _blobs) {
            b.writeBlob();
        }
    }

    /** Writes this commit to the .gitlet directory. Should only
     *  occur once this commit has been updated. */
    public void writeToGitlet() {
        File file = new File(".gitlet/" + _shaid);
        try {
            file.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(_message + "\n" + _time.toString() + "\n");
            for (Blob b : _blobs){
                b.writeBlob(file);
                bw.write("\n");
            }

        } catch (IOException io) {
            Main.error("Writing to gitlet failed.");
        }
    }

    /** Updates this commit object to have the correct time stamp.
     *  Do not call update on a commit more than once. */
    public void update () {
        _time = new Date();
        byte[][] b = new byte[_blobs.size()][];
        int index = 0;
        for (Blob c:_blobs) {
            b[index] = c.getBytes();
            index++;
        }
        _shaid = Utils.sha1((Object[]) b);
    }

    /** Add a modified snapshot of a file to this working commit.
     *  @param  file  the file we want to add to the commit */
    public void add (File file){
        Blob b = new Blob (file.getName(), file);
        add(b);
    }
    
    /** Add a modified snapshot of a blob to this working commit.
     *  @param  b  the blob we want to add to the commit */
    public void add (Blob b){
        for (Blob o: _blobs) {
            if(o.getName().equals(b.getName())) {
                _blobs.remove(o);
                break;
            }
        }
        _blobs.add(b);
        _modified = true;
    }

    /** Remove a file in this working commit (untrack).
     *  @param  name  the name of the file the user wishes to remove */
    public void remove (String name){
        boolean removed = false;
        for (Blob o: _blobs) {
            if(o.getName().equals(name)) {
                _blobs.remove(o);
                removed = true;
                break;
            }
        }
        if(!removed) {
            Main.error("No reason to remove the file.");
        } else {
            _modified = true;
        }
    }


    /** Returns the blob with 'name' in this commit's harem of blobs. If
     *  the blob is not found, returns null.
     *  @param  name  the name of the blob you want
     *  @return       the blob that is called 'name' */
    public Blob find (String name) {
        for (Blob b: _blobs) {
            if (b.getName().equals(name)) {
                return b;
            }
        }
        return null;
    }

    /** Returns if the blob with 'name' in this commit's harem of blobs.
     *  @param  name  the name of the blob you want
     *  @return       whether _blobs contains the specified blob. */
    public boolean contains (String name) {
        for (Blob b: _blobs) {
            if (b.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /** Compares the time stamp of this commit to the commit passed in.
     *  If the other commit is more recent it will return -1, if this
     *  commit is more recent it will return 1. Otherwise it will return 0.
     *  @param  other  the commit we are comparing this commit to
     *  @return        the integer corresponding to the commit relation */
    public int compare (Commit other){
        if (_time.before(other._time)) {
            return 1;
        } else if (_time.after(other._time)) {
            return -1;
        }
        return 0;
    }

    /** List of files that are both PRESENT (not necessarily the same) in the two commits).
     *  @param  other  the other commit object
     *  @return  the array list of blobs that are in both commits */
    public ArrayList<Blob> sameFiles(Commit other) {
        ArrayList<Blob> blobs = new ArrayList<Blob>();
        for (Blob b: _blobs) {
            if (other.contains(b.getName())) {
                blobs.add(b);
            }
        }
        return blobs;
    }

    /** Checks to see that addedFiles and deletedFiles are empty lists
     *  assumes that the current commit comes BEFORE the provided OTHER commit.
     public ArrayList<Blob> sameFilesPresent(Commit other) {

     } */

    /** List of blobs that have been modified from the current commit to
     *  the provided commit, assuming that the current commit came before
     *  the OTHER argument.
     *  @param  other  the other commit object
     *  @return  the array list of blobs that were modified */
    public ArrayList<Blob> modifiedFiles (Commit other) {
        ArrayList<Blob> blobs = new ArrayList<Blob>();
        for (Blob b: _blobs) {
            if (other.contains(b.getName()) &&
                    !other.find(b.getName()).getSHA().equals(b.getSHA())) {
                blobs.add(b);
            }
        }
        return blobs;
    }

    /** List of files that are in current commit, not in the provided commit.
     *  @param  other  the other commit object
     *  @return  the array list of blobs that were deleted */
    public ArrayList<Blob> deletedFiles(Commit other){
        ArrayList<Blob> blobs = new ArrayList<Blob>();
        for (Blob b: _blobs) {
            if (!other.contains(b.getName())) {
                blobs.add(b);
            }
        }
        return blobs;
    }

    /** List of blobs that are not in current commit but in the provided commit.
     *  @param  other  the other commit object
     *  @return  the array list of the blobs added */
    public ArrayList<Blob> addedFiles (Commit other) {
        ArrayList<Blob> blobs = new ArrayList<Blob>();
        for (Blob b: _blobs) {
            if (!other.contains(b.getName())) {
                blobs.add(b);
            }
        }
        return blobs;
    }


    /** The time stamp for this commit. */
    private Date _time;

    /** The parent commit of this commit. */
    private Commit _parent;

    /** The message corresponding to this commit. */
    private String _message;

    /** Whether or not this commit has been modified
     * since previous commit. */
    private boolean _modified;

    /** The SHA ID of this commit. */
    private String _shaid;

    /** Array list of blob files I contain. */
    private ArrayList<Blob> _blobs;

}