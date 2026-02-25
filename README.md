# SpigotEssentials

A lightweight essentials plugin for Spigot/Bukkit servers with essential commands and features.

## Features

- **Game Mode Management** - Change game modes for yourself and others
- **Fly Mode** - Toggle flight ability
- **Player Management** - Heal and feed players
- **Home System** - Set, teleport to, and manage multiple homes
- **Teleport Requests** - Request to teleport to other players
- **Spawn Management** - Set and teleport to server spawn
- **Back Command** - Return to previous location after death or teleport

## Requirements

- Spigot/Paper 1.21+
- Java 17+

## Installation

1. Download the latest `SpigotEssentials-1.0-SNAPSHOT.jar` from releases
2. Place the JAR file in your server's `plugins` folder
3. Restart your server
4. Configure the plugin in `plugins/SpigotEssentials/config.yml`

## Commands

### Game Mode
| Command | Description | Usage | Aliases |
|---------|-------------|-------|---------|
| `/gamemode` | Change your game mode | `/gamemode <mode> [player]` | `/gm` |

**Modes:** `survival`, `creative`, `adventure`, `spectator` (or `0`, `1`, `2`, `3`)

### Player Utilities
| Command | Description | Usage |
|---------|-------------|-------|
| `/fly` | Toggle fly mode | `/fly [player]` |
| `/heal` | Heal yourself or another player | `/heal [player]` |
| `/feed` | Feed yourself or another player | `/feed [player]` |

### Home System
| Command | Description | Usage |
|---------|-------------|-------|
| `/sethome` | Set your home location | `/sethome [name]` |
| `/home` | Teleport to your home | `/home [name]` |
| `/delhome` | Delete a home | `/delhome [name]` |

### Teleport Requests
| Command | Description | Usage |
|---------|-------------|-------|
| `/tpa` | Request to teleport to a player | `/tpa <player>` |
| `/tpaaccept` | Accept a teleport request | `/tpaaccept` |
| `/tpadeny` | Deny a teleport request | `/tpadeny` |
| `/tpahere` | Request a player to teleport to you | `/tpahere <player>` |
| `/tpahereaccept` | Accept a tpahere request | `/tpahereaccept` |
| `/tpaheredeny` | Deny a tpahere request | `/tpaheredeny` |

**Notes:**
- Teleport requests expire after 60 seconds
- Only one request can be pending at a time per player
- Requests are automatically cleaned up when players disconnect

### Spawn Management
| Command | Description | Usage |
|---------|-------------|-------|
| `/setspawn` | Set the server spawn point | `/setspawn` |
| `/spawn` | Teleport to spawn | `/spawn` |

**Spawn Features:**
- New players automatically teleport to spawn on first join
- Players respawn at spawn if no bed is set

### Other Commands
| Command | Description | Usage |
|---------|-------------|-------|
| `/back` | Return to previous location | `/back` |

**Back Features:**
- Returns to location after death
- Returns to location before teleport
- Only stores one previous location

## Permissions

### Game Mode
| Permission | Description | Default |
|------------|-------------|---------|
| `spigotessentials.gamemode.self` | Change your own game mode | op |
| `spigotessentials.gamemode.others` | Change others' game mode | op |

### Fly
| Permission | Description | Default |
|------------|-------------|---------|
| `spigotessentials.fly.self` | Toggle your own fly mode | op |
| `spigotessentials.fly.others` | Toggle others' fly mode | op |

### Heal
| Permission | Description | Default |
|------------|-------------|---------|
| `spigotessentials.heal.self` | Heal yourself | op |
| `spigotessentials.heal.others` | Heal other players | op |

### Feed
| Permission | Description | Default |
|------------|-------------|---------|
| `spigotessentials.feed.self` | Feed yourself | op |
| `spigotessentials.feed.others` | Feed other players | op |

### Home
| Permission | Description | Default |
|------------|-------------|---------|
| `spigotessentials.home` | Use home commands | true |

### Teleport Requests
| Permission | Description | Default |
|------------|-------------|---------|
| `spigotessentials.tpa` | Use teleport request commands | true |

### Spawn
| Permission | Description | Default |
|------------|-------------|---------|
| `spigotessentials.setspawn` | Set spawn point | op |
| `spigotessentials.spawn` | Teleport to spawn | true |

### Back
| Permission | Description | Default |
|------------|-------------|---------|
| `spigotessentials.back` | Use /back command | true |

## Configuration

### config.yml

```yaml
# Join/Leave Messages
join-message: "§aWelcome to the server!"
leave-message: "§cGoodbye!"

# Enable/Disable Features
back-command: true
```

## Data Files

### locations.yml
Stores all location data including:
- Player homes
- Server spawn point
- Custom locations

**Format:**
```yaml
PlayerName.home: "world;x;y;z;yaw;pitch"
PlayerName.home2: "world;x;y;z;yaw;pitch"
spawn: "world;x;y;z;yaw;pitch"
```

## Building from Source

```bash
# Clone the repository
git clone https://github.com/yourusername/SpigotEssentials.git
cd SpigotEssentials

# Build with Maven
mvn clean package

# Output: target/SpigotEssentials-1.0-SNAPSHOT.jar
```

## Support

For issues, bugs, or feature requests, please open an issue on the GitHub repository.

## License

This project is open source. See LICENSE file for details.

## Version

Current Version: **1.0-SNAPSHOT**
