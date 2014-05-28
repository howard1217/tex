package tex61;

import java.util.ArrayList;
import java.util.List;

import static tex61.FormatException.reportError;

/** An object that receives a sequence of words of text and formats
 *  the words into filled and justified text lines that are sent to a receiver.
 *  @author Chia-Hao Chiao
 */
class LineAssembler {

    /** A new, empty line assembler with default settings of all
     *  parameters, sending finished lines to PAGES. */
    LineAssembler(PageAssembler pages) {
        _pages = pages;
    }

    /** Add TEXT to the word currently being built. */
    void addText(String text) {
        _currentWord += text;
    }

    /** Finish the current word, if any, and add to words being accumulated. */
    void finishWord() {
        if (!_currentWord.equals("")) {
            addWord(_currentWord);
        }
    }

    /** Add WORD to the formatted text. */
    void addWord(String word) {
        if (_fill) {
            int indent = _indentation + _paragraphIndentation;
            if (_isatbop && _listlength + word.length()
                - 1 + indent >= _textWidth) {
                _listlength -= 1;
                newLine();
            } else if (!_isatbop && _listlength + _indentation
                + word.length() - 1 >= _textWidth) {
                _listlength -= 1;
                newLine();
            }
        }
        _currentList.add(word);
        _listlength += word.length() + 1;
        _currentWord = "";
    }

    /** Add LINE to our output, with no preceding paragraph skip.  There must
     *  not be an unfinished line pending. */
    void addLine(String line) {
        int linenum = ((PageCollector) _pages).size() % _textHeight;
        boolean attop = (linenum == 0);
        if (_isatbop && !attop) {
            for (int i = 0; i < _paragraphSkip; i += 1)  {
                if (((PageCollector) _pages).size() >= _textHeight) {
                    break;
                }
                _pages.addLine("");
            }
        }
        _pages.addLine(line);
    }

    /** Set the current indentation to VAL. VAL >= 0. */
    void setIndentation(int val) {
        _indentation = val;
    }

    /** Set the current paragraph indentation to VAL. VAL >= 0. */
    void setParIndentation(int val) {
        if (val + _indentation < 0) {
            reportError("Total indentation has to be positive.");
        }
        _paragraphIndentation = val;
    }

    /** Set the text width to VAL, where VAL >= 0. */
    void setTextWidth(int val) {
        _textWidth = val;
        if (_textWidth < _indentation + _paragraphIndentation) {
            reportError("Textwidth should be larger.", val);
        }
    }

    /** Iff ON, set fill mode. */
    void setFill(boolean on) {
        _fill = on;
        if (on) {
            String toadd = "";
            int indentation;
            if (_isatbop) {
                indentation = _indentation + _paragraphIndentation;
            } else {
                indentation = _indentation;
            }
            for (int j = 0; j < indentation; j += 1) {
                toadd += " ";
            }
            if (_justify && !_isateop && _currentList.size() > 1) {
                setJustify(_justify);
                addLine(toadd + _sentence);
                _sentence = "";
            } else {
                for (int i = 0; i < _currentList.size(); i += 1) {
                    toadd += _currentList.get(i) + " ";
                }
                addLine(toadd);
            }
            _isatbop = false;
        }
    }

    /** Iff ON, set justify mode (which is active only when filling is
     *  also on). */
    void setJustify(boolean on) {
        _justify = on;
        if (on && _fill) {
            if (_currentList.size() > 1 && !_isateop) {
                int w = _textWidth;
                int n = _currentList.size();
                int l = _listlength - n + 1;
                int p = _indentation;
                int totalblanks = 0;
                int blanks = 0;
                if (_isatbop) {
                    p = _indentation + _paragraphIndentation;
                }
                int b = w - l - p;
                _sentence += _currentList.get(0);
                if (b >= 3 * (_currentList.size() - 1)) {
                    _sentence += "   ";
                    for (int j = 1; j < _currentList.size(); j += 1) {
                        _sentence += _currentList.get(j) + "   ";
                    }
                } else {
                    for (int i = 1; i <= n - 1; i += 1) {
                        float result = b * i / (float) (n - 1);
                        int num = (int) Math.floor(0.5 + result);
                        blanks = num - totalblanks;
                        for (int j = 1; j <= blanks; j += 1) {
                            _sentence += " ";
                        }
                        totalblanks += blanks;
                        _sentence += _currentList.get(i);
                    }
                }
            }
        }
    }

    /** Set paragraph skip to VAL.  VAL >= 0. */
    void setParSkip(int val) {
        _paragraphSkip = val;
    }

    /** Set page height to VAL > 0. */
    void setTextHeight(int val) {
        _textHeight = val;
    }

    /** Process the end of the current input line.  No effect if
     *  current line accumulator is empty or in fill mode.  Otherwise,
     *  adds a new complete line to the finished line queue and clears
     *  the line accumulator. */
    void newLine() {
        if (_listlength != 0) {
            if (_fill) {
                setFill(_fill);
            } else {
                finishWord();
                int parindent = _paragraphIndentation;
                String toadd = "";
                if (_isatbop) {
                    for (int j = 0; j < parindent; j += 1) {
                        toadd += " ";
                    }
                }
                for (int i = 0; i < _currentList.size(); i += 1) {
                    toadd += _currentList.get(i) + " ";
                }
                addLine(toadd);
                _isatbop = false;
            }
            _currentList.clear();
            _listlength = 0;
        }
    }

    /** Iff ON, at the end of paragraph. */
    void setIsateop(boolean on) {
        _isateop = on;
    }

    /** Return true if fill mode is on. */
    boolean fill() {
        return _fill;
    }

    /** Iff ON, fill mode is ON. */
    void turnFill(boolean on) {
        _fill = on;
    }

    /** Iff ON, justify mode is ON. */
    void turnJustify(boolean on) {
        _justify = on;
    }


    /** If there is a current unfinished paragraph pending, close it
     *  out and start a new one. */
    void endParagraph() {
        finishWord();
        _isateop = true;
        newLine();
        _isateop = false;
        _isatbop = true;
    }

    /** Returns the textheight. */
    int getTextHeight() {
        return _textHeight;
    }

    /** Destination given in constructor for formatted lines. */
    private final PageAssembler _pages;

    /** True iff we are at the end of paragraph. */
    private boolean _isateop;
    /** True iff we are at the beginning of paragraph. */
    private boolean _isatbop = true;
    /** True iff fill is on. */
    private boolean _fill = true;
    /** True iff justify is on. */
    private boolean _justify = true;
    /** The current word. */
    private String _currentWord = "";
    /** The current word list. */
    private List<String> _currentList = new ArrayList<String>();
    /** The current sentence. */
    private String _sentence = "";
    /** The length of the current list. */
    private int _listlength = 0;

    /** Setting for \textheight. */
    private int _textHeight = Defaults.TEXT_HEIGHT;
    /** Setting for \parskip. */
    private int _paragraphSkip = Defaults.PARAGRAPH_SKIP;
    /** Setting for \indent. */
    private int _indentation = Defaults.INDENTATION;
    /** Setting for \parindent. */
    private int _paragraphIndentation = Defaults.PARAGRAPH_INDENTATION;
    /** Setting for \textwidth. */
    private int _textWidth = Defaults.TEXT_WIDTH;

}
