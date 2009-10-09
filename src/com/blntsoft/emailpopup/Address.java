package com.blntsoft.emailpopup;

import android.text.util.Rfc822Token;
import android.text.util.Rfc822Tokenizer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Bao-Long Nguyen-Trong (baolong@nguyentrong.com)
 */
public class Address {

    String mAddress;
    String mPersonal;

    public Address(String address, String personal) {
        this.mAddress = address;
        if ("".equals(personal)) {
            personal = null;
        }
        if (personal!=null) {
            personal = personal.trim();
        }
        this.mPersonal = personal;
    }
    
    public static Address[] parseUnencoded(String addressList) {
        List<Address> addresses = new ArrayList<Address>();
        if (addressList!=null
            && !"".equals(addressList)) {
            Rfc822Token[] tokens = Rfc822Tokenizer.tokenize(addressList);
            for (Rfc822Token token : tokens) {
                String address = token.getAddress();
                if (address!=null
                    && !"".equals(address)) {
                    addresses.add(new Address(token.getAddress(), token.getName()));
                }
            }
        }
        return addresses.toArray(new Address[0]);
    }

}//Address
