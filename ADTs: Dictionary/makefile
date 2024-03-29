# Which compiler to use e.g. gcc, clang, gcc-8 etc.
CC := clang

# The house-style flags
CWARNS := -Wall -Wextra -Wpedantic -Wfloat-equal -Wvla -std=c99 -Werror

# Optimized version
PRODUCTION := $(CWARNS) -O2

SANITIZE := $(CWARNS) -fsanitize=address -fsanitize=undefined -g3

VALGRIND := $(CWARNS) -g3
PROFILE := $(CWARNS) -pg -g3

# 'make allexact' builds production and sanitize exectuables
# against the exact/hashing implementation and testdict.c or spelling.c
allexact: spellexact spellexact_s spellexact_v testexact testexact_s testexact_v

# 'make allapprox' builds production and sanitize exectuables
# against the approx/Bloom implementation and testdict.c or spelling.c
allapprox : spellapprox spellapprox_s spellapprox_v testapprox testapprox_s testapprox_v

# 'make all' builds production and sanitize exectuables
# against both implementations
all : allexact allapprox


testexact: Exact/exact.c testdict.c general.c
	$(CC) testdict.c Exact/exact.c general.c $(PRODUCTION) -o testexact -lm
testexact_s: Exact/exact.c testdict.c general.c
	$(CC) testdict.c Exact/exact.c general.c $(SANITIZE) -o testexact_s -lm
testexact_v: Exact/exact.c testdict.c general.c
	$(CC) testdict.c Exact/exact.c general.c $(VALGRIND) -o testexact_v -lm

spellexact: Exact/exact.c spelling.c general.c
	$(CC) spelling.c Exact/exact.c general.c $(PRODUCTION) -o spellexact -lm
spellexact_s: Exact/exact.c spelling.c general.c
	$(CC) spelling.c Exact/exact.c general.c $(SANITIZE) -o spellexact_s -lm
spellexact_v: Exact/exact.c spelling.c general.c
	$(CC) spelling.c Exact/exact.c general.c $(VALGRIND) -o spellexact_v -lm


testapprox: Approx/approx.c testdict.c general.c
	$(CC) testdict.c Approx/approx.c general.c $(PRODUCTION) -o testapprox -lm
testapprox_s: Approx/approx.c testdict.c general.c
	$(CC) testdict.c Approx/approx.c general.c $(SANITIZE) -o testapprox_s -lm
testapprox_v: Approx/approx.c testdict.c general.c
	$(CC) testdict.c Approx/approx.c general.c $(VALGRIND) -o testapprox_v -lm
spellapprox: Approx/approx.c spelling.c general.c
	$(CC) spelling.c Approx/approx.c general.c $(PRODUCTION) -o spellapprox -lm
spellapprox_s: Approx/approx.c spelling.c general.c
	$(CC) spelling.c Approx/approx.c general.c $(SANITIZE) -o spellapprox_s -lm
spellapprox_v: Approx/approx.c spelling.c general.c
	$(CC) spelling.c Approx/approx.c general.c $(VALGRIND) -o spellapprox_v -lm

# 'make clean' removes all executables
clean: 
	rm -f spellapprox spellexact spellapprox_s spellapprox_v spellexact_s spellexact_v testapprox testexact testapprox_s testapprox_v testexact_s testexact_v
