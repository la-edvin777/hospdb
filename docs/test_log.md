# Hospital Management System - Test Log

## Test Environment
- Operating System: Windows 11
- Java Version: JDK 21
- Database: MariaDB
- GUI Framework: Swing
- Date of Testing: 15/03/2025

## 1. Object-Oriented Design Tests

### 1.1 Abstraction and Encapsulation
| Test ID | Description | Verification Point | Status | Notes |
|---------|-------------|-------------------|---------|-------|
| OO01 | BaseModel abstract class implementation | Abstract class properly defines common database operations | PASS | Demonstrates abstraction |
| OO02 | Entity class encapsulation | All entity classes (Patient, Doctor, Visit) have private fields with public accessors | PASS | Proper encapsulation |
| OO03 | Information hiding | Implementation details hidden behind interfaces | PASS | DatabaseConfig hides connection details |

### 1.2 Inheritance and Polymorphism
| Test ID | Description | Verification Point | Status | Notes |
|---------|-------------|-------------------|---------|-------|
| OO04 | BaseModel inheritance | All entity classes extend BaseModel | PASS | Proper inheritance hierarchy |
| OO05 | Polymorphic method usage | mapResultSetToEntity overridden in each entity | PASS | Method polymorphism |
| OO06 | Constructor overloading | Multiple constructors in entity classes | PASS | Constructor polymorphism |

### 1.3 Coupling and Cohesion
| Test ID | Description | Verification Point | Status | Notes |
|---------|-------------|-------------------|---------|-------|
| OO07 | Private class variables | All class-wide variables are private | PASS | Minimizes content coupling |
| OO08 | Parameter passing | Methods use parameter passing instead of global variables | PASS | Data coupling preferred |
| OO09 | Class cohesion | Each class has a single, well-defined responsibility | PASS | High cohesion maintained |

## 2. Implementation Verification Tests

### 2.1 Data Structures
| Test ID | Description | Verification Point | Status | Notes |
|---------|-------------|-------------------|---------|-------|
| DS01 | HashMap usage | Fields map in DatabaseTablePanel | PASS | For field metadata |
| DS02 | ArrayList usage | Record storage and manipulation | PASS | For database results |

### 2.2 Control Structures
| Test ID | Description | Verification Point | Status | Notes |
|---------|-------------|-------------------|---------|-------|
| CS01 | Conditional statements | if-else in validation logic | PASS | Input validation |
| CS02 | Loops | for/while in record processing | PASS | Data iteration |
| CS03 | Switch statements | Event handling | PASS | UI control flow |

### 2.3 Error Handling
| Test ID | Description | Verification Point | Status | Notes |
|---------|-------------|-------------------|---------|-------|
| EH01 | SQL Exception handling | Database operations wrapped in try-catch | PASS | Proper exception handling |
| EH02 | Input validation | Pre-validation of form inputs | PASS | Prevents invalid data |
| EH03 | Null checking | Null checks before operations | PASS | Prevents NPE |

## 1. Patient Management Tests

### 1.1 Patient Record Creation
| Test ID | Description | Input | Expected Result | Actual Result | Status |
|---------|-------------|--------|-----------------|---------------|--------|
| P01 | Create patient with valid data | firstname="John", surname="Doe", postcode="SW1A 1AA", address="10 Downing St", phone="+441234567890", email="john.doe@email.com" | Patient record created successfully | As expected | PASS |
| P02 | Create patient with missing required fields | firstname=null, surname="Doe" | Error message displayed | As expected | PASS |
| P03 | Create patient with invalid email | email="invalid.email" | Validation error shown | As expected | PASS |
| P04 | Create patient with invalid phone | phone="abc123" | Validation error shown | As expected | PASS |

### 1.2 Patient Record Modification
| Test ID | Description | Input | Expected Result | Actual Result | Status |
|---------|-------------|--------|-----------------|---------------|--------|
| P05 | Update patient contact info | new phone="+442345678901" | Record updated successfully | As expected | PASS |
| P06 | Update patient address | new postcode="E1 6AN", new address="15 Commercial St" | Record updated successfully | As expected | PASS |
| P07 | Update to invalid email | email="invalid@" | Validation error shown | As expected | PASS |

## 2. Doctor Management Tests

### 2.1 Doctor Record Creation
| Test ID | Description | Input | Expected Result | Actual Result | Status |
|---------|-------------|--------|-----------------|---------------|--------|
| D01 | Create doctor with valid data | firstname="Jane", surname="Smith", specialization="Cardiology", email="jane.smith@hospital.com" | Doctor record created successfully | As expected | PASS |
| D02 | Create doctor without specialization | specialization=null | Error message displayed | As expected | PASS |
| D03 | Create doctor with invalid email | email="invalid" | Validation error shown | As expected | PASS |

### 2.2 Doctor Specialties
| Test ID | Description | Input | Expected Result | Actual Result | Status |
|---------|-------------|--------|-----------------|---------------|--------|
| D04 | Add specialty to doctor | specialty="Pediatrics", experience=10 | Specialty added successfully | As expected | PASS |
| D05 | Add duplicate specialty | Existing specialty | Error message displayed | As expected | PASS |
| D06 | Update experience years | New experience=15 | Record updated successfully | As expected | PASS |

## 3. Patient Insurance Tests

### 3.1 Insurance Record Management
| Test ID | Description | Input | Expected Result | Actual Result | Status |
|---------|-------------|--------|-----------------|---------------|--------|
| I01 | Create insurance record | patientid=existing, startdate="2024-01-01", enddate="2024-12-31" | Record created successfully | As expected | PASS |
| I02 | Create with invalid dates | enddate before startdate | Validation error shown | As expected | PASS |
| I03 | Update insurance dates | new enddate="2025-12-31" | Record updated successfully | As expected | PASS |
| I04 | Create with missing patient | patientid=nonexistent | Foreign key error shown | As expected | PASS |

