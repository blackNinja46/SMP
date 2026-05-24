# Ninja SMP Plugin

Multimodulares Paper-Plugin-System für den BlackNinja SMP-Server auf Basis von Minecraft `1.21.x`.

Join: `smp.blackninja.live`  
Discord: [discord.gg/MWMCqtY9](https://discord.gg/MWMCqtY9)

## Inhalt

- [Überblick](#ueberblick)
- [Module](#module)
- [Features](#features)
- [Voraussetzungen](#voraussetzungen)
- [Build und Deployment](#build-und-deployment)
- [Konfiguration](#konfiguration)
- [Commands](#commands)
- [Permissions](#permissions)
- [Dateien und Persistenz](#dateien-und-persistenz)
- [Technik-Stack](#technik-stack)
- [Hinweise](#hinweise)

## Überblick

Dieses Repository enthält vier zusammengehörige Plugins:

- `SMP`: Core-System mit Homes, TPA, Spawn, Status, Stats, Staff-Tools, Timeout- und VoteBan-Logik
- `SMPEconomy`: Auktionshaus mit Geboten, Rückgaben, ausstehenden Zahlungen und Admin-Tools
- `SMPEvent`: Event-Server-Freigabe, Login-Whitelist und Server-Links
- `SMPWebhook`: Discord-Bot mit Chat-Sync und Slash-Commands

Das Projekt ist als Gradle-Multi-Module-Setup aufgebaut und richtet sich an `Paper 1.21.10`.

## Module

### `core`

Hauptplugin `SMP`. Registriert die Grundsysteme, Listener, GUIs und nahezu alle Minecraft-Commands.

Wichtige Bereiche:

- Homes mit GUI
- TPA-System mit Accept/Deny-Shortcuts
- Spawn-Management
- Status-System
- Stats- und Leaderboard-GUIs
- VoteBan-System
- TimeOut-System
- Staff-Utilities wie Vanish, Fly, Freeze, Staffchat und RandomTP
- Resourcepack-Steuerung
- Delayed Opening für Nether/End
- Dragon-Egg-, Elytra- und diverse QoL-Features

### `economy`

Addon `SMPEconomy`, das sich an das Core-Plugin anhängt.

Wichtige Bereiche:

- Auktionshaus-GUI
- Auktionen erstellen
- Gebote über Chat-Eingabe
- Verbotene Auktions-Items
- Rückgabe nicht abgeholter Items
- Pending Payouts und Pending Payments
- Auktions-Logs

### `event`

Addon `SMPEvent` für eine einfache Server-Freigabe und Event-Verwaltung.

Wichtige Bereiche:

- Event-Server aktivieren/deaktivieren
- Login-Sperre bei deaktiviertem Event-Server
- Bypass-Permission für Teammitglieder
- Ausgabe interner Server-URLs

### `webhook`

Addon `SMPWebhook` für die Discord-Anbindung via JDA.

Wichtige Bereiche:

- Discord Bot Login
- Slash-Commands
- Sync von Minecraft-Chat nach Discord
- Sync von Discord nach Minecraft
- Rate-Limit für Ingame-Chat
- Embed-basierte Darstellung im Discord-Channel

## Features

### Gameplay und QoL

- Homes mit Setzen, Umbenennen, Teleportieren, Löschen und GUI-Verwaltung
- TPA-Anfragen mit Bestätigungs-GUI
- Privatchat via `/msg`, `/r` und `/reply`
- Spawn-System mit zentralem Teleportpunkt
- Spieler-Stats und Leaderboard-GUI
- Status-System für frei definierbare Status-Tags
- Enderchest-Zugriff per Command
- Skalierungs-Command für Spezialeffekte
- Ping-Abfrage für sich selbst oder andere Spieler

### Moderation und Staff

- Staff Mode
- Vanish
- Fly
- Freeze
- Heal
- Random Teleport
- Staff Chat
- Inventory- und Enderchest-Einsicht
- Temporäre TimeOuts
- VoteBan-Abstimmungen
- Neustart-Countdown
- ErrorWatcher für Live-Fehlerbeobachtung

### Serververwaltung

- Spawn-Punkt setzen
- Spawn-Display setzen/entfernen
- Nether-/End-Timer pausieren, fortsetzen und zurücksetzen
- Resourcepack-URL und Hash live setzen
- Plugin-/Addon-Status per `/info`

### Economy

- Auktionshaus mit Startpreis, Sofortkaufpreis und Laufzeit
- Gebotsmodus über Chat
- Manuelles Zurückziehen eigener Auktionen
- Nachträgliches Bezahlen offener Auktionsschulden
- Rückgabe abgelaufener oder nicht abgeholter Items
- Admin-Funktionen zum Bannen/Entbannen von Items

### Discord / Webhook

- Slash-Command `/syncchat` zum Aktivieren oder Deaktivieren des Chat-Syncs
- Slash-Command `/players` für aktuelle Online-Spieler
- Slash-Command `/embed-builder` für vorbereitete Discord-Embeds
- Discord-Nachrichten werden im Sync-Channel gelöscht und als Embed neu gepostet
- Minecraft-Chat kann in einen konfigurierten Discord-Channel gespiegelt werden

## Voraussetzungen

- Java `21` oder kompatible Laufzeit für aktuelle Paper-Versionen
- Paper Server `1.21.x`
- LuckPerms auf dem Server
- Gradle Wrapper aus diesem Repository

für einzelne Addons zusätzlich:

- `SMPEconomy` benötigt `SMP`
- `SMPEvent` benötigt `SMP`
- `SMPWebhook` benötigt `SMP`

## Build und Deployment

### Projekt bauen

```bash
./gradlew build
```

Wenn du gezielt ein Modul bauen willst:

```bash
./gradlew :core:build
./gradlew :economy:build
./gradlew :event:build
./gradlew :webhook:build
```

### JARs

Die Build-Artefakte liegen anschließend in den jeweiligen Modulordnern unter:

- `core/build/libs`
- `economy/build/libs`
- `event/build/libs`
- `webhook/build/libs`

### Deployment auf dem Server

1. `SMP` nach `plugins/` kopieren
2. Gewünschte Addons ebenfalls nach `plugins/` kopieren
3. Sicherstellen, dass `LuckPerms` vorhanden ist
4. Server starten
5. Konfigurationsdateien anpassen
6. Server neu starten oder Plugins sauber reloaden

## Konfiguration

### Core

Datei: `core/src/main/resources/config.yml`

Standardwerte:

- `HomeLimit: 3`
- `nether-open: true`
- `end-open: false`
- `DevServer: false`

Im laufenden Betrieb werden zusätzliche Werte wie diese gespeichert:

- `SpawnLocation`
- `ServerState`
- `ResourcePack.URL`
- `ResourcePack.Hash`
- Delayed-Opening-Daten für Nether und End

### Economy

Dateien:

- `economy/src/main/resources/config.yml`
- `auctions.yml`
- `banneditems.yml`
- `logs.yml`
- `pendingpayments.yml`
- `pendingpayouts.yml`
- `unclaimeditems.yml`

Das Economy-Modul speichert Auktionen und ausstehende Aktionen dateibasiert im Plugin-Ordner.

### Webhook

Datei: `webhook/src/main/resources/config.yml`

Verwendete Keys:

```yml
Bot:
  Token: "DISCORD_BOT_TOKEN"
  GuildID: "DISCORD_GUILD_ID"
  SyncChat:
    ChannelID: "DISCORD_CHANNEL_ID"
    Enabled: true
```

Hinweis:

- Der Bot startet nur korrekt mit gültigem Token
- Der konfigurierte Channel wird für Chat-Sync und Discord-Embeds verwendet
- Im Code wird aktuell beim Start eine Channel-ID gesetzt und gespeichert, falls vorhanden

## Commands

Die folgende Liste beschreibt die registrierten Commands aus allen Modulen.

### Core Commands

| Command | Alias | Beschreibung | Nutzung |
| --- | --- | --- | --- |
| `/home` | `/homes` | Home-GUI oeffnen und Homes verwalten | `/home`, `/home set <name>`, `/home tp <name>`, `/home delete <name>`, `/home rename <alt> <neu>`, `/home list` |
| `/sethome` | - | Shortcut für Home setzen | `/sethome <name>` |
| `/delhome` | - | Shortcut für Home loeschen | `/delhome <name>` |
| `/tpa` | - | Teleport-Anfragen senden und verwalten | `/tpa <spieler>`, `/tpa accept <spieler>`, `/tpa deny <spieler>`, `/tpa cancel` |
| `/tpaaccept` | `/accept` | Shortcut für TPA annehmen | `/tpaaccept <spieler>` |
| `/tpadeny` | `/deny` | Shortcut für TPA ablehnen | `/tpadeny <spieler>` |
| `/spawn` | - | Teleport zum gesetzten Spawn | `/spawn` |
| `/msg` | `/r`, `/reply`, `/whisper` | Private Nachrichten und Antworten | `/msg <spieler> <nachricht>` oder `/msg <nachricht>` für letzten Kontakt |
| `/ping` | - | Eigenen oder fremden Ping anzeigen | `/ping`, `/ping <spieler>` |
| `/stats` | `/statistics` | Stats-GUI oeffnen | `/stats`, `/stats <spieler>` |
| `/leaderboard` | - | Leaderboard-GUI oeffnen | `/leaderboard` |
| `/status` | - | Status setzen oder verwalten | `/status set <status>`, `/status set none`, `/status list`, `/status add <name> <display>`, `/status remove <name>` |
| `/ec` | - | Eigene Enderchest oeffnen | `/ec` |
| `/hubschrauber` | - | Fun-Command | `/hubschrauber` |

### Admin- und Staff-Commands

| Command | Alias | Beschreibung | Nutzung |
| --- | --- | --- | --- |
| `/smp` | - | SMP-Verwaltung | `/smp spawn`, `/smp resetElytra <spieler>`, `/smp spawnDisplay`, `/smp removeDisplay` |
| `/voteban` | `/vote` | VoteBan-System | `/voteban yes`, `/voteban no`, `/voteban create <spieler> <abstimmungsminuten> <grund> <dauer> <seconds\|minutes\|hours\|days>`, `/voteban cancel` |
| `/resourcepack` | - | Resourcepack verwalten | `/resourcepack load`, `/resourcepack setUrl <url>`, `/resourcepack setHash <sha1>` |
| `/timeout` | `/ban` | Spieler temporaer sperren | `/timeout <spieler> <grund> <dauer> <seconds\|minutes\|hours\|days>` |
| `/untimeout` | `/unban`, `/pardon` | TimeOut entfernen | `/untimeout <spieler>` |
| `/ecsee` | - | Enderchest eines Spielers oeffnen | `/ecsee <spieler>` |
| `/invsee` | - | Inventar eines Spielers oeffnen | `/invsee <spieler>` |
| `/timer` | - | Timer für Delayed Opening steuern | `/timer set <sekunden> <nether\|end>`, `/timer resume`, `/timer pause`, `/timer reset`, `/timer spawnDisplay`, `/timer removeDisplay` |
| `/restart` | - | Server-Restart mit Countdown oder sofort | `/restart`, `/restart after <sekunden>`, `/restart instant` |
| `/errorwatcher` | - | Fehler-Liveansicht toggeln | `/errorwatcher` |
| `/scale` | - | Spielergroesse aendern | `/scale small`, `/scale large`, `/scale normal` |
| `/info` | - | Status von Core und Addons anzeigen | `/info` |
| `/vanish` | - | Vanish toggeln | `/vanish` |
| `/fly` | - | Flugmodus toggeln | `/fly` |
| `/staffchat` | - | Staff-Chat verwenden | `/staffchat <nachricht>` |
| `/staff` | - | Staff-Mode toggeln | `/staff` |
| `/randomtp` | - | Zufallsteleport ausfuehren | `/randomtp` |
| `/freeze` | - | Spieler einfrieren | `/freeze <spieler>` |
| `/heal` | - | Spieler heilen | `/heal`, `/heal <spieler>` |

Hinweis:

- Einige Staff-Commands werden über den `StaffManger` registriert und liegen funktional im Core-System.

### Economy Commands

| Command | Alias | Beschreibung | Nutzung |
| --- | --- | --- | --- |
| `/auctionhouse` | `/ah` | Auktionshaus-GUI und Unterbefehle | `/ah`, `/ah sell <kaufpreis> <startpreis> <dauerInMinuten>`, `/ah withdraw`, `/ah paydebt`, `/ah cancel`, `/ah ban`, `/ah unban`, `/ah logs` |

Besonderheiten:

- `sell` verwendet das Item in der Haupthand
- Gebote werden über Chat-Eingabe verarbeitet
- `ban` und `unban` arbeiten ebenfalls mit dem Item in der Haupthand

### Event Commands

| Command | Alias | Beschreibung | Nutzung |
| --- | --- | --- | --- |
| `/get-server-urls` | - | Dashboard- und BlueMap-Links ausgeben | `/get-server-urls` |
| `/switch-event-server-state` | - | Event-Server freischalten oder sperren | `/switch-event-server-state true`, `/switch-event-server-state false` |

### Discord Slash-Commands

Diese Commands kommen aus `SMPWebhook` und werden in Discord registriert:

| Command | Beschreibung |
| --- | --- |
| `/syncchat` | Aktiviert oder deaktiviert den Chat-Sync |
| `/players` | Zeigt die aktuellen Online-Spieler an |
| `/embed-builder` | Sendet vorbereitete Embed-Nachrichten anhand einer ID |

## Permissions

### Core und Admin

| Permission | Beschreibung |
| --- | --- |
| `ninjasmp.*` | Sammelpermission für viele Admin/Core-Rechte |
| `ninjasmp.cmd.smp` | Zugriff auf `/smp` |
| `ninjasmp.voteban.admin` | VoteBan erstellen und abbrechen |
| `ninjasmp.status.admin` | Status anlegen und entfernen |
| `ninjasmp.cmd.restart` | Zugriff auf `/restart` |
| `ninjasmp.cmd.scale` | Zugriff auf `/scale` |
| `ninjasmp.cmd.invsee` | Zugriff auf `/invsee` |
| `ninjasmp.cmd.ecsee` | Zugriff auf `/ecsee` |
| `ninjasmp.cmd.ec` | Zugriff auf `/ec` |
| `ninjasmp.cmd.timeout` | Zugriff auf `/timeout` und `/untimeout` |
| `ninjasmp.cmd.resourcepack` | Gedachte Permission für Resourcepack-Verwaltung laut `plugin.yml` |
| `ninjasmp.cmd.timer` | Zugriff auf `/timer` |
| `ninjasmp.cmd.info` | Zugriff auf `/info` |

Wichtig:

- Im Code prueft `/resourcepack` aktuell auf `smp.command.resourcepack`, nicht auf `ninjasmp.cmd.resourcepack`
- `/errorwatcher` prüft aktuell auf `ninjasmp.debug`

### Staff

| Permission | Beschreibung |
| --- | --- |
| `ninjasmp.staff.*` | Sammelpermission für Staff-Funktionen |
| `ninjasmp.staff.bypass` | Allgemeiner Staff-Bypass |
| `ninjasmp.staff.cmd.vanish` | Zugriff auf `/vanish` |
| `ninjasmp.staff.cmd.fly` | Zugriff auf `/fly` |
| `ninjasmp.staff.cmd.chat` | Zugriff auf `/staffchat` |
| `ninjasmp.staff.cmd.randomtp` | Zugriff auf `/randomtp` |
| `ninjasmp.staff.cmd.staffmode` | Zugriff auf `/staff` |
| `ninjasmp.staff.cmd.heal` | Zugriff auf `/heal` |
| `ninjasmp.staff.cmd.freeze` | Zugriff auf `/freeze` |

### Ranks

| Permission | Beschreibung |
| --- | --- |
| `ninjasmp.rank.player` | Spieler-Rang |
| `ninjasmp.rank.ninja+` | Ninja+-Rang |
| `ninjasmp.rank.vip` | VIP-Rang |
| `ninjasmp.rank.moderator` | Moderator-Rang |

### Economy

| Permission | Beschreibung |
| --- | --- |
| `ninjasmp.economy.cmd.auction.admin` | Admin-Befehle für `/ah ban`, `/ah unban` und erweiterte Verwaltung |

Zusatz:

- für `/ah logs` wird im Code zusaetzlich `auctionhouse.logs` verwendet

### Event

| Permission | Beschreibung |
| --- | --- |
| `ninjasmp.event.cmd.serverstate` | Zugriff auf `/switch-event-server-state` |
| `ninjasmp.event.whitelist.bypass` | Bypass für Login-Sperre, wenn der Event-Server geschlossen ist |
| `ninjasmp.event.cmd.dashboard` | In `plugin.yml` vorhanden |

Wichtig:

- Der URL-Command prüft im Code aktuell auf `ninjasmp.event.cmd.urls`

## Dateien und Persistenz

Folgende Daten werden vom System gespeichert:

- Homes
- Status-Daten
- Spawn-Position
- Delayed-Opening-Daten für Nether und End
- Resourcepack-Konfiguration
- Event-Server-Status
- Auktionen
- Gebannte Auktions-Items
- Unclaimed Items
- Pending Payments
- Pending Payouts
- Auktions-Logs
- Discord-Bot-Konfiguration

## Technik-Stack

- Java
- Gradle Kotlin DSL
- Paper API `1.21.10-R0.1-SNAPSHOT`
- LuckPerms API `5.4`
- SmartInvs `1.2.7`
- JDA `5.6.1`

## Hinweise

- Das Core-Plugin setzt `LuckPerms` als harte Abhängigkeit voraus
- Die Addons `economy`, `event` und `webhook` deaktivieren sich selbst, wenn `SMP` nicht geladen ist
- Das Webhook-Modul benötigt gültige Discord-Credentials, sonst startet der Bot nicht sinnvoll
- Das Event-Modul nutzt den Core-Config-Wert `ServerState`, um Logins zu erlauben oder zu blockieren
- Das TimeOut-System und einige andere Bereiche verwenden Dateispeicherung statt Datenbank
- In mehreren Bereichen gibt es kleine Inkonsistenzen zwischen `plugin.yml` und Code bei Permission-Namen; die README dokumentiert beides bewusst
