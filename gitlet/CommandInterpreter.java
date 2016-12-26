package gitlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.io.File;

/**
 * This class represents the command interpreter in gitlet.
 * @author Divya Chandrasekaran, Ilina Bhaya Grossman
 */
public class CommandInterpreter {

    public CommandInterpreter(String[] args) {
        if (new File(".gitlet").exists()) {
            initialized = true;
        }
        _commands = args;
    }

    /** This method processes the command that the command interpreter
     *  is initialized with and modifies the tree accordingly. */
    protected void processCommand() {
        int numArgs = _commands.length;
        if (numArgs == 0) {
            Main.error("Please enter a command.");
        }

        String command = _commands[0].toLowerCase();
        String operand = "";
        if (numArgs > 1) {
            operand = _commands[1].toLowerCase();
        }

        if(command.equals("init")) {
            init();
        } else if (initialized) {
            switch (command) {
                case "status":
                    checkOperand(1, numArgs);
                    status();
                case "log":
                    checkOperand(1, numArgs);
                    log();
                case "global-log":
                    checkOperand(1, numArgs);
                    globalLog();
                case "add":
                    checkOperand(2, numArgs);
                    add(operand);
                case "checkout":
                    checkOperand(2, numArgs);
                    checkout(operand); //COMPLICATED
                case "reset":
                    checkOperand(2, numArgs);
                    reset(operand);
                case "merge":
                    checkOperand(2, numArgs);
                    merge(operand);
                case "remove":
                    checkOperand(2, numArgs);
                    removeBranch(operand);
                case "commit":
                    checkOperand(2, numArgs);
                    commit(operand);
                case "branch":
                    checkOperand(2, numArgs);
                    branch(operand);
                case "rm-branch":
                    checkOperand(2, numArgs);
                    removeBranch(operand);
                case "find":
                    checkOperand(2, numArgs);
                    find(operand);
                case "rm":
                    checkOperand(2, numArgs);
                    removeFile(operand);
                default:
                    Main.error("No command with that name exists.");
            }
        } else {
             Main.error("Not in an initialized gitlet directory.");
        }
    }

    /** This method checks the operand number for this command. */
    public void checkOperand(int expected, int given) {
        if(expected != given) {
            Main.error("Incorrect operands.");
        }
    }

    /** This method initializes the gitlet version-control
     *  system and sets the first commit to 'initial commit'. */
    public void init() {
        File gitlet = new File(".gitlet");
        if(gitlet.exists()) {
            Main.error("A gitlet version-control system already exists in the current directory.");
        } else {
            gitlet.mkdir();
            _tree = new Tree();
        }
        initialized = true;

    }

    /** This method commits the files that have been modified
     *  since the previous commit. */
    public void commit(String message) {
        if (message.equals("")) {
            Main.error("Please enter a commit message.");
        } else {
            _tree.commit(message);
        }
        //NO CHANGES ERROR
    }

    /** This method adds files to the staging area of the
     *  current commit.
     *  @param  name  the name of the file to add */
    public void add(String name) {
        File f = new File(name);
        if (f.exists()) {
            _tree.add(f);
        } else {
            Main.error("File does not exist.");
        }
    }

    /** Displays what branches currently exist, and marks the
     * current branch with a *. Also displays what files have been
     * staged or marked for untracking. */
    public void status() {
        //NOPE
    }

    /**  */
    public void checkout(String name) {
        _tree.checkoutBranch(name);
    }

    /** Checks out all the files tracked by the given commit.
     *  Removes tracked files that are not present in the given
     *  file. Also moves the current branch's head to that commit
     *  node.
     *  @param  commitID  the commit id of the commit to reset */
    public void reset(String commitID) {
        if (commitID.isEmpty()) {
            Main.error("");
        }

    }

    /** Merges files from the given branch into the current branch.
     * @param  branch  the name of the branch to merge */
    public void merge(String branch) {
         _tree.merge(branch);
    }

    /** Starting at the current head commit, display information
     *  about each commit backwards along the commit tree until
     *  the initial commit. */
    public void log() {
        System.out.println(_tree.current.toString());
    }

    /** Like log, except displays information about all commits
     *  ever made. */
    public void globalLog() {
        System.out.println(_tree.toString());
    }

    /** Creates a new branch with the given name, and
     *  points it at the current head node.
     *  @param  name  the name of the file to add*/
    public void branch (String name) {
        _tree.addBranch(name); //TREE HANDLES ERROR

    }

    /** Prints out the ids of all commits that have the
     *  given commit message, one per line. If there are multiple
     *  such commits, it prints the ids out on separate lines.*/
    public void find (String message) {
        ArrayList<Commit> allCommits = new ArrayList<Commit>();
        for (Branch b: _tree.branches) {
            try {
                allCommits.add(b.find(message));
            } catch (IllegalArgumentException io) {
            }
        }

        if (!allCommits.isEmpty()) {
            for (Commit c : allCommits) {
                System.out.print(c.toString());
            }

        } else {
            Main.error("Found no commit with that message.");
        }
    }

    /** Untrack the file or insure it is not to be included in the
     *  next commit, even if it is tracked in the current commit.
     *  Remove the file from the working directory if it was tracked
     *  in the current commit.*/
    public void removeFile(String name) {
        File f = new File(name);
        if (f.exists()) {
            _tree.remove(f);
        } else {
            Main.error("File does not exist.");
        }
    }

    /** Deletes the branch with the given name. This only
     *  means to delete the pointer associated with the branch;
     *  it does not mean to delete all commits that were created
     *  under the branch. */
    public void removeBranch(String name) {
        _tree.removeBranch(name); //ERROR IS TAKEN CARE OF
    }

    /** The tree that holds all made commits. */
    Tree _tree;

    /** The array of command strings. */
    String[] _commands;

    /** Whether the gitlet version control has been initialized. */
    boolean initialized;
}
