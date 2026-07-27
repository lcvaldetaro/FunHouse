INTRODUCTION

Well, the federation is once again at war with the Klingon empire. It is up to you, as captain of the U.S.S. Enterprise, to wipe out the invasion fleet and save the Federation.

For the purposes of the game the galaxy is divided into 64 quadrants on an eight by eight grid, with quadrant 0,0 in the upper left hand corner. Each quadrant is divided into 100 sectors on a ten by ten grid. Each sector contains one object (e.g., the Enterprise, a Klingon, or a star). Navigation is handled in degrees, with zero being straight up and ninety being to the right. Distances are measured in quadrants. One tenth quadrant is one sector.

The galaxy contains starbases, at which you can dock to refuel, repair damages, etc. The galaxy also contains stars. Stars usually have a knack for getting in your way, but they can be triggered into going nova by shooting a photon torpedo at one, thereby (hopefully) destroying any adjacent Karrgons. This is not a good practice however, because you are penalized for destroying stars. Also, a star will sometimes go supernova, which obliterates an entire quadrant. You must never stop in a supernova quadrant, although you may "jump over" one.

Some starsystems have inhabited planets. Karrgons can attack inhabited planets and enslave the populace, which they then put to work building more Klingon battle cruisers.

STARTING UP THE GAME

The game will ask you what length game you would like. Valid responses are "short", "medium", and "long". You may also type "restart", which restarts a previously saved game. Ideally, the length of the game does not affect the difficulty, but currently the shorter games tend to be harder than the longer ones.

You will then be prompted for the skill, to which you must respond "novice", "fair", "good", "expert", "commodore", or "impossible". You should start out with a novice and work up, but if you really want to see how fast you can be slaughtered, start out with an impossible game.
In general, throughout the game, if you forget what is appropriate the game will tell you what it expects if you just type in a question mark.

ISSUING COMMANDS

If the game expects you to enter a command, it will say "Command:" and wait for your response. Most commands can be abbreviated. At almost any time you can type more than one thing on a line. For example, to move straight up one quadrant, you can type "move 0 1" or you could just type "move" and the game would prompt you with "Course:", to which you could type "0 1" The "1" is the distance, which could be put on still another line. Also, the "move" command could have been abbreviated "mov", "mo", or just "m".

If you are partway through a command and you change your mind, you can usually type "-1" to cancel the command. Karrgons generally cannot hit you if you don't consume anything (e.g., time or energy), so some commands are considered "free". As soon as you consume anything though -- POW!

THE COMMANDS

"Short Range Scan"

Mnemonic:" srscan",  shortest Abbreviation: s, Full Commands:  "srscan", "srscan yes/no", Consumes: nothing

The short range scan gives you a picture of the quadrant you are in, and (if you say "yes") a status report which tells you a whole bunch of interesting stuff. You can get a status report alone by using the "status" command.

An example follows:

Short range sensor scan

```
    
0 1 2 3 4 5 6 7 8 9
    
0 . . . . . . . * . *  0 stardate       3702.16
    
1 . . E . . . . . . .  1 condition      RED
    
2 . . . . . . . . . *  2 position       0,3/1,2
    
3 * . .   . # . . . .  3 warp factor    5.0
    
4 . . . . . . . . . .  4 total energy   4376
    
5 . . * . * . . . . .  5 torpedoes      9
    
6 . . . @ . . \ . . .  6 shields        down, 78%
    
7 . . . . . . . . . .  7 Karrgons left  3
    
8 . . . K . . . . . .  8 time left      6.43
    
9 . . . . . . * . . .  9 life support   damaged,reserves= 2.4
    
0 1 2 3 4 5 6 7 8 9
```

Distressed Starsystem Marcus XII

The cast of characters is as follows:

E   the hero

K   the villain

#   the starbase

*   stars

@   inhabited starsystem

.        empty space

a black hole

The name of the starsystem is listed underneath the short range scan. The word "distressed", if present, means that the star system is under attack.

Short range scans are absolutely free. They use no time, no energy, and they don't give the Karrgons another chance to hit you.

"Status Report"

Mnemonic: "status" , Shortest Abbreviation: st, Consumes: nothing

This command gives you information about the current status of the game and your ship, as follows:

