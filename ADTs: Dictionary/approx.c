#include "specific.h"


dict* dict_init(unsigned int maxwords){
   dict* boolarray;
   boolarray = (dict*) calloc((maxwords * MAXIMUM), sizeof(dict));
   if(boolarray == NULL){
      on_error("Cannot calloc() space");
   }
   boolarray[0].length = maxwords * MAXIMUM;
   return boolarray;
}


bool dict_add(dict* x, const char* s){
   if(x == NULL){
      return false;
   }

   unsigned long* hashes = _hashes(s);
   int size = sizeof(hashes);
   int length = x[0].length;
   int tmp;

   for(int i = 0; i < size; i++){
      tmp = hashes[i] % length;
      x[tmp].flag = true;
   }
   free(hashes);
   return true;
}


bool dict_spelling(dict* x, const char* s){
   if(x == NULL){
      return false;
   }
   unsigned long* hashes = _hashes(s);
   int size = sizeof(hashes);
   int length = x[0].length;
   int tmp;

   for (int i = 0; i < size; i++){
      tmp = hashes[i] % length;
      if(x[tmp].flag == false){
         free(hashes);
         return false;
      }
   }
   free(hashes);
   return true;
}


unsigned long* _hashes(const char* s){
   // You’ll need to free this later
   unsigned long* hashes = ncalloc(KHASHES, sizeof(unsigned long)); 
   // Use Bernstein from Lecture Notes (or other suitable hash)
   unsigned long bh = _hash(s);
   int ln = strlen(s);
   /* If two different strings have the same bh, then
   we need a separate way to distiguish them when using
   bh to generate a sequence */
   srand(bh*(ln*s[0] + s[ln-1])); 
   unsigned long h2 = bh;
   for (int i = 0; i < KHASHES; i++){ 
      h2 = 33 * h2 ^ rand(); 
      hashes[i] = h2;
   }
   // Still need to apply modulus to these to fit table size
   return hashes;
}


unsigned long _hash(const char* s){
   unsigned long hash = BERNSTEIN_NUM; 
   int c;
   while((c = (*s++))){
      hash = 33 * hash ^ c; 
   }
   return hash;
}


void dict_free(dict* x){
   if(x){ 
      free(x);
   }
}


//This is edge case testing
//void test(void){
//    dict* d = dict_init(50);
//    assert(dict_add(d, "oshiaa"));
//    assert(dict_add(d, "project"));
//    assert(dict_add(d, "AABBCC"));
//    assert(dict_add(d, "AAB BCC"));
//    assert(dict_add(d, "12345"));
//    assert(dict_add(d, "1ab2c3!"));

//    assert(dict_spelling(d, "oshiaa"));
//    assert(dict_spelling(d, "project"));
//    assert(dict_spelling(d, "AABBCC"));
//    assert(dict_spelling(d, "AAB BCC"));
//    assert(dict_spelling(d, "12345"));
//    assert(dict_spelling(d, "1ab2c3!"));
//    assert(!dict_spelling(d, "Oshiaa"));
//    assert(!dict_spelling(d, "AABBcc"));
//    assert(!dict_spelling(d, "12 345"));
//    dict_free(d);
// }


