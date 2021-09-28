import java.util.ArrayList;
import java.util.List;

/**
 *
 * AVLTree
 *
 * An implementation of a AVL Tree with
 * distinct integer keys and info
 *
 */
//Shir Shintel
//Eden May Tal

public class AVLTree {
	
	private IAVLNode root;
	private IAVLNode min;
	private IAVLNode max;
	
	
	public AVLTree() { // Constructor 
		this.root = null;
		this.min = null;
		this.max = null;
	}
	/**
	 * public IAVLNode getMin()
	 * 
	 * returns the node with the minimum key or null if tree is empty
	 * complexity: O(1).
	 */
	public IAVLNode getMin() {
		return this.min;
	}
	
	/**
	 * public IAVLNode getMax()
	 * 
	 * returns the node with the maximum key or null if tree is empty
	 * complexity: O(1).
	 */
	public IAVLNode getMax() {
		return this.max;
	}
	
	/**
	 * public void setMin(IAVLNode node)
	 * 
	 * sets new min
	 * complexity: O(1).
	 */
	private void setMin(IAVLNode node) {
		this.min=node;
	}
	
	/**
	 * public void setMax(IAVLNode node)
	 * 
	 * sets new max
	 * complexity: O(1).
	 */
	private void setMax(IAVLNode node) {
		this.max=node;
	}
	
	/**
	 * public void setRoot(IAVLNode node)
	 * 
	 * sets new root
	 * complexity: O(1).
	 */
	private void setRoot(IAVLNode node) {
		this.root=node;
	}

  /**
   * public boolean empty()
   *
   * returns true if and only if the tree is empty
   * complexity: O(1).
   */
  public boolean empty() {
    if (getRoot() == null) {
    	return true;
    }
    if (!getRoot().isRealNode()) {
    	return true;
    }
    return false; 
  }

 /**
   * public String search(int k)
   *
   * returns the info of an item with key k if it exists in the tree
   * otherwise, returns null
   * complexity = recSearch complexity= O(logn).
   */
  public String search(int k) {
	  if (empty()) { return null; }
	  IAVLNode currNode = getRoot();
	  String valueToReturn = recSearch(k, currNode);
	  return valueToReturn;
	}
  /**
   * private String recSearch(int k, IAVLNode node)
   *
   * returns the info of an item with key k if it exists in sub-tree with node 'node'
   * otherwise, returns null
   * complexity = O(logn).
   */  
  private String recSearch(int k, IAVLNode node) {
	  while (node.isRealNode()) {
		  if (node.getKey() == k) {
			  return node.getValue();
		  }
		  else {
		  if (node.getKey() < k) {
			  return recSearch(k, node.getRight());
		  }
		  else {
		  if (node.getKey() > k) {
			  return recSearch(k, node.getLeft());
		  }
		  }
	  }
	  }
	  return null;
  }

  /**
   * public int insert(int k, String i)
   *
   * inserts an item with key k and info i to the AVL tree.
   * the tree must remain valid (keep its invariants).
   * returns the number of rebalancing operations, or 0 if no rebalancing operations were necessary.
   * promotion/rotation - counted as one rebalnce operation, double-rotation is counted as 2.
   * returns -1 if an item with key k already exists in the tree.
   * complexity: O(logn).
   */
   public int insert(int k, String i) {
	   	   
	   if (empty()) { // insert the root
		  IAVLNode rootToInsert = new AVLNode(k, i, null); 
		  this.setRoot(rootToInsert); 
		  this.setMin(rootToInsert); 
		  this.setMax(rootToInsert); 
		  return 0;  // no need for rebalancing operations
	  }
	   
	   IAVLNode root = getRoot(); 
	   IAVLNode currentNode = treeInsert(root, k, i); // insert node with key k and value i, returns the node
	   if (currentNode == null) { // the key is already exist in the tree
		   return -1;
	   }
	   
	   int rebalancing = insertRebalance(currentNode); // rebalancing the tree, returns the number of rebalancing operations
	   insertUpdate(currentNode, k); // update size, height, min and max
	   return rebalancing;
   } 
   /**
    * private int insertRebalance(IAVLNode currentNode)
    *
    * Receives the node that was inserted (currentNode) and rebalance the tree
    * promote and move forward in the tree as long as possible, then if the the is not balanced -> rotate/ double rotate 
    * pre condition: currentNode is not null, the tree is not balanced
    * returns the number of rebalancing operations
    * complexity: O(logn).
    */
	private int insertRebalance(IAVLNode currentNode) {
		int rebalancing = 0;
		
		while ((currentNode.getParent() != null) && (!(isBalanced(currentNode.getParent()))) && (canPromote(currentNode.getParent()))) { 
			// currentNode is not the root, promote is possible and the tree is not balanced 
			promote(currentNode.getParent());
			rebalancing ++;
			currentNode = currentNode.getParent(); // move towards the root
		}
		if (currentNode.getParent() == null) { // reached the root -> done
			return rebalancing;
		}
		if (!(isBalanced(currentNode.getParent()))){ // can't promote but the tree is not balanced 
			if (rankDifferenceLeft(currentNode.getParent()) == 0) { // node's parent is 0,2
				if (rankDifferenceLeft(currentNode) == 1) { // node is 1,2 -> single right rotation
					rebalancing += singleRightRotation(currentNode.getParent(), 3); 
 				}
				else { // node is 2,1 -> double rotation (LR)
					IAVLNode parent = currentNode.getParent();
					rebalancing += singleLeftRotation(currentNode, 3);
					rebalancing += singleRightRotation(parent, 2);
					} 
			}
			else { // node's parent is 2,0 (symmetric cases)
				if (rankDifferenceRight(currentNode) == 1) { // node is 2,1 -> single left rotation
					rebalancing += singleLeftRotation(currentNode.getParent(), 3); 
				}
				else { // node is 1,2 -> double rotation (RL)
					IAVLNode parent = currentNode.getParent();
					rebalancing += singleRightRotation(currentNode, 3); 
					rebalancing += singleLeftRotation(parent, 2);
					} 
			}
		}
		return rebalancing;	
	}
	
