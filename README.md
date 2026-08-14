# Melody

An efficient report system plugin for Minecraft servers.

## Libraries

- **Lamp** – Command framework
- **Triumph GUI** – GUI system
- **MongoDB** – Persistent storage for resolved and expired reports
- **Redis** – Cache and pub/sub for active reports and cross-server sync

## Database Strategy

**Redis** – Primary cache for active reports. Fast access, shared across servers, pub/sub for global updates.

**MongoDB** – Persistence layer for resolved and expired reports. Long-term storage and historical data.

## Features

- Create and manage player reports
- Real-time cross-server synchronization
- Persistent storage across restarts
- Flexible GUI with sorting and filtering
- Modular design

