/**
 SENG300 Iteration 3 Group 1 Members
 Prempreet Brar (30112576)
 Justin Yee (30113485)
 Saad Abdullah (30142511)
 Jinze Xu (10106091)
 George Vassilev (30123526)
 Carter Tam (30146065)
 Filip Cotra (30086750)
 Nicole Maurer (30077223)
 Vita Vysochina (30118374)
 Alex Tran (30075197)
 Ryan Loi (30019520)
 Aaron St. Omer (30144511)
 Fares Senjar (30113420)
 Arian Safari (30161346)
 Taylor Diegel (30110147)
 Jamie MacDonald (10174719)
 Darren Roszell (30163669)
 Mankaranpreet Pandher (30154733)
 Ba Khanh Tung Nguyen (30116961)
 Sornali Banik (30143087)
 Adrian Llonor (30018780)
 Farbod Moghaddam (30115199)
 Namit Aneja (30146188)
 Ramin Kahidi (30110218)
 Abdullah Zubair (30113730)
 **/

package com.autovend.software;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Set;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.AbstractDevice;
import com.autovend.devices.EmptyException;
import com.autovend.devices.OverloadException;
import com.autovend.devices.SimulationException;
import com.autovend.devices.observers.AbstractDeviceObserver;
import com.autovend.devices.observers.ReceiptPrinterObserver;
import com.autovend.devices.ReceiptPrinter;


/**
 * PrintReceiptController for printReceipt functionality
 *
 * Modified to monitor low ink and paper
 * @author Adrian Llonor
 */
public class PrintReceiptController implements ReceiptPrinterObserver{
	private BillRecord billRecord;
	private BigDecimal total;
	private BigDecimal change;
	boolean outOfPaperFlag = false;
	boolean outOfInkFlag = false;

	boolean paperAdd = false;
	boolean inkAdd = false;

	boolean lowPaper = false;
	boolean lowInk = false;


	protected ArrayList<PrintReceiptObserver> observers = new ArrayList<>();

	private ReceiptPrinter rPrinter;
	public InkAndPaperObserver watchLowLevelsObserver;


	public SelfCheckoutStation station;

	private boolean abort = false;
	
	private SelfCheckoutStationLogic scslogic;

	public PrintReceiptController (SelfCheckoutStation scs, SelfCheckoutStationLogic logic){
		station = scs;
		scslogic = logic;
		rPrinter = scs.printer;

		// Create an observer that monitors the minimum amount of ink and paper possibly added to throw a warning
		// to the attendant
		watchLowLevelsObserver = new InkAndPaperObserver();
		rPrinter.register(watchLowLevelsObserver);
		rPrinter.enable();
	}

		// The purpose of this observer is to track the minimum amount of ink and paper that are added to the
		// ReceiptPrinter. Every time addPaper() or addInk() is called, we know that a minimum of one unit of either
		// paper or ink is added. This observer operates with the assumption that the software is installed on the
		// hardware prior to any paper or ink being called.
		public class InkAndPaperObserver implements ReceiptPrinterObserver{
			// Flags to monitor low ink and paper as well as out of ink and paper
			public boolean outOfInk;
			public boolean outOfPaper;
			public boolean lowInk;
			public boolean lowPaper;
			// Int to track the minimum amount of ink and paper added
			public int minimumInk;
			public int minimumPaper;

			InkAndPaperObserver(){
				outOfInk = false;
				outOfPaper = false;
				lowInk = false;
				lowPaper = false;
				minimumInk = 0;
				minimumPaper = 0;
			}

			@Override
			public void reactToEnabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {
			}

			@Override
			public void reactToDisabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {
			}

			// If out of paper, throw a warning
			@Override
			public void reactToOutOfPaperEvent(ReceiptPrinter printer) {
				this.outOfPaper = true;
				this.lowPaper = true;
			}

			// If out of ink, throw a warning
			@Override
			public void reactToOutOfInkEvent(ReceiptPrinter printer) {
				this.outOfInk = true;
				this.lowInk = true;
			}

			// At least one unit of paper must be added when addPaper() in ReceiptPrinter.java is called
			// So increment by one each time it's called then monitor these levels when printing
			@Override
			public void reactToPaperAddedEvent(ReceiptPrinter printer) {
				this.minimumPaper++;
				this.outOfPaper = false;
			}

			// At least one unit of ink must be added when addPaper() in ReceiptPrinter.java is called
			// So increment by one each time it's called then monitor these levels when printing
			@Override
			public void reactToInkAddedEvent(ReceiptPrinter printer) {
				this.minimumInk++;
				this.outOfPaper = false;
			}
		}
	
