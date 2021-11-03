package trie;

import java.util.ArrayList;


public class Trie {

	// prevent instantiation
	private Trie() {
	}

	/**
	 * Builds a trie by inserting all words in the input array, one at a time, in
	 * sequence FROM FIRST TO LAST. (The sequence is IMPORTANT!) The words in the
	 * input array are all lower case.
	 * 
	 * @param allWords Input array of words (lowercase) to be inserted.
	 * @return Root of trie with all words inserted from the input array
	 */
	public static TrieNode buildTrie(String[] allWords) {

		TrieNode rootNode = new TrieNode(null,
				new TrieNode(new Indexes(0, (short) 0, (short) (allWords[0].length() - 1)), null, null), null);

		for (int i = 0; i < allWords.length; i++) {

			TrieNode currentNode = rootNode.firstChild;
			TrieNode lastNode = rootNode;
			short startIndex = 0;

			insertWord(currentNode, lastNode, rootNode, allWords, startIndex, i);

		}

		return rootNode;
	}

	private static void insertWord(TrieNode currentNode, TrieNode lastNode, TrieNode rootNode, String[] allWords,
			short startIndex, int i) {
		while (currentNode != null) {
			if (allWords[i].charAt(startIndex) == allWords[currentNode.substr.wordIndex].charAt(startIndex)) {

				short endIndex = 0;
				for (short n = startIndex; n < allWords[i].length(); n++) {
					if (allWords[i].charAt(n) != allWords[currentNode.substr.wordIndex].charAt(n)) {
						break;
					}
					if (n > currentNode.substr.endIndex) {
						break;
					}
					endIndex = n;
				}
				if (endIndex == currentNode.substr.endIndex) {
					startIndex = (short) (endIndex + 1);
					lastNode = currentNode;
					currentNode = currentNode.firstChild;
					continue;
				} else {
					TrieNode newNode = new TrieNode(new Indexes(currentNode.substr.wordIndex, startIndex, endIndex),
							null, null);
					TrieNode childNode = new TrieNode(new Indexes(currentNode.substr.wordIndex, (short) (endIndex + 1),
							currentNode.substr.endIndex), null, null);
					TrieNode siblingNode = new TrieNode(
							new Indexes(i, (short) (endIndex + 1), (short) (allWords[i].length() - 1)), null, null);

					if (lastNode == rootNode || lastNode.firstChild == currentNode) {
						lastNode.firstChild = newNode;
					} else {
						lastNode.sibling = newNode;
					}

					childNode.sibling = siblingNode;
					newNode.firstChild = childNode;
					newNode.sibling = currentNode.sibling;

					if (currentNode.firstChild != null) {
						childNode.firstChild = currentNode.firstChild;
					}
					break;
				}
			} else if (currentNode.sibling == null
					&& allWords[i].charAt(startIndex) != allWords[currentNode.substr.wordIndex].charAt(startIndex)) {
				short endIndex = 0;

				for (short n = startIndex; n < allWords[i].length(); n++) {
					endIndex = n;
				}

				TrieNode newNode = new TrieNode(new Indexes(i, startIndex, endIndex), null, null);
				currentNode.sibling = newNode;
				break;

			}
			lastNode = currentNode;
			currentNode = currentNode.sibling;

		}
	}

	/**
	 * Given a trie, returns the "completion list" for a prefix, i.e. all the leaf
	 * nodes in the trie whose words start with this prefix. For instance, if the
	 * trie had the words "bear", "bull", "stock", and "bell", the completion list
	 * for prefix "b" would be the leaf nodes that hold "bear", "bull", and "bell";
	 * for prefix "be", the completion would be the leaf nodes that hold "bear" and
	 * "bell", and for prefix "bell", completion would be the leaf node that holds
	 * "bell". (The last example shows that an input prefix can be an entire word.)
	 * The order of returned leaf nodes DOES NOT MATTER. So, for prefix "be", the
	 * returned list of leaf nodes can be either hold [bear,bell] or [bell,bear].
	 *
	 * @param root     Root of Trie that stores all words to search on for
	 *                 completion lists
	 * @param allWords Array of words that have been inserted into the trie
	 * @param prefix   Prefix to be completed with words in trie
	 * @return List of all leaf nodes in trie that hold words that start with the
	 *         prefix, order of leaf nodes does not matter. If there is no word in
	 *         the tree that has this prefix, null is returned.
	 */
	public static ArrayList<TrieNode> completionList(TrieNode root, String[] allWords, String prefix) {
		TrieNode currentNode = root.firstChild;
		if (allWords.length == 0 || currentNode == null) {
			return null;
		}
		String result = "";
		ArrayList<TrieNode> nodesOfPrefixes = new ArrayList<>();

		while (currentNode != null) {
			if (prefix.charAt(currentNode.substr.startIndex) != allWords[currentNode.substr.wordIndex]
					.charAt(currentNode.substr.startIndex)) {
				currentNode = currentNode.sibling;
			} else {
				for (int i = currentNode.substr.startIndex; i <= currentNode.substr.endIndex; i++) {
					if (i == prefix.length()) {
						break;
					}
					if (prefix.charAt(i) != allWords[currentNode.substr.wordIndex].charAt(i)) {
						return null;
					}
					result += prefix.charAt(i);

					if (result.equals(prefix)) {
						break;
					}
				}
				if (result.equals(prefix)) {
					if (currentNode.firstChild == null) {
						nodesOfPrefixes.add(currentNode);
						break;
					}
					TrieNode rootNode = currentNode.firstChild;
					while (rootNode != null) {
						if (rootNode.firstChild != null) {
							nodesOfPrefixes = addToList(rootNode.firstChild, nodesOfPrefixes);
						} else {
							nodesOfPrefixes.add(rootNode);
						}
						rootNode = rootNode.sibling;
					}
					break;
				} else {
					currentNode = currentNode.firstChild;
					continue;
				}
			}
		}

		if (result == null || result == "" || !(result.equals(prefix))) {
			return null;
		}
		return nodesOfPrefixes;
	}

	private static ArrayList<TrieNode> addToList(TrieNode rootNode, ArrayList<TrieNode> nodesOfPrefixes) {
		while (rootNode != null) {
			if (rootNode.firstChild != null) {
				nodesOfPrefixes = addToList(rootNode.firstChild, nodesOfPrefixes);
			} else {
				nodesOfPrefixes.add(rootNode);
			}
			rootNode = rootNode.sibling;
		}
		return nodesOfPrefixes;
	}

	public static void print(TrieNode root, String[] allWords) {
		System.out.println("\nTRIE\n newNode");
		print(root, 1, allWords);
	}

	private static void print(TrieNode root, int indent, String[] words) {
		if (root == null) {
			return;
		}
		for (int i = 0; i < indent - 1; i++) {
			System.out.print("    ");
		}

		if (root.substr != null) {
			String pre = words[root.substr.wordIndex].substring(0, root.substr.endIndex + 1);
			System.out.println("      " + pre);
		}

		for (int i = 0; i < indent - 1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		if (root.substr == null) {
			System.out.println("root");
		} else {
			System.out.println(root.substr);
		}

		for (TrieNode currentNode = root.firstChild; currentNode != null; currentNode = currentNode.sibling) {
			for (int i = 0; i < indent - 1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(currentNode, indent + 1, words);
		}
	}
}