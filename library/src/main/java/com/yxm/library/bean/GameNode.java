package com.yxm.library.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


public class GameNode implements Comparable<GameNode>, Cloneable ,Serializable{
	private final GameNode parentNode;
	private final List<GameNode> children = new ArrayList<>();//如果用TreeMap,前面有相同节点会漏掉解答
	private final Set<GameNode> nextNodeS = new TreeSet<>();
	private final Map<String, String> properties = new HashMap<>();

	private int moveNo;
	private int visualDepth = 0;
	private GameNode nextNode = null;
	private GameNode prevNode = null;

	/**
	 * Constructs a new node with the argument as the parent.
	 * 
	 * @param parentNode
	 *            node to be the parent of the just created node.
	 */
	public GameNode(GameNode parentNode) {
		this.parentNode = parentNode;
	}

	public void addProperty(String key, String value) {
		properties.put(key, value);
	}

	public String getProperty(String key) {
		return properties.get(key);
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public boolean isMove() {
		return properties.get("W") != null || properties.get("B") != null;
	}

	public String getMoveString() {
		if (properties.get("W") != null) {
			return properties.get("W");
		} else if (properties.get("B") != null) {
			return properties.get("B");
		} else {
			throw new RuntimeException();
		}
	}

	public boolean hasLB() {
		return properties.get("LB") != null;
	}


	public boolean isWhite() {
		return properties.get("W") != null;
	}
	
	public boolean hasC() {
		return properties.get("C") != null;
	}

	public boolean isBlack() {
		return properties.get("B") != null;
	}

	public String getColor() {
		if (properties.get("W") != null)
			return "W";
		return "B";
	}

	public void addChild(GameNode node) {
		if (nextNode == null) {
			nextNode = node;
			nextNode.setVisualDepth(0);
			node.setPrevNode(this);
			return;
		}
		if (children.contains(node)) {
//			throw new RuntimeException("Node " + node + " already exists for "
//					+ this);
		}
		children.add(node);
	}

	public boolean hasChildren() {
		return children.size() > 0;
	}

	public List<GameNode> getChildren() {
		return children;
	}

	public Set<GameNode> getNextNodeS() {
		return nextNodeS;
	}

	public GameNode getParentNode() {
		return parentNode;
	}

	@Override
	public String toString() {
		return "Props: " + properties.toString() + " moveNo: " + moveNo
				+ " children: " + children.size() + " vdepth: " + visualDepth;
	}

	public void setMoveNo(int i) {
		this.moveNo = i;
	}

	public int getMoveNo() {
		return moveNo;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result
				+ ((children == null) ? 0 : children.hashCode());
		result = prime * result + moveNo;
		result = prime * result
				+ ((parentNode == null) ? 0 : parentNode.properties.hashCode());
		result = prime * result
				+ ((properties == null) ? 0 : properties.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GameNode other = (GameNode) obj;
		if (children == null) {
			if (other.children != null)
				return false;
		} else if (!children.equals(other.children))
			return false;
		if (moveNo != other.moveNo)
			return false;
		if (parentNode == null) {
			if (other.parentNode != null)
				return false;
		} else if (!parentNode.equals(other.parentNode))
			return false;
		if (properties == null) {
			if (other.properties != null)
				return false;
		} else if (!properties.equals(other.properties))
			return false;
		return true;
	}

	@Override
	public int compareTo(GameNode o) {
		if (this.visualDepth < o.visualDepth)
			return -1;

		if (this.visualDepth > o.visualDepth)
			return 1;

		if (this.moveNo < o.moveNo)
			return -1;

		if (this.moveNo > o.moveNo)
			return 1;

		// so the move no is the same and the depth is the same
		if (this.hashCode() < o.hashCode())
			return -1;
		else if (this.hashCode() > o.hashCode())
			return 1;

		return 0;
	}

	public void setVisualDepth(int visualDepth) {
		this.visualDepth = visualDepth;
	}

	public int getVisualDepth() {
		return visualDepth;
	}

	public GameNode getNextNode() {
		return nextNode;
	}

	public GameNode getPrevNode() {
		return prevNode;
	}

	public void setPrevNode(GameNode node) {
		this.prevNode = node;
	}
}
