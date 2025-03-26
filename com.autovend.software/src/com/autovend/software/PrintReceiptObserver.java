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

import com.autovend.devices.SelfCheckoutStation;


/**
 * Observer for printReceipt functionality
 *
 */
public interface PrintReceiptObserver {
    /**
     * Announces that the Customer Session is complete.
     * 
     * @param station
     *            The device whose session is complete.
     */
    void sessionComplete(SelfCheckoutStation station);

    /**
     * Announces that the machine requires Maintenance.
     *
     * @param station
     *            The device who requires Maintenance.
     */
    void requiresMaintenance(SelfCheckoutStation station, String message);

    /**
     * Announces that a ReceiptPrinter has low paper
     *
     * @param station
     *              The station linked to the ReceiptPrinter that needs more paper
     */
    void lowPaper (SelfCheckoutStation station, String message);

    /**
     * Announces that a ReceiptPrinter has low ink
     *
     * @param station
     *              The station linked to the ReceiptPrinter that needs more ink
     */
    void lowInk (SelfCheckoutStation station, String message);
}
