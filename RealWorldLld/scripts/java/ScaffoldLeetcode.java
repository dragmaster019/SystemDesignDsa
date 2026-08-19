import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Scaffolds a LeetCode problem: creates <problemsDir>/<number>/ containing
 * <number>.java, <number>.cpp, <number>.js, <number>.py, each with the
 * problem statement inserted as a comment block.
 *
 * Run from this directory (scripts/java) so the default problemsDir
 * (../.. -> mock02) resolves correctly, or pass a problemsDir explicitly.
 *
 * Usage: java ScaffoldLeetcode <problemNumber> [problemsDir]
 */
public class ScaffoldLeetcode {
    private static final String[] LANGUAGE_EXTS = {".java", ".cpp", ".js", ".py"};

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: java ScaffoldLeetcode <problemNumber> [problemsDir]");
            System.exit(1);
        }

        String problemNumber = args[0];
        Path problemsDir = args.length > 1 ? Path.of(args[1]) : Path.of("../..");
        Path folder = problemsDir.resolve(problemNumber);
        Files.createDirectories(folder);

        Map<String, Object> problem = LeetcodeLib.fetchProblemByNumber(problemNumber);

        StringBuilder created = new StringBuilder();
        for (String ext : LANGUAGE_EXTS) {
            Path filePath = folder.resolve(problemNumber + ext);
            String prefix = LeetcodeLib.commentPrefixForExt(ext);
            String block = LeetcodeLib.buildCommentBlock(problem, prefix);
            LeetcodeLib.writeCommentBlock(filePath, block);

            if (created.length() > 0) created.append(", ");
            created.append(problemNumber).append(ext);
        }

        System.out.println("Created " + folder + "/ with " + created + " for \"" + problem.get("title") + "\"");
    }
}
