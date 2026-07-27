```
Through the wonder of Wander, you are going to explore the remains
    of a world after Chaos has had its way with it. There are treasures
    to be had here, but there are also undreamed of dangers. The ghosts
    of the people who once ruled this world are there still, and the
    products of their godlike meddling have survived them. Be cautious,
    daring, and sneaky.
```

Commands

```
Wander includes several built-in commands.  Aside from recognizing the
    standard compass directions (and their abbreviations),
    and "up" and "down",
```

```
    
The following commands are recognized:
    
```

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
