# Discussion Board Updates

## Q: Do we need to include Audio I/O?
A: Feature has been poorly considered, has serious issues, company has spent a lot of time working with customers to understand this, but it just isn't ready. **DROP THIS.** (see https://d2l.ucalgary.ca/d2l/le/497088/discussions/threads/1877426/View).

## Q: Bagging Dispenser still doesn't exist.
A: Same as iteration 2; just maintain flexibility to add it when it indeed arrives. (see https://d2l.ucalgary.ca/d2l/le/497088/discussions/threads/1877429/ViewPostInContext?postId=5133895#post5133895).

## Q: Enter Membership Number by Scanning doesn't seem possible.
A: It should now be possible with the new hardware release. (see https://d2l.ucalgary.ca/d2l/le/497088/discussions/threads/1877841/View).

## Q: How do we check if the attendant entered in the correct username for login or logout?
A: You should implement a fake "database" in your code upon which you can check usernames and passwords; populate it with some "dummy" attendant login info. (see https://d2l.ucalgary.ca/d2l/le/497088/discussions/threads/1878152/View).

## Q: How do we programatically test the GUI?
A: Java Swing components have a "doClick" or similar method that simulates a user click. You would simply invoke this on the button you want to test. For example, to test an addItem button, you would simply invoke doClick on addItem and see if the expected result occurs (don't wait for the user to click on the actual GUI). (see https://d2l.ucalgary.ca/d2l/le/497088/discussions/threads/1878235/View).
