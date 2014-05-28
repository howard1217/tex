package tex61;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/** Unit tests of InputParsers.
 *  @author Chia-Hao Chiao
 */
public class LineAssemblerTest {

    private LineAssembler testAssembler(List<String> list) {
        PageAssembler testPage = new PageCollector(list);
        return new LineAssembler(testPage);
    }

    @Test
    public void testWords() {
        List<String> list = new ArrayList<String>();
        LineAssembler test = testAssembler(list);
        test.setFill(false);
        test.addText("apple");
        test.addText("banana");
        test.finishWord();
        test.addWord("clementine");
        test.newLine();
        String output = list.get(0);
        String correct = "   applebanana clementine ";
        assertEquals("Words methods do not work", correct, output);
    }

    @Test
    public void testAddLine() {
        List<String> list = new ArrayList<String>();
        LineAssembler test = testAssembler(list);
        String line = "yo this is a test line.";
        test.addLine(line);
        String output = list.get(0);
        assertEquals("AddLine does not work", line, output);
    }

    @Test
    public void testSetTextWidth() {
        List<String> list = new ArrayList<String>();
        LineAssembler test = testAssembler(list);
        int testVal = 72;
        test.setTextWidth(72);
        assertEquals("Textwidth does not work.", testVal, 72);
    }

}
