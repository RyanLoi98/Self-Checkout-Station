# Self-checkout Station

![Alt text](https://i.imgur.com/eGhaige.png)


![Alt text](https://i.imgur.com/23MEaa2.png)


![Self-Checkout System](https://i.imgur.com/zUGFJSN.png)


Self-checkout Station: A Java and Java Swing-based self-checkout system developed as part of a software engineering course. The project featured item scanning, total calculations, payment integration, receipt printing, and inventory management. JUnit testing was also utilized to ensure reliability. Designed to teach event listener design techniques and teamwork within a large group of over 25 people, the project presented challenges in coordination and time management but was successfully completed as a one-off project.



## Usage Instructions

### 1️⃣ Navigate to the Project Directory
Open a terminal and change to the project directory:

```sh
cd com.autovend.demo
cd src
cd com.autovend.demo
```

### 2️⃣ Run the Demo Environment
Execute the `demoEnv` file to launch the application:

```sh
./demoEnv  # Use `bash demoEnv` if needed
```

### 3️⃣ Application Overview
Running the `demoEnv` file will open multiple windows, simulating a self-checkout system:

- **Self-Checkout Station Window** 🛒  
  - The main interface where customers interact (add bags, call attendants, add item by product numbers, etc) and complete purchases. Customers can enter their membership as well - default membership accounts are as follows:
        Membership ID: 56789, User: Mary,Jane, Points: 100
        Membership ID: 12345, User:Peter, Points: Parker,100
    

- **Keypad Hardware Simulator Window** ⌨️  
  - Simulates hardware like barcode scanners, allowing item scanning and other actions without physical hardware.


- **Admin Terminal Window/Attedant Terminal Window** 👨‍💻

A window will open up asking for a username and password, if you enter the attedant's credentials the attendant window will open, if you enter the admin's credentials then the admin window will open.

  Admin:
  - A control panel for store admins to manage the system and add new members.
  - **Login Credentials**:  
    - **Username**: `admin`  
    - **Password**: `admin`

   Attendant:
  - A control panel for self-checkout station attendants to manage the indvidual stations and respond to help requests.
    - **Login Credentials**:  
      - **Username**: `test`  
      - **Password**: `test`
  

## Running Tests
Tests are available in the following directory:
```
com.autovend.software.test/src/com.autovend.software.test
```
To run the JUnit tests, navigate to the test directory and use the appropriate testing framework which is JUnit.

## Requirements
Ensure you have the following dependencies installed:
- Java (JDK 11 or later)
- Bash (for executing `demoEnv`)






