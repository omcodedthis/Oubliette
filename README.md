# ![Logo](https://github.com/omcodedthis/Oubliette/assets/119602009/c3dd4cdc-5d42-4548-b76f-492201f411ff) Oubliette
Oubliette is a 2D tile-based world exploration game, complete with its very own world exploration engine made from scratch. The world exploration engine will build a world, which the user will be able to explore by walking around and interacting with objects in that world with an overhead perspective. This project has been adapted from my final project for [CS61B.](https://github.com/omcodedthis/CS61B-Scores)

Each world is pseudorandomly generated & is based on a unique seed which can be decided & saved by the user. It should be able to handle any positive seed up to 9,223,372,036,854,775,807. There is no defined behavior for seeds larger than this. 

The name Oubliette comes from French & is defined as a secret dungeon with access only through a trapdoor in its ceiling, similar to the context of the game. The task is to to collect sixteen orbs and reach the gate within eighty seconds. It is currently hosted on [Itch.io,](https://itch.io/) a game hosting service.

## Demo
https://github.com/omcodedthis/Oubliette/assets/119602009/96fefde3-72dd-43a1-a0b4-114054ecfe07


## Interactivity in Oubliette
This section explains the interactivity aspects of Oubliette in greater detail. The reasoning & the purposes of the classes created are explained in the files itself.

<details>
<summary>View more information on the Title Screen.</summary>

![titlescreen](https://github.com/omcodedthis/Oubliette/assets/119602009/e8a8fa81-0bda-44c8-8982-ab56aa629f7a)

This screen shows the title of the game & the relevant menu options: start a new game, load a previous save or save & quit. If the user chooses to load a new game, the user can input their desired seed. This seed is then used pseudorandomly to generate a world (ie: the same seed will always generate the exact same world each run).

---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
</details>

<details>
<summary>View more information on seed & username inputs.</summary>
  
![seedscreen](https://github.com/omcodedthis/Oubliette/assets/119602009/27019119-a124-4a48-8b52-728ff4ffc4e1)

![namescreen](https://github.com/omcodedthis/Oubliette/assets/119602009/4a46e7e6-ac4b-4d24-9d82-2770a8f3e9a9)

These screens asks the user for their desired seed & username. The seed can be any integer & the user denotes the end of the seed using an 's'. The username can be any valid keyboard input, '.' denotes the end.

---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
</details>

<details>
<summary>View more information on gameplay & controls. </summary>

![gameplayscreen](https://github.com/omcodedthis/Oubliette/assets/119602009/e27352c7-76ab-4a3c-835d-43036d435757)

Using the seed, a world is generated, placing the sacred Orbs & Gate in random positions. The user is then placed on a random FLOOR tile & the countdown timer starts. The user controls the character using the "WASD" keys & picks up the orbs by going towards the tile. Once all the orbs have been collected, the user has to go towards the Gate to win the game within the time limit to view the "Win" screen, where their time & seed is shown. If the user is unable to collect all the orbs & reach the Gate, the "Lose" screen is shown.

This project uses [StdDraw](https://introcs.cs.princeton.edu/java/stdlib/javadoc/StdDraw.html) to handle user input. This results in a couple of limitations:

* StdDraw does not support key combinations. ":Q" means ":" followed by "Q".
* It can only register key presses that result in a char. This means any unicode character will be fine but keys such as the arrow keys and escape will not work.

---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
</details>

<details>
<summary>View more information on the Heads-up display (HUD).</summary>
  
![HUD](https://github.com/omcodedthis/Oubliette/assets/119602009/bf68fdd4-289d-42d7-95de-a00b7d31a6cf)

The HUD provides additional information that is useful to the user. This is split into 4 components. The description of a given tile when the user mouses-over a tile, the username chosen by the user, the time left & the number of orbs collected.

---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
</details>

<details>
<summary>View more information on saving & loading.</summary> 

![savescreen](https://github.com/omcodedthis/Oubliette/assets/119602009/dfddb3ee-a502-469a-8c1f-5e3134102b58)

Oubliette has the ability to save the state of the world while exploring, as well as to subsequently load the world into the exact state it was in when last saved. When the user restarts Oubliette and presses L, the world loaded is exactly the same state as it was before the world was terminated. The command “:Q” saves the data to world_save and completely terminates the program. The user is shown a "successful save" screen upon saving.

---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
</details>

## The World Generation Algorithm Explained

![wgar](https://github.com/omcodedthis/Oubliette/assets/119602009/8b163d97-5217-4fff-8f55-a5b1d08f2391)

Above is a simplified visual representation of how the the Algorithm works, relying on simple principles. The pseudorandomness is created by passing the seed the user chooses to a `Random` object which outputs the sequence of random numbers which is then used to generate the World.

Firstly, the World is split into five sectors. Each sector has one room drawn by default by `drawRoom()` at a random x-coordinate & a 50% chance for a second room to be drawn, decided by `drawSecondRoom()`. Every room's top left coordinates are added to `rooms`,  a `RoomTracker` object. After all the possible rooms for every sector has been drawn, the rooms are connected sequentially using `drawLink()` to generate the hallways. 

The edge cases caused by these functions are rectified using `fixEdgeCases()` before finalising the 2D TETile array in `worldFrame`.

Do note that more expansive Worlds can be generated just by tweaking the constants `ROOMMAX` & `ROOMMIN`, although I have not tested the stability of this yet.

<br>
<details>
<summary><b>View some generated Worlds & their respective seeds.</b></summary>

<br>

**Seed: 43095430**
![w0](https://github.com/omcodedthis/Oubliette/assets/119602009/6109a971-6998-44e3-ae32-a5e997642376)

**Seed: 78459393**
![w1](https://github.com/omcodedthis/Oubliette/assets/119602009/6b794802-4a5b-425f-a8b2-dd74846f96c6)

**Seed: 80923490**
![w2](https://github.com/omcodedthis/Oubliette/assets/119602009/7b61e381-8fe9-45d8-8a40-3e2470f001f0)

**Seed: 39504394**
![w3](https://github.com/omcodedthis/Oubliette/assets/119602009/2a882156-735d-4ff1-bc03-ae5389ae5de1)

**Seed: 14092325**
![w4](https://github.com/omcodedthis/Oubliette/assets/119602009/8a772144-1f1a-4517-88b2-4e1e70ab7a92)

---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
</details>


## Credits
* [Pattern Pixel Pack by Kenny:](https://www.kenney.nl/assets/pattern-pack-pixel) This pack was adapted to create the 16x16 tile assets.

* [StdDraw by Princeton University:](https://introcs.cs.princeton.edu/java/stdlib/javadoc/StdDraw.html) This was used to render the world & used as an interface for user input. 

* [The Archon tile from NetHack's Wiki:](https://nethackwiki.com/wiki/File:Archon.png) This 16x16 asset was used to create the character used in Oubliette.


## Getting Started
* Download the game from [Oubliette's Itch.io page.](https://oubliettegame.itch.io/oubliette)

* Download this repository & run this project using an IDE, such as [IntelliJ IDEA.](https://www.jetbrains.com/idea/)


## My Thoughts
Oubliette provided a point of growth, as the goal of this project was to teach myself how to handle a larger piece of code with little starter code in the hopes of emulating something like a product development cycle. Since there is no notion of “the correct answer” when it comes to world design and implementation, a great deal of exploration and experimentation was required for such an open-ended project. I learnt that it is ok and expected to go through several iterations before settling on something that I deemed good. That is, this project is about the fundamentals of software engineering.
