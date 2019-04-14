package org.brainstorm.tree;

import java.util.Enumeration;
import javax.swing.tree.*;

/**
 * @author Francois Tadel
 */
public class BstTreeModel extends DefaultTreeModel{
    boolean m_filterIsActive = true;
    BstNodeFilter m_filterNode = null;
    
    public BstTreeModel(TreeNode root){
        super(root);
    }
    
    public boolean isActivatedFilter() {
        return m_filterIsActive;
    }
    public void activateFilter(boolean isActive) {
        m_filterIsActive = isActive;
    }
    public void setFilter(BstNodeFilter filterNode) {
        m_filterNode = filterNode;
    }

    public void fireDropEvent(){
        Object []path = new Object[1];
        path[0] = root;
        this.fireTreeNodesChanged(root, path, null, null);
    }
    
    // Reimplement builtin functions with node filter
    @Override
    public Object getChild(Object parent, int index) {
        if (m_filterIsActive && parent instanceof BstNode)
            return ((BstNode) parent).getChildAt(index, m_filterIsActive);
        else
            return ((TreeNode) parent).getChildAt(index);
    }

    public int getChildCount(Object parent) {
        if (m_filterIsActive && parent instanceof BstNode)
            return ((BstNode) parent).getChildCount(m_filterIsActive);
        else
            return ((TreeNode) parent).getChildCount();
    }
    
    // Filter functions
    private boolean matchesFilter(BstNode node) {
        // If no filter applied, match all
        if (m_filterNode == null)
            return true;
        else
            return m_filterNode.matchesFilter(node);
    }
    
    public void applyFilter() {
        applyFilter((BstNode) root);
    }
    private boolean applyFilter(BstNode node) {
        if (node == null)
            return false;
        
        boolean isVisible = matchesFilter(node);
        int numChildren = node.getChildCount();
        
        // If a child matches the filter, keep the parent as well
        for (int iChild = 0; iChild < numChildren; iChild++) {
            isVisible |= applyFilter((BstNode) node.getChildAt(iChild));
        }
        
        node.setVisible(isVisible);
        return isVisible;
    }

}
