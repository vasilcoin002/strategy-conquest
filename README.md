# Strategy Conquest

**Strategy Conquest** is a turn-based, grid-based strategy game developed with Java and JavaFX.  
The game supports both local singleplayer matches against an AI bot and multiplayer gameplay using TCP sockets.

The project follows a layered MVC-inspired architecture with separated gameplay systems, networking, rendering, and service abstraction.

---

# Features

- Turn-based gameplay
- Grid-based movement system
- Singleplayer mode with AI
- Multiplayer support
- Server-authoritative networking
- Save/load system
- City economy and upgrades
- Unit recruitment system
- JavaFX rendering
- BFS pathfinding
- DTO serialization architecture

---

# Technologies

| Technology | Purpose |
|---|---|
| Java | Main programming language |
| JavaFX | GUI framework |
| Maven | Dependency management |
| Gson | Serialization |
| Java Sockets | Multiplayer networking |

---

# High-Level Architecture

The project follows a layered architecture:

```text
UI Layer
↓
Controllers
↓
Service Layer
↓
Gameplay Managers
↓
Domain Model
```

Networking works separately:

```text
Client ↔ Server ↔ GameSession
```

---

# Full Project Structure

```text
src/main/java/pjvsemproj
│
├── config/
│   ├── EntityDTODeserializer
│   ├── GameConfigValidator
│   ├── GameConfigSanitizer
│   ├── InvalidGameConfigException
│   └── other configuration utilities
│
├── controllers/
│   ├── GameController
│   ├── MainMenuController
│   ├── MultiplayerLobbyController
│   ├── SceneDirector
│   └── other JavaFX controllers
│
├── dto/
│   ├── GameDTO
│   ├── PlayerDTO
│   ├── TileDTO
│   ├── EntityDTO
│   ├── CityDTO
│   ├── TroopUnitDTO
│   └── other transfer objects
│
├── models/
│   │
│   ├── entities/
│   │   │
│   │   ├── Entity
│   │   ├── DamageableEntity
│   │   │
│   │   ├── cities/
│   │   │   ├── City
│   │   │   ├── CityType
│   │   │   └── city-related logic
│   │   │
│   │   ├── troopUnits/
│   │   │   ├── TroopUnit
│   │   │   ├── TroopType
│   │   │   └── troop-related logic
│   │   │
│   │   └── interfaces/
│   │       ├── Movable
│   │       ├── Damageable
│   │       ├── Ownable
│   │       ├── IDamager
│   │       └── Upgradable<T>
│   │
│   ├── game/
│   │   ├── Game
│   │   ├── GameMap
│   │   ├── Tile
│   │   ├── GameConstants
│   │   ├── players/
│   │   │   ├── Player
│   │   │   └── BotPlayer
│   │   └── game state classes
│   │
│   ├── managers/
│   │   ├── TurnManager
│   │   ├── MovementManager
│   │   ├── CombatManager
│   │   ├── EconomyManager
│   │   ├── ConquestManager
│   │   ├── ITurnListener
│   │   │
│   │   └── utils/
│   │       ├── GridPositionHelper
│   │       └── OwnershipHelper
│   │
│   └── services/
│       ├── CoreGameService
│       ├── AbstractGameService
│       ├── AbstractClientService
│       ├── LocalGameService
│       ├── NetworkGameService
│       ├── ServerGameService
│       ├── BotExecutor
│       └── service-related logic
│
├── server/
│   ├── Client
│   ├── Connection
│   ├── GameServer
│   ├── GameSession
│   ├── Protocol
│   ├── NetworkGameListener
│   └── multiplayer networking classes
│
├── views/
│   ├── GameView
│   ├── MainMenuView
│   ├── MultiplayerLobbyView
│   ├── SidePanelView
│   ├── MapRenderer
│   ├── Renderer
│   ├── ViewConstants
│   └── rendering/UI classes
│
├── HelloApplication
└── Launcher
```

---

# Domain Layer

The domain layer contains the core game state.

## Game

Stores:
- players
- current player
- game map

---

## GameMap

Represents the game world as a 2D tile grid.

Responsible for:
- tile storage
- coordinate validation
- map structure

---

## Tile

Represents one map cell.

A tile may contain:
- troop units
- cities

---

## Player

Stores:
- gold balance
- owned cities
- troop units

---

# Entity System

The project uses an entity hierarchy.

```text
Entity
 └── DamageableEntity
      ├── TroopUnit
      └── City
```

---

## TroopUnit

Represents military units.

Contains:
- HP
- movement range
- attack damage
- owner
- movement state
- attack state

---

## City

Represents a city on the map.

Cities:
- generate gold
- can be upgraded
- can recruit troops

---

# Gameplay Managers

Gameplay rules are separated into manager classes.

---

## TurnManager

