package io.moresushant48.callrec.Helpers;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GetCurrentDateTime {

    public static String getCurrentDateTime() {

        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());

    }

}
