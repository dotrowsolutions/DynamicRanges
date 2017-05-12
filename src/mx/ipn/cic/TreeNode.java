package mx.ipn.cic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sergio on 01/05/17.
 */
public class TreeNode {
    private Integer feature;
    private Interval intervalA;
    private Interval intervalB;
    private DataSet notClassified;
    private List<TreeNode> childs = new ArrayList<>();

    public TreeNode() {
    }

    public TreeNode( Interval intervalA, Interval intervalB, DataSet notClassified ) {
        this.intervalA = intervalA;
        this.intervalB = intervalB;
        this.notClassified = notClassified;
    }

    public TreeNode( Interval intervalA, Interval intervalB ) {
        this.intervalA = intervalA;
        this.intervalB = intervalB;
    }

    public Integer getFeature() {
        return feature;
    }

    public void setFeature( Integer feature ) {
        this.feature = feature;
    }

    public TreeNode( DataSet notClassified ) {
        this.notClassified = notClassified;
    }

    public Interval getIntervalA() {
        return intervalA;
    }

    public void setIntervalA( Interval intervalA ) {
        this.intervalA = intervalA;
    }

    public Interval getIntervalB() {
        return intervalB;
    }

    public void setIntervalB( Interval intervalB ) {
        this.intervalB = intervalB;
    }

    public DataSet getNotClassified() {
        return notClassified;
    }

    public void setNotClassified( DataSet notClassified ) {
        this.notClassified = notClassified;
    }

    public List<TreeNode> getChilds() {
        return childs;
    }

    public void addChild( TreeNode child ) {
        this.childs.add( child );
    }

    public boolean hasChilds(){
        return this.childs.size() > 0;
    }
}
