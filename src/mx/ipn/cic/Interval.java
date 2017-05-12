package mx.ipn.cic;

import java.math.BigDecimal;

import static mx.ipn.cic.BigDecimalUtil.*;

/**
 * Created by sergio on 01/05/17.
 */
public class Interval {
    enum Type {
        CLOSED,
        OPEN,
        SEMIOPEN_RIGHT,
        SEMIOPEN_LEFT,
        NULL,
    }

    private BigDecimal lower;
    private BigDecimal upper;
    private Type type;
    private String clazz;

    public Interval() {
        this.type = Type.NULL;
    }

    public Interval( BigDecimal lower, BigDecimal upper ) {
        this.lower = lower;
        this.upper = upper;
        this.type = Type.CLOSED;
    }

    public Interval( BigDecimal lower, BigDecimal upper, String clazz ) {
        this.lower = lower;
        this.upper = upper;
        this.type = Type.CLOSED;
        this.clazz = clazz;
    }

    public Interval( BigDecimal lower, BigDecimal upper, Type type ) {
        this.lower = lower;
        this.upper = upper;
        this.type = type;
    }

    public Interval( BigDecimal lower, BigDecimal upper, Type type, String clazz ) {
        this.lower = lower;
        this.upper = upper;
        this.type = type;
        this.clazz = clazz;
    }

    public BigDecimal getLower() {
        return lower;
    }

    public void setLower( BigDecimal lower ) {
        this.lower = lower;
    }

    public BigDecimal getUpper() {
        return upper;
    }

    public void setUpper( BigDecimal upper ) {
        this.upper = upper;
    }

    public Type getType() {
        return type;
    }

    public void setType( Type type ) {
        this.type = type;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz( String clazz ) {
        this.clazz = clazz;
    }

    public Interval inter( Interval other ) {
        if( this.getType() == Type.NULL || other.getType() == Type.NULL ){
            return new Interval();
        }
        Interval a = isLess( this.getLower(), other.getLower() ) ? this : other;
        Interval b = isLess( this.getLower(), other.getLower() ) ? other : this;
        if( isLess( a.getUpper(), b.getLower() ) ) {
            return new Interval(); // Null
        } else if( isEqual( a.getUpper(), b.getLower() ) ) {
            if( ( a.getType() == Type.CLOSED || a.getType() == Type.SEMIOPEN_LEFT ) &&
                    ( b.getType() == Type.CLOSED || b.getType() == Type.SEMIOPEN_RIGHT ) ) {
                return new Interval( a.getUpper(), b.getLower() );
            } else return new Interval();
        } else {
            Boolean closedLeft;
            Interval interval = new Interval();
            if( isEqual( a.getLower(), b.getLower() ) ) {
                interval.setLower( a.getLower() );
                closedLeft = ( a.getType() == Type.CLOSED || a.getType() == Type.SEMIOPEN_RIGHT ) &&
                        ( b.getType() == Type.CLOSED || b.getType() == Type.SEMIOPEN_RIGHT );
            } else {
                interval.setLower( b.getLower() );
                closedLeft = ( b.getType() == Type.CLOSED || b.getType() == Type.SEMIOPEN_RIGHT );
            }
            Boolean closedRight;
            if( isEqual( a.getUpper(), b.getUpper() ) ) {
                interval.setUpper( a.getUpper() );
                closedRight = ( a.getType() == Type.CLOSED || a.getType() == Type.SEMIOPEN_LEFT ) &&
                        ( b.getType() == Type.CLOSED || b.getType() == Type.SEMIOPEN_LEFT );
            } else {
                if( isGreater( a.getUpper(), b.getUpper() ) ) {
                    interval.setUpper( b.getUpper() );
                    closedRight = ( b.getType() == Type.CLOSED || b.getType() == Type.SEMIOPEN_LEFT );
                } else {
                    interval.setUpper( a.getUpper() );
                    closedRight = ( a.getType() == Type.CLOSED || a.getType() == Type.SEMIOPEN_LEFT );
                }
            }
            if( closedLeft && closedRight ) {
                interval.setType( Type.CLOSED );
            } else if( ! closedLeft && ! closedRight ) {
                interval.setType( Type.OPEN );
            } else if( closedLeft && ! closedRight ) {
                interval.setType( Type.SEMIOPEN_RIGHT );
            } else if( ! closedLeft && closedRight ) {
                interval.setType( Type.SEMIOPEN_LEFT );
            }
            return interval;
        }
    }

    public Interval subst( Interval other ) {
        return new Interval();
    }

    @Override
    public boolean equals( Object o ) {
        if( this == o ) return true;
        if( ! ( o instanceof Interval ) ) return false;

        Interval interval = ( Interval ) o;

        if( getLower() != null ? ! getLower().equals( interval.getLower() ) : interval.getLower() != null )
            return false;
        if( getUpper() != null ? ! getUpper().equals( interval.getUpper() ) : interval.getUpper() != null )
            return false;
        return true;
        //return true;//getType() == interval.getType();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( lower );
        sb.append( ", " );
        sb.append( upper );
        switch( getType() ) {
            case OPEN:
                sb.insert( 0, "(" );
                sb.append( ")" );
                break;
            case CLOSED:
                sb.insert( 0, "[" );
                sb.append( "]" );
                break;
            case SEMIOPEN_LEFT:
                sb.insert( 0, "(" );
                sb.append( "]" );
                break;
            case SEMIOPEN_RIGHT:
                sb.insert( 0, "[" );
                sb.append( ")" );
                break;
            case NULL:
                sb = new StringBuilder( "0" );
                break;
        }
        if( clazz != null )
            sb.append( " -> " ).append( clazz );
        return sb.toString();
    }
}
