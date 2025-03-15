package models;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Prescription extends BaseModel<Prescription> {
    private String prescriptionid;
    private Date dateprescribed;
    private String dosage;
    private String duration;
    private String comment;
    private int drugid;
    private String doctorid;
    private String patientid;
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);

    @Override
    protected String getTableName() {
        return "prescription";
    }

    @Override
    protected Prescription mapResultSetToEntity(ResultSet rs) throws SQLException {
        Prescription prescription = new Prescription();
        prescription.prescriptionid = rs.getString("prescriptionid");
        prescription.dateprescribed = rs.getDate("dateprescribed");
        prescription.dosage = rs.getString("dosage");
        prescription.duration = rs.getString("duration");
        prescription.comment = rs.getString("comment");
        prescription.drugid = rs.getInt("drugid");
        prescription.doctorid = rs.getString("doctorid");
        prescription.patientid = rs.getString("patientid");
        return prescription;
    }

    @Override
    protected void setCreateStatement(PreparedStatement stmt, Prescription prescription) throws SQLException {
        stmt.setString(1, prescription.prescriptionid);
        stmt.setDate(2, prescription.dateprescribed);
        stmt.setString(3, prescription.dosage);
        stmt.setString(4, prescription.duration);
        stmt.setString(5, prescription.comment);
        stmt.setInt(6, prescription.drugid);
        stmt.setString(7, prescription.doctorid);
        stmt.setString(8, prescription.patientid);
    }

    @Override
    protected void setUpdateStatement(PreparedStatement stmt, Prescription prescription) throws SQLException {
        stmt.setDate(1, prescription.dateprescribed);
        stmt.setString(2, prescription.dosage);
        stmt.setString(3, prescription.duration);
        stmt.setString(4, prescription.comment);
        stmt.setInt(5, prescription.drugid);
        stmt.setString(6, prescription.doctorid);
        stmt.setString(7, prescription.patientid);
        stmt.setString(8, prescription.prescriptionid);
    }

    @Override
    protected String getCreateSQL() {
        return "INSERT INTO prescription (prescriptionid, dateprescribed, dosage, duration, comment, drugid, doctorid, patientid) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String getUpdateSQL() {
        return "UPDATE prescription SET dateprescribed=?, dosage=?, duration=?, comment=?, drugid=?, doctorid=?, patientid=? WHERE prescriptionid=?";
    }

    @Override
    protected String getDeleteSQL() {
        return "DELETE FROM prescription WHERE prescriptionid=?";
    }

    @Override
    protected String getSelectAllSQL() {
        return "SELECT * FROM prescription";
    }

    @Override
    protected String getSelectByIdSQL() {
        return "SELECT * FROM prescription WHERE prescriptionid=?";
    }

    @Override
    protected void setIdParameter(PreparedStatement stmt, Object id) throws SQLException {
        stmt.setString(1, (String)id);
    }

    // Getters and Setters
    public String getPrescriptionid() { return prescriptionid; }
    public void setPrescriptionid(String prescriptionid) { this.prescriptionid = prescriptionid; }
    
    public Date getDateprescribed() { return dateprescribed; }
    public void setDateprescribed(Date dateprescribed) { this.dateprescribed = dateprescribed; }
    
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    
    public int getDrugid() { return drugid; }
    public void setDrugid(int drugid) { this.drugid = drugid; }
    
    public String getDoctorid() { return doctorid; }
    public void setDoctorid(String doctorid) { this.doctorid = doctorid; }
    
    public String getPatientid() { return patientid; }
    public void setPatientid(String patientid) { this.patientid = patientid; }

    @Override
    public String toString() {
        String formattedDate = dateprescribed != null ? DATE_FORMAT.format(dateprescribed) : "N/A";
        return String.format("Prescription{prescriptionid='%s', dateprescribed='%s', dosage='%s', duration='%s', " +
                "comment='%s', drugid=%d, doctorid='%s', patientid='%s'}", 
                prescriptionid, formattedDate, dosage, duration, comment, drugid, doctorid, patientid);
    }

    @Override
    protected Object getId() {
        return prescriptionid;
    }
} 