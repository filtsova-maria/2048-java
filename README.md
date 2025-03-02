# 2048-java project description

- A clone of the popular mobile game [2048](https://en.wikipedia.org/wiki/2048_(video_game)#Gameplay).
- **Grid**: 4x4.
- **Tiles**: 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048.
- **Start**: Two randomly placed tiles with a value of 2 or 4.
- **Move (up/down/left/right)**: All tiles shift until they hit another tile or the edge of the grid
    - If two tiles with the same number collide during a move, they merge into a new tile with the sum of the two tiles.
    - After each move, a new tile with a value of 2 or 4 appears randomly.
- **Game ending**
    - **Loss**: The grid is full, and no more moves can be made (no adjacent tiles with the same value).
    - **Win**: A tile with the number 2048 is created.
- **Controls**: Arrow keys.
- **Graphics**: JavaFX.

# User Documentation

## Game Features

### Navigation

- **Main Menu**: From the main menu, you can start a new game, view high scores, or exit the game.
- **In-Game**: During the game, you can move tiles using the arrow keys. You can also restart the game or return to the
  main menu using the buttons provided.
- **High Scores**: View the top scores from the high scores menu. You can return to the main menu from here.

### Game Modes

- **Standard Mode**: The classic 2048 game where you combine tiles to reach the 2048 tile. You can choose a 4x4, 5x5, or
  6x6 grid size, larger grids are easier to win.
- **Automatic Solver**: On any board you can activate an automatic solver that makes moves for you every second. You can
  start or pause the solver using the "Start Solver"/"Pause Solver" button.
    - **Solver Behavior**: The solver analyzes the board and takes the first mergeable direction it finds. If no merges
      are possible, it chooses a random direction.

### Score Display and Saving

- **Score Saving**: When the game ends (either by winning or losing), the score is saved along with the date and time.
  The top 3 scores are displayed on both winning and losing screens. All the high scores can be viewed from the main
  menu.
    - **Score Animation**: When the score increases, the score text scales up and then returns to its original size. The
      amount of scaling depends on the score change.
    - **Score Colors**: The color of the score text changes based on the current score. The higher the score, the more
      intense the red color becomes. This is determined by the ratio of the score to a predefined top score value.
- **Score Display**: The current score is displayed at the top left of the game screen. The score updates dynamically as
  you play.

