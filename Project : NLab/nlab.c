#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <assert.h>
#include <stdbool.h>
#include <math.h>

#define MAXNUMTOKENS 1000
#define MAXTOKENSIZE 1000
#define varname_len 1
#define strsame(A,B) (strcmp(A, B) == 0)
#define ON_ERROR(PHRASE) {fprintf(stderr, " Fatal Error: %s Occurred in %s, line %d", PHRASE, __FILE__, __LINE__ ); exit(EXIT_FAILURE);}


struct variable{
   char varname;
   int label;
   int row;
   int col;
   int arr[MAXNUMTOKENS][MAXTOKENSIZE];
};

typedef struct variable variable;

struct execute{
   int archive;
   int label;
   int row;
   int col;
   int arr[MAXNUMTOKENS][MAXTOKENSIZE];
};

typedef struct execute execute;

struct prog{
   char wds [MAXNUMTOKENS][MAXTOKENSIZE]; // program instruction content
   char str [MAXNUMTOKENS][MAXTOKENSIZE]; // string content
   variable var [MAXTOKENSIZE]; // var and its value
   execute exe [MAXTOKENSIZE]; // program execution
   int cw; // Current Word
   int wds_length; // the rows of wds
   int size_e; // index for execute array
   int size_v; // index for var's string
   int size_s; // index for str's string
};

typedef struct prog Program;


void Prog(Program *p);
void INSTRCLIST(Program *p);
void INSTRC(Program *p);
int PRINT(Program *p);
int SET(Program *p);
int LOOP(Program *p);
int VARNAME(Program *p);
int STRING(Program *p);
int POLISHLIST(Program *p);
int POLISH(Program *p);
int PUSHDOWN(Program *p);
int IS_INTEGER(char *a);
int UNARYOP(Program *p);
int BINARYOP(Program *p);
int CREATE(Program *p);
void ROWS(Program *p);
void COLS(Program *p);
void FILENAME(Program *p);
int TO_INTEGER(char* a);
void PRINT_VAR(Program *p);
void PRINT_STRING(Program *p);
void SET_VAR(Program *p, char a);
void LOOP_EXECUTE(Program *p, char vname, int cnt, int cap);
void GET_VAR_VALUE(Program *p);
void GET_INTEGER(Program *p);
void U_NOT_EXECUTE(Program *p, int sp, int rows, int cols);
void U_EIGHTCOUNT_EXECUTE(Program *p, int sp, int rows, int cols);
void CHECK_ARRAY_LENGTH(Program *p, int sp, int *rows, int *cols);
void B_AND_EXECUTE(Program *p, int sp, int rows, int cols);
void B_OR_EXECUTE(Program *p, int sp, int rows, int cols);
void B_GREATER_EXECUTE(Program *p, int sp, int rows, int cols);
void B_LESS_EXECUTE(Program *p, int sp, int rows, int cols);
void B_ADD_EXECUTE(Program *p, int sp, int rows, int cols);
void B_TIMES_EXECUTE(Program *p, int sp, int rows, int cols);
void B_EQUALS_EXECUTE(Program *p, int sp, int rows, int cols);
void ONES_EXECUTE(Program *p, int rows, int cols);
void READ_EXECUTE(Program *p);
int SWAP(Program *p);
void SWAP_EXECUTE(Program *p);
void ELSE(Program *p);
int ELSEINSTRCLIST(Program *p);
int COMPARE(Program *p);
int IF(Program *p);
void TEST(void);

int main(int argc, char* argv[])
{
   if(argc != 2){
      fprintf(stderr, "Usage : %s <program.nlb> or %s <program.arr>\n", argv[0], argv[0]);
      exit(EXIT_FAILURE);
   }
   Program* prog = calloc(1, sizeof(Program));
   FILE* fp = fopen(argv[1], "rt");
   if(!fp){
      fprintf(stderr, "Cannot read %s\n", argv[1]);
      exit(EXIT_FAILURE);
   }

   int i = 1;
   char str[MAXTOKENSIZE];
   bool flag = false;
   while(flag == false && fscanf(fp, "%s", str) == 1){
      if(strsame(str, "BEGIN")){
         sprintf(prog->wds[0], "%s", str);
         flag = true;
      }
   }
   if(flag == false){
      ON_ERROR("No BEGIN statement?");
   }
   while(fscanf(fp, "%s", str) == 1 && i < MAXNUMTOKENS){
      sprintf(prog->wds[i], "%s", str);
      assert(i < MAXNUMTOKENS);
      i++;
   }
   prog->wds_length = i;
   Prog(prog);
   fclose(fp);
   TEST();
   free(prog);

   return 0;
}


