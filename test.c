#include "specific.h"


void test(void){
   dict* d = dict_init(50);
   assert(dict_add(d, "oshiaa"));
   //assert(_hash("oshiaa") == 6950972059480);
   assert(dict_add(d, "project"));
   //assert(_hash("project") == 229387417841584);
   assert(dict_add(d, "AABBCC"));
   
   assert(_hash("AABBCC") == 6951777655813);

   assert(dict_spelling(d, "oshiaa"));
   assert(dict_spelling(d, "project"));
   assert(dict_spelling(d, "AABBCC"));
   assert(!dict_spelling(d, "Oshiaa"));
   assert(!dict_spelling(d, "AABBcc"));
}



