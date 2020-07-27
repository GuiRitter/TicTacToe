#include <LiquidCrystal.h>

// LiquidCrystal lcd(RS, EN, D4, D5, D6, D7);
LiquidCrystal lcd(8, 9, 10, 11, 12, 13);

#define interval 93

// warning
// all optimal playing methods refer to the human player as 'opponent'
// all other methods refer to the human player as 'player'

// amount of squares that conform to the square condition
int amountMaskHits = 0;

// amount of opponents that share a border with a square
int amountOpponentsBorderingPosition = 0;

int board[3][3] = {
  {0, 0, 0},
  {0, 0, 0},
  {0, 0, 0}};

enum Choice {
  EXIT,
  P1,
  P2,
  P3,
  P4,
  P5,
  P6,
  P7,
  P8,
  P9,
  PvC,
  PvP,
  RESET};

Choice choice = PvC;

// whether a column's squares contains two tokens of a given player and a
// blank square
boolean doubleColumn = false;

// whether a line's squares contains two tokens of a given player and a
// blank square
boolean doubleLine = false;

// whether the primary diagonal's squares contains two tokens of a given
// player and a blank square
boolean doublePrimaryDiagonal = false;

// whether the secondary diagonal's squares contains two tokens of a given
// player and a blank square
boolean doubleSecondaryDiagonal = false;

// iterator and/or position coordinate
int i = 0;

// iterator and/or position coordinate
int j = 0;

Choice lastChoice = PvC;

// iterator
int m = 0;

boolean mainMenu = false;

// which squares conform to the square condition
boolean mask[3][3] = {
  {false, false, false},
  {false, false, false},
  {false, false, false}};

int move = 0;

// iterator
int n = 0;

boolean playingGame = false;

boolean playingGames = false;

// potentiometer value
int pot = 0;

// conditions used by the random blank square methods to select a square
enum SquareCondition {
  
  // corner square
  CORNER,
  
  // corner square bordering only one X
  CORNER_BORDER_1_X,
  
  // edge square
  EDGE,
  
  // No condition
  NONE,
  
  // Square that completes a winning move
  WIN
};

// false if this player plays as X, true
boolean team = false;

// iterator
int x = 0;

// prints who won or if the game tied
// also plays the last move
// then tests for 
void checkGameState () {
  if (((!team) && hasXWon()) || (team && hasOWon())) {
    printComputerWon();
  } else if (((!team) && hasOWon()) || (team && hasXWon())) {
    printPlayerWon();
  } else if (move == 9) {
    getBlankSquare();
    board[i][j] = 9;
    printBoard();
    if (((!team) && hasXWon()) || (team && hasOWon())) {
      printComputerWon();
    } else if (((!team) && hasOWon()) || (team && hasXWon())) {
      printPlayerWon();
    } else {
      printTie();
    }
  } else {
    printCursorConfigurePosition();
  }
}

// how much opponents borders the square
int getAmountOpponentsBorderingSquare () {
  amountOpponentsBorderingPosition = 0;
  if ((i > 0) && isOpponent(board[i - 1][j])) {
    amountOpponentsBorderingPosition++;
  }
  if ((i < 2) && isOpponent(board[i + 1][j])) {
    amountOpponentsBorderingPosition++;
  }
  if ((j > 0) && isOpponent(board[i][j - 1])) {
    amountOpponentsBorderingPosition++;
  }
  if ((j < 2) && isOpponent(board[i][j + 1])) {
    amountOpponentsBorderingPosition++;
  }
  return amountOpponentsBorderingPosition;
}

// configures the coordinates of the first blank corner square. calling this
// method means that the desired square is actually the last corner. this is
// preferable to ramdomly find a square when there's only one available
void getBlankCornerSquare () {
  for (i = 0; i < 3; i += 2) {
    for (j = 0; j < 3; j += 2) {
      if (isBlankSquare()) {
        return;
      }
    }
  }
}

// configures the coordinates to the blank corner square bordering p opponents
void getBlankCornerSquareBorderOpponents (int p) {
  for (i = 0; i < 3; i += 2) {
    for (j = 0; j < 3; j += 2) {
      if (isBlankSquare() && (getAmountOpponentsBorderingSquare() == p)) {
        return;
      }
    }
  }
}

