package models;

import java.sql.*;
import java.text.SimpleDateFormat;

public class Visit extends BaseModel<Visit> {
    private String patientid;
    private String doctorid;
    private Date dateofvisit;
    private String symptoms;
    private String diagnosis;
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected String getTableName() {
        return "Visit";
    }

    @Override
    protected Visit mapResultSetToEntity(ResultSet rs) throws SQLException {
        Visit visit = new Visit();
        visit.patientid = rs.getString("patientid");
        visit.doctorid = rs.getString("doctorid");
        visit.dateofvisit = rs.getDate("dateofvisit");
        visit.symptoms = rs.getString("symptoms");
        visit.diagnosis = rs.getString("diagnosis");
        return visit;
    }

    @Override
    protected void setCreateStatement(PreparedStatement stmt, Visit visit) throws SQLException {
        stmt.setString(1, visit.patientid);
        stmt.setString(2, visit.doctorid);
        stmt.setDate(3, visit.dateofvisit);
        stmt.setString(4, visit.symptoms);
        stmt.setString(5, visit.diagnosis);
    }

    @Override
    protected void setUpdateStatement(PreparedStatement stmt, Visit visit) throws SQLException {
        stmt.setString(1, visit.doctorid);
        stmt.setDate(2, visit.dateofvisit);
        stmt.setString(3, visit.symptoms);
        stmt.setString(4, visit.diagnosis);
        stmt.setString(5, visit.patientid);
    }

    @Override
    protected String getCreateSQL() {
        return "INSERT INTO Visit (patientid, doctorid, dateofvisit, symptoms, diagnosis) VALUES (?, ?, ?, ?, ?)";
    }

    @Override
    protected String getUpdateSQL() {
        return "UPDATE Visit SET doctorid=?, dateofvisit=?, symptoms=?, diagnosis=? WHERE patientid=?";
    }

    @Override
    protected String getDeleteSQL() {
        return "DELETE FROM Visit WHERE patientid=?";
    }

    @Override
    protected String getSelectAllSQL() {
        return "SELECT * FROM Visit";
    }

    @Override
    protected String getSelectByIdSQL() {
        return "SELECT * FROM Visit WHERE patientid=?";
    }

    @Override
    protected void setIdParameter(PreparedStatement stmt, Object id) throws SQLException {
        stmt.setString(1, (String)id);
    }

    // Getters and Setters
    public String getPatientid() { return patientid; }
    public void setPatientid(String patientid) { this.patientid = patientid; }
    
    public String getDoctorid() { return doctorid; }
    public void setDoctorid(String doctorid) { this.doctorid = doctorid; }
    
    public Date getDateofvisit() { return dateofvisit; }
    public void setDateofvisit(Date dateofvisit) { this.dateofvisit = dateofvisit; }
    
    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }
    
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    @Override
    public String toString() {
        String formattedDate = dateofvisit != null ? DATE_FORMAT.format(dateofvisit) : "N/A";
        return String.format("Visit{patientid='%s', doctorid='%s', dateofvisit='%s', symptoms='%s', diagnosis='%s'}", 
                patientid, doctorid, formattedDate, symptoms, diagnosis);
    }

    @Override
    protected Object getId() {
        return patientid;
    }
} 