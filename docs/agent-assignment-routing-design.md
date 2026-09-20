# Agent Assignment Routing Design

## Status

Accepted for the MVP design. Implementation is intentionally deferred until the routing flow has been agreed.

## Context

A chat enters `TRIAGE` when it is created. Completing triage records its topic skill, preferred language, and priority, then moves it to `WAITING`. Assignment is a separate operation that selects one waiting chat and one eligible agent.

PostgreSQL is the source of truth. The MVP will not use an in-memory queue, AWS SQS, or WebSocket delivery for assignment.

The design separates two questions:

1. Which waiting chat should be considered next?
2. Which eligible agent should receive that chat?

Priority answers the first question. Eligibility, workload, and proficiency answer the second.

## Decision

### Waiting pool

All waiting chats remain in one central PostgreSQL-backed pool. Separate physical queues are not created for topics or priorities. Priority scheduling is applied when querying `WAITING` chat-session rows.

### Fixed weighted priority cycle

The MVP uses a `4:3:2:1` weighting:

| Priority | Opportunities per cycle | Percentage under continuous demand |
|---|---:|---:|
| CRITICAL | 4 | 40% |
| HIGH | 3 | 30% |
| NORMAL | 2 | 20% |
| LOW | 1 | 10% |

The ten positions are interleaved:

```text
CRITICAL -> HIGH -> CRITICAL -> NORMAL -> HIGH
         -> CRITICAL -> LOW -> HIGH -> CRITICAL -> NORMAL
```

Interleaving spreads urgent opportunities across the cycle. Compared with grouping all four CRITICAL positions together, it reduces the longest gap before the next CRITICAL opportunity and handles newly arriving urgent chats more consistently.

The percentages apply only when every priority continuously contains assignable chats. If a priority has no assignable chat, its position is skipped, so available capacity is not wasted.

Within a priority, the oldest assignable chat is selected using `triageCompletedAt` ascending. A chat without an eligible agent stays `WAITING`; it is reconsidered on later assignment attempts when agent availability or capacity changes.

The cycle must continue from its persisted position. It must not restart at CRITICAL for every request, because doing so would starve lower priorities whenever CRITICAL work exists.

One REST assignment request assigns at most one chat. It may examine up to all ten cycle positions to find one assignable chat. If no assignment is possible, it returns no assignment and leaves the waiting data unchanged.

### Agent eligibility and ranking

An agent is eligible when the agent:

- is `AVAILABLE`;
- has remaining capacity (`openChatCount < maxOpenChats`);
- has the chat's required skill; and
- supports the chat's preferred language.

Any recorded proficiency level is eligible, including `BEGINNER`. Proficiency ranks capable agents; it is not a global authorization threshold.

Eligible agents are ranked by:

1. lowest workload percentage (`openChatCount / maxOpenChats`);
2. highest skill proficiency;
3. highest language proficiency; and
4. agent ID as a deterministic final tie-breaker.

This prevents the strongest agents from receiving nearly every chat while still preferring greater proficiency when workloads are comparable.

## Why fixed weighting for the MVP

The `4:3:2:1` ratio is a Bridged product rule, not an industry-standard ratio. It was selected because it is simple to explain, gives every priority a distinct share, guarantees progress for LOW work, and is deterministic enough to test thoroughly.

Advantages:

- predictable behavior and straightforward automated tests;
- distinct priority weights of 40%, 30%, 20%, and 10%;
- guaranteed opportunities for every priority under sustained demand;
- no SLA calendar or deadline calculations;
- weights can be made configurable later without replacing the overall assignment flow.

Disadvantages:

- assignment shares do not react to actual waiting time;
- a chat close to an expected response deadline receives no automatic boost;
- the initial weights are assumptions until traffic and wait-time metrics exist;
- persisting and safely advancing the cycle position introduces a small amount of state.

## Alternative considered: SLA-based routing and aging

SLA-based routing assigns a response target to each priority and orders chats by their proximity to breaching that target. Priority aging similarly increases a chat's effective urgency as it waits.

These approaches adapt to real waiting time and traffic spikes, and they can prevent old work from being overlooked. They are deferred because Bridged does not yet define response-time promises, business-hour calendars, paused SLA conditions, escalation rules, or the metrics needed to tune them. Adding those concepts now would make the MVP harder to reason about without evidence that the extra complexity improves its behavior.

SLA-aware routing remains a possible later evolution. It can replace the fixed chat-ordering policy without changing the separation between chat selection and agent selection.

## Deferred concerns

- automatic assignment triggers when triage completes or agent capacity changes;
- agent acceptance, rejection, timeout, and reassignment;
- SLA targets and priority aging;
- WebSocket assignment notifications;
- AWS SQS or other external queue infrastructure;
- tuning weights using observed queue volume and wait-time metrics.
