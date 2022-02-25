#include "../dict.h"
#define Longest 2000
#define MAXIMUM 2

struct dict{
   int length;
   struct dict_node* p;
};


struct dict_node{
   struct item* next;
};
typedef struct dict_node dict_node;


struct item{
   char str[Longest];
   struct item* next;
};
typedef struct item item;


