# ===========================================================================
#        TODO: Add URL
# ===========================================================================
#
# SYNOPSIS
#
#   CF_PROG_CUT
#   CF_PROG_HEAD
#   CF_PROG_SORT
#   CF_PROG_TR
#   CF_PROG_UNIQ
#
#   CF_PROG_CPPCHECK
#   CF_PROG_VALGRIND
#
# DESCRIPTION
#     Checks for tools and exports the following values
#
#     CF_TR - Where tr lives.
#     CF_SORT - Where sort lives.
#     CF_UNIQ - Where uniq lives.
#     ....
#     etc
#
#   Example lines for Makefile.in:
#
#     TR = @CF_TR@
#
# LICENSE
#   TODO: License

### Basic *nix tools in alphabectical order ###
AC_DEFUN([CF_PROG_CUT], [
  AC_PATH_PROG(CF_CUT, cut, [no-cut])
  AS_IF([test "x$CF_CUT" = "xno-cut"], [AC_MSG_ERROR([Can't find cut])])
])

AC_DEFUN([CF_PROG_HEAD], [
  AC_PATH_PROG(CF_HEAD, head, [no-head])
  AS_IF([test "x$CF_HEAD" = "xno-head"], [AC_MSG_ERROR([Can't find head])])
])


AC_DEFUN([CF_PROG_SORT], [
  AC_PATH_PROG(CF_SORT, sort, [no-sort])
  AS_IF([test "x$CF_SORT" = "xno-sort"], [AC_MSG_ERROR([Can't find sort])])
])

AC_DEFUN([CF_PROG_TR], [
  AC_PATH_PROG(CF_TR, tr, [no-tr])
  AS_IF([test "x$CF_TR" = "xno-tr"], [AC_MSG_ERROR([Can't find tr])])
])

AC_DEFUN([CF_PROG_UNIQ], [
  AC_PATH_PROG(CF_UNIQ, uniq, [no-uniq])
  AS_IF([test "x$CF_UNIQ" = "xno-uniq"], [AC_MSG_ERROR([Can't find uniq])])
])

### Specific tool checks ###
AC_DEFUN([CF_PROG_CPPCHECK], [
  AC_REQUIRE([CF_PROG_CUT])
  AC_PATH_PROG(CF_CPPCHECK, cppcheck, [no-cppcheck])
  AS_IF([test "x$CF_CPPCHECK" = "xno-cppcheck"], [AC_MSG_ERROR([Can't find cppcheck])])
  _CF_CHECK_VERSION([$CF_CPPCHECK], [$CF_CPPCHECK --version | $CF_CUT -d ' ' -f 2], [1.48])
])

AC_DEFUN([CF_PROG_VALGRIND], [
  AC_REQUIRE([CF_PROG_CUT])
  AC_PATH_PROG(CF_VALGRIND, valgrind, [no-valgrind])
  AS_IF([test "x$CF_VALGRIND" = "xno-valgrind"], [AC_MSG_ERROR([Can't find valgrind])])
  _CF_CHECK_VERSION([$CF_VALGRIND], [$CF_VALGRIND --version | $CF_CUT -d '-' -f 2], [3.6.1])
])

### Private utility macros ###
AC_DEFUN([_CF_CHECK_VERSION], [
  prog="$1"
  vsn_check="$2"
  vsn="$3"
  
  val=`eval $vsn_check`
  
  AC_MSG_CHECKING([for correct version of $prog])
  AS_IF([test "x$val" = "x$vsn"], ,
        [AC_MSG_ERROR([$prog not at required version of '$vsn' (at '$val')])])
  AC_MSG_RESULT([ok ($val)])
])

AC_DEFUN([_CF_FILTER_DUPLICATES], [
  AC_REQUIRE([CF_PROG_SORT])
  AC_REQUIRE([CF_PROG_TR])
  AC_REQUIRE([CF_PROG_UNIQ])
  AC_REQUIRE([AC_PROG_GREP])
  
  $1=`echo $2 | $CF_TR ' ' '\n' | $CF_SORT | $CF_UNIQ | $GREP -v '^ *$' | $CF_TR '\n' ' '`
])

### Vars to sub ###
AC_SUBST([CF_CPPCHECK])
AC_SUBST([CF_CUT])
AC_SUBST([CF_SORT])
AC_SUBST([CF_TR])
AC_SUBST([CF_UNIQ])