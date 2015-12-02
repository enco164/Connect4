package rs.project4420.connect4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by enco on 7.11.15..
 */
public class Tree {
    public static final int PARENT = 1;
    private static final String TAG = Tree.class.getSimpleName()                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    ;

    private int value;
    private int column;
    private int dubina;
    private List<Tree> children;
    private int alpha;
    private int beta;
    private Tree parent;
    private int abTree;
    private CoinItem[][] data;

    public Tree(int value) {
        this.value = value;
        this.children = new ArrayList<Tree>();
    }

    public Tree() {
        this.value = (int) Double.POSITIVE_INFINITY;
        this.children = new ArrayList<Tree>();
        this.abTree = -1;
    }

    public Tree(String a) {
        this.value = (int) Double.NEGATIVE_INFINITY;
        this.children = new ArrayList<Tree>();
        this.alpha = (int)Double.NEGATIVE_INFINITY;
        this.beta = (int)Double.POSITIVE_INFINITY;
        this.parent = null;
        this.dubina = 1;
        this.abTree = -1;
    }

    public void addTreeChild(Tree child){
        children.add(child);
    }

    public void printTree(){
        //if (value == 0) return;
        System.out.println(value + " ("+ children.size()+")");
    }

    public Tree obilazak(int param){
        if (!children.isEmpty()){
            printTree();
            for(Tree aChildren : children) { aChildren.obilazak(param * (-1)); }
        }
        else if (children.isEmpty()) { System.out.println("List: "+value); }
        return this;
    }

    /*public Tree createTree(int turn){
        int maxDubina = 4;
        int znak;
        //Tree pobednik = null;
        //return CointItem koji vodi do pobede
        //CointItem pobednik;
        //if CoinItem doveo do pobede pobednik = coinItem
        //if CoinItem doveo do pobede u nizem nivou pobednik = coinItem
        if (this.getDubina() < maxDubina) { //izlazak iz rekurzije
            for (int i = 0; i < 7; i++) {
                CoinItem[][] copy = copyData(this.getCoinData());
                CoinItem[][] newCopy = addNode(copy, i, -1*turn);
                Tree node = new Tree();
                node.setParent(this);
                node.setDubina(this.getDubina() + 1);
                node.setColumn(i);
                if (node.getDubina() == maxDubina) node.setAbTree(node.getColumn());
                if (newCopy != null) {
                    node.setCoinData(newCopy);
                    if (node.getDubina()%2 == 0) znak = 1; else znak = -1;
                    //node.setValue(NodeScoring.vratiSumu(node.getCoinData(), node.getColumn(), -1*turn, znak));
                    node.setValue(Evaluation.evalFunction(node.getCoinData(), node.getColumn(), -1*turn, znak));
                    this.addTreeChild(node);
                    if (Math.abs(node.getValue()) != (int)Double.POSITIVE_INFINITY)
                        *//*if (pobednik == null) pobednik = node;
                        else if (pobednik.getDubina() > node.getDubina()) pobednik = node;*//*
                        node.createTree(-1*turn);  //rekurzivni poziv
                    else {
                        node.setAbTree(node.getDubina());
                        //Log.d(TAG, "POBEDA!!!!!");
                    }
                }
            }
            this.setCoinData(null);
        }
        else return null;
        *//*if (pobednik != null && pobednik.getParent() != null) {
            while (pobednik.getParent().getParent() != null) {
                pobednik = pobednik.getParent();
            }
            this.setColumn(pobednik.getColumn());
        }*//*
        return this;
    }*/



    /*private void logData(CoinItem[][] data){
        for (int x = 0; x < 6; x++) {
            String s = "";
            for (int y = 0; y < 7; y++) {
                s+= " " + data[x][y].getCoinOwner();
            }
            Log.d(TAG, s);
        }
        Log.d(TAG, "-------");
    }*/

    /*public int miniMax(int param){
        if (!children.isEmpty()){
            //alpha = (int)Double.NEGATIVE_INFINITY; beta = (int)Double.POSITIVE_INFINITY;
            value = (int)Double.POSITIVE_INFINITY*param*(-1);}
        //printTree();
        for(Tree aChildren : children) {
            aChildren.miniMax(param * (-1));
            if (param == 1 && aChildren.value > value) setValue(aChildren.value);
            else if (param == -1 && aChildren.value < value) setValue(aChildren.value);
        }
        return value;
    }*/

