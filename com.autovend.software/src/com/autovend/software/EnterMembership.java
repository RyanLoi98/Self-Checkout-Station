package com.autovend.software;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.autovend.Barcode;
import com.autovend.Card.CardData;
import com.autovend.devices.AbstractDevice;
import com.autovend.devices.BarcodeScanner;
import com.autovend.devices.CardReader;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.observers.AbstractDeviceObserver;
import com.autovend.devices.observers.BarcodeScannerObserver;
import com.autovend.devices.observers.CardReaderObserver;

/**
 * Enter membership controller class
 *
 */
public class EnterMembership implements BarcodeScannerObserver, CardReaderObserver{

    // EnterMembershipObserver implementation
	private SelfCheckoutStation scs;
	


    // Arraylist storing Entermembership observers
    protected ArrayList<EnterMembershipObserver> observers = new ArrayList<>();

    /**
     * Registers EnterMembershipObservers to receive event notifications from EnterMembership logic.
     * Used by Customer I/O to communicate with Logic.
     *
     * @param observer: The observer to be added.
     */
    public void registerObserver(EnterMembershipObserver observer) {
        observers.add(observer);
    }

    /**
     * Deregisters EnterMembershipObservers from arraylist
     *
     * @param observer: The observer to be removed.
     */
    public void deregisterObserver(EnterMembershipObserver observer){observers.remove(observer);}

    //getter to obtain the arraylist of observers
    public ArrayList<EnterMembershipObserver> getObservers(){return this.observers;}


    // informs all Entermembership observers of the points linked to a valid membership number
    public void updatePoints(int points) {
        for (EnterMembershipObserver observer: observers){
              observer.updatePoints(points);
        }
    }

    /**
     * Informs all Entermembership observers of the status of the memberships database (if it was loaded
     * correctly or not)
     * @param status: True = membership database is corrupted, false = membership database ok
     */
    public void updateDataBaseStatus(boolean status){
        for (EnterMembershipObserver observer: observers){
            observer.isDataBaseCorrupt(status);
        }
    }



    // Fields

    // field to contain the observers of EnterMembership


    // field to hold the number of digits allowed in a membership number
    private int membershipLen;

    /**
     * field to act as the membership database, memberships will be stored in a hashmap: key = membership number
     * Value = ArrayList<String>, where the first element is the member's first name, second element is the member's
     * last name, third element is the points a member has - the string representing this must be capable of being
     * converted to an integer.
     *
     * Precondition: - membership number must be a valid length and a positive integer
     *               - 1st element in the arraylist associated with the membership number must be the member's first name,
     *                 the second element must be the member's last name, and the last element must be the points that
     *                 this member has (this must also be a string capable of being converted to a positive integer).
     *                - None of the inputs may contain commas
     *
     */
    private HashMap<Integer, ArrayList<String>> memberships;



    // variables to access the arraylist in memberships
    private int firstNameIndx = 0;
    private int lastNameIndx = 1;
    private int pointsIndx = 2;


    // field to hold the file name
    String fileName;


    // setters and getters for each of the fields (only accessible by the store)



    /**
     * Setter to update the memberships database with a new one - used only for testing
     * @param memberships: field to act as the membership database, memberships will be stored in a hashmap: key = membership number
     * Value = ArrayList<String>, where the first element is the member's first name, second element is the member's
     * last name, third element is the points a member has - the string representing this must be capable of being
     * converted to an integer.
     *
     * Precondition: - membership number must be a valid length and a positive integer
     *                 1st element in the arraylist associated with the membership number must be the member's first name,
     *                 the second element must be the member's last name, and the last element must be the points that
     *                 this member has (this must also be a string capable of being converted to a positive integer).
     *               - None of the inputs may contain commas
     *
     */
    public void updateMemberships(HashMap<Integer, ArrayList<String>> memberships){
        this.memberships=memberships;
        uploadMemberships();
    }


    /**
     * Getter to obtain the memberships database
     * @return: memberships database
     */
    public HashMap<Integer, ArrayList<String>> getMemberships(){
        return this.memberships;
    }


    /**
     * setter to change the valid membership length.
     *
     * Caution! The membership database will be wiped if this method is used
     *
     * Precondition: Membership length must be an integer >= 0
     * Poscondition: membershiplen is changed, membership database is wiped
     *
     * @param membershipLen: a positive integer for membership length
     * @return: 0 if success, -1 if membership length is <= 0
     */
    public int editMembershipLen(int membershipLen){
        if(membershipLen <= 0){
            return -1;
        }else {
            this.membershipLen = membershipLen;
            memberships.clear();
            uploadMemberships();
            return 0;
        }
    }