	/**
	 * private void insertUpdate(IAVLNode currentNode, int k)
	 * 
	 * update the size and height of the inserted node and the rest of the nodes up to the root
	 * update the minimum and maximum of the tree if necessary
	 * complexity: O(logn).
	 */
	private void insertUpdate(IAVLNode currentNode, int k) {
	
		if ((this.getMin() == null) || (k < this.getMin().getKey())) { // update minimum 
			this.setMin(currentNode);;
		}
		if ((this.getMax() == null) || (k > this.getMax().getKey())) { // update maximum
			this.setMax(currentNode);
		}
		while (currentNode.getParent() != null) { 
			update(currentNode.getParent()); // updates the size and the height of the node
			currentNode = currentNode.getParent();
		}
		
   }
   /**
    * private int singleLeftRotation(IAVLNode node, int caseNumber)
    * 
    * rotate the received node once to the left
    * return the number of rebalancing: 1 for the rotation itself and the rest for the promote and demote
    * updates the size and the height of the affected nodes
    * the int caseNumber states the promotion and demotion operations:
    * case 1 - demote the node twice
    * case 2 - demote the node and promote his right child
    * case 3 - demote the node once
    * complexity: O(1).
    */
   private int singleLeftRotation(IAVLNode node, int caseNumber) { 	   
	   IAVLNode r = node.getRight();
	   IAVLNode rl = r.getLeft();
	   
	   if (node.getParent() != null) { // node is not the root
		   if (((AVLNode)node).isLeftChild()) {
			   node.getParent().setLeft(r);
		   }
		   else { // node is a right child
			   node.getParent().setRight(r);
		   }
		   r.setParent(node.getParent());
		   }
	   
	   else { // the node is the root
		   this.setRoot(r);
		   r.setParent(null);
	   }
		   
	   r.setLeft(node);
	   node.setParent(r);
	   node.setRight(rl);
	   rl.setParent(node);
	  
	   // updates size and height
	   update(rl);
	   update(node);
	   update(r);  
	   
	   // promotions and demotions according to the case number
	    if (caseNumber == 1) {
	    	demote(node);
	    	demote(node);
	    	return 3;
	    } 
	    else {
	    	if (caseNumber == 2) {
	    		demote(node);
	    		promote(r);
	    		return 3;
	    	}
	    }
	    // case 3
	    demote(node);
	    return 2;
   }
   
   /**
    * private int singleRightRotation(IAVLNode node, int caseNumber)
    * 
    * rotate the received node once to the right
    * return the number of rebalancing: 1 for the rotation itself and the rest for the promote and demote
    * updates the size and the height of the affected nodes
    * the int caseNumber states the promotion and demotion operations:
    * case 1 - demote the node twice
    * case 2 - demote the node and promote his right child
    * case 3 - demote the node once
    * complexity: O(1).
    */
   private int singleRightRotation(IAVLNode node, int caseNumber) { 
	   IAVLNode l = node.getLeft();
	   IAVLNode lr = l.getRight();
	   
	   if (node.getParent() != null) { // node is not the root
		   if (((AVLNode)node).isLeftChild()) {
			   node.getParent().setLeft(l);
		   }
		   else { // node is a right child
			   node.getParent().setRight(l);
		   }
		   l.setParent(node.getParent());
		   }
	 
	   else { // the node is the root
		   this.setRoot(l);
		   l.setParent(null);
	   }
	   l.setRight(node);
	   node.setParent(l);
	   node.setLeft(lr);
	   lr.setParent(node);
	   
	   // update size and height
	   update(lr);
	   update(node);
	   update(l);	 
	  

	   // promotions and demotions
	   if (caseNumber == 1) {
	    	demote(node);
	    	demote(node);
	    	return 3;
	    } else {
	    	if (caseNumber == 2) {
	    		demote(node);
	    		promote(l);
	    		return 3;
	    	}
	    }
	    // case 3
	    demote(node);
	    return 2;
   }
   
   /**
    * checks if promote is legal
    * return true if the right or left rank differences of the node are greater/ equals 2
    * otherwise returns false
    * complexity: O(1).
    */
   private boolean canPromote(IAVLNode node) {
	  if ((rankDifferenceLeft(node) >= 2) || (rankDifferenceRight(node) >= 2)) {
		  return false;
	  }
	  return true;
   }
   
   /**
    * promote the node's rank by 1
    * complexity: O(1).
    */
   private void promote(IAVLNode node) {
	   int updatedRank = ((AVLNode)node).getRank() + 1; 
	   ((AVLNode)node).setRank(updatedRank);
   }
   
   /**
    * demote the node's rank by 1
    * complexity: O(1).
    */
   private void demote(IAVLNode node) { 
	   int updatedRank = ((AVLNode) node).getRank() - 1; 
	   ((AVLNode)node).setRank(updatedRank); 
   }
   /**
    * update the size and the height of the node
    * using only the right and left children of the node
    * complexity: O(1).
    */
   private void update(IAVLNode parent) {
	   if (!parent.isRealNode()) { // no need to update a virtual node
		   return; 
	   }
	   
	   ((AVLNode)parent).setSize(((AVLNode) parent.getLeft()).getSize()+((AVLNode) parent.getRight()).getSize() + 1); // update size
	   int rightHeight = parent.getRight().getHeight();
	   int leftHight = parent.getLeft().getHeight();
	   ((AVLNode)parent).setHeight((Math.max(rightHeight, leftHight)) + 1); // update height
}

/**
    * returns true if 'node' if 1,1 or 1,2 or 2,1
    * returns false otherwise
    * complexity: O(1).
    */
   private boolean isBalanced(IAVLNode node) {
	  int rankDifferenceLeft = rankDifferenceLeft(node);
	  int rankDifferenceRight = rankDifferenceRight(node);
	  if ((rankDifferenceLeft == 1 && rankDifferenceRight == 1) || (rankDifferenceLeft == 1 && rankDifferenceRight == 2) || (rankDifferenceLeft == 2 && rankDifferenceRight == 1)) {
		  return true;
	  }
	  return false;
   }
   
   /**
    * returns the rank difference between 'node' and his left child
    * complexity: O(1).
    */
   private int rankDifferenceLeft(IAVLNode node) {
	   int nodeRank = ((AVLNode)node).getRank();
	   AVLNode leftChild = (AVLNode)(node.getLeft());
	   int leftChildRank = leftChild.getRank();
	   return (nodeRank - leftChildRank);
	   
   }
   