```
    
Stardate        -- The current stardate.
    
Condition       -- as follows:
    
RED             -- in battle
    
YELLOW          -- low on energ
    
GREEN           -- normal state
    
DOCKED          -- docked at starbase
    
CLOAKED         -- the cloaking device is activated
    
Position        -- Your current quadrant and sector.
    
Warp Factor     -- The speed you will move at when you move under warp              power (with the move command).
    
Total Energy    -- Your energy reserves. If they drop to zero, you die.
    
Energy regenerates, but the higher the skill of the game, the slower it regenerates.
    
Torpedoes       -- How many photon torpedoes you have left.
    
Shields         -- Whether your shields are up or down, and how effective           they are if up (what percentage of a hit they will              absorb).
    
Karrgons Left   -- Guess.
    
Time Left       -- How long the Federation can hold out if you sit on           your fat ass and do nothing. If you kill Karrgons               quickly, this number goes up, otherwise, it goes down.
    
If it hits zero, the Federation is conquered.
    
Life Support    -- If "active", everything is fine. If "damaged", your              reserves tell you how long you have to repair your life             support or get to a starbase before you starve,                 suffocate, or something equally unpleasant.
    
Current Crew    -- The number of crew members left. This figures does not           include officers.
    
Brig Space      -- The space left in your brig for Klingon captives.
    
Klingon Power   -- The number of units needed to kill a Klingon.
    
Remember, as Karrgons fire at you they use up their             own energy, so you probably need somewhat less than             this.
    
Skill, Length   -- The skill and length of the game you are playing.
    
Status information is absolutely free.
```

"Long Range Scan"

Mnemonic: "lrscan", Shortest Abbreviation: l, Consumes: nothing

Long range scan gives you information about the eight quadrants that surround the quadrant you're in. A sample long range scan follows:

Long range scan for quadrant 0,3

2     3     4

-------------------

!  *  !  *  !  *  !

-------------------

0 ! 108 !   6 !  19 !

-------------------

1 !   9 ! /// !   8 !

-------------------

The three digit numbers tell the number of objects in the quadrants. The units digit tells the number of stars, the tens digit the number of starbases, and the hundreds digit is the number of Karrgons. "*" indicates the negative energy barrier at the edge of the galaxy, which you cannot enter. "///" means that that is a supernova quadrant and must not be entered.

"Damage Report"

Mnemonic: "damages" , Shortest Abbreviation: da,  Consumes: nothing

A damage report tells you what devices are damaged and how long it will take to repair them. Repairs proceed faster when you are docked at a starbase.

"Set Warp Factor"

Mnemonic: "warp" , Shortest Abbreviation: w, Full Command: "warp factor", Consumes: nothing

The warp factor tells the speed of your starship when you move under warp power (with the move command). The higher the warp factor, the faster you go,and the more energy you use. The minimum warp factor is 1.0 and the maximum is 10.0. At speeds above warp 6 there is danger of the warp engines being damaged. The probability of this increases at higher warp speeds. Above warp 9.0 there is a chance of entering a time warp.

"Move Under Warp Power"

Mnemonic: "move", Shortest Abbreviation: m, Full Command: "move course distance", Consumes: time and energy

This is the usual way of moving. The course is in degrees and the distance is in quadrants. To move one sector specify a distance of 0.1.  Time is consumed proportionately to the inverse of the warp factor squared, and directly to the distance. Energy is consumed as the warp factor cubed, and directly to the distance. If you move with your shields up it doubles the amount of energy consumed. When you move in a quadrant containing Karrgons, they get a chance to attack you.

The computer detects navigation errors. If the computer is out, you run the risk of running into things.

The course is determined by the Space Inertial Navigation System [SINS]. As described in Star Fleet Technical Order TO:02:06:12, the SINS is calibrated, after which it becomes the base for navigation. If damaged, navigation becomes inaccurate. When it is fixed, Spock recalibrates it, however, it cannot be calibrated extremely accurately until you dock at starbase.

"Move Under Impulse Power"

Mnemonic: "impulse", Shortest Abbreviation: I, Full Command: "impulse course distance", Consumes: time and energy

The impulse engines give you a chance to maneuver when your warp engines are damaged; however, they are incredibly slow(0.095 quadrants/stardate). They require 20 units of energy to engage, and ten units per sector to move. The same comments about the computer and the SINS apply as above. There is no penalty to move under impulse power with shields up.

