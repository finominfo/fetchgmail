package hu.finominfo.fetchgmail;

import com.itextpdf.text.DocumentException;
import hu.finominfo.invoicemaker.Globals;
import hu.finominfo.invoicemaker.Invoice;
import hu.finominfo.invoicemaker.InvoiceData;
import hu.finominfo.invoicemaker.InvoiceItemData;
import hu.finominfo.zip.ZipDir;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.CompletionHandler;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import org.apache.log4j.PropertyConfigurator;
import org.omg.CORBA.CompletionStatus;

public class GMailReader implements Consts {

    private static Date addDaysToDate(final Date date, int noOfDays) {
        Date newDate = new Date(date.getTime());

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(newDate);
        calendar.add(Calendar.DATE, noOfDays);
        newDate.setTime(calendar.getTime().getTime());

        return newDate;
    }

    private static int pos = 0;

    private static String getNextLineString(String str) {
        int pos2 = str.indexOf("\n", pos + 2);
        String retVal = str.substring(pos, pos2).trim();
        pos = pos2;
        return retVal;
    }

    private static boolean exists(String str, final String const1) {
        int pos1 = str.indexOf(const1, pos);
        if (pos1 == -1) {
            return false;
        }
        pos = pos1 + const1.length();
        return true;
    }

    private static String getString(String str, final String const1) {
        int pos1 = str.indexOf(const1, pos);
        if (pos1 == -1) {
            return "";
        }
        pos = str.indexOf("\n", pos1);
        int pos2 = str.indexOf("\n", pos + 2);
        return str.substring(pos, pos2).trim();
    }

    private static int getNumber(String str, final String const1, final String const2) {
        int pos1 = str.indexOf(const1, pos);
        if (pos1 == -1) {
            return 0;
        }
        pos = pos1 + const1.length();
        int pos2 = str.indexOf(const2, pos);
        String num = "0";
        for (int j = pos; j < pos2; j++) {
            if (str.charAt(j) >= '0' && str.charAt(j) <= '9') {
                num += str.charAt(j);
            }
        }
        pos = pos2;
        return Integer.valueOf(num);
    }

    private static String _myPath = null;

    public static String getPath() {
        return _myPath;
    }

    public static void main(String[] args) throws DocumentException, ClassNotFoundException, SQLException, InterruptedException {
        (new GMailReader()).execute();
    }