    /**
     * setter to change points for individual membership numbers
     * @param membershipNum: a valid membership number to which we will update the points on
     * @param points: an integer representing the points that a membership number will now have
     * @return: 0 if success, -1 if membershipNum is invalid, -2 if points are invalid, -3 is membership number is not yet
     *          in the memberships database
     *
     * Precondition: - membershipNum must be a valid length positive integer, and must be currently in the memberships database
     *               - points must be a positive integer
     *
     * postcondition: a member's points is updated with the new points amount
     */
    public int editIndivPoints(int membershipNum, Integer points){
        if((lenOfInt(membershipNum) != membershipLen) || (Integer.signum(membershipNum) == -1)){
            return -1;
        } else if (points < 0) {
            return -2;
        }else if(!isMember(membershipNum)){
            return -3;
        }else {
            this.memberships.get(membershipNum).set(pointsIndx, points.toString());
            uploadMemberships();
            return 0;
        }
    }


    /**
     * setter to add individual memberships
     * @param membershipNumber: a valid membership number for a member to be added to the memberships database
     * @param Fname: first name of the member
     * @param Lname: last name of the member
     * @param points: integer representing the number of points this member will have
     * @return: -1 if invalid membership number, -2 if failed due to the membership number already being used,
     *          -3 for negative points,  0 if success
     *
     *  precondition: - the membership number is a valid length, positive integer, and is unique (not yet in the database)
     *                - points must be a positive integer
     *                - None of the inputs may contain commas
     *
     * postcondition: a new membership is created and added to the database, as per the database rules
     */

    public int addMembership(int membershipNumber, String Fname, String Lname, Integer points){
        ArrayList<String> userData = new ArrayList<>();

        // check if membership number is unique
        if((lenOfInt(membershipNumber) != membershipLen) || (Integer.signum(membershipNumber) == -1)){
            return -1;
        }else if(isMember(membershipNumber)){
            return -2;
        }else if (points < 0) {
            return -3;
        }else {
            userData.add(firstNameIndx, Fname);
            userData.add(lastNameIndx, Lname);
            userData.add(pointsIndx, points.toString());
            this.memberships.put(membershipNumber, userData);
            uploadMemberships();
            return 0;
        }
    }

    /**
     * // setter to remove a membership
     * @param membershipNumber: a valid membership number of a member already in the memberships database
     * @return: 0 for success, -1 for invalid membership number, -2 for membership number not in the memberships database
     *
     * Precondition: membership number must be valid, and membership number must be presently in the memberships database
     */
    public int removeMembership(Integer membershipNumber){
        if((lenOfInt(membershipNumber) != membershipLen) || (Integer.signum(membershipNumber) == -1)){
            return -1;
        } else if (!isMember(membershipNumber)) {
            return -2;
        }else {
            this.memberships.remove(membershipNumber);
            uploadMemberships();
            return 0;
        }
    }

    // getters (only accessible by the store)

    /**
     * getter to obtain the number of points a user has
     * @param membershipNumber: a valid membership number of a member already in the memberships database
     * @return: upon success the number of points the user has (>=0), else: -1 if the membership number is invalid,
     * -2 if the membership number is not yet in the memberships database, -3 if the points was not an integer (shouldn't
     * ever happen if the preconditions for memberships database is followed).
     *
     * Precondition: membership number is valid and a positive integer, and in the membership database
     */
    public int getPoints(int membershipNumber){
        if((lenOfInt(membershipNumber) != membershipLen) || (Integer.signum(membershipNumber) == -1)){
            return -1;
        } else if (!isMember(membershipNumber)) {
            return -2;
        }else {
            try {
                return Integer.parseInt(this.memberships.get(membershipNumber).get(pointsIndx));
            }catch (Exception e){
                return -3;
            }
        }
    }



    /**
     * Getter to get a customer's first name
     * @param membershipNumber: a valid membership number of a member already in the memberships database
     * @return: upon success returns a string of the customers first name, else: "1" if invalid membership number, "2"
     * if the membership number is not yet in the memberships database.
     *
     *  Precondition: membership number is valid and a positive integer, and in the membership database
     */
    public String getFName(int membershipNumber){
        if((lenOfInt(membershipNumber) != membershipLen) || (Integer.signum(membershipNumber) == -1)){
            return "1";
        } else if (!isMember(membershipNumber)) {
            return "2";
        }else {
            return this.memberships.get(membershipNumber).get(firstNameIndx);
        }
    }


    /**
     * Getter to get a customer's last name
     * @param membershipNumber: a valid membership number of a member already in the memberships database
     * @return: upon success returns a string of the customers last name, else: "1" if invalid membership number, "2"
     * if the membership number is not yet in the memberships database.
     *
     *  Precondition: membership number is valid and a positive integer, and in the membership database
     */
    public String getLName(int membershipNumber){
        if((lenOfInt(membershipNumber) != membershipLen) || (Integer.signum(membershipNumber) == -1)){
            return "1";
        } else if (!isMember(membershipNumber)) {
            return "2";
        }else {
            return this.memberships.get(membershipNumber).get(lastNameIndx);
        }
    }

