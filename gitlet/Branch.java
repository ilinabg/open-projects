package gitlet;

import java.io.File;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * This class represents a branch in gitlet, and contains all of the
 * commits contained in the specified branch.
 * @author Divya Chandrasekaran, Ilina Bhaya Grossman
 */
public class Branch implements Serializable{
    /**The parent of the commit, or the split point. */
    Commit parent;

    /**The current head of the branch. */
    Commit head;
    
    /**The commit in progress .*/
    Commit workingCommit;
    
    /**Linked list of the all the commits. */
    LinkedList<Commit> commits = new LinkedList<Commit>();
    
    /**Name of the current branch. */
    String name;
    
    /**Time stamp */
    Instant timeStamp;
    
    public Branch(Commit parent, String name) {
        this.parent = parent;
        this.head = parent;
        commits.add(parent);
        this.name = name;
        this.workingCommit = new Commit(head);
        timeStamp = Instant.now(); 
    }
    
    /**Adds a commit to the branch. Assumes that
     * working commit has all files.
     * @name The commit message */
    void addCommit(String name){
        workingCommit.setMessage(name);
        workingCommit.update();
        commits.addFirst(workingCommit);
        head = workingCommit;
        workingCommit = new Commit(head);
    }
    
    
    /**
     * Stages a blob
     * @blob Blob to be staged
     */
    void stage(Blob blob) {
        workingCommit.add(blob);
    }
    
    /**
     * Stages a file
     * @file File to be staged
     */
    void stage(File file) {
        workingCommit.add(file);
    }
    
    /**
     * Removes a file from staging area
     * @blob to be removed from staging area
     */
    void remove(Blob blob) {
        workingCommit.remove(blob.getName());
    }
    
    /**
     * Removes a file from staging area
     * @file to be removed from staging area
     */
    void remove(File file) {
        workingCommit.remove(file.getName());
    }
    
    /**Finds the corresponding commit.Assumes that
     * the commit is contained in the list.
     * @id SHA id of the commit.
     * @return Commit corresponding to value. Else null.
     */
    Commit find(String id){
        Iterator<Commit> it = commits.iterator();
        while (it.hasNext()) {
            Commit next = it.next();
            if (id.equals(next.getSHA())) {
                return next;
            }
        }
        throw new IllegalArgumentException("No such commit in this branch");
    }
    
    /**Finds a list of commits with corresponding
     * commit message.Assumes that at least one is present.
     * @message message that is associated with the commit
     * @return List of commits that contain that commit
     */
    ArrayList<Commit> findCommits(String message){
        ArrayList<Commit> commits = new ArrayList<Commit>();
        Iterator<Commit> it = commits.iterator();
        while (it.hasNext()) {
            Commit next = it.next();
            if (message.equals(next.getMessage())) {
                commits.add(next);
            }
        }
        return commits;
    }
    
    /**Retrieves all files from the current commit
     * @return List of Blobs. 
     */
    ArrayList<Blob> currentFiles() {
        return head.getBlobs();
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        Branch other = (Branch) obj;
        return other.commits.equals(commits)
                && other.name.equals(name);
        
    }
    
    public int compareTo(Branch branch) {
        if (this.timeStamp.isBefore(branch.timeStamp)) {
            return -1;
        } else if (this.timeStamp.isAfter((branch.timeStamp))) {
            return 1;
        } else {
            return 0;
        }
    }
    
    @Override 
    public String toString() {
        String holder = "";
        for (Commit com: commits) {
            holder += commits.toString();
        }
        return holder;
    }

}
