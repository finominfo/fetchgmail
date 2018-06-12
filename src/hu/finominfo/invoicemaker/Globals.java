package hu.finominfo.invoicemaker;

import hu.finominfo.fetchgmail.GMailReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
//import org.apache.log4j.Logger;

public enum Globals {

    MYNAME {

        @Override
        public String value() {
            return "fetchgmail";
        }
    },
    /**
     * SEPARATOR is the file separator
     */
    SEPARATOR {

        @Override
        public String value() {
            return System.getProperty("file.separator");
        }
    },
    /**
     * BASE_DIR is the base directory of this application.
     * If we are in development mode then the base dir will be one level higher.
     */
    BASE_DIR {

        @Override
        public String value() {
            String myName = MYNAME.toString();
            String jarName = myName + ".jar";
            int jarLength = jarName.length();
            String myPath = System.getProperty("user.dir");
            if (GMailReader.getPath().endsWith(jarName)) {
                myPath = GMailReader.getPath().substring(0, GMailReader.getPath().length() - jarLength);
            }
            if (myPath.endsWith(myName + System.getProperty("file.separator") + "dist")) {
                return myPath.substring(0, myPath.length() - 5);
            }
            myPath = myPath.replace("\\", "/").replace("/", Globals.SEPARATOR.toString()).replace("%20", " ");
            while (myPath.contains(Globals.SEPARATOR.toString() + Globals.SEPARATOR.toString())) {
                myPath = myPath.replace(Globals.SEPARATOR.toString() + Globals.SEPARATOR.toString(), Globals.SEPARATOR.toString());
            }
            if (myPath.endsWith(Globals.SEPARATOR.toString())) {
                myPath = myPath.substring(0, myPath.length() - 1);
            }
            return myPath;
        }
    },
    /**
     * CONF_DIR is the conf directory of this application.
     */
    CONF_DIR {

        @Override
        public String value() {
            return BASE_DIR.value()
                    + System.getProperty("file.separator")
                    + "conf";
        }
    };

    public abstract String value();

    @Override
    public String toString() {
        return value();
    }
    
//    private static Logger log4j = Logger.getLogger(Globals.class);


    public static void writeTextFile(StringBuilder sb, String fileName) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        Writer out = new OutputStreamWriter(new FileOutputStream(fileName), "UTF8");
        try {
            out.write(sb.toString());
        } finally {
            out.close();
        }
    }
    
}
