# WinampStreamDisplay
Displays the currently playing song in Winamp on a nice, easily-capturable window for OBS/XSplit/etc.

The properties file has the following fields:

height      height of the window, including the title bar

width       width of the window

bgcolor     background color, in 24bit, decimal format (0=black, 16777215=white)

fgcolor     foreground color, in 24bit, decimal format (0=black, 16777215=white)

font        name of the font to use

fontSize    size of the font to use

scrollSpeed scroll speen, 30 is maximum

Uses Java Winamp API (http://sourceforge.net/projects/javawinampapi/), which is released under the public domain.
