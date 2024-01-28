# Quests plugin (1.20)
This is a simple quests plugin designed as a quest rotating system. This means that when the quest pool is reset, a brand new list of randomized quests are generated for the players to enjoy! This is different from the traditional static quests, and is best used as a daily or weekly rotation.

![2024-01-28_03 57 55](https://github.com/TrevorMickelson/Quests/assets/70197204/108138d0-442a-4c37-887f-c4576e11a45b)
![2024-01-28_04 01 57](https://github.com/TrevorMickelson/Quests/assets/70197204/fbe827be-2c1b-4956-b611-e5eb5fac3d27)

### Getting started
Want to skip all the details and just add/test a quest? Here's examples of a commands you can type to add a quest!

```/quest add emmy crafting emerald_block 1 30 quest.emmy give %player% diamond 1,give %player% apple 1```

```/quest add emmy block_break dirt 15 100 quest.dirt give %player% diamond 1,give %player% apple 1```

After you have added these quests, but sure to execute ```/quest reset``` to refresh the quest active cycle. Now
you can join a quest via ```/quest menu``` and enjoy :D

### Commands
Commands are fully configurable in the config, but here's the default format of the commands.

```/quest add <id> <type> <association> <min> <max> <permission> <console-command-rewards>``` adds quest to database, but not to the active quest pool. This will be randomly selected when the reset command is executed.


```/quest delete <id>```

```/quest language <lang>``` per player langauge

```/quest menu``` opens quest menu to join/leave quests

```/quest reset``` resets active quest cycle

![Screenshot 2024-01-28 035427](https://github.com/TrevorMickelson/Quests/assets/70197204/ceb40ce3-cb16-41b2-b372-3cec0962469c)


### Multi-instance

### Multi-language

### PlaceholderAPI

### Configs

### Dependencies
