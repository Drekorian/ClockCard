package cz.muni.fi.pv168.clockcard;

/**
 *
 * @author Marek
 */

public class QueryParameter {
    public static final int BOOLEAN = 0;
    public static final int LONG = 1;
    public static final int TIMESTAMP = 2;
    public static final int STRING = 3;

    private int type;
    private Object value;

    /**
     * TODO: javadoc
     * @param type
     * @param value
     */
    public QueryParameter(int type, Object value) {
        if (type < 0 || type > 3) {
            throw new IllegalArgumentException("Type cannot be lower than 0 or greater than 3");
        }
        
        this.type = type;
        this.value = value;
    }

    /**
     * TODO: javadoc
     * @param type
     * @param value
     */
    public int getType() {
        return type;
    }

    /**
     * TODO: javadoc
     * @param type
     * @param value
     */
    public Object getValue() {
        return value;
    }
}
