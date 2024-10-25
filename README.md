# 2048-java

- Clone of the Popular Mobile Game [2048](https://en.wikipedia.org/wiki/2048_(video_game)#Gameplay)
- **Grid**: 4x4
- **Tiles**: 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048
- **Start**: Two randomly placed tiles with a value of 2 or 4
- **Move (up/down/left/right)**: All tiles shift until they hit another tile or the edge of the grid
    - If two tiles with the same number collide during a move, they merge into a new tile with the sum of the two tiles.
    - After each move, a new tile with a value of 2 or 4 appears randomly.
- **Game Over**
    - **Loss**: The grid is full, and no more moves can be made (no adjacent tiles with the same value).
    - **Win**: A tile with the number 2048 is created.
- **Controls**: Arrow keys
- **Graphics**: JavaFX