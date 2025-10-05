# Synapse — Product Requirements Document (PRD)

## 1) Product Overview

**Vision**  
Synapse is an AI-powered, local-first (but cloud-capable) Zettelkasten note app that actually *understands* your notes. It delivers **hybrid search** (full-text + semantic) with structure awareness (graph proximity, tags, sources) and **proactive resurfacing** that proposes links and relevant notes while you work.

**Core theses**
- **Capture is everything** (web clipper, OCR, email-in, optional mic capture).
- **Search must feel unfairly good** (hybrid ranking, operators, zero-result rescue).
- **Linking should be largely automatic** (suggested backlinks at creation & edit time).
- **Reliability & privacy first** (local-first PWA later; cloud POC now).

**Primary personas**
- Tech leads & developers, researchers/students, consultants/knowledge workers.

**North-star metric**  
Average time to find the right note ≤ **5 seconds** from opening search.

---

## 2) Differentiators (USP)

1. **Hybrid search + structure awareness**  
   Merges **full-text**, **semantic vectors**, **recency**, and **graph proximity** into one ranked list. Power operators: `tag:`, `source:`, `link:`, `in:title`, `is:citation`, `before:`, `after:`.

2. **Proactive resurfacing engine**  
   While creating/editing notes, Synapse surfaces **contextual suggestions** (semantic neighbors + graph neighbors + same-source notes). One-click confirm to create backlinks or insert references.

3. **Frictionless capture**  
   Web clipper (Readability), **OCR** (on-device by default), **email-in** with auto-triage, and **bot-less audio capture** (optional).

4. **Local-first privacy with a cloud POC**  
   Start cloud-only to validate data/search; evolve to PWA/local-first with the same APIs.

---

## 3) Functional Requirements

### A. Notes & Structure
- Markdown notes with **YAML front-matter**:  
  `title, tags[], source_url, source_domain, source_email, created_at, updated_at, summary, ai_tags[], link_ids[], confidence_scores, citations[]`.
- `[[wikilinks]]` resolve to internal note IDs; **automatic backlinks**.
- Graph stored via `note_links (source_id, target_id, type, created_at)`.

**Acceptance**
- Creating `[[Note A]]` inside Note B adds a backlink in A.
- Importing a folder of `.md` preserves links and metadata.

### B. Capture
1. **Web clipper (MV3)** → Readability → title, content, URL, domain.
2. **OCR** (image/PDF) → on-device extraction; attach original as asset.
3. **Email-in** → per-user address/alias; parse subject/body/attachments; add `source_email`, `from_domain`; auto-triage into Inbox with 3 suggested tags.
4. **Bot-less audio capture (optional)** → local mic record → transcribe → chunk into atomic sub-notes → propose links.

**Acceptance**
- Any capture route creates a note with summary, tags, and ≥1 suggested link within **10s** (network permitting).

### C. AI Assist
- **Auto-summary** (3–5 sentences + 3–7 bullets).
- **Auto-tags** (3–7 topics) with confidence.
- **Suggested links** at creation & edit time (semantic + structure rules):
   - Top 3 **semantic neighbors** (embeddings)
   - Top 3 **graph neighbors** (1–2 hops)
   - Up to 2 **same-source** candidates (same domain/sender)
- One-click **Confirm link** → writes both edges (backlinks).

**Acceptance**
- ≥70% of new notes produce ≥1 high-confidence suggestion; user can accept with one click.

### D. Search (Hybrid + Structure-Aware)
- Single search box with hybrid ranking and **operators**:  
  `tag:devops`, `source:medium.com`, `link:"Zero-Downtime Deploys"`, `in:title:Kubernetes`, `is:citation`, `before:2025-06-01`, `after:2025-01-01`.
- **Zero-result rescue** → if no FTS hits, show semantic neighbors with a banner.
- **Explainability** → “Why this result?” tooltip listing factors (term match, semantic proximity, links to current note, recency).

