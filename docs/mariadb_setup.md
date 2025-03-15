# MariaDB Setup Guide for Hospital Management System

## Prerequisites
- Windows 10 or later
- Administrative privileges on your workstation
- At least 1GB of free disk space
- Internet connection for downloading

## 1. Download and Install MariaDB

### 1.1 Download MariaDB
1. Visit the official MariaDB download page: https://mariadb.org/download/
2. Select the following options:
   - Choose "Windows" as your operating system
   - Select "10.6" as the version (this is the version used in development)
   - Choose "MSI Package" as the package format
3. Click the download button for the x64 version

### 1.2 Install MariaDB
1. Run the downloaded MSI file as administrator
2. Follow the installation wizard:
   - Accept the license agreement
   - Choose "Complete" installation type
   - Set root password (IMPORTANT: Remember this password!)
   - Keep the default port (3306)
   - Check "Install as service" and "Enable networking"
3. Click "Install" and wait for the process to complete

## 2. Database Configuration

### 2.1 Initial Setup
1. Open Command Prompt as administrator
2. Connect to MariaDB:
```sql
mysql -u root -p
```
3. Enter the root password you set during installation

### 2.2 Create Database and User
1. Create the hospital management database:
```sql
CREATE DATABASE hospital_management;
```

2. Create a dedicated user for the application:
```sql
CREATE USER 'hospital_user'@'localhost' IDENTIFIED BY 'your_password_here';
```

3. Grant privileges to the user:
```sql
GRANT ALL PRIVILEGES ON hospital_management.* TO 'hospital_user'@'localhost';
FLUSH PRIVILEGES;
```

## 3. Database Schema Setup

### 3.1 Create Tables
Execute the following SQL commands to create the required tables:

```sql
USE hospital_management;

CREATE TABLE doctor (
    doctorid VARCHAR(10) PRIMARY KEY,
    firstname VARCHAR(50) NOT NULL,
    surname VARCHAR(50) NOT NULL,
    address VARCHAR(100),
    email VARCHAR(100),
    specialization VARCHAR(50)
);

CREATE TABLE patient (
    patientid VARCHAR(10) PRIMARY KEY,
    firstname VARCHAR(50) NOT NULL,
    surname VARCHAR(50) NOT NULL,
    postcode VARCHAR(10),
    address VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    insuranceid VARCHAR(10)
);

CREATE TABLE visit (
    visitid VARCHAR(10) PRIMARY KEY,
    patientid VARCHAR(10),
    doctorid VARCHAR(10),
    visitdate DATE,
    diagnosis TEXT,
    FOREIGN KEY (patientid) REFERENCES patient(patientid),
    FOREIGN KEY (doctorid) REFERENCES doctor(doctorid)
);

CREATE TABLE prescription (
    prescriptionid VARCHAR(10) PRIMARY KEY,
    visitid VARCHAR(10),
    medication VARCHAR(100),
    dosage VARCHAR(50),
    duration VARCHAR(50),
    FOREIGN KEY (visitid) REFERENCES visit(visitid)
);

CREATE TABLE doctorspecialty (
    doctorid VARCHAR(10),
    specialty VARCHAR(50),
    experience INTEGER,
    PRIMARY KEY (doctorid, specialty),
    FOREIGN KEY (doctorid) REFERENCES doctor(doctorid)
);

CREATE TABLE patientinsurance (
    insuranceid VARCHAR(10),
    patientid VARCHAR(10),
    startdate DATE,
    enddate DATE,
    PRIMARY KEY (insuranceid, patientid),
    FOREIGN KEY (patientid) REFERENCES patient(patientid)
);
```

## 4. Configure Java Application

### 4.1 Update Database Connection Settings
1. Locate the `DatabaseConfig.java` file in the project
2. Update the following parameters:
```java
private static final String URL = "jdbc:mariadb://localhost:3306/hospital_management";
private static final String USER = "hospital_user";
private static final String PASSWORD = "your_password_here";
```

### 4.2 Add MariaDB JDBC Driver
1. Add the MariaDB JDBC driver dependency to your project:
   
If using Maven (`pom.xml`):
```xml
<dependency>
    <groupId>org.mariadb.jdbc</groupId>
    <artifactId>mariadb-java-client</artifactId>
    <version>3.1.4</version>
</dependency>
```

If using Gradle (`build.gradle`):
```groovy
implementation 'org.mariadb.jdbc:mariadb-java-client:3.1.4'
```

## 5. Verify Setup

### 5.1 Test Database Connection
1. Open Command Prompt
2. Connect to MariaDB and verify tables:
```sql
mysql -u hospital_user -p
USE hospital_management;
SHOW TABLES;
```

### 5.2 Test Application Connection
1. Run the Hospital Management System application
2. Verify that you can:
   - View all tabs
   - Add new records
   - View existing records
   - Update records
   - Delete records

## 6. Troubleshooting

### 6.1 Common Issues and Solutions

1. Connection refused:
   - Verify MariaDB service is running
   - Check Windows Services
   - Restart MariaDB service if needed

2. Access denied:
   - Verify username and password
   - Check user privileges
   - Reset user permissions if needed

3. Port conflicts:
   - Verify port 3306 is not in use
   - Change port in MariaDB configuration if needed

### 6.2 Getting Help
- Official MariaDB documentation: https://mariadb.com/kb/en/documentation/
- MariaDB community forums: https://mariadb.com/kb/en/community/
- Project documentation: See `docs/` folder

## 7. Backup and Maintenance

### 7.1 Regular Backups
Execute the following command to backup the database:
```bash
mysqldump -u root -p hospital_management > backup.sql
```

### 7.2 Restore from Backup
To restore from a backup:
```bash
mysql -u root -p hospital_management < backup.sql
```

## 8. Security Recommendations

1. Regular password updates
2. Limit remote access
3. Keep MariaDB updated
4. Regular security audits
5. Implement proper backup strategy 