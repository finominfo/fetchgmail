package hu.finominfo.addressmaker;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author Kovács Kálmán
 */
public class Address {

    private static final List<Pair<Address, Address>> senderReceiver = new ArrayList<Pair<Address, Address>>();
    
    private final String name;
    private final String city;
    private final String address;
    private final String zip;

    public Address(String name, String city, String address, String zip) {
        this.name = name;
        this.city = city;
        this.address = address;
        this.zip = zip;
    }
    
    public static boolean putSenderReceiver(Pair<Address, Address> pair) {
        return senderReceiver.add(pair);
    }

    public static List<Pair<Address, Address>> getSenderReceiver() {
        return senderReceiver;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    public String getZip() {
        return zip;
    }
    
    
}
