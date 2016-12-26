package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

/**This class represents a tree in git that contains several branches in the .gitlet
 * directory
 * @author Divya Chandrasekaran, Ilina Bhaya-Grossman
 */
public class Tree implements Serializable {
    /*A list of all the branches*/
    ArrayList<Branch> branches = new ArrayList<Branch>();

    /*Current branch.*/
    Branch current;
    
  
    /**This is the constructor that should be used when instantiated the .gitlet
     * directory. Else, use other constructor.
     */
    public Tree(){
        Commit initialCommit = new Commit(null);
        Branch master = new Branch(initialCommit, "master");
        current = master;
        branches.add(current);
    }
    
    /**Adds a file to the staging area. Assumes file exists.
     * @param file File to be staged.
     */
    void add(File file){
        current.stage(file);
    }
    
    /**Remove a file from staging area. Assumes file exists.
     * @param file File to be removed from staging area.
     */
    void remove(File file) {
        current.remove(file);
    }
    
    /**Adds the designated branch to the tree.
     * @param name Name of the branch.
     */
    void addBranch(String name) {
        if (containsBranch(name) != -1) {
            Main.error("A branch "
                    + "with that name already exists.");
        }else {
            Commit parentCommit = current.head;
            Branch br = new Branch(parentCommit, name);
            branches.add(br);
        }
    }

    /**
     * Removes the designated branch from the tree.
     * @param name Name of branch.
     */
    void removeBranch(String name) {
        Branch b = findBranch(name);
        if (b.equals(current)) {
            throw new IllegalArgumentException("Cannot "
                    + "remove current branch.");
        } else {
            branches.remove(b);
        }
    }

    /**Officially creates and saves a commit with the given files and
     * commit message and saves it in the tree.
     * @param commitMessage Message for the commit statement
     */
    void commit(String commitMessage) {
        current.addCommit(commitMessage);
    }

    /**
     * Checks out the necessary files and sends it back
     * to the pseudo main program.
     * @commitID CommitID of the commit to find file in, null
     * if no specific commitID
     * @fileName the name of the File to be retrieved
     * @return Blob containing the value of the file to be overwritten
     */
    Blob checkoutFile(String commitID, String fileName) {
        if (commitID == null ) { 
            return findBlob(fileName);
        } else {
            return findBlob(fileName, commitID);
        }
    }

    /**
     * Checks out the current branch, changes the  head.
     * @param branchName Name of the branch
     * @return A list of all of the blobs of files that is most recent
     * for the that branch. All files that can be used wto be overwritten.
     */
    ArrayList<Blob> checkoutBranch (String branchName) {
        Branch b = findBranch(branchName);
        current = b;
        return b.currentFiles();
    }
    