   /**
    * returns the rank difference between 'node' and his right child
    * complexity: O(1).
    */
   private int rankDifferenceRight(IAVLNode node) {
	   int nodeRank = ((AVLNode)node).getRank();
	   AVLNode rightChild = (AVLNode)(node.getRight());
	   return (nodeRank - rightChild.getRank());
   }

   
   /**
    * private IAVLNode treePosition(IAVLNode node, int key)
    *
	* search for the place of insertion of a node with key 'key' at AVLTree with a root 'node'
	* pre condition: 'node' is not null
	* returns null if 'node' is a virtual Node
	* return the node itself is the key is already exist is the tree
	* complexity: O(logn).
    */
   private IAVLNode treePosition(IAVLNode node, int key) {
	   IAVLNode position = null;  // not suppose to return it if 'node' is not a virtual Node
	   while (node.isRealNode()) {
		   position = node;
		   if (key == node.getKey()) {
			   return node;
		   }
		   if (key < node.getKey()) {
			   node = node.getLeft();
		   }
		   else {
			   node = node.getRight();
		   }
	   }
	   return position;
   }
   /**
    * private void treeInsert(IAVLNode root, int key, String value)
    *
	* inserts an item with key k and info i to AVL tree with root 'root'.
	* root is not null.
	* the tree does not remain a valid AVL tree.
	* returns null if an item with key 'k' already exists in the tree. 
	* otherwise, returns the node that was inserted.
	* complexity: O(logn).
    */
   private IAVLNode treeInsert(IAVLNode root, int k, String i) {
	   IAVLNode position = treePosition(root, k); // finds the position of insertion
	   AVLNode nodeToInsert = new AVLNode(k, i, position); // create a new node with key k, value i and parent position
	   if (k == position.getKey()) { // the key is already in the tree
		   return null;
	   }
	   if (k < position.getKey()) {
		   position.setLeft(nodeToInsert); // insertion as a left child
	   }
	   if (k > position.getKey()) {
		   position.setRight(nodeToInsert); // insertion as a right child
	   }
	   return nodeToInsert;
   }

  /**
   * public int delete(int k)
   *
   * deletes an item with key k from the binary tree, if it is there;
   * the tree must remain valid (keep its invariants).
   * returns the number of rebalancing operations, or 0 if no rebalancing operations were needed.
   * demotion/rotation - counted as one rebalnce operation, double-rotation is counted as 2.
   * returns -1 if an item with key k was not found in the tree.
   * complexity: O(logn).
   */
   public int delete(int k) {
	   if (empty()) { // tree is empty, the key is not in the tree
		   return -1;
	   }
	   IAVLNode nodeToDelete = treePosition(root, k); // finds a pointer to the node we need to delete
	   if (k != nodeToDelete.getKey()) { // the key is not in the tree
		   return -1; 
	   }
	   
	   int rebalancing = 0;
	   IAVLNode deletedNodeParent = treeDelete(nodeToDelete); // delete the node and return it's parent, do not rebalance the tree
	   if (deletedNodeParent != null) {
		   rebalancing = deleteRebalancing(deletedNodeParent); // rebalance the tree and return the number of rebalancing operations
		 	  
		   while (deletedNodeParent != null) { // update size and height from the deleted node's parent up to the root
					update(deletedNodeParent);
					deletedNodeParent = deletedNodeParent.getParent(); // move towards the root 
		 	  }
	   }
	   else { // deletedNodeParent = null -> the root was deleted
		   if (empty()) {
			   return 0;
		   }
	   }
	 	  if (k == this.getMin().getKey()) { // update min 
	 		  this.setMin(treeMin(root)); 
	 	  }
	 	  if (k == this.getMax().getKey() ) { // update max
	 		  this.setMax(treeMax(root));;
	 	  } 
	   return rebalancing;
   }
   
  
   /**
    * public IAVLNode treeMin(IAVLNode node)
    * 
    * return the node with the smallest key at subtree with root 'node'
    * complexity: O(logn).
    */
   
   public IAVLNode treeMin(IAVLNode node) {
	   IAVLNode current = node;
	   while (current.getLeft().isRealNode()) {
		   current = current.getLeft();
	   }
	   return current;
   }
   
