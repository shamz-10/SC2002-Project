# SC2002 BTO Management System (BTOMS)

A comprehensive Java-based system for managing Build-To-Order (BTO) flat applications, officer assignments, and project management for the Housing & Development Board (HDB).

## Table of Contents
- [Features](#features)
- [System Architecture](#system-architecture)
- [Installation](#installation)
- [Usage](#usage)
- [User Roles](#user-roles)
- [Data Management](#data-management)
- [Development](#development)
- [Contributing](#contributing)
- [License](#license)

## Features

### For Applicants
- View available BTO projects
- Apply for BTO flats
- Submit enquiries
- Track application status
- Withdraw applications
- View application history

### For HDB Officers
- View assigned projects
- Process applications
- Reply to enquiries
- Manage flat bookings
- View project details
- Register for project handling

### For HDB Managers
- Create and manage BTO projects
- Assign officers to projects
- Approve/reject applications
- Manage project visibility
- Process withdrawal requests
- Generate reports

### System Features
- Secure authentication system
- Role-based access control
- Data persistence using CSV files
- Real-time status updates
- Comprehensive error handling
- User-friendly interface


### Directory Structure
```
BTOMS/
├── bin/         # Compiled class files
├── data/        # CSV data files
├── src/
│   ├── controllers/   # Application controllers
│   ├── enumeration/   # Enumerated types
│   ├── interfaces/    # Service interfaces
│   ├── main/          # Main application class
│   ├── models/        # Data models
│   ├── services/      # Business logic services
│   ├── stores/        # Data storage
│   ├── utils/         # Utility classes
│   └── view/          # User interface
```




### Key Components
- **Models**: Data structures and business objects
- **Controllers**: Application logic and flow control
- **Services**: Business logic implementation
- **Views**: User interface components
- **Stores**: Data persistence layer
- **Utils**: Helper functions and utilities

## Installation

### Prerequisites
- Java JDK 11 or higher
- Git (for version control)

### Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/shamz-10/SC2002-Project.git
   cd btoms
   ```

2. Build the project:
   ```bash
   find . -name "*.java" | xargs javac
   ```

3. Run the application:
   ```bash
   java main.BtomsApp   
   ```

## Usage

### Starting the System
1. Launch the application
2. Login with your credentials
3. Select your role (Applicant/HDB Officer/HDB Manager)
4. Access available features based on your role

### Data Files
The system uses CSV files for data storage:
- `ApplicantList.csv`: Applicant information
- `BTOProjectList.csv`: Project details
- `BTOApplicationList.csv`: Application records
- `HDBOfficerList.csv`: Officer information
- `HDBManagerList.csv`: Manager information
- `EnquiryList.csv`: Enquiry records
- `WithdrawalRequestList.csv`: Withdrawal requests

## User Roles

### Applicant
- Register and login
- View available projects
- Submit applications
- Track application status
- Submit enquiries
- Withdraw applications

### HDB Officer
- View assigned projects
- Process applications
- Reply to enquiries
- Register for project handling
- Manage flat bookings

### HDB Manager
- Create and manage projects
- Assign officers
- Approve/reject applications
- Manage project visibility
- Process withdrawal requests
- Generate reports

## Data Management

### Data Storage
- CSV-based storage system
- Automatic data backup
- Data validation and integrity checks
- Secure data handling

### Data Validation
- Input validation
- Business rule enforcement
- Error handling
- Data consistency checks

## Development

### Building from Source
1. Clone the repository
2. Install dependencies
3. Build the project
4. Run tests
5. Package the application



## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Collaborators

- [shamz-10](https://github.com/shamz-10)
- [riaz1017](https://github.com/riaz1017)
- [nalydch00](https://github.com/nalydch00)
