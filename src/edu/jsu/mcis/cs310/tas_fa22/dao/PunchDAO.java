package edu.jsu.mcis.cs310.tas_fa22.dao;

import edu.jsu.mcis.cs310.tas_fa22.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class PunchDAO {

    private static final String QUERY_FIND = "SELECT * FROM event WHERE id = ?";
    private static final String QUERY_LIST = "SELECT * FROM event WHERE badgeid = ? ORDER BY timestamp";
<<<<<<< HEAD
    private static final String QUERY_CREATE = "INSERT INTO event (terminalid, badgeid, timestamp, eventtypeid) (?, ?, ?, ?)";
=======
    private static final String QUERY_LIST_E = "SELECT * FROM event WHERE badgeid = ? AND timestamp > ? LIMIT 1";
>>>>>>> 43a04f76a2aff161c468cb4b4f1971ae3e2370a1

    private final DAOFactory daoFactory;

    // constructor
    PunchDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    public Punch find(int id) {

        Punch punch = null;

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            Connection conn = daoFactory.getConnection();

            if (conn.isValid(0)) {

                ps = conn.prepareStatement(QUERY_FIND);
                ps.setString(1, Integer.toString(id));

                boolean hasresults = ps.execute();

                if (hasresults) {

                    rs = ps.getResultSet();

                    while (rs.next()) {

                        int terminalid;
                        String badgeid;
                        EventType punchtype;
                        LocalDateTime originaltimestamp;

                        // get terminal id  
                        terminalid = rs.getInt("terminalid");

                        // getting badge
                        badgeid = rs.getString("badgeid");
                        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();
                        Badge b = badgeDAO.find(badgeid);

                        // get punch type 
                        punchtype = EventType.values()[rs.getInt("eventtypeid")];

                        // get timestamp
                        originaltimestamp = rs.getTimestamp("timestamp").toLocalDateTime();

                        punch = new Punch(id, terminalid, b, originaltimestamp, punchtype);

                    }
//This adds a sample case for us to test later on
                Statement stmt = conn.createStatement();
                stmt.executeUpdate("DELETE FROM event WHERE terminalid = '101' AND timestamp = '2018-10-01 20:00:00' AND eventtypeid = '1'");
                stmt.executeUpdate("DELETE FROM event WHERE terminalid = '101' AND timestamp = '2018-10-01 06:00:00' AND eventtypeid = '1'");
                
                stmt.executeUpdate("INSERT INTO event (terminalid, badgeid, timestamp, eventtypeid) VALUES (101, \"95497F63\", \"2018-10-01 20:00:00\", 1)");
                stmt.executeUpdate("INSERT INTO event (terminalid, badgeid, timestamp, eventtypeid) VALUES (101, \"95497F63\", \"2018-10-02 06:00:00\", 0)");  
                }

            }

        } catch (SQLException e) {

            throw new DAOException(e.getMessage());

        } finally {

            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }

        }

        return punch;

    }

    public ArrayList list(Badge badge, LocalDate date) {
        ArrayList<Punch> list = new ArrayList();
<<<<<<< HEAD

=======
        
        Timestamp ts = Timestamp.valueOf(date.atStartOfDay());
>>>>>>> 43a04f76a2aff161c468cb4b4f1971ae3e2370a1
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            Connection conn = daoFactory.getConnection();

            if (conn.isValid(0)) {

                ps = conn.prepareStatement(QUERY_LIST);
                ps.setString(1, badge.getId());

                boolean hasresults = ps.execute();
                if (hasresults) {

                    rs = ps.getResultSet();

                    while (rs.next()) {

                        Timestamp punchdate = rs.getTimestamp(4);
                        LocalDateTime local = punchdate.toLocalDateTime();
                        LocalDate ld = local.toLocalDate();
<<<<<<< HEAD
                        boolean isclosed = false;
                        Punch last = null;

                        if (ld.equals(date)) {
                            int id = rs.getInt(1);
                            last = find(id);
                            list.add(last);
                        } else if ((!isclosed) && (ld.isAfter(date)) && (last != null)
                                && (last.getPunchtype() == EventType.CLOCK_IN)) {
                            int id = rs.getInt(1);
=======
                        
                        if (ld.equals(date)) {
                            int id = rs.getInt(1);
>>>>>>> 43a04f76a2aff161c468cb4b4f1971ae3e2370a1
                            list.add(find(id));
                        }

                    }

                }

            }
            
            if ((list != null) && ((list.get(list.size() - 1)).getPunchtype() == EventType.CLOCK_IN)) {
                LocalDateTime newdate = list.get(list.size() - 1).getOriginaltimestamp();
                Timestamp newts = Timestamp.valueOf(newdate);
                
                ps = conn.prepareStatement(QUERY_LIST_E);
                ps.setString(1, badge.getId());
                ps.setString(2, newts.toString());
                    
                boolean hasresults = ps.execute();
                
                if (hasresults) {
                    
                    rs = ps.getResultSet();
                    
                    while (rs.next()) {
                        int id = rs.getInt(1);
                        list.add(find(id));
                    }
                    
                }
                    
            }

        } catch (SQLException e) {

            throw new DAOException(e.getMessage());

        } finally {

            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }

        }

        // testing
        for (Punch p : list) {
            System.out.println(p.toString());
        }

        return list;

    }

    public int create(Punch newPunch) {

        Integer punchId = null;

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            Connection conn = daoFactory.getConnection();

            if (conn.isValid(0)) {
                EmployeeDAO empDao = daoFactory.getEmployeeDAO();
                Employee emp = empDao.find(newPunch.getBadge());

                Integer empTerminalId = emp.getDepartment().getTerminalId();

                if (empTerminalId.equals(newPunch.getTerminalid())) {

                    ps = conn.prepareStatement(QUERY_CREATE);
                    ps.setString(1, Integer.toString(newPunch.getTerminalid()));
                    ps.setString(2, newPunch.getBadge().getId());
                    ps.setString(3, newPunch.getOriginaltimestamp().toString());
                    ps.setInt(4, newPunch.getPunchtype().ordinal());

                    int rowAffected = ps.executeUpdate();

                    if (rowAffected == 1) {

                        rs = ps.getGeneratedKeys();

                        if (rs.next()) {
                            punchId = rs.getInt(1);
                        }
                    }
                }
            }

        } catch (SQLException e) {

            throw new DAOException(e.getMessage());

        } finally {

            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }

        }

        return punchId;

    }
 
}
