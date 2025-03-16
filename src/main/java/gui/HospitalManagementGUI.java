package gui;

import models.*;
import utils.DatabaseConfig;
import utils.CSVLoader;
import utils.DatabaseInitializer;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

public class HospitalManagementGUI extends JFrame {
    private final Connection connection;
    private final JTabbedPane tabbedPane;
    private final CSVLoader csvLoader;

    public HospitalManagementGUI() throws SQLException {
        super("Hospital Management System");
        
        try {
            // Get connection (this will create the database if it doesn't exist)
            connection = DatabaseConfig.getConnection();
            
            // Initialize database schema
            DatabaseInitializer.initializeDatabase(connection);
            
            // Initialize CSV loader
            csvLoader = new CSVLoader(connection);
            
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(800, 600);
            
            // Create menu bar
            setupMenuBar();
            
            tabbedPane = new JTabbedPane();
            add(tabbedPane);
            
            // Load initial data
            setupDatabase();
            setupTabs();
            
            setLocationRelativeTo(null);
            setVisible(true);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Error initializing application: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
            throw e;  // Re-throw to allow main method to handle it
        }
    }
    
    private void setupDatabase() {
        try {
            // First try using LOAD DATA LOCAL INFILE (faster but requires server configuration)
            csvLoader.loadAllData();
        } catch (SQLException e) {
            // If it fails with "Loading local data is disabled", try the alternative method
            if (e.getMessage().contains("Loading local data is disabled")) {
                try {
                    System.out.println("Retrying data load using INSERT statements instead of LOAD DATA LOCAL INFILE...");
                    csvLoader.loadAllDataWithInsert();
                } catch (SQLException e2) {
                    e2.printStackTrace();
                    JOptionPane.showMessageDialog(this, 
                        "Error loading data: " + e2.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Error loading data: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void setupTabs() {
        setupDoctorPanel();
        setupPatientPanel();
        setupVisitPanel();
        setupPrescriptionPanel();
        setupDoctorSpecialtyPanel();
        setupPatientInsurancePanel();
    }
    
    private void setupDoctorPanel() {
        Map<String, FieldMetadata> fields = new HashMap<>();
        fields.put("doctorid", new FieldMetadata(String.class, true));
        fields.put("firstname", new FieldMetadata(String.class));
        fields.put("surname", new FieldMetadata(String.class));
        fields.put("specialization", new FieldMetadata(String.class));
        fields.put("address", new FieldMetadata(String.class));
        fields.put("email", new FieldMetadata(String.class));
        
        DatabaseTablePanel<Doctor> panel = new DatabaseTablePanel<>(
            connection,
            new Doctor(),
            "doctor",
            fields,
            Doctor::new
        );
        
        tabbedPane.addTab("Doctors", panel);
    }
    
    private void setupPatientPanel() {
        Map<String, FieldMetadata> fields = new HashMap<>();
        fields.put("patientid", new FieldMetadata(String.class, true));
        fields.put("firstname", new FieldMetadata(String.class));
        fields.put("surname", new FieldMetadata(String.class));
        fields.put("phone", new FieldMetadata(String.class));
        fields.put("email", new FieldMetadata(String.class));
        fields.put("address", new FieldMetadata(String.class));
        fields.put("postcode", new FieldMetadata(String.class));
        fields.put("insuranceid", new FieldMetadata(String.class, "patientinsurance", "insuranceid", "insuranceid"));
        
        DatabaseTablePanel<Patient> panel = new DatabaseTablePanel<>(
            connection,
            new Patient(),
            "patient",
            fields,
            Patient::new
        );
        
        tabbedPane.addTab("Patients", panel);
    }
    
    private void setupVisitPanel() {
        Map<String, FieldMetadata> fields = new HashMap<>();
        fields.put("patientid", new FieldMetadata(String.class, true));
        fields.put("doctorid", new FieldMetadata(String.class, "doctor", "doctorid", "firstname || ' ' || surname"));
        fields.put("dateofvisit", new FieldMetadata(Date.class));
        fields.put("symptoms", new FieldMetadata(String.class));
        fields.put("diagnosis", new FieldMetadata(String.class));
        
        DatabaseTablePanel<Visit> panel = new DatabaseTablePanel<>(
            connection,
            new Visit(),
            "visit",
            fields,
            Visit::new
        );
        
        tabbedPane.addTab("Visits", panel);
    }
    
    private void setupPrescriptionPanel() {
        Map<String, FieldMetadata> fields = new HashMap<>();
        fields.put("prescriptionid", new FieldMetadata(String.class, true));
        fields.put("patientid", new FieldMetadata(String.class, "patient", "patientid", "firstname || ' ' || surname"));
        fields.put("doctorid", new FieldMetadata(String.class, "doctor", "doctorid", "firstname || ' ' || surname"));
        fields.put("drugid", new FieldMetadata(Integer.class));
        fields.put("dateprescribed", new FieldMetadata(Date.class));
        fields.put("dosage", new FieldMetadata(String.class));
        fields.put("duration", new FieldMetadata(String.class));
        fields.put("comment", new FieldMetadata(String.class));
        
        DatabaseTablePanel<Prescription> panel = new DatabaseTablePanel<>(
            connection,
            new Prescription(),
            "prescription",
            fields,
            Prescription::new
        );
        
        tabbedPane.addTab("Prescriptions", panel);
    }
    
    private void setupDoctorSpecialtyPanel() {
        Map<String, FieldMetadata> fields = new HashMap<>();
        fields.put("doctorid", new FieldMetadata(String.class, "doctor", "doctorid", "firstname || ' ' || surname"));
        fields.put("specialty", new FieldMetadata(String.class));
        fields.put("experience", new FieldMetadata(Integer.class));
        
        DatabaseTablePanel<DoctorSpecialty> panel = new DatabaseTablePanel<>(
            connection,
            new DoctorSpecialty(),
            "doctorspecialty",
            fields,
            DoctorSpecialty::new
        );
        tabbedPane.addTab("Specialists", panel);
    }
    
    private void setupPatientInsurancePanel() {
        Map<String, FieldMetadata> fields = new HashMap<>();
        fields.put("insuranceid", new FieldMetadata(String.class, true));
        fields.put("patientid", new FieldMetadata(String.class, "patient", "patientid", "firstname || ' ' || surname"));
        fields.put("startdate", new FieldMetadata(Date.class));
        fields.put("enddate", new FieldMetadata(Date.class));
        
        DatabaseTablePanel<PatientInsurance> panel = new DatabaseTablePanel<>(
            connection,
            new PatientInsurance(),
            "patientinsurance",
            fields,
            PatientInsurance::new
        );
        
        tabbedPane.addTab("Patient Insurance", panel);
    }
    
    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // Database menu
        JMenu databaseMenu = new JMenu("Database");
        JMenuItem reinitItem = new JMenuItem("Reinitialize Database");
        reinitItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "This will reset the database and delete all data. Are you sure?",
                "Confirm Database Reset",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    DatabaseInitializer.initializeDatabase(connection);
                    
                    // Try original method first
                    try {
                        csvLoader.loadAllData();
                    } catch (SQLException ex) {
                        // If it fails with "Loading local data is disabled", try the alternative method
                        if (ex.getMessage().contains("Loading local data is disabled")) {
                            System.out.println("Retrying data load using INSERT statements instead of LOAD DATA LOCAL INFILE...");
                            csvLoader.loadAllDataWithInsert();
                        } else {
                            throw ex;
                        }
                    }
                    
                    refreshAllTabs();
                    JOptionPane.showMessageDialog(
                        this,
                        "Database reinitialized successfully with updated schema.",
                        "Database Reset",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                        this,
                        "Error reinitializing database: " + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });
        
        databaseMenu.add(reinitItem);
        menuBar.add(databaseMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void refreshAllTabs() {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            Component comp = tabbedPane.getComponentAt(i);
            if (comp instanceof DatabaseTablePanel) {
                ((DatabaseTablePanel<?>) comp).refreshTable();
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new HospitalManagementGUI();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Error starting application: " + e.getMessage(),
                    "Application Error",
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
} 