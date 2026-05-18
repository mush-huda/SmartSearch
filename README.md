# SmartSearch
A demo microservices platform that lets users search for live events using natural language (e.g., "Jazz concerts in Berlin under €40 this weekend").

# How to run: 

- Start everything:

```bash
docker compose up --build
``` 

- Rebuild a single service after a code change

```bash
docker compose up --build consumer-api-service
``` 

- Only run the infrastructure

```bash
docker compose up postgres redis kafka
```

# Architecture

## Service Overview

```mermaid
graph TD
    Client["Client"]

    subgraph SmartSearch
        CA["consumer-api-service"]
        SA["search-agent-service"]
        MCP["search-mcp-server"]
    end

    subgraph Infrastructure
        Redis[("Redis")]
        Kafka["Kafka"]
        PG[("PostgreSQL")]
        Ollama["Ollama<br/>llama3.2 (host)"]
    end

    Client -->|"POST /search"| CA
    CA -->|"sliding-window rate limit"| Redis
    CA -->|"POST /agent/search"| SA
    CA -->|"search.performed (async)"| Kafka
    SA -->|"semantic cache"| Redis
    SA -->|"LLM: extract params"| Ollama
    SA -->|"MCP tool: search_events (SSE)"| MCP
    MCP -->|"native SQL"| PG
```

## Request Sequence

```mermaid
sequenceDiagram
    actor User
    participant CA as consumer-api-service
    participant Redis
    participant Kafka
    participant SA as search-agent-service
    participant Ollama
    participant MCP as search-mcp-server
    participant PG as PostgreSQL

    User->>CA: POST /search {query} + X-API-Key

    CA->>Redis: ZADD / ZCARD (rate limit check)
    alt over limit
        CA-->>User: 429 Too Many Requests
    end

    CA->>SA: POST /agent/search {query}
    CA-->>Kafka: publish search.performed (async, fire-and-forget)

    SA->>Ollama: extract structured params from query
    Ollama-->>SA: {city, genre, artist, maxPrice, date}

    SA->>Redis: GET cache:{city}:{genre}:{artist}:{maxPrice}:{date}
    alt cache hit
        Redis-->>SA: List<EventResult>
        SA-->>CA: List<EventResult>
        CA-->>User: 200 OK
    end

    SA->>MCP: callTool("search_events", params) via SSE
    MCP->>PG: SELECT with optional filters (native SQL)
    PG-->>MCP: matching events
    MCP-->>SA: List<EventDto> as JSON

    SA->>Redis: SET cache key (TTL 5 min)
    SA-->>CA: List<EventResult>
    CA-->>User: 200 OK
```