void Prog(Program *p)
{
   if(!strsame(p->wds[p->cw], "BEGIN")){ 
      ON_ERROR("No BEGIN statement?");
   }
   p->cw = p->cw + 1; 
   if(!strsame(p->wds[p->cw], "{")){ 
      ON_ERROR("Expected a '{' ?\n");
   }
   p->cw = p->cw + 1; 
   INSTRCLIST(p);
   return;
}


void INSTRCLIST(Program *p)
{
   if(strsame(p->wds[p->cw], "}")){ 
      return;
   }
   INSTRC(p);
   p->cw = p->cw + 1; 
   INSTRCLIST(p);
   return;

   ON_ERROR("Expected a '}' ?\n");
}


void INSTRC(Program *p)
{
   if(!PRINT(p)){
      if(!SET(p)){
         if(!CREATE(p)){
            if(!LOOP(p)){
#ifdef EXTENSION
               if(!SWAP(p)){
                  if(!IF(p)){
#endif
                  ON_ERROR("Expected some instruction, e.g. PRINT/SET/CREATE/LOOP/{ .\n");
#ifdef EXTENSION
                  }
               }
#endif
            }
         }
      }
   }
   return;
}


int PRINT(Program *p)
{
   if(strsame(p->wds[p->cw], "PRINT")){ 
      p->cw = p->cw + 1;
      if(VARNAME(p)){
#ifdef LETSDOIT
         PRINT_VAR(p);
#endif
         return 1;
      }

      if(STRING(p)){
#ifdef LETSDOIT
         PRINT_STRING(p);
#endif
         return 1;
      }
      ON_ERROR("Expected a varname or string after PRINT.\n");
   }
   return 0;
}


void PRINT_VAR(Program *p)
{
   for(int i = 0; i < p->size_v; i++){
      if(p->wds[p->cw][1] == p->var[i].varname){
         if(p->var[i].label == 0){
            ON_ERROR("Cannot print an undefined variable.\n");
         }
         if(p->var[i].label == 1){
            int row = p->var[i].row;
            int col = p->var[i].col;
            int len = row * col;
            if(len == 1){
               printf("%d\n", p->var[i].arr[0][0]);
            }
            else{
               for(int k = 0; k < row; k++){
                  for(int j = 0; j < col; j++){
                     printf("%d ", p->var[i].arr[k][j]);
                  }
                  printf("\n");
               }
               printf("\n");
            }
         }
      }
   }
}


void PRINT_STRING(Program *p)
{
   int len = strlen(p->str[p->size_s-1]);
   for(int i = 1; i < len-1; i++){
      printf("%c", p->str[p->size_s-1][i]);
   }
   printf("\n");
}


int SET(Program *p)
{ 
   if(!strsame(p->wds[p->cw], "SET")){ 
     return 0;
   }
   p->cw = p->cw + 1;
   if(!VARNAME(p)){
      ON_ERROR("Expected a VARNAME.\n");
   }
#ifdef LETSDOIT
   char tmp = p->wds[p->cw][1];
#endif
   p->cw = p->cw + 1;
   if(!strsame(p->wds[p->cw], ":=")){ 
      ON_ERROR("Expected a ':='.\n");
   }
   p->cw = p->cw + 1;
   if(!POLISHLIST(p)){
      ON_ERROR("Expected a POLISHLIST.\n");
   }
#ifdef LETSDOIT
   SET_VAR(p, tmp);
#endif
   return 1;
}


void SET_VAR(Program *p, char a)
{
   p->exe[p->size_e-1].archive = 1;
   for(int i = 0; i < p->size_e; i++){
      if(p->exe[i].archive == 0){
         ON_ERROR("Cannot assign more than one value to a variable.\n");
      }
   }
   for(int i = 0; i < p->size_v; i++){
      if(a == p->var[i].varname){
         p->var[i].label = p->exe[p->size_e-1].label;
         p->var[i].row = p->exe[p->size_e-1].row;
         p->var[i].col = p->exe[p->size_e-1].col;
         for(int k = 0; k < p->var[i].row; k++){
            for(int j = 0; j < p->var[i].col; j++){
               p->var[i].arr[k][j] = p->exe[p->size_e-1].arr[k][j];
            }
         }
      }
   }
}


