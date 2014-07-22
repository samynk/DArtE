/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.project;

/**
 * Defines the methods that every child in a project should support.
 * @author Koen Samyn
 */
public interface ProjectTreeNode {
    public boolean hasChildren();
    public ProjectTreeNode getProjectChild(int index);
    public int getIndexOfChild(ProjectTreeNode object);
    public ProjectTreeNode getProjectParent();
    public boolean isLeaf();
}
