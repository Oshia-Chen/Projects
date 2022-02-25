# Which compiler to use e.g. gcc, clang, gcc-8 etc.
CC := clang

# The house-style flags
CWARNS := -Wall -Wextra -Wpedantic -Wfloat-equal -Wvla -std=c99 -Werror 

# Optimized version
PRODUCTION := $(CWARNS) -O2

SANITIZE := $(CWARNS) -fsanitize=address -fsanitize=undefined -g3

#VALGRIND := $(CWARNS) -g3
#PROFILE := $(CWARNS) -pg -g3

# 'make parse' builds production and sanitize exectuables
parse : parse parse_s

# 'make interp' builds production and sanitize exectuables
interp : interp interp_s

# 'make extension' builds production and sanitize exectuables
extension : extension extension_s

# 'make all' builds production and sanitize exectuables
# against both implementations
all: parse interp extension

parse : nlab.c
	$(CC) nlab.c $(PRODUCTION) -O2 -o parse -lm

parse_s :nlab.c
	$(CC) nlab.c  $(SANITIZE) -O2 -o parse_s -lm

interp: nlab.c
	$(CC) nlab.c $(PRODUCTION) -O2 -DLETSDOIT -o interp -lm

interp_s: nlab.c
	$(CC) nlab.c $(SANITIZE) -O2 -DLETSDOIT -o interp_s -lm

extension: nlab.c
	$(CC) nlab.c $(PRODUCTION) -O2 -DEXTENSION -DLETSDOIT -o extension -lm

extension_s: nlab.c
	$(CC) nlab.c $(SANITIZE) -O2 -DEXTENSION -DLETSDOIT -o extension_s -lm


# 'make clean' removes all executables
clean: 
	rm -f parse parse_s interp interp_s extension extension_s