int LOOP(Program *p)
{
   if(!strsame(p->wds[p->cw], "LOOP")){ 
     return 0;
   }
   p->cw = p->cw + 1;
   if(!VARNAME(p)){
      ON_ERROR("LOOP : Expected a VARNAME.\n");
   }
#ifdef LETSDOIT
   char vname = p->wds[p->cw][1];
   for(int i = 0; i < p->size_v; i++){
      if(vname == p->var[i].varname){
         p->var[i].arr[0][0] = 1;
         p->var[i].label = 1;
         p->var[i].row = 1;
         p->var[i].col = 1;
      }
   }
   int loop_cnt = 1;
#endif
   p->cw = p->cw + 1;
   if(!IS_INTEGER(p->wds[p->cw])){
      ON_ERROR("LOOP : Expected an INTEGER.\n");
   }
#ifdef LETSDOIT
   int cap = TO_INTEGER(p->wds[p->cw]);
#endif
   p->cw = p->cw + 1;
   if(!strsame(p->wds[p->cw], "{")){ 
      ON_ERROR("LOOP : Expected a '{'.\n");
   } 
   p->cw = p->cw + 1;
#ifdef LETSDOIT
   LOOP_EXECUTE(p, vname, loop_cnt, cap);
#endif
#ifndef LETSDOIT
   INSTRCLIST(p);
#endif
   return 1;
}


void LOOP_EXECUTE(Program *p, char vname, int cnt, int cap)
{
   int pnt = p->cw;
   while(cnt <= cap){
      p->cw = pnt;
      INSTRCLIST(p);
      for(int i = 0; i < p->size_v; i++){
         if(vname == p->var[i].varname){
            cnt = p->var[i].arr[0][0];
            cnt++;
            p->var[i].arr[0][0] = cnt;
         }
      }     
   }
}


int VARNAME(Program *p)
{
   if((p->wds[p->cw][0] != '$')){ 
      return 0;
   } 
   int len = strlen(p->wds[p->cw]);
   if(len > 2){
      ON_ERROR("Expected a vaild varname, e.g. $A, $B, $Z.\n");
   }
   if((int)p->wds[p->cw][1] >= 65 && (int)p->wds[p->cw][1] <= 90){
#ifdef LETSDOIT
      bool flag = false; 
      for(int i = 0; i < p->size_v; i++){
         if(p->wds[p->cw][1] == p->var[i].varname){
            flag = true;
         }
      }
      if(flag == false){
         p->var[p->size_v].varname = p->wds[p->cw][1];
         p->size_v = p->size_v + 1;
      }
#endif
      return 1;
   }
   else{
      ON_ERROR("The vaild varname should be upper-case letters, e.g. $A, $B, $Z.\n");
   }
   return 0;
}


int STRING(Program *p)
{
   if(p->wds[p->cw][0] != '"'){ 
      return 0;
   }
   int len = strlen(p->wds[p->cw]);
   if(p->wds[p->cw][len-1] != '"'){
      ON_ERROR("Expected a double quote at the end of the string or file name.\n");
   }
#ifdef LETSDOIT
   sprintf(p->str[p->size_s], "%s", p->wds[p->cw]);
   p->size_s = p->size_s + 1;
#endif
   return 1; 
}



int POLISHLIST(Program *p)
{
   if(strsame(p->wds[p->cw], ";")){
      return 1;
   }
   if(POLISH(p)){
      p->cw = p->cw + 1;
      return POLISHLIST(p);
   }
   return 0;
}


int POLISH(Program *p)
{
   if(!PUSHDOWN(p) && !UNARYOP(p) && !BINARYOP(p)){
      return 0;
   }
   return 1;
}


int PUSHDOWN(Program *p)
{
   if(p->wds[p->cw][0] == '$'){
      if(VARNAME(p)){
#ifdef LETSDOIT
         GET_VAR_VALUE(p);
#endif
         return 1;
      }
   }
   else{
      if(IS_INTEGER(p->wds[p->cw])){
#ifdef LETSDOIT
         GET_INTEGER(p);
#endif
         return 1;
      }
   }
   return 0;
}


void GET_VAR_VALUE(Program *p)
{
   for(int i = 0; i < p->size_v; i++){
      if(p->wds[p->cw][1] == p->var[i].varname){
         if(p->var[i].label == 0){
            ON_ERROR("Using an undefined variable!\n");
         }
         if(p->var[i].label == 1){
            for(int k = 0; k < p->var[i].row; k++){
               for(int j = 0; j < p->var[i].col; j++){
                  p->exe[p->size_e].arr[k][j] = p->var[i].arr[k][j];
               }
            }
            p->exe[p->size_e].label = 1;
            p->exe[p->size_e].row = p->var[i].row;
            p->exe[p->size_e].col = p->var[i].col;
         }                 
         p->size_e = p->size_e+1;
      }
   }
}


