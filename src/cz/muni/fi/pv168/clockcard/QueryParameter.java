package cz.muni.fi.pv168.clockcard;

/**
 * Data holding the type and value of a SQL query parameter.
 *
 * @author Marek Osvald
 * @version 2011.0629
 */

public class QueryParameter {
    public static final int BOOLEAN = 0;
    public static final int LONG = 1;
    public static final int TIMESTAMP = 2;
    public static final int STRING = 3;

    private int type;
    private Object value;

    /**
     * Parametric constructor. Sets the type and value of a parameter.
     *
     * @param type type of the parameter
     * @param value value of the parameter
     */
    public QueryParameter(int type, Object value) {
        if (type < 0 || type > 3) {
            throw new IllegalArgumentException("Type cannot be lower than 0 or greater than 3");
        }
        
        this.type = type;
        this.value = value;
    }
    /**
     * Returns the type of the parameter.
     *
     * @return type of the parameter
     */
    public int getType() {
        return type;
    }
    /**
     * Returns the value of the parameter.
     * 
     * @return value of the parameter
     */
    public Object getValue() {
        return value;
    }
}
