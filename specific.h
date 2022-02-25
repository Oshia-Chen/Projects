#include "../dict.h"
#define KHASHES 11
#define BERNSTEIN_NUM 5381
#define MAXIMUM 20

struct dict{
   int length;
   bool flag;
};

unsigned long* _hashes(const char* s);
unsigned long _hash(const char* s);



