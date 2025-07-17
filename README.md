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
  - if someone opens it they can steal but a couple of random things will be cursed with the risk of killing them or swapping places with the dead person who they are stealing from (which will place another chest where the robber died) -- and then the revived player can steal too Curses: Poison, Max Health decreased or taken from dead player.
  - once revived your max health will go down by one half heart
- we want to design a grave stone
- the treasure chest that shows up upon death should have a message (that cannot be changed) that describes who and how the chests owner died - the chest can be destroyed but all curses would be levied
-Karma!!! If you die with bad karma your grave will be filled with very little curses so you have a decreased chance of coming back. If you have good Karma you will have tons of curses giving protection to your stuff. Curses will worsen you if you have bad karma but if you have good you the curses will benefit you. A good amount of those curses will make you swap with the person who is robbing you.
Karma will be accounted for by: Damage done to players and animals
Villagers count too! Stealing from Villagers will go against you!
You can lose and regain Karma!
- make it so when you're dead the mobs, hostile or otherwise (except for cats or witches) do not see you.
-Possesion of Entities
-Different levels of Graves in different dimensions: Depends on your Karma and EXP earned!
It could also depend on what you have built, materials you've used, entities you've killed, resources you've collected!

[minecraft]: https://www.minecraft.net
[spigot]: https://www.spigotmc.org/
[asdf]: https://asdf-vm.com
[just]: https://just.systems