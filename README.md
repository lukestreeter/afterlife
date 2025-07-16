# Afterlife Plugin
A [spigot] plugin for [minecraft] players who die.

Chain of Events
 - Player receive fatal damage
 - Program prevents player death event
 - player is teleported to paradise and feels groggy
 - location is inescapable for 7 day period of ban
   - surroundings are indestructible
   - player is reteleported if location of player exits paradise perimeter
 - program sends player message saying “7 day ban period initiated” ( or something more playful...it is a game)
 - once the ban is over player is teleported back to lobby (original join location, not where they die)
 - player is sent a message saying “your 7-day ban period is over!” (or something more fun)


# REQUIREMENTS
These tools are not required but they make the development process much easier

- [asdf]
- [just]


# UPCOMING FEATURES

- upon death your stuff is transported to a chest at the location of your death
  - if someone opens it they can steal but a couple of random things will be cursed with the risk of killing them or swapping places with the dead person who they are stealing from (which will place another chest where the robber died) -- and then the revived player can steal too
  - once revived your max health will go down by one half heart
- we want to design a grave stone
- the treasure chest that shows up upon death should have a message (that cannot be changed) that describes who and how the chests owner died - the chest can be destroyed but all curses would be levied


[minecraft]: https://www.minecraft.net
[spigot]: https://www.spigotmc.org/
[asdf]: https://asdf-vm.com
[just]: https://just.systems