**Hybrid ranking formula (v1)**
score = 0.45 * fts_rank # ts_rank_cd normalized [0..1]
+ 0.35 * semantic_similarity # 1 - cosine distance, normalized
+ 0.10 * recency_score # half-life decay (e.g., 30 days)
+ 0.10 * graph_proximity # 1=neighbor, 0.6=2 hops, else 0

Results sorted by `score` desc; present as one list with badges:
- **Exact match** (FTS-weighted)
- **Related via AI** (semantic-weighted)

**Acceptance**
- Query returns blended results in < **500ms** (FTS) and < **1.5s** (semantic) at ~10k notes.
- Operators filter correctly; explainability shown.

### E. Proactive Resurfacing Engine
- **Triggers**: open note; during typing (debounce 800ms, content delta >200 chars); on paste/clip.
- **Fetch**: 3 semantic + 3 graph + up to 2 same-source; de-dupe & rank via hybrid formula (boost current context).
- **UI**: inline “Relevant right now” panel with title, snippet, confidence, **[Link]** / **[Insert ref]**.

**Acceptance**
- Panel appears within **1.5s** of trigger; actions apply instantly and persist.

### F. Modes & Onboarding
- **Simple Mode (default)**: minimal editor, auto-link panel, search, tags; graph secondary.
- **Power Mode**: full graph filters, operators helper, citation tools.
- **First-5-minutes tour**: import 3 sample notes → auto-link preview → run a search → “aha”.

**Acceptance**
- First-time user completes tour in ≤ **2 minutes** and sees ≥1 suggested link and a semantic search result.

---

## 4) Backend APIs (Cloud POC first)

### Entities
- `notes(id, user_id, title, content, metadata JSONB, updated_at, version, tsv TSVECTOR)`
- `note_links(id, user_id, source_id, target_id, type, created_at)`
- `note_embeddings(note_id, user_id, embedding vector(D), updated_at)`

### Endpoints
- `POST /notes`, `GET /notes/{id}`, `PUT /notes/{id}`, `DELETE /notes/{id}`
- `POST /links`, `GET /notes/{id}/links`
- `GET /search?q=&operators` → blended results + per-result components (fts, semantic, recency, proximity) + explanation
- `GET /notes/{id}/suggested-links` → candidates with reasons (semantic 0.82, neighbor 1 hop, same domain)
- `POST /ingest/email` → webhook/ingest for email-in
- `POST /ingest/transcript` → accepts audio transcript payload for chunking

**Sync (later)**
- `POST /sync/push` (batch dirty notes); `GET /sync/pull?since=` (delta)
- Last-write-wins with conflict flag in POC; 3-way merge or CRDT in roadmap.

---

## 5) Search/Ranking Implementation Notes (POC)

**Full-text (FTS)**
- `tsvector` over `title + content + tags`; GIN index.

**Semantic (Vectors)**
- `pgvector` (HNSW index); use embeddings API (e.g., `text-embedding-3-small/large`) or local pipeline later.
- Store `(note_id, embedding)`.

**Graph proximity**
- Compute on the fly (neighbors + 2-hop) or maintain a light adjacency cache.

**Recency (half-life decay)**
recency_score = exp(-ln(2) * age_days / 30)

pgsql
Copy code

**Normalization**
- Scale each score to `[0..1]` per query batch before combining.

---

## 6) Non-Functional Requirements

| Area         | Requirement                                                                 |
|--------------|-----------------------------------------------------------------------------|
| Performance  | FTS < **500ms** p95; semantic < **1.5s** p95 @ 10k notes; panel < **1.5s** |
| Reliability  | Sync tests (idempotent endpoints, version checks)                           |
| Security     | JWT auth; per-user isolation; TLS; encrypt at rest (cloud)                  |
| Privacy      | **Privacy Mode** disables cloud AI; local embeddings later (Transformers.js)|
| Observability| Log query latencies; store anonymized score components for tuning           |
| Cost         | Cache summaries/tags/embeddings; batch embeddings; cap free-tier AI         |
| Accessibility| Keyboard shortcuts; screen-reader labels; WCAG-aligned contrast & focus     |

