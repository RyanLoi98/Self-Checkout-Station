# Self-Checkout Station

![Self-Checkout System](https://i.imgur.com/eGhaige.png)

![Self-Checkout System](https://i.imgur.com/23MEaa2.png)

![Self-Checkout System](https://i.imgur.com/zUGFJSN.png)

Self-Checkout Station: A Java and Java Swing-based self-checkout system developed as part of a software engineering course. The project features item scanning, total calculations, payment integration, receipt printing, and inventory management. JUnit testing was utilized to ensure reliability. Designed to teach event listener design techniques, Object-Oriented Programming, and teamwork within a large group of over 25 people, the project presented challenges in coordination and time management but was successfully completed as a one-off project.

## Usage Instructions

### 1️⃣ Navigate to the Project Directory
Open a terminal and change to the project directory:

```sh
cd com.autovend.demo/src/com.autovend.demo
```

### 2️⃣ Run the Demo Environment
Execute the `demoEnv` file to launch the application:

```sh
./demoEnv  # Use `bash demoEnv` if needed
```

### 3️⃣ Application Overview
Running the `demoEnv` file will open multiple windows, simulating a self-checkout system:

- **Self-Checkout Station Window** 🛒  
  - The main interface where customers interact (add bags, call attendants, add items by product numbers, etc.) and complete purchases. Customers can enter their membership as well.
  - **Default Membership Accounts:**  
    - **Membership ID:** 56789, **User:** Mary Jane, **Points:** 100  
    - **Membership ID:** 12345, **User:** Peter Parker, **Points:** 100  

- **Keypad Hardware Simulator Window** ⌨️  
  - Simulates hardware like barcode scanners, allowing item scanning and other actions without physical hardware.

- **Admin Terminal Window / Attendant Terminal Window** 👨‍💻  
  A window will open asking for a username and password. Enter the respective credentials to open the attendant or admin window.

  - **Admin:**  
    - A control panel for store admins to manage the system and add new members.
    - **Login Credentials:**  
      - **Username:** `admin`  
      - **Password:** `admin`

  - **Attendant:**  
    - A control panel for self-checkout station attendants to manage individual stations and respond to help requests.
    - **Login Credentials:**  
      - **Username:** `test`  
      - **Password:** `test`

## Running Tests
Tests are available in the following directory:
```
com.autovend.software.test/src/com.autovend.software.test
```
To run the JUnit tests, navigate to the test directory and use the appropriate testing framework, JUnit.

## Requirements
Ensure you have the following dependencies installed:
- Java (JDK 11 or later)
- Bash (for executing `demoEnv`)