// configures the coordinates to the first blank square
void getBlankSquare () {
  for (i = 0; i < 3; i++) {
    for (j = 0; j < 3; j++) {
      if (board[i][j] == 0) {
        return;
      }
    }
  }
}

// configures the coordinates to the first blank square that blocks a win
boolean getBlockingSquare () {
  for (i = 0; i < 3; i++) {
    for (j = 0; j < 3; j++) {
      if (isBlockingSquare()) {
        return true;
      }
    }
  }
  return false;
}

// configures the coordinates of the square that is on the opposite side of
// the square that contains the passed move.
void getOppositeSquare (int p) {
  for (i = 0; i < 3; i++) {
    for (j = 0; j < 3; j++) {
      if (board[i][j] == p) {
        i = 2 - i;
        j = 2 - j;
        return;
      }
    }
  }
}

void getPotSetChoice () {
  pot = analogRead(0);
  if (pot < interval) {
    choice = P1;
  } else if (pot < (2*interval)) {
    choice = P2;
  } else if (pot < (3*interval)) {
    choice = P3;
  } else if (pot < (4*interval)) {
    choice = P4;
  } else if (pot < (5*interval)) {
    choice = P5;
  } else if (pot < (6*interval)) {
    choice = P6;
  } else if (pot < (7*interval)) {
    choice = P7;
  } else if (pot < (8*interval)) {
    choice = P8;
  } else if (pot < (9*interval)) {
    choice = P9;
  } else if (pot < (10*interval)) {
    choice = RESET;
  } else if (pot < (11*interval)) {
    choice = EXIT;
  }
}

// configures the coordinates to a random blank square that conforms to a
// square condition
boolean getRandomBlankSquare (/*enum SquareCondition*/int condition) {
  amountMaskHits = 0;
  for (i = 0; i < 3; i++) {
    for (j = 0; j < 3; j++) {
      if (isBlankSquare()
       && ((condition == NONE)
       || (isCornerSquare()
       && ((condition == CORNER)
       || ((condition == CORNER_BORDER_1_X)
       && (getAmountOpponentsBorderingSquare() == 1))))
       || ((condition == EDGE)
       && isEdgeSquare())
       || ((condition == WIN)
       && isWinSquare()))) {
        mask[i][j] = true;
        amountMaskHits++;
      } else {
        mask[i][j] = false;
      }
    }
  }
  if (amountMaskHits > 0) {
    if (amountMaskHits == 1) {
      for (i = 0; i < 3; i++) {
        for (j = 0; j < 3; j++) {
          if (mask[i][j]) {
            return true;
          }
        }
      }
    } else {
      x = random(1, amountMaskHits + 1);
      for (i = 0; i < 3; i++) {
        for (j = 0; j < 3; j++) {
          if (mask[i][j]) {
            if (x == 1) {
              return true;
            } else {
              x--;
            }
          }
        }
      }
    }
  } else {
    return false;
  }
  return false;
}

// whether the square is blank
boolean isBlankSquare () {
  return (isMoveBlank(board[i][j]))?true:false;
}

// whether the square will block the opponent's win
boolean isBlockingSquare () {
  // secondary diagonal
  doubleSecondaryDiagonal = false;
  if (i == j) {
    doubleSecondaryDiagonal = true;
    for (x = 0; x < 3; x++) {
      if ((x != i) && (!isOpponent(board[x][x]))) {
        doubleSecondaryDiagonal = false;
        break;
      }
    }
  }
  if (!doubleSecondaryDiagonal) {
    // primary diagonal
    doublePrimaryDiagonal = false;
    if ((2 - i) == j) {
      doublePrimaryDiagonal = true;
      for (x = 0; x < 3; x++) {
        if ((x != j) && (!isOpponent(board[2 - x][x]))) {
          doublePrimaryDiagonal = false;
          break;
        }
      }
    }
    if (!doublePrimaryDiagonal) {
      // line
      doubleLine = true;
      for (x = 0; x < 3; x++) {
        if ((x != i) && (!isOpponent(board[x][j]))) {
          doubleLine = false;
          break;
        }
      }
      if (!doubleLine) {
        // column
        doubleColumn = true;
        for (x = 0; x < 3; x++) {
          if ((x != j) && (!isOpponent(board[i][x]))) {
            doubleColumn = false;
            break;
          }
        }
      }
    }
  }
  return ((doubleSecondaryDiagonal || doublePrimaryDiagonal
   || doubleColumn || doubleLine) && isBlankSquare());
}

