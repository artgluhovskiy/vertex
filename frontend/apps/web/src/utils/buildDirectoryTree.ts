import type { Directory, Note } from '@synapse/types/domain';
import type { DirectoryTreeNode, BuildTreeOptions } from '@/types/directoryTree';

/**
 * Builds a hierarchical tree structure from flat directory and note lists.
 *
 * Algorithm:
 * 1. Count notes per directory (O(n) where n = notes)
 * 2. Create tree nodes for all directories (O(m) where m = directories)
 * 3. Connect parent-child relationships (O(m))
 * 4. Sort children alphabetically (O(m log m))
 * 5. Calculate recursive note counts (O(m))
 *
 * Total complexity: O(n + m log m)
 *
 * @param directories - Flat list of all directories
 * @param rootDirectory - The ROOT "Folders" directory (parentId = null)
 * @param notes - List of all notes (used to count notes per directory)
 * @param options - Options for building the tree
 * @returns The complete directory tree with ROOT as the root node
 */
export function buildDirectoryTree(
  directories: Directory[],
  rootDirectory: Directory,
  notes: Note[],
  options: BuildTreeOptions
): DirectoryTreeNode {
  const {
    expandedIds,
    includeChildNoteCounts = true,
    sortAlphabetically = true,
  } = options;

  // Step 1: Group notes by directory
  const notesByDirectory = new Map<string, Note[]>();
  notes.forEach((note) => {
    if (note.directoryId) {
      const existing = notesByDirectory.get(note.directoryId) || [];
      notesByDirectory.set(note.directoryId, [...existing, note]);
    }
  });

  // Step 2: Create the ROOT node
  const rootNotes = notesByDirectory.get(rootDirectory.id) || [];
  const rootNode: DirectoryTreeNode = {
    directory: rootDirectory,
    children: [],
    notes: rootNotes,
    noteCount: rootNotes.length,
    isExpanded: true, // ROOT is always expanded
    level: 0,
  };

  // Step 3: Create a map for O(1) lookups
  const nodeMap = new Map<string, DirectoryTreeNode>();
  nodeMap.set(rootDirectory.id, rootNode);

  // Step 4: Create nodes for all non-root directories
  directories.forEach((dir) => {
    if (dir.id === rootDirectory.id) return; // Skip ROOT (already created)

    const dirNotes = notesByDirectory.get(dir.id) || [];
    const node: DirectoryTreeNode = {
      directory: dir,
      children: [],
      notes: dirNotes,
      noteCount: dirNotes.length,
      isExpanded: expandedIds.has(dir.id),
      level: 0, // Will be calculated when connecting relationships
    };
    nodeMap.set(dir.id, node);
  });

  // Step 5: Connect parent-child relationships and calculate levels
  directories.forEach((dir) => {
    if (dir.id === rootDirectory.id) return; // Skip ROOT

    const node = nodeMap.get(dir.id);
    if (!node) return; // Shouldn't happen, but guard against it

    // Find parent (default to ROOT if parentId is null or not found)
    const parentId = dir.parentId ?? rootDirectory.id;
    const parent = nodeMap.get(parentId);

    if (parent) {
      parent.children.push(node);
      node.level = parent.level + 1;
    } else {
      // Orphaned directory (parent doesn't exist) - attach to ROOT
      console.warn(
        `Directory "${dir.name}" (${dir.id}) has invalid parent ${parentId}. Attaching to ROOT.`
      );
      rootNode.children.push(node);
      node.level = 1;
    }
  });

  // Step 6: Sort children alphabetically (recursive)
  if (sortAlphabetically) {
    const sortChildren = (node: DirectoryTreeNode) => {
      node.children.sort((a, b) =>
        a.directory.name.localeCompare(b.directory.name, undefined, {
          sensitivity: 'base',
        })
      );
      node.children.forEach(sortChildren);
    };
    sortChildren(rootNode);
  }

  // Step 7: Calculate recursive note counts (includes children's notes)
  if (includeChildNoteCounts) {
    const calculateTotalNotes = (node: DirectoryTreeNode): number => {
      const childNotesTotal = node.children.reduce(
        (sum, child) => sum + calculateTotalNotes(child),
        0
      );

      const directNotes = node.notes.length;
      node.noteCount = directNotes + childNotesTotal;

      return node.noteCount;
    };
    calculateTotalNotes(rootNode);
  }

  return rootNode;
}

/**
 * Flattens a directory tree into a flat array (for searching/filtering)
 *
 * @param node - Root node to start flattening from
 * @returns Flat array of all nodes in depth-first order
 */
export function flattenDirectoryTree(node: DirectoryTreeNode): DirectoryTreeNode[] {
  const result: DirectoryTreeNode[] = [node];

  node.children.forEach((child) => {
    result.push(...flattenDirectoryTree(child));
  });

  return result;
}

/**
 * Finds a node in the tree by directory ID
 *
 * @param node - Root node to search from
 * @param directoryId - ID of the directory to find
 * @returns The found node or undefined
 */
export function findNodeById(
  node: DirectoryTreeNode,
  directoryId: string
): DirectoryTreeNode | undefined {
  if (node.directory.id === directoryId) {
    return node;
  }

  for (const child of node.children) {
    const found = findNodeById(child, directoryId);
    if (found) return found;
  }

  return undefined;
}

/**
 * Gets all parent directory IDs for a given directory (path to root)
 *
 * @param directories - Flat list of all directories
 * @param directoryId - ID of the directory to get parents for
 * @returns Array of parent IDs from immediate parent to root
 */
export function getParentPath(
  directories: Directory[],
  directoryId: string
): string[] {
  const dirMap = new Map(directories.map((d) => [d.id, d]));
  const path: string[] = [];

  let currentId: string | null = directoryId;
  while (currentId) {
    const dir = dirMap.get(currentId);
    if (!dir || !dir.parentId) break;

    path.push(dir.parentId);
    currentId = dir.parentId;
  }

  return path;
}