Handles:
- active player
- turn switching
- turn events

Uses the Observer pattern through `ITurnListener`.

---

## MovementManager

Handles:
- BFS pathfinding
- reachable tile calculation
- movement validation
- troop movement

---

## CombatManager

Handles:
- damage calculation
- combat validation
- healing
- troop death

---

## EconomyManager

Handles:
- gold generation
- troop purchases
- city upgrades

---

## ConquestManager

Handles:
- city conquest
- elimination detection
- winner detection

---

# Turn Event System

Managers subscribe to turn lifecycle events.

```text
TurnManager
 ├── MovementManager
 ├── CombatManager
 ├── EconomyManager
 └── ConquestManager
```

This reduces coupling between gameplay systems.

---

# Service Layer

The service layer separates gameplay logic from the UI.

---

## CoreGameService

Main abstraction used by the UI.

The UI does not directly access:
- gameplay managers
- networking logic
- domain entities

---

## LocalGameService

Handles:
- local gameplay
- AI bot turns


---

## NetworkGameService

Handles multiplayer client-side gameplay.

Instead of applying actions locally:
- commands are serialized into packets
- packets are sent to the server
- validated synchronization is received back

---

## ServerGameService

Server-side authoritative gameplay implementation.

Responsible for:
- validating actions
- modifying authoritative game state

---

# Multiplayer Architecture

The multiplayer framework uses raw TCP socket communication.

---

# Main Components

## Client

Sends commands to the server.

Example packets:

```text
MOVE|unitId|x|y
ATTACK|attackerId|targetId
BUY_UNIT|cityId|troopType
```

---

## Connection

Dedicated runnable thread for each connected player.

Responsible for:
- socket communication
- packet processing
- blocking read loop

---

## GameServer

Accepts incoming connections and validates usernames.

---

## GameSession

Represents one multiplayer match.

Stores:
- authoritative game state
- connected clients
- server-side services

Broadcasts synchronization updates to clients.

---

# Multiplayer Flow

Example movement flow:

```text
Player Input
↓
NetworkGameService
↓
Client sends packet
↓
Server validates move
↓
Server updates authoritative state
↓
Synchronization packet broadcast
↓
Clients update local state
```

---

# DTO System

The project uses DTOs (Data Transfer Objects).

---

# Main DTOs

- `GameDTO`
- `PlayerDTO`
- `TileDTO`
- `EntityDTO`
- `CityDTO`
- `TroopUnitDTO`

DTOs are used for:
- multiplayer synchronization
- save/load serialization
- UI rendering

---

# Save and Load System

The project uses Gson serialization.

---

## EntityDTODeserializer

Handles polymorphic deserialization.

Example:

```json
{
  "entityType": "City"
}
```

The deserializer determines which DTO subclass should be instantiated.

---

## GameConfigValidator

Validates loaded game states before reconstruction.

Validation includes:
- map bounds
- valid ownership
- unique entity IDs
- valid HP values
- overlap validation
- winner state validation

---

## GameConfigSanitizer

Sanitizes save files before reconstruction.

Prevents:
- corrupted saves
- invalid references
- malformed game state

---

# Rendering System

The UI uses JavaFX Canvas rendering.

---

## GameView

Main gameplay screen.

Uses:
- entities canvas
- overlay canvas

---

## MapRenderer

Responsible for:
- drawing cities
- drawing troop units
- overlays
- HP bars
- ownership indicators
- selection rendering

---

## SidePanelView

Displays:
- selected entity information
- player balance
- available actions

---

# AI System

The project contains a simple AI opponent.

---

## BotExecutor

The AI can:
- move units
- attack enemies
- recruit troops
- upgrade cities

The AI executes asynchronously.

---

# Helper Utilities

## GridPositionHelper

Synchronizes:
- entities
- map tiles

Prevents map desynchronization bugs.

---

## OwnershipHelper

Synchronizes:
- players
- troop units
- cities

---

# Design Patterns

## MVC Architecture

Separates:
- UI
- controllers
- domain logic

---

## Observer Pattern

Used in:
- `TurnManager`
- `ITurnListener`

---

## Layered Architecture

Separates:
- UI
- services
- gameplay systems
- networking
- domain model

---

## Service Abstraction

Used through:
- `CoreGameService`
- `AbstractGameService`

---

## Capability-Based Design

Used through:
- `Movable`
- `Damageable`
- `Ownable`
- `IDamager`
- `Upgradable<T>`

---

---

# Save Files

Game saves are stored in JSON format using Gson serialization.

Save files contain:
- players
- troop units
- cities
- map state
- current turn information

---

# Development Notes

The project follows:
- layered architecture
- service abstraction
- DTO-based serialization
- server-authoritative multiplayer architecture

Gameplay logic is separated into managers:
- movement
- combat
- economy
- conquest
- turn lifecycle