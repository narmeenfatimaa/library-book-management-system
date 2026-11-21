# MCS Library Management System

A Java Swing desktop application for the library of the **Military College of Signals (MCS), NUST**.
Developed for the Software Construction Open-Ended Lab (OEL).

**Group members:**
- Narmeen Fatima (504271)
- Faique Mustafa (534731)
- Usaid Rauf (534737)

## Sign in

The program starts with a sign-in window. Choose **Student** or **Staff**.

| Role | How to get an account | What the role can do |
|---|---|---|
| **Staff** (librarian) | Created automatically: username `librarian`, password `mcs2026` | Everything: add, update, delete, issue, reserve and return books, see all students and the activity log |
| **Student** | Click "Create an account" (registration number, name, password) | Search the catalogue, reserve a book, cancel their own reservation, see "My Books", fines and account page. Students cannot change books and cannot see other people's names |

Passwords are never stored. Each account keeps only a random salt and the SHA-256 hash of the salt and the password.

## Features

Required by the OEL:
- Add a book (ID, title, author), view all books in a table, update, delete
- Set a book's status: Available / Issued / Reserved
- Input validation (no empty ID, title or author; no duplicate IDs) with clear error messages

Extra features:
- **Login with two roles** and different access control (see above), plus student self-registration
- **Dashboard** cards (total, available, issued, reserved, overdue for staff; personal numbers for students) that count up when they change
- **Search** by ID, title, author, category or borrower while typing, a **status filter**, and **sorting** by clicking a column title
- **Categories** for every book
- **Issue and reserve to a registered student**, with a due date (loan of 1 to 60 days, 14 by default)
- **Borrowing limit:** a member can hold at most 3 books (issued and reserved together)
- **Fines:** Rs 10 for each day an issued book is overdue; shown in the table, on the dashboard and when the book is returned
- **Students reserve books themselves** and can cancel their own reservation
- An issued book **cannot be deleted** until it has been returned
- **Members page** (staff): all students with their issued and reserved books and fines
- **Activity log** (staff): who did what and when
- **Automatic saving** to CSV files, loaded again when the program starts
- Colored status badges, row highlighting under the mouse, empty-state messages

## Requirements
- JDK 17 or later
- IntelliJ IDEA, NetBeans or Eclipse (or the command line)

## Project structure
```text
src/library/
    Main.java            starts the program, loads the saved data, shows the sign-in window
    LoginWindow.java     sign-in and student registration window
    LibraryGUI.java      the main window (staff console and student portal) and its event handling
    Ui.java              colors and drawing components (appearance only)
    LibraryManager.java  the book list (ArrayList) and all book rules, linear search by ID
    Book.java            a book (ID, title, author, category, status, borrower, due date, fine)
    BookStatus.java      Available, Issued, Reserved
    Role.java            Staff or Student
    User.java            an account (password stored as salted hash)
    UserManager.java     registration and login
    ActivityEntry.java   one line of the activity log
    ActivityLog.java     the activity log
    DataStorage.java     saves and loads books, accounts and activity as CSV files
    CsvUtil.java         helper for reading and writing CSV lines
docs/apidocs/            generated Javadoc (open index.html)
docs/TEST_PLAN.md        test cases, results and screenshots
screenshots/             screenshots of the running application
```

## Run in IntelliJ IDEA
1. Open the project folder in IntelliJ IDEA and set the project SDK to JDK 17.
2. Open `src/library/Main.java` and click the green run arrow beside `main`.

## Run from the command line
Run these commands from the project folder:
```bash
mkdir -p out
javac -d out src/library/*.java
java -cp out library.Main
```

## Saved data
Three files are created in the folder the program is started from:
`library_books.csv`, `library_users.csv` and `library_activity.csv`.
Delete them to start again with an empty library (the staff account is created again automatically).

## Generate the Javadoc
```bash
javadoc -d docs/apidocs -sourcepath src -author -encoding UTF-8 -docencoding UTF-8 -charset UTF-8 -windowtitle "MCS Library Management System" -doctitle "MCS Library Management System API" library
```
Then open `docs/apidocs/index.html` in a browser.
