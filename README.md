# WinampStreamDisplay
Displays the currently playing song in Winamp on a nice, easily-capturable window for OBS/XSplit/etc.

## Using

I have included the compiled WinampStreamDisplay.jar - if you're not interested in the source and just want the program, you want this, wpcom.dll and wsd.properties. Run the program as follows:

`java -jar WinampStreamDisplay.jar`

If you get a `java.lang.UnsatisfiedLinkError: Can't load IA 32-bit .dll on a AMD 64-bit platform` error, make sure to use a 32bit JRE/JDK to run, as the wpcom.dll file is 32bit.

## Configuration

The wsd.properties file has the following fields:

height      height of the window, including the title bar

width       width of the window

bgcolor     background color, in 24bit, decimal format (0=black, 16777215=white)

fgcolor     foreground color, in 24bit, decimal format (0=black, 16777215=white)

font        name of the font to use

fontSize    size of the font to use

scrollSpeed scroll speen, 30 is maximum

## Other

Uses Java Winamp API (http://sourceforge.net/projects/javawinampapi/), which is released under the public domain.
