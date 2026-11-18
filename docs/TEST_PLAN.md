# Test Plan and Results

Application: Library Book Management System. Data is kept in memory only, so **start the application fresh before TC01** and run the tests **in order**, because each test builds on the previous one.

**Test environment:** macOS, JDK 17.0.18 (Temurin), IntelliJ IDEA, Java Swing application run from `library.Main`.
**Tested by:** Narmeen Fatima  **Date:** 18 November 2026

**Summary (as reported by the tester):** 23 test cases run, 23 passed, 0 failed. No bugs were found, so no bug-fix commits were needed.

| ID | Test case | Steps and input | Expected result | Actual result | Pass/Fail | Screenshot file |
|---|---|---|---|---|---|---|
| TC01 | View an empty library | Start the app fresh (Run Main). Do nothing else. | Table has no rows. Bottom line reads: "The library is empty. Add a book using the form above." | Matched the expected result: Table has no rows. Bottom line reads: "The library is empty. Add a book using the form above." | Pass | [TC01_empty_library_main_window.png](../screenshots/TC01_empty_library_main_window.png) |
| TC02 | Add a valid book | ID `B001`, Title `Clean Code`, Author `Robert Martin` -> click Add. | One row B001 / Clean Code / Robert Martin / Available. Form clears. Green message: Book "Clean Code" (ID B001) added successfully. Bottom line: Total books: 1. | Matched the expected result: One row B001 / Clean Code / Robert Martin / Available. Form clears. Green message: Book "Clean Code" (ID B001) added successfully. Bottom line: Total books: 1. | Pass | [TC02_add_valid.png](../screenshots/TC02_add_valid.png) |
| TC03 | Add a book with an empty title | Click Clear. ID `B002`, Title (leave empty), Author `Test Author` -> Add. | Error dialog "Title cannot be empty." Red line at bottom. Table still has 1 row. Click OK. | Matched the expected result: Error dialog "Title cannot be empty." Red line at bottom. Table still has 1 row. Click OK. | Pass | [TC03_empty_title.png](../screenshots/TC03_empty_title.png) |
| TC04 | Add a book with an empty author | Click Clear. ID `B002`, Title `Test Title`, Author (leave empty) -> Add. | Error dialog "Author cannot be empty." Table still has 1 row. Click OK. | Matched the expected result: Error dialog "Author cannot be empty." Table still has 1 row. Click OK. | Pass | [TC04_empty_author.png](../screenshots/TC04_empty_author.png) |
| TC05 | Add a book with an empty ID | Click Clear. ID (leave empty), Title `Test Title`, Author `Test Author` -> Add. | Error dialog "Book ID cannot be empty." Table still has 1 row. Click OK. | Matched the expected result: Error dialog "Book ID cannot be empty." Table still has 1 row. Click OK. | Pass | TC05_empty_id.png |
| TC06 | Add a book with a duplicate ID | Click Clear. ID `B001`, Title `Another Book`, Author `Another Author` -> Add. | Error dialog: A book with ID "B001" already exists. Table still has 1 row. Click OK. | Matched the expected result: Error dialog: A book with ID "B001" already exists. Table still has 1 row. Click OK. | Pass | [TC06_duplicate_id.png](../screenshots/TC06_duplicate_id.png) |
| TC07 | Duplicate ID in different letter case | Click Clear. ID `b001` (lowercase), Title `Another Book`, Author `Another Author` -> Add. | Error dialog: A book with ID "b001" already exists. Table still has 1 row. Click OK. | Matched the expected result: Error dialog: A book with ID "b001" already exists. Table still has 1 row. Click OK. | Pass | TC07_duplicate_id_case.png (optional) |
| TC08 | View all books | Click Clear. Add ID `B002`, Title `Effective Java`, Author `Joshua Bloch` (Add). Then add ID `B003`, Title `Head First Java`, Author `Kathy Sierra` (Add). | Table shows three rows in this order: B001, B002, B003, all Available. Bottom line: Total books: 3. | Matched the expected result: Table shows three rows in this order: B001, B002, B003, all Available. Bottom line: Total books: 3. | Pass | [TC08_view_all_books.png](../screenshots/TC08_view_all_books.png) |
| TC09 | Selecting a book fills the form | Click the B002 row in the table. | Form shows ID B002, Title Effective Java, Author Joshua Bloch, Status Available. The row is highlighted. | Matched the expected result: Form shows ID B002, Title Effective Java, Author Joshua Bloch, Status Available. The row is highlighted. | Pass | TC09_select_row.png (optional) |
| TC10 | Update an existing book | With B002 still selected, change Title to `Effective Java 3rd Edition` -> Update. | Row B002 now shows the new title. Green message: Book B002 updated successfully. Form clears. | Matched the expected result: Row B002 now shows the new title. Green message: Book B002 updated successfully. Form clears. | Pass | [TC10_update_success.png](../screenshots/TC10_update_success.png) |
| TC11 | Update a book to an ID that belongs to another book | Click the B003 row. Change the ID field to `B001` -> Update. | Error dialog: Cannot update: ID "B001" already belongs to another book. Row B003 is unchanged. Click OK. | Matched the expected result: Error dialog: Cannot update: ID "B001" already belongs to another book. Row B003 is unchanged. Click OK. | Pass | [TC11_update_duplicate_id.png](../screenshots/TC11_update_duplicate_id.png) |
| TC12 | Update a non-existing book | Click Clear. ID `Z999`, Title `Ghost Book`, Author `Nobody` -> Update. | Error dialog: Cannot update: no book with ID "Z999" exists. Table unchanged (3 rows). Click OK. | Matched the expected result: Error dialog: Cannot update: no book with ID "Z999" exists. Table unchanged (3 rows). Click OK. | Pass | [TC12_update_nonexistent.png](../screenshots/TC12_update_nonexistent.png) |
| TC13 | Set status to Issued | Click Clear, then click the B001 row. Set the Status box to `Issued` -> Set Status. | Row B001 shows Issued. Green message: Status of book B001 set to Issued. | Matched the expected result: Row B001 shows Issued. Green message: Status of book B001 set to Issued. | Pass | [TC13_status_issued.png](../screenshots/TC13_status_issued.png) |
| TC14 | Set status to Reserved | Click the B001 row. Set the Status box to `Reserved` -> Set Status. | Row B001 shows Reserved. Green message: Status of book B001 set to Reserved. | Matched the expected result: Row B001 shows Reserved. Green message: Status of book B001 set to Reserved. | Pass | [TC14_status_reserved.png](../screenshots/TC14_status_reserved.png) |
| TC15 | Set status back to Available | Click the B001 row. Set the Status box to `Available` -> Set Status. | Row B001 shows Available. Green message: Status of book B001 set to Available. | Matched the expected result: Row B001 shows Available. Green message: Status of book B001 set to Available. | Pass | TC15_status_available.png (optional) |
| TC16 | Set status of a non-existing book | Click Clear. ID `Z999`. Status box `Issued` -> Set Status. | Error dialog: Cannot change status: no book with ID "Z999" exists. Table unchanged. Click OK. | Matched the expected result: Error dialog: Cannot change status: no book with ID "Z999" exists. Table unchanged. Click OK. | Pass | TC16_status_nonexistent.png (optional) |
| TC17 | Cancel a delete | Click Clear, then click the B003 row -> Delete. In the confirmation dialog click No. | Dialog asks: Delete book B003 (Head First Java)? After No, the table still has 3 rows. | Matched the expected result: Dialog asks: Delete book B003 (Head First Java)? After No, the table still has 3 rows. | Pass | TC17_delete_cancelled.png (optional) |
| TC18 | Delete an existing book | Click the B003 row -> Delete -> click Yes. | Row B003 disappears. Green message: Book B003 deleted successfully. Bottom line: Total books: 2. | Matched the expected result: Row B003 disappears. Green message: Book B003 deleted successfully. Bottom line: Total books: 2. | Pass | [TC18_delete_success.png](../screenshots/TC18_delete_success.png) |
| TC19 | Delete a non-existing book | Click Clear. ID `Z999` -> Delete. | Error dialog: Cannot delete: no book with ID "Z999" exists. Table unchanged (2 rows). Click OK. | Matched the expected result: Error dialog: Cannot delete: no book with ID "Z999" exists. Table unchanged (2 rows). Click OK. | Pass | TC19_delete_nonexistent.png (optional) |
| TC20 | Delete with no ID entered | Click Clear (nothing selected, ID empty) -> Delete. | Error dialog: Book ID cannot be empty. Table unchanged. Click OK. | Matched the expected result: Error dialog: Book ID cannot be empty. Table unchanged. Click OK. | Pass | (no screenshot needed) |
| TC21 | Clear button | Click the B001 row so the form fills, then click Clear. | All text fields are empty, Status box shows Available, no row is highlighted, the message line is empty. | Matched the expected result: All text fields are empty, Status box shows Available, no row is highlighted, the message line is empty. | Pass | TC21_clear.png (optional) |
| TC22 | Empty library after deleting all books | Click the B001 row -> Delete -> Yes. Click the B002 row -> Delete -> Yes. | Table has no rows. Bottom line reads: The library is empty. Add a book using the form above. | Matched the expected result: Table has no rows. Bottom line reads: The library is empty. Add a book using the form above. | Pass | [TC22_empty_after_delete.png](../screenshots/TC22_empty_after_delete.png) |
| TC23 | Data is not kept after closing the app | Add ID `B100`, Title `Temp Book`, Author `Temp Author`. Quit the app (Cmd+Q), then run Main again. | The new window starts empty with the empty-library message (data is kept in memory only). | Matched the expected result: The new window starts empty with the empty-library message (data is kept in memory only). | Pass | (no screenshot needed) |

