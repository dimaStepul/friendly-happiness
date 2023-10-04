package com.github.dimastepul.friendlyhappiness

class AmountsDirs(name: String) : Node(name) {
    val children = mutableMapOf<String, Node>()

    private fun traverseDir(directory: String): Node? {
        var node: Node = this
        for (dirName in splitDirectory(directory)) {
            if (node is AmountsDirs) {
                val next = node.children[dirName] ?: return null
                node = next
            } else {
                return null
            }
        }
        return node
    }

    fun isDirectoryExists(dir: String): Boolean {
        return traverseDir(dir) != null
    }

    fun countComponentsForDir(dir: String): AmountClassesFuncs? {
        val file = traverseDir(dir) as? AmountFiles ?: return null
        return file.amountClassesFuncs
    }

    fun setCountsForDirectory(directoryName: String, amountClassesFuncs: AmountClassesFuncs): AmountFiles {
        val directoryNames = splitDirectory(directoryName).toMutableList()
        val fileName = directoryNames.removeLast()
        var currentNode: AmountsDirs = this
        for (dirName in directoryNames) {
            val nextNode = currentNode.children[dirName]
            if (nextNode == null || nextNode !is AmountsDirs) {
                val newNext = AmountsDirs(dirName)
                currentNode.children[dirName] = newNext
                currentNode = newNext
            } else {
                currentNode = nextNode
            }
        }
        val file = AmountFiles(fileName, amountClassesFuncs)
        currentNode.children[fileName] = file
        return file
    }
}
