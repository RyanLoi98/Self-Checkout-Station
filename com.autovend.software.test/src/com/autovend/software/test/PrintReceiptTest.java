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

package com.autovend.software.test;
import com.autovend.Barcode;
import com.autovend.BarcodedUnit;
import com.autovend.Bill;
import com.autovend.Numeral;
import com.autovend.devices.*;
import com.autovend.products.BarcodedProduct;
import com.autovend.software.BillRecord;
import com.autovend.software.PrintReceiptController;
import com.autovend.software.SelfCheckoutStationLogic;
import org.junit.*;
import com.autovend.external.ProductDatabases;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import static org.junit.Assert.assertEquals;


// The test class for printReceipt() with a focus on low ink and low paper
public class PrintReceiptTest {
    // Variables for SelfCheckoutStation
    Currency currency;
    int[] denominations;
    BigDecimal[] coinDenominations;
    int scaleMaxWeight;
    int scaleSensitivity;
    SelfCheckoutStation scs;
    SelfCheckoutStationLogic scsLog;

    // Variables for PrintReceiptController functions
    PrintReceiptController demoPrintController;
    BillRecord bRecord;

    // Attendant and customer stubs
    AttendantStub attendantStub;
    CustomerStub customerStub;

    // Create product and barcode to be printed
    Barcode barcode = new Barcode(Numeral.eight,Numeral.one,Numeral.two,Numeral.three);
    BarcodedProduct product = new BarcodedProduct(barcode, "Milk", new BigDecimal("20"), 2.5);

    // ********************** Additional PrintReceipt Testing Variables ***********************
    SelfCheckoutStationLogic logic;
    BarcodedUnit unit = new BarcodedUnit(barcode, 2.5);
    Currency c1 = Currency.getInstance(Locale.CANADA);
    Bill bill20 = new Bill(20, c1);
    Bill bill10 = new Bill(10, c1);
    Bill bill50 = new Bill(50, c1);

    // Registers customerStub to PayObserver, PrintObserver, AddItemObserver
    // Registers attendantStub to PrintObserver
    private void registerStubs(SelfCheckoutStationLogic scsl) {
        scsl.registerPrintObserver(customerStub);
        scsl.registerPrintObserver(attendantStub);

    }