    /**
     * Takes care of the merge command, given the name of another existing branch.
     * Overwrites conflicting files accordingly.
     * @param branchName The name of the branch of the other branch to merged from. 
     */
    void merge(String branchName) {
        Branch branch = findBranch(branchName);
        Commit splitPoint = splitPoint (branch);
        ArrayList<Blob> curBlobs = current.currentFiles();
        ArrayList<Blob> otherBlobs = branch.currentFiles();
        if (splitPoint.equals(branch.head)){
            return;
        }else if (splitPoint.equals(current.head)) {
            current.workingCommit = branch.head;
            current.addCommit(current.workingCommit.getMessage()); /*----CHECKOUT----**/
            return;
        }
        
        ArrayList<Blob> splittoCurrentMods = splitPoint.modifiedFiles(current.head);
        ArrayList<Blob> splittoOtherMods = splitPoint.modifiedFiles(branch.head);
        
        /*Takes care of modifications made in the other branch, but not in my current branch */
        for (Blob blob: splittoOtherMods) {
            if (!contains(blob, splittoCurrentMods)) {
                current.workingCommit.add(blob);
            }
        }
        ArrayList<Blob> splittoCurrentAdds = splitPoint.addedFiles(current.head);
        ArrayList<Blob> splittoOtherAdds = splitPoint.addedFiles(branch.head);
        HashMap<Blob, Blob> moreConflicts = new HashMap<Blob, Blob>();
        
        /*If a new file was added to the other branch since the split, it is either  noted as a conflict if
         * also present in my current branch, or is added to the commit. */
        for (Blob addition: splittoOtherAdds) {
            if (!contains(addition, splittoCurrentAdds)) {
                current.workingCommit.add(addition);
            } else {
                moreConflicts.put(get(addition.getName(), splittoCurrentAdds), addition);
            }
        }
        
        /*If a file has been deleted in the other branch, and is remains unmodified in the current branch,
         * then remove's the file from my current staging area. If a modification was made in the current branch but
         * is absent in the other branch, then the other branch's removed file is treated as an empty file, and added to the 
         * list of conflicts. 
         */
        ArrayList<Blob> splittoOtherDels = splitPoint.deletedFiles(branch.head); 
        for (Blob otherDeleted: splittoOtherDels) {
            if (!contains(otherDeleted, splittoCurrentMods)) {
                current.remove(otherDeleted);
            } else {
                moreConflicts.put(get(otherDeleted.getName(), splittoCurrentMods), 
                        new Blob(otherDeleted.getName(), new File(otherDeleted.getName())));
            }
        }
        
        /* If the files in the current directory have been deleted  since split but have been
         * modified, add to the conflict list. Else, continue on with process.
         */
        ArrayList<Blob> splittoCurrentDels = splitPoint.deletedFiles(current.head); 
        for (Blob currentDeleted: splittoCurrentDels) {
            if (!contains(currentDeleted, splittoOtherMods)) {
                continue;
            } else {
                moreConflicts.put(currentDeleted, 
                        new Blob(currentDeleted.getName(), new File(currentDeleted.getName())));
            }
        }
        
        /*If there are two new files, and both have different version, or there have been conflicts acquired from
         * any of the processes above, program will write the corresponding files with a formatted error version, 
         * and return from program without committing */
        if (inConflictModifications (splittoCurrentMods, splittoOtherMods) || !moreConflicts.isEmpty()) {
            if (inConflictModifications (splittoCurrentMods, splittoOtherMods)) {
                writeConflicts (conflictingFiles (splittoCurrentMods, splittoOtherMods));
            }
            if (!moreConflicts.isEmpty()) {
                writeConflicts(moreConflicts);
            }
            System.out.println("Encountered a merge conflict.");
            return;
        }
        current.addCommit("Merged " + current.name + " with " + branchName);
    }
    

    /**
     * Replaces the contents of the conflicting files with
     * <<<<<<< HEAD
     * contents of file in current branch
     * =======
     * contents of file in given branch
     * >>>>>>>
     * @param conflicts A HashMap of conflicts that maps the current branch's version of the file
     * to the other branch's version of the file. 
     */
    void writeConflicts (HashMap<Blob, Blob> conflicts) {
        Set<Blob> keys = conflicts.keySet();
        byte[] firstLine = new String("<<<<<<< HEAD").getBytes();
        byte[] thirdLine = new String("=======").getBytes();
        byte[] fifthLine = new String(">>>>>>>").getBytes();
        byte[] finalLine = System.getProperty("line.separator").getBytes();
        for (Blob key: keys) {
            byte[] currentFileContents = key.getBytes(); //assumes that the blob ends with a new line
            byte[] fourthLine = conflicts.get(key).getBytes();
            byte[] finalContents = new byte[firstLine.length 
                                            + currentFileContents.length + thirdLine.length
                                            + fourthLine.length + fifthLine.length + finalLine.length];
            System.arraycopy(firstLine, 0, finalContents, 0, firstLine.length);
            int index = firstLine.length;
            System.arraycopy(currentFileContents, 0, finalContents, index, currentFileContents.length);
            index += currentFileContents.length;
            System.arraycopy(thirdLine, 0, finalContents, index, thirdLine.length);
            index += thirdLine.length;
            System.arraycopy(fourthLine, 0, finalContents, index, fourthLine.length);
            index += fourthLine.length;
            System.arraycopy(fifthLine, 0, finalContents, index, fifthLine.length);
            index += fifthLine.length;
            System.arraycopy(finalLine, 0, finalContents, index, finalLine.length);
            
            Utils.writeContents(new File(key.getName()), finalContents);

        }
    }
    
