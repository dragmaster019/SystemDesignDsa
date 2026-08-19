import java.nio.file.Path;
import java.util.Map;

/**
 * Fetches a LeetCode problem by its number and writes it into a single file
 * as a comment block.
 *
 * Usage: java InsertLeetcode <targetFile> <problemNumber>
 */

//cd RealWorldLld/mockTest/mock02/scripts/java
public class InsertLeetcode {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: java InsertLeetcode <targetFile> <problemNumber>");
            System.exit(1);
        }

        String targetFile = args[0];
        String problemNumber = args[1];

        Map<String, Object> problem = LeetcodeLib.fetchProblemByNumber(problemNumber);

        int dot = targetFile.lastIndexOf('.');
        String ext = dot >= 0 ? targetFile.substring(dot) : "";
        String prefix = LeetcodeLib.commentPrefixForExt(ext);
        String block = LeetcodeLib.buildCommentBlock(problem, prefix);

        LeetcodeLib.writeCommentBlock(Path.of(targetFile), block);
        System.out.println("Inserted LeetCode #" + problemNumber + " (" + problem.get("title") + ") into " + targetFile);
    }
}
