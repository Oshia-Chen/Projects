#include "specific.h"

/* The maximum number of words we will want to input.
   Exact : Hashtable will be twice this size
   Approx : Hashtable will be (e.g.) 20 times this size
*/
dict* dict_init(unsigned int maxwords){
   dict* info;
   dict_node* hashtable;
   info = (dict*) calloc(1, sizeof(dict));
   hashtable = (dict_node*) calloc((maxwords * MAXIMUM), sizeof(dict_node));
   if(hashtable == NULL || info == NULL){
      on_error("Cannot calloc() space");
   }
   info->length = maxwords * MAXIMUM;
   info->p = hashtable;

   return info;
}


/* Add string to dictionary
   Exact : A deep-copy is stored in the hashtable only if the word
           has not already been added to the table.
   Approx : Multiple hashes (e.g. 11) are computed and corresponding
            Boolean flags set in the Bloom hashtable. 
*/
bool dict_add(dict* x, const char* s){
   if(x == NULL){
      return false;
   }
   int sum = 0, len = strlen(s), index;
   int size = x->length;

   for(int i = 0; i < len; i++){
      sum = sum + (int)s[i];
   }
   index = sum % size;
   dict_node* current = &x->p[index];
   item* f = calloc(1, sizeof(item)); 
   for(int i = 0; i < len; i++){
      f->str[i] = s[i];
   }
   f->str[len] = '\0';
   if(current->next != NULL){
      f->next = current->next;
      current->next = f;
      return true;
   }
   else{
      current->next = f;
      return true;
   }
   return false;
}

/* Returns true if the word is already in the dictionary,
   false otherwise.
*/
bool dict_spelling(dict* x, const char* s){
   if(x == NULL){
      return false;
   }
   int size = x->length;
   int len = strlen(s);
   int sum = 0, index;
   for(int i = 0; i < len; i++){
      sum = sum + (int)s[i];
   }
   index = sum % size;
   dict_node* temp = &x->p[index];
   item* current = temp->next;
   while(current != NULL){
      if(strcmp(current->str, s) == 0){
         return true;
      }
      current = current->next;
   }
   return false;
}


/* Frees all space used */
void dict_free(dict* x){
   if(x){ 
      dict_node* index;
      item* dest;
      item* tmp;
      int len = x->length;
      for(int i = 0; i < len; i++){
         index = &x->p[i];
         dest = index->next;
         while(dest!=NULL){
            tmp = dest->next; 
            free(dest);
            dest = tmp;
         }
      }
      free(x->p);
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