    @Before
    public void setup(){
        // Initialize the SelfCheckoutStation
        currency = Currency.getInstance(Locale.CANADA);
        denominations = new int[]{5, 10, 15, 20, 50};
        coinDenominations = new BigDecimal[]{BigDecimal.ONE};
        scaleMaxWeight = 20;
        scaleSensitivity = 1;
        scs = new SelfCheckoutStation(currency, denominations, coinDenominations, scaleMaxWeight, scaleSensitivity);

        // Install the SelfCheckoutStation logic
        scsLog = new SelfCheckoutStationLogic(scs, null, null, 0, null, 10, 10);
        
        try {
			scs.billDispensers.get(20).load(bill20,bill20,bill20);
			scs.billDispensers.get(10).load(bill10,bill10,bill10);
			

		} catch (SimulationException | OverloadException e) {}

        // Install the PrintReceiptController
        demoPrintController = new PrintReceiptController(scs, scsLog);

        // Format bill record for startPrint in PrintReceiptController
        bRecord = new BillRecord();

        // Instantiate the stubs
        attendantStub = new AttendantStub();
        customerStub = new CustomerStub();

        // Register the stubs
        demoPrintController.registerObserver(attendantStub);
        demoPrintController.registerObserver(customerStub);

        // Add test product to database
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product);

    }

    @After
    public void tearDown(){
        currency = null;
        denominations = null;
        coinDenominations = null;
        scaleMaxWeight = 0;
        scaleSensitivity = 0;
        scs = null;
        scsLog = null;
        demoPrintController = null;
        bRecord = null;
        attendantStub = null;
        customerStub = null;
    }

    @Test
    public void testWarningReceiptNotEnoughInk() throws OverloadException {
        // Add cost of milk ($20 due to inflation)
        BigDecimal cost = BigDecimal.valueOf(20);
        bRecord.addItemToRecord("Milk", cost);
        // Add paper
        scs.printer.addPaper(50);
        // Don't add ink
        demoPrintController.startPrint(bRecord, BigDecimal.valueOf(20.0), BigDecimal.valueOf(50.0));

        // Since there's no ink, we expect it to abort and signal low ink on demoPrintController
        Assert.assertTrue(demoPrintController.isLowInk());
        // Check if lowInk thrown. No ink would not be thrown since print() in ReceiptPrinter throws EmptyException
        Assert.assertTrue(demoPrintController.watchLowLevelsObserver.lowInk);

        // Attendant should be warned
        Assert.assertTrue(attendantStub.isLowInk());
        // Attendant should be notified that maintenance is required
        Assert.assertTrue(attendantStub.isMaintaince_required());
        // Without enough ink, we expect null to be receipt
        assertEquals(null, scs.printer.removeReceipt());
    }

    @Test
    public void testWarningReceiptJustEnoughInk() throws OverloadException {
        // Generate expected receipt output
        String receipt = "Milk 1 $20\n"
                + "Total: $20.0\n"
                + "Change: $30.0";
        // Add cost of milk ($20 due to inflation)
        BigDecimal cost = BigDecimal.valueOf(20);
        bRecord.addItemToRecord("Milk", cost);
        // Add paper
        scs.printer.addPaper(50);
        // Add ink
        scs.printer.addInk(31); // 31 characters in receipt not including spaces
        demoPrintController.startPrint(bRecord, BigDecimal.valueOf(20.0), BigDecimal.valueOf(30.0));
        // We expect warning and print receipt
        Assert.assertTrue(demoPrintController.isLowInk());
        Assert.assertTrue(demoPrintController.watchLowLevelsObserver.outOfInk);
        Assert.assertTrue(demoPrintController.watchLowLevelsObserver.lowInk);
        Assert.assertTrue(attendantStub.isLowInk());
        assertEquals(receipt, scs.printer.removeReceipt());
    }

    @Test
    public void testWarningReceiptSurplusInk() throws OverloadException {
        // Generate expected receipt output
        String receipt = "Milk 1 $20\n"
                + "Total: $20.0\n"
                + "Change: $30.0";
        // Add cost of milk ($20 due to inflation)
        BigDecimal cost = BigDecimal.valueOf(20);
        bRecord.addItemToRecord("Milk", cost);
        // Add paper
        scs.printer.addPaper(50);
        // Add ink in a loop to increment the minimumInk to 50
        for (int i = 0; i < 50; i++) {
            scs.printer.addInk(1);
        }
        demoPrintController.startPrint(bRecord, BigDecimal.valueOf(20.0), BigDecimal.valueOf(30.0));

        // We expect no warning
        Assert.assertFalse(demoPrintController.isLowInk());
        Assert.assertFalse(demoPrintController.watchLowLevelsObserver.outOfInk);
        Assert.assertFalse(demoPrintController.watchLowLevelsObserver.lowInk);
        Assert.assertFalse(attendantStub.isLowInk());
        // We expect receipt to be printed
        assertEquals(receipt, scs.printer.removeReceipt());
    }

    @Test
    public void testWarningReceiptNotEnoughPaper() throws OverloadException {
        // Add cost of milk ($20 due to inflation)
        BigDecimal cost = BigDecimal.valueOf(20);
        bRecord.addItemToRecord("Milk", cost);
        // Add ink
        scs.printer.addInk(50);
        // Don't add paper
        demoPrintController.startPrint(bRecord, BigDecimal.valueOf(20.0), BigDecimal.valueOf(50.0));

        // Since there's no paper, we expect it to abort and signal low paper on demoPrintController
        Assert.assertTrue(demoPrintController.isLowPaper());
        // Check if lowPaper thrown. No paper would not be thrown since print() in ReceiptPrinter throws EmptyException
        Assert.assertTrue(demoPrintController.watchLowLevelsObserver.lowPaper);

        // Attendant should be warned
        Assert.assertTrue(attendantStub.isLowPaper());
        // Attendant should be notified that maintenance is required
        Assert.assertTrue(attendantStub.isMaintaince_required());
        // Without enough paper, we expect null to be receipt
        assertEquals(null, scs.printer.removeReceipt());
    }

    @Test
    public void testWarningReceiptJustEnoughPaper() throws OverloadException {
        // Generate expected receipt output
        String receipt = "Milk 1 $20\n"
                + "Total: $20.0\n"
                + "Change: $30.0";
        // Add cost of milk ($20 due to inflation)
        BigDecimal cost = BigDecimal.valueOf(20);
        bRecord.addItemToRecord("Milk", cost);
        // Add paper
        scs.printer.addPaper(2); // Two '\n' so need two lines of paper
        // Add ink
        scs.printer.addInk(50); // 31 characters in receipt not including spaces
        demoPrintController.startPrint(bRecord, BigDecimal.valueOf(20.0), BigDecimal.valueOf(30.0));
        // We expect warning and print receipt
        Assert.assertTrue(demoPrintController.isLowPaper());
        Assert.assertTrue(demoPrintController.watchLowLevelsObserver.outOfPaper);
        Assert.assertTrue(demoPrintController.watchLowLevelsObserver.lowPaper);
        Assert.assertTrue(attendantStub.isLowPaper());
        assertEquals(receipt, scs.printer.removeReceipt());
    }

    @Test
    public void testWarningReceiptSurplusPaper() throws OverloadException {
        // Generate expected receipt output
        String receipt = "Milk 1 $20\n"
                + "Total: $20.0\n"
                + "Change: $30.0";
        // Add cost of milk ($20 due to inflation)
        BigDecimal cost = BigDecimal.valueOf(20);
        bRecord.addItemToRecord("Milk", cost);
        // Add ink
        scs.printer.addInk(50);
        // Add paper in a loop to increment the minimumPaper to 50
        for (int i = 0; i < 50; i++) {
            scs.printer.addPaper(1);
        }
        demoPrintController.startPrint(bRecord, BigDecimal.valueOf(20.0), BigDecimal.valueOf(30.0));

        // We expect no warning
        Assert.assertFalse(demoPrintController.isLowPaper());
        Assert.assertFalse(demoPrintController.watchLowLevelsObserver.outOfPaper);
        Assert.assertFalse(demoPrintController.watchLowLevelsObserver.lowPaper);
        Assert.assertFalse(attendantStub.isLowPaper());
        // We expect receipt to be printed
        assertEquals(receipt, scs.printer.removeReceipt());
    }

    // Check that the customer is notified that their session is complete upon printing receipt
    @Test
    public void printReceipt_notifyCustomerSessionComplete_true() throws DisabledException, OverloadException {
//        logic = new SelfCheckoutStationLogic(scs);
        // scsLog
        registerStubs(scsLog);
        scs.printer.addInk(50);
        scs.printer.addPaper(50);
        boolean scanned = scs.mainScanner.scan(unit);
        if (scanned) { //if item was scanned successfully
            scs.baggingArea.add(unit);
            scsLog.CustomerPay();
            scs.billInput.accept(bill20);
            if (scs.billInput.hasSpace()) {
                assertEquals(customerStub.isSessionOver(), true);
			}
        }
    }
    // Check that a valid receipt is printed following payment
    @Test
    public void printReceipt_checkValidReceipt_true() throws DisabledException, OverloadException {
//        logic = new SelfCheckoutStationLogic(scs);
        registerStubs(scsLog);
        String receipt = "Milk 1 $20\n"
                + "Total: $20.0\n"
                + "Change: $30.0";
        scs.printer.addInk(50);
        scs.printer.addPaper(50);
        boolean scanned = scs.mainScanner.scan(unit);
		while(!scanned) {
			scanned = scs.mainScanner.scan(unit);
		}
        if (scanned) { //if item was scanned successfully
            scs.baggingArea.add(unit);
            scsLog.CustomerPay();
            scs.billInput.accept(bill50);
            if (scs.billInput.hasSpace()) {
				double change = 0;
				while(!scs.billOutput.hasSpace()) {
					change += scs.billOutput.removeDanglingBill().getValue();
				}
                assertEquals(receipt, scs.printer.removeReceipt());
                assertEquals(30, change,0.0);
			}
        }
    }
    // Check that maintenance is called for when a receipt tries to print with no ink or paper
    @Test
    public void printReceipt_noInknoPaperatStart_MaintanceNeeded_true() throws DisabledException, OverloadException {
//        logic = new SelfCheckoutStationLogic(scs);
        registerStubs(scsLog);
        boolean scanned = scs.mainScanner.scan(unit);
        while (scanned != true) {	//Until it scans
            scanned = scs.mainScanner.scan(unit);
        }
        scs.baggingArea.add(unit);
        scsLog.CustomerPay();
        scs.billInput.accept(bill20);
        assertEquals(true, attendantStub.isMaintaince_required());

    }
    // Check that maintenance is called for when a receipt tries to print with low paper
    @Test
    public void printReceipt_PaperRunsOut_MaintanceNeeded_true() throws DisabledException, OverloadException {
//        logic = new SelfCheckoutStationLogic(scs);
        registerStubs(scsLog);
        scs.printer.addInk(20);
        scs.printer.addPaper(2);
        boolean scanned = scs.mainScanner.scan(unit);
        while (scanned != true) {	//Until it scans
            scanned = scs.mainScanner.scan(unit);
        }
        scs.baggingArea.add(unit);
        scsLog.CustomerPay();
        scs.billInput.accept(bill20);
        assertEquals(true,attendantStub.isMaintaince_required());
    }
    // Check that maintenance is called for when a receipt tries to print with low ink
    @Test
    public void printReceipt_InkRunsOut_MaintanceNeeded_true() throws DisabledException, OverloadException {
//        logic = new SelfCheckoutStationLogic(scs);
        registerStubs(scsLog);
        scs.printer.addInk(2);
        scs.printer.addPaper(20);
        boolean scanned = scs.mainScanner.scan(unit);
        while (scanned != true) {	//Until it scans
            scanned = scs.mainScanner.scan(unit);
        }
        scs.baggingArea.add(unit);
        scsLog.CustomerPay();
        scs.billInput.accept(bill20);
        assertEquals(true, attendantStub.isMaintaince_required());
    }
    // Checks that receipt prints and maintenance is not required
    @Test
    public void printReceipt_checkAttendantCall_false() throws OverloadException{
//        logic = new SelfCheckoutStationLogic(scs);
        registerStubs(scsLog);
        scs.printer.addInk(50);
        scs.printer.addPaper(50);
        boolean scanned = scs.mainScanner.scan(unit);
        while (scanned != true) {	//Until it scans
            scanned = scs.mainScanner.scan(unit);
        }
        scs.baggingArea.add(unit);
        scsLog.CustomerPay();
        scs.billInput.accept(bill20);
        assertEquals(false, attendantStub.isMaintaince_required());
    }
}