void GET_INTEGER(Program *p)
{
   int num = TO_INTEGER(p->wds[p->cw]);
   p->exe[p->size_e].label = 1;
   p->exe[p->size_e].row = 1;
   p->exe[p->size_e].col = 1;
   p->exe[p->size_e].arr[0][0] = num;
   p->size_e = p->size_e+1;
}


int IS_INTEGER(char *a)
{
   if(a[0] == '-'){
      ON_ERROR("Expected a non-negative integer.\n");
   }
   int len = strlen(a);
   for(int i = 0; i < len; i++){
      if(a[i] < '0' || a[i] > '9'){
         return 0;
      }
   }
   return 1;
}


int UNARYOP(Program *p)
{
   bool flag = false;
#ifdef LETSDOIT
   int sp = p->size_e-1;
#endif
   if(strsame(p->wds[p->cw], "U-NOT")){
#ifdef LETSDOIT
      if(sp < 0 || p->exe[sp].archive == 1){
         ON_ERROR("Expected a variable or an integer before doing UNARYOP.\n");
      }
      int rows = p->exe[sp].row;
      int cols = p->exe[sp].col;
      U_NOT_EXECUTE(p, sp, rows, cols);
#endif
      flag = true;
   }
   if(strsame(p->wds[p->cw], "U-EIGHTCOUNT")){
#ifdef LETSDOIT
      if(sp < 0 || p->exe[sp].archive == 1){
         ON_ERROR("Expected a variable or an integer before doing UNARYOP.\n");
      }
      int rows = p->exe[sp].row;
      int cols = p->exe[sp].col;
      U_EIGHTCOUNT_EXECUTE(p, sp, rows, cols);
#endif
      flag = true;
   }

   if(flag == true){
#ifdef LETSDOIT
      int rows = p->exe[sp].row;
      int cols = p->exe[sp].col;
      p->exe[sp+1].label = 1;
      p->exe[sp+1].row = rows;
      p->exe[sp+1].col = cols;
      p->size_e = p->size_e+1;
#endif
      return 1;
   }
   return 0;
}


void U_NOT_EXECUTE(Program *p, int sp, int rows, int cols)
{
   for(int j = 0; j < rows; j++){
      for(int i = 0; i < cols; i++){
         if(p->exe[sp].arr[j][i] == 0){
            p->exe[sp+1].arr[j][i] = 1;
         }
         if(p->exe[sp].arr[j][i] >= 1){
            p->exe[sp+1].arr[j][i] = 0;
         }
      }
   }
   p->exe[sp].archive = 1;
}


void U_EIGHTCOUNT_EXECUTE(Program *p, int sp, int rows, int cols)
{
   int cnt = 0;
   for(int j = 0; j < rows; j++){
      for(int i = 0; i < cols; i++){
         if(j-1 >= 0 && p->exe[sp].arr[j-1][i] >= 1){
            cnt++;
         }
         if(j-1 >= 0 && i-1 >= 0 && p->exe[sp].arr[j-1][i-1] >= 1){
            cnt++;
         }
          if(j-1 >= 0 && i+1 < cols && p->exe[sp].arr[j-1][i+1] >= 1){
            cnt++;
         } 
         if(j+1 < rows && p->exe[sp].arr[j+1][i] >= 1){
            cnt++;
         }   
         if(j+1 < rows && i-1 >= 0 && p->exe[sp].arr[j+1][i-1] >= 1){
            cnt++;
         }       
         if(j+1 < rows && i+1 < cols && p->exe[sp].arr[j+1][i+1] >= 1){
            cnt++;
         }
         if(i-1 >= 0 && p->exe[sp].arr[j][i-1] >= 1){
            cnt++;
         }
         if(i+1 < cols && p->exe[sp].arr[j][i+1] >= 1){
            cnt++;
         }
         p->exe[sp+1].arr[j][i] = cnt;
         cnt = 0;
      }
   }
   p->exe[sp].archive = 1;
}


