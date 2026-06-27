# AGENTS.md

AI coding agent instructions for the **asset-optimizer** project.

## Obsidian Memory

This project uses an Obsidian vault (`asset-optimizer`) for cross-session memory.
On session start, read the project context and preferences:

```bash
obsidian vault="asset-optimizer" read path="preferences.md"
```

### During Session

- All documentation must be managed within Obsidian. Do not leave any documentation within the project.

- **Decision made** → create `Sessions/{title}.md`
- **Task completed/created** → update `Tasks/active.md`

### Session End

- Update `Tasks/active.md`, `Tasks/completed.md` and `Sessions/{title}.md`
- Rebuild `_Index.md`

## Git

- Never add `Co-authored-by` trailer to commits.
