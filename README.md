# PaperEssentials

A comprehensive essentials plugin for Paper/Spigot servers with essential commands and features.

## Features

- **Game Mode Management** - Change game modes for yourself and others
- **Fly Mode** - Toggle flight ability
- **God Mode** - Toggle invincibility
- **Player Management** - Heal, feed, freeze, and manage players
- **Home System** - Set, teleport to, and manage multiple homes
- **Warp System** - Create and manage server-wide warp points
- **Teleport Requests** - Request to teleport to other players
- **Spawn Management** - Set and teleport to server spawn
- **Back Command** - Return to previous location after death or teleport
- **AFK System** - Manual and automatic AFK detection
- **Vanish Mode** - Toggle invisibility from other players
- **Personal Environment** - Set personal time and weather
- **World Management** - Change world time and weather
- **Utility Commands** - Speed, workbench, enderchest, repair, enchanting table, and more
- **Inventory Management** - View and clear player inventories
- **Broadcast System** - Send server-wide announcements
- **Configurable Messages** - Fully customizable messages via messages.yml

## Requirements

- Paper/Spigot 1.21+
- Java 21+

## Installation

1. Download the latest `PaperEssentials-1.0-SNAPSHOT.jar` from releases
2. Place the JAR file in your server's `plugins` folder
3. Restart your server
4. Configure the plugin in `plugins/PaperEssentials/config.yml`
5. Customize messages in `plugins/PaperEssentials/messages.yml`

## Commands

### Game Mode & Player State
| Command | Description | Usage | Aliases |
|---------|-------------|-------|---------|
| `/gamemode` | Change your game mode | `/gamemode <mode> [player]` | `/gm` |
| `/fly` | Toggle fly mode | `/fly [player]` | - |
| `/god` | Toggle god mode | `/god [player]` | `/godmode` |
| `/speed` | Adjust walk or fly speed | `/speed <walk\|fly> <0-10> [player]` | - |

**Game Modes:** `survival`, `creative`, `adventure`, `spectator` (or `0`, `1`, `2`, `3`)

### Player Utilities
| Command | Description | Usage | Aliases |
|---------|-------------|-------|---------|
| `/heal` | Heal yourself or another player | `/heal [player]` | - |
| `/feed` | Feed yourself or another player | `/feed [player]` | - |
| `/clear` | Clear inventory | `/clear [player]` | `/ci` |
| `/suicide` | Commit suicide | `/suicide` | `/kill` |
| `/ping` | Check ping | `/ping [player]` | - |
| `/hat` | Wear item in hand as hat | `/hat` | - |
| `/afk` | Toggle AFK status | `/afk` | - |

### Home System
| Command | Description | Usage |
|---------|-------------|-------|
| `/sethome` | Set your home location | `/sethome [name]` |
| `/home` | Teleport to your home | `/home [name]` |
| `/delhome` | Delete a home | `/delhome [name]` |

**Features:**
- Multiple homes per player
- Named home locations
- Persistent across server restarts

### Warp System
| Command | Description | Usage | Aliases |
|---------|-------------|-------|---------|
| `/warp` | Teleport to warp or list warps | `/warp [name]` | - |
| `/setwarp` | Create a warp point | `/setwarp <name>` | - |
| `/delwarp` | Delete a warp point | `/delwarp <name>` | `/deletewarp`, `/removewarp` |

**Features:**
- Server-wide warp points
- Op-only creation/deletion
- List all warps with `/warp`

### Teleport System
| Command | Description | Usage |
|---------|-------------|-------|
| `/tpa` | Request to teleport to a player | `/tpa <player>` |
| `/tpaaccept` | Accept a teleport request | `/tpaaccept` |
| `/tpadeny` | Deny a teleport request | `/tpadeny` |
| `/tpahere` | Request a player to teleport to you | `/tpahere <player>` |
| `/tpahereaccept` | Accept a tpahere request | `/tpahereaccept` |
| `/tpaheredeny` | Deny a tpahere request | `/tpaheredeny` |
| `/back` | Return to previous location | `/back` |

**Notes:**
- Teleport requests expire after 60 seconds
- Only one request can be pending at a time per player
- Back command works after death or teleport