   /**
    * public IAVLNode treeMax(IAVLNode node)
    * 
    * return the node with the biggest key at subtree with root 'node'
    * complexity: O(logn).
    */
   public IAVLNode treeMax(IAVLNode node) {
	   while (node.getRight().isRealNode()) {
		   node = node.getRight();
	   }
	   return node;
   }
   /**
    * private IAVLNode treeDelete(IAVLNode nodeToDelete, int k)
    * 
    * delete a node and return it's parent 
    * complexity: O(logn).
    */
   private IAVLNode treeDelete(IAVLNode nodeToDelete) { //the deleting part of delete, nodeToDelte is not null
	   	   
	   if (((AVLNode)nodeToDelete).isBinary()) { 
		   IAVLNode successor = successor(nodeToDelete); // find the successor of the node
		   replace(nodeToDelete, successor); // replace the node and his successor
		// after the replacement the node we need to delete is a leaf or an unary node		   
	   		}
	   
	   if (((AVLNode)nodeToDelete).isLeaf()) {
		   IAVLNode parent = deleteLeaf(nodeToDelete); // delete a leaf and returns it's parent
		   return parent;
	   }
	   
	   else { //unary node
		   IAVLNode parent = deleteUnary(nodeToDelete); // delete an unary node and returns it's parent
		   return parent;
	   }
	}
   /**
    * private IAVLNode successor(IAVLNode node)
    * 
    * finds the successor of a node:
    * if the node have a right child - the successor is the minimum of the right subtree
    * else, go up the tree until the first turn right
    * complexity: O(logn).
    */
   private IAVLNode successor(IAVLNode node) {
	   if (node.getRight().isRealNode()) { // node has a right child
		   node = node.getRight();
		   while (node.getLeft().isRealNode()) { // find the minimum of the right sub-tree 
			   node = node.getLeft();
		   }   
		   return node;
	   }
	   IAVLNode parent = node.getParent(); // node has no right child
	   while ((parent != null) && (node == parent.getRight())) {
		  node = parent;
		  parent = node.getParent();
	   }
	   return parent;
   }
/**
 * private void replace(IAVLNode nodeToDelete, IAVLNode successor)
 *  
 *  replacing between nodeToDelete and his successor
 *  complexity: O(1).
 */
   private void replace(IAVLNode nodeToDelete, IAVLNode successor) { 
	   IAVLNode p = successor.getParent();
	   IAVLNode pn = nodeToDelete.getParent();
	   IAVLNode r = successor.getRight();
	   IAVLNode l = successor.getLeft();

	   if (nodeToDelete.getParent() != null) { // node is not the root 
    	   if (((AVLNode) nodeToDelete).isLeftChild()) {
    		   nodeToDelete.getParent().setLeft(successor);
    	   } else { // right child
    		   nodeToDelete.getParent().setRight(successor);
    	   }
       } else { // the node we need to delete is the root
    	   this.setRoot(successor); // make the successor the root of the tree
    	   successor.setParent(null);
       }
	   if (nodeToDelete.getRight().getKey() == successor.getKey()) { // the successor is the node's right child
		   successor.setParent(nodeToDelete.getParent());
		   nodeToDelete.setParent(successor);
		   successor.setRight(nodeToDelete);
		   successor.setLeft(nodeToDelete.getLeft());
		   successor.getLeft().setParent(successor);
		   nodeToDelete.setRight(r);
		   nodeToDelete.getRight().setParent(nodeToDelete);
		   nodeToDelete.setLeft(l);
		   nodeToDelete.getLeft().setParent(nodeToDelete);
	   } 
	   
	   else {  // the successor is not the node's right child
	   successor.setLeft(nodeToDelete.getLeft()); 
	   successor.setRight(nodeToDelete.getRight()); 
	   successor.getLeft().setParent(successor);
	   successor.getRight().setParent(successor);
	   p.setLeft(nodeToDelete);
	   nodeToDelete.setParent(p);
	   r.setParent(nodeToDelete);
	   nodeToDelete.setRight(r);
	   l.setParent(nodeToDelete);
	   nodeToDelete.setLeft(l);
	   //p.setParent(successor);
	   successor.setParent(pn);
   }
	   // update ranks
	  int successorRank = ((AVLNode) successor).getRank();
	  ((AVLNode)successor).setRank(((AVLNode)nodeToDelete).getRank());
	  ((AVLNode)nodeToDelete).setRank(successorRank);
   }
	   
	      
   /**
    * private IAVLNode deleteLeaf(IAVLNode node)
    * 
    * delete a leaf 'node' by connecting a virtual leaf instead
    * return the deleted leaf's parent
    * complexity: O(1).
    */
   private IAVLNode deleteLeaf(IAVLNode node) {
	   if (node.getParent() == null) { // node is the root
		   root = null;
		   return null;
	   } 
	   IAVLNode vitrualNode = ((AVLNode)node.getParent()).createVirtualNode((AVLNode)node.getParent()); // create a virtual node
	   IAVLNode parent = node.getParent();
	   if (((AVLNode) node).isLeftChild()) { // check if the node is a left child
		   node.getParent().setLeft(vitrualNode); // delete the left child
	   }
	   else { // the node is a right child
		   node.getParent().setRight(vitrualNode);
	   }
	   node.setParent(null); // disconnect the node that was deleted
	   return parent;	   
   }
   /**
    * private IAVLNode deleteUnary(IAVLNode node)
    * 
    * delete an unary node from the tree 
    * return the deleted node's parent
    * complexity: O(1).
    */
   private IAVLNode deleteUnary(IAVLNode node) {	   
	   if (node.getParent() == null) { // the node is the root
		   if (root.getLeft().isRealNode()) { // the root has a left child
			   root.getLeft().setParent(null);
			   root = root.getLeft(); 
			   
		   }
		   else { // the root has a right child
			   root.getRight().setParent(null);
			   root = root.getRight(); 
			   
		   }
		   return null;
	   }
	   else { // the node is not the root 
	   IAVLNode parent = node.getParent();
	   IAVLNode nodeChild;
	   if (node.getLeft().isRealNode()) {
		   nodeChild = node.getLeft();
	   }
	   else {
		   nodeChild = node.getRight();
	   }
	   
	   if (((AVLNode) node).isLeftChild()) { // check if the node is a left child
		   node.getParent().setLeft(nodeChild); // connect the parent to the node's child
		   node.getParent().getLeft().setParent(node.getParent()); // connect the new child to the parent
	   }
	   else { // the node is a right child
		   node.getParent().setRight(nodeChild); 
		   node.getParent().getRight().setParent(node.getParent()); // connect the new child to the parent
	   }
	   node.setParent(null); // disconnect the node we deleted
	   return parent;		   
   }
   }
   /**
    * private int deleteRebalancing(IAVLNode deletedNodeParent)
    * 
    * rebalance the tree after the deletion on 'deletedNodeParent' child
    * return the number of rebalancing (promote, demote and rotations)
    * complexity: O(logn).
    */
   private int deleteRebalancing(IAVLNode deletedNodeParent) {
	   int rebalancing = 0;
	   while ((deletedNodeParent != null) && (!isBalanced(deletedNodeParent))) {	
		   // while the tree is not balanced and we are not at the root
		   
		   if ((rankDifferenceLeft(deletedNodeParent) == 2) && (rankDifferenceRight(deletedNodeParent) == 2)) { // node is 2,2
			   demote(deletedNodeParent); // node is 1,1
			   rebalancing++;
			   deletedNodeParent = deletedNodeParent.getParent(); // move towards the root

		   } 
		   else {
		   if ((rankDifferenceLeft(deletedNodeParent) == 3) && (rankDifferenceRight(deletedNodeParent) == 1)) { // node is 3,1 - can't demote
			  
			   if ((rankDifferenceLeft(deletedNodeParent.getRight()) == 1) && (rankDifferenceRight(deletedNodeParent.getRight()) == 1) ) { // node is 1,1 -> single L rotation
				   rebalancing += singleLeftRotation(deletedNodeParent, 2);
				   return rebalancing; // the tree is balanced, no need for more checks
				   
			   } else { if ((rankDifferenceLeft(deletedNodeParent.getRight()) == 1) && (rankDifferenceRight(deletedNodeParent.getRight()) == 2)) { // 1,2 -> RL rotation
					   rebalancing += singleRightRotation(deletedNodeParent.getRight(), 2);
					   rebalancing += singleLeftRotation(deletedNodeParent, 1);
					   deletedNodeParent = deletedNodeParent.getParent().getParent();
	
				   	} else { if ((rankDifferenceLeft(deletedNodeParent.getRight()) == 2) && (rankDifferenceRight(deletedNodeParent.getRight()) == 1)) { // 2,1 -> single L rotation
				   		rebalancing += singleLeftRotation(deletedNodeParent, 1);
				   		deletedNodeParent = deletedNodeParent.getParent().getParent();
				   		
			   }    
			   }
		   }
	   }
		   else { // can't demote - symmetric cases
			   if ((rankDifferenceLeft(deletedNodeParent) == 1) && (rankDifferenceRight(deletedNodeParent) == 3)) { // 1,3
				  
				   if ((rankDifferenceLeft(deletedNodeParent.getLeft()) == 1) && (rankDifferenceRight(deletedNodeParent.getLeft()) == 1) ) { // node is 1,1 -> single R rotation
					   rebalancing += singleRightRotation(deletedNodeParent, 2);
					   return rebalancing; // the tree is balanced, no need for more checks
					   
				   } else { if ((rankDifferenceLeft(deletedNodeParent.getLeft()) == 1) && (rankDifferenceRight(deletedNodeParent.getLeft()) == 2)) { // 1,2 -> single R rotation
						   rebalancing += singleRightRotation(deletedNodeParent, 1);
						   deletedNodeParent = deletedNodeParent.getParent().getParent(); 
						 
					   	} 
				   else {
					   if ((rankDifferenceLeft(deletedNodeParent.getLeft()) == 2) && (rankDifferenceRight(deletedNodeParent.getLeft()) == 1)) { // 2,1 -> LR rotation
					   		rebalancing += singleLeftRotation(deletedNodeParent.getLeft(), 2);
					   		rebalancing += singleRightRotation(deletedNodeParent, 1);
					   		deletedNodeParent = deletedNodeParent.getParent().getParent();
				
				   }    
				   }
			   }
			   }
		   }
		   }
		   
	   }
	   return rebalancing;
   }
   
   
   /**
    * public String min()
    *
    * Returns the info of the item with the smallest key in the tree,
    * or null if the tree is empty
    * complexity: O(1).
    */
   
