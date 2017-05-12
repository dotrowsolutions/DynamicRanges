package mx.ipn.cic;

/**
 * Created by sergio on 02/05/17.
 */
public class PrintUtils {
    private static String pad( int len ) {
        StringBuilder padded = new StringBuilder();
        while( padded.length() < len ) {
            padded.append( " " );
        }
        return padded.toString();
    }

    public static void printTreeNode( TreeNode tn, int offset ) {
        StringBuilder sb = new StringBuilder();
        sb.append( pad( offset ) + String.format( "Feature %d:", tn.getFeature() ) );
        sb.append( "\n" + pad( offset ) + "┌─────────────────┬─────────────────┐\n" );
        sb.append( String.format( pad( offset ) + "│ %10s │ %10s │\n", tn.getIntervalA(), tn.getIntervalB() ) );
        sb.append( pad( offset ) + "├─────────────────┴─────────────────┤\n" );
        int it = 0;
        for( Pattern p : tn.getNotClassified() ) {
            sb.append( String.format( pad( offset ) + "│       %-27s │\n", p ) );
            if( it > 2 ) {
                sb.append( String.format( pad( offset ) + "│       %-27s │\n", "... +" + ( tn.getNotClassified().size() - 4 ) ) );
                break;
            }
            it++;
        }
        sb.append( pad( offset ) + "└───────────────────────────────────┘\n" );
        System.out.println( sb );
    }

    public static void printTree( TreeNode tn, int deepLevel ) {
        printTreeNode( tn, deepLevel * 10 );
        for( TreeNode child : tn.getChilds() ) {
            printTree( child, deepLevel + 1 );
        }
    }
}
