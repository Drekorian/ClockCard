package cz.muni.fi.pv168.clockcard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Represents a shift worked by a worker.
 *
 * @author David Stein
 * @author Marek Osvald
 *
 * @version 2011.0518
 */

public class Shift {
   //TODO: Refactor - create property file and load SQL queries from it
   private final static String INSERT_SHIFT = "INSERT INTO APP.SHIFTS(worker_id, shift_start, shift_end, last_break, total_break_time) values(?,?,?,?,?)";
   private final static String UPDATE_SHIFT = "UPDATE APP.SHIFTS SET shift_start=?,shift_end=?,last_break=?,total_break_time=?";
   private final static String DELETE_SHIFT = "DELETE FROM APP.SHIFTS WHERE id=?";

   private long id = 0;
   private long workerId = 0;
   private Calendar start = new GregorianCalendar(0, 0, 0, 0, 0, 0);
   private Calendar end = new GregorianCalendar(0, 0, 0, 0, 0, 0);
   private Calendar lastBreakStart = new GregorianCalendar(0, 0, 0, 0, 0, 0);
   private long totalBreakTime = 0;

    /**
    * Parametric constructor.
    * 
    * @param id unique ID of the shift
    * @param workerId unique ID of the workrer
    * @param start date and time of the shift's start
    * @param end date and time of the shift's start
    * @param lastBreakStart date and time of the start of the last break
    * @param totalBreakTime total break time
    *
    * TODO: This seems to be COMPLETELY WRONG! Total misunderstanding of intended design.
    */
    public Shift(long id, long workerId, Calendar start, Calendar end, Calendar lastBreakStart, long totalBreakTime){
        this.id = id;
        this.workerId = workerId;
        this.start = start;
        this.end = end;
        this.lastBreakStart = lastBreakStart;
        this.totalBreakTime = totalBreakTime;
   }

    /**
    * Parametric constructor. Creates shift based on worker's ID and date and time of the shift's start.
    * @param workerId unique ID of the worker
    * @param start date and time of the shift's starts
    */
    public Shift(long workerId, Calendar start) {
        this.workerId = workerId;
        this.start = start;
    }

    /**
     * Saves shift to the database.
     *
     * Provided that shift was created by a 2-parameter constructor, saves shift
     * to the database and sets generated IS to this object. Otherwise just saves
     * shift to the database.
     *
     * @throws SQLException
     */
    public void save() throws SQLException{
        Connection con = ConnectionManager.getConnection();
        
        if (id == 0) {
            PreparedStatement prepareStatement = con.prepareStatement(INSERT_SHIFT, Statement.RETURN_GENERATED_KEYS);
            prepareStatement.setLong(1, workerId);
            prepareStatement.setTimestamp(2, new Timestamp(start.getTimeInMillis()));
            prepareStatement.setTimestamp(3, new Timestamp(end.getTimeInMillis()));
            prepareStatement.setTimestamp(4, new Timestamp(lastBreakStart.getTimeInMillis()));
            prepareStatement.setLong(5, totalBreakTime);
            prepareStatement.execute();
            
            ResultSet result =  prepareStatement.getGeneratedKeys();
            result.next();
            id = result.getLong(1);
        } else {
            PreparedStatement prepareStatement = con.prepareStatement(UPDATE_SHIFT);
            prepareStatement.setTimestamp(1, new Timestamp(start.getTimeInMillis()));
            prepareStatement.setTimestamp(2, new Timestamp(end.getTimeInMillis()));
            prepareStatement.setTimestamp(3, new Timestamp(lastBreakStart.getTimeInMillis()));
            prepareStatement.setLong(4, totalBreakTime);
            prepareStatement.executeUpdate();
        }
    }

    /**
     * Deletes a shift from the database.
     *
     * @throws SQLException
     */
    public void destroy() throws SQLException{
        //TODO: Returns WRONG return type. Should return BOOLEAN - true upon success and false otherwise.
        Connection con = ConnectionManager.getConnection();
        PreparedStatement prep = con.prepareStatement(DELETE_SHIFT);
        prep.setLong(1, this.id);
        prep.execute();
    }

    /**
     * Returns date and time of the end of the shift.
     *
     * @return date and time of the end of the shift
     */
    public Calendar getEnd() {
        return end;
    }

    /**
     * Sets end date and time of the shift.
     * 
     * @param end end date and time of the shift
     */
    public void setEnd(Calendar end) {
        this.end = end;
    }

    /**
     * Returns unique ID of the shift.
     *
     * @return unique ID of the shift
     */
    public long getID() {
        return id;
    }

    /**
     * Sets unique ID of the shift.
     *
     * @param id unique ID of the shift
     */
    public void setId(long id) {
        //Bad smell in code. Get back to this.
        this.id = id;
    }

    /**
     * Returns start date and time of the last break during shift.
     *
     * @return start date and time of the last break during shift
     */
    public Calendar getLastBreakStart() {
        return lastBreakStart;
    }

    /**
     * Sets start date and time of the last break during shift.
     *
     * @param lastBreakStart start date and time of the last break during shift
     */
    public void setLastBreakStart(Calendar lastBreakStart) {
        this.lastBreakStart = lastBreakStart;
    }

    public Calendar getStart() {
        return start;
    }

    public void setStart(Calendar start) {
        this.start = start;
    }

    public long getTotalBreakTime() {
        return totalBreakTime;
    }

    public void setTotalBreakTime(long totalBreakTime) {
        this.totalBreakTime = totalBreakTime;
    }

    public long getWorkerId() {
        return workerId;
    }
    
    public void setWorkerId(long workerId) {
        this.workerId = workerId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final Shift other = (Shift) obj;
        if (this.id != other.getID()) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }
}