### Spawn Management
| Command | Description | Usage |
|---------|-------------|-------|
| `/setspawn` | Set the server spawn point | `/setspawn` |
| `/spawn` | Teleport to spawn | `/spawn` |

**Features:**
- New players automatically teleport to spawn on first join
- Players respawn at spawn if no bed is set

### World Management
| Command | Description | Usage | Aliases |
|---------|-------------|-------|---------|
| `/day` | Set world time to day | `/day [world]` | - |
| `/night` | Set world time to night | `/night [world]` | - |
| `/sun` | Set weather to clear | `/sun [world]` | `/clear` |
| `/rain` | Set weather to rain | `/rain [world]` | - |
| `/thunder` | Set weather to thunder | `/thunder [world]` | `/storm` |

### Personal Environment
| Command | Description | Usage | Aliases |
|---------|-------------|-------|---------|
| `/playertime` | Set personal time | `/playertime <time\|reset> [player]` | `/ptime`, `/pt` |
| `/playerweather` | Set personal weather | `/playerweather <clear\|rain\|reset> [player]` | `/pweather`, `/pw` |

**Time Options:** `day`, `night`, `noon`, `midnight`, or any number (0-24000)

### Inventory & Items
| Command | Description | Usage | Aliases |
|---------|-------------|-------|---------|
| `/workbench` | Open crafting table | `/workbench` | `/craft`, `/wb` |
| `/enderchest` | Open ender chest | `/enderchest [player]` | `/ec`, `/echest` |
| `/invsee` | View player inventory | `/invsee <player>` | `/inv` |
| `/repair` | Repair item or all items | `/repair [all]` | `/fix` |
| `/enchantingtable` | Open enchanting table | `/enchantingtable` | `/etable` |

### Moderation
| Command | Description | Usage | Aliases |
|---------|-------------|-------|---------|
| `/freeze` | Freeze/unfreeze a player | `/freeze <player>` | - |
| `/vanish` | Toggle invisibility | `/vanish` | `/v` |
| `/broadcast` | Broadcast message to all players | `/broadcast <message>` | `/bc`, `/announce` |

## Permissions

### Master Permission
| Permission | Description | Default |
|------------|-------------|---------|
| `spigotessentials.*` | Grants all permissions | op |

### Game Mode & Player State
| Permission | Description | Default |
|------------|-------------|---------|
| `spigotessentials.gamemode.self` | Change your own game mode | op |
| `spigotessentials.gamemode.others` | Change others' game mode | op |
| `spigotessentials.fly.self` | Toggle your own fly mode | op |
| `spigotessentials.fly.others` | Toggle others' fly mode | op |
| `spigotessentials.godmode.self` | Toggle your own god mode | op |
| `spigotessentials.godmode.others` | Toggle others' god mode | op |
| `spigotessentials.speed` | Adjust movement speed | op |

### Player Utilities
| Permission | Description | Default |
|------------|-------------|---------|
| `spigotessentials.heal.self` | Heal yourself | op |
| `spigotessentials.heal.others` | Heal other players | op |
| `spigotessentials.feed.self` | Feed yourself | op |
| `spigotessentials.feed.others` | Feed other players | op |
| `spigotessentials.clear.self` | Clear own inventory | op |
| `spigotessentials.clear.others` | Clear others' inventory | op |
| `spigotessentials.suicide` | Use suicide command | true |
| `spigotessentials.ping.self` | Check own ping | true |
| `spigotessentials.ping.others` | Check others' ping | true |
| `spigotessentials.hat` | Wear items as hat | true |
| `spigotessentials.afk` | Use AFK command | true |

### Home & Warp
| Permission | Description | Default |
|------------|-------------|---------|
| `spigotessentials.home.set` | Set home locations | true |
| `spigotessentials.home.teleport` | Teleport to homes | true |
| `spigotessentials.home.delete` | Delete homes | true |
| `spigotessentials.warp` | Teleport to warps | true |
| `spigotessentials.setwarp` | Create warp points | op |
| `spigotessentials.delwarp` | Delete warp points | op |