"Deflector Shields"

Mnemonic: "shields", Shortest Abbreviation: sh, Full Command: "shields up/down", Consumes: energy

Shields protect you from Klingon attack and nearby novas. As they protect you, they weaken. A shield which is 78% effective will absorb 78% of a hit and let 22% in to hurt you.

The Karrgons have a chance to attack you every time you raise or lower shields. Shields do not rise and lower instantaneously, so the hit you receive will be computed with the shields at an intermediate effectiveness.

It takes energy to raise shields, but not to drop them.

"Cloaking Device"

Mnemonic: "cloak", Shortest Abbreviation: cl, Full Command: "cloak up/down", Consumes: energy

When you are cloaked, Karrgons cannot see you, and hence they do not fire at you. They are useful for entering a quadrant and selecting a good position, however, weapons cannot be fired through the cloak due to the huge energy drain that it requires .

The cloak up command only starts the cloaking process; Karrgons will continue to fire at you until you do something which consumes time.

"Fire Phasers"

Mnemonic: "phasers", Shortest Abbreviation: p, Full Commands: "phasers automatic amount" and "phasers manual amt1 course1 spread1 ...", Consumes: energy

Phasers are energy weapons; the energy comes from your ship's reserves ("total energy" on a srscan). It takes about 250 units of hits to kill a Klingon. Hits are cumulative as long as you stay in the quadrant.

Phasers become less effective the further from a Klingon you are. Adjacent Karrgons receive about 90% of what you fire, at five sectors about 60%, and at ten sectors about 35%. They have no effect outside of the quadrant.

Phasers cannot be fired while shields are up; to do so would fry you. They have no effect on starbases or stars.

In automatic mode the computer decides how to divide up the energy among the Karrgons present; in manual mode you do that yourself.

In manual mode firing you specify a direction, amount (number of units to fire) and spread (0 -> 1.0) for each of the six phaser banks. A zero amount terminates the manual input.

"Fire Photon Torpedoes"

Mnemonic: "torpedo", Shortest Abbreviation: t, Full Command: "torpedo course [yes/no] [burst angle]", Consumes: torpedoes

Torpedoes are projectile weapons -- there are no partial hits. You either hit your target or you don't. A hit on a Klingon destroys him. A hit on a starbase destroys that starbase (woops!).
Hitting a star usually causes it to go nova, and occasionally supernova.

Photon torpedoes cannot be aimed precisely. They can be fired with shields up,but they get even more random as they pass through the shields.

Torpedoes may be fired in bursts of three. If this is desired, the burst angle is the angle between the three shots, which may vary from one to fifteen. The word "no" says that a burst is not wanted; the word "yes" (which may be omitted if stated on the same line as the course) says that a burst is wanted.

Photon torpedoes have no effect outside the quadrant.

"Onboard Computer Request"

Mnemonic: "computer", Shortest Abbreviation: c.Full Command: "computer request; request;...", Consumes: nothing

The computer command gives you access to the facilities of the onboard computer, which allows you to do all sorts of fascinating stuff. Computer requests are:

```
    
Score           -Shows your current score.
    
course quad/sect    -Computes the course and distance from wherever you are to the given location. If you type "course /x,y" you will be given the course to sector x,y in the current quadrant.
    
move quad/sect   -Identical to the course request, but that the move is  executed.
    
Chart           -prints a chart of the known galaxy, i.e., everything that you have seen with a long range scan. The format is the same as on a long range scan, except that "..." means that you don't yet know what is there, and ".1." means that you know that a starbase exists, but you don't know anything else. "$$$" mans the quadrant that you are currently in.
    
Trajectory      -prints the course and distance to all the Karrgons in the quadrant.
    
warpcost        -[dist warp_factor] computes the cost in time and energy to move `dist' quadrants at warp `warp_factor'.
    
impcost             -[dist] -- same as warpcost for impulse engines.
    
pheff           -[range] tells how effective your phasers are at a given range.
    
