```
    Well, G'Day mate, all you SynTax readers out there sling another
    prawn on the barbie, down another can of XXXX, and listen to my
    review of this aggressively Australian adventure - with a title
    like "Dinkum", you could hardly expect anything else, now could
    you? The initial above ground locations in this tale of
    treasure-seeking and derring-do are relentlessly, determinedly
    Australian in flavour; and although the author appears to be
    Australian (he lives in Queensland), he mucks up the spelling of
    both `kangaroo' and `witchetty grub' in the introduction and first
    few rooms in the game. Oh dear. But wait, hang on half a mo...Yes!
    He gets `taipan' and `kookaburra' right! (Good on yer, mate!)
```

```
    Basically you have to discover for yourself the aim of this game
    as you play your way through it - at the beginning you are told
    only that Dinkum is an Australian adventure game, where you'll
    search for treasure in the Australian Outback. After wandering
    around for a while through randomly-changing scenery you'll
    eventually stumble across the old Acme gold mine and premises (if
    you head roughly east-ish). Now mined out and deserted, the gold
    mine has some dilapidated office buildings to the north, a mine
    lift to the south, and a network of tunnels and old shafts
    underground.
```

```
    In the office buildings you'll discover a safe, now empty, and a
    map of one of the three levels of the mine still accessible by the
    mine lift. A note tells you that you'll have to return with any
    treasures you discover in the mine and deposit them in the safe
    for points; and that it is vital to collect gleeps and store them
    in gleep tanks somewhere in the mines so they can reproduce. (No,
    I don't know what gleeps are either). The ability to collect and
    cultivate gleeps is essential to win the game.
```

```
    So it's off on another trot through 3 levels of underground
    tunnels, slaughter any monsters with the appropriate weapon, find
    some gleeps and store them in the gleep tank, collect the
    treasures and totter back to the office to deposit them in the
    safe. Funny thing though, the first treasure I found was an
    emerald, where I'd assumed the obvious valuables to be found in a
    gold mine would be...err, let me take a wild guess here...various
    objects made from gold. Ah well, ours not to question why, ours
    but to do and die...and die....and die. By this stage you'll
    probably be gritting your teeth viciously, and willing to do
    (almost) anything for the usual SAVE command instead of the fiddly
    data recorder. Oops. I've got a bit ahead of myself here, but read
    on to discover the function of the data recorder in Dinkum.
```

```
                Comments on the Revised Version of Dinkum
                              5 January 1993
```

```
This version  of  Dinkum  (Ver.  2.3)  has  several  slight  improvements
perceptible to the user and significant improvements perceptible  to  the
programmer.   Because  the  improvements  are  slight  from  the   user's
perspective, a non-programming user should not delete the  older  version
of Dinkum until it has been established  that  this  newer  version  will
compile on the user's computer.  The user will  notice  some  minor  bugs
corrected and there being more "Australia content" in the  above  ground,
pre-game component of Dinkum.  There were some fairly  glaring  omissions
in the earlier version of the game  concerning  Australia  content,  i.e.
there is a road in Dinkum but the earlier version had no  road trains  or
dead kangaroos (the  two  most  striking  characteristics  of  Australian
roads).  Many people complained about the  lack  of  a  Save  command  in
Dinkum.  My original omission of the Save command  was  deliberate.   The
two main reasons why people play a text adventure game is either to be  a
an active  participant  in  an  action/fantasy  story  or  to  experience
communicating through natural language to  a  computer.   Both  of  these
aspects are defeated by using a Save command.  However my  impression  is
that many people insist on having a Save command and won't play the  game
if it isn't there.  Consequently I've come up with a compromise.  If  you
start the game by typing "dinkum -s" rather than simply "dinkum" then the
game creates a "data recorder" which appears  in  the  beginning  of  the
game.  The data recorder acts like a tape recorder allowing the  user  to
save moves and play them  back  in  the  current  or  later  games.   The
generated file is in ASCII format  and  can  be  edited  after  finishing
Dinkum.  Type "examine recorder" after having taken it  and  Dinkum  will
explain how to use it.  This data recorder  has  a  couple  of  important
limitations:  Unlike most adventure games, Dinkum is  dynamic,  viz.  the
game changes every time you play it.  So if you record  a  script  for  a
game which takes you to a room where  you  find  a  weapon  and  then  to
another room where you find a monster, then the next time  you  use  that
script you may find the monster where you  previously  found  the  weapon
(leading to a fairly short game).   The  other  limitation  of  the  data
recorder is if you use the  "dinkum -s"  switch  then  you  will  not  be
admitted into the end game with  the  consequence  being  that  "winning"
Dinkum is impossible.  To win Dinkum you  must  play  it  "on  your  own"
without the unfair advantage of script files and a data recorder.
```

```
The central layout of the game  from  the  user's
perspective should remain unchanged.  Improvements should  focus  instead
on enhancing natural language recognition, for example:  Dinkum  can  not
currently support compound sentences, i.e. "Take the whidjitty grub after
lifting the rock and eat the  grub".   Dinkum  does  not  really  support
questions, i.e. a character appears and the user asks the character  "How
do I unlock the door?".  Questions represent an entirely different syntax
from imperative commands.  Also question answering capability would allow
for inclusion of an ELIZA type subroutine for responding to questions not
accounted for in the game's context.  This could allow for the game going
through many question/answer  cycles  before  failing  the  Turing  test.
There are many words and synonyms that Dinkum  does  not  recognize  even
though these words appear in the game.  Anyway these are the improvements
I would like to see in Dinkum and over the years will probably write them
in myself if someone else doesn't beat me  to  it.   Any  suggestions  or
software contributions along these lines would be gratefully received.
```

                            Gary A. Allen, Jr.