## Screenshots

All screenshots were taken from the running application.

**TC01 - View an empty library**

![TC01](../screenshots/TC01_empty_library_main_window.png)

**TC02 - Add a valid book**

![TC02](../screenshots/TC02_add_valid.png)

**TC03 - Add a book with an empty title**

![TC03](../screenshots/TC03_empty_title.png)

**TC04 - Add a book with an empty author**

![TC04](../screenshots/TC04_empty_author.png)

**TC06 - Add a book with a duplicate ID**

![TC06](../screenshots/TC06_duplicate_id.png)

**TC08 - View all books**

![TC08](../screenshots/TC08_view_all_books.png)

**TC10 - Update an existing book**

![TC10](../screenshots/TC10_update_success.png)

**TC11 - Update a book to an ID that belongs to another book**

![TC11](../screenshots/TC11_update_duplicate_id.png)

**TC12 - Update a non-existing book**

![TC12](../screenshots/TC12_update_nonexistent.png)

**TC13 - Set status to Issued**

![TC13](../screenshots/TC13_status_issued.png)

**TC14 - Set status to Reserved**

![TC14](../screenshots/TC14_status_reserved.png)

**TC18 - Delete an existing book**

![TC18](../screenshots/TC18_delete_success.png)

**TC22 - Empty library after deleting all books**

![TC22](../screenshots/TC22_empty_after_delete.png)