// whether the square is at the board's corner
boolean isCornerSquare () {
  return (((i % 2) == 0) && ((j % 2) == 0))?true:false;
}

// whether the square is at the board's edge
boolean isEdgeSquare () {
  return (abs(j - i) == 1)?true:false;
}

// whether the move was made at the center square
boolean isMoveAtCenter (int p) {
  return (board[1][1] == p)?true:false;
}

// whether the move was made at a corner square
boolean isMoveAtCorner (int p) {
  return ((board[0][0] == p) || (board[2][0] == p)
   || (board[0][2] == p) || (board[2][2] == p))?true:false;
}

// whether the move was made at an edge square
boolean isMoveAtEdge (int p) {
  return ((board[1][0] == p) || (board[0][1] == p)
   || (board[2][1] == p) || (board[1][2] == p))?true:false;
}

// whether the move haven't been made
boolean isMoveBlank (int p) {
  return (p == 0)?true:false;
}

// whether O made this move
boolean isMoveO (int p) {
  return ((!isMoveBlank(p)) && ((p % 2) == 0))?true:false;
}

// whether X made this move
boolean isMoveX (int p) {
  return ((!isMoveBlank(p)) && ((p % 2) != 0))?true:false;
}

// whether the numbers are bordering each other
boolean isMovesBorderingEachOther (int a, int b) {
  for (i = 0; i < 3; i++) {
    for (j = 0; j < 3; j++) {
      if (board[i][j] == a) {
        goto found;
      }
    }
  }
  found:
  for (m = 0; m < 3; m++) {
    for (n = 0; n < 3; n++) {
      if (board[m][n] == b) {
        if (i == m) {
          if ((j + 1) == n) {
            return true;
          } else if ((j - 1) == n) {
            return true;
          }
        } else if (j == n) {
          if ((i + 1) == m) {
            return true;
          } else if ((i - 1) == m) {
            return true;
          }
        }
        return false;
      }
    }
  }
  return false;
}

// whether the move was made by the opponent
boolean isOpponent (int p) {
  return (team)?(isMoveX(p)):(isMoveO(p));
}

// whether the position of the two moves are at opposite sides
boolean isOppositeSquares (int a, int b) {
  for (i = 0; i < 3; i++) {
    for (j = 0; j < 3; j++) {
      if ((board[i][j] == a) && (board[2 - i][2 - j] == b)) {
        return true;
      }
    }
  }
  return false;
}

// whether the move was made by this player
boolean isPlayer (int p) {
  return (team)?(isMoveO(p)):(isMoveX(p));
}

// whether the square will make a win when played
boolean isWinSquare () {
  // secondary diagonal
  doubleSecondaryDiagonal = false;
  if (i == j) {
    doubleSecondaryDiagonal = true;
    for (x = 0; x < 3; x++) {
      if ((x != i) && (!isPlayer(board[x][x]))) {
        doubleSecondaryDiagonal = false;
        break;
      }
    }
  }
  if (!doubleSecondaryDiagonal) {
    // primary diagonal
    doublePrimaryDiagonal = false;
    if ((2 - i) == j) {
      doublePrimaryDiagonal = true;
      for (x = 0; x < 3; x++) {
        if ((x != j) && (!isPlayer(board[2 - x][x]))) {
          doublePrimaryDiagonal = false;
          break;
        }
      }
    }
    if (!doublePrimaryDiagonal) {
      // line
      doubleLine = true;
      for (x = 0; x < 3; x++) {
        if ((x != i) && (!isPlayer(board[x][j]))) {
          doubleLine = false;
          break;
        }
      }
      if (!doubleLine) {
        // column
        doubleColumn = true;
        for (x = 0; x < 3; x++) {
          if ((x != j) && (!isPlayer(board[i][x]))) {
            doubleColumn = false;
            break;
          }
        }
      }
    }
  }
  return ((doubleSecondaryDiagonal || doublePrimaryDiagonal
   || doubleColumn || doubleLine) && isBlankSquare());
}

