# Library Management System 2026

Modern Java GUI Library Management System built with FlatLaf, Oracle Database connectivity, BCrypt password hashing, and role-based workflows (Admin, Librarian, Student).

---

## 🛠 Prerequisites
- **JDK 17 or higher** (JDK 21 recommended)
- **Oracle Database** (XE 18c/21c or 23c with `XEPDB1` or similar PDB)

---

## 🔨 How to Compile

### Option 1: Using the provided Batch Script (Windows)
Double-click compile.bat

### Option 2: Using the Command Prompt / PowerShell
Open your terminal in this directory and execute:

```cmd
javac -cp ".;flatlaf-3.5.4.jar;jbcrypt-0.4.jar;ojdbc17-23.26.3.0.0.jar" *.java
```

---

## 🚀 How to Run

### Option 1: Using the provided Batch Script (Windows)
Double-click run.bat

### Option 2: Using the Command Prompt / PowerShell
```cmd
java -cp ".;flatlaf-3.5.4.jar;jbcrypt-0.4.jar;ojdbc17-23.26.3.0.0.jar" Main
```

---

## 🔑 Login Credentials

1. **Database Setup Dialog**:
   - Host: `localhost`
   - Port: `1521`
   - Service: `XEPDB1`
   - Username: `system` (or your Oracle DB user)
   - Password: `<your_oracle_password>`

2. **Application Login**:
   - **Default Administrator**: `admin` / `admin`
   - Or any registered member account (Admin, Librarian, Student).