int BINARYOP(Program *p)
{
   bool flag = false;
#ifdef LETSDOIT
   int sp = p->size_e-1, rows = 1, cols = 1; 
#endif

   if(strsame(p->wds[p->cw], "B-AND")){
#ifdef LETSDOIT
      CHECK_ARRAY_LENGTH(p, sp, &rows, &cols);
      B_AND_EXECUTE(p, sp, rows, cols);
#endif
      flag = true;
   }

   if(strsame(p->wds[p->cw], "B-OR")){
#ifdef LETSDOIT
      CHECK_ARRAY_LENGTH(p, sp, &rows, &cols);
      B_OR_EXECUTE(p, sp, rows, cols);
#endif
      flag = true;
   }

   if(strsame(p->wds[p->cw], "B-GREATER")){
#ifdef LETSDOIT
      CHECK_ARRAY_LENGTH(p, sp, &rows, &cols);
      B_GREATER_EXECUTE(p, sp, rows, cols);
#endif
      flag = true;
   } 

   if(strsame(p->wds[p->cw], "B-LESS")){
#ifdef LETSDOIT
      CHECK_ARRAY_LENGTH(p, sp, &rows, &cols);
      B_LESS_EXECUTE(p, sp, rows, cols);
#endif
      flag = true;
   }  

   if(strsame(p->wds[p->cw], "B-ADD")){
#ifdef LETSDOIT
      CHECK_ARRAY_LENGTH(p, sp, &rows, &cols);
      B_ADD_EXECUTE(p, sp, rows, cols);
#endif
      flag = true;
   }

   if(strsame(p->wds[p->cw], "B-TIMES")){
#ifdef LETSDOIT
      CHECK_ARRAY_LENGTH(p, sp, &rows, &cols);
      B_TIMES_EXECUTE(p, sp, rows, cols);
#endif
      flag = true;
   }

   if(strsame(p->wds[p->cw], "B-EQUALS")){
#ifdef LETSDOIT
      CHECK_ARRAY_LENGTH(p, sp, &rows, &cols);
      B_EQUALS_EXECUTE(p, sp, rows, cols);
#endif
      flag = true;
   }

   if(flag == true){
#ifdef LETSDOIT
      p->exe[sp+1].label = 1;
      p->exe[sp+1].row = rows;
      p->exe[sp+1].col = cols;
      p->size_e = p->size_e+1;
#endif
      return 1;
   }
   return 0;
}


void CHECK_ARRAY_LENGTH(Program *p, int sp, int *rows, int *cols)
{
   if(sp < 0 || sp-1 < 0 || p->exe[sp].archive == 1 || p->exe[sp-1].archive == 1){
      ON_ERROR("Expected two variables or integers before doing BINARYOP.\n");
   }
   int arr1, arr2, rows1, cols1, rows2, cols2;

   rows1 = p->exe[sp].row;
   cols1 = p->exe[sp].col;
   arr1 = rows1 * cols1;

   rows2 = p->exe[sp-1].row;
   cols2 = p->exe[sp-1].col;
   arr2 = rows2 * cols2;

   if(arr1 > 1 && arr2 > 1){
      if(arr1 != arr2 || cols1 != cols2 || rows1 != rows2){
         ON_ERROR("Expected two arrays of the same length, including the rows & cols.\n");
      }
      else{
         *rows = rows1;
         *cols = cols1;
      }
   }

   if(arr1 == 1 && arr2 > 1){
      int temp = p->exe[sp].arr[0][0];
      for(int j = 0; j < rows2; j++){
         for(int i = 0; i < cols2; i++){
            p->exe[sp].arr[j][i] = temp;
         }
      }
      *rows = rows2;
      *cols = cols2;
   }

   if(arr2 == 1 && arr1 > 1){
      int temp = p->exe[sp-1].arr[0][0];
      for(int j = 0; j < rows1; j++){
         for(int i = 0; i < cols1; i++){
            p->exe[sp-1].arr[j][i] = temp;
         }
      }
      *rows = rows1;
      *cols = cols1;
   }
}


void B_AND_EXECUTE(Program *p, int sp, int rows, int cols)
{
   for(int j = 0; j < rows; j++){
      for(int i = 0; i < cols; i++){
         if(p->exe[sp].arr[j][i] >= 1 && p->exe[sp-1].arr[j][i] >= 1){
            p->exe[sp+1].arr[j][i] = 1;
         }
         else{
            p->exe[sp+1].arr[j][i] = 0;
         }
      }
   }
   p->exe[sp].archive = 1;
   p->exe[sp-1].archive = 1;
}


