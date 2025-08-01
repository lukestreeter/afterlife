# Leaderboard System

This plugin now includes a comprehensive leaderboard system that allows players to view server statistics and rankings.

## Features

### Compass Item
- New players automatically receive a **Leaderboard Compass** when they first join the server
- The compass has a custom name and lore to identify it
- Right-clicking the compass opens the main leaderboard menu

### Main Menu
The main menu displays various stat categories that players can click to view detailed leaderboards:

- **Kills** - Player vs Player kills
- **Deaths** - Player deaths
- **K/D Ratio** - Kill to Death ratio
- **Animals Killed** - Peaceful mob kills
- **Hostile Mobs** - Hostile mob kills
- **Blocks Mined** - Total blocks mined
- **Warden Kills** - Warden boss kills
- **Ender Dragon** - Ender Dragon kills
- **Wither Kills** - Wither boss kills
- **Items Crafted** - Total items crafted
- **XP Collected** - Total XP collected

### Leaderboard Display
- Each stat category shows the top 10 players
- Players are displayed with their heads and rankings
- Special formatting for 1st, 2nd, and 3rd place
- Values are properly formatted (K/D ratio as decimal, XP with commas)

### Navigation
- Click any stat category to view its leaderboard
- Use the back arrow to return to the main menu
- Use the barrier (X) button to close the menu

## Technical Implementation

### Files Created
- `LeaderboardManager.java` - Manages leaderboard data and calculations
- `CompassManager.java` - Handles compass creation and identification
- `CompassDataManager.java` - Tracks which players have received compasses
- `LeaderboardMenu.java` - Creates and manages the menu interfaces
- `LeaderboardListener.java` - Handles compass and menu interactions

### Data Persistence
- Compass data is stored in `compass_data.yml`
- Player stats are already tracked in the existing `stats.yml`
- The system integrates with the existing `StatsManager`

### Integration
- Automatically registered in the main plugin class
- Compasses are given to new players on join
- All existing stat tracking continues to work

## Usage

1. **For New Players**: Automatically receive a compass on first join
2. **For Existing Players**: Can be given a compass via admin commands (to be implemented)
3. **Right-click** the compass to open the main menu
4. **Click** any stat category to view its leaderboard
5. **Use navigation buttons** to move between menus

## Future Enhancements

- Admin commands to give compasses to existing players
- Offline player support in leaderboards
- More detailed player statistics
- Customizable menu layouts
- Achievement system integration 