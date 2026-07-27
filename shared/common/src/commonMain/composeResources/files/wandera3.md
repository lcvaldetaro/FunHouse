Wander Adventure Game

```
    You are traveling as  First Under-secretary to the Ambassador for
    the   Corps   Diplomatique  Terrestrienne,  (CDT).   Your  direct
    superior, Mr. Magnan, has managed to duck out of the  action  and
    leave   you   as  sole  assistant  to  his  superior,  Ambassador
    Pouncetrifle.  (The Ambassador is a classic bungler and would, if
    left on his own, mess things up badly.)
```

```
    You have been sent to Aldebaran III where you  are  to  avert  an
    uprising against Terran nationals expected at the end of April.
```

```
    During your trip you  were  able  to  peruse  the  ship's  meager
    library  and  make  a  few  notes  on the history, life-forms and
    society of Aldebaran III, but much of Aldebaran culture is  still
    a mystery.
```

```
    It is the middle of the night; the ship  on which you arrived has
    just departed from the small spaceport which you find to be windy
    and deserted.
```

Commands

Wander includes several built-in commands.  Aside from recognizing the
standard compass directions (and their abbreviations),
and "up" and "down",

The following commands are recognized:

```
    
inventory   list objects being carried
    
take        pick up specified object
    
drop        drop specified object
    
quit        stop playing
    
save        quit & save environment for later continuation
    
restore     restore saved environment
    
look        print the long description of the current location
    
init        read new .wrld & .misc files, (switch worlds)
    
```

```
Whenever the word "all" is encountered as the second recognized
    word of user input on a line it will be macro expanded.  This expansion
    replaces the word "all" with each object in the current location including
    objects being carried.  E.g. "drop all" may expand to "drop keys; drop net;
    drop leaflet".
```
