package mx.ipn.cic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static mx.ipn.cic.BigDecimalUtil.isGreater;
import static mx.ipn.cic.Interval.Type.NULL;
import static mx.ipn.cic.PrintUtils.printTree;

/**
 * Created by sergio on 01/05/17.
 */
public class Main {

    private static final String DATASET = "vertebral_column.csv";
    private static final String CLASSA = "0";
    private static final String CLASSB = "1";

    private DataSet load( String file ) {
        DataSet ds = new DataSet();
        BufferedReader br = null;
        String cvsSplitBy = ",";
        try {
            br = new BufferedReader( new FileReader( file ) );
            String line;
            while( ( line = br.readLine() ) != null ) {
                String[] split = line.split( cvsSplitBy );
                Pattern pattern = new Pattern( split[split.length - 1] );
                for( int i = 0; i < split.length - 1; i++ ) {
                    pattern.add( new BigDecimal( split[i] ) );
                }
                ds.add( pattern );
            }
        } catch( FileNotFoundException e ) {
            e.printStackTrace();
        } catch( IOException e ) {
            e.printStackTrace();
        } finally {
            if( br != null ) {
                try {
                    br.close();
                } catch( IOException e ) {
                    e.printStackTrace();
                }
            }
        }
        return ds;
    }

    private TreeNode contention( Interval interval, Interval intersection ) {
        Interval left = new Interval( interval.getLower(), intersection.getLower(), Interval.Type.OPEN );
        left.setClazz( interval.getClazz() );
        Interval right = new Interval( intersection.getUpper(), interval.getUpper(), Interval.Type.OPEN );
        right.setClazz( interval.getClazz() );
        return new TreeNode( left, right );
    }

    private TreeNode exclusion( Interval intervalA, Interval intervalB ) {
        return new TreeNode( intervalA, intervalB );
    }

    private TreeNode overlap( Interval intervalA, Interval intervalB ) {
        if( isGreater( intervalB.getLower(), intervalA.getLower() ) ) {
            Interval left = new Interval( intervalA.getLower(), intervalB.getLower(), Interval.Type.OPEN, intervalA.getClazz() );
            //Interval left = new Interval( BigDecimalUtil.InfinityNegative(), intervalB.getLower(), Interval.Type.SEMIOPEN_LEFT, intervalA.getClazz() );
            Interval right = new Interval( intervalA.getUpper(), intervalB.getUpper(), Interval.Type.OPEN, intervalB.getClazz() );
            //Interval right = new Interval( intervalA.getUpper(), BigDecimalUtil.InfinityPositive(), Interval.Type.SEMIOPEN_RIGHT, intervalB.getClazz() );
            return new TreeNode( left, right );
        } else {
            Interval left = new Interval( intervalB.getLower(), intervalA.getLower(), Interval.Type.OPEN, intervalB.getClazz() );
            //Interval left = new Interval( BigDecimalUtil.InfinityNegative(), intervalA.getLower(), Interval.Type.SEMIOPEN_LEFT, intervalB.getClazz() );
            Interval right = new Interval( intervalB.getUpper(), intervalA.getUpper(), Interval.Type.OPEN, intervalA.getClazz() );
            //Interval right = new Interval( intervalB.getUpper(), BigDecimalUtil.InfinityPositive(), Interval.Type.SEMIOPEN_RIGHT, intervalA.getClazz() );
            return new TreeNode( left, right );
        }
    }

    public Main() {
        DataSet ds = load( DATASET );
        DataSet c2 = ds.filterByClass( CLASSA );
        DataSet c3 = ds.filterByClass( CLASSB );

        System.out.println( "=============== INTERVALS ================\n" );

        System.out.println( "Class " + CLASSA );
        for( int f = 0; f < c2.getDimension(); f++ ) {
            System.out.printf( "\t + Feature %d: %s \n", f, getInterval( c2, f ) );
        }

        System.out.println( "Class " + CLASSB );
        for( int f = 0; f < c3.getDimension(); f++ ) {
            System.out.printf( "\t + Feature %d: %s \n", f, getInterval( c3, f ) );
        }

        System.out.println( "=============== INTERSECTIONS ================\n" );
        for( int f = 0; f < c3.getDimension(); f++ ) {
            System.out.printf( "\t + Feature %d: %s \n", f, getInterval( c3, f ).inter( getInterval( c2, f ) ) );
        }

        System.out.println( "=============== TREE NODES ================\n" );
        List<Integer> features = new ArrayList<>();
        for( int f = 0; f < ds.getDimension(); f++ ) {
            features.add( new Integer( f ) );
        }

        List<TreeNode> rootNodes = new ArrayList<>();
        for( int f = 0; f < ds.getDimension(); f++ ) {
            TreeNode tn = getTreeNode( ds, f );
            getTree( tn, new ArrayList<>( features ) );
            printTree( tn, 0 );
            rootNodes.add( tn );
        }
        System.out.println( "\n=============== CLASSIFY C2 ================\n" );
        for( Pattern p : c2 ) {
            System.out.println( p + " : " + classify2( rootNodes, p ) );
        }

        System.out.println( "=============== CLASSIFY C3 ================\n" );
        for( Pattern p : c3 ) {
            System.out.println( p + " : " + classify2( rootNodes, p ) );
        }

    }

    private String classify1( List<TreeNode> rootNodes, Pattern p ) {
        List<String> classes = new ArrayList<>();
        for( TreeNode rootNode : rootNodes ) {
            String clazz = exploreTreeWeightless( rootNode, p );
            classes.add( clazz );
        }
        return majority( classes );
    }