boolean hasOWon() {
  if (isMoveO(board[0][0]) && isMoveO(board[1][1]) && isMoveO(board[2][2])) {
    return true;
  } else if (isMoveO(board[0][2]) && isMoveO(board[1][1]) && isMoveO(board[2][0])) {
    return true;
  } else {
    for (x = 0; x < 3; x++) {
      if (isMoveO(board[x][0]) && isMoveO(board[x][1]) && isMoveO(board[x][2])) {
        return true;
      } else if (isMoveO(board[0][x]) && isMoveO(board[1][x]) && isMoveO(board[2][x])) {
        return true;
      }
    }
  }
  return false;
}

boolean hasXWon() {
  if (isMoveX(board[0][0])&& isMoveX(board[1][1]) && isMoveX(board[2][2])) {
    return true;
  } else if (isMoveX(board[0][2]) && isMoveX(board[1][1]) && isMoveX(board[2][0])) {
    return true;
  } else {
    for (x = 0; x < 3; x++) {
      if (isMoveX(board[x][0]) && isMoveX(board[x][1]) && isMoveX(board[x][2])) {
        return true;
      } else if (isMoveX(board[0][x]) && isMoveX(board[1][x]) && isMoveX(board[2][x])) {
        return true;
      }
    }
  }
  return false;
}

// Configures the coordinates to the square that this player decided to
// play in
void play () {
  if (!getRandomBlankSquare(WIN)) {
    // player is O, those who play second
    if (team) {
      // first move; X at the center
      if (isMoveAtCenter(1)) {
        // second move; O at a corner
        if (move == 2) {
          getRandomBlankSquare(CORNER);
        // third move; X didn't made a double
        } else if (isOppositeSquares(2, 3)) {
          // fourth move; O at a corner
          if (move == 4) {
            getRandomBlankSquare(CORNER);
          // sixth and eighth move; O blocking or random
          } else {
            if (!getBlockingSquare()) {
              getRandomBlankSquare(NONE);
            }
          }
        // third move; X made a double
        } else {
          // fourth, sixth and eighth move; O blocking or random
          if (!getBlockingSquare()) {
            getRandomBlankSquare(NONE);
          }
        }
      // first move; X not at the center
      } else {
        // second move; O at the center
        if (move == 2) {
          i = 1;
          j = 1;
        } else if (isOppositeSquares(1, 3)) {
          // third move; Xs at opposite corners
          if (isMoveAtCorner(1)) {
            // fourth move; O at an edge
            if (move == 4) {
              getRandomBlankSquare(EDGE);
            // sixth and eighth move; O blocking
            } else {
              getBlockingSquare();
            }
          // third move; Xs at opposite edges
          } else {
            // fourth move; O at an edge
            if (move == 4) {
              getRandomBlankSquare(EDGE);
            // fourth move; O at a corner bordering only one X
            } else {
              getRandomBlankSquare(CORNER_BORDER_1_X);
            }
          }
        // third move; Xs at non opposite corners
        } else if (isMoveAtCorner(1) && isMoveAtCorner(3)) {
          // fourth move; O blocking
          if (move == 4) {
            getBlockingSquare();
          // sixth move; O to an edge
          } else if (move == 6) {
            getRandomBlankSquare(EDGE);
          // eighth move; O blocking
          } else {
            getBlockingSquare();
          }
        // third move; Xs at adjacent edges
        } else if (isMoveAtEdge(1) && isMoveAtEdge(3)) {
          // fourth move; O at corner bordering two Xs
          if (move == 4) {
            getBlankCornerSquareBorderOpponents(2);
          // sixth and eighth move; O blocking or random
          } else {
            if (!getBlockingSquare()) {
              getRandomBlankSquare(NONE);
            }
          }
        // third move; one X at a corner and one X at a close edge
        } else if (isMovesBorderingEachOther(1, 3)) {
          // fourth, sixth and eighth move; O blocking or random
          if (!getBlockingSquare()) {
            getRandomBlankSquare(NONE);
          }
        // third move: one X at a corner and one X at a far edge
        } else {
          // fourth move; O opposite to the corner X
          if (move == 4) {
            if (isMoveAtCorner(1)) {
              getOppositeSquare(1);
            } else {
              getOppositeSquare(3);
            }
          // sixth and eighth move; O blocking or random
          } else {
            if (!getBlockingSquare()) {
              getRandomBlankSquare(NONE);
            }
          }
        }
      }
    // player is X, those who play first
    } else {
      // first move; X at a corner
      if (move == 1) {
        getRandomBlankSquare(CORNER);
      // second move; O at the center
      } else if (isMoveAtCenter(2)) {
        // third move; X opposite to the first move
        if (move == 3) {
          getOppositeSquare(1);
        // fourth move; O at a corner
        } else if (isMoveAtCorner(4)) {
          // fifth move; X at the remaining corner
          if (move == 5) {
            getBlankCornerSquare();
          }
        // fourth move; O at an edge
        } else if (isMoveAtEdge(4)) {
          // fifth, seventh and ninth move; X blocking
          getBlockingSquare();
        }
      // second move; O at a corner
      } else if (isMoveAtCorner(2)) {
        // third move; X at a corner
        if (move == 3) {
          getRandomBlankSquare(CORNER);
        // fifth move; X at the remaining corner
        } else {
          getBlankCornerSquare();
        }
      // second move; O at an edge
      } else {
        // third move; X at the center
        if (move == 3) {
          i = 1;
          j = 1;
        } else {
          // fourth move; O blocked and made a double
          // fifth move; X blocking
          if (!getBlockingSquare()) {
            // fourth move; O just blocked
            // fifth move; X at the corner not bordered by an O
            getBlankCornerSquareBorderOpponents(0);
          }
        }
      }
    }
  }
}

