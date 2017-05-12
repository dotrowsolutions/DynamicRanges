package mx.ipn.cic;

import java.util.HashSet;
import java.util.stream.Collectors;

import static mx.ipn.cic.BigDecimalUtil.*;

/**
 * Created by sergio on 01/05/17.
 */
public class DataSet extends HashSet<Pattern> {

    public DataSet() {
    }

    public DataSet filterByClass( String clazz ) {
        return this.stream().filter( p -> p.getClazz().equals( clazz ) ).collect( Collectors.toCollection( DataSet::new ) );
    }

    public DataSet filterByRange( Interval interval, int feature ) {
        DataSet filtered = new DataSet();
        switch( interval.getType() ) {
            case OPEN:
                filtered = this.stream().filter( p -> ( isGreater( p.get( feature ), interval.getLower() ) &&
                        isLess( p.get( feature ), interval.getUpper() ) ) )
                        .collect( Collectors.toCollection( DataSet::new ) );
                break;
            case CLOSED:
                filtered = this.stream().filter( p -> ( isGreaterOrEqual( p.get( feature ), interval.getLower() ) &&
                        isLessOrEqual( p.get( feature ), interval.getUpper() ) ) )
                        .collect( Collectors.toCollection( DataSet::new ) );
                break;
            case SEMIOPEN_LEFT:
                filtered = this.stream().filter( p -> ( isGreater( p.get( feature ), interval.getLower() ) &&
                        isLessOrEqual( p.get( feature ), interval.getUpper() ) ) )
                        .collect( Collectors.toCollection( DataSet::new ) );
                break;
            case SEMIOPEN_RIGHT:
                filtered = this.stream().filter( p -> ( isGreaterOrEqual( p.get( feature ), interval.getLower() ) &&
                        isLess( p.get( feature ), interval.getUpper() ) ) )
                        .collect( Collectors.toCollection( DataSet::new ) );
                break;
        }
        return filtered;
    }

    public int getDimension() {
        return size() == 0 ? 0 : this.iterator().next().size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for( Pattern p : this ) {
            sb.append( p + "\n" );
        }
        return sb.toString();
    }
}