   public String min() {
	   if (empty()) {
		   return null;
	   }
	   return this.getMin().getValue() ;
   }

   /**
    * public String max()
    *
    * Returns the info of the item with the largest key in the tree,
    * or null if the tree is empty
    * complexity: O(1).
    */
   public String max() {
	   if (empty()) {
		   return null;
	   }
	   return this.getMax().getValue() ;
   }

  /**
   * public int[] keysToArray()
   *
   * Returns a sorted array which contains all keys in the tree,
   * or an empty array if the tree is empty.
   * complexity: O(n).
   */
  public int[] keysToArray()
  {
	  if (empty()) { 
		  return new int[0]; // returns an empty array
	  }
	  
	  List<Integer> keysArray = new ArrayList<>();
	  IAVLNode currentNode = getRoot();
	  List<Integer> tmp = recKeysToArray(currentNode, keysArray);
	  int[] result = new int[tmp.size()];
	  for(int i = 0; i < tmp.size(); i++) {
		  result[i] = tmp.get(i);
	  }
	  return result;
  }
  private List<Integer> recKeysToArray(IAVLNode node, List<Integer> keysArray) {
	  if (node.isRealNode()) {
		  recKeysToArray(node.getLeft(), keysArray);
		  keysArray.add(node.getKey()); 
		  recKeysToArray(node.getRight(), keysArray);
	  }
	  
	  return keysArray;
  }
  

  /**
   * public String[] infoToArray()
   *
   * Returns an array which contains all info in the tree,
   * sorted by their respective keys,
   * or an empty array if the tree is empty.
   * complexity: O(n).
   */
  public String[] infoToArray()
  {
	  if (empty()) { 
		  return new String[0]; // returns empty array
	}
	  List<String> infoArray = new ArrayList<>();
	  IAVLNode currentNode = getRoot();
	  List<String> tmp = recInfoToArray(currentNode, infoArray);
	  String[] result = new String[tmp.size()];
	  for(int i = 0; i < tmp.size(); i++) {
		  result[i] = tmp.get(i);
	  }
	  return result;
  }
  private List<String> recInfoToArray(IAVLNode node, List<String> infoArray) {
	  if (node.isRealNode()) {
		  recInfoToArray(node.getLeft(), infoArray);
		  infoArray.add(node.getValue()); 
		  recInfoToArray(node.getRight(), infoArray);
	  }
	  
	  return infoArray;
  }
  


   /**
    * public int size()
    *
    * Returns the number of nodes in the tree.
    *
    * precondition: none
    * postcondition: none
    * complexity: O(1).
    */
   public int size()
   {
	   if (empty()) {
		   return 0;
	   }
	   return ((AVLNode)getRoot()).getSize(); // the size of the tree is the size of the root
   }
   
     /**
    * public int getRoot()
    *
    * Returns the root AVL node, or null if the tree is empty
    *
    * precondition: none
    * postcondition: none
    * complexity: O(1).
    */
   public IAVLNode getRoot()
   {
	   return this.root;
   }
   /**
    * private String recSearchNode(int k, IAVLNode node)
    *
    * returns the node with key k 
    	* precondition:  search(x) != null
	* complexity: O(logn).
    */  
   private IAVLNode recSearchNode(int k, IAVLNode node) {
 	  while (node.isRealNode()) {
 		  if (node.getKey() == k) {
 			  return node;
 		  }
 		  else {
 		  if (node.getKey() < k) {
 			  return recSearchNode(k, node.getRight());
 		  }
 		  else {
 		  if (node.getKey() > k) {
 			  return recSearchNode(k, node.getLeft());
 		  }
 		  }
 	  }
 	  }
 	  return null;
   }
   
