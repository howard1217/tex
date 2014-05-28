package tex61;

import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.MatchResult;
import java.io.Reader;

import static tex61.FormatException.reportError;

/** Reads commands and text from an input source and send the results
 *  to a designated Controller. This essentially breaks the input down
 *  into "tokens"---commands and pieces of text.
 *  @author Chia-Hao Chiao
 */
class InputParser {

    /** Matches text between { } in a command, including the last
     *  }, but not the opening {.  When matched, group 1 is the matched
     *  text.  Always matches at least one character against a non-empty
     *  string or input source. If it matches and group 1 is null, the
     *  argument was not well-formed (the final } was missing or the
     *  argument list was nested too deeply). */
    private static final Pattern BALANCED_TEXT =
        Pattern.compile("(?s)((?:\\\\.|[^\\\\{}]"
                        + "|[{](?:\\\\.|[^\\\\{}])*[}])*)"
                        + "\\}"
                        + "|.");

    /** Matches input to the text formatter.  Always matches something
     *  in a non-empty string or input source.  After matching, one or
     *  more of the groups described by *_TOKEN declarations will
     *  be non-null.  See these declarations for descriptions of what
     *  this pattern matches.  To test whether .group(*_TOKEN) is null
     *  quickly, check for .end(*_TOKEN) > -1).  */
    private static final Pattern INPUT_PATTERN =
        Pattern.compile("(?s)(\\p{Blank}+)"
                        + "|(\\r?\\n((?:\\r?\\n)+)?)"
                        + "|\\\\([\\p{Blank}{}\\\\])"
                        + "|\\\\(\\p{Alpha}+)([{]?)"
                        + "|((?:[^\\p{Blank}\\r\\n\\\\{}]+))"
                        + "|(.)");

    /** Symbolic names for the groups in INPUT_PATTERN. */
    private static final int
        /** Blank or tab. */
        BLANK_TOKEN = 1,
        /** End of line or paragraph. */
        EOL_TOKEN = 2,
        /** End of paragraph (>1 newline). EOL_TOKEN group will also
         *  be present. */
        EOP_TOKEN = 3,
        /** \{, \}, \\, or \ .  .group(ESCAPED_CHAR_TOKEN) will be the
         *  character after the backslash. */
        ESCAPED_CHAR_TOKEN = 4,
        /** Command (\<alphabetic characters>).  .group(COMMAND_TOKEN)
         *  will be the characters after the backslash.  */
        COMMAND_TOKEN = 5,
        /** A '{' immediately following a command. When this group is present,
         *  .group(COMMAND_TOKEN) will also be present. */
        COMMAND_ARG_TOKEN = 6,
        /** Segment of other text (none of the above, not including
         *  any of the special characters \, {, or }). */
        TEXT_TOKEN = 7,
        /** A character that should not be here. */
        ERROR_TOKEN = 8;

    /** A new InputParser taking input from READER and sending tokens to
     *  OUT. */
    InputParser(Reader reader, Controller out) {
        _input = new Scanner(reader);
        _out = out;
    }

    /** A new InputParser whose input is TEXT and that sends tokens to
     *  OUT. */
    InputParser(String text, Controller out) {
        _input = new Scanner(text);
        _out = out;
    }

    /** Break all input source text into tokens, and send them to our
     *  output controller.  Finishes by calling .close on the controller.
     */
    void process() {
        while (_input.hasNext()) {
            if (_input.findWithinHorizon(INPUT_PATTERN, 0) != null) {
                MatchResult mat = _input.match();
                if (mat.group(BLANK_TOKEN) != null) {
                    _out.endWord();
                } else if (mat.group(EOP_TOKEN) != null) {
                    _out.endParagraph();
                } else if (mat.group(EOL_TOKEN) != null) {
                    _out.addNewline();
                } else if (mat.group(ESCAPED_CHAR_TOKEN) != null) {
                    _out.addText(mat.group(ESCAPED_CHAR_TOKEN));
                } else if (mat.group(COMMAND_TOKEN) != null) {
                    if (mat.group(COMMAND_ARG_TOKEN).length() > 0) {
                        if (mat.group(COMMAND_TOKEN).equals("endnote")) {
                            processCommand(mat.group(COMMAND_TOKEN), null);
                        } else {
                            _command = mat.group(COMMAND_TOKEN);
                            _inCommand = true;
                        }
                    } else { processCommand(mat.group(COMMAND_TOKEN), null); }
                } else if (mat.group(TEXT_TOKEN) != null) {
                    if (_inCommand) {
                        if (_argument != null) {
                            reportError("Only one argument is allowed.");
                        }
                        _argument = mat.group(TEXT_TOKEN);
                    } else if (_inEndnote) {
                        Scanner sc = new Scanner(mat.group(TEXT_TOKEN));
                        if (sc.findInLine(BALANCED_TEXT) == null) {
                            reportError("Text has to be balanced.");
                        }
                        _out.addText(mat.group(TEXT_TOKEN));
                    } else {
                        _out.addText(mat.group(TEXT_TOKEN));
                    }
                } else if (mat.group(ERROR_TOKEN) != null) {
                    if (_inCommand
                        && mat.group(ERROR_TOKEN).equals("}")) {
                        processCommand(_command, _argument);
                        _command = null;
                        _argument = null;
                    } else if (_inEndnote
                        && mat.group(ERROR_TOKEN).equals("}")) {
                        _out.endParagraph();
                        _out.setNormalMode();
                        _inEndnote = false;
                    } else {
                        FormatException.reportError("There is an error token.");
                    }
                }
            }
        }
        _out.close();
    }

    /** Process \COMMAND{ARG} or (if ARG is null) \COMMAND.  Call the
     *  appropriate methods in our Controller (_out). */
    private void processCommand(String command, String arg) {
        try {
            int num = -1;
            try {
                if (arg != null) {
                    num = Integer.parseInt(arg);
                }
            } catch (NumberFormatException e) {
                reportError("Need integer argument.", e);
            }
            switch (command) {
            case "indent":
                _out.setIndentation(num);
                break;
            case "parindent":
                _out.setParIndentation(num);
                break;
            case "textwidth":
                _out.setTextWidth(num);
                break;
            case "textheight":
                _out.setTextHeight(num);
                break;
            case "parskip":
                _out.setParSkip(num);
                break;
            case "nofill":
                _out.setFill(false);
                break;
            case "fill":
                _out.setFill(true);
                break;
            case "justify":
                _out.setJustify(true);
                break;
            case "nojustify":
                _out.setJustify(false);
                break;
            case "endnote":
                _inEndnote = true;
                _out.setEndnoteMode();
                _out.formatEndnote();
                break;
            default:
                reportError("unknown command: %s", command);
                break;
            }
        } catch (FormatException e) {
            reportError("Malformed command", e);
        }
        _inCommand = false;
    }

    /** My input source. */
    private final Scanner _input;
    /** The Controller to which I send input tokens. */
    private Controller _out;
    /** The boolean that shows whether in command mode. */
    private boolean _inCommand;
    /** The boolean that shows whether in endnote. */
    private boolean _inEndnote;
    /** The string that shows the argument of a command. */
    private String _argument;
    /** The string that shows the current command. */
    private String _command;

}
