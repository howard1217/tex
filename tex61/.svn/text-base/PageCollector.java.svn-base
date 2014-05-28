package tex61;

import java.util.List;

/** A PageAssembler that collects its lines into a designated List.
 *  @author Chia-Hao Chiao
 */
class PageCollector extends PageAssembler {

    /** A new PageCollector that stores lines in OUT. */
    PageCollector(List<String> out) {
        _out = out;
    }

    /** Add LINE to my List. */
    @Override
    void write(String line) {
        _out.add(line);
    }

    /** Returns the size of lines in the collector. */
    public int size() {
        return _out.size();
    }

    /** The stored lines. */
    private List<String> _out;

}
