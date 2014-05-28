package tex61;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static tex61.FormatException.reportError;

/** Receives (partial) words and commands, performs commands, and
 *  accumulates and formats words into lines of text, which are sent to a
 *  designated PageAssembler.  At any given time, a Controller has a
 *  current word, which may be added to by addText, a current list of
 *  words that are being accumulated into a line of text, and a list of
 *  lines of endnotes.
 *  @author Chia-Hao Chiao
 */
class Controller {

    /** A new Controller that sends formatted output to OUT. */
    Controller(PrintWriter out) {
        _out = out;
    }

    /** Add TEXT to the end of the word of formatted text currently
     *  being accumulated. */
    void addText(String text) {
        _currentAssembler.addText(text);
    }

    /** Finish any current word of text and, if present, add to the
     *  list of words for the next line.  Has no effect if no unfinished
     *  word is being accumulated. */
    void endWord() {
        _currentAssembler.finishWord();
    }

    /** Finish any current word of formatted text and process an end-of-line
     *  according to the current formatting parameters. */
    void addNewline() {
        if (_currentAssembler.fill()) {
            endWord();
        } else {
            _currentAssembler.newLine();
        }
    }

    /** Finish any current word of formatted text, format and output any
     *  current line of text, and start a new paragraph. */
    void endParagraph() {
        _currentAssembler.endParagraph();
    }

    /** If valid, process into an endnote, first appending a reference
     *  to it to the line currently being accumulated. */
    void formatEndnote() {
        _refNum += 1;
        String start = "[" + _refNum + "]";
        _currentAssembler.addText(start + " ");
        _mainText.addText(start);
    }

    /** Set the current text height (number of lines per page) to VAL, if
     *  it is a valid setting.  Ignored when accumulating an endnote. */
    void setTextHeight(int val) {
        if (val < 0) {
            reportError("TextHeight has to be positive.");
        }
        _currentAssembler.setTextHeight(val);
    }

    /** Set the current text width (width of lines including indentation)
     *  to VAL, if it is a valid setting. */
    void setTextWidth(int val) {
        if (val < 0) {
            reportError("TextWidth has to be positive.");
        }
        _currentAssembler.setTextWidth(val);
    }

    /** Set the current text indentation (number of spaces inserted before
     *  each line of formatted text) to VAL, if it is a valid setting. */
    void setIndentation(int val) {
        if (val < 0) {
            reportError("Indentation has to be positive.");
        }
        _currentAssembler.setIndentation(val);
    }

    /** Set the current paragraph indentation (number of spaces inserted before
     *  first line of a paragraph in addition to indentation) to VAL, if it is
     *  a valid setting. */
    void setParIndentation(int val) {
        _currentAssembler.setParIndentation(val);
    }

    /** Set the current paragraph skip (number of blank lines inserted before
     *  a new paragraph, if it is not the first on a page) to VAL, if it is
     *  a valid setting. */
    void setParSkip(int val) {
        if (val < 0) {
            reportError("ParSkip has to be positive.");
        }
        _currentAssembler.setParSkip(val);
    }

    /** Iff ON, begin filling lines of formatted text. */
    void setFill(boolean on) {
        _currentAssembler.turnFill(on);
    }

    /** Iff ON, begin justifying lines of formatted text whenever filling is
     *  also on. */
    void setJustify(boolean on) {
        _currentAssembler.turnJustify(on);
    }

    /** Finish the current formatted document or endnote (depending on mode).
     *  Formats and outputs all pending text. */
    void close() {
        int pagesize = _lines.size();
        int linenum;
        _currentAssembler.endParagraph();
        _currentAssembler.newLine();
        if (_currentAssembler == _mainText) {
            _endNote.endParagraph();
            _endNote.newLine();
        } else if (_currentAssembler == _endNote) {
            _mainText.endParagraph();
            _mainText.newLine();
        }
        _printer = new PagePrinter(_out);
        for (int i = 0; i < _lines.size(); i += 1) {
            _currentHeight += 1;
            linenum = i % _mainText.getTextHeight();
            if (linenum == 0 && i != 0) {
                _out.print("\f");
            }
            _printer.write(_lines.get(i));
        }
        writeEndnotes();
    }

    /** Start directing all formatted text to the endnote assembler. */
    void setEndnoteMode() {
        _currentAssembler = _endNote;
        if (_firstEndnote) {
            _currentAssembler.setIndentation(Defaults.ENDNOTE_INDENTATION);
            _currentAssembler
                .setParIndentation(Defaults.ENDNOTE_PARAGRAPH_INDENTATION);
            _currentAssembler.setTextWidth(Defaults.ENDNOTE_TEXT_WIDTH);
            _currentAssembler.setParSkip(Defaults.ENDNOTE_PARAGRAPH_SKIP);
            _firstEndnote = false;
        }
    }

    /** Return to directing all formatted text to _mainText. */
    void setNormalMode() {
        _currentAssembler = _mainText;
    }

    /** Write all accumulated endnotes to _mainText. */
    private void writeEndnotes() {
        int linenum;
        for (int i = 0; i < _endnoteLines.size(); i += 1) {
            _currentHeight += 1;
            linenum = _currentHeight % _mainText.getTextHeight();
            if (linenum == 1 && _currentHeight != 1) {
                _out.print("\f");
            }
            _printer.write(_endnoteLines.get(i));
        }
    }

    /** True iff the first time for endnote. */
    private boolean _firstEndnote = true;
    /** The formatted output. */
    private PrintWriter _out;
    /** Number of next endnote. */
    private int _refNum = 0;
    /** The list of lines of endnotes. */
    private List<String> _endnoteLines = new ArrayList<String>();
    /** The list of lines. */
    private List<String> _lines = new ArrayList<String>();
    /** The pagecollector that collects lines in normal mode. */
    private PageCollector _mainTextCollector = new PageCollector(_lines);
    /** The lineassmbler that collects words in normal mode. */
    private LineAssembler _mainText = new LineAssembler(_mainTextCollector);
    /** The pagecollector that collects lines in endnotes. */
    private PageCollector _endNoteCollector = new PageCollector(_endnoteLines);
    /** The lineassambler that collects words in endnotes. */
    private LineAssembler _endNote = new LineAssembler(_endNoteCollector);
    /** The current lineassambler. */
    private LineAssembler _currentAssembler = _mainText;
    /** The printer. */
    private PagePrinter _printer;
    /** The current printed text height. */
    private int _currentHeight = 0;
}

