# Memory format

Every durable Markdown memory file begins with YAML front matter:

```yaml
---
id: stable-slug
kind: identity | semantic_memory | rules | state | project | person | decisions | lessons
version: 1
updated_at: 2026-01-01T00:00:00Z
owner: user | agent
sensitivity: public | private | secret_ref
status: active | superseded | draft
source_refs: []
review_after: 2026-02-01
---
```

## Promotion gate

A candidate memory must have:

- a source reference or an explicit `templates` source;
- a status;
- a sensitivity label;
- an owner or reviewer;
- no secret values or unnecessary personal data.

## Required separation

- Durable behavior and preferences belong in Markdown.
- Append-only events belong in `events.jsonl`.
- Current work belongs in state files.
- Credentials belong in an external secure store and may be represented only by `secure://` references.
- Raw transcripts, media, and archives are source material, not prompt context.