    @Override
    public String toString() {
        String holder = "";
        for (Branch b: branches) {
            holder += b.toString();
        }
        return holder;
    }
    
    @Override
    public int hashCode() {
        return branches.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        Tree tree = (Tree) obj;
        return tree.branches.equals(branches) && tree.current.equals(current);
    }

    /*==============================PRIVATE METHODS===========================================*/

    /**
     * Returns whether a blob with the given name exists within the 
     * given list of blobs.
     * @param blob Blob that is to be searched for.
     * @param list List of blobs
     * @return Whether the blob exists in the list
     */
    private boolean contains(Blob blob, ArrayList<Blob> list) {
        for (Blob b: list) {
            if (blob.getName().equals(b.getName())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns the a blob in the given list with the corresponding name.
     * Assumes that there exists such a blob, else an error is thrown.
     * @param blobName Name of the blob.
     * @param blobs The list of blobs to find it from.
     * @return The corresponding blob with the given name.
     */
    private Blob get(String blobName, ArrayList<Blob> blobs) {
        for (Blob b: blobs) {
            if (b.getName().equals(blobName)) {
                return b;
            }
        }
        throw new IllegalArgumentException ("Does not exist");
    }



    /**
     * Returns a HashMap linking the current branch file version to the other branch's file version.
     * This assumes that there exist conflicts. 
     * @param cur The modifications made from split point to the current branch's recent commit.
     * @param other The modifications made from split point to the other branch's recent commit.
     * @return HashMap linking the conflicting files, from current branch's file version to other
     * branch's file version. 
     */
    private HashMap<Blob, Blob> conflictingFiles (ArrayList<Blob> cur, ArrayList<Blob> other) {
        HashMap<Blob, Blob> conflicts = new HashMap<Blob, Blob>();
        for (Blob cblob: cur) {
            if (contains(cblob, other)) {
                conflicts.put(cblob, get(cblob.getName(), other));
            }
        }
        return conflicts;
    }

    /**
     * Returns whether two lists have any files that are in conflict.
     * @param b1 List of one branches modified files from split point
     * @param b2 List of the other branches modified files from split point
     * @return Whether the two lists have files in common 
     */
    private boolean inConflictModifications(ArrayList<Blob> b1, ArrayList<Blob> b2) {
        for (Blob b: b1) {
            if (contains (b, b2)) {
                return true;
            }
        }
        return true;
    }

    /**
     * Returns the split point of the provided branch and the current branch.
     * @param branch The branch to be compared to. Not necessarily before the current branch.
     * @return Commit that is the split point
     */
    private Commit splitPoint (Branch branch) {
        if (current.compareTo(branch) < 0) {
            return branch.parent;
        } else {
            return current.parent;
        }
    }

    /**
     * Returns the index of the branch in the internal data.
     * @param name Name of branch.
     * @return Index of the branch in the internal data structure.
     */
    private int containsBranch(String name) {
        int index = 0;
        for (Branch b: branches) {
            if (name.equals(b.name)) {
                return index;
            }
            index ++;
        }
        return -1;
    }

    /**Returns the designated branch from the tree.
     * @param name Name of the tree.
     * @return The branch in the tree, else null
     */
    private Branch findBranch(String name) {
        int index = containsBranch(name);
        if (index == -1){
            throw new IllegalArgumentException("Branch does not exist.");
        } else {
            return branches.get(index);
        }
    }

    /**Returns the designated blob from the tree, in the current commit. 
     * @param name Name of file
     * @return Blob of the tree*/
    private Blob findBlob(String name){
        return findBlob(name, current.head.getSHA());
    }

    /**Returns the designated blob from the tree form the specified commit.
     * This is restricted to the current branch. 
     * @param name Name of file
     * @return Blob of the tree*/
    private Blob findBlob(String name, String commitID){
        Commit com = current.find(name);
        return com.find(name);
    }
    
}
