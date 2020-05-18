package Misson6_bing;

public class Tree implements Comparable<Tree> {
    private Node root;

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }
    /**
     *中序遍历
     */
    public void inDisplay(Node node){//递归遍历，左根右
        if (node!=null){
            inDisplay(node.leftChild);
            System.out.println(node.key+":"+node.charData);
            inDisplay(node.rightChild);
        }
    }

    @Override
    public int compareTo(Tree o) {
        return 0;
    }
}
