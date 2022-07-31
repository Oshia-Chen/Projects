package edu.uob;

import edu.uob.OXOMoveException.*;

import java.util.ArrayList;

class OXOController {
  OXOModel gameModel;

  public OXOController(OXOModel model) {
    gameModel = model;
  }

  public void handleIncomingCommand(String command) throws OXOMoveException {
    if(gameModel.isGameDrawn() || gameModel.getWinner() != null){
      return;
    }
    int player_num = gameModel.getCurrentPlayerNumber();
    int len_col = gameModel.getNumberOfColumns();
    int len_row = gameModel.getNumberOfRows();
    if(command.length() != 2 ){
      throw new InvalidIdentifierLengthException(command.length());
    }
    if(!Character.isLetter(command.charAt(0))){
      throw new InvalidIdentifierCharacterException(RowOrColumn.ROW, command.charAt(0));
    }
    char tmp = Character.toLowerCase(command.charAt(0));
    int rows = tmp - 97;
    if(rows > len_row - 1 || rows < 0 || rows > 8){
      throw new OutsideCellRangeException(RowOrColumn.ROW, command.charAt(0));
    }
    if(!Character.isDigit(command.charAt(1))){
      throw new InvalidIdentifierCharacterException(RowOrColumn.COLUMN, command.charAt(1));
    }
    int cols = Character.getNumericValue(command.charAt(1)) - 1;
    if(cols > len_col - 1 || cols < 0 || cols > 8){
      throw new OutsideCellRangeException(RowOrColumn.COLUMN,  Character.getNumericValue(command.charAt(1)));
    }
    int threshold = gameModel.getWinThreshold();
    OXOPlayer player = gameModel.getPlayerByNumber(player_num);
    if (gameModel.getCellOwner(rows, cols).getPlayingLetter() == '\0') {
      gameModel.setCellOwner(rows, cols, player);
    } else {
      throw new CellAlreadyTakenException(rows, cols);
    }
    int h = count_Horizontal(rows, cols, len_col, player);
    int v = count_Vertical(rows, cols, len_row, player);
    int r = count_Diagonals_Right(rows, cols, len_row, len_col, player);
    int l = count_Diagonals_Left(rows, cols, len_row, len_col, player);
    int cnt = Math.max(h,v);
    if(Math.max(r,l) > cnt){
      cnt = Math.max(r,l);
    }
    updateConnectingCnt(cnt, player, threshold);
    if(gameModel.getWinner() == null) {
      drawnDetection(len_row, len_col);
      gameModel.setCurrentPlayerNumber(gameModel.switchCurrentPlayerNumber(player_num));
    }
  }

  public void addColumn() {
    gameModel.addColumn();
  }

  public void addRow() {
    gameModel.addRow();
  }

  public void removeRow() {
    gameModel.removeRow();
    drawnDetection(gameModel.getNumberOfRows(), gameModel.getNumberOfColumns());
  }

  public void removeColumn() {
    gameModel.removeColumn();
    drawnDetection(gameModel.getNumberOfRows(), gameModel.getNumberOfColumns());
  }

  public void increaseWinThreshold() {
    if(gameModel.isGameDrawn() || gameModel.getWinner() != null){
      return;
    }
    gameModel.setWinThreshold(gameModel.getWinThreshold() + 1);
  }

  public void decreaseWinThreshold() {
    if(gameModel.isGameDrawn() || gameModel.getWinner() != null){
      return;
    }
    if(gameModel.getWinThreshold() >= 1){
      gameModel.setWinThreshold(gameModel.getWinThreshold() - 1);
      winDetection(gameModel.getWinThreshold());
    }
  }

  public int count_Horizontal(int row_index, int col_index, int length_col, OXOPlayer player){
    int cnt = 1;
    for(int i = 1; col_index+i < length_col; i++) {
      if (gameModel.getCells().get(row_index).get(col_index + i) == player) {
        cnt++;
      } else {
        break;
      }
    }
    for (int i = 1; col_index - i >= 0; i++) {
      if (gameModel.getCells().get(row_index).get(col_index - i) == player) {
        cnt++;
      } else {
        break;
      }
    }
    return cnt;
  }

  public int count_Vertical(int row_index, int col_index, int length_row, OXOPlayer player){
    int cnt = 1;
    for (int i = 1; row_index+i < length_row; i++) {
      if (gameModel.getCells().get(row_index + i).get(col_index) == player) {
        cnt++;
      } else {
        break;
      }
    }
    for (int i = 1; row_index - i >= 0; i++) {
      if (gameModel.getCells().get(row_index - i).get(col_index) == player) {
        cnt++;
      } else {
        break;
      }
    }
    return cnt;
  }

  public int count_Diagonals_Right(int row_index, int col_index, int length_row, int length_col, OXOPlayer player){
    int cnt = 1;
    int col_pnt = col_index-1;
    for (int i = 1; row_index - i >= 0; i++) {
      if (col_pnt >= 0 && gameModel.getCells().get(row_index - i).get(col_pnt) == player) {
        cnt++;
        col_pnt--;
      } else {
        break;
      }
    }
    col_pnt = col_index+1;
    for (int i = 1; row_index + i < length_row; i++) {
      if (col_pnt < length_col && gameModel.getCells().get(row_index + i).get(col_pnt) == player) {
        cnt++;
        col_pnt++;
      } else {
        break;
      }
    }
    return cnt;
  }

  public int count_Diagonals_Left(int row_index, int col_index, int length_row, int length_col, OXOPlayer player){
    int cnt = 1;
    int col_pnt = col_index+1;
    for (int i = 1; row_index - i >= 0; i++) {
      if (col_pnt < length_col && gameModel.getCells().get(row_index - i).get(col_pnt) == player) {
        cnt++;
        col_pnt++;
      } else {
        break;
      }
    }
    col_pnt = col_index-1;
    for (int i = 1; row_index + i < length_row; i++) {
      if (col_pnt >= 0 && gameModel.getCells().get(row_index + i).get(col_pnt) == player) {
        cnt++;
        col_pnt--;
      } else {
        break;
      }
    }
    return cnt;
  }

  public void winDetection(int threshold){
    OXOPlayer win = null;
    for(int i = 0; i < gameModel.getNumberOfPlayers(); i++ ) {
      if (gameModel.getConnectingCnt().get(i) >= threshold) {
        if(win != null){
          gameModel.setGameDrawn();
          return;
        }
        win = gameModel.getPlayerByNumber(i);
      }
    }
    gameModel.setWinner(win);
  }

  public void drawnDetection(int length_row, int length_col){
    boolean flag = false;
    for(int j = 0; j < length_row; j++){
      for(int i = 0; i < length_col; i++){
        if(gameModel.getCells().get(j).get(i).getPlayingLetter() == '\0'){
          flag = true;
        }
      }
    }
    if(!flag){
      gameModel.setGameDrawn();
    }
  }

  public void updateConnectingCnt(int cnt, OXOPlayer player, int threshold) {
    for (int i = 0; i < gameModel.getNumberOfPlayers(); i++) {
      if (gameModel.getPlayerByNumber(i) == player) {
        if (gameModel.getConnectingCnt().get(i) < cnt) {
          gameModel.setConnectingCnt(i, cnt);
          winDetection(threshold);
        }
      }
    }
  }
}



