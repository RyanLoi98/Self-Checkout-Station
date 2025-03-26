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
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import com.autovend.Barcode;
import com.autovend.Numeral;
import com.autovend.PriceLookUpCode;
import com.autovend.PriceLookUpCodedUnit;
import com.autovend.devices.OverloadException;
import com.autovend.devices.ReusableBagDispenser;
import com.autovend.devices.SelfCheckoutStation;
import com.autovend.devices.SupervisionStation;
import com.autovend.external.CardIssuer;
import com.autovend.external.ProductDatabases;
import com.autovend.products.BarcodedProduct;
import com.autovend.products.PLUCodedProduct;
import com.autovend.products.Product;

import GUIComponents.AttendantIO;

public class AttendantCommunicationsController implements AdjustChangeListener {
//	private static AttendantCommunicationsController attendantCommunicationsController = new AttendantCommunicationsController();
	private SupervisionStation supervisionStation;
	private Map<SelfCheckoutStation, SelfCheckoutStationLogic> stationLogicMap;
	ArrayList<SelfCheckoutStation> stationList;
	ArrayList<SelfCheckoutStationLogic> stationLogicList;
	Boolean no_bag_flag = false;

	public AttendantCommunicationsController() {
		// Should delete the following initialization of database in the final project

		supervisionStation = new SupervisionStation();
		stationLogicMap = new HashMap<>();
		stationList = new ArrayList<>();
		stationLogicList = new ArrayList<>();
		Barcode barcode = new Barcode(Numeral.eight, Numeral.one, Numeral.two, Numeral.three);
		BarcodedProduct product = new BarcodedProduct(barcode, "Milk", new BigDecimal("20"), 2.5);
		Barcode barcode2 = new Barcode(Numeral.five, Numeral.one, Numeral.two, Numeral.three);
		BarcodedProduct product2 = new BarcodedProduct(barcode2, "Apple", new BigDecimal("5"), 2.5);
		Barcode barcode3 = new Barcode(Numeral.nine, Numeral.one, Numeral.two, Numeral.three);
		BarcodedProduct product3 = new BarcodedProduct(barcode3, "Grape", new BigDecimal("20"), 3);
		PriceLookUpCode PLUCode1 = new PriceLookUpCode(Numeral.one, Numeral.two, Numeral.three, Numeral.four);
		PLUCodedProduct product4 = new PLUCodedProduct(PLUCode1, "Banana", new BigDecimal("20"));
		PriceLookUpCode PLUCode2 = new PriceLookUpCode(Numeral.one, Numeral.two, Numeral.three, Numeral.five);
		PLUCodedProduct product5 = new PLUCodedProduct(PLUCode2, "Bread", new BigDecimal("20"));
		PriceLookUpCodedUnit pLUCode1Unit = new PriceLookUpCodedUnit(PLUCode1, 10);
		PriceLookUpCodedUnit pLUCode2Unit = new PriceLookUpCodedUnit(PLUCode2, 20);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode2, product2);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode3, product3);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(PLUCode1, product4);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(PLUCode2, product5);
	}

	public SupervisionStation getSupervisionStation() {
		return supervisionStation;
	}

	public Set<Product> searchItemByText(String keyword, Map<Barcode, BarcodedProduct> barcodedProductDatabase,
			Map<PriceLookUpCode, PLUCodedProduct> plucodedProcutDatabase) {
		Set<Product> results = new HashSet<>();
		Map<String, Product> uniqueProducts = new HashMap<>();

		if (barcodedProductDatabase != null) {
			for (BarcodedProduct product : barcodedProductDatabase.values()) {
				if (product.getBarcode().toString().toLowerCase().contains(keyword.toLowerCase())
						|| product.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
					String barcodeValue = product.getBarcode().toString().toLowerCase();
					if (!uniqueProducts.containsKey(barcodeValue)) {
						uniqueProducts.put(barcodeValue, product);
					}
				}
			}
		}

		if (plucodedProcutDatabase != null) {
			for (PLUCodedProduct product : plucodedProcutDatabase.values()) {
				if (product.getPLUCode().toString().toLowerCase().contains(keyword.toLowerCase())
						|| product.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
					String pluCode = product.getPLUCode().toString().toLowerCase();
					if (!uniqueProducts.containsKey(pluCode)) {
						uniqueProducts.put(pluCode, product);
					}
				}
			}
		}

		results.addAll(uniqueProducts.values());
		return results;
	}

	public void addItemByText(ArrayList<Product> addedProducts, SelfCheckoutStationLogic logic)
			throws OverloadException {
		for (Product addedProduct : addedProducts) {
			if (addedProduct instanceof BarcodedProduct) {
				logic.getAddItemController().startAdding((BarcodedProduct) addedProduct);
			}
			if (addedProduct instanceof PLUCodedProduct) {
				logic.getAddItemController().startAdding(addedProduct);
			}
		}
	}

	public void removeItems(ArrayList<String> removedProducts, AddItemController addItemController)
			throws OverloadException {
		try {
			for (String removedProduct : removedProducts) {
				addItemController.remove(removedProduct);
			}
		} catch (Exception e) {
		}

	}

	public void registerAttendantToLogic(AttendantIO aIO) {
		for (SelfCheckoutStationLogic scsl : stationLogicList) {
			scsl.registerPayCashObserver(aIO);
			scsl.registerPrintObserver(aIO);
			scsl.registerAddItemObserver(aIO);
		}
	}

	public Map<SelfCheckoutStation, SelfCheckoutStationLogic> initializeSelfCheckoutStationLogics(int numberOfStation,
			Currency currency, int[] billDenominations, BigDecimal[] coinDenominations, int scaleMaximumWeight,
			int scaleSensitivity, CardIssuer CreditBank, CardIssuer DebitBank, int membershipLen, String fileName
			) {
		for (int i = 0; i < numberOfStation; i++) {
			SelfCheckoutStation scs = new SelfCheckoutStation(currency, billDenominations, coinDenominations,
					scaleMaximumWeight, scaleSensitivity);

			supervisionStation.add(scs);
			stationList.add(scs);

			stationLogicMap.put(scs, new SelfCheckoutStationLogic(scs, CreditBank, DebitBank, membershipLen, fileName,
					0, 0));
			stationLogicList.add(stationLogicMap.get(scs));
			/**
			 * try { for (int j = 0; j < 20; j++) { for (int bd : billDenominations) {
			 * scs.billDispensers.get(bd).load(new Bill(bd, currency)); } for (BigDecimal cd
			 * : coinDenominations) { scs.coinDispensers.get(cd).load(new Coin(cd,
			 * currency)); } } } catch (Exception e) { }
			 */
		}
		return stationLogicMap;
	}

	public ArrayList<SelfCheckoutStationLogic> getStationLogicList() {
		return (ArrayList<SelfCheckoutStationLogic>) stationLogicList;
	}

	public ArrayList<SelfCheckoutStation> getStationList() {
		return stationList;
	}

	public int getNumberOfSCS() {
		return stationList.size();
	}

	public int findStation(SelfCheckoutStationLogic scsl) {
		for (int i = 0; i < stationLogicList.size(); i++) {
			if (stationLogicList.get(i) == scsl) {
				return i + 1;
			}
		}
		return 0;
	}

	public int findStation(SelfCheckoutStation scs) {
		for (SelfCheckoutStation selfCheckoutStation : stationLogicMap.keySet()) {
			if (selfCheckoutStation == scs) {
				return findStation(stationLogicMap.get(selfCheckoutStation));
			}
		}
		return 0;
	}

	public void enableStation(SelfCheckoutStation station) {
		if (stationLogicMap.containsKey(station)) {
			stationLogicMap.get(station).startUpStation();
			if (stationLogicMap.get(station).isDisabled()) {
				JOptionPane.showMessageDialog(supervisionStation.screen.getFrame(),
						"Station disabled due to low change", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void disableStation(SelfCheckoutStation station) {
		if (stationLogicMap.containsKey(station)) {
			stationLogicMap.get(station).shutDownStation();
		}
	}

	public void preventStationUse(SelfCheckoutStation station) {
		if (stationLogicMap.containsKey(station)) {
			stationLogicMap.get(station).preventStationUse();
		}
	}
	public void solveWeightDiscrep(SelfCheckoutStation station) throws OverloadException {
		if (stationLogicMap.containsKey(station)) {
			stationLogicMap.get(station).weightDiscprepancyApproval();;
		}
	}

	public void permitStationUse(SelfCheckoutStation station) {
		if (stationLogicMap.containsKey(station)) {
			stationLogicMap.get(station).premitStationUse();
		}
	}

	public void approveNoBags(SelfCheckoutStation station) {
		System.out.println("called");
		if (stationLogicMap.containsKey(station)) {
			stationLogicMap.get(station).noBaggingRequestApproved();
			no_bag_flag = true;
		}
	}

	public void approveOwnBags(SelfCheckoutStation station) {
		System.out.println("called");
		if (stationLogicMap.containsKey(station)) {
			stationLogicMap.get(station).CustomerAddOwnBagsApproved();
			;
			no_bag_flag = true;
		}
	}

// for testing
	public boolean isApproved() {
		return no_bag_flag;
	}

	@Override
	public void reactToLowBanknotes(SelfCheckoutStation scs) {
		JOptionPane.showMessageDialog(supervisionStation.screen.getFrame(),
				"Low Banknotes Detected at station " + stationList.indexOf(scs), "Warning", JOptionPane.WARNING_MESSAGE);
	}

	@Override
	public void reactToLowCoins(SelfCheckoutStation scs) {
		JOptionPane.showMessageDialog(supervisionStation.screen.getFrame(),
				"Low Coins Detected at station " + stationList.indexOf(scs), "Warning", JOptionPane.WARNING_MESSAGE);
	}

	@Override
	public void reactToEmptyBanknotes(SelfCheckoutStation scs) {
		JOptionPane.showMessageDialog(supervisionStation.screen.getFrame(),
				"Empty Banknotes Detected at station " + stationList.indexOf(scs), "Warning", JOptionPane.WARNING_MESSAGE);
	}

	@Override
	public void reactToEmptyCoins(SelfCheckoutStation scs) {
		JOptionPane.showMessageDialog(supervisionStation.screen.getFrame(),
				"Empty Coins Detected at station " + stationList.indexOf(scs), "Warning", JOptionPane.WARNING_MESSAGE);
	}
}