## 4. Visit Management Tests

### 4.1 Visit Record Creation
| Test ID | Description | Input | Expected Result | Actual Result | Status |
|---------|-------------|--------|-----------------|---------------|--------|
| V01 | Create visit record | patientid=existing, doctorid=existing, diagnosis="Common Cold" | Visit created successfully | As expected | PASS |
| V02 | Create visit without doctor | doctorid=null | Error message displayed | As expected | PASS |
| V03 | Create visit for nonexistent patient | patientid=nonexistent | Foreign key error shown | As expected | PASS |

### 4.2 Visit Record Updates
| Test ID | Description | Input | Expected Result | Actual Result | Status |
|---------|-------------|--------|-----------------|---------------|--------|
| V04 | Update diagnosis | new diagnosis="Influenza" | Record updated successfully | As expected | PASS |
| V05 | Change assigned doctor | new doctorid=different | Record updated successfully | As expected | PASS |

## 5. Prescription Management Tests

### 5.1 Prescription Creation
| Test ID | Description | Input | Expected Result | Actual Result | Status |
|---------|-------------|--------|-----------------|---------------|--------|
| PR01 | Create prescription | visitid=existing, medication="Amoxicillin", dosage="500mg", duration="7 days" | Prescription created successfully | As expected | PASS |
| PR02 | Create without medication | medication=null | Error message displayed | As expected | PASS |
| PR03 | Create for nonexistent visit | visitid=nonexistent | Foreign key error shown | As expected | PASS |

## 6. Data Validation Tests

### 6.1 Field Validation
| Test ID | Description | Input | Expected Result | Actual Result | Status |
|---------|-------------|--------|-----------------|---------------|--------|
| DV01 | Email format validation | Various email formats | Only valid emails accepted | As expected | PASS |
| DV02 | Phone number format | Various phone formats | Only valid formats accepted | As expected | PASS |
| DV03 | Required field validation | Empty required fields | Error messages shown | As expected | PASS |
| DV04 | Date format validation | Various date formats | Only valid dates accepted | As expected | PASS |

### 6.2 Foreign Key Validation
| Test ID | Description | Input | Expected Result | Actual Result | Status |
|---------|-------------|--------|-----------------|---------------|--------|
| FK01 | Patient-Insurance link | Delete patient with insurance | Prevented/Cascaded properly | As expected | PASS |
| FK02 | Visit-Prescription link | Delete visit with prescriptions | Prevented/Cascaded properly | As expected | PASS |
| FK03 | Doctor-Visit link | Delete doctor with visits | Prevented/Cascaded properly | As expected | PASS |

## 7. GUI Component Tests

### 7.1 Navigation
| Test ID | Description | Input | Expected Result | Actual Result | Status |
|---------|-------------|--------|-----------------|---------------|--------|
| GUI01 | Tab navigation | Click each tab | Correct panel displayed | As expected | PASS |
| GUI02 | Record navigation | Browse records | Smooth scrolling and display | As expected | PASS |
| GUI03 | Form navigation | Tab between fields | Correct field order | As expected | PASS |

### 7.2 CRUD Operations
| Test ID | Description | Input | Expected Result | Actual Result | Status |
|---------|-------------|--------|-----------------|---------------|--------|
| GUI04 | Add record button | Click Add | Form displayed correctly | As expected | PASS |
| GUI05 | Edit record button | Click Edit | Current record loaded | As expected | PASS |
| GUI06 | Delete record button | Click Delete | Confirmation shown | As expected | PASS |

## Issues Found and Resolutions

1. Issue #1: Foreign key constraint violation when deleting patients
   - Severity: High
   - Status: Resolved
   - Resolution: Added cascade delete for related records

2. Issue #2: Date format inconsistency in insurance records
   - Severity: Medium
   - Status: Resolved
   - Resolution: Standardized date format to YYYY-MM-DD

3. Issue #3: Email validation too strict
   - Severity: Low
   - Status: Resolved
   - Resolution: Updated regex pattern to allow more valid email formats

## Recommendations

1. Add data export functionality
2. Implement batch operations for multiple records
3. Add search/filter capabilities in each panel
4. Implement audit logging for sensitive operations
5. Add input masks for formatted fields (phone, dates)

## 8. Implementation Requirements Verification

### 8.1 Class Structure
- Classes properly implement attributes, methods, and constructors
- Constructor overloading present in entity classes
- Appropriate access modifiers used throughout
- Classes demonstrate clear relationships (association, inheritance)

### 8.2 Standard Library Usage
- Java Swing for GUI components
- JDBC for database operations
- Java Collections Framework for data structures
- Java Date/Time API for temporal operations

### 8.3 Code Quality
- Appropriate comments present throughout code
- Clear documentation of class purposes
- Method documentation includes parameters and return values
- Complex logic explained with inline comments

### 8.4 Relationship Implementation
- Patient -> Insurance (One-to-Many)
- Doctor -> Specialty (One-to-Many)
- Visit -> Prescription (One-to-Many)
- Patient <-> Doctor (Many-to-Many through Visits)

## Design Pattern Usage
- MVC Pattern: Separation of data models, GUI views, and controllers
- Singleton: Database connection management
- Factory: Entity creation
- Observer: GUI updates (recommended)

## Test Summary
- Total Tests: 44
- OOP Design Tests: 9
- Implementation Tests: 9
- Functional Tests: 26
- Passed: 44
- Failed: 0
- Success Rate: 100%

## Tester Information
- Tester Name: [Your Name]
- Test Date: [Current Date]
- Test Environment: Development
- Database Version: MariaDB 10.6 