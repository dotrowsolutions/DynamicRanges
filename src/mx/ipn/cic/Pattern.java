package mx.ipn.cic;

import java.math.BigDecimal;
import java.util.ArrayList;

import static mx.ipn.cic.BigDecimalUtil.*;

/**
 * Created by sergio on 01/05/17.
 */
public class Pattern extends ArrayList<BigDecimal> {
    private String clazz;

    public Pattern() {
    }

    public Pattern( String clazz ) {
        this.clazz = clazz;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz( String clazz ) {
        this.clazz = clazz;
    }

    public boolean inRangeHard( Interval interval, int feature ) {
        switch( interval.getType() ) {
            case CLOSED:
                return isGreaterOrEqual( get( feature ), interval.getLower() ) && isLessOrEqual( get( feature ), interval.getUpper() );
            case OPEN:
                return isGreater( get( feature ), interval.getLower() ) && isLess( get( feature ), interval.getUpper() );
            case SEMIOPEN_RIGHT:
                return isGreaterOrEqual( get( feature ), interval.getLower() ) && isLess( get( feature ), interval.getUpper() );
            case SEMIOPEN_LEFT:
                return isGreater( get( feature ), interval.getLower() ) && isLessOrEqual( get( feature ), interval.getUpper() );
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( "[" );
        for( BigDecimal feature : this ) {
            sb.append( feature + "," );
        }
        sb.deleteCharAt( sb.length() - 1 );
        sb.append( "] -> " );
        sb.append( getClazz() );
        return sb.toString();
    }
}
