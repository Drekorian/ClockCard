package cz.muni.fi.pv168.clockcard;

/**
 * Abstract class that represents a worker in the ClockCard system.
 *
 * @author Marek Osvald
 */

public abstract class APerson {
    private static String DEFAULT_PASSWORD;
    
    private long id;
    private String name;
    private String surname;
    private String login;
    private String password;

    /**
     * Resets forgotten password of a person to the default password.
     *
     * @param person Person whose password is being reset.
     */
    public static void resetForgtottenPassword(APerson person) {
        person.password = DEFAULT_PASSWORD;
    }

    /**
     * Parametric constructor.
     *
     * @param name name of the person
     * @param surname surname of the person
     * @param login login of the person
     */
    public APerson(String name, String surname, String login) {
        this.name = name;
        this.surname = surname;
        this.login = login;
        this.password = DEFAULT_PASSWORD;
    }
    /**
     * Returns ID of the person.
     *
     * @return ID of the person.
     */
    public long getID() {
        return id;
    }
    /**
     * Returns name of the person.
     *
     * @return name of the person.
     */
    public String getName() {
        return name;
    }
    /**
     * Sets name of the person.
     *
     * @param name name of the person to be set
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * Returns surname of the person.
     *
     * @return surname of the person
     */
    public String getSurname() {
        return surname;
    }
    /**
     * Sets surname of the person.
     *
     * @param surname surname of the person to be set
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }
    /**
     * Returns login of the person.
     * 
     * @return login of the person
     */
    public String getLogin() {
        return login;
    }
    /**
     * Sets login of the person.
     *
     * @param login of the person to be set.
     */
    public void setLogin(String login) {
        this.login = login;
    }
}
