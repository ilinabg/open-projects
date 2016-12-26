package gitlet;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Divya Chandrasekaran and Ilina Bhaya-Grossman
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        /*for (String a: args) {
            System.out.println(a);
        }*/
        CommandInterpreter interp = new CommandInterpreter(args);
        interp.processCommand();
    }

    /** Report an error.  If _strict, then exit (code 2).  Otherwise,
     *  simply return. FORMAT is the message format (as for printf),
     *  and ARGS any additional arguments. */
    static void error(String format, Object... args) {
        System.err.printf(format, args);
        System.out.println();
    }

}