void playLastMove () {
  if (move == 9) {
    for (i = 0; i < 3; i++) {
      for (j = 0; j < 3; j++) {
        if (board[i][j] == 0) {
          board[i][j] = 9;
        }
      }
    }
    printBoard();
  }
}

void printBoard () {
  for (j = 0; j < 3; j++) {
    if (j == 2) {
      lcd.setCursor(0, j);
    } else {
      lcd.setCursor(0, j);
    }
    for (i = 0; i < 3; i++) {
      if (board[i][j] == 0) {
        lcd.write(byte(0));
      } else if (isMoveO(board[i][j])) {
        lcd.print('O');
      } else {
        lcd.print('X');
      }
      lcd.print(' ');
    }
  }
}

void printChoices () {
  lcd.setCursor(7, 2);
  lcd.print("reset");
  lcd.setCursor(7, 3);
  lcd.print("exit");
}

void printComputerWon () {
  lcd.setCursor(7, 0);
  lcd.print("computer won!");
  printGameOver();
}

void printCursorConfigurePosition () {
  if (choice == EXIT) {
    lcd.setCursor(7, 3);
  } else if (choice == P1) {
    i = 0;
    j = 0;
    lcd.setCursor(0, 0);
  } else if (choice == P2) {
    i = 1;
    j = 0;
    lcd.setCursor(2, 0);
  } else if (choice == P3) {
    i = 2;
    j = 0;
    lcd.setCursor(4, 0);
  } else if (choice == P4) {
    i = 0;
    j = 1;
    lcd.setCursor(0, 1);
  } else if (choice == P5) {
    i = 1;
    j = 1;
    lcd.setCursor(2, 1);
  } else if (choice == P6) {
    i = 2;
    j = 1;
    lcd.setCursor(4, 1);
  } else if (choice == P7) {
    i = 0;
    j = 2;
    lcd.setCursor(0, 2);
  } else if (choice == P8) {
    i = 1;
    j = 2;
    lcd.setCursor(2, 2);
  } else if (choice == P9) {
    i = 2;
    j = 2;
    lcd.setCursor(4, 2);
  } else if (choice == RESET) {
    lcd.setCursor(7, 2);
  }
}

void printGameOver () {
  printBoard();
  lcd.noCursor();
  while (!digitalRead(0));
  while (digitalRead(0));
  lcd.cursor();
  playingGame = false;
}

void printOWon () {
  lcd.setCursor(7, 0);
  lcd.print("O won!       ");
  printGameOver();
}

