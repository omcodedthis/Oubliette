# ![Logo](https://github.com/omcodedthis/Oubliette/assets/119602009/c3dd4cdc-5d42-4548-b76f-492201f411ff) Oubliette
Oubliette is a a 2D tile-based world exploration game, complete with its very own world exploration engine. The world exploration engine will build a world, which the user will be able to explore by walking around and interacting with objects in that world with an overhead perspective. 

Each world is pseudorandomly generated & is based on a unique seed which can be decided & saved by the user. The name Oubliette is french for a secret dungeon with access only through a trapdoor in its ceiling similar to the context of the game. The task is to to collect sixteen orbs and reach the gate within eighty seconds. It is currently hosted on [Itch.io,](https://itch.io/) a game hosting service.

## Demo
https://github.com/omcodedthis/Oubliette/assets/119602009/17a36234-9d0d-470b-943c-e5965fead78e


## Interactivity in Oubliette
<details>
<summary>View more information on the Title Screen.</summary>

![titlescreen](https://github.com/omcodedthis/Oubliette/assets/119602009/e8a8fa81-0bda-44c8-8982-ab56aa629f7a)

This screen shows the title of the game & the relevant menu options: start a new game, load a previous save or save & quit. If the user chooses to load a new game, the user can input their desired seed. This seed is then used pseudorandomly to generate a world (ie: the same seed will always generate the exact same world each run).

----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
</details>

<details>
<summary>View more information on seed & username inputs.</summary>
  
![seedscreen](https://github.com/omcodedthis/Oubliette/assets/119602009/27019119-a124-4a48-8b52-728ff4ffc4e1)

![namescreen](https://github.com/omcodedthis/Oubliette/assets/119602009/4a46e7e6-ac4b-4d24-9d82-2770a8f3e9a9)

These screens asks the user for their desired seed & username. The seed can be any integer & the user denotes the end of the seed using an 's'. The username can be any valid keyboard input, '.' denotes the end.

----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
</details>

<details>
<summary>View more information on gameplay & controls. </summary>

![gameplayscreen](https://github.com/omcodedthis/Oubliette/assets/119602009/e27352c7-76ab-4a3c-835d-43036d435757)

Using the seed, a world is generated, placing the sacred Orbs & Gate in random positions. The user is then placed on a random FLOOR tile & the countdown timer starts. The user controls the character using the "WASD" keys & picks up the orbs by going towards the tile. Once all the orbs have been collected, the user has to go towards the Gate to win the game within the time limit to view the "Win" screen, where their time & seed is shown. If the user is unable to collect all the orbs & reach the Gate, the "Lose" screen is shown.

This project uses [StdDraw](https://introcs.cs.princeton.edu/java/stdlib/javadoc/StdDraw.html) to handle user input. This results in a couple of limitations:

* StdDraw does not support key combinations. ":Q" means ":" followed by "Q".
* It can only register key presses that result in a char. This means any unicode character will be fine but keys such as the arrow keys and escape will not work.

----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
</details>

<details>
<summary>View more information on the Heads-up display (HUD).</summary>
  
![HUD](https://github.com/omcodedthis/Oubliette/assets/119602009/bf68fdd4-289d-42d7-95de-a00b7d31a6cf)

The HUD provides additional information that is useful to the user. This is split into 4 components. The description of a given tile when the user mouses-over a tile, the username chosen by the user, the time left & the number of orbs collected.

----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
</details>

<details>
<summary>View more information on saving & loading.</summary> 

![savescreen](https://github.com/omcodedthis/Oubliette/assets/119602009/dfddb3ee-a502-469a-8c1f-5e3134102b58)

Oubliette has the ability to save the state of the world while exploring, as well as to subsequently load the world into the exact state it was in when last saved. When the user restarts Oubliette and presses L, the world loaded is exactly the same state as it was before the world was terminated. The command “:Q” saves the data to world_save and completely terminates the program. The user is shown a "successful save" screen upon saving.

----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
</details>

## The World Generation Algorithm Explained

![wgar](https://github.com/omcodedthis/Oubliette/assets/119602009/60643e32-0044-430c-9bac-e90734d2d630)

Above is a simplified visual representation of how the the Algorithm works, relying on simple principles. 

Firstly, the World is split into five sectors. Each sector has one room drawn by default by `drawRoom()` at a random x coordinate & a 50% chance for a second room to be drawn, where the outcome is decided by `drawSecondRoom()`. Every room's top left coordinates are added to a `RoomTracker` object. After all the possible rooms for every sector has been drawn, the rooms are connected sequentially using `drawLink()` to generate the hallways. 

The edge cases caused by these functions are rectified using `fixEdgeCases()` before finalising the 2D TETile array in `worldFrame`.


## Credits


## Getting Started


## My Thoughts


# _WORK IN PROGESS._
