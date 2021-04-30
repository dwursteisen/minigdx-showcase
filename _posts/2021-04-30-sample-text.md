---
layout: showcase
title: Text animation
description: Display text and animate it
game: sampleText
---

# Controls

- ⎵ to change text alignment (left, center, right)

# Description

Write some text on the screen and animate it. 
The animation is created using a `TextEffect`. Some of the effect can be compinated. 
For example: 

````kotlin
val wave = WaveEffect(WriteText("Wave Effect"))
val typewriter = TypeWriterEffect(WriteText("Wave Effect"))
val combined = TypeWritterEffect(WaveEffect(WriteText("Hello World!")))
````