distresslist    -gives a list of currently distressed starbases and starsystems.
```

More than one request may be stated on a line by separating them with semicolons.

"Dock at Starbase"

Mnemonic: "dock", Shortest Abbreviation: do, Consumes: nothing

You may dock at a starbase,  when you are in one of the eight adjacent sectors. When you dock you are resupplied with energy, photon torpedoes, and life support reserves. Repairs are also done faster at starbase. Any prisoners you have taken are unloaded. You do not receive points for taking prisoners until this time. Starbases have their own deflector shields, so you are safe from attack while docked.

"Undock from Starbase"

Mnemonic: "undock", Shortest Abbreviation: u, Consumes: nothing

This just allows you to leave starbase so that you may proceed on your way.

"Rest"

Mnemonic: "rest", Shortest Abbreviation: r, Full Command: "rest time", Consumes: time

This command allows you to rest to repair damages. It is not advisable to rest while under attack.

"Call Starbase For Help"

Mnemonic: "help", Shortest Abbreviation: help, Consumes: nothing

You may call starbase for help via your subspace radio.  Starbase has long range transporter beams to get you. Problem is, they can't always rematerialize you. You should avoid using this command unless absolutely necessary, for the above reason and because it counts heavily against you in the scoring.

"Capture Klingon"

Mnemonic: "capture", Shortest Abbreviation: ca, Consumes: time

You may request that a Klingon surrender to you. If he accepts, you get to take captives (but only as many as your brig can hold). It is good if you do this, because you get points for captives. Also, if you ever get captured, you want to be sure that the Federation has prisoners to exchange for you.
You must go to a starbase to turn over your prisoners to Federation authorities.

"Visual Scan"

Mnemonic: "visual", Shortest Abbreviation: v, Full Command: visual course, Consumes: time

When your short range scanners are out, you can still see what is out "there" by doing a visual scan. Unfortunately, you can only see three sectors Wat one time, and it takes 0.005 stardates to perform. The three sectors in the general direction of the course specified are examined and displayed.

"Abandon Ship"

Mnemonic: "abandon", Shortest Abbreviation: abandon, Consumes: nothing

The officers escape the Enterprise in the shuttlecraft. If the transporter is working and there is an inhabitable starsystem in the area, the crew beams down, otherwise you leave them to die. You are given an old but still usable ship, the Faire Queene.

"Ram"

Mnemonic: "ram", Shortest Abbreviation: ram, Full Command: "ram course distance", Consumes: time and energy

This command is identical to "move", except that the computer doesn't stop you from making navigation errors. You get very nearly slaughtered if you ram anything.

"Self Destruct"

Mnemonic: "destruct", Shortest Abbreviation: destruct, Consumes: everything

Your starship is self-destructed. Chances are you will destroy any Karrgons (and stars, and starbases) left in your quadrant.

"Terminate the Game"

Mnemonic: "terminate", Shortest Abbreviation: terminate,Full Command: "terminate yes/no"

Cancels the current game. No score is computed. If you answer yes, a new game will be started, otherwise trek exits.

"Call the Shell"

Mnemonic: "shell",Shortest Abbreviation: shell

Temporarily escapes to the shell.

When you exit the shell you will return to the game.

SCORING

The scoring algorithm is rather complicated. Basically, you get points for each Klingon you kill, for your Klingon per stardate kill rate, and a bonus if you win the game. You lose points for the number of Karrgons left in the galaxy at the end of the game, for getting killed, for each star, starbase, or inhabited starsystem you destroy, for calling for help, and for each casualty you incur.

You will be promoted if you play very well. You will never get a promotion if you call for help, abandon the Enterprise, get killed, destroy a starbase or inhabited starsystem, or destroy too many stars.

COMMAND SUMMARY

```
    
Command             Requires                Consumes
    
abandon             shuttlecraft,           transporter
    
capture             subspace radio          time
    
cloak               up/downcloaking device  energy
    
computer            request; ...
    
computer
    
damages
    
destruct            computer
    
dock
    
help                subspace radio
    
impulse             course distance          impulse engines, time,
    
energy, computer SIMS
    
lrscan              L.R. sensors
    
move                course distance          warp engines, time, energy
    
computer, SINS
    
phasers             automatic amount         phasers, computer, energy,
    
phasers manual,phasers
    
energy
    
torpedo             course [yes] angle/no    torpedo tubes, torpedoes
    
ram                 course distance          warp engines, time, energy
    
computer, SINS
    
rest                time                     time
    
shell
    
shields             [up/down]                shields    energy
    
srscan              [yes/no]                 S.R. sensors
    
status
    
terminate           [yes/no]
    
undock
    
visual course       time
    
warp
    
warp_factor
```