void B_OR_EXECUTE(Program *p, int sp, int rows, int cols)
{
   for(int j = 0; j < rows; j++){
      for(int i = 0; i < cols; i++){
         if(p->exe[sp].arr[j][i] >= 1 || p->exe[sp-1].arr[j][i] >= 1){
            p->exe[sp+1].arr[j][i] = 1;
         }
         else{
            p->exe[sp+1].arr[j][i] = 0;
         }
      }
   }
   p->exe[sp].archive = 1;
   p->exe[sp-1].archive = 1;
}


void B_GREATER_EXECUTE(Program *p, int sp, int rows, int cols)
{
   for(int j = 0; j < rows; j++){
      for(int i = 0; i < cols; i++){
         if(p->exe[sp-1].arr[j][i] > p->exe[sp].arr[j][i]){
            p->exe[sp+1].arr[j][i] = 1;
         }
         else{
            p->exe[sp+1].arr[j][i] = 0;
         }
      }
   }
   p->exe[sp].archive = 1;
   p->exe[sp-1].archive = 1;
}


void B_LESS_EXECUTE(Program *p, int sp, int rows, int cols)
{
   for(int j = 0; j < rows; j++){
      for(int i = 0; i < cols; i++){
         if(p->exe[sp-1].arr[j][i] < p->exe[sp].arr[j][i]){
            p->exe[sp+1].arr[j][i] = 1;
         }
         else{
            p->exe[sp+1].arr[j][i] = 0;
         }
      }
   }
   p->exe[sp].archive = 1;
   p->exe[sp-1].archive = 1;
}


void B_ADD_EXECUTE(Program *p, int sp, int rows, int cols)
{
   int sum;
   for(int j = 0; j < rows; j++){
      for(int i = 0; i < cols; i++){
         sum = p->exe[sp-1].arr[j][i] + p->exe[sp].arr[j][i];
         p->exe[sp+1].arr[j][i] = sum;
      }
   }
   p->exe[sp].archive = 1;
   p->exe[sp-1].archive = 1;
}


void B_TIMES_EXECUTE(Program *p, int sp, int rows, int cols)
{
   int sum;
   for(int j = 0; j < rows; j++){
      for(int i = 0; i < cols; i++){
         sum = p->exe[sp-1].arr[j][i] * p->exe[sp].arr[j][i];
         p->exe[sp+1].arr[j][i] = sum;
      }
   }
   p->exe[sp].archive = 1;
   p->exe[sp-1].archive = 1;
}


void B_EQUALS_EXECUTE(Program *p, int sp, int rows, int cols)
{
   for(int j = 0; j < rows; j++){
      for(int i = 0; i < cols; i++){
         if(p->exe[sp-1].arr[j][i] == p->exe[sp].arr[j][i]){
            p->exe[sp+1].arr[j][i] = 1;
         }
         else{
            p->exe[sp+1].arr[j][i] = 0;
         }
      }
   }
   p->exe[sp].archive = 1;
   p->exe[sp-1].archive = 1;
}


int CREATE(Program *p)
{
   if(strsame(p->wds[p->cw], "ONES")){
      p->cw = p->cw + 1;
      ROWS(p);
#ifdef LETSDOIT
      int rows = TO_INTEGER(p->wds[p->cw]);
#endif
      p->cw = p->cw + 1;
      COLS(p);
#ifdef LETSDOIT
      int cols = TO_INTEGER(p->wds[p->cw]);
#endif
      p->cw = p->cw + 1;
      VARNAME(p); 
#ifdef LETSDOIT
      ONES_EXECUTE(p, rows, cols);
#endif
      return 1;
   }
 
   if(strsame(p->wds[p->cw], "READ")){ 
      p->cw = p->cw + 1;
      FILENAME(p);
      p->cw = p->cw + 1;
      VARNAME(p);
#ifdef LETSDOIT
      READ_EXECUTE(p);
#endif
      return 1;
   }
   return 0;
}


void ONES_EXECUTE(Program *p, int rows, int cols)
{
   for(int i = 0; i < p->size_v; i++){
      if(p->var[i].varname == p->wds[p->cw][1]){
         p->var[i].row = rows;
         p->var[i].col = cols;
         p->var[i].label = 1;
         for(int k = 0; k < rows; k++){
            for(int j = 0; j < cols; j++){
               p->var[i].arr[k][j] = 1;
            }
         }
      }
   }
}