### Teleport System
| Permission | Description | Default |
|------------|-------------|---------|
| `spigotessentials.tpa` | Use TPA commands | true |
| `spigotessentials.tpahere` | Use TPAHere commands | true |
| `spigotessentials.back` | Use /back command | true |

### Spawn
| Permission | Description | Default |
|------------|-------------|---------|
| `spigotessentials.spawn.set` | Set spawn point | op |
| `spigotessentials.spawn.teleport` | Teleport to spawn | true |

### World & Environment
| Permission | Description | Default |
|------------|-------------|---------|
| `spigotessentials.time` | Change world time | op |
| `spigotessentials.weather` | Change world weather | op |
| `spigotessentials.playertime.self` | Set own personal time | true |
| `spigotessentials.playertime.others` | Set others' personal time | op |
| `spigotessentials.playerweather.self` | Set own personal weather | true |
| `spigotessentials.playerweather.others` | Set others' personal weather | op |

### Inventory & Items
| Permission | Description | Default |
|------------|-------------|---------|
| `spigotessentials.workbench` | Open virtual crafting table | true |
| `spigotessentials.enderchest.self` | Open own ender chest | true |
| `spigotessentials.enderchest.others` | Open others' ender chests | op |
| `spigotessentials.invsee` | View player inventories | op |
| `spigotessentials.repair` | Repair item in hand | op |
| `spigotessentials.repair.all` | Repair all items | op |
| `spigotessentials.enchant` | Open enchanting table | true |

### Moderation
| Permission | Description | Default |
|------------|-------------|---------|
| `spigotessentials.freeze` | Freeze/unfreeze players | op |
| `spigotessentials.vanish` | Toggle vanish mode | op |
| `spigotessentials.vanish.see` | See vanished players | op |
| `spigotessentials.broadcast` | Broadcast messages | op |

## Configuration

### config.yml

```yaml
# Spawn settings
spawn-on-join: true
spawn-on-death: true

# Back command
back-command: true

# AFK settings
auto-afk-enabled: true
auto-afk-time: 300  # Time in seconds before auto-AFK (5 minutes)

# First join
first-join-broadcast: true
```

### messages.yml

All plugin messages are fully customizable. Use `&` for color codes (e.g., `&a` = green, `&c` = red).

**Example:**
```yaml
# Permission messages
no-permission-self: "&cYou do not have permission to use this command."
player-not-found: "&cPlayer not found."

# Fly messages
fly-enabled-self: "&aFly mode enabled."
fly-disabled-self: "&cFly mode disabled."

# Warp messages
warp-teleported: "&aTeleported to warp '%s'."
warp-created: "&aWarp '%s' created successfully."

# ... and many more
```

## Data Files

### locations.yml
Stores player homes and spawn point:
```yaml
PlayerName.home: "world;x;y;z;yaw;pitch"
PlayerName.home2: "world;x;y;z;yaw;pitch"
spawn: "world;x;y;z;yaw;pitch"
```

### warps.yml
Stores server warp points:
```yaml
spawn:
  world: world
  x: 0.0
  y: 64.0
  z: 0.0
  yaw: 0.0
  pitch: 0.0
```

## Features in Detail

### AFK System
- Manual AFK toggle with `/afk`
- Auto-AFK after 5 minutes of inactivity (configurable)
- Broadcasts when players go AFK or return
- Activity tracking via movement and chat

### Vanish Mode
- Makes players invisible to others
- `spigotessentials.vanish.see` permission allows seeing vanished players
- Vanish state persists across logins
- Hidden from players without permission on join

### Freeze Command
- Completely prevents player movement
- Players can still look around
- Automatic cleanup on logout

### Warp System
- Server-wide teleport points
- List all warps with `/warp`
- Admin-controlled creation/deletion
- Persistent across restarts

## Building from Source

```bash
# Clone the repository
git clone https://github.com/frame-dev/PaperEssentials.git
cd PaperEssentials

# Build with Maven
mvn clean package

# Output: target/PaperEssentials-1.0-SNAPSHOT.jar
```

## Support

For issues, bugs, or feature requests, please open an issue on the GitHub repository.

## License

This project is open source. See LICENSE file for details.

## Version

Current Version: **1.0-SNAPSHOT**
