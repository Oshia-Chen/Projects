package edu.uob;

import java.util.ArrayList;

class OXOModel {

  private final ArrayList<ArrayList<OXOPlayer>> cells;
  private final ArrayList<OXOPlayer> players;
  private ArrayList<Integer> connecting_cnt;
  private int currentPlayerNumber = 0;
  private OXOPlayer winner;
  private boolean gameDrawn;
  private int winThreshold;
  private int cells_rows;
  private int cells_cols;

  public OXOModel(int numberOfRows, int numberOfColumns, int winThresh) {
    winThreshold = winThresh;
    cells = new ArrayList<>();
    connecting_cnt = new ArrayList<>();
    cells_rows = numberOfRows;
    cells_cols = numberOfColumns;
    for(int j = 0; j < numberOfRows; j++){
      cells.add(new ArrayList<>());
      for(int i = 0; i < numberOfColumns; i++){
        cells.get(j).add(new OXOPlayer('\0'));
      }
    }
    players = new ArrayList<>();
  }

  public int getNumberOfPlayers() {
    return players.size();
  }

  public void addPlayer(OXOPlayer player) {
    players.add(player);
    connecting_cnt.add(0);
  }

  public OXOPlayer getPlayerByNumber(int number) {
    return players.get(number);
  }

  public OXOPlayer getWinner() {
    return winner;
  }

  public void setWinner(OXOPlayer player) {
    winner = player;
  }

  public int getCurrentPlayerNumber() {
    return currentPlayerNumber;
  }

  public void setCurrentPlayerNumber(int playerNumber) {
    currentPlayerNumber = playerNumber;
  }

  public int getNumberOfRows() {
    return cells_rows;
  }

  public int getNumberOfColumns() {
    return cells_cols;
  }

  public ArrayList<ArrayList<OXOPlayer>> getCells(){
    return this.cells;
  }


  public OXOPlayer getCellOwner(int rowNumber, int colNumber) {
    return cells.get(rowNumber).get(colNumber);
  }

  public void setCellOwner(int rowNumber, int colNumber, OXOPlayer player) {
    cells.get(rowNumber).set(colNumber, player);
  }

  public void setWinThreshold(int winThresh) {
    winThreshold = winThresh;
  }

  public int getWinThreshold() {
    return winThreshold;
  }

  public void setGameDrawn() {
    gameDrawn = true;
  }

  public boolean isGameDrawn() {
    return gameDrawn;
  }

  public int switchCurrentPlayerNumber(int CurrentPlayerNumber){
    int length = getNumberOfPlayers();
    return (CurrentPlayerNumber+1) % length;
  }

  public void addColumn() {
    if(winner == null && !isGameDrawn() && cells_cols <= 8){
      for (int i = 0; i < cells_rows; i++) {
        cells.get(i).add(new OXOPlayer('\0'));
      }
      cells_cols++;
    }
  }

  public void addRow() {
    if(winner == null && !isGameDrawn() && cells_rows <= 8){
      cells.add(new ArrayList<>());
      cells_rows++;
      for(int i = 0; i < cells_cols; i++){
        cells.get(cells_rows-1).add(new OXOPlayer('\0'));
      }
    }
  }

  public void removeRow() {
    boolean flag = false;
    if(winner == null && !isGameDrawn() && cells_rows >= 2) {
      for (int i = 0; i < cells_cols; i++) {
        if (cells.get(cells_rows - 1).get(i).getPlayingLetter() != '\0') {
          flag = true;
        }
      }
      if (!flag) {
        cells.remove(cells_rows - 1);
        cells_rows--;
      }
    }
  }

  public void removeColumn() {
    boolean flag = false;
    if(winner == null && !isGameDrawn() && cells_cols >= 2) {
      for (int i = 0; i < cells_rows; i++) {
        if (cells.get(i).get(cells_cols - 1).getPlayingLetter() != '\0') {
          flag = true;
        }
      }
      if (!flag) {
        for (int j = 0; j < cells_rows; j++) {
          cells.get(j).remove(cells_cols - 1);
        }
        cells_cols--;
      }
    }
  }

  public ArrayList<Integer> getConnectingCnt(){
    return connecting_cnt;
  }

  public void setConnectingCnt(int player_num, int cnt){
    connecting_cnt.set(player_num, cnt);
  }

}