   /**
    * private IAVLNode resetNode (IAVLNode node)
    * returns a new node identical to the one given with pointers to virtual leaves
    * complexity: O(1).
    */
   private IAVLNode resetNode (IAVLNode node) {
	   return new AVLNode(node.getKey(), node.getValue(), null, true);
   }
   
   /**
    * private AVLTree createSmallerTree (IAVLNode root)
    * returns subtree with root "root" 
    *  tree.max will be the correct max
    *  tree.min will be this.min
    *  postcondition:
    *  		tree's min might not be tree's real min
    *  complexity:
    *  		let h be root's subtree's height.
    *  		complexity = treeMax complexity = O(logh).
    */
   private AVLTree createSmallerTree (IAVLNode root) {
	   AVLTree tree =new AVLTree();
	   tree.setRoot(root);  
	   tree.setMax(treeMax(root));
	   tree.setMin(this.getMin());
	   root.setParent(null);
	   return tree;
   }
   
   /**
    * private AVLTree createLargerTree (IAVLNode root)
    * returns subtree with root "root" 
    *  tree.min will be the correct min
    *  tree.max will be this.max
    *  *  postcondition:
    *  		tree's max might not be tree's real max
    *  complexity:
    *  		let h be root's subtree's height.
    *  		complexity = treeMin complexity = O(logh).
    */
   private AVLTree createLargerTree (IAVLNode root) {
	   AVLTree tree =new AVLTree();
	   tree.setRoot(root);  
	   tree.setMax(this.getMax());
	   tree.setMin(treeMin(root));
	   root.setParent(null);
	   return tree;
   }
      
     /**
    * public string split(int x)
    *
    * splits the tree into 2 trees according to the key x. 
    * Returns an array [t1, t2] with two AVL trees. keys(t1) < x < keys(t2).
	  * precondition: search(x) != null (i.e. you can also assume that the tree is not empty)
    * postcondition: none
    * complexity = O(logn).
    */   
   public AVLTree[] split(int x)
   {
	   AVLTree t1 = new AVLTree();
	   AVLTree t2 = new AVLTree();
	   IAVLNode nodeToSplit = this.recSearchNode(x,this.getRoot());// find node with key x
	   if (nodeToSplit.getLeft().isRealNode()) {// t1 is x's left subtree
		   t1=this.createSmallerTree(nodeToSplit.getLeft());
	   }
	   else if (!(nodeToSplit==this.getMin())) {//smaller tree min will be this.min, max will be the first node to join to t1
		   t1.setMin(this.getMin());
		   t1.setMax(this.getMin());
	   }
	   if (nodeToSplit.getRight().isRealNode()) {// t2 is x's right subtree
		   t2=this.createLargerTree(nodeToSplit.getRight());
	   }
	   else if (!(nodeToSplit==this.getMax())) {//larger tree max will be this.max, min will be the first node to join to t2
		   t2.setMax(this.getMax());
		   t2.setMin(this.getMax());
	   }
	   while (nodeToSplit.getParent()!=null) {
		   if (nodeToSplit.getParent().getRight()==nodeToSplit) {// nodeToSplit.getParent() and its' left subtree are smaller than x
			   AVLTree leftSubTree= new AVLTree();
			   IAVLNode xNodeToJoin = nodeToSplit.getParent();
			   leftSubTree.setRoot(nodeToSplit.getParent().getLeft());
			   leftSubTree.getRoot().setParent(null);// delete parent to use only the left subtree
			   t1.join(resetNode(xNodeToJoin), leftSubTree); //join with t1
		   }
		   else{// nodeToSplit.getParent() and its' right subtree are larger than x
			   AVLTree rightSubTree= new AVLTree();
			   IAVLNode xNodeToJoin = nodeToSplit.getParent();
			   rightSubTree.setRoot(nodeToSplit.getParent().getRight());
			   rightSubTree.getRoot().setParent(null);// delete parent to use only the right subtree
			   t2.join(resetNode(xNodeToJoin), rightSubTree); //join with t2
		   }
		   nodeToSplit = nodeToSplit.getParent(); //continue until reached the root
	   }
	   AVLTree[] result = new AVLTree[2];
	   result[0]=t1;
	   result[1]=t2;
	   return result; 
   }
   /**
    * public join(IAVLNode x, AVLTree t)
    *
    * joins t and x with the tree. 	
    * Returns the complexity of the operation (|tree.rank - t.rank| + 1).
	  * precondition: keys(x,t) < keys() or keys(x,t) > keys(). t/tree might be empty (rank = -1).
    * postcondition: none
    * complexity = |tree.rank - t.rank| + 1
    */   
   public int join(IAVLNode x, AVLTree t)
   {
	   int complexity;
	   IAVLNode nodeToJoin;
	   if ((this.empty())&&(t.empty())) {//return x
		   this.setRoot(x);
		   complexity = 1;

	   }
	   else if (this.empty()){//only tree is empty 
		   complexity = ((AVLNode)t.getRoot()).getRank()+2;//=|tree.rank --1| + 1
		   t.insert(x.getKey(),x.getValue());
		   this.setRoot(t.getRoot());
		   
	   }
	   else if (t.empty()){//symetric case, only t is empty
		   complexity = ((AVLNode)this.getRoot()).getRank()+2;//=|-1 - t.rank| + 1
		   this.insert(x.getKey(), x.getValue());
		   
	   }
	   else {
		   complexity = Math.abs(((AVLNode)t.getRoot()).getRank()-((AVLNode)this.getRoot()).getRank())+1;   
		   if (x.getKey()<this.getRoot().getKey()) {//the order is t -> x -> tree
			   if (((AVLNode) t.getRoot()).getRank()<((AVLNode) this.getRoot()).getRank()) {//t has smaller rank, travel down tree
				   nodeToJoin=findNodeLeft(this,((AVLNode) t.getRoot()).getRank()+1);
				   joinToTheLeft (nodeToJoin,x,t);
			   }
			   else if (((AVLNode) t.getRoot()).getRank()>((AVLNode) this.getRoot()).getRank()) {//tree has smaller rank, travel down t
				   nodeToJoin=findNodeRight(t,((AVLNode) this.getRoot()).getRank()+1);
				   joinToTheRight (nodeToJoin,x,this);
				   this.setRoot(t.getRoot());
			   }
			   else {//both trees have the same rank
				   joinEqualRanks(t, x, this);
				   this.setRoot(x);;
			   }
		   }
		   else {//the order is tree -> x -> t
			   if (((AVLNode) t.getRoot()).getRank()<((AVLNode) this.getRoot()).getRank()) {//t has smaller rank, travel down tree
				   nodeToJoin=findNodeRight(this,((AVLNode) t.getRoot()).getRank()+1);
				   joinToTheRight (nodeToJoin,x,t);
			   }
			   else if (((AVLNode) t.getRoot()).getRank()>((AVLNode) this.getRoot()).getRank()) {//tree has smaller rank, travel down t
				   nodeToJoin=findNodeLeft(t,((AVLNode) this.getRoot()).getRank()+1);
				   joinToTheLeft (nodeToJoin,x,this);
				   this.setRoot(t.getRoot());
			   }
			   else {//both trees have the same rank
				   joinEqualRanks(this, x, t);
				   this.setRoot(x);
			   } 
		   }
	   }
	   update(x);
	   ((AVLNode)x).setRank(x.getHeight());//subtree from x downwards is legal AVL -> x.rank=x.height
	   this.joinTreeFieldsUpdate(t, x);// update min, max
	   if ((this.getRoot()!=x)&&(x.getParent()!=null)) {// may need to rebalance and update up to the root
		   this.joinUpdateAndRebalance(x);//update IAVLNode fields and rebalance

	   }
	   else {//both of x's subtrees are AVL of the same rank therefore no need to update them or rebalance, only update x
		   this.update(x);
	   }

	   return complexity; 
   }
   
