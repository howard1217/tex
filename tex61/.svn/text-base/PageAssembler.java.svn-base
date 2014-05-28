package tex61;

/** A PageAssembler accepts complete lines of text (minus any
 *  terminating newlines) and turns them into pages, adding form
 *  feeds as needed.  It prepends a form feed (Control-L  or ASCII 12)
 *  to the first line of each page after the first.  By overriding the
 *  'write' method, subtypes can determine what is done with
 *  the finished lines.
 *  @author Chia-Hao Chiao
 */
abstract class PageAssembler {

    /** Create a new PageAssembler that sends its output to OUT.
     *  Initially, its text height is unlimited. It prepends a form
     *  feed character to the first line of each page except the first. */
    PageAssembler() {
    }

    /** Add LINE to the current page, starting a new page with it if
     *  the previous page is full. A null LINE indicates a skipped line,
     *  and has no effect at the top of a page. */
    void addLine(String line) {
        write(line);
        _currentheight += 1;
    }

    /** Set text height to VAL, where VAL > 0. */
    void setTextHeight(int val) {
        if (val < 0) {
            FormatException.reportError("TextHeight has to be positive.");
        }
        _textheight = val;
    }

    /** Perform final disposition of LINE, as determined by the
     *  concrete subtype. */
    abstract void write(String line);

    /** The current height of the page. */
    private int _currentheight = 0;
    /** The text height of the page. */
    private int _textheight;

}
