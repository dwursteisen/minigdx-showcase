---
layout: showcase
title: 2D Platformer
description: Simple platformer in 2D
game: demo-twod
---

# Controls

- ⎵ to jump
- ← and → to move

# Description

2D games can be created using miniGDX.
The level is composed of two distinct layers : the background and the foreground.
The latest contains the player and the coins.

Collisions are managed using an AABB collision resolver, to check if the player 
is in contact with a coin. It also use a Ray resolver to check if the player 
has a platform under it's feet. In no, then the player will fall.
