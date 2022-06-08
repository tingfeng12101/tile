package com.yxm.library.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Game implements Serializable {
	private Map<String, String> properties = new HashMap<>();
	private GameNode rootNode;
	private int noMoves = 0;

	public void addProperty(String key, String value) {
		properties.put(key, value);
	}

	public String getProperty(String key) {
		return properties.get(key);
	}

	public String toString() {
		return properties.toString();
	}

	public void setRootNode(GameNode rootNode) {
		this.rootNode = rootNode;
	}

	public GameNode getRootNode() {
		return rootNode;
	}

	public int getNoMoves() {
		return noMoves;
	}

	public void setNoMoves(int noMoves) {
		this.noMoves = noMoves;
	}

	public void postProcess() {
		// count the moves
		GameNode node = getRootNode();
		do {
			if (node.isMove()) {
				setNoMoves(getNoMoves() + 1);
			}
		} while (((node = node.getNextNode()) != null));
	}

	public GameNode getFirstMove() {
		GameNode node = getRootNode();

		do {
			if (node.isMove())
				return node;
		} while ((node = node.getNextNode()) != null);

		return null;
	}

	public GameNode getLastMove() {
		GameNode node = getRootNode();
		GameNode rtrn = null;
		do {
			if (node.isMove()) {
				rtrn = node;
			}
		} while ((node = node.getNextNode()) != null);
		return rtrn;
	}
}