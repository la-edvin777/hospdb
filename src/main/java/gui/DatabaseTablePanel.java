package gui;

import models.BaseModel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.function.Supplier;
import java.util.Comparator;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.ArrayList;

public class DatabaseTablePanel<T extends BaseModel<T>> extends JPanel {
    private final Connection connection;
    private final T entity;
    private final String tableName;
    private final Map<String, FieldMetadata> fields;
    private final Supplier<T> entityFactory;
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final TableRowSorter<DefaultTableModel> sorter;
    private final JButton addButton;
    private final JButton editButton;
    private final JButton deleteButton;
    private final JButton refreshButton;
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);

    public DatabaseTablePanel(Connection connection, T entity, String tableName, 
                            Map<String, FieldMetadata> fields, Supplier<T> entityFactory) {
        this.connection = connection;
        this.entity = entity;
        this.tableName = tableName;
        this.fields = fields;
        this.entityFactory = entityFactory;

        setLayout(new BorderLayout());

        // Create table with proper column class detection
        tableModel = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // Return String.class by default for empty tables
                if (getRowCount() == 0) {
                    return String.class;
                }
                
                // Get the first non-null value in the column
                for (int row = 0; row < getRowCount(); row++) {
                    Object value = getValueAt(row, columnIndex);
                    if (value != null) {
                        return value.getClass();
                    }
                }
                return String.class;
            }
        };
        
        table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        
        // Add mouse listener for column sorting
        table.getTableHeader().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int columnModelIndex = table.getColumnModel().getColumnIndexAtX(evt.getX());
                int viewIndex = table.convertColumnIndexToView(columnModelIndex);
                
                if (viewIndex != -1) {
                    int column = table.convertColumnIndexToModel(viewIndex);
                    if (evt.getClickCount() == 1) {
                        // Single click - sort ascending
                        sorter.setSortKeys(List.of(new RowSorter.SortKey(column, SortOrder.ASCENDING)));
                    } else if (evt.getClickCount() == 2) {
                        // Double click - sort descending
                        sorter.setSortKeys(List.of(new RowSorter.SortKey(column, SortOrder.DESCENDING)));
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Add");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");
        refreshButton = new JButton("Refresh");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add button listeners
        addButton.addActionListener(e -> showAddDialog());
        editButton.addActionListener(e -> showEditDialog());
        deleteButton.addActionListener(e -> deleteSelected());
        refreshButton.addActionListener(e -> refreshTable());

        // Initial load
        refreshTable();
    }

    private void showAddDialog() {
        T newEntity = entityFactory.get();
        EntityDialog<T> dialog = new EntityDialog<>(
            (JFrame)SwingUtilities.getWindowAncestor(this),
            "Add " + tableName,
            newEntity,
            fields,
            this::saveEntity,
            null
        );
        dialog.setVisible(true);
    }

    private void showEditDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a record to edit");
            return;
        }

        T selectedEntity = getEntityFromRow(selectedRow);
        EntityDialog<T> dialog = new EntityDialog<>(
            (JFrame)SwingUtilities.getWindowAncestor(this),
            "Edit " + tableName,
            selectedEntity,
            fields,
            this::updateEntity,
            null
        );

        // Set current values
        for (Map.Entry<String, FieldMetadata> entry : fields.entrySet()) {
            String fieldName = entry.getKey();
            try {
                Object value = selectedEntity.getClass().getMethod("get" + capitalize(fieldName)).invoke(selectedEntity);
                dialog.setValue(fieldName, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        dialog.setVisible(true);
    }

    private void deleteSelected() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a record to delete");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this record?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            T selectedEntity = getEntityFromRow(selectedRow);
            try {
                selectedEntity.delete(connection);
                refreshTable();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting record: " + e.getMessage());
            }
        }
    }

    public void refreshTable() {
        try {
            List<T> entities = entity.selectAll(connection);
            updateTableModel(entities);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error refreshing table: " + e.getMessage());
        }
    }

    private void updateTableModel(List<T> entities) {
        // Clear existing data
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        // Define column order based on table name
        String[] columnOrder;
        switch (tableName.toLowerCase()) {
            case "doctor":
                columnOrder = new String[]{"doctorid", "firstname", "surname", "specialization", "address", "email"};
                break;
            case "patient":
                columnOrder = new String[]{"patientid", "firstname", "surname", "phone", "email", "address", "postcode", "insuranceid"};
                break;
            case "visit":
                columnOrder = new String[]{"patientid", "doctorid", "dateofvisit", "symptoms", "diagnosis"};
                break;
            case "prescription":
                columnOrder = new String[]{"prescriptionid", "patientid", "doctorid", "drugid", "dateprescribed", "dosage", "duration", "comment"};
                break;
            case "doctorspecialty":
                columnOrder = new String[]{"doctorid", "specialty", "experience"};
                break;
            case "patientinsurance":
                columnOrder = new String[]{"insuranceid", "patientid", "startdate", "enddate"};
                break;
            default:
                columnOrder = fields.keySet().toArray(new String[0]);
        }

        // Add columns in the specified order
        for (String fieldName : columnOrder) {
            if (fields.containsKey(fieldName)) {
                tableModel.addColumn(fieldName);
            }
        }

        // Add rows
        for (T entity : entities) {
            Vector<Object> row = new Vector<>();
            for (String fieldName : columnOrder) {
                if (fields.containsKey(fieldName)) {
                    try {
                        Object value = entity.getClass().getMethod("get" + capitalize(fieldName)).invoke(entity);
                        // Format date values
                        if (value instanceof Date) {
                            value = DATE_FORMAT.format(value);
                        }
                        row.add(value);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            tableModel.addRow(row);
        }

        // Set default sorting based on table
        if (tableModel.getRowCount() > 0) {
            switch (tableName.toLowerCase()) {
                case "doctor":
                    sorter.setSortKeys(List.of(new RowSorter.SortKey(1, SortOrder.ASCENDING))); // Sort by firstname
                    break;
                case "patient":
                    sorter.setSortKeys(List.of(new RowSorter.SortKey(1, SortOrder.ASCENDING))); // Sort by firstname
                    break;
                case "visit":
                    sorter.setSortKeys(List.of(new RowSorter.SortKey(2, SortOrder.DESCENDING))); // Sort by dateofvisit
                    break;
                case "prescription":
                    sorter.setSortKeys(List.of(new RowSorter.SortKey(4, SortOrder.DESCENDING))); // Sort by dateprescribed
                    break;
                case "doctorspecialty":
                    sorter.setSortKeys(List.of(new RowSorter.SortKey(1, SortOrder.ASCENDING))); // Sort by specialty
                    break;
                case "patientinsurance":
                    sorter.setSortKeys(List.of(new RowSorter.SortKey(2, SortOrder.ASCENDING))); // Sort by startdate
                    break;
            }
        }
    }

    private T getEntityFromRow(int row) {
        T entity = entityFactory.get();
        for (int col = 0; col < table.getColumnCount(); col++) {
            String fieldName = table.getColumnName(col);
            Object value = table.getValueAt(row, col);
            try {
                // Handle date values
                if (fields.get(fieldName).getType() == Date.class && value instanceof String) {
                    try {
                        java.sql.Date sqlDate = java.sql.Date.valueOf((String)value);
                        entity.getClass().getMethod("set" + capitalize(fieldName), java.sql.Date.class)
                            .invoke(entity, sqlDate);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (value != null) {
                        entity.getClass().getMethod("set" + capitalize(fieldName), value.getClass())
                            .invoke(entity, value);
                    } else {
                        // Handle null values - find the appropriate setter method and invoke it with null
                        for (Method method : entity.getClass().getMethods()) {
                            if (method.getName().equals("set" + capitalize(fieldName)) 
                                && method.getParameterCount() == 1 
                                && !method.getParameterTypes()[0].isPrimitive()) {
                                method.invoke(entity, (Object) null);
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return entity;
    }

    private void saveEntity(T entity) {
        try {
            entity.create(connection);
            refreshTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saving record: " + e.getMessage());
        }
    }

    private void updateEntity(T entity) {
        try {
            entity.update(connection);
            refreshTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating record: " + e.getMessage());
        }
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private void setupEditDialog(JDialog dialog, T entity, boolean isNew) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        Map<String, JComponent> inputFields = new HashMap<>();
        
        for (String fieldName : fields.keySet()) {
            FieldMetadata metadata = fields.get(fieldName);
            JLabel label = new JLabel(fieldName + ":");
            JComponent input;
            
            // Check if this is an auto-generated ID field
            boolean isAutoGeneratedId = (fieldName.toLowerCase().endsWith("id") && 
                (fieldName.equals("visitid") || fieldName.equals("prescriptionid")));
            
            if (metadata.getForeignKeyTable() != null) {
                // Create combobox for foreign key fields
                JComboBox<ComboBoxItem> comboBox = new JComboBox<>();
                loadForeignKeyItems(comboBox, metadata);
                input = comboBox;
                
                // Set initial value if editing
                if (!isNew && entity != null) {
                    Object currentValue = getFieldValue(entity, fieldName);
                    String currentId = currentValue != null ? currentValue.toString() : null;
                    for (int i = 0; i < comboBox.getItemCount(); i++) {
                        if (comboBox.getItemAt(i).getId().equals(currentId)) {
                            comboBox.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            } else if (metadata.getType() == Date.class) {
                // Create formatted text field for date input
                JTextField dateField = new JTextField(10);
                dateField.setToolTipText("Enter date in YYYY-MM-DD format");
                if (!isNew && entity != null) {
                    Date value = (Date) getFieldValue(entity, fieldName);
                    if (value != null) {
                        dateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(value));
                    }
                }
                input = dateField;
            } else {
                // Create text field for other fields
                JTextField textField = new JTextField(20);
                if (!isNew && entity != null) {
                    Object value = getFieldValue(entity, fieldName);
                    textField.setText(value != null ? value.toString() : "");
                } else if (isNew && isAutoGeneratedId) {
                    // For new records, generate ID and make it read-only
                    textField.setText(metadata.generateId(tableName));
                    textField.setEditable(false);
                }
                input = textField;
            }
            
            // Make ID fields read-only when editing
            if (!isNew && fieldName.toLowerCase().endsWith("id")) {
                if (input instanceof JTextField) {
                    ((JTextField) input).setEditable(false);
                } else if (input instanceof JComboBox) {
                    ((JComboBox<?>) input).setEnabled(false);
                }
            }
            
            panel.add(label);
            panel.add(input);
            inputFields.put(fieldName, input);
        }
        
        // Add buttons
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            try {
                T newEntity = entityFactory.get();
                for (String fieldName : inputFields.keySet()) {
                    JComponent input = inputFields.get(fieldName);
                    String value = null;
                    
                    if (input instanceof JTextField) {
                        value = ((JTextField) input).getText();
                    } else if (input instanceof JComboBox) {
                        ComboBoxItem selected = (ComboBoxItem) ((JComboBox<?>) input).getSelectedItem();
                        value = selected != null ? selected.getId() : null;
                    }
                    
                    if (value != null && !value.isEmpty()) {
                        String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                        Method setter = newEntity.getClass().getMethod(setterName, fields.get(fieldName).getType());
                        
                        if (fields.get(fieldName).getType() == Date.class) {
                            try {
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                java.util.Date parsedDate = format.parse(value);
                                setter.invoke(newEntity, new java.sql.Date(parsedDate.getTime()));
                            } catch (java.text.ParseException pe) {
                                throw new Exception("Invalid date format. Please use YYYY-MM-DD format.");
                            }
                        } else if (fields.get(fieldName).getType() == Integer.class || fields.get(fieldName).getType() == int.class) {
                            setter.invoke(newEntity, Integer.parseInt(value));
                        } else {
                            setter.invoke(newEntity, value);
                        }
                    }
                }
                
                if (isNew) {
                    newEntity.create(connection);
                } else {
                    newEntity.update(connection);
                }
                
                updateTableModel(loadEntities());
                dialog.dispose();
                
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog,
                    "Error saving record: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
    }

    /**
     * Helper class for combo box items
     */
    private static class ComboBoxItem {
        private final String id;
        private final String display;

        public ComboBoxItem(String id, String display) {
            this.id = id;
            this.display = display;
        }

        public String getId() { return id; }

        @Override
        public String toString() {
            return display;
        }
    }

    /**
     * Load foreign key items into a combo box
     */
    private void loadForeignKeyItems(JComboBox<ComboBoxItem> comboBox, FieldMetadata metadata) {
        try {
            String sql = String.format("SELECT %s, %s FROM %s",
                metadata.getForeignKeyColumn(),
                metadata.getDisplayColumn(),
                metadata.getForeignKeyTable());
            
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    String id = rs.getString(1);
                    String display = rs.getString(2);
                    comboBox.addItem(new ComboBoxItem(id, display));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading foreign key data: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Get field value using reflection
     */
    private Object getFieldValue(T entity, String fieldName) {
        try {
            String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Method getter = entity.getClass().getMethod(getterName);
            return getter.invoke(entity);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Load all entities from the database
     */
    private List<T> loadEntities() throws SQLException {
        List<T> entities = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName;
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                T entity = entityFactory.get();
                for (String fieldName : fields.keySet()) {
                    String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    Method setter = entity.getClass().getMethod(setterName, fields.get(fieldName).getType());
                    
                    Object value;
                    if (fields.get(fieldName).getType() == Date.class) {
                        value = rs.getDate(fieldName);
                    } else if (fields.get(fieldName).getType() == Integer.class || fields.get(fieldName).getType() == int.class) {
                        value = rs.getInt(fieldName);
                    } else {
                        value = rs.getString(fieldName);
                    }
                    
                    if (value != null) {
                        setter.invoke(entity, value);
                    }
                }
                entities.add(entity);
            }
        } catch (Exception e) {
            throw new SQLException("Error loading entities: " + e.getMessage(), e);
        }
        return entities;
    }
} 