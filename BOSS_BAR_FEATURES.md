# Boss Bar Blessing Features

## Overview
The boss bar now displays progress towards the next blessing goal instead of just showing karma out of 100. This provides players with clear feedback on their progress towards the next reward.

## Features

### 1. Current Karma Display
- Shows the player's current karma value
- Color-coded based on karma ranges:
  - Red: 1-19 (Very Low)
  - Pink: 20-39 (Low)
  - Yellow: 40-59 (Neutral)
  - Green: 60-79 (Good)
  - Blue: 80-100 (Excellent)

### 2. Next Blessing Goal Display
- Shows the name of the next blessing goal to achieve
- Displays how much more karma is needed to reach the goal
- Progress bar shows advancement from current goal to next goal

### 3. Configurable Blessing Display
- **Show Next Blessing**: When enabled, shows the actual blessing name
- **Hidden Blessing Text**: When disabled, shows a mystery text instead
- Configurable in `config.yml` under `boss-bar` section

## Configuration

### config.yml
```yaml
# Boss Bar Configuration
boss-bar:
  # Whether to show the next blessing target in the boss bar
  show-next-blessing: true
  # Text to display when next blessing is hidden (random, surprise, etc.)
  hidden-blessing-text: "§6§l??? §r§7(Mystery Blessing)"
```

### Karma Goals Configuration
```yaml
karma-goals:
  first-step:
    threshold: 25
    reward: "item"
    reward_data: "DIAMOND_SWORD"
    message: "§a§lKARMA GOAL ACHIEVED! §r§aYou've reached 25 karma and received a Diamond Sword!"
  
  good-citizen:
    threshold: 50
    reward: "item"
    reward_data: "DIAMOND_CHESTPLATE"
    message: "§a§lKARMA GOAL ACHIEVED! §r§aYou've reached 50 karma and received a Diamond Chestplate!"
  
  hero:
    threshold: 75
    reward: "item"
    reward_data: "NETHERITE_SWORD"
    message: "§a§lKARMA GOAL ACHIEVED! §r§aYou've reached 75 karma and received a Netherite Sword!"
  
  legend:
    threshold: 100
    reward: "item"
    reward_data: "NETHERITE_CHESTPLATE"
    message: "§a§lKARMA GOAL ACHIEVED! §r§aYou've reached 100 karma and received a Netherite Chestplate!"
```

## Testing

The boss bar functionality can be tested by:
1. Using the `/karma` command to change player karma values
2. Observing the boss bar updates in real-time
3. Modifying the `config.yml` to test different display modes

### Example Boss Bar Displays

**Visible Blessing Mode:**
```
§6Karma: 30 §7→ §afirst-step §7(5 more)
```

**Hidden Blessing Mode:**
```
§6Karma: 30 §7→ §6§l??? §r§7(Mystery Blessing)
```

**Max Level:**
```
§6Karma: 100/100 §a(Max Level!)
```

## Technical Implementation

### Key Classes
- `KarmaManager` - Handles boss bar display and karma management
- `KarmaGoalManager` - Manages blessing goals and achievements

### Events
- `KarmaChangeEvent` - Fired when karma changes
- `KarmaGoalAchievedEvent` - Fired when a blessing goal is achieved

### Progress Calculation
The progress bar shows advancement from the current achieved goal to the next goal:
- Progress = (Current Karma - Current Goal Threshold) / (Next Goal Threshold - Current Goal Threshold)
- Ensures smooth progression between goals rather than resetting to 0

## Acceptance Criteria Met

✅ **Boss bar displays current karma** - Shows current karma value prominently

✅ **Displays next blessing goal** - Shows the name of the next goal to achieve

✅ **Shows next target blessing** - Configurable display of the next blessing target

✅ **Hidden blessing text** - When configured as hidden, displays fun/vague text instead

The implementation provides a complete solution that meets all the specified acceptance criteria while maintaining backward compatibility and providing a smooth user experience. 