    /**
     * getter to obtain if the user is in the membership database, true if in the database, otherwise false
     * @param membershipNumber: a valid membership number of a member already in the memberships database
     * @return: true if membership number is in the database, else false
     *
     * Precondition: membership number is valid and a positive integer, and in the membership database
     */
    public Boolean isMember(int membershipNumber){return memberships.containsKey(membershipNumber);}


    // getter to get the current valid membership length
    public int getMembershipLen(){return this.membershipLen;};


    // File reading and writing for memberships

    /**
     * Function that loads the membership database from a file upon instantiation of this class
     */
    public void loadMemberships() {
        // Generate file object pointing to our file
        File fileObject = new File("./" + this.fileName);
        // check if the file is a file, exists, and we can read from it
        if (fileObject.isFile() && fileObject.exists() && fileObject.canRead()) {
            // Creating the fileReader and bufferedReader within the try round brackets to enable file closing via resources
            // also catches any exceptions that may occur
            try (FileReader fileReader = new FileReader(fileObject); BufferedReader bufferedReader = new BufferedReader(fileReader);) {
                // flag to control while loop
                boolean loopFlag = false;

                // first input is the valid membership length
                String input = bufferedReader.readLine();

                // validate membership length of the file matches what the system is set to, otherwise prevent loop execution
                try {
                    int memLen = Integer.parseInt(input);
                    if(memLen == this.membershipLen){
                        this.memberships = new HashMap<>();
                        loopFlag = true;
                    }

                    // catch exceptions to prevent program halting, and make the database empty
                }catch (Exception e){
                    this.memberships = new HashMap<>();
                }

                // prime the loop for the membership info
                input = bufferedReader.readLine();

                // loop to the end of the database file
                while ((input != null) && (loopFlag)){
                    // when split make sure there are enough entries in the array for the id, name, points etc
                    String[] memberInfo = input.split(",");

                    // make sure all the data is present (needs to have 4 fields, membership number, Fname, Lname, points)
                    if(memberInfo.length != 4){
                        loopFlag = false;
                        break;
                    }

                    int memberNo;
                    
                    try {
                        // get the membership number (index 0)
                        memberNo = Integer.parseInt(memberInfo[0]);
                    }catch (Exception e){
                        loopFlag = false;
                        break;
                    }
                    // put info into database, index 1 = FName, index 2 = LName, Index3 = points
                    memberships.put(memberNo,new ArrayList<String>(List.of(memberInfo[1], memberInfo[2], memberInfo[3])));

                    input = bufferedReader.readLine();
                }

                bufferedReader.close();

                // set flag based on loop flag
                if(loopFlag){
                    updateDataBaseStatus(false);
                }else {
                    updateDataBaseStatus(true);
                    // make database empty
                    this.memberships = new HashMap<>();
                }


                // if any exceptions occur during reading then set the flag to indicate the database is not loaded and make the database empty
            }catch (Exception e){
                updateDataBaseStatus(true);
                this.memberships = new HashMap<>();
            }

            // otherwise the file doesn't exist so we will just initialize the memberships database to an empty database
        } else {
            this.memberships = new HashMap<>();
            // set the flag to indicate the database is not loaded
            updateDataBaseStatus(true);
        }
    }


    /**
     * Function that uploads the membership database to a file
     */
    public void uploadMemberships() {
        boolean status;

        // try to wipe the old file, if there is an issue raise the database error flag and our internal flag
        try {
           File oldFile = new File(this.fileName);
           oldFile.delete();
           status = true;
       }catch (Exception e){
            updateDataBaseStatus(true);
            status = false;
       }

        // Generate file object pointing to our new fresh file
        File fileObject = new File(this.fileName);
        try {
            fileObject.createNewFile();
        } // This should never happen
        catch (IOException e) {
            status = false;
        }
        // This should always execute unless an IOException above occurs, which shouldn't happen
        if(status) {
            // create the print writer if conditions are met, and do so with resources so it can catch exceptions that
            // may occur and close the file if any are found
            try (PrintWriter writer = new PrintWriter(new FileWriter(fileObject));) {

                writer.println(getMembershipLen());
                // loop through all of the keys in the membership database
                for (int key:memberships.keySet()){
	                    writer.println("" + key + "," + memberships.get(key).get(0) + "," + memberships.get(key).get(1) + "," + memberships.get(key).get(2));
	                    writer.flush();
                }

                writer.close();

                // if any exceptions occur during reading then set the flag to indicate the database unable to be written to
            } catch (Exception e) {
                updateDataBaseStatus(true);
            }
        }
    }