void printPlayerMove () {
  lcd.setCursor(7, 0);
  if (isMoveO(move)) {
    lcd.print("O's move     ");
  } else {
    lcd.print("X's move     ");
  }
}

void printPlayerTeam () {
  lcd.setCursor(7, 0);
  if (team) {
    lcd.print("player is X  ");
  } else {
    lcd.print("player is O  ");
  }
}

void printPlayerWon () {
  lcd.setCursor(7, 0);
  lcd.print("player won!  ");
  printGameOver();
}

void printTie () {
  lcd.setCursor(7, 0);
  lcd.print("tie!         ");
  printGameOver();
}

void printXWon () {
  lcd.setCursor(7, 0);
  lcd.print("X won!       ");
  printGameOver();
}

void resetBoard () {
  for (i = 0; i < 3; i++) {
    for (j = 0; j < 3; j++) {
      board[i][j] = 0;
    }
  }
}

void setup () {
  lcd.begin(20, 4);
  resetBoard();
  lcd.cursor();
  lcd.createChar(0, (byte []){0, 0, 0, 4, 0, 0, 0, 0});
  pinMode(0, INPUT);
  pinMode(A0, INPUT);
  randomSeed(analogRead(1));
}

void loop () {
  lcd.clear();
  lcd.print("PvC");
  lcd.setCursor(0, 1);
  lcd.print("PvP");
  lcd.setCursor(0, 0);
  mainMenu = true;
  while (mainMenu) {
    pot = analogRead(0);
    if (pot < 512) {
      choice = PvC;
    } else {
      choice = PvP;
    }
    if (lastChoice != choice) {
      lastChoice = choice;
      if (choice == PvC) {
        lcd.setCursor(0, 0);
      } else {
        lcd.setCursor(0, 1);
      }
    }
    if (digitalRead(0)) {
      while (digitalRead(0));
      if (choice == PvC) {
        lcd.clear();
        playingGames = true;
        while (playingGames) {
          getPotSetChoice();
          lastChoice = choice;
          move = 1;
          playingGame = true;
          team = ~team;
          resetBoard();
          printBoard();
          printChoices();
          printPlayerTeam();
          printCursorConfigurePosition();
          while (playingGame) {
            // computer's move
            if (isPlayer(move)) {
              lcd.noCursor();
              play();
              lcd.cursor();
              board[i][j] = move;
              move++;
              printBoard();
              checkGameState();
            }
            getPotSetChoice();
            if (lastChoice != choice) {
              lastChoice = choice;
              printCursorConfigurePosition();
            }
            if (digitalRead(0)) {
              while(digitalRead(0));
              if (choice == RESET) {
                playingGame = false;
              } else if (choice == EXIT) {
                mainMenu = false;
                playingGame = false;
                playingGames = false;
                team = ~team;
              } else {
                // human player's move
                if ((isOpponent(move)) && (board[i][j] == 0)) {
                  board[i][j] = move;
                  move++;
                  printBoard();
                  checkGameState();
                }
              }
            }
          }
        }
      } else {
        lcd.clear();
        playingGames = true;
        while (playingGames) {
          getPotSetChoice();
          lastChoice = choice;
          move = 1;
          resetBoard();
          printBoard();
          printChoices();
          printPlayerMove();
          printCursorConfigurePosition();
          playingGame = true;
          while (playingGame) {
            getPotSetChoice();
            if (lastChoice != choice) {
              lastChoice = choice;
              printCursorConfigurePosition();
            }
            if (digitalRead(0)) {
              while(digitalRead(0));
              if (choice == RESET) {
                playingGame = false;
              } else if (choice == EXIT) {
                mainMenu = false;
                playingGame = false;
                playingGames = false;
              } else {
                if (board[i][j] == 0) {
                  board[i][j] = move;
                  move++;
                  printBoard();
                  if (hasOWon()) {
                    printOWon();
                  } else if (hasXWon()) {
                    printXWon();
                  } else if (move == 9) {
                    getBlankSquare();
                    board[i][j] = 9;
                    printBoard();
                    if (hasOWon()) {
                      printOWon();
                    } else if (hasXWon()) {
                      printXWon();
                    } else {
                      printTie();
                    }
                  } else {
                    printPlayerMove();
                    printCursorConfigurePosition();
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

