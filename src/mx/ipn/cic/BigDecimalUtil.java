package mx.ipn.cic;

import java.math.BigDecimal;

/**
 * Created by sergio on 01/05/17.
 */
public class BigDecimalUtil {
    public static boolean isGreater( BigDecimal a, BigDecimal b ) {
        return a.compareTo( b ) > 0;
    }

    public static boolean isGreaterOrEqual( BigDecimal a, BigDecimal b ) {
        return a.compareTo( b ) > 0 || a.compareTo( b ) == 0;
    }

    public static boolean isLess( BigDecimal a, BigDecimal b ) {
        return a.compareTo( b ) < 0;
    }

    public static boolean isEqual( BigDecimal a, BigDecimal b ) {
        return a.compareTo( b ) == 0;
    }

    public static boolean isLessOrEqual( BigDecimal a, BigDecimal b ) {
        return a.compareTo( b ) < 0 || a.compareTo( b ) == 0;
    }

    public static BigDecimal InfinityPositive(){
        return new BigDecimal( Double.MAX_VALUE );
    }

    public static BigDecimal InfinityNegative(){
        return new BigDecimal( -Double.MAX_VALUE );
    }
}
