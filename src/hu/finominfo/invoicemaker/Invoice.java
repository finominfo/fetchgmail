package hu.finominfo.invoicemaker;

import static com.itextpdf.text.Font.BOLD;
import static com.itextpdf.text.Font.NORMAL;
import static hu.finominfo.invoicemaker.Globals.CONF_DIR;
import static hu.finominfo.invoicemaker.Globals.SEPARATOR;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 *
 * @author Kovács Kálmán
 */
public class Invoice {

    private static Integer invoiceNumber = null;
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-");
    private static final SimpleDateFormat formatterDaily = new SimpleDateFormat("yyyy-MM-dd");
    private static final String PATH_OF_INVOICE_finominfo = "." + SEPARATOR + "iFinominfo" + SEPARATOR;
    private static final String PATH_OF_INVOICE_rendszerkovacs = "." + SEPARATOR + "iRendszerkovacs" + SEPARATOR;
    private static final byte LENGTH_OF_INVOICE_NUMBER = 4;
    private static final byte DEADLINE_OF_PAYING = 8;

    public String makeInvoice(InvoiceData od) throws DocumentException, IOException {

        // Legelőszőr beállítjuk az invoiceNumber helyes értékét
        if (invoiceNumber == null) {
            invoiceNumber = 0;
            File directory;
            if (od.getInvoiceIssuer().startsWith("Rendszer")) {
                directory = new File(PATH_OF_INVOICE_rendszerkovacs);
            } else {
                directory = new File(PATH_OF_INVOICE_finominfo);
            }
            String[] myFiles;
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File directory, String fileName) {
                    return fileName.endsWith(".pdf");
                }
            };
            myFiles = directory.list(filter);
            if (myFiles == null) {
                directory.mkdirs();
            } else {
                for (String file : myFiles) {
                    int i = Integer.valueOf(file.substring(5, 5 + LENGTH_OF_INVOICE_NUMBER));
                    if (invoiceNumber < i) {
                        invoiceNumber = i;
                    }
                }
            }
        }

        invoiceNumber++;
        String invoiceNo = invoiceNumber.toString();
        while (invoiceNo.length() < LENGTH_OF_INVOICE_NUMBER) {
            invoiceNo = "0" + invoiceNo;
        }
        invoiceNo = formatter.format(new Date()) + invoiceNo;
        String today = formatterDaily.format(new Date());
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DATE, DEADLINE_OF_PAYING);
        String todayPlusEightDay = formatterDaily.format(calendar.getTime());
        String filename = PATH_OF_INVOICE_finominfo + invoiceNo + ".pdf";
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(filename));
        document.open();

        String fontPlace = CONF_DIR.toString() + SEPARATOR + "arial.ttf";
        BaseFont unicode = BaseFont.createFont(fontPlace, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        //        BaseFont helvetica = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED);
        //        Font font = new Font(helvetica, 12, Font.NORMAL);
        Font fej1Bal = new Font(unicode, 17, BOLD, new BaseColor(new Color(0, 0, 128)));
        Font fej1Jobb = new Font(unicode, 12, BOLD, new BaseColor(new Color(128, 0, 0)));
        Font osszesen = new Font(unicode, 12, BOLD, new BaseColor(new Color(0, 0, 0)));
        Font fej2 = new Font(unicode, 9, NORMAL, new BaseColor(new Color(0, 0, 0)));
        Font partnerek = new Font(unicode, 9, BOLD, new BaseColor(new Color(0, 0, 0)));
        Font torzsVastag = new Font(unicode, 7, BOLD, new BaseColor(new Color(0, 64, 0)));
        Font torzs1 = new Font(unicode, 7, NORMAL, new BaseColor(new Color(0, 0, 0)));
        //        Font font = FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLD, new BaseColor(new Color(0, 0, 128)));
        //        Font font2 = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD, new BaseColor(new Color(128, 0, 0)));
        //        Font font3 = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.BOLD, new BaseColor(new Color(0, 0, 0)));

        for (int masolatok = 0; masolatok < 3; masolatok++) {

            if (masolatok > 0) {
                document.newPage();
            }
            // *****************************************************************************        
            PdfPTable fejlec1 = new PdfPTable(2);

            fejlec1.setWidths(new float[]{2f, 1f});
            fejlec1.setWidthPercentage(100);
            PdfPCell cell = new PdfPCell();
            cell.setBorder(0);
            cell.setHorizontalAlignment(0); //0=Left, 1=Centre, 2=Right
            cell.setVerticalAlignment(PdfPCell.ALIGN_TOP);
            cell.setColspan(1);

            if (masolatok < 1) {
                cell.setPhrase(new Phrase(new Chunk("KÉSZPÉNZFIZETÉSI SZÁMLA - EREDETI", fej1Bal)));
            } else {
                cell.setPhrase(new Phrase(new Chunk("KÉSZPÉNZFIZETÉSI SZÁMLA " + masolatok + ". MÁSOLAT", fej1Bal)));
            }
            cell.setFixedHeight(50);
            fejlec1.addCell(cell);

            cell.setHorizontalAlignment(2); //0=Left, 1=Centre, 2=Right
            cell.setPhrase(new Phrase(new Chunk("Sorszám: " + invoiceNo, fej1Jobb)));
            fejlec1.addCell(cell);
            document.add(fejlec1);

            // *****************************************************************************        
            PdfPTable fejlec2 = new PdfPTable(4);

            fejlec2.setWidths(new float[]{2f, 3f, 3f, 1f});
            fejlec2.setWidthPercentage(100);
            cell.setBorder(15);
            cell.setHorizontalAlignment(1); //0=Left, 1=Centre, 2=Right
            cell.setFixedHeight(20);
            cell.setPhrase(new Phrase(new Chunk("Kelt: " + today, fej2)));
            fejlec2.addCell(cell);
            cell.setPhrase(new Phrase(new Chunk("Teljesítési határidő: " + today, fej2)));
            fejlec2.addCell(cell);
            cell.setPhrase(new Phrase(new Chunk("Fizetési határidő: " + today, fej2)));
            fejlec2.addCell(cell);
            cell.setHorizontalAlignment(1); //0=Left, 1=Centre, 2=Right
            cell.setPhrase(new Phrase(new Chunk("1/1 oldal", fej2)));
            fejlec2.addCell(cell);

            cell.setColspan(1);

            document.add(fejlec2);

            // *****************************************************************************        
            PdfPTable fejlec3 = new PdfPTable(2);

            fejlec3.setWidthPercentage(100);
            PdfPTable seller = new PdfPTable(1);
            PdfPCell sellerCell = new PdfPCell();
            sellerCell.setBorder(0);

            sellerCell.setHorizontalAlignment(1); //0=Left, 1=Centre, 2=Right
            sellerCell.setPhrase(new Phrase(new Chunk("ELADÓ", fej1Jobb)));
            seller.addCell(sellerCell);
            sellerCell.setHorizontalAlignment(0); //0=Left, 1=Centre, 2=Right
            if (od.getInvoiceIssuer().startsWith("Rendszer")) {
                sellerCell.setPhrase(new Phrase(new Chunk("Rendszerkovács Kft", partnerek)));
                seller.addCell(sellerCell);
                sellerCell.setPhrase(new Phrase(new Chunk("1174 Budapest", partnerek)));
                seller.addCell(sellerCell);
                sellerCell.setPhrase(new Phrase(new Chunk("Kutassy Ágoston utca 17.", partnerek)));
                seller.addCell(sellerCell);
                sellerCell.setPhrase(new Phrase(new Chunk("Adószám: 23859867-2-42", partnerek)));
                seller.addCell(sellerCell);
            } else {
                sellerCell.setPhrase(new Phrase(new Chunk("Finominfo Bt", partnerek)));
                seller.addCell(sellerCell);
                sellerCell.setPhrase(new Phrase(new Chunk("1174 Budapest", partnerek)));
                seller.addCell(sellerCell);
                sellerCell.setPhrase(new Phrase(new Chunk("Kutassy Ágoston utca 17.", partnerek)));
                seller.addCell(sellerCell);
                sellerCell.setPhrase(new Phrase(new Chunk("Adószám: 21072952-1-42", partnerek)));
                seller.addCell(sellerCell);
            }
            fejlec3.addCell(seller);

            PdfPTable buyer = new PdfPTable(1);
            PdfPCell buyerCell = new PdfPCell();
            buyerCell.setBorder(0);

            buyerCell.setHorizontalAlignment(1); //0=Left, 1=Centre, 2=Right
            buyerCell.setPhrase(new Phrase(new Chunk("VEVŐ", fej1Jobb)));
            buyer.addCell(buyerCell);
            buyerCell.setHorizontalAlignment(0); //0=Left, 1=Centre, 2=Right
            buyerCell.setPhrase(new Phrase(new Chunk(od.getCompany(), partnerek)));
            buyer.addCell(buyerCell);
            buyerCell.setPhrase(new Phrase(new Chunk(od.getZip() + " " + od.getCity(), partnerek)));
            buyer.addCell(buyerCell);
            buyerCell.setPhrase(new Phrase(new Chunk(od.getAddress_1(), partnerek)));
            buyer.addCell(buyerCell);
            buyerCell.setPhrase(new Phrase(new Chunk(od.getTaxNo(), partnerek)));
            buyer.addCell(buyerCell);
            fejlec3.addCell(buyer);

            document.add(fejlec3);

            // *****************************************************************************        
            PdfPTable torzs = new PdfPTable(5);
            torzs.setWidths(new float[]{5f, 0.8f, 1.2f, 1.2f, 0.8f});
            torzs.setWidthPercentage(100);
            cell.setBorder(12);
            cell.setHorizontalAlignment(0); //0=Left, 1=Centre, 2=Right
            cell.setPhrase(new Phrase(new Chunk("Tétel megnevezése", torzsVastag)));
            torzs.addCell(cell);
            cell.setPhrase(new Phrase(new Chunk("Mennyiség", torzsVastag)));
            torzs.addCell(cell);
            cell.setPhrase(new Phrase(new Chunk("Bruttó egységár", torzsVastag)));
            torzs.addCell(cell);
            cell.setPhrase(new Phrase(new Chunk("Bruttó érték", torzsVastag)));
            torzs.addCell(cell);
            cell.setPhrase(new Phrase(new Chunk("Áfa tartalom", torzsVastag)));
            torzs.addCell(cell);
            document.add(torzs);

            int sum = 0;
            int x = 0;
            int y = od.getOrderItems().size();
            for (InvoiceItemData item : od.getOrderItems()) {
                torzs = new PdfPTable(5);
                torzs.setWidths(new float[]{5f, 0.8f, 1.2f, 1.2f, 0.8f});
                torzs.setWidthPercentage(100);
                cell.setBackgroundColor((++x & 1) == 0 ? BaseColor.WHITE : new BaseColor(new Color(235, 235, 235)));
                cell.setBorder(y == x ? 14 : 12);
                cell.setHorizontalAlignment(0); //0=Left, 1=Centre, 2=Right
                cell.setPhrase(new Phrase(new Chunk(item.getOrder_item_sku(), torzs1)));
                torzs.addCell(cell);
                cell.setPhrase(new Phrase(new Chunk("" + item.getProduct_quantiy(), torzs1)));
                torzs.addCell(cell);
                cell.setPhrase(new Phrase(new Chunk("" + item.getProduct_item_price(), torzs1)));
                torzs.addCell(cell);
                int price = item.getProduct_quantiy() * item.getProduct_item_price();
                sum += price;
                cell.setPhrase(new Phrase(new Chunk("" + price, torzs1)));
                torzs.addCell(cell);
                cell.setPhrase(new Phrase(new Chunk(item.getVAT(), torzs1)));
                torzs.addCell(cell);
                document.add(torzs);
            }
            // *****************************************************************************        

            PdfPTable also1 = new PdfPTable(1);

            also1.setWidths(new float[]{1f});
            also1.setWidthPercentage(100);
            cell = new PdfPCell();
            cell.setBorder(0);
            cell.setHorizontalAlignment(2); //0=Left, 1=Centre, 2=Right
            cell.setVerticalAlignment(PdfPCell.ALIGN_BOTTOM);
            cell.setColspan(1);

            cell.setPhrase(new Phrase(new Chunk(
                    "Fizetendő összesen: " + sum + " Ft.  (ÁFA tartalom: " + od.getVAT() + ")", osszesen)));
            cell.setFixedHeight(30);
            also1.addCell(cell);

            document.add(also1);

            // *****************************************************************************        
            also1 = new PdfPTable(1);

            also1.setWidths(new float[]{1f});
            also1.setWidthPercentage(100);
            cell = new PdfPCell();
            cell.setBorder(0);
            cell.setHorizontalAlignment(2); //0=Left, 1=Centre, 2=Right
            cell.setVerticalAlignment(PdfPCell.ALIGN_BOTTOM);
            cell.setColspan(1);

            cell.setPhrase(new Phrase(new Chunk(
                    "Jelen számlát VEVŐ a mai napon (" + today + ") kiegyenlítette.", partnerek)));
            cell.setFixedHeight(25);
            also1.addCell(cell);

            document.add(also1);

            // *****************************************************************************        
        }

        //        Paragraph paragraph = new Paragraph();
        //        paragraph.add("Test");
        //        document.add(paragraph);
        document.close();

        return invoiceNo;
    }
}