    private String classify2( List<TreeNode> rootNodes, Pattern p ) {
        List<WeightClass> weightClasses = new ArrayList<>();
        for( TreeNode rootNode : rootNodes ) {
            exploreTreeWeightfull( rootNode, p, 0, weightClasses );
        }
        return majorityWeight( weightClasses );
    }

    private String exploreTreeWeightless( TreeNode tn, Pattern p ) {
        if( p.inRangeHard( tn.getIntervalA(), tn.getFeature() ) ) {
            return tn.getIntervalA().getClazz();
        } else if( p.inRangeHard( tn.getIntervalB(), tn.getFeature() ) ) {
            return tn.getIntervalB().getClazz();
        } else {
            List<String> classes = new ArrayList<>();
            for( TreeNode child : tn.getChilds() ) {
                String clazz = exploreTreeWeightless( child, p );
                classes.add( clazz );
            }
            return majority( classes );
        }
    }

    private void exploreTreeWeightfull( TreeNode tn, Pattern p, int deepLevel, List<WeightClass> weightClasses ) {
        if( p.inRangeHard( tn.getIntervalA(), tn.getFeature() ) ) {
            weightClasses.add( new WeightClass( tn.getIntervalA().getClazz(), 1.0 / ( double ) ( deepLevel + 1 ) ) );
        } else if( p.inRangeHard( tn.getIntervalB(), tn.getFeature() ) ) {
            weightClasses.add( new WeightClass( tn.getIntervalB().getClazz(), 1.0 / ( double ) ( deepLevel + 1 ) ) );
        } else {
            for( TreeNode child : tn.getChilds() ) {
                exploreTreeWeightfull( child, p, deepLevel + 1, weightClasses );
            }
        }
    }

    private String majorityWeight( List<WeightClass> classes ) {
        double votesClassA = 0d;
        double votesClassB = 0d;
        for( WeightClass clazz : classes ) {
            if( clazz.getClazz() != null ) {
                votesClassA += clazz.getClazz().equals( CLASSA ) ? clazz.getWeight() : 0;
                votesClassB += clazz.getClazz().equals( CLASSB ) ? clazz.getWeight() : 0;
            }
        }
        if( votesClassA == votesClassB ) {
            System.out.println( votesClassA + "=" + votesClassB );
        }
        return votesClassA == votesClassB ? null : ( votesClassA > votesClassB ? CLASSA : CLASSB );
    }

    private String majority( List<String> classes ) {
        int votesClassA = 0;
        int votesClassB = 0;
        for( String clazz : classes ) {
            if( clazz != null ) {
                votesClassA += clazz.equals( CLASSA ) ? 1 : 0;
                votesClassB += clazz.equals( CLASSB ) ? 1 : 0;
            }
        }
        return votesClassA == votesClassB ? null : ( votesClassA > votesClassB ? CLASSA : CLASSB );
    }

    private void getTree( TreeNode parentNode, List<Integer> features ) {
        features.remove( new Integer( parentNode.getFeature() ) );
        for( int f = 0; f < features.size(); f++ ) {
            int feature = features.get( f );
            TreeNode tn = getTreeNode( parentNode.getNotClassified(), feature );
            parentNode.addChild( tn );
            if( tn.getNotClassified().size() != 0 ) {
                getTree( tn, new ArrayList<>( features ) );
            }
        }
    }

    private TreeNode getTreeNode( DataSet ds, int feature ) {
        DataSet dsA = ds.filterByClass( CLASSA );
        DataSet dsB = ds.filterByClass( CLASSB );
        Interval intClassA = getInterval( dsA, feature );
        intClassA.setClazz( CLASSA );
        Interval intClassB = getInterval( dsB, feature );
        intClassB.setClazz( CLASSB );
        Interval intersection = intClassA.inter( intClassB );
        /*if( intClassA.equals( intClassB ) ) {
            do {
                intersection =
            } while( intClassA.equals( intClassB ) && intersection.equals( intClassB ) );
        } else {
            intersection = intClassA.inter( intClassB );
        }*/
        if( intClassA.equals( intClassB ) ){
            return null;
        }

        TreeNode treeNode;
        if( intClassA.equals( intersection ) ) {
            treeNode = contention( intClassB, intersection );
        } else if( intClassB.equals( intersection ) ) {
            treeNode = contention( intClassA, intersection );
        } else if( intersection.getType() == NULL ) {
            treeNode = exclusion( intClassA, intClassB );
        } else {
            treeNode = overlap( intClassA, intClassB );
        }
        treeNode.setFeature( feature );
        treeNode.setNotClassified( ds.filterByRange( intersection, feature ) );
        return treeNode;
    }

    private Interval getInterval( DataSet ds, int feature ) {
        Iterator<Pattern> iterator = ds.iterator();
        if( ! iterator.hasNext() )
            return new Interval();
        Pattern first = iterator.next();
        BigDecimal max = first.get( feature );
        BigDecimal min = first.get( feature );
        while( iterator.hasNext() ) {
            Pattern pattern = iterator.next();
            BigDecimal value = pattern.get( feature );
            max = max.max( value );
            min = min.min( value );
        }
        return new Interval( min, max, Interval.Type.CLOSED );
    }

    public static void main( String[] args ) {
        new Main();
    }

    private class WeightClass {
        private String clazz;
        private Double weight;

        public WeightClass( String clazz, Double weight ) {
            this.clazz = clazz;
            this.weight = weight;
        }

        public String getClazz() {
            return clazz;
        }

        public Double getWeight() {
            return weight;
        }
    }
}
