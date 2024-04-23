import java.util.Collection;
import java.util.Collections;
import java.util.Random;

/**
 * TODO: Implement the fillCache(), getResult(), and traceback() methods, in
 * that order. This is the biggest part of this project.
 */

public class SequenceAligner {
    private static Random gen = new Random();

    private String x, y;
    private int n, m;
    private String alignedX, alignedY;
    private Result[][] cache;
    private Judge judge;

    /**
     * Generates a pair of random DNA strands, where x is of length n and
     * y has some length between n/2 and 3n/2, and aligns them using the
     * default judge.
     */
    public SequenceAligner(int n) {
        this(randomDNA(n), randomDNA(n - gen.nextInt(n / 2) * (gen.nextInt(2) * 2 - 1)));
    }

    /**
     * Aligns the given strands using the default judge.
     */
    public SequenceAligner(String x, String y) {
        this(x, y, new Judge());
    }

    /**
     * Aligns the given strands using the specified judge.
     */
    public SequenceAligner(String x, String y, Judge judge) {
        this.x = x.toUpperCase();
        this.y = y.toUpperCase();
        this.judge = judge;
        n = x.length();
        m = y.length();
        cache = new Result[n + 1][m + 1];
        fillCache();
        traceback();
    }

    /**
     * Returns the x strand.
     */
    public String getX() {
        return x;
    }

    /**
     * Returns the y strand.
     */
    public String getY() {
        return y;
    }

    /**
     * Returns the judge associated with this pair.
     */
    public Judge getJudge() {
        return judge;
    }

    /**
     * Returns the aligned version of the x strand.
     */
    public String getAlignedX() {
        return alignedX;
    }

    /**
     * Returns the aligned version of the y strand.
     */
    public String getAlignedY() {
        return alignedY;
    }

    /**
     * TODO: Solve the alignment problem using bottom-up dynamic programming
     * algorithm described in lecture. When you're done, cache[i][j] will hold
     * the result of solving the alignment problem for the first i characters
     * in x and the first j characters in y.
     * <p>
     * Your algorithm must run in O(n * m) time, where n is the length of x
     * and m is the length of y.
     * <p>
     * Ordering convention: So that your code will identify the same alignment
     * as is expected in Testing, we establish the following preferred order
     * of operations: M (diag), I (left), D (up). This only applies when you
     * are picking the operation with the biggest payoff and two or more
     * operations have the same max score.
     */
    private void fillCache() {
        // Fill first result (i,j = 0)
        cache[0][0] = new Result(0, Direction.NONE);

        // Fill first row in cache with results
        for (int j = 1; j != (m + 1); ++j) {
            cache[0][j] = new Result((j * judge.getGapCost()), Direction.LEFT);
        }

        // Fill first column in cache with results
        for (int i = 1; i != (n + 1); ++i) {
            cache[i][0] = new Result((i * judge.getGapCost()), Direction.UP);
        }

        // Iterate through remaining squares

        // i changes row number
        for (int i = 1; i != (n+1); ++i) {
            // j changes column number
            for (int j = 1; j != (m + 1); ++j) {
                // Int to store best result so far (M I or D) and a var to store direction
                int best;
                Direction parent;

                // Calculate cases
                int M = this.judge.score(x.charAt(i - 1), y.charAt(j - 1)) + (cache[i-1][j-1]).getScore();
                int I = (cache[i][j-1]).getScore() + judge.getGapCost();
                int D = (cache[i-1][j]).getScore() + judge.getGapCost();

                // Compare cases, update values
                best = M;
                parent = Direction.DIAGONAL;

                if (I > best) {
                    best = I;
                    parent = Direction.LEFT;
                }
                if (D > best) {
                    best = D;
                    parent = Direction.UP;
                }

                // Assign a result to (i, j)
                cache[i][j] = new Result(best, parent);
            }
        }

    }

    /**
     * TODO: Returns the result of solving the alignment problem for the
     * first i characters in x and the first j characters in y. You can
     * find the result in O(1) time by looking in your cache.
     */
    public Result getResult(int i, int j) {
        return cache[i][j];
    }

    /**
     * TODO: Mark the path by tracing back through parent pointers, starting
     * with the Result in the lower right corner of the cache. Call Result.markPath()
     * on each Result along the path. The GUI will highlight all such marked cells
     * when you check 'Show path'. As you're tracing back along the path, build
     * the aligned strings in alignedX and alignedY (using Constants.GAP_CHAR
     * to denote a gap in the strand).
     * <p>
     * Your algorithm must run in O(n + m) time, where n is the length of x
     * and m is the length of y.
     */
    private void traceback() {
        // String builders for aligned strings
        StringBuilder sbX = new StringBuilder();
        StringBuilder sbY = new StringBuilder();

        // Keep track of indexes for navigation
        int i = n;
        int j = m;

        // Start at bottom right corner
        Result curr = cache[i][j];


        while (curr.getParent() != Direction.NONE) {
            // Mark that it is on the path
            curr.markPath();
            // Get parent
            Direction par = curr.getParent();

            // Handle cases based on parent, M (diag) -> [x+y add char], I (left) -> [y add char, x add _], D (up) -> [x add char, y add _]
            if (par == Direction.LEFT) {
                // Add chars to strings
                sbY.append(y.charAt(j-1));
                sbX.append(Constants.GAP_CHAR);

                // New curr is result to left, update indexes and curr
                j = j - 1;
                curr = cache[i][j];

            } else if (par == Direction.UP) {
                // Add chars to string
                sbY.append(Constants.GAP_CHAR);
                sbX.append(x.charAt(i-1));

                // New curr is result above, update indexes and curr
                i = i - 1;
                curr = cache[i][j];

            } else if (par == Direction.DIAGONAL) {
                // Add chars
                sbY.append(y.charAt(j-1));
                sbX.append(x.charAt(i-1));

                // New curr diagonal, update
                i = i - 1;
                j = j - 1;
                curr = cache[i][j];
            }
        }

        // Reverse
        sbX.reverse();
        sbY.reverse();

        // Convert SB's to strings
        alignedX = sbX.toString();
        alignedY = sbY.toString();

        // Mark (0, 0) as on path
        (cache[0][0]).markPath();
    }

    /**
     * Returns true iff these strands are seemingly aligned.
     */
    public boolean isAligned() {
        return alignedX != null && alignedY != null &&
                alignedX.length() == alignedY.length();
    }

    /**
     * Returns the score associated with the current alignment.
     */
    public int getScore() {
        if (isAligned())
            return judge.score(alignedX, alignedY);
        return 0;
    }

    /**
     * Returns a nice textual version of this alignment.
     */
    public String toString() {
        if (!isAligned())
            return "[X=" + x + ",Y=" + y + "]";
        final char GAP_SYM = '.', MATCH_SYM = '|', MISMATCH_SYM = ':';
        StringBuilder ans = new StringBuilder();
        ans.append(alignedX).append('\n');
        int n = alignedX.length();
        for (int i = 0; i < n; i++)
            if (alignedX.charAt(i) == Constants.GAP_CHAR || alignedY.charAt(i) == Constants.GAP_CHAR)
                ans.append(GAP_SYM);
            else if (alignedX.charAt(i) == alignedY.charAt(i))
                ans.append(MATCH_SYM);
            else
                ans.append(MISMATCH_SYM);
        ans.append('\n').append(alignedY).append('\n').append("score = ").append(getScore());
        return ans.toString();
    }

    /**
     * Returns a DNA strand of length n with randomly selected nucleotides.
     */
    private static String randomDNA(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++)
            sb.append("ACGT".charAt(gen.nextInt(4)));
        return sb.toString();
    }

}