void READ_EXECUTE(Program *p)
{
   char fname[MAXTOKENSIZE] = "";
   int len = strlen(p->str[p->size_s-1]);
   int index = 0;
   for(int i = 1; i < len-1; i++){
      fname[index] = p->str[p->size_s-1][i];
      index++;
   }
   fname[index] = '\0';

   FILE* fp1 = fopen(fname, "rt");
   if(!fp1){
      fprintf(stderr, "Cannot read %s\n", fname);
      exit(EXIT_FAILURE);
   }
   char tmp_1[MAXTOKENSIZE] = "";
   char tmp_2[MAXTOKENSIZE] = "";
   fscanf(fp1, "%s", tmp_1);
   fscanf(fp1, "%s", tmp_2);
   if(!IS_INTEGER(tmp_1) || !IS_INTEGER(tmp_2)){
      ON_ERROR("Expected rows and cols are defined as integers in the array file.\n");
   }
   int rows = TO_INTEGER(tmp_1);
   int cols = TO_INTEGER(tmp_2);
   for(int i = 0; i < p->size_v; i++){
      if(p->var[i].varname == p->wds[p->cw][1]){
         p->var[i].row = rows;
         p->var[i].col = cols;
         p->var[i].label = 1;
         for(int k = 0; k < rows; k++){
            for(int j = 0; j < cols; j++){
               if(fscanf(fp1, "%s", tmp_1) == 1){
                  if(IS_INTEGER(tmp_1)){
                     p->var[i].arr[k][j] = TO_INTEGER(tmp_1);
                  }
                  else{
                     ON_ERROR("Expected the array file only contains integer.\n");
                  }
               }
               else{
                  ON_ERROR("The array didn't match its cols and rols which defined in the file.\n");
               }
            }
         }
      }
   }
   if(fscanf(fp1, "%s", tmp_1) == 1){
      ON_ERROR("The array didn't match its cols and rols which defined in the file.\n");
   }
   fclose(fp1);
}


void ROWS(Program *p)
{
   if(!IS_INTEGER(p->wds[p->cw])){
      ON_ERROR("Expected a ROWS after ONES.\n");
   }
}



void COLS(Program *p)
{
   if(!IS_INTEGER(p->wds[p->cw])){
      ON_ERROR("Expected a COLS after ONES.\n");
   }
}


void FILENAME(Program *p)
{
   if(!STRING(p)){
      ON_ERROR("Expected a file name with double quote after READ.\n");
   }
}



int TO_INTEGER(char* a)
{
   int sum = 0, num = 0;
   int index = strlen(a);
   for(int i = index-1; i >= 0; i--){
      int tmp = a[i] - '0';
      sum = sum + (pow(10,num) * tmp);
      num++;
   }
   return sum;
}



int SWAP(Program *p)
{
   if(!strsame(p->wds[p->cw], "SWAP")){ 
     return 0;
   }
   p->cw = p->cw + 1;
   if(!VARNAME(p)){
      ON_ERROR("Expected two VARNAME after SWAP.\n");
   }
   p->cw = p->cw + 1;
   if(!VARNAME(p)){
      ON_ERROR("Expected two VARNAME after SWAP.\n");
   } 
#ifdef EXTENSION
   SWAP_EXECUTE(p);
#endif
   return 1;
}



void SWAP_EXECUTE(Program *p)
{
   bool flag1 = false;
   bool flag2 = false;
   int tmp1, tmp2;
   for(int i = 0; i < p->size_v; i++){
      if(p->wds[p->cw][1] == p->var[i].varname){
         if(p->var[i].label == 0){
            ON_ERROR("Using an undefined variable!\n");
         }
         else{
            tmp1 = i;
            flag1 = true;
         }
      }
      if(p->wds[p->cw-1][1] == p->var[i].varname){
         if(p->var[i].label == 0){
            ON_ERROR("Using an undefined variable!\n");
         }
         else{
            tmp2 = i;
            flag2 = true;
         }
      }
   }
   if(flag1 == true && flag2 == true){
      char a;
      a = p->var[tmp1].varname;
      p->var[tmp1].varname = p->var[tmp2].varname;
      p->var[tmp2].varname = a;
   }
}



