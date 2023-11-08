# ChargeSimulator
2D charge simulator

Red charges are attractive, and blue charges are repulsive. They create a field landscape that the green test charges glide along. 
The green charges get brighter when they move faster.

**To get started, simply clone the project and run `charge-simulator.jar` in the root directory.**

## Controls
- `Left click (& drag)` - place static blue repulsive charge
- `Right click (& drag)` - place static red attractive charge
- `Middle click (& drag)` - place green test charge which moves but doesn't contribute to the field
- `Mouse wheel` - zoom in and out
- `Left click and drag` - move the viewport around, only when in move mode (toggled by `M`)
---
- `B` - clear all blue charges
- `R` - clear all red charges
- `G` - clear all green charges
- `X` - clear all charges
- `T` - toggle all red charges into blue charges and vice versa
- `M` - toggle move mode on and off
- `Z` - undo the last placement action
- `Y` - redo the action that was last undone
- `O` - switch through different particle display sizes
---
- `1` - place random green charges around the screen
- `2` - place a grid of blue charges on the screen
- `3` - place a random set of red, blue, and green charges around the screen
- `4` - place a circle of red charges in the middle of the screen (using radial count param)
- `5` - place a grid of green charges around the screen
  - `Shift + 5` - increase grid density
  - `Ctrl + 5` - even more grid density
- `6` - place a circle of green charges in the middle of the screen (using radial count param)
---
- `[` - reduce physics speed
- `]` - increase physics speed
- `,` - decrease radial count param
- `.` - increase radial count param
- `Spacebar` - pause/play simulation
- `-` - reduce rendering frequency to only every [1, 2, 4, 8] physics update frames
- `=` - increase rendering frequency
- `D` - toggle between drawing every [1, 2, 4] frames
- `P` - toggle field grid visualization (may be very slow)
---
- `C` - input a command string to execute
  - the format is `XX,Y` where XX is the number of times to execute a command, and Y is the command itself
  - e.g., a command string of `10,6` will run the `6` command (placing a circle of green charges) 10 times, in 10 successive frames, leading to trails of test charges following each other
- `Ctrl + S` - Save snapshot
- `Ctrl + L` - Load snapshot
- `Ctrl + Shift + L` - Load autosaved snapshot (Autosave happens every 60s)



## Screenshots & Videos

![2023-08-18_11-12-56](https://github.com/awjc/ChargeSimulator/assets/2312902/2b070438-a6bd-40e5-a0f4-394abd61da40)

![image](https://github.com/awjc/ChargeSimulator/assets/2312902/f752161a-ddac-4859-887d-c781767b2553)

https://github.com/awjc/ChargeSimulator/assets/2312902/7e496a66-068c-4e13-b90e-0c65cef7aad4

https://github.com/awjc/ChargeSimulator/assets/2312902/699d2f3f-b72c-45c1-aadd-ab24321e66d1

https://github.com/awjc/ChargeSimulator/assets/2312902/789ef3ac-ec09-4b9b-8bea-a11d4b7b7b7c

https://github.com/awjc/ChargeSimulator/assets/2312902/f9cbca2e-59cf-468f-83a9-0219887d8423