---

## 7) Data Model (SQL Sketches)

```sql
-- Notes
CREATE TABLE notes (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL,
  title TEXT NOT NULL,
  content TEXT,
  metadata JSONB,
  updated_at TIMESTAMPTZ DEFAULT now(),
  version BIGINT DEFAULT 1,
  tsv tsvector
);
CREATE INDEX idx_notes_user ON notes(user_id);
CREATE INDEX idx_notes_tsv ON notes USING gin(tsv);

-- Links
CREATE TABLE note_links (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL,
  source_id UUID REFERENCES notes(id) ON DELETE CASCADE,
  target_id UUID REFERENCES notes(id) ON DELETE CASCADE,
  type TEXT,              -- explicit | ai_suggested | email_thread | citation
  created_at TIMESTAMPTZ DEFAULT now()
);
CREATE INDEX idx_links_user ON note_links(user_id);
CREATE INDEX idx_links_source ON note_links(source_id);
CREATE INDEX idx_links_target ON note_links(target_id);

-- Embeddings
CREATE EXTENSION IF NOT EXISTS vector;
CREATE TABLE note_embeddings (
  note_id UUID PRIMARY KEY REFERENCES notes(id) ON DELETE CASCADE,
  user_id UUID NOT NULL,
  embedding vector(1536),
  updated_at TIMESTAMPTZ DEFAULT now()
);
CREATE INDEX idx_embeddings_user ON note_embeddings(user_id);
CREATE INDEX idx_embeddings_hnsw ON note_embeddings
USING hnsw (embedding vector_cosine_ops);
```

8) Acceptance Tests (End-to-End)
Clip → Note → Suggestions
Clip a web article; in ≤ 10s see summary/tags/≥1 suggestion; confirm backlink.

OCR → Searchable
Upload image; OCR text is searchable; hybrid search returns it under “Related” when keywords differ.

Email-in
Send email; note lands in Inbox with 3 auto-tags; shows ≥1 same-domain suggestion.

Audio capture
Upload transcript; chunks created; relevant past notes suggested.

Proactive resurfacing
Open a note; panel shows 3–8 relevant items with reasons; linking persists.

Hybrid search
Operators work; Explain-Why shows factors; zero-result rescue triggers when applicable.

Performance
Meets SLAs with 10k notes (avg note 1–2k tokens).

9) Roadmap (Backend-first → Local-first)
Phase 1 — Backend POC (4–6 wks)

Tables, CRUD, FTS, embeddings, hybrid search, suggested links, basic auth

Seed 10k notes; tune ranking

Phase 2 — Backend Maturity (3–4 wks)

Multi-user; email-in; transcript ingest; graph APIs; search explainability; monitoring

Phase 3 — Thin Cloud UI (3–4 wks)

Minimal React client for APIs (search, note view, suggestions, graph)

Proactive panel behavior

Phase 4 — Local-First (4–6 wks)

PWA + IndexedDB; offline CRUD; sync endpoints; local FTS (FlexSearch); cache embeddings

(Optional desktop) SQLite + FTS5 + sqlite-vec

Phase 5 — Polish

Simple/Power modes; first-run tour; pricing; privacy controls; import/export

10) Pricing & Packaging (Guidelines)
Free: core capture, FTS, limited AI actions/month, local storage.

Pro: unlimited AI (fair-use), hybrid search, proactive resurfacing, email-in, audio capture.

Single clear subscription (avoid separate “AI add-on” tax).

11) Open Questions / Tuning Knobs
- Embeddings model & dimension vs. cost/latency.
- Graph proximity weights and hop decay values.
- Operator set & syntax details (finalize after dogfooding).
- Email-in provider (inbound SMTP vs. provider webhook).
- On-device OCR baseline vs. server fallback thresholds.

_Last updated: 2025-10-04_