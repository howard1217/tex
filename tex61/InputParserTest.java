package tex61;

import java.io.PrintWriter;

import org.junit.Test;
import static org.junit.Assert.*;

/** Unit tests of InputParsers.
 *  @author Chia-Hao Chiao
 */
public class InputParserTest {

    @Test
    public void testInputParser() {
        String input = "hi this is a test"
            + "\\parindent{0}\\textwidth{68}"
            + "\\indent{4}\\parskip{0}"
            + "\\fill\\nofill"
            + "\\justify\\nojustify";
        String text = "hi this is a test STW 68 SI 4 "
            + "SP 0 SF true SF false SJ true SJ falseCLOSED";
        PrintWriter out = new PrintWriter(System.out);
        TestController cntrl = new TestController(out);
        InputParser src = new InputParser(input, cntrl);
        src.process();
        out.close();
        assertEquals("Parse Incorrectly", cntrl.getAccumulatedText(), text);
    }

    private class TestController extends Controller {
        TestController(PrintWriter out) {
            super(out);
            _accumulatedText = "";
        }

        @Override
        void addText(String text) {
            _accumulatedText += text;
        }

        @Override
        void endWord() {
            _accumulatedText += " ";
        }

        @Override
        void addNewline() {
            _accumulatedText += " NEWLINE ";
        }

        @Override
        void endParagraph() {
            _accumulatedText += " EP ";
        }

        @Override
        void formatEndnote() {
            _accumulatedText += " FE ";
        }

        @Override
        void setTextHeight(int val) {
            _accumulatedText += " STH " + val;
        }

        @Override
        void setTextWidth(int val) {
            _accumulatedText += " STW " + val;
        }

        @Override
        void setIndentation(int val) {
            _accumulatedText += " SI " + val;
        }

        @Override
        void setParSkip(int val) {
            _accumulatedText += " SP " + val;
        }

        @Override
        void setFill(boolean on) {
            _accumulatedText += " SF " + on;
        }

        @Override
        void setJustify(boolean on) {
            _accumulatedText += " SJ " + on;
        }

        @Override
        void close() {
            _accumulatedText += "CLOSED";
        }

        String getAccumulatedText() {
            return _accumulatedText;
        }

        private String _accumulatedText;

    }
}
