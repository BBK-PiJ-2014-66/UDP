# Audio files for the letters A, B and C. 

Originally downloaded from 
[https://evolution.voxeo.com/library/audio/prompts/alphabet/index.jsp]([https://evolution.voxeo.com/library/audio/prompts/alphabet/index.jsp)
and then converted from `.wav` to `.aiff` format using Linux [sox](http://sox.sourceforge.net/soxformat.html)


# 9 seconds of Bach

Downloaded http://en.wikipedia.org/wiki/File:JOHN_MICHEL_CELLO-J_S_BACH_CELLO_SUITE_1_in_G_Prelude.ogg and created separate audio
files for each of the first 9 second by:

```
$ wget http://upload.wikimedia.org/wikipedia/commons/4/43/JOHN_MICHEL_CELLO-J_S_BACH_CELLO_SUITE_1_in_G_Prelude.ogg
$ sox JOHN_MICHEL_CELLO-J_S_BACH_CELLO_SUITE_1_in_G_Prelude.ogg Bach%1n.wav \
  rate 12k trim 0 1 : newfile : \
  rate 12k trim 0 1 : newfile : \
  rate 12k trim 0 1 : newfile : \
  rate 12k trim 0 1 : newfile : \
  rate 12k trim 0 1 : newfile : \
  rate 12k trim 0 1 : newfile : \
  rate 12k trim 0 1 : newfile : \
  rate 12k trim 0 1 : newfile : \
  rate 12k trim 0 1 : newfile : \
  rate 12k trim 0 1 : newfile 
```

Reduced sampling rate to 12k to ensure files are smaller than 64k for data transmission.