	/**
	 * Initiates the printing sequence, called by PayByBill once full payment
	 * received
	 * 
	 * @param items:  Maps item names to their prices
	 * @param total:  total of order
	 * @param change: change dispensed to customer
	 * @throws EmptyException 
	 */
	public void startPrint(BillRecord bRecord, BigDecimal customerTotal, BigDecimal customerChange) {
		billRecord = bRecord;
		total = customerTotal;
		change = customerChange;

		bRecord.addPaymentDetails(total, customerChange);
		printRecord();
	}

	/**
	 * Prints Bill Record. Prior and after printing, it checks if there is sufficient ink and paper for the next print.
	 *
	 *
	 * @throws EmptyException : if printing is aborted
	 */
	private void printRecord(){
		String printString = formatRecord();

		if (printString.length() >= watchLowLevelsObserver.minimumInk){
			// Since the length is greater, set minimum ink to zero to be empty
			watchLowLevelsObserver.minimumInk = 0;
			watchLowLevelsObserver.lowInk =  true;
			for (PrintReceiptObserver observer: observers){
				observer.lowInk(station, "Warning! Possibly low on ink.");
			}
			lowInk = true;
		} else {
			watchLowLevelsObserver.minimumInk -= printString.length();
		}

		int linesOfPaperNeeded = 1;
		for (char letter : printString.toCharArray())
			if (letter == '\n')
				linesOfPaperNeeded++;
		if (linesOfPaperNeeded >= watchLowLevelsObserver.minimumPaper){
			// Since the lines of paper needed is greater, set minimum paper to zero
			watchLowLevelsObserver.minimumPaper = 0;
			watchLowLevelsObserver.lowPaper =  true;
			for (PrintReceiptObserver observer: observers){
				observer.lowPaper(station, "Warning! Possibly low on paper");
			}
			lowPaper = true;
		} else {
			watchLowLevelsObserver.minimumPaper -= linesOfPaperNeeded;
		}


		if (!abort){
			for (char letter : printString.toCharArray()) {
				try {
					rPrinter.print(letter);
				} catch (OverloadException | EmptyException e) {
					for(PrintReceiptObserver observer : observers)
						observer.requiresMaintenance(station, e.getMessage());
					abort = true;
					break;
				}

				if(outOfPaperFlag == true) {
					for(PrintReceiptObserver observer : observers)
						observer.requiresMaintenance(station, "The printer is out of paper!");
					abort = true;
					break;
				}
				if(outOfInkFlag == true) {
					for(PrintReceiptObserver observer : observers)
						observer.requiresMaintenance(station, "The printer is out of Ink!");
					abort = true;
					break;
				}

			}
		}
		if (!abort) {
			rPrinter.cutPaper();
			for(PrintReceiptObserver observer : observers)
                observer.sessionComplete(station);

			scslogic.sessionEnd();
		}
	}

	/**
	 * Formats the bill record to be printed.
	 * @return
	 */
	private String formatRecord() {
		String printString = "";
		Set<String> itemSet = billRecord.getItems();
		for(String name : itemSet) {
			Integer quantity = billRecord.getItemQuantity(name);
			BigDecimal cost = billRecord.getItemCost(name);
			
			printString += name + " " + quantity + " 	$" + cost +  "\n";
		}
		
		printString += "Total: $" + total + "\n";
		printString += "Change: $" + change;
		
		return printString;
	}

	// Getter for low ink boolean
	public boolean isLowInk(){return lowInk;}
	// Getter for low paper boolean
	public boolean isLowPaper(){return lowPaper;}



	/**
	 * Registers printReceiptObserver to receive event notifications from PrintReceipt logic.
	 * Used by Customer I/O to communicate with Logic.
	 * 
	 * @param observer: The observer to be added.
	 */
	public void registerObserver(PrintReceiptObserver observer) {
		observers.add(observer);
	}

		@Override
		public void reactToOutOfPaperEvent(ReceiptPrinter printer) {
			outOfPaperFlag = true;
			lowPaper = true;
		}


		@Override
		public void reactToOutOfInkEvent(ReceiptPrinter printer) {
			outOfInkFlag = true;
			lowInk = true;
		}
		@Override
		public void reactToPaperAddedEvent(ReceiptPrinter printer) {
			outOfPaperFlag = false;
		}
		@Override
		public void reactToInkAddedEvent(ReceiptPrinter printer) {
			outOfInkFlag = false;
		}
		
		@Override
		public void reactToEnabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {}
		@Override
		public void reactToDisabledEvent(AbstractDevice<? extends AbstractDeviceObserver> device) {}
}