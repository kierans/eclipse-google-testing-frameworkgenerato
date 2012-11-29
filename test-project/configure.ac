dnl Process this file with autoconf to produce a configure script.

AC_PREREQ([2.68])
AC_INIT([google-test-generator-test-project], [1.0])

AM_INIT_AUTOMAKE([-Wall -Werror foreign])

LT_INIT()

AC_CONFIG_MACRO_DIR([m4])

AC_PROG_CXX
CF_HAVE_GTEST([1.6.0])

# Output files from configure
AC_CONFIG_HEADERS([config.h])
AC_CONFIG_FILES([Makefile src/Makefile])

AC_OUTPUT