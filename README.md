C4 CTF
-------

C4's CTF is a simple capture the flag plugin for post 1.8 servers.

Team creation is fully dynamic and scores persist across restarts. A scoreboard shows live team scores. New players will be placed in the least populated team on join, as well as receive a fancy wool block hat based on their team color.

![screenshot](screenshot.png)

### Permissions

- `ctf.user`	Nearly every user playing the game
- `ctf.op`	Gives access to /ctfadmin commands

### Administration

- `/ctfadmin broadcast`	A debug command, showing all created teams and members
- `/ctfadmin save`	Force all CTF teams to persist to config. Should only ever be run after using *setspawn* and *setasset*

The following commands must be run while holding the color wool block of the team you wish to modify in your hand

- `/ctfadmin create`	Create a new CTF team
- `/ctfadmin setspawn`	Set the team spawn for a CTF team
- `/ctfadmin setasset`	Set the asset for the color wool block in hand, your cursor should be focuses on the block you wish to use

### Configuration Options

- `respawnDelay`	The number of seconds to wait before re-spawning an asset
- `assetHardness`	The number of block breaks it takes to capture an asset