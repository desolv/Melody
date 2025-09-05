# Melody

Melody is an efficient **report system** plugin for Minecraft servers.

---

## Libraries

- **Lamp** – Command framework for clean, annotation-based commands
- **Triumph GUI** – Simplified and flexible GUI system
- **MongoDB** – Permanent storage for resolved and expired reports
- **Redis** – Caching and pub/sub system for active reports and cross-server synchronization (async)

---

## Database Strategy

- **Redis (Primary Cache for Active Reports)**
    - All active reports are stored here for fast access.
    - Shared cache across servers.
    - Used for pub/sub to broadcast report updates globally.

- **MongoDB (Persistence Layer for Resolved & Expired Reports)**
    - Stores all resolved and expired reports permanently.
    - Acts as the final fallback for historical data.

This design keeps active reports lightweight and fast in Redis, while MongoDB handles long-term persistence.

---

## Features

- Create and manage player reports
- Active reports synchronized across servers in real time
- Persistent storage of resolved and expired reports across restarts
- Flexible GUI to view, sort, and filter reports
- Modular and extensible design  