    /*public int alphaBetaAlgorithm(int param){
        if (!children.isEmpty()){
            //System.out.println("this: "+this); //alpha = (int)Double.NEGATIVE_INFINITY; beta = (int)Double.POSITIVE_INFINITY;
            value = (int)Double.POSITIVE_INFINITY*param*(-1);}
        //System.out.println(value+", "+ getAlpha()+", "+getBeta());
        for (Tree aChildren : children) {
            aChildren.setAlpha(getAlpha());
            aChildren.setBeta(getBeta());
            int alphaBeta = aChildren.alphaBetaAlgorithm(-param);
            if (!children.isEmpty()) {
                if (param == -1) {
                    if (alphaBeta < getBeta()) setBeta(alphaBeta);
                    if (alphaBeta < value) {
                        setValue(aChildren.value);
                    }
                    //System.out.println("changed: " + getValue() + " " + getAlpha() + " " + getBeta());
                } else {
                    if (alphaBeta > getAlpha()) setAlpha(alphaBeta);
                    if (alphaBeta > value) setValue(aChildren.value);
                    //System.out.println("changed: " + getValue() + " " + getAlpha() + " " + getBeta());
                }
                if (getAlpha() > getBeta()) break;
            }
        }
        return value;
    }*/

    /*public int newAlphaBetaAlgorithm(int param){
        //if (!children.isEmpty()) value = (int)Double.POSITIVE_INFINITY*param*(-1);
        for (Tree aChildren : children) {
            aChildren.setAlpha(getAlpha());
            aChildren.setBeta(getBeta());
            int alphaBeta = aChildren.newAlphaBetaAlgorithm(-param);
            if (this.getAbTree() == -1) {
                this.setAbTree(aChildren.getColumn());
            }
            if (!children.isEmpty()) {
                if (param == -1) {
                    if (alphaBeta < getBeta()) setBeta(alphaBeta);
                    if (alphaBeta < value) {
                        setValue(aChildren.value);
                        setAbTree(aChildren.getColumn());
                    }
                } else {
                    if (alphaBeta > getAlpha()) setAlpha(alphaBeta);
                    if (alphaBeta > value) {
                        setValue(aChildren.value);
                        setAbTree(aChildren.getColumn());
                    }
                }
                if (getAlpha() > getBeta()) break;
            }
        }
        return value;
    }*/

    /*public CoinItem[][] addNode(CoinItem[][] data, int col, int turn){
        for (int i = 5; i >= 0; i--) {
            if (data[i][col].getCoinOwner() == Constants.COIN_OWNER_GRID) {
                data[i][col].setCoinOwner(turn);
                return data;
            }
        }
        return null;
    }*/

    /*public CoinItem[][] copyData(CoinItem[][] data){
        CoinItem[][] copy = new CoinItem[6][7];
        for (int x = 0; x < 6; x++) {
            for (int y = 0; y < 7; y++) {
                copy[x][y] = new CoinItem();
                copy[x][y].setCoinOwner(data[x][y].getCoinOwner());
            }
        }
        return copy;
    }*/


    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public List<Tree> getChildren() {
        return children;
    }

    public void setChildren(List<Tree> children) {
        this.children = children;
    }

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public int getBeta() {
        return beta;
    }

    public void setBeta(int beta) {
        this.beta = beta;
    }

    public CoinItem[][] getData() {
        return data;
    }

    public void setData(CoinItem[][] data) {
        this.data = data;
    }

    public int getDubina() {
        return dubina;
    }

    public void setDubina(int dubina) {
        this.dubina = dubina;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public Tree getParent() {
        return parent;
    }

    public void setParent(Tree parent) {
        this.parent = parent;
    }

    public int getAbTree() {
        return abTree;
    }

    public void setAbTree(int abTree) {
        this.abTree = abTree;
    }

    @Override
    public String toString() {
        return "Tree{" +
                "value=" + value +
                ", column=" + column +
                ", dubina=" + dubina +
                ", alpha=" + alpha +
                ", beta=" + beta +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