    /**
     * constructor to initialize an Enter Membership object (only accessible by the self-checkout station control software)
     * @param membershipLen: integer defining how long a valid membership can be, it should be >0
     * @param fileName: name of the file to read and write from
     */
    public EnterMembership(SelfCheckoutStation station, int membershipLen, String fileName) {
        scs = station;
    	this.membershipLen = membershipLen;
        scs.cardReader.register(this);
        scs.mainScanner.register(this);
        scs.handheldScanner.register(this);
        this.fileName = fileName;
    }


    /**
     * Supplemental function that determines the number of digits an integer has
     * @param number: an integer
     * @return: the number of digits an integer number has
     */
    public int lenOfInt(int number){
    	if(number > 0) {
        return (int)(Math.log10(number)+1);
    	}
    	else {
    		 return (int)(Math.log10(-number)+1);
    	}
    }



    //Methods

    /**
     * Method that obtains the user's input for a membership number and checks to see if it is valid. Upon success the
     *
     *
     * @return: 0 if success, -1 if the user cancelled membership number input, -2 if invalid membership number, -3 if
     * the membership number is not in the memberships database. -4 (extremely rare error) if points were non integers
     */
    public int enterMembershipNumber(String membershipNumberStr){
             // trim membershipNumberStr to remove white spaces
            membershipNumberStr = membershipNumberStr.trim();
            int membershipNumber;
            // using try and catch for parseInt verify it is an integer then convert to an int
            try{
                membershipNumber = Integer.parseInt(membershipNumberStr);

                //otherwise check if the user entered cancel to stop
            }catch (NumberFormatException e){
                if(membershipNumberStr.equals("cancel")){
                    return -1;

                // otherwise the input was invalid as it contained characters
                }else {
                    return -2;
                }
            }

            // check if length is valid and the membership number is not negative.
            if((lenOfInt(membershipNumber) != membershipLen) || (Integer.signum(membershipNumber) == -1)){
               return -2;

            }else{
                // check to make sure the member exists and then inform the observer of the points.
                if(isMember(membershipNumber)){

                    // try to convert points to an int, if it fails return -4, but it shouldn't if the preconditions
                    // were met in the memberships database
                    try {
                        updatePoints(Integer.parseInt(memberships.get(membershipNumber).get(pointsIndx)));
                    }catch (Exception e){
                        return -4;
                    }
                    return 0;
                }
                // if the membership number isn't in the database return -3.
                else {
                    return -3;
                }
            }
    }
    // Card number variable used to populate number after member card events
    private String cardNumber = "";
    // Initialize to 1 since unused on any return calls for enter membership number
    private int validCode = 1;

    /*
     * Getter for card number
     */
    public String getCardNumber() {
    	return cardNumber;
    }

    /*
     * Return the code resulting from a membership verification to ensure was correct
     */
    public int getValidCode() {
    	return validCode;
    }
    
    /*
     * Get the barcode as a string from the scanned card
     */
    public String scanMembershipCard(Barcode barcode) {
    	return barcode.toString();
    }

    /*
     * Get the card number from the swipe data
     */
    public String swipeMembershipCard(CardData swipeData){
    	return swipeData.getNumber();
    }

	@Override
	public void reactToEnabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// Feature not used for enter membership functionality
		// Therefore, this method not executed as part of test coverage
		
	}

	@Override
	public void reactToDisabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// Feature not used for enter membership functionality
		// Therefore, this method not executed as part of test coverage
		
	}

	@Override
	public void reactToCardInsertedEvent(CardReader reader) {
		// Feature not used for enter membership functionality
		// Therefore, this method not executed as part of test coverage
		
	}

	@Override
	public void reactToCardRemovedEvent(CardReader reader) {
		// Feature not used for enter membership functionality
		// Therefore, this method not executed as part of test coverage
		
	}

	@Override
	public void reactToCardTappedEvent(CardReader reader) {
		// Feature not used for enter membership functionality
		// Therefore, this method not executed as part of test coverage
		
	}

	@Override
	public void reactToCardSwipedEvent(CardReader reader) {
		for (EnterMembershipObserver observer: observers){
            observer.swipeAttemptEvent();
      }
		
	}

	@Override
	public void reactToCardDataReadEvent(CardReader reader, CardData data) {
		if (data.getType() == "membership") {
			cardNumber = swipeMembershipCard(data);
			for (EnterMembershipObserver observer: observers){
	              observer.memberCardEvent(cardNumber);
	        }
			
			validCode = enterMembershipNumber(cardNumber);
		}
		
	}

	@Override
	public void reactToBarcodeScannedEvent(BarcodeScanner barcodeScanner, Barcode barcode) {
		cardNumber = scanMembershipCard(barcode);
		for (EnterMembershipObserver observer: observers){
			observer.memberCardEvent(cardNumber);
	    }
		validCode = enterMembershipNumber(cardNumber);
	} 


}