int IF(Program *p)
{
   if(!strsame(p->wds[p->cw], "IF")){ 
     return 0;
   }
   p->cw = p->cw + 1;
   if(!VARNAME(p)){
      ON_ERROR("Expected at least a VARNAME after IF.\n");
   }
   GET_VAR_VALUE(p);
   if(p->exe[p->size_e-1].col > 1 || p->exe[p->size_e-1].row > 1){
      ON_ERROR("VARNAME should only contain integer in IF condition.\n");
   }
   p->cw = p->cw + 1;
   if(VARNAME(p)){
      GET_VAR_VALUE(p);
      if(p->exe[p->size_e-1].col > 1 || p->exe[p->size_e-1].row > 1){
         ON_ERROR("VARNAME should only contain integer in IF condition.\n");
      }
   }
   else if(IS_INTEGER(p->wds[p->cw])){
      GET_INTEGER(p);
   }
   else{
      ON_ERROR("Expected a VARNAME and a INTEGER or two VARNAME after IF.\n");
   }
   p->cw = p->cw + 1;
   if(!COMPARE(p)){
      ON_ERROR("Expected a COMPARE before { in IF.\n");
   }
   p->cw = p->cw + 1;
   if(!strsame(p->wds[p->cw], "{")){
      ON_ERROR("Expected a { after COMPARE in IF.\n");
   }
   p->cw = p->cw + 1;
   if(!ELSEINSTRCLIST(p)){
      ON_ERROR("Expected a ELSEINSTRCLIST after { in IF.\n");
   }
   return 1;
}



int COMPARE(Program *p)
{
   if(strsame(p->wds[p->cw], "B-EQUALS")){
      int sp = p->size_e-1, rows = 1, cols = 1;
      B_EQUALS_EXECUTE(p, sp, rows, cols);
      return 1;
   }
   if(strsame(p->wds[p->cw], "B-GREATER")){
      int sp = p->size_e-1, rows = 1, cols = 1;
      B_GREATER_EXECUTE(p, sp, rows, cols);
      return 1;
   }
   if(strsame(p->wds[p->cw], "B-LESS")){
      int sp = p->size_e-1, rows = 1, cols = 1;
      B_LESS_EXECUTE(p, sp, rows, cols);
      return 1;
   }
   return 0;
}



int ELSEINSTRCLIST(Program *p)
{
   //<ELSEINSTRCLIST> ::=  <INSTRCLIST> | <INSTRCLIST> <ELSE>
#ifndef LETSDOIT
   INSTRCLIST(p);
   p->cw = p->cw + 1;
   if(strsame(p->wds[p->cw], "ELSE")){
      ELSE(p);
   }
#endif
#ifdef LETSDOIT
   if(p->exe[p->size_e].arr[0][0] == 1){
      INSTRCLIST(p);
      if(strsame(p->wds[p->cw+1], "ELSE")){
         if(!strsame(p->wds[p->cw+2], "{")){
            ON_ERROR("Expected a { after ELSE.\n");
         }
         int Parentheses = 1;
         for(int j = p->cw+3; j < p->wds_length; j++){
            if(Parentheses == 0){
               p->cw = j - 1;
               return 1;
            }
            if(strsame(p->wds[j], "{")){
               Parentheses++;
            }
            if(strsame(p->wds[j], "}")){
               Parentheses--;
            }
         }
         ON_ERROR("Expected a } at the end of the ELSE.\n");
      }
      return 1;
   }
   else{
      int Parentheses = 1;
      for(int j = p->cw; j < p->wds_length; j++){
         if(Parentheses == 0){
            p->cw = j - 1;
            j = p->wds_length;
         }
         else if(strsame(p->wds[j], "{")){
            Parentheses++;
         }
         else if(strsame(p->wds[j], "}")){
            Parentheses--;
         }
      }
      if(Parentheses != 0){
         ON_ERROR("Expected a } at the end of the IF.\n");
      }
      if(strsame(p->wds[p->cw+1], "ELSE")){
         p->cw++;
         ELSE(p);
         return 1;
      }
   }
#endif
   return 1;
}



void ELSE(Program *p)
{
   //<ELSE> ::= ELSE "{" <INSTRCLIST>
   p->cw = p->cw + 1;
   if(!strsame(p->wds[p->cw], "{")){
      ON_ERROR("Expected a { after ELSE.\n");
   }
   p->cw = p->cw + 1;
   INSTRCLIST(p);
}



void TEST(void)
{
   char* a = "26756";
   assert(IS_INTEGER(a));
   assert(TO_INTEGER(a) == 26756);

   char* b = "*6756";
   assert(!IS_INTEGER(b));

   char* c = "3-756";
   assert(!IS_INTEGER(c));

   char* d = "90001";
   assert(IS_INTEGER(d));
   assert(TO_INTEGER(d) == 90001);

}