 /**
  * private IAVLNode findNodeLeft (AVLTree tree, int rank)
  * finds node in tree's leftmost subtree with rank <= "rank"
  * returns node
  	* preconditions: tree.root.rank>rank
  * postconditions: none
  * complexity = O(rank).
  */
   private IAVLNode findNodeLeft (AVLTree tree, int rank) {
	   IAVLNode currentNode=tree.getRoot();
	   while (((AVLNode)currentNode.getLeft()).getRank()>rank) {//travel to desired rank
		   currentNode=currentNode.getLeft();
	   }
	   return currentNode;
   }
   
   /**
    * private IAVLNode findNodeRight (AVLTree tree, int rank)
    * finds node in tree's rightmost subtree with rank <= "rank"
    * returns node
    	* preconditions: tree.root.rank>rank
    * postconditions: none
    * complexity = O(rank).
    */
     private IAVLNode findNodeRight (AVLTree tree, int rank) {
  	   IAVLNode currentNode=tree.getRoot();
  	   while (((AVLNode)currentNode.getRight()).getRank()>rank) {//travel to desired rank
  		   currentNode=currentNode.getRight();
  	   }
  	   return currentNode;
     }
   
     /**
      * private void joinToTheLeft (IAVLNode node, IAVLNode x, AVLTree tree)
      * joins x and tree to the left side of the node and it's son
      * preconditions: node.left != null
      * complexity = O(1).
      */
   private void joinToTheLeft (IAVLNode node, IAVLNode x, AVLTree tree) {
	   x.setLeft(tree.getRoot());
	   tree.getRoot().setParent(x);
	   x.setRight(node.getLeft());
	   x.getRight().setParent(x);
	   node.setLeft(x);
	   x.setParent(node);  
   }
   
   /**
    * private void joinToTheRight (IAVLNode node, IAVLNode x, AVLTree tree)
    * joins x and tree to the right side of the node and it's son
    * preconditions: node.right != null
    * complexity = O(1).
    */
   private void joinToTheRight (IAVLNode node, IAVLNode x, AVLTree tree) {
	   x.setRight(tree.getRoot());
	   tree.getRoot().setParent(x);
	   x.setLeft(node.getRight());
	   x.getLeft().setParent(x);
	   node.setRight(x);
	   x.setParent(node); 
   }
   
   /**
    * private void joinEqualRanks (AVLTree leftTree, IAVLNode x, AVLTree rightTree)
    * joins leftTree to the left of x, and rightTree to the right
    *  complexity = O(1).
    */
   private void joinEqualRanks (AVLTree leftTree, IAVLNode x, AVLTree rightTree) {
	   x.setLeft(leftTree.getRoot());
	   leftTree.getRoot().setParent(x);
	   x.setRight(rightTree.getRoot());
	   rightTree.getRoot().setParent(x);
	    
   }
   
   /**
    *private void joinTreeFieldsUpdate (AVLTree t, IAVLNode x)
    *
    *  updates the tree's AVLTree fields in O(1):
    	*  min
    	*  max
    *  complexity = O(1).
    */
   
   private void joinTreeFieldsUpdate (AVLTree t, IAVLNode x) {
	   if ((t.getMin()==null)&&(this.getMin()==null)) {//both trees were empty
		   this.setMin(x);
		   this.setMax(x);
	   }
	   else if (t.getMin()==null) {//only t was empty
		   this.setMin(findMinimalNode(this.getMin(),x));
		   this.setMax(findMaximalNode(this.getMax(),x));
	   }
	   else if (this.getMin()==null) {//only tree was empty
		   this.setMin(findMinimalNode(t.getMin(),x));
		   this.setMax(findMaximalNode(t.getMax(),x));
	   }
	   else {// both trees not empty
		   //min, max between tree and x
		   this.setMin(findMinimalNode(this.getMin(),x));
		   this.setMax(findMaximalNode(this.getMax(),x));
		   //min, max between updated tree and t
		   this.setMin(findMinimalNode(this.getMin(),t.getMin()));
		   this.setMax(findMaximalNode(this.getMax(),t.getMax()));
		   
	   }
   }
   