    public void execute() throws DocumentException, ClassNotFoundException, SQLException, InterruptedException {
        _myPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        String log4jPath = Globals.CONF_DIR.value()
                + System.getProperty("file.separator")
                + "log4j.properties";
        System.out.println("log4j path:" + log4jPath);
        PropertyConfigurator.configure(log4jPath);
        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        props.put("mail.imaps.ssl.trust", "*");
        try {
            final AtomicBoolean zipReady = new AtomicBoolean(false);
            ZipDir.makeSave(new CompletionHandler<Object, Object>() {

                @Override
                public void completed(Object result, Object attachment) {
                    zipReady.set(true);
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                }
            });

            System.out.println("Try to open gmail...");
            Session session = Session.getDefaultInstance(props, null);
            Store store = session.getStore("imaps");
            System.out.print("Gmail password:");
            String input = System.console().readLine();
            store.connect("imap.gmail.com", "kalman.kovacs", input);
//            System.out.println(store);

            Folder folder = store.getFolder("vatera/rendeles");
            folder.open(Folder.READ_ONLY);

            // 30 nappal korábbra, de legkorább az adott negyedév első napjára állítjuk
            Date lastdate = addDaysToDate(Calendar.getInstance().getTime(), -93);
            Date date = addDaysToDate(Calendar.getInstance().getTime(), -93);
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
            cal.set(Calendar.DAY_OF_MONTH, 1);
            int month = (cal.get(Calendar.MONTH) / 3) * 3;
            cal.set(Calendar.MONTH, month);
            Date start = cal.getTime();
            if (lastdate.before(start)) {
                lastdate = start;
                date = start;
            }

            Properties props2 = new Properties();
            Class.forName("org.relique.jdbc.csv.CsvDriver");
            props2.put("charset", "UTF-16");
            props2.put("fileExtension", ".csv");
            Connection conn = DriverManager.getConnection("jdbc:relique:csv:data", props2);
            Statement stmt = conn.createStatement();

            Message messages[] = folder.getMessages();
//            System.out.println("messages.length: " + messages.length);
            while (!zipReady.get()) {
                Thread.sleep(1000);
                System.out.println("ZIP not finished yet");
            }
            int mess = 200;
            for (int i = messages.length - mess; i < messages.length; i++) {
                int actual = i - messages.length + mess;
                if (actual % 50 == 0) {
                    System.out.println("Vizsgált üzenetek: " + (actual + 50));
                }
                pos = 0;
                Message message = messages[i];
                String mail;
                if ((message.getSubject().startsWith("Vatera") || message.getSubject().startsWith("TeszVesz"))
                        && message.getReceivedDate().after(date)) {
                    Object content = message.getContent();
                    if (content instanceof Multipart) {
                        Multipart mp = (Multipart) message.getContent();
                        mail = mp.getBodyPart(0).getContent().toString();
                    } else {
                        mail = (String) content;
                    }
                    String azonosito = getString(mail, VEVOAZONOSITO);
                    azonosito = azonosito.substring(0, azonosito.indexOf("[")).trim();

                    ResultSet results = stmt.executeQuery("SELECT * FROM partnerek WHERE AZON='" + azonosito + "'");
                    ResultSet rs = stmt.getResultSet();
                    boolean partnerLetezik = false;
                    boolean szamlaMehet = false;
                    boolean finominfo = false;
                    boolean rendszerkovacs = false;
                    InvoiceData data = new InvoiceData();
                    if (rs.next()) {
                        partnerLetezik = true;
                        if (rs.getString("iNev").length() > 2) {
                            szamlaMehet = true;
                            data.setCompany(rs.getString("iNev"));
                            data.setCity(rs.getString("iVaros"));
                            data.setAddress_1(rs.getString("iCim"));
                            data.setZip(rs.getString("iIR"));
                        }
                    }
                    results.close();
                    if (!partnerLetezik) {
                        data.setCompany(getString(mail, VEVONEVE));
                    }
                    String email = getString(mail, VEVOEMAIL);
                    email = email.substring(0, email.length() - 4);
                    data.setUser_email(email);
                    data.setPhone_1(getString(mail, VEVOTEL));

                    if (!partnerLetezik) {
                        if (exists(mail, SZALLITASI)) {
                            data.setCity(getString(mail, SZALLCIM));
                            getNextLineString(mail);
                            data.setAddress_1(getNextLineString(mail));
                            data.setZip(getNextLineString(mail));
                            data.setTaxNo("Tel: " + data.getPhone_1());
                        } else {
                            data.setAddress_1(email);
                        }
                    }

                    int itemNumber = 1;
                    int pos2 = pos;
                    String itemName = getString(mail, itemNumber + ". " + TERMEKNEV);
                    pos = pos2;
                    List<InvoiceItemData> items = new ArrayList<InvoiceItemData>();
                    String szamlaAdo = ELADO;
                    while (!itemName.isEmpty()) {
                        if (itemName.contains("toner")) {
                            rendszerkovacs = true;
                            szamlaAdo = "Rendszerkovács";
                            data.setInvoiceIssuer("Rendszerkovács Kft");
                        }
                        if (itemName.contains("Raspberry")) {
                            finominfo = true;
                            szamlaAdo = "Finominfo";
                            data.setInvoiceIssuer("Finominfo Bt");
                        }
                        InvoiceItemData item = new InvoiceItemData();
                        item.setOrder_item_sku(itemName);
                        item.setProduct_item_price((int) (getNumber(mail, EGYSEGAR, FT)));
                        item.setProduct_quantiy(getNumber(mail, DARAB, DB));
                        item.setProduct_final_price((int) (getNumber(mail, OSSZESEN, FT)));
                        items.add(item);
                        itemNumber++;
                        pos2 = pos;
                        itemName = getString(mail, itemNumber + ". " + TERMEKNEV);
                    }
                    pos = pos2;

                    int postageCost = (int) (getNumber(mail, SZALLDIJ, FT));
                    if (postageCost > 0) {
                        InvoiceItemData item = new InvoiceItemData();
                        item.setOrder_item_sku(SZALLITAS);
                        item.setProduct_item_price(postageCost);
                        item.setProduct_quantiy(1);
                        item.setProduct_final_price(postageCost);
                        items.add(item);
                    }

                    data.setOrderItems(items);

                    int vegosszeg = getNumber(mail, VEGOSSZEG, FT);

                    DateFormat formatter = new SimpleDateFormat(DATEFORMAT);
                    String receivedDate = formatter.format(message.getReceivedDate());

                    if (szamlaMehet) {
                        ResultSet results2 = stmt.executeQuery("SELECT * FROM szamlak WHERE AZON='" + azonosito + "' AND DATUM='" + receivedDate + "'");
                        ResultSet rs2 = stmt.getResultSet();
                        boolean szamlaLetezik = false;
                        if (rs2.next()) {
                            szamlaLetezik = true;
                        }
                        results2.close();

                        if (!szamlaLetezik) {
                            String invoiceNo = (new Invoice()).makeInvoice(data);
                            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                                    new FileOutputStream("./data/szamlak.csv", true), "UTF16"));
                            String itemName0 = data.getOrderItems().get(0).getOrder_item_sku().replace(',', ';');
                            bw.write("\n" + invoiceNo + "," + azonosito + "," + itemName0 + "," + receivedDate + "," + vegosszeg + "," + postageCost + "," + szamlaAdo);
                            bw.close();
                        }

                    } else {
                        if (!partnerLetezik) {
                            if (finominfo || rendszerkovacs) {
                                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                                        new FileOutputStream("./data/partnerek.csv", true), "UTF16"));
                                System.out.println(azonosito + ",,,,," + data.getCompany() + "," + data.getCity() + "," + data.getAddress_1() + "," + data.getZip() + "," + data.getPhone_1() + "," + data.getUser_email());
                                bw.write("\n" + azonosito + ",,,,," + data.getCompany() + "," + data.getCity() + "," + data.getAddress_1() + "," + data.getZip() + "," + data.getPhone_1() + "," + data.getUser_email());
                                bw.close();
                            }
                        }
                    }
                    ResultSet results2 = stmt.executeQuery("SELECT * FROM rendelesek WHERE AZON='" + azonosito + "' AND DATUM='" + receivedDate + "'");
                    ResultSet rs2 = stmt.getResultSet();
                    boolean rendelesLetezik = false;
                    if (rs2.next()) {
                        rendelesLetezik = true;
                    }
                    results2.close();

