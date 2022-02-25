/*
Trivial testing file for Dictionary; notice it is case-sensitive.
Should work for the any Dictionary implementation
*/
#include "dict.h"

#define BIGSTR 1000

int main(void)
{
   assert(!dict_add(NULL, ""));
   assert(!dict_add(NULL, "one"));
   assert(!dict_spelling(NULL, ""));
   assert(!dict_spelling(NULL, "one"));

   dict* d = dict_init(50);
   assert(dict_add(d, "one"));
   assert(dict_add(d, "one"));
   assert(dict_add(d, "two"));

   assert(dict_spelling(d, "one"));
   assert(dict_spelling(d, "two"));
   assert(!dict_spelling(d, "Two"));
   assert(!dict_spelling(d, "One"));

   assert(dict_add(d, "oshiaa"));
   //assert((int)_hash("oshiaa") == 6950972059480);
   assert(dict_add(d, "project"));
   //assert(_hash("project") == 229387417841584);
   assert(dict_add(d, "AABBCC"));
   //assert(_hash("AABBCC") == 6951777655813);

   assert(dict_add(d, "AAB BCC"));
   assert(dict_add(d, "12345"));
   assert(dict_add(d, "1ab2c3!"));

   assert(dict_spelling(d, "oshiaa"));
   assert(dict_spelling(d, "project"));
   assert(dict_spelling(d, "AABBCC"));
   assert(dict_spelling(d, "AAB BCC"));
   assert(dict_spelling(d, "12345"));
   assert(dict_spelling(d, "1ab2c3!"));
   assert(!dict_spelling(d, "Oshiaa"));
   assert(!dict_spelling(d, "AABBcc"));
   assert(!dict_spelling(d, "12 345"));

   dict_free(d);
   return EXIT_SUCCESS;
}