   /**
    * joinUpdateAndRebalance(IAVLNode node)
    * rebalances if necessary 
    * updates node's size and height using update(IAVL node)
    * complexity = O(|tree.rank - t.rank|+1).
    */
   private void joinUpdateAndRebalance(IAVLNode node) {
			   if ((rankDifferenceLeft(node)==1)&&(rankDifferenceRight(node)==1)) {//join private case
				   if ((rankDifferenceLeft(node.getParent())==2)&&(rankDifferenceRight(node.getParent())==0)) {//join private case
					   singleLeftRotation(node.getParent(), 2);
				   }
				   else if ((rankDifferenceLeft(node.getParent())==0)&&(rankDifferenceRight(node.getParent())==2)) {//join private case
					   singleRightRotation(node.getParent(), 2);
				   }
			   }
			   if ((node.getParent()!=null)&&!isBalanced(node.getParent())) {// parent node is not balanced
				   insertRebalance(node.getParent());  //same as insert
			   }
		   while (node!=null) {//update nodes
			   update(node);
			   node=node.getParent();
		   }
   }
   /**
    * private IAVLNode findMinimalNode (IAVLNode node1, IAVLNode node2)
    * returns node with minimal key
    * complexity = O(1).
    */
   private IAVLNode findMinimalNode (IAVLNode node1, IAVLNode node2) {
	   if (node1.getKey()<node2.getKey()) {
		   return node1;
	   }
	   return node2;
   }
   
   /**
    * private IAVLNode findMaximalNode (IAVLNode node1, IAVLNode node2)
    * returns node with maximal key
    * complexity = O(1).
    */
   private IAVLNode findMaximalNode (IAVLNode node1, IAVLNode node2) {
	   if (node1.getKey()<node2.getKey()) {
		   return node2;
	   }
	   return node1;
   }
   
	/**
	   * public interface IAVLNode
	   * ! Do not delete or modify this - otherwise all tests will fail !
	   */
	public interface IAVLNode{	
		public int getKey(); //returns node's key (for virtuval node return -1)
		public String getValue(); //returns node's value [info] (for virtuval node return null)
		public void setLeft(IAVLNode node); //sets left child
		public IAVLNode getLeft(); //returns left child (if there is no left child return null)
		public void setRight(IAVLNode node); //sets right child
		public IAVLNode getRight(); //returns right child (if there is no right child return null)
		public void setParent(IAVLNode node); //sets parent
		public IAVLNode getParent(); //returns the parent (if there is no parent return null)
		public boolean isRealNode(); // Returns True if this is a non-virtual AVL node
    	public void setHeight(int height); // sets the height of the node
    	public int getHeight(); // Returns the height of the node (-1 for virtual nodes)
		public int getSize();
		public int getRank();
	}
	

   /**
   * public class AVLNode
   *
   * If you wish to implement classes other than AVLTree
   * (for example AVLNode), do it in this file, not in 
   * another file.
   * This class can and must be modified.
   * (It must implement IAVLNode)
   * 
   * Complexity of all methods: O(1)
   */
  public class AVLNode implements IAVLNode{
	  
	  	private int key;
	  	private String value;
	  	private	IAVLNode left;
	  	private IAVLNode right;
	  	private IAVLNode parent;
	  	private int height;
	  	private int size;
	  	private int rank;
	  	
	  	public AVLNode(int key, String value, IAVLNode parent) { // constructor of real nodes
	  		this(key, value, parent, true);
	  	}
	  	
	  	private AVLNode(int key, String value, IAVLNode parent, boolean realNode) {
	  			this.key = key;
	  			this.value = value;
	  			this.parent = parent;
	  			
	  			if (realNode) {
	  			this.height = 0;
	  			this.size = 1;
	  			this.left = createVirtualNode(this);
	  			this.right = createVirtualNode(this);
	  			}
	  			else {
	  			this.height = -1;
	  			this.size = 0;
	  			this.left = null;
	  			this.right = null;
	  			this.rank = -1;
	  		}
	  	}
	  	
	  	public IAVLNode createVirtualNode(IAVLNode parent) {
	  		return new AVLNode(-1, null, parent, false);
	  	}
	  	
		public int getKey()
		{
			return this.key;
		}
		public String getValue()
		{
			return this.value; 
		}
		public void setLeft(IAVLNode node)
		{
			this.left = node; 
		}
		public IAVLNode getLeft()
		{
			return this.left;
		}
		public void setRight(IAVLNode node)
		{
			this.right = node; 
		}
		public IAVLNode getRight()
		{
			return this.right; 
		}
		public void setParent(IAVLNode node)
		{
			this.parent = node; 
		}
		public IAVLNode getParent()
		{
			return this.parent; 
		}
		// Returns True if this is a non-virtual AVL node
		public boolean isRealNode()
		{
			if (getLeft() == null) {
				return false;
			}
			return true;
		}
		public void setHeight(int height) {
			this.height = height; 
		}
		public int getHeight() {
			return this.height; 
		}
		
		public int getSize() {
			return this.size;
		}
		public void setSize(int size) {
			this.size = size;
		}
		
		public int getRank() {
			return this.rank;
		}
		
		public void setRank(int rank) {
			this.rank = rank;
		}
		
		/**
		 * public boolean isLeaf()
		 * 
		 * returns true if both the right and the left children are virtual nodes
		 * otherwise, returns false
		 */
		 public boolean isLeaf() {
			   if ((!(this.getRight().isRealNode())) && (!(this.getLeft().isRealNode()))) {
				   return true;
			   }
			   return false;
		   }
		 
		/**
		 * public boolean isUnary()
		 * 
		 * returns true if the right or the left children are virtual nodes (and the other is real)
		 * otherwise, returns false
		 */		 
		 public boolean isUnary() {
			 if ((this.getLeft().isRealNode() && !(this.getRight().isRealNode())) || ((this.getRight().isRealNode()) && !(this.getLeft().isRealNode()))) {
				 return true;
			 }
			 return false;
		 }
		 /**
		  * public boolean isBinary()
		  * 
		  * return true if both the right and the left children are real nodes
		  * otherwise, returns false
		  */
		 public boolean isBinary() {
			 if ((this.getLeft().isRealNode()) && (this.getRight().isRealNode())) {
				 return true;
			 }
			 return false;
		 }
	
		 public boolean isLeftChild() {
			 if (this.getParent().getLeft().getKey() == this.getKey()) {
				 return true;
			 }
			 return false;
		 }
  }


}