                    if (!rendelesLetezik) {
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                                new FileOutputStream("./data/rendelesek.csv", true), "UTF16"));
                        String itemName0 = data.getOrderItems().get(0).getOrder_item_sku().replace(',', ';');
                        bw.write("\n" + azonosito + "," + itemName0 + "," + receivedDate + "," + (vegosszeg - postageCost) + "," + postageCost + "," + szamlaAdo + ",NOK");
                        bw.close();
                    }

//                    System.out.println("******************************");
//                    System.out.println(message.getSubject());
//                    System.out.println("******************************");
//                    System.out.println(mail + "\n");
                }
            }

            ResultSet results3;
            results3 = stmt.executeQuery("SELECT sum(osszesen) FROM rendelesek");
            ResultSet rs2 = stmt.getResultSet();
            if (rs2.next()) {
                System.out.println("Összesen: " + rs2.getInt(1));
            }
            results3.close();
//            ResultSet results4 = stmt.executeQuery("SELECT sum(ktgszal) FROM rendelesek");
//            ResultSet rs3 = stmt.getResultSet();
//            if (rs3.next()) {
//                System.out.println("Ebből szállítás: " + rs2.getInt(1));
//            }
//            results4.close();
            results3 = stmt.executeQuery("SELECT count(*) FROM rendelesek");
            rs2 = stmt.getResultSet();
            if (rs2.next()) {
                System.out.println("Eladott darabszám: " + rs2.getInt(1));
            }
            results3.close();

            stmt.close();
            conn.close();

        } catch (NoSuchProviderException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        } catch (MessagingException e) {
            System.out.println(e.getMessage());
            System.exit(2);